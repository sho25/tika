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
package|;
end_package

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
name|HashMap
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
name|Properties
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Identifier of the language that best matches a given content profile.  * The content profile is compared to generic language profiles based on  * material from various sources.  *  * @since Apache Tika 0.5  * @see<a href="http://www.iccs.inf.ed.ac.uk/~pkoehn/publications/europarl/">  *      Europarl: A Parallel Corpus for Statistical Machine Translation</a>  * @see<a href="http://www.loc.gov/standards/iso639-2/php/code_list.php">  *      ISO 639 Language Codes</a>  */
end_comment

begin_class
specifier|public
class|class
name|LanguageIdentifier
block|{
comment|/**      * The available language profiles.      */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|LanguageProfile
argument_list|>
name|PROFILES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LanguageProfile
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROFILE_SUFFIX
init|=
literal|".ngp"
decl_stmt|;
specifier|private
specifier|static
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|String
name|errors
init|=
literal|""
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROPERTIES_OVERRIDE_FILE
init|=
literal|"tika.language.override.properties"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROPERTIES_FILE
init|=
literal|"tika.language.properties"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LANGUAGES_KEY
init|=
literal|"languages"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|double
name|CERTAINTY_LIMIT
init|=
literal|0.022
decl_stmt|;
specifier|private
specifier|final
name|String
name|language
decl_stmt|;
specifier|private
specifier|final
name|double
name|distance
decl_stmt|;
comment|/*      * Always attempt initializing language profiles when class is loaded first time      */
static|static
block|{
name|initProfiles
argument_list|()
expr_stmt|;
block|}
comment|/*      * Add one language profile based on config in property file      */
specifier|private
specifier|static
name|void
name|addProfile
parameter_list|(
name|String
name|language
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|LanguageProfile
name|profile
init|=
operator|new
name|LanguageProfile
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|LanguageIdentifier
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|language
operator|+
name|PROFILE_SUFFIX
argument_list|)
init|)
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
name|int
name|space
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
name|profile
operator|.
name|add
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|space
argument_list|)
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|space
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
name|addProfile
argument_list|(
name|language
argument_list|,
name|profile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Failed trying to load language profile for language \""
operator|+
name|language
operator|+
literal|"\". Error: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Adds a single language profile      * @param language an ISO 639 code representing language      * @param profile the language profile      */
specifier|public
specifier|static
name|void
name|addProfile
parameter_list|(
name|String
name|language
parameter_list|,
name|LanguageProfile
name|profile
parameter_list|)
block|{
name|PROFILES
operator|.
name|put
argument_list|(
name|language
argument_list|,
name|profile
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a language identifier based on a LanguageProfile      * @param profile the language profile      */
specifier|public
name|LanguageIdentifier
parameter_list|(
name|LanguageProfile
name|profile
parameter_list|)
block|{
name|String
name|minLanguage
init|=
literal|"unknown"
decl_stmt|;
name|double
name|minDistance
init|=
literal|1.0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LanguageProfile
argument_list|>
name|entry
range|:
name|PROFILES
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|double
name|distance
init|=
name|profile
operator|.
name|distance
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|distance
operator|<
name|minDistance
condition|)
block|{
name|minDistance
operator|=
name|distance
expr_stmt|;
name|minLanguage
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|language
operator|=
name|minLanguage
expr_stmt|;
name|this
operator|.
name|distance
operator|=
name|minDistance
expr_stmt|;
block|}
comment|/**      * Constructs a language identifier based on a String of text content      * @param content the text      */
specifier|public
name|LanguageIdentifier
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|LanguageProfile
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the identified language      * @return an ISO 639 code representing the detected language      */
specifier|public
name|String
name|getLanguage
parameter_list|()
block|{
return|return
name|language
return|;
block|}
comment|/**      * Tries to judge whether the identification is certain enough      * to be trusted.      * WARNING: Will never return true for small amount of input texts.       * @return<code>true</code> if the distance is smaller then {@value #CERTAINTY_LIMIT},<code>false</code> otherwise      */
specifier|public
name|boolean
name|isReasonablyCertain
parameter_list|()
block|{
return|return
name|distance
operator|<
name|CERTAINTY_LIMIT
return|;
block|}
comment|/**      * Builds the language profiles.      * The list of languages are fetched from a property file named "tika.language.properties"      * If a file called "tika.language.override.properties" is found on classpath, this is used instead      * The property file contains a key "languages" with values being comma-separated language codes      */
specifier|public
specifier|static
name|void
name|initProfiles
parameter_list|()
block|{
name|clearProfiles
argument_list|()
expr_stmt|;
name|errors
operator|=
literal|""
expr_stmt|;
name|InputStream
name|stream
decl_stmt|;
name|stream
operator|=
name|LanguageIdentifier
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|PROPERTIES_OVERRIDE_FILE
argument_list|)
expr_stmt|;
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
name|stream
operator|=
name|LanguageIdentifier
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|PROPERTIES_FILE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|props
operator|.
name|load
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|errors
operator|+=
literal|"IOException while trying to load property file. Message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n"
expr_stmt|;
block|}
block|}
name|String
index|[]
name|languages
init|=
name|props
operator|.
name|getProperty
argument_list|(
name|LANGUAGES_KEY
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|language
range|:
name|languages
control|)
block|{
name|language
operator|=
name|language
operator|.
name|trim
argument_list|()
expr_stmt|;
name|String
name|name
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"name."
operator|+
name|language
argument_list|,
literal|"Unknown"
argument_list|)
decl_stmt|;
try|try
block|{
name|addProfile
argument_list|(
name|language
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|errors
operator|+=
literal|"Language "
operator|+
name|language
operator|+
literal|" ("
operator|+
name|name
operator|+
literal|") not initialized. Message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n"
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Initializes the language profiles from a user supplied initialized Map.      * This overrides the default set of profiles initialized at startup,      * and provides an alternative to configuring profiles through property file      *      * @param profilesMap map of language profiles      */
specifier|public
specifier|static
name|void
name|initProfiles
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|LanguageProfile
argument_list|>
name|profilesMap
parameter_list|)
block|{
name|clearProfiles
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LanguageProfile
argument_list|>
name|entry
range|:
name|profilesMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|addProfile
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Clears the current map of language profiles      */
specifier|public
specifier|static
name|void
name|clearProfiles
parameter_list|()
block|{
name|PROFILES
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests whether there were errors initializing language config      * @return true if there are errors. Use getErrors() to retrieve.      */
specifier|public
specifier|static
name|boolean
name|hasErrors
parameter_list|()
block|{
return|return
name|errors
operator|!=
literal|""
return|;
block|}
comment|/**      * Returns a string of error messages related to initializing langauge profiles      * @return the String containing the error messages      */
specifier|public
specifier|static
name|String
name|getErrors
parameter_list|()
block|{
return|return
name|errors
return|;
block|}
comment|/**      * Returns what languages are supported for language identification      * @return A set of Strings being the ISO 639 language codes      */
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedLanguages
parameter_list|()
block|{
return|return
name|PROFILES
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|language
operator|+
literal|" ("
operator|+
name|distance
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

