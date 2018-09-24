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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|DataInputStream
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
name|int
name|restarts
init|=
literal|0
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
comment|//if the child thread is in stop-the-world mode, and isn't
comment|//responding to the ping, this thread checks to make sure
comment|//that the parent ping is sent and received often enough
comment|//If it isn't, this force destroys the child process.
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
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|//ignore
block|}
name|destroyChildForcibly
argument_list|(
name|processToDestroy
argument_list|)
expr_stmt|;
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
try|try
block|{
name|childProcess
operator|=
operator|new
name|ChildProcess
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|setChildStatus
argument_list|(
name|CHILD_STATUS
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
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
name|info
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
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully restarted child process -- {} restarts so far)"
argument_list|,
operator|++
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
if|if
condition|(
name|childProcess
operator|!=
literal|null
condition|)
block|{
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
name|Process
name|process
decl_stmt|;
name|DataInputStream
name|fromChild
decl_stmt|;
name|DataOutputStream
name|toChild
decl_stmt|;
specifier|private
name|ChildProcess
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|process
operator|=
name|startProcess
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|fromChild
operator|=
operator|new
name|DataInputStream
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
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
name|byte
name|status
init|=
name|fromChild
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|ServerStatus
operator|.
name|STATUS
operator|.
name|OPERATING
operator|.
name|getByte
argument_list|()
condition|)
block|{
try|try
block|{
name|ServerStatus
operator|.
name|STATUS
name|currStatus
init|=
name|ServerStatus
operator|.
name|STATUS
operator|.
name|lookup
argument_list|(
name|status
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"bad status from child process: "
operator|+
name|currStatus
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
name|int
name|len
init|=
name|process
operator|.
name|getInputStream
argument_list|()
operator|.
name|available
argument_list|()
decl_stmt|;
name|byte
index|[]
name|msg
init|=
operator|new
name|byte
index|[
name|len
operator|+
literal|1
index|]
decl_stmt|;
name|msg
index|[
literal|0
index|]
operator|=
name|status
expr_stmt|;
name|process
operator|.
name|getInputStream
argument_list|()
operator|.
name|read
argument_list|(
name|msg
argument_list|,
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unrecognized status code; message:\n"
operator|+
operator|new
name|String
argument_list|(
name|msg
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
throw|;
block|}
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
name|lastPing
operator|=
name|Instant
operator|.
name|now
argument_list|()
expr_stmt|;
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
try|try
block|{
name|byte
name|status
init|=
name|fromChild
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|ServerStatus
operator|.
name|STATUS
operator|.
name|OPERATING
operator|.
name|getByte
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Received status from child: {}"
argument_list|,
name|ServerStatus
operator|.
name|STATUS
operator|.
name|lookup
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
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
literal|"Exception receiving status from child"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|close
parameter_list|()
block|{
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
literal|"Exception asking child to shutdown"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|//TODO: add a gracefully timed shutdown routine
try|try
block|{
name|fromChild
operator|.
name|close
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
literal|"Problem shutting down reader from child"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|toChild
operator|.
name|close
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
block|}
specifier|private
name|Process
name|startProcess
parameter_list|(
name|String
index|[]
name|args
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
name|argList
operator|.
name|add
argument_list|(
literal|"java"
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
name|process
operator|.
name|destroyForcibly
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
block|}
end_class

end_unit

