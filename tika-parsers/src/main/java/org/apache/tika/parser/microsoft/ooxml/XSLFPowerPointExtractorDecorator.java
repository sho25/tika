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
name|HashMap
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
name|common
operator|.
name|usermodel
operator|.
name|Hyperlink
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
name|exceptions
operator|.
name|InvalidFormatException
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
name|openxml4j
operator|.
name|opc
operator|.
name|PackagePartName
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
name|PackageRelationship
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
name|PackageRelationshipCollection
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
name|PackagingURIHelper
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
name|TargetMode
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
name|sl
operator|.
name|extractor
operator|.
name|SlideShowExtractor
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
name|sl
operator|.
name|usermodel
operator|.
name|Placeholder
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
name|xslf
operator|.
name|extractor
operator|.
name|XSLFPowerPointExtractor
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
name|xslf
operator|.
name|usermodel
operator|.
name|XMLSlideShow
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFComment
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFCommentAuthors
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFComments
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFGraphicFrame
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFGroupShape
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFHyperlink
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFNotes
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFNotesMaster
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFPictureShape
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFRelation
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFShape
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFSheet
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFSlide
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFSlideLayout
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFSlideShow
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFTable
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFTableCell
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFTableRow
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFTextParagraph
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFTextRun
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFTextShape
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
name|presentationml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTPicture
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
name|presentationml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTSlideIdList
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
name|presentationml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTSlideIdListEntry
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
name|XSLFPowerPointExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
specifier|private
specifier|final
specifier|static
name|String
name|HANDOUT_MASTER
init|=
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/handoutMaster"
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
specifier|public
name|XSLFPowerPointExtractorDecorator
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|XSLFPowerPointExtractor
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
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
comment|/**      * use {@link XSLFPowerPointExtractorDecorator#XSLFPowerPointExtractorDecorator(Metadata, ParseContext, XSLFPowerPointExtractor)}      * @param context      * @param extractor      */
annotation|@
name|Deprecated
specifier|public
name|XSLFPowerPointExtractorDecorator
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|XSLFPowerPointExtractor
name|extractor
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|,
name|extractor
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.poi.xslf.extractor.XSLFPowerPointExtractor#getText()      */
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
name|IOException
block|{
name|XMLSlideShow
name|slideShow
init|=
operator|(
name|XMLSlideShow
operator|)
name|extractor
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|XSLFCommentAuthors
name|commentAuthors
init|=
name|slideShow
operator|.
name|getCommentAuthors
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|XSLFSlide
argument_list|>
name|slides
init|=
name|slideShow
operator|.
name|getSlides
argument_list|()
decl_stmt|;
for|for
control|(
name|XSLFSlide
name|slide
range|:
name|slides
control|)
block|{
name|String
name|slideDesc
decl_stmt|;
if|if
condition|(
name|slide
operator|.
name|getPackagePart
argument_list|()
operator|!=
literal|null
operator|&&
name|slide
operator|.
name|getPackagePart
argument_list|()
operator|.
name|getPartName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|slideDesc
operator|=
name|getJustFileName
argument_list|(
name|slide
operator|.
name|getPackagePart
argument_list|()
operator|.
name|getPartName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|slideDesc
operator|+=
literal|"_"
expr_stmt|;
block|}
else|else
block|{
name|slideDesc
operator|=
literal|null
expr_stmt|;
block|}
comment|// slide content
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
name|extractContent
argument_list|(
name|slide
operator|.
name|getShapes
argument_list|()
argument_list|,
literal|false
argument_list|,
name|xhtml
argument_list|,
name|slideDesc
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|getIncludeSlideMasterContent
argument_list|()
condition|)
block|{
comment|// slide layout which is the master sheet for this slide
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
name|XSLFSlideLayout
name|slideLayout
init|=
name|slide
operator|.
name|getMasterSheet
argument_list|()
decl_stmt|;
name|extractContent
argument_list|(
name|slideLayout
operator|.
name|getShapes
argument_list|()
argument_list|,
literal|true
argument_list|,
name|xhtml
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
comment|// slide master which is the master sheet for all text layouts
name|XSLFSheet
name|slideMaster
init|=
name|slideLayout
operator|.
name|getMasterSheet
argument_list|()
decl_stmt|;
name|extractContent
argument_list|(
name|slideMaster
operator|.
name|getShapes
argument_list|()
argument_list|,
literal|true
argument_list|,
name|xhtml
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|getIncludeSlideNotes
argument_list|()
condition|)
block|{
comment|// notes (if present)
name|XSLFNotes
name|slideNotes
init|=
name|slide
operator|.
name|getNotes
argument_list|()
decl_stmt|;
if|if
condition|(
name|slideNotes
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
name|extractContent
argument_list|(
name|slideNotes
operator|.
name|getShapes
argument_list|()
argument_list|,
literal|false
argument_list|,
name|xhtml
argument_list|,
name|slideDesc
argument_list|)
expr_stmt|;
comment|// master sheet for this notes
name|XSLFNotesMaster
name|notesMaster
init|=
name|slideNotes
operator|.
name|getMasterSheet
argument_list|()
decl_stmt|;
if|if
condition|(
name|notesMaster
operator|!=
literal|null
condition|)
block|{
name|extractContent
argument_list|(
name|notesMaster
operator|.
name|getShapes
argument_list|()
argument_list|,
literal|true
argument_list|,
name|xhtml
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// comments (if present)
name|List
argument_list|<
name|XSLFComment
argument_list|>
name|comments
init|=
name|slide
operator|.
name|getComments
argument_list|()
decl_stmt|;
if|if
condition|(
name|comments
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|authorStringBuilder
init|=
operator|new
name|StringBuilder
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
name|comments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|authorStringBuilder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|XSLFComment
name|comment
init|=
name|comments
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
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
name|comment
operator|.
name|getText
argument_list|()
operator|!=
literal|null
operator|&&
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
literal|" - "
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
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//now dump diagram data
name|handleGeneralTextContainingPart
argument_list|(
name|RELATION_DIAGRAM_DATA
argument_list|,
literal|"diagram-data"
argument_list|,
name|slide
operator|.
name|getPackagePart
argument_list|()
argument_list|,
name|metadata
argument_list|,
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
comment|//empty
argument_list|)
argument_list|)
expr_stmt|;
comment|//now dump chart data
name|handleGeneralTextContainingPart
argument_list|(
name|XSLFRelation
operator|.
name|CHART
operator|.
name|getRelation
argument_list|()
argument_list|,
literal|"chart"
argument_list|,
name|slide
operator|.
name|getPackagePart
argument_list|()
argument_list|,
name|metadata
argument_list|,
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
comment|//empty
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractContent
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|XSLFShape
argument_list|>
name|shapes
parameter_list|,
name|boolean
name|skipPlaceholders
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|String
name|slideDesc
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|XSLFShape
name|sh
range|:
name|shapes
control|)
block|{
if|if
condition|(
name|sh
operator|instanceof
name|XSLFTextShape
condition|)
block|{
name|XSLFTextShape
name|txt
init|=
operator|(
name|XSLFTextShape
operator|)
name|sh
decl_stmt|;
name|Placeholder
name|ph
init|=
name|txt
operator|.
name|getTextType
argument_list|()
decl_stmt|;
if|if
condition|(
name|skipPlaceholders
operator|&&
name|ph
operator|!=
literal|null
condition|)
block|{
continue|continue;
block|}
name|boolean
name|inHyperlink
init|=
literal|false
decl_stmt|;
for|for
control|(
name|XSLFTextParagraph
name|p
range|:
name|txt
operator|.
name|getTextParagraphs
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|config
operator|.
name|getIncludeHeadersAndFooters
argument_list|()
operator|&&
name|p
operator|.
name|isHeaderOrFooter
argument_list|()
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|XSLFTextRun
name|run
range|:
name|p
operator|.
name|getTextRuns
argument_list|()
control|)
block|{
comment|//TODO: add check for targetmode=external into POI
comment|//then check to confirm that the urls are actually
comment|//external and not footnote refs via the current hack
name|Hyperlink
name|hyperlink
init|=
name|run
operator|.
name|getHyperlink
argument_list|()
decl_stmt|;
if|if
condition|(
name|hyperlink
operator|!=
literal|null
operator|&&
name|hyperlink
operator|.
name|getAddress
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|hyperlink
operator|.
name|getAddress
argument_list|()
operator|.
name|contains
argument_list|(
literal|"#_ftn"
argument_list|)
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
name|hyperlink
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|inHyperlink
operator|=
literal|true
expr_stmt|;
block|}
name|xhtml
operator|.
name|characters
argument_list|(
name|run
operator|.
name|getRawText
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|inHyperlink
operator|==
literal|true
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
name|inHyperlink
operator|=
literal|false
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
block|}
elseif|else
if|if
condition|(
name|sh
operator|instanceof
name|XSLFGroupShape
condition|)
block|{
comment|// recurse into groups of shapes
name|XSLFGroupShape
name|group
init|=
operator|(
name|XSLFGroupShape
operator|)
name|sh
decl_stmt|;
name|extractContent
argument_list|(
name|group
operator|.
name|getShapes
argument_list|()
argument_list|,
name|skipPlaceholders
argument_list|,
name|xhtml
argument_list|,
name|slideDesc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sh
operator|instanceof
name|XSLFTable
condition|)
block|{
comment|//unlike tables in Word, ppt/x can't have recursive tables...I don't think
name|extractTable
argument_list|(
operator|(
name|XSLFTable
operator|)
name|sh
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sh
operator|instanceof
name|XSLFGraphicFrame
condition|)
block|{
name|XSLFGraphicFrame
name|frame
init|=
operator|(
name|XSLFGraphicFrame
operator|)
name|sh
decl_stmt|;
name|XmlObject
index|[]
name|sp
init|=
name|frame
operator|.
name|getXmlObject
argument_list|()
operator|.
name|selectPath
argument_list|(
literal|"declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:oleObj"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sp
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|XmlObject
name|emb
range|:
name|sp
control|)
block|{
name|XmlObject
name|relIDAtt
init|=
name|emb
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
if|if
condition|(
name|slideDesc
operator|!=
literal|null
condition|)
block|{
name|relID
operator|=
name|slideDesc
operator|+
name|relID
expr_stmt|;
block|}
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
block|}
elseif|else
if|if
condition|(
name|sh
operator|instanceof
name|XSLFPictureShape
condition|)
block|{
if|if
condition|(
operator|!
name|skipPlaceholders
operator|&&
operator|(
name|sh
operator|.
name|getXmlObject
argument_list|()
operator|instanceof
name|CTPicture
operator|)
condition|)
block|{
name|CTPicture
name|ctPic
init|=
operator|(
operator|(
name|CTPicture
operator|)
name|sh
operator|.
name|getXmlObject
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|ctPic
operator|.
name|getBlipFill
argument_list|()
operator|!=
literal|null
operator|&&
name|ctPic
operator|.
name|getBlipFill
argument_list|()
operator|.
name|getBlip
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|relID
init|=
name|ctPic
operator|.
name|getBlipFill
argument_list|()
operator|.
name|getBlip
argument_list|()
operator|.
name|getEmbed
argument_list|()
decl_stmt|;
if|if
condition|(
name|relID
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|slideDesc
operator|!=
literal|null
condition|)
block|{
name|relID
operator|=
name|slideDesc
operator|+
name|relID
expr_stmt|;
block|}
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
block|}
block|}
block|}
specifier|private
name|void
name|extractTable
parameter_list|(
name|XSLFTable
name|tbl
parameter_list|,
name|XHTMLContentHandler
name|xhtml
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
name|XSLFTableRow
name|row
range|:
name|tbl
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
name|XSLFTableCell
name|c
range|:
name|row
operator|.
name|getCells
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
comment|//TODO: Need to wait for fix in POI to test for hyperlink first
comment|//shouldn't need to catch NPE...
name|XSLFHyperlink
name|hyperlink
init|=
literal|null
decl_stmt|;
try|try
block|{
name|hyperlink
operator|=
name|c
operator|.
name|getHyperlink
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
if|if
condition|(
name|hyperlink
operator|!=
literal|null
operator|&&
name|hyperlink
operator|.
name|getAddress
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
name|hyperlink
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|characters
argument_list|(
name|c
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|hyperlink
operator|!=
literal|null
operator|&&
name|hyperlink
operator|.
name|getAddress
argument_list|()
operator|!=
literal|null
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
literal|"table"
argument_list|)
expr_stmt|;
block|}
comment|/**      * In PowerPoint files, slides have things embedded in them,      * and slide drawings which have the images      */
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|PackagePart
argument_list|>
name|getMainDocumentParts
parameter_list|()
throws|throws
name|TikaException
block|{
name|List
argument_list|<
name|PackagePart
argument_list|>
name|parts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|XSLFSlideShow
name|document
init|=
literal|null
decl_stmt|;
try|try
block|{
name|document
operator|=
operator|new
name|XSLFSlideShow
argument_list|(
name|extractor
operator|.
name|getPackage
argument_list|()
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
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
comment|// Shouldn't happen
block|}
name|CTSlideIdList
name|ctSlideIdList
init|=
name|document
operator|.
name|getSlideReferences
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctSlideIdList
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ctSlideIdList
operator|.
name|sizeOfSldIdArray
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CTSlideIdListEntry
name|ctSlide
init|=
name|ctSlideIdList
operator|.
name|getSldIdArray
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// Add the slide
name|PackagePart
name|slidePart
decl_stmt|;
try|try
block|{
name|slidePart
operator|=
name|document
operator|.
name|getSlidePart
argument_list|(
name|ctSlide
argument_list|)
expr_stmt|;
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
literal|"Broken OOXML file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XmlException
name|xe
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Broken OOXML file"
argument_list|,
name|xe
argument_list|)
throw|;
block|}
name|addSlideParts
argument_list|(
name|slidePart
argument_list|,
name|parts
argument_list|)
expr_stmt|;
block|}
block|}
comment|//add full document to include macros
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
for|for
control|(
name|String
name|rel
range|:
operator|new
name|String
index|[]
block|{
name|XSLFRelation
operator|.
name|SLIDE_MASTER
operator|.
name|getRelation
argument_list|()
block|,
name|HANDOUT_MASTER
block|}
control|)
block|{
try|try
block|{
name|PackageRelationshipCollection
name|prc
init|=
name|document
operator|.
name|getPackagePart
argument_list|()
operator|.
name|getRelationshipsByType
argument_list|(
name|rel
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
name|prc
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PackagePart
name|pp
init|=
name|document
operator|.
name|getPackagePart
argument_list|()
operator|.
name|getRelatedPart
argument_list|(
name|prc
operator|.
name|getRelationship
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|pp
operator|!=
literal|null
condition|)
block|{
name|parts
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
comment|//log
block|}
block|}
return|return
name|parts
return|;
block|}
specifier|private
name|void
name|addSlideParts
parameter_list|(
name|PackagePart
name|slidePart
parameter_list|,
name|List
argument_list|<
name|PackagePart
argument_list|>
name|parts
parameter_list|)
block|{
for|for
control|(
name|String
name|relation
range|:
operator|new
name|String
index|[]
block|{
name|XSLFRelation
operator|.
name|VML_DRAWING
operator|.
name|getRelation
argument_list|()
block|,
name|XSLFRelation
operator|.
name|SLIDE_LAYOUT
operator|.
name|getRelation
argument_list|()
block|,
name|XSLFRelation
operator|.
name|NOTES_MASTER
operator|.
name|getRelation
argument_list|()
block|,
name|XSLFRelation
operator|.
name|NOTES
operator|.
name|getRelation
argument_list|()
block|}
control|)
block|{
try|try
block|{
for|for
control|(
name|PackageRelationship
name|packageRelationship
range|:
name|slidePart
operator|.
name|getRelationshipsByType
argument_list|(
name|relation
argument_list|)
control|)
block|{
if|if
condition|(
name|packageRelationship
operator|.
name|getTargetMode
argument_list|()
operator|==
name|TargetMode
operator|.
name|INTERNAL
condition|)
block|{
name|PackagePartName
name|relName
init|=
name|PackagingURIHelper
operator|.
name|createPartName
argument_list|(
name|packageRelationship
operator|.
name|getTargetURI
argument_list|()
argument_list|)
decl_stmt|;
name|parts
operator|.
name|add
argument_list|(
name|packageRelationship
operator|.
name|getPackage
argument_list|()
operator|.
name|getPart
argument_list|(
name|relName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{              }
block|}
comment|//and slide of course
name|parts
operator|.
name|add
argument_list|(
name|slidePart
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

