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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|BodyContentHandler
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
extends|extends
name|TestCase
block|{
comment|// TODO: This is a workaround until TIKA-371 is fixed
specifier|private
name|Locale
name|defaultLocale
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
name|defaultLocale
operator|=
name|Locale
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|Locale
operator|.
name|setDefault
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
name|Locale
operator|.
name|setDefault
argument_list|(
name|defaultLocale
argument_list|)
expr_stmt|;
block|}
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
name|Metadata
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
name|Metadata
operator|.
name|AUTHOR
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
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Sample Excel Worksheet"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Numbers and their Squares"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"\t\tNumber\tSquare"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"9.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"196"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"196.0"
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
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"1,599.99"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"-1,599.99"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Currency $#,##0.00;[Red]($#,##0.00)
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"$1,599.99"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"($1,599.99)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Scientific 0.00E+00
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"1.98E08"
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
argument_list|)
expr_stmt|;
comment|// Percentage
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"2%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"2.50%"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Time Format: h:mm
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"6:15"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"18:15"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Date Format: d-mmm-yy
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"17-May-07"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Below assertions represent outstanding formatting issues to be addressed
comment|// they are included to allow the issues to be progressed with the Apache POI
comment|// team - See TIKA-103.
comment|/*************************************************************************             // Date Format: m/d/yy             assertTrue(content.contains("03/10/2009"));              // Date/Time Format             assertTrue(content.contains("19/01/2008 04:35"));              // Custom Number (0 "dollars and" .00 "cents")             assertTrue(content.contains("19 dollars and .99 cents"));              // Custom Number ("At" h:mm AM/PM "on" dddd mmmm d"," yyyy)             assertTrue(content.contains("At 4:20 AM on Thursday May 17, 2007"));              // Fraction (2.5): # ?/?             assertTrue(content.contains("2 1 / 2"));             **************************************************************************/
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
block|}
end_class

end_unit

