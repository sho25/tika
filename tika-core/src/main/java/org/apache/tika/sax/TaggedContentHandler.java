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
name|sax
package|;
end_package

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * A content handler decorator that tags potential exceptions so that the  * handler that caused the exception can easily be identified. This is  * done by using the {@link TaggedSAXException} class to wrap all thrown  * {@link SAXException}s. See below for an example of using this class.  *<pre>  * TaggedContentHandler handler = new TaggedContentHandler(...);  * try {  *     // Processing that may throw an SAXException either from this handler  *     // or from some other XML parsing activity  *     processXML(handler);  * } catch (SAXException e) {  *     if (handler.isCauseOf(e)) {  *         // The exception was caused by this handler.  *         // Use e.getCause() to get the original exception.  *     } else {  *         // The exception was caused by something else.  *     }  * }  *</pre>  *<p>  * Alternatively, the {@link #throwIfCauseOf(Exception)} method can be  * used to let higher levels of code handle the exception caused by this  * stream while other processing errors are being taken care of at this  * lower level.  *<pre>  * TaggedContentHandler handler = new TaggedContentHandler(...);  * try {  *     processXML(handler);  * } catch (SAXException e) {  *     stream.throwIfCauseOf(e);  *     // ... or process the exception that was caused by something else  * }  *</pre>  *  * @see TaggedSAXException  */
end_comment

begin_class
specifier|public
class|class
name|TaggedContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
comment|/**      * Creates a tagging decorator for the given content handler.      *      * @param proxy content handler to be decorated      */
specifier|public
name|TaggedContentHandler
parameter_list|(
name|ContentHandler
name|proxy
parameter_list|)
block|{
name|super
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests if the given exception was caused by this handler.      *      * @param exception an exception      * @return<code>true</code> if the exception was thrown by this handler,      *<code>false</code> otherwise      */
specifier|public
name|boolean
name|isCauseOf
parameter_list|(
name|SAXException
name|exception
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|instanceof
name|TaggedSAXException
condition|)
block|{
name|TaggedSAXException
name|tagged
init|=
operator|(
name|TaggedSAXException
operator|)
name|exception
decl_stmt|;
return|return
name|this
operator|==
name|tagged
operator|.
name|getTag
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Re-throws the original exception thrown by this handler. This method      * first checks whether the given exception is a {@link TaggedSAXException}      * wrapper created by this decorator, and then unwraps and throws the      * original wrapped exception. Returns normally if the exception was      * not thrown by this handler.      *      * @param exception an exception      * @throws SAXException original exception, if any, thrown by this handler      */
specifier|public
name|void
name|throwIfCauseOf
parameter_list|(
name|Exception
name|exception
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|exception
operator|instanceof
name|TaggedSAXException
condition|)
block|{
name|TaggedSAXException
name|tagged
init|=
operator|(
name|TaggedSAXException
operator|)
name|exception
decl_stmt|;
if|if
condition|(
name|this
operator|==
name|tagged
operator|.
name|getTag
argument_list|()
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
comment|/**      * Tags any {@link SAXException}s thrown, wrapping and re-throwing.      *       * @param e The SAXException thrown      * @throws SAXException if an XML error occurs      */
annotation|@
name|Override
specifier|protected
name|void
name|handleException
parameter_list|(
name|SAXException
name|e
parameter_list|)
throws|throws
name|SAXException
block|{
throw|throw
operator|new
name|TaggedSAXException
argument_list|(
name|e
argument_list|,
name|this
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

