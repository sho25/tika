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
name|asm
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
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|Tika
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test case for parsing Java class files.  */
end_comment

begin_class
specifier|public
class|class
name|ClassParserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testClassParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/test-documents/AutoDetectParser.class"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
operator|new
name|Tika
argument_list|()
operator|.
name|parseToString
argument_list|(
name|ClassParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"AutoDetectParser"
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
literal|"AutoDetectParser.class"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"package org.apache.tika.parser;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"class AutoDetectParser extends CompositeParser"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"private org.apache.tika.mime.MimeTypes types"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"public void parse("
operator|+
literal|"java.io.InputStream, org.xml.sax.ContentHandler,"
operator|+
literal|" org.apache.tika.metadata.Metadata) throws"
operator|+
literal|" java.io.IOException, org.xml.sax.SAXException,"
operator|+
literal|" org.apache.tika.exception.TikaException;"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"private byte[] getPrefix(java.io.InputStream, int)"
operator|+
literal|" throws java.io.IOException;"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

