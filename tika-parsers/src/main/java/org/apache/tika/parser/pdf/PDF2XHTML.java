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
name|ByteArrayInputStream
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
name|io
operator|.
name|Writer
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|commons
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
name|pdfbox
operator|.
name|cos
operator|.
name|COSBase
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
name|pdmodel
operator|.
name|PDDocument
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
name|graphics
operator|.
name|PDXObject
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
name|form
operator|.
name|PDFormXObject
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
name|text
operator|.
name|PDFTextStripper
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
name|text
operator|.
name|TextPosition
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

begin_comment
comment|/**  * Utility class that overrides the {@link PDFTextStripper} functionality  * to produce a semi-structured XHTML SAX events instead of a plain text  * stream.  */
end_comment

begin_class
class|class
name|PDF2XHTML
extends|extends
name|AbstractPDF2XHTML
block|{
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
comment|/**      * This keeps track of the pdf object ids for inline      * images that have been processed.      * If {@link PDFParserConfig#getExtractUniqueInlineImagesOnly()      * is true, this will be checked before extracting an embedded image.      * The integer keeps track of the inlineImageCounter for that image.      * This integer is used to identify images in the markup.      *      * This is used across the document.  To avoid infinite recursion      * TIKA-1742, we're limiting the export to one image per page.      */
specifier|private
name|Map
argument_list|<
name|COSStream
argument_list|,
name|Integer
argument_list|>
name|processedInlineImages
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|inlineImageCounter
init|=
literal|0
decl_stmt|;
specifier|private
name|PDF2XHTML
parameter_list|(
name|PDDocument
name|document
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|PDFParserConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|document
argument_list|,
name|handler
argument_list|,
name|context
argument_list|,
name|metadata
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Converts the given PDF document (and related metadata) to a stream      * of XHTML SAX events sent to the given content handler.      *      * @param document PDF document      * @param handler  SAX content handler      * @param metadata PDF metadata      * @throws SAXException  if the content handler fails to process SAX events      * @throws TikaException if there was an exception outside of per page processing      */
specifier|public
specifier|static
name|void
name|process
parameter_list|(
name|PDDocument
name|document
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|PDFParserConfig
name|config
parameter_list|)
throws|throws
name|SAXException
throws|,
name|TikaException
block|{
name|PDF2XHTML
name|pdf2XHTML
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Extract text using a dummy Writer as we override the
comment|// key methods to output to the given content
comment|// handler.
name|pdf2XHTML
operator|=
operator|new
name|PDF2XHTML
argument_list|(
name|document
argument_list|,
name|handler
argument_list|,
name|context
argument_list|,
name|metadata
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|config
operator|.
name|configure
argument_list|(
name|pdf2XHTML
argument_list|)
expr_stmt|;
name|pdf2XHTML
operator|.
name|writeText
argument_list|(
name|document
argument_list|,
operator|new
name|Writer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{                 }
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
block|{                 }
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{                 }
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SAXException
condition|)
block|{
throw|throw
operator|(
name|SAXException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to extract PDF content"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|pdf2XHTML
operator|.
name|exceptions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//throw the first
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to extract PDF content"
argument_list|,
name|pdf2XHTML
operator|.
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|processPage
parameter_list|(
name|PDPage
name|page
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|processPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
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
specifier|protected
name|void
name|endPage
parameter_list|(
name|PDPage
name|page
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|writeParagraphEnd
argument_list|()
expr_stmt|;
try|try
block|{
name|extractImages
argument_list|(
name|page
operator|.
name|getResources
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<
name|COSBase
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|endPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
literal|"Unable to end a page"
argument_list|,
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
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractImages
parameter_list|(
name|PDResources
name|resources
parameter_list|,
name|Set
argument_list|<
name|COSBase
argument_list|>
name|seenThisPage
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
if|if
condition|(
name|resources
operator|==
literal|null
operator|||
name|config
operator|.
name|getExtractInlineImages
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return;
block|}
for|for
control|(
name|COSName
name|name
range|:
name|resources
operator|.
name|getXObjectNames
argument_list|()
control|)
block|{
name|PDXObject
name|object
init|=
literal|null
decl_stmt|;
try|try
block|{
name|object
operator|=
name|resources
operator|.
name|getXObject
argument_list|(
name|name
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
name|metadata
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|object
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|COSStream
name|cosStream
init|=
name|object
operator|.
name|getCOSObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|seenThisPage
operator|.
name|contains
argument_list|(
name|cosStream
argument_list|)
condition|)
block|{
comment|//avoid infinite recursion TIKA-1742
continue|continue;
block|}
name|seenThisPage
operator|.
name|add
argument_list|(
name|cosStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|object
operator|instanceof
name|PDFormXObject
condition|)
block|{
name|extractImages
argument_list|(
operator|(
operator|(
name|PDFormXObject
operator|)
name|object
operator|)
operator|.
name|getResources
argument_list|()
argument_list|,
name|seenThisPage
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|PDImageXObject
condition|)
block|{
name|PDImageXObject
name|image
init|=
operator|(
name|PDImageXObject
operator|)
name|object
decl_stmt|;
name|Metadata
name|embeddedMetadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|extension
init|=
name|image
operator|.
name|getSuffix
argument_list|()
decl_stmt|;
if|if
condition|(
name|extension
operator|==
literal|null
operator|||
name|extension
operator|.
name|equals
argument_list|(
literal|"png"
argument_list|)
condition|)
block|{
name|embeddedMetadata
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
name|extension
operator|=
literal|"png"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|extension
operator|.
name|equals
argument_list|(
literal|"jpg"
argument_list|)
condition|)
block|{
name|embeddedMetadata
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
name|extension
operator|.
name|equals
argument_list|(
literal|"tiff"
argument_list|)
condition|)
block|{
name|embeddedMetadata
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
name|extension
operator|=
literal|"tif"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|extension
operator|.
name|equals
argument_list|(
literal|"jpx"
argument_list|)
condition|)
block|{
name|embeddedMetadata
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
block|}
else|else
block|{
comment|//TODO: determine if we need to add more image types
comment|//                    throw new RuntimeException("EXTEN:" + extension);
block|}
name|Integer
name|imageNumber
init|=
name|processedInlineImages
operator|.
name|get
argument_list|(
name|cosStream
argument_list|)
decl_stmt|;
if|if
condition|(
name|imageNumber
operator|==
literal|null
condition|)
block|{
name|imageNumber
operator|=
name|inlineImageCounter
operator|++
expr_stmt|;
block|}
name|String
name|fileName
init|=
literal|"image"
operator|+
name|imageNumber
operator|+
literal|"."
operator|+
name|extension
decl_stmt|;
name|embeddedMetadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
comment|// Output the img tag
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
comment|//Do we only want to process unique COSObject ids?
comment|//If so, have we already processed this one?
if|if
condition|(
name|config
operator|.
name|getExtractUniqueInlineImagesOnly
argument_list|()
operator|==
literal|true
condition|)
block|{
if|if
condition|(
name|processedInlineImages
operator|.
name|containsKey
argument_list|(
name|cosStream
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|processedInlineImages
operator|.
name|put
argument_list|(
name|cosStream
argument_list|,
name|imageNumber
argument_list|)
expr_stmt|;
block|}
name|embeddedMetadata
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
name|embeddedMetadata
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
try|try
block|{
comment|//TODO: handle image.getMetadata()?
try|try
block|{
name|writeToBuffer
argument_list|(
name|image
argument_list|,
name|extension
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordException
argument_list|(
name|e
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|embeddedDocumentExtractor
operator|.
name|parseEmbedded
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|embeddedMetadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
comment|//nearly directly copied from PDFBox ExtractImages
specifier|private
name|void
name|writeToBuffer
parameter_list|(
name|PDImageXObject
name|pdImage
parameter_list|,
name|String
name|suffix
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
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
comment|//TODO: figure out if we want directJPEG as a configuration
comment|//previously: if (directJPeg || PDDeviceGray....
if|if
condition|(
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
condition|)
block|{
comment|// RGB or Gray colorspace: get and write the unmodifiedJPEG stream
name|InputStream
name|data
init|=
name|pdImage
operator|.
name|getStream
argument_list|()
operator|.
name|createInputStream
argument_list|(
name|JPEG
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|copy
argument_list|(
name|data
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
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
elseif|else
if|if
condition|(
literal|"jp2"
operator|.
name|equals
argument_list|(
name|suffix
argument_list|)
operator|||
literal|"jpx"
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
name|JP2
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|copy
argument_list|(
name|data
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|io
operator|.
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeParagraphStart
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeParagraphStart
argument_list|()
expr_stmt|;
try|try
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
literal|"Unable to start a paragraph"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeParagraphEnd
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeParagraphEnd
argument_list|()
expr_stmt|;
try|try
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
literal|"Unable to end a paragraph"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeString
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
literal|"Unable to write a string: "
operator|+
name|text
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeCharacters
parameter_list|(
name|TextPosition
name|text
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|text
operator|.
name|getUnicode
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
literal|"Unable to write a character: "
operator|+
name|text
operator|.
name|getUnicode
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeWordSeparator
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|getWordSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
literal|"Unable to write a space character"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeLineSeparator
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
literal|"Unable to write a newline character"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

