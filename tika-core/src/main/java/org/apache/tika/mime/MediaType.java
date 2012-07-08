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
name|mime
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|HashMap
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
name|Locale
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
comment|/**  * Internet media type.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MediaType
implements|implements
name|Comparable
argument_list|<
name|MediaType
argument_list|>
implements|,
name|Serializable
block|{
comment|/**      * Serial version UID.      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3831000556189036392L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|SPECIAL
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=]"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|SPECIAL_OR_WHITESPACE
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]"
argument_list|)
decl_stmt|;
comment|/**      * See http://www.ietf.org/rfc/rfc2045.txt for valid mime-type characters.      */
specifier|private
specifier|static
specifier|final
name|String
name|VALID_CHARS
init|=
literal|"([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|TYPE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?s)\\s*"
operator|+
name|VALID_CHARS
operator|+
literal|"\\s*/\\s*"
operator|+
name|VALID_CHARS
operator|+
literal|"\\s*($|;.*)"
argument_list|)
decl_stmt|;
comment|// TIKA-350: handle charset as first element in content-type
specifier|private
specifier|static
specifier|final
name|Pattern
name|CHARSET_FIRST_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?is)\\s*(charset\\s*=\\s*[^\\c;\\s]+)\\s*;\\s*"
operator|+
name|VALID_CHARS
operator|+
literal|"\\s*/\\s*"
operator|+
name|VALID_CHARS
operator|+
literal|"\\s*"
argument_list|)
decl_stmt|;
comment|/**      * Set of basic types with normalized "type/subtype" names.      * Used to optimize type lookup and to avoid having too many      * {@link MediaType} instances in memory.      */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MediaType
argument_list|>
name|SIMPLE_TYPES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|OCTET_STREAM
init|=
name|parse
argument_list|(
literal|"application/octet-stream"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TEXT_PLAIN
init|=
name|parse
argument_list|(
literal|"text/plain"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TEXT_HTML
init|=
name|parse
argument_list|(
literal|"text/html"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|APPLICATION_XML
init|=
name|parse
argument_list|(
literal|"application/xml"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|APPLICATION_ZIP
init|=
name|parse
argument_list|(
literal|"application/zip"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|MediaType
name|application
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
literal|"application/"
operator|+
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|MediaType
name|audio
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
literal|"audio/"
operator|+
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|MediaType
name|image
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
literal|"image/"
operator|+
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|MediaType
name|text
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
literal|"text/"
operator|+
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|MediaType
name|video
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
literal|"video/"
operator|+
name|type
argument_list|)
return|;
block|}
comment|/**      * Convenience method that returns an unmodifiable set that contains      * all the given media types.      *      * @since Apache Tika 1.2      * @param types media types      * @return unmodifiable set of the given types      */
specifier|public
specifier|static
name|Set
argument_list|<
name|MediaType
argument_list|>
name|set
parameter_list|(
name|MediaType
modifier|...
name|types
parameter_list|)
block|{
name|Set
argument_list|<
name|MediaType
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|MediaType
name|type
range|:
name|types
control|)
block|{
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|set
argument_list|)
return|;
block|}
comment|/**      * Convenience method that parses the given media type strings and      * returns an unmodifiable set that contains all the parsed types.      *      * @since Apache Tika 1.2      * @param types media type strings      * @return unmodifiable set of the parsed types      */
specifier|public
specifier|static
name|Set
argument_list|<
name|MediaType
argument_list|>
name|set
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|Set
argument_list|<
name|MediaType
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
name|MediaType
name|mt
init|=
name|parse
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|mt
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|mt
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|set
argument_list|)
return|;
block|}
comment|/**      * Parses the given string to a media type. The string is expected      * to be of the form "type/subtype(; parameter=...)*" as defined in      * RFC 2045, though we also handle "charset=xxx; type/subtype" for      * broken web servers.      *      * @param string media type string to be parsed      * @return parsed media type, or<code>null</code> if parsing fails      */
specifier|public
specifier|static
name|MediaType
name|parse
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|string
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Optimization for the common cases
synchronized|synchronized
init|(
name|SIMPLE_TYPES
init|)
block|{
name|MediaType
name|type
init|=
name|SIMPLE_TYPES
operator|.
name|get
argument_list|(
name|string
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|int
name|slash
init|=
name|string
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|slash
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|SIMPLE_TYPES
operator|.
name|size
argument_list|()
operator|<
literal|10000
operator|&&
name|isSimpleName
argument_list|(
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
argument_list|)
operator|&&
name|isSimpleName
argument_list|(
name|string
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|type
operator|=
operator|new
name|MediaType
argument_list|(
name|string
argument_list|,
name|slash
argument_list|)
expr_stmt|;
name|SIMPLE_TYPES
operator|.
name|put
argument_list|(
name|string
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
name|Matcher
name|matcher
decl_stmt|;
name|matcher
operator|=
name|TYPE_PATTERN
operator|.
name|matcher
argument_list|(
name|string
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
operator|new
name|MediaType
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|parseParameters
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
name|matcher
operator|=
name|CHARSET_FIRST_PATTERN
operator|.
name|matcher
argument_list|(
name|string
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
operator|new
name|MediaType
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
name|parseParameters
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isSimpleName
parameter_list|(
name|String
name|name
parameter_list|)
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
name|name
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|'-'
operator|&&
name|c
operator|!=
literal|'+'
operator|&&
name|c
operator|!=
literal|'.'
operator|&&
name|c
operator|!=
literal|'_'
operator|&&
operator|!
operator|(
literal|'0'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'9'
operator|)
operator|&&
operator|!
operator|(
literal|'a'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'z'
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|name
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parseParameters
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
expr|<
name|String
operator|,
name|String
operator|>
name|emptyMap
argument_list|()
return|;
block|}
comment|// Extracts k1=v1, k2=v2 from mime/type; k1=v1; k2=v2
comment|// Note - this logic isn't fully RFC2045 compliant yet, as it
comment|//  doesn't fully handle quoted keys or values (eg containing ; or =)
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|string
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|key
init|=
name|string
decl_stmt|;
name|String
name|value
init|=
literal|""
decl_stmt|;
name|int
name|semicolon
init|=
name|string
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|semicolon
operator|!=
operator|-
literal|1
condition|)
block|{
name|key
operator|=
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|semicolon
argument_list|)
expr_stmt|;
name|string
operator|=
name|string
operator|.
name|substring
argument_list|(
name|semicolon
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|string
operator|=
literal|""
expr_stmt|;
block|}
name|int
name|equals
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|equals
operator|!=
operator|-
literal|1
condition|)
block|{
name|value
operator|=
name|key
operator|.
name|substring
argument_list|(
name|equals
operator|+
literal|1
argument_list|)
expr_stmt|;
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|equals
argument_list|)
expr_stmt|;
block|}
name|key
operator|=
name|key
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|parameters
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|unquote
argument_list|(
name|value
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parameters
return|;
block|}
comment|/**      * Fuzzy unquoting mechanism that works also with somewhat malformed      * quotes.      *      * @param s string to unquote      * @return unquoted string      */
specifier|private
specifier|static
name|String
name|unquote
parameter_list|(
name|String
name|s
parameter_list|)
block|{
while|while
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"'"
argument_list|)
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"\""
argument_list|)
operator|||
name|s
operator|.
name|endsWith
argument_list|(
literal|"'"
argument_list|)
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**      * Canonical string representation of this media type.      */
specifier|private
specifier|final
name|String
name|string
decl_stmt|;
comment|/**      * Location of the "/" character separating the type and the subtype      * tokens in {@link #string}.      */
specifier|private
specifier|final
name|int
name|slash
decl_stmt|;
comment|/**      * Location of the first ";" character separating the type part of      * {@link #string} from possible parameters. Length of {@link #string}      * in case there are no parameters.      */
specifier|private
specifier|final
name|int
name|semicolon
decl_stmt|;
comment|/**      * Immutable sorted map of media type parameters.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
decl_stmt|;
specifier|public
name|MediaType
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|subtype
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
block|{
name|type
operator|=
name|type
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
name|subtype
operator|=
name|subtype
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
name|this
operator|.
name|slash
operator|=
name|type
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|semicolon
operator|=
name|slash
operator|+
literal|1
operator|+
name|subtype
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|parameters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|parameters
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|string
operator|=
name|type
operator|+
literal|'/'
operator|+
name|subtype
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|subtype
argument_list|)
expr_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|parameters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|SPECIAL_OR_WHITESPACE
operator|.
name|matcher
argument_list|(
name|value
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|SPECIAL
operator|.
name|matcher
argument_list|(
name|value
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\\\$0"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|string
operator|=
name|builder
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|Collections
operator|.
name|unmodifiableSortedMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|MediaType
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|subtype
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|subtype
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|MediaType
parameter_list|(
name|String
name|string
parameter_list|,
name|int
name|slash
parameter_list|)
block|{
assert|assert
name|slash
operator|!=
operator|-
literal|1
assert|;
assert|assert
name|string
operator|.
name|charAt
argument_list|(
name|slash
argument_list|)
operator|==
literal|'/'
assert|;
assert|assert
name|isSimpleName
argument_list|(
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
argument_list|)
assert|;
assert|assert
name|isSimpleName
argument_list|(
name|string
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|)
argument_list|)
assert|;
name|this
operator|.
name|string
operator|=
name|string
expr_stmt|;
name|this
operator|.
name|slash
operator|=
name|slash
expr_stmt|;
name|this
operator|.
name|semicolon
operator|=
name|string
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|union
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|a
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|b
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|a
return|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|union
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|union
operator|.
name|putAll
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|union
operator|.
name|putAll
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|union
return|;
block|}
block|}
specifier|public
name|MediaType
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
argument_list|(
name|type
operator|.
name|getType
argument_list|()
argument_list|,
name|type
operator|.
name|getSubtype
argument_list|()
argument_list|,
name|union
argument_list|(
name|type
operator|.
name|parameters
argument_list|,
name|parameters
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a media type by adding a parameter to a base type.      *      * @param type base type      * @param name parameter name      * @param value parameter value      * @since Apache Tika 1.2      */
specifier|public
name|MediaType
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a media type by adding the "charset" parameter to a base type.      *      * @param type base type      * @param charset charset value      * @since Apache Tika 1.2      */
specifier|public
name|MediaType
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|Charset
name|charset
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
literal|"charset"
argument_list|,
name|charset
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the base form of the MediaType, excluding      *  any parameters, such as "text/plain" for      *  "text/plain; charset=utf-8"      */
specifier|public
name|MediaType
name|getBaseType
parameter_list|()
block|{
if|if
condition|(
name|parameters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|semicolon
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Return the Type of the MediaType, such as      *  "text" for "text/plain"      */
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
return|;
block|}
comment|/**      * Return the Sub-Type of the MediaType,       *  such as "plain" for "text/plain"      */
specifier|public
name|String
name|getSubtype
parameter_list|()
block|{
return|return
name|string
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|,
name|semicolon
argument_list|)
return|;
block|}
comment|/**      * Checks whether this media type contains parameters.      *      * @since Apache Tika 0.8      * @return<code>true</code> if this type has one or more parameters,      *<code>false</code> otherwise      */
specifier|public
name|boolean
name|hasParameters
parameter_list|()
block|{
return|return
operator|!
name|parameters
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Returns an immutable sorted map of the parameters of this media type.      * The parameter names are guaranteed to be trimmed and in lower case.      *      * @return sorted map of parameters      */
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|string
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|instanceof
name|MediaType
condition|)
block|{
name|MediaType
name|that
init|=
operator|(
name|MediaType
operator|)
name|object
decl_stmt|;
return|return
name|string
operator|.
name|equals
argument_list|(
name|that
operator|.
name|string
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|string
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|MediaType
name|that
parameter_list|)
block|{
return|return
name|string
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|string
argument_list|)
return|;
block|}
block|}
end_class

end_unit

