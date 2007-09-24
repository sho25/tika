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
name|config
package|;
end_package

begin_comment
comment|/**  * Content object, used to configure and store data pupulated from XPATH, Regex,  * Document fulltext, document metadata etc.  *   */
end_comment

begin_class
specifier|public
class|class
name|Content
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|value
decl_stmt|;
specifier|private
name|String
index|[]
name|values
decl_stmt|;
specifier|private
name|String
name|textSelect
decl_stmt|;
specifier|private
name|String
name|xPathSelect
decl_stmt|;
specifier|private
name|String
name|regexSelect
decl_stmt|;
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getRegexSelect
parameter_list|()
block|{
return|return
name|regexSelect
return|;
block|}
specifier|public
name|void
name|setRegexSelect
parameter_list|(
name|String
name|regexSelect
parameter_list|)
block|{
name|this
operator|.
name|regexSelect
operator|=
name|regexSelect
expr_stmt|;
block|}
specifier|public
name|String
name|getTextSelect
parameter_list|()
block|{
return|return
name|textSelect
return|;
block|}
specifier|public
name|void
name|setTextSelect
parameter_list|(
name|String
name|select
parameter_list|)
block|{
name|this
operator|.
name|textSelect
operator|=
name|select
expr_stmt|;
block|}
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|String
name|getXPathSelect
parameter_list|()
block|{
return|return
name|xPathSelect
return|;
block|}
specifier|public
name|void
name|setXPathSelect
parameter_list|(
name|String
name|pathSelect
parameter_list|)
block|{
name|xPathSelect
operator|=
name|pathSelect
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
specifier|public
name|void
name|setValues
parameter_list|(
name|String
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
block|}
end_class

end_unit

