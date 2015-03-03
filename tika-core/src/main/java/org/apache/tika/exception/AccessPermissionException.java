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
name|exception
package|;
end_package

begin_comment
comment|/**  * Exception to be thrown when a document does not allow content extraction.  * As of this writing, PDF documents are the only type of document that might  * cause this type of exception.  */
end_comment

begin_class
specifier|public
class|class
name|AccessPermissionException
extends|extends
name|TikaException
block|{
specifier|public
name|AccessPermissionException
parameter_list|()
block|{
name|super
argument_list|(
literal|"Unable to process: content extraction is not allowed"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AccessPermissionException
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|super
argument_list|(
literal|"Unable to process: content extraction is not allowed"
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AccessPermissionException
parameter_list|(
name|String
name|info
parameter_list|)
block|{
name|super
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AccessPermissionException
parameter_list|(
name|String
name|info
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{
name|super
argument_list|(
name|info
argument_list|,
name|th
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

