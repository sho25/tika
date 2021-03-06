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
name|xmp
operator|.
name|convert
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|exception
operator|.
name|TikaException
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
name|HttpHeaders
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
name|OfficeOpenXMLCore
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
name|TikaCoreProperties
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPConst
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPMeta
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|options
operator|.
name|PropertyOptions
import|;
end_import

begin_comment
comment|/**  * Tika to XMP mapping for the Office Open XML formats Word (.docx), Excel (.xlsx) and PowerPoint  * (.pptx).  */
end_comment

begin_class
specifier|public
class|class
name|MSOfficeXMLConverter
extends|extends
name|AbstractConverter
block|{
specifier|protected
specifier|static
specifier|final
name|Set
argument_list|<
name|Namespace
argument_list|>
name|ADDITIONAL_NAMESPACES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Namespace
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Namespace
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|PREFIX
argument_list|)
argument_list|,
operator|new
name|Namespace
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|PREFIX
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|MSOfficeXMLConverter
parameter_list|()
throws|throws
name|TikaException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|XMPMeta
name|process
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|XMPException
block|{
name|super
operator|.
name|setMetadata
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_TYPE
argument_list|,
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"format"
argument_list|)
expr_stmt|;
comment|// Core Properties
name|createProperty
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|CATEGORY
argument_list|,
name|XMPConst
operator|.
name|NS_IPTCCORE
argument_list|,
literal|"intellectualGenre"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|CONTENT_STATUS
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"contentStatus"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|XMPConst
operator|.
name|NS_XMP
argument_list|,
literal|"CreateDate"
argument_list|)
expr_stmt|;
name|createCommaSeparatedArray
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|,
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"creator"
argument_list|,
name|PropertyOptions
operator|.
name|ARRAY_ORDERED
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|COMMENTS
argument_list|,
name|XMPConst
operator|.
name|NS_PDFX
argument_list|,
literal|"Comments"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|IDENTIFIER
argument_list|,
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"identifier"
argument_list|)
expr_stmt|;
name|createCommaSeparatedArray
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|,
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"subject"
argument_list|,
name|PropertyOptions
operator|.
name|ARRAY
argument_list|)
expr_stmt|;
name|createLangAltProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|,
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"description"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|LANGUAGE
argument_list|,
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"language"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIER
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"lastModifiedBy"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|PRINT_DATE
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"lastPrinted"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|XMPConst
operator|.
name|NS_XMP
argument_list|,
literal|"ModifyDate"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|REVISION
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"revision"
argument_list|)
expr_stmt|;
name|createLangAltProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|VERSION
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"version"
argument_list|)
expr_stmt|;
comment|// Extended Properties
comment|// Put both App name and version in xmp:CreatorTool
name|String
name|creatorTool
init|=
literal|""
decl_stmt|;
name|String
name|value
init|=
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|APPLICATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|creatorTool
operator|=
name|value
expr_stmt|;
name|value
operator|=
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|APP_VERSION
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|creatorTool
operator|+=
literal|" "
operator|+
name|value
expr_stmt|;
block|}
block|}
if|if
condition|(
name|creatorTool
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|meta
operator|.
name|setProperty
argument_list|(
name|XMPConst
operator|.
name|NS_XMP
argument_list|,
literal|"CreatorTool"
argument_list|,
name|creatorTool
argument_list|)
expr_stmt|;
block|}
name|createProperty
argument_list|(
name|Office
operator|.
name|CHARACTER_COUNT
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Characters"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|Office
operator|.
name|CHARACTER_COUNT_WITH_SPACES
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"CharactersWithSpaces"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|TikaCoreProperties
operator|.
name|PUBLISHER
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Company"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|Office
operator|.
name|LINE_COUNT
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Lines"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|MANAGER
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Manager"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|NOTES
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Notes"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|Office
operator|.
name|PAGE_COUNT
argument_list|,
name|XMPConst
operator|.
name|TYPE_PAGEDFILE
argument_list|,
literal|"NPages"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|Office
operator|.
name|PARAGRAPH_COUNT
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Paragraphs"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|PRESENTATION_FORMAT
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"PresentationFormat"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|Office
operator|.
name|SLIDE_COUNT
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Slides"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|TEMPLATE
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Template"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|TOTAL_TIME
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"TotalTime"
argument_list|)
expr_stmt|;
name|createProperty
argument_list|(
name|Office
operator|.
name|WORD_COUNT
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"Words"
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getXMPMeta
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|Namespace
argument_list|>
name|getAdditionalNamespaces
parameter_list|()
block|{
return|return
name|ADDITIONAL_NAMESPACES
return|;
block|}
block|}
end_class

end_unit

