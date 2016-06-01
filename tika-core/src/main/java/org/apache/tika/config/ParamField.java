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
name|config
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
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

begin_comment
comment|/**  * This class stores metdata for {@link Field} annotation are used to map them  * to {@link Param} at runtime  *  * @since Apache Tika 1.14  */
end_comment

begin_class
specifier|public
class|class
name|ParamField
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT
init|=
literal|"#default"
decl_stmt|;
comment|//NOTE: since (primitive type) is NOT AssignableFrom (BoxedType),
comment|// we just use boxed type for everything!
comment|// Example : short.class.isAssignableFrom(Short.class) ? false
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|PRIMITIVE_MAP
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|short
operator|.
name|class
argument_list|,
name|Short
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|long
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|float
operator|.
name|class
argument_list|,
name|Float
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|double
operator|.
name|class
argument_list|,
name|Double
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
specifier|private
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|field
decl_stmt|;
specifier|private
name|Method
name|setter
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|type
decl_stmt|;
specifier|private
name|boolean
name|required
decl_stmt|;
comment|/**      * Creates a ParamField object      * @param member a field or method which has {@link Field} annotation      */
specifier|public
name|ParamField
parameter_list|(
name|AccessibleObject
name|member
parameter_list|)
block|{
if|if
condition|(
name|member
operator|instanceof
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
condition|)
block|{
name|field
operator|=
operator|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
operator|)
name|member
expr_stmt|;
block|}
else|else
block|{
name|setter
operator|=
operator|(
name|Method
operator|)
name|member
expr_stmt|;
block|}
name|Field
name|annotation
init|=
name|member
operator|.
name|getAnnotation
argument_list|(
name|Field
operator|.
name|class
argument_list|)
decl_stmt|;
name|required
operator|=
name|annotation
operator|.
name|required
argument_list|()
expr_stmt|;
if|if
condition|(
name|annotation
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|DEFAULT
argument_list|)
condition|)
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|field
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|funcName
init|=
name|setter
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|funcName
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
condition|)
block|{
name|name
operator|=
name|funcName
operator|.
name|replaceFirst
argument_list|(
literal|"^set"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|name
operator|=
name|retrieveParamName
argument_list|(
name|annotation
argument_list|)
expr_stmt|;
name|type
operator|=
name|retrieveType
argument_list|()
expr_stmt|;
block|}
specifier|public
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
specifier|public
name|Method
name|getSetter
parameter_list|()
block|{
return|return
name|setter
return|;
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
name|Class
argument_list|<
name|?
argument_list|>
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|boolean
name|isRequired
parameter_list|()
block|{
return|return
name|required
return|;
block|}
comment|/**      * Sets given value to the annotated field of bean      * @param bean bean with annotation for field      * @param value value of field      * @throws IllegalAccessException when it occurs      * @throws InvocationTargetException when it occurs      */
specifier|public
name|void
name|assignValue
parameter_list|(
name|Object
name|bean
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InvocationTargetException
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|field
operator|.
name|set
argument_list|(
name|bean
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setter
operator|.
name|invoke
argument_list|(
name|bean
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Class
name|retrieveType
parameter_list|()
block|{
name|Class
name|type
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|field
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Class
index|[]
name|params
init|=
name|setter
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
comment|//todo:Tika config exception
name|String
name|msg
init|=
literal|"Invalid setter method. Must have one and only one parameter. "
decl_stmt|;
if|if
condition|(
name|setter
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
name|msg
operator|+=
literal|"Perhaps the annotation is misplaced on "
operator|+
name|setter
operator|.
name|getName
argument_list|()
operator|+
literal|" while a set'X' is expected?"
expr_stmt|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|type
operator|=
name|params
index|[
literal|0
index|]
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|isPrimitive
argument_list|()
operator|&&
name|PRIMITIVE_MAP
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|type
operator|=
name|PRIMITIVE_MAP
operator|.
name|get
argument_list|(
name|type
argument_list|)
expr_stmt|;
comment|//primitive types have hard time
block|}
return|return
name|type
return|;
block|}
specifier|private
name|String
name|retrieveParamName
parameter_list|(
name|Field
name|annotation
parameter_list|)
block|{
name|String
name|name
decl_stmt|;
if|if
condition|(
name|annotation
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|DEFAULT
argument_list|)
condition|)
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|field
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|setterName
init|=
name|setter
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|setterName
operator|.
name|startsWith
argument_list|(
literal|"set"
argument_list|)
operator|&&
name|setterName
operator|.
name|length
argument_list|()
operator|>
literal|3
condition|)
block|{
name|name
operator|=
name|setterName
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
name|setterName
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|setter
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|name
operator|=
name|annotation
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ParamField{"
operator|+
literal|"name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|", type="
operator|+
name|type
operator|+
literal|", required="
operator|+
name|required
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

