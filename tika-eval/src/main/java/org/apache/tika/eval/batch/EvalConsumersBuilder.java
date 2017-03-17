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
name|eval
operator|.
name|batch
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|batch
operator|.
name|ConsumersManager
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
name|batch
operator|.
name|FileResource
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
name|batch
operator|.
name|FileResourceConsumer
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
name|batch
operator|.
name|builders
operator|.
name|AbstractConsumersBuilder
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
name|batch
operator|.
name|builders
operator|.
name|BatchProcessBuilder
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
name|eval
operator|.
name|AbstractProfiler
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
name|eval
operator|.
name|db
operator|.
name|H2Util
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
name|eval
operator|.
name|db
operator|.
name|JDBCUtil
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
name|eval
operator|.
name|db
operator|.
name|MimeBuffer
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
name|eval
operator|.
name|util
operator|.
name|LanguageIDWrapper
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
name|util
operator|.
name|ClassLoaderUtil
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
name|util
operator|.
name|PropsUtil
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
name|util
operator|.
name|XMLDOMUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_class
specifier|public
class|class
name|EvalConsumersBuilder
extends|extends
name|AbstractConsumersBuilder
block|{
annotation|@
name|Override
specifier|public
name|ConsumersManager
name|build
parameter_list|(
name|Node
name|node
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|runtimeAttributes
parameter_list|,
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|queue
parameter_list|)
block|{
name|List
argument_list|<
name|FileResourceConsumer
argument_list|>
name|consumers
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numConsumers
init|=
name|BatchProcessBuilder
operator|.
name|getNumConsumers
argument_list|(
name|runtimeAttributes
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|localAttrs
init|=
name|XMLDOMUtil
operator|.
name|mapifyAttrs
argument_list|(
name|node
argument_list|,
name|runtimeAttributes
argument_list|)
decl_stmt|;
name|Path
name|db
init|=
name|getPath
argument_list|(
name|localAttrs
argument_list|,
literal|"db"
argument_list|)
decl_stmt|;
name|String
name|jdbcConnectionString
init|=
name|localAttrs
operator|.
name|get
argument_list|(
literal|"jdbc"
argument_list|)
decl_stmt|;
name|Path
name|langModelDir
init|=
name|getPath
argument_list|(
name|localAttrs
argument_list|,
literal|"langModelDir"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|langModelDir
operator|==
literal|null
condition|)
block|{
name|LanguageIDWrapper
operator|.
name|loadBuiltInModels
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LanguageIDWrapper
operator|.
name|loadModels
argument_list|(
name|langModelDir
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Path
name|commonTokens
init|=
name|getPath
argument_list|(
name|localAttrs
argument_list|,
literal|"commonTokens"
argument_list|)
decl_stmt|;
comment|//can be null, in which case will load from memory
try|try
block|{
name|AbstractProfiler
operator|.
name|loadCommonTokens
argument_list|(
name|commonTokens
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|JDBCUtil
name|jdbcUtil
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|jdbcUtil
operator|=
operator|new
name|H2Util
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|jdbcConnectionString
operator|!=
literal|null
condition|)
block|{
name|jdbcUtil
operator|=
operator|new
name|JDBCUtil
argument_list|(
name|jdbcConnectionString
argument_list|,
name|localAttrs
operator|.
name|get
argument_list|(
literal|"jdbcDriver"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must specify: -db or -jdbc"
argument_list|)
throw|;
block|}
name|EvalConsumerBuilder
name|consumerBuilder
init|=
name|ClassLoaderUtil
operator|.
name|buildClass
argument_list|(
name|EvalConsumerBuilder
operator|.
name|class
argument_list|,
name|PropsUtil
operator|.
name|getString
argument_list|(
name|localAttrs
operator|.
name|get
argument_list|(
literal|"consumerBuilderClass"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|consumerBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must specify consumerBuilderClass in config file"
argument_list|)
throw|;
block|}
name|boolean
name|forceDrop
init|=
name|PropsUtil
operator|.
name|getBoolean
argument_list|(
name|localAttrs
operator|.
name|get
argument_list|(
literal|"drop"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|MimeBuffer
name|mimeBuffer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|mimeBuffer
operator|=
name|consumerBuilder
operator|.
name|init
argument_list|(
name|queue
argument_list|,
name|localAttrs
argument_list|,
name|jdbcUtil
argument_list|,
name|forceDrop
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|consumers
operator|.
name|add
argument_list|(
name|consumerBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|DBConsumersManager
name|manager
decl_stmt|;
try|try
block|{
name|manager
operator|=
operator|new
name|DBConsumersManager
argument_list|(
name|jdbcUtil
argument_list|,
name|mimeBuffer
argument_list|,
name|consumers
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|consumerBuilder
operator|.
name|addErrorLogTablePairs
argument_list|(
name|manager
argument_list|)
expr_stmt|;
return|return
name|manager
return|;
block|}
specifier|private
name|Path
name|getNonNullPath
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|Path
name|p
init|=
name|getPath
argument_list|(
name|attrs
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must specify a file for this attribute: "
operator|+
name|key
argument_list|)
throw|;
block|}
return|return
name|p
return|;
block|}
specifier|protected
name|Path
name|getPath
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|String
name|filePath
init|=
name|attrs
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|filePath
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Paths
operator|.
name|get
argument_list|(
name|filePath
argument_list|)
return|;
block|}
block|}
end_class

end_unit

