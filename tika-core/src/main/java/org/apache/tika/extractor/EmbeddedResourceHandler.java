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
name|extractor
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
comment|/**  * Tika container extractor callback interface.  * To work with a {@link ContainerExtractor}, your code needs  *  to implement this interface.  */
end_comment

begin_interface
specifier|public
interface|interface
name|EmbeddedResourceHandler
block|{
comment|/**      * Called to process an embedded resource within the container.      * This will be called once per embedded resource within the      *  container, along with whatever details are available on      *  the embedded resource.      *        * @since Apache Tika 0.8      * @param filename The filename of the embedded resource, if known      * @param mediaType The media type of the embedded resource, if known      * @param stream The contents of the embedded resource      */
name|void
name|handle
parameter_list|(
name|String
name|filename
parameter_list|,
name|MediaType
name|mediaType
parameter_list|,
name|InputStream
name|stream
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

