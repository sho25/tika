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
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|DateUtils
operator|.
name|formatDate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

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
name|Calendar
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
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Iterator
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|java
operator|.
name|util
operator|.
name|TimeZone
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
operator|.
name|PropertyType
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
name|utils
operator|.
name|DateUtils
import|;
end_import

begin_comment
comment|/**  * A multi-valued metadata container.  */
end_comment

begin_class
specifier|public
class|class
name|Metadata
implements|implements
name|CreativeCommons
implements|,
name|Geographic
implements|,
name|HttpHeaders
implements|,
name|Message
implements|,
name|ClimateForcast
implements|,
name|TIFF
implements|,
name|TikaMimeKeys
implements|,
name|Serializable
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5623926545693153182L
decl_stmt|;
comment|/**      * A map of all metadata attributes.      */
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|metadata
init|=
literal|null
decl_stmt|;
comment|/**      * Some parsers will have the date as a ISO-8601 string      *  already, and will set that into the Metadata object.      */
specifier|private
specifier|static
specifier|final
name|DateUtils
name|DATE_UTILS
init|=
operator|new
name|DateUtils
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|DateFormat
name|createDateFormat
parameter_list|(
name|String
name|format
parameter_list|,
name|TimeZone
name|timezone
parameter_list|)
block|{
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|format
argument_list|,
operator|new
name|DateFormatSymbols
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|timezone
operator|!=
literal|null
condition|)
block|{
name|sdf
operator|.
name|setTimeZone
argument_list|(
name|timezone
argument_list|)
expr_stmt|;
block|}
return|return
name|sdf
return|;
block|}
comment|/**      * Parses the given date string. This method is synchronized to prevent      * concurrent access to the thread-unsafe date formats.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-495">TIKA-495</a>      * @param date date string      * @return parsed date, or<code>null</code> if the date can't be parsed      */
specifier|private
specifier|static
specifier|synchronized
name|Date
name|parseDate
parameter_list|(
name|String
name|date
parameter_list|)
block|{
return|return
name|DATE_UTILS
operator|.
name|tryToParse
argument_list|(
name|date
argument_list|)
return|;
block|}
comment|/**      * Constructs a new, empty metadata.      */
specifier|public
name|Metadata
parameter_list|()
block|{
name|metadata
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns true if named value is multivalued.      *       * @param property      *          metadata property      * @return true is named value is multivalued, false if single value or null      */
specifier|public
name|boolean
name|isMultiValued
parameter_list|(
specifier|final
name|Property
name|property
parameter_list|)
block|{
return|return
name|metadata
operator|.
name|get
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
operator|!=
literal|null
operator|&&
name|metadata
operator|.
name|get
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|length
operator|>
literal|1
return|;
block|}
comment|/**      * Returns true if named value is multivalued.      *       * @param name      *          name of metadata      * @return true is named value is multivalued, false if single value or null      */
specifier|public
name|boolean
name|isMultiValued
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
name|metadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
operator|&&
name|metadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|.
name|length
operator|>
literal|1
return|;
block|}
comment|/**      * Returns an array of the names contained in the metadata.      *       * @return Metadata names      */
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
name|metadata
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|metadata
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Get the value associated to a metadata name. If many values are assiociated      * to the specified name, then the first one is returned.      *       * @param name      *          of the metadata.      * @return the value associated to the specified metadata name.      */
specifier|public
name|String
name|get
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|String
index|[]
name|values
init|=
name|metadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|values
index|[
literal|0
index|]
return|;
block|}
block|}
comment|/**      * Returns the value (if any) of the identified metadata property.      *      * @since Apache Tika 0.7      * @param property property definition      * @return property value, or<code>null</code> if the property is not set      */
specifier|public
name|String
name|get
parameter_list|(
name|Property
name|property
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the value of the identified Integer based metadata property.      *       * @since Apache Tika 0.8      * @param property simple integer property definition      * @return property value as a Integer, or<code>null</code> if the property is not set, or not a valid Integer      */
specifier|public
name|Integer
name|getInt
parameter_list|(
name|Property
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
operator|!=
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|INTEGER
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|v
init|=
name|get
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Returns the value of the identified Date based metadata property.      *       * @since Apache Tika 0.8      * @param property simple date property definition      * @return property value as a Date, or<code>null</code> if the property is not set, or not a valid Date      */
specifier|public
name|Date
name|getDate
parameter_list|(
name|Property
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
operator|!=
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|DATE
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|v
init|=
name|get
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
return|return
name|parseDate
argument_list|(
name|v
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Get the values associated to a metadata name.      *       * @param property      *          of the metadata.      * @return the values associated to a metadata name.      */
specifier|public
name|String
index|[]
name|getValues
parameter_list|(
specifier|final
name|Property
name|property
parameter_list|)
block|{
return|return
name|_getValues
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Get the values associated to a metadata name.      *       * @param name      *          of the metadata.      * @return the values associated to a metadata name.      */
specifier|public
name|String
index|[]
name|getValues
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
name|_getValues
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
name|String
index|[]
name|_getValues
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|String
index|[]
name|values
init|=
name|metadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
operator|new
name|String
index|[
literal|0
index|]
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
specifier|private
name|String
index|[]
name|appendedValues
parameter_list|(
name|String
index|[]
name|values
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
name|String
index|[]
name|newValues
init|=
operator|new
name|String
index|[
name|values
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|newValues
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|newValues
index|[
name|newValues
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|value
expr_stmt|;
return|return
name|newValues
return|;
block|}
comment|/**      * Add a metadata name/value mapping. Add the specified value to the list of      * values associated to the specified metadata name.      *       * @param name      *          the metadata name.      * @param value      *          the metadata value.      */
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
name|String
index|[]
name|values
init|=
name|metadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|appendedValues
argument_list|(
name|values
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Add a metadata property/value mapping. Add the specified value to the list of      * values associated to the specified metadata property.      *       * @param property      *          the metadata property.      * @param value      *          the metadata value.      */
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Property
name|property
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"property must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPropertyType
argument_list|()
operator|==
name|PropertyType
operator|.
name|COMPOSITE
condition|)
block|{
name|add
argument_list|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|getSecondaryExtractProperties
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Property
name|secondaryExtractProperty
range|:
name|property
operator|.
name|getSecondaryExtractProperties
argument_list|()
control|)
block|{
name|add
argument_list|(
name|secondaryExtractProperty
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|String
index|[]
name|values
init|=
name|metadata
operator|.
name|get
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|set
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|property
operator|.
name|isMultiValuePermitted
argument_list|()
condition|)
block|{
name|set
argument_list|(
name|property
argument_list|,
name|appendedValues
argument_list|(
name|values
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|property
operator|.
name|getName
argument_list|()
operator|+
literal|" : "
operator|+
name|property
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**      * Copy All key-value pairs from properties.      *       * @param properties      *          properties to copy from      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|setAll
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|Enumeration
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|(
name|Enumeration
argument_list|<
name|String
argument_list|>
operator|)
name|properties
operator|.
name|propertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|String
index|[]
block|{
name|properties
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Set metadata name/value. Associate the specified value to the specified      * metadata name. If some previous values were associated to this name,      * they are removed. If the given value is<code>null</code>, then the      * metadata entry is removed.      *      * @param name the metadata name.      * @param value  the metadata value, or<code>null</code>      */
specifier|public
name|void
name|set
parameter_list|(
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
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|String
index|[]
block|{
name|value
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets the value of the identified metadata property.      *      * @since Apache Tika 0.7      * @param property property definition      * @param value    property value      */
specifier|public
name|void
name|set
parameter_list|(
name|Property
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"property must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPropertyType
argument_list|()
operator|==
name|PropertyType
operator|.
name|COMPOSITE
condition|)
block|{
name|set
argument_list|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|getSecondaryExtractProperties
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Property
name|secondaryExtractProperty
range|:
name|property
operator|.
name|getSecondaryExtractProperties
argument_list|()
control|)
block|{
name|set
argument_list|(
name|secondaryExtractProperty
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|set
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets the values of the identified metadata property.      *      * @since Apache Tika 1.2      * @param property property definition      * @param values    property values      */
specifier|public
name|void
name|set
parameter_list|(
name|Property
name|property
parameter_list|,
name|String
index|[]
name|values
parameter_list|)
block|{
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"property must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPropertyType
argument_list|()
operator|==
name|PropertyType
operator|.
name|COMPOSITE
condition|)
block|{
name|set
argument_list|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|getSecondaryExtractProperties
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Property
name|secondaryExtractProperty
range|:
name|property
operator|.
name|getSecondaryExtractProperties
argument_list|()
control|)
block|{
name|set
argument_list|(
name|secondaryExtractProperty
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|metadata
operator|.
name|put
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets the integer value of the identified metadata property.      *      * @since Apache Tika 0.8      * @param property simple integer property definition      * @param value    property value      */
specifier|public
name|void
name|set
parameter_list|(
name|Property
name|property
parameter_list|,
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
operator|!=
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|INTEGER
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|ValueType
operator|.
name|INTEGER
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
argument_list|)
throw|;
block|}
name|set
argument_list|(
name|property
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds the integer value of the identified metadata property.      *      * @since Apache Tika 1.21      * @param property seq integer property definition      * @param value    property value      */
specifier|public
name|void
name|add
parameter_list|(
name|Property
name|property
parameter_list|,
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
operator|!=
name|PropertyType
operator|.
name|SEQ
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|PropertyType
operator|.
name|SEQ
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|INTEGER
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|ValueType
operator|.
name|INTEGER
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
argument_list|)
throw|;
block|}
name|add
argument_list|(
name|property
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the array of ints of the identified "seq" integer metadata property.      *      * @since Apache Tika 1.21      * @param property seq integer property definition      * @return array of ints      */
specifier|public
name|int
index|[]
name|getIntValues
parameter_list|(
name|Property
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
operator|!=
name|PropertyType
operator|.
name|SEQ
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|PropertyType
operator|.
name|SEQ
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|INTEGER
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|ValueType
operator|.
name|INTEGER
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
argument_list|)
throw|;
block|}
name|String
index|[]
name|vals
init|=
name|getValues
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|int
index|[]
name|ret
init|=
operator|new
name|int
index|[
name|vals
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ret
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**      * Sets the real or rational value of the identified metadata property.      *      * @since Apache Tika 0.8      * @param property simple real or simple rational property definition      * @param value    property value      */
specifier|public
name|void
name|set
parameter_list|(
name|Property
name|property
parameter_list|,
name|double
name|value
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
operator|!=
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|REAL
operator|&&
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|RATIONAL
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|ValueType
operator|.
name|REAL
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
argument_list|)
throw|;
block|}
name|set
argument_list|(
name|property
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the date value of the identified metadata property.      *      * @since Apache Tika 0.8      * @param property simple integer property definition      * @param date     property value      */
specifier|public
name|void
name|set
parameter_list|(
name|Property
name|property
parameter_list|,
name|Date
name|date
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
operator|!=
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|DATE
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|ValueType
operator|.
name|DATE
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|dateString
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|date
operator|!=
literal|null
condition|)
block|{
name|dateString
operator|=
name|formatDate
argument_list|(
name|date
argument_list|)
expr_stmt|;
block|}
name|set
argument_list|(
name|property
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the date value of the identified metadata property.      *      * @since Apache Tika 0.8      * @param property simple integer property definition      * @param date     property value      */
specifier|public
name|void
name|set
parameter_list|(
name|Property
name|property
parameter_list|,
name|Calendar
name|date
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
operator|!=
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|PropertyType
operator|.
name|SIMPLE
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getPropertyType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
operator|!=
name|Property
operator|.
name|ValueType
operator|.
name|DATE
condition|)
block|{
throw|throw
operator|new
name|PropertyTypeException
argument_list|(
name|Property
operator|.
name|ValueType
operator|.
name|DATE
argument_list|,
name|property
operator|.
name|getPrimaryProperty
argument_list|()
operator|.
name|getValueType
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|dateString
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|date
operator|!=
literal|null
condition|)
block|{
name|dateString
operator|=
name|formatDate
argument_list|(
name|date
argument_list|)
expr_stmt|;
block|}
name|set
argument_list|(
name|property
argument_list|,
name|dateString
argument_list|)
expr_stmt|;
block|}
comment|/**      * Remove a metadata and all its associated values.      *       * @param name      *          metadata name to remove      */
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|metadata
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the number of metadata names in this metadata.      *       * @return number of metadata names      */
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|metadata
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|>
name|i
init|=
name|metadata
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|h
operator|+=
name|getMetadataEntryHashCode
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|h
return|;
block|}
specifier|private
name|int
name|getMetadataEntryHashCode
parameter_list|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|e
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
operator|^
name|Arrays
operator|.
name|hashCode
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Metadata
name|other
init|=
literal|null
decl_stmt|;
try|try
block|{
name|other
operator|=
operator|(
name|Metadata
operator|)
name|o
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|cce
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|.
name|size
argument_list|()
operator|!=
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
index|[]
name|names
init|=
name|names
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|otherValues
init|=
name|other
operator|.
name|_getValues
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|String
index|[]
name|thisValues
init|=
name|_getValues
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherValues
operator|.
name|length
operator|!=
name|thisValues
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|otherValues
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|otherValues
index|[
name|j
index|]
operator|.
name|equals
argument_list|(
name|thisValues
index|[
name|j
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
index|[]
name|names
init|=
name|names
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|values
init|=
name|_getValues
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|values
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|values
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

