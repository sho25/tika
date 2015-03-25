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
operator|.
name|fs
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|OutputStreamWriter
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|CommandLineParser
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|HelpFormatter
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
name|cli
operator|.
name|Option
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
name|cli
operator|.
name|Options
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
name|BasicConfigurator
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
name|ConsoleAppender
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
name|apache
operator|.
name|log4j
operator|.
name|PatternLayout
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
name|BatchProcess
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
name|BatchProcessDriverCLI
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
name|ParallelFileProcessingResult
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
name|batch
operator|.
name|builders
operator|.
name|CommandLineParserBuilder
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
name|io
operator|.
name|IOUtils
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
name|io
operator|.
name|TikaInputStream
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

begin_class
specifier|public
class|class
name|FSBatchProcessCLI
block|{
specifier|public
specifier|static
name|String
name|FINISHED_STRING
init|=
literal|"Main thread in TikaFSBatchCLI has finished processing."
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
name|FSBatchProcessCLI
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Options
name|options
decl_stmt|;
specifier|public
name|FSBatchProcessCLI
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|TikaInputStream
name|configIs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|configIs
operator|=
name|getConfigInputStream
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|CommandLineParserBuilder
name|builder
init|=
operator|new
name|CommandLineParserBuilder
argument_list|()
decl_stmt|;
name|options
operator|=
name|builder
operator|.
name|build
argument_list|(
name|configIs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|configIs
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|usage
parameter_list|()
block|{
name|HelpFormatter
name|helpFormatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|helpFormatter
operator|.
name|printHelp
argument_list|(
literal|"tika filesystem batch"
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TikaInputStream
name|getConfigInputStream
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|TikaInputStream
name|is
init|=
literal|null
decl_stmt|;
name|File
name|batchConfigFile
init|=
name|getConfigFile
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|batchConfigFile
operator|!=
literal|null
condition|)
block|{
comment|//this will throw IOException if it can't find a specified config file
comment|//better to throw an exception than silently back off to default.
name|is
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|batchConfigFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"No config file set via -bc, relying on default-tika-batch-config.xml"
argument_list|)
expr_stmt|;
name|is
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|FSBatchProcessCLI
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"default-tika-batch-config.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|is
return|;
block|}
specifier|private
name|void
name|execute
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|CommandLineParser
name|cliParser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
name|CommandLine
name|line
init|=
name|cliParser
operator|.
name|parse
argument_list|(
name|options
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"help"
argument_list|)
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|BatchProcessDriverCLI
operator|.
name|PROCESS_NO_RESTART_EXIT_CODE
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapArgs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Option
name|option
range|:
name|line
operator|.
name|getOptions
argument_list|()
control|)
block|{
name|String
name|v
init|=
name|option
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
operator|||
name|v
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|v
operator|=
literal|"true"
expr_stmt|;
block|}
name|mapArgs
operator|.
name|put
argument_list|(
name|option
operator|.
name|getOpt
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|BatchProcessBuilder
name|b
init|=
operator|new
name|BatchProcessBuilder
argument_list|()
decl_stmt|;
name|TikaInputStream
name|is
init|=
literal|null
decl_stmt|;
name|BatchProcess
name|process
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|getConfigInputStream
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|process
operator|=
name|b
operator|.
name|build
argument_list|(
name|is
argument_list|,
name|mapArgs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Thread
name|mainThread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|ParallelFileProcessingResult
argument_list|>
name|futureResult
init|=
name|executor
operator|.
name|submit
argument_list|(
name|process
argument_list|)
decl_stmt|;
name|ParallelFileProcessingResult
name|result
init|=
name|futureResult
operator|.
name|get
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|FINISHED_STRING
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|result
operator|.
name|getExitStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|File
name|getConfigFile
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|File
name|configFile
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
literal|"-bc"
argument_list|)
operator|||
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-batch-config"
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|<
name|args
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|configFile
operator|=
operator|new
name|File
argument_list|(
name|args
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|configFile
return|;
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
comment|//if no log4j config file has been set via
comment|//sysprops, use BasicConfigurator
comment|//TODO: figure out if this can cleanly be moved to pure slf4j?
name|String
name|log4jFile
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"log4j.configuration"
argument_list|)
decl_stmt|;
if|if
condition|(
name|log4jFile
operator|==
literal|null
operator|||
name|log4jFile
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|ConsoleAppender
name|appender
init|=
operator|new
name|ConsoleAppender
argument_list|()
decl_stmt|;
name|appender
operator|.
name|setLayout
argument_list|(
operator|new
name|PatternLayout
argument_list|(
literal|"%m%n"
argument_list|)
argument_list|)
expr_stmt|;
name|appender
operator|.
name|setWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|BasicConfigurator
operator|.
name|configure
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|FSBatchProcessCLI
name|cli
init|=
operator|new
name|FSBatchProcessCLI
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|cli
operator|.
name|execute
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
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
literal|"Fatal exception from FSBatchProcessCLI: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|BatchProcessDriverCLI
operator|.
name|PROCESS_NO_RESTART_EXIT_CODE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

