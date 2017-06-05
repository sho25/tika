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
name|digest
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
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Provider
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
name|IOExceptionWithCause
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
name|io
operator|.
name|TikaInputStream
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
name|parser
operator|.
name|DigestingParser
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

begin_class
specifier|public
class|class
name|InputStreamDigester
implements|implements
name|DigestingParser
operator|.
name|Digester
block|{
specifier|private
specifier|final
name|String
name|algorithm
decl_stmt|;
specifier|private
specifier|final
name|String
name|algorithmKeyName
decl_stmt|;
specifier|private
specifier|final
name|DigestingParser
operator|.
name|Encoder
name|encoder
decl_stmt|;
specifier|private
specifier|final
name|int
name|markLimit
decl_stmt|;
specifier|public
name|InputStreamDigester
parameter_list|(
name|int
name|markLimit
parameter_list|,
name|String
name|algorithm
parameter_list|,
name|DigestingParser
operator|.
name|Encoder
name|encoder
parameter_list|)
block|{
name|this
argument_list|(
name|markLimit
argument_list|,
name|algorithm
argument_list|,
name|algorithm
argument_list|,
name|encoder
argument_list|)
expr_stmt|;
block|}
comment|/**      *      * @param markLimit limit in bytes to allow for mark/reset.  If the inputstream is longer      *                  than this limit, the stream will be reset and then spooled to a temporary file.      *                  Throws IllegalArgumentException if< 0.      * @param algorithm name of the digest algorithm to retrieve from the Provider      * @param algorithmKeyName name of the algorithm to store      *                         as part of the key in the metadata      *                         when {@link #digest(InputStream, Metadata, ParseContext)} is called      * @param encoder encoder to convert the byte array returned from the digester to a string      */
specifier|public
name|InputStreamDigester
parameter_list|(
name|int
name|markLimit
parameter_list|,
name|String
name|algorithm
parameter_list|,
name|String
name|algorithmKeyName
parameter_list|,
name|DigestingParser
operator|.
name|Encoder
name|encoder
parameter_list|)
block|{
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
name|this
operator|.
name|algorithmKeyName
operator|=
name|algorithmKeyName
expr_stmt|;
name|this
operator|.
name|encoder
operator|=
name|encoder
expr_stmt|;
name|this
operator|.
name|markLimit
operator|=
name|markLimit
expr_stmt|;
if|if
condition|(
name|markLimit
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"markLimit must be>= 0"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|MessageDigest
name|newMessageDigest
parameter_list|()
block|{
try|try
block|{
name|Provider
name|provider
init|=
name|getProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
return|return
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|,
name|provider
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      *      * When subclassing this, becare to ensure that your provider is      * thread-safe (not likely) or return a new provider with each call.      *      *      * @return provider to use to get the MessageDigest from the algorithm name.      * Default is to return null.      */
specifier|protected
name|Provider
name|getProvider
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      *      * @param is InputStream to digest. Best to use a TikaInputStream because      *           of potential need to spool to disk.  InputStream must      *           support mark/reset.      * @param metadata metadata in which to store the digest information      * @param parseContext ParseContext -- not actually used yet, but there for future expansion      * @throws IOException on IO problem or IllegalArgumentException if algorithm couldn't be found      */
annotation|@
name|Override
specifier|public
name|void
name|digest
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|cast
argument_list|(
name|is
argument_list|)
decl_stmt|;
if|if
condition|(
name|tis
operator|!=
literal|null
operator|&&
name|tis
operator|.
name|hasFile
argument_list|()
condition|)
block|{
name|long
name|sz
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|tis
operator|.
name|hasFile
argument_list|()
condition|)
block|{
name|sz
operator|=
name|tis
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
comment|//if the inputstream has a file,
comment|//and its size is greater than its mark limit,
comment|//just digest the underlying file.
if|if
condition|(
name|sz
operator|>
name|markLimit
condition|)
block|{
name|digestFile
argument_list|(
name|tis
operator|.
name|getFile
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|//try the usual mark/reset stuff.
comment|//however, if you actually hit the bound,
comment|//then stop and spool to file via TikaInputStream
name|SimpleBoundedInputStream
name|bis
init|=
operator|new
name|SimpleBoundedInputStream
argument_list|(
name|markLimit
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|boolean
name|finishedStream
init|=
literal|false
decl_stmt|;
name|bis
operator|.
name|mark
argument_list|(
name|markLimit
operator|+
literal|1
argument_list|)
expr_stmt|;
name|finishedStream
operator|=
name|digestStream
argument_list|(
name|bis
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|bis
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|finishedStream
condition|)
block|{
return|return;
block|}
comment|//if the stream wasn't finished -- if the stream was longer than the mark limit --
comment|//spool to File and digest that.
if|if
condition|(
name|tis
operator|!=
literal|null
condition|)
block|{
name|digestFile
argument_list|(
name|tis
operator|.
name|getFile
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
try|try
block|{
name|TikaInputStream
name|tmpTikaInputStream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|is
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
name|digestFile
argument_list|(
name|tmpTikaInputStream
operator|.
name|getFile
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|private
name|String
name|getMetadataKey
parameter_list|()
block|{
return|return
name|TikaCoreProperties
operator|.
name|TIKA_META_PREFIX
operator|+
literal|"digest"
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
name|algorithmKeyName
return|;
block|}
specifier|private
name|void
name|digestFile
parameter_list|(
name|File
name|f
parameter_list|,
name|Metadata
name|m
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
init|)
block|{
name|digestStream
argument_list|(
name|is
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param is       input stream to read from      * @param metadata metadata for reporting the digest      * @return whether or not this finished the input stream      * @throws IOException      */
specifier|private
name|boolean
name|digestStream
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|digestBytes
decl_stmt|;
name|MessageDigest
name|messageDigest
init|=
name|newMessageDigest
argument_list|()
decl_stmt|;
name|updateDigest
argument_list|(
name|messageDigest
argument_list|,
name|is
argument_list|)
expr_stmt|;
name|digestBytes
operator|=
name|messageDigest
operator|.
name|digest
argument_list|()
expr_stmt|;
if|if
condition|(
name|is
operator|instanceof
name|SimpleBoundedInputStream
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|SimpleBoundedInputStream
operator|)
name|is
operator|)
operator|.
name|hasHitBound
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|metadata
operator|.
name|set
argument_list|(
name|getMetadataKey
argument_list|()
argument_list|,
name|encoder
operator|.
name|encode
argument_list|(
name|digestBytes
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Copied from commons-codec      */
specifier|private
specifier|static
name|MessageDigest
name|updateDigest
parameter_list|(
name|MessageDigest
name|digest
parameter_list|,
name|InputStream
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|int
name|read
init|=
name|data
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
init|;
name|read
operator|>
operator|-
literal|1
condition|;
name|read
operator|=
name|data
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
control|)
block|{
name|digest
operator|.
name|update
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
return|return
name|digest
return|;
block|}
comment|/**      * Very slight modification of Commons' BoundedInputStream      * so that we can figure out if this hit the bound or not.      */
specifier|private
specifier|static
class|class
name|SimpleBoundedInputStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|final
specifier|static
name|int
name|EOF
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
name|long
name|max
decl_stmt|;
specifier|private
specifier|final
name|InputStream
name|in
decl_stmt|;
specifier|private
name|long
name|pos
decl_stmt|;
specifier|private
name|SimpleBoundedInputStream
parameter_list|(
name|long
name|max
parameter_list|,
name|InputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|max
operator|>=
literal|0
operator|&&
name|pos
operator|>=
name|max
condition|)
block|{
return|return
name|EOF
return|;
block|}
specifier|final
name|int
name|result
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
name|pos
operator|++
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**          * Invokes the delegate's<code>read(byte[])</code> method.          *          * @param b the buffer to read the bytes into          * @return the number of bytes read or -1 if the end of stream or          * the limit has been reached.          * @throws IOException if an I/O error occurs          */
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**          * Invokes the delegate's<code>read(byte[], int, int)</code> method.          *          * @param b   the buffer to read the bytes into          * @param off The start offset          * @param len The number of bytes to read          * @return the number of bytes read or -1 if the end of stream or          * the limit has been reached.          * @throws IOException if an I/O error occurs          */
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|max
operator|>=
literal|0
operator|&&
name|pos
operator|>=
name|max
condition|)
block|{
return|return
name|EOF
return|;
block|}
specifier|final
name|long
name|maxRead
init|=
name|max
operator|>=
literal|0
condition|?
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|max
operator|-
name|pos
argument_list|)
else|:
name|len
decl_stmt|;
specifier|final
name|int
name|bytesRead
init|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
operator|(
name|int
operator|)
name|maxRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|==
name|EOF
condition|)
block|{
return|return
name|EOF
return|;
block|}
name|pos
operator|+=
name|bytesRead
expr_stmt|;
return|return
name|bytesRead
return|;
block|}
comment|/**          * Invokes the delegate's<code>skip(long)</code> method.          *          * @param n the number of bytes to skip          * @return the actual number of bytes skipped          * @throws IOException if an I/O error occurs          */
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
specifier|final
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|toSkip
init|=
name|max
operator|>=
literal|0
condition|?
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|max
operator|-
name|pos
argument_list|)
else|:
name|n
decl_stmt|;
specifier|final
name|long
name|skippedBytes
init|=
name|in
operator|.
name|skip
argument_list|(
name|toSkip
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|skippedBytes
expr_stmt|;
return|return
name|skippedBytes
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readLimit
parameter_list|)
block|{
name|in
operator|.
name|mark
argument_list|(
name|readLimit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasHitBound
parameter_list|()
block|{
return|return
name|pos
operator|>=
name|max
return|;
block|}
block|}
block|}
end_class

end_unit
