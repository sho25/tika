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
name|multiple
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
name|Collection
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
name|config
operator|.
name|Param
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
name|mime
operator|.
name|MediaTypeRegistry
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
name|parser
operator|.
name|ParseContext
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
name|parser
operator|.
name|Parser
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
name|ContentHandler
import|;
end_import

begin_comment
comment|/**  * Tries multiple parsers in turn, until one succeeds.  *   * Can optionally keep Metadata from failed parsers when  *  trying the next one, depending on the {@link AbstractMultipleParser.MetadataPolicy}  *  chosen.  *  * @since Apache Tika 1.18  */
end_comment

begin_class
specifier|public
class|class
name|FallbackParser
extends|extends
name|AbstractMultipleParser
block|{
comment|/**      * Serial version UID.      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5844409020977206167L
decl_stmt|;
comment|/**      * The different Metadata Policies we support (all)      */
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|MetadataPolicy
argument_list|>
name|allowedPolicies
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|MetadataPolicy
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|FallbackParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Parser
argument_list|>
name|parsers
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Param
argument_list|>
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|registry
argument_list|,
name|parsers
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FallbackParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|MetadataPolicy
name|policy
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Parser
argument_list|>
name|parsers
parameter_list|)
block|{
name|super
argument_list|(
name|registry
argument_list|,
name|policy
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FallbackParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|MetadataPolicy
name|policy
parameter_list|,
name|Parser
modifier|...
name|parsers
parameter_list|)
block|{
name|super
argument_list|(
name|registry
argument_list|,
name|policy
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|parserCompleted
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|Exception
name|exception
parameter_list|)
block|{
comment|// If there was no exception, abort further parsers
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|// Have the next parser tried
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

