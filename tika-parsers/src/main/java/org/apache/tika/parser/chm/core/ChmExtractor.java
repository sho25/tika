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
name|chm
operator|.
name|core
package|;
end_package

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
name|List
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
name|parser
operator|.
name|chm
operator|.
name|accessor
operator|.
name|ChmDirectoryListingSet
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
name|chm
operator|.
name|accessor
operator|.
name|ChmItsfHeader
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
name|chm
operator|.
name|accessor
operator|.
name|ChmItspHeader
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
name|chm
operator|.
name|accessor
operator|.
name|ChmLzxcControlData
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
name|chm
operator|.
name|accessor
operator|.
name|ChmLzxcResetTable
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
name|chm
operator|.
name|accessor
operator|.
name|DirectoryListingEntry
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
name|chm
operator|.
name|assertion
operator|.
name|ChmAssert
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
name|chm
operator|.
name|core
operator|.
name|ChmCommons
operator|.
name|EntryType
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
name|chm
operator|.
name|lzx
operator|.
name|ChmBlockInfo
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
name|chm
operator|.
name|lzx
operator|.
name|ChmLzxBlock
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
name|ooxml
operator|.
name|xwpf
operator|.
name|XWPFEventBasedWordExtractor
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
comment|/**  * Extracts text from chm file. Enumerates chm entries.  */
end_comment

