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
name|util
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
name|pdmodel
operator|.
name|interactive
operator|.
name|annotation
operator|.
name|PDAnnotation
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
name|interactive
operator|.
name|annotation
operator|.
name|PDAnnotationMarkup
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
name|TextPosition
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
name|PDFTextStripper
block|{
comment|// TODO: remove once PDFBOX-1130 is fixed:
specifier|private
name|boolean
name|inParagraph
init|=
literal|false
decl_stmt|;
comment|/**      * Converts the given PDF document (and related metadata) to a stream      * of XHTML SAX events sent to the given content handler.      *      * @param document PDF document      * @param handler SAX content handler      * @param metadata PDF metadata      * @throws SAXException if the content handler fails to process SAX events      * @throws TikaException if the PDF document can not be processed      */
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
name|Metadata
name|metadata
parameter_list|,
name|boolean
name|extractAnnotationText
parameter_list|,
name|boolean
name|enableAutoSpace
parameter_list|)
throws|throws
name|SAXException
throws|,
name|TikaException
block|{
try|try
block|{
comment|// Extract text using a dummy Writer as we override the
comment|// key methods to output to the given content handler.
operator|new
name|PDF2XHTML
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|,
name|extractAnnotationText
argument_list|,
name|enableAutoSpace
argument_list|)
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
block|}
specifier|private
specifier|final
name|XHTMLContentHandler
name|handler
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|extractAnnotationText
decl_stmt|;
specifier|private
name|PDF2XHTML
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|boolean
name|extractAnnotationText
parameter_list|,
name|boolean
name|enableAutoSpace
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|handler
operator|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|this
operator|.
name|extractAnnotationText
operator|=
name|extractAnnotationText
expr_stmt|;
name|setForceParsing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setSortByPosition
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|enableAutoSpace
condition|)
block|{
name|setWordSeparator
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setWordSeparator
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// TODO: maybe expose setting these too:
comment|//setAverageCharTolerance(1.0f);
comment|//setSpacingTolerance(1.0f);
block|}
annotation|@
name|Override
specifier|protected
name|void
name|startDocument
parameter_list|(
name|PDDocument
name|pdf
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|handler
operator|.
name|startDocument
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
literal|"Unable to start a document"
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
name|endDocument
parameter_list|(
name|PDDocument
name|pdf
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|handler
operator|.
name|endDocument
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
literal|"Unable to end a document"
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
name|startPage
parameter_list|(
name|PDPage
name|page
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"page"
argument_list|)
expr_stmt|;
name|handler
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
literal|"Unable to start a page"
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
comment|// TODO: remove once PDFBOX-1143 is fixed:
name|handler
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
if|if
condition|(
name|extractAnnotationText
condition|)
block|{
name|boolean
name|foundTextAnnots
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|page
operator|.
name|getAnnotations
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|o
operator|instanceof
name|PDAnnotation
operator|)
operator|&&
name|PDAnnotationMarkup
operator|.
name|SUB_TYPE_FREETEXT
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|PDAnnotation
operator|)
name|o
operator|)
operator|.
name|getSubtype
argument_list|()
argument_list|)
condition|)
block|{
comment|// It's a text annotation:
name|PDAnnotationMarkup
name|annot
init|=
operator|(
name|PDAnnotationMarkup
operator|)
name|o
decl_stmt|;
name|String
name|title
init|=
name|annot
operator|.
name|getTitlePopup
argument_list|()
decl_stmt|;
name|String
name|subject
init|=
name|annot
operator|.
name|getTitlePopup
argument_list|()
decl_stmt|;
name|String
name|contents
init|=
name|annot
operator|.
name|getContents
argument_list|()
decl_stmt|;
comment|// TODO: maybe also annot.getRichContents()?
if|if
condition|(
name|title
operator|!=
literal|null
operator|||
name|subject
operator|!=
literal|null
operator|||
name|contents
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|foundTextAnnots
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|foundTextAnnots
operator|=
literal|true
expr_stmt|;
block|}
name|handler
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"annotation"
argument_list|)
expr_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"annotationTitle"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"annotationSubject"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|subject
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contents
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"annotationContents"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
name|handler
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
name|handler
operator|.
name|endElement
argument_list|(
literal|"div"
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
comment|// TODO: remove once PDFBOX-1130 is fixed
if|if
condition|(
name|inParagraph
condition|)
block|{
comment|// Close last paragraph
name|writeParagraphEnd
argument_list|()
expr_stmt|;
block|}
assert|assert
operator|!
name|inParagraph
assert|;
name|inParagraph
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|handler
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
comment|// TODO: remove once PDFBOX-1130 is fixed
if|if
condition|(
operator|!
name|inParagraph
condition|)
block|{
name|writeParagraphStart
argument_list|()
expr_stmt|;
block|}
assert|assert
name|inParagraph
assert|;
name|inParagraph
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|handler
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
name|handler
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
name|handler
operator|.
name|characters
argument_list|(
name|text
operator|.
name|getCharacter
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
name|getCharacter
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
name|handler
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
name|handler
operator|.
name|characters
argument_list|(
literal|"\n"
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

