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
name|language
operator|.
name|LanguageIdentifier
import|;
end_import

begin_class
specifier|public
class|class
name|LanguageIdentifierExample
block|{
specifier|public
name|String
name|identifyLanguage
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|LanguageIdentifier
name|identifier
init|=
operator|new
name|LanguageIdentifier
argument_list|(
name|text
argument_list|)
decl_stmt|;
return|return
name|identifier
operator|.
name|getLanguage
argument_list|()
return|;
block|}
block|}
end_class

end_unit

