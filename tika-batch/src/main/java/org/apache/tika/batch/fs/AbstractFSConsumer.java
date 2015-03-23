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
name|OutputStream
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
name|tika
operator|.
name|batch
operator|.
name|BatchNoRestartError
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
name|OutputStreamFactory
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
name|metadata
operator|.
name|Metadata
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
name|parser
operator|.
name|ParseContext
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
name|parser
operator|.
name|Parser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractFSConsumer
extends|extends
name|FileResourceConsumer
block|{
specifier|public
name|AbstractFSConsumer
parameter_list|(
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|fileQueue
parameter_list|)
block|{
name|super
argument_list|(
name|fileQueue
argument_list|)
expr_stmt|;
block|}
comment|/**      * Use this for consistent logging of exceptions.  Clients must      * check for whether the os is null, which is the signal      * that the output file already exists and should be skipped.      *      * @param fsOSFactory factory that creates the outputstream      * @param fileResource used by the OSFactory to create the stream      * @return the OutputStream or null if the output file already exists      */
specifier|protected
name|OutputStream
name|getOutputStream
parameter_list|(
name|OutputStreamFactory
name|fsOSFactory
parameter_list|,
name|FileResource
name|fileResource
parameter_list|)
block|{
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|os
operator|=
name|fsOSFactory
operator|.
name|getOutputStream
argument_list|(
name|fileResource
operator|.
name|getMetadata
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//This can happen if the disk has run out of space,
comment|//or if there was a failure with mkdirs in fsOSFactory
name|logWithResourceId
argument_list|(
name|Level
operator|.
name|FATAL
argument_list|,
literal|"ioe_opening_os"
argument_list|,
name|fileResource
operator|.
name|getResourceId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BatchNoRestartError
argument_list|(
literal|"IOException trying to open output stream for "
operator|+
name|fileResource
operator|.
name|getResourceId
argument_list|()
operator|+
literal|" :: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|os
return|;
block|}
specifier|protected
name|InputStream
name|getInputStream
parameter_list|(
name|FileResource
name|fileResource
parameter_list|)
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|fileResource
operator|.
name|openInputStream
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logWithResourceId
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"ioe_opening_is"
argument_list|,
name|fileResource
operator|.
name|getResourceId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|flushAndClose
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
return|return
name|is
return|;
block|}
specifier|protected
name|void
name|parse
parameter_list|(
specifier|final
name|String
name|resourceId
parameter_list|,
specifier|final
name|Parser
name|parser
parameter_list|,
name|InputStream
name|is
parameter_list|,
specifier|final
name|ContentHandler
name|handler
parameter_list|,
specifier|final
name|Metadata
name|m
parameter_list|,
specifier|final
name|ParseContext
name|parseContext
parameter_list|)
throws|throws
name|Throwable
block|{
name|Throwable
name|thrown
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|m
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
name|logWithResourceId
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"oom"
argument_list|,
name|resourceId
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|Error
condition|)
block|{
name|logWithResourceId
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"parse_err"
argument_list|,
name|resourceId
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logWithResourceId
argument_list|(
name|Level
operator|.
name|ERROR
argument_list|,
literal|"parse_ex"
argument_list|,
name|resourceId
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|incrementHandledExceptions
argument_list|()
expr_stmt|;
block|}
name|thrown
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|close
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|thrown
operator|!=
literal|null
condition|)
block|{
throw|throw
name|thrown
throw|;
block|}
block|}
block|}
end_class

end_unit

