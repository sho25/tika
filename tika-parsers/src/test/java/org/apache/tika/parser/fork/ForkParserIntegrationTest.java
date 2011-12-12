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
name|fork
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
name|fork
operator|.
name|ForkParser
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

begin_comment
comment|/**  * Test that the ForkParser correctly behaves when  *  wired in to the regular Parsers and their test data  */
end_comment

begin_class
specifier|public
class|class
name|ForkParserIntegrationTest
extends|extends
name|TestCase
block|{
comment|/**      * Simple text parsing      * TODO Fix this test so it passes      */
specifier|public
name|void
name|DISABLEDtestForkedTextParsing
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|ForkParser
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|ContentHandler
name|output
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"testTXT.txt"
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|output
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|output
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
literal|"Test d'indexation"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"http://www.apache.org/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * TIKA-808 - Ensure that parsing of our test PDFs work under      *  the Fork Parser, to ensure that complex parsing behaves      * TODO Fix this test so it passes      */
specifier|public
name|void
name|DISABLEDtestForkedPDFParsing
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|ForkParser
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|ContentHandler
name|output
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"testPDF.pdf"
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|output
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|output
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
literal|"Apache Tika"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Tika - Content Analysis Toolkit"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"incubator"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Apache Software Foundation"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testDUMMY
parameter_list|()
block|{
comment|// To avoid warnings about no tests while others are disabled
block|}
block|}
end_class

end_unit

