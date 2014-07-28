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
name|*
import|;
end_import

begin_comment
comment|/**  * Test harness for the {@link CachedTranslator}. Take care to choose your target language carefully  * if you're testing the size of the cache!  */
end_comment

begin_class
specifier|public
class|class
name|CachedTranslatorTest
block|{
specifier|private
name|CachedTranslator
name|cachedTranslator
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|cachedTranslator
operator|=
operator|new
name|CachedTranslator
argument_list|(
operator|new
name|GoogleTranslator
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCachingSingleString
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|cachedTranslator
operator|.
name|translate
argument_list|(
literal|"This is a test string to translate!"
argument_list|,
literal|"en"
argument_list|,
literal|"sv"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Cache doesn't have a single translation pair!"
argument_list|,
name|cachedTranslator
operator|.
name|getNumTranslationPairs
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cache has more than one element!"
argument_list|,
name|cachedTranslator
operator|.
name|getNumTranslationsFor
argument_list|(
literal|"en"
argument_list|,
literal|"sv"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCachingTwoStrings
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|cachedTranslator
operator|.
name|translate
argument_list|(
literal|"This is a test string to translate!"
argument_list|,
literal|"en"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|cachedTranslator
operator|.
name|translate
argument_list|(
literal|"This is a different string..."
argument_list|,
literal|"en"
argument_list|,
literal|"fr"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Cache doesn't have two translation pairs!"
argument_list|,
name|cachedTranslator
operator|.
name|getNumTranslationPairs
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cache has more than en to no translation!"
argument_list|,
name|cachedTranslator
operator|.
name|getNumTranslationsFor
argument_list|(
literal|"en"
argument_list|,
literal|"no"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cache has more than en to fr translation!"
argument_list|,
name|cachedTranslator
operator|.
name|getNumTranslationsFor
argument_list|(
literal|"en"
argument_list|,
literal|"fr"
argument_list|)
argument_list|,
literal|1
argument_list|)
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
literal|"hola senor"
decl_stmt|;
name|String
name|expected
init|=
literal|"hello sir"
decl_stmt|;
if|if
condition|(
name|cachedTranslator
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|String
name|result
init|=
name|cachedTranslator
operator|.
name|translate
argument_list|(
name|source
argument_list|,
literal|"es"
argument_list|,
literal|"en"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Result: ["
operator|+
name|result
operator|+
literal|"]: not equal to expected: ["
operator|+
name|expected
operator|+
literal|"]"
argument_list|,
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCacheContains
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"Text that should be long enough to detect a language from."
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Cache should not contain a translation!"
argument_list|,
name|cachedTranslator
operator|.
name|contains
argument_list|(
name|text
argument_list|,
literal|"en"
argument_list|,
literal|"it"
argument_list|)
argument_list|)
expr_stmt|;
name|cachedTranslator
operator|.
name|translate
argument_list|(
name|text
argument_list|,
literal|"en"
argument_list|,
literal|"it"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Cache should contain a translation!"
argument_list|,
name|cachedTranslator
operator|.
name|contains
argument_list|(
name|text
argument_list|,
literal|"en"
argument_list|,
literal|"it"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Cache should detect source language when checking if contains."
argument_list|,
name|cachedTranslator
operator|.
name|contains
argument_list|(
name|text
argument_list|,
literal|"it"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
