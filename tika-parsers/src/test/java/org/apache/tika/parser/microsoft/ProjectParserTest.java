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
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|OfficeOpenXMLCore
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
comment|/**  * Tests for Microsoft Project (MPP) Files.  *   * Note - we don't currently have a dedicated Project  *  Parser, all we have is the common office metadata  */
end_comment

begin_class
specifier|public
class|class
name|ProjectParserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testProject2003
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ProjectParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPROJECT2003.mpp"
argument_list|)
decl_stmt|;
try|try
block|{
name|doTestProject
argument_list|(
name|input
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
name|testProject2007
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|ProjectParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPROJECT2007.mpp"
argument_list|)
decl_stmt|;
try|try
block|{
name|doTestProject
argument_list|(
name|input
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
specifier|private
name|void
name|doTestProject
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|Exception
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
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-project"
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
literal|"The quick brown fox jumps over the lazy dog"
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
literal|"Gym class featuring a brown fox and lazy dog"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Gym class featuring a brown fox and lazy dog"
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
name|assertEquals
argument_list|(
literal|"Nevin Nollop"
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
literal|"Pangram, fox, dog"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|KEYWORDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Comment Vulpes vulpes comment"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|COMMENTS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Category1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|CATEGORY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Mr Burns"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|MANAGER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"CompanyA"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|COMPANY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2011-11-24T10:58:00Z"
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
literal|"2011-11-24T10:58:00Z"
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
name|assertEquals
argument_list|(
literal|"2011-11-24T11:31:00Z"
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
literal|"2011-11-24T11:31:00Z"
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
comment|// Custom Project metadata is present with prefix
name|assertEquals
argument_list|(
literal|"0%"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:% Complete"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0%"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:% Work Complete"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\u00a3"
operator|+
literal|"0.00"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Cost"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2d?"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Duration"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"16h"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Work"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Currently, we don't do textual contents of the file
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
block|}
end_class

end_unit

