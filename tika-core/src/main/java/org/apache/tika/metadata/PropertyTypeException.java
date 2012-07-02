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
name|metadata
operator|.
name|Property
operator|.
name|ValueType
import|;
end_import

begin_comment
comment|/**  * XMP property definition violation exception. This is thrown when  * you try to set a {@link Property} value with an incorrect type,  * such as storing an Integer when the property is of type Date.  *  * @since Apache Tika 0.8  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|PropertyTypeException
extends|extends
name|IllegalArgumentException
block|{
specifier|public
name|PropertyTypeException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PropertyTypeException
parameter_list|(
name|PropertyType
name|expected
parameter_list|,
name|PropertyType
name|found
parameter_list|)
block|{
name|super
argument_list|(
literal|"Expected a property of type "
operator|+
name|expected
operator|+
literal|", but received "
operator|+
name|found
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PropertyTypeException
parameter_list|(
name|ValueType
name|expected
parameter_list|,
name|ValueType
name|found
parameter_list|)
block|{
name|super
argument_list|(
literal|"Expected a property with a "
operator|+
name|expected
operator|+
literal|" value, but received a "
operator|+
name|found
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PropertyTypeException
parameter_list|(
name|PropertyType
name|unsupportedPropertyType
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|unsupportedPropertyType
operator|!=
name|PropertyType
operator|.
name|COMPOSITE
operator|)
condition|?
operator|(
name|unsupportedPropertyType
operator|+
literal|" is not supported"
operator|)
else|:
operator|(
literal|"Composite Properties must not include other Composite Properties as either Primary or Secondary"
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

