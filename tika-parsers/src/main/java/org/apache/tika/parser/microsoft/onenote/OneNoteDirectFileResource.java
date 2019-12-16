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
name|parser
operator|.
name|microsoft
operator|.
name|onenote
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * This is copied mostly from the {@link org.apache.tika.parser.mp4.DirectFileReadDataSource}.  *<p>  * Implements a simple way to encapsulate a {@link org.apache.tika.io.TikaInputStream} that you will have to seek,read,repeat  * while parsing OneNote contents.  */
end_comment

begin_class
class|class
name|OneNoteDirectFileResource
implements|implements
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|int
name|TRANSFER_SIZE
init|=
literal|8192
decl_stmt|;
specifier|private
name|RandomAccessFile
name|raf
decl_stmt|;
specifier|public
name|OneNoteDirectFileResource
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|raf
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|f
argument_list|,
literal|"r"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|raf
operator|.
name|read
argument_list|()
return|;
block|}
specifier|public
name|int
name|read
parameter_list|(
name|ByteBuffer
name|byteBuffer
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|byteBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|int
name|totalRead
init|=
literal|0
decl_stmt|;
name|int
name|bytesRead
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|TRANSFER_SIZE
index|]
decl_stmt|;
while|while
condition|(
name|totalRead
operator|<
name|len
condition|)
block|{
name|int
name|bytesToRead
init|=
name|Math
operator|.
name|min
argument_list|(
operator|(
name|len
operator|-
name|totalRead
operator|)
argument_list|,
name|TRANSFER_SIZE
argument_list|)
decl_stmt|;
name|bytesRead
operator|=
name|raf
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bytesToRead
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesRead
operator|<
literal|0
condition|)
block|{
break|break;
block|}
else|else
block|{
name|totalRead
operator|+=
name|bytesRead
expr_stmt|;
block|}
name|byteBuffer
operator|.
name|put
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bytesRead
operator|<
literal|0
operator|&&
name|position
argument_list|()
operator|==
name|size
argument_list|()
operator|&&
name|byteBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"End of stream reached earlier than expected"
argument_list|)
throw|;
block|}
return|return
operator|(
operator|(
name|bytesRead
operator|<
literal|0
operator|)
operator|&&
operator|(
name|totalRead
operator|==
literal|0
operator|)
operator|)
condition|?
operator|-
literal|1
else|:
name|totalRead
return|;
block|}
specifier|public
name|long
name|size
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|raf
operator|.
name|length
argument_list|()
return|;
block|}
specifier|public
name|long
name|position
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|raf
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
specifier|public
name|void
name|position
parameter_list|(
name|long
name|nuPos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nuPos
operator|>
name|raf
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"requesting seek past end of stream"
argument_list|)
throw|;
block|}
name|raf
operator|.
name|seek
argument_list|(
name|nuPos
argument_list|)
expr_stmt|;
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
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

