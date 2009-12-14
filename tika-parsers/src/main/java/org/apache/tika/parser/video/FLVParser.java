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
name|video
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
name|Date
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Parser
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

begin_comment
comment|/**  *<p>  * Parser for metadata contained in Flash Videos (.flv). Resources:  * http://osflash.org/flv and for AMF:  * http://download.macromedia.com/pub/labs/amf/amf0_spec_121207.pdf  *<p>  * This parser is capable of extracting the general metadata from header as well  * as embedded metadata.  *<p>  * Known keys for metadata (from file header):  *<ol>  *<li>hasVideo: true|false  *<li>hasSound: true|false  *</ol>  *<p>  * In addition to the above values also metadata that is inserted in to the  * actual stream will be picked. Usually there are keys like:  * hasKeyframes, lastkeyframetimestamp, audiocodecid, keyframes, filepositions,  * hasMetadata, audiosamplerate, videodatarate metadatadate, videocodecid,  * metadatacreator, audiosize, hasVideo, height, audiosamplesize, framerate,  * hasCuePoints width, cuePoints, lasttimestamp, canSeekToEnd, datasize,  * duration, videosize, filesize, audiodatarate, hasAudio, stereo audiodelay  */
end_comment

begin_class
specifier|public
class|class
name|FLVParser
implements|implements
name|Parser
block|{
specifier|private
specifier|static
name|int
name|TYPE_METADATA
init|=
literal|0x12
decl_stmt|;
specifier|private
specifier|static
name|byte
name|MASK_AUDIO
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
name|byte
name|MASK_VIDEO
init|=
literal|4
decl_stmt|;
specifier|private
name|long
name|readUInt32
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|input
operator|.
name|readInt
argument_list|()
operator|&
literal|0xFFFFFFFFL
return|;
block|}
specifier|private
name|int
name|readUInt24
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|uint
init|=
name|input
operator|.
name|read
argument_list|()
operator|<<
literal|16
decl_stmt|;
name|uint
operator|+=
name|input
operator|.
name|read
argument_list|()
operator|<<
literal|8
expr_stmt|;
name|uint
operator|+=
name|input
operator|.
name|read
argument_list|()
expr_stmt|;
return|return
name|uint
return|;
block|}
specifier|private
name|Object
name|readAMFData
parameter_list|(
name|DataInputStream
name|input
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|type
operator|==
operator|-
literal|1
condition|)
block|{
name|type
operator|=
name|input
operator|.
name|readUnsignedByte
argument_list|()
expr_stmt|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
literal|0
case|:
return|return
name|input
operator|.
name|readDouble
argument_list|()
return|;
case|case
literal|1
case|:
return|return
name|input
operator|.
name|readUnsignedByte
argument_list|()
operator|==
literal|1
return|;
case|case
literal|2
case|:
return|return
name|readAMFString
argument_list|(
name|input
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|readAMFObject
argument_list|(
name|input
argument_list|)
return|;
case|case
literal|8
case|:
return|return
name|readAMFEcmaArray
argument_list|(
name|input
argument_list|)
return|;
case|case
literal|10
case|:
return|return
name|readAMFStrictArray
argument_list|(
name|input
argument_list|)
return|;
case|case
literal|11
case|:
specifier|final
name|Date
name|date
init|=
operator|new
name|Date
argument_list|(
operator|(
name|long
operator|)
name|input
operator|.
name|readDouble
argument_list|()
argument_list|)
decl_stmt|;
name|input
operator|.
name|skip
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// time zone
return|return
name|date
return|;
case|case
literal|13
case|:
return|return
literal|"UNDEFINED"
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|Object
name|readAMFStrictArray
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|count
init|=
name|readUInt32
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|readAMFData
argument_list|(
name|input
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|private
name|String
name|readAMFString
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|input
operator|.
name|readUnsignedShort
argument_list|()
decl_stmt|;
name|byte
index|[]
name|chars
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|input
operator|.
name|readFully
argument_list|(
name|chars
argument_list|)
expr_stmt|;
name|String
name|value
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
return|return
name|value
return|;
block|}
specifier|private
name|Object
name|readAMFObject
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|array
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|key
init|=
name|readAMFString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|int
name|dataType
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|dataType
operator|==
literal|9
condition|)
block|{
comment|// object end marker
break|break;
block|}
name|array
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|readAMFData
argument_list|(
name|input
argument_list|,
name|dataType
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
specifier|private
name|Object
name|readAMFEcmaArray
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|size
init|=
name|readUInt32
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|array
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|readAMFString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|int
name|dataType
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
name|array
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|readAMFData
argument_list|(
name|input
argument_list|,
name|dataType
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
specifier|private
name|boolean
name|checkSignature
parameter_list|(
name|DataInputStream
name|fis
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fis
operator|.
name|read
argument_list|()
operator|==
literal|'F'
operator|&&
name|fis
operator|.
name|read
argument_list|()
operator|==
literal|'L'
operator|&&
name|fis
operator|.
name|read
argument_list|()
operator|==
literal|'V'
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
name|DataInputStream
name|datainput
init|=
operator|new
name|DataInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|checkSignature
argument_list|(
name|datainput
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"FLV signature not detected"
argument_list|)
throw|;
block|}
comment|// header
name|int
name|version
init|=
name|datainput
operator|.
name|readUnsignedByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|1
condition|)
block|{
comment|// should be 1, perhaps this is not flv?
return|return;
block|}
name|int
name|typeFlags
init|=
name|datainput
operator|.
name|readUnsignedByte
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"hasVideo"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
operator|(
name|typeFlags
operator|&
name|MASK_VIDEO
operator|)
operator|!=
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"hasAudio"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
operator|(
name|typeFlags
operator|&
name|MASK_AUDIO
operator|)
operator|!=
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|len
init|=
name|readUInt32
argument_list|(
name|datainput
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|!=
literal|9
condition|)
block|{
comment|// we only know about format with header of 9 bytes
return|return;
block|}
name|long
name|sizePrev
init|=
name|readUInt32
argument_list|(
name|datainput
argument_list|)
decl_stmt|;
if|if
condition|(
name|sizePrev
operator|!=
literal|0
condition|)
block|{
comment|// should be 0, perhaps this is not flv?
return|return;
block|}
comment|// flv tag stream follows...
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|type
init|=
name|datainput
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
operator|-
literal|1
condition|)
block|{
comment|// EOF
break|break;
block|}
name|int
name|datalen
init|=
name|readUInt24
argument_list|(
name|datainput
argument_list|)
decl_stmt|;
comment|//body length
name|stream
operator|.
name|skip
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// timestamp
name|stream
operator|.
name|skip
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// streamid
if|if
condition|(
name|type
operator|==
name|TYPE_METADATA
condition|)
block|{
comment|// found metadata Tag, read content to buffer
name|byte
index|[]
name|metaBytes
init|=
operator|new
name|byte
index|[
name|datalen
index|]
decl_stmt|;
for|for
control|(
name|int
name|readCount
init|=
literal|0
init|;
name|readCount
operator|<
name|datalen
condition|;
control|)
block|{
name|int
name|r
init|=
name|stream
operator|.
name|read
argument_list|(
name|metaBytes
argument_list|,
name|readCount
argument_list|,
name|datalen
operator|-
name|readCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
operator|-
literal|1
condition|)
block|{
name|readCount
operator|+=
name|r
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|metaBytes
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|Object
name|data
init|=
literal|null
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|data
operator|=
name|readAMFData
argument_list|(
name|dis
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|data
operator|instanceof
name|Map
condition|)
block|{
comment|// TODO if there are multiple metadata values with same key (in
comment|// separate AMF blocks, we currently loose previous values)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|extractedMetadata
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|extractedMetadata
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// Tag was not metadata, skip over data we cannot handle
for|for
control|(
name|int
name|skiplen
init|=
literal|0
init|;
name|skiplen
operator|<
name|datalen
condition|;
control|)
block|{
name|long
name|currentSkipLen
init|=
name|datainput
operator|.
name|skip
argument_list|(
name|datalen
operator|-
name|skiplen
argument_list|)
decl_stmt|;
name|skiplen
operator|+=
name|currentSkipLen
expr_stmt|;
block|}
block|}
name|sizePrev
operator|=
name|readUInt32
argument_list|(
name|datainput
argument_list|)
expr_stmt|;
comment|// previous block size
if|if
condition|(
name|sizePrev
operator|!=
name|datalen
operator|+
literal|11
condition|)
block|{
comment|// file was corrupt or we could not parse it...
break|break;
block|}
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

