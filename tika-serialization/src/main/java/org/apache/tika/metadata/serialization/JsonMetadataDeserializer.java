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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|JsonDeserializationContext
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
name|JsonDeserializer
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
name|JsonParseException
import|;
end_import

begin_comment
comment|/**  * Deserializer for Metadata  *  * If overriding this, remember that this is called from a static context.  * Share state only with great caution.  */
end_comment

begin_class
specifier|public
class|class
name|JsonMetadataDeserializer
implements|implements
name|JsonDeserializer
argument_list|<
name|Metadata
argument_list|>
block|{
comment|/**      * Deserializes a json object (equivalent to: Map<String, String[]>)       * into a Metadata object.      *       * @param element to serialize      * @param type (ignored)      * @param context (ignored)      * @return Metadata       * @throws JsonParseException if element is not able to be parsed      */
annotation|@
name|Override
specifier|public
name|Metadata
name|deserialize
parameter_list|(
name|JsonElement
name|element
parameter_list|,
name|Type
name|type
parameter_list|,
name|JsonDeserializationContext
name|context
parameter_list|)
throws|throws
name|JsonParseException
block|{
specifier|final
name|JsonObject
name|obj
init|=
name|element
operator|.
name|getAsJsonObject
argument_list|()
decl_stmt|;
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|JsonElement
argument_list|>
name|entry
range|:
name|obj
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|JsonElement
name|v
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|isJsonPrimitive
argument_list|()
condition|)
block|{
name|m
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|v
operator|.
name|getAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|isJsonArray
argument_list|()
condition|)
block|{
name|JsonArray
name|vArr
init|=
name|v
operator|.
name|getAsJsonArray
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|JsonElement
argument_list|>
name|itr
init|=
name|vArr
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|JsonElement
name|valueItem
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|m
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|valueItem
operator|.
name|getAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|m
return|;
block|}
block|}
end_class

end_unit

