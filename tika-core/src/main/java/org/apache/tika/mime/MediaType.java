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
name|mime
package|;
end_package

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
name|Map
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
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|NO_PARAMETERS
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
comment|// TIKA-350: handle charset as first element in content-type
comment|// See http://www.ietf.org/rfc/rfc2045.txt for valid mime-type characters.
specifier|private
specifier|static
specifier|final
name|String
name|VALID_MIMETYPE_CHARS
init|=
literal|"[^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MIME_TYPE_PATTERN_STRING
init|=
literal|"("
operator|+
name|VALID_MIMETYPE_CHARS
operator|+
literal|"+)"
operator|+
literal|"\\s*/\\s*"
operator|+
literal|"("
operator|+
name|VALID_MIMETYPE_CHARS
operator|+
literal|"+)"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|CONTENT_TYPE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?is)\\s*"
operator|+
name|MIME_TYPE_PATTERN_STRING
operator|+
literal|"\\s*($|;.*)"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|CONTENT_TYPE_CHARSET_FIRST_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?i)\\s*(charset\\s*=\\s*[^\\c;\\s]+)\\s*;\\s*"
operator|+
name|MIME_TYPE_PATTERN_STRING
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|OCTET_STREAM
init|=
operator|new
name|MediaType
argument_list|(
literal|"application"
argument_list|,
literal|"octet-stream"
argument_list|,
name|NO_PARAMETERS
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TEXT_PLAIN
init|=
operator|new
name|MediaType
argument_list|(
literal|"text"
argument_list|,
literal|"plain"
argument_list|,
name|NO_PARAMETERS
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|APPLICATION_XML
init|=
operator|new
name|MediaType
argument_list|(
literal|"application"
argument_list|,
literal|"xml"
argument_list|,
name|NO_PARAMETERS
argument_list|)
decl_stmt|;
comment|/**      * Parses the given string to a media type. The string is expected to be of      * the form "type/subtype(; parameter=...)*" as defined in RFC 2045, though      * we also handle "charset=xxx; type/subtype" for broken web servers.      *       * @param string      *            media type string to be parsed      * @return parsed media type, or<code>null</code> if parsing fails      */
specifier|public
specifier|static
name|MediaType
name|parse
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|String
name|type
decl_stmt|;
name|String
name|subtype
decl_stmt|;
name|String
name|params
decl_stmt|;
name|Matcher
name|m
init|=
name|CONTENT_TYPE_PATTERN
operator|.
name|matcher
argument_list|(
name|string
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|type
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|subtype
operator|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|params
operator|=
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|m
operator|=
name|CONTENT_TYPE_CHARSET_FIRST_PATTERN
operator|.
name|matcher
argument_list|(
name|string
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|params
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|type
operator|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|subtype
operator|=
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
name|MediaType
name|result
init|=
operator|new
name|MediaType
argument_list|(
name|type
argument_list|,
name|subtype
argument_list|)
decl_stmt|;
name|String
index|[]
name|paramPieces
init|=
name|params
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|paramPiece
range|:
name|paramPieces
control|)
block|{
name|String
index|[]
name|keyValue
init|=
name|paramPiece
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyValue
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
continue|continue;
block|}
name|String
name|key
init|=
name|keyValue
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
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
name|result
operator|.
name|parameters
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|keyValue
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
specifier|private
specifier|final
name|String
name|subtype
decl_stmt|;
specifier|private
specifier|final
name|SortedMap
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
name|this
operator|.
name|type
operator|=
name|type
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
name|this
operator|.
name|subtype
operator|=
name|subtype
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
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
name|this
operator|.
name|parameters
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
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
name|NO_PARAMETERS
argument_list|)
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
name|type
argument_list|,
name|type
operator|.
name|subtype
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
operator|new
name|MediaType
argument_list|(
name|type
argument_list|,
name|subtype
argument_list|)
return|;
block|}
block|}
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|String
name|getSubtype
parameter_list|()
block|{
return|return
name|subtype
return|;
block|}
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
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|parameters
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isSpecializationOf
parameter_list|(
name|MediaType
name|that
parameter_list|)
block|{
if|if
condition|(
name|OCTET_STREAM
operator|.
name|equals
argument_list|(
name|that
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|that
operator|.
name|type
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|parameters
operator|.
name|entrySet
argument_list|()
operator|.
name|containsAll
argument_list|(
name|that
operator|.
name|parameters
operator|.
name|entrySet
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|TEXT_PLAIN
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getBaseType
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|APPLICATION_XML
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getBaseType
argument_list|()
argument_list|)
operator|&&
name|subtype
operator|.
name|endsWith
argument_list|(
literal|"+xml"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|subtype
operator|.
name|equals
argument_list|(
name|that
operator|.
name|subtype
argument_list|)
return|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
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
return|return
name|builder
operator|.
name|toString
argument_list|()
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
name|type
operator|.
name|equals
argument_list|(
name|that
operator|.
name|type
argument_list|)
operator|&&
name|subtype
operator|.
name|equals
argument_list|(
name|that
operator|.
name|subtype
argument_list|)
operator|&&
name|parameters
operator|.
name|equals
argument_list|(
name|that
operator|.
name|parameters
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
name|int
name|hash
init|=
literal|17
decl_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|type
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|subtype
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|parameters
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
block|}
end_class

end_unit

