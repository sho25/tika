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
name|envi
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
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
comment|/**  * Test cases to exercise the {@link EnviHeaderParser}.  */
end_comment

begin_class
specifier|public
class|class
name|EnviHeaderParserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testParseGlobalMetadata
parameter_list|()
throws|throws
name|Exception
block|{
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
return|return;
block|}
name|Parser
name|parser
init|=
operator|new
name|EnviHeaderParser
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
name|InputStream
name|stream
init|=
name|EnviHeaderParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/envi_test_header.hdr"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Test ENVI file not found"
argument_list|,
name|stream
argument_list|)
expr_stmt|;
try|try
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
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Check content of test file
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
literal|"<body><p>ENVI</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>samples = 2400</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>lines   = 2400</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>map info = {Sinusoidal, 1.5000, 1.5000, -10007091.3643, 5559289.2856, 4.6331271653e+02, 4.6331271653e+02, , units=Meters}</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"content=\"application/envi.hdr\""
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"projection info = {16, 6371007.2, 0.000000, 0.0, 0.0, Sinusoidal, units=Meters}"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

