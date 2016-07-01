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
comment|/**  * Parses wordml 2003 format Excel files.  These are single xml files  * that predate ooxml.  *  * @see {@url https://en.wikipedia.org/wiki/Microsoft_Office_XML_formats}  */
end_comment

begin_class
specifier|public
class|class
name|SpreadsheetMLParser
extends|extends
name|AbstractXML2003Parser
block|{
specifier|final
specifier|static
name|String
name|CELL
init|=
literal|"cell"
decl_stmt|;
specifier|final
specifier|static
name|String
name|DATA
init|=
literal|"data"
decl_stmt|;
specifier|final
specifier|static
name|String
name|ROW
init|=
literal|"row"
decl_stmt|;
specifier|final
specifier|static
name|String
name|WORKSHEET
init|=
literal|"worksheet"
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
literal|"vnd.ms-spreadsheetml"
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
name|ExcelMLHandler
argument_list|(
name|ch
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
name|ExcelMLHandler
extends|extends
name|DefaultHandler
block|{
specifier|final
name|ContentHandler
name|handler
decl_stmt|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|href
init|=
literal|null
decl_stmt|;
name|boolean
name|inData
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inBody
init|=
literal|false
decl_stmt|;
specifier|public
name|ExcelMLHandler
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
name|MS_SPREADSHEET_URN
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
block|}
elseif|else
if|if
condition|(
name|TABLE
operator|.
name|equals
argument_list|(
name|localName
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
name|TABLE
argument_list|,
name|TABLE
argument_list|,
name|EMPTY_ATTRS
argument_list|)
expr_stmt|;
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
elseif|else
if|if
condition|(
name|WORKSHEET
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|String
name|worksheetName
init|=
name|attrs
operator|.
name|getValue
argument_list|(
name|MS_SPREADSHEET_URN
argument_list|,
literal|"Name"
argument_list|)
decl_stmt|;
name|AttributesImpl
name|xhtmlAttrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|worksheetName
operator|!=
literal|null
condition|)
block|{
name|xhtmlAttrs
operator|.
name|addAttribute
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|NAME_ATTR
argument_list|,
name|NAME_ATTR
argument_list|,
name|CDATA
argument_list|,
name|worksheetName
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
name|DIV
argument_list|,
name|DIV
argument_list|,
name|xhtmlAttrs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ROW
operator|.
name|equals
argument_list|(
name|localName
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
name|TR
argument_list|,
name|TR
argument_list|,
name|EMPTY_ATTRS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CELL
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|href
operator|=
name|attrs
operator|.
name|getValue
argument_list|(
name|MS_SPREADSHEET_URN
argument_list|,
literal|"HRef"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|TD
argument_list|,
name|TD
argument_list|,
name|EMPTY_ATTRS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DATA
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inData
operator|=
literal|true
expr_stmt|;
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
name|inData
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
name|MS_SPREADSHEET_URN
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
if|if
condition|(
name|TABLE
operator|.
name|equals
argument_list|(
name|localName
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
name|handler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|TABLE
argument_list|,
name|TABLE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|WORKSHEET
operator|.
name|equals
argument_list|(
name|localName
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
name|DIV
argument_list|,
name|DIV
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ROW
operator|.
name|equals
argument_list|(
name|localName
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
name|TR
argument_list|,
name|TR
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CELL
operator|.
name|equals
argument_list|(
name|localName
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
name|TD
argument_list|,
name|TD
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DATA
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
if|if
condition|(
name|href
operator|!=
literal|null
condition|)
block|{
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
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
name|href
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
name|A
argument_list|,
name|A
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
name|String
name|b
init|=
name|buffer
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
name|b
operator|=
literal|""
expr_stmt|;
block|}
name|char
index|[]
name|chars
init|=
name|b
operator|.
name|trim
argument_list|()
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|href
operator|!=
literal|null
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
name|A
argument_list|,
name|A
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|inData
operator|=
literal|false
expr_stmt|;
name|href
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

