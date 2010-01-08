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
name|Iterator
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
name|ss
operator|.
name|usermodel
operator|.
name|Cell
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
name|CellStyle
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
name|Comment
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
name|ss
operator|.
name|usermodel
operator|.
name|Row
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
name|XSSFExcelExtractor
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
name|XSSFCell
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
name|XSSFSheet
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
name|XSSFWorkbook
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
name|SAXException
import|;
end_import

begin_class
specifier|public
class|class
name|XSSFExcelExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
comment|/**      * Internal<code>DataFormatter</code> for formatting Numbers.      */
specifier|private
specifier|final
name|DataFormatter
name|formatter
init|=
operator|new
name|DataFormatter
argument_list|()
decl_stmt|;
specifier|public
name|XSSFExcelExtractorDecorator
parameter_list|(
name|XSSFExcelExtractor
name|extractor
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|super
argument_list|(
name|extractor
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
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
name|XSSFWorkbook
name|document
init|=
operator|(
name|XSSFWorkbook
operator|)
name|extractor
operator|.
name|getDocument
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
name|document
operator|.
name|getNumberOfSheets
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|XSSFSheet
name|sheet
init|=
operator|(
name|XSSFSheet
operator|)
name|document
operator|.
name|getSheetAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|document
operator|.
name|getSheetName
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// Header(s), if present
name|extractHeaderFooter
argument_list|(
name|sheet
operator|.
name|getFirstHeader
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
name|extractHeaderFooter
argument_list|(
name|sheet
operator|.
name|getOddHeader
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
name|extractHeaderFooter
argument_list|(
name|sheet
operator|.
name|getEvenHeader
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
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
comment|// Rows and cells
for|for
control|(
name|Object
name|rawR
range|:
name|sheet
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|Row
name|row
init|=
operator|(
name|Row
operator|)
name|rawR
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|ri
init|=
name|row
operator|.
name|cellIterator
argument_list|()
init|;
name|ri
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|Cell
name|cell
init|=
name|ri
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|type
init|=
name|cell
operator|.
name|getCellType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Cell
operator|.
name|CELL_TYPE_FORMULA
condition|)
block|{
name|type
operator|=
name|cell
operator|.
name|getCachedFormulaResultType
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
name|Cell
operator|.
name|CELL_TYPE_STRING
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|cell
operator|.
name|getRichStringCellValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Cell
operator|.
name|CELL_TYPE_NUMERIC
condition|)
block|{
name|CellStyle
name|style
init|=
name|cell
operator|.
name|getCellStyle
argument_list|()
decl_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|formatter
operator|.
name|formatRawCellContents
argument_list|(
name|cell
operator|.
name|getNumericCellValue
argument_list|()
argument_list|,
name|style
operator|.
name|getIndex
argument_list|()
argument_list|,
name|style
operator|.
name|getDataFormatString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XSSFCell
name|xc
init|=
operator|(
name|XSSFCell
operator|)
name|cell
decl_stmt|;
name|String
name|rawValue
init|=
name|xc
operator|.
name|getRawValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|rawValue
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|rawValue
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Output the comment in the same cell as the content
name|Comment
name|comment
init|=
name|cell
operator|.
name|getCellComment
argument_list|()
decl_stmt|;
if|if
condition|(
name|comment
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
comment|// Finally footer(s), if present
name|extractHeaderFooter
argument_list|(
name|sheet
operator|.
name|getFirstFooter
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
name|extractHeaderFooter
argument_list|(
name|sheet
operator|.
name|getOddFooter
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
name|extractHeaderFooter
argument_list|(
name|sheet
operator|.
name|getEvenFooter
argument_list|()
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
block|}
specifier|private
name|void
name|extractHeaderFooter
parameter_list|(
name|HeaderFooter
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
name|hf
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
block|}
end_class

end_unit

