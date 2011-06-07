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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|detect
operator|.
name|TestContainerAwareDetector
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
name|core
operator|.
name|ChmConstants
import|;
end_import

begin_comment
comment|/**  * Tests all public methods of the ChmItspHeader  *   */
end_comment

begin_class
specifier|public
class|class
name|TestChmItspHeader
extends|extends
name|TestCase
block|{
specifier|private
name|ChmItspHeader
name|chmItspHeader
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|TestContainerAwareDetector
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|TestParameters
operator|.
name|chmFile
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|TestUtils
operator|.
name|toByteArray
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|ChmItsfHeader
name|chmItsfHeader
init|=
operator|new
name|ChmItsfHeader
argument_list|()
decl_stmt|;
name|chmItsfHeader
operator|.
name|parse
argument_list|(
name|Arrays
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
name|chmItspHeader
operator|=
operator|new
name|ChmItspHeader
argument_list|()
expr_stmt|;
name|chmItspHeader
operator|.
name|parse
argument_list|(
name|Arrays
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
block|}
specifier|public
name|void
name|testGetBlock_len
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_BLOCK_LENGTH
argument_list|,
name|chmItspHeader
operator|.
name|getBlock_len
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetBlockidx_intvl
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_BLOCK_INDEX_INTERVAL
argument_list|,
name|chmItspHeader
operator|.
name|getBlockidx_intvl
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetHeader_len
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_ITSP_HEADER_LENGTH
argument_list|,
name|chmItspHeader
operator|.
name|getHeader_len
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetIndex_depth
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_INDEX_DEPTH
argument_list|,
name|chmItspHeader
operator|.
name|getIndex_depth
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetIndex_head
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_INDEX_HEAD
argument_list|,
name|chmItspHeader
operator|.
name|getIndex_head
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetIndex_root
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_INDEX_ROOT
argument_list|,
name|chmItspHeader
operator|.
name|getIndex_root
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetLang_id
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_LANGUAGE_ID
argument_list|,
name|chmItspHeader
operator|.
name|getLang_id
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetNum_blocks
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_UNKNOWN_NUM_BLOCKS
argument_list|,
name|chmItspHeader
operator|.
name|getNum_blocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetUnknown_000c
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_ITSP_UNKNOWN_000C
argument_list|,
name|chmItspHeader
operator|.
name|getUnknown_000c
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetUnknown_0024
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_ITSP_UNKNOWN_0024
argument_list|,
name|chmItspHeader
operator|.
name|getUnknown_0024
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetUnknown_002
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_ITSP_UNKNOWN_002C
argument_list|,
name|chmItspHeader
operator|.
name|getUnknown_002c
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetUnknown_0044
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_ITSP_BYTEARR_LEN
argument_list|,
name|chmItspHeader
operator|.
name|getUnknown_0044
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetVersion
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_ITSP_VERSION
argument_list|,
name|chmItspHeader
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetSignature
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_ISTP_SIGNATURE
argument_list|,
operator|new
name|String
argument_list|(
name|chmItspHeader
operator|.
name|getSignature
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetSystem_uuid
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_ITSP_BYTEARR_LEN
argument_list|,
name|chmItspHeader
operator|.
name|getSystem_uuid
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|chmItspHeader
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|TestParameters
operator|.
name|VP_ISTP_SIGNATURE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|chmItspHeader
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

