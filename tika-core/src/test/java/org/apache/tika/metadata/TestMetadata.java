begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
package|;
end_package

begin_comment
comment|//JDK imports
end_comment

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

begin_comment
comment|//Junit imports
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_comment
comment|/**  * JUnit based tests of class {@link org.apache.tika.metadata.Metadata}.  */
end_comment

begin_class
specifier|public
class|class
name|TestMetadata
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|CONTENTTYPE
init|=
literal|"contenttype"
decl_stmt|;
specifier|public
name|TestMetadata
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|super
argument_list|(
name|testName
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
operator|new
name|TestSuite
argument_list|(
name|TestMetadata
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test for the<code>add(String, String)</code> method. */
specifier|public
name|void
name|testAdd
parameter_list|()
block|{
name|String
index|[]
name|values
init|=
literal|null
decl_stmt|;
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
name|CONTENTTYPE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
name|CONTENTTYPE
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
name|CONTENTTYPE
argument_list|)
expr_stmt|;
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
literal|"value1"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
name|CONTENTTYPE
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
name|CONTENTTYPE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|values
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// NOTE : For now, the same value can be added many times.
comment|// Should it be changed?
name|meta
operator|.
name|add
argument_list|(
name|CONTENTTYPE
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
name|CONTENTTYPE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|values
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|values
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** Test for the<code>set(String, String)</code> method. */
specifier|public
name|void
name|testSet
parameter_list|()
block|{
name|String
index|[]
name|values
init|=
literal|null
decl_stmt|;
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
name|CONTENTTYPE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|meta
operator|.
name|set
argument_list|(
name|CONTENTTYPE
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
name|CONTENTTYPE
argument_list|)
expr_stmt|;
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
literal|"value1"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|meta
operator|.
name|set
argument_list|(
name|CONTENTTYPE
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
name|CONTENTTYPE
argument_list|)
expr_stmt|;
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
literal|"value2"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|meta
operator|.
name|set
argument_list|(
name|CONTENTTYPE
argument_list|,
literal|"new value 1"
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"contenttype"
argument_list|,
literal|"new value 2"
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
name|CONTENTTYPE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new value 1"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new value 2"
argument_list|,
name|values
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** Test for<code>setAll(Properties)</code> method. */
specifier|public
name|void
name|testSetProperties
parameter_list|()
block|{
name|String
index|[]
name|values
init|=
literal|null
decl_stmt|;
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|meta
operator|.
name|setAll
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|meta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"name-one"
argument_list|,
literal|"value1.1"
argument_list|)
expr_stmt|;
name|meta
operator|.
name|setAll
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|meta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
literal|"name-one"
argument_list|)
expr_stmt|;
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
literal|"value1.1"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"name-two"
argument_list|,
literal|"value2.1"
argument_list|)
expr_stmt|;
name|meta
operator|.
name|setAll
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|meta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
literal|"name-one"
argument_list|)
expr_stmt|;
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
literal|"value1.1"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|values
operator|=
name|meta
operator|.
name|getValues
argument_list|(
literal|"name-two"
argument_list|)
expr_stmt|;
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
literal|"value2.1"
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** Test for<code>get(String)</code> method. */
specifier|public
name|void
name|testGet
parameter_list|()
block|{
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|meta
operator|.
name|get
argument_list|(
literal|"a-name"
argument_list|)
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"a-name"
argument_list|,
literal|"value-1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value-1"
argument_list|,
name|meta
operator|.
name|get
argument_list|(
literal|"a-name"
argument_list|)
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"a-name"
argument_list|,
literal|"value-2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value-1"
argument_list|,
name|meta
operator|.
name|get
argument_list|(
literal|"a-name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for<code>isMultiValued()</code> method. */
specifier|public
name|void
name|testIsMultiValued
parameter_list|()
block|{
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|meta
operator|.
name|isMultiValued
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"key"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta
operator|.
name|isMultiValued
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"key"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|meta
operator|.
name|isMultiValued
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for<code>names</code> method. */
specifier|public
name|void
name|testNames
parameter_list|()
block|{
name|String
index|[]
name|names
init|=
literal|null
decl_stmt|;
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|names
operator|=
name|meta
operator|.
name|names
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|names
operator|.
name|length
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"name-one"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|names
operator|=
name|meta
operator|.
name|names
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|names
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"name-one"
argument_list|,
name|names
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"name-two"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|names
operator|=
name|meta
operator|.
name|names
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|names
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Test for<code>remove(String)</code> method. */
specifier|public
name|void
name|testRemove
parameter_list|()
block|{
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|meta
operator|.
name|remove
argument_list|(
literal|"name-one"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|meta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"name-one"
argument_list|,
literal|"value-1.1"
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"name-one"
argument_list|,
literal|"value-1.2"
argument_list|)
expr_stmt|;
name|meta
operator|.
name|add
argument_list|(
literal|"name-two"
argument_list|,
literal|"value-2.2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|meta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|meta
operator|.
name|get
argument_list|(
literal|"name-one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|meta
operator|.
name|get
argument_list|(
literal|"name-two"
argument_list|)
argument_list|)
expr_stmt|;
name|meta
operator|.
name|remove
argument_list|(
literal|"name-one"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|meta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|meta
operator|.
name|get
argument_list|(
literal|"name-one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|meta
operator|.
name|get
argument_list|(
literal|"name-two"
argument_list|)
argument_list|)
expr_stmt|;
name|meta
operator|.
name|remove
argument_list|(
literal|"name-two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|meta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|meta
operator|.
name|get
argument_list|(
literal|"name-one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|meta
operator|.
name|get
argument_list|(
literal|"name-two"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for<code>equals(Object)</code> method. */
specifier|public
name|void
name|testObject
parameter_list|()
block|{
name|Metadata
name|meta1
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|Metadata
name|meta2
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
literal|"String"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
name|meta1
operator|.
name|add
argument_list|(
literal|"name-one"
argument_list|,
literal|"value-1.1"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
name|meta2
operator|.
name|add
argument_list|(
literal|"name-one"
argument_list|,
literal|"value-1.1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
name|meta1
operator|.
name|add
argument_list|(
literal|"name-one"
argument_list|,
literal|"value-1.2"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
name|meta2
operator|.
name|add
argument_list|(
literal|"name-one"
argument_list|,
literal|"value-1.2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
name|meta1
operator|.
name|add
argument_list|(
literal|"name-two"
argument_list|,
literal|"value-2.1"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
name|meta2
operator|.
name|add
argument_list|(
literal|"name-two"
argument_list|,
literal|"value-2.1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
name|meta1
operator|.
name|add
argument_list|(
literal|"name-two"
argument_list|,
literal|"value-2.2"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
name|meta2
operator|.
name|add
argument_list|(
literal|"name-two"
argument_list|,
literal|"value-2.x"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta1
operator|.
name|equals
argument_list|(
name|meta2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests for getting and setting integer      *  based properties      */
specifier|public
name|void
name|testGetSetInt
parameter_list|()
block|{
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|// Isn't initially set, will get null back
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
comment|// Can only set as a single valued int
try|try
block|{
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to set a multi valued property as an int"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PropertyTypeException
name|e
parameter_list|)
block|{}
try|try
block|{
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to set a date property as an int"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PropertyTypeException
name|e
parameter_list|)
block|{}
comment|// Can set it and retrieve it
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"22"
argument_list|,
name|meta
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// If you save a non int value, you get null
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|,
literal|"INVALID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"INVALID"
argument_list|,
name|meta
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
comment|// If you try to retrieve a non simple int value, you get null
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|,
literal|22
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests for getting and setting date      *  based properties      */
specifier|public
name|void
name|testGetSetDate
parameter_list|()
block|{
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|// Isn't initially set, will get null back
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Can only set as a single valued date
try|try
block|{
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|,
operator|new
name|Date
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to set a multi valued property as a date"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PropertyTypeException
name|e
parameter_list|)
block|{}
try|try
block|{
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|,
operator|new
name|Date
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to set an int property as an date"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PropertyTypeException
name|e
parameter_list|)
block|{}
comment|// Can set it and retrieve it
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|,
operator|new
name|Date
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:01Z"
argument_list|,
name|meta
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|meta
operator|.
name|getDate
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// If you save a non date value, you get null
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|,
literal|"INVALID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"INVALID"
argument_list|,
name|meta
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|getDate
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
comment|// If you try to retrieve a non simple date value, you get null
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|,
operator|new
name|Date
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|meta
operator|.
name|getDate
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|meta
operator|.
name|getInt
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Our format doesn't include milliseconds
comment|// This means things get rounded
name|meta
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|,
operator|new
name|Date
argument_list|(
literal|1050
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1970-01-01T00:00:01Z"
argument_list|,
name|meta
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|meta
operator|.
name|getDate
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

