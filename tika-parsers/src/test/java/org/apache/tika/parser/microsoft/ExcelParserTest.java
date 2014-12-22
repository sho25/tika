begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|TikaTest
operator|.
name|assertContains
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|TikaTest
operator|.
name|assertNotContained
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|Locale
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
name|detect
operator|.
name|DefaultDetector
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
name|detect
operator|.
name|Detector
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
name|EncryptedDocumentException
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
name|Office
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
name|OfficeOpenXMLExtended
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
name|mime
operator|.
name|MediaType
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
name|AutoDetectParser
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
name|parser
operator|.
name|PasswordProvider
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
name|microsoft
operator|.
name|ooxml
operator|.
name|OOXMLParser
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
name|BodyContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
specifier|public
class|class
name|ExcelParserTest
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// Checks legacy Tika-1.0 style metadata keys
specifier|public
name|void
name|testExcelParser
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL.xls"
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Simple Excel document"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Keith Bennett"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Keith Bennett"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
comment|// Mon Oct 01 17:13:56 BST 2007
name|assertEquals
argument_list|(
literal|"2007-10-01T16:13:56Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2007-10-01T16:13:56Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Mon Oct 01 17:31:43 BST 2007
name|assertEquals
argument_list|(
literal|"2007-10-01T16:31:43Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2007-10-01T16:31:43Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Sample Excel Worksheet"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Numbers and their Squares"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\t\tNumber\tSquare"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"9"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"9.0"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"196"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"196.0"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExcelParserFormatting
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL-formats.xls"
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Number #,##0.00
name|assertContains
argument_list|(
literal|"1,599.99"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"-1,599.99"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Currency $#,##0.00;[Red]($#,##0.00)
name|assertContains
argument_list|(
literal|"$1,599.99"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"($1,599.99)"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Scientific 0.00E+00
comment|// poi<=3.8beta1 returns 1.98E08, newer versions return 1.98+E08
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"1.98E08"
argument_list|)
operator|||
name|content
operator|.
name|contains
argument_list|(
literal|"1.98E+08"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"-1.98E08"
argument_list|)
operator|||
name|content
operator|.
name|contains
argument_list|(
literal|"-1.98E+08"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Percentage.
name|assertContains
argument_list|(
literal|"2.50%"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Excel rounds up to 3%, but that requires Java 1.6 or later
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"1.5"
argument_list|)
condition|)
block|{
name|assertContains
argument_list|(
literal|"2%"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertContains
argument_list|(
literal|"3%"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
comment|// Time Format: h:mm
name|assertContains
argument_list|(
literal|"6:15"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"18:15"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Date Format: d-mmm-yy
name|assertContains
argument_list|(
literal|"17-May-07"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Date Format: m/d/yy
name|assertContains
argument_list|(
literal|"10/3/09"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Date/Time Format: m/d/yy h:mm
name|assertContains
argument_list|(
literal|"1/19/08 4:35"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Fraction (2.5): # ?/?
name|assertContains
argument_list|(
literal|"2 1/2"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Below assertions represent outstanding formatting issues to be addressed
comment|// they are included to allow the issues to be progressed with the Apache POI
comment|// team - See TIKA-103.
comment|/*************************************************************************             // Custom Number (0 "dollars and" .00 "cents")             assertContains("19 dollars and .99 cents", content);              // Custom Number ("At" h:mm AM/PM "on" dddd mmmm d"," yyyy)             assertContains("At 4:20 AM on Thursday May 17, 2007", content);             **************************************************************************/
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExcelParserPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL_protected_passtika.xls"
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Document is encrypted, shouldn't parse"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EncryptedDocumentException
name|e
parameter_list|)
block|{
comment|// Good
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Try again, this time with the password
name|input
operator|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL_protected_passtika.xls"
argument_list|)
expr_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|context
operator|.
name|set
argument_list|(
name|PasswordProvider
operator|.
name|class
argument_list|,
operator|new
name|PasswordProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
literal|"tika"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Antoni"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2011-11-25T09:52:48Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"This is an Encrypted Excel spreadsheet"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"9.0"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * TIKA-214 - Ensure we extract labels etc from Charts      */
annotation|@
name|Test
specifier|public
name|void
name|testExcelParserCharts
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL-charts.xls"
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// The first sheet has a pie chart
name|assertContains
argument_list|(
literal|"charttabyodawg"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"WhamPuff"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// The second sheet has a bar chart and some text
name|assertContains
argument_list|(
literal|"Sheet1"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Test Excel Spreasheet"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"foo"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"bar"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"fizzlepuff"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"whyaxis"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"eksaxis"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// The third sheet has some text
name|assertContains
argument_list|(
literal|"Sheet2"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"dingdong"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJXL
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/jxl.xls"
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Number Formats"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWorksSpreadsheet70
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORKSSpreadsheet7.0.xlr"
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Microsoft Works"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * We don't currently support the .xlsb file format       *  (an OOXML container with binary blobs), but we       *  shouldn't break on these files either (TIKA-826)        */
annotation|@
name|Test
specifier|public
name|void
name|testExcelXLSB
parameter_list|()
throws|throws
name|Exception
block|{
name|Detector
name|detector
init|=
operator|new
name|DefaultDetector
argument_list|()
decl_stmt|;
name|AutoDetectParser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|InputStream
name|input
init|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL.xlsb"
argument_list|)
decl_stmt|;
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"excel.xlsb"
argument_list|)
expr_stmt|;
comment|// Should be detected correctly
name|MediaType
name|type
init|=
literal|null
decl_stmt|;
try|try
block|{
name|type
operator|=
name|detector
operator|.
name|detect
argument_list|(
name|input
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel.sheet.binary.macroenabled.12"
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// OfficeParser won't handle it
name|assertEquals
argument_list|(
literal|false
argument_list|,
operator|(
operator|new
name|OfficeParser
argument_list|()
operator|)
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
comment|// OOXMLParser won't handle it
name|assertEquals
argument_list|(
literal|false
argument_list|,
operator|(
operator|new
name|OOXMLParser
argument_list|()
operator|)
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
comment|// AutoDetectParser doesn't break on it
name|input
operator|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL.xlsb"
argument_list|)
expr_stmt|;
try|try
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|m
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Excel 5 and 95 are older formats, and only get basic support      */
annotation|@
name|Test
specifier|public
name|void
name|testExcel95
parameter_list|()
throws|throws
name|Exception
block|{
name|Detector
name|detector
init|=
operator|new
name|DefaultDetector
argument_list|()
decl_stmt|;
name|AutoDetectParser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|InputStream
name|input
decl_stmt|;
name|MediaType
name|type
decl_stmt|;
name|Metadata
name|m
decl_stmt|;
comment|// First try detection of Excel 5
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|m
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"excel_5.xls"
argument_list|)
expr_stmt|;
name|input
operator|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL_5.xls"
argument_list|)
expr_stmt|;
try|try
block|{
name|type
operator|=
name|detector
operator|.
name|detect
argument_list|(
name|input
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Now Excel 95
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|m
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"excel_95.xls"
argument_list|)
expr_stmt|;
name|input
operator|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL_95.xls"
argument_list|)
expr_stmt|;
try|try
block|{
name|type
operator|=
name|detector
operator|.
name|detect
argument_list|(
name|input
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// OfficeParser can handle it
name|assertEquals
argument_list|(
literal|true
argument_list|,
operator|(
operator|new
name|OfficeParser
argument_list|()
operator|)
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
comment|// OOXMLParser won't handle it
name|assertEquals
argument_list|(
literal|false
argument_list|,
operator|(
operator|new
name|OOXMLParser
argument_list|()
operator|)
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
comment|// Parse the Excel 5 file
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|input
operator|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL_5.xls"
argument_list|)
expr_stmt|;
try|try
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|m
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Sheet names
name|assertContains
argument_list|(
literal|"Feuil1"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Feuil3"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Text
name|assertContains
argument_list|(
literal|"Sample Excel"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Number"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Numbers
name|assertContains
argument_list|(
literal|"15"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"225"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Metadata was also fetched
name|assertEquals
argument_list|(
literal|"Simple Excel document"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Keith Bennett"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Parse the Excel 95 file
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|input
operator|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL_95.xls"
argument_list|)
expr_stmt|;
try|try
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|m
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Sheet name
name|assertContains
argument_list|(
literal|"Foglio1"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Very boring file, no actual text or numbers!
comment|// Metadata was also fetched
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Marco Quaranta"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Office
operator|.
name|LAST_AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Ensures that custom OLE2 (HPSF) properties are extracted      */
annotation|@
name|Test
specifier|public
name|void
name|testCustomProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ExcelParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL_custom_props.xls"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2011-08-22T13:45:54Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2006-09-12T15:06:44Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Microsoft Excel"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:myCustomBoolean"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:myCustomNumber"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"MyStringValue"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:MyCustomString"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2010-12-30T22:00:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:MyCustomDate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2010-12-29T22:00:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:myCustomSecondDate"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

