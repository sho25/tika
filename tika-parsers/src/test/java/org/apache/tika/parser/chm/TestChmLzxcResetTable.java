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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|ChmConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|TestChmLzxcResetTable
block|{
specifier|private
name|ChmLzxcResetTable
name|chmLzxcResetTable
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|data
init|=
name|TestParameters
operator|.
name|chmData
decl_stmt|;
comment|/* Creates and parses itsf header */
name|ChmItsfHeader
name|chmItsfHeader
init|=
operator|new
name|ChmItsfHeader
argument_list|()
decl_stmt|;
comment|// chmItsfHeader.parse(Arrays.copyOfRange(data, 0,
comment|// ChmConstants.CHM_ITSF_V3_LEN - 1), chmItsfHeader);
name|chmItsfHeader
operator|.
name|parse
argument_list|(
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|data
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
name|chmItsfHeader
argument_list|)
expr_stmt|;
comment|/* Creates and parses itsp block */
name|ChmItspHeader
name|chmItspHeader
init|=
operator|new
name|ChmItspHeader
argument_list|()
decl_stmt|;
comment|// chmItspHeader.parse(Arrays.copyOfRange( data, (int)
comment|// chmItsfHeader.getDirOffset(),
comment|// (int) chmItsfHeader.getDirOffset()
comment|// + ChmConstants.CHM_ITSP_V1_LEN), chmItspHeader);
name|chmItspHeader
operator|.
name|parse
argument_list|(
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|data
argument_list|,
operator|(
name|int
operator|)
name|chmItsfHeader
operator|.
name|getDirOffset
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|chmItsfHeader
operator|.
name|getDirOffset
argument_list|()
operator|+
name|ChmConstants
operator|.
name|CHM_ITSP_V1_LEN
argument_list|)
argument_list|,
name|chmItspHeader
argument_list|)
expr_stmt|;
comment|/* Creating instance of ChmDirListingContainer */
name|ChmDirectoryListingSet
name|chmDirListCont
init|=
operator|new
name|ChmDirectoryListingSet
argument_list|(
name|data
argument_list|,
name|chmItsfHeader
argument_list|,
name|chmItspHeader
argument_list|)
decl_stmt|;
name|int
name|indexOfControlData
init|=
name|chmDirListCont
operator|.
name|getControlDataIndex
argument_list|()
decl_stmt|;
name|int
name|indexOfResetTable
init|=
name|ChmCommons
operator|.
name|indexOfResetTableBlock
argument_list|(
name|data
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
name|indexOfResetTable
operator|>
literal|0
condition|)
block|{
comment|// dir_chunk = Arrays.copyOfRange( data, indexOfResetTable,
comment|// indexOfResetTable
comment|// +
comment|// chmDirListCont.getDirectoryListingEntryList().get(indexOfControlData).getLength());
name|dir_chunk
operator|=
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|data
argument_list|,
name|indexOfResetTable
argument_list|,
name|indexOfResetTable
operator|+
name|chmDirListCont
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
block|}
comment|/* Creates and parses control block */
name|ChmLzxcControlData
name|chmLzxcControlData
init|=
operator|new
name|ChmLzxcControlData
argument_list|()
decl_stmt|;
name|chmLzxcControlData
operator|.
name|parse
argument_list|(
name|dir_chunk
argument_list|,
name|chmLzxcControlData
argument_list|)
expr_stmt|;
name|indexOfResetTable
operator|=
name|chmDirListCont
operator|.
name|getResetTableIndex
argument_list|()
expr_stmt|;
name|chmLzxcResetTable
operator|=
operator|new
name|ChmLzxcResetTable
argument_list|()
expr_stmt|;
name|int
name|startIndex
init|=
operator|(
name|int
operator|)
name|chmDirListCont
operator|.
name|getDataOffset
argument_list|()
operator|+
name|chmDirListCont
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
name|ChmAssert
operator|.
name|assertCopyingDataIndex
argument_list|(
name|startIndex
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// dir_chunk = Arrays.copyOfRange(data, startIndex, startIndex
comment|// +
comment|// chmDirListCont.getDirectoryListingEntryList().get(indexOfResetTable).getLength());
name|dir_chunk
operator|=
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|data
argument_list|,
name|startIndex
argument_list|,
name|startIndex
operator|+
name|chmDirListCont
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
name|chmLzxcResetTable
operator|.
name|parse
argument_list|(
name|dir_chunk
argument_list|,
name|chmLzxcResetTable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBlockAddress
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_RESET_TABLE_BA
argument_list|,
name|chmLzxcResetTable
operator|.
name|getBlockAddress
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBlockCount
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_RESET_TABLE_BA
argument_list|,
name|chmLzxcResetTable
operator|.
name|getBlockCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBlockLen
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_RES_TBL_BLOCK_LENGTH
argument_list|,
name|chmLzxcResetTable
operator|.
name|getBlockLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetCompressedLen
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_RES_TBL_COMPR_LENGTH
argument_list|,
name|chmLzxcResetTable
operator|.
name|getCompressedLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTableOffset
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_TBL_OFFSET
argument_list|,
name|chmLzxcResetTable
operator|.
name|getTableOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetUncompressedLen
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_RES_TBL_UNCOMP_LENGTH
argument_list|,
name|chmLzxcResetTable
operator|.
name|getUncompressedLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetUnknown
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_RES_TBL_UNKNOWN
argument_list|,
name|chmLzxcResetTable
operator|.
name|getUnknown
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetVersion
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_RES_TBL_VERSION
argument_list|,
name|chmLzxcResetTable
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|chmLzxcResetTable
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// TODO: add setters to be tested
block|}
end_class

end_unit

