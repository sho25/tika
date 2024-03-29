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
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Properties
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
name|DublinCore
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
name|Property
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
name|PropertyTypeException
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
name|metadata
operator|.
name|XMPRights
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
name|XMPUtils
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

begin_class
specifier|public
class|class
name|XMPMetadataTest
block|{
specifier|private
name|Metadata
name|tikaMetadata
decl_stmt|;
specifier|private
name|XMPMetadata
name|xmpMeta
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GENERIC_MIMETYPE
init|=
literal|"generic/mimetype"
decl_stmt|;
comment|// --- SETUP ---
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|XMPMetadata
operator|.
name|registerNamespace
argument_list|(
name|DublinCore
operator|.
name|NAMESPACE_URI_DC_TERMS
argument_list|,
name|DublinCore
operator|.
name|PREFIX_DC_TERMS
argument_list|)
expr_stmt|;
name|xmpMeta
operator|=
operator|new
name|XMPMetadata
argument_list|()
expr_stmt|;
name|tikaMetadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|setupMetadata
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupMetadata
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
name|FORMAT
argument_list|,
name|GENERIC_MIMETYPE
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
comment|// array
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
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
comment|// date
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
literal|"2001-01-01T01:01"
argument_list|)
expr_stmt|;
comment|// int simple property
name|metadata
operator|.
name|set
argument_list|(
name|Property
operator|.
name|internalInteger
argument_list|(
literal|"xmp:Integer"
argument_list|)
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
block|}
comment|// --- HELPER ---
specifier|private
name|void
name|checkArrayValues
parameter_list|(
name|String
index|[]
name|values
parameter_list|,
name|String
name|baseValue
parameter_list|)
block|{
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|assertEquals
argument_list|(
name|baseValue
operator|+
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
comment|// --- TESTS ---
annotation|@
name|Test
specifier|public
name|void
name|process_genericConversion_ok
parameter_list|()
throws|throws
name|TikaException
throws|,
name|XMPException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|,
name|GENERIC_MIMETYPE
argument_list|)
expr_stmt|;
name|XMPMeta
name|xmp
init|=
name|xmpMeta
operator|.
name|getXMPData
argument_list|()
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
name|isMultiValued_multiProp_true
parameter_list|()
throws|throws
name|TikaException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|xmpMeta
operator|.
name|isMultiValued
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isMultiValued_simpleProp_false
parameter_list|()
throws|throws
name|TikaException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|xmpMeta
operator|.
name|isMultiValued
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|get_simpleProp_valueReturned
parameter_list|()
throws|throws
name|TikaException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GENERIC_MIMETYPE
argument_list|,
name|xmpMeta
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|get_arrayProp_firstValueReturned
parameter_list|()
throws|throws
name|TikaException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword1"
argument_list|,
name|xmpMeta
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|get_notExistingProp_null
parameter_list|()
throws|throws
name|TikaException
block|{
name|assertNull
argument_list|(
name|xmpMeta
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PropertyTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|get_nullInput_throw
parameter_list|()
block|{
name|String
name|notInitialized
init|=
literal|null
decl_stmt|;
name|xmpMeta
operator|.
name|get
argument_list|(
name|notInitialized
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PropertyTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|get_notQualifiedKey_throw
parameter_list|()
block|{
name|xmpMeta
operator|.
name|get
argument_list|(
literal|"wrongKey"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PropertyTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|get_unknownPrefixKey_throw
parameter_list|()
block|{
name|xmpMeta
operator|.
name|get
argument_list|(
literal|"unknown:key"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getInt_IntegerProperty_valueReturned
parameter_list|()
throws|throws
name|TikaException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|2
argument_list|)
argument_list|,
name|xmpMeta
operator|.
name|getInt
argument_list|(
name|Property
operator|.
name|get
argument_list|(
literal|"xmp:Integer"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getDate_DateProperty_valueReturned
parameter_list|()
throws|throws
name|TikaException
throws|,
name|XMPException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|Date
name|date
init|=
name|XMPUtils
operator|.
name|convertToDate
argument_list|(
literal|"2001-01-01T01:01"
argument_list|)
operator|.
name|getCalendar
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|date
operator|.
name|equals
argument_list|(
name|xmpMeta
operator|.
name|getDate
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getValues_arrayProperty_allElementsReturned
parameter_list|()
throws|throws
name|TikaException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|String
index|[]
name|values
init|=
name|xmpMeta
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|checkArrayValues
argument_list|(
name|values
argument_list|,
literal|"keyword"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetAll
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
operator|.
name|getName
argument_list|()
argument_list|,
literal|"format"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
operator|.
name|getName
argument_list|()
argument_list|,
literal|"keyword"
argument_list|)
expr_stmt|;
name|xmpMeta
operator|.
name|setAll
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"format"
argument_list|,
name|xmpMeta
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|values
init|=
name|xmpMeta
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|set_simpleProp_ok
parameter_list|()
block|{
name|xmpMeta
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
name|assertEquals
argument_list|(
name|GENERIC_MIMETYPE
argument_list|,
name|xmpMeta
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PropertyTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|set_nullInput_throw
parameter_list|()
block|{
name|String
name|notInitialized
init|=
literal|null
decl_stmt|;
name|xmpMeta
operator|.
name|set
argument_list|(
name|notInitialized
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PropertyTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|set_notQualifiedKey_throw
parameter_list|()
block|{
name|xmpMeta
operator|.
name|set
argument_list|(
literal|"wrongKey"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PropertyTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|set_unknownPrefixKey_throw
parameter_list|()
block|{
name|xmpMeta
operator|.
name|set
argument_list|(
literal|"unknown:key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|set_arrayProperty_ok
parameter_list|()
block|{
name|xmpMeta
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
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
name|String
index|[]
name|values
init|=
name|xmpMeta
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|checkArrayValues
argument_list|(
name|values
argument_list|,
literal|"keyword"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|PropertyTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|set_simplePropWithMultipleValues_throw
parameter_list|()
block|{
name|xmpMeta
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|,
literal|"value2"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|remove_existingProperty_propertyRemoved
parameter_list|()
throws|throws
name|TikaException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|xmpMeta
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
argument_list|)
expr_stmt|;
name|xmpMeta
operator|.
name|remove
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|xmpMeta
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|size_numberOfNamespacesReturned
parameter_list|()
throws|throws
name|TikaException
block|{
name|xmpMeta
operator|.
name|process
argument_list|(
name|tikaMetadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|xmpMeta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|xmpMeta
operator|.
name|set
argument_list|(
name|XMPRights
operator|.
name|OWNER
argument_list|,
literal|"owner"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|xmpMeta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

