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
name|Collections
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
name|commons
operator|.
name|math3
operator|.
name|stat
operator|.
name|descriptive
operator|.
name|SummaryStatistics
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
name|math3
operator|.
name|util
operator|.
name|FastMath
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

begin_class
specifier|public
class|class
name|TokenCounter
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ALPHA_IDEOGRAPH_SUFFIX
init|=
literal|"_a"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//Map<field, Map<token, count>>
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStatistics
argument_list|>
name|tokenStatistics
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|TokenStatistics
name|NULL_TOKEN_STAT
init|=
operator|new
name|TokenStatistics
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
operator|new
name|TokenIntPair
index|[
literal|0
index|]
argument_list|,
literal|0.0d
argument_list|,
operator|new
name|SummaryStatistics
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Analyzer
name|generalAnalyzer
decl_stmt|;
specifier|private
specifier|final
name|Analyzer
name|alphaIdeoAnalyzer
decl_stmt|;
specifier|private
name|int
name|topN
init|=
literal|10
decl_stmt|;
specifier|public
name|TokenCounter
parameter_list|(
name|Analyzer
name|generalAnalyzer
parameter_list|,
name|Analyzer
name|alphaIdeoAnalyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|generalAnalyzer
operator|=
name|generalAnalyzer
expr_stmt|;
name|this
operator|.
name|alphaIdeoAnalyzer
operator|=
name|alphaIdeoAnalyzer
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|_add
argument_list|(
name|field
argument_list|,
name|generalAnalyzer
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|_add
argument_list|(
name|field
operator|+
name|ALPHA_IDEOGRAPH_SUFFIX
argument_list|,
name|alphaIdeoAnalyzer
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|_add
parameter_list|(
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|totalTokens
init|=
literal|0
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|content
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
name|MutableInt
argument_list|>
name|tokenMap
init|=
name|map
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenMap
operator|==
literal|null
condition|)
block|{
name|tokenMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|tokenMap
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|token
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|MutableInt
name|cnt
init|=
name|tokenMap
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|cnt
operator|==
literal|null
condition|)
block|{
name|cnt
operator|=
operator|new
name|MutableInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|tokenMap
operator|.
name|put
argument_list|(
name|token
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cnt
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
name|totalTokens
operator|++
expr_stmt|;
block|}
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|int
name|totalUniqueTokens
init|=
name|tokenMap
operator|.
name|size
argument_list|()
decl_stmt|;
name|double
name|ent
init|=
literal|0.0d
decl_stmt|;
name|double
name|p
init|=
literal|0.0d
decl_stmt|;
name|double
name|base
init|=
literal|2.0
decl_stmt|;
name|TokenCountPriorityQueue
name|queue
init|=
operator|new
name|TokenCountPriorityQueue
argument_list|(
name|topN
argument_list|)
decl_stmt|;
name|SummaryStatistics
name|summaryStatistics
init|=
operator|new
name|SummaryStatistics
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|e
range|:
name|tokenMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|token
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|termFreq
init|=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|p
operator|=
operator|(
name|double
operator|)
name|termFreq
operator|/
operator|(
name|double
operator|)
name|totalTokens
expr_stmt|;
name|ent
operator|+=
name|p
operator|*
name|FastMath
operator|.
name|log
argument_list|(
name|base
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|token
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|token
operator|.
name|length
argument_list|()
argument_list|)
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
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|intValue
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|summaryStatistics
operator|.
name|addValue
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queue
operator|.
name|top
argument_list|()
operator|==
literal|null
operator|||
name|queue
operator|.
name|size
argument_list|()
operator|<
name|topN
operator|||
name|termFreq
operator|>=
name|queue
operator|.
name|top
argument_list|()
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|queue
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|TokenIntPair
argument_list|(
name|token
argument_list|,
name|termFreq
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|totalTokens
operator|>
literal|0
condition|)
block|{
name|ent
operator|=
operator|(
operator|-
literal|1.0d
operator|/
operator|(
name|double
operator|)
name|totalTokens
operator|)
operator|*
name|ent
expr_stmt|;
block|}
comment|/*            Collections.sort(allTokens);             List<TokenIntPair> topNList = new ArrayList<>(topN);             for (int i = 0; i< topN&& i< allTokens.size(); i++) {                 topNList.add(allTokens.get(i));             }*/
name|tokenStatistics
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|TokenStatistics
argument_list|(
name|totalUniqueTokens
argument_list|,
name|totalTokens
argument_list|,
name|queue
operator|.
name|getArray
argument_list|()
argument_list|,
name|ent
argument_list|,
name|summaryStatistics
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TokenStatistics
name|getTokenStatistics
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|TokenStatistics
name|tokenStat
init|=
name|tokenStatistics
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenStat
operator|==
literal|null
condition|)
block|{
return|return
name|NULL_TOKEN_STAT
return|;
block|}
return|return
name|tokenStat
return|;
block|}
specifier|public
name|void
name|setTopN
parameter_list|(
name|int
name|topN
parameter_list|)
block|{
name|this
operator|.
name|topN
operator|=
name|topN
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|tokenMap
init|=
name|map
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenMap
operator|!=
literal|null
condition|)
block|{
name|tokenMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|alphaMap
init|=
name|map
operator|.
name|get
argument_list|(
name|field
operator|+
name|ALPHA_IDEOGRAPH_SUFFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|alphaMap
operator|!=
literal|null
condition|)
block|{
name|alphaMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|tokenStatistics
operator|.
name|put
argument_list|(
name|field
operator|+
name|ALPHA_IDEOGRAPH_SUFFIX
argument_list|,
name|NULL_TOKEN_STAT
argument_list|)
expr_stmt|;
name|tokenStatistics
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|NULL_TOKEN_STAT
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|getAlphaTokens
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|ret
init|=
name|map
operator|.
name|get
argument_list|(
name|field
operator|+
name|ALPHA_IDEOGRAPH_SUFFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
return|return
name|ret
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|getTokens
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|ret
init|=
name|map
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
return|return
name|ret
return|;
block|}
specifier|public
name|TokenStatistics
name|getAlphaTokenStatistics
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|getTokenStatistics
argument_list|(
name|fieldName
operator|+
name|ALPHA_IDEOGRAPH_SUFFIX
argument_list|)
return|;
block|}
block|}
end_class

end_unit

