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
name|image
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
name|EOFException
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
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|extractor
operator|.
name|EmbeddedDocumentExtractor
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
name|extractor
operator|.
name|EmbeddedDocumentUtil
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
name|EndianUtils
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
name|Photoshop
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
name|TIFF
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
name|mime
operator|.
name|MediaType
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
name|AbstractParser
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
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|image
operator|.
name|xmp
operator|.
name|JempboxExtractor
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
name|sax
operator|.
name|XHTMLContentHandler
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|US_ASCII
import|;
end_import

begin_comment
comment|/**  * Parser for the Adobe Photoshop PSD File Format.  *<p/>  * Documentation on the file format is available from  * http://www.adobe.com/devnet-apps/photoshop/fileformatashtml/PhotoshopFileFormats.htm  *  * An MIT-licensed python parser with test files is:  * https://github.com/psd-tools/psd-tools  */
end_comment

begin_class
specifier|public
class|class
name|PSDParser
extends|extends
name|AbstractParser
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|883387734607994914L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|MediaType
operator|.
name|image
argument_list|(
literal|"vnd.adobe.photoshop"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DATA_LENGTH_BYTES
init|=
literal|1000000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_BLOCKS
init|=
literal|10000
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
comment|// Check for the magic header signature
name|byte
index|[]
name|signature
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|signature
argument_list|)
expr_stmt|;
if|if
condition|(
name|signature
index|[
literal|0
index|]
operator|==
operator|(
name|byte
operator|)
literal|'8'
operator|&&
name|signature
index|[
literal|1
index|]
operator|==
operator|(
name|byte
operator|)
literal|'B'
operator|&&
name|signature
index|[
literal|2
index|]
operator|==
operator|(
name|byte
operator|)
literal|'P'
operator|&&
name|signature
index|[
literal|3
index|]
operator|==
operator|(
name|byte
operator|)
literal|'S'
condition|)
block|{
comment|// Good, signature found
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"PSD/PSB magic signature invalid"
argument_list|)
throw|;
block|}
comment|// Check the version
name|int
name|version
init|=
name|EndianUtils
operator|.
name|readUShortBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
literal|1
operator|||
name|version
operator|==
literal|2
condition|)
block|{
comment|// Good, we support these two
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Invalid PSD/PSB version "
operator|+
name|version
argument_list|)
throw|;
block|}
comment|// Skip the reserved block
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
operator|new
name|byte
index|[
literal|6
index|]
argument_list|)
expr_stmt|;
comment|// Number of channels in the image
name|int
name|numChannels
init|=
name|EndianUtils
operator|.
name|readUShortBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// TODO Identify a suitable metadata key for this
comment|// Width and Height
name|int
name|height
init|=
name|EndianUtils
operator|.
name|readIntBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|int
name|width
init|=
name|EndianUtils
operator|.
name|readIntBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TIFF
operator|.
name|IMAGE_LENGTH
argument_list|,
name|height
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TIFF
operator|.
name|IMAGE_WIDTH
argument_list|,
name|width
argument_list|)
expr_stmt|;
comment|// Depth (bits per channel)
name|int
name|depth
init|=
name|EndianUtils
operator|.
name|readUShortBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TIFF
operator|.
name|BITS_PER_SAMPLE
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|depth
argument_list|)
argument_list|)
expr_stmt|;
comment|// Colour mode, eg Bitmap or RGB
name|int
name|colorMode
init|=
name|EndianUtils
operator|.
name|readUShortBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|colorMode
operator|<
name|Photoshop
operator|.
name|_COLOR_MODE_CHOICES_INDEXED
operator|.
name|length
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Photoshop
operator|.
name|COLOR_MODE
argument_list|,
name|Photoshop
operator|.
name|_COLOR_MODE_CHOICES_INDEXED
index|[
name|colorMode
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Next is the Color Mode section
comment|// We don't care about this bit
name|long
name|colorModeSectionSize
init|=
name|EndianUtils
operator|.
name|readIntBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|stream
argument_list|,
name|colorModeSectionSize
argument_list|)
expr_stmt|;
comment|// Next is the Image Resources section
comment|// Check for certain interesting keys here
name|long
name|imageResourcesSectionSize
init|=
name|EndianUtils
operator|.
name|readIntBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|long
name|read
init|=
literal|0
decl_stmt|;
comment|//if something is corrupt about this number, prevent an
comment|//infinite loop by only reading 10000 blocks
name|int
name|blocks
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|read
operator|<
name|imageResourcesSectionSize
operator|&&
name|blocks
operator|<
name|MAX_BLOCKS
condition|)
block|{
name|ResourceBlock
name|rb
init|=
operator|new
name|ResourceBlock
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|rb
operator|.
name|totalLength
operator|<=
literal|0
condition|)
block|{
comment|//break;
block|}
name|read
operator|+=
name|rb
operator|.
name|totalLength
expr_stmt|;
comment|// Is it one we can do something useful with?
if|if
condition|(
name|rb
operator|.
name|id
operator|==
name|ResourceBlock
operator|.
name|ID_CAPTION
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|,
name|rb
operator|.
name|getDataAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rb
operator|.
name|id
operator|==
name|ResourceBlock
operator|.
name|ID_EXIF_1
condition|)
block|{
comment|// TODO Parse the EXIF info via ImageMetadataExtractor
block|}
elseif|else
if|if
condition|(
name|rb
operator|.
name|id
operator|==
name|ResourceBlock
operator|.
name|ID_EXIF_3
condition|)
block|{
comment|// TODO Parse the EXIF info via ImageMetadataExtractor
block|}
elseif|else
if|if
condition|(
name|rb
operator|.
name|id
operator|==
name|ResourceBlock
operator|.
name|ID_XMP
condition|)
block|{
comment|//if there are multiple xmps in a file, this will
comment|//overwrite the data from the earlier xmp
name|JempboxExtractor
name|ex
init|=
operator|new
name|JempboxExtractor
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|ex
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|rb
operator|.
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|blocks
operator|++
expr_stmt|;
block|}
comment|// Next is the Layer and Mask Info
comment|// Finally we have Image Data
comment|// We can't do anything with these parts
comment|// We don't have any helpful text, sorry...
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|ResourceBlock
block|{
specifier|private
specifier|static
specifier|final
name|long
name|SIGNATURE
init|=
literal|0x3842494d
decl_stmt|;
comment|// 8BIM
specifier|private
specifier|static
specifier|final
name|int
name|ID_CAPTION
init|=
literal|0x03F0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ID_EXIF_1
init|=
literal|0x0422
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ID_EXIF_3
init|=
literal|0x0423
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ID_XMP
init|=
literal|0x0424
decl_stmt|;
comment|//TODO
specifier|private
specifier|static
specifier|final
name|int
name|ID_URL
init|=
literal|0x040B
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ID_AUTO_SAVE_FILE_PATH
init|=
literal|0x043E
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ID_THUMBNAIL_RESOURCE
init|=
literal|0x040C
decl_stmt|;
specifier|private
name|int
name|id
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
name|int
name|totalLength
decl_stmt|;
specifier|static
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|private
name|ResourceBlock
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|counter
operator|++
expr_stmt|;
comment|// Verify the signature
name|long
name|sig
init|=
name|EndianUtils
operator|.
name|readIntBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|sig
operator|!=
name|SIGNATURE
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Invalid Image Resource Block Signature Found, got "
operator|+
name|sig
operator|+
literal|" 0x"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|sig
argument_list|)
operator|+
literal|" but the spec defines "
operator|+
name|SIGNATURE
argument_list|)
throw|;
block|}
comment|// Read the block
name|id
operator|=
name|EndianUtils
operator|.
name|readUShortBE
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|StringBuffer
name|nameB
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|nameLen
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|v
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|nameLen
operator|++
expr_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
condition|)
block|{
comment|// The name length is padded to be even
if|if
condition|(
name|nameLen
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|stream
operator|.
name|read
argument_list|()
expr_stmt|;
name|nameLen
operator|++
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
name|nameB
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|nameB
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|int
name|dataLen
init|=
name|EndianUtils
operator|.
name|readIntBE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataLen
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"data length must be>= 0: "
operator|+
name|dataLen
argument_list|)
throw|;
block|}
if|if
condition|(
name|dataLen
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
comment|// Data Length is even padded
name|dataLen
operator|=
name|dataLen
operator|+
literal|1
expr_stmt|;
block|}
comment|//protect against overflow
if|if
condition|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|dataLen
operator|<
name|nameLen
operator|+
literal|10
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"data length is too long:"
operator|+
name|dataLen
argument_list|)
throw|;
block|}
name|totalLength
operator|=
literal|4
operator|+
literal|2
operator|+
name|nameLen
operator|+
literal|4
operator|+
name|dataLen
expr_stmt|;
comment|// Do we have use for the data segment?
if|if
condition|(
name|captureData
argument_list|(
name|id
argument_list|)
condition|)
block|{
if|if
condition|(
name|dataLen
operator|>
name|MAX_DATA_LENGTH_BYTES
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"data length must be< "
operator|+
name|MAX_DATA_LENGTH_BYTES
operator|+
literal|": "
operator|+
name|dataLen
argument_list|)
throw|;
block|}
name|data
operator|=
operator|new
name|byte
index|[
name|dataLen
index|]
expr_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|stream
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * To save memory, only capture the data          * section of resource blocks we process          */
specifier|private
specifier|static
name|boolean
name|captureData
parameter_list|(
name|int
name|id
parameter_list|)
block|{
switch|switch
condition|(
name|id
condition|)
block|{
case|case
name|ID_CAPTION
case|:
case|case
name|ID_EXIF_1
case|:
case|case
name|ID_EXIF_3
case|:
case|case
name|ID_XMP
case|:
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|String
name|getDataAsString
parameter_list|()
block|{
comment|// Will be null padded
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
operator|-
literal|1
argument_list|,
name|US_ASCII
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

