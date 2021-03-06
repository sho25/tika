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
name|fork
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
name|RecursiveParserWrapperHandler
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
class|class
name|RecursiveMetadataContentHandlerResource
implements|implements
name|ForkResource
block|{
specifier|private
specifier|static
specifier|final
name|ContentHandler
name|DEFAULT_HANDLER
init|=
operator|new
name|DefaultHandler
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AbstractRecursiveParserWrapperHandler
name|handler
decl_stmt|;
specifier|public
name|RecursiveMetadataContentHandlerResource
parameter_list|(
name|RecursiveParserWrapperHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|Throwable
name|process
parameter_list|(
name|DataInputStream
name|input
parameter_list|,
name|DataOutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|internalProcess
argument_list|(
name|input
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
return|return
name|e
return|;
block|}
block|}
specifier|private
name|void
name|internalProcess
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|byte
name|embeddedOrMain
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|byte
name|handlerAndMetadataOrMetadataOnly
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|ContentHandler
name|localContentHandler
init|=
name|DEFAULT_HANDLER
decl_stmt|;
if|if
condition|(
name|handlerAndMetadataOrMetadataOnly
operator|==
name|RecursiveMetadataContentHandlerProxy
operator|.
name|HANDLER_AND_METADATA
condition|)
block|{
name|localContentHandler
operator|=
operator|(
name|ContentHandler
operator|)
name|readObject
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|handlerAndMetadataOrMetadataOnly
operator|!=
name|RecursiveMetadataContentHandlerProxy
operator|.
name|METADATA_ONLY
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Expected HANDLER_AND_METADATA or METADATA_ONLY, but got:"
operator|+
name|handlerAndMetadataOrMetadataOnly
argument_list|)
throw|;
block|}
name|Metadata
name|metadata
init|=
operator|(
name|Metadata
operator|)
name|readObject
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|embeddedOrMain
operator|==
name|RecursiveMetadataContentHandlerProxy
operator|.
name|EMBEDDED_DOCUMENT
condition|)
block|{
name|handler
operator|.
name|endEmbeddedDocument
argument_list|(
name|localContentHandler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|embeddedOrMain
operator|==
name|RecursiveMetadataContentHandlerProxy
operator|.
name|MAIN_DOCUMENT
condition|)
block|{
name|handler
operator|.
name|endDocument
argument_list|(
name|localContentHandler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Expected either 0x01 or 0x02, but got: "
operator|+
name|embeddedOrMain
argument_list|)
throw|;
block|}
name|byte
name|isComplete
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|isComplete
operator|!=
name|RecursiveMetadataContentHandlerProxy
operator|.
name|COMPLETE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expected the 'complete' signal, but got: "
operator|+
name|isComplete
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Object
name|readObject
parameter_list|(
name|DataInputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|ForkObjectInputStream
operator|.
name|readObject
argument_list|(
name|inputStream
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

