begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|serialization
package|;
end_package

begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonArray
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonElement
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonNull
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonPrimitive
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonSerializationContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonSerializer
import|;
end_import

begin_comment
comment|/**  * Serializer for Metadata  *   * If overriding this, remember that this is called from a static context.  * Share state only with great caution.  *  */
end_comment

begin_class
specifier|public
class|class
name|JsonMetadataSerializer
implements|implements
name|JsonSerializer
argument_list|<
name|Metadata
argument_list|>
block|{
comment|/**      * Serializes a Metadata object into effectively Map<String, String[]>.      *       * @param metadata object to serialize      * @param type (ignored)      * @param context (ignored)      * @return JsonElement with key/value(s) pairs or JsonNull if metadata is null.      */
annotation|@
name|Override
specifier|public
name|JsonElement
name|serialize
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Type
name|type
parameter_list|,
name|JsonSerializationContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|metadata
operator|==
literal|null
condition|)
block|{
return|return
name|JsonNull
operator|.
name|INSTANCE
return|;
block|}
name|String
index|[]
name|names
init|=
name|getNames
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|==
literal|null
condition|)
block|{
return|return
name|JsonNull
operator|.
name|INSTANCE
return|;
block|}
name|JsonObject
name|root
init|=
operator|new
name|JsonObject
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|names
control|)
block|{
name|String
index|[]
name|vals
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|==
literal|null
condition|)
block|{
comment|//silently skip
continue|continue;
block|}
if|if
condition|(
name|vals
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|root
operator|.
name|addProperty
argument_list|(
name|n
argument_list|,
name|vals
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|JsonArray
name|jArr
init|=
operator|new
name|JsonArray
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
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|jArr
operator|.
name|add
argument_list|(
operator|new
name|JsonPrimitive
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|jArr
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|root
return|;
block|}
comment|/**      * Override to get a custom sort order      * or to filter names.      *       * @param metadata metadata from which to grab names      * @return list of names in the order in which they should be serialized      */
specifier|protected
name|String
index|[]
name|getNames
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|String
index|[]
name|names
init|=
name|metadata
operator|.
name|names
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
return|return
name|names
return|;
block|}
block|}
end_class

end_unit

