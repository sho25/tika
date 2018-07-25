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
name|batch
operator|.
name|fs
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
name|metadata
operator|.
name|TikaCoreProperties
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
name|serialization
operator|.
name|JsonStreamingSerializer
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
name|parser
operator|.
name|RecursiveParserWrapper
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
name|AbstractRecursiveParserWrapperHandler
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|RecursiveParserWrapperHandler
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
name|utils
operator|.
name|ExceptionUtils
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
import|;
end_import

begin_comment
comment|/**  * This uses the {@link JsonStreamingSerializer} to write out a  * single metadata object at a time.  */
end_comment

begin_class
specifier|public
class|class
name|StreamOutRPWFSConsumer
extends|extends
name|AbstractFSConsumer
block|{
specifier|private
specifier|final
name|Parser
name|parser
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
name|String
name|outputEncoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|public
name|StreamOutRPWFSConsumer
parameter_list|(
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|queue
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|ContentHandlerFactory
name|contentHandlerFactory
parameter_list|,
name|OutputStreamFactory
name|fsOSFactory
parameter_list|)
block|{
name|super
argument_list|(
name|queue
argument_list|)
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
name|parser
operator|=
name|parser
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
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
comment|//try to open outputstream first
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
comment|//try to open the inputstream before the parse.
comment|//if the parse hangs or throws a nasty exception, at least there will
comment|//be a zero byte file there so that the batchrunner can skip that problematic
comment|//file during the next run.
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
name|Metadata
name|containerMetadata
init|=
name|fileResource
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
name|JsonStreamingSerializer
name|writer
init|=
operator|new
name|JsonStreamingSerializer
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|WriteoutRPWHandler
name|handler
init|=
operator|new
name|WriteoutRPWHandler
argument_list|(
name|contentHandlerFactory
argument_list|,
name|writer
argument_list|)
decl_stmt|;
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
name|containerMetadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
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
try|try
block|{
name|writer
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
comment|//this is a stop the world kind of thing
name|LOG
operator|.
name|error
argument_list|(
literal|"{}"
argument_list|,
name|getXMLifiedLogMsg
argument_list|(
name|IO_OS
operator|+
literal|"json"
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
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
block|}
if|if
condition|(
name|thrown
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|thrown
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|thrown
throw|;
block|}
elseif|else
if|if
condition|(
name|thrown
operator|instanceof
name|SecurityException
condition|)
block|{
throw|throw
operator|(
name|SecurityException
operator|)
name|thrown
throw|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
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
comment|//extend AbstractRPWH instead of RecursiveParserWrapperHandler so that
comment|//if we use the ForkParser, the output will not have to be streamed
comment|//back to the proxy, but can
comment|//be written straight to disk.
specifier|private
class|class
name|WriteoutRPWHandler
extends|extends
name|AbstractRecursiveParserWrapperHandler
block|{
specifier|private
specifier|final
name|JsonStreamingSerializer
name|jsonWriter
decl_stmt|;
specifier|public
name|WriteoutRPWHandler
parameter_list|(
name|ContentHandlerFactory
name|contentHandlerFactory
parameter_list|,
name|JsonStreamingSerializer
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|contentHandlerFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|jsonWriter
operator|=
name|writer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endEmbeddedDocument
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SAXException
block|{
name|metadata
operator|.
name|add
argument_list|(
name|RecursiveParserWrapperHandler
operator|.
name|TIKA_CONTENT
argument_list|,
name|contentHandler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|jsonWriter
operator|.
name|add
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SAXException
block|{
name|endEmbeddedDocument
argument_list|(
name|contentHandler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
