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
name|language
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|exception
operator|.
name|TikaException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|LanguageProfilerBuilderTest
block|{
comment|/* Test members */
specifier|private
name|LanguageProfilerBuilder
name|ngramProfile
init|=
literal|null
decl_stmt|;
specifier|private
name|LanguageProfile
name|langProfile
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|String
name|profileName
init|=
literal|"../tika-core/src/test/resources/org/apache/tika/language/langbuilder/"
operator|+
name|LanguageProfilerBuilderTest
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|corpusName
init|=
literal|"langbuilder/welsh_corpus.txt"
decl_stmt|;
specifier|private
specifier|final
name|String
name|FILE_EXTENSION
init|=
literal|"ngp"
decl_stmt|;
specifier|private
specifier|final
name|String
name|LANGUAGE
init|=
literal|"welsh"
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxlen
init|=
literal|1000
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testCreateProfile
parameter_list|()
throws|throws
name|TikaException
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|LanguageProfilerBuilderTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|corpusName
argument_list|)
init|)
block|{
name|ngramProfile
operator|=
name|LanguageProfilerBuilder
operator|.
name|create
argument_list|(
name|profileName
argument_list|,
name|is
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|profileName
operator|+
literal|"."
operator|+
name|FILE_EXTENSION
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|ngramProfile
operator|.
name|save
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|maxlen
argument_list|,
name|ngramProfile
operator|.
name|getSorted
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNGramProfile
parameter_list|()
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|URISyntaxException
block|{
name|createLanguageProfile
argument_list|()
expr_stmt|;
name|LanguageIdentifier
operator|.
name|addProfile
argument_list|(
name|LANGUAGE
argument_list|,
name|langProfile
argument_list|)
expr_stmt|;
name|LanguageIdentifier
name|identifier
init|=
operator|new
name|LanguageIdentifier
argument_list|(
name|langProfile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|LANGUAGE
argument_list|,
name|identifier
operator|.
name|getLanguage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|identifier
operator|.
name|isReasonablyCertain
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createLanguageProfile
parameter_list|()
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|URISyntaxException
block|{
comment|// Sort of dependency injection
if|if
condition|(
name|ngramProfile
operator|==
literal|null
condition|)
name|testCreateProfile
argument_list|()
expr_stmt|;
name|langProfile
operator|=
operator|new
name|LanguageProfile
argument_list|()
expr_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|profileName
operator|+
literal|"."
operator|+
name|FILE_EXTENSION
argument_list|)
argument_list|)
init|)
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
comment|// skips the
comment|// ngp
comment|// header/comment
name|int
name|space
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
name|langProfile
operator|.
name|add
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|space
argument_list|)
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|space
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|profile
init|=
operator|new
name|File
argument_list|(
name|profileName
operator|+
literal|"."
operator|+
name|FILE_EXTENSION
argument_list|)
decl_stmt|;
if|if
condition|(
name|profile
operator|.
name|exists
argument_list|()
condition|)
name|profile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

