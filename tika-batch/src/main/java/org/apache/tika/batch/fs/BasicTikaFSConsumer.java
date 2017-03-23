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
name|io
operator|.
name|UnsupportedEncodingException
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
name|batch
operator|.
name|ParserFactory
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
name|config
operator|.
name|TikaConfig
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|ContentHandlerFactory
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

begin_comment
comment|/**  * Basic FileResourceConsumer that reads files from an input  * directory and writes content to the output directory.  *<p>  * This catches all exceptions and errors and then logs them.  * This will re-throw errors.  *  */
end_comment

begin_class
specifier|public
class|class
name|BasicTikaFSConsumer
extends|extends
name|AbstractFSConsumer
block|{
specifier|private
name|boolean
name|parseRecursively
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
name|ParserFactory
name|parserFactory
decl_stmt|;
specifier|private
specifier|final
name|ContentHandlerFactory
name|contentHandlerFactory
decl_stmt|;
specifier|private
specifier|final
name|OutputStreamFactory
name|fsOSFactory
decl_stmt|;
specifier|private
specifier|final
name|TikaConfig
name|config
decl_stmt|;
specifier|private
name|String
name|outputEncoding
init|=
name|UTF_8
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|public
name|BasicTikaFSConsumer
parameter_list|(
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|queue
parameter_list|,
name|ParserFactory
name|parserFactory
parameter_list|,
name|ContentHandlerFactory
name|contentHandlerFactory
parameter_list|,
name|OutputStreamFactory
name|fsOSFactory
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|this
operator|.
name|parserFactory
operator|=
name|parserFactory
expr_stmt|;
name|this
operator|.
name|contentHandlerFactory
operator|=
name|contentHandlerFactory
expr_stmt|;
name|this
operator|.
name|fsOSFactory
operator|=
name|fsOSFactory
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|processFileResource
parameter_list|(
name|FileResource
name|fileResource
parameter_list|)
block|{
name|Parser
name|parser
init|=
name|parserFactory
operator|.
name|getParser
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|parseRecursively
condition|)
block|{
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
name|OutputStream
name|os
init|=
name|getOutputStream
argument_list|(
name|fsOSFactory
argument_list|,
name|fileResource
argument_list|)
decl_stmt|;
comment|//os can be null if fsOSFactory is set to skip processing a file if the output
comment|//file already exists
if|if
condition|(
name|os
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping: {}"
argument_list|,
name|fileResource
operator|.
name|getMetadata
argument_list|()
operator|.
name|get
argument_list|(
name|FSProperties
operator|.
name|FS_REL_PATH
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|InputStream
name|is
init|=
name|getInputStream
argument_list|(
name|fileResource
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|os
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|ContentHandler
name|handler
decl_stmt|;
try|try
block|{
name|handler
operator|=
name|contentHandlerFactory
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|getOutputEncoding
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|incrementHandledExceptions
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|getXMLifiedLogMsg
argument_list|(
literal|"output_encoding_ex"
argument_list|,
name|fileResource
operator|.
name|getResourceId
argument_list|()
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|flushAndClose
argument_list|(
name|os
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|//now actually call parse!
name|Throwable
name|thrown
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parse
argument_list|(
name|fileResource
operator|.
name|getResourceId
argument_list|()
argument_list|,
name|parser
argument_list|,
name|is
argument_list|,
name|handler
argument_list|,
name|fileResource
operator|.
name|getMetadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|t
parameter_list|)
block|{
throw|throw
name|t
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|thrown
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
name|flushAndClose
argument_list|(
name|os
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
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|getOutputEncoding
parameter_list|()
block|{
return|return
name|outputEncoding
return|;
block|}
specifier|public
name|void
name|setOutputEncoding
parameter_list|(
name|String
name|outputEncoding
parameter_list|)
block|{
name|this
operator|.
name|outputEncoding
operator|=
name|outputEncoding
expr_stmt|;
block|}
block|}
end_class

end_unit

