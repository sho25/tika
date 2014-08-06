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
name|ChmPmglHeader
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_class
specifier|public
class|class
name|TestPmglHeader
block|{
name|ChmPmglHeader
name|chmPmglHeader
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
name|chmPmglHeader
operator|=
operator|new
name|ChmPmglHeader
argument_list|()
expr_stmt|;
name|chmPmglHeader
operator|.
name|parse
argument_list|(
name|ChmCommons
operator|.
name|copyOfRange
argument_list|(
name|data
argument_list|,
name|ChmConstants
operator|.
name|START_PMGL
argument_list|,
name|ChmConstants
operator|.
name|START_PMGL
operator|+
name|ChmConstants
operator|.
name|CHM_PMGL_LEN
operator|+
literal|10
argument_list|)
argument_list|,
name|chmPmglHeader
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
operator|(
name|chmPmglHeader
operator|!=
literal|null
operator|)
operator|&&
name|chmPmglHeader
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
annotation|@
name|Test
specifier|public
name|void
name|testChmPmglHeaderGet
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_PMGL_SIGNATURE
argument_list|,
operator|new
name|String
argument_list|(
name|chmPmglHeader
operator|.
name|getSignature
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBlockNext
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_PMGL_BLOCK_NEXT
argument_list|,
name|chmPmglHeader
operator|.
name|getBlockNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetBlockPrev
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_PMGL_BLOCK_PREV
argument_list|,
name|chmPmglHeader
operator|.
name|getBlockPrev
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetFreeSpace
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_PMGL_FREE_SPACE
argument_list|,
name|chmPmglHeader
operator|.
name|getFreeSpace
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetUnknown0008
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_PMGL_UNKNOWN_008
argument_list|,
name|chmPmglHeader
operator|.
name|getUnknown0008
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

