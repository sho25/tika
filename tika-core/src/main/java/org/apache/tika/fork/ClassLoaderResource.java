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
name|Enumeration
import|;
end_import

begin_class
class|class
name|ClassLoaderResource
implements|implements
name|ForkResource
block|{
specifier|private
specifier|final
name|ClassLoader
name|loader
decl_stmt|;
specifier|public
name|ClassLoaderResource
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
comment|/**      * Processes a request for one (code 1) or many (code 2) class loader      * resources. The requested resources are sent preceded with a boolean      *<code>true</code> value. If the resource was not found (code 1) or      * when the last resource has been sent (code 2), a boolean      *<code>false</code> value is sent instead.      *      * @param name resource name      * @throws IOException if the resource could not be sent      */
specifier|public
name|void
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
name|byte
name|type
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|input
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|1
condition|)
block|{
name|InputStream
name|stream
init|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|writeAndCloseStream
argument_list|(
name|output
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|2
condition|)
block|{
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|resources
init|=
name|loader
operator|.
name|getResources
argument_list|(
name|name
argument_list|)
decl_stmt|;
while|while
condition|(
name|resources
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|output
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|resources
operator|.
name|nextElement
argument_list|()
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|writeAndCloseStream
argument_list|(
name|output
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sends the contents of the given input stream to the given output.      * The stream is sent in chunks of less than 64kB, each preceded by      * a 16-bit integer value that indicates the length of the following      * chunk. A zero short value is sent at the end to signify the end of      * the stream.      *<p>      * The stream is guaranteed to be closed by this method, regardless of      * the way it returns.      *      * @param stream the stream to be sent      * @throws IOException if the stream could not be sent      */
specifier|private
name|void
name|writeAndCloseStream
parameter_list|(
name|DataOutputStream
name|output
parameter_list|,
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|0x10000
operator|-
literal|1
index|]
decl_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|stream
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeShort
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|writeShort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

