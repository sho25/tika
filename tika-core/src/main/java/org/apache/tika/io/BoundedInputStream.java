begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|io
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

begin_comment
comment|/**  * Very slight modification of Commons' BoundedInputStream  * so that we can figure out if this hit the bound or not.  */
end_comment

begin_class
specifier|public
class|class
name|BoundedInputStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|final
specifier|static
name|int
name|EOF
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
name|long
name|max
decl_stmt|;
specifier|private
specifier|final
name|InputStream
name|in
decl_stmt|;
specifier|private
name|long
name|pos
decl_stmt|;
specifier|public
name|BoundedInputStream
parameter_list|(
name|long
name|max
parameter_list|,
name|InputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|max
operator|>=
literal|0
operator|&&
name|pos
operator|>=
name|max
condition|)
block|{
return|return
name|EOF
return|;
block|}
specifier|final
name|int
name|result
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
name|pos
operator|++
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Invokes the delegate's<code>read(byte[])</code> method.      *      * @param b the buffer to read the bytes into      * @return the number of bytes read or -1 if the end of stream or      * the limit has been reached.      * @throws IOException if an I/O error occurs      */
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**      * Invokes the delegate's<code>read(byte[], int, int)</code> method.      *      * @param b   the buffer to read the bytes into      * @param off The start offset      * @param len The number of bytes to read      * @return the number of bytes read or -1 if the end of stream or      * the limit has been reached.      * @throws IOException if an I/O error occurs      */
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|max
operator|>=
literal|0
operator|&&
name|pos
operator|>=
name|max
condition|)
block|{
return|return
name|EOF
return|;
block|}
specifier|final
name|long
name|maxRead
init|=
name|max
operator|>=
literal|0
condition|?
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|max
operator|-
name|pos
argument_list|)
else|:
name|len
decl_stmt|;
specifier|final
name|int
name|bytesRead
init|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
operator|(
name|int
operator|)
name|maxRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|==
name|EOF
condition|)
block|{
return|return
name|EOF
return|;
block|}
name|pos
operator|+=
name|bytesRead
expr_stmt|;
return|return
name|bytesRead
return|;
block|}
comment|/**      * Invokes the delegate's<code>skip(long)</code> method.      *      * @param n the number of bytes to skip      * @return the actual number of bytes skipped      * @throws IOException if an I/O error occurs      */
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
specifier|final
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|toSkip
init|=
name|max
operator|>=
literal|0
condition|?
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|max
operator|-
name|pos
argument_list|)
else|:
name|n
decl_stmt|;
specifier|final
name|long
name|skippedBytes
init|=
name|in
operator|.
name|skip
argument_list|(
name|toSkip
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|skippedBytes
expr_stmt|;
return|return
name|skippedBytes
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readLimit
parameter_list|)
block|{
name|in
operator|.
name|mark
argument_list|(
name|readLimit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasHitBound
parameter_list|()
block|{
return|return
name|pos
operator|>=
name|max
return|;
block|}
block|}
end_class

end_unit

