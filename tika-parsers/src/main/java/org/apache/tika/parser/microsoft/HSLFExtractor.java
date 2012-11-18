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
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hslf
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
name|model
operator|.
name|*
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
name|ObjectData
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
name|PictureData
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
name|SlideShow
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
name|SlideShow
name|_show
init|=
operator|new
name|SlideShow
argument_list|(
name|ss
argument_list|)
decl_stmt|;
name|Slide
index|[]
name|_slides
init|=
name|_show
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
name|Slide
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
comment|// TODO: re-enable this once we fix TIKA-712
comment|/*          MasterSheet master = slide.getMasterSheet();          if(master != null) {             xhtml.startElement("p", "class", "slide-master-content");             textRunsToText(xhtml, master.getTextRuns() );             xhtml.endElement("p");          }          */
comment|// Slide text
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
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
name|getTextRuns
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
name|comment
operator|.
name|getAuthor
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
literal|" - "
argument_list|)
expr_stmt|;
block|}
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
literal|"slideNotes"
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
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|HeadersFooters
name|hf
init|=
name|_show
operator|.
name|getNotesHeadersFooters
argument_list|()
decl_stmt|;
for|for
control|(
name|Slide
name|slide
range|:
name|_slides
control|)
block|{
name|Notes
name|notes
init|=
name|slide
operator|.
name|getNotesSheet
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
name|Integer
operator|.
name|valueOf
argument_list|(
name|notes
operator|.
name|_getSheetNumber
argument_list|()
argument_list|)
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
name|getTextRuns
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
name|_show
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
name|textRunsToText
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|TextRun
index|[]
name|runs
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|runs
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|TextRun
name|run
range|:
name|runs
control|)
block|{
if|if
condition|(
name|run
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|run
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
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
specifier|private
name|void
name|handleSlideEmbeddedPictures
parameter_list|(
name|SlideShow
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
name|PictureData
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
init|=
literal|null
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
name|Picture
operator|.
name|EMF
case|:
name|mediaType
operator|=
literal|"application/x-emf"
expr_stmt|;
break|break;
case|case
name|Picture
operator|.
name|JPEG
case|:
name|mediaType
operator|=
literal|"image/jpeg"
expr_stmt|;
break|break;
case|case
name|Picture
operator|.
name|PNG
case|:
name|mediaType
operator|=
literal|"image/png"
expr_stmt|;
break|break;
case|case
name|Picture
operator|.
name|WMF
case|:
name|mediaType
operator|=
literal|"application/x-msmetafile"
expr_stmt|;
break|break;
case|case
name|Picture
operator|.
name|DIB
case|:
name|mediaType
operator|=
literal|"image/bmp"
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
name|Slide
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
name|Shape
index|[]
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
name|Shape
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
name|ObjectData
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
decl_stmt|;
try|try
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
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

