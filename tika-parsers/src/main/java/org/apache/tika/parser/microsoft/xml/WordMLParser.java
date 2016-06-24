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
name|parser
operator|.
name|microsoft
operator|.
name|xml
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Locale
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|io
operator|.
name|TikaInputStream
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
name|sax
operator|.
name|TeeContentHandler
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
name|xml
operator|.
name|sax
operator|.
name|Attributes
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Parses wordml 2003 format word files.  These are single xml files  * that predate ooxml.  *  * @see {@url https://en.wikipedia.org/wiki/Microsoft_Office_XML_formats}  */
end_comment

begin_class
specifier|public
class|class
name|WordMLParser
extends|extends
name|AbstractXML2003Parser
block|{
comment|//map between wordml and xhtml entities
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|WORDML_TO_XHTML
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//ignore all characters within these elements
specifier|private
specifier|final
specifier|static
name|Set
argument_list|<
name|QName
argument_list|>
name|IGNORE_CHARACTERS
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|QName
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|MEDIA_TYPE
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-wordml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|MEDIA_TYPE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
static|static
block|{
name|WORDML_TO_XHTML
operator|.
name|put
argument_list|(
name|P
argument_list|,
name|P
argument_list|)
expr_stmt|;
name|WORDML_TO_XHTML
operator|.
name|put
argument_list|(
literal|"tbl"
argument_list|,
name|TABLE
argument_list|)
expr_stmt|;
name|WORDML_TO_XHTML
operator|.
name|put
argument_list|(
name|TR
argument_list|,
name|TR
argument_list|)
expr_stmt|;
name|WORDML_TO_XHTML
operator|.
name|put
argument_list|(
literal|"tc"
argument_list|,
name|TD
argument_list|)
expr_stmt|;
comment|//not a typo -- table cell -> tc
name|IGNORE_CHARACTERS
operator|.
name|add
argument_list|(
operator|new
name|QName
argument_list|(
name|WORD_ML_URL
argument_list|,
name|HLINK
argument_list|)
argument_list|)
expr_stmt|;
name|IGNORE_CHARACTERS
operator|.
name|add
argument_list|(
operator|new
name|QName
argument_list|(
name|WORD_ML_URL
argument_list|,
name|PICT
argument_list|)
argument_list|)
expr_stmt|;
name|IGNORE_CHARACTERS
operator|.
name|add
argument_list|(
operator|new
name|QName
argument_list|(
name|WORD_ML_URL
argument_list|,
name|BIN_DATA
argument_list|)
argument_list|)
expr_stmt|;
name|IGNORE_CHARACTERS
operator|.
name|add
argument_list|(
operator|new
name|QName
argument_list|(
name|MS_OFFICE_PROPERTIES_URN
argument_list|,
name|DOCUMENT_PROPERTIES
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
return|return
name|SUPPORTED_TYPES
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ContentHandler
name|getContentHandler
parameter_list|(
name|ContentHandler
name|ch
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
block|{
name|EmbeddedDocumentExtractor
name|ex
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
name|ex
operator|==
literal|null
condition|)
block|{
name|ex
operator|=
operator|new
name|ParsingEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TeeContentHandler
argument_list|(
name|super
operator|.
name|getContentHandler
argument_list|(
name|ch
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
argument_list|,
operator|new
name|WordMLHandler
argument_list|(
name|ch
argument_list|)
argument_list|,
operator|new
name|HyperlinkHandler
argument_list|(
name|ch
argument_list|,
name|WORD_ML_URL
argument_list|)
argument_list|,
operator|new
name|PictHandler
argument_list|(
name|ch
argument_list|,
name|ex
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setContentType
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|MEDIA_TYPE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|WordMLHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|final
name|ContentHandler
name|handler
decl_stmt|;
specifier|private
name|boolean
name|ignoreCharacters
decl_stmt|;
specifier|private
name|boolean
name|inBody
init|=
literal|false
decl_stmt|;
comment|//use inP to keep track of whether the handler is
comment|//in a paragraph or not.<p><p></p></p> was allowed
comment|//in wordml. Use this boolean to prevent<p> within<p>
specifier|private
name|boolean
name|inP
decl_stmt|;
specifier|public
name|WordMLHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attrs
parameter_list|)
throws|throws
name|SAXException
block|{
name|localName
operator|=
name|localName
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
if|if
condition|(
name|WORD_ML_URL
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
if|if
condition|(
name|BODY
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inBody
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|String
name|html
init|=
name|WORDML_TO_XHTML
operator|.
name|get
argument_list|(
name|localName
argument_list|)
decl_stmt|;
if|if
condition|(
name|html
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|P
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|//close p if already in a p to prevent nested<p>
if|if
condition|(
name|inP
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|P
argument_list|,
name|P
argument_list|)
expr_stmt|;
block|}
name|inP
operator|=
literal|true
expr_stmt|;
block|}
name|handler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|html
argument_list|,
name|html
argument_list|,
name|EMPTY_ATTRS
argument_list|)
expr_stmt|;
if|if
condition|(
name|html
operator|.
name|equals
argument_list|(
name|TABLE
argument_list|)
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|TBODY
argument_list|,
name|TBODY
argument_list|,
name|EMPTY_ATTRS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|IGNORE_CHARACTERS
operator|.
name|contains
argument_list|(
operator|new
name|QName
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|)
argument_list|)
condition|)
block|{
name|ignoreCharacters
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|str
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|ignoreCharacters
operator|&&
name|inBody
condition|)
block|{
name|handler
operator|.
name|characters
argument_list|(
name|str
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|WORD_ML_URL
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
comment|//for now, don't bother checking for end of body...if there's any text
comment|//after the close of body, we should extract it
name|localName
operator|=
name|localName
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|String
name|html
init|=
name|WORDML_TO_XHTML
operator|.
name|get
argument_list|(
name|localName
argument_list|)
decl_stmt|;
if|if
condition|(
name|html
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|html
operator|.
name|equals
argument_list|(
name|TABLE
argument_list|)
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|TBODY
argument_list|,
name|TBODY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|P
operator|.
name|equals
argument_list|(
name|html
argument_list|)
operator|&&
operator|!
name|inP
condition|)
block|{
comment|//start p if not already in one to prevent non-matching<p>
name|handler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|P
argument_list|,
name|P
argument_list|,
name|EMPTY_ATTRS
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|html
argument_list|,
name|html
argument_list|)
expr_stmt|;
if|if
condition|(
name|P
operator|.
name|equals
argument_list|(
name|html
argument_list|)
condition|)
block|{
name|inP
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|IGNORE_CHARACTERS
operator|.
name|contains
argument_list|(
operator|new
name|QName
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|)
argument_list|)
condition|)
block|{
name|ignoreCharacters
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|PictHandler
extends|extends
name|DefaultHandler
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ContentHandler
name|handler
decl_stmt|;
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
decl_stmt|;
name|boolean
name|inPict
init|=
literal|false
decl_stmt|;
name|boolean
name|inBin
init|=
literal|false
decl_stmt|;
name|String
name|pictName
init|=
literal|null
decl_stmt|;
specifier|final
name|Base64
name|base64
init|=
operator|new
name|Base64
argument_list|()
decl_stmt|;
specifier|public
name|PictHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|embeddedDocumentExtractor
operator|=
name|embeddedDocumentExtractor
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attrs
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|WORD_ML_URL
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
if|if
condition|(
name|PICT
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inPict
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BIN_DATA
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inBin
operator|=
literal|true
expr_stmt|;
name|pictName
operator|=
name|attrs
operator|.
name|getValue
argument_list|(
name|WORD_ML_URL
argument_list|,
name|NAME_ATTR
argument_list|)
expr_stmt|;
if|if
condition|(
name|pictName
operator|!=
literal|null
condition|)
block|{
name|pictName
operator|=
name|pictName
operator|.
name|replaceFirst
argument_list|(
literal|"wordml://"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|str
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|inBin
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|str
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inPict
condition|)
block|{
name|handler
operator|.
name|characters
argument_list|(
name|str
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|WORD_ML_URL
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|PICT
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inPict
operator|=
literal|false
expr_stmt|;
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|pictName
operator|!=
literal|null
condition|)
block|{
name|attrs
operator|.
name|addAttribute
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|HREF
argument_list|,
name|HREF
argument_list|,
name|CDATA
argument_list|,
name|pictName
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|IMG
argument_list|,
name|IMG
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|IMG
argument_list|,
name|IMG
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BIN_DATA
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inBin
operator|=
literal|false
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|base64
operator|.
name|decode
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
init|(
name|TikaInputStream
name|is
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|bytes
argument_list|)
init|)
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|pictName
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|pictName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|embeddedDocumentExtractor
operator|.
name|shouldParseEmbedded
argument_list|(
name|metadata
argument_list|)
condition|)
block|{
name|embeddedDocumentExtractor
operator|.
name|parseEmbedded
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//log
block|}
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

