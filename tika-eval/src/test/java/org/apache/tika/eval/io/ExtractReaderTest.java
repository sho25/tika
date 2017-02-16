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
name|eval
operator|.
name|io
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|RecursiveParserWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
specifier|public
class|class
name|ExtractReaderTest
extends|extends
name|TikaTest
block|{
specifier|private
name|Path
name|testJsonFile
decl_stmt|;
specifier|private
name|Path
name|testTxtFile
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|testJsonFile
operator|=
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsA/file2_attachANotB.doc.json"
argument_list|)
operator|.
name|toPath
argument_list|()
expr_stmt|;
name|testTxtFile
operator|=
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsB/file13_attachANotB.doc.txt"
argument_list|)
operator|.
name|toPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractReader
name|extractReader
init|=
operator|new
name|ExtractReader
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|extractReader
operator|.
name|loadExtract
argument_list|(
name|testJsonFile
argument_list|,
name|ExtractReader
operator|.
name|ALTER_METADATA_LIST
operator|.
name|AS_IS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getValues
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"fox"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"attachment"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|metadataList
operator|=
name|extractReader
operator|.
name|loadExtract
argument_list|(
name|testJsonFile
argument_list|,
name|ExtractReader
operator|.
name|ALTER_METADATA_LIST
operator|.
name|FIRST_ONLY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"fox"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"attachment"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|metadataList
operator|=
name|extractReader
operator|.
name|loadExtract
argument_list|(
name|testJsonFile
argument_list|,
name|ExtractReader
operator|.
name|ALTER_METADATA_LIST
operator|.
name|CONCATENATE_CONTENT_INTO_FIRST
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"fox"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"attachment"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTextBasic
parameter_list|()
throws|throws
name|IOException
block|{
name|ExtractReader
name|extractReader
init|=
operator|new
name|ExtractReader
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|extractReader
operator|.
name|loadExtract
argument_list|(
name|testTxtFile
argument_list|,
name|ExtractReader
operator|.
name|ALTER_METADATA_LIST
operator|.
name|AS_IS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|m
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|m
operator|.
name|getValues
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the quick brown fox fox fox jumped over the lazy lazy dog\n"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
comment|//test that the mime is inferred from the file extension
name|assertEquals
argument_list|(
literal|"application/msword"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

