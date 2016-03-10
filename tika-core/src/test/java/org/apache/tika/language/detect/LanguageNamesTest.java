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
name|detect
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
name|LanguageNamesTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
block|{
comment|// macro language + language == language
name|String
name|languageA
init|=
name|LanguageNames
operator|.
name|normalizeName
argument_list|(
literal|"zh-yue"
argument_list|)
decl_stmt|;
name|String
name|languageB
init|=
name|LanguageNames
operator|.
name|normalizeName
argument_list|(
literal|"yue"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|LanguageNames
operator|.
name|equals
argument_list|(
name|languageA
argument_list|,
name|languageB
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO verify that "en-Latn" == "en"
comment|// TODO verify that "en-GB" == "en"???
block|}
block|}
end_class

end_unit

