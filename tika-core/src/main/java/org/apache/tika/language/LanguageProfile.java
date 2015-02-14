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
name|ArrayList
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
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Language profile based on ngram counts.  *  * @since Apache Tika 0.5  */
end_comment

begin_class
specifier|public
class|class
name|LanguageProfile
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NGRAM_LENGTH
init|=
literal|3
decl_stmt|;
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
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
comment|/**      * Sorted ngram cache for faster distance calculation.      */
specifier|private
name|Interleaved
name|interleaved
init|=
operator|new
name|Interleaved
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|useInterleaved
init|=
literal|true
decl_stmt|;
comment|// For testing purposes
comment|/**      * The sum of all ngram counts in this profile.      * Used to calculate relative ngram frequency.      */
specifier|private
name|long
name|count
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
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
name|LanguageProfile
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
specifier|public
name|LanguageProfile
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_NGRAM_LENGTH
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LanguageProfile
parameter_list|(
name|String
name|content
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|ProfilingWriter
name|writer
init|=
operator|new
name|ProfilingWriter
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|char
index|[]
name|ch
init|=
name|content
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LanguageProfile
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|this
argument_list|(
name|content
argument_list|,
name|DEFAULT_NGRAM_LENGTH
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|length
operator|!=
name|ngram
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to add an ngram of incorrect length: "
operator|+
name|ngram
operator|.
name|length
argument_list|()
operator|+
literal|" != "
operator|+
name|length
argument_list|)
throw|;
block|}
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
return|return
name|useInterleaved
condition|?
name|distanceInterleaved
argument_list|(
name|that
argument_list|)
else|:
name|distanceStandard
argument_list|(
name|that
argument_list|)
return|;
block|}
specifier|private
name|double
name|distanceStandard
parameter_list|(
name|LanguageProfile
name|that
parameter_list|)
block|{
if|if
condition|(
name|length
operator|!=
name|that
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to calculage distance of language profiles"
operator|+
literal|" with different ngram lengths: "
operator|+
name|that
operator|.
name|length
operator|+
literal|" != "
operator|+
name|length
argument_list|)
throw|;
block|}
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
comment|/* Code for interleaved distance calculation below */
specifier|private
name|double
name|distanceInterleaved
parameter_list|(
name|LanguageProfile
name|that
parameter_list|)
block|{
if|if
condition|(
name|length
operator|!=
name|that
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to calculage distance of language profiles"
operator|+
literal|" with different ngram lengths: "
operator|+
name|that
operator|.
name|length
operator|+
literal|" != "
operator|+
name|length
argument_list|)
throw|;
block|}
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
name|Interleaved
operator|.
name|Entry
name|thisEntry
init|=
name|updateInterleaved
argument_list|()
operator|.
name|firstEntry
argument_list|()
decl_stmt|;
name|Interleaved
operator|.
name|Entry
name|thatEntry
init|=
name|that
operator|.
name|updateInterleaved
argument_list|()
operator|.
name|firstEntry
argument_list|()
decl_stmt|;
comment|// Iterate the lists in parallel, until both lists has been depleted
while|while
condition|(
name|thisEntry
operator|.
name|hasNgram
argument_list|()
operator|||
name|thatEntry
operator|.
name|hasNgram
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|thisEntry
operator|.
name|hasNgram
argument_list|()
condition|)
block|{
comment|// Depleted this
name|sumOfSquares
operator|+=
name|square
argument_list|(
name|thatEntry
operator|.
name|count
operator|/
name|thatCount
argument_list|)
expr_stmt|;
name|thatEntry
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|thatEntry
operator|.
name|hasNgram
argument_list|()
condition|)
block|{
comment|// Depleted that
name|sumOfSquares
operator|+=
name|square
argument_list|(
name|thisEntry
operator|.
name|count
operator|/
name|thisCount
argument_list|)
expr_stmt|;
name|thisEntry
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
specifier|final
name|int
name|compare
init|=
name|thisEntry
operator|.
name|compareTo
argument_list|(
name|thatEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|==
literal|0
condition|)
block|{
comment|// Term exists both in this and that
name|double
name|difference
init|=
name|thisEntry
operator|.
name|count
operator|/
name|thisCount
operator|-
name|thatEntry
operator|.
name|count
operator|/
name|thatCount
decl_stmt|;
name|sumOfSquares
operator|+=
name|square
argument_list|(
name|difference
argument_list|)
expr_stmt|;
name|thisEntry
operator|.
name|next
argument_list|()
expr_stmt|;
name|thatEntry
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|compare
operator|<
literal|0
condition|)
block|{
comment|// Term exists only in this
name|sumOfSquares
operator|+=
name|square
argument_list|(
name|thisEntry
operator|.
name|count
operator|/
name|thisCount
argument_list|)
expr_stmt|;
name|thisEntry
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Term exists only in that
name|sumOfSquares
operator|+=
name|square
argument_list|(
name|thatEntry
operator|.
name|count
operator|/
name|thatCount
argument_list|)
expr_stmt|;
name|thatEntry
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|double
name|square
parameter_list|(
name|double
name|count
parameter_list|)
block|{
return|return
name|count
operator|*
name|count
return|;
block|}
specifier|private
class|class
name|Interleaved
block|{
specifier|private
name|char
index|[]
name|entries
init|=
literal|null
decl_stmt|;
comment|//<ngram(length chars)><count(2 chars)>*
specifier|private
name|int
name|size
init|=
literal|0
decl_stmt|;
comment|// Number of entries (one entry = length+2 chars)
specifier|private
name|long
name|entriesGeneratedAtCount
init|=
operator|-
literal|1
decl_stmt|;
comment|// Keeps track of when the sequential structure was current
comment|/**          * Ensure that the entries array is in sync with the ngrams.          */
specifier|public
name|void
name|update
parameter_list|()
block|{
if|if
condition|(
name|count
operator|==
name|entriesGeneratedAtCount
condition|)
block|{
comment|// Already up to date
return|return;
block|}
name|size
operator|=
name|ngrams
operator|.
name|size
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numChars
init|=
operator|(
name|length
operator|+
literal|2
operator|)
operator|*
name|size
decl_stmt|;
if|if
condition|(
name|entries
operator|==
literal|null
operator|||
name|entries
operator|.
name|length
operator|<
name|numChars
condition|)
block|{
name|entries
operator|=
operator|new
name|char
index|[
name|numChars
index|]
expr_stmt|;
block|}
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|entry
range|:
name|getSortedNgrams
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|l
init|=
literal|0
init|;
name|l
operator|<
name|length
condition|;
name|l
operator|++
control|)
block|{
name|entries
index|[
name|pos
operator|+
name|l
index|]
operator|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|charAt
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
name|entries
index|[
name|pos
operator|+
name|length
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|count
operator|/
literal|65536
argument_list|)
expr_stmt|;
comment|// Upper 16 bit
name|entries
index|[
name|pos
operator|+
name|length
operator|+
literal|1
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|count
operator|%
literal|65536
argument_list|)
expr_stmt|;
comment|// lower 16 bit
name|pos
operator|+=
name|length
operator|+
literal|2
expr_stmt|;
block|}
name|entriesGeneratedAtCount
operator|=
name|count
expr_stmt|;
block|}
specifier|public
name|Entry
name|firstEntry
parameter_list|()
block|{
name|Entry
name|entry
init|=
operator|new
name|Entry
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|entry
operator|.
name|update
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|entry
return|;
block|}
specifier|private
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|>
name|getSortedNgrams
parameter_list|()
block|{
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|>
argument_list|(
name|ngrams
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|entries
operator|.
name|addAll
argument_list|(
name|ngrams
operator|.
name|entrySet
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|entries
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|o1
parameter_list|,
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getKey
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|entries
return|;
block|}
specifier|private
class|class
name|Entry
implements|implements
name|Comparable
argument_list|<
name|Entry
argument_list|>
block|{
name|char
index|[]
name|ngram
init|=
operator|new
name|char
index|[
name|length
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
specifier|private
name|void
name|update
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|size
condition|)
block|{
comment|// Reached the end
return|return;
block|}
specifier|final
name|int
name|origo
init|=
name|pos
operator|*
operator|(
name|length
operator|+
literal|2
operator|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|entries
argument_list|,
name|origo
argument_list|,
name|ngram
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|count
operator|=
name|entries
index|[
name|origo
operator|+
name|length
index|]
operator|*
literal|65536
operator|+
name|entries
index|[
name|origo
operator|+
name|length
operator|+
literal|1
index|]
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Entry
name|other
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ngram
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ngram
index|[
name|i
index|]
operator|!=
name|other
operator|.
name|ngram
index|[
name|i
index|]
condition|)
block|{
return|return
name|ngram
index|[
name|i
index|]
operator|-
name|other
operator|.
name|ngram
index|[
name|i
index|]
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<
name|size
operator|-
literal|1
return|;
block|}
specifier|public
name|boolean
name|hasNgram
parameter_list|()
block|{
return|return
name|pos
operator|<
name|size
return|;
block|}
specifier|public
name|void
name|next
parameter_list|()
block|{
name|update
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|ngram
argument_list|)
operator|+
literal|"("
operator|+
name|count
operator|+
literal|")"
return|;
block|}
block|}
block|}
specifier|private
name|Interleaved
name|updateInterleaved
parameter_list|()
block|{
name|interleaved
operator|.
name|update
argument_list|()
expr_stmt|;
return|return
name|interleaved
return|;
block|}
block|}
end_class

end_unit

