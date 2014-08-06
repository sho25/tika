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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|ChmLzxState
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
name|TestChmLzxState
block|{
specifier|private
name|ChmLzxState
name|chmLzxState
decl_stmt|;
specifier|private
name|int
name|windowSize
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
name|chmItsHeader
init|=
operator|new
name|ChmItsfHeader
argument_list|()
decl_stmt|;
comment|// chmItsHeader.parse(Arrays.copyOfRange(data, 0,
comment|// ChmConstants.CHM_ITSF_V3_LEN - 1), chmItsHeader);
name|chmItsHeader
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
name|chmItsHeader
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
comment|// chmItsHeader.getDirOffset(),
comment|// (int) chmItsHeader.getDirOffset()
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
name|chmItsHeader
operator|.
name|getDirOffset
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|chmItsHeader
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
name|chmItsHeader
argument_list|,
name|chmItspHeader
argument_list|)
decl_stmt|;
name|int
name|indexOfControlData
init|=
name|ChmCommons
operator|.
name|indexOf
argument_list|(
name|chmDirListCont
operator|.
name|getDirectoryListingEntryList
argument_list|()
argument_list|,
name|ChmConstants
operator|.
name|CONTROL_DATA
argument_list|)
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
literal|"UTF-8"
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
name|ChmLzxcControlData
name|clcd
init|=
operator|new
name|ChmLzxcControlData
argument_list|()
decl_stmt|;
name|clcd
operator|.
name|parse
argument_list|(
name|dir_chunk
argument_list|,
name|clcd
argument_list|)
expr_stmt|;
name|windowSize
operator|=
operator|(
name|int
operator|)
name|clcd
operator|.
name|getWindowSize
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChmLzxStateConstructor
parameter_list|()
throws|throws
name|TikaException
block|{
name|chmLzxState
operator|=
operator|new
name|ChmLzxState
argument_list|(
name|windowSize
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chmLzxState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|TikaException
block|{
if|if
condition|(
name|chmLzxState
operator|==
literal|null
condition|)
name|testChmLzxStateConstructor
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|chmLzxState
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|20
argument_list|)
expr_stmt|;
block|}
comment|// TODO add more tests
block|}
end_class

end_unit

