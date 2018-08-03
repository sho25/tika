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
name|io
operator|.
name|InputStream
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
name|Locale
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
name|POIXMLTextExtractor
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
name|exceptions
operator|.
name|OpenXML4JException
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
name|OPCPackage
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
name|xssf
operator|.
name|binary
operator|.
name|XSSFBCommentsTable
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
name|xssf
operator|.
name|binary
operator|.
name|XSSFBSharedStringsTable
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
name|xssf
operator|.
name|binary
operator|.
name|XSSFBSheetHandler
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
name|xssf
operator|.
name|binary
operator|.
name|XSSFBStylesTable
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
name|xssf
operator|.
name|eventusermodel
operator|.
name|XSSFBReader
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
name|xssf
operator|.
name|eventusermodel
operator|.
name|XSSFSheetXMLHandler
operator|.
name|SheetContentsHandler
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
name|xssf
operator|.
name|extractor
operator|.
name|XSSFBEventBasedExcelExtractor
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
name|xssf
operator|.
name|usermodel
operator|.
name|XSSFShape
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

begin_class
specifier|public
class|class
name|XSSFBExcelExtractorDecorator
extends|extends
name|XSSFExcelExtractorDecorator
block|{
specifier|public
name|XSSFBExcelExtractorDecorator
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|POIXMLTextExtractor
name|extractor
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|extractor
argument_list|,
name|locale
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|configureExtractor
parameter_list|(
name|POIXMLTextExtractor
name|extractor
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
comment|//need to override this because setFormulasNotResults is not yet available
comment|//for xlsb
comment|//((XSSFBEventBasedExcelExtractor)extractor).setFormulasNotResults(false);
operator|(
operator|(
name|XSSFBEventBasedExcelExtractor
operator|)
name|extractor
operator|)
operator|.
name|setLocale
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|getXHTML
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
throws|,
name|TikaException
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|parseContext
operator|=
name|context
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|PROTECTED
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|super
operator|.
name|getXHTML
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.poi.xssf.extractor.XSSFBEventBasedExcelExtractor#getText()      */
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
name|OPCPackage
name|container
init|=
name|extractor
operator|.
name|getPackage
argument_list|()
decl_stmt|;
name|XSSFBSharedStringsTable
name|strings
decl_stmt|;
name|XSSFBReader
operator|.
name|SheetIterator
name|iter
decl_stmt|;
name|XSSFBReader
name|xssfReader
decl_stmt|;
name|XSSFBStylesTable
name|styles
decl_stmt|;
try|try
block|{
name|xssfReader
operator|=
operator|new
name|XSSFBReader
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|String
name|originalPath
init|=
name|xssfReader
operator|.
name|getAbsPathMetadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|originalPath
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|,
name|originalPath
argument_list|)
expr_stmt|;
block|}
name|styles
operator|=
name|xssfReader
operator|.
name|getXSSFBStylesTable
argument_list|()
expr_stmt|;
name|iter
operator|=
operator|(
name|XSSFBReader
operator|.
name|SheetIterator
operator|)
name|xssfReader
operator|.
name|getSheetsData
argument_list|()
expr_stmt|;
name|strings
operator|=
operator|new
name|XSSFBSharedStringsTable
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XmlException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|OpenXML4JException
name|oe
parameter_list|)
block|{
throw|throw
operator|new
name|XmlException
argument_list|(
name|oe
argument_list|)
throw|;
block|}
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|InputStream
name|stream
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|PackagePart
name|sheetPart
init|=
name|iter
operator|.
name|getSheetPart
argument_list|()
decl_stmt|;
name|addDrawingHyperLinks
argument_list|(
name|sheetPart
argument_list|)
expr_stmt|;
name|sheetParts
operator|.
name|add
argument_list|(
name|sheetPart
argument_list|)
expr_stmt|;
name|SheetTextAsHTML
name|sheetExtractor
init|=
operator|new
name|SheetTextAsHTML
argument_list|(
name|config
argument_list|,
name|xhtml
argument_list|)
decl_stmt|;
name|XSSFBCommentsTable
name|comments
init|=
name|iter
operator|.
name|getXSSFBSheetComments
argument_list|()
decl_stmt|;
comment|// Start, and output the sheet name
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|iter
operator|.
name|getSheetName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Extract the main sheet contents
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
name|processSheet
argument_list|(
name|sheetExtractor
argument_list|,
name|comments
argument_list|,
name|styles
argument_list|,
name|strings
argument_list|,
name|stream
argument_list|)
expr_stmt|;
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
comment|// Output any headers and footers
comment|// (Need to process the sheet to get them, so we can't
comment|//  do the headers before the contents)
for|for
control|(
name|String
name|header
range|:
name|sheetExtractor
operator|.
name|headers
control|)
block|{
name|extractHeaderFooter
argument_list|(
name|header
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|footer
range|:
name|sheetExtractor
operator|.
name|footers
control|)
block|{
name|extractHeaderFooter
argument_list|(
name|footer
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|XSSFShape
argument_list|>
name|shapes
init|=
name|iter
operator|.
name|getShapes
argument_list|()
decl_stmt|;
name|processShapes
argument_list|(
name|shapes
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|//for now dump sheet hyperlinks at bottom of page
comment|//consider a double-pass of the inputstream to reunite hyperlinks with cells/textboxes
comment|//step 1: extract hyperlink info from bottom of page
comment|//step 2: process as we do now, but with cached hyperlink relationship info
name|extractHyperLinks
argument_list|(
name|sheetPart
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// All done with this sheet
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|extractHeaderFooter
parameter_list|(
name|String
name|hf
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|hf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|hf
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|processSheet
parameter_list|(
name|SheetContentsHandler
name|sheetContentsExtractor
parameter_list|,
name|XSSFBCommentsTable
name|comments
parameter_list|,
name|XSSFBStylesTable
name|styles
parameter_list|,
name|XSSFBSharedStringsTable
name|strings
parameter_list|,
name|InputStream
name|sheetInputStream
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|XSSFBSheetHandler
name|xssfbSheetHandler
init|=
operator|new
name|XSSFBSheetHandler
argument_list|(
name|sheetInputStream
argument_list|,
name|styles
argument_list|,
name|comments
argument_list|,
name|strings
argument_list|,
name|sheetContentsExtractor
argument_list|,
name|formatter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|xssfbSheetHandler
operator|.
name|parse
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

