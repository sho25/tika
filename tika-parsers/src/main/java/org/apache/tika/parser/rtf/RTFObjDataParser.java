begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|rtf
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
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|UnsupportedEncodingException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|FilenameUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|DirectoryNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|DocumentEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|DocumentInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
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
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|FileMagic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|POIFSFileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|Ole10Native
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|Ole10NativeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|util
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
name|exception
operator|.
name|TikaMemoryLimitException
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
name|BoundedInputStream
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
name|RTFMetadata
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
name|microsoft
operator|.
name|OfficeParser
operator|.
name|POIFSDocumentType
import|;
end_import

begin_comment
comment|/**  * Many thanks to Simon Mourier for:  * http://stackoverflow.com/questions/14779647/extract-embedded-image-object-in-rtf  * and for granting permission to use his code in Tika.  */
end_comment

begin_class
class|class
name|RTFObjDataParser
block|{
specifier|private
specifier|final
specifier|static
name|String
name|WIN_ASCII
init|=
literal|"WINDOWS-1252"
decl_stmt|;
specifier|private
specifier|final
name|int
name|memoryLimitInKb
decl_stmt|;
name|RTFObjDataParser
parameter_list|(
name|int
name|memoryLimitInKb
parameter_list|)
block|{
name|this
operator|.
name|memoryLimitInKb
operator|=
name|memoryLimitInKb
expr_stmt|;
block|}
comment|/**      * Parses the embedded object/pict string      *      * @param bytes actual bytes (already converted from the       *  hex pair string stored in the embedded object data into actual bytes or read      *  as raw binary bytes)      * @return a SimpleRTFEmbObj or null      * @throws IOException if there are any surprise surprises during parsing      */
comment|/**      * @param bytes      * @param metadata             incoming metadata      * @param unknownFilenameCount      * @return byte[] for contents of obj data      * @throws IOException      */
specifier|protected
name|byte
index|[]
name|parse
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|AtomicInteger
name|unknownFilenameCount
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|long
name|version
init|=
name|readUInt
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|RTFMetadata
operator|.
name|EMB_APP_VERSION
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|formatId
init|=
name|readUInt
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|//2 is an embedded object. 1 is a link.
if|if
condition|(
name|formatId
operator|!=
literal|2L
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|className
init|=
name|readLengthPrefixedAnsiString
argument_list|(
name|is
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|topicName
init|=
name|readLengthPrefixedAnsiString
argument_list|(
name|is
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|itemName
init|=
name|readLengthPrefixedAnsiString
argument_list|(
name|is
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
operator|&&
name|className
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|RTFMetadata
operator|.
name|EMB_CLASS
argument_list|,
name|className
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|topicName
operator|!=
literal|null
operator|&&
name|topicName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|RTFMetadata
operator|.
name|EMB_TOPIC
argument_list|,
name|topicName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|itemName
operator|!=
literal|null
operator|&&
name|itemName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|RTFMetadata
operator|.
name|EMB_ITEM
argument_list|,
name|itemName
argument_list|)
expr_stmt|;
block|}
name|long
name|dataSz
init|=
name|readUInt
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|//readBytes tests for reading too many bytes
name|byte
index|[]
name|embObjBytes
init|=
name|readBytes
argument_list|(
name|is
argument_list|,
name|dataSz
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|equals
argument_list|(
literal|"package"
argument_list|)
condition|)
block|{
return|return
name|handlePackage
argument_list|(
name|embObjBytes
argument_list|,
name|metadata
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|className
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|equals
argument_list|(
literal|"pbrush"
argument_list|)
condition|)
block|{
comment|//simple bitmap bytes
return|return
name|embObjBytes
return|;
block|}
else|else
block|{
name|ByteArrayInputStream
name|embIs
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|embObjBytes
argument_list|)
decl_stmt|;
name|boolean
name|hasPoifs
init|=
literal|false
decl_stmt|;
try|try
block|{
name|hasPoifs
operator|=
name|hasPOIFSHeader
argument_list|(
name|embIs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordEmbeddedStreamException
argument_list|(
name|e
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
return|return
name|embObjBytes
return|;
block|}
if|if
condition|(
name|hasPoifs
condition|)
block|{
try|try
block|{
return|return
name|handleEmbeddedPOIFS
argument_list|(
name|embIs
argument_list|,
name|metadata
argument_list|,
name|unknownFilenameCount
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordEmbeddedStreamException
argument_list|(
name|e
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|embObjBytes
return|;
block|}
comment|//will throw IOException if not actually POIFS
comment|//can return null byte[]
specifier|private
name|byte
index|[]
name|handleEmbeddedPOIFS
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|AtomicInteger
name|unknownFilenameCount
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|byte
index|[]
name|ret
init|=
literal|null
decl_stmt|;
try|try
init|(
name|POIFSFileSystem
name|fs
init|=
operator|new
name|POIFSFileSystem
argument_list|(
name|is
argument_list|)
init|)
block|{
name|DirectoryNode
name|root
init|=
name|fs
operator|.
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
return|return
name|ret
return|;
block|}
if|if
condition|(
name|root
operator|.
name|hasEntry
argument_list|(
literal|"Package"
argument_list|)
condition|)
block|{
name|Entry
name|ooxml
init|=
name|root
operator|.
name|getEntry
argument_list|(
literal|"Package"
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|BoundedInputStream
name|bis
init|=
operator|new
name|BoundedInputStream
argument_list|(
name|memoryLimitInKb
operator|*
literal|1024
argument_list|,
operator|new
name|DocumentInputStream
argument_list|(
operator|(
name|DocumentEntry
operator|)
name|ooxml
argument_list|)
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|bis
argument_list|,
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|bis
operator|.
name|hasHitBound
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TikaMemoryLimitException
argument_list|(
literal|"Hit memory limit exception. Tried to copy> "
operator|+
name|memoryLimitInKb
operator|*
literal|1024
argument_list|)
throw|;
block|}
block|}
name|ret
operator|=
name|out
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//try poifs
name|POIFSDocumentType
name|type
init|=
name|POIFSDocumentType
operator|.
name|detectType
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|POIFSDocumentType
operator|.
name|OLE10_NATIVE
condition|)
block|{
try|try
block|{
comment|// Try to un-wrap the OLE10Native record:
name|Ole10Native
name|ole
init|=
name|Ole10Native
operator|.
name|createFromEmbeddedOleObject
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|ret
operator|=
name|ole
operator|.
name|getDataBuffer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Ole10NativeException
name|ex
parameter_list|)
block|{
comment|// Not a valid OLE10Native record, skip it
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|POIFSDocumentType
operator|.
name|COMP_OBJ
condition|)
block|{
name|DocumentEntry
name|contentsEntry
decl_stmt|;
try|try
block|{
name|contentsEntry
operator|=
operator|(
name|DocumentEntry
operator|)
name|root
operator|.
name|getEntry
argument_list|(
literal|"CONTENTS"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ioe
parameter_list|)
block|{
name|contentsEntry
operator|=
operator|(
name|DocumentEntry
operator|)
name|root
operator|.
name|getEntry
argument_list|(
literal|"Contents"
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|DocumentInputStream
name|inp
init|=
operator|new
name|DocumentInputStream
argument_list|(
name|contentsEntry
argument_list|)
init|)
block|{
name|ret
operator|=
operator|new
name|byte
index|[
name|contentsEntry
operator|.
name|getSize
argument_list|()
index|]
expr_stmt|;
name|inp
operator|.
name|readFully
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|is
operator|.
name|reset
argument_list|()
expr_stmt|;
name|BoundedInputStream
name|bis
init|=
operator|new
name|BoundedInputStream
argument_list|(
name|memoryLimitInKb
operator|*
literal|1024
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|bis
operator|.
name|hasHitBound
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TikaMemoryLimitException
argument_list|(
literal|"Hit memory limit exception. Tried to copy> "
operator|+
name|memoryLimitInKb
operator|*
literal|1024
argument_list|)
throw|;
block|}
name|ret
operator|=
name|out
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"file_"
operator|+
name|unknownFilenameCount
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|"."
operator|+
name|type
operator|.
name|getExtension
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
comment|/**      * can return null if there is a linked object      * instead of an embedded file      */
specifier|private
name|byte
index|[]
name|handlePackage
parameter_list|(
name|byte
index|[]
name|pkgBytes
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
comment|//now parse the package header
name|ByteArrayInputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|pkgBytes
argument_list|)
decl_stmt|;
name|readUShort
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|String
name|displayName
init|=
name|readAnsiString
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|//should we add this to the metadata?
name|readAnsiString
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|//iconFilePath
try|try
block|{
comment|//iconIndex
name|EndianUtils
operator|.
name|readUShortBE
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EndianUtils
operator|.
name|BufferUnderrunException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|int
name|type
init|=
name|readUShort
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|//type
comment|//1 is link, 3 is embedded object
comment|//this only handles embedded objects
if|if
condition|(
name|type
operator|!=
literal|3
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|//should we really be ignoring this filePathLen?
name|readUInt
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|//filePathLen
name|String
name|ansiFilePath
init|=
name|readAnsiString
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|//filePath
name|long
name|bytesLen
init|=
name|readUInt
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|byte
index|[]
name|objBytes
init|=
name|initByteArray
argument_list|(
name|bytesLen
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|is
argument_list|,
name|objBytes
argument_list|)
expr_stmt|;
name|StringBuilder
name|unicodeFilePath
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|long
name|unicodeLen
init|=
name|readUInt
argument_list|(
name|is
argument_list|)
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
name|unicodeLen
condition|;
name|i
operator|++
control|)
block|{
name|int
name|lo
init|=
name|is
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|hi
init|=
name|is
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|sum
init|=
name|lo
operator|+
literal|256
operator|*
name|hi
decl_stmt|;
if|if
condition|(
name|hi
operator|==
operator|-
literal|1
operator|||
name|lo
operator|==
operator|-
literal|1
condition|)
block|{
comment|//stream ran out; empty SB and stop
name|unicodeFilePath
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
block|}
name|unicodeFilePath
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|sum
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//swallow; the unicode file path is optional and might not happen
name|unicodeFilePath
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|String
name|fileNameToUse
init|=
literal|""
decl_stmt|;
name|String
name|pathToUse
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|unicodeFilePath
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|p
init|=
name|unicodeFilePath
operator|.
name|toString
argument_list|()
decl_stmt|;
name|fileNameToUse
operator|=
name|p
expr_stmt|;
name|pathToUse
operator|=
name|p
expr_stmt|;
block|}
else|else
block|{
name|fileNameToUse
operator|=
name|displayName
operator|==
literal|null
condition|?
literal|""
else|:
name|displayName
expr_stmt|;
name|pathToUse
operator|=
name|ansiFilePath
operator|==
literal|null
condition|?
literal|""
else|:
name|ansiFilePath
expr_stmt|;
block|}
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|,
name|fileNameToUse
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|FilenameUtils
operator|.
name|getName
argument_list|(
name|fileNameToUse
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|EMBEDDED_RELATIONSHIP_ID
argument_list|,
name|pathToUse
argument_list|)
expr_stmt|;
return|return
name|objBytes
return|;
block|}
specifier|private
name|int
name|readUShort
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|EndianUtils
operator|.
name|readUShortLE
argument_list|(
name|is
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EndianUtils
operator|.
name|BufferUnderrunException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|long
name|readUInt
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|EndianUtils
operator|.
name|readUIntLE
argument_list|(
name|is
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EndianUtils
operator|.
name|BufferUnderrunException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|String
name|readAnsiString
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|c
init|=
name|is
operator|.
name|read
argument_list|()
decl_stmt|;
while|while
condition|(
name|c
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Hit end of stream before end of AnsiString"
argument_list|)
throw|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|String
name|readLengthPrefixedAnsiString
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|long
name|len
init|=
name|readUInt
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|readBytes
argument_list|(
name|is
argument_list|,
name|len
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|WIN_ASCII
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
comment|//shouldn't ever happen
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported encoding"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|byte
index|[]
name|readBytes
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
comment|//initByteArray tests for "reading of too many bytes"
name|byte
index|[]
name|bytes
init|=
name|initByteArray
argument_list|(
name|len
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|is
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
specifier|private
name|byte
index|[]
name|initByteArray
parameter_list|(
name|long
name|len
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Requested length for reading bytes< 0?!: "
operator|+
name|len
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|memoryLimitInKb
operator|>
operator|-
literal|1
operator|&&
name|len
operator|>
name|memoryLimitInKb
operator|*
literal|1024
condition|)
block|{
throw|throw
operator|new
name|TikaMemoryLimitException
argument_list|(
literal|"File embedded in RTF caused this ("
operator|+
name|len
operator|+
literal|") bytes), but maximum allowed is ("
operator|+
operator|(
name|memoryLimitInKb
operator|*
literal|1024
operator|)
operator|+
literal|")."
operator|+
literal|"If this is a valid RTF file, consider increasing the memory limit via TikaConfig."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|len
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|TikaMemoryLimitException
argument_list|(
literal|"File embedded in RTF caused this ("
operator|+
name|len
operator|+
literal|") bytes), but there is a hard limit of Integer.MAX_VALUE+"
argument_list|)
throw|;
block|}
return|return
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|len
index|]
return|;
block|}
specifier|private
specifier|static
name|boolean
name|hasPOIFSHeader
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileMagic
operator|.
name|valueOf
argument_list|(
name|is
argument_list|)
operator|==
name|FileMagic
operator|.
name|OLE2
return|;
block|}
block|}
end_class

end_unit

