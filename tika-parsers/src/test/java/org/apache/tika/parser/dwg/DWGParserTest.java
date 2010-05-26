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
name|dwg
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
name|DWGParserTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testDWG2004Parser
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|DWGParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testDWG2004.dwg"
argument_list|)
decl_stmt|;
name|testParser
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDWG2007Parser
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|DWGParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testDWG2007.dwg"
argument_list|)
decl_stmt|;
name|testParser
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDWG2010Parser
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|DWGParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testDWG2010.dwg"
argument_list|)
decl_stmt|;
name|testParser
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testParser
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|Exception
block|{
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
name|DWGParser
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
comment|//            assertEquals(
comment|//                    "application/dwg",
comment|//                    metadata.get(Metadata.CONTENT_TYPE));
name|assertEquals
argument_list|(
literal|"The quick brown fox jumps over the lazy dog"
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
name|Metadata
operator|.
name|AUTHOR
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
name|Metadata
operator|.
name|KEYWORDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lorem ipsum"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|COMMENTS
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|11
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://www.alfresco.com"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RELATION
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
literal|"The quick brown fox jumps over the lazy dog"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Gym class"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"www.alfresco.com"
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
block|}
end_class

end_unit

