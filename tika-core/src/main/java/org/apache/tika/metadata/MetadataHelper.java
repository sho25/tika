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
name|metadata
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
name|net
operator|.
name|URL
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
name|io
operator|.
name|TikaInputStream
import|;
end_import

begin_comment
comment|/**  * Collection of static helper methods for handling metadata.  *  * @deprecated Use {@link TikaInputStream} instead  * @since Apache Tika 0.7  */
end_comment

begin_class
specifier|public
class|class
name|MetadataHelper
block|{
comment|/**      * Private constructor to prevent instantiation.      */
specifier|private
name|MetadataHelper
parameter_list|()
block|{     }
comment|/**      * Returns the content at the given URL, and sets any related      * metadata entries.      *      * @param url the URL of the resource to be read      * @param metadata where the resource metadata is stored      * @return resource content      * @throws IOException if the URL can not be accessed      */
specifier|public
specifier|static
name|InputStream
name|getInputStream
parameter_list|(
name|URL
name|url
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|TikaInputStream
operator|.
name|get
argument_list|(
name|url
argument_list|,
name|metadata
argument_list|)
return|;
block|}
block|}
end_class

end_unit

