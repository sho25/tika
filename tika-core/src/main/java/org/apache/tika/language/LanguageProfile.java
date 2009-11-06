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
name|language
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
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Language profile based on ngram counts.  */
end_comment

begin_class
specifier|public
class|class
name|LanguageProfile
block|{
comment|/**      * The ngrams that make up this profile.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|ngrams
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * The sum of all ngram counts in this profile.      * Used to calculate relative ngram frequency.      */
specifier|private
name|long
name|count
init|=
literal|0
decl_stmt|;
specifier|private
class|class
name|Counter
block|{
specifier|private
name|long
name|count
init|=
literal|0
decl_stmt|;
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|count
argument_list|)
return|;
block|}
block|}
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
specifier|public
name|long
name|getCount
parameter_list|(
name|String
name|ngram
parameter_list|)
block|{
name|Counter
name|counter
init|=
name|ngrams
operator|.
name|get
argument_list|(
name|ngram
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|!=
literal|null
condition|)
block|{
return|return
name|counter
operator|.
name|count
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/**      * Adds a single occurrence of the given ngram to this profile.      *      * @param ngram the ngram      */
specifier|public
name|void
name|add
parameter_list|(
name|String
name|ngram
parameter_list|)
block|{
name|add
argument_list|(
name|ngram
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds multiple occurrences of the given ngram to this profile.      *      * @param ngram the ngram      * @param count number of occurrences to add      */
specifier|public
name|void
name|add
parameter_list|(
name|String
name|ngram
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|Counter
name|counter
init|=
name|ngrams
operator|.
name|get
argument_list|(
name|ngram
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|==
literal|null
condition|)
block|{
name|counter
operator|=
operator|new
name|Counter
argument_list|()
expr_stmt|;
name|ngrams
operator|.
name|put
argument_list|(
name|ngram
argument_list|,
name|counter
argument_list|)
expr_stmt|;
block|}
name|counter
operator|.
name|count
operator|+=
name|count
expr_stmt|;
name|this
operator|.
name|count
operator|+=
name|count
expr_stmt|;
block|}
comment|/**      * Calculates the geometric distance between this and the given      * other language profile.      *      * @param that the other language profile      * @return distance between the profiles      */
specifier|public
name|double
name|distance
parameter_list|(
name|LanguageProfile
name|that
parameter_list|)
block|{
name|double
name|sumOfSquares
init|=
literal|0.0
decl_stmt|;
name|double
name|thisCount
init|=
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|count
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
name|double
name|thatCount
init|=
name|Math
operator|.
name|max
argument_list|(
name|that
operator|.
name|count
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ngrams
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ngrams
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|ngrams
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|ngrams
operator|.
name|addAll
argument_list|(
name|that
operator|.
name|ngrams
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|ngram
range|:
name|ngrams
control|)
block|{
name|double
name|thisFrequency
init|=
name|this
operator|.
name|getCount
argument_list|(
name|ngram
argument_list|)
operator|/
name|thisCount
decl_stmt|;
name|double
name|thatFrequency
init|=
name|that
operator|.
name|getCount
argument_list|(
name|ngram
argument_list|)
operator|/
name|thatCount
decl_stmt|;
name|double
name|difference
init|=
name|thisFrequency
operator|-
name|thatFrequency
decl_stmt|;
name|sumOfSquares
operator|+=
name|difference
operator|*
name|difference
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|sumOfSquares
argument_list|)
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
name|ngrams
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

