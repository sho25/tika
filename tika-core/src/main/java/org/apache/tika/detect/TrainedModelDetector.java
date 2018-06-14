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
name|detect
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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|Buffer
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Channels
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ReadableByteChannel
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
name|Files
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|TemporaryResources
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
name|mime
operator|.
name|MediaType
import|;
end_import

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

begin_class
specifier|public
specifier|abstract
class|class
name|TrainedModelDetector
implements|implements
name|Detector
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|MediaType
argument_list|,
name|TrainedModel
argument_list|>
name|MODEL_MAP
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|public
name|TrainedModelDetector
parameter_list|()
block|{
name|loadDefaultModels
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getMinLength
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
comment|// convert to byte-histogram
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|mark
argument_list|(
name|getMinLength
argument_list|()
argument_list|)
expr_stmt|;
name|float
index|[]
name|histogram
init|=
name|readByteFrequencies
argument_list|(
name|input
argument_list|)
decl_stmt|;
comment|// writeHisto(histogram); //on testing purpose
comment|/*              * iterate the map to find out the one that gives the higher              * prediction value.              */
name|Iterator
argument_list|<
name|MediaType
argument_list|>
name|iter
init|=
name|MODEL_MAP
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|float
name|threshold
init|=
literal|0.5f
decl_stmt|;
comment|// probability threshold, any value below the
comment|// threshold will be considered as
comment|// MediaType.OCTET_STREAM
name|float
name|maxprob
init|=
name|threshold
decl_stmt|;
name|MediaType
name|maxType
init|=
name|MediaType
operator|.
name|OCTET_STREAM
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MediaType
name|key
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|TrainedModel
name|model
init|=
name|MODEL_MAP
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|float
name|prob
init|=
name|model
operator|.
name|predict
argument_list|(
name|histogram
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxprob
operator|<
name|prob
condition|)
block|{
name|maxprob
operator|=
name|prob
expr_stmt|;
name|maxType
operator|=
name|key
expr_stmt|;
block|}
block|}
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|maxType
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Read the {@code inputstream} and build a byte frequency histogram      *      * @param input stream to read from      * @return byte frequencies array      * @throws IOException      */
specifier|protected
name|float
index|[]
name|readByteFrequencies
parameter_list|(
specifier|final
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|ReadableByteChannel
name|inputChannel
decl_stmt|;
comment|// TODO: any reason to avoid closing of input& inputChannel?
try|try
block|{
name|inputChannel
operator|=
name|Channels
operator|.
name|newChannel
argument_list|(
name|input
argument_list|)
expr_stmt|;
comment|// long inSize = inputChannel.size();
name|float
name|histogram
index|[]
init|=
operator|new
name|float
index|[
literal|257
index|]
decl_stmt|;
name|histogram
index|[
literal|0
index|]
operator|=
literal|1
expr_stmt|;
comment|// create buffer with capacity of maxBufSize bytes
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|1024
operator|*
literal|5
argument_list|)
decl_stmt|;
name|int
name|bytesRead
init|=
name|inputChannel
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
comment|// read into buffer.
name|float
name|max
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|bytesRead
operator|!=
operator|-
literal|1
condition|)
block|{
operator|(
operator|(
name|Buffer
operator|)
name|buf
operator|)
operator|.
name|flip
argument_list|()
expr_stmt|;
comment|// make buffer ready for read
while|while
condition|(
name|buf
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|byte
name|byt
init|=
name|buf
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
name|byt
decl_stmt|;
name|idx
operator|++
expr_stmt|;
if|if
condition|(
name|byt
operator|<
literal|0
condition|)
block|{
name|idx
operator|=
literal|256
operator|+
name|idx
expr_stmt|;
name|histogram
index|[
name|idx
index|]
operator|++
expr_stmt|;
block|}
else|else
block|{
name|histogram
index|[
name|idx
index|]
operator|++
expr_stmt|;
block|}
name|max
operator|=
name|max
operator|<
name|histogram
index|[
name|idx
index|]
condition|?
name|histogram
index|[
name|idx
index|]
else|:
name|max
expr_stmt|;
block|}
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// make buffer ready for writing
name|bytesRead
operator|=
name|inputChannel
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<
name|histogram
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|histogram
index|[
name|i
index|]
operator|/=
name|max
expr_stmt|;
name|histogram
index|[
name|i
index|]
operator|=
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|histogram
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|histogram
return|;
block|}
finally|finally
block|{
comment|// inputChannel.close();
block|}
block|}
comment|/**      * for testing purposes; this method write the histogram vector to a file.      *      * @param histogram      * @throws IOException      */
specifier|private
name|void
name|writeHisto
parameter_list|(
specifier|final
name|float
index|[]
name|histogram
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|histPath
init|=
operator|new
name|TemporaryResources
argument_list|()
operator|.
name|createTempFile
argument_list|()
decl_stmt|;
try|try
init|(
name|Writer
name|writer
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|histPath
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
for|for
control|(
name|float
name|bin
range|:
name|histogram
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|bin
argument_list|)
operator|+
literal|"\t"
argument_list|)
expr_stmt|;
comment|// writer.write(i + "\t");
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|loadDefaultModels
parameter_list|(
name|Path
name|modelFile
parameter_list|)
block|{
try|try
init|(
name|InputStream
name|in
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|modelFile
argument_list|)
init|)
block|{
name|loadDefaultModels
argument_list|(
name|in
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
name|RuntimeException
argument_list|(
literal|"Unable to read the default media type registry"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|loadDefaultModels
parameter_list|(
name|File
name|modelFile
parameter_list|)
block|{
name|loadDefaultModels
argument_list|(
name|modelFile
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|loadDefaultModels
parameter_list|(
specifier|final
name|InputStream
name|modelStream
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|loadDefaultModels
parameter_list|(
specifier|final
name|ClassLoader
name|classLoader
parameter_list|)
function_decl|;
specifier|protected
name|void
name|registerModels
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|TrainedModel
name|model
parameter_list|)
block|{
name|MODEL_MAP
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|model
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

