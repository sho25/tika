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
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Configuration for the "file" (or file-alternative) command.  *  */
end_comment

begin_class
specifier|public
class|class
name|FileConfig
implements|implements
name|Serializable
block|{
comment|/** 	 * Serial version UID 	 */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5712655467296441314L
decl_stmt|;
specifier|private
name|String
name|filePath
init|=
literal|""
decl_stmt|;
specifier|private
name|boolean
name|mimetype
init|=
literal|false
decl_stmt|;
comment|/** 	 * Default constructor. 	 */
specifier|public
name|FileConfig
parameter_list|()
block|{
comment|// TODO Loads properties from InputStream.
block|}
comment|/** 	 * Returns the "file" installation folder. 	 *  	 * @return the "file" installation folder. 	 */
specifier|public
name|String
name|getFilePath
parameter_list|()
block|{
return|return
name|filePath
return|;
block|}
comment|/** 	 * Sets the "file" installation folder. 	 *  	 * @param filePath the "file" installation folder. 	 */
specifier|public
name|void
name|setFilePath
parameter_list|(
name|String
name|filePath
parameter_list|)
block|{
name|this
operator|.
name|filePath
operator|=
name|filePath
expr_stmt|;
block|}
comment|/** 	 * Returns {@code true} if the mime option is enabled. 	 *  	 * @return {@code true} if the mime option is enabled, {@code} otherwise. 	 */
specifier|public
name|boolean
name|isMimetype
parameter_list|()
block|{
return|return
name|mimetype
return|;
block|}
comment|/** 	 * Sets the mime option. If {@code true}, it causes the file command to 	 * output mime type strings rather than the more traditional human readable 	 * ones. 	 *  	 * @param mimetype 	 */
specifier|public
name|void
name|setMimetype
parameter_list|(
name|boolean
name|mimetype
parameter_list|)
block|{
name|this
operator|.
name|mimetype
operator|=
name|mimetype
expr_stmt|;
block|}
block|}
end_class

end_unit

