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
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLOutputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Flushable
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|TimeUnit
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|org
operator|.
name|slf4j
operator|.
name|MarkerFactory
import|;
end_import

begin_comment
comment|/**  * This is a base class for file consumers. The  * goal of this class is to abstract out the multithreading  * and recordkeeping components.  *<p/>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|FileResourceConsumer
implements|implements
name|Callable
argument_list|<
name|IFileProcessorFutureResult
argument_list|>
block|{
specifier|private
specifier|static
enum|enum
name|STATE
block|{
name|NOT_YET_STARTED
block|,
name|ACTIVELY_CONSUMING
block|,
name|SWALLOWED_POISON
block|,
name|THREAD_INTERRUPTED
block|,
name|EXCEEDED_MAX_CONSEC_WAIT_MILLIS
block|,
name|ASKED_TO_SHUTDOWN
block|,
name|TIMED_OUT
block|,
name|CONSUMER_EXCEPTION
block|,
name|CONSUMER_ERROR
block|,
name|COMPLETED
block|}
specifier|public
specifier|static
name|String
name|TIME_OUT
init|=
literal|"timeout"
decl_stmt|;
specifier|public
specifier|static
name|String
name|ELAPSED_MILLIS
init|=
literal|"elapsedMS"
decl_stmt|;
specifier|private
specifier|static
name|AtomicInteger
name|numConsumers
init|=
operator|new
name|AtomicInteger
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FileResourceConsumer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|maxConsecWaitInMillis
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 10 minutes
specifier|private
specifier|final
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|fileQueue
decl_stmt|;
specifier|private
specifier|final
name|XMLOutputFactory
name|xmlOutputFactory
init|=
name|XMLOutputFactory
operator|.
name|newFactory
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|consumerId
decl_stmt|;
comment|//used to lock checks on state to prevent
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|//this records the file that is currently
comment|//being processed.  It is null if no file is currently being processed.
comment|//no need for volatile because of lock for checkForStales
specifier|private
name|FileStarted
name|currentFile
init|=
literal|null
decl_stmt|;
comment|//total number of files consumed; volatile so that reporter
comment|//sees the latest
specifier|private
specifier|volatile
name|int
name|numResourcesConsumed
init|=
literal|0
decl_stmt|;
comment|//total number of exceptions that were handled by subclasses;
comment|//volatile so that reporter sees the latest
specifier|private
specifier|volatile
name|int
name|numHandledExceptions
init|=
literal|0
decl_stmt|;
comment|//after this has been set to ACTIVELY_CONSUMING,
comment|//this should only be set by setEndedState.
specifier|private
specifier|volatile
name|STATE
name|currentState
init|=
name|STATE
operator|.
name|NOT_YET_STARTED
decl_stmt|;
specifier|public
name|FileResourceConsumer
parameter_list|(
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|fileQueue
parameter_list|)
block|{
name|this
operator|.
name|fileQueue
operator|=
name|fileQueue
expr_stmt|;
name|consumerId
operator|=
name|numConsumers
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
specifier|public
name|IFileProcessorFutureResult
name|call
parameter_list|()
block|{
name|currentState
operator|=
name|STATE
operator|.
name|ACTIVELY_CONSUMING
expr_stmt|;
try|try
block|{
name|FileResource
name|fileResource
init|=
name|getNextFileResource
argument_list|()
decl_stmt|;
while|while
condition|(
name|fileResource
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"file consumer is about to process: "
operator|+
name|fileResource
operator|.
name|getResourceId
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|consumed
init|=
name|_processFileResource
argument_list|(
name|fileResource
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"file consumer has finished processing: "
operator|+
name|fileResource
operator|.
name|getResourceId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumed
condition|)
block|{
name|numResourcesConsumed
operator|++
expr_stmt|;
block|}
name|fileResource
operator|=
name|getNextFileResource
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|setEndedState
argument_list|(
name|STATE
operator|.
name|THREAD_INTERRUPTED
argument_list|)
expr_stmt|;
block|}
name|setEndedState
argument_list|(
name|STATE
operator|.
name|COMPLETED
argument_list|)
expr_stmt|;
return|return
operator|new
name|FileConsumerFutureResult
argument_list|(
name|currentFile
argument_list|,
name|numResourcesConsumed
argument_list|)
return|;
block|}
comment|/**      * Main piece of code that needs to be implemented.  Clients      * are responsible for closing streams and handling the exceptions      * that they'd like to handle.      *<p/>      * Unchecked throwables can be thrown past this, of course.  When an unchecked      * throwable is thrown, this logs the error, and then rethrows the exception.      * Clients/subclasses should make sure to catch and handle everything they can.      *<p/>      * The design goal is that the whole process should close up and shutdown soon after      * an unchecked exception or error is thrown.      *<p/>      * Make sure to call {@link #incrementHandledExceptions()} appropriately in      * your implementation of this method.      *<p/>      *      * @param fileResource resource to process      * @return whether or not a file was successfully processed      */
specifier|public
specifier|abstract
name|boolean
name|processFileResource
parameter_list|(
name|FileResource
name|fileResource
parameter_list|)
function_decl|;
comment|/**      * Make sure to call this appropriately!      */
specifier|protected
name|void
name|incrementHandledExceptions
parameter_list|()
block|{
name|numHandledExceptions
operator|++
expr_stmt|;
block|}
comment|/**      * Returns whether or not the consumer is still could process      * a file or is still processing a file (ACTIVELY_CONSUMING or ASKED_TO_SHUTDOWN)      * @return whether this consumer is still active      */
specifier|public
name|boolean
name|isStillActive
parameter_list|()
block|{
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|currentState
operator|==
name|STATE
operator|.
name|NOT_YET_STARTED
operator|||
name|currentState
operator|==
name|STATE
operator|.
name|ACTIVELY_CONSUMING
operator|||
name|currentState
operator|==
name|STATE
operator|.
name|ASKED_TO_SHUTDOWN
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|_processFileResource
parameter_list|(
name|FileResource
name|fileResource
parameter_list|)
block|{
name|currentFile
operator|=
operator|new
name|FileStarted
argument_list|(
name|fileResource
operator|.
name|getResourceId
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|consumed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumed
operator|=
name|processFileResource
argument_list|(
name|fileResource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|setEndedState
argument_list|(
name|STATE
operator|.
name|CONSUMER_EXCEPTION
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|setEndedState
argument_list|(
name|STATE
operator|.
name|CONSUMER_ERROR
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|//if anything is thrown from processFileResource, then the fileStarted
comment|//will remain what it was right before the exception was thrown.
name|currentFile
operator|=
literal|null
expr_stmt|;
return|return
name|consumed
return|;
block|}
comment|/**      * This politely asks the consumer to shutdown.      * Before processing another file, the consumer will check to see      * if it has been asked to terminate.      *<p>      * This offers another method for politely requesting      * that a FileResourceConsumer stop processing      * besides passing it {@link org.apache.tika.batch.PoisonFileResource}.      *      */
specifier|public
name|void
name|pleaseShutdown
parameter_list|()
block|{
name|setEndedState
argument_list|(
name|STATE
operator|.
name|ASKED_TO_SHUTDOWN
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the name and start time of a file that is currently being processed.      * If no file is currently being processed, this will return null.      *      * @return FileStarted or null      */
specifier|public
name|FileStarted
name|getCurrentFile
parameter_list|()
block|{
return|return
name|currentFile
return|;
block|}
specifier|public
name|int
name|getNumResourcesConsumed
parameter_list|()
block|{
return|return
name|numResourcesConsumed
return|;
block|}
specifier|public
name|int
name|getNumHandledExceptions
parameter_list|()
block|{
return|return
name|numHandledExceptions
return|;
block|}
comment|/**      * Checks to see if the currentFile being processed (if there is one)      * should be timed out (still being worked on after staleThresholdMillis).      *<p>      * If the consumer should be timed out, this will return the currentFile and      * set the state to TIMED_OUT.      *<p>      * If the consumer was already timed out earlier or      * is not processing a file or has been working on a file      * for less than #staleThresholdMillis, then this will return null.      *<p>      * @param staleThresholdMillis threshold to determine whether the consumer has gone stale.      * @return null or the file started that triggered the stale condition      */
specifier|public
name|FileStarted
name|checkForTimedOutMillis
parameter_list|(
name|long
name|staleThresholdMillis
parameter_list|)
block|{
comment|//if there isn't a current file, don't bother obtaining lock
if|if
condition|(
name|currentFile
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//if threshold is< 0, don't even look.
if|if
condition|(
name|staleThresholdMillis
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
synchronized|synchronized
init|(
name|lock
init|)
block|{
comment|//check again once the lock has been obtained
if|if
condition|(
name|currentState
operator|!=
name|STATE
operator|.
name|ACTIVELY_CONSUMING
operator|&&
name|currentState
operator|!=
name|STATE
operator|.
name|ASKED_TO_SHUTDOWN
condition|)
block|{
return|return
literal|null
return|;
block|}
name|FileStarted
name|tmp
init|=
name|currentFile
decl_stmt|;
if|if
condition|(
name|tmp
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|tmp
operator|.
name|getElapsedMillis
argument_list|()
operator|>
name|staleThresholdMillis
condition|)
block|{
name|setEndedState
argument_list|(
name|STATE
operator|.
name|TIMED_OUT
argument_list|)
expr_stmt|;
name|logWithResourceId
argument_list|(
name|Level
operator|.
name|FATAL
argument_list|,
name|TIME_OUT
argument_list|,
name|tmp
operator|.
name|getResourceId
argument_list|()
argument_list|,
name|ELAPSED_MILLIS
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|tmp
operator|.
name|getElapsedMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|tmp
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|logWithResourceId
parameter_list|(
name|Level
name|level
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|resourceId
parameter_list|,
name|String
modifier|...
name|attrs
parameter_list|)
block|{
name|logWithResourceId
argument_list|(
name|level
argument_list|,
name|type
argument_list|,
name|resourceId
argument_list|,
literal|null
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
comment|/**      * Use this for structured output that captures resourceId and other attributes.      *      * @param level level      * @param type entity name for exception      * @param resourceId resourceId string      * @param t throwable can be null      * @param attrs (array of key0, value0, key1, value1, etc.)      */
specifier|protected
name|void
name|logWithResourceId
parameter_list|(
name|Level
name|level
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|resourceId
parameter_list|,
name|Throwable
name|t
parameter_list|,
name|String
modifier|...
name|attrs
parameter_list|)
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
try|try
block|{
name|XMLStreamWriter
name|xml
init|=
name|xmlOutputFactory
operator|.
name|createXMLStreamWriter
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|xml
operator|.
name|writeStartDocument
argument_list|()
expr_stmt|;
name|xml
operator|.
name|writeStartElement
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|xml
operator|.
name|writeAttribute
argument_list|(
literal|"resourceId"
argument_list|,
name|resourceId
argument_list|)
expr_stmt|;
if|if
condition|(
name|attrs
operator|!=
literal|null
condition|)
block|{
comment|//this assumes args has name value pairs alternating, name0 at 0, val0 at 1, name1 at 2, val2 at 3, etc.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attrs
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|xml
operator|.
name|writeAttribute
argument_list|(
name|attrs
index|[
name|i
index|]
argument_list|,
name|attrs
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|StringWriter
name|stackWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|stackWriter
argument_list|)
decl_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|printWriter
argument_list|)
expr_stmt|;
name|xml
operator|.
name|writeCharacters
argument_list|(
name|stackWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|xml
operator|.
name|writeEndElement
argument_list|()
expr_stmt|;
name|xml
operator|.
name|writeEndDocument
argument_list|()
expr_stmt|;
name|xml
operator|.
name|flush
argument_list|()
expr_stmt|;
name|xml
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"error writing xml stream for: "
operator|+
name|resourceId
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|level
operator|.
name|toInt
argument_list|()
condition|)
block|{
case|case
name|Level
operator|.
name|FATAL_INT
case|:
name|logger
operator|.
name|error
argument_list|(
name|MarkerFactory
operator|.
name|getMarker
argument_list|(
literal|"FATAL"
argument_list|)
argument_list|,
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|ERROR_INT
case|:
name|logger
operator|.
name|error
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|WARN_INT
case|:
name|logger
operator|.
name|warn
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|DEBUG_INT
case|:
name|logger
operator|.
name|debug
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Level
operator|.
name|TRACE_INT
case|:
name|logger
operator|.
name|trace
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
empty_stmt|;
block|}
specifier|private
name|FileResource
name|getNextFileResource
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|FileResource
name|fileResource
init|=
literal|null
decl_stmt|;
name|long
name|start
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
while|while
condition|(
name|fileResource
operator|==
literal|null
condition|)
block|{
comment|//check to see if thread is interrupted before polling
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
name|setEndedState
argument_list|(
name|STATE
operator|.
name|THREAD_INTERRUPTED
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Consumer thread was interrupted."
argument_list|)
expr_stmt|;
break|break;
block|}
synchronized|synchronized
init|(
name|lock
init|)
block|{
comment|//need to lock here to prevent race condition with other threads setting state
if|if
condition|(
name|currentState
operator|!=
name|STATE
operator|.
name|ACTIVELY_CONSUMING
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Consumer already closed because of: "
operator|+
name|currentState
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|fileResource
operator|=
name|fileQueue
operator|.
name|poll
argument_list|(
literal|1L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileResource
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fileResource
operator|instanceof
name|PoisonFileResource
condition|)
block|{
name|setEndedState
argument_list|(
name|STATE
operator|.
name|SWALLOWED_POISON
argument_list|)
expr_stmt|;
name|fileResource
operator|=
literal|null
expr_stmt|;
block|}
break|break;
block|}
name|logger
operator|.
name|debug
argument_list|(
name|consumerId
operator|+
literal|" is waiting for file and the queue size is: "
operator|+
name|fileQueue
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|elapsed
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|maxConsecWaitInMillis
operator|>
literal|0
operator|&&
name|elapsed
operator|>
name|maxConsecWaitInMillis
condition|)
block|{
name|setEndedState
argument_list|(
name|STATE
operator|.
name|EXCEEDED_MAX_CONSEC_WAIT_MILLIS
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|fileResource
return|;
block|}
specifier|protected
name|void
name|close
parameter_list|(
name|Closeable
name|closeable
parameter_list|)
block|{
if|if
condition|(
name|closeable
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|closeable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|closeable
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|void
name|flushAndClose
parameter_list|(
name|Closeable
name|closeable
parameter_list|)
block|{
if|if
condition|(
name|closeable
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|closeable
operator|instanceof
name|Flushable
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|Flushable
operator|)
name|closeable
operator|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|close
argument_list|(
name|closeable
argument_list|)
expr_stmt|;
block|}
comment|//do not overwrite a finished state except if
comment|//not yet started, actively consuming or shutting down.  This should
comment|//represent the initial cause; all subsequent calls
comment|//to set will be ignored!!!
specifier|private
name|void
name|setEndedState
parameter_list|(
name|STATE
name|cause
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
name|currentState
operator|==
name|STATE
operator|.
name|NOT_YET_STARTED
operator|||
name|currentState
operator|==
name|STATE
operator|.
name|ACTIVELY_CONSUMING
operator|||
name|currentState
operator|==
name|STATE
operator|.
name|ASKED_TO_SHUTDOWN
condition|)
block|{
name|currentState
operator|=
name|cause
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

