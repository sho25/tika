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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|POIXMLProperties
operator|.
name|CoreProperties
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
name|POIXMLProperties
operator|.
name|CustomProperties
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
name|POIXMLProperties
operator|.
name|ExtendedProperties
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
name|openxml4j
operator|.
name|opc
operator|.
name|internal
operator|.
name|PackagePropertiesPart
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
name|openxml4j
operator|.
name|util
operator|.
name|Nullable
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
name|XSSFEventBasedExcelExtractor
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
name|PagedText
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

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|officeDocument
operator|.
name|x2006
operator|.
name|customProperties
operator|.
name|CTProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|officeDocument
operator|.
name|x2006
operator|.
name|extendedProperties
operator|.
name|CTProperties
import|;
end_import

begin_comment
comment|/**  * OOXML metadata extractor.  *   * Currently POI doesn't support metadata extraction for OOXML.  *   * @see OOXMLExtractor#getMetadataExtractor()  */
end_comment

begin_class
specifier|public
class|class
name|MetadataExtractor
block|{
specifier|private
specifier|final
name|POIXMLTextExtractor
name|extractor
decl_stmt|;
specifier|public
name|MetadataExtractor
parameter_list|(
name|POIXMLTextExtractor
name|extractor
parameter_list|)
block|{
name|this
operator|.
name|extractor
operator|=
name|extractor
expr_stmt|;
block|}
specifier|public
name|void
name|extract
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|TikaException
block|{
if|if
condition|(
name|extractor
operator|.
name|getDocument
argument_list|()
operator|!=
literal|null
operator|||
operator|(
name|extractor
operator|instanceof
name|XSSFEventBasedExcelExtractor
operator|&&
name|extractor
operator|.
name|getPackage
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
name|extractMetadata
argument_list|(
name|extractor
operator|.
name|getCoreProperties
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|extractMetadata
argument_list|(
name|extractor
operator|.
name|getExtendedProperties
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|extractMetadata
argument_list|(
name|extractor
operator|.
name|getCustomProperties
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractMetadata
parameter_list|(
name|CoreProperties
name|properties
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|PackagePropertiesPart
name|propsHolder
init|=
name|properties
operator|.
name|getUnderlyingProperties
argument_list|()
decl_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|CATEGORY
argument_list|,
name|propsHolder
operator|.
name|getCategoryProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|CONTENT_STATUS
argument_list|,
name|propsHolder
operator|.
name|getContentStatusProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|DATE
argument_list|,
name|propsHolder
operator|.
name|getCreatedProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|CREATION_DATE
argument_list|,
name|propsHolder
operator|.
name|getCreatedProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|CREATOR
argument_list|,
name|propsHolder
operator|.
name|getCreatorProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|propsHolder
operator|.
name|getCreatorProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|DESCRIPTION
argument_list|,
name|propsHolder
operator|.
name|getDescriptionProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|IDENTIFIER
argument_list|,
name|propsHolder
operator|.
name|getIdentifierProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|KEYWORDS
argument_list|,
name|propsHolder
operator|.
name|getKeywordsProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LANGUAGE
argument_list|,
name|propsHolder
operator|.
name|getLanguageProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LAST_AUTHOR
argument_list|,
name|propsHolder
operator|.
name|getLastModifiedByProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LAST_PRINTED
argument_list|,
name|propsHolder
operator|.
name|getLastPrintedProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LAST_MODIFIED
argument_list|,
name|propsHolder
operator|.
name|getModifiedProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|REVISION_NUMBER
argument_list|,
name|propsHolder
operator|.
name|getRevisionProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|SUBJECT
argument_list|,
name|propsHolder
operator|.
name|getSubjectProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|TITLE
argument_list|,
name|propsHolder
operator|.
name|getTitleProperty
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|VERSION
argument_list|,
name|propsHolder
operator|.
name|getVersionProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractMetadata
parameter_list|(
name|ExtendedProperties
name|properties
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|CTProperties
name|propsHolder
init|=
name|properties
operator|.
name|getUnderlyingProperties
argument_list|()
decl_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|APPLICATION_NAME
argument_list|,
name|propsHolder
operator|.
name|getApplication
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|APPLICATION_VERSION
argument_list|,
name|propsHolder
operator|.
name|getAppVersion
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|CHARACTER_COUNT
argument_list|,
name|propsHolder
operator|.
name|getCharacters
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|CHARACTER_COUNT_WITH_SPACES
argument_list|,
name|propsHolder
operator|.
name|getCharactersWithSpaces
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|PUBLISHER
argument_list|,
name|propsHolder
operator|.
name|getCompany
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LINE_COUNT
argument_list|,
name|propsHolder
operator|.
name|getLines
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|MANAGER
argument_list|,
name|propsHolder
operator|.
name|getManager
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|NOTES
argument_list|,
name|propsHolder
operator|.
name|getNotes
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|PAGE_COUNT
argument_list|,
name|propsHolder
operator|.
name|getPages
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|propsHolder
operator|.
name|getPages
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PagedText
operator|.
name|N_PAGES
argument_list|,
name|propsHolder
operator|.
name|getPages
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propsHolder
operator|.
name|getSlides
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PagedText
operator|.
name|N_PAGES
argument_list|,
name|propsHolder
operator|.
name|getSlides
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|PARAGRAPH_COUNT
argument_list|,
name|propsHolder
operator|.
name|getParagraphs
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|PRESENTATION_FORMAT
argument_list|,
name|propsHolder
operator|.
name|getPresentationFormat
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|SLIDE_COUNT
argument_list|,
name|propsHolder
operator|.
name|getSlides
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|TEMPLATE
argument_list|,
name|propsHolder
operator|.
name|getTemplate
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|TOTAL_TIME
argument_list|,
name|propsHolder
operator|.
name|getTotalTime
argument_list|()
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|WORD_COUNT
argument_list|,
name|propsHolder
operator|.
name|getWords
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractMetadata
parameter_list|(
name|CustomProperties
name|properties
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|officeDocument
operator|.
name|x2006
operator|.
name|customProperties
operator|.
name|CTProperties
name|props
init|=
name|properties
operator|.
name|getUnderlyingProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|CTProperty
name|property
range|:
name|props
operator|.
name|getPropertyList
argument_list|()
control|)
block|{
name|String
name|val
init|=
literal|null
decl_stmt|;
name|Date
name|date
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|property
operator|.
name|isSetLpwstr
argument_list|()
condition|)
block|{
name|val
operator|=
name|property
operator|.
name|getLpwstr
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetLpstr
argument_list|()
condition|)
block|{
name|val
operator|=
name|property
operator|.
name|getLpstr
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetDate
argument_list|()
condition|)
block|{
name|date
operator|=
name|property
operator|.
name|getDate
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetFiletime
argument_list|()
condition|)
block|{
name|date
operator|=
name|property
operator|.
name|getFiletime
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetBool
argument_list|()
condition|)
block|{
name|val
operator|=
name|Boolean
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getBool
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Integers
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetI1
argument_list|()
condition|)
block|{
name|val
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getI1
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetI2
argument_list|()
condition|)
block|{
name|val
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getI2
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetI4
argument_list|()
condition|)
block|{
name|val
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getI4
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetI8
argument_list|()
condition|)
block|{
name|val
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getI8
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetInt
argument_list|()
condition|)
block|{
name|val
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Unsigned Integers
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetUi1
argument_list|()
condition|)
block|{
name|val
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getUi1
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetUi2
argument_list|()
condition|)
block|{
name|val
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getUi2
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetUi4
argument_list|()
condition|)
block|{
name|val
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getUi4
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetUi8
argument_list|()
condition|)
block|{
name|val
operator|=
name|property
operator|.
name|getUi8
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetUint
argument_list|()
condition|)
block|{
name|val
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getUint
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Reals
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetR4
argument_list|()
condition|)
block|{
name|val
operator|=
name|Float
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getR4
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetR8
argument_list|()
condition|)
block|{
name|val
operator|=
name|Double
operator|.
name|toString
argument_list|(
name|property
operator|.
name|getR8
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetDecimal
argument_list|()
condition|)
block|{
name|BigDecimal
name|d
init|=
name|property
operator|.
name|getDecimal
argument_list|()
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
name|val
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|val
operator|=
name|d
operator|.
name|toPlainString
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetArray
argument_list|()
condition|)
block|{
comment|// TODO Fetch the array values and output
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetVector
argument_list|()
condition|)
block|{
comment|// TODO Fetch the vector values and output
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetBlob
argument_list|()
operator|||
name|property
operator|.
name|isSetOblob
argument_list|()
condition|)
block|{
comment|// TODO Decode, if possible
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetStream
argument_list|()
operator|||
name|property
operator|.
name|isSetOstream
argument_list|()
operator|||
name|property
operator|.
name|isSetVstream
argument_list|()
condition|)
block|{
comment|// TODO Decode, if possible
block|}
elseif|else
if|if
condition|(
name|property
operator|.
name|isSetStorage
argument_list|()
operator|||
name|property
operator|.
name|isSetOstorage
argument_list|()
condition|)
block|{
comment|// TODO Decode, if possible
block|}
else|else
block|{
comment|// This type isn't currently supported yet, skip the property
block|}
name|String
name|propName
init|=
literal|"custom:"
operator|+
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|date
operator|!=
literal|null
condition|)
block|{
name|Property
name|tikaProp
init|=
name|Property
operator|.
name|externalDate
argument_list|(
name|propName
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|tikaProp
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|propName
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|addProperty
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|property
parameter_list|,
name|Nullable
argument_list|<
name|Date
argument_list|>
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|property
argument_list|,
name|value
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addProperty
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|Nullable
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addProperty
argument_list|(
name|metadata
argument_list|,
name|name
argument_list|,
name|value
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addProperty
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addProperty
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

