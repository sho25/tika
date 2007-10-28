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
name|mime
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_comment
comment|/**  * Creates instances of MimeTypes.  */
end_comment

begin_class
specifier|public
class|class
name|MimeTypesFactory
block|{
comment|/**      * Creates an empty instance; same as calling new MimeTypes().      *      * @return an empty instance      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|()
block|{
return|return
operator|new
name|MimeTypes
argument_list|()
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance from the specified document.      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|MimeTypes
name|mimeTypes
init|=
operator|new
name|MimeTypes
argument_list|()
decl_stmt|;
operator|new
name|MimeTypesReader
argument_list|(
name|mimeTypes
argument_list|)
operator|.
name|read
argument_list|(
name|document
argument_list|)
expr_stmt|;
return|return
name|mimeTypes
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance from the specified input stream.      * Does not close the input stream.      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
block|{
name|MimeTypes
name|mimeTypes
init|=
operator|new
name|MimeTypes
argument_list|()
decl_stmt|;
operator|new
name|MimeTypesReader
argument_list|(
name|mimeTypes
argument_list|)
operator|.
name|read
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
return|return
name|mimeTypes
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance from the resource      * at the location specified by the URL.  Opens and closes the      * InputStream from the URL.      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|inputStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|inputStream
operator|=
name|url
operator|.
name|openStream
argument_list|()
expr_stmt|;
return|return
name|create
argument_list|(
name|inputStream
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|inputStream
operator|!=
literal|null
condition|)
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Creates and returns a MimeTypes instance from the specified file path,      * as interpreted by the class loader in getResource().      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|create
argument_list|(
name|MimeTypesReader
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|filePath
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

