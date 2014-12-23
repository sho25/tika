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
name|parser
operator|.
name|microsoft
operator|.
name|AbstractPOIContainerExtractionTest
operator|.
name|getTestFile
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
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|TikaTest
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
name|BodyContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Tests for the Old Excel (2-4) parser  */
end_comment

begin_class
specifier|public
class|class
name|OldExcelParserTest
extends|extends
name|TikaTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|file
init|=
literal|"testEXCEL_4.xls"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaInputStream
name|stream
init|=
name|getTestFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|Detector
name|detector
init|=
operator|new
name|DefaultDetector
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.sheet.4"
argument_list|)
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Disabled, until we can get the POI code to tell us the version
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|testMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaInputStream
name|stream
init|=
name|getTestFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
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
name|OldExcelParser
name|parser
init|=
operator|new
name|OldExcelParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
comment|// We can get the content type
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel.sheet.4"
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
comment|// But no other metadata
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
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Check we can get the plain text properly      */
annotation|@
name|Test
specifier|public
name|void
name|testPlainText
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|TikaInputStream
name|stream
init|=
name|getTestFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|OldExcelParser
argument_list|()
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
name|text
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Check we find a few words we expect in there
name|assertContains
argument_list|(
literal|"Size"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Returns"
argument_list|,
name|text
argument_list|)
expr_stmt|;
comment|// Check we find a few numbers we expect in there
name|assertContains
argument_list|(
literal|"11"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"784"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
comment|/**      * Check the HTML version comes through correctly      */
annotation|@
name|Test
specifier|public
name|void
name|testHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|result
init|=
name|getXML
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|result
operator|.
name|xml
decl_stmt|;
comment|// Sheet name not found - only 5+ have sheet names
name|assertNotContained
argument_list|(
literal|"<p>Sheet 1</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// String cells
name|assertContains
argument_list|(
literal|"<p>Table 10 -"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>Tax</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>N/A</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// Number cells
name|assertContains
argument_list|(
literal|"<p>(1)</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>5.0</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
