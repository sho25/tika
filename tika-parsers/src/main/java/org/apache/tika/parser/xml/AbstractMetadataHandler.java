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
name|xml
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Base class for SAX handlers that map SAX events into document metadata.  *  * @since Apache Tika 0.10  */
end_comment

begin_class
class|class
name|AbstractMetadataHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|private
specifier|final
name|Property
name|property
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|protected
name|AbstractMetadataHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|property
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|protected
name|AbstractMetadataHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|property
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|property
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
comment|/**      * Adds the given metadata value. The value is ignored if it is      *<code>null</code> or empty. If the metadata entry already exists,      * then the given value is appended to it with a comma as the separator.      *      * @param value metadata value      */
specifier|protected
name|void
name|addMetadata
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|metadata
operator|.
name|isMultiValued
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// Add the value, assuming it's not already there
name|List
argument_list|<
name|String
argument_list|>
name|previous
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|previous
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// Set the value, assuming it's not already there
name|String
name|previous
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
name|previous
operator|!=
literal|null
operator|&&
name|previous
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|previous
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|property
operator|.
name|isMultiValuePermitted
argument_list|()
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Replace the existing value if isMultiValuePermitted is false
name|metadata
operator|.
name|set
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|metadata
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
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
name|metadata
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

