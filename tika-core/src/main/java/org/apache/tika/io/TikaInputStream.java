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
name|io
package|;
end_package

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
name|FileInputStream
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
name|OutputStream
import|;
end_import

begin_comment
comment|/**  *  * @since Apache Tika 0.8  */
end_comment

begin_class
specifier|public
class|class
name|TikaInputStream
extends|extends
name|ProxyInputStream
block|{
comment|/**      * Casts or wraps the given stream to a TikaInputStream instance.      * This method can be used to access the functionality of this class      * even when given just a normal input stream instance.      *      * @param stream normal input stream      * @return a TikaInputStream instance      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
if|if
condition|(
name|stream
operator|instanceof
name|TikaInputStream
condition|)
block|{
return|return
operator|(
name|TikaInputStream
operator|)
name|stream
return|;
block|}
else|else
block|{
return|return
operator|new
name|TikaInputStream
argument_list|(
name|stream
argument_list|)
return|;
block|}
block|}
comment|/**      * The file that contains the contents of this stream. This is either      * the original file passed to the {@link #TikaInputStream(File)}      * constructor or a temporary file created by a call to the      * {@link #getFile()} method. If neither has been called, then      * the value is<code>null</code>.      */
specifier|private
name|File
name|file
decl_stmt|;
comment|/**      * Flag to indicate that {@link #file} is a temporary file that should      * be removed when this stream is {@link #close() closed}.      */
specifier|private
name|boolean
name|temporary
decl_stmt|;
comment|/**      * Current read position within this stream.      */
specifier|private
name|long
name|position
init|=
literal|0
decl_stmt|;
specifier|public
name|TikaInputStream
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
name|super
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|temporary
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|TikaInputStream
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|temporary
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|File
name|getFile
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream has already been read"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|position
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream is already being read"
argument_list|)
throw|;
block|}
else|else
block|{
name|file
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"apache-tika-"
argument_list|,
literal|".tmp"
argument_list|)
expr_stmt|;
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|file
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|temporary
condition|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|file
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeRead
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"End of the stream reached"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterRead
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|n
operator|!=
operator|-
literal|1
condition|)
block|{
name|position
operator|+=
name|n
expr_stmt|;
block|}
else|else
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

