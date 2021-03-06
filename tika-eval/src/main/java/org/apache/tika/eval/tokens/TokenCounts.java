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

begin_class
specifier|public
class|class
name|TokenCounts
block|{
specifier|private
name|int
name|totalTokens
init|=
literal|0
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|tokens
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|increment
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|MutableInt
name|i
init|=
name|tokens
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|null
condition|)
block|{
name|i
operator|=
operator|new
name|MutableInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|put
argument_list|(
name|token
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|i
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
name|totalTokens
operator|++
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|getTokens
parameter_list|()
block|{
return|return
name|tokens
return|;
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
name|tokens
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

