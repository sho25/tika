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
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hslf
operator|.
name|model
operator|.
name|Comment
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
name|hslf
operator|.
name|model
operator|.
name|HeadersFooters
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
name|hslf
operator|.
name|model
operator|.
name|OLEShape
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFMasterSheet
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFNotes
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFObjectData
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFPictureData
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFShape
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFSlide
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFSlideShow
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFTable
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFTableCell
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFTextParagraph
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFTextRun
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
name|hslf
operator|.
name|usermodel
operator|.
name|HSLFTextShape
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
name|poifs
operator|.
name|filesystem
operator|.
name|DirectoryNode
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
name|poifs
operator|.
name|filesystem
operator|.
name|NPOIFSFileSystem
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
name|HSLFExtractor
extends|extends
name|AbstractPOIFSExtractor
block|{
specifier|public
name|HSLFExtractor
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|parse
parameter_list|(
name|NPOIFSFileSystem
name|filesystem
parameter_list|,
name|XHTMLContentHandler
name|xhtml
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
name|filesystem
operator|.
name|getRoot
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|parse
parameter_list|(
name|DirectoryNode
name|root
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|HSLFSlideShow
name|ss
init|=
operator|new
name|HSLFSlideShow
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|HSLFSlide
argument_list|>
name|_slides
init|=
name|ss
operator|.
name|getSlides
argument_list|()
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"slideShow"
argument_list|)
expr_stmt|;
comment|/* Iterate over slides and extract text */
for|for
control|(
name|HSLFSlide
name|slide
range|:
name|_slides
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"slide"
argument_list|)
expr_stmt|;
comment|// Slide header, if present
name|HeadersFooters
name|hf
init|=
name|slide
operator|.
name|getHeadersFooters
argument_list|()
decl_stmt|;
if|if
condition|(
name|hf
operator|!=
literal|null
operator|&&
name|hf
operator|.
name|isHeaderVisible
argument_list|()
operator|&&
name|hf
operator|.
name|getHeaderText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-header"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|hf
operator|.
name|getHeaderText
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
comment|// Slide master, if present
name|extractMaster
argument_list|(
name|xhtml
argument_list|,
name|slide
operator|.
name|getMasterSheet
argument_list|()
argument_list|)
expr_stmt|;
comment|// Slide text
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-content"
argument_list|)
expr_stmt|;
name|textRunsToText
argument_list|(
name|xhtml
argument_list|,
name|slide
operator|.
name|getTextParagraphs
argument_list|()
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
comment|// Table text
for|for
control|(
name|HSLFShape
name|shape
range|:
name|slide
operator|.
name|getShapes
argument_list|()
control|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|HSLFTable
condition|)
block|{
name|extractTableText
argument_list|(
name|xhtml
argument_list|,
operator|(
name|HSLFTable
operator|)
name|shape
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Slide footer, if present
if|if
condition|(
name|hf
operator|!=
literal|null
operator|&&
name|hf
operator|.
name|isFooterVisible
argument_list|()
operator|&&
name|hf
operator|.
name|getFooterText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-footer"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|hf
operator|.
name|getFooterText
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
comment|// Comments, if present
name|StringBuilder
name|authorStringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Comment
name|comment
range|:
name|slide
operator|.
name|getComments
argument_list|()
control|)
block|{
name|authorStringBuilder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-comment"
argument_list|)
expr_stmt|;
if|if
condition|(
name|comment
operator|.
name|getAuthor
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|authorStringBuilder
operator|.
name|append
argument_list|(
name|comment
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|comment
operator|.
name|getAuthorInitials
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|authorStringBuilder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|authorStringBuilder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|authorStringBuilder
operator|.
name|append
argument_list|(
literal|"("
operator|+
name|comment
operator|.
name|getAuthorInitials
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|authorStringBuilder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|comment
operator|.
name|getText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|authorStringBuilder
operator|.
name|append
argument_list|(
literal|" - "
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|authorStringBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|comment
operator|.
name|getText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|comment
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
literal|"p"
argument_list|)
expr_stmt|;
block|}
comment|// Now any embedded resources
name|handleSlideEmbeddedResources
argument_list|(
name|slide
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// TODO Find the Notes for this slide and extract inline
name|HSLFNotes
name|notes
init|=
name|slide
operator|.
name|getNotes
argument_list|()
decl_stmt|;
if|if
condition|(
name|notes
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-notes"
argument_list|)
expr_stmt|;
name|textRunsToText
argument_list|(
name|xhtml
argument_list|,
name|notes
operator|.
name|getTextParagraphs
argument_list|()
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
comment|// Slide complete
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
comment|// All slides done
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
comment|/* notes */
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-notes"
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|seenNotes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|HeadersFooters
name|hf
init|=
name|ss
operator|.
name|getNotesHeadersFooters
argument_list|()
decl_stmt|;
for|for
control|(
name|HSLFSlide
name|slide
range|:
name|_slides
control|)
block|{
name|HSLFNotes
name|notes
init|=
name|slide
operator|.
name|getNotes
argument_list|()
decl_stmt|;
if|if
condition|(
name|notes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|Integer
name|id
init|=
name|notes
operator|.
name|_getSheetNumber
argument_list|()
decl_stmt|;
if|if
condition|(
name|seenNotes
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|seenNotes
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
comment|// Repeat the Notes header, if set
if|if
condition|(
name|hf
operator|!=
literal|null
operator|&&
name|hf
operator|.
name|isHeaderVisible
argument_list|()
operator|&&
name|hf
operator|.
name|getHeaderText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-note-header"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|hf
operator|.
name|getHeaderText
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
comment|// Notes text
name|textRunsToText
argument_list|(
name|xhtml
argument_list|,
name|notes
operator|.
name|getTextParagraphs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Repeat the notes footer, if set
if|if
condition|(
name|hf
operator|!=
literal|null
operator|&&
name|hf
operator|.
name|isFooterVisible
argument_list|()
operator|&&
name|hf
operator|.
name|getFooterText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-note-footer"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|hf
operator|.
name|getFooterText
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
block|}
name|handleSlideEmbeddedPictures
argument_list|(
name|ss
argument_list|,
name|xhtml
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
specifier|private
name|void
name|extractMaster
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|HSLFMasterSheet
name|master
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|master
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|HSLFShape
argument_list|>
name|shapes
init|=
name|master
operator|.
name|getShapes
argument_list|()
decl_stmt|;
if|if
condition|(
name|shapes
operator|==
literal|null
operator|||
name|shapes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-master-content"
argument_list|)
expr_stmt|;
for|for
control|(
name|HSLFShape
name|shape
range|:
name|shapes
control|)
block|{
if|if
condition|(
name|shape
operator|!=
literal|null
operator|&&
operator|!
name|HSLFMasterSheet
operator|.
name|isPlaceholder
argument_list|(
name|shape
argument_list|)
condition|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|HSLFTextShape
condition|)
block|{
name|HSLFTextShape
name|tsh
init|=
operator|(
name|HSLFTextShape
operator|)
name|shape
decl_stmt|;
name|String
name|text
init|=
name|tsh
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
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
name|text
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractTableText
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|HSLFTable
name|shape
parameter_list|)
throws|throws
name|SAXException
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|0
init|;
name|row
operator|<
name|shape
operator|.
name|getNumberOfRows
argument_list|()
condition|;
name|row
operator|++
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
name|int
name|col
init|=
literal|0
init|;
name|col
operator|<
name|shape
operator|.
name|getNumberOfColumns
argument_list|()
condition|;
name|col
operator|++
control|)
block|{
name|HSLFTableCell
name|cell
init|=
name|shape
operator|.
name|getCell
argument_list|(
name|row
argument_list|,
name|col
argument_list|)
decl_stmt|;
comment|//insert empty string for empty cell if cell is null
name|String
name|txt
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|cell
operator|!=
literal|null
condition|)
block|{
name|txt
operator|=
name|cell
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
name|xhtml
operator|.
name|element
argument_list|(
literal|"td"
argument_list|,
name|txt
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
literal|"table"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|textRunsToText
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|HSLFTextParagraph
argument_list|>
argument_list|>
name|paragraphsList
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|paragraphsList
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|List
argument_list|<
name|HSLFTextParagraph
argument_list|>
name|run
range|:
name|paragraphsList
control|)
block|{
comment|// Leaving in wisdom from TIKA-712 for easy revert.
comment|// Avoid boiler-plate text on the master slide (0
comment|// = TextHeaderAtom.TITLE_TYPE, 1 = TextHeaderAtom.BODY_TYPE):
comment|//if (!isMaster || (run.getRunType() != 0&& run.getRunType() != 1)) {
name|boolean
name|isBullet
init|=
literal|false
decl_stmt|;
for|for
control|(
name|HSLFTextParagraph
name|htp
range|:
name|run
control|)
block|{
name|boolean
name|nextBullet
init|=
name|htp
operator|.
name|isBullet
argument_list|()
decl_stmt|;
comment|// TODO: identify bullet/list type
if|if
condition|(
name|isBullet
operator|!=
name|nextBullet
condition|)
block|{
name|isBullet
operator|=
name|nextBullet
expr_stmt|;
if|if
condition|(
name|isBullet
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|HSLFTextRun
argument_list|>
name|textRuns
init|=
name|htp
operator|.
name|getTextRuns
argument_list|()
decl_stmt|;
name|String
name|firstLine
init|=
name|removePBreak
argument_list|(
name|textRuns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getRawText
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|showBullet
init|=
operator|(
name|isBullet
operator|&&
operator|(
name|textRuns
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|||
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|firstLine
argument_list|)
operator|)
operator|)
decl_stmt|;
name|String
name|paraTag
init|=
name|showBullet
condition|?
literal|"li"
else|:
literal|"p"
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
name|paraTag
argument_list|)
expr_stmt|;
for|for
control|(
name|HSLFTextRun
name|htr
range|:
name|textRuns
control|)
block|{
name|String
name|line
init|=
name|htr
operator|.
name|getRawText
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|boolean
name|isfirst
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|fragment
range|:
name|line
operator|.
name|split
argument_list|(
literal|"\\u000b"
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|isfirst
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"br"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"br"
argument_list|)
expr_stmt|;
block|}
name|isfirst
operator|=
literal|false
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|removePBreak
argument_list|(
name|fragment
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|endsWith
argument_list|(
literal|"\u000b"
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"br"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"br"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
name|paraTag
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isBullet
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// remove trailing paragraph break
specifier|private
specifier|static
name|String
name|removePBreak
parameter_list|(
name|String
name|fragment
parameter_list|)
block|{
comment|// the last text run of a text paragraph contains the paragraph break (\r)
comment|// line breaks (\\u000b) can happen more often
return|return
name|fragment
operator|.
name|replaceFirst
argument_list|(
literal|"\\r$"
argument_list|,
literal|""
argument_list|)
return|;
block|}
specifier|private
name|void
name|handleSlideEmbeddedPictures
parameter_list|(
name|HSLFSlideShow
name|slideshow
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|TikaException
throws|,
name|SAXException
throws|,
name|IOException
block|{
for|for
control|(
name|HSLFPictureData
name|pic
range|:
name|slideshow
operator|.
name|getPictureData
argument_list|()
control|)
block|{
name|String
name|mediaType
decl_stmt|;
switch|switch
condition|(
name|pic
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|EMF
case|:
name|mediaType
operator|=
literal|"application/x-emf"
expr_stmt|;
break|break;
case|case
name|WMF
case|:
name|mediaType
operator|=
literal|"application/x-msmetafile"
expr_stmt|;
break|break;
case|case
name|DIB
case|:
name|mediaType
operator|=
literal|"image/bmp"
expr_stmt|;
break|break;
default|default:
name|mediaType
operator|=
name|pic
operator|.
name|getContentType
argument_list|()
expr_stmt|;
break|break;
block|}
name|handleEmbeddedResource
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|pic
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mediaType
argument_list|,
name|xhtml
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleSlideEmbeddedResources
parameter_list|(
name|HSLFSlide
name|slide
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|TikaException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|HSLFShape
argument_list|>
name|shapes
decl_stmt|;
try|try
block|{
name|shapes
operator|=
name|slide
operator|.
name|getShapes
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// Sometimes HSLF hits problems
comment|// Please open POI bugs for any you come across!
return|return;
block|}
for|for
control|(
name|HSLFShape
name|shape
range|:
name|shapes
control|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|OLEShape
condition|)
block|{
name|OLEShape
name|oleShape
init|=
operator|(
name|OLEShape
operator|)
name|shape
decl_stmt|;
name|HSLFObjectData
name|data
init|=
literal|null
decl_stmt|;
try|try
block|{
name|data
operator|=
name|oleShape
operator|.
name|getObjectData
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|/* getObjectData throws NPE some times. */
block|}
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|String
name|objID
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|oleShape
operator|.
name|getObjectID
argument_list|()
argument_list|)
decl_stmt|;
comment|// Embedded Object: add a<div
comment|// class="embedded" id="X"/> so consumer can see where
comment|// in the main text each embedded document
comment|// occurred:
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
name|objID
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
try|try
init|(
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|data
operator|.
name|getData
argument_list|()
argument_list|)
init|)
block|{
name|String
name|mediaType
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"Excel.Chart.8"
operator|.
name|equals
argument_list|(
name|oleShape
operator|.
name|getProgID
argument_list|()
argument_list|)
condition|)
block|{
name|mediaType
operator|=
literal|"application/vnd.ms-excel"
expr_stmt|;
block|}
name|handleEmbeddedResource
argument_list|(
name|stream
argument_list|,
name|objID
argument_list|,
name|objID
argument_list|,
name|mediaType
argument_list|,
name|xhtml
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

