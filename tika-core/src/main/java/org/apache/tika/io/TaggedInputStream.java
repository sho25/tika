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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * An input stream decorator that tags potential exceptions so that the  * stream that caused the exception can easily be identified. This is  * done by using the {@link TaggedIOException} class to wrap all thrown  * {@link IOException}s. See below for an example of using this class.  *<pre>  * TaggedInputStream stream = new TaggedInputStream(...);  * try {  *     // Processing that may throw an IOException either from this stream  *     // or from some other IO activity like temporary files, etc.  *     processStream(stream);  * } catch (IOException e) {  *     if (stream.isCauseOf(e)) {  *         // The exception was caused by this stream.  *         // Use e.getCause() to get the original exception.  *     } else {  *         // The exception was caused by something else.  *     }  * }  *</pre>  *<p>  * Alternatively, the {@link #throwIfCauseOf(Exception)} method can be  * used to let higher levels of code handle the exception caused by this  * stream while other processing errors are being taken care of at this  * lower level.  *<pre>  * TaggedInputStream stream = new TaggedInputStream(...);  * try {  *     processStream(stream);  * } catch (IOException e) {  *     stream.throwIfCauseOf(e);  *     // ... or process the exception that was caused by something else  * }  *</pre>  *  * @see TaggedIOException  */
end_comment

begin_class
specifier|public
class|class
name|TaggedInputStream
extends|extends
name|ProxyInputStream
block|{
comment|/**      * The unique (serializable) tag of this stream.      */
specifier|private
specifier|final
name|Serializable
name|tag
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
comment|/**      * Creates a tagging decorator for the given input stream.      *      * @param proxy input stream to be decorated      */
specifier|public
name|TaggedInputStream
parameter_list|(
name|InputStream
name|proxy
parameter_list|)
block|{
name|super
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
comment|/**      * Casts or wraps the given stream to a TaggedInputStream instance.      *      * @param stream normal input stream      * @return a TaggedInputStream instance      */
specifier|public
specifier|static
name|TaggedInputStream
name|get
parameter_list|(
name|InputStream
name|proxy
parameter_list|)
block|{
if|if
condition|(
name|proxy
operator|instanceof
name|TaggedInputStream
condition|)
block|{
return|return
operator|(
name|TaggedInputStream
operator|)
name|proxy
return|;
block|}
return|return
operator|new
name|TaggedInputStream
argument_list|(
name|proxy
argument_list|)
return|;
block|}
comment|/**      * Tests if the given exception was caused by this stream.      *      * @param exception an exception      * @return<code>true</code> if the exception was thrown by this stream,      *<code>false</code> otherwise      */
specifier|public
name|boolean
name|isCauseOf
parameter_list|(
name|IOException
name|exception
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|instanceof
name|TaggedIOException
condition|)
block|{
name|TaggedIOException
name|tagged
init|=
operator|(
name|TaggedIOException
operator|)
name|exception
decl_stmt|;
return|return
name|tag
operator|.
name|equals
argument_list|(
name|tagged
operator|.
name|getTag
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Re-throws the original exception thrown by this stream. This method      * first checks whether the given exception is a {@link TaggedIOException}      * wrapper created by this decorator, and then unwraps and throws the      * original wrapped exception. Returns normally if the exception was      * not thrown by this stream.      *      * @param exception an exception      * @throws IOException original exception, if any, thrown by this stream      */
specifier|public
name|void
name|throwIfCauseOf
parameter_list|(
name|Exception
name|exception
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|exception
operator|instanceof
name|TaggedIOException
condition|)
block|{
name|TaggedIOException
name|tagged
init|=
operator|(
name|TaggedIOException
operator|)
name|exception
decl_stmt|;
if|if
condition|(
name|tag
operator|.
name|equals
argument_list|(
name|tagged
operator|.
name|getTag
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|tagged
operator|.
name|getCause
argument_list|()
throw|;
block|}
block|}
block|}
comment|/**      * Tags any IOExceptions thrown, wrapping and re-throwing.      *       * @param e The IOException thrown      * @throws IOException if an I/O error occurs      */
annotation|@
name|Override
specifier|protected
name|void
name|handleIOException
parameter_list|(
name|IOException
name|e
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|TaggedIOException
argument_list|(
name|e
argument_list|,
name|tag
argument_list|)
throw|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Tika Tagged InputStream wrapping "
operator|+
name|in
return|;
block|}
block|}
end_class

end_unit

