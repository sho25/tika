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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|apache
operator|.
name|tika
operator|.
name|eval
operator|.
name|tokens
operator|.
name|AnalyzerManager
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
name|AnalyzerManagerTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testGeneral
parameter_list|()
throws|throws
name|Exception
block|{
name|AnalyzerManager
name|analyzerManager
init|=
name|AnalyzerManager
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Analyzer
name|general
init|=
name|analyzerManager
operator|.
name|getGeneralAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|general
operator|.
name|tokenStream
argument_list|(
literal|"f"
argument_list|,
literal|"tHe quick aaaa aaa anD dirty dog"
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
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
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
operator|new
name|HashSet
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
name|seen
operator|.
name|add
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
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
name|assertTrue
argument_list|(
name|seen
operator|.
name|contains
argument_list|(
literal|"the"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seen
operator|.
name|contains
argument_list|(
literal|"and"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seen
operator|.
name|contains
argument_list|(
literal|"dog"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCommon
parameter_list|()
throws|throws
name|Exception
block|{
name|AnalyzerManager
name|analyzerManager
init|=
name|AnalyzerManager
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Analyzer
name|common
init|=
name|analyzerManager
operator|.
name|getAlphaIdeoAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|common
operator|.
name|tokenStream
argument_list|(
literal|"f"
argument_list|,
literal|"the 5,000.12 and dirty dog"
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
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
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
operator|new
name|HashSet
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
if|if
condition|(
name|termAtt
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"5"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Shouldn't have found a numeric"
argument_list|)
expr_stmt|;
block|}
name|seen
operator|.
name|add
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
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
name|assertTrue
argument_list|(
name|seen
operator|.
name|contains
argument_list|(
literal|"the"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seen
operator|.
name|contains
argument_list|(
literal|"and"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|seen
operator|.
name|contains
argument_list|(
literal|"dog"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
