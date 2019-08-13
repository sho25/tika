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
name|textstats
package|;
end_package

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
name|tika
operator|.
name|eval
operator|.
name|tokens
operator|.
name|TokenCounts
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
name|TokenIntPair
import|;
end_import

begin_class
specifier|public
class|class
name|TopNTokens
implements|implements
name|TokenCountStatsCalculator
argument_list|<
name|TokenIntPair
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|topN
decl_stmt|;
specifier|public
name|TopNTokens
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
annotation|@
name|Override
specifier|public
name|TokenIntPair
index|[]
name|calculate
parameter_list|(
name|TokenCounts
name|tokenCounts
parameter_list|)
block|{
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
name|tokenCounts
operator|.
name|getTokens
argument_list|()
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
return|return
name|queue
operator|.
name|getArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

