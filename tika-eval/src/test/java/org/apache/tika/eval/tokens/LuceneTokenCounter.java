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
name|index
operator|.
name|LeafReader
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|TermsEnum
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
name|index
operator|.
name|memory
operator|.
name|MemoryIndex
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
name|search
operator|.
name|IndexSearcher
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Experimental class uses Lucene's MemoryIndex to effectively build the  * token info.  */
end_comment

begin_class
specifier|public
class|class
name|LuceneTokenCounter
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ALPHA_IDEOGRAPH_SUFFIX
init|=
literal|"_a"
decl_stmt|;
specifier|private
specifier|final
name|LeafReader
name|leafReader
decl_stmt|;
specifier|private
specifier|final
name|MemoryIndex
name|memoryIndex
decl_stmt|;
specifier|private
specifier|final
name|Analyzer
name|generalAnalyzer
decl_stmt|;
specifier|private
specifier|final
name|Analyzer
name|alphaIdeographAnalyzer
decl_stmt|;
specifier|private
name|int
name|topN
init|=
literal|10
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|TokenStatistics
argument_list|>
name|fieldStats
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|LuceneTokenCounter
parameter_list|(
name|Analyzer
name|generalAnalyzer
parameter_list|,
name|Analyzer
name|alphaIdeographAnalyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|memoryIndex
operator|=
operator|new
name|MemoryIndex
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|memoryIndex
operator|.
name|createSearcher
argument_list|()
decl_stmt|;
name|leafReader
operator|=
operator|(
name|LeafReader
operator|)
name|searcher
operator|.
name|getIndexReader
argument_list|()
expr_stmt|;
name|this
operator|.
name|generalAnalyzer
operator|=
name|generalAnalyzer
expr_stmt|;
name|this
operator|.
name|alphaIdeographAnalyzer
operator|=
name|alphaIdeographAnalyzer
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
name|memoryIndex
operator|.
name|addField
argument_list|(
name|field
argument_list|,
name|content
argument_list|,
name|generalAnalyzer
argument_list|)
expr_stmt|;
comment|//memoryIndex.addField(field+ALPHA_IDEOGRAPH_SUFFIX,
comment|//        content, alphaIdeographAnalyzer);
name|count
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|//count(field+ALPHA_IDEOGRAPH_SUFFIX);
block|}
name|void
name|count
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|tokenCount
init|=
name|leafReader
operator|.
name|getSumTotalTermFreq
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenCount
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can't handle longs"
argument_list|)
throw|;
block|}
name|int
name|tokenCountInt
init|=
operator|(
name|int
operator|)
name|tokenCount
decl_stmt|;
name|int
name|uniqueTokenCount
init|=
literal|0
decl_stmt|;
name|SummaryStatistics
name|summStats
init|=
operator|new
name|SummaryStatistics
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
name|Terms
name|terms
init|=
name|leafReader
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|//if there were no terms
name|fieldStats
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|TokenStatistics
argument_list|(
name|uniqueTokenCount
argument_list|,
name|tokenCountInt
argument_list|,
operator|new
name|TokenIntPair
index|[
literal|0
index|]
argument_list|,
name|ent
argument_list|,
name|summStats
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|bytesRef
init|=
name|termsEnum
operator|.
name|next
argument_list|()
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
while|while
condition|(
name|bytesRef
operator|!=
literal|null
condition|)
block|{
name|long
name|termFreq
init|=
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|termFreq
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Sorry can't handle longs yet"
argument_list|)
throw|;
block|}
name|int
name|tf
init|=
operator|(
name|int
operator|)
name|termFreq
decl_stmt|;
comment|//TODO: figure out how to avoid Stringifying this
comment|//to get codepoint count
name|String
name|t
init|=
name|bytesRef
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|t
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|t
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
name|tf
condition|;
name|i
operator|++
control|)
block|{
name|summStats
operator|.
name|addValue
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
name|p
operator|=
operator|(
name|double
operator|)
name|tf
operator|/
operator|(
name|double
operator|)
name|tokenCount
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
name|tf
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
name|t
argument_list|,
name|tf
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|uniqueTokenCount
operator|++
expr_stmt|;
name|bytesRef
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tokenCountInt
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
name|tokenCountInt
operator|)
operator|*
name|ent
expr_stmt|;
block|}
name|fieldStats
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|TokenStatistics
argument_list|(
name|uniqueTokenCount
argument_list|,
name|tokenCountInt
argument_list|,
name|queue
operator|.
name|getArray
argument_list|()
argument_list|,
name|ent
argument_list|,
name|summStats
argument_list|)
argument_list|)
expr_stmt|;
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
name|TokenStatistics
name|getTokenStatistics
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|fieldStats
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
specifier|public
name|Terms
name|getAlphaTerms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|leafReader
operator|.
name|terms
argument_list|(
name|field
operator|+
name|ALPHA_IDEOGRAPH_SUFFIX
argument_list|)
return|;
block|}
specifier|public
name|Terms
name|getTerms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|leafReader
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|memoryIndex
operator|.
name|reset
argument_list|()
expr_stmt|;
name|fieldStats
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/*     public ContrastStatistics contrast(String fieldA, String fieldB) throws IOException {         long diceDenom = getUniqueTokenCount(fieldA) +                 getUniqueTokenCount(fieldB);          long diceNum = 0;         long overlapNum = 0;          Terms termsA = getTerms(fieldA);         Terms termsB = getTerms(fieldB);          TermsEnum termsEnumA = termsA.iterator();         TermsEnum termsEnumB = termsB.iterator();          BytesRef bytesRefA = termsEnumA.next();         BytesRef bytesRefB = termsEnumB.next();          while (bytesRefA != null) {             int compare = bytesRefA.compareTo(bytesRefB);             while (compare> 0) {                 if (bytesRefB == null) {                     break;                 }                 //handle term in B, but not A                  compare = bytesRefA.compareTo(bytesRefB);                 bytesRefB = termsEnumB.next();             }             if (compare == 0) {                 diceNum += 2;                 overlapNum += 2 * Math.min(termsEnumA.totalTermFreq(), termsEnumB.totalTermFreq());             }              bytesRefA = termsEnumA.next();         }           for (PairCount p : tokens.values()) {             if (p.a> 0&& p.b> 0) {                 diceNum += 2;                 overlapNum += 2 * Math.min(p.a, p.b);             }         }          float dice = (float) diceNum / (float) diceDenom;         float overlap = (float) overlapNum / (float) (theseTokens.getTokenCount() + thoseTokens.getTokenCount());     } */
block|}
end_class

end_unit

