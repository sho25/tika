begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|utils
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Collections
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
name|Locale
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.tika.parser.DigestingParser.Digester}  * that relies on commons.codec.digest.DigestUtils to calculate digest hashes.  *<p>  * This digester tries to use the regular mark/reset protocol on the InputStream.  * However, this wraps an internal BoundedInputStream, and if the InputStream  * is not fully read, then this will reset the stream and  * spool the InputStream to disk (via TikaInputStream) and then digest the file.  *<p>  * If a TikaInputStream is passed in and it has an underlying file that is longer  * than the {@link #markLimit}, then this digester digests the file directly.  */
end_comment

begin_class
specifier|public
class|class
name|CommonsDigester
implements|implements
name|DigestingParser
operator|.
name|Digester
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CommonsDigester
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
enum|enum
name|DigestAlgorithm
block|{
comment|//those currently available in commons.digest
name|MD2
block|,
name|MD5
block|,
name|SHA1
block|,
name|SHA256
block|,
name|SHA384
block|,
name|SHA512
block|;
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
name|this
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|final
name|List
argument_list|<
name|DigestAlgorithm
argument_list|>
name|algorithms
init|=
operator|new
name|ArrayList
argument_list|<
name|DigestAlgorithm
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|markLimit
decl_stmt|;
specifier|public
name|CommonsDigester
parameter_list|(
name|int
name|markLimit
parameter_list|,
name|DigestAlgorithm
modifier|...
name|algorithms
parameter_list|)
block|{
name|Collections
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|algorithms
argument_list|,
name|algorithms
argument_list|)
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
name|this
operator|.
name|markLimit
operator|=
name|markLimit
expr_stmt|;
block|}
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
name|m
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
name|m
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
for|for
control|(
name|DigestAlgorithm
name|algorithm
range|:
name|algorithms
control|)
block|{
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
name|digestEach
argument_list|(
name|algorithm
argument_list|,
name|bis
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|bis
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|finishedStream
condition|)
block|{
break|break;
block|}
block|}
comment|//if the stream wasn't finished -- if the stream was longer than the mark limit --
comment|//spool to File and digest that.
if|if
condition|(
operator|!
name|finishedStream
condition|)
block|{
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
name|m
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
name|m
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
for|for
control|(
name|DigestAlgorithm
name|algorithm
range|:
name|algorithms
control|)
block|{
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|digestEach
argument_list|(
name|algorithm
argument_list|,
name|is
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @param algorithm algo to use      * @param is        input stream to read from      * @param metadata  metadata for reporting the digest      * @return whether or not this finished the input stream      * @throws IOException      */
specifier|private
name|boolean
name|digestEach
parameter_list|(
name|DigestAlgorithm
name|algorithm
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|digest
init|=
literal|null
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|algorithm
condition|)
block|{
case|case
name|MD2
case|:
name|digest
operator|=
name|DigestUtils
operator|.
name|md2Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|MD5
case|:
name|digest
operator|=
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|SHA1
case|:
name|digest
operator|=
name|DigestUtils
operator|.
name|sha1Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|SHA256
case|:
name|digest
operator|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|SHA384
case|:
name|digest
operator|=
name|DigestUtils
operator|.
name|sha384Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|SHA512
case|:
name|digest
operator|=
name|DigestUtils
operator|.
name|sha512Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Sorry, not aware of algorithm: "
operator|+
name|algorithm
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Problem digesting"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//swallow, or should we throw this?
block|}
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
name|algorithm
operator|.
name|getMetadataKey
argument_list|()
argument_list|,
name|digest
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * @param s comma-delimited (no space) list of algorithms to use: md5,sha256      * @return      */
specifier|public
specifier|static
name|DigestAlgorithm
index|[]
name|parse
parameter_list|(
name|String
name|s
parameter_list|)
block|{
assert|assert
operator|(
name|s
operator|!=
literal|null
operator|)
assert|;
name|List
argument_list|<
name|DigestAlgorithm
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|algoString
range|:
name|s
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|String
name|uc
init|=
name|algoString
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|uc
operator|.
name|equals
argument_list|(
name|DigestAlgorithm
operator|.
name|MD2
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|DigestAlgorithm
operator|.
name|MD2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uc
operator|.
name|equals
argument_list|(
name|DigestAlgorithm
operator|.
name|MD5
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|DigestAlgorithm
operator|.
name|MD5
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uc
operator|.
name|equals
argument_list|(
name|DigestAlgorithm
operator|.
name|SHA1
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|DigestAlgorithm
operator|.
name|SHA1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uc
operator|.
name|equals
argument_list|(
name|DigestAlgorithm
operator|.
name|SHA256
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|DigestAlgorithm
operator|.
name|SHA256
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uc
operator|.
name|equals
argument_list|(
name|DigestAlgorithm
operator|.
name|SHA384
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|DigestAlgorithm
operator|.
name|SHA384
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uc
operator|.
name|equals
argument_list|(
name|DigestAlgorithm
operator|.
name|SHA512
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|DigestAlgorithm
operator|.
name|SHA512
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DigestAlgorithm
name|algo
range|:
name|DigestAlgorithm
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|algo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Couldn't match "
operator|+
name|s
operator|+
literal|" with any of: "
operator|+
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|ret
operator|.
name|toArray
argument_list|(
operator|new
name|DigestAlgorithm
index|[
name|ret
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Very slight modification of Commons' BoundedInputStream      * so that we can figure out if this hit the bound or not.      */
specifier|private
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

