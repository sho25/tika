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
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|ServiceLoader
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
name|MediaType
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

begin_comment
comment|/**  * A composite parser based on all the {@link Parser} implementations  * available through the  * {@link javax.imageio.spi.ServiceRegistry service provider mechanism}.  *  * @since Apache Tika 0.8  */
end_comment

begin_class
specifier|public
class|class
name|DefaultParser
extends|extends
name|CompositeParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3612324825403757520L
decl_stmt|;
comment|/**      * Finds all statically loadable parsers and sort the list by name,      * rather than discovery order. CompositeParser takes the last      * parser for any given media type, so put the Tika parsers first      * so that non-Tika (user supplied) parsers can take precedence.      *      * @param loader service loader      * @return ordered list of statically loadable parsers      */
specifier|private
specifier|static
name|List
argument_list|<
name|Parser
argument_list|>
name|getDefaultParsers
parameter_list|(
name|ServiceLoader
name|loader
parameter_list|)
block|{
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
name|loader
operator|.
name|loadStaticServiceProviders
argument_list|(
name|Parser
operator|.
name|class
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|parsers
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Parser
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Parser
name|p1
parameter_list|,
name|Parser
name|p2
parameter_list|)
block|{
name|String
name|n1
init|=
name|p1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|n2
init|=
name|p2
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|boolean
name|t1
init|=
name|n1
operator|.
name|startsWith
argument_list|(
literal|"org.apache.tika."
argument_list|)
decl_stmt|;
name|boolean
name|t2
init|=
name|n2
operator|.
name|startsWith
argument_list|(
literal|"org.apache.tika."
argument_list|)
decl_stmt|;
if|if
condition|(
name|t1
operator|==
name|t2
condition|)
block|{
return|return
name|n1
operator|.
name|compareTo
argument_list|(
name|n2
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|t1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|parsers
return|;
block|}
specifier|private
specifier|transient
specifier|final
name|ServiceLoader
name|loader
decl_stmt|;
specifier|public
name|DefaultParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|ServiceLoader
name|loader
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Parser
argument_list|>
argument_list|>
name|excludeParsers
parameter_list|)
block|{
name|super
argument_list|(
name|registry
argument_list|,
name|getDefaultParsers
argument_list|(
name|loader
argument_list|)
argument_list|,
name|excludeParsers
argument_list|)
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
specifier|public
name|DefaultParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|ServiceLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|registry
argument_list|,
name|loader
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|registry
argument_list|,
operator|new
name|ServiceLoader
argument_list|(
name|loader
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultParser
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|MediaTypeRegistry
operator|.
name|getDefaultRegistry
argument_list|()
argument_list|,
operator|new
name|ServiceLoader
argument_list|(
name|loader
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|)
block|{
name|this
argument_list|(
name|registry
argument_list|,
operator|new
name|ServiceLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultParser
parameter_list|()
block|{
name|this
argument_list|(
name|MediaTypeRegistry
operator|.
name|getDefaultRegistry
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|getParsers
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|map
init|=
name|super
operator|.
name|getParsers
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|loader
operator|!=
literal|null
condition|)
block|{
comment|// Add dynamic parser service (they always override static ones)
name|MediaTypeRegistry
name|registry
init|=
name|getMediaTypeRegistry
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
name|loader
operator|.
name|loadDynamicServiceProviders
argument_list|(
name|Parser
operator|.
name|class
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|parsers
argument_list|)
expr_stmt|;
comment|// best parser last
for|for
control|(
name|Parser
name|parser
range|:
name|parsers
control|)
block|{
for|for
control|(
name|MediaType
name|type
range|:
name|parser
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|registry
operator|.
name|normalize
argument_list|(
name|type
argument_list|)
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|map
return|;
block|}
block|}
end_class

end_unit

