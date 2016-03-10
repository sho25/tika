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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|config
operator|.
name|ServiceLoader
import|;
end_import

begin_comment
comment|// We should use the IANA registry for primary language names...see
end_comment

begin_comment
comment|// http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry
end_comment

begin_comment
comment|// There must be a package that uses this dataset to support knowledge of
end_comment

begin_comment
comment|// the default script, etc. And how to map from<lang>-<country> (e.g. 'zh-CN')
end_comment

begin_comment
comment|// to<sublang> ('cmn'), or<lang>-<sublang> to<sublan> ('zh-cmn' => 'cmn')
end_comment

begin_comment
comment|// We'd also want to know the default sublang for a macro language ('zh' => 'zh-cmn')
end_comment

begin_comment
comment|// There's also mapping 'zh-CN' to 'cmn-Hans' (simplified chinese script)
end_comment

begin_comment
comment|// TODO decide how deep to go into supporting extended language tags, see
end_comment

begin_comment
comment|// http://www.w3.org/International/articles/language-tags/. For example,
end_comment

begin_comment
comment|// what should you expect from calling hasModel("en-GB") if there's only
end_comment

begin_comment
comment|// a model for "en"?
end_comment

begin_comment
comment|// This is mostly an issue for interpreting language tags in (X)HTML docs,
end_comment

begin_comment
comment|// and maybe XML if we really care. In those cases you could get something
end_comment

begin_comment
comment|// like "ast" (three letter language code), or even zh-cmn-Hant-SG
end_comment

begin_comment
comment|// (Chinese, Mandarin, Traditional script, in Singapore) plus additional:
end_comment

begin_comment
comment|// language-extlang-script-region-variant-extension-privateuse
end_comment

