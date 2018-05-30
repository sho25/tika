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
name|AbstractRecursiveParserWrapperHandler
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
name|int
name|type
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|RecursiveMetadataContentHandlerProxy
operator|.
name|EMBEDDED_DOCUMENT
condition|)
block|{
name|Metadata
name|metadata
init|=
literal|null
decl_stmt|;
try|try
block|{
name|metadata
operator|=
operator|(
name|Metadata
operator|)
name|ForkObjectInputStream
operator|.
name|readObject
argument_list|(
name|input
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
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
name|handler
operator|.
name|endEmbeddedDocument
argument_list|(
name|DEFAULT_HANDLER
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|RecursiveMetadataContentHandlerProxy
operator|.
name|MAIN_DOCUMENT
condition|)
block|{
name|Metadata
name|metadata
init|=
literal|null
decl_stmt|;
try|try
block|{
name|metadata
operator|=
operator|(
name|Metadata
operator|)
name|ForkObjectInputStream
operator|.
name|readObject
argument_list|(
name|input
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
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
name|handler
operator|.
name|endDocument
argument_list|(
name|DEFAULT_HANDLER
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
literal|"I regret that I don't understand: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Metadata
name|deserializeMetadata
parameter_list|(
name|DataInputStream
name|dataInputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|dataInputStream
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|dataInputStream
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|ObjectInputStream
name|ois
init|=
operator|new
name|ObjectInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|obj
init|=
literal|null
decl_stmt|;
try|try
block|{
name|obj
operator|=
name|ois
operator|.
name|readObject
argument_list|()
expr_stmt|;
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
return|return
operator|(
name|Metadata
operator|)
name|obj
return|;
block|}
specifier|private
name|ContentHandler
name|deserializeContentHandler
parameter_list|(
name|DataInputStream
name|dataInputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|dataInputStream
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|dataInputStream
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|ObjectInputStream
name|ois
init|=
operator|new
name|ObjectInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|obj
init|=
literal|null
decl_stmt|;
try|try
block|{
name|obj
operator|=
name|ois
operator|.
name|readObject
argument_list|()
expr_stmt|;
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
return|return
operator|(
name|ContentHandler
operator|)
name|obj
return|;
block|}
block|}
end_class

end_unit

