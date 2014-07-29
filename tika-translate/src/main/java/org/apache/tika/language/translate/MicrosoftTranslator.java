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
name|com
operator|.
name|memetix
operator|.
name|mst
operator|.
name|language
operator|.
name|Language
import|;
end_import

begin_import
import|import
name|com
operator|.
name|memetix
operator|.
name|mst
operator|.
name|translate
operator|.
name|Translate
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Wrapper class to access the Windows translation service. This class uses the com.memetix.mst  * package as a wrapper for the API calls.  * @since Tika 1.6  */
end_comment

begin_class
specifier|public
class|class
name|MicrosoftTranslator
implements|implements
name|Translator
block|{
name|boolean
name|available
decl_stmt|;
comment|// Flag for whether or not translation is available.
name|String
name|clientId
decl_stmt|,
name|clientSecret
decl_stmt|;
comment|// Keys used for the API calls.
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTIES_FILE
init|=
literal|"org/apache/tika/language/translate/translator.microsoft.properties"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ID_PROPERTY
init|=
literal|"translator.client-id"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SECRET_PROPERTY
init|=
literal|"translator.client-secret"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_ID
init|=
literal|"dummy-id"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_SECRET
init|=
literal|"dummy-secret"
decl_stmt|;
comment|/**      * Create a new MicrosoftTranslator with the client keys specified in      * resources/org/apache/tika/language/translate/translator.microsoft.properties. Silently becomes unavailable      * when client keys are unavailable. translator.microsoft.client-id and translator.client-secret must be set      * in translator.microsoft.properties for translation to work.      * @since Tika 1.6      */
specifier|public
name|MicrosoftTranslator
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|InputStream
name|stream
decl_stmt|;
name|stream
operator|=
name|MicrosoftTranslator
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|PROPERTIES_FILE
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|load
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|clientId
operator|=
name|props
operator|.
name|getProperty
argument_list|(
name|ID_PROPERTY
argument_list|)
expr_stmt|;
name|clientSecret
operator|=
name|props
operator|.
name|getProperty
argument_list|(
name|SECRET_PROPERTY
argument_list|)
expr_stmt|;
name|this
operator|.
name|available
operator|=
name|checkAvailable
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// Error with properties file. Translation will not work.
name|available
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**      * Use the Microsoft service to translate the given text from the given source language to the given target.      * You must set the client keys in translator.microsoft.properties.      *      * @param text The text to translate.      * @param sourceLanguage The input text language (for example, "en").      * @param targetLanguage The desired language to translate to (for example, "fr").      * @return The translated text. If translation is unavailable, returns the unchanged text.      * @throws Exception      * @see org.apache.tika.language.translate.Translator      * @since Tika 1.6      */
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
block|{
if|if
condition|(
operator|!
name|available
condition|)
return|return
name|text
return|;
name|Language
name|source
init|=
name|Language
operator|.
name|fromString
argument_list|(
name|sourceLanguage
argument_list|)
decl_stmt|;
name|Language
name|target
init|=
name|Language
operator|.
name|fromString
argument_list|(
name|targetLanguage
argument_list|)
decl_stmt|;
name|Translate
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|Translate
operator|.
name|setClientSecret
argument_list|(
name|clientSecret
argument_list|)
expr_stmt|;
return|return
name|Translate
operator|.
name|execute
argument_list|(
name|text
argument_list|,
name|source
argument_list|,
name|target
argument_list|)
return|;
block|}
comment|/**      * Use the Microsoft service to translate the given text to the given target language. The source language      * is automatically detected by Microsoft. You must set the client keys in translator.microsoft.properties.      * @param text The text to translate.      * @param targetLanguage The desired language to translate to (for example, "hi").      * @return The translated text. If translation is unavailable, returns the unchanged text.      * @throws Exception      * @see org.apache.tika.language.translate.Translator      * @since Tika 1.6      */
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
block|{
if|if
condition|(
operator|!
name|available
condition|)
return|return
name|text
return|;
name|Language
name|target
init|=
name|Language
operator|.
name|fromString
argument_list|(
name|targetLanguage
argument_list|)
decl_stmt|;
name|Translate
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|Translate
operator|.
name|setClientSecret
argument_list|(
name|clientSecret
argument_list|)
expr_stmt|;
return|return
name|Translate
operator|.
name|execute
argument_list|(
name|text
argument_list|,
name|target
argument_list|)
return|;
block|}
comment|/**      * Check whether this instance has a working property file and its keys are not the defaults.      * This is not guaranteed to work, since keys may be incorrect or the webservice may be down.      * @return whether translation will probably work.      */
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|available
return|;
block|}
comment|/**      * Sets the client Id for the translator API.      * @param id The ID to set.      */
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|clientId
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|available
operator|=
name|checkAvailable
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets the client secret for the translator API.      * @param secret The secret to set.      */
specifier|public
name|void
name|setSecret
parameter_list|(
name|String
name|secret
parameter_list|)
block|{
name|this
operator|.
name|clientSecret
operator|=
name|secret
expr_stmt|;
name|this
operator|.
name|available
operator|=
name|checkAvailable
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|checkAvailable
parameter_list|()
block|{
return|return
name|clientId
operator|!=
literal|null
operator|&&
operator|!
name|clientId
operator|.
name|equals
argument_list|(
name|DEFAULT_ID
argument_list|)
operator|&&
name|clientSecret
operator|!=
literal|null
operator|&&
operator|!
name|clientSecret
operator|.
name|equals
argument_list|(
name|DEFAULT_SECRET
argument_list|)
return|;
block|}
block|}
end_class

end_unit

