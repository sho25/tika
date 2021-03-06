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
name|parser
operator|.
name|iwork
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * Utility class to allow for conversion from an integer to Roman numerals  * or alpha-numeric symbols in line with Pages auto numbering formats.  */
end_comment

begin_class
class|class
name|AutoPageNumberUtils
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ALPHABET
index|[]
init|=
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|,
literal|"E"
block|,
literal|"F"
block|,
literal|"G"
block|,
literal|"H"
block|,
literal|"I"
block|,
literal|"J"
block|,
literal|"K"
block|,
literal|"L"
block|,
literal|"M"
block|,
literal|"N"
block|,
literal|"O"
block|,
literal|"P"
block|,
literal|"Q"
block|,
literal|"R"
block|,
literal|"S"
block|,
literal|"T"
block|,
literal|"U"
block|,
literal|"V"
block|,
literal|"W"
block|,
literal|"X"
block|,
literal|"Y"
block|,
literal|"Z"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX
init|=
literal|26
decl_stmt|;
specifier|public
specifier|static
name|String
name|asAlphaNumeric
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|StringBuffer
name|sbuff
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|i
operator|%
name|MAX
decl_stmt|;
name|int
name|ratio
init|=
name|i
operator|/
name|MAX
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
name|ratio
operator|--
expr_stmt|;
name|index
operator|=
name|MAX
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|ratio
condition|;
name|j
operator|++
control|)
block|{
name|sbuff
operator|.
name|append
argument_list|(
name|ALPHABET
index|[
name|index
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|sbuff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|asAlphaNumericLower
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|asAlphaNumeric
argument_list|(
name|i
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
comment|/* 	 * Code copied from jena.apache.org. 	 * @see com.hp.hpl.jena.sparql.util.RomanNumeral 	 */
specifier|public
specifier|static
name|String
name|asRomanNumerals
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Roman numerals are 1-3999 ("
operator|+
name|i
operator|+
literal|")"
argument_list|)
throw|;
if|if
condition|(
name|i
operator|>
literal|3999
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Roman numerals are 1-3999 ("
operator|+
name|i
operator|+
literal|")"
argument_list|)
throw|;
name|StringBuffer
name|sbuff
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|i
operator|=
name|i2r
argument_list|(
name|sbuff
argument_list|,
name|i
argument_list|,
literal|"M"
argument_list|,
literal|1000
argument_list|,
literal|"CM"
argument_list|,
literal|900
argument_list|,
literal|"D"
argument_list|,
literal|500
argument_list|,
literal|"CD"
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|i
operator|=
name|i2r
argument_list|(
name|sbuff
argument_list|,
name|i
argument_list|,
literal|"C"
argument_list|,
literal|100
argument_list|,
literal|"XC"
argument_list|,
literal|90
argument_list|,
literal|"L"
argument_list|,
literal|50
argument_list|,
literal|"XL"
argument_list|,
literal|40
argument_list|)
expr_stmt|;
name|i
operator|=
name|i2r
argument_list|(
name|sbuff
argument_list|,
name|i
argument_list|,
literal|"X"
argument_list|,
literal|10
argument_list|,
literal|"IX"
argument_list|,
literal|9
argument_list|,
literal|"V"
argument_list|,
literal|5
argument_list|,
literal|"IV"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
while|while
condition|(
name|i
operator|>=
literal|1
condition|)
block|{
name|sbuff
operator|.
name|append
argument_list|(
literal|"I"
argument_list|)
expr_stmt|;
name|i
operator|-=
literal|1
expr_stmt|;
block|}
return|return
name|sbuff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|asRomanNumeralsLower
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|asRomanNumerals
argument_list|(
name|i
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|i2r
parameter_list|(
name|StringBuffer
name|sbuff
parameter_list|,
name|int
name|i
parameter_list|,
name|String
name|tens
parameter_list|,
name|int
name|iTens
parameter_list|,
name|String
name|nines
parameter_list|,
name|int
name|iNines
parameter_list|,
name|String
name|fives
parameter_list|,
name|int
name|iFives
parameter_list|,
name|String
name|fours
parameter_list|,
name|int
name|iFours
parameter_list|)
block|{
while|while
condition|(
name|i
operator|>=
name|iTens
condition|)
block|{
name|sbuff
operator|.
name|append
argument_list|(
name|tens
argument_list|)
expr_stmt|;
name|i
operator|-=
name|iTens
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>=
name|iNines
condition|)
block|{
name|sbuff
operator|.
name|append
argument_list|(
name|nines
argument_list|)
expr_stmt|;
name|i
operator|-=
name|iNines
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>=
name|iFives
condition|)
block|{
name|sbuff
operator|.
name|append
argument_list|(
name|fives
argument_list|)
expr_stmt|;
name|i
operator|-=
name|iFives
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>=
name|iFours
condition|)
block|{
name|sbuff
operator|.
name|append
argument_list|(
name|fours
argument_list|)
expr_stmt|;
name|i
operator|-=
name|iFours
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
block|}
end_class

end_unit

