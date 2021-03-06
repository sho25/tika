begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
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
name|net
operator|.
name|URL
import|;
end_import

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

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

begin_comment
comment|/**  * A wrapper around a {@link ClassLoader} that logs all  *  the Resources loaded through it.  * Used to check that a specific ClassLoader was used  *  when unit testing  */
end_comment

begin_class
specifier|public
class|class
name|ResourceLoggingClassLoader
extends|extends
name|ClassLoader
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|URL
argument_list|>
argument_list|>
name|loadedResources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|URL
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|ResourceLoggingClassLoader
parameter_list|(
name|ClassLoader
name|realClassloader
parameter_list|)
block|{
name|super
argument_list|(
name|realClassloader
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|URL
argument_list|>
name|fetchRecord
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|URL
argument_list|>
name|alreadyLoaded
init|=
name|loadedResources
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|alreadyLoaded
operator|==
literal|null
condition|)
block|{
name|alreadyLoaded
operator|=
operator|new
name|ArrayList
argument_list|<
name|URL
argument_list|>
argument_list|()
expr_stmt|;
name|loadedResources
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|alreadyLoaded
argument_list|)
expr_stmt|;
block|}
return|return
name|alreadyLoaded
return|;
block|}
annotation|@
name|Override
specifier|public
name|URL
name|getResource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|URL
name|resource
init|=
name|super
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|URL
argument_list|>
name|alreadyLoaded
init|=
name|fetchRecord
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|alreadyLoaded
operator|.
name|add
argument_list|(
name|resource
argument_list|)
expr_stmt|;
return|return
name|resource
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|getResources
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|resources
init|=
name|super
operator|.
name|getResources
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|URL
argument_list|>
name|alreadyLoaded
init|=
name|fetchRecord
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// Need to copy as we record
name|List
argument_list|<
name|URL
argument_list|>
name|these
init|=
name|Collections
operator|.
name|list
argument_list|(
name|resources
argument_list|)
decl_stmt|;
name|alreadyLoaded
operator|.
name|addAll
argument_list|(
name|these
argument_list|)
expr_stmt|;
comment|// Return our copy
return|return
name|Collections
operator|.
name|enumeration
argument_list|(
name|these
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|URL
argument_list|>
name|getLoadedResources
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
name|List
argument_list|<
name|URL
argument_list|>
name|resources
init|=
name|loadedResources
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|resources
operator|==
literal|null
condition|)
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
argument_list|)
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|URL
argument_list|>
argument_list|>
name|getLoadedResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|loadedResources
argument_list|)
return|;
block|}
specifier|public
name|void
name|resetLoadedResources
parameter_list|()
block|{
name|loadedResources
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

