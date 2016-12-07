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
operator|.
name|xwpf
operator|.
name|ml2006
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
name|tika
operator|.
name|metadata
operator|.
name|Metadata
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
name|metadata
operator|.
name|Office
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
name|metadata
operator|.
name|OfficeOpenXMLExtended
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
name|metadata
operator|.
name|Property
import|;
end_import

begin_class
class|class
name|ExtendedPropertiesHandler
extends|extends
name|CorePropertiesHandler
block|{
specifier|final
specifier|static
name|String
name|EP_NS
init|=
literal|"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
decl_stmt|;
specifier|public
name|ExtendedPropertiesHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|super
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|addProperties
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Property
argument_list|>
name|ep
init|=
name|properties
operator|.
name|get
argument_list|(
name|EP_NS
argument_list|)
decl_stmt|;
if|if
condition|(
name|ep
operator|==
literal|null
condition|)
block|{
name|ep
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|ep
operator|.
name|put
argument_list|(
literal|"AppVersion"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|APP_VERSION
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Application"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|APPLICATION
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Comments"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|COMMENTS
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Company"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|COMPANY
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"DocSecurity"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|DOC_SECURITY
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"HiddenSlides"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|HIDDEN_SLIDES
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Manager"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|MANAGER
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Notes"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NOTES
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"PresentationFormat"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|PRESENTATION_FORMAT
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Template"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|TEMPLATE
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"TotalTime"
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|TOTAL_TIME
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Pages"
argument_list|,
name|Office
operator|.
name|PAGE_COUNT
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Words"
argument_list|,
name|Office
operator|.
name|WORD_COUNT
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Characters"
argument_list|,
name|Office
operator|.
name|CHARACTER_COUNT
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"CharactersWithSpaces"
argument_list|,
name|Office
operator|.
name|CHARACTER_COUNT_WITH_SPACES
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Paragraphs"
argument_list|,
name|Office
operator|.
name|PARAGRAPH_COUNT
argument_list|)
expr_stmt|;
name|ep
operator|.
name|put
argument_list|(
literal|"Lines"
argument_list|,
name|Office
operator|.
name|LINE_COUNT
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|EP_NS
argument_list|,
name|ep
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
literal|"application/vnd.openxmlformats-officedocument.extended-properties+xml"
return|;
block|}
block|}
end_class

end_unit
