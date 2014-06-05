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
comment|/**  * Interface for Translator services.  * @since Tika 1.6  */
end_comment

begin_interface
specifier|public
interface|interface
name|Translator
block|{
comment|/**      * Translate text between given languages. The following languages are supported:      * Arabic("ar"), Bulgarian("bg"), Catalan("ca"), Chinese-Simplified("zh-CHS"), Chinese-Traditional("zh-CHT"),      * Czech("cs"), Danish("da"), Dutch("nl"), English("en"), Estonian("et"),  Innish("fi"), French("fr"), German("de"),      * Greek("el"), Haitian-Creole("ht"), Hebrew("he"), Hindi("hi"), Hmong-Daw("mww"), Hungarian("hu"),      * Indonesian("id"), Italian("it"), Japanese("ja"), Korean("ko"), Latvian("lv"), Lithuanian("lt"), Malay("ms"),      * Norwegian("no"), Persian("fa"), Polish("pl"), Portuguese("pt"), Romanian("ro"), Russian("ru"), Slovak("sk"),      * Slovenian("sl"), Spanish("es"), Swedish("sv"), Thai("th"), Turkish("tr"), Ukranian("uk"), Urdu("ur"),      * Vietnemese("vi").      * @param text The text to translate.      * @param sourceLanguage The input text language (for example, "en").      * @param targetLanguage The desired language to translate to (for example, "fr").      * @return The translation result. If translation is unavailable, returns the same text back.      * @throws Exception When there is an error with the API call.      * @since Tika 1.6      */
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
name|Exception
function_decl|;
comment|/**      * Translate text to the given language. This method attempts to auto-detect the source language of the text.      * The following languages are supported:      * Arabic("ar"), Bulgarian("bg"), Catalan("ca"), Chinese-Simplified("zh-CHS"), Chinese-Traditional("zh-CHT"),      * Czech("cs"), Danish("da"), Dutch("nl"), English("en"), Estonian("et"),  Innish("fi"), French("fr"), German("de"),      * Greek("el"), Haitian-Creole("ht"), Hebrew("he"), Hindi("hi"), Hmong-Daw("mww"), Hungarian("hu"),      * Indonesian("id"), Italian("it"), Japanese("ja"), Korean("ko"), Latvian("lv"), Lithuanian("lt"), Malay("ms"),      * Norwegian("no"), Persian("fa"), Polish("pl"), Portuguese("pt"), Romanian("ro"), Russian("ru"), Slovak("sk"),      * Slovenian("sl"), Spanish("es"), Swedish("sv"), Thai("th"), Turkish("tr"), Ukranian("uk"), Urdu("ur"),      * Vietnemese("vi").      * @param text The text to translate.      * @param targetLanguage The desired language to translate to (for example, "hi").      * @return The translation result. If translation is unavailable, returns the same text back.      * @throws Exception When there is an error with the API call.      * @since Tika 1.6      */
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
name|Exception
function_decl|;
comment|/**      * @return true if this Translator is probably able to translate right now.      * @since Tika 1.6      */
specifier|public
name|boolean
name|isAvailable
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

