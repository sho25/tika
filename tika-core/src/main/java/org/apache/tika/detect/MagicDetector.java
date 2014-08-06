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
name|CharArrayWriter
import|;
end_import

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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|CharBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
comment|/**  * Content type detection based on magic bytes, i.e. type-specific patterns  * near the beginning of the document input stream.  *  * Because this works on bytes, not characters, by default any string  *  matching is done as ISO_8859_1. To use an explicit different  *  encoding, supply a type other than "string" / "stringignorecase"  *  * @since Apache Tika 0.3  */
end_comment

begin_class
specifier|public
class|class
name|MagicDetector
implements|implements
name|Detector
block|{
specifier|private
specifier|static
specifier|final
name|Charset
name|ISO_8859_1
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"ISO-8859-1"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|MagicDetector
name|parse
parameter_list|(
name|MediaType
name|mediaType
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|offset
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|mask
parameter_list|)
block|{
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|offset
operator|!=
literal|null
condition|)
block|{
name|int
name|colon
init|=
name|offset
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|==
operator|-
literal|1
condition|)
block|{
name|start
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|end
operator|=
name|start
expr_stmt|;
block|}
else|else
block|{
name|start
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|offset
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
argument_list|)
expr_stmt|;
name|end
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|offset
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|byte
index|[]
name|patternBytes
init|=
name|decodeValue
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|byte
index|[]
name|maskBytes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|mask
operator|!=
literal|null
condition|)
block|{
name|maskBytes
operator|=
name|decodeValue
argument_list|(
name|mask
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MagicDetector
argument_list|(
name|mediaType
argument_list|,
name|patternBytes
argument_list|,
name|maskBytes
argument_list|,
name|type
operator|.
name|equals
argument_list|(
literal|"regex"
argument_list|)
argument_list|,
name|type
operator|.
name|equals
argument_list|(
literal|"stringignorecase"
argument_list|)
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|byte
index|[]
name|decodeValue
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|type
parameter_list|)
block|{
comment|// Preliminary check
if|if
condition|(
operator|(
name|value
operator|==
literal|null
operator|)
operator|||
operator|(
name|type
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|byte
index|[]
name|decoded
init|=
literal|null
decl_stmt|;
name|String
name|tmpVal
init|=
literal|null
decl_stmt|;
name|int
name|radix
init|=
literal|8
decl_stmt|;
comment|// hex
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0x"
argument_list|)
condition|)
block|{
name|tmpVal
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|radix
operator|=
literal|16
expr_stmt|;
block|}
else|else
block|{
name|tmpVal
operator|=
name|value
expr_stmt|;
name|radix
operator|=
literal|8
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"string"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"regex"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"unicodeLE"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"unicodeBE"
argument_list|)
condition|)
block|{
name|decoded
operator|=
name|decodeString
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"stringignorecase"
argument_list|)
condition|)
block|{
name|decoded
operator|=
name|decodeString
argument_list|(
name|value
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
try|try
block|{
name|decoded
operator|=
name|tmpVal
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"UTF-8 not supported."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"host16"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"little16"
argument_list|)
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0x00FF
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"big16"
argument_list|)
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0x00FF
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"host32"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"little32"
argument_list|)
condition|)
block|{
name|long
name|i
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x000000FF
operator|)
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x0000FF00
operator|)
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x00FF0000
operator|)
operator|>>
literal|16
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0xFF000000
operator|)
operator|>>
literal|24
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"big32"
argument_list|)
condition|)
block|{
name|long
name|i
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0xFF000000
operator|)
operator|>>
literal|24
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x00FF0000
operator|)
operator|>>
literal|16
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x0000FF00
operator|)
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x000000FF
operator|)
argument_list|)
block|}
expr_stmt|;
block|}
return|return
name|decoded
return|;
block|}
specifier|private
specifier|static
name|byte
index|[]
name|decodeString
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0x"
argument_list|)
condition|)
block|{
name|byte
index|[]
name|vals
init|=
operator|new
name|byte
index|[
operator|(
name|value
operator|.
name|length
argument_list|()
operator|-
literal|2
operator|)
operator|/
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|2
operator|+
name|i
operator|*
literal|2
argument_list|,
literal|4
operator|+
name|i
operator|*
literal|2
argument_list|)
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
return|return
name|vals
return|;
block|}
name|CharArrayWriter
name|decoded
init|=
operator|new
name|CharArrayWriter
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
name|decoded
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'x'
condition|)
block|{
name|decoded
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|2
argument_list|,
name|i
operator|+
literal|4
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|+=
literal|3
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'r'
condition|)
block|{
name|decoded
operator|.
name|write
argument_list|(
operator|(
name|int
operator|)
literal|'\r'
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'n'
condition|)
block|{
name|decoded
operator|.
name|write
argument_list|(
operator|(
name|int
operator|)
literal|'\n'
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
else|else
block|{
name|int
name|j
init|=
name|i
operator|+
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|j
operator|<
name|i
operator|+
literal|4
operator|)
operator|&&
operator|(
name|j
operator|<
name|value
operator|.
name|length
argument_list|()
operator|)
operator|&&
operator|(
name|Character
operator|.
name|isDigit
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|)
operator|)
condition|)
block|{
name|j
operator|++
expr_stmt|;
block|}
name|decoded
operator|.
name|write
argument_list|(
name|Short
operator|.
name|decode
argument_list|(
literal|"0"
operator|+
name|value
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|j
argument_list|)
argument_list|)
operator|.
name|byteValue
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|=
name|j
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
name|decoded
operator|.
name|write
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Now turn the chars into bytes
name|char
index|[]
name|chars
init|=
name|decoded
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
decl_stmt|;
if|if
condition|(
literal|"unicodeLE"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|chars
operator|.
name|length
operator|*
literal|2
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
operator|*
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|chars
index|[
name|i
index|]
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|bytes
index|[
name|i
operator|*
literal|2
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|chars
index|[
name|i
index|]
operator|>>
literal|8
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"unicodeBE"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|chars
operator|.
name|length
operator|*
literal|2
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
operator|*
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|chars
index|[
name|i
index|]
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|bytes
index|[
name|i
operator|*
literal|2
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|chars
index|[
name|i
index|]
operator|&
literal|0xff
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Copy with truncation
name|bytes
operator|=
operator|new
name|byte
index|[
name|chars
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|chars
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|bytes
return|;
block|}
comment|/**      * The matching media type. Returned by the      * {@link #detect(InputStream, Metadata)} method if a match is found.      */
specifier|private
specifier|final
name|MediaType
name|type
decl_stmt|;
comment|/**      * Length of the comparison window.      */
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
comment|/**      * The magic match pattern. If this byte pattern is equal to the      * possibly bit-masked bytes from the input stream, then the type      * detection succeeds and the configured {@link #type} is returned.      */
specifier|private
specifier|final
name|byte
index|[]
name|pattern
decl_stmt|;
comment|/**      * Length of the pattern, which in the case of regular expressions will      * not be the same as the comparison window length.      */
specifier|private
specifier|final
name|int
name|patternLength
decl_stmt|;
comment|/**      * True if pattern is a regular expression, false otherwise.      */
specifier|private
specifier|final
name|boolean
name|isRegex
decl_stmt|;
comment|/**      * True if we're doing a case-insensitive string match, false otherwise.      */
specifier|private
specifier|final
name|boolean
name|isStringIgnoreCase
decl_stmt|;
comment|/**      * Bit mask that is applied to the source bytes before pattern matching.      */
specifier|private
specifier|final
name|byte
index|[]
name|mask
decl_stmt|;
comment|/**      * First offset (inclusive) of the comparison window within the      * document input stream. Greater than or equal to zero.      */
specifier|private
specifier|final
name|int
name|offsetRangeBegin
decl_stmt|;
comment|/**      * Last offset (inclusive) of the comparison window within the document      * input stream. Greater than or equal to the      * {@link #offsetRangeBegin first offset}.      *<p>      * Note that this is<em>not</em> the offset of the last byte read from      * the document stream. Instead, the last window of bytes to be compared      * starts at this offset.      */
specifier|private
specifier|final
name|int
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
name|int
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
comment|/**      * Creates a detector for input documents that meet the specified magic      * match.  {@code pattern} must NOT be a regular expression.      * Constructor maintained for legacy reasons.      */
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
name|int
name|offsetRangeBegin
parameter_list|,
name|int
name|offsetRangeEnd
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|pattern
argument_list|,
name|mask
argument_list|,
literal|false
argument_list|,
name|offsetRangeBegin
argument_list|,
name|offsetRangeEnd
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a detector for input documents that meet the specified      * magic match.      */
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
name|boolean
name|isRegex
parameter_list|,
name|int
name|offsetRangeBegin
parameter_list|,
name|int
name|offsetRangeEnd
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|pattern
argument_list|,
name|mask
argument_list|,
name|isRegex
argument_list|,
literal|false
argument_list|,
name|offsetRangeBegin
argument_list|,
name|offsetRangeEnd
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a detector for input documents that meet the specified      * magic match.      */
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
name|boolean
name|isRegex
parameter_list|,
name|boolean
name|isStringIgnoreCase
parameter_list|,
name|int
name|offsetRangeBegin
parameter_list|,
name|int
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
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|isRegex
operator|=
name|isRegex
expr_stmt|;
name|this
operator|.
name|isStringIgnoreCase
operator|=
name|isStringIgnoreCase
expr_stmt|;
name|this
operator|.
name|patternLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|pattern
operator|.
name|length
argument_list|,
name|mask
operator|!=
literal|null
condition|?
name|mask
operator|.
name|length
else|:
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|isRegex
condition|)
block|{
comment|// 8K buffer should cope with most regex patterns
name|this
operator|.
name|length
operator|=
literal|8
operator|*
literal|1024
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|length
operator|=
name|patternLength
expr_stmt|;
block|}
name|this
operator|.
name|mask
operator|=
operator|new
name|byte
index|[
name|this
operator|.
name|patternLength
index|]
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
operator|new
name|byte
index|[
name|this
operator|.
name|patternLength
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|patternLength
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|mask
operator|!=
literal|null
operator|&&
name|i
operator|<
name|mask
operator|.
name|length
condition|)
block|{
name|this
operator|.
name|mask
index|[
name|i
index|]
operator|=
name|mask
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|mask
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<
name|pattern
operator|.
name|length
condition|)
block|{
name|this
operator|.
name|pattern
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|pattern
index|[
name|i
index|]
operator|&
name|this
operator|.
name|mask
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|pattern
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
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
comment|/**      *       * @param input document input stream, or<code>null</code>      * @param metadata ignored      */
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
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
name|input
operator|.
name|mark
argument_list|(
name|offsetRangeEnd
operator|+
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|int
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
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|length
operator|+
operator|(
name|offsetRangeEnd
operator|-
name|offsetRangeBegin
operator|)
index|]
decl_stmt|;
name|int
name|n
init|=
name|input
operator|.
name|read
argument_list|(
name|buffer
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
while|while
condition|(
name|n
operator|!=
operator|-
literal|1
operator|&&
name|offset
operator|<
name|offsetRangeEnd
operator|+
name|length
condition|)
block|{
name|int
name|bufferOffset
init|=
name|offset
operator|-
name|offsetRangeBegin
decl_stmt|;
name|n
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|bufferOffset
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|bufferOffset
argument_list|)
expr_stmt|;
comment|// increment offset - in case not all read (see testDetectStreamReadProblems)
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
block|}
if|if
condition|(
name|this
operator|.
name|isRegex
condition|)
block|{
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|isStringIgnoreCase
condition|)
block|{
name|flags
operator|=
name|Pattern
operator|.
name|CASE_INSENSITIVE
expr_stmt|;
block|}
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
operator|new
name|String
argument_list|(
name|this
operator|.
name|pattern
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|ByteBuffer
name|bb
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|CharBuffer
name|result
init|=
name|ISO_8859_1
operator|.
name|decode
argument_list|(
name|bb
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|boolean
name|match
init|=
literal|false
decl_stmt|;
comment|// Loop until we've covered the entire offset range
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|offsetRangeEnd
operator|-
name|offsetRangeBegin
condition|;
name|i
operator|++
control|)
block|{
name|m
operator|.
name|region
argument_list|(
name|i
argument_list|,
name|length
operator|+
name|i
argument_list|)
expr_stmt|;
name|match
operator|=
name|m
operator|.
name|lookingAt
argument_list|()
expr_stmt|;
comment|// match regex from start of region
if|if
condition|(
name|match
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|offset
operator|<
name|offsetRangeBegin
operator|+
name|length
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
comment|// Loop until we've covered the entire offset range
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|offsetRangeEnd
operator|-
name|offsetRangeBegin
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|match
init|=
literal|true
decl_stmt|;
name|int
name|masked
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|match
operator|&&
name|j
operator|<
name|length
condition|;
name|j
operator|++
control|)
block|{
name|masked
operator|=
operator|(
name|buffer
index|[
name|i
operator|+
name|j
index|]
operator|&
name|mask
index|[
name|j
index|]
operator|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|isStringIgnoreCase
condition|)
block|{
name|masked
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|masked
argument_list|)
expr_stmt|;
block|}
name|match
operator|=
operator|(
name|masked
operator|==
name|pattern
index|[
name|j
index|]
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|match
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
block|}
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
finally|finally
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|patternLength
return|;
block|}
comment|/**      * Returns a string representation of the Detection Rule.      * Should sort nicely by type and details, as we sometimes      *  compare these.      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// Needs to be unique, as these get compared.
return|return
literal|"Magic Detection for "
operator|+
name|type
operator|+
literal|" looking for "
operator|+
name|pattern
operator|.
name|length
operator|+
literal|" bytes = "
operator|+
name|this
operator|.
name|pattern
operator|+
literal|" mask = "
operator|+
name|this
operator|.
name|mask
return|;
block|}
block|}
end_class

end_unit

