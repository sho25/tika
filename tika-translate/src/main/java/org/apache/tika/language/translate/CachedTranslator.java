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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|exception
operator|.
name|TikaException
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
name|language
operator|.
name|detect
operator|.
name|LanguageResult
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|util
operator|.
name|LRUMap
import|;
end_import

begin_comment
comment|/**  * CachedTranslator. Saves a map of previous translations in order to prevent repetitive translation requests.  */
end_comment

begin_class
specifier|public
class|class
name|CachedTranslator
extends|extends
name|AbstractTranslator
block|{
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_ENTRIES
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_ENTRIES
init|=
literal|1000
decl_stmt|;
specifier|private
name|Translator
name|translator
decl_stmt|;
comment|// The cache is a map from sourceLang:targetLang to an LRUMap of previously translated pairs.
comment|// Old entries are removed from the cache when it reaches its limit.
comment|// For example, {en:fr -> {hello -> salut}}.
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|LRUMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|cache
decl_stmt|;
comment|/**      * Create a new CachedTranslator (must set the {@link Translator} with {@link #setTranslator(Translator)} before use!)      */
specifier|public
name|CachedTranslator
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new CachedTranslator.      *      * @param translator The translator that should be used for the underlying translation service. The properties      *                   for that service must be set properly!      */
specifier|public
name|CachedTranslator
parameter_list|(
name|Translator
name|translator
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|=
name|translator
expr_stmt|;
name|this
operator|.
name|cache
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @return the translator 	 */
specifier|public
name|Translator
name|getTranslator
parameter_list|()
block|{
return|return
name|translator
return|;
block|}
comment|/** 	 * @param translator the translator to set 	 */
specifier|public
name|void
name|setTranslator
parameter_list|(
name|Translator
name|translator
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|=
name|translator
expr_stmt|;
block|}
annotation|@
name|Override
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
throws|throws
name|TikaException
throws|,
name|IOException
block|{
if|if
condition|(
name|translator
operator|==
literal|null
condition|)
block|{
return|return
name|text
return|;
block|}
name|LRUMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|translationCache
init|=
name|getTranslationCache
argument_list|(
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
decl_stmt|;
name|String
name|translatedText
init|=
name|translationCache
operator|.
name|get
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|translatedText
operator|==
literal|null
condition|)
block|{
name|translatedText
operator|=
name|translator
operator|.
name|translate
argument_list|(
name|text
argument_list|,
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
expr_stmt|;
name|translationCache
operator|.
name|put
argument_list|(
name|text
argument_list|,
name|translatedText
argument_list|)
expr_stmt|;
block|}
return|return
name|translatedText
return|;
block|}
annotation|@
name|Override
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
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|LanguageResult
name|language
init|=
name|detectLanguage
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|String
name|sourceLanguage
init|=
name|language
operator|.
name|getLanguage
argument_list|()
decl_stmt|;
return|return
name|translate
argument_list|(
name|text
argument_list|,
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|translator
operator|!=
literal|null
operator|&&
name|translator
operator|.
name|isAvailable
argument_list|()
return|;
block|}
comment|/**      * Get the number of different source/target translation pairs this CachedTranslator      * currently has in its cache.      *      * @return Number of translation source/target pairs in this CachedTranslator's cache.      * @since Tika 1.6      */
specifier|public
name|int
name|getNumTranslationPairs
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Get the number of different translations from the source language to the target language      * this CachedTranslator has in its cache.      *      * @param sourceLanguage The source language of translation.      * @param targetLanguage The target language of translation.      * @return The number of translations between source and target.      * @since Tika 1.6      */
specifier|public
name|int
name|getNumTranslationsFor
parameter_list|(
name|String
name|sourceLanguage
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
block|{
name|LRUMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|translationCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|buildCacheKeyString
argument_list|(
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|translationCache
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|translationCache
operator|.
name|size
argument_list|()
return|;
block|}
block|}
comment|/**      * Check whether this CachedTranslator's cache contains a translation of the text from the      * source language to the target language.      *      * @param text What string to check for.      * @param sourceLanguage The source language of translation.      * @param targetLanguage The target language of translation.      * @return true if the cache contains a translation of the text, false otherwise.      */
specifier|public
name|boolean
name|contains
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
name|LRUMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|translationCache
init|=
name|getTranslationCache
argument_list|(
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
decl_stmt|;
return|return
name|translationCache
operator|.
name|get
argument_list|(
name|text
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * Check whether this CachedTranslator's cache contains a translation of the text to the target language,      * attempting to auto-detect the source language.      *      * @param text What string to check for.      * @param targetLanguage The target language of translation.      * @return true if the cache contains a translation of the text, false otherwise.      */
specifier|public
name|boolean
name|contains
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
block|{
try|try
block|{
name|LanguageResult
name|language
init|=
name|detectLanguage
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|String
name|sourceLanguage
init|=
name|language
operator|.
name|getLanguage
argument_list|()
decl_stmt|;
return|return
name|contains
argument_list|(
name|text
argument_list|,
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO what to do if we get an error?
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Build the String to be used as the key into this CachedTranslator's cache.      *      * @param sourceLanguage The source language of translation.      * @param targetLanguage The target language of translation.      * @return The string to be used as the key into this CachedTranslator's cache.      */
specifier|private
name|String
name|buildCacheKeyString
parameter_list|(
name|String
name|sourceLanguage
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
block|{
return|return
name|sourceLanguage
operator|+
literal|":"
operator|+
name|targetLanguage
return|;
block|}
comment|/**      * Get the cache of translations from the given source language to target language.      *      * @param sourceLanguage The source language of translation.      * @param targetLanguage The target language of translation.      * @return The LRUMap representing the translation cache.      */
specifier|private
name|LRUMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getTranslationCache
parameter_list|(
name|String
name|sourceLanguage
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
block|{
name|LRUMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|translationCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|buildCacheKeyString
argument_list|(
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|translationCache
operator|==
literal|null
condition|)
block|{
name|translationCache
operator|=
operator|new
name|LRUMap
argument_list|<>
argument_list|(
name|INITIAL_ENTRIES
argument_list|,
name|MAX_ENTRIES
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|buildCacheKeyString
argument_list|(
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
argument_list|,
name|translationCache
argument_list|)
expr_stmt|;
block|}
return|return
name|translationCache
return|;
block|}
block|}
end_class

end_unit

