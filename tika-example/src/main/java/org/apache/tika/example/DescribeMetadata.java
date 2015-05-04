begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|example
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|cli
operator|.
name|TikaCLI
import|;
end_import

begin_comment
comment|/**  *  * Print the supported Tika Metadata models and their fields.  *  */
end_comment

begin_class
specifier|public
class|class
name|DescribeMetadata
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|TikaCLI
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"--list-met-models"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

