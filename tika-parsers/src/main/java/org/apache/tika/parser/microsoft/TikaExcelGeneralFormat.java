begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|microsoft
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|MathContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|RoundingMode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|FieldPosition
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Format
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParsePosition
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|ss
operator|.
name|usermodel
operator|.
name|DataFormatter
import|;
end_import

begin_comment
comment|/**  * A Format that allows up to 15 significant digits for integers.  * This goes against the Microsoft spec, but it preserves information  * for long strings of digits.  *<p>  * This was derived from POI's ExcelGeneralNumberFormat  */
end_comment

begin_class
specifier|public
class|class
name|TikaExcelGeneralFormat
extends|extends
name|Format
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MathContext
name|TO_15_SF
init|=
operator|new
name|MathContext
argument_list|(
literal|15
argument_list|,
name|RoundingMode
operator|.
name|HALF_UP
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DecimalFormatSymbols
name|decimalSymbols
decl_stmt|;
specifier|private
specifier|final
name|DecimalFormat
name|integerFormat
decl_stmt|;
specifier|private
specifier|final
name|DecimalFormat
name|decimalFormat
decl_stmt|;
specifier|private
specifier|final
name|DecimalFormat
name|scientificFormat
decl_stmt|;
specifier|public
name|TikaExcelGeneralFormat
parameter_list|(
specifier|final
name|Locale
name|locale
parameter_list|)
block|{
name|decimalSymbols
operator|=
name|DecimalFormatSymbols
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
name|scientificFormat
operator|=
operator|new
name|DecimalFormat
argument_list|(
literal|"0.##############E0"
argument_list|,
name|decimalSymbols
argument_list|)
expr_stmt|;
name|DataFormatter
operator|.
name|setExcelStyleRoundingMode
argument_list|(
name|scientificFormat
argument_list|)
expr_stmt|;
name|integerFormat
operator|=
operator|new
name|DecimalFormat
argument_list|(
literal|"#"
argument_list|,
name|decimalSymbols
argument_list|)
expr_stmt|;
name|DataFormatter
operator|.
name|setExcelStyleRoundingMode
argument_list|(
name|integerFormat
argument_list|)
expr_stmt|;
name|decimalFormat
operator|=
operator|new
name|DecimalFormat
argument_list|(
literal|"#.##########"
argument_list|,
name|decimalSymbols
argument_list|)
expr_stmt|;
name|DataFormatter
operator|.
name|setExcelStyleRoundingMode
argument_list|(
name|decimalFormat
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StringBuffer
name|format
parameter_list|(
name|Object
name|number
parameter_list|,
name|StringBuffer
name|toAppendTo
parameter_list|,
name|FieldPosition
name|pos
parameter_list|)
block|{
specifier|final
name|double
name|value
decl_stmt|;
if|if
condition|(
name|number
operator|instanceof
name|Number
condition|)
block|{
name|value
operator|=
operator|(
operator|(
name|Number
operator|)
name|number
operator|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
operator|||
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|integerFormat
operator|.
name|format
argument_list|(
name|number
argument_list|,
name|toAppendTo
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// testBug54786 gets here with a date, so retain previous behaviour
return|return
name|integerFormat
operator|.
name|format
argument_list|(
name|number
argument_list|,
name|toAppendTo
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|final
name|double
name|abs
init|=
name|Math
operator|.
name|abs
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|abs
operator|>
literal|1E15
operator|||
operator|(
name|abs
operator|<=
literal|1E
operator|-
literal|15
operator|&&
name|abs
operator|>
literal|0
operator|)
condition|)
block|{
return|return
name|scientificFormat
operator|.
name|format
argument_list|(
name|number
argument_list|,
name|toAppendTo
argument_list|,
name|pos
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Math
operator|.
name|floor
argument_list|(
name|value
argument_list|)
operator|==
name|value
operator|||
name|abs
operator|>
literal|1E15
condition|)
block|{
comment|// integer, or integer portion uses all 15 allowed digits
return|return
name|integerFormat
operator|.
name|format
argument_list|(
name|number
argument_list|,
name|toAppendTo
argument_list|,
name|pos
argument_list|)
return|;
block|}
comment|// Non-integers of non-scientific magnitude are formatted as "up to 11
comment|// numeric characters, with the decimal point counting as a numeric
comment|// character". We know there is a decimal point, so limit to 10 digits.
comment|// https://support.microsoft.com/en-us/kb/65903
specifier|final
name|double
name|rounded
init|=
operator|new
name|BigDecimal
argument_list|(
name|value
argument_list|)
operator|.
name|round
argument_list|(
name|TO_15_SF
argument_list|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
return|return
name|decimalFormat
operator|.
name|format
argument_list|(
name|rounded
argument_list|,
name|toAppendTo
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|Object
name|parseObject
parameter_list|(
name|String
name|source
parameter_list|,
name|ParsePosition
name|pos
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

