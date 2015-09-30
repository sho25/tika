begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|batch
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Locale
import|;
end_import

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

begin_class
specifier|public
class|class
name|BatchProcessDriverCLI
block|{
comment|/**      * This relies on an special exit values of 254 (do not restart),      * 0 ended correctly, 253 ended with exception (do restart)      */
specifier|public
specifier|static
specifier|final
name|int
name|PROCESS_RESTART_EXIT_CODE
init|=
literal|253
decl_stmt|;
comment|//make sure this is above 255 to avoid stopping on system errors
comment|//that is, if there is a system error (e.g. 143), you
comment|//should restart the process.
specifier|public
specifier|static
specifier|final
name|int
name|PROCESS_NO_RESTART_EXIT_CODE
init|=
literal|254
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|PROCESS_COMPLETED_SUCCESSFULLY
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BatchProcessDriverCLI
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|maxProcessRestarts
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|pulseMillis
init|=
literal|1000
decl_stmt|;
comment|//how many times to wait pulseMillis milliseconds if a restart
comment|//message has been received through stdout, but the
comment|//child process has not yet exited
specifier|private
name|int
name|waitNumLoopsAfterRestartmessage
init|=
literal|60
decl_stmt|;
name|int
name|loopsAfterRestartMessageReceived
init|=
literal|0
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|userInterrupted
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|receivedRestartMsg
init|=
literal|false
decl_stmt|;
specifier|private
name|Process
name|process
init|=
literal|null
decl_stmt|;
specifier|private
name|StreamGobbler
name|errorWatcher
init|=
literal|null
decl_stmt|;
specifier|private
name|StreamGobbler
name|outGobbler
init|=
literal|null
decl_stmt|;
specifier|private
name|InterruptWriter
name|interruptWriter
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|InterruptWatcher
name|interruptWatcher
init|=
operator|new
name|InterruptWatcher
argument_list|(
name|System
operator|.
name|in
argument_list|)
decl_stmt|;
specifier|private
name|Thread
name|errorWatcherThread
init|=
literal|null
decl_stmt|;
specifier|private
name|Thread
name|outGobblerThread
init|=
literal|null
decl_stmt|;
specifier|private
name|Thread
name|interruptWriterThread
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Thread
name|interruptWatcherThread
init|=
operator|new
name|Thread
argument_list|(
name|interruptWatcher
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|commandLine
decl_stmt|;
specifier|private
name|int
name|numRestarts
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|redirectChildProcessToStdOut
init|=
literal|true
decl_stmt|;
specifier|public
name|BatchProcessDriverCLI
parameter_list|(
name|String
index|[]
name|commandLine
parameter_list|)
block|{
name|this
operator|.
name|commandLine
operator|=
name|tryToReadMaxRestarts
argument_list|(
name|commandLine
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
index|[]
name|tryToReadMaxRestarts
parameter_list|(
name|String
index|[]
name|commandLine
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
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
name|commandLine
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|arg
init|=
name|commandLine
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-maxRestarts"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|commandLine
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
literal|"Must specify an integer after \"-maxRestarts\""
argument_list|)
throw|;
block|}
name|String
name|restartNumString
init|=
name|commandLine
index|[
name|i
operator|+
literal|1
index|]
decl_stmt|;
try|try
block|{
name|maxProcessRestarts
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|restartNumString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must specify an integer after \"-maxRestarts\" arg."
argument_list|)
throw|;
block|}
name|i
operator|++
expr_stmt|;
block|}
else|else
block|{
name|args
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|args
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|args
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|interruptWatcherThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|interruptWatcherThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"about to start driver"
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|userInterrupted
condition|)
block|{
name|Integer
name|exit
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"about to check exit value"
argument_list|)
expr_stmt|;
name|exit
operator|=
name|process
operator|.
name|exitValue
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"The child process has finished with an exit value of: "
operator|+
name|exit
argument_list|)
expr_stmt|;
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalThreadStateException
name|e
parameter_list|)
block|{
comment|//hasn't exited
name|logger
operator|.
name|trace
argument_list|(
literal|"process has not exited; IllegalThreadStateException"
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"Before sleep:"
operator|+
literal|" exit="
operator|+
name|exit
operator|+
literal|" receivedRestartMsg="
operator|+
name|receivedRestartMsg
argument_list|)
expr_stmt|;
comment|//Even if the process has exited,
comment|//wait just a little bit to make sure that
comment|//mustRestart hasn't been set to true
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|pulseMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"interrupted exception during sleep"
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"After sleep:"
operator|+
literal|" exit="
operator|+
name|exit
operator|+
literal|" receivedRestartMsg="
operator|+
name|receivedRestartMsg
argument_list|)
expr_stmt|;
comment|//if we've gotten the message via stdout to restart
comment|//but the process hasn't exited yet, give it another
comment|//chance
if|if
condition|(
name|receivedRestartMsg
operator|&&
name|exit
operator|==
literal|null
operator|&&
name|loopsAfterRestartMessageReceived
operator|<=
name|waitNumLoopsAfterRestartmessage
condition|)
block|{
name|loopsAfterRestartMessageReceived
operator|++
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Must restart, still not exited; loops after restart: "
operator|+
name|loopsAfterRestartMessageReceived
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|loopsAfterRestartMessageReceived
operator|>
name|waitNumLoopsAfterRestartmessage
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"About to try to restart because:"
operator|+
literal|" exit="
operator|+
name|exit
operator|+
literal|" receivedRestartMsg="
operator|+
name|receivedRestartMsg
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Restarting after exceeded wait loops waiting for exit: "
operator|+
name|loopsAfterRestartMessageReceived
argument_list|)
expr_stmt|;
name|boolean
name|restarted
init|=
name|restart
argument_list|(
name|exit
argument_list|,
name|receivedRestartMsg
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|restarted
condition|)
block|{
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|exit
operator|!=
literal|null
operator|&&
name|exit
operator|!=
name|BatchProcessDriverCLI
operator|.
name|PROCESS_NO_RESTART_EXIT_CODE
operator|&&
name|exit
operator|!=
name|BatchProcessDriverCLI
operator|.
name|PROCESS_COMPLETED_SUCCESSFULLY
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"About to try to restart because:"
operator|+
literal|" exit="
operator|+
name|exit
operator|+
literal|" receivedRestartMsg="
operator|+
name|receivedRestartMsg
argument_list|)
expr_stmt|;
if|if
condition|(
name|exit
operator|==
name|BatchProcessDriverCLI
operator|.
name|PROCESS_RESTART_EXIT_CODE
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Restarting on expected restart code"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Restarting on unexpected restart code: "
operator|+
name|exit
argument_list|)
expr_stmt|;
block|}
name|boolean
name|restarted
init|=
name|restart
argument_list|(
name|exit
argument_list|,
name|receivedRestartMsg
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|restarted
condition|)
block|{
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|exit
operator|!=
literal|null
operator|&&
operator|(
name|exit
operator|==
name|PROCESS_COMPLETED_SUCCESSFULLY
operator|||
name|exit
operator|==
name|BatchProcessDriverCLI
operator|.
name|PROCESS_NO_RESTART_EXIT_CODE
operator|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Will not restart: "
operator|+
name|exit
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"about to call shutdown driver now"
argument_list|)
expr_stmt|;
name|shutdownDriverNow
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Process driver has completed"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|shutdownDriverNow
parameter_list|()
block|{
if|if
condition|(
name|process
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"trying to shut down: "
operator|+
name|i
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|exit
init|=
name|process
operator|.
name|exitValue
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"trying to stop:"
operator|+
name|exit
argument_list|)
expr_stmt|;
name|stop
argument_list|()
expr_stmt|;
name|interruptWatcherThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|IllegalThreadStateException
name|e
parameter_list|)
block|{
comment|//hasn't exited
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
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
name|logger
operator|.
name|error
argument_list|(
literal|"Process didn't stop after 60 seconds after shutdown. "
operator|+
literal|"I am forcefully killing it."
argument_list|)
expr_stmt|;
block|}
name|interruptWatcherThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getNumRestarts
parameter_list|()
block|{
return|return
name|numRestarts
return|;
block|}
specifier|public
name|boolean
name|getUserInterrupted
parameter_list|()
block|{
return|return
name|userInterrupted
return|;
block|}
comment|/**      * Tries to restart (stop and then start) the child process      * @return whether or not this was successful, will be false if numRestarts>= maxProcessRestarts      * @throws Exception      */
specifier|private
name|boolean
name|restart
parameter_list|(
name|Integer
name|exitValue
parameter_list|,
name|boolean
name|receivedRestartMsg
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|maxProcessRestarts
operator|>
operator|-
literal|1
operator|&&
name|numRestarts
operator|>=
name|maxProcessRestarts
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Hit the maximum number of process restarts. Driver is shutting down now."
argument_list|)
expr_stmt|;
name|stop
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|logger
operator|.
name|warn
argument_list|(
literal|"Must restart process (exitValue="
operator|+
name|exitValue
operator|+
literal|" numRestarts="
operator|+
name|numRestarts
operator|+
literal|" receivedRestartMessage="
operator|+
name|receivedRestartMsg
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|stop
argument_list|()
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
name|numRestarts
operator|++
expr_stmt|;
name|loopsAfterRestartMessageReceived
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|process
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"destroying a non-null process"
argument_list|)
expr_stmt|;
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
name|receivedRestartMsg
operator|=
literal|false
expr_stmt|;
comment|//interrupt the writer thread first
name|interruptWriterThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|errorWatcher
operator|.
name|stopGobblingAndDie
argument_list|()
expr_stmt|;
name|outGobbler
operator|.
name|stopGobblingAndDie
argument_list|()
expr_stmt|;
name|errorWatcherThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|outGobblerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|commandLine
argument_list|)
decl_stmt|;
name|builder
operator|.
name|directory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|process
operator|=
name|builder
operator|.
name|start
argument_list|()
expr_stmt|;
name|errorWatcher
operator|=
operator|new
name|StreamWatcher
argument_list|(
name|process
operator|.
name|getErrorStream
argument_list|()
argument_list|)
expr_stmt|;
name|errorWatcherThread
operator|=
operator|new
name|Thread
argument_list|(
name|errorWatcher
argument_list|)
expr_stmt|;
name|errorWatcherThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|outGobbler
operator|=
operator|new
name|StreamGobbler
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|outGobblerThread
operator|=
operator|new
name|Thread
argument_list|(
name|outGobbler
argument_list|)
expr_stmt|;
name|outGobblerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|interruptWriter
operator|=
operator|new
name|InterruptWriter
argument_list|(
name|process
operator|.
name|getOutputStream
argument_list|()
argument_list|)
expr_stmt|;
name|interruptWriterThread
operator|=
operator|new
name|Thread
argument_list|(
name|interruptWriter
argument_list|)
expr_stmt|;
name|interruptWriterThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * Typically only used for testing.  This determines whether or not      * to redirect child process's stdOut to driver's stdout      * @param redirectChildProcessToStdOut should the driver redirect the child's stdout      */
specifier|public
name|void
name|setRedirectChildProcessToStdOut
parameter_list|(
name|boolean
name|redirectChildProcessToStdOut
parameter_list|)
block|{
name|this
operator|.
name|redirectChildProcessToStdOut
operator|=
name|redirectChildProcessToStdOut
expr_stmt|;
block|}
comment|/**      * Class to watch stdin from the driver for anything that is typed.      * This will currently cause an interrupt if anything followed by      * a return key is entered.  We may want to add an "Are you sure?" dialogue.      */
specifier|private
class|class
name|InterruptWatcher
implements|implements
name|Runnable
block|{
specifier|private
name|BufferedReader
name|reader
decl_stmt|;
specifier|private
name|InterruptWatcher
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|//this will block.
comment|//as soon as it reads anything,
comment|//set userInterrupted to true and stop
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|userInterrupted
operator|=
literal|true
expr_stmt|;
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
comment|/**      * Class that writes to the child process      * to force an interrupt in the child process.      */
specifier|private
class|class
name|InterruptWriter
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|Writer
name|writer
decl_stmt|;
specifier|private
name|InterruptWriter
parameter_list|(
name|OutputStream
name|os
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
if|if
condition|(
name|userInterrupted
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"Ave atque vale!%n"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//job is done, ok
block|}
block|}
block|}
specifier|private
class|class
name|StreamGobbler
implements|implements
name|Runnable
block|{
comment|//plagiarized from org.apache.oodt's StreamGobbler
specifier|protected
specifier|final
name|BufferedReader
name|reader
decl_stmt|;
specifier|protected
name|boolean
name|running
init|=
literal|true
decl_stmt|;
specifier|private
name|StreamGobbler
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"gobbler starting to read"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
operator|&&
name|this
operator|.
name|running
condition|)
block|{
if|if
condition|(
name|redirectChildProcessToStdOut
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"BatchProcess:"
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"gobbler io exception"
argument_list|)
expr_stmt|;
comment|//swallow ioe
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"gobbler done"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|stopGobblingAndDie
parameter_list|()
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"stop gobbling"
argument_list|)
expr_stmt|;
name|running
operator|=
literal|false
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|StreamWatcher
extends|extends
name|StreamGobbler
implements|implements
name|Runnable
block|{
comment|//plagiarized from org.apache.oodt's StreamGobbler
specifier|private
name|StreamWatcher
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|super
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"watcher starting to read"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
operator|&&
name|this
operator|.
name|running
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|BatchProcess
operator|.
name|BATCH_CONSTANTS
operator|.
name|BATCH_PROCESS_FATAL_MUST_RESTART
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|receivedRestartMsg
operator|=
literal|true
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"BatchProcess: "
operator|+
name|line
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
name|logger
operator|.
name|trace
argument_list|(
literal|"watcher io exception"
argument_list|)
expr_stmt|;
comment|//swallow ioe
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"watcher done"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|BatchProcessDriverCLI
name|runner
init|=
operator|new
name|BatchProcessDriverCLI
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|runner
operator|.
name|execute
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FSBatchProcessDriver has gracefully completed"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

