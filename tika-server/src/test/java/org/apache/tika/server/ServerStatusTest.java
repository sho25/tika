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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Random
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
name|Callable
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
name|ExecutorCompletionService
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
name|ExecutorService
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
name|Executors
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
name|Future
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|ServerStatusTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testBadId
parameter_list|()
throws|throws
name|Exception
block|{
name|ServerStatus
name|status
init|=
operator|new
name|ServerStatus
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|status
operator|.
name|complete
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testBasicMultiThreading
parameter_list|()
throws|throws
name|Exception
block|{
comment|//make sure that synchronization is basically working
name|int
name|numThreads
init|=
literal|100
decl_stmt|;
name|int
name|filesToProcess
init|=
literal|100
decl_stmt|;
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|ExecutorCompletionService
argument_list|<
name|Integer
argument_list|>
name|completionService
init|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|service
argument_list|)
decl_stmt|;
name|ServerStatus
name|serverStatus
init|=
operator|new
name|ServerStatus
argument_list|(
operator|-
literal|1
argument_list|)
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|completionService
operator|.
name|submit
argument_list|(
operator|new
name|MockTask
argument_list|(
name|serverStatus
argument_list|,
name|filesToProcess
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|finished
init|=
literal|0
decl_stmt|;
name|int
name|totalProcessed
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|finished
operator|<
name|numThreads
condition|)
block|{
name|Future
argument_list|<
name|Integer
argument_list|>
name|future
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
if|if
condition|(
name|future
operator|!=
literal|null
condition|)
block|{
name|finished
operator|++
expr_stmt|;
name|Integer
name|completed
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|totalProcessed
operator|+=
name|completed
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|numThreads
operator|*
name|filesToProcess
argument_list|,
name|totalProcessed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|serverStatus
operator|.
name|getTasks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|totalProcessed
argument_list|,
name|serverStatus
operator|.
name|getFilesProcessed
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|MockTask
implements|implements
name|Callable
argument_list|<
name|Integer
argument_list|>
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ServerStatus
name|serverStatus
decl_stmt|;
specifier|private
specifier|final
name|int
name|filesToProcess
decl_stmt|;
specifier|public
name|MockTask
parameter_list|(
name|ServerStatus
name|serverStatus
parameter_list|,
name|int
name|filesToProcess
parameter_list|)
block|{
name|this
operator|.
name|serverStatus
operator|=
name|serverStatus
expr_stmt|;
name|this
operator|.
name|filesToProcess
operator|=
name|filesToProcess
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|processed
init|=
literal|0
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
name|filesToProcess
condition|;
name|i
operator|++
control|)
block|{
name|sleepRandom
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|int
name|taskId
init|=
name|serverStatus
operator|.
name|start
argument_list|(
name|ServerStatus
operator|.
name|TASK
operator|.
name|PARSE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|sleepRandom
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|serverStatus
operator|.
name|complete
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
name|processed
operator|++
expr_stmt|;
name|serverStatus
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|sleepRandom
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|serverStatus
operator|.
name|setStatus
argument_list|(
name|ServerStatus
operator|.
name|STATUS
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|sleepRandom
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|TaskStatus
argument_list|>
name|tasks
init|=
name|serverStatus
operator|.
name|getTasks
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|tasks
argument_list|)
expr_stmt|;
block|}
return|return
name|processed
return|;
block|}
specifier|private
name|void
name|sleepRandom
parameter_list|(
name|int
name|millis
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|sleep
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|millis
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

