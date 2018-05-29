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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|NotSerializableException
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
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
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
name|exception
operator|.
name|TikaException
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
name|utils
operator|.
name|ProcessUtils
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
class|class
name|ForkClient
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|ForkResource
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ClassLoader
name|loader
decl_stmt|;
specifier|private
specifier|final
name|File
name|jar
decl_stmt|;
specifier|private
specifier|final
name|Process
name|process
decl_stmt|;
specifier|private
specifier|final
name|DataOutputStream
name|output
decl_stmt|;
specifier|private
specifier|final
name|DataInputStream
name|input
decl_stmt|;
specifier|private
specifier|final
name|InputStream
name|error
decl_stmt|;
specifier|public
name|ForkClient
parameter_list|(
name|Path
name|tikaDir
parameter_list|,
name|ParserFactoryFactory
name|parserFactoryFactory
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|java
parameter_list|,
name|long
name|serverPulseMillis
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|jar
operator|=
literal|null
expr_stmt|;
name|loader
operator|=
literal|null
expr_stmt|;
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|command
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|command
operator|.
name|addAll
argument_list|(
name|java
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"-cp"
argument_list|)
expr_stmt|;
name|String
name|dirString
init|=
name|tikaDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dirString
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|dirString
operator|+=
literal|"/*"
expr_stmt|;
block|}
else|else
block|{
name|dirString
operator|+=
literal|"/"
expr_stmt|;
block|}
name|dirString
operator|=
name|ProcessUtils
operator|.
name|escapeCommandLine
argument_list|(
name|dirString
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|dirString
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"org.apache.tika.fork.ForkServer"
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|serverPulseMillis
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|command
argument_list|(
name|command
argument_list|)
expr_stmt|;
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
try|try
block|{
name|this
operator|.
name|process
operator|=
name|builder
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|output
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
name|this
operator|.
name|input
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
name|error
operator|=
name|process
operator|.
name|getErrorStream
argument_list|()
expr_stmt|;
name|waitForStartBeacon
argument_list|()
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|ForkServer
operator|.
name|INIT_PARSER_FACTORY_FACTORY
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
name|sendObject
argument_list|(
name|parserFactoryFactory
argument_list|,
name|resources
argument_list|)
expr_stmt|;
name|waitForStartBeacon
argument_list|()
expr_stmt|;
name|ok
operator|=
literal|true
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
throw|throw
name|t
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      *      * @param tikaDir directory containing jars from which to start the child server and load the Parser      * @param parserFactoryFactory factory to send to child process to build parser upon arrival      * @param classLoader class loader to use for non-parser resource (content-handler, etc.)      * @param java java commandline to use for the commandline server      * @param serverPulseMillis how often to check if the server has been active      * @throws IOException      * @throws TikaException      */
specifier|public
name|ForkClient
parameter_list|(
name|Path
name|tikaDir
parameter_list|,
name|ParserFactoryFactory
name|parserFactoryFactory
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|java
parameter_list|,
name|long
name|serverPulseMillis
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|jar
operator|=
literal|null
expr_stmt|;
name|loader
operator|=
literal|null
expr_stmt|;
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|command
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|command
operator|.
name|addAll
argument_list|(
name|java
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"-cp"
argument_list|)
expr_stmt|;
name|String
name|dirString
init|=
name|tikaDir
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dirString
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|dirString
operator|+=
literal|"/*"
expr_stmt|;
block|}
else|else
block|{
name|dirString
operator|+=
literal|"/"
expr_stmt|;
block|}
name|dirString
operator|=
name|ProcessUtils
operator|.
name|escapeCommandLine
argument_list|(
name|dirString
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|dirString
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"org.apache.tika.fork.ForkServer"
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|serverPulseMillis
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|command
argument_list|(
name|command
argument_list|)
expr_stmt|;
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
try|try
block|{
name|this
operator|.
name|process
operator|=
name|builder
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|output
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
name|this
operator|.
name|input
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
name|error
operator|=
name|process
operator|.
name|getErrorStream
argument_list|()
expr_stmt|;
name|waitForStartBeacon
argument_list|()
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|ForkServer
operator|.
name|INIT_PARSER_FACTORY_FACTORY_LOADER
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
name|sendObject
argument_list|(
name|parserFactoryFactory
argument_list|,
name|resources
argument_list|)
expr_stmt|;
name|sendObject
argument_list|(
name|classLoader
argument_list|,
name|resources
argument_list|)
expr_stmt|;
name|waitForStartBeacon
argument_list|()
expr_stmt|;
name|ok
operator|=
literal|true
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
throw|throw
name|t
throw|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|ForkClient
parameter_list|(
name|ClassLoader
name|loader
parameter_list|,
name|Object
name|object
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|java
parameter_list|,
name|long
name|serverPulseMillis
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|jar
operator|=
name|createBootstrapJar
argument_list|()
expr_stmt|;
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|command
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|command
operator|.
name|addAll
argument_list|(
name|java
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"-jar"
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|jar
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|serverPulseMillis
argument_list|)
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|command
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|this
operator|.
name|process
operator|=
name|builder
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|output
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
name|this
operator|.
name|input
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
name|error
operator|=
name|process
operator|.
name|getErrorStream
argument_list|()
expr_stmt|;
name|waitForStartBeacon
argument_list|()
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|ForkServer
operator|.
name|INIT_LOADER_PARSER
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
name|sendObject
argument_list|(
name|loader
argument_list|,
name|resources
argument_list|)
expr_stmt|;
name|sendObject
argument_list|(
name|object
argument_list|,
name|resources
argument_list|)
expr_stmt|;
name|waitForStartBeacon
argument_list|()
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|waitForStartBeacon
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|type
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|byte
operator|)
name|type
operator|==
name|ForkServer
operator|.
name|READY
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
operator|(
name|byte
operator|)
name|type
operator|==
name|ForkServer
operator|.
name|FAILED_TO_START
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Server had a catastrophic initialization failure"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"EOF while waiting for start beacon"
argument_list|)
throw|;
block|}
else|else
block|{
comment|//can't do this because of ForkParserIntegrationTest#testAttachingADebuggerOnTheForkedParserShouldWork
comment|//                throw new IOException("Unexpected byte while waiting for start beacon: "+type);
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|boolean
name|ping
parameter_list|()
block|{
try|try
block|{
name|output
operator|.
name|writeByte
argument_list|(
name|ForkServer
operator|.
name|PING
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|type
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|ForkServer
operator|.
name|PING
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
specifier|synchronized
name|Throwable
name|call
parameter_list|(
name|String
name|method
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|List
argument_list|<
name|ForkResource
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|resources
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|ForkServer
operator|.
name|CALL
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeUTF
argument_list|(
name|method
argument_list|)
expr_stmt|;
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
name|sendObject
argument_list|(
name|args
index|[
name|i
index|]
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|waitForResponse
argument_list|(
name|r
argument_list|)
return|;
block|}
comment|/**      * Serializes the object first into an in-memory buffer and then      * writes it to the output stream with a preceding size integer.      *      * @param object object to be serialized      * @param resources list of fork resources, used when adding proxies      * @throws IOException if the object could not be serialized      */
specifier|private
name|void
name|sendObject
parameter_list|(
name|Object
name|object
parameter_list|,
name|List
argument_list|<
name|ForkResource
argument_list|>
name|resources
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|int
name|n
init|=
name|resources
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|object
operator|instanceof
name|InputStream
condition|)
block|{
name|resources
operator|.
name|add
argument_list|(
operator|new
name|InputStreamResource
argument_list|(
operator|(
name|InputStream
operator|)
name|object
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
operator|new
name|InputStreamProxy
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|ContentHandler
condition|)
block|{
name|resources
operator|.
name|add
argument_list|(
operator|new
name|ContentHandlerResource
argument_list|(
operator|(
name|ContentHandler
operator|)
name|object
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
operator|new
name|ContentHandlerProxy
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|ClassLoader
condition|)
block|{
name|resources
operator|.
name|add
argument_list|(
operator|new
name|ClassLoaderResource
argument_list|(
operator|(
name|ClassLoader
operator|)
name|object
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
operator|new
name|ClassLoaderProxy
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|ForkObjectInputStream
operator|.
name|sendObject
argument_list|(
name|object
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotSerializableException
name|nse
parameter_list|)
block|{
comment|// Build a more friendly error message for this
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to serialize "
operator|+
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" to pass to the Forked Parser"
argument_list|,
name|nse
argument_list|)
throw|;
block|}
name|waitForResponse
argument_list|(
name|resources
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|error
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{         }
if|if
condition|(
name|process
operator|!=
literal|null
condition|)
block|{
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
try|try
block|{
comment|//TIKA-1933
name|process
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{              }
block|}
if|if
condition|(
name|jar
operator|!=
literal|null
condition|)
block|{
name|jar
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Throwable
name|waitForResponse
parameter_list|(
name|List
argument_list|<
name|ForkResource
argument_list|>
name|resources
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|type
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Lost connection to a forked server process"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ForkServer
operator|.
name|RESOURCE
condition|)
block|{
name|ForkResource
name|resource
init|=
name|resources
operator|.
name|get
argument_list|(
name|input
operator|.
name|readUnsignedByte
argument_list|()
argument_list|)
decl_stmt|;
name|resource
operator|.
name|process
argument_list|(
name|input
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|byte
operator|)
name|type
operator|==
name|ForkServer
operator|.
name|ERROR
condition|)
block|{
try|try
block|{
return|return
operator|(
name|Throwable
operator|)
name|ForkObjectInputStream
operator|.
name|readObject
argument_list|(
name|input
argument_list|,
name|loader
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
literal|"Unable to deserialize an exception"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|/**      * Creates a temporary jar file that can be used to bootstrap the forked      * server process. Remember to remove the file when no longer used.      *      * @return the created jar file      * @throws IOException if the bootstrap archive could not be created      */
specifier|private
specifier|static
name|File
name|createBootstrapJar
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|file
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"apache-tika-fork-"
argument_list|,
literal|".jar"
argument_list|)
decl_stmt|;
name|boolean
name|ok
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fillBootstrapJar
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|ok
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|ok
condition|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|file
return|;
block|}
comment|/**      * Fills in the jar file used to bootstrap the forked server process.      * All the required<code>.class</code> files and a manifest with a      *<code>Main-Class</code> entry are written into the archive.      *      * @param file file to hold the bootstrap archive      * @throws IOException if the bootstrap archive could not be created      */
specifier|private
specifier|static
name|void
name|fillBootstrapJar
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|JarOutputStream
name|jar
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
init|)
block|{
name|String
name|manifest
init|=
literal|"Main-Class: "
operator|+
name|ForkServer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"\n"
decl_stmt|;
name|jar
operator|.
name|putNextEntry
argument_list|(
operator|new
name|ZipEntry
argument_list|(
literal|"META-INF/MANIFEST.MF"
argument_list|)
argument_list|)
expr_stmt|;
name|jar
operator|.
name|write
argument_list|(
name|manifest
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|bootstrap
init|=
block|{
name|ForkServer
operator|.
name|class
block|,
name|ForkObjectInputStream
operator|.
name|class
block|,
name|ForkProxy
operator|.
name|class
block|,
name|ClassLoaderProxy
operator|.
name|class
block|,
name|MemoryURLConnection
operator|.
name|class
block|,
name|MemoryURLStreamHandler
operator|.
name|class
block|,
name|MemoryURLStreamHandlerFactory
operator|.
name|class
block|,
name|MemoryURLStreamRecord
operator|.
name|class
block|,
name|TikaException
operator|.
name|class
block|}
decl_stmt|;
name|ClassLoader
name|loader
init|=
name|ForkServer
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|klass
range|:
name|bootstrap
control|)
block|{
name|String
name|path
init|=
name|klass
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|".class"
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
init|)
block|{
name|jar
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|jar
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

