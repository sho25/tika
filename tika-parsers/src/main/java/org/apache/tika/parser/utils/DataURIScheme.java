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
name|utils
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
name|mime
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Objects
import|;
end_import

begin_class
specifier|public
class|class
name|DataURIScheme
block|{
specifier|private
specifier|final
name|String
name|rawMediaTypeString
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isBase64
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
name|DataURIScheme
parameter_list|(
name|String
name|mediaTypeString
parameter_list|,
name|boolean
name|isBase64
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|rawMediaTypeString
operator|=
name|mediaTypeString
expr_stmt|;
name|this
operator|.
name|isBase64
operator|=
name|isBase64
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
return|;
block|}
comment|/**      *      * @return parsed media type or<code>null</code> if parse fails or if media type string was      * not specified      */
specifier|public
name|MediaType
name|getMediaType
parameter_list|()
block|{
if|if
condition|(
name|rawMediaTypeString
operator|!=
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
name|rawMediaTypeString
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isBase64
parameter_list|()
block|{
return|return
name|isBase64
return|;
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
operator|!
operator|(
name|o
operator|instanceof
name|DataURIScheme
operator|)
condition|)
return|return
literal|false
return|;
name|DataURIScheme
name|that
init|=
operator|(
name|DataURIScheme
operator|)
name|o
decl_stmt|;
return|return
name|isBase64
argument_list|()
operator|==
name|that
operator|.
name|isBase64
argument_list|()
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|rawMediaTypeString
argument_list|,
name|that
operator|.
name|rawMediaTypeString
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|data
argument_list|,
name|that
operator|.
name|data
argument_list|)
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
name|Objects
operator|.
name|hash
argument_list|(
name|rawMediaTypeString
argument_list|,
name|isBase64
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

