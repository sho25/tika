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
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|ProcessUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|Buffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|OverlappingFileLockException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Files
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
name|time
operator|.
name|Duration
import|;
end_import

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
name|ArrayList
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|READ
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|WRITE
import|;
end_import

begin_class
specifier|public
class|class
name|TikaServerWatchDog
block|{
specifier|private
enum|enum
name|CHILD_STATUS
block|{
name|INITIALIZING
block|,
name|RUNNING
block|,
name|SHUTTING_DOWN
block|}
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TikaServerWatchDog
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CHILD_STATUS_FILE_PREFIX
init|=
literal|"tika-server-child-process-mmap-"
decl_stmt|;
specifier|private
name|Object
index|[]
name|childStatusLock
init|=
operator|new
name|Object
index|[
literal|0
index|]
decl_stmt|;
specifier|private
specifier|volatile
name|CHILD_STATUS
name|childStatus
init|=
name|CHILD_STATUS
operator|.
name|INITIALIZING
decl_stmt|;
specifier|private
specifier|volatile
name|Instant
name|lastPing
init|=
literal|null
decl_stmt|;
specifier|private
name|ChildProcess
name|childProcess
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|ServerTimeouts
name|serverTimeouts
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"server watch dog is starting up"
argument_list|)
expr_stmt|;
name|startPingTimer
argument_list|(
name|serverTimeouts
argument_list|)
expr_stmt|;
try|try
block|{
name|childProcess
operator|=
operator|new
name|ChildProcess
argument_list|(
name|args
argument_list|,
name|serverTimeouts
argument_list|)
expr_stmt|;
name|setChildStatus
argument_list|(
name|CHILD_STATUS
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|int
name|restarts
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|childProcess
operator|.
name|ping
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"bad ping, initializing"
argument_list|)
expr_stmt|;
name|setChildStatus
argument_list|(
name|CHILD_STATUS
operator|.
name|INITIALIZING
argument_list|)
expr_stmt|;
name|lastPing
operator|=
literal|null
expr_stmt|;
name|childProcess
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"About to restart the child process"
argument_list|)
expr_stmt|;
name|childProcess
operator|=
operator|new
name|ChildProcess
argument_list|(
name|args
argument_list|,
name|serverTimeouts
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully restarted child process -- {} restarts so far)"
argument_list|,
name|restarts
argument_list|)
expr_stmt|;
name|setChildStatus
argument_list|(
name|CHILD_STATUS
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|restarts
operator|++
expr_stmt|;
if|if
condition|(
name|serverTimeouts
operator|.
name|getMaxRestarts
argument_list|()
operator|>
operator|-
literal|1
operator|&&
name|restarts
operator|>=
name|serverTimeouts
operator|.
name|getMaxRestarts
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"hit max restarts: "
operator|+
name|restarts
operator|+
literal|". Stopping now"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|serverTimeouts
operator|.
name|getPingPulseMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//interrupted...shutting down
block|}
finally|finally
block|{
name|setChildStatus
argument_list|(
name|CHILD_STATUS
operator|.
name|SHUTTING_DOWN
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"about to shutdown"
argument_list|)
expr_stmt|;
if|if
condition|(
name|childProcess
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"about to shutdown process"
argument_list|)
expr_stmt|;
name|childProcess
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|startPingTimer
parameter_list|(
name|ServerTimeouts
name|serverTimeouts
parameter_list|)
block|{
comment|//if the child thread is in stop-the-world mode, and isn't
comment|//reading the ping, this thread checks to make sure
comment|//that the parent ping is sent often enough.
comment|//The write() in ping() could block.
comment|//If there isn't a successful ping often enough,
comment|//this force destroys the child process.
name|Thread
name|pingTimer
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|long
name|tmpLastPing
init|=
operator|-
literal|1L
decl_stmt|;
synchronized|synchronized
init|(
name|childStatusLock
init|)
block|{
if|if
condition|(
name|childStatus
operator|==
name|CHILD_STATUS
operator|.
name|RUNNING
condition|)
block|{
name|tmpLastPing
operator|=
name|lastPing
operator|.
name|toEpochMilli
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tmpLastPing
operator|>
literal|0
condition|)
block|{
name|long
name|elapsed
init|=
name|Duration
operator|.
name|between
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|tmpLastPing
argument_list|)
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|elapsed
operator|>
name|serverTimeouts
operator|.
name|getPingTimeoutMillis
argument_list|()
condition|)
block|{
name|Process
name|processToDestroy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|processToDestroy
operator|=
name|childProcess
operator|.
name|process
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} ms have elapsed since last successful ping. Destroying child now"
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
name|destroyChildForcibly
argument_list|(
name|processToDestroy
argument_list|)
expr_stmt|;
name|childProcess
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|//ignore
block|}
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|serverTimeouts
operator|.
name|getPingPulseMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|pingTimer
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pingTimer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setChildStatus
parameter_list|(
name|CHILD_STATUS
name|status
parameter_list|)
block|{
synchronized|synchronized
init|(
name|childStatusLock
init|)
block|{
name|childStatus
operator|=
name|status
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|extractArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|argList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-J"
argument_list|)
operator|||
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-spawnChild"
argument_list|)
operator|||
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"--spawnChild"
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-javaHome"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|args
operator|.
name|length
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must specify a value for -javaHome"
argument_list|)
throw|;
block|}
name|i
operator|++
expr_stmt|;
comment|//skip argument value
continue|continue;
block|}
name|argList
operator|.
name|add
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|argList
return|;
block|}
specifier|private
specifier|static
name|String
name|extractJavaPath
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|javaHome
init|=
literal|null
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-javaHome"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|args
operator|.
name|length
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"must specify a value for -javaHome"
argument_list|)
throw|;
block|}
name|javaHome
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|javaHome
operator|==
literal|null
condition|)
block|{
name|javaHome
operator|=
name|System
operator|.
name|getenv
argument_list|(
literal|"JAVA_HOME"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|javaHome
operator|!=
literal|null
condition|)
block|{
name|Path
name|jPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|javaHome
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"bin"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"java"
argument_list|)
decl_stmt|;
return|return
name|ProcessUtils
operator|.
name|escapeCommandLine
argument_list|(
name|jPath
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|"java"
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|extractJVMArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|jvmArgs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|foundHeadlessOption
init|=
literal|false
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-J"
argument_list|)
condition|)
block|{
name|jvmArgs
operator|.
name|add
argument_list|(
literal|"-"
operator|+
name|args
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|contains
argument_list|(
literal|"java.awt.headless"
argument_list|)
condition|)
block|{
name|foundHeadlessOption
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|//if user has already specified headless...don't modify
if|if
condition|(
operator|!
name|foundHeadlessOption
condition|)
block|{
name|jvmArgs
operator|.
name|add
argument_list|(
literal|"-Djava.awt.headless=true"
argument_list|)
expr_stmt|;
block|}
return|return
name|jvmArgs
return|;
block|}
specifier|private
class|class
name|ChildProcess
block|{
specifier|private
name|Thread
name|SHUTDOWN_HOOK
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Process
name|process
decl_stmt|;
specifier|private
specifier|final
name|DataOutputStream
name|toChild
decl_stmt|;
specifier|private
specifier|final
name|ServerTimeouts
name|serverTimeouts
decl_stmt|;
specifier|private
specifier|final
name|Path
name|childStatusFile
decl_stmt|;
specifier|private
specifier|final
name|ByteBuffer
name|statusBuffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|16
argument_list|)
decl_stmt|;
specifier|private
name|ChildProcess
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|ServerTimeouts
name|serverTimeouts
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|prefix
init|=
name|DEFAULT_CHILD_STATUS_FILE_PREFIX
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-tmpFilePrefix"
argument_list|)
condition|)
block|{
name|prefix
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
block|}
block|}
name|this
operator|.
name|childStatusFile
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|prefix
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|serverTimeouts
operator|=
name|serverTimeouts
expr_stmt|;
name|this
operator|.
name|process
operator|=
name|startProcess
argument_list|(
name|args
argument_list|,
name|childStatusFile
argument_list|)
expr_stmt|;
comment|//wait for file to be written/initialized by child process
name|Instant
name|start
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
name|Duration
operator|.
name|between
argument_list|(
name|start
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|process
operator|.
name|isAlive
argument_list|()
operator|&&
name|Files
operator|.
name|size
argument_list|(
name|childStatusFile
argument_list|)
operator|<
literal|12
operator|&&
name|elapsed
operator|<
name|serverTimeouts
operator|.
name|getMaxChildStartupMillis
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|elapsed
operator|=
name|Duration
operator|.
name|between
argument_list|(
name|start
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//the childStatusFile can be deleted by the
comment|//child process if it closes...this can lead to a NoSuchFileException
name|LOG
operator|.
name|warn
argument_list|(
literal|"failed to start child process"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|elapsed
operator|>
name|serverTimeouts
operator|.
name|getMaxChildStartupMillis
argument_list|()
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Child process failed to start after "
operator|+
name|elapsed
operator|+
literal|" (ms)"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|process
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to start child process -- child is not alive"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|childStatusFile
argument_list|)
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to start child process -- child status file does not exist"
argument_list|)
throw|;
block|}
name|this
operator|.
name|toChild
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|process
operator|.
name|getOutputStream
argument_list|()
argument_list|)
expr_stmt|;
name|lastPing
operator|=
name|Instant
operator|.
name|now
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|ping
parameter_list|()
block|{
if|if
condition|(
operator|!
name|process
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"process is not alive"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
try|try
block|{
name|toChild
operator|.
name|writeByte
argument_list|(
name|ServerStatus
operator|.
name|DIRECTIVES
operator|.
name|PING
operator|.
name|getByte
argument_list|()
argument_list|)
expr_stmt|;
name|toChild
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception pinging child process"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|ChildStatus
name|childStatus
init|=
literal|null
decl_stmt|;
try|try
block|{
name|childStatus
operator|=
name|readStatus
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception reading status from child"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|childStatus
operator|.
name|status
operator|!=
name|ServerStatus
operator|.
name|STATUS
operator|.
name|OPERATING
operator|.
name|getInt
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Received non-operating status from child: {}"
argument_list|,
name|ServerStatus
operator|.
name|STATUS
operator|.
name|lookup
argument_list|(
name|childStatus
operator|.
name|status
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|long
name|elapsedSinceLastUpdate
init|=
name|Duration
operator|.
name|between
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|childStatus
operator|.
name|timestamp
argument_list|)
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"last update: {}, elapsed:{}, status:{}"
argument_list|,
name|childStatus
operator|.
name|timestamp
argument_list|,
name|elapsedSinceLastUpdate
argument_list|,
name|childStatus
operator|.
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
name|elapsedSinceLastUpdate
operator|>
name|serverTimeouts
operator|.
name|getPingTimeoutMillis
argument_list|()
condition|)
block|{
comment|//child hasn't written a status update in a longer time than allowed
name|LOG
operator|.
name|warn
argument_list|(
literal|"Child's last update exceeded ping timeout: {} (ms) with status {}"
argument_list|,
name|elapsedSinceLastUpdate
argument_list|,
name|childStatus
operator|.
name|status
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|lastPing
operator|=
name|Instant
operator|.
name|now
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|ChildStatus
name|readStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|Instant
name|started
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|Long
name|elapsed
init|=
name|Duration
operator|.
name|between
argument_list|(
name|started
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
comment|//only reading, but need to include write to allow for locking
try|try
init|(
name|FileChannel
name|fc
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|childStatusFile
argument_list|,
name|READ
argument_list|,
name|WRITE
argument_list|)
init|)
block|{
while|while
condition|(
name|elapsed
operator|<
name|serverTimeouts
operator|.
name|getPingTimeoutMillis
argument_list|()
condition|)
block|{
try|try
init|(
name|FileLock
name|lock
init|=
name|fc
operator|.
name|tryLock
argument_list|(
literal|0
argument_list|,
literal|16
argument_list|,
literal|true
argument_list|)
init|)
block|{
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|Buffer
operator|)
name|statusBuffer
operator|)
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fc
operator|.
name|read
argument_list|(
name|statusBuffer
argument_list|)
expr_stmt|;
name|long
name|timestamp
init|=
name|statusBuffer
operator|.
name|getLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|status
init|=
name|statusBuffer
operator|.
name|getInt
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|int
name|numTasks
init|=
name|statusBuffer
operator|.
name|getInt
argument_list|(
literal|12
argument_list|)
decl_stmt|;
return|return
operator|new
name|ChildStatus
argument_list|(
name|timestamp
argument_list|,
name|status
argument_list|,
name|numTasks
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|OverlappingFileLockException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
name|elapsed
operator|=
name|Duration
operator|.
name|between
argument_list|(
name|started
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"couldn't read from status file after "
operator|+
name|elapsed
operator|+
literal|" millis"
argument_list|)
throw|;
block|}
specifier|private
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|toChild
operator|!=
literal|null
condition|)
block|{
name|toChild
operator|.
name|writeByte
argument_list|(
name|ServerStatus
operator|.
name|DIRECTIVES
operator|.
name|SHUTDOWN
operator|.
name|getByte
argument_list|()
argument_list|)
expr_stmt|;
name|toChild
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exception asking child to shutdown"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|toChild
operator|!=
literal|null
condition|)
block|{
name|toChild
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Problem shutting down writer to child"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|destroyChildForcibly
argument_list|(
name|process
argument_list|)
expr_stmt|;
if|if
condition|(
name|childStatusFile
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|Files
operator|.
name|isRegularFile
argument_list|(
name|childStatusFile
argument_list|)
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|childStatusFile
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"deleted "
operator|+
name|childStatusFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"problem deleting child status file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Process
name|startProcess
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Path
name|childStatusFile
parameter_list|)
throws|throws
name|IOException
block|{
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|redirectError
argument_list|(
name|ProcessBuilder
operator|.
name|Redirect
operator|.
name|INHERIT
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|argList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|javaPath
init|=
name|extractJavaPath
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|jvmArgs
init|=
name|extractJVMArgs
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|childArgs
init|=
name|extractArgs
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|childArgs
operator|.
name|add
argument_list|(
literal|"-childStatusFile"
argument_list|)
expr_stmt|;
name|childArgs
operator|.
name|add
argument_list|(
name|ProcessUtils
operator|.
name|escapeCommandLine
argument_list|(
name|childStatusFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|argList
operator|.
name|add
argument_list|(
name|javaPath
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|jvmArgs
operator|.
name|contains
argument_list|(
literal|"-cp"
argument_list|)
operator|&&
operator|!
name|jvmArgs
operator|.
name|contains
argument_list|(
literal|"--classpath"
argument_list|)
condition|)
block|{
name|String
name|cp
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
decl_stmt|;
name|jvmArgs
operator|.
name|add
argument_list|(
literal|"-cp"
argument_list|)
expr_stmt|;
name|jvmArgs
operator|.
name|add
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
name|argList
operator|.
name|addAll
argument_list|(
name|jvmArgs
argument_list|)
expr_stmt|;
name|argList
operator|.
name|add
argument_list|(
literal|"org.apache.tika.server.TikaServerCli"
argument_list|)
expr_stmt|;
name|argList
operator|.
name|addAll
argument_list|(
name|childArgs
argument_list|)
expr_stmt|;
name|argList
operator|.
name|add
argument_list|(
literal|"-child"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"child process commandline: "
operator|+
name|argList
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|command
argument_list|(
name|argList
argument_list|)
expr_stmt|;
name|Process
name|process
init|=
name|builder
operator|.
name|start
argument_list|()
decl_stmt|;
comment|//redirect stdout to parent stderr to avoid error msgs
comment|//from maven during build: Corrupted STDOUT by directly writing to native stream in forked
name|redirectIO
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
if|if
condition|(
name|SHUTDOWN_HOOK
operator|!=
literal|null
condition|)
block|{
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|removeShutdownHook
argument_list|(
name|SHUTDOWN_HOOK
argument_list|)
expr_stmt|;
block|}
name|SHUTDOWN_HOOK
operator|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
name|this
operator|.
name|close
argument_list|()
argument_list|)
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
name|SHUTDOWN_HOOK
argument_list|)
expr_stmt|;
return|return
name|process
return|;
block|}
block|}
specifier|private
specifier|static
name|void
name|redirectIO
parameter_list|(
specifier|final
name|InputStream
name|src
parameter_list|,
specifier|final
name|PrintStream
name|targ
parameter_list|)
block|{
name|Thread
name|gobbler
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|src
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
try|try
block|{
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|targ
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|gobbler
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|gobbler
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|synchronized
name|void
name|destroyChildForcibly
parameter_list|(
name|Process
name|process
parameter_list|)
block|{
name|process
operator|=
name|process
operator|.
name|destroyForcibly
argument_list|()
expr_stmt|;
try|try
block|{
name|boolean
name|destroyed
init|=
name|process
operator|.
name|waitFor
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|destroyed
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Child process still alive after 60 seconds. "
operator|+
literal|"Shutting down the parent."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
specifier|private
specifier|static
class|class
name|ChildStatus
block|{
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
specifier|private
specifier|final
name|int
name|status
decl_stmt|;
specifier|private
specifier|final
name|int
name|numTasks
decl_stmt|;
specifier|public
name|ChildStatus
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|int
name|status
parameter_list|,
name|int
name|numTasks
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|numTasks
operator|=
name|numTasks
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ChildStatus{"
operator|+
literal|"timestamp="
operator|+
name|timestamp
operator|+
literal|", status="
operator|+
name|status
operator|+
literal|", numTasks="
operator|+
name|numTasks
operator|+
literal|'}'
return|;
block|}
block|}
block|}
end_class

end_unit

