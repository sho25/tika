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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|hssf
operator|.
name|extractor
operator|.
name|ExcelExtractor
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
name|ss
operator|.
name|usermodel
operator|.
name|DataFormatter
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
name|ss
operator|.
name|usermodel
operator|.
name|HeaderFooter
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
name|ReadOnlySharedStringsTable
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
name|XSSFReader
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
name|XSSFEventBasedExcelExtractor
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
name|model
operator|.
name|CommentsTable
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
name|model
operator|.
name|StylesTable
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
name|XSSFComment
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
name|XSSFRelation
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
name|poi
operator|.
name|xssf
operator|.
name|usermodel
operator|.
name|XSSFSimpleShape
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
name|helpers
operator|.
name|HeaderFooterHelper
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
name|TikaMetadataKeys
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
name|Attributes
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
name|InputSource
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
name|Locator
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
name|XMLReader
import|;
end_import

begin_class
specifier|public
class|class
name|XSSFExcelExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
specifier|private
specifier|final
name|XSSFEventBasedExcelExtractor
name|extractor
decl_stmt|;
specifier|private
specifier|final
name|DataFormatter
name|formatter
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|PackagePart
argument_list|>
name|sheetParts
init|=
operator|new
name|ArrayList
argument_list|<
name|PackagePart
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
specifier|public
name|XSSFExcelExtractorDecorator
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|XSSFEventBasedExcelExtractor
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|extractor
operator|=
name|extractor
expr_stmt|;
name|extractor
operator|.
name|setFormulasNotResults
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|extractor
operator|.
name|setLocale
argument_list|(
name|locale
argument_list|)
expr_stmt|;
if|if
condition|(
name|locale
operator|==
literal|null
condition|)
block|{
name|formatter
operator|=
operator|new
name|DataFormatter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|formatter
operator|=
operator|new
name|DataFormatter
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
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
name|metadata
operator|.
name|set
argument_list|(
name|TikaMetadataKeys
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
comment|/**      * @see org.apache.poi.xssf.extractor.XSSFExcelExtractor#getText()      */
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
name|ReadOnlySharedStringsTable
name|strings
decl_stmt|;
name|XSSFReader
operator|.
name|SheetIterator
name|iter
decl_stmt|;
name|XSSFReader
name|xssfReader
decl_stmt|;
name|StylesTable
name|styles
decl_stmt|;
try|try
block|{
name|xssfReader
operator|=
operator|new
name|XSSFReader
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|styles
operator|=
name|xssfReader
operator|.
name|getStylesTable
argument_list|()
expr_stmt|;
name|iter
operator|=
operator|(
name|XSSFReader
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
name|ReadOnlySharedStringsTable
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
name|sheetParts
operator|.
name|add
argument_list|(
name|iter
operator|.
name|getSheetPart
argument_list|()
argument_list|)
expr_stmt|;
name|SheetTextAsHTML
name|sheetExtractor
init|=
operator|new
name|SheetTextAsHTML
argument_list|(
name|xhtml
argument_list|)
decl_stmt|;
name|CommentsTable
name|comments
init|=
name|iter
operator|.
name|getSheetComments
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
name|processShapes
argument_list|(
name|iter
operator|.
name|getShapes
argument_list|()
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
specifier|private
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
name|String
name|content
init|=
name|ExcelExtractor
operator|.
name|_extractHeaderFooter
argument_list|(
operator|new
name|HeaderFooterFromString
argument_list|(
name|hf
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|content
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
name|content
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|processShapes
parameter_list|(
name|List
argument_list|<
name|XSSFShape
argument_list|>
name|shapes
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|shapes
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|XSSFShape
name|shape
range|:
name|shapes
control|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|XSSFSimpleShape
condition|)
block|{
name|String
name|sText
init|=
operator|(
operator|(
name|XSSFSimpleShape
operator|)
name|shape
operator|)
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|sText
operator|!=
literal|null
operator|&&
name|sText
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
name|sText
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|processSheet
parameter_list|(
name|SheetContentsHandler
name|sheetContentsExtractor
parameter_list|,
name|CommentsTable
name|comments
parameter_list|,
name|StylesTable
name|styles
parameter_list|,
name|ReadOnlySharedStringsTable
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
name|InputSource
name|sheetSource
init|=
operator|new
name|InputSource
argument_list|(
name|sheetInputStream
argument_list|)
decl_stmt|;
name|SAXParserFactory
name|saxFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|SAXParser
name|saxParser
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|sheetParser
init|=
name|saxParser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|XSSFSheetInterestingPartsCapturer
name|handler
init|=
operator|new
name|XSSFSheetInterestingPartsCapturer
argument_list|(
operator|new
name|XSSFSheetXMLHandler
argument_list|(
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
argument_list|)
decl_stmt|;
name|sheetParser
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|sheetParser
operator|.
name|parse
argument_list|(
name|sheetSource
argument_list|)
expr_stmt|;
name|sheetInputStream
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|handler
operator|.
name|hasProtection
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TikaMetadataKeys
operator|.
name|PROTECTED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SAX parser appears to be broken - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Turns formatted sheet events into HTML      */
specifier|protected
specifier|static
class|class
name|SheetTextAsHTML
implements|implements
name|SheetContentsHandler
block|{
specifier|private
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|headers
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|footers
decl_stmt|;
specifier|protected
name|SheetTextAsHTML
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|)
block|{
name|this
operator|.
name|xhtml
operator|=
name|xhtml
expr_stmt|;
name|headers
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|footers
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startRow
parameter_list|(
name|int
name|rowNum
parameter_list|)
block|{
try|try
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{}
block|}
specifier|public
name|void
name|endRow
parameter_list|(
name|int
name|rowNum
parameter_list|)
block|{
try|try
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{}
block|}
specifier|public
name|void
name|cell
parameter_list|(
name|String
name|cellRef
parameter_list|,
name|String
name|formattedValue
parameter_list|,
name|XSSFComment
name|comment
parameter_list|)
block|{
try|try
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
comment|// Main cell contents
if|if
condition|(
name|formattedValue
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|formattedValue
argument_list|)
expr_stmt|;
block|}
comment|// Comments
if|if
condition|(
name|comment
operator|!=
literal|null
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
name|characters
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|comment
operator|.
name|getString
argument_list|()
operator|.
name|getString
argument_list|()
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
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{}
block|}
specifier|public
name|void
name|headerFooter
parameter_list|(
name|String
name|text
parameter_list|,
name|boolean
name|isHeader
parameter_list|,
name|String
name|tagName
parameter_list|)
block|{
if|if
condition|(
name|isHeader
condition|)
block|{
name|headers
operator|.
name|add
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|footers
operator|.
name|add
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Allows access to headers/footers from raw xml strings      */
specifier|private
specifier|static
name|HeaderFooterHelper
name|hfHelper
init|=
operator|new
name|HeaderFooterHelper
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
class|class
name|HeaderFooterFromString
implements|implements
name|HeaderFooter
block|{
specifier|private
name|String
name|text
decl_stmt|;
specifier|protected
name|HeaderFooterFromString
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
specifier|public
name|String
name|getCenter
parameter_list|()
block|{
return|return
name|hfHelper
operator|.
name|getCenterSection
argument_list|(
name|text
argument_list|)
return|;
block|}
specifier|public
name|String
name|getLeft
parameter_list|()
block|{
return|return
name|hfHelper
operator|.
name|getLeftSection
argument_list|(
name|text
argument_list|)
return|;
block|}
specifier|public
name|String
name|getRight
parameter_list|()
block|{
return|return
name|hfHelper
operator|.
name|getRightSection
argument_list|(
name|text
argument_list|)
return|;
block|}
specifier|public
name|void
name|setCenter
parameter_list|(
name|String
name|paramString
parameter_list|)
block|{}
specifier|public
name|void
name|setLeft
parameter_list|(
name|String
name|paramString
parameter_list|)
block|{}
specifier|public
name|void
name|setRight
parameter_list|(
name|String
name|paramString
parameter_list|)
block|{}
block|}
comment|/**      * Captures information on interesting tags, whilst      *  delegating the main work to the formatting handler      */
specifier|protected
specifier|static
class|class
name|XSSFSheetInterestingPartsCapturer
implements|implements
name|ContentHandler
block|{
specifier|private
name|ContentHandler
name|delegate
decl_stmt|;
specifier|private
name|boolean
name|hasProtection
init|=
literal|false
decl_stmt|;
specifier|protected
name|XSSFSheetInterestingPartsCapturer
parameter_list|(
name|ContentHandler
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"sheetProtection"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|hasProtection
operator|=
literal|true
expr_stmt|;
block|}
name|delegate
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|ignorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
name|delegate
operator|.
name|setDocumentLocator
argument_list|(
name|locator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|skippedEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|skippedEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * In Excel files, sheets have things embedded in them,      *  and sheet drawings which have the images      */
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
for|for
control|(
name|PackagePart
name|part
range|:
name|sheetParts
control|)
block|{
comment|// Add the sheet
name|parts
operator|.
name|add
argument_list|(
name|part
argument_list|)
expr_stmt|;
comment|// If it has drawings, return those too
try|try
block|{
for|for
control|(
name|PackageRelationship
name|rel
range|:
name|part
operator|.
name|getRelationshipsByType
argument_list|(
name|XSSFRelation
operator|.
name|DRAWINGS
operator|.
name|getRelation
argument_list|()
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
for|for
control|(
name|PackageRelationship
name|rel
range|:
name|part
operator|.
name|getRelationshipsByType
argument_list|(
name|XSSFRelation
operator|.
name|VML_DRAWINGS
operator|.
name|getRelation
argument_list|()
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

