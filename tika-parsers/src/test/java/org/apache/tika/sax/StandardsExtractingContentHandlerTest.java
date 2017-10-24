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
name|sax
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Parser
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
comment|/**  * Test class for the {@link StandardsExtractingContentHandler} class.  */
end_comment

begin_class
specifier|public
class|class
name|StandardsExtractingContentHandlerTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testExtractStandards
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|StandardsExtractingContentHandler
name|handler
init|=
operator|new
name|StandardsExtractingContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|handler
operator|.
name|setThreshold
argument_list|(
literal|0.75
argument_list|)
expr_stmt|;
name|InputStream
name|inputStream
init|=
name|StandardsExtractingContentHandlerTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testStandardsExtractor.pdf"
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|inputStream
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
name|String
index|[]
name|standardReferences
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|StandardsExtractingContentHandler
operator|.
name|STANDARD_REFERENCES
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|standardReferences
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"ANSI/TIA 222-G"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|standardReferences
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"TIA/ANSI 222-G-1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|standardReferences
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
literal|"FIPS 140-2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|standardReferences
index|[
literal|3
index|]
operator|.
name|equals
argument_list|(
literal|"FIPS 197"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

