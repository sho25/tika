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
name|eval
operator|.
name|AbstractProfiler
operator|.
name|EXCEPTION_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|eval
operator|.
name|AbstractProfiler
operator|.
name|getContent
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|eval
operator|.
name|io
operator|.
name|ExtractReader
operator|.
name|IGNORE_LENGTH
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|MockDBWriter
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
name|eval
operator|.
name|db
operator|.
name|Cols
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
name|eval
operator|.
name|db
operator|.
name|TableInfo
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
name|eval
operator|.
name|io
operator|.
name|ExtractReader
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
name|eval
operator|.
name|io
operator|.
name|ExtractReaderException
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
name|eval
operator|.
name|util
operator|.
name|LanguageIDWrapper
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

begin_comment
comment|//These tests ensure that the comparer is extracting the right information
end_comment

begin_comment
comment|//into a Map<String,String>.  A full integration test
end_comment

begin_comment
comment|//should also ensure that the elements are properly being written to the db
end_comment

begin_class
specifier|public
class|class
name|SimpleComparerTest
extends|extends
name|TikaTest
block|{
specifier|private
name|ExtractComparer
name|comparer
init|=
literal|null
decl_stmt|;
specifier|private
name|MockDBWriter
name|writer
init|=
literal|null
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
name|writer
operator|=
operator|new
name|MockDBWriter
argument_list|()
expr_stmt|;
name|comparer
operator|=
operator|new
name|ExtractComparer
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"extractsA"
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"extractsB"
argument_list|)
argument_list|,
operator|new
name|ExtractReader
argument_list|(
name|ExtractReader
operator|.
name|ALTER_METADATA_LIST
operator|.
name|AS_IS
argument_list|,
name|IGNORE_LENGTH
argument_list|,
name|IGNORE_LENGTH
argument_list|)
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|AbstractProfiler
operator|.
name|loadCommonTokens
argument_list|(
name|this
operator|.
name|getResourceAsFile
argument_list|(
literal|"/common_tokens"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
name|LanguageIDWrapper
operator|.
name|loadBuiltInModels
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
name|EvalFilePaths
name|fpsA
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file1.pdf.json"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsA/file1.pdf.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|EvalFilePaths
name|fpsB
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file1.pdf.json"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsB/file1.pdf.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|comparer
operator|.
name|compareFiles
argument_list|(
name|fpsA
argument_list|,
name|fpsB
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
name|tableInfos
init|=
name|writer
operator|.
name|getTable
argument_list|(
name|ExtractComparer
operator|.
name|CONTENT_COMPARISONS
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
name|row
init|=
name|tableInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|TOP_10_UNIQUE_TOKEN_DIFFS_A
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"1,200: 1 | 120000: 1 | over: 1"
argument_list|)
argument_list|)
expr_stmt|;
name|tableInfos
operator|=
name|writer
operator|.
name|getTable
argument_list|(
name|ExtractComparer
operator|.
name|CONTENTS_TABLE_A
argument_list|)
expr_stmt|;
name|row
operator|=
name|tableInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"70"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|CONTENT_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_UNIQUE_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"14"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"12"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_ALPHABETIC_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"6"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_COMMON_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"57"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|TOKEN_LENGTH_SUM
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"en"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|COMMON_TOKENS_LANG
argument_list|)
argument_list|)
expr_stmt|;
name|tableInfos
operator|=
name|writer
operator|.
name|getTable
argument_list|(
name|ExtractComparer
operator|.
name|CONTENTS_TABLE_B
argument_list|)
expr_stmt|;
name|row
operator|=
name|tableInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"76"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|CONTENT_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"9"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_UNIQUE_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"13"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_COMMON_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"64"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|TOKEN_LENGTH_SUM
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"en"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|COMMON_TOKENS_LANG
argument_list|)
argument_list|)
expr_stmt|;
name|tableInfos
operator|=
name|writer
operator|.
name|getTable
argument_list|(
name|ExtractComparer
operator|.
name|PROFILES_A
argument_list|)
expr_stmt|;
name|row
operator|=
name|tableInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_PAGES
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBasicSpanish
parameter_list|()
throws|throws
name|Exception
block|{
name|EvalFilePaths
name|fpsA
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file1.pdf.json"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsA/file12_es.txt.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|EvalFilePaths
name|fpsB
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file1.pdf.json"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsB/file12_es.txt.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|comparer
operator|.
name|compareFiles
argument_list|(
name|fpsA
argument_list|,
name|fpsB
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
name|tableInfos
init|=
name|writer
operator|.
name|getTable
argument_list|(
name|ExtractComparer
operator|.
name|CONTENTS_TABLE_A
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
name|row
init|=
name|tableInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"133"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|CONTENT_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"7"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_UNIQUE_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"24"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_COMMON_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"108"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|TOKEN_LENGTH_SUM
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"es"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|COMMON_TOKENS_LANG
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"24"
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|NUM_ALPHABETIC_TOKENS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|EvalFilePaths
name|fpsA
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file1.pdf"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsA/file1.pdf.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|EvalFilePaths
name|fpsB
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file1.pdf"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsB/file4_emptyB.pdf.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|comparer
operator|.
name|compareFiles
argument_list|(
name|fpsA
argument_list|,
name|fpsB
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
name|table
init|=
name|writer
operator|.
name|getTable
argument_list|(
name|ExtractComparer
operator|.
name|EXTRACT_EXCEPTION_TABLE_B
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
name|row
init|=
name|table
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//debugPrintRow(row);
name|assertEquals
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|ExtractReaderException
operator|.
name|TYPE
operator|.
name|ZERO_BYTE_EXTRACT_FILE
operator|.
name|ordinal
argument_list|()
argument_list|)
argument_list|,
name|row
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|EXTRACT_EXCEPTION_TYPE_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetContent
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m
operator|.
name|add
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|,
literal|"0123456789"
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|getContent
argument_list|(
name|m
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|content
operator|=
name|getContent
argument_list|(
name|m
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|//test Metadata with no content
name|content
operator|=
name|getContent
argument_list|(
operator|new
name|Metadata
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|//test null Metadata
name|content
operator|=
name|getContent
argument_list|(
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessException
parameter_list|()
throws|throws
name|Exception
block|{
name|EvalFilePaths
name|fpsA
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file6_accessEx.pdf.json"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsA/file6_accessEx.pdf.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|EvalFilePaths
name|fpsB
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file6_accessEx.pdf.json"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsB/file6_accessEx.pdf.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|comparer
operator|.
name|compareFiles
argument_list|(
name|fpsA
argument_list|,
name|fpsB
argument_list|)
expr_stmt|;
for|for
control|(
name|TableInfo
name|t
range|:
operator|new
name|TableInfo
index|[]
block|{
name|ExtractComparer
operator|.
name|EXCEPTION_TABLE_A
block|,
name|ExtractComparer
operator|.
name|EXCEPTION_TABLE_B
block|}
control|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
name|table
init|=
name|writer
operator|.
name|getTable
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
name|rowA
init|=
name|table
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//debugPrintRow(rowA);
name|assertEquals
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|EXCEPTION_TYPE
operator|.
name|ACCESS_PERMISSION
operator|.
name|ordinal
argument_list|()
argument_list|)
argument_list|,
name|rowA
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|PARSE_EXCEPTION_TYPE_ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|rowA
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|ORIG_STACK_TRACE
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|rowA
operator|.
name|get
argument_list|(
name|Cols
operator|.
name|SORT_STACK_TRACE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAttachmentCounts
parameter_list|()
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Metadata
name|m0
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m0
operator|.
name|set
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_PATH
argument_list|,
literal|"dir1/dir2/file.zip"
argument_list|)
expr_stmt|;
comment|//bad data should be ignored
comment|//in the first metadata object
name|list
operator|.
name|add
argument_list|(
name|m0
argument_list|)
expr_stmt|;
name|Metadata
name|m1
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m1
operator|.
name|set
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_PATH
argument_list|,
literal|"/f1.docx/f2.zip/text1.txt"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|m1
argument_list|)
expr_stmt|;
name|Metadata
name|m2
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m2
operator|.
name|set
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_PATH
argument_list|,
literal|"/f1.docx/f2.zip/text2.txt"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|m2
argument_list|)
expr_stmt|;
name|Metadata
name|m3
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m3
operator|.
name|set
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_PATH
argument_list|,
literal|"/f1.docx/f2.zip"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|m3
argument_list|)
expr_stmt|;
name|Metadata
name|m4
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m4
operator|.
name|set
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_PATH
argument_list|,
literal|"/f1.docx"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|m4
argument_list|)
expr_stmt|;
name|Metadata
name|m5
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m5
operator|.
name|set
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_PATH
argument_list|,
literal|"/f1.docx/text3.txt"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|m5
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|counts
init|=
name|AbstractProfiler
operator|.
name|countAttachments
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|expected
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|counts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|testDebug
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|commonTokens
init|=
name|Paths
operator|.
name|get
argument_list|(
name|getResourceAsFile
argument_list|(
literal|"/common_tokens_short.txt"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|AbstractProfiler
operator|.
name|loadCommonTokens
argument_list|(
name|commonTokens
argument_list|)
expr_stmt|;
name|EvalFilePaths
name|fpsA
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file1.pdf.json"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsA/file1.pdf.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|EvalFilePaths
name|fpsB
init|=
operator|new
name|EvalFilePaths
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"file1.pdf.json"
argument_list|)
argument_list|,
name|getResourceAsFile
argument_list|(
literal|"/test-dirs/extractsB/file1.pdf.json"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|comparer
operator|.
name|compareFiles
argument_list|(
name|fpsA
argument_list|,
name|fpsB
argument_list|)
expr_stmt|;
for|for
control|(
name|TableInfo
name|t
range|:
operator|new
name|TableInfo
index|[]
block|{
name|ExtractComparer
operator|.
name|COMPARISON_CONTAINERS
block|,
name|ExtractComparer
operator|.
name|EXTRACT_EXCEPTION_TABLE_A
block|,
name|ExtractComparer
operator|.
name|EXTRACT_EXCEPTION_TABLE_B
block|,
name|ExtractComparer
operator|.
name|EXCEPTION_TABLE_A
block|,
name|ExtractComparer
operator|.
name|EXCEPTION_TABLE_B
block|,
name|ExtractComparer
operator|.
name|PROFILES_A
block|,
name|ExtractComparer
operator|.
name|PROFILES_B
block|,
name|ExtractComparer
operator|.
name|CONTENTS_TABLE_A
block|,
name|ExtractComparer
operator|.
name|CONTENTS_TABLE_B
block|,
name|ExtractComparer
operator|.
name|CONTENT_COMPARISONS
block|}
control|)
block|{
comment|//debugPrintTable(t);
block|}
block|}
specifier|private
name|void
name|debugPrintTable
parameter_list|(
name|TableInfo
name|tableInfo
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
name|table
init|=
name|writer
operator|.
name|getTable
argument_list|(
name|tableInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TABLE: "
operator|+
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
name|row
range|:
name|table
control|)
block|{
name|SortedSet
argument_list|<
name|Cols
argument_list|>
name|keys
init|=
operator|new
name|TreeSet
argument_list|<
name|Cols
argument_list|>
argument_list|(
name|row
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Cols
name|key
range|:
name|keys
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" :: "
operator|+
name|key
operator|+
literal|" : "
operator|+
name|row
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|debugPrintRow
parameter_list|(
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
name|row
parameter_list|)
block|{
name|SortedSet
argument_list|<
name|Cols
argument_list|>
name|keys
init|=
operator|new
name|TreeSet
argument_list|<
name|Cols
argument_list|>
argument_list|(
name|row
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Cols
name|key
range|:
name|keys
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|key
operator|+
literal|" : "
operator|+
name|row
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

