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
name|java
operator|.
name|io
operator|.
name|File
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
name|Tika
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleTextExtractor
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
comment|// Create a Tika instance with the default configuration
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
comment|// Parse all given files and print out the extracted
comment|// text content
for|for
control|(
name|String
name|file
range|:
name|args
control|)
block|{
name|String
name|text
init|=
name|tika
operator|.
name|parseToString
argument_list|(
operator|new
name|File
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

