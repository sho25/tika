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
name|xslf
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
name|XSLFCommonSlideData
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
name|DrawingParagraph
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
name|drawingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTRegularTextRun
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
name|drawingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTTextBody
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
name|drawingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTTextParagraph
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
name|CTComment
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
name|CTCommentList
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
name|CTGroupShape
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
name|CTNotesSlide
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
name|CTShape
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
name|CTSlide
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

begin_class
specifier|public
class|class
name|XSLFPowerPointExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
specifier|public
name|XSLFPowerPointExtractorDecorator
parameter_list|(
name|XSLFPowerPointExtractor
name|extractor
parameter_list|)
block|{
name|super
argument_list|(
name|extractor
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.poi.xslf.extractor.XSLFPowerPointExtractor#getText()      */
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
name|XSLFSlideShow
name|slideShow
init|=
operator|(
name|XSLFSlideShow
operator|)
name|extractor
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|XMLSlideShow
name|xmlSlideShow
init|=
operator|new
name|XMLSlideShow
argument_list|(
name|slideShow
argument_list|)
decl_stmt|;
name|XSLFSlide
index|[]
name|slides
init|=
name|xmlSlideShow
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
name|CTSlide
name|rawSlide
init|=
name|slide
operator|.
name|_getCTSlide
argument_list|()
decl_stmt|;
name|CTSlideIdListEntry
name|slideId
init|=
name|slide
operator|.
name|_getCTSlideId
argument_list|()
decl_stmt|;
name|CTNotesSlide
name|notes
init|=
name|xmlSlideShow
operator|.
name|_getXSLFSlideShow
argument_list|()
operator|.
name|getNotes
argument_list|(
name|slideId
argument_list|)
decl_stmt|;
name|CTCommentList
name|comments
init|=
name|xmlSlideShow
operator|.
name|_getXSLFSlideShow
argument_list|()
operator|.
name|getSlideComments
argument_list|(
name|slideId
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|extractShapeContent
argument_list|(
name|slide
operator|.
name|getCommonSlideData
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
if|if
condition|(
name|comments
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CTComment
name|comment
range|:
name|comments
operator|.
name|getCmArray
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|comment
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|notes
operator|!=
literal|null
condition|)
block|{
name|extractShapeContent
argument_list|(
operator|new
name|XSLFCommonSlideData
argument_list|(
name|notes
operator|.
name|getCSld
argument_list|()
argument_list|)
argument_list|,
name|xhtml
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
specifier|private
name|void
name|extractShapeContent
parameter_list|(
name|XSLFCommonSlideData
name|data
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|DrawingParagraph
name|p
range|:
name|data
operator|.
name|getText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|p
operator|.
name|getText
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * In PowerPoint files, slides have things embedded in them,      *  and slide drawings which have the images      */
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
argument_list|<
name|PackagePart
argument_list|>
argument_list|()
decl_stmt|;
name|XSLFSlideShow
name|document
init|=
operator|(
name|XSLFSlideShow
operator|)
name|extractor
operator|.
name|getDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|CTSlideIdListEntry
name|ctSlide
range|:
name|document
operator|.
name|getSlideReferences
argument_list|()
operator|.
name|getSldIdList
argument_list|()
control|)
block|{
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
name|parts
operator|.
name|add
argument_list|(
name|slidePart
argument_list|)
expr_stmt|;
comment|// If it has drawings, return those too
try|try
block|{
comment|// TODO Improve when we upgrade POI
comment|//             for(PackageRelationship rel : slidePart.getRelationshipsByType(XSLFRelation.VML_DRAWING.getRelation())) {
for|for
control|(
name|PackageRelationship
name|rel
range|:
name|slidePart
operator|.
name|getRelationshipsByType
argument_list|(
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/vmlDrawing"
argument_list|)
control|)
block|{
if|if
condition|(
name|rel
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
name|rel
operator|.
name|getTargetURI
argument_list|()
argument_list|)
decl_stmt|;
name|parts
operator|.
name|add
argument_list|(
name|rel
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
block|}
return|return
name|parts
return|;
block|}
block|}
end_class

end_unit

