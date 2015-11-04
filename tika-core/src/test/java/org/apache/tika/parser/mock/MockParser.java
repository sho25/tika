begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|mock
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|Date
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
name|extractor
operator|.
name|EmbeddedDocumentExtractor
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
name|extractor
operator|.
name|ParsingEmbeddedDocumentExtractor
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
name|metadata
operator|.
name|Metadata
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
name|metadata
operator|.
name|TikaMetadataKeys
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
name|parser
operator|.
name|AbstractParser
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
name|ParseContext
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
name|sax
operator|.
name|EmbeddedContentHandler
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
name|sax
operator|.
name|XHTMLContentHandler
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
name|NamedNodeMap
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
name|ContentHandler
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
comment|/**  * This class enables mocking of parser behavior for use in testing  * wrappers and drivers of parsers.  *<p>  * See resources/test-documents/mock/example.xml in tika-parsers/test for the documentation  * of all the options for this MockParser.  *<p>  * Tests for this class are in tika-parsers.  *<p>  * See also {@link org.apache.tika.parser.DummyParser} for another option.  */
end_comment

begin_class
specifier|public
class|class
name|MockParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
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
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"mock+xml"
argument_list|)
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|types
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|Document
name|doc
init|=
literal|null
decl_stmt|;
name|DocumentBuilderFactory
name|fact
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|docBuilder
init|=
literal|null
decl_stmt|;
try|try
block|{
name|docBuilder
operator|=
name|fact
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
name|doc
operator|=
name|docBuilder
operator|.
name|parse
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
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
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Node
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|NodeList
name|actions
init|=
name|root
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|actions
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|executeAction
argument_list|(
name|actions
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|executeAction
parameter_list|(
name|Node
name|action
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
if|if
condition|(
name|action
operator|.
name|getNodeType
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return;
block|}
name|String
name|name
init|=
name|action
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"metadata"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|metadata
argument_list|(
name|action
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"write"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|write
argument_list|(
name|action
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"throw"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|throwIt
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"hang"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|hang
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"oom"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|kabOOM
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"print_out"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"print_err"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|print
argument_list|(
name|action
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"embedded"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|handleEmbedded
argument_list|(
name|action
argument_list|,
name|xhtml
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"throwIllegalChars"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|throwIllegalChars
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Didn't recognize mock action: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|throwIllegalChars
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't say \u0000 in xml or \u0001 or \u0002 or \u0003"
argument_list|)
throw|;
block|}
specifier|private
name|void
name|handleEmbedded
parameter_list|(
name|Node
name|action
parameter_list|,
name|XHTMLContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|TikaException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|String
name|fileName
init|=
literal|""
decl_stmt|;
name|String
name|contentType
init|=
literal|""
decl_stmt|;
name|NamedNodeMap
name|attrs
init|=
name|action
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|attrs
operator|!=
literal|null
condition|)
block|{
name|Node
name|n
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"filename"
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|fileName
operator|=
name|n
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
name|n
operator|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"content-type"
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|contentType
operator|=
name|n
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|embeddedText
init|=
name|action
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
name|EmbeddedDocumentExtractor
name|extractor
init|=
name|getEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m
operator|.
name|set
argument_list|(
name|TikaMetadataKeys
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|contentType
argument_list|)
condition|)
block|{
name|m
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|contentType
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|embeddedText
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|extractor
operator|.
name|parseEmbedded
argument_list|(
name|is
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|m
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|EmbeddedDocumentExtractor
name|getEmbeddedDocumentExtractor
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|EmbeddedDocumentExtractor
name|extractor
init|=
name|context
operator|.
name|get
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|extractor
operator|==
literal|null
condition|)
block|{
name|Parser
name|p
init|=
name|context
operator|.
name|get
argument_list|(
name|Parser
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
operator|new
name|MockParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|extractor
operator|=
operator|new
name|ParsingEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|extractor
return|;
block|}
specifier|private
name|void
name|print
parameter_list|(
name|Node
name|action
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|content
init|=
name|action
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"print_out"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"print_err"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must be print_out or print_err"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|hang
parameter_list|(
name|Node
name|action
parameter_list|)
block|{
name|boolean
name|interruptible
init|=
literal|true
decl_stmt|;
name|boolean
name|heavy
init|=
literal|false
decl_stmt|;
name|long
name|millis
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|pulseMillis
init|=
operator|-
literal|1
decl_stmt|;
name|NamedNodeMap
name|attrs
init|=
name|action
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Node
name|iNode
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"interruptible"
argument_list|)
decl_stmt|;
if|if
condition|(
name|iNode
operator|!=
literal|null
condition|)
block|{
name|interruptible
operator|=
operator|(
literal|"true"
operator|.
name|equals
argument_list|(
name|iNode
operator|.
name|getNodeValue
argument_list|()
argument_list|)
operator|)
expr_stmt|;
block|}
name|Node
name|hNode
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"heavy"
argument_list|)
decl_stmt|;
if|if
condition|(
name|hNode
operator|!=
literal|null
condition|)
block|{
name|heavy
operator|=
operator|(
literal|"true"
operator|.
name|equals
argument_list|(
name|hNode
operator|.
name|getNodeValue
argument_list|()
argument_list|)
operator|)
expr_stmt|;
block|}
name|Node
name|mNode
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"millis"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mNode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must specify \"millis\" attribute for hang."
argument_list|)
throw|;
block|}
name|String
name|millisString
init|=
name|mNode
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
try|try
block|{
name|millis
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|millisString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Value for \"millis\" attribute must be a long."
argument_list|)
throw|;
block|}
if|if
condition|(
name|heavy
condition|)
block|{
name|Node
name|pNode
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"pulse_millis"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pNode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must specify attribute \"pulse_millis\" if the hang is \"heavy\""
argument_list|)
throw|;
block|}
name|String
name|pulseMillisString
init|=
name|mNode
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
try|try
block|{
name|pulseMillis
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|pulseMillisString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Value for \"millis\" attribute must be a long."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|heavy
condition|)
block|{
name|hangHeavy
argument_list|(
name|millis
argument_list|,
name|pulseMillis
argument_list|,
name|interruptible
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sleep
argument_list|(
name|millis
argument_list|,
name|interruptible
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|throwIt
parameter_list|(
name|Node
name|action
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|NamedNodeMap
name|attrs
init|=
name|action
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|String
name|className
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"class"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|String
name|msg
init|=
name|action
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
name|throwIt
argument_list|(
name|className
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|metadata
parameter_list|(
name|Node
name|action
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|NamedNodeMap
name|attrs
init|=
name|action
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
comment|//throws npe unless there is a name
name|String
name|name
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|action
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
name|Node
name|actionType
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"action"
argument_list|)
decl_stmt|;
if|if
condition|(
name|actionType
operator|==
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|"set"
operator|.
name|equals
argument_list|(
name|actionType
operator|.
name|getNodeValue
argument_list|()
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|write
parameter_list|(
name|Node
name|action
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
name|NamedNodeMap
name|attrs
init|=
name|action
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Node
name|eNode
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"element"
argument_list|)
decl_stmt|;
name|String
name|elementType
init|=
literal|"p"
decl_stmt|;
if|if
condition|(
name|eNode
operator|!=
literal|null
condition|)
block|{
name|elementType
operator|=
name|eNode
operator|.
name|getTextContent
argument_list|()
expr_stmt|;
block|}
name|String
name|text
init|=
name|action
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|throwIt
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|Throwable
name|t
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
operator|||
name|msg
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
try|try
block|{
name|t
operator|=
operator|(
name|Throwable
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"couldn't create throwable class:"
operator|+
name|className
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
argument_list|>
name|con
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|t
operator|=
operator|(
name|Throwable
operator|)
name|con
operator|.
name|newInstance
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"couldn't create throwable class:"
operator|+
name|className
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|t
operator|instanceof
name|SAXException
condition|)
block|{
throw|throw
operator|(
name|SAXException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|TikaException
condition|)
block|{
throw|throw
operator|(
name|TikaException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|t
throw|;
block|}
else|else
block|{
comment|//wrap the throwable in a RuntimeException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|kabOOM
parameter_list|()
block|{
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|ints
init|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
index|[]
name|intArr
init|=
operator|new
name|int
index|[
literal|32000
index|]
decl_stmt|;
name|ints
operator|.
name|add
argument_list|(
name|intArr
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|hangHeavy
parameter_list|(
name|long
name|maxMillis
parameter_list|,
name|long
name|pulseCheckMillis
parameter_list|,
name|boolean
name|interruptible
parameter_list|)
block|{
comment|//do some heavy computation and occasionally check for
comment|//whether time has exceeded maxMillis (see TIKA-1132 for inspiration)
comment|//or whether the thread was interrupted
name|long
name|start
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|int
name|lastChecked
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|;
name|j
operator|++
control|)
block|{
name|double
name|div
init|=
operator|(
name|double
operator|)
name|i
operator|/
operator|(
name|double
operator|)
name|j
decl_stmt|;
name|lastChecked
operator|++
expr_stmt|;
if|if
condition|(
name|lastChecked
operator|>
name|pulseCheckMillis
condition|)
block|{
name|lastChecked
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|interruptible
operator|&&
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
return|return;
block|}
name|long
name|elapsed
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|elapsed
operator|>
name|maxMillis
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
block|}
block|}
specifier|private
name|void
name|sleep
parameter_list|(
name|long
name|maxMillis
parameter_list|,
name|boolean
name|isInterruptible
parameter_list|)
block|{
name|long
name|start
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|millisRemaining
init|=
name|maxMillis
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|millisRemaining
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
if|if
condition|(
name|isInterruptible
condition|)
block|{
return|return;
block|}
block|}
name|long
name|elapsed
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
decl_stmt|;
name|millisRemaining
operator|=
name|maxMillis
operator|-
name|elapsed
expr_stmt|;
if|if
condition|(
name|millisRemaining
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

