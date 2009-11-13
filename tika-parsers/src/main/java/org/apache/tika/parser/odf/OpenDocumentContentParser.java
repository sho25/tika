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
name|odf
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|XHTMLContentHandler
operator|.
name|XHTML
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
name|util
operator|.
name|BitSet
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
name|Stack
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
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
name|SAXParserFactory
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
name|io
operator|.
name|CloseShieldInputStream
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
name|ElementMappingContentHandler
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
name|OfflineContentHandler
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|ElementMappingContentHandler
operator|.
name|TargetElement
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
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Parser for ODF<code>content.xml</code> files.  */
end_comment

begin_class
specifier|public
class|class
name|OpenDocumentContentParser
implements|implements
name|Parser
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_NS
init|=
literal|"urn:oasis:names:tc:opendocument:xmlns:text:1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TABLE_NS
init|=
literal|"urn:oasis:names:tc:opendocument:xmlns:table:1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OFFICE_NS
init|=
literal|"urn:oasis:names:tc:opendocument:xmlns:office:1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SVG_NS
init|=
literal|"urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PRESENTATION_NS
init|=
literal|"urn:oasis:names:tc:opendocument:xmlns:presentation:1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DRAW_NS
init|=
literal|"urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|XLINK_NS
init|=
literal|"http://www.w3.org/1999/xlink"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|char
index|[]
name|TAB
init|=
operator|new
name|char
index|[]
block|{
literal|'\t'
block|}
decl_stmt|;
comment|/**      * Mappings between ODF tag names and XHTML tag names      * (including attributes). All other tag names/attributes are ignored      * and left out from event stream.       */
specifier|private
specifier|static
specifier|final
name|HashMap
argument_list|<
name|QName
argument_list|,
name|TargetElement
argument_list|>
name|MAPPINGS
init|=
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|TargetElement
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
comment|// general mappings of text:-tags
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TEXT_NS
argument_list|,
literal|"p"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
comment|// text:h-tags are mapped specifically in startElement/endElement
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TEXT_NS
argument_list|,
literal|"line-break"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"br"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TEXT_NS
argument_list|,
literal|"list"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"ul"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TEXT_NS
argument_list|,
literal|"list-item"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"li"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TEXT_NS
argument_list|,
literal|"note"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"div"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|OFFICE_NS
argument_list|,
literal|"annotation"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"div"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|PRESENTATION_NS
argument_list|,
literal|"notes"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"div"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|DRAW_NS
argument_list|,
literal|"object"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"object"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|DRAW_NS
argument_list|,
literal|"text-box"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"div"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|SVG_NS
argument_list|,
literal|"title"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"span"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|SVG_NS
argument_list|,
literal|"desc"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"span"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TEXT_NS
argument_list|,
literal|"span"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"span"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|HashMap
argument_list|<
name|QName
argument_list|,
name|QName
argument_list|>
name|aAttsMapping
init|=
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|QName
argument_list|>
argument_list|()
decl_stmt|;
name|aAttsMapping
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|XLINK_NS
argument_list|,
literal|"href"
argument_list|)
argument_list|,
operator|new
name|QName
argument_list|(
literal|"href"
argument_list|)
argument_list|)
expr_stmt|;
name|aAttsMapping
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|XLINK_NS
argument_list|,
literal|"title"
argument_list|)
argument_list|,
operator|new
name|QName
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TEXT_NS
argument_list|,
literal|"a"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"a"
argument_list|,
name|aAttsMapping
argument_list|)
argument_list|)
expr_stmt|;
comment|// create HTML tables from table:-tags
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TABLE_NS
argument_list|,
literal|"table"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"table"
argument_list|)
argument_list|)
expr_stmt|;
comment|// repeating of rows is ignored; for columns, see below!
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TABLE_NS
argument_list|,
literal|"table-row"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"tr"
argument_list|)
argument_list|)
expr_stmt|;
comment|// special mapping for rowspan/colspan attributes
specifier|final
name|HashMap
argument_list|<
name|QName
argument_list|,
name|QName
argument_list|>
name|tableCellAttsMapping
init|=
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|QName
argument_list|>
argument_list|()
decl_stmt|;
name|tableCellAttsMapping
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TABLE_NS
argument_list|,
literal|"number-columns-spanned"
argument_list|)
argument_list|,
operator|new
name|QName
argument_list|(
literal|"colspan"
argument_list|)
argument_list|)
expr_stmt|;
name|tableCellAttsMapping
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TABLE_NS
argument_list|,
literal|"number-rows-spanned"
argument_list|)
argument_list|,
operator|new
name|QName
argument_list|(
literal|"rowspan"
argument_list|)
argument_list|)
expr_stmt|;
comment|/* TODO: The following is not correct, the cell should be repeated not spanned!          * Code generates a HTML cell, spanning all repeated columns, to make the cell look correct.          * Problems may occur when both spanning and repeating is given, which is not allowed by spec.          * Cell spanning instead of repeating  is not a problem, because OpenOffice uses it          * only for empty cells.          */
name|tableCellAttsMapping
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TABLE_NS
argument_list|,
literal|"number-columns-repeated"
argument_list|)
argument_list|,
operator|new
name|QName
argument_list|(
literal|"colspan"
argument_list|)
argument_list|)
expr_stmt|;
name|MAPPINGS
operator|.
name|put
argument_list|(
operator|new
name|QName
argument_list|(
name|TABLE_NS
argument_list|,
literal|"table-cell"
argument_list|)
argument_list|,
operator|new
name|TargetElement
argument_list|(
name|XHTML
argument_list|,
literal|"td"
argument_list|,
name|tableCellAttsMapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
specifier|final
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
name|DefaultHandler
name|dh
init|=
operator|new
name|ElementMappingContentHandler
argument_list|(
name|xhtml
argument_list|,
name|MAPPINGS
argument_list|)
block|{
specifier|private
specifier|final
name|BitSet
name|textNodeStack
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
specifier|private
name|int
name|nodeDepth
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|completelyFiltered
init|=
literal|0
decl_stmt|;
specifier|private
name|Stack
argument_list|<
name|String
argument_list|>
name|headingStack
init|=
operator|new
name|Stack
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// only forward content of tags from text:-namespace
if|if
condition|(
name|completelyFiltered
operator|==
literal|0
operator|&&
name|nodeDepth
operator|>
literal|0
operator|&&
name|textNodeStack
operator|.
name|get
argument_list|(
name|nodeDepth
operator|-
literal|1
argument_list|)
condition|)
block|{
name|super
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|// helper for checking tags which need complete filtering
comment|// (with sub-tags)
specifier|private
name|boolean
name|needsCompleteFiltering
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
if|if
condition|(
name|TEXT_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
return|return
name|localName
operator|.
name|endsWith
argument_list|(
literal|"-template"
argument_list|)
operator|||
name|localName
operator|.
name|endsWith
argument_list|(
literal|"-style"
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|TABLE_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
return|return
literal|"covered-table-cell"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// map the heading level to<hX> HTML tags
specifier|private
name|String
name|getXHTMLHeaderTagName
parameter_list|(
name|Attributes
name|atts
parameter_list|)
block|{
name|String
name|depthStr
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|TEXT_NS
argument_list|,
literal|"outline-level"
argument_list|)
decl_stmt|;
if|if
condition|(
name|depthStr
operator|==
literal|null
condition|)
block|{
return|return
literal|"h1"
return|;
block|}
name|int
name|depth
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|depthStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|depth
operator|>=
literal|6
condition|)
block|{
return|return
literal|"h6"
return|;
block|}
elseif|else
if|if
condition|(
name|depth
operator|<=
literal|1
condition|)
block|{
return|return
literal|"h1"
return|;
block|}
else|else
block|{
return|return
literal|"h"
operator|+
name|depth
return|;
block|}
block|}
comment|/**              * Check if a node is a text node              */
specifier|private
name|boolean
name|isTextNode
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
if|if
condition|(
name|TEXT_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|SVG_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
return|return
literal|"title"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
operator|||
literal|"desc"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// keep track of current node type. If it is a text node,
comment|// a bit at the current depth ist set in textNodeStack.
comment|// characters() checks the top bit to determine, if the
comment|// actual node is a text node to print out nodeDepth contains
comment|// the depth of the current node and also marks top of stack.
assert|assert
name|nodeDepth
operator|>=
literal|0
assert|;
name|textNodeStack
operator|.
name|set
argument_list|(
name|nodeDepth
operator|++
argument_list|,
name|isTextNode
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
argument_list|)
expr_stmt|;
comment|// filter *all* content of some tags
assert|assert
name|completelyFiltered
operator|>=
literal|0
assert|;
if|if
condition|(
name|needsCompleteFiltering
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
condition|)
block|{
name|completelyFiltered
operator|++
expr_stmt|;
block|}
comment|// call next handler if no filtering
if|if
condition|(
name|completelyFiltered
operator|==
literal|0
condition|)
block|{
comment|// special handling of text:h, that are directly passed
comment|// to xhtml handler
if|if
condition|(
name|TEXT_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
operator|&&
literal|"h"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
name|headingStack
operator|.
name|push
argument_list|(
name|getXHTMLHeaderTagName
argument_list|(
name|atts
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespaceURI
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
comment|// call next handler if no filtering
if|if
condition|(
name|completelyFiltered
operator|==
literal|0
condition|)
block|{
comment|// special handling of text:h, that are directly passed
comment|// to xhtml handler
if|if
condition|(
name|TEXT_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
operator|&&
literal|"h"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
name|headingStack
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|endElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
comment|// special handling of tabulators
if|if
condition|(
name|TEXT_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"tab-stop"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
operator|||
literal|"tab"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|this
operator|.
name|characters
argument_list|(
name|TAB
argument_list|,
literal|0
argument_list|,
name|TAB
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// revert filter for *all* content of some tags
if|if
condition|(
name|needsCompleteFiltering
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
condition|)
block|{
name|completelyFiltered
operator|--
expr_stmt|;
block|}
assert|assert
name|completelyFiltered
operator|>=
literal|0
assert|;
comment|// reduce current node depth
name|nodeDepth
operator|--
expr_stmt|;
assert|assert
name|nodeDepth
operator|>=
literal|0
assert|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
comment|// remove prefix mappings as they should not occur in XHTML
block|}
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
comment|// remove prefix mappings as they should not occur in XHTML
block|}
block|}
decl_stmt|;
try|try
block|{
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setFeature
argument_list|(
name|XMLConstants
operator|.
name|FEATURE_SECURE_PROCESSING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
operator|new
name|OfflineContentHandler
argument_list|(
operator|new
name|NSNormalizerContentHandler
argument_list|(
name|dh
argument_list|)
argument_list|)
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
name|TikaException
argument_list|(
literal|"XML parser configuration error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0.      */
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

