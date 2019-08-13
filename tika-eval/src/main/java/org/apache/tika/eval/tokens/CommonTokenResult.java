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

begin_class
specifier|public
class|class
name|CommonTokenResult
block|{
specifier|private
specifier|final
name|String
name|langCode
decl_stmt|;
specifier|private
specifier|final
name|int
name|uniqueCommonTokens
decl_stmt|;
comment|//types
specifier|private
specifier|final
name|int
name|commonTokens
decl_stmt|;
specifier|private
specifier|final
name|int
name|uniqueAlphabeticTokens
decl_stmt|;
specifier|private
specifier|final
name|int
name|alphabeticTokens
decl_stmt|;
specifier|public
name|CommonTokenResult
parameter_list|(
name|String
name|langCode
parameter_list|,
name|int
name|uniqueCommonTokens
parameter_list|,
name|int
name|commonTokens
parameter_list|,
name|int
name|uniqueAlphabeticTokens
parameter_list|,
name|int
name|alphabeticTokens
parameter_list|)
block|{
name|this
operator|.
name|langCode
operator|=
name|langCode
expr_stmt|;
name|this
operator|.
name|uniqueCommonTokens
operator|=
name|uniqueCommonTokens
expr_stmt|;
name|this
operator|.
name|commonTokens
operator|=
name|commonTokens
expr_stmt|;
name|this
operator|.
name|uniqueAlphabeticTokens
operator|=
name|uniqueAlphabeticTokens
expr_stmt|;
name|this
operator|.
name|alphabeticTokens
operator|=
name|alphabeticTokens
expr_stmt|;
block|}
specifier|public
name|String
name|getLangCode
parameter_list|()
block|{
return|return
name|langCode
return|;
block|}
comment|/**      *      * @return total number of "common tokens"      */
specifier|public
name|int
name|getCommonTokens
parameter_list|()
block|{
return|return
name|commonTokens
return|;
block|}
comment|/**      *      * @return number of unique "common tokens" (types)      */
specifier|public
name|int
name|getUniqueCommonTokens
parameter_list|()
block|{
return|return
name|uniqueCommonTokens
return|;
block|}
comment|/**      *      * @return number of unique alphabetic tokens (types)      */
specifier|public
name|int
name|getUniqueAlphabeticTokens
parameter_list|()
block|{
return|return
name|uniqueAlphabeticTokens
return|;
block|}
comment|/**      *      * @return number of tokens that had at least one alphabetic/ideographic character      * whether or not a common token      */
specifier|public
name|int
name|getAlphabeticTokens
parameter_list|()
block|{
return|return
name|alphabeticTokens
return|;
block|}
specifier|public
name|double
name|getOOV
parameter_list|()
block|{
return|return
literal|1.0
operator|-
operator|(
name|double
operator|)
name|commonTokens
operator|/
operator|(
name|double
operator|)
name|alphabeticTokens
return|;
block|}
block|}
end_class

end_unit

