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
name|tokens
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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|mutable
operator|.
name|MutableInt
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|TokenCounterTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|FIELD
init|=
literal|"f"
decl_stmt|;
specifier|private
specifier|static
name|AnalyzerManager
name|analyzerManager
decl_stmt|;
specifier|private
specifier|final
name|int
name|topN
init|=
literal|10
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|analyzerManager
operator|=
name|AnalyzerManager
operator|.
name|newInstance
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
name|String
name|s
init|=
literal|" bde cde def abc efg f f f f ghijklmnop a a a a a a a a a a a a a a a a a b b b b b b b b b b b b b"
decl_stmt|;
name|TokenCounter
name|counter
init|=
operator|new
name|TokenCounter
argument_list|(
name|analyzerManager
operator|.
name|getGeneralAnalyzer
argument_list|()
argument_list|,
name|analyzerManager
operator|.
name|getAlphaIdeoAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|counter
operator|.
name|add
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|TokenStatistics
name|simpleTokenStatistics
init|=
name|counter
operator|.
name|getTokenStatistics
argument_list|(
name|FIELD
argument_list|)
decl_stmt|;
name|LuceneTokenCounter
name|tokenCounter
init|=
operator|new
name|LuceneTokenCounter
argument_list|(
name|analyzerManager
operator|.
name|getGeneralAnalyzer
argument_list|()
argument_list|,
name|analyzerManager
operator|.
name|getAlphaIdeoAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|tokenCounter
operator|.
name|add
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|simpleTokenStatistics
argument_list|,
name|tokenCounter
operator|.
name|getTokenStatistics
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|simple
init|=
literal|0
decl_stmt|;
name|long
name|lucene
init|=
literal|0
decl_stmt|;
name|int
name|numberOfTests
init|=
literal|100
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfTests
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|generateString
argument_list|()
decl_stmt|;
name|long
name|start
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|TokenCounter
name|counter
init|=
operator|new
name|TokenCounter
argument_list|(
name|analyzerManager
operator|.
name|getGeneralAnalyzer
argument_list|()
argument_list|,
name|analyzerManager
operator|.
name|getAlphaIdeoAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|counter
operator|.
name|add
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|simple
operator|+=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
expr_stmt|;
name|TokenStatistics
name|simpleTokenStatistics
init|=
name|counter
operator|.
name|getTokenStatistics
argument_list|(
name|FIELD
argument_list|)
decl_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|LuceneTokenCounter
name|tokenCounter
init|=
operator|new
name|LuceneTokenCounter
argument_list|(
name|analyzerManager
operator|.
name|getGeneralAnalyzer
argument_list|()
argument_list|,
name|analyzerManager
operator|.
name|getAlphaIdeoAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|tokenCounter
operator|.
name|add
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|lucene
operator|+=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|simpleTokenStatistics
argument_list|,
name|tokenCounter
operator|.
name|getTokenStatistics
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("SIMPLE: " + simple + " lucene: "+lucene);
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCommonTokens
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenCounter
name|tokenCounter
init|=
operator|new
name|TokenCounter
argument_list|(
name|analyzerManager
operator|.
name|getGeneralAnalyzer
argument_list|()
argument_list|,
name|analyzerManager
operator|.
name|getAlphaIdeoAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|s
init|=
literal|"the http://www.cnn.com and blahdeblah@apache.org are in valuable www.sites.org 普林斯顿大学"
decl_stmt|;
name|tokenCounter
operator|.
name|add
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|tokens
init|=
name|tokenCounter
operator|.
name|getAlphaTokens
argument_list|(
name|FIELD
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|MutableInt
argument_list|(
literal|2
argument_list|)
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|"___url___"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|MutableInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|"___email___"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCJKFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|s
init|=
literal|"then quickbrownfoxjumpedoverthelazy dogss dog 普林斯顿大学"
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|analyzerManager
operator|.
name|getCommonTokensAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|tokens
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|t
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Integer
name|count
init|=
name|tokens
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|count
operator|=
operator|(
name|count
operator|==
literal|null
operator|)
condition|?
name|count
operator|=
literal|0
else|:
name|count
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|,
name|tokens
operator|.
name|get
argument_list|(
literal|"林斯"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|generateString
parameter_list|()
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|int
name|uniqueVocabTerms
init|=
literal|10000
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|uniqueVocabTerms
argument_list|)
operator|+
literal|100000
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
