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
name|language
operator|.
name|translate
package|;
end_package

begin_comment
comment|/**  * Dummy translator that always declines to give any text. Useful as a   * sentinel translator for when none others are available.  * for unknown document types.  */
end_comment

begin_class
specifier|public
class|class
name|EmptyTranslator
implements|implements
name|Translator
block|{
specifier|public
name|String
name|translate
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|sourceLanguage
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|translate
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

