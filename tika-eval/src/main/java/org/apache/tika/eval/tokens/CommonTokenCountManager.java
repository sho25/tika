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
name|tokens
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|mutable
operator|.
name|MutableInt
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|CommonTokenCountManager
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CommonTokenCountManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Charset
name|COMMON_TOKENS_CHARSET
init|=
name|StandardCharsets
operator|.
name|UTF_8
decl_stmt|;
specifier|private
specifier|final
name|Path
name|commonTokensDir
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|commonTokenMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|alreadyTriedToLoad
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|//if we have no model or if no langid is passed in
comment|//make this configurable
specifier|private
specifier|final
name|String
name|defaultLangCode
decl_stmt|;
specifier|public
name|CommonTokenCountManager
parameter_list|(
name|Path
name|commonTokensDir
parameter_list|,
name|String
name|defaultLangCode
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|defaultLangCode
operator|=
name|defaultLangCode
expr_stmt|;
name|this
operator|.
name|commonTokensDir
operator|=
name|commonTokensDir
expr_stmt|;
name|tryToLoad
argument_list|(
name|defaultLangCode
argument_list|)
expr_stmt|;
comment|//if you couldn't load it, make sure to add an empty
comment|//set to prevent npes later
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|commonTokenMap
operator|.
name|get
argument_list|(
name|defaultLangCode
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No common tokens for default language: '"
operator|+
name|defaultLangCode
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|commonTokenMap
operator|.
name|put
argument_list|(
name|defaultLangCode
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|CommonTokenResult
name|countTokenOverlaps
parameter_list|(
name|String
name|langCode
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|actualLangCode
init|=
name|getActualLangCode
argument_list|(
name|langCode
argument_list|)
decl_stmt|;
name|int
name|numUniqueCommonTokens
init|=
literal|0
decl_stmt|;
name|int
name|numCommonTokens
init|=
literal|0
decl_stmt|;
name|int
name|numUniqueAlphabeticTokens
init|=
literal|0
decl_stmt|;
name|int
name|numAlphabeticTokens
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|commonTokens
init|=
name|commonTokenMap
operator|.
name|get
argument_list|(
name|actualLangCode
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MutableInt
argument_list|>
name|e
range|:
name|tokens
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|token
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|AlphaIdeographFilterFactory
operator|.
name|isAlphabetic
argument_list|(
name|token
operator|.
name|toCharArray
argument_list|()
argument_list|,
name|token
operator|.
name|length
argument_list|()
argument_list|)
condition|)
block|{
name|numAlphabeticTokens
operator|+=
name|count
expr_stmt|;
name|numUniqueAlphabeticTokens
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|commonTokens
operator|.
name|contains
argument_list|(
name|token
argument_list|)
condition|)
block|{
name|numCommonTokens
operator|+=
name|count
expr_stmt|;
name|numUniqueCommonTokens
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|new
name|CommonTokenResult
argument_list|(
name|actualLangCode
argument_list|,
name|numUniqueCommonTokens
argument_list|,
name|numCommonTokens
argument_list|,
name|numUniqueAlphabeticTokens
argument_list|,
name|numAlphabeticTokens
argument_list|)
return|;
block|}
comment|//return langcode for lang that you are actually using
comment|//lazily load the appropriate model
specifier|private
name|String
name|getActualLangCode
parameter_list|(
name|String
name|langCode
parameter_list|)
block|{
if|if
condition|(
name|langCode
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|langCode
argument_list|)
condition|)
block|{
return|return
name|defaultLangCode
return|;
block|}
if|if
condition|(
name|commonTokenMap
operator|.
name|containsKey
argument_list|(
name|langCode
argument_list|)
condition|)
block|{
return|return
name|langCode
return|;
block|}
name|tryToLoad
argument_list|(
name|langCode
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|commonTokenMap
operator|.
name|get
argument_list|(
name|langCode
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
name|defaultLangCode
return|;
block|}
return|return
name|langCode
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|commonTokenMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|void
name|tryToLoad
parameter_list|(
name|String
name|langCode
parameter_list|)
block|{
if|if
condition|(
name|alreadyTriedToLoad
operator|.
name|contains
argument_list|(
name|langCode
argument_list|)
condition|)
block|{
return|return;
block|}
comment|//check once more now that we're in a
comment|//synchronized block
if|if
condition|(
name|commonTokenMap
operator|.
name|get
argument_list|(
name|langCode
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|Path
name|p
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|commonTokensDir
operator|!=
literal|null
condition|)
block|{
name|p
operator|=
name|commonTokensDir
operator|.
name|resolve
argument_list|(
name|langCode
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|p
operator|==
literal|null
operator|||
operator|!
name|Files
operator|.
name|isRegularFile
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|is
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/common_tokens/"
operator|+
name|langCode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|is
operator|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|String
name|path
init|=
operator|(
name|p
operator|==
literal|null
operator|)
condition|?
literal|"resource on class path: /common_tokens/"
operator|+
name|langCode
else|:
name|p
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't find common tokens file for: '"
operator|+
name|langCode
operator|+
literal|"' tried here: "
operator|+
name|path
argument_list|)
expr_stmt|;
name|alreadyTriedToLoad
operator|.
name|add
argument_list|(
name|langCode
argument_list|)
expr_stmt|;
return|return;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|commonTokenMap
operator|.
name|get
argument_list|(
name|langCode
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|set
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|commonTokenMap
operator|.
name|put
argument_list|(
name|langCode
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|COMMON_TOKENS_CHARSET
argument_list|)
argument_list|)
init|)
block|{
name|alreadyTriedToLoad
operator|.
name|add
argument_list|(
name|langCode
argument_list|)
expr_stmt|;
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
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
continue|continue;
block|}
comment|//allow language models with, e.g. tab-delimited counts after the term
name|String
index|[]
name|cols
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
name|String
name|t
init|=
name|cols
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|t
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"IOException trying to read: '"
operator|+
name|langCode
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

