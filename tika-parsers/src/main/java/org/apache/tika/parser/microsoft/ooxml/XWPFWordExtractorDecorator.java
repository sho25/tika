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
name|ooxml
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
name|ArrayList
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
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|PackagePart
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|extractor
operator|.
name|XWPFWordExtractor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|model
operator|.
name|XWPFCommentsDecorator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|model
operator|.
name|XWPFHeaderFooterPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|BodyType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|IBody
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|IBodyElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|ICell
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|IRunElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|ISDTContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFHeaderFooter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFHyperlink
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFHyperlinkRun
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFParagraph
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFPicture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFPictureData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFRun
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFSDT
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFSDTCell
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFStyle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFStyles
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFTableCell
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFTableRow
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
name|microsoft
operator|.
name|WordExtractor
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
name|microsoft
operator|.
name|WordExtractor
operator|.
name|TagAndStyle
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
name|xmlbeans
operator|.
name|XmlCursor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlbeans
operator|.
name|XmlException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlbeans
operator|.
name|XmlObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTBookmark
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTP
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTSectPr
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

begin_class
specifier|public
class|class
name|XWPFWordExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
comment|// could be improved by using the real delimiter in xchFollow [MS-DOC], v20140721, 2.4.6.3, Part 3, Step 3
specifier|private
specifier|static
specifier|final
name|String
name|LIST_DELIMITER
init|=
literal|" "
decl_stmt|;
specifier|private
name|XWPFDocument
name|document
decl_stmt|;
specifier|private
name|XWPFStyles
name|styles
decl_stmt|;
specifier|public
name|XWPFWordExtractorDecorator
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|XWPFWordExtractor
name|extractor
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|extractor
argument_list|)
expr_stmt|;
name|document
operator|=
operator|(
name|XWPFDocument
operator|)
name|extractor
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|styles
operator|=
name|document
operator|.
name|getStyles
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.poi.xwpf.extractor.XWPFWordExtractor#getText()      */
annotation|@
name|Override
specifier|protected
name|void
name|buildXHTML
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
name|XWPFHeaderFooterPolicy
name|hfPolicy
init|=
name|document
operator|.
name|getHeaderFooterPolicy
argument_list|()
decl_stmt|;
name|XWPFListManager
name|listManager
init|=
operator|new
name|XWPFListManager
argument_list|(
name|document
argument_list|)
decl_stmt|;
comment|// headers
if|if
condition|(
name|hfPolicy
operator|!=
literal|null
condition|)
block|{
name|extractHeaders
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
comment|// process text in the order that it occurs in
name|extractIBodyText
argument_list|(
name|document
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// then all document tables
if|if
condition|(
name|hfPolicy
operator|!=
literal|null
condition|)
block|{
name|extractFooters
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractIBodyText
parameter_list|(
name|IBody
name|bodyElement
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
for|for
control|(
name|IBodyElement
name|element
range|:
name|bodyElement
operator|.
name|getBodyElements
argument_list|()
control|)
block|{
if|if
condition|(
name|element
operator|instanceof
name|XWPFParagraph
condition|)
block|{
name|XWPFParagraph
name|paragraph
init|=
operator|(
name|XWPFParagraph
operator|)
name|element
decl_stmt|;
name|extractParagraph
argument_list|(
name|paragraph
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|element
operator|instanceof
name|XWPFTable
condition|)
block|{
name|XWPFTable
name|table
init|=
operator|(
name|XWPFTable
operator|)
name|element
decl_stmt|;
name|extractTable
argument_list|(
name|table
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|element
operator|instanceof
name|XWPFSDT
condition|)
block|{
name|extractSDT
argument_list|(
operator|(
name|XWPFSDT
operator|)
name|element
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|extractSDT
parameter_list|(
name|XWPFSDT
name|element
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
name|ISDTContent
name|content
init|=
name|element
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|String
name|tag
init|=
literal|"p"
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|content
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
name|tag
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractParagraph
parameter_list|(
name|XWPFParagraph
name|paragraph
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
comment|// If this paragraph is actually a whole new section, then
comment|//  it could have its own headers and footers
comment|// Check and handle if so
name|XWPFHeaderFooterPolicy
name|headerFooterPolicy
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|paragraph
operator|.
name|getCTP
argument_list|()
operator|.
name|getPPr
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|CTSectPr
name|ctSectPr
init|=
name|paragraph
operator|.
name|getCTP
argument_list|()
operator|.
name|getPPr
argument_list|()
operator|.
name|getSectPr
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctSectPr
operator|!=
literal|null
condition|)
block|{
name|headerFooterPolicy
operator|=
operator|new
name|XWPFHeaderFooterPolicy
argument_list|(
name|document
argument_list|,
name|ctSectPr
argument_list|)
expr_stmt|;
name|extractHeaders
argument_list|(
name|xhtml
argument_list|,
name|headerFooterPolicy
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Is this a paragraph, or a heading?
name|String
name|tag
init|=
literal|"p"
decl_stmt|;
name|String
name|styleClass
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|paragraph
operator|.
name|getStyleID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|XWPFStyle
name|style
init|=
name|styles
operator|.
name|getStyle
argument_list|(
name|paragraph
operator|.
name|getStyleID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|style
operator|!=
literal|null
operator|&&
name|style
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|TagAndStyle
name|tas
init|=
name|WordExtractor
operator|.
name|buildParagraphTagAndStyle
argument_list|(
name|style
operator|.
name|getName
argument_list|()
argument_list|,
name|paragraph
operator|.
name|getPartType
argument_list|()
operator|==
name|BodyType
operator|.
name|TABLECELL
argument_list|)
decl_stmt|;
name|tag
operator|=
name|tas
operator|.
name|getTag
argument_list|()
expr_stmt|;
name|styleClass
operator|=
name|tas
operator|.
name|getStyleClass
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|styleClass
operator|==
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
name|tag
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
name|tag
argument_list|,
literal|"class"
argument_list|,
name|styleClass
argument_list|)
expr_stmt|;
block|}
name|writeParagraphNumber
argument_list|(
name|paragraph
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// Output placeholder for any embedded docs:
comment|// TODO: replace w/ XPath/XQuery:
for|for
control|(
name|XWPFRun
name|run
range|:
name|paragraph
operator|.
name|getRuns
argument_list|()
control|)
block|{
name|XmlCursor
name|c
init|=
name|run
operator|.
name|getCTR
argument_list|()
operator|.
name|newCursor
argument_list|()
decl_stmt|;
name|c
operator|.
name|selectPath
argument_list|(
literal|"./*"
argument_list|)
expr_stmt|;
while|while
condition|(
name|c
operator|.
name|toNextSelection
argument_list|()
condition|)
block|{
name|XmlObject
name|o
init|=
name|c
operator|.
name|getObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|CTObject
condition|)
block|{
name|XmlCursor
name|c2
init|=
name|o
operator|.
name|newCursor
argument_list|()
decl_stmt|;
name|c2
operator|.
name|selectPath
argument_list|(
literal|"./*"
argument_list|)
expr_stmt|;
while|while
condition|(
name|c2
operator|.
name|toNextSelection
argument_list|()
condition|)
block|{
name|XmlObject
name|o2
init|=
name|c2
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|XmlObject
name|embedAtt
init|=
name|o2
operator|.
name|selectAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"Type"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|embedAtt
operator|!=
literal|null
operator|&&
name|embedAtt
operator|.
name|getDomNode
argument_list|()
operator|.
name|getNodeValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Embed"
argument_list|)
condition|)
block|{
comment|// Type is "Embed"
name|XmlObject
name|relIDAtt
init|=
name|o2
operator|.
name|selectAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships"
argument_list|,
literal|"id"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|relIDAtt
operator|!=
literal|null
condition|)
block|{
name|String
name|relID
init|=
name|relIDAtt
operator|.
name|getDomNode
argument_list|()
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|AttributesImpl
name|attributes
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"class"
argument_list|,
literal|"class"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"embedded"
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"id"
argument_list|,
literal|"id"
argument_list|,
literal|"CDATA"
argument_list|,
name|relID
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|c2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
name|c
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|// Attach bookmarks for the paragraph
comment|// (In future, we might put them in the right place, for now
comment|//  we just put them in the correct paragraph)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paragraph
operator|.
name|getCTP
argument_list|()
operator|.
name|sizeOfBookmarkStartArray
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CTBookmark
name|bookmark
init|=
name|paragraph
operator|.
name|getCTP
argument_list|()
operator|.
name|getBookmarkStartArray
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"a"
argument_list|,
literal|"name"
argument_list|,
name|bookmark
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
name|TmpFormatting
name|fmtg
init|=
operator|new
name|TmpFormatting
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Do the iruns
for|for
control|(
name|IRunElement
name|run
range|:
name|paragraph
operator|.
name|getIRuns
argument_list|()
control|)
block|{
if|if
condition|(
name|run
operator|instanceof
name|XWPFSDT
condition|)
block|{
name|fmtg
operator|=
name|closeStyleTags
argument_list|(
name|xhtml
argument_list|,
name|fmtg
argument_list|)
expr_stmt|;
name|processSDTRun
argument_list|(
operator|(
name|XWPFSDT
operator|)
name|run
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|//for now, we're ignoring formatting in sdt
comment|//if you hit an sdt reset to false
name|fmtg
operator|.
name|setBold
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fmtg
operator|.
name|setItalic
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fmtg
operator|=
name|processRun
argument_list|(
operator|(
name|XWPFRun
operator|)
name|run
argument_list|,
name|paragraph
argument_list|,
name|xhtml
argument_list|,
name|fmtg
argument_list|)
expr_stmt|;
block|}
block|}
name|closeStyleTags
argument_list|(
name|xhtml
argument_list|,
name|fmtg
argument_list|)
expr_stmt|;
comment|// Now do any comments for the paragraph
name|XWPFCommentsDecorator
name|comments
init|=
operator|new
name|XWPFCommentsDecorator
argument_list|(
name|paragraph
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|commentText
init|=
name|comments
operator|.
name|getCommentText
argument_list|()
decl_stmt|;
if|if
condition|(
name|commentText
operator|!=
literal|null
operator|&&
name|commentText
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|commentText
argument_list|)
expr_stmt|;
block|}
name|String
name|footnameText
init|=
name|paragraph
operator|.
name|getFootnoteText
argument_list|()
decl_stmt|;
if|if
condition|(
name|footnameText
operator|!=
literal|null
operator|&&
name|footnameText
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|footnameText
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
comment|// Also extract any paragraphs embedded in text boxes:
for|for
control|(
name|XmlObject
name|embeddedParagraph
range|:
name|paragraph
operator|.
name|getCTP
argument_list|()
operator|.
name|selectPath
argument_list|(
literal|"declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape' .//*/wps:txbx/w:txbxContent/w:p"
argument_list|)
control|)
block|{
name|extractParagraph
argument_list|(
operator|new
name|XWPFParagraph
argument_list|(
name|CTP
operator|.
name|Factory
operator|.
name|parse
argument_list|(
name|embeddedParagraph
operator|.
name|xmlText
argument_list|()
argument_list|)
argument_list|,
name|paragraph
operator|.
name|getBody
argument_list|()
argument_list|)
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
comment|// Finish this paragraph
name|xhtml
operator|.
name|endElement
argument_list|(
name|tag
argument_list|)
expr_stmt|;
if|if
condition|(
name|headerFooterPolicy
operator|!=
literal|null
condition|)
block|{
name|extractFooters
argument_list|(
name|xhtml
argument_list|,
name|headerFooterPolicy
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|writeParagraphNumber
parameter_list|(
name|XWPFParagraph
name|paragraph
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|paragraph
operator|.
name|getNumIlvl
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|number
init|=
name|listManager
operator|.
name|getFormattedNumber
argument_list|(
name|paragraph
argument_list|)
decl_stmt|;
if|if
condition|(
name|number
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|number
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|TmpFormatting
name|closeStyleTags
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|TmpFormatting
name|fmtg
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// Close any still open style tags
if|if
condition|(
name|fmtg
operator|.
name|isItalic
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
name|fmtg
operator|.
name|setItalic
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fmtg
operator|.
name|isBold
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|fmtg
operator|.
name|setBold
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|fmtg
return|;
block|}
specifier|private
name|TmpFormatting
name|processRun
parameter_list|(
name|XWPFRun
name|run
parameter_list|,
name|XWPFParagraph
name|paragraph
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|TmpFormatting
name|tfmtg
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
comment|// True if we are currently in the named style tag:
if|if
condition|(
name|run
operator|.
name|isBold
argument_list|()
operator|!=
name|tfmtg
operator|.
name|isBold
argument_list|()
condition|)
block|{
if|if
condition|(
name|tfmtg
operator|.
name|isItalic
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
name|tfmtg
operator|.
name|setItalic
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|run
operator|.
name|isBold
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
name|tfmtg
operator|.
name|setBold
argument_list|(
name|run
operator|.
name|isBold
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|run
operator|.
name|isItalic
argument_list|()
operator|!=
name|tfmtg
operator|.
name|isItalic
argument_list|()
condition|)
block|{
if|if
condition|(
name|run
operator|.
name|isItalic
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
block|}
name|tfmtg
operator|.
name|setItalic
argument_list|(
name|run
operator|.
name|isItalic
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|addedHREF
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|run
operator|instanceof
name|XWPFHyperlinkRun
condition|)
block|{
name|XWPFHyperlinkRun
name|linkRun
init|=
operator|(
name|XWPFHyperlinkRun
operator|)
name|run
decl_stmt|;
name|XWPFHyperlink
name|link
init|=
name|linkRun
operator|.
name|getHyperlink
argument_list|(
name|document
argument_list|)
decl_stmt|;
if|if
condition|(
name|link
operator|!=
literal|null
operator|&&
name|link
operator|.
name|getURL
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"a"
argument_list|,
literal|"href"
argument_list|,
name|link
operator|.
name|getURL
argument_list|()
argument_list|)
expr_stmt|;
name|addedHREF
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|linkRun
operator|.
name|getAnchor
argument_list|()
operator|!=
literal|null
operator|&&
name|linkRun
operator|.
name|getAnchor
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"a"
argument_list|,
literal|"href"
argument_list|,
literal|"#"
operator|+
name|linkRun
operator|.
name|getAnchor
argument_list|()
argument_list|)
expr_stmt|;
name|addedHREF
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|xhtml
operator|.
name|characters
argument_list|(
name|run
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// If we have any pictures, output them
for|for
control|(
name|XWPFPicture
name|picture
range|:
name|run
operator|.
name|getEmbeddedPictures
argument_list|()
control|)
block|{
if|if
condition|(
name|paragraph
operator|.
name|getDocument
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|XWPFPictureData
name|data
init|=
name|picture
operator|.
name|getPictureData
argument_list|()
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|AttributesImpl
name|attr
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"src"
argument_list|,
literal|"src"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"embedded:"
operator|+
name|data
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"alt"
argument_list|,
literal|"alt"
argument_list|,
literal|"CDATA"
argument_list|,
name|picture
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"img"
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"img"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|addedHREF
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
return|return
name|tfmtg
return|;
block|}
specifier|private
name|void
name|processSDTRun
parameter_list|(
name|XWPFSDT
name|run
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|run
operator|.
name|getContent
argument_list|()
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractTable
parameter_list|(
name|XWPFTable
name|table
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tbody"
argument_list|)
expr_stmt|;
for|for
control|(
name|XWPFTableRow
name|row
range|:
name|table
operator|.
name|getRows
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
for|for
control|(
name|ICell
name|cell
range|:
name|row
operator|.
name|getTableICells
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
if|if
condition|(
name|cell
operator|instanceof
name|XWPFTableCell
condition|)
block|{
name|extractIBodyText
argument_list|(
operator|(
name|XWPFTableCell
operator|)
name|cell
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cell
operator|instanceof
name|XWPFSDTCell
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
operator|(
operator|(
name|XWPFSDTCell
operator|)
name|cell
operator|)
operator|.
name|getContent
argument_list|()
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tbody"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractFooters
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|XWPFHeaderFooterPolicy
name|hfPolicy
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
comment|// footers
if|if
condition|(
name|hfPolicy
operator|.
name|getFirstPageFooter
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|extractHeaderText
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
operator|.
name|getFirstPageFooter
argument_list|()
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hfPolicy
operator|.
name|getEvenPageFooter
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|extractHeaderText
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
operator|.
name|getEvenPageFooter
argument_list|()
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hfPolicy
operator|.
name|getDefaultFooter
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|extractHeaderText
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
operator|.
name|getDefaultFooter
argument_list|()
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractHeaders
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|XWPFHeaderFooterPolicy
name|hfPolicy
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
if|if
condition|(
name|hfPolicy
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|hfPolicy
operator|.
name|getFirstPageHeader
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|extractHeaderText
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
operator|.
name|getFirstPageHeader
argument_list|()
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hfPolicy
operator|.
name|getEvenPageHeader
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|extractHeaderText
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
operator|.
name|getEvenPageHeader
argument_list|()
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hfPolicy
operator|.
name|getDefaultHeader
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|extractHeaderText
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
operator|.
name|getDefaultHeader
argument_list|()
argument_list|,
name|listManager
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractHeaderText
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|XWPFHeaderFooter
name|header
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
block|{
for|for
control|(
name|IBodyElement
name|e
range|:
name|header
operator|.
name|getBodyElements
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|XWPFParagraph
condition|)
block|{
name|extractParagraph
argument_list|(
operator|(
name|XWPFParagraph
operator|)
name|e
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|XWPFTable
condition|)
block|{
name|extractTable
argument_list|(
operator|(
name|XWPFTable
operator|)
name|e
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|XWPFSDT
condition|)
block|{
name|extractSDT
argument_list|(
operator|(
name|XWPFSDT
operator|)
name|e
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Word documents are simple, they only have the one      *  main part      */
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|PackagePart
argument_list|>
name|getMainDocumentParts
parameter_list|()
block|{
name|List
argument_list|<
name|PackagePart
argument_list|>
name|parts
init|=
operator|new
name|ArrayList
argument_list|<
name|PackagePart
argument_list|>
argument_list|()
decl_stmt|;
name|parts
operator|.
name|add
argument_list|(
name|document
operator|.
name|getPackagePart
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parts
return|;
block|}
specifier|private
class|class
name|TmpFormatting
block|{
specifier|private
name|boolean
name|bold
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|italic
init|=
literal|false
decl_stmt|;
specifier|private
name|TmpFormatting
parameter_list|(
name|boolean
name|bold
parameter_list|,
name|boolean
name|italic
parameter_list|)
block|{
name|this
operator|.
name|bold
operator|=
name|bold
expr_stmt|;
name|this
operator|.
name|italic
operator|=
name|italic
expr_stmt|;
block|}
specifier|public
name|boolean
name|isBold
parameter_list|()
block|{
return|return
name|bold
return|;
block|}
specifier|public
name|void
name|setBold
parameter_list|(
name|boolean
name|bold
parameter_list|)
block|{
name|this
operator|.
name|bold
operator|=
name|bold
expr_stmt|;
block|}
specifier|public
name|boolean
name|isItalic
parameter_list|()
block|{
return|return
name|italic
return|;
block|}
specifier|public
name|void
name|setItalic
parameter_list|(
name|boolean
name|italic
parameter_list|)
block|{
name|this
operator|.
name|italic
operator|=
name|italic
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

