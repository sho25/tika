begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  * IPTC Metadata Descriptions taken from the IPTC Photo Metadata (July 2010)   * standard. These parts Copyright 2010 International Press Telecommunications   * Council.  */
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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|TikaCoreProperties
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
name|xmp
operator|.
name|convert
operator|.
name|ITikaToXMPConverter
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
name|xmp
operator|.
name|convert
operator|.
name|MSOfficeXMLConverter
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
name|xmp
operator|.
name|convert
operator|.
name|TikaToXMP
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|XMPIterator
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
name|XMPMetaFactory
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
name|properties
operator|.
name|XMPProperty
import|;
end_import

begin_comment
comment|/**  * Tests the Tika<code>Metadata</code> to XMP conversion functionatlity  */
end_comment

begin_class
specifier|public
class|class
name|TikaToXMPTest
block|{
specifier|private
name|Metadata
name|tikaMetadata
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OOXML_MIMETYPE
init|=
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GENERIC_MIMETYPE
init|=
literal|"generic/mimetype"
decl_stmt|;
comment|// --- Set up ---
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|tikaMetadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setupOOXMLMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
comment|// simple property
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|LANGUAGE
argument_list|,
literal|"language"
argument_list|)
expr_stmt|;
comment|// language alternative
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
comment|// comma separated array
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|KEYWORDS
argument_list|,
literal|"keyword1,keyword2"
argument_list|)
expr_stmt|;
comment|// OOXML specific simple prop
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIER
argument_list|,
literal|"lastModifiedBy"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkOOXMLMetadata
parameter_list|(
name|XMPMeta
name|xmp
parameter_list|)
throws|throws
name|XMPException
block|{
comment|// check simple property
name|XMPProperty
name|prop
init|=
name|xmp
operator|.
name|getProperty
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"language"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"language"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// check lang alt
name|prop
operator|=
name|xmp
operator|.
name|getLocalizedText
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"title"
argument_list|,
literal|null
argument_list|,
name|XMPConst
operator|.
name|X_DEFAULT
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"title"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// check array
name|prop
operator|=
name|xmp
operator|.
name|getArrayItem
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"subject"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword1"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|prop
operator|=
name|xmp
operator|.
name|getArrayItem
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"subject"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword2"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// check OOXML specific simple property
name|prop
operator|=
name|xmp
operator|.
name|getProperty
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"lastModifiedBy"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lastModifiedBy"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// --- TESTS ---
annotation|@
name|Test
specifier|public
name|void
name|convert_OOXMLMetadataWithMimetype_everythingConverted
parameter_list|()
throws|throws
name|XMPException
throws|,
name|TikaException
block|{
name|setupOOXMLMetadata
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|tikaMetadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|OOXML_MIMETYPE
argument_list|)
expr_stmt|;
name|XMPMeta
name|xmp
init|=
name|TikaToXMP
operator|.
name|convert
argument_list|(
name|tikaMetadata
argument_list|)
decl_stmt|;
name|checkOOXMLMetadata
argument_list|(
name|xmp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|convert_OOXMLMetadataWithExtraMimetype_everythingConverted
parameter_list|()
throws|throws
name|XMPException
throws|,
name|TikaException
block|{
name|setupOOXMLMetadata
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|XMPMeta
name|xmp
init|=
name|TikaToXMP
operator|.
name|convert
argument_list|(
name|tikaMetadata
argument_list|,
name|OOXML_MIMETYPE
argument_list|)
decl_stmt|;
name|checkOOXMLMetadata
argument_list|(
name|xmp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|convert_OOXMLMetadataWithoutMimetype_onlyGeneralMetadataconverted
parameter_list|()
throws|throws
name|XMPException
throws|,
name|TikaException
block|{
name|setupOOXMLMetadata
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|XMPMeta
name|xmp
init|=
name|TikaToXMP
operator|.
name|convert
argument_list|(
name|tikaMetadata
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// general metadata is converted
comment|// check simple property
name|XMPProperty
name|prop
init|=
name|xmp
operator|.
name|getProperty
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"language"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"language"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// check lang alt
name|prop
operator|=
name|xmp
operator|.
name|getLocalizedText
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"title"
argument_list|,
literal|null
argument_list|,
name|XMPConst
operator|.
name|X_DEFAULT
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"title"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// OOXML one is not, the namespace has also not been registiered as the converter has not been used
name|XMPMetaFactory
operator|.
name|getSchemaRegistry
argument_list|()
operator|.
name|registerNamespace
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|PREFIX
argument_list|)
expr_stmt|;
name|prop
operator|=
name|xmp
operator|.
name|getProperty
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|NAMESPACE_URI
argument_list|,
literal|"lastModifiedBy"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|convert_genericMetadataAllQualified_allConverted
parameter_list|()
throws|throws
name|XMPException
throws|,
name|TikaException
block|{
comment|// simple property
name|tikaMetadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|,
name|GENERIC_MIMETYPE
argument_list|)
expr_stmt|;
comment|// language alternative
name|tikaMetadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
comment|// array
name|tikaMetadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|KEYWORDS
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"keyword1"
block|,
literal|"keyword2"
block|}
argument_list|)
expr_stmt|;
name|XMPMeta
name|xmp
init|=
name|TikaToXMP
operator|.
name|convert
argument_list|(
name|tikaMetadata
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// check simple property
name|XMPProperty
name|prop
init|=
name|xmp
operator|.
name|getProperty
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"format"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GENERIC_MIMETYPE
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// check lang alt
name|prop
operator|=
name|xmp
operator|.
name|getLocalizedText
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"title"
argument_list|,
literal|null
argument_list|,
name|XMPConst
operator|.
name|X_DEFAULT
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"title"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// check array
name|prop
operator|=
name|xmp
operator|.
name|getArrayItem
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"subject"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword1"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|prop
operator|=
name|xmp
operator|.
name|getArrayItem
argument_list|(
name|XMPConst
operator|.
name|NS_DC
argument_list|,
literal|"subject"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword2"
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|convert_wrongGenericMetadata_notConverted
parameter_list|()
throws|throws
name|XMPException
throws|,
name|TikaException
block|{
comment|// unknown prefix
name|tikaMetadata
operator|.
name|set
argument_list|(
literal|"unknown:key"
argument_list|,
literal|"unknownPrefixValue"
argument_list|)
expr_stmt|;
comment|// not qualified key
name|tikaMetadata
operator|.
name|set
argument_list|(
literal|"wrongKey"
argument_list|,
literal|"wrongKeyValue"
argument_list|)
expr_stmt|;
name|XMPMeta
name|xmp
init|=
name|TikaToXMP
operator|.
name|convert
argument_list|(
name|tikaMetadata
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// XMP is empty
name|XMPIterator
name|iter
init|=
name|xmp
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|convert_nullInput_throw
parameter_list|()
throws|throws
name|TikaException
block|{
name|TikaToXMP
operator|.
name|convert
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isConverterAvailable_availableMime_true
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|TikaToXMP
operator|.
name|isConverterAvailable
argument_list|(
name|OOXML_MIMETYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isConverterAvailable_noAvailableMime_false
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|TikaToXMP
operator|.
name|isConverterAvailable
argument_list|(
name|GENERIC_MIMETYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isConverterAvailable_nullInput_false
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|TikaToXMP
operator|.
name|isConverterAvailable
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getConverter_ConverterAvailable_class
parameter_list|()
throws|throws
name|TikaException
block|{
name|ITikaToXMPConverter
name|converter
init|=
name|TikaToXMP
operator|.
name|getConverter
argument_list|(
name|OOXML_MIMETYPE
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|converter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|converter
operator|instanceof
name|MSOfficeXMLConverter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getConverter_noConverterAvailable_null
parameter_list|()
throws|throws
name|TikaException
block|{
name|ITikaToXMPConverter
name|converter
init|=
name|TikaToXMP
operator|.
name|getConverter
argument_list|(
name|GENERIC_MIMETYPE
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|converter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|getConverter_nullInput_throw
parameter_list|()
throws|throws
name|TikaException
block|{
name|TikaToXMP
operator|.
name|getConverter
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

