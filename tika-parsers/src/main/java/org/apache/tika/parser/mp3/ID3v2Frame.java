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
name|mp3
package|;
end_package

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
name|PushbackInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|mp3
operator|.
name|ID3Tags
operator|.
name|ID3Comment
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
name|ISO_8859_1
import|;
end_import

begin_comment
comment|/**  * A frame of ID3v2 data, which is then passed to a handler to   * be turned into useful data.  */
end_comment

begin_class
specifier|public
class|class
name|ID3v2Frame
implements|implements
name|MP3Frame
block|{
specifier|private
name|int
name|majorVersion
decl_stmt|;
specifier|private
name|int
name|minorVersion
decl_stmt|;
specifier|private
name|int
name|flags
decl_stmt|;
specifier|private
name|int
name|length
decl_stmt|;
comment|/** Excludes the header size part */
specifier|private
name|byte
index|[]
name|extendedHeader
decl_stmt|;
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
specifier|public
name|int
name|getMajorVersion
parameter_list|()
block|{
return|return
name|majorVersion
return|;
block|}
specifier|public
name|int
name|getMinorVersion
parameter_list|()
block|{
return|return
name|minorVersion
return|;
block|}
specifier|public
name|int
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|public
name|byte
index|[]
name|getExtendedHeader
parameter_list|()
block|{
return|return
name|extendedHeader
return|;
block|}
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
comment|/**      * Returns the next ID3v2 Frame in      *  the file, or null if the next batch of data      *  doesn't correspond to either an ID3v2 header.      * If no ID3v2 frame could be detected and the passed in input stream is a      * {@code PushbackInputStream}, the bytes read so far are pushed back so      * that they can be read again.      * ID3v2 Frames should come before all Audio ones.      */
specifier|public
specifier|static
name|MP3Frame
name|createFrameIfPresent
parameter_list|(
name|InputStream
name|inp
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|h1
init|=
name|inp
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|h2
init|=
name|inp
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|h3
init|=
name|inp
operator|.
name|read
argument_list|()
decl_stmt|;
comment|// Is it an ID3v2 Frame?
if|if
condition|(
name|h1
operator|==
operator|(
name|int
operator|)
literal|'I'
operator|&&
name|h2
operator|==
operator|(
name|int
operator|)
literal|'D'
operator|&&
name|h3
operator|==
operator|(
name|int
operator|)
literal|'3'
condition|)
block|{
name|int
name|majorVersion
init|=
name|inp
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|minorVersion
init|=
name|inp
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|majorVersion
operator|==
operator|-
literal|1
operator|||
name|minorVersion
operator|==
operator|-
literal|1
condition|)
block|{
name|pushBack
argument_list|(
name|inp
argument_list|,
name|h1
argument_list|,
name|h2
argument_list|,
name|h3
argument_list|,
name|majorVersion
argument_list|,
name|minorVersion
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ID3v2Frame
argument_list|(
name|majorVersion
argument_list|,
name|minorVersion
argument_list|,
name|inp
argument_list|)
return|;
block|}
comment|// Not a frame header
name|pushBack
argument_list|(
name|inp
argument_list|,
name|h1
argument_list|,
name|h2
argument_list|,
name|h3
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**      * Pushes bytes back into the stream if possible. This method is called if      * no ID3v2 header could be found at the current stream position.      *       * @param inp the input stream      * @param bytes the bytes to be pushed back      * @throws IOException if an error occurs      */
specifier|private
specifier|static
name|void
name|pushBack
parameter_list|(
name|InputStream
name|inp
parameter_list|,
name|int
modifier|...
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|inp
operator|instanceof
name|PushbackInputStream
condition|)
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|bytes
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buf
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|bytes
index|[
name|i
index|]
expr_stmt|;
block|}
operator|(
operator|(
name|PushbackInputStream
operator|)
name|inp
operator|)
operator|.
name|unread
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|ID3v2Frame
parameter_list|(
name|int
name|majorVersion
parameter_list|,
name|int
name|minorVersion
parameter_list|,
name|InputStream
name|inp
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|majorVersion
operator|=
name|majorVersion
expr_stmt|;
name|this
operator|.
name|minorVersion
operator|=
name|minorVersion
expr_stmt|;
comment|// Get the flags and the length
name|flags
operator|=
name|inp
operator|.
name|read
argument_list|()
expr_stmt|;
name|length
operator|=
name|get7BitsInt
argument_list|(
name|readFully
argument_list|(
name|inp
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Do we have an extended header?
if|if
condition|(
operator|(
name|flags
operator|&
literal|0x02
operator|)
operator|==
literal|0x02
condition|)
block|{
name|int
name|size
init|=
name|getInt
argument_list|(
name|readFully
argument_list|(
name|inp
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|extendedHeader
operator|=
name|readFully
argument_list|(
name|inp
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|// Get the frame's data, or at least as much
comment|//  of it as we could do
name|data
operator|=
name|readFully
argument_list|(
name|inp
argument_list|,
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|int
name|getInt
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|getInt
argument_list|(
name|data
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|int
name|getInt
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|b0
init|=
name|data
index|[
name|offset
operator|+
literal|0
index|]
operator|&
literal|0xFF
decl_stmt|;
name|int
name|b1
init|=
name|data
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
decl_stmt|;
name|int
name|b2
init|=
name|data
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFF
decl_stmt|;
name|int
name|b3
init|=
name|data
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xFF
decl_stmt|;
return|return
operator|(
name|b0
operator|<<
literal|24
operator|)
operator|+
operator|(
name|b1
operator|<<
literal|16
operator|)
operator|+
operator|(
name|b2
operator|<<
literal|8
operator|)
operator|+
operator|(
name|b3
operator|<<
literal|0
operator|)
return|;
block|}
specifier|protected
specifier|static
name|int
name|getInt3
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|b0
init|=
name|data
index|[
name|offset
operator|+
literal|0
index|]
operator|&
literal|0xFF
decl_stmt|;
name|int
name|b1
init|=
name|data
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
decl_stmt|;
name|int
name|b2
init|=
name|data
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFF
decl_stmt|;
return|return
operator|(
name|b0
operator|<<
literal|16
operator|)
operator|+
operator|(
name|b1
operator|<<
literal|8
operator|)
operator|+
operator|(
name|b2
operator|<<
literal|0
operator|)
return|;
block|}
specifier|protected
specifier|static
name|int
name|getInt2
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|b0
init|=
name|data
index|[
name|offset
operator|+
literal|0
index|]
operator|&
literal|0xFF
decl_stmt|;
name|int
name|b1
init|=
name|data
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
decl_stmt|;
return|return
operator|(
name|b0
operator|<<
literal|8
operator|)
operator|+
operator|(
name|b1
operator|<<
literal|0
operator|)
return|;
block|}
comment|/**      * AKA a Synchsafe integer.      * 4 bytes hold a 28 bit number. The highest      *  bit in each byte is always 0 and always ignored.      */
specifier|protected
specifier|static
name|int
name|get7BitsInt
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|b0
init|=
name|data
index|[
name|offset
operator|+
literal|0
index|]
operator|&
literal|0x7F
decl_stmt|;
name|int
name|b1
init|=
name|data
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0x7F
decl_stmt|;
name|int
name|b2
init|=
name|data
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0x7F
decl_stmt|;
name|int
name|b3
init|=
name|data
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0x7F
decl_stmt|;
return|return
operator|(
name|b0
operator|<<
literal|21
operator|)
operator|+
operator|(
name|b1
operator|<<
literal|14
operator|)
operator|+
operator|(
name|b2
operator|<<
literal|7
operator|)
operator|+
operator|(
name|b3
operator|<<
literal|0
operator|)
return|;
block|}
specifier|protected
specifier|static
name|byte
index|[]
name|readFully
parameter_list|(
name|InputStream
name|inp
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFully
argument_list|(
name|inp
argument_list|,
name|length
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|byte
index|[]
name|readFully
parameter_list|(
name|InputStream
name|inp
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|shortDataIsFatal
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|int
name|read
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|read
operator|=
name|inp
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|pos
argument_list|,
name|length
operator|-
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|read
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|shortDataIsFatal
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Tried to read "
operator|+
name|length
operator|+
literal|" bytes, but only "
operator|+
name|pos
operator|+
literal|" bytes present"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Give them what we found
comment|// TODO Log the short read
return|return
name|b
return|;
block|}
block|}
name|pos
operator|+=
name|read
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
specifier|protected
specifier|static
class|class
name|TextEncoding
block|{
specifier|public
specifier|final
name|boolean
name|doubleByte
decl_stmt|;
specifier|public
specifier|final
name|String
name|encoding
decl_stmt|;
specifier|private
name|TextEncoding
parameter_list|(
name|String
name|encoding
parameter_list|,
name|boolean
name|doubleByte
parameter_list|)
block|{
name|this
operator|.
name|doubleByte
operator|=
name|doubleByte
expr_stmt|;
name|this
operator|.
name|encoding
operator|=
name|encoding
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
specifier|final
name|TextEncoding
index|[]
name|encodings
init|=
operator|new
name|TextEncoding
index|[]
block|{
operator|new
name|TextEncoding
argument_list|(
literal|"ISO-8859-1"
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|TextEncoding
argument_list|(
literal|"UTF-16"
argument_list|,
literal|true
argument_list|)
block|,
comment|// With BOM
operator|new
name|TextEncoding
argument_list|(
literal|"UTF-16BE"
argument_list|,
literal|true
argument_list|)
block|,
comment|// Without BOM
operator|new
name|TextEncoding
argument_list|(
literal|"UTF-8"
argument_list|,
literal|false
argument_list|)
block|}
decl_stmt|;
comment|/**      * Returns the (possibly null padded) String at the given offset and      * length. String encoding is held in the first byte;       */
specifier|protected
specifier|static
name|String
name|getTagString
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|int
name|actualLength
init|=
name|length
decl_stmt|;
if|if
condition|(
name|actualLength
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
if|if
condition|(
name|actualLength
operator|==
literal|1
operator|&&
name|data
index|[
name|offset
index|]
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
comment|// Does it have an encoding flag?
comment|// Detect by the first byte being sub 0x20
name|TextEncoding
name|encoding
init|=
name|encodings
index|[
literal|0
index|]
decl_stmt|;
name|byte
name|maybeEncodingFlag
init|=
name|data
index|[
name|offset
index|]
decl_stmt|;
if|if
condition|(
name|maybeEncodingFlag
operator|>=
literal|0
operator|&&
name|maybeEncodingFlag
operator|<
name|encodings
operator|.
name|length
condition|)
block|{
name|offset
operator|++
expr_stmt|;
name|actualLength
operator|--
expr_stmt|;
name|encoding
operator|=
name|encodings
index|[
name|maybeEncodingFlag
index|]
expr_stmt|;
block|}
comment|// Trim off null termination / padding (as present)
while|while
condition|(
name|encoding
operator|.
name|doubleByte
operator|&&
name|actualLength
operator|>=
literal|2
operator|&&
name|data
index|[
name|offset
operator|+
name|actualLength
operator|-
literal|1
index|]
operator|==
literal|0
operator|&&
name|data
index|[
name|offset
operator|+
name|actualLength
operator|-
literal|2
index|]
operator|==
literal|0
condition|)
block|{
name|actualLength
operator|-=
literal|2
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|encoding
operator|.
name|doubleByte
operator|&&
name|actualLength
operator|>=
literal|1
operator|&&
name|data
index|[
name|offset
operator|+
name|actualLength
operator|-
literal|1
index|]
operator|==
literal|0
condition|)
block|{
name|actualLength
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|actualLength
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
comment|// TIKA-1024: If it's UTF-16 (with BOM) and all we
comment|// have is a naked BOM then short-circuit here
comment|// (return empty string), because new String(..)
comment|// gives different results on different JVMs
if|if
condition|(
name|encoding
operator|.
name|encoding
operator|.
name|equals
argument_list|(
literal|"UTF-16"
argument_list|)
operator|&&
name|actualLength
operator|==
literal|2
operator|&&
operator|(
operator|(
name|data
index|[
name|offset
index|]
operator|==
operator|(
name|byte
operator|)
literal|0xff
operator|&&
name|data
index|[
name|offset
operator|+
literal|1
index|]
operator|==
operator|(
name|byte
operator|)
literal|0xfe
operator|)
operator|||
operator|(
name|data
index|[
name|offset
index|]
operator|==
operator|(
name|byte
operator|)
literal|0xfe
operator|&&
name|data
index|[
name|offset
operator|+
literal|1
index|]
operator|==
operator|(
name|byte
operator|)
literal|0xff
operator|)
operator|)
condition|)
block|{
return|return
literal|""
return|;
block|}
try|try
block|{
comment|// Build the base string
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|actualLength
argument_list|,
name|encoding
operator|.
name|encoding
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Core encoding "
operator|+
name|encoding
operator|.
name|encoding
operator|+
literal|" is not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Builds up the ID3 comment, by parsing and extracting      *  the comment string parts from the given data.       */
specifier|protected
specifier|static
name|ID3Comment
name|getComment
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
comment|// Comments must have an encoding
name|int
name|encodingFlag
init|=
name|data
index|[
name|offset
index|]
decl_stmt|;
if|if
condition|(
name|encodingFlag
operator|>=
literal|0
operator|&&
name|encodingFlag
operator|<
name|encodings
operator|.
name|length
condition|)
block|{
comment|// Good, valid flag
block|}
else|else
block|{
comment|// Invalid string
return|return
literal|null
return|;
block|}
name|TextEncoding
name|encoding
init|=
name|encodings
index|[
name|encodingFlag
index|]
decl_stmt|;
comment|// First is a 3 byte language
name|String
name|lang
init|=
name|getString
argument_list|(
name|data
argument_list|,
name|offset
operator|+
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// After that we have [Desc]\0(\0)[Text]
name|int
name|descStart
init|=
name|offset
operator|+
literal|4
decl_stmt|;
name|int
name|textStart
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|description
init|=
literal|null
decl_stmt|;
name|String
name|text
init|=
literal|null
decl_stmt|;
comment|// Find where the description ends
try|try
block|{
for|for
control|(
name|int
name|i
init|=
name|descStart
init|;
name|i
operator|<
name|offset
operator|+
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|encoding
operator|.
name|doubleByte
operator|&&
name|data
index|[
name|i
index|]
operator|==
literal|0
operator|&&
name|data
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|0
condition|)
block|{
comment|// Handle LE vs BE on low byte text
if|if
condition|(
name|i
operator|+
literal|2
operator|<
name|offset
operator|+
name|length
operator|&&
name|data
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|0
operator|&&
name|data
index|[
name|i
operator|+
literal|2
index|]
operator|==
literal|0
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
name|textStart
operator|=
name|i
operator|+
literal|2
expr_stmt|;
name|description
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|descStart
argument_list|,
name|i
operator|-
name|descStart
argument_list|,
name|encoding
operator|.
name|encoding
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|!
name|encoding
operator|.
name|doubleByte
operator|&&
name|data
index|[
name|i
index|]
operator|==
literal|0
condition|)
block|{
name|textStart
operator|=
name|i
operator|+
literal|1
expr_stmt|;
name|description
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|descStart
argument_list|,
name|i
operator|-
name|descStart
argument_list|,
name|encoding
operator|.
name|encoding
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// Did we find the end?
if|if
condition|(
name|textStart
operator|>
operator|-
literal|1
condition|)
block|{
name|text
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|textStart
argument_list|,
name|offset
operator|+
name|length
operator|-
name|textStart
argument_list|,
name|encoding
operator|.
name|encoding
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Assume everything is the text
name|text
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|descStart
argument_list|,
name|offset
operator|+
name|length
operator|-
name|descStart
argument_list|,
name|encoding
operator|.
name|encoding
argument_list|)
expr_stmt|;
block|}
comment|// Return
return|return
operator|new
name|ID3Comment
argument_list|(
name|lang
argument_list|,
name|description
argument_list|,
name|text
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Core encoding "
operator|+
name|encoding
operator|.
name|encoding
operator|+
literal|" is not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the String at the given      *  offset and length. Strings are ISO-8859-1       */
specifier|protected
specifier|static
name|String
name|getString
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|ISO_8859_1
argument_list|)
return|;
block|}
comment|/**      * Iterates over id3v2 raw tags.      * Create an instance of this that configures the      *  various length and multipliers.      */
specifier|protected
class|class
name|RawTagIterator
implements|implements
name|Iterator
argument_list|<
name|RawTag
argument_list|>
block|{
specifier|private
name|int
name|nameLength
decl_stmt|;
specifier|private
name|int
name|sizeLength
decl_stmt|;
specifier|private
name|int
name|sizeMultiplier
decl_stmt|;
specifier|private
name|int
name|flagLength
decl_stmt|;
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|;
specifier|protected
name|RawTagIterator
parameter_list|(
name|int
name|nameLength
parameter_list|,
name|int
name|sizeLength
parameter_list|,
name|int
name|sizeMultiplier
parameter_list|,
name|int
name|flagLength
parameter_list|)
block|{
name|this
operator|.
name|nameLength
operator|=
name|nameLength
expr_stmt|;
name|this
operator|.
name|sizeLength
operator|=
name|sizeLength
expr_stmt|;
name|this
operator|.
name|sizeMultiplier
operator|=
name|sizeMultiplier
expr_stmt|;
name|this
operator|.
name|flagLength
operator|=
name|flagLength
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
comment|// Check for padding at the end
return|return
name|offset
operator|<
name|data
operator|.
name|length
operator|&&
name|data
index|[
name|offset
index|]
operator|!=
literal|0
return|;
block|}
specifier|public
name|RawTag
name|next
parameter_list|()
block|{
name|RawTag
name|tag
init|=
operator|new
name|RawTag
argument_list|(
name|nameLength
argument_list|,
name|sizeLength
argument_list|,
name|sizeMultiplier
argument_list|,
name|flagLength
argument_list|,
name|data
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|+=
name|tag
operator|.
name|getSize
argument_list|()
expr_stmt|;
return|return
name|tag
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{         }
block|}
specifier|protected
specifier|static
class|class
name|RawTag
block|{
specifier|private
name|int
name|headerSize
decl_stmt|;
specifier|protected
name|String
name|name
decl_stmt|;
specifier|protected
name|int
name|flag
decl_stmt|;
specifier|protected
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
name|RawTag
parameter_list|(
name|int
name|nameLength
parameter_list|,
name|int
name|sizeLength
parameter_list|,
name|int
name|sizeMultiplier
parameter_list|,
name|int
name|flagLength
parameter_list|,
name|byte
index|[]
name|frameData
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|headerSize
operator|=
name|nameLength
operator|+
name|sizeLength
operator|+
name|flagLength
expr_stmt|;
comment|// Name, normally 3 or 4 bytes
name|name
operator|=
name|getString
argument_list|(
name|frameData
argument_list|,
name|offset
argument_list|,
name|nameLength
argument_list|)
expr_stmt|;
comment|// Size
name|int
name|rawSize
decl_stmt|;
if|if
condition|(
name|sizeLength
operator|==
literal|3
condition|)
block|{
name|rawSize
operator|=
name|getInt3
argument_list|(
name|frameData
argument_list|,
name|offset
operator|+
name|nameLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rawSize
operator|=
name|getInt
argument_list|(
name|frameData
argument_list|,
name|offset
operator|+
name|nameLength
argument_list|)
expr_stmt|;
block|}
name|int
name|size
init|=
name|rawSize
operator|*
name|sizeMultiplier
decl_stmt|;
comment|// Flag
if|if
condition|(
name|flagLength
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|flagLength
operator|==
literal|1
condition|)
block|{
name|flag
operator|=
operator|(
name|int
operator|)
name|frameData
index|[
name|offset
operator|+
name|nameLength
operator|+
name|sizeLength
index|]
expr_stmt|;
block|}
else|else
block|{
name|flag
operator|=
name|getInt2
argument_list|(
name|frameData
argument_list|,
name|offset
operator|+
name|nameLength
operator|+
name|sizeLength
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Now data
name|int
name|copyFrom
init|=
name|offset
operator|+
name|nameLength
operator|+
name|sizeLength
operator|+
name|flagLength
decl_stmt|;
name|size
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|size
argument_list|,
name|frameData
operator|.
name|length
operator|-
name|copyFrom
argument_list|)
argument_list|)
expr_stmt|;
comment|// TIKA-1218, prevent negative size for malformed files.
name|data
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|frameData
argument_list|,
name|copyFrom
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|int
name|getSize
parameter_list|()
block|{
return|return
name|headerSize
operator|+
name|data
operator|.
name|length
return|;
block|}
block|}
block|}
end_class

end_unit

