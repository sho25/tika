begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   *     http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|strings
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
name|*
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
name|StringsConfigTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testNoConfig
parameter_list|()
block|{
name|StringsConfig
name|config
init|=
operator|new
name|StringsConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid default filePath value"
argument_list|,
literal|""
argument_list|,
name|config
operator|.
name|getStringsPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid default encoding value"
argument_list|,
name|StringsEncoding
operator|.
name|SINGLE_7_BIT
argument_list|,
name|config
operator|.
name|getEncoding
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid default min-len value"
argument_list|,
literal|4
argument_list|,
name|config
operator|.
name|getMinLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid default timeout value"
argument_list|,
literal|120
argument_list|,
name|config
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

