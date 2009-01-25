begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|detect
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MediaType
import|;
end_import

begin_comment
comment|/**  * Content type detection based on magic bytes, i.e. type-specific patterns  * near the beginning of the document input stream.  *  * @since Apache Tika 0.3  */
end_comment

begin_class
specifier|public
class|class
name|MagicDetector
implements|implements
name|Detector
block|{
comment|/**      * The matching media type. Returned by the      * {@link #detect(InputStream, Metadata)} method if a match is found.      */
specifier|private
specifier|final
name|MediaType
name|type
decl_stmt|;
comment|/**      * Length of the comparison window. All the byte arrays here are this long.      */
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
comment|/**      * The magic match pattern. If this byte pattern is equal to the      * possibly bit-masked bytes in the comparison window, then the type      * detection succeeds and the configured {@link #type} is returned.      */
specifier|private
specifier|final
name|byte
index|[]
name|pattern
decl_stmt|;
comment|/**      * Bit mask that is applied to the source bytes in the comparison window      * before pattern matching. This mask may be<code>null</code>, in which      * case the source bytes are compared as-is against the configured pattern.      */
specifier|private
specifier|final
name|byte
index|[]
name|mask
decl_stmt|;
comment|/**      * Byte buffer that contains the raw input bytes in the current comparison      * window. This buffer is first filled with the byte sequence starting at      * the beginning of the configured offset range. Then the buffer is moved      * forward one byte at a time until a match is found or the entire offset      * range has been covered.      */
specifier|private
specifier|final
name|byte
index|[]
name|sourceBuffer
decl_stmt|;
comment|/**      * The comparison buffer that contains the result of combining the raw      * input bytes in the current comparison window with the configured      * {@link #mask bit mask}. If a bit mask is not configured, then this      * reference points to the {@link #sourceBuffer raw source buffer} to      * avoid extra logic or copying when doing the pattern match.      */
specifier|private
specifier|final
name|byte
index|[]
name|compareBuffer
decl_stmt|;
comment|/**      * First offset (inclusive) of the comparison window within the      * document input stream. Greater than or equal to zero.      */
specifier|private
specifier|final
name|long
name|offsetRangeBegin
decl_stmt|;
comment|/**      * Last offset (inclusive) of the comparison window within the document      * input stream. Greater than or equal to the      * {@link #offsetRangeBegin first offset}.      *<p>      * Note that this is<em>not</em> the offset of the last byte read from      * the document stream. Instead, the last window of bytes to be compared      * starts at this offset.      */
specifier|private
specifier|final
name|long
name|offsetRangeEnd
decl_stmt|;
comment|/**      * Creates a detector for input documents that have the exact given byte      * pattern at the beginning of the document stream.      *      * @param type matching media type      * @param pattern magic match pattern      */
specifier|public
name|MagicDetector
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|byte
index|[]
name|pattern
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|pattern
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a detector for input documents that have the exact given byte      * pattern at the given offset of the document stream.      *      * @param type matching media type      * @param pattern magic match pattern      * @param offset offset of the pattern match      */
specifier|public
name|MagicDetector
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|byte
index|[]
name|pattern
parameter_list|,
name|long
name|offset
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|pattern
argument_list|,
literal|null
argument_list|,
name|offset
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a detector for input documents that meet the specified      * magic match.      *      */
specifier|public
name|MagicDetector
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|byte
index|[]
name|pattern
parameter_list|,
name|byte
index|[]
name|mask
parameter_list|,
name|long
name|offsetRangeBegin
parameter_list|,
name|long
name|offsetRangeEnd
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Matching media type is null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|pattern
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Magic match pattern is null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|mask
operator|!=
literal|null
operator|&&
name|mask
operator|.
name|length
operator|!=
name|pattern
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Different pattern and mask lengths: "
operator|+
name|pattern
operator|.
name|length
operator|+
literal|" != "
operator|+
name|mask
operator|.
name|length
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|offsetRangeBegin
operator|<
literal|0
operator|||
name|offsetRangeEnd
operator|<
name|offsetRangeBegin
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid offset range: ["
operator|+
name|offsetRangeBegin
operator|+
literal|","
operator|+
name|offsetRangeEnd
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|pattern
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|mask
operator|=
name|mask
expr_stmt|;
name|this
operator|.
name|sourceBuffer
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
if|if
condition|(
name|mask
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|compareBuffer
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|compareBuffer
operator|=
name|this
operator|.
name|sourceBuffer
expr_stmt|;
block|}
name|this
operator|.
name|offsetRangeBegin
operator|=
name|offsetRangeBegin
expr_stmt|;
name|this
operator|.
name|offsetRangeEnd
operator|=
name|offsetRangeEnd
expr_stmt|;
block|}
block|}
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|offset
init|=
literal|0
decl_stmt|;
comment|// Skip bytes at the beginning, using skip() or read()
while|while
condition|(
name|offset
operator|<
name|offsetRangeBegin
condition|)
block|{
name|long
name|n
init|=
name|input
operator|.
name|skip
argument_list|(
name|offsetRangeBegin
operator|-
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|offset
operator|+=
name|n
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|input
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|offset
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
comment|// Fill in the comparison window
while|while
condition|(
name|offset
operator|<
name|offsetRangeBegin
operator|+
name|sourceBuffer
operator|.
name|length
condition|)
block|{
name|int
name|i
init|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|-
name|offsetRangeBegin
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|input
operator|.
name|read
argument_list|(
name|sourceBuffer
argument_list|,
name|i
argument_list|,
name|sourceBuffer
operator|.
name|length
operator|-
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
name|offset
operator|+=
name|n
expr_stmt|;
block|}
comment|// Loop until we've covered the entire offset range
while|while
condition|(
literal|true
condition|)
block|{
comment|// Apply the mask, if any
if|if
condition|(
name|mask
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|compareBuffer
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|sourceBuffer
index|[
name|i
index|]
operator|&
name|mask
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|pattern
argument_list|,
name|compareBuffer
argument_list|)
condition|)
block|{
comment|// We have a match, so return the matching media type
return|return
name|type
return|;
block|}
elseif|else
if|if
condition|(
name|offset
operator|<
name|offsetRangeEnd
operator|+
name|sourceBuffer
operator|.
name|length
condition|)
block|{
comment|// No match, move the comparison window forward and try again
name|int
name|c
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|sourceBuffer
argument_list|,
literal|1
argument_list|,
name|sourceBuffer
argument_list|,
literal|0
argument_list|,
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|sourceBuffer
index|[
name|length
operator|-
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|c
expr_stmt|;
name|offset
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// We have reached the end of the offset range, no match.
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

