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
name|microsoft
operator|.
name|ooxml
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
name|POIXMLDocument
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
name|POIXMLTextExtractor
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
name|xslf
operator|.
name|XSLFSlideShow
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
name|xslf
operator|.
name|extractor
operator|.
name|XSLFPowerPointExtractor
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
name|xssf
operator|.
name|extractor
operator|.
name|XSSFExcelExtractor
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
name|xssf
operator|.
name|usermodel
operator|.
name|XSSFWorkbook
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
name|xwpf
operator|.
name|extractor
operator|.
name|XWPFWordExtractor
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
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFDocument
import|;
end_import

begin_comment
comment|/**  * Figures out the correct {@link OOXMLExtractor} for the supplied document and  * returns it.  */
end_comment

begin_class
specifier|public
class|class
name|OOXMLExtractorFactory
block|{
specifier|public
specifier|static
name|OOXMLExtractor
name|createExtractor
parameter_list|(
name|POIXMLTextExtractor
name|extractor
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|POIXMLDocument
name|document
init|=
name|extractor
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|document
operator|instanceof
name|XSLFSlideShow
condition|)
block|{
return|return
operator|new
name|XSLFPowerPointExtractorDecorator
argument_list|(
operator|(
name|XSLFPowerPointExtractor
operator|)
name|extractor
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|document
operator|instanceof
name|XSSFWorkbook
condition|)
block|{
return|return
operator|new
name|XSSFExcelExtractorDecorator
argument_list|(
operator|(
name|XSSFExcelExtractor
operator|)
name|extractor
argument_list|,
name|locale
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|document
operator|instanceof
name|XWPFDocument
condition|)
block|{
return|return
operator|new
name|XWPFWordExtractorDecorator
argument_list|(
operator|(
name|XWPFWordExtractor
operator|)
name|extractor
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|POIXMLTextExtractorDecorator
argument_list|(
name|extractor
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

