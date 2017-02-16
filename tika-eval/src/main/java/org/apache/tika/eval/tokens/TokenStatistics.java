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
name|util
operator|.
name|Arrays
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

begin_class
specifier|public
class|class
name|TokenStatistics
block|{
specifier|private
specifier|final
name|int
name|totalTokens
decl_stmt|;
specifier|private
specifier|final
name|int
name|totalUniqueTokens
decl_stmt|;
specifier|private
specifier|final
name|TokenIntPair
index|[]
name|topN
decl_stmt|;
specifier|private
specifier|final
name|double
name|entropy
decl_stmt|;
specifier|private
specifier|final
name|SummaryStatistics
name|summaryStatistics
decl_stmt|;
specifier|public
name|TokenStatistics
parameter_list|(
name|int
name|totalUniqueTokens
parameter_list|,
name|int
name|totalTokens
parameter_list|,
name|TokenIntPair
index|[]
name|topN
parameter_list|,
name|double
name|entropy
parameter_list|,
name|SummaryStatistics
name|summaryStatistics
parameter_list|)
block|{
name|this
operator|.
name|totalUniqueTokens
operator|=
name|totalUniqueTokens
expr_stmt|;
name|this
operator|.
name|totalTokens
operator|=
name|totalTokens
expr_stmt|;
name|this
operator|.
name|topN
operator|=
name|topN
expr_stmt|;
name|this
operator|.
name|entropy
operator|=
name|entropy
expr_stmt|;
name|this
operator|.
name|summaryStatistics
operator|=
name|summaryStatistics
expr_stmt|;
block|}
specifier|public
name|int
name|getTotalTokens
parameter_list|()
block|{
return|return
name|totalTokens
return|;
block|}
specifier|public
name|int
name|getTotalUniqueTokens
parameter_list|()
block|{
return|return
name|totalUniqueTokens
return|;
block|}
specifier|public
name|TokenIntPair
index|[]
name|getTopN
parameter_list|()
block|{
return|return
name|topN
return|;
block|}
specifier|public
name|double
name|getEntropy
parameter_list|()
block|{
return|return
name|entropy
return|;
block|}
specifier|public
name|SummaryStatistics
name|getSummaryStatistics
parameter_list|()
block|{
return|return
name|summaryStatistics
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TokenStatistics{"
operator|+
literal|"totalTokens="
operator|+
name|totalTokens
operator|+
literal|", totalUniqueTokens="
operator|+
name|totalUniqueTokens
operator|+
literal|", topN="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|topN
argument_list|)
operator|+
literal|", entropy="
operator|+
name|entropy
operator|+
literal|", summaryStatistics="
operator|+
name|summaryStatistics
operator|+
literal|'}'
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TokenStatistics
name|that
init|=
operator|(
name|TokenStatistics
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|totalTokens
operator|!=
name|that
operator|.
name|totalTokens
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|totalUniqueTokens
operator|!=
name|that
operator|.
name|totalUniqueTokens
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|doubleEquals
argument_list|(
name|that
operator|.
name|entropy
argument_list|,
name|entropy
argument_list|)
condition|)
return|return
literal|false
return|;
comment|// Probably incorrect - comparing Object[] arrays with Arrays.equals
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|topN
argument_list|,
name|that
operator|.
name|topN
argument_list|)
condition|)
return|return
literal|false
return|;
name|SummaryStatistics
name|thatS
init|=
operator|(
operator|(
name|TokenStatistics
operator|)
name|o
operator|)
operator|.
name|summaryStatistics
decl_stmt|;
if|if
condition|(
name|summaryStatistics
operator|.
name|getN
argument_list|()
operator|!=
name|thatS
operator|.
name|getN
argument_list|()
condition|)
return|return
literal|false
return|;
comment|//if both have n==0, don't bother with the stats
if|if
condition|(
name|summaryStatistics
operator|.
name|getN
argument_list|()
operator|==
literal|0L
condition|)
return|return
literal|true
return|;
comment|//TODO: consider adding others...
if|if
condition|(
operator|!
name|doubleEquals
argument_list|(
name|summaryStatistics
operator|.
name|getGeometricMean
argument_list|()
argument_list|,
name|thatS
operator|.
name|getGeometricMean
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|doubleEquals
argument_list|(
name|summaryStatistics
operator|.
name|getMax
argument_list|()
argument_list|,
name|thatS
operator|.
name|getMax
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|doubleEquals
argument_list|(
name|summaryStatistics
operator|.
name|getMean
argument_list|()
argument_list|,
name|thatS
operator|.
name|getMean
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|doubleEquals
argument_list|(
name|summaryStatistics
operator|.
name|getMin
argument_list|()
argument_list|,
name|thatS
operator|.
name|getMin
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|doubleEquals
argument_list|(
name|summaryStatistics
operator|.
name|getSum
argument_list|()
argument_list|,
name|thatS
operator|.
name|getSum
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|doubleEquals
argument_list|(
name|summaryStatistics
operator|.
name|getStandardDeviation
argument_list|()
argument_list|,
name|thatS
operator|.
name|getStandardDeviation
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|result
operator|=
call|(
name|int
call|)
argument_list|(
name|totalTokens
operator|^
operator|(
name|totalTokens
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|totalUniqueTokens
operator|^
operator|(
name|totalUniqueTokens
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|topN
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|entropy
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|summaryStatistics
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|boolean
name|doubleEquals
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|)
block|{
return|return
name|doubleEquals
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|0.000000000001d
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|doubleEquals
parameter_list|(
name|double
name|a
parameter_list|,
name|double
name|b
parameter_list|,
name|double
name|epsilon
parameter_list|)
block|{
return|return
name|a
operator|==
name|b
condition|?
literal|true
else|:
name|Math
operator|.
name|abs
argument_list|(
name|a
operator|-
name|b
argument_list|)
operator|<
name|epsilon
return|;
block|}
block|}
end_class

end_unit
