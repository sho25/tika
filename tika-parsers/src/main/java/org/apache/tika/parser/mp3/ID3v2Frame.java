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
comment|/**      * Returns the next Frame (ID3v2 or Audio) in      *  the file, or null if the next batch of data      *  doesn't correspond to either an ID3v2 Frame      *  or an Audio Frame.      * ID3v2 Frames should come before all Audio ones.      */
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
comment|// Is it an Audio Frame?
name|int
name|h4
init|=
name|inp
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|AudioFrame
operator|.
name|isAudioHeader
argument_list|(
name|h1
argument_list|,
name|h2
argument_list|,
name|h3
argument_list|,
name|h4
argument_list|)
condition|)
block|{
return|return
operator|new
name|AudioFrame
argument_list|(
name|h1
argument_list|,
name|h2
argument_list|,
name|h3
argument_list|,
name|h4
argument_list|,
name|inp
argument_list|)
return|;
block|}
comment|// Not a frame header
return|return
literal|null
return|;
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
while|while
condition|(
name|actualLength
operator|>
literal|0
operator|&&
name|data
index|[
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
comment|// Does it have an encoding flag?
comment|// Detect by the first byte being sub 0x20
name|String
name|encoding
init|=
literal|"ISO-8859-1"
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
operator|==
literal|0
operator|||
name|maybeEncodingFlag
operator|==
literal|1
operator|||
name|maybeEncodingFlag
operator|==
literal|2
operator|||
name|maybeEncodingFlag
operator|==
literal|3
condition|)
block|{
name|offset
operator|++
expr_stmt|;
name|actualLength
operator|--
expr_stmt|;
if|if
condition|(
name|maybeEncodingFlag
operator|==
literal|1
condition|)
block|{
comment|// With BOM
name|encoding
operator|=
literal|"UTF-16"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|maybeEncodingFlag
operator|==
literal|2
condition|)
block|{
comment|// Without BOM
name|encoding
operator|=
literal|"UTF-16BE"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|maybeEncodingFlag
operator|==
literal|3
condition|)
block|{
name|encoding
operator|=
literal|"UTF8"
expr_stmt|;
block|}
block|}
try|try
block|{
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
try|try
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
literal|"ISO-8859-1"
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
literal|"Core encoding ISO-8859-1 encoding is not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
expr_stmt|;
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

