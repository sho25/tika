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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|ObjectOutputStream
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
name|io
operator|.
name|IOUtils
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
name|String
name|java
init|=
literal|"java"
decl_stmt|;
comment|// TODO: Make configurable
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
argument_list|<
name|ForkResource
argument_list|>
argument_list|()
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
name|ClassLoader
name|loader
parameter_list|,
name|Object
name|object
parameter_list|)
throws|throws
name|IOException
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
name|builder
operator|.
name|command
argument_list|(
name|java
argument_list|,
literal|"-jar"
argument_list|,
name|jar
operator|.
name|getPath
argument_list|()
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
specifier|public
specifier|synchronized
name|void
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
block|{
name|List
argument_list|<
name|ForkResource
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|ForkResource
argument_list|>
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
name|waitForResponse
argument_list|(
name|r
argument_list|)
expr_stmt|;
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
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|serializer
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|writeObject
argument_list|(
name|object
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|buffer
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
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
block|}
if|if
condition|(
name|jar
operator|!=
literal|null
condition|)
block|{
comment|// jar.delete();
block|}
block|}
specifier|private
name|byte
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
name|consumeErrorStream
argument_list|()
expr_stmt|;
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
else|else
block|{
return|return
operator|(
name|byte
operator|)
name|type
return|;
block|}
block|}
block|}
comment|/**      * Consumes all pending bytes from the standard error stream of the      * forked server process, and prints them out to the standard error      * stream of this process. This method should be called always before      * expecting some output from the server, to prevent the server from      * blocking due to a filled up pipe buffer of the error stream.      *      * @throws IOException if the error stream could not be read      */
specifier|private
name|void
name|consumeErrorStream
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|error
operator|.
name|available
argument_list|()
operator|)
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|n
index|]
decl_stmt|;
name|n
operator|=
name|error
operator|.
name|read
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
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
decl_stmt|;
try|try
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
literal|"UTF-8"
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
name|ForkSerializer
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
name|InputStream
name|input
init|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
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
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|jar
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

