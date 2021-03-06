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
name|mat
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
name|parser
operator|.
name|Parser
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
name|ToXMLContentHandler
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

begin_comment
comment|/**  * Test cases to exercise the {@link MatParser}.  */
end_comment

begin_class
specifier|public
class|class
name|MatParserTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testParser
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|xmlResult
init|=
name|getXML
argument_list|(
literal|"breidamerkurjokull_radar_profiles_2009.mat"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
name|xmlResult
operator|.
name|metadata
decl_stmt|;
name|String
name|content
init|=
name|xmlResult
operator|.
name|xml
decl_stmt|;
comment|// Check Metadata
name|assertEquals
argument_list|(
literal|"PCWIN64"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"platform"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"MATLAB 5.0 MAT-file"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"fileType"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"IM"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"endian"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Thu Feb 21 15:52:49 2013"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"createdOn"
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<li>[1x909  double array]</li>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>c1:[1x1  struct array]</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<li>[1024x1  double array]</li>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>b1:[1x1  struct array]</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>a1:[1x1  struct array]</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<li>[1024x1261  double array]</li>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<li>[1x1  double array]</li>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"</body></html>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParserForText
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|MatParser
argument_list|()
decl_stmt|;
name|ToXMLContentHandler
name|handler
init|=
operator|new
name|ToXMLContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|"/test-documents/test_mat_text.mat"
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
init|)
block|{
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
block|}
comment|// Check Content
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
literal|"<p>double:[2x2  double array]</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

