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
name|metadata
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
name|HashMap
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * XMP property definition. Each instance of this class defines a single  * metadata property like "dc:format". In addition to the property name,  * the {@link ValueType value type} and category (internal or external)  * of the property are included in the property definition. The available  * choice values are also stored for open and closed choice value types.  *  * @since Apache Tika 0.7  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Property
implements|implements
name|Comparable
argument_list|<
name|Property
argument_list|>
block|{
specifier|public
specifier|static
enum|enum
name|PropertyType
block|{
comment|/** A single value */
name|SIMPLE
block|,
name|STRUCTURE
block|,
comment|/** An un-ordered array */
name|BAG
block|,
comment|/** An ordered array */
name|SEQ
block|,
comment|/** An ordered array with some sort of criteria */
name|ALT
block|,
comment|/** Multiple child properties */
name|COMPOSITE
block|}
specifier|public
specifier|static
enum|enum
name|ValueType
block|{
name|BOOLEAN
block|,
name|OPEN_CHOICE
block|,
name|CLOSED_CHOICE
block|,
name|DATE
block|,
name|INTEGER
block|,
name|LOCALE
block|,
name|MIME_TYPE
block|,
name|PROPER_NAME
block|,
name|RATIONAL
block|,
name|REAL
block|,
name|TEXT
block|,
name|URI
block|,
name|URL
block|,
name|XPATH
block|,
name|PROPERTY
block|}
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Property
argument_list|>
name|properties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Property
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|internal
decl_stmt|;
specifier|private
specifier|final
name|PropertyType
name|propertyType
decl_stmt|;
specifier|private
specifier|final
name|ValueType
name|valueType
decl_stmt|;
specifier|private
specifier|final
name|Property
name|primaryProperty
decl_stmt|;
specifier|private
specifier|final
name|Property
index|[]
name|secondaryExtractProperties
decl_stmt|;
comment|/**      * The available choices for the open and closed choice value types.      */
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|choices
decl_stmt|;
specifier|private
name|Property
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|internal
parameter_list|,
name|PropertyType
name|propertyType
parameter_list|,
name|ValueType
name|valueType
parameter_list|,
name|String
index|[]
name|choices
parameter_list|,
name|Property
name|primaryProperty
parameter_list|,
name|Property
index|[]
name|secondaryExtractProperties
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|internal
operator|=
name|internal
expr_stmt|;
name|this
operator|.
name|propertyType
operator|=
name|propertyType
expr_stmt|;
name|this
operator|.
name|valueType
operator|=
name|valueType
expr_stmt|;
if|if
condition|(
name|choices
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|choices
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|choices
operator|.
name|clone
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|choices
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|primaryProperty
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|primaryProperty
operator|=
name|primaryProperty
expr_stmt|;
name|this
operator|.
name|secondaryExtractProperties
operator|=
name|secondaryExtractProperties
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|primaryProperty
operator|=
name|this
expr_stmt|;
name|this
operator|.
name|secondaryExtractProperties
operator|=
literal|null
expr_stmt|;
comment|// Only store primary properties for lookup, not composites
synchronized|synchronized
init|(
name|properties
init|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Property
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|internal
parameter_list|,
name|PropertyType
name|propertyType
parameter_list|,
name|ValueType
name|valueType
parameter_list|,
name|String
index|[]
name|choices
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|internal
argument_list|,
name|propertyType
argument_list|,
name|valueType
argument_list|,
name|choices
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Property
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|internal
parameter_list|,
name|ValueType
name|valueType
parameter_list|,
name|String
index|[]
name|choices
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|internal
argument_list|,
name|PropertyType
operator|.
name|SIMPLE
argument_list|,
name|valueType
argument_list|,
name|choices
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Property
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|internal
parameter_list|,
name|ValueType
name|valueType
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|internal
argument_list|,
name|PropertyType
operator|.
name|SIMPLE
argument_list|,
name|valueType
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Property
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|internal
parameter_list|,
name|PropertyType
name|propertyType
parameter_list|,
name|ValueType
name|valueType
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|internal
argument_list|,
name|propertyType
argument_list|,
name|valueType
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|boolean
name|isInternal
parameter_list|()
block|{
return|return
name|internal
return|;
block|}
specifier|public
name|boolean
name|isExternal
parameter_list|()
block|{
return|return
operator|!
name|internal
return|;
block|}
comment|/**      * Is the PropertyType one which accepts multiple values?      */
specifier|public
name|boolean
name|isMultiValuePermitted
parameter_list|()
block|{
if|if
condition|(
name|propertyType
operator|==
name|PropertyType
operator|.
name|BAG
operator|||
name|propertyType
operator|==
name|PropertyType
operator|.
name|SEQ
operator|||
name|propertyType
operator|==
name|PropertyType
operator|.
name|ALT
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|propertyType
operator|==
name|PropertyType
operator|.
name|COMPOSITE
condition|)
block|{
comment|// Base it on the primary property's behaviour
return|return
name|primaryProperty
operator|.
name|isMultiValuePermitted
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Get the type of a property      * @param key name of the property      * @return the type of the property      */
specifier|public
specifier|static
name|PropertyType
name|getPropertyType
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|PropertyType
name|type
init|=
literal|null
decl_stmt|;
name|Property
name|prop
init|=
name|properties
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|prop
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|prop
operator|.
name|getPropertyType
argument_list|()
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
comment|/**      * Retrieve the property object that corresponds to the given key      * @param key the property key or name      * @return the Property object      */
specifier|public
specifier|static
name|Property
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|properties
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|PropertyType
name|getPropertyType
parameter_list|()
block|{
return|return
name|propertyType
return|;
block|}
specifier|public
name|ValueType
name|getValueType
parameter_list|()
block|{
return|return
name|valueType
return|;
block|}
comment|/**      * Returns the (immutable) set of choices for the values of this property.      * Only defined for {@link ValueType#OPEN_CHOICE open} and      * {@link ValueType#CLOSED_CHOICE closed choice} value types.      *      * @return available choices, or<code>null</code>      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getChoices
parameter_list|()
block|{
return|return
name|choices
return|;
block|}
comment|/**      * Gets the primary property for a composite property      *       * @return the primary property      */
specifier|public
name|Property
name|getPrimaryProperty
parameter_list|()
block|{
return|return
name|primaryProperty
return|;
block|}
comment|/**      * Gets the secondary properties for a composite property      *       * @return the secondary properties      */
specifier|public
name|Property
index|[]
name|getSecondaryExtractProperties
parameter_list|()
block|{
return|return
name|secondaryExtractProperties
return|;
block|}
specifier|public
specifier|static
name|SortedSet
argument_list|<
name|Property
argument_list|>
name|getProperties
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|SortedSet
argument_list|<
name|Property
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<
name|Property
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|p
init|=
name|prefix
operator|+
literal|":"
decl_stmt|;
synchronized|synchronized
init|(
name|properties
init|)
block|{
for|for
control|(
name|String
name|name
range|:
name|properties
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|set
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalClosedChoise
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|choices
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|CLOSED_CHOICE
argument_list|,
name|choices
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalDate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|DATE
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalInteger
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|INTEGER
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalIntegerSequence
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|PropertyType
operator|.
name|SEQ
argument_list|,
name|ValueType
operator|.
name|INTEGER
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalRational
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|RATIONAL
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalOpenChoise
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|choices
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|OPEN_CHOICE
argument_list|,
name|choices
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalReal
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|REAL
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalText
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|TEXT
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalTextBag
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|PropertyType
operator|.
name|BAG
argument_list|,
name|ValueType
operator|.
name|TEXT
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|internalURI
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ValueType
operator|.
name|URI
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|externalClosedChoise
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|choices
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|ValueType
operator|.
name|CLOSED_CHOICE
argument_list|,
name|choices
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|externalOpenChoise
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|choices
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|ValueType
operator|.
name|OPEN_CHOICE
argument_list|,
name|choices
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|externalDate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|ValueType
operator|.
name|DATE
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|externalReal
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|ValueType
operator|.
name|REAL
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|externalInteger
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|ValueType
operator|.
name|INTEGER
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|externalBoolean
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|ValueType
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|externalText
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|ValueType
operator|.
name|TEXT
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Property
name|externalTextBag
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|PropertyType
operator|.
name|BAG
argument_list|,
name|ValueType
operator|.
name|TEXT
argument_list|)
return|;
block|}
comment|/**      * Constructs a new composite property from the given primary and array of secondary properties.      *<p>      * Note that name of the composite property is taken from its primary property,       * and primary and secondary properties must not be composite properties themselves.      *       * @param primaryProperty      * @param secondaryExtractProperties      * @return the composite property      */
specifier|public
specifier|static
name|Property
name|composite
parameter_list|(
name|Property
name|primaryProperty
parameter_list|,
name|Property
index|[]
name|secondaryExtractProperties
parameter_list|)
block|{
if|if
condition|(
name|primaryProperty
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"primaryProperty must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|primaryProperty
operator|.
name|getPropertyType
argument_list|()
operator|==
name|PropertyType
operator|.
name|COMPOSITE
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|primaryProperty
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|secondaryExtractProperties
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Property
name|secondaryExtractProperty
range|:
name|secondaryExtractProperties
control|)
block|{
if|if
condition|(
name|secondaryExtractProperty
operator|.
name|getPropertyType
argument_list|()
operator|==
name|PropertyType
operator|.
name|COMPOSITE
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|secondaryExtractProperty
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
name|String
index|[]
name|choices
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|primaryProperty
operator|.
name|getChoices
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|choices
operator|=
name|primaryProperty
operator|.
name|getChoices
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|primaryProperty
operator|.
name|getChoices
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Property
argument_list|(
name|primaryProperty
operator|.
name|getName
argument_list|()
argument_list|,
name|primaryProperty
operator|.
name|isInternal
argument_list|()
argument_list|,
name|PropertyType
operator|.
name|COMPOSITE
argument_list|,
name|ValueType
operator|.
name|PROPERTY
argument_list|,
name|choices
argument_list|,
name|primaryProperty
argument_list|,
name|secondaryExtractProperties
argument_list|)
return|;
block|}
comment|//----------------------------------------------------------< Comparable>
specifier|public
name|int
name|compareTo
parameter_list|(
name|Property
name|o
parameter_list|)
block|{
return|return
name|name
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|name
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------< Object>
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|instanceof
name|Property
operator|&&
name|name
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Property
operator|)
name|o
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

