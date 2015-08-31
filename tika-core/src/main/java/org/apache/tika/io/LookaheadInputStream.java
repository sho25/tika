begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Stream wrapper that make it easy to read up to n bytes ahead from  * a stream that supports the mark feature. This class insulates the  * underlying stream from things like possible mark(), reset() and close()  * calls by external components that might otherwise invalidate the marked  * state of a stream.  *<p>  * The recommended usage pattern of this class is:  *<pre>  *     try (InputStream lookahead = new LookaheadInputStream(stream, n)) {  *         processStream(lookahead);  *     }  *</pre>  *<p>  * This usage pattern guarantees that only up to n bytes from the original  * stream can ever be read, and that the stream will have been marked and  * then reset to its original state once the above code block exits. No  * code in the fictional processStream() method can affect the the state of  * the original stream.  *  * @since Apache Tika 0.10  */
end_comment

begin_class
specifier|public
class|class
name|LookaheadInputStream
extends|extends
name|InputStream
block|{
specifier|private
name|InputStream
name|stream
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|buffer
decl_stmt|;
specifier|private
name|int
name|buffered
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|position
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|mark
init|=
literal|0
decl_stmt|;
comment|/**      * Creates a lookahead wrapper for the given input stream.      * The given input stream should support the mark feature,      * as otherwise the state of that stream will be undefined      * after the lookahead wrapper has been closed. As a special      * case a<code>null</code> stream is treated as an empty stream.      *      * @param stream input stream, can be<code>null</code>      * @param n maximum number of bytes to look ahead      */
specifier|public
name|LookaheadInputStream
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|n
index|]
expr_stmt|;
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|stream
operator|.
name|mark
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|stream
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fill
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|available
argument_list|()
operator|==
literal|0
operator|&&
name|buffered
operator|<
name|buffer
operator|.
name|length
operator|&&
name|stream
operator|!=
literal|null
condition|)
block|{
name|int
name|n
init|=
name|stream
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|buffered
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|buffered
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|!=
operator|-
literal|1
condition|)
block|{
name|buffered
operator|+=
name|n
expr_stmt|;
block|}
else|else
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|fill
argument_list|()
expr_stmt|;
if|if
condition|(
name|buffered
operator|>
name|position
condition|)
block|{
return|return
literal|0xff
operator|&
name|buffer
index|[
name|position
operator|++
index|]
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|fill
argument_list|()
expr_stmt|;
if|if
condition|(
name|buffered
operator|>
name|position
condition|)
block|{
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|buffered
operator|-
name|position
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|position
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|position
operator|+=
name|len
expr_stmt|;
return|return
name|len
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|fill
argument_list|()
expr_stmt|;
name|n
operator|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|available
argument_list|()
argument_list|)
expr_stmt|;
name|position
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
block|{
return|return
name|buffered
operator|-
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
name|mark
operator|=
name|position
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|position
operator|=
name|mark
expr_stmt|;
block|}
block|}
end_class

end_unit