begin_class
specifier|public
class|class
name|ChmExtractor
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
name|ChmExtractor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|List
argument_list|<
name|ChmLzxBlock
argument_list|>
name|lzxBlocksCache
init|=
literal|null
decl_stmt|;
specifier|private
name|ChmDirectoryListingSet
name|chmDirList
init|=
literal|null
decl_stmt|;
specifier|private
name|ChmItsfHeader
name|chmItsfHeader
init|=
literal|null
decl_stmt|;
specifier|private
name|ChmItspHeader
name|chmItspHeader
init|=
literal|null
decl_stmt|;
specifier|private
name|ChmLzxcResetTable
name|chmLzxcResetTable
init|=
literal|null
decl_stmt|;
specifier|private
name|ChmLzxcControlData
name|chmLzxcControlData
init|=
literal|null
decl_stmt|;
specifier|private
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|indexOfContent
decl_stmt|;
specifier|private
name|long
name|lzxBlockOffset
decl_stmt|;
specifier|private
name|long
name|lzxBlockLength
decl_stmt|;
specifier|private
specifier|final
name|ChmBlockInfo
name|chmBlockInfo
init|=
operator|new
name|ChmBlockInfo
argument_list|()
decl_stmt|;
comment|/**      * Returns lzxc control data.      *       * @return ChmLzxcControlData      */
specifier|private
name|ChmLzxcControlData
name|getChmLzxcControlData
parameter_list|()
block|{
return|return
name|chmLzxcControlData
return|;
block|}
comment|/**      * Sets lzxc control data      *       * @param chmLzxcControlData      */
specifier|private
name|void
name|setChmLzxcControlData
parameter_list|(
name|ChmLzxcControlData
name|chmLzxcControlData
parameter_list|)
block|{
name|this
operator|.
name|chmLzxcControlData
operator|=
name|chmLzxcControlData
expr_stmt|;
block|}
specifier|private
name|ChmItspHeader
name|getChmItspHeader
parameter_list|()
block|{
return|return
name|chmItspHeader
return|;
block|}
specifier|private
name|void
name|setChmItspHeader
parameter_list|(
name|ChmItspHeader
name|chmItspHeader
parameter_list|)
block|{
name|this
operator|.
name|chmItspHeader
operator|=
name|chmItspHeader
expr_stmt|;
block|}
comment|/**      * Returns lzxc reset table      *       * @return ChmLzxcResetTable      */
specifier|private
name|ChmLzxcResetTable
name|getChmLzxcResetTable
parameter_list|()
block|{
return|return
name|chmLzxcResetTable
return|;
block|}
comment|/**      * Sets lzxc reset table      *       * @param chmLzxcResetTable      */
specifier|private
name|void
name|setChmLzxcResetTable
parameter_list|(
name|ChmLzxcResetTable
name|chmLzxcResetTable
parameter_list|)
block|{
name|this
operator|.
name|chmLzxcResetTable
operator|=
name|chmLzxcResetTable
expr_stmt|;
block|}
comment|/**      * Returns lzxc hit_cache length      *       * @return lzxBlockLength      */
specifier|private
name|long
name|getLzxBlockLength
parameter_list|()
block|{
return|return
name|lzxBlockLength
return|;
block|}
comment|/**      * Sets lzxc hit_cache length      *       * @param lzxBlockLength      */
specifier|private
name|void
name|setLzxBlockLength
parameter_list|(
name|long
name|lzxBlockLength
parameter_list|)
block|{
name|this
operator|.
name|lzxBlockLength
operator|=
name|lzxBlockLength
expr_stmt|;
block|}
comment|/**      * Returns lzxc hit_cache offset      *       * @return lzxBlockOffset      */
specifier|private
name|long
name|getLzxBlockOffset
parameter_list|()
block|{
return|return
name|lzxBlockOffset
return|;
block|}
comment|/**      * Sets lzxc hit_cache offset      */
specifier|private
name|void
name|setLzxBlockOffset
parameter_list|(
name|long
name|lzxBlockOffset
parameter_list|)
block|{
name|this
operator|.
name|lzxBlockOffset
operator|=
name|lzxBlockOffset
expr_stmt|;
block|}
specifier|private
name|int
name|getIndexOfContent
parameter_list|()
block|{
return|return
name|indexOfContent
return|;
block|}
specifier|private
name|void
name|setIndexOfContent
parameter_list|(
name|int
name|indexOfContent
parameter_list|)
block|{
name|this
operator|.
name|indexOfContent
operator|=
name|indexOfContent
expr_stmt|;
block|}
specifier|private
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
specifier|private
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|ChmExtractor
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|ChmAssert
operator|.
name|assertInputStreamNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
try|try
block|{
name|setData
argument_list|(
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
comment|/* Creates and parses chm itsf header */
name|setChmItsfHeader
argument_list|(
operator|new
name|ChmItsfHeader
argument_list|()
argument_list|)
expr_stmt|;
comment|// getChmItsfHeader().parse(Arrays.copyOfRange(getData(), 0,
comment|// ChmConstants.CHM_ITSF_V3_LEN - 1), getChmItsfHeader());
name|getChmItsfHeader
argument_list|()
operator|.
name|parse
argument_list|(
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|ChmConstants
operator|.
name|CHM_ITSF_V3_LEN
operator|-
literal|1
argument_list|)
argument_list|,
name|getChmItsfHeader
argument_list|()
argument_list|)
expr_stmt|;
comment|/* Creates and parses chm itsp header */
name|setChmItspHeader
argument_list|(
operator|new
name|ChmItspHeader
argument_list|()
argument_list|)
expr_stmt|;
comment|// getChmItspHeader().parse(Arrays.copyOfRange( getData(), (int)
comment|// getChmItsfHeader().getDirOffset(),
comment|// (int) getChmItsfHeader().getDirOffset() +
comment|// ChmConstants.CHM_ITSP_V1_LEN), getChmItspHeader());
name|getChmItspHeader
argument_list|()
operator|.
name|parse
argument_list|(
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|getData
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|getChmItsfHeader
argument_list|()
operator|.
name|getDirOffset
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|getChmItsfHeader
argument_list|()
operator|.
name|getDirOffset
argument_list|()
operator|+
name|ChmConstants
operator|.
name|CHM_ITSP_V1_LEN
argument_list|)
argument_list|,
name|getChmItspHeader
argument_list|()
argument_list|)
expr_stmt|;
comment|/* Creates instance of ChmDirListingContainer */
name|setChmDirList
argument_list|(
operator|new
name|ChmDirectoryListingSet
argument_list|(
name|getData
argument_list|()
argument_list|,
name|getChmItsfHeader
argument_list|()
argument_list|,
name|getChmItspHeader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|indexOfControlData
init|=
name|getChmDirList
argument_list|()
operator|.
name|getControlDataIndex
argument_list|()
decl_stmt|;
name|int
name|indexOfResetData
init|=
name|ChmCommons
operator|.
name|indexOfResetTableBlock
argument_list|(
name|getData
argument_list|()
argument_list|,
name|ChmConstants
operator|.
name|LZXC
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dir_chunk
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|indexOfResetData
operator|>
literal|0
condition|)
name|dir_chunk
operator|=
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|getData
argument_list|()
argument_list|,
name|indexOfResetData
argument_list|,
name|indexOfResetData
operator|+
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
operator|.
name|get
argument_list|(
name|indexOfControlData
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// dir_chunk = Arrays.copyOfRange(getData(), indexOfResetData,
comment|// indexOfResetData
comment|// +
comment|// getChmDirList().getDirectoryListingEntryList().get(indexOfControlData).getLength());
comment|/* Creates and parses chm control data */
name|setChmLzxcControlData
argument_list|(
operator|new
name|ChmLzxcControlData
argument_list|()
argument_list|)
expr_stmt|;
name|getChmLzxcControlData
argument_list|()
operator|.
name|parse
argument_list|(
name|dir_chunk
argument_list|,
name|getChmLzxcControlData
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|indexOfResetTable
init|=
name|getChmDirList
argument_list|()
operator|.
name|getResetTableIndex
argument_list|()
decl_stmt|;
name|setChmLzxcResetTable
argument_list|(
operator|new
name|ChmLzxcResetTable
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|startIndex
init|=
operator|(
name|int
operator|)
name|getChmDirList
argument_list|()
operator|.
name|getDataOffset
argument_list|()
operator|+
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
operator|.
name|get
argument_list|(
name|indexOfResetTable
argument_list|)
operator|.
name|getOffset
argument_list|()
decl_stmt|;
comment|// assert startIndex< data.length
name|ChmAssert
operator|.
name|assertCopyingDataIndex
argument_list|(
name|startIndex
argument_list|,
name|getData
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// dir_chunk = Arrays.copyOfRange(getData(), startIndex, startIndex
comment|// +
comment|// getChmDirList().getDirectoryListingEntryList().get(indexOfResetTable).getLength());
name|dir_chunk
operator|=
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|getData
argument_list|()
argument_list|,
name|startIndex
argument_list|,
name|startIndex
operator|+
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
operator|.
name|get
argument_list|(
name|indexOfResetTable
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|getChmLzxcResetTable
argument_list|()
operator|.
name|parse
argument_list|(
name|dir_chunk
argument_list|,
name|getChmLzxcResetTable
argument_list|()
argument_list|)
expr_stmt|;
name|setIndexOfContent
argument_list|(
name|ChmCommons
operator|.
name|indexOf
argument_list|(
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
argument_list|,
name|ChmConstants
operator|.
name|CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|setLzxBlockOffset
argument_list|(
operator|(
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
operator|.
name|get
argument_list|(
name|getIndexOfContent
argument_list|()
argument_list|)
operator|.
name|getOffset
argument_list|()
operator|+
name|getChmItsfHeader
argument_list|()
operator|.
name|getDataOffset
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|setLzxBlockLength
argument_list|(
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
operator|.
name|get
argument_list|(
name|getIndexOfContent
argument_list|()
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|setLzxBlocksCache
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ChmLzxBlock
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"IOException parsing chm file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Enumerates chm entities      *       * @return list of chm entities      */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|enumerateChm
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|listOfEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|DirectoryListingEntry
name|directoryListingEntry
range|:
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
control|)
block|{
name|listOfEntries
operator|.
name|add
argument_list|(
name|directoryListingEntry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|listOfEntries
return|;
block|}
comment|/**      * Decompresses a chm entry      *       * @param directoryListingEntry      *       * @return decompressed data      * @throws TikaException       */
specifier|public
name|byte
index|[]
name|extractChmEntry
parameter_list|(
name|DirectoryListingEntry
name|directoryListingEntry
parameter_list|)
throws|throws
name|TikaException
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ChmLzxBlock
name|lzxBlock
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|/* UNCOMPRESSED type is easiest one */
if|if
condition|(
name|directoryListingEntry
operator|.
name|getEntryType
argument_list|()
operator|==
name|EntryType
operator|.
name|UNCOMPRESSED
operator|&&
name|directoryListingEntry
operator|.
name|getLength
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|ChmCommons
operator|.
name|hasSkip
argument_list|(
name|directoryListingEntry
argument_list|)
condition|)
block|{
name|int
name|dataOffset
init|=
call|(
name|int
call|)
argument_list|(
name|getChmItsfHeader
argument_list|()
operator|.
name|getDataOffset
argument_list|()
operator|+
name|directoryListingEntry
operator|.
name|getOffset
argument_list|()
argument_list|)
decl_stmt|;
comment|// dataSegment = Arrays.copyOfRange(getData(), dataOffset,
comment|// dataOffset + directoryListingEntry.getLength());
name|buffer
operator|.
name|write
argument_list|(
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|getData
argument_list|()
argument_list|,
name|dataOffset
argument_list|,
name|dataOffset
operator|+
name|directoryListingEntry
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|directoryListingEntry
operator|.
name|getEntryType
argument_list|()
operator|==
name|EntryType
operator|.
name|COMPRESSED
operator|&&
operator|!
name|ChmCommons
operator|.
name|hasSkip
argument_list|(
name|directoryListingEntry
argument_list|)
condition|)
block|{
comment|/* Gets a chm hit_cache info */
name|ChmBlockInfo
operator|.
name|resetChmBlockInfoInstance
argument_list|(
name|directoryListingEntry
argument_list|,
operator|(
name|int
operator|)
name|getChmLzxcResetTable
argument_list|()
operator|.
name|getBlockLen
argument_list|()
argument_list|,
name|getChmLzxcControlData
argument_list|()
argument_list|,
name|chmBlockInfo
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|,
name|start
init|=
literal|0
decl_stmt|,
name|hit_cache
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|(
name|getLzxBlockLength
argument_list|()
operator|<
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|&&
operator|(
name|getLzxBlockOffset
argument_list|()
operator|<
name|Integer
operator|.
name|MAX_VALUE
operator|)
condition|)
block|{
comment|// TODO: Improve the caching
comment|// caching ... = O(n^2) - depends on startBlock and endBlock
name|start
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
operator|!
name|getLzxBlocksCache
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|getLzxBlocksCache
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|//lzxBlock = getLzxBlocksCache().get(i);
name|int
name|bn
init|=
name|getLzxBlocksCache
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBlockNumber
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|chmBlockInfo
operator|.
name|getIniBlock
argument_list|()
init|;
name|j
operator|<=
name|chmBlockInfo
operator|.
name|getStartBlock
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|bn
operator|==
name|j
condition|)
block|{
if|if
condition|(
name|j
operator|>
name|start
condition|)
block|{
name|start
operator|=
name|j
expr_stmt|;
name|hit_cache
operator|=
name|i
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|start
operator|==
name|chmBlockInfo
operator|.
name|getStartBlock
argument_list|()
condition|)
break|break;
block|}
block|}
comment|//                    if (i == getLzxBlocksCache().size()&& i == 0) {
if|if
condition|(
name|start
operator|<
literal|0
condition|)
block|{
name|start
operator|=
name|chmBlockInfo
operator|.
name|getIniBlock
argument_list|()
expr_stmt|;
name|byte
index|[]
name|dataSegment
init|=
name|ChmCommons
operator|.
name|getChmBlockSegment
argument_list|(
name|getData
argument_list|()
argument_list|,
name|getChmLzxcResetTable
argument_list|()
argument_list|,
name|start
argument_list|,
operator|(
name|int
operator|)
name|getLzxBlockOffset
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|getLzxBlockLength
argument_list|()
argument_list|)
decl_stmt|;
name|lzxBlock
operator|=
operator|new
name|ChmLzxBlock
argument_list|(
name|start
argument_list|,
name|dataSegment
argument_list|,
name|getChmLzxcResetTable
argument_list|()
operator|.
name|getBlockLen
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|getLzxBlocksCache
argument_list|()
operator|.
name|add
argument_list|(
name|lzxBlock
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lzxBlock
operator|=
name|getLzxBlocksCache
argument_list|()
operator|.
name|get
argument_list|(
name|hit_cache
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|i
operator|=
name|start
init|;
name|i
operator|<=
name|chmBlockInfo
operator|.
name|getEndBlock
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|i
operator|==
name|chmBlockInfo
operator|.
name|getStartBlock
argument_list|()
operator|&&
name|i
operator|==
name|chmBlockInfo
operator|.
name|getEndBlock
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|write
argument_list|(
name|lzxBlock
operator|.
name|getContent
argument_list|(
name|chmBlockInfo
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|chmBlockInfo
operator|.
name|getEndOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|i
operator|==
name|chmBlockInfo
operator|.
name|getStartBlock
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|write
argument_list|(
name|lzxBlock
operator|.
name|getContent
argument_list|(
name|chmBlockInfo
operator|.
name|getStartOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
name|chmBlockInfo
operator|.
name|getStartBlock
argument_list|()
operator|&&
name|i
operator|<
name|chmBlockInfo
operator|.
name|getEndBlock
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|write
argument_list|(
name|lzxBlock
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
name|chmBlockInfo
operator|.
name|getEndBlock
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|write
argument_list|(
name|lzxBlock
operator|.
name|getContent
argument_list|(
literal|0
argument_list|,
name|chmBlockInfo
operator|.
name|getEndOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|%
name|getChmLzxcControlData
argument_list|()
operator|.
name|getResetInterval
argument_list|()
operator|==
literal|0
condition|)
block|{
name|lzxBlock
operator|=
operator|new
name|ChmLzxBlock
argument_list|(
name|i
argument_list|,
name|ChmCommons
operator|.
name|getChmBlockSegment
argument_list|(
name|getData
argument_list|()
argument_list|,
name|getChmLzxcResetTable
argument_list|()
argument_list|,
name|i
argument_list|,
operator|(
name|int
operator|)
name|getLzxBlockOffset
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|getLzxBlockLength
argument_list|()
argument_list|)
argument_list|,
name|getChmLzxcResetTable
argument_list|()
operator|.
name|getBlockLen
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lzxBlock
operator|=
operator|new
name|ChmLzxBlock
argument_list|(
name|i
argument_list|,
name|ChmCommons
operator|.
name|getChmBlockSegment
argument_list|(
name|getData
argument_list|()
argument_list|,
name|getChmLzxcResetTable
argument_list|()
argument_list|,
name|i
argument_list|,
operator|(
name|int
operator|)
name|getLzxBlockOffset
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|getLzxBlockLength
argument_list|()
argument_list|)
argument_list|,
name|getChmLzxcResetTable
argument_list|()
operator|.
name|getBlockLen
argument_list|()
argument_list|,
name|lzxBlock
argument_list|)
expr_stmt|;
block|}
name|getLzxBlocksCache
argument_list|()
operator|.
name|add
argument_list|(
name|lzxBlock
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getLzxBlocksCache
argument_list|()
operator|.
name|size
argument_list|()
operator|>
name|getChmLzxcResetTable
argument_list|()
operator|.
name|getBlockCount
argument_list|()
condition|)
block|{
name|getLzxBlocksCache
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|//end of if
if|if
condition|(
name|buffer
operator|.
name|size
argument_list|()
operator|!=
name|directoryListingEntry
operator|.
name|getLength
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"CHM file extract error: extracted Length is wrong."
argument_list|)
throw|;
block|}
block|}
comment|//end of if compressed
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|buffer
operator|.
name|toByteArray
argument_list|()
return|;
block|}
specifier|private
name|void
name|setLzxBlocksCache
parameter_list|(
name|List
argument_list|<
name|ChmLzxBlock
argument_list|>
name|lzxBlocksCache
parameter_list|)
block|{
name|this
operator|.
name|lzxBlocksCache
operator|=
name|lzxBlocksCache
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|ChmLzxBlock
argument_list|>
name|getLzxBlocksCache
parameter_list|()
block|{
return|return
name|lzxBlocksCache
return|;
block|}
specifier|private
name|void
name|setChmDirList
parameter_list|(
name|ChmDirectoryListingSet
name|chmDirList
parameter_list|)
block|{
name|this
operator|.
name|chmDirList
operator|=
name|chmDirList
expr_stmt|;
block|}
specifier|public
name|ChmDirectoryListingSet
name|getChmDirList
parameter_list|()
block|{
return|return
name|chmDirList
return|;
block|}
specifier|private
name|void
name|setChmItsfHeader
parameter_list|(
name|ChmItsfHeader
name|chmItsfHeader
parameter_list|)
block|{
name|this
operator|.
name|chmItsfHeader
operator|=
name|chmItsfHeader
expr_stmt|;
block|}
specifier|private
name|ChmItsfHeader
name|getChmItsfHeader
parameter_list|()
block|{
return|return
name|chmItsfHeader
return|;
block|}
block|}
end_class

end_unit

