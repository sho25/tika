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
name|PDFOperator
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
name|pdfbox
operator|.
name|util
operator|.
name|operator
operator|.
name|OperatorProcessor
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
parameter_list|)
throws|throws
name|SAXException
throws|,
name|TikaException
block|{
try|try
block|{
operator|new
name|PDF2XHTML
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
operator|.
name|getText
argument_list|(
name|document
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
name|PDF2XHTML
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
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
comment|// TIKA-292: Ignore unneeded PDF operators
comment|// TODO: Remove this once PDFBox is no longer so verbose
name|OperatorProcessor
name|ignore
init|=
operator|new
name|OperatorProcessor
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|process
parameter_list|(
name|PDFOperator
name|operator
parameter_list|,
name|List
name|arguments
parameter_list|)
block|{             }
block|}
decl_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"b"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"B"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"b*"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"B*"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"BDC"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"BI"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"BMC"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"b"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"BX"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"c"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"CS"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"cs"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"d"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"d0"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"d1"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"DP"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"El"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"EMC"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"EX"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"f"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"F"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"f*"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"G"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"g"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"h"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"i"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"ID"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"j"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"J"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"K"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"k"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"l"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"m"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"M"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"MP"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"n"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"re"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"RG"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"rg"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"ri"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"s"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"S"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"SC"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"sc"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"SCN"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"scn"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"sh"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"v"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"W"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"W*"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
name|registerOperatorProcessor
argument_list|(
literal|"y"
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
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
name|handler
operator|.
name|endElement
argument_list|(
literal|"p"
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
comment|// Two methods added to work around lack of support for processWordSeparator
comment|// and processLineSeparator in PDFBox-0.7.3. This is fixed in CVS Head (PDFBox-0.7.4)
annotation|@
name|Override
specifier|public
name|String
name|getWordSeparator
parameter_list|()
block|{
try|try
block|{
name|handler
operator|.
name|characters
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
return|return
name|super
operator|.
name|getWordSeparator
argument_list|()
return|;
comment|//To change body of overridden methods use File | Settings | File Templates.
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLineSeparator
parameter_list|()
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
block|{          }
return|return
name|super
operator|.
name|getLineSeparator
argument_list|()
return|;
block|}
comment|//    protected void processLineSeparator(TextPosition p) throws IOException {
comment|//        try {
comment|//            handler.characters("\n");
comment|//        } catch (SAXException e) {
comment|//            throw new IOExceptionWithCause("Unable to write a newline", e);
comment|//        }
comment|//    }
comment|//
comment|//    protected void processWordSeparator(TextPosition a, TextPosition b)
comment|//            throws IOException {
comment|//        try {
comment|//            handler.characters(" ");
comment|//        } catch (SAXException e) {
comment|//            throw new IOExceptionWithCause("Unable to write a space", e);
comment|//        }
comment|//    }
block|}
end_class

end_unit

