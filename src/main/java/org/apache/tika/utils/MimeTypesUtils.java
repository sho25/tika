begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|hedges
operator|.
name|mimeinfo
operator|.
name|MimeInfo
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hedges
operator|.
name|mimeinfo
operator|.
name|MimeInfoException
import|;
end_import

begin_comment
comment|/**  * Detect mime type from file  *   * @author Rida Benjelloun (ridabenjelloun@apache.org)  */
end_comment

begin_class
specifier|public
class|class
name|MimeTypesUtils
block|{
specifier|public
specifier|static
name|String
name|getMimeType
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|MimeInfoException
throws|,
name|IOException
block|{
name|MimeInfo
name|mimeInfo
init|=
operator|new
name|MimeInfo
argument_list|()
decl_stmt|;
return|return
name|mimeInfo
operator|.
name|getMimeType
argument_list|(
name|file
argument_list|)
return|;
block|}
block|}
end_class

end_unit

