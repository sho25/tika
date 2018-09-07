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
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
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
name|ConcurrentHashMap
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
specifier|public
class|class
name|ServerStatus
block|{
enum|enum
name|STATUS
block|{
name|OPEN
argument_list|(
literal|0
argument_list|)
block|,
name|HIT_MAX
argument_list|(
literal|1
argument_list|)
block|,
name|TIMEOUT
argument_list|(
literal|2
argument_list|)
block|,
name|ERROR
argument_list|(
literal|3
argument_list|)
block|,
name|PARENT_REQUESTED_SHUTDOWN
argument_list|(
literal|4
argument_list|)
block|;
specifier|private
specifier|final
name|int
name|shutdownCode
decl_stmt|;
name|STATUS
parameter_list|(
name|int
name|shutdownCode
parameter_list|)
block|{
name|this
operator|.
name|shutdownCode
operator|=
name|shutdownCode
expr_stmt|;
block|}
name|int
name|getShutdownCode
parameter_list|()
block|{
return|return
name|shutdownCode
return|;
block|}
block|}
enum|enum
name|TASK
block|{
name|PARSE
block|,
name|UNZIP
block|,
name|DETECT
block|,
name|METADATA
block|}
empty_stmt|;
specifier|private
specifier|final
name|int
name|maxFilesToProcess
decl_stmt|;
specifier|private
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|TaskStatus
argument_list|>
name|tasks
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|STATUS
name|status
init|=
name|STATUS
operator|.
name|OPEN
decl_stmt|;
specifier|public
name|ServerStatus
parameter_list|(
name|int
name|maxFilesToProcess
parameter_list|)
block|{
name|this
operator|.
name|maxFilesToProcess
operator|=
name|maxFilesToProcess
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|int
name|start
parameter_list|(
name|TASK
name|task
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|FileCountExceededException
block|{
name|int
name|i
init|=
name|counter
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|Integer
operator|.
name|MAX_VALUE
operator|||
operator|(
name|maxFilesToProcess
operator|>
literal|0
operator|&&
name|i
operator|>=
name|maxFilesToProcess
operator|)
condition|)
block|{
name|setStatus
argument_list|(
name|STATUS
operator|.
name|HIT_MAX
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FileCountExceededException
argument_list|()
throw|;
block|}
name|tasks
operator|.
name|put
argument_list|(
name|i
argument_list|,
operator|new
name|TaskStatus
argument_list|(
name|task
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|,
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|i
return|;
block|}
comment|/**      * Removes the task from the collection of currently running tasks.      *      * @param taskId      * @throws IllegalArgumentException if there is no task by that taskId in the collection      */
specifier|public
specifier|synchronized
name|void
name|complete
parameter_list|(
name|int
name|taskId
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|TaskStatus
name|status
init|=
name|tasks
operator|.
name|remove
argument_list|(
name|taskId
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"TaskId is not in map:"
operator|+
name|taskId
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|setStatus
parameter_list|(
name|STATUS
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|STATUS
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|Integer
argument_list|,
name|TaskStatus
argument_list|>
name|getTasks
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|TaskStatus
argument_list|>
name|ret
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ret
operator|.
name|putAll
argument_list|(
name|tasks
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|getFilesProcessed
parameter_list|()
block|{
return|return
name|counter
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

