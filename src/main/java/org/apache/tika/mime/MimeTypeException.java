begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2007 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
package|;
end_package

begin_comment
comment|/**  * A class to encapsulate MimeType related exceptions.  *   * @author Hari Kodungallur  * @author J&eacute;r&ocirc;me Charron  */
end_comment

begin_class
specifier|public
class|class
name|MimeTypeException
extends|extends
name|Exception
block|{
comment|/**      * Constructs a MimeTypeException with no specified detail message.      */
specifier|public
name|MimeTypeException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a MimeTypeException with the specified detail message.      *       * @param msg      *            the detail message.      */
specifier|public
name|MimeTypeException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a MimeTypeException with the specified cause.      *       * @param t      *            the cause.      */
specifier|public
name|MimeTypeException
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|super
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

