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
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|langid
operator|.
name|Language
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
name|CommonTokenCountManager
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
name|LangModel
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
name|CommonTokensKLDivergence
implements|implements
name|LanguageAwareTokenCountStats
argument_list|<
name|Double
argument_list|>
block|{
specifier|private
specifier|final
name|CommonTokenCountManager
name|commonTokenCountManager
decl_stmt|;
specifier|public
name|CommonTokensKLDivergence
parameter_list|(
name|CommonTokenCountManager
name|mgr
parameter_list|)
block|{
name|this
operator|.
name|commonTokenCountManager
operator|=
name|mgr
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Double
name|calculate
parameter_list|(
name|List
argument_list|<
name|Language
argument_list|>
name|languages
parameter_list|,
name|TokenCounts
name|tokenCounts
parameter_list|)
block|{
name|Pair
argument_list|<
name|String
argument_list|,
name|LangModel
argument_list|>
name|pair
init|=
name|commonTokenCountManager
operator|.
name|getLangTokens
argument_list|(
name|languages
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLanguage
argument_list|()
argument_list|)
decl_stmt|;
name|LangModel
name|model
init|=
name|pair
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|double
name|kl
init|=
literal|0.0
decl_stmt|;
if|if
condition|(
name|tokenCounts
operator|.
name|getTokens
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|1.0
return|;
block|}
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
name|double
name|p
init|=
operator|(
name|double
operator|)
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|intValue
argument_list|()
operator|/
operator|(
name|double
operator|)
name|tokenCounts
operator|.
name|getTotalTokens
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|0.0
condition|)
block|{
comment|//shouldn't happen, but be defensive
continue|continue;
block|}
name|double
name|q
init|=
name|model
operator|.
name|getProbability
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|kl
operator|+=
name|p
operator|*
name|FastMath
operator|.
name|log
argument_list|(
name|q
operator|/
name|p
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
literal|1.0
operator|*
name|kl
return|;
block|}
block|}
end_class

end_unit

