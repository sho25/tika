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
name|eval
operator|.
name|db
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
import|;
end_import

begin_class
specifier|public
class|class
name|ColInfo
block|{
specifier|private
specifier|final
name|Cols
name|name
decl_stmt|;
specifier|private
specifier|final
name|int
name|type
decl_stmt|;
specifier|private
specifier|final
name|Integer
name|precision
decl_stmt|;
specifier|private
specifier|final
name|String
name|constraints
decl_stmt|;
specifier|public
name|ColInfo
parameter_list|(
name|Cols
name|name
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ColInfo
parameter_list|(
name|Cols
name|name
parameter_list|,
name|int
name|type
parameter_list|,
name|String
name|constraints
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
literal|null
argument_list|,
name|constraints
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ColInfo
parameter_list|(
name|Cols
name|name
parameter_list|,
name|int
name|type
parameter_list|,
name|Integer
name|precision
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|precision
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ColInfo
parameter_list|(
name|Cols
name|name
parameter_list|,
name|int
name|type
parameter_list|,
name|Integer
name|precision
parameter_list|,
name|String
name|constraints
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
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
name|this
operator|.
name|constraints
operator|=
name|constraints
expr_stmt|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|Cols
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      *      * @return constraints string or null      */
specifier|public
name|String
name|getConstraints
parameter_list|()
block|{
return|return
name|constraints
return|;
block|}
comment|/**      * Gets the precision.  This can be null!      * @return precision or null      */
specifier|public
name|Integer
name|getPrecision
parameter_list|()
block|{
return|return
name|precision
return|;
block|}
specifier|public
name|String
name|getSqlDef
parameter_list|()
block|{
if|if
condition|(
name|type
operator|==
name|Types
operator|.
name|VARCHAR
condition|)
block|{
return|return
literal|"VARCHAR("
operator|+
name|precision
operator|+
literal|")"
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Types
operator|.
name|CHAR
condition|)
block|{
return|return
literal|"CHAR("
operator|+
name|precision
operator|+
literal|")"
return|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Types
operator|.
name|FLOAT
case|:
return|return
literal|"FLOAT"
return|;
case|case
name|Types
operator|.
name|DOUBLE
case|:
return|return
literal|"DOUBLE"
return|;
case|case
name|Types
operator|.
name|BLOB
case|:
return|return
literal|"BLOB"
return|;
case|case
name|Types
operator|.
name|INTEGER
case|:
return|return
literal|"INTEGER"
return|;
case|case
name|Types
operator|.
name|BIGINT
case|:
return|return
literal|"BIGINT"
return|;
case|case
name|Types
operator|.
name|BOOLEAN
case|:
return|return
literal|"BOOLEAN"
return|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Don't yet recognize a type for: "
operator|+
name|type
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ColInfo
name|colInfo
init|=
operator|(
name|ColInfo
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|colInfo
operator|.
name|type
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|name
operator|!=
name|colInfo
operator|.
name|name
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|precision
operator|!=
literal|null
condition|?
operator|!
name|precision
operator|.
name|equals
argument_list|(
name|colInfo
operator|.
name|precision
argument_list|)
else|:
name|colInfo
operator|.
name|precision
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
operator|!
operator|(
name|constraints
operator|!=
literal|null
condition|?
operator|!
name|constraints
operator|.
name|equals
argument_list|(
name|colInfo
operator|.
name|constraints
argument_list|)
else|:
name|colInfo
operator|.
name|constraints
operator|!=
literal|null
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|name
operator|!=
literal|null
condition|?
name|name
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|type
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|precision
operator|!=
literal|null
condition|?
name|precision
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|constraints
operator|!=
literal|null
condition|?
name|constraints
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit
