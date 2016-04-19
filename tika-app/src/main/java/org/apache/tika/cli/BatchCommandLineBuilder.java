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
name|cli
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
name|LinkedHashMap
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * This takes a TikaCLI commandline and builds the full commandline for  * org.apache.tika.batch.fs.FSBatchProcessCLI.  *<p>  * The "default" batch config file that this relies on  * if no batch config file is specified on the commandline  * is: tika-batch/src/main/resources/.../default-tika-batch-config.xml  */
end_comment

begin_class
class|class
name|BatchCommandLineBuilder
block|{
specifier|static
name|Pattern
name|JVM_OPTS_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(--?)J(.+)"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|String
index|[]
name|build
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|processArgs
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jvmOpts
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//take the args, and divide them into process args and options for
comment|//the child jvm process (i.e. log files, etc)
name|mapifyArgs
argument_list|(
name|args
argument_list|,
name|processArgs
argument_list|,
name|jvmOpts
argument_list|)
expr_stmt|;
comment|//now modify processArgs in place
name|translateCommandLine
argument_list|(
name|args
argument_list|,
name|processArgs
argument_list|)
expr_stmt|;
comment|//maybe the user specified a different classpath?!
if|if
condition|(
operator|!
name|jvmOpts
operator|.
name|containsKey
argument_list|(
literal|"-cp"
argument_list|)
operator|&&
operator|!
name|jvmOpts
operator|.
name|containsKey
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
comment|//need to test for " " on *nix, can't just add double quotes
comment|//across platforms.
if|if
condition|(
name|cp
operator|.
name|contains
argument_list|(
literal|" "
argument_list|)
condition|)
block|{
name|cp
operator|=
literal|"\""
operator|+
name|cp
operator|+
literal|"\""
expr_stmt|;
block|}
name|jvmOpts
operator|.
name|put
argument_list|(
literal|"-cp"
argument_list|,
name|cp
argument_list|)
expr_stmt|;
block|}
name|boolean
name|hasLog4j
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|k
range|:
name|jvmOpts
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|k
operator|.
name|startsWith
argument_list|(
literal|"-Dlog4j.configuration="
argument_list|)
condition|)
block|{
name|hasLog4j
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|//use the log4j config file inside the app /resources/log4j_batch_process.properties
if|if
condition|(
operator|!
name|hasLog4j
condition|)
block|{
name|jvmOpts
operator|.
name|put
argument_list|(
literal|"-Dlog4j.configuration=\"log4j_batch_process.properties\""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|//now build the full command line
name|List
argument_list|<
name|String
argument_list|>
name|fullCommand
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|fullCommand
operator|.
name|add
argument_list|(
literal|"java"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|jvmOpts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|fullCommand
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|fullCommand
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|fullCommand
operator|.
name|add
argument_list|(
literal|"org.apache.tika.batch.fs.FSBatchProcessCLI"
argument_list|)
expr_stmt|;
comment|//now add the process commands
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|processArgs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|fullCommand
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|fullCommand
operator|.
name|add
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fullCommand
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fullCommand
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Take the input args and separate them into args that belong on the commandline      * and those that belong as jvm args for the child process.      * @param args -- literal args from TikaCLI commandline      * @param commandLine args that should be part of the batch commandline      * @param jvmArgs args that belong as jvm arguments for the child process      */
specifier|private
specifier|static
name|void
name|mapifyArgs
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commandLine
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jvmArgs
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|Matcher
name|matcher
init|=
name|JVM_OPTS_PATTERN
operator|.
name|matcher
argument_list|(
literal|""
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
name|matcher
operator|.
name|reset
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|jvmArg
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|+
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|v
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|args
operator|.
name|length
operator|-
literal|1
operator|&&
operator|!
name|args
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|v
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|jvmArgs
operator|.
name|put
argument_list|(
name|jvmArg
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|String
name|k
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
name|String
name|v
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|args
operator|.
name|length
operator|-
literal|1
operator|&&
operator|!
name|args
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|v
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|commandLine
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|translateCommandLine
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
comment|//if there are only two args and they are both directories, treat the first
comment|//as input and the second as output.
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
operator|&&
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
operator|&&
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|Path
name|candInput
init|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Path
name|candOutput
init|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isRegularFile
argument_list|(
name|candOutput
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't specify an existing file as the "
operator|+
literal|"second argument for the output directory of a batch process"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|candInput
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"-inputDir"
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-outputDir"
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|//look for tikaConfig
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"--config="
argument_list|)
condition|)
block|{
name|String
name|configPath
init|=
name|arg
operator|.
name|substring
argument_list|(
literal|"--config="
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-c"
argument_list|,
name|configPath
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|//now translate output types
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"-h"
argument_list|)
operator|||
name|map
operator|.
name|containsKey
argument_list|(
literal|"--html"
argument_list|)
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
literal|"-h"
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|"--html"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-basicHandlerType"
argument_list|,
literal|"html"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"-x"
argument_list|)
operator|||
name|map
operator|.
name|containsKey
argument_list|(
literal|"--xml"
argument_list|)
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
literal|"-x"
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|"--xml"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-basicHandlerType"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"-t"
argument_list|)
operator|||
name|map
operator|.
name|containsKey
argument_list|(
literal|"--text"
argument_list|)
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
literal|"-t"
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|"--text"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-basicHandlerType"
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"-m"
argument_list|)
operator|||
name|map
operator|.
name|containsKey
argument_list|(
literal|"--metadata"
argument_list|)
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
literal|"-m"
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|"--metadata"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-basicHandlerType"
argument_list|,
literal|"ignore"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"-T"
argument_list|)
operator|||
name|map
operator|.
name|containsKey
argument_list|(
literal|"--text-main"
argument_list|)
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
literal|"-T"
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|"--text-main"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-basicHandlerType"
argument_list|,
literal|"body"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"-J"
argument_list|)
operator|||
name|map
operator|.
name|containsKey
argument_list|(
literal|"--jsonRecursive"
argument_list|)
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
literal|"-J"
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
literal|"--jsonRecursive"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-recursiveParserWrapper"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"--inputDir"
argument_list|)
operator|||
name|map
operator|.
name|containsKey
argument_list|(
literal|"-i"
argument_list|)
condition|)
block|{
name|String
name|v1
init|=
name|map
operator|.
name|remove
argument_list|(
literal|"--inputDir"
argument_list|)
decl_stmt|;
name|String
name|v2
init|=
name|map
operator|.
name|remove
argument_list|(
literal|"-i"
argument_list|)
decl_stmt|;
name|String
name|v
init|=
operator|(
name|v1
operator|==
literal|null
operator|)
condition|?
name|v2
else|:
name|v1
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-inputDir"
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"--outputDir"
argument_list|)
operator|||
name|map
operator|.
name|containsKey
argument_list|(
literal|"-o"
argument_list|)
condition|)
block|{
name|String
name|v1
init|=
name|map
operator|.
name|remove
argument_list|(
literal|"--outputDir"
argument_list|)
decl_stmt|;
name|String
name|v2
init|=
name|map
operator|.
name|remove
argument_list|(
literal|"-o"
argument_list|)
decl_stmt|;
name|String
name|v
init|=
operator|(
name|v1
operator|==
literal|null
operator|)
condition|?
name|v2
else|:
name|v1
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"-outputDir"
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

