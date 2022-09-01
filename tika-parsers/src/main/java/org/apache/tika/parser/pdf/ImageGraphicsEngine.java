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
name|pdf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|contentstream
operator|.
name|PDFGraphicsStreamEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|cos
operator|.
name|COSName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|cos
operator|.
name|COSStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|filter
operator|.
name|MissingImageReaderException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|PDPage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|PDResources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|font
operator|.
name|PDFont
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|color
operator|.
name|PDColor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|color
operator|.
name|PDDeviceGray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|color
operator|.
name|PDDeviceRGB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|color
operator|.
name|PDPattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|form
operator|.
name|PDTransparencyGroup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|image
operator|.
name|PDImage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|image
operator|.
name|PDImageXObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|pattern
operator|.
name|PDAbstractPattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|pattern
operator|.
name|PDTilingPattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|state
operator|.
name|PDExtendedGraphicsState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|state
operator|.
name|PDSoftMask
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|graphics
operator|.
name|state
operator|.
name|RenderingMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|tools
operator|.
name|imageio
operator|.
name|ImageIOUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|util
operator|.
name|Matrix
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|util
operator|.
name|Vector
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
name|exception
operator|.
name|TikaMemoryLimitException
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
name|EmbeddedDocumentUtil
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
name|BoundedInputStream
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
name|IOExceptionWithCause
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
name|metadata
operator|.
name|TikaCoreProperties
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
name|java
operator|.
name|awt
operator|.
name|geom
operator|.
name|Point2D
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|image
operator|.
name|BufferedImage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|OutputStream
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
name|Arrays
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
name|Map
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Copied nearly verbatim from PDFBox  */
end_comment

