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
name|XmlException
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

begin_class
specifier|public
class|class
name|XWPFWordExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
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
name|XWPFWordExtractor
name|extractor
parameter_list|)
block|{
name|super
argument_list|(
name|extractor
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
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
comment|// headers
name|extractHeaders
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
argument_list|)
expr_stmt|;
comment|// process text in the order that it occurs in
name|extractIBodyText
argument_list|(
name|document
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// then all document tables
name|extractFooters
argument_list|(
name|xhtml
argument_list|,
name|hfPolicy
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractIBodyText
parameter_list|(
name|IBody
name|bodyElement
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
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|extractParagraph
parameter_list|(
name|XWPFParagraph
name|paragraph
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
for|for
control|(
name|CTBookmark
name|bookmark
range|:
name|paragraph
operator|.
name|getCTP
argument_list|()
operator|.
name|getBookmarkStartList
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|bookmark
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
comment|// Do the text
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
name|List
argument_list|<
name|String
argument_list|>
name|tags
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|tags
operator|.
name|add
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
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
name|tags
operator|.
name|add
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
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
name|tags
operator|.
name|add
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
name|tags
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"img"
argument_list|,
literal|"src"
argument_list|,
literal|"embedded:"
operator|+
name|data
operator|.
name|getFileName
argument_list|()
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
block|}
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
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractTable
parameter_list|(
name|XWPFTable
name|table
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
name|XWPFTableCell
name|cell
range|:
name|row
operator|.
name|getTableCells
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
name|extractIBodyText
argument_list|(
name|cell
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
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
parameter_list|)
throws|throws
name|SAXException
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
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|hfPolicy
operator|.
name|getFirstPageFooter
argument_list|()
operator|.
name|getText
argument_list|()
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
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|hfPolicy
operator|.
name|getEvenPageFooter
argument_list|()
operator|.
name|getText
argument_list|()
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
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|hfPolicy
operator|.
name|getDefaultFooter
argument_list|()
operator|.
name|getText
argument_list|()
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
parameter_list|)
throws|throws
name|SAXException
block|{
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
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|hfPolicy
operator|.
name|getFirstPageHeader
argument_list|()
operator|.
name|getText
argument_list|()
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
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|hfPolicy
operator|.
name|getEvenPageHeader
argument_list|()
operator|.
name|getText
argument_list|()
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
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|hfPolicy
operator|.
name|getDefaultHeader
argument_list|()
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

