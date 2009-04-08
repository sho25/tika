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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|exception
operator|.
name|TikaException
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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|Element
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
name|Node
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
name|NodeList
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
name|TikaException
throws|,
name|IOException
throws|,
name|SAXException
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
name|TikaException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
name|getBuilder
argument_list|()
operator|.
name|parse
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
name|TikaException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
name|getBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|url
operator|.
name|toString
argument_list|()
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
name|TikaException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
name|getBuilder
argument_list|()
operator|.
name|parse
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
name|TikaException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|document
operator|.
name|getDocumentElement
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
name|TikaException
throws|,
name|IOException
block|{
name|Element
name|mtr
init|=
name|getChild
argument_list|(
name|element
argument_list|,
literal|"mimeTypeRepository"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mtr
operator|!=
literal|null
condition|)
block|{
name|mimeTypes
operator|=
name|MimeTypesFactory
operator|.
name|create
argument_list|(
name|mtr
operator|.
name|getAttribute
argument_list|(
literal|"resource"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|NodeList
name|nodes
init|=
name|element
operator|.
name|getElementsByTagName
argument_list|(
literal|"parser"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|node
init|=
operator|(
name|Element
operator|)
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|node
operator|.
name|getAttribute
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
name|name
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|NodeList
name|mimes
init|=
name|node
operator|.
name|getElementsByTagName
argument_list|(
literal|"mime"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|mimes
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|parsers
operator|.
name|put
argument_list|(
name|getText
argument_list|(
name|mimes
operator|.
name|item
argument_list|(
name|j
argument_list|)
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// TODO: Log warning about an invalid parser configuration
comment|// For now we just ignore this parser class
block|}
block|}
block|}
specifier|private
name|String
name|getText
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
return|return
name|node
operator|.
name|getNodeValue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|NodeList
name|list
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|getText
argument_list|(
name|list
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
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
name|Map
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
name|getParsers
parameter_list|()
block|{
return|return
name|parsers
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
comment|/**      * Provides a default configuration (TikaConfig).  Currently creates a      * new instance each time it's called; we may be able to have it      * return a shared instance once it is completely immutable.      *      * @return default configuration      * @throws TikaException if the default configuration is not available      */
specifier|public
specifier|static
name|TikaConfig
name|getDefaultConfig
parameter_list|()
throws|throws
name|TikaException
block|{
try|try
block|{
name|InputStream
name|stream
init|=
name|TikaConfig
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|DEFAULT_CONFIG_LOCATION
argument_list|)
decl_stmt|;
return|return
operator|new
name|TikaConfig
argument_list|(
name|stream
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to read default configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to parse default configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|DocumentBuilder
name|getBuilder
parameter_list|()
throws|throws
name|TikaException
block|{
try|try
block|{
return|return
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newDocumentBuilder
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"XML parser not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Element
name|getChild
parameter_list|(
name|Element
name|element
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Node
name|child
init|=
name|element
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|(
name|Element
operator|)
name|child
return|;
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

