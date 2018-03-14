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
name|io
operator|.
name|IOException
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
name|exception
operator|.
name|TikaException
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
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|multiple
operator|.
name|AbstractMultipleParser
operator|.
name|MetadataPolicy
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Runs the input stream through all available parsers,  *  merging the metadata from them based on the  *  {@link MetadataPolicy} chosen.  *    * Warning - currently only one Parser should output  *  any Content to the {@link ContentHandler}, the rest  *  should only output {@link Metadata}. A solution to  *  multiple-content is still being worked on...  *  * @since Apache Tika 1.18  */
end_comment

begin_class
specifier|public
class|class
name|SupplementingParser
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
literal|313179254565350994L
decl_stmt|;
comment|/**      * The different Metadata Policies we support (not discard)      */
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
name|FIRST_WINS
argument_list|,
name|MetadataPolicy
operator|.
name|LAST_WINS
argument_list|,
name|MetadataPolicy
operator|.
name|KEEP_ALL
argument_list|)
decl_stmt|;
specifier|public
name|SupplementingParser
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
name|this
argument_list|(
name|registry
argument_list|,
name|policy
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|parsers
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SupplementingParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|MetadataPolicy
name|policy
parameter_list|,
name|List
argument_list|<
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
comment|// Ensure it's a supported policy
if|if
condition|(
operator|!
name|allowedPolicies
operator|.
name|contains
argument_list|(
name|policy
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported policy for SupplementingParser: "
operator|+
name|policy
argument_list|)
throw|;
block|}
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
comment|// If there was no exception, just carry on to the next
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
return|return
literal|true
return|;
comment|// Have the next parser tried
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

