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
name|parser
operator|.
name|microsoft
package|;
end_package

begin_comment
comment|/**  * Package protected class for the MS Powerpoint TextBox content  *   *   */
end_comment

begin_class
class|class
name|TextBox
block|{
comment|/**    * Current id of a text box    */
specifier|protected
specifier|transient
specifier|final
name|long
name|currentID
decl_stmt|;
comment|/**    * Content of text box    */
specifier|protected
name|String
name|content
decl_stmt|;
comment|/**    * Instantiates the text box object    *     * @param textBoxId    *          id of text box    */
specifier|public
name|TextBox
parameter_list|(
specifier|final
name|long
name|textBoxId
parameter_list|)
block|{
name|this
operator|.
name|currentID
operator|=
name|textBoxId
expr_stmt|;
name|this
operator|.
name|content
operator|=
literal|""
expr_stmt|;
block|}
comment|/**    * Instantiates the text box object    *     * @param textBoxId    *          id of text box    * @param content    *          content of text box    */
specifier|public
name|TextBox
parameter_list|(
specifier|final
name|long
name|textBoxId
parameter_list|,
specifier|final
name|String
name|content
parameter_list|)
block|{
name|this
operator|.
name|currentID
operator|=
name|textBoxId
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
comment|/**    * Sets the content of the text box    *     * @param content    *          content of text Box    */
specifier|public
name|void
name|setContent
parameter_list|(
specifier|final
name|String
name|content
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
comment|/**    * Returns the content of the text box    *     * @return content of text box    */
specifier|public
name|String
name|getContent
parameter_list|()
block|{
return|return
name|this
operator|.
name|content
return|;
block|}
comment|/**    * Returns the current text box id    *     * @return long    */
specifier|public
name|long
name|getCurrentId
parameter_list|()
block|{
return|return
name|this
operator|.
name|currentID
return|;
block|}
block|}
end_class

end_unit

