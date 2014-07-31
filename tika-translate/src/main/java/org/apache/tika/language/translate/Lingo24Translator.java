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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|JsonNode
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
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|client
operator|.
name|WebClient
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
name|LanguageIdentifier
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
name|LanguageProfile
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
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
comment|/**  * An implementation of a REST client for the  *<a href="https://developer.lingo24.com/premium-machine-translation-api">Premium MT API v1</a>.  * You can sign up for an access plan online on the<a href="https://developer.lingo24.com/plans">Lingo24 Developer Portal</a>  * and set your Application's User Key in the<code>translator.lingo24.properties</code> file.  */
end_comment

begin_class
specifier|public
class|class
name|Lingo24Translator
implements|implements
name|Translator
block|{
specifier|private
specifier|static
specifier|final
name|String
name|LINGO24_TRANSLATE_URL_BASE
init|=
literal|"https://api.lingo24.com/mt/v1/translate"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_KEY
init|=
literal|"dummy-key"
decl_stmt|;
specifier|private
name|WebClient
name|client
decl_stmt|;
specifier|private
name|String
name|userKey
decl_stmt|;
specifier|private
name|boolean
name|isAvailable
decl_stmt|;
specifier|public
name|Lingo24Translator
parameter_list|()
block|{
name|this
operator|.
name|client
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|LINGO24_TRANSLATE_URL_BASE
argument_list|)
expr_stmt|;
name|this
operator|.
name|isAvailable
operator|=
literal|true
expr_stmt|;
name|Properties
name|config
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|config
operator|.
name|load
argument_list|(
name|Lingo24Translator
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"org/apache/tika/language/translate/translator.lingo24.properties"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|userKey
operator|=
name|config
operator|.
name|getProperty
argument_list|(
literal|"translator.user-key"
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|userKey
operator|.
name|equals
argument_list|(
name|DEFAULT_KEY
argument_list|)
condition|)
name|this
operator|.
name|isAvailable
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|isAvailable
operator|=
literal|false
expr_stmt|;
block|}
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
name|Exception
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|isAvailable
condition|)
return|return
name|text
return|;
name|Response
name|response
init|=
name|client
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|query
argument_list|(
literal|"user_key"
argument_list|,
name|userKey
argument_list|)
operator|.
name|query
argument_list|(
literal|"source"
argument_list|,
name|sourceLanguage
argument_list|)
operator|.
name|query
argument_list|(
literal|"target"
argument_list|,
name|targetLanguage
argument_list|)
operator|.
name|query
argument_list|(
literal|"q"
argument_list|,
name|text
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|StringBuffer
name|responseText
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|responseText
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|JsonNode
name|jsonResp
init|=
name|mapper
operator|.
name|readTree
argument_list|(
name|responseText
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|jsonResp
operator|.
name|findValuesAsText
argument_list|(
literal|"errors"
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|jsonResp
operator|.
name|findValuesAsText
argument_list|(
literal|"translation"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
name|jsonResp
operator|.
name|findValue
argument_list|(
literal|"errors"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|asText
argument_list|()
argument_list|)
throw|;
block|}
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
name|Exception
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|isAvailable
condition|)
return|return
name|text
return|;
name|LanguageIdentifier
name|language
init|=
operator|new
name|LanguageIdentifier
argument_list|(
operator|new
name|LanguageProfile
argument_list|(
name|text
argument_list|)
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
name|this
operator|.
name|isAvailable
return|;
block|}
block|}
end_class

end_unit

