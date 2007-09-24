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
name|parser
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
name|util
operator|.
name|List
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
name|config
operator|.
name|Content
import|;
end_import

begin_comment
comment|/**  * Abstract class Parser  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Parser
block|{
specifier|private
name|InputStream
name|is
decl_stmt|;
specifier|private
name|String
name|mimeType
decl_stmt|;
specifier|private
name|String
name|namespace
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Content
argument_list|>
name|contents
decl_stmt|;
specifier|public
name|void
name|setInputStream
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|this
operator|.
name|is
operator|=
name|is
expr_stmt|;
block|}
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
block|{
return|return
name|is
return|;
block|}
comment|/**      * Get document mime type      */
specifier|public
name|String
name|getMimeType
parameter_list|()
block|{
return|return
name|mimeType
return|;
block|}
comment|/**      * Set document mime type      */
specifier|public
name|void
name|setMimeType
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
name|this
operator|.
name|mimeType
operator|=
name|mimeType
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespace
parameter_list|()
block|{
return|return
name|namespace
return|;
block|}
specifier|public
name|void
name|setNamespace
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
name|this
operator|.
name|namespace
operator|=
name|namespace
expr_stmt|;
block|}
comment|/**      * Get the string content of the document      */
specifier|public
specifier|abstract
name|String
name|getStrContent
parameter_list|()
function_decl|;
comment|/**      * Get a content object, this object is configured from the LiusConfig Xml.      * It could be a document metadata, XPath selection, regex selection or      * fulltext      */
specifier|public
specifier|abstract
name|Content
name|getContent
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Get a List of contents objects, this objects are configured from the      * LiusConfig Xml file. It could be a document metadata, XPath selection,      * regex selection or fulltext      */
specifier|public
name|List
argument_list|<
name|Content
argument_list|>
name|getContents
parameter_list|()
block|{
return|return
name|contents
return|;
block|}
specifier|public
name|void
name|setContents
parameter_list|(
name|List
argument_list|<
name|Content
argument_list|>
name|contents
parameter_list|)
block|{
name|this
operator|.
name|contents
operator|=
name|contents
expr_stmt|;
block|}
block|}
end_class

end_unit

