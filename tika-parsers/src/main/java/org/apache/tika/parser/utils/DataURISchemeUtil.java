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
name|parser
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|mime
operator|.
name|MediaType
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
name|IllegalCharsetNameException
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
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Not thread safe.  Create a separate util for each thread.  */
end_comment

begin_class
specifier|public
class|class
name|DataURISchemeUtil
block|{
specifier|public
specifier|static
name|String
name|UNSPECIFIED_MEDIA_TYPE
init|=
literal|"text/plain;charset=US-ASCII"
decl_stmt|;
specifier|private
specifier|static
name|Pattern
name|PARSE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?s)data:([^,]*?)(base64)?,(.*)$"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Pattern
name|EXTRACT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?s)data:([^,]*?)(base64)?,([^\"\']*)[\"\']"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Matcher
name|parseMatcher
init|=
name|PARSE_PATTERN
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Matcher
name|extractMatcher
init|=
name|EXTRACT_PATTERN
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|Base64
name|base64
init|=
operator|new
name|Base64
argument_list|()
decl_stmt|;
specifier|public
name|DataURIScheme
name|parse
parameter_list|(
name|String
name|string
parameter_list|)
throws|throws
name|DataURISchemeParseException
block|{
name|parseMatcher
operator|.
name|reset
argument_list|(
name|string
argument_list|)
expr_stmt|;
if|if
condition|(
name|parseMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
name|build
argument_list|(
name|parseMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|parseMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|parseMatcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|DataURISchemeParseException
argument_list|(
literal|"Couldn't find expected pattern"
argument_list|)
throw|;
block|}
specifier|private
name|DataURIScheme
name|build
parameter_list|(
name|String
name|mediaTypeString
parameter_list|,
name|String
name|isBase64
parameter_list|,
name|String
name|dataString
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
comment|//strip out back slashes as you might have in css
name|dataString
operator|=
operator|(
name|dataString
operator|!=
literal|null
operator|)
condition|?
name|dataString
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|" "
argument_list|)
else|:
name|dataString
expr_stmt|;
if|if
condition|(
name|dataString
operator|==
literal|null
operator|||
name|dataString
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isBase64
operator|!=
literal|null
condition|)
block|{
name|data
operator|=
name|base64
operator|.
name|decode
argument_list|(
name|dataString
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//TODO: handle encodings
name|MediaType
name|mediaType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|mediaTypeString
argument_list|)
decl_stmt|;
name|Charset
name|charset
init|=
name|StandardCharsets
operator|.
name|UTF_8
decl_stmt|;
if|if
condition|(
name|mediaType
operator|.
name|hasParameters
argument_list|()
condition|)
block|{
name|String
name|charsetName
init|=
name|mediaType
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
literal|"charset"
argument_list|)
decl_stmt|;
if|if
condition|(
name|charsetName
operator|!=
literal|null
operator|&&
name|Charset
operator|.
name|isSupported
argument_list|(
name|charsetName
argument_list|)
condition|)
block|{
try|try
block|{
name|charset
operator|=
name|Charset
operator|.
name|forName
argument_list|(
name|charsetName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalCharsetNameException
name|e
parameter_list|)
block|{
comment|//swallow and default to UTF-8
block|}
block|}
block|}
name|data
operator|=
name|dataString
operator|.
name|getBytes
argument_list|(
name|charset
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DataURIScheme
argument_list|(
name|mediaTypeString
argument_list|,
operator|(
name|isBase64
operator|!=
literal|null
operator|)
argument_list|,
name|data
argument_list|)
return|;
block|}
comment|/**      * Extracts DataURISchemes from free text, as in javascript.      *      * @param string      * @return list of extracted DataURISchemes      */
specifier|public
name|List
argument_list|<
name|DataURIScheme
argument_list|>
name|extract
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|extractMatcher
operator|.
name|reset
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DataURIScheme
argument_list|>
name|list
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|extractMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|DataURIScheme
name|dataURIScheme
init|=
name|build
argument_list|(
name|extractMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|extractMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|extractMatcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|dataURIScheme
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|list
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
name|EMPTY_LIST
else|:
name|list
return|;
block|}
block|}
end_class

end_unit