begin_class
class|class
name|ImageGraphicsEngine
extends|extends
name|PDFGraphicsStreamEngine
block|{
comment|//We're currently copying images to byte[].  We should
comment|//limit the length to avoid OOM on crafted files.
specifier|private
specifier|static
specifier|final
name|long
name|MAX_IMAGE_LENGTH_BYTES
init|=
literal|100
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|JPEG
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|COSName
operator|.
name|DCT_DECODE
operator|.
name|getName
argument_list|()
argument_list|,
name|COSName
operator|.
name|DCT_DECODE_ABBREVIATION
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|JP2
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|COSName
operator|.
name|JPX_DECODE
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|JB2
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|COSName
operator|.
name|JBIG2_DECODE
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|//TODO: parameterize this ?
specifier|private
name|boolean
name|useDirectJPEG
init|=
literal|false
decl_stmt|;
specifier|final
name|List
argument_list|<
name|IOException
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
decl_stmt|;
specifier|private
specifier|final
name|PDFParserConfig
name|pdfParserConfig
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|COSStream
argument_list|,
name|Integer
argument_list|>
name|processedInlineImages
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|imageCounter
decl_stmt|;
specifier|private
specifier|final
name|Metadata
name|parentMetadata
decl_stmt|;
specifier|private
specifier|final
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
specifier|private
specifier|final
name|ParseContext
name|parseContext
decl_stmt|;
comment|//TODO: this is an embarrassment of an initializer...fix
specifier|protected
name|ImageGraphicsEngine
parameter_list|(
name|PDPage
name|page
parameter_list|,
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
parameter_list|,
name|PDFParserConfig
name|pdfParserConfig
parameter_list|,
name|Map
argument_list|<
name|COSStream
argument_list|,
name|Integer
argument_list|>
name|processedInlineImages
parameter_list|,
name|AtomicInteger
name|imageCounter
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|parentMetadata
parameter_list|,
name|ParseContext
name|parseContext
parameter_list|)
block|{
name|super
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|this
operator|.
name|embeddedDocumentExtractor
operator|=
name|embeddedDocumentExtractor
expr_stmt|;
name|this
operator|.
name|pdfParserConfig
operator|=
name|pdfParserConfig
expr_stmt|;
name|this
operator|.
name|processedInlineImages
operator|=
name|processedInlineImages
expr_stmt|;
name|this
operator|.
name|imageCounter
operator|=
name|imageCounter
expr_stmt|;
name|this
operator|.
name|xhtml
operator|=
name|xhtml
expr_stmt|;
name|this
operator|.
name|parentMetadata
operator|=
name|parentMetadata
expr_stmt|;
name|this
operator|.
name|parseContext
operator|=
name|parseContext
expr_stmt|;
block|}
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|PDPage
name|page
init|=
name|getPage
argument_list|()
decl_stmt|;
comment|//TODO: is there a better way to do this rather than reprocessing the page
comment|//can we process the text and images in one go?
name|processPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|PDResources
name|res
init|=
name|page
operator|.
name|getResources
argument_list|()
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|COSName
name|name
range|:
name|res
operator|.
name|getExtGStateNames
argument_list|()
control|)
block|{
name|PDExtendedGraphicsState
name|extendedGraphicsState
init|=
name|res
operator|.
name|getExtGState
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|extendedGraphicsState
operator|!=
literal|null
condition|)
block|{
name|PDSoftMask
name|softMask
init|=
name|extendedGraphicsState
operator|.
name|getSoftMask
argument_list|()
decl_stmt|;
if|if
condition|(
name|softMask
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|PDTransparencyGroup
name|group
init|=
name|softMask
operator|.
name|getGroup
argument_list|()
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
comment|// PDFBOX-4327: without this line NPEs will occur
name|res
operator|.
name|getExtGState
argument_list|(
name|name
argument_list|)
operator|.
name|copyIntoGraphicsState
argument_list|(
name|getGraphicsState
argument_list|()
argument_list|)
expr_stmt|;
name|processSoftMask
argument_list|(
name|group
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
name|handleCatchableIOE
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|drawImage
parameter_list|(
name|PDImage
name|pdImage
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|imageNumber
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|pdImage
operator|instanceof
name|PDImageXObject
condition|)
block|{
if|if
condition|(
name|pdImage
operator|.
name|isStencil
argument_list|()
condition|)
block|{
name|processColor
argument_list|(
name|getGraphicsState
argument_list|()
operator|.
name|getNonStrokingColor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PDImageXObject
name|xobject
init|=
operator|(
name|PDImageXObject
operator|)
name|pdImage
decl_stmt|;
name|Integer
name|cachedNumber
init|=
name|processedInlineImages
operator|.
name|get
argument_list|(
name|xobject
operator|.
name|getCOSObject
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedNumber
operator|!=
literal|null
operator|&&
name|pdfParserConfig
operator|.
name|getExtractUniqueInlineImagesOnly
argument_list|()
condition|)
block|{
comment|// skip duplicate image
return|return;
block|}
if|if
condition|(
name|cachedNumber
operator|==
literal|null
condition|)
block|{
name|imageNumber
operator|=
name|imageCounter
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
name|processedInlineImages
operator|.
name|put
argument_list|(
name|xobject
operator|.
name|getCOSObject
argument_list|()
argument_list|,
name|imageNumber
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|imageNumber
operator|=
name|imageCounter
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
comment|//TODO: should we use the hash of the PDImage to check for seen
comment|//For now, we're relying on the cosobject, but this could lead to
comment|//duplicates if the pdImage is not a PDImageXObject?
try|try
block|{
name|processImage
argument_list|(
name|pdImage
argument_list|,
name|imageNumber
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
decl||
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|handleCatchableIOE
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|appendRectangle
parameter_list|(
name|Point2D
name|p0
parameter_list|,
name|Point2D
name|p1
parameter_list|,
name|Point2D
name|p2
parameter_list|,
name|Point2D
name|p3
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|public
name|void
name|clip
parameter_list|(
name|int
name|windingRule
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|public
name|void
name|moveTo
parameter_list|(
name|float
name|x
parameter_list|,
name|float
name|y
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|public
name|void
name|lineTo
parameter_list|(
name|float
name|x
parameter_list|,
name|float
name|y
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|public
name|void
name|curveTo
parameter_list|(
name|float
name|x1
parameter_list|,
name|float
name|y1
parameter_list|,
name|float
name|x2
parameter_list|,
name|float
name|y2
parameter_list|,
name|float
name|x3
parameter_list|,
name|float
name|y3
parameter_list|)
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|public
name|Point2D
name|getCurrentPoint
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Point2D
operator|.
name|Float
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|closePath
parameter_list|()
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|public
name|void
name|endPath
parameter_list|()
throws|throws
name|IOException
block|{      }
annotation|@
name|Override
specifier|protected
name|void
name|showGlyph
parameter_list|(
name|Matrix
name|textRenderingMatrix
parameter_list|,
name|PDFont
name|font
parameter_list|,
name|int
name|code
parameter_list|,
name|String
name|unicode
parameter_list|,
name|Vector
name|displacement
parameter_list|)
throws|throws
name|IOException
block|{
name|RenderingMode
name|renderingMode
init|=
name|getGraphicsState
argument_list|()
operator|.
name|getTextState
argument_list|()
operator|.
name|getRenderingMode
argument_list|()
decl_stmt|;
if|if
condition|(
name|renderingMode
operator|.
name|isFill
argument_list|()
condition|)
block|{
name|processColor
argument_list|(
name|getGraphicsState
argument_list|()
operator|.
name|getNonStrokingColor
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|renderingMode
operator|.
name|isStroke
argument_list|()
condition|)
block|{
name|processColor
argument_list|(
name|getGraphicsState
argument_list|()
operator|.
name|getStrokingColor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|strokePath
parameter_list|()
throws|throws
name|IOException
block|{
name|processColor
argument_list|(
name|getGraphicsState
argument_list|()
operator|.
name|getStrokingColor
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillPath
parameter_list|(
name|int
name|windingRule
parameter_list|)
throws|throws
name|IOException
block|{
name|processColor
argument_list|(
name|getGraphicsState
argument_list|()
operator|.
name|getNonStrokingColor
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillAndStrokePath
parameter_list|(
name|int
name|windingRule
parameter_list|)
throws|throws
name|IOException
block|{
name|processColor
argument_list|(
name|getGraphicsState
argument_list|()
operator|.
name|getNonStrokingColor
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|shadingFill
parameter_list|(
name|COSName
name|shadingName
parameter_list|)
throws|throws
name|IOException
block|{      }
comment|// find out if it is a tiling pattern, then process that one
specifier|private
name|void
name|processColor
parameter_list|(
name|PDColor
name|color
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|color
operator|.
name|getColorSpace
argument_list|()
operator|instanceof
name|PDPattern
condition|)
block|{
name|PDPattern
name|pattern
init|=
operator|(
name|PDPattern
operator|)
name|color
operator|.
name|getColorSpace
argument_list|()
decl_stmt|;
name|PDAbstractPattern
name|abstractPattern
init|=
name|pattern
operator|.
name|getPattern
argument_list|(
name|color
argument_list|)
decl_stmt|;
if|if
condition|(
name|abstractPattern
operator|instanceof
name|PDTilingPattern
condition|)
block|{
name|processTilingPattern
argument_list|(
operator|(
name|PDTilingPattern
operator|)
name|abstractPattern
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|processImage
parameter_list|(
name|PDImage
name|pdImage
parameter_list|,
name|int
name|imageNumber
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
comment|//this is the metadata for this particular image
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|suffix
init|=
name|getSuffix
argument_list|(
name|pdImage
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
literal|"image"
operator|+
name|imageNumber
operator|+
literal|"."
operator|+
name|suffix
decl_stmt|;
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
name|fileName
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
name|fileName
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
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|EMBEDDED_RESOURCE_TYPE
argument_list|,
name|TikaCoreProperties
operator|.
name|EmbeddedResourceType
operator|.
name|INLINE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|pdImage
operator|instanceof
name|PDImageXObject
condition|)
block|{
name|PDMetadataExtractor
operator|.
name|extract
argument_list|(
operator|(
operator|(
name|PDImageXObject
operator|)
name|pdImage
operator|)
operator|.
name|getMetadata
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
comment|//extract the metadata contained outside of the image
try|try
block|{
name|writeToBuffer
argument_list|(
name|pdImage
argument_list|,
name|suffix
argument_list|,
name|useDirectJPEG
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MissingImageReaderException
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordException
argument_list|(
name|e
argument_list|,
name|parentMetadata
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordEmbeddedStreamException
argument_list|(
name|e
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
init|(
name|InputStream
name|embeddedIs
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
init|)
block|{
name|embeddedDocumentExtractor
operator|.
name|parseEmbedded
argument_list|(
name|embeddedIs
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|metadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|String
name|getSuffix
parameter_list|(
name|PDImage
name|pdImage
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|suffix
init|=
name|pdImage
operator|.
name|getSuffix
argument_list|()
decl_stmt|;
if|if
condition|(
name|suffix
operator|==
literal|null
operator|||
name|suffix
operator|.
name|equals
argument_list|(
literal|"png"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"image/png"
argument_list|)
expr_stmt|;
name|suffix
operator|=
literal|"png"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|suffix
operator|.
name|equals
argument_list|(
literal|"jpg"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"image/jpeg"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|suffix
operator|.
name|equals
argument_list|(
literal|"tiff"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"image/tiff"
argument_list|)
expr_stmt|;
name|suffix
operator|=
literal|"tif"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|suffix
operator|.
name|equals
argument_list|(
literal|"jpx"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"image/jp2"
argument_list|)
expr_stmt|;
comment|// use jp2 suffix for file because jpx not known by windows
name|suffix
operator|=
literal|"jp2"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|suffix
operator|.
name|equals
argument_list|(
literal|"jb2"
argument_list|)
condition|)
block|{
comment|//PDFBox resets suffix to png when image's suffix == jb2
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"image/x-jbig2"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//TODO: determine if we need to add more image types
comment|//                    throw new RuntimeException("EXTEN:" + extension);
block|}
if|if
condition|(
name|hasMasks
argument_list|(
name|pdImage
argument_list|)
condition|)
block|{
comment|// TIKA-3040, PDFBOX-4771: can't save ARGB as JPEG
name|suffix
operator|=
literal|"png"
expr_stmt|;
block|}
return|return
name|suffix
return|;
block|}
name|void
name|handleCatchableIOE
parameter_list|(
name|IOException
name|e
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pdfParserConfig
operator|.
name|getCatchIntermediateIOExceptions
argument_list|()
condition|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SAXException
operator|&&
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Your document contained more than"
argument_list|)
condition|)
block|{
comment|//TODO -- is there a cleaner way of checking for:
comment|// WriteOutContentHandler.WriteLimitReachedException?
throw|throw
name|e
throw|;
block|}
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|msg
operator|=
literal|"IOException, no message"
expr_stmt|;
block|}
name|parentMetadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|List
argument_list|<
name|IOException
argument_list|>
name|getExceptions
parameter_list|()
block|{
return|return
name|exceptions
return|;
block|}
comment|//nearly directly copied from PDFBox ExtractImages
specifier|private
specifier|static
name|void
name|writeToBuffer
parameter_list|(
name|PDImage
name|pdImage
parameter_list|,
name|String
name|suffix
parameter_list|,
name|boolean
name|directJPEG
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
if|if
condition|(
literal|"jpg"
operator|.
name|equals
argument_list|(
name|suffix
argument_list|)
condition|)
block|{
name|String
name|colorSpaceName
init|=
name|pdImage
operator|.
name|getColorSpace
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|directJPEG
operator|||
operator|(
name|PDDeviceGray
operator|.
name|INSTANCE
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|colorSpaceName
argument_list|)
operator|||
name|PDDeviceRGB
operator|.
name|INSTANCE
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|colorSpaceName
argument_list|)
operator|)
condition|)
block|{
comment|// RGB or Gray colorspace: get and write the unmodified JPEG stream
name|InputStream
name|data
init|=
name|pdImage
operator|.
name|createInputStream
argument_list|(
name|JPEG
argument_list|)
decl_stmt|;
try|try
block|{
name|copyUpToMaxLength
argument_list|(
name|data
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|BufferedImage
name|image
init|=
name|pdImage
operator|.
name|getImage
argument_list|()
decl_stmt|;
if|if
condition|(
name|image
operator|!=
literal|null
condition|)
block|{
comment|// for CMYK and other "unusual" colorspaces, the JPEG will be converted
name|ImageIOUtil
operator|.
name|writeImage
argument_list|(
name|image
argument_list|,
name|suffix
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"jp2"
operator|.
name|equals
argument_list|(
name|suffix
argument_list|)
condition|)
block|{
name|String
name|colorSpaceName
init|=
name|pdImage
operator|.
name|getColorSpace
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|directJPEG
operator|||
operator|!
name|hasMasks
argument_list|(
name|pdImage
argument_list|)
operator|&&
operator|(
name|PDDeviceGray
operator|.
name|INSTANCE
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|colorSpaceName
argument_list|)
operator|||
name|PDDeviceRGB
operator|.
name|INSTANCE
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|colorSpaceName
argument_list|)
operator|)
condition|)
block|{
comment|// RGB or Gray colorspace: get and write the unmodified JPEG2000 stream
name|InputStream
name|data
init|=
name|pdImage
operator|.
name|createInputStream
argument_list|(
name|JP2
argument_list|)
decl_stmt|;
try|try
block|{
name|copyUpToMaxLength
argument_list|(
name|data
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// for CMYK and other "unusual" colorspaces, the image will be converted
name|BufferedImage
name|image
init|=
name|pdImage
operator|.
name|getImage
argument_list|()
decl_stmt|;
if|if
condition|(
name|image
operator|!=
literal|null
condition|)
block|{
comment|// for CMYK and other "unusual" colorspaces, the JPEG will be converted
name|ImageIOUtil
operator|.
name|writeImage
argument_list|(
name|image
argument_list|,
literal|"jpeg2000"
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"tif"
operator|.
name|equals
argument_list|(
name|suffix
argument_list|)
operator|&&
name|pdImage
operator|.
name|getColorSpace
argument_list|()
operator|.
name|equals
argument_list|(
name|PDDeviceGray
operator|.
name|INSTANCE
argument_list|)
condition|)
block|{
name|BufferedImage
name|image
init|=
name|pdImage
operator|.
name|getImage
argument_list|()
decl_stmt|;
if|if
condition|(
name|image
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// CCITT compressed images can have a different colorspace, but this one is B/W
comment|// This is a bitonal image, so copy to TYPE_BYTE_BINARY
comment|// so that a G4 compressed TIFF image is created by ImageIOUtil.writeImage()
name|int
name|w
init|=
name|image
operator|.
name|getWidth
argument_list|()
decl_stmt|;
name|int
name|h
init|=
name|image
operator|.
name|getHeight
argument_list|()
decl_stmt|;
name|BufferedImage
name|bitonalImage
init|=
operator|new
name|BufferedImage
argument_list|(
name|w
argument_list|,
name|h
argument_list|,
name|BufferedImage
operator|.
name|TYPE_BYTE_BINARY
argument_list|)
decl_stmt|;
comment|// copy image the old fashioned way - ColorConvertOp is slower!
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|h
condition|;
name|y
operator|++
control|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|w
condition|;
name|x
operator|++
control|)
block|{
name|bitonalImage
operator|.
name|setRGB
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|image
operator|.
name|getRGB
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|ImageIOUtil
operator|.
name|writeImage
argument_list|(
name|bitonalImage
argument_list|,
name|suffix
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"jb2"
operator|.
name|equals
argument_list|(
name|suffix
argument_list|)
condition|)
block|{
name|InputStream
name|data
init|=
name|pdImage
operator|.
name|createInputStream
argument_list|(
name|JB2
argument_list|)
decl_stmt|;
try|try
block|{
name|copyUpToMaxLength
argument_list|(
name|data
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|BufferedImage
name|image
init|=
name|pdImage
operator|.
name|getImage
argument_list|()
decl_stmt|;
if|if
condition|(
name|image
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ImageIOUtil
operator|.
name|writeImage
argument_list|(
name|image
argument_list|,
name|suffix
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|copyUpToMaxLength
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|BoundedInputStream
name|bis
init|=
operator|new
name|BoundedInputStream
argument_list|(
name|MAX_IMAGE_LENGTH_BYTES
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|bis
argument_list|,
name|os
argument_list|)
expr_stmt|;
if|if
condition|(
name|bis
operator|.
name|hasHitBound
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TikaMemoryLimitException
argument_list|(
literal|"Image size is larger than allowed ("
operator|+
name|MAX_IMAGE_LENGTH_BYTES
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|hasMasks
parameter_list|(
name|PDImage
name|pdImage
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pdImage
operator|instanceof
name|PDImageXObject
condition|)
block|{
name|PDImageXObject
name|ximg
init|=
operator|(
name|PDImageXObject
operator|)
name|pdImage
decl_stmt|;
return|return
name|ximg
operator|.
name|getMask
argument_list|()
operator|!=
literal|null
operator|||
name|ximg
operator|.
name|getSoftMask
argument_list|()
operator|!=
literal|null
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit
