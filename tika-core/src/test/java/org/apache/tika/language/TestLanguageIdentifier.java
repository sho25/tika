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

begin_comment
comment|// JDK imports
end_comment

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
name|BufferedReader
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
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|language
operator|.
name|NGramProfile
operator|.
name|NGramEntry
import|;
end_import

begin_comment
comment|// JUnit imports
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_comment
comment|/**  * JUnit based test of class {@link LanguageIdentifier}.  *  * @author Sami Siren  * @author Jerome Charron - http://frutch.free.fr/  */
end_comment

begin_class
specifier|public
class|class
name|TestLanguageIdentifier
extends|extends
name|TestCase
block|{
specifier|public
name|TestLanguageIdentifier
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|super
argument_list|(
name|testName
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
operator|new
name|TestSuite
argument_list|(
name|TestLanguageIdentifier
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|tokencontent1
init|=
literal|"testaddtoken"
decl_stmt|;
name|String
name|tokencontent2
init|=
literal|"anotherteststring"
decl_stmt|;
name|int
index|[]
name|counts1
init|=
block|{
literal|3
block|,
literal|2
block|,
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
name|String
index|[]
name|chars1
init|=
block|{
literal|"t"
block|,
literal|"d"
block|,
literal|"e"
block|,
literal|"a"
block|,
literal|"k"
block|,
literal|"n"
block|,
literal|"o"
block|,
literal|"s"
block|}
decl_stmt|;
comment|/**    * Test analyze method    */
specifier|public
name|void
name|testAnalyze
parameter_list|()
block|{
name|String
name|tokencontent
init|=
literal|"testmeagain"
decl_stmt|;
name|NGramProfile
name|p
init|=
operator|new
name|NGramProfile
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|p
operator|.
name|analyze
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|tokencontent
argument_list|)
argument_list|)
expr_stmt|;
comment|//test that profile size is ok, eg 9 different NGramEntries "tesmagin"
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|p
operator|.
name|getSorted
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test addNGrams method with StringBuffer argument    *    */
specifier|public
name|void
name|testAddNGramsStringBuffer
parameter_list|()
block|{
name|String
name|tokencontent
init|=
literal|"testmeagain"
decl_stmt|;
name|NGramProfile
name|p
init|=
operator|new
name|NGramProfile
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|p
operator|.
name|add
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|tokencontent
argument_list|)
argument_list|)
expr_stmt|;
comment|//test that profile size is ok, eg 8 different NGramEntries "tesmagin"
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|p
operator|.
name|getSorted
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * test getSorted method    */
specifier|public
name|void
name|testGetSorted
parameter_list|()
block|{
name|int
index|[]
name|count
init|=
block|{
literal|4
block|,
literal|3
block|,
literal|1
block|}
decl_stmt|;
name|String
index|[]
name|ngram
init|=
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
decl_stmt|;
name|String
name|teststring
init|=
literal|"AAaaBbbC"
decl_stmt|;
name|NGramProfile
name|p
init|=
operator|new
name|NGramProfile
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|p
operator|.
name|analyze
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|teststring
argument_list|)
argument_list|)
expr_stmt|;
comment|//test size of profile
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|p
operator|.
name|getSorted
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|testCounts
argument_list|(
name|p
operator|.
name|getSorted
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|testContents
argument_list|(
name|p
operator|.
name|getSorted
argument_list|()
argument_list|,
name|ngram
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetSimilarity
parameter_list|()
block|{
name|NGramProfile
name|a
init|=
operator|new
name|NGramProfile
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|NGramProfile
name|b
init|=
operator|new
name|NGramProfile
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|a
operator|.
name|analyze
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|tokencontent1
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|analyze
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|tokencontent2
argument_list|)
argument_list|)
expr_stmt|;
comment|//because of rounding errors might slightly return different results
name|assertEquals
argument_list|(
name|a
operator|.
name|getSimilarity
argument_list|(
name|b
argument_list|)
argument_list|,
name|b
operator|.
name|getSimilarity
argument_list|(
name|a
argument_list|)
argument_list|,
literal|0.0000002
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExactMatch
parameter_list|()
block|{
name|NGramProfile
name|a
init|=
operator|new
name|NGramProfile
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|a
operator|.
name|analyze
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|tokencontent1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|getSimilarity
argument_list|(
name|a
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIO
parameter_list|()
block|{
comment|//Create profile and set some contents
name|NGramProfile
name|a
init|=
operator|new
name|NGramProfile
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|a
operator|.
name|analyze
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|this
operator|.
name|tokencontent1
argument_list|)
argument_list|)
expr_stmt|;
name|NGramProfile
name|b
init|=
operator|new
name|NGramProfile
argument_list|(
literal|"a_from_inputstream"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|//save profile
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|a
operator|.
name|save
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
comment|//load profile
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|b
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
comment|//check it
name|testCounts
argument_list|(
name|b
operator|.
name|getSorted
argument_list|()
argument_list|,
name|counts1
argument_list|)
expr_stmt|;
name|testContents
argument_list|(
name|b
operator|.
name|getSorted
argument_list|()
argument_list|,
name|chars1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testContents
parameter_list|(
name|List
argument_list|<
name|NGramEntry
argument_list|>
name|entries
parameter_list|,
name|String
name|contents
index|[]
parameter_list|)
block|{
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NGramEntry
name|nge
range|:
name|entries
control|)
block|{
name|assertEquals
argument_list|(
name|contents
index|[
name|c
index|]
argument_list|,
name|nge
operator|.
name|getSeq
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|++
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testCounts
parameter_list|(
name|List
argument_list|<
name|NGramEntry
argument_list|>
name|entries
parameter_list|,
name|int
name|counts
index|[]
parameter_list|)
block|{
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NGramEntry
name|nge
range|:
name|entries
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|nge
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|counts
index|[
name|c
index|]
argument_list|,
name|nge
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testIdentify
parameter_list|()
block|{
try|try
block|{
name|long
name|total
init|=
literal|0
decl_stmt|;
name|LanguageIdentifier
name|idfr
init|=
operator|new
name|LanguageIdentifier
argument_list|()
decl_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"test-referencial.txt"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|tokens
init|=
name|line
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tokens
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// Identify the whole file
name|String
name|lang
init|=
name|idfr
operator|.
name|identify
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|total
operator|+=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
expr_stmt|;
name|assertEquals
argument_list|(
name|tokens
index|[
literal|1
index|]
argument_list|,
name|lang
argument_list|)
expr_stmt|;
comment|// Then, each line of the file...
name|BufferedReader
name|testFile
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|testLine
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|testLine
operator|=
name|testFile
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|testLine
operator|=
name|testLine
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|testLine
operator|.
name|length
argument_list|()
operator|>
literal|256
condition|)
block|{
name|lang
operator|=
name|idfr
operator|.
name|identify
argument_list|(
name|testLine
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tokens
index|[
literal|1
index|]
argument_list|,
name|lang
argument_list|)
expr_stmt|;
block|}
block|}
name|testFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Total Time="
operator|+
name|total
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

