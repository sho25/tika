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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|CheckedInputStream
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
name|CheckedOutputStream
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
name|Checksum
import|;
end_import

begin_class
class|class
name|ForkServer
implements|implements
name|Runnable
implements|,
name|Checksum
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|ERROR
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|DONE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|CALL
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|PING
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|RESOURCE
init|=
literal|3
decl_stmt|;
comment|/**      * Starts a forked server process using the standard input and output      * streams for communication with the parent process. Any attempts by      * stray code to read from standard input or write to standard output      * is redirected to avoid interfering with the communication channel.      *       * @param args command line arguments, ignored      * @throws Exception if the server could not be started      */
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
name|URL
operator|.
name|setURLStreamHandlerFactory
argument_list|(
operator|new
name|MemoryURLStreamHandlerFactory
argument_list|()
argument_list|)
expr_stmt|;
name|ForkServer
name|server
init|=
operator|new
name|ForkServer
argument_list|(
name|System
operator|.
name|in
argument_list|,
name|System
operator|.
name|out
argument_list|)
decl_stmt|;
name|System
operator|.
name|setIn
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|Thread
name|watchdog
init|=
operator|new
name|Thread
argument_list|(
name|server
argument_list|,
literal|"Tika Watchdog"
argument_list|)
decl_stmt|;
name|watchdog
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|watchdog
operator|.
name|start
argument_list|()
expr_stmt|;
name|server
operator|.
name|processRequests
argument_list|()
expr_stmt|;
block|}
comment|/** Input stream for reading from the parent process */
specifier|private
specifier|final
name|DataInputStream
name|input
decl_stmt|;
comment|/** Output stream for writing to the parent process */
specifier|private
specifier|final
name|DataOutputStream
name|output
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|active
init|=
literal|true
decl_stmt|;
comment|/**      * Sets up a forked server instance using the given stdin/out      * communication channel.      *      * @param input input stream for reading from the parent process      * @param output output stream for writing to the parent process      * @throws IOException if the server instance could not be created      */
specifier|public
name|ForkServer
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|OutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|input
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|CheckedInputStream
argument_list|(
name|input
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|output
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|CheckedOutputStream
argument_list|(
name|output
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
name|active
condition|)
block|{
name|active
operator|=
literal|false
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|processRequests
parameter_list|()
block|{
try|try
block|{
name|ClassLoader
name|loader
init|=
operator|(
name|ClassLoader
operator|)
name|readObject
argument_list|(
name|ForkServer
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|Object
name|object
init|=
name|readObject
argument_list|(
name|loader
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|request
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|request
operator|==
name|CALL
condition|)
block|{
name|call
argument_list|(
name|loader
argument_list|,
name|object
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected request"
argument_list|)
throw|;
block|}
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
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
block|}
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|call
parameter_list|(
name|ClassLoader
name|loader
parameter_list|,
name|Object
name|object
parameter_list|)
throws|throws
name|Exception
block|{
name|Method
name|method
init|=
name|getMethod
argument_list|(
name|object
argument_list|,
name|input
operator|.
name|readUTF
argument_list|()
argument_list|)
decl_stmt|;
name|Object
index|[]
name|args
init|=
operator|new
name|Object
index|[
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
index|]
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
name|args
index|[
name|i
index|]
operator|=
name|readObject
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|method
operator|.
name|invoke
argument_list|(
name|object
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|DONE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|output
operator|.
name|write
argument_list|(
name|ERROR
argument_list|)
expr_stmt|;
name|ForkObjectInputStream
operator|.
name|sendObject
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Method
name|getMethod
parameter_list|(
name|Object
name|object
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|klass
init|=
name|object
operator|.
name|getClass
argument_list|()
decl_stmt|;
while|while
condition|(
name|klass
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|iface
range|:
name|klass
operator|.
name|getInterfaces
argument_list|()
control|)
block|{
for|for
control|(
name|Method
name|method
range|:
name|iface
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|method
return|;
block|}
block|}
block|}
name|klass
operator|=
name|klass
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Deserializes an object from the given stream. The serialized object      * is expected to be preceded by a size integer, that is used for reading      * the entire serialization into a memory before deserializing it.      *      * @param input input stream from which the serialized object is read      * @param loader class loader to be used for loading referenced classes      * @throws IOException if the object could not be deserialized      * @throws ClassNotFoundException if a referenced class is not found      */
specifier|private
name|Object
name|readObject
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|Object
name|object
init|=
name|ForkObjectInputStream
operator|.
name|readObject
argument_list|(
name|input
argument_list|,
name|loader
argument_list|)
decl_stmt|;
if|if
condition|(
name|object
operator|instanceof
name|ForkProxy
condition|)
block|{
operator|(
operator|(
name|ForkProxy
operator|)
name|object
operator|)
operator|.
name|init
argument_list|(
name|input
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|// Tell the parent process that we successfully received this object
name|output
operator|.
name|writeByte
argument_list|(
name|ForkServer
operator|.
name|DONE
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|object
return|;
block|}
comment|//-------------------------------------------------------------< Checsum>
specifier|public
name|void
name|update
parameter_list|(
name|int
name|b
parameter_list|)
block|{
name|active
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|update
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|active
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{     }
block|}
end_class

end_unit

