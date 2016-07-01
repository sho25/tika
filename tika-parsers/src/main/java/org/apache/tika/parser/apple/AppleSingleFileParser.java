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
name|apple
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Comparator
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
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|CloseShieldInputStream
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
name|ParsingEmbeddedDocumentExtractor
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
comment|/**  * Parser that strips the header off of AppleSingle and AppleDouble  * files.  *<p>  * See<a href="http://kaiser-edv.de/documents/AppleSingle_AppleDouble.pdf">spec document</a>.  */
end_comment

begin_class
specifier|public
class|class
name|AppleSingleFileParser
extends|extends
name|AbstractParser
block|{
comment|/**      * Entry types      */
specifier|private
specifier|static
specifier|final
name|int
name|DATA_FORK
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|RESOURCE_FORK
init|=
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|REAL_NAME
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|COMMENT
init|=
literal|4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ICON_BW
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ICON_COLOR
init|=
literal|6
decl_stmt|;
comment|//7?!
specifier|private
specifier|static
specifier|final
name|int
name|FILE_DATES_INFO
init|=
literal|8
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|FINDER_INFO
init|=
literal|9
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MACINTOSH_FILE_INFO
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PRODOS_FILE_INFO
init|=
literal|11
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MSDOS_FILE_INFO
init|=
literal|12
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|SHORT_NAME
init|=
literal|13
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|AFP_FILE_INFO
init|=
literal|14
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DIRECTORY_ID
init|=
literal|15
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
name|singleton
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"applefile"
argument_list|)
argument_list|)
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
annotation|@
name|Override
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
name|EmbeddedDocumentExtractor
name|ex
init|=
name|context
operator|.
name|get
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|ex
operator|==
literal|null
condition|)
block|{
name|ex
operator|=
operator|new
name|ParsingEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|short
name|numEntries
init|=
name|readThroughNumEntries
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|long
name|bytesRead
init|=
literal|26
decl_stmt|;
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|fieldInfoList
init|=
name|getSortedFieldInfoList
argument_list|(
name|stream
argument_list|,
name|numEntries
argument_list|)
decl_stmt|;
name|bytesRead
operator|+=
literal|12
operator|*
name|numEntries
expr_stmt|;
name|Metadata
name|embeddedMetadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|bytesRead
operator|=
name|processFieldEntries
argument_list|(
name|stream
argument_list|,
name|fieldInfoList
argument_list|,
name|embeddedMetadata
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|FieldInfo
name|contentFieldInfo
init|=
name|getContentFieldInfo
argument_list|(
name|fieldInfoList
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|contentFieldInfo
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|contentFieldInfo
operator|.
name|offset
operator|+
literal|" "
operator|+
name|bytesRead
argument_list|)
expr_stmt|;
name|long
name|diff
init|=
name|contentFieldInfo
operator|.
name|offset
operator|-
name|bytesRead
decl_stmt|;
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|stream
argument_list|,
name|diff
argument_list|)
expr_stmt|;
if|if
condition|(
name|ex
operator|.
name|shouldParseEmbedded
argument_list|(
name|embeddedMetadata
argument_list|)
condition|)
block|{
comment|// TODO: we should probably add a readlimiting wrapper around this
comment|// stream to ensure that not more than contentFieldInfo.length bytes
comment|// are read
name|ex
operator|.
name|parseEmbedded
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|xhtml
argument_list|,
name|embeddedMetadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|FieldInfo
name|getContentFieldInfo
parameter_list|(
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|fieldInfoList
parameter_list|)
block|{
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|fieldInfoList
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|entryId
operator|==
literal|1
condition|)
block|{
return|return
name|fieldInfo
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|long
name|processFieldEntries
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|fieldInfoList
parameter_list|,
name|Metadata
name|embeddedMetadata
parameter_list|,
name|long
name|bytesRead
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|byte
index|[]
name|buffer
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FieldInfo
name|f
range|:
name|fieldInfoList
control|)
block|{
name|long
name|diff
init|=
name|f
operator|.
name|offset
operator|-
name|bytesRead
decl_stmt|;
comment|//just in case
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|stream
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
name|diff
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|entryId
operator|==
name|REAL_NAME
condition|)
block|{
if|if
condition|(
name|f
operator|.
name|length
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"File name length can't be> integer max"
argument_list|)
throw|;
block|}
name|buffer
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|f
operator|.
name|length
index|]
expr_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
name|f
operator|.
name|length
expr_stmt|;
name|String
name|originalFileName
init|=
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
decl_stmt|;
name|embeddedMetadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|,
name|originalFileName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|f
operator|.
name|entryId
operator|!=
name|DATA_FORK
condition|)
block|{
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|stream
argument_list|,
name|f
operator|.
name|length
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
name|f
operator|.
name|length
expr_stmt|;
block|}
block|}
return|return
name|bytesRead
return|;
block|}
specifier|private
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|getSortedFieldInfoList
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|short
name|numEntries
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
comment|//this is probably overkill.  I'd hope that these were already
comment|//in order.  This ensures it.
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|fieldInfoList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numEntries
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
name|numEntries
condition|;
name|i
operator|++
control|)
block|{
comment|//convert 32-bit unsigned ints to longs
name|fieldInfoList
operator|.
name|add
argument_list|(
operator|new
name|FieldInfo
argument_list|(
name|EndianUtils
operator|.
name|readUIntBE
argument_list|(
name|stream
argument_list|)
argument_list|,
comment|//entry id
name|EndianUtils
operator|.
name|readUIntBE
argument_list|(
name|stream
argument_list|)
argument_list|,
comment|//offset
name|EndianUtils
operator|.
name|readUIntBE
argument_list|(
name|stream
argument_list|)
comment|//length
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldInfoList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"AppleSingleFile missing field info"
argument_list|)
throw|;
block|}
comment|//make absolutely sure these are in order!
name|Collections
operator|.
name|sort
argument_list|(
name|fieldInfoList
argument_list|,
operator|new
name|FieldInfoComparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|fieldInfoList
return|;
block|}
comment|//read through header until you hit the number of entries
specifier|private
name|short
name|readThroughNumEntries
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
comment|//mime
name|EndianUtils
operator|.
name|readIntBE
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|//version
name|long
name|version
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
name|version
operator|!=
literal|0x00020000
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Version should have been 0x00020000, but was:"
operator|+
name|version
argument_list|)
throw|;
block|}
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|stream
argument_list|,
literal|16
argument_list|)
expr_stmt|;
comment|//filler
return|return
name|EndianUtils
operator|.
name|readShortBE
argument_list|(
name|stream
argument_list|)
return|;
comment|//number of entries
block|}
specifier|private
class|class
name|FieldInfo
block|{
specifier|private
specifier|final
name|long
name|entryId
decl_stmt|;
specifier|private
specifier|final
name|long
name|offset
decl_stmt|;
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
specifier|private
name|FieldInfo
parameter_list|(
name|long
name|entryId
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|entryId
operator|=
name|entryId
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|FieldInfoComparator
implements|implements
name|Comparator
argument_list|<
name|FieldInfo
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FieldInfo
name|o1
parameter_list|,
name|FieldInfo
name|o2
parameter_list|)
block|{
return|return
operator|(
name|o1
operator|.
name|offset
operator|>
name|o2
operator|.
name|offset
operator|)
condition|?
literal|1
else|:
operator|(
name|o1
operator|.
name|offset
operator|==
name|o2
operator|.
name|offset
operator|)
condition|?
literal|0
else|:
operator|-
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