begin_comment
comment|// The full spec is at http://www.rfc-editor.org/rfc/bcp/bcp47.txt
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|LanguageDetector
block|{
specifier|private
specifier|static
specifier|final
name|ServiceLoader
name|DEFAULT_SERVICE_LOADER
init|=
operator|new
name|ServiceLoader
argument_list|()
decl_stmt|;
comment|// True if text is expected to be a mix of languages, and thus higher-resolution
comment|// detection must be done to avoid under-sampling the text.
specifier|protected
name|boolean
name|mixedLanguages
init|=
literal|false
decl_stmt|;
comment|// True if the text is expected to be 'short' (typically less than 100 chars), and
comment|// thus a different algorithm and/or set of profiles should be used.
specifier|protected
name|boolean
name|shortText
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
name|LanguageDetector
name|getDefaultLanguageDetector
parameter_list|()
block|{
name|List
argument_list|<
name|LanguageDetector
argument_list|>
name|detectors
init|=
name|getLanguageDetectors
argument_list|()
decl_stmt|;
if|if
condition|(
name|detectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No language detectors available"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|detectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|LanguageDetector
argument_list|>
name|getLanguageDetectors
parameter_list|()
block|{
return|return
name|getLanguageDetectors
argument_list|(
name|DEFAULT_SERVICE_LOADER
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|LanguageDetector
argument_list|>
name|getLanguageDetectors
parameter_list|(
name|ServiceLoader
name|loader
parameter_list|)
block|{
name|List
argument_list|<
name|LanguageDetector
argument_list|>
name|detectors
init|=
name|loader
operator|.
name|loadStaticServiceProviders
argument_list|(
name|LanguageDetector
operator|.
name|class
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|detectors
argument_list|,
operator|new
name|Comparator
argument_list|<
name|LanguageDetector
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|LanguageDetector
name|d1
parameter_list|,
name|LanguageDetector
name|d2
parameter_list|)
block|{
name|String
name|n1
init|=
name|d1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|n2
init|=
name|d2
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|boolean
name|tika1
init|=
name|n1
operator|.
name|startsWith
argument_list|(
literal|"org.apache.tika."
argument_list|)
decl_stmt|;
name|boolean
name|tika2
init|=
name|n2
operator|.
name|startsWith
argument_list|(
literal|"org.apache.tika."
argument_list|)
decl_stmt|;
if|if
condition|(
name|tika1
operator|==
name|tika2
condition|)
block|{
return|return
name|n1
operator|.
name|compareTo
argument_list|(
name|n2
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|tika1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|detectors
return|;
block|}
specifier|public
name|boolean
name|isMixedLanguages
parameter_list|()
block|{
return|return
name|mixedLanguages
return|;
block|}
specifier|public
name|LanguageDetector
name|setMixedLanguages
parameter_list|(
name|boolean
name|mixedLanguages
parameter_list|)
block|{
name|this
operator|.
name|mixedLanguages
operator|=
name|mixedLanguages
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|boolean
name|isShortText
parameter_list|()
block|{
return|return
name|shortText
return|;
block|}
specifier|public
name|LanguageDetector
name|setShortText
parameter_list|(
name|boolean
name|shortText
parameter_list|)
block|{
name|this
operator|.
name|shortText
operator|=
name|shortText
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** 	 * Load (or re-load) all available language models. This must 	 * be called after any settings that would impact the models 	 * being loaded (e.g. mixed language/short text), but 	 * before any of the document processing routines (below) 	 * are called. Note that it only needs to be called once. 	 *  	 * @return this 	 */
specifier|public
specifier|abstract
name|LanguageDetector
name|loadModels
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** 	 * Load (or re-load) the models specified in<languages>. These use the 	 * ISO 639-1 names, with an optional "-<country code>" for more 	 * specific specification (e.g. "zh-CN" for Chinese in China). 	 *  	 * @param languages list of target languages. 	 * @return this 	 */
specifier|public
specifier|abstract
name|LanguageDetector
name|loadModels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|languages
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** 	 * Provide information about whether a model exists for a specific 	 * language. 	 *  	 * @param language ISO 639-1 name for language 	 * @return true if a model for this language exists. 	 */
specifier|public
specifier|abstract
name|boolean
name|hasModel
parameter_list|(
name|String
name|language
parameter_list|)
function_decl|;
comment|/** 	 * Set the a-priori probabilities for these languages. The provided map uses the language 	 * as the key, and the probability (0.0> probability< 1.0) of text being in that language. 	 * Note that if the probabilities don't sum to 1.0, these values will be normalized. 	 *  	 * If hasModel() returns false for any of the languages, an IllegalArgumentException is thrown. 	 *  	 * Use of these probabilities is detector-specific, and thus might not impact the results at all. 	 * As such, these should be viewed as a hint. 	 *  	 * @param languageProbabilities Map from language to probability 	 * @return this 	 */
specifier|public
specifier|abstract
name|LanguageDetector
name|setPriors
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|languageProbabilities
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// ============================================================
comment|// The routines below are called when processing a document
comment|// ============================================================
comment|/** 	 * Reset statistics about the current document being processed 	 */
specifier|public
specifier|abstract
name|void
name|reset
parameter_list|()
function_decl|;
comment|/** 	 * Add statistics about this text for the current document. Note 	 * that we assume an implicit word break exists before/after 	 * each of these runs of text. 	 *  	 * @param cbuf Character buffer 	 * @param off Offset into cbuf to first character in the run of text 	 * @param len Number of characters in the run of text. 	 */
specifier|public
specifier|abstract
name|void
name|addText
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
comment|/** 	 * Add<text> to the statistics being accumulated for the current 	 * document. Note that this is a default implementation for adding 	 * a string (not optimized) 	 *  	 * @param text Characters to add to current statistics. 	 */
specifier|public
name|void
name|addText
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|char
index|[]
name|chars
init|=
name|text
operator|.
name|toString
argument_list|()
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|addText
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tell the caller whether more text is required for the current document 	 * before the language can be reliably detected. 	 *  	 * Implementations can override this to do early termination of stats 	 * collection, which can improve performance with longer documents. 	 *  	 * Note that detect() can be called even when this returns false 	 *  	 * @return true if we have enough text for reliable detection. 	 */
specifier|public
name|boolean
name|hasEnoughText
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** 	 * Detect languages based on previously submitted text (via addText calls). 	 *  	 * @return	list of all possible languages with at least medium confidence, 	 * 			sorted by confidence from highest to lowest. There will always 	 * 			be at least one result, which might have a confidence of NONE. 	 */
specifier|public
specifier|abstract
name|List
argument_list|<
name|LanguageResult
argument_list|>
name|detectAll
parameter_list|()
function_decl|;
specifier|public
name|LanguageResult
name|detect
parameter_list|()
block|{
name|List
argument_list|<
name|LanguageResult
argument_list|>
name|results
init|=
name|detectAll
argument_list|()
decl_stmt|;
return|return
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/** 	 * Utility wrapper that detects the language of a given chunk of text. 	 *  	 * @param text String to add to current statistics. 	 * @return list of all possible languages with at least medium confidence, 	 * 			sorted by confidence from highest to lowest. 	 */
specifier|public
name|List
argument_list|<
name|LanguageResult
argument_list|>
name|detectAll
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|reset
argument_list|()
expr_stmt|;
name|addText
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|detectAll
argument_list|()
return|;
block|}
specifier|public
name|LanguageResult
name|detect
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|reset
argument_list|()
expr_stmt|;
name|addText
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|detect
argument_list|()
return|;
block|}
block|}
end_class

end_unit

