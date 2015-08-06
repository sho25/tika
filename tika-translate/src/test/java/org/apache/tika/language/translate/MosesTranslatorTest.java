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
name|language
operator|.
name|translate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|MosesTranslatorTest
block|{
name|MosesTranslator
name|translator
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|translator
operator|=
operator|new
name|MosesTranslator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleTranslate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|source
init|=
literal|"hola"
decl_stmt|;
name|String
name|expected
init|=
literal|"hello"
decl_stmt|;
name|String
name|translated
init|=
name|translator
operator|.
name|translate
argument_list|(
name|source
argument_list|,
literal|"sp"
argument_list|,
literal|"en"
argument_list|)
decl_stmt|;
if|if
condition|(
name|translator
operator|.
name|isAvailable
argument_list|()
condition|)
name|assertTrue
argument_list|(
literal|"Translate "
operator|+
name|source
operator|+
literal|" to "
operator|+
name|expected
operator|+
literal|" (was "
operator|+
name|translated
operator|+
literal|")"
argument_list|,
name|expected
operator|.
name|equalsIgnoreCase
argument_list|(
name|translated
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

