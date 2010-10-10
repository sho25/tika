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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|imageio
operator|.
name|spi
operator|.
name|ServiceRegistry
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
comment|/**  * A composite parser based on all the {@link Parser} implementations  * available through the {@link ServiceRegistry service provider mechanism}.  *  * @since Apache Tika 0.8  */
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
comment|/**      * Returns the context class loader of the current thread. If such      * a class loader is not available, then the loader of this class or      * finally the system class loader is returned.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-441">TIKA-441</a>      * @return context class loader, or<code>null</code> if no loader      *         is available      */
specifier|private
specifier|static
name|ClassLoader
name|getContextClassLoader
parameter_list|()
block|{
name|ClassLoader
name|loader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
name|loader
operator|=
name|DefaultParser
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
name|loader
operator|=
name|ClassLoader
operator|.
name|getSystemClassLoader
argument_list|()
expr_stmt|;
block|}
return|return
name|loader
return|;
block|}
comment|/**      * Returns all the parsers available through the given class loader.      *      * @param loader class loader       * @return available parsers      */
specifier|private
specifier|static
name|List
argument_list|<
name|Parser
argument_list|>
name|loadParsers
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
operator|new
name|ArrayList
argument_list|<
name|Parser
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|!=
literal|null
condition|)
block|{
name|Iterator
argument_list|<
name|Parser
argument_list|>
name|iterator
init|=
name|ServiceRegistry
operator|.
name|lookupProviders
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|loader
argument_list|)
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|parsers
operator|.
name|add
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parsers
return|;
block|}
specifier|public
name|DefaultParser
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|MediaTypeRegistry
argument_list|()
argument_list|,
name|loadParsers
argument_list|(
name|loader
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultParser
parameter_list|()
block|{
name|this
argument_list|(
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

