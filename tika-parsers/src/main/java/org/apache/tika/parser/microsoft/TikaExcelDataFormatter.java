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
name|DateUtil
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
name|util
operator|.
name|LocaleUtil
import|;
end_import

begin_comment
comment|/**  * Overrides Excel's General format to include more  * significant digits than the MS Spec allows.  * See TIKA-2025.  */
end_comment

begin_class
specifier|public
class|class
name|TikaExcelDataFormatter
extends|extends
name|DataFormatter
block|{
specifier|private
name|String
name|dateOverrideFormatString
decl_stmt|;
specifier|public
name|TikaExcelDataFormatter
parameter_list|()
block|{
name|this
argument_list|(
name|LocaleUtil
operator|.
name|getUserLocale
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TikaExcelDataFormatter
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
name|super
argument_list|(
name|locale
argument_list|)
expr_stmt|;
name|addFormat
argument_list|(
literal|"General"
argument_list|,
operator|new
name|TikaExcelGeneralFormat
argument_list|(
name|locale
argument_list|)
argument_list|)
expr_stmt|;
name|addFormat
argument_list|(
literal|"general"
argument_list|,
operator|new
name|TikaExcelGeneralFormat
argument_list|(
name|locale
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|formatRawCellContents
parameter_list|(
name|double
name|value
parameter_list|,
name|int
name|formatIndex
parameter_list|,
name|String
name|formatString
parameter_list|,
name|boolean
name|use1904Windowing
parameter_list|)
block|{
if|if
condition|(
name|DateUtil
operator|.
name|isADateFormat
argument_list|(
name|formatIndex
argument_list|,
name|formatString
argument_list|)
condition|)
block|{
name|String
name|activeDateFormatString
init|=
operator|(
name|dateOverrideFormatString
operator|==
literal|null
operator|)
condition|?
name|formatString
else|:
name|dateOverrideFormatString
decl_stmt|;
return|return
name|super
operator|.
name|formatRawCellContents
argument_list|(
name|value
argument_list|,
name|formatIndex
argument_list|,
name|activeDateFormatString
argument_list|,
name|use1904Windowing
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|formatRawCellContents
argument_list|(
name|value
argument_list|,
name|formatIndex
argument_list|,
name|formatString
argument_list|,
name|use1904Windowing
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|setDateFormatOverride
parameter_list|(
name|String
name|dateOverrideFormat
parameter_list|)
block|{
if|if
condition|(
name|dateOverrideFormat
operator|!=
literal|null
operator|&&
name|dateOverrideFormat
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|dateOverrideFormatString
operator|=
name|dateOverrideFormat
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

