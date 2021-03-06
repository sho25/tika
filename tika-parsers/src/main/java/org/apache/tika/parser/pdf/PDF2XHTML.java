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
name|Writer
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
name|COSArray
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
name|PDPageContentStream
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

begin_comment
comment|/**  * Utility class that overrides the {@link PDFTextStripper} functionality  * to produce a semi-structured XHTML SAX events instead of a plain text  * stream.  */
end_comment

begin_class
class|class
name|PDF2XHTML
extends|extends
name|AbstractPDF2XHTML
block|{
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
name|AtomicInteger
name|inlineImageCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|config
operator|.
name|getDetectAngles
argument_list|()
condition|)
block|{
name|pdf2XHTML
operator|=
operator|new
name|AngleDetectingPDF2XHTML
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
else|else
block|{
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
block|}
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
name|endPage
argument_list|(
name|page
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
name|IOException
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
name|handleCatchableIOE
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|extractImages
parameter_list|(
name|PDPage
name|page
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
if|if
condition|(
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
name|ImageGraphicsEngine
name|engine
init|=
operator|new
name|ImageGraphicsEngine
argument_list|(
name|page
argument_list|,
name|embeddedDocumentExtractor
argument_list|,
name|config
argument_list|,
name|processedInlineImages
argument_list|,
name|inlineImageCounter
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|engine
operator|.
name|run
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|IOException
argument_list|>
name|engineExceptions
init|=
name|engine
operator|.
name|getExceptions
argument_list|()
decl_stmt|;
if|if
condition|(
name|engineExceptions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|IOException
name|first
init|=
name|engineExceptions
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|getCatchIntermediateIOExceptions
argument_list|()
condition|)
block|{
name|exceptions
operator|.
name|addAll
argument_list|(
name|engineExceptions
argument_list|)
expr_stmt|;
block|}
throw|throw
name|first
throw|;
block|}
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
name|IOException
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
name|IOException
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
name|IOException
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
name|IOException
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
name|IOException
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
name|IOException
argument_list|(
literal|"Unable to write a newline character"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
class|class
name|AngleCollector
extends|extends
name|PDFTextStripper
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|angles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|Integer
argument_list|>
name|getAngles
parameter_list|()
block|{
return|return
name|angles
return|;
block|}
comment|/**          * Instantiate a new PDFTextStripper object.          *          * @throws IOException If there is an error loading the properties.          */
name|AngleCollector
parameter_list|()
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
specifier|protected
name|void
name|processTextPosition
parameter_list|(
name|TextPosition
name|text
parameter_list|)
block|{
name|Matrix
name|m
init|=
name|text
operator|.
name|getTextMatrix
argument_list|()
decl_stmt|;
name|m
operator|.
name|concatenate
argument_list|(
name|text
operator|.
name|getFont
argument_list|()
operator|.
name|getFontMatrix
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|angle
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|toDegrees
argument_list|(
name|Math
operator|.
name|atan2
argument_list|(
name|m
operator|.
name|getShearY
argument_list|()
argument_list|,
name|m
operator|.
name|getScaleY
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|angle
operator|=
operator|(
name|angle
operator|+
literal|360
operator|)
operator|%
literal|360
expr_stmt|;
name|angles
operator|.
name|add
argument_list|(
name|angle
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AngleDetectingPDF2XHTML
extends|extends
name|PDF2XHTML
block|{
specifier|private
name|AngleDetectingPDF2XHTML
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
annotation|@
name|Override
specifier|protected
name|void
name|startPage
parameter_list|(
name|PDPage
name|page
parameter_list|)
throws|throws
name|IOException
block|{
comment|//no-op
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
comment|//no-op
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
name|startPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
name|detectAnglesAndProcessPage
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
finally|finally
block|{
name|super
operator|.
name|endPage
argument_list|(
name|page
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|detectAnglesAndProcessPage
parameter_list|(
name|PDPage
name|page
parameter_list|)
throws|throws
name|IOException
block|{
comment|//copied and pasted from https://issues.apache.org/jira/secure/attachment/12947452/ExtractAngledText.java
comment|//PDFBOX-4371
name|AngleCollector
name|angleCollector
init|=
operator|new
name|AngleCollector
argument_list|()
decl_stmt|;
comment|// alternatively, reset angles
name|angleCollector
operator|.
name|setStartPage
argument_list|(
name|getCurrentPageNo
argument_list|()
argument_list|)
expr_stmt|;
name|angleCollector
operator|.
name|setEndPage
argument_list|(
name|getCurrentPageNo
argument_list|()
argument_list|)
expr_stmt|;
name|angleCollector
operator|.
name|getText
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|int
name|rotation
init|=
name|page
operator|.
name|getRotation
argument_list|()
decl_stmt|;
name|page
operator|.
name|setRotation
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|angle
range|:
name|angleCollector
operator|.
name|getAngles
argument_list|()
control|)
block|{
if|if
condition|(
name|angle
operator|==
literal|0
condition|)
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
else|else
block|{
comment|// prepend a transformation
try|try
init|(
name|PDPageContentStream
name|cs
init|=
operator|new
name|PDPageContentStream
argument_list|(
name|document
argument_list|,
name|page
argument_list|,
name|PDPageContentStream
operator|.
name|AppendMode
operator|.
name|PREPEND
argument_list|,
literal|false
argument_list|)
init|)
block|{
name|cs
operator|.
name|transform
argument_list|(
name|Matrix
operator|.
name|getRotateInstance
argument_list|(
operator|-
name|Math
operator|.
name|toRadians
argument_list|(
name|angle
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
comment|// remove transformation
name|COSArray
name|contents
init|=
operator|(
name|COSArray
operator|)
name|page
operator|.
name|getCOSObject
argument_list|()
operator|.
name|getItem
argument_list|(
name|COSName
operator|.
name|CONTENTS
argument_list|)
decl_stmt|;
name|contents
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|page
operator|.
name|setRotation
argument_list|(
name|rotation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|processTextPosition
parameter_list|(
name|TextPosition
name|text
parameter_list|)
block|{
name|Matrix
name|m
init|=
name|text
operator|.
name|getTextMatrix
argument_list|()
decl_stmt|;
name|m
operator|.
name|concatenate
argument_list|(
name|text
operator|.
name|getFont
argument_list|()
operator|.
name|getFontMatrix
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|angle
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|toDegrees
argument_list|(
name|Math
operator|.
name|atan2
argument_list|(
name|m
operator|.
name|getShearY
argument_list|()
argument_list|,
name|m
operator|.
name|getScaleY
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|angle
operator|==
literal|0
condition|)
block|{
name|super
operator|.
name|processTextPosition
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

