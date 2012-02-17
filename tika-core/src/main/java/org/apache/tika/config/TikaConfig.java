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
name|FileInputStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|imageio
operator|.
name|spi
operator|.
name|ServiceRegistry
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
name|detect
operator|.
name|CompositeDetector
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
name|detect
operator|.
name|DefaultDetector
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
name|detect
operator|.
name|Detector
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
name|MediaType
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
name|MediaTypeRegistry
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
name|MimeTypeException
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
name|AutoDetectParser
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
name|CompositeParser
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
name|DefaultParser
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
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|ParserDecorator
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
specifier|private
specifier|final
name|CompositeParser
name|parser
decl_stmt|;
specifier|private
specifier|final
name|Detector
name|detector
decl_stmt|;
specifier|private
specifier|final
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
name|url
argument_list|,
name|ServiceLoader
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TikaConfig
parameter_list|(
name|URL
name|url
parameter_list|,
name|ClassLoader
name|loader
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
operator|.
name|getDocumentElement
argument_list|()
argument_list|,
name|loader
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
name|this
argument_list|(
name|element
argument_list|,
name|ServiceLoader
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TikaConfig
parameter_list|(
name|Element
name|element
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|this
operator|.
name|mimeTypes
operator|=
name|typesFromDomElement
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|this
operator|.
name|detector
operator|=
name|detectorFromDomElement
argument_list|(
name|element
argument_list|,
name|mimeTypes
argument_list|,
name|loader
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parserFromDomElement
argument_list|(
name|element
argument_list|,
name|mimeTypes
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a Tika configuration from the built-in media type rules      * and all the {@link Parser} implementations available through the      * {@link ServiceRegistry service provider mechanism} in the given      * class loader.      *      * @since Apache Tika 0.8      * @param loader the class loader through which parser implementations      *               are loaded, or<code>null</code> for no parsers      * @throws MimeTypeException if the built-in media type rules are broken      * @throws IOException  if the built-in media type rules can not be read      */
specifier|public
name|TikaConfig
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
throws|throws
name|MimeTypeException
throws|,
name|IOException
block|{
name|this
operator|.
name|mimeTypes
operator|=
name|MimeTypes
operator|.
name|getDefaultMimeTypes
argument_list|()
expr_stmt|;
name|this
operator|.
name|detector
operator|=
operator|new
name|DefaultDetector
argument_list|(
name|mimeTypes
argument_list|,
name|loader
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
operator|new
name|DefaultParser
argument_list|(
name|mimeTypes
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a default Tika configuration.      * First checks whether an XML config file is specified, either in      *<ol>      *<li>System property "tika.config", or</li>      *<li>Environment variable TIKA_CONFIG</li>      *</ol>      *<p>If one of these have a value, try to resolve it relative to file      * system or classpath.</p>      *<p>If XML config is not specified, initialize from the built-in media      * type rules and all the {@link Parser} implementations available through      * the {@link ServiceRegistry service provider mechanism} in the context      * class loader of the current thread.</p>      *      * @throws IOException if the configuration can not be read      * @throws TikaException if problem with MimeTypes or parsing XML config      */
specifier|public
name|TikaConfig
parameter_list|()
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|String
name|config
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tika.config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
name|config
operator|=
name|System
operator|.
name|getenv
argument_list|(
literal|"TIKA_CONFIG"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|mimeTypes
operator|=
name|MimeTypes
operator|.
name|getDefaultMimeTypes
argument_list|()
expr_stmt|;
name|this
operator|.
name|parser
operator|=
operator|new
name|DefaultParser
argument_list|(
name|mimeTypes
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|detector
operator|=
operator|new
name|DefaultDetector
argument_list|(
name|mimeTypes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ClassLoader
name|loader
init|=
name|ServiceLoader
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|InputStream
name|stream
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|stream
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stream
operator|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Element
name|element
init|=
name|getBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|stream
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|this
operator|.
name|mimeTypes
operator|=
name|typesFromDomElement
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parserFromDomElement
argument_list|(
name|element
argument_list|,
name|mimeTypes
argument_list|,
name|loader
argument_list|)
expr_stmt|;
name|this
operator|.
name|detector
operator|=
name|detectorFromDomElement
argument_list|(
name|element
argument_list|,
name|mimeTypes
argument_list|,
name|loader
argument_list|)
expr_stmt|;
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
literal|"Specified Tika configuration has syntax errors: "
operator|+
name|config
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Specified Tika configuration not found: "
operator|+
name|config
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
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
comment|/**      * @deprecated Use the {@link #getParser()} method instead      */
specifier|public
name|Parser
name|getParser
parameter_list|(
name|MediaType
name|mimeType
parameter_list|)
block|{
return|return
name|parser
operator|.
name|getParsers
argument_list|()
operator|.
name|get
argument_list|(
name|mimeType
argument_list|)
return|;
block|}
comment|/**      * Returns the configured parser instance.      *      * @return configured parser      */
specifier|public
name|Parser
name|getParser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
comment|/**      * Returns the configured detector instance.      *      * @return configured detector      */
specifier|public
name|Detector
name|getDetector
parameter_list|()
block|{
return|return
name|detector
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
specifier|public
name|MediaTypeRegistry
name|getMediaTypeRegistry
parameter_list|()
block|{
return|return
name|mimeTypes
operator|.
name|getMediaTypeRegistry
argument_list|()
return|;
block|}
comment|/**      * Provides a default configuration (TikaConfig).  Currently creates a      * new instance each time it's called; we may be able to have it      * return a shared instance once it is completely immutable.      *      * @return default configuration      */
specifier|public
specifier|static
name|TikaConfig
name|getDefaultConfig
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|TikaConfig
argument_list|()
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
name|RuntimeException
argument_list|(
literal|"Unable to read default configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to access default configuration"
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
specifier|private
specifier|static
name|MimeTypes
name|typesFromDomElement
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
operator|&&
name|mtr
operator|.
name|hasAttribute
argument_list|(
literal|"resource"
argument_list|)
condition|)
block|{
return|return
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
return|;
block|}
else|else
block|{
return|return
name|MimeTypes
operator|.
name|getDefaultMimeTypes
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
name|CompositeParser
name|parserFromDomElement
parameter_list|(
name|Element
name|element
parameter_list|,
name|MimeTypes
name|mimeTypes
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
operator|new
name|ArrayList
argument_list|<
name|Parser
argument_list|>
argument_list|()
decl_stmt|;
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
name|Class
argument_list|<
name|?
argument_list|>
name|parserClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|loader
argument_list|)
decl_stmt|;
comment|// https://issues.apache.org/jira/browse/TIKA-866
if|if
condition|(
name|DefaultParser
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|parserClass
argument_list|)
operator|||
name|AutoDetectParser
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|parserClass
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Composite parsers not supported in<parser>"
operator|+
literal|" configuration elements: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|Object
name|instance
init|=
name|parserClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|instance
operator|instanceof
name|Parser
operator|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Configured class is not a Tika Parser: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|Parser
name|parser
init|=
operator|(
name|Parser
operator|)
name|instance
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
if|if
condition|(
name|mimes
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Set
argument_list|<
name|MediaType
argument_list|>
name|types
init|=
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|()
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
name|String
name|mime
init|=
name|getText
argument_list|(
name|mimes
operator|.
name|item
argument_list|(
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|mime
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Invalid media type name: "
operator|+
name|mime
argument_list|)
throw|;
block|}
block|}
name|parser
operator|=
name|ParserDecorator
operator|.
name|withTypes
argument_list|(
name|parser
argument_list|,
name|types
argument_list|)
expr_stmt|;
block|}
name|parsers
operator|.
name|add
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Configured parser class not found: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to access a parser class: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to instantiate a parser class: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|CompositeParser
argument_list|(
name|mimeTypes
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|parsers
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Detector
name|detectorFromDomElement
parameter_list|(
name|Element
name|element
parameter_list|,
name|MimeTypes
name|mimeTypes
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|Detector
argument_list|>
name|detectors
init|=
operator|new
name|ArrayList
argument_list|<
name|Detector
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|nodes
init|=
name|element
operator|.
name|getElementsByTagName
argument_list|(
literal|"detector"
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
name|Class
argument_list|<
name|?
argument_list|>
name|detectorClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|loader
argument_list|)
decl_stmt|;
name|Object
name|instance
init|=
name|detectorClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|instance
operator|instanceof
name|Detector
operator|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Configured class is not a Tika Detector: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|Detector
name|detector
init|=
operator|(
name|Detector
operator|)
name|instance
decl_stmt|;
name|detectors
operator|.
name|add
argument_list|(
name|detector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Configured detector class not found: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to access a detector class: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to instantiate a detector class: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|CompositeDetector
argument_list|(
name|mimeTypes
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|detectors
argument_list|)
return|;
block|}
block|}
end_class

end_unit

