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
name|mime
package|;
end_package

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
name|Map
import|;
end_import

begin_comment
comment|/**  * Registry of known Internet media types.  */
end_comment

begin_class
specifier|public
class|class
name|MediaTypeRegistry
block|{
comment|/**      * Registry of known media types, including type aliases. All the types      * in this map are base types, i.e. they have no parameters. A canonical      * media type is handled as an identity mapping, while an alias is stored      * as a mapping from the alias to the corresponding canonical type.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|MediaType
argument_list|,
name|MediaType
argument_list|>
name|registry
init|=
operator|new
name|HashMap
argument_list|<
name|MediaType
argument_list|,
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Known type inheritance relationships. The mapping is from a media type      * to the closest supertype. All types in this map are canonical and have      * no parameters.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|MediaType
argument_list|,
name|MediaType
argument_list|>
name|inheritance
init|=
operator|new
name|HashMap
argument_list|<
name|MediaType
argument_list|,
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|addType
parameter_list|(
name|MediaType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|hasParameters
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|registry
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
else|else
block|{
name|registry
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addAlias
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|MediaType
name|alias
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|alias
operator|==
literal|null
operator|||
name|type
operator|.
name|hasParameters
argument_list|()
operator|||
name|alias
operator|.
name|hasParameters
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|registry
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
operator|||
name|registry
operator|.
name|containsKey
argument_list|(
name|alias
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
else|else
block|{
name|registry
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|MediaType
name|normalize
parameter_list|(
name|MediaType
name|type
parameter_list|)
block|{
name|MediaType
name|canonical
init|=
name|registry
operator|.
name|get
argument_list|(
name|type
operator|.
name|getBaseType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|canonical
operator|==
literal|null
condition|)
block|{
return|return
name|type
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|hasParameters
argument_list|()
condition|)
block|{
return|return
operator|new
name|MediaType
argument_list|(
name|canonical
argument_list|,
name|type
operator|.
name|getParameters
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|canonical
return|;
block|}
block|}
comment|/**      * Returns the supertype of the given type. If the given type has any      * parameters, then the respective base type is returned. Otherwise      * built-in heuristics like text/... -&gt; text/plain and      * .../...+xml -&gt; application/xml are used in addition to explicit      * type inheritance rules read from the media type database. Finally      * application/octet-stream is returned for all types for which no other      * supertype is known, and the return value for application/octet-stream      * is<code>null</code>.      *      * @param type media type      * @return supertype, or<code>null</code> for application/octet-stream      */
specifier|public
name|MediaType
name|getSuperType
parameter_list|(
name|MediaType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|hasParameters
argument_list|()
condition|)
block|{
return|return
name|type
return|;
block|}
elseif|else
if|if
condition|(
name|inheritance
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|inheritance
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|getSubtype
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"+xml"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|APPLICATION_XML
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|getSubtype
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"+zip"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|APPLICATION_ZIP
return|;
block|}
elseif|else
if|if
condition|(
literal|"text"
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getType
argument_list|()
argument_list|)
operator|&&
operator|!
name|MediaType
operator|.
name|TEXT_PLAIN
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|TEXT_PLAIN
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|MediaType
operator|.
name|OCTET_STREAM
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

