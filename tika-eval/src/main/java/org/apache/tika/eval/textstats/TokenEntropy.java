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
name|tika
operator|.
name|eval
operator|.
name|tokens
operator|.
name|TokenCounts
import|;
end_import

begin_class
specifier|public
class|class
name|TokenEntropy
implements|implements
name|TokenCountStatsCalculator
argument_list|<
name|Double
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Double
name|calculate
parameter_list|(
name|TokenCounts
name|tokenCounts
parameter_list|)
block|{
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
name|double
name|totalTokens
init|=
operator|(
name|double
operator|)
name|tokenCounts
operator|.
name|getTotalTokens
argument_list|()
decl_stmt|;
for|for
control|(
name|MutableInt
name|i
range|:
name|tokenCounts
operator|.
name|getTokens
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|int
name|termFreq
init|=
name|i
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
block|}
return|return
operator|-
literal|1.0
operator|*
name|ent
return|;
block|}
block|}
end_class

end_unit

