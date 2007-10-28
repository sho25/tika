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
name|config
package|;
end_package

begin_comment
comment|//JDK imports
end_comment

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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|MimeTypes
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
name|MimeTypesFactory
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
name|parser
operator|.
name|Parser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|JDOMException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|input
operator|.
name|SAXBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import

begin_comment
comment|/**  * Parse xml config file.  */
end_comment

begin_class
specifier|public
class|class
name|TikaConfig
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CONFIG_LOCATION
init|=
literal|"/org/apache/tika/tika-config.xml"
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
name|parsers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|MimeTypes
name|mimeTypes
decl_stmt|;
specifier|public
name|TikaConfig
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|JDOMException
throws|,
name|IOException
block|{
name|this
argument_list|(
operator|new
name|File
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TikaConfig
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|JDOMException
throws|,
name|IOException
block|{
name|this
argument_list|(
operator|new
name|SAXBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TikaConfig
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|JDOMException
throws|,
name|IOException
block|{
name|this
argument_list|(
operator|new
name|SAXBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TikaConfig
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|JDOMException
throws|,
name|IOException
block|{
name|this
argument_list|(
operator|new
name|SAXBuilder
argument_list|()
operator|.
name|build
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TikaConfig
parameter_list|(
name|Document
name|document
parameter_list|)
throws|throws
name|JDOMException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|document
operator|.
name|getRootElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TikaConfig
parameter_list|(
name|Element
name|element
parameter_list|)
throws|throws
name|JDOMException
throws|,
name|IOException
block|{
name|Element
name|mtr
init|=
name|element
operator|.
name|getChild
argument_list|(
literal|"mimeTypeRepository"
argument_list|)
decl_stmt|;
name|String
name|mimeTypeRepoResource
init|=
name|mtr
operator|.
name|getAttributeValue
argument_list|(
literal|"resource"
argument_list|)
decl_stmt|;
name|mimeTypes
operator|=
name|MimeTypesFactory
operator|.
name|create
argument_list|(
name|mimeTypeRepoResource
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|node
range|:
name|XPath
operator|.
name|selectNodes
argument_list|(
name|element
argument_list|,
literal|"//parser"
argument_list|)
control|)
block|{
name|String
name|className
init|=
operator|(
operator|(
name|Element
operator|)
name|node
operator|)
operator|.
name|getAttributeValue
argument_list|(
literal|"class"
argument_list|)
decl_stmt|;
try|try
block|{
name|Parser
name|parser
init|=
operator|(
name|Parser
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|child
range|:
operator|(
operator|(
name|Element
operator|)
name|node
operator|)
operator|.
name|getChildren
argument_list|(
literal|"mime"
argument_list|)
control|)
block|{
name|parsers
operator|.
name|put
argument_list|(
operator|(
operator|(
name|Element
operator|)
name|child
operator|)
operator|.
name|getTextTrim
argument_list|()
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JDOMException
argument_list|(
literal|"Invalid parser configuration: "
operator|+
name|className
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Returns the parser instance configured for the given MIME type.      * Returns<code>null</code> if the given MIME type is unknown.      *      * @param mimeType MIME type      * @return configured Parser instance, or<code>null</code>      */
specifier|public
name|Parser
name|getParser
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
return|return
name|parsers
operator|.
name|get
argument_list|(
name|mimeType
argument_list|)
return|;
block|}
specifier|public
name|MimeTypes
name|getMimeRepository
parameter_list|()
block|{
return|return
name|mimeTypes
return|;
block|}
comment|/**      * Provides a default configuration (TikaConfig).  Currently creates a      * new instance each time it's called; we may be able to have it      * return a shared instance once it is completely immutable.      *      * @return      * @throws IOException      * @throws JDOMException      */
specifier|public
specifier|static
name|TikaConfig
name|getDefaultConfig
parameter_list|()
throws|throws
name|IOException
throws|,
name|JDOMException
block|{
return|return
operator|new
name|TikaConfig
argument_list|(
name|TikaConfig
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|DEFAULT_CONFIG_LOCATION
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

