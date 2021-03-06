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
name|eval
operator|.
name|langid
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|eval
operator|.
name|tokens
operator|.
name|CommonTokenCountManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|LangIdTest
block|{
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|LanguageIDWrapper
operator|.
name|loadBuiltInModels
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCommonTokensCoverage
parameter_list|()
throws|throws
name|Exception
block|{
comment|//make sure that there is a common tokens file for every
comment|//language
name|LanguageIDWrapper
name|wrapper
init|=
operator|new
name|LanguageIDWrapper
argument_list|()
decl_stmt|;
name|CommonTokenCountManager
name|commonTokens
init|=
operator|new
name|CommonTokenCountManager
argument_list|(
literal|null
argument_list|,
literal|"eng"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|lang
range|:
name|wrapper
operator|.
name|getSupportedLanguages
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tokens
init|=
name|commonTokens
operator|.
name|getTokens
argument_list|(
name|lang
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|US
argument_list|,
literal|"missing common tokens for: %s"
argument_list|,
name|lang
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tokens
operator|.
name|size
argument_list|()
operator|<
literal|1000
condition|)
block|{
comment|//kur has 1357
name|fail
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|US
argument_list|,
literal|"common tokens too small (%s) for: %s"
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|,
name|lang
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

