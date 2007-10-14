begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
package|;
end_package

begin_comment
comment|// Junit imports
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|// Tika imports
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|TikaConfig
import|;
end_import

begin_comment
comment|/**  *   * Test Suite for the {@link MimeTypes} repository.  *   */
end_comment

begin_class
specifier|public
class|class
name|TestMimeTypes
extends|extends
name|TestCase
block|{
specifier|private
name|MimeTypes
name|repo
decl_stmt|;
specifier|public
name|TestMimeTypes
parameter_list|()
block|{
try|try
block|{
name|repo
operator|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
operator|.
name|getMimeRepository
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testCaseSensitivity
parameter_list|()
block|{
name|MimeType
name|type
init|=
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.PDF"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.pdf"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.PdF"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.pdF"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

