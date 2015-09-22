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
name|io
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
name|assertTrue
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
name|Path
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
name|TemporaryResourcesTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testFileDeletion
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|tempFile
decl_stmt|;
try|try
init|(
name|TemporaryResources
name|tempResources
init|=
operator|new
name|TemporaryResources
argument_list|()
init|)
block|{
name|tempFile
operator|=
name|tempResources
operator|.
name|createTempFile
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Temp file should exist while TempResources is used"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|tempFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Temp file should not exist after TempResources is closed"
argument_list|,
name|Files
operator|.
name|notExists
argument_list|(
name|tempFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

