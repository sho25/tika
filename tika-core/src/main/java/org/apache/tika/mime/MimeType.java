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

begin_comment
comment|// JDK imports
end_comment

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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|StringUtil
import|;
end_import

begin_comment
comment|/**  * Internet media type.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MimeType
implements|implements
name|Comparable
argument_list|<
name|MimeType
argument_list|>
block|{
comment|/**      * Checks that the given string is a valid Internet media type name      * based on rules from RFC 2054 section 5.3. For validation purposes the      * rules can be simplified to the following:      *<pre>      * name := token "/" token      * token := 1*&lt;any (US-ASCII) CHAR except SPACE, CTLs, or tspecials&gt;      * tspecials :=  "(" / ")" / "&lt;" / "&gt;" / "@" / "," / ";" / ":" /      *               "\" /<"> / "/" / "[" / "]" / "?" / "="      *</pre>      *      * @param name name string      * @return<code>true</code> if the string is a valid media type name,      *<code>false</code> otherwise      */
specifier|public
specifier|static
name|boolean
name|isValid
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Name is missing"
argument_list|)
throw|;
block|}
name|boolean
name|slash
init|=
literal|false
decl_stmt|;
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
name|ch
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
name|ch
operator|<=
literal|' '
operator|||
name|ch
operator|>=
literal|127
operator|||
name|ch
operator|==
literal|'('
operator|||
name|ch
operator|==
literal|')'
operator|||
name|ch
operator|==
literal|'<'
operator|||
name|ch
operator|==
literal|'>'
operator|||
name|ch
operator|==
literal|'@'
operator|||
name|ch
operator|==
literal|','
operator|||
name|ch
operator|==
literal|';'
operator|||
name|ch
operator|==
literal|':'
operator|||
name|ch
operator|==
literal|'\\'
operator|||
name|ch
operator|==
literal|'"'
operator|||
name|ch
operator|==
literal|'['
operator|||
name|ch
operator|==
literal|']'
operator|||
name|ch
operator|==
literal|'?'
operator|||
name|ch
operator|==
literal|'='
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
literal|'/'
condition|)
block|{
if|if
condition|(
name|slash
operator|||
name|i
operator|==
literal|0
operator|||
name|i
operator|+
literal|1
operator|==
name|name
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|slash
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|slash
return|;
block|}
comment|/**      * The media type registry that contains this type.      */
specifier|private
specifier|final
name|MimeTypes
name|registry
decl_stmt|;
comment|/**      * Lower case name of this media type.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Description of this media type.      */
specifier|private
name|String
name|description
init|=
literal|""
decl_stmt|;
comment|/**      * The parent type of this media type, or<code>null</code> if this      * is a top-level type.      */
specifier|private
name|MimeType
name|superType
init|=
literal|null
decl_stmt|;
comment|/**      * The child types of this media type.      */
specifier|private
specifier|final
name|SortedSet
argument_list|<
name|MimeType
argument_list|>
name|subTypes
init|=
operator|new
name|TreeSet
argument_list|<
name|MimeType
argument_list|>
argument_list|()
decl_stmt|;
comment|/** The magics associated to this Mime-Type */
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|Magic
argument_list|>
name|magics
init|=
operator|new
name|ArrayList
argument_list|<
name|Magic
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Lower case alias names of this media type.      */
specifier|private
specifier|final
name|SortedSet
argument_list|<
name|String
argument_list|>
name|aliases
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/** The root-XML associated to this Mime-Type */
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|RootXML
argument_list|>
name|rootXML
init|=
operator|new
name|ArrayList
argument_list|<
name|RootXML
argument_list|>
argument_list|()
decl_stmt|;
comment|/** The minimum length of data to provides for magic analyzis */
specifier|private
name|int
name|minLength
init|=
literal|0
decl_stmt|;
comment|/**      * Creates a media type with the give name and containing media type      * registry. The name is expected to be valid and normalized to lower      * case. This constructor should only be called by      * {@link MimeTypes#forName(String)} to keep the media type registry      * up to date.      *      * @param registry the media type registry that contains this type      * @param name media type name      */
name|MimeType
parameter_list|(
name|MimeTypes
name|registry
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|registry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Registry is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|MimeType
operator|.
name|isValid
argument_list|(
name|name
argument_list|)
operator|||
operator|!
name|name
operator|.
name|equals
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Media type name is invalid"
argument_list|)
throw|;
block|}
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Returns the name of this media type.      *       * @return media type name (lower case)      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Returns the parent of this media type.      *      * @return parent media type, or<code>null</code>      */
specifier|public
name|MimeType
name|getSuperType
parameter_list|()
block|{
return|return
name|superType
return|;
block|}
specifier|public
name|void
name|setSuperType
parameter_list|(
name|MimeType
name|type
parameter_list|)
throws|throws
name|MimeTypeException
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"MimeType is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|.
name|registry
operator|!=
name|registry
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"MimeType is from a different registry"
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|isDescendantOf
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// ignore, already a descendant of the given type
block|}
elseif|else
if|if
condition|(
name|this
operator|==
name|type
condition|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Media type can not inherit itself: "
operator|+
name|type
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|isDescendantOf
argument_list|(
name|this
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Media type can not inherit its descendant: "
operator|+
name|type
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|superType
operator|==
literal|null
condition|)
block|{
name|superType
operator|=
name|type
expr_stmt|;
name|superType
operator|.
name|subTypes
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|isDescendantOf
argument_list|(
name|superType
argument_list|)
condition|)
block|{
name|superType
operator|.
name|subTypes
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|superType
operator|=
name|type
expr_stmt|;
name|superType
operator|.
name|subTypes
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Conflicting media type inheritance: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
specifier|public
name|SortedSet
argument_list|<
name|MimeType
argument_list|>
name|getSubTypes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSortedSet
argument_list|(
name|subTypes
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isDescendantOf
parameter_list|(
name|MimeType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"MimeType is missing"
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|registry
init|)
block|{
for|for
control|(
name|MimeType
name|t
init|=
name|superType
init|;
name|t
operator|!=
literal|null
condition|;
name|t
operator|=
name|t
operator|.
name|superType
control|)
block|{
if|if
condition|(
name|t
operator|==
name|type
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Returns the description of this media type.      *       * @return media type description      */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/**      * Set the description of this media type.      *       * @param description media type description      */
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
if|if
condition|(
name|description
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Description is missing"
argument_list|)
throw|;
block|}
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/**      * Returns the aliases of this media type. The returned set is      * an immutable view of the set.      *      * @return media type aliases, unmodifiable, not null      */
specifier|public
name|SortedSet
argument_list|<
name|String
argument_list|>
name|getAliases
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSortedSet
argument_list|(
name|aliases
argument_list|)
return|;
block|}
comment|/**      * Adds an alias name for this media type.      *      * @param alias media type alias (case insensitive)      * @throws MimeTypeException if the alias is invalid or      *                           already registered for another media type      */
specifier|public
name|void
name|addAlias
parameter_list|(
name|String
name|alias
parameter_list|)
throws|throws
name|MimeTypeException
block|{
if|if
condition|(
name|isValid
argument_list|(
name|alias
argument_list|)
condition|)
block|{
name|alias
operator|=
name|alias
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|alias
argument_list|)
operator|&&
operator|!
name|aliases
operator|.
name|contains
argument_list|(
name|alias
argument_list|)
condition|)
block|{
name|registry
operator|.
name|addAlias
argument_list|(
name|this
argument_list|,
name|alias
argument_list|)
expr_stmt|;
name|aliases
operator|.
name|add
argument_list|(
name|alias
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Invalid media type alias: "
operator|+
name|alias
argument_list|)
throw|;
block|}
block|}
comment|/**      * Add some rootXML info to this mime-type      *       * @param namespaceURI      * @param localName      */
name|void
name|addRootXML
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
name|rootXML
operator|.
name|add
argument_list|(
operator|new
name|RootXML
argument_list|(
name|this
argument_list|,
name|namespaceURI
argument_list|,
name|localName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|matchesXML
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|RootXML
name|xml
init|=
literal|null
decl_stmt|;
name|String
name|content
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rootXML
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|xml
operator|=
name|rootXML
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|xml
operator|.
name|matches
argument_list|(
name|content
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
name|boolean
name|hasRootXML
parameter_list|()
block|{
return|return
operator|(
name|rootXML
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
return|;
block|}
name|RootXML
index|[]
name|getRootXMLs
parameter_list|()
block|{
return|return
name|rootXML
operator|.
name|toArray
argument_list|(
operator|new
name|RootXML
index|[
name|rootXML
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
name|Magic
index|[]
name|getMagics
parameter_list|()
block|{
return|return
name|magics
operator|.
name|toArray
argument_list|(
operator|new
name|Magic
index|[
name|magics
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
name|void
name|addMagic
parameter_list|(
name|Magic
name|magic
parameter_list|)
block|{
if|if
condition|(
name|magic
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|magics
operator|.
name|add
argument_list|(
name|magic
argument_list|)
expr_stmt|;
block|}
name|int
name|getMinLength
parameter_list|()
block|{
return|return
name|minLength
return|;
block|}
specifier|public
name|boolean
name|hasMagic
parameter_list|()
block|{
return|return
operator|(
name|magics
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
return|;
block|}
specifier|public
name|boolean
name|matchesMagic
parameter_list|(
name|byte
index|[]
name|data
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
name|magics
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Magic
name|magic
init|=
name|magics
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|magic
operator|.
name|eval
argument_list|(
name|data
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|matchesXML
argument_list|(
name|data
argument_list|)
operator|||
name|matchesMagic
argument_list|(
name|data
argument_list|)
return|;
block|}
comment|/**      * Defines a RootXML description. RootXML is made of a localName and/or a      * namespaceURI.      */
class|class
name|RootXML
block|{
specifier|private
specifier|final
specifier|static
name|int
name|PATTERN_FLAGS
init|=
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|DOTALL
operator||
name|Pattern
operator|.
name|MULTILINE
decl_stmt|;
specifier|private
name|MimeType
name|type
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|namespaceURI
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|localName
init|=
literal|null
decl_stmt|;
specifier|private
name|Pattern
name|pattern
init|=
literal|null
decl_stmt|;
name|RootXML
parameter_list|(
name|MimeType
name|type
parameter_list|,
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|namespaceURI
operator|=
name|namespaceURI
expr_stmt|;
name|this
operator|.
name|localName
operator|=
name|localName
expr_stmt|;
if|if
condition|(
operator|(
name|StringUtil
operator|.
name|isEmpty
argument_list|(
name|namespaceURI
argument_list|)
operator|)
operator|&&
operator|(
name|StringUtil
operator|.
name|isEmpty
argument_list|(
name|localName
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Both namespaceURI and localName cannot be null"
argument_list|)
throw|;
block|}
name|String
name|regex
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|StringUtil
operator|.
name|isEmpty
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
name|regex
operator|=
literal|".*<"
operator|+
name|localName
operator|+
literal|"[^<>]*>.*"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|StringUtil
operator|.
name|isEmpty
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|regex
operator|=
literal|".*<[^<>]*\\p{Space}xmlns=[\"\']?"
operator|+
name|namespaceURI
operator|+
literal|"[\"\']?[^<>]*>.*"
expr_stmt|;
block|}
else|else
block|{
name|regex
operator|=
literal|".*<"
operator|+
name|localName
operator|+
literal|"[^<>]*\\p{Space}xmlns=[\"\']?"
operator|+
name|namespaceURI
operator|+
literal|"[\"\']?[^<>]*>.*"
expr_stmt|;
block|}
name|this
operator|.
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|,
name|PATTERN_FLAGS
argument_list|)
expr_stmt|;
block|}
name|boolean
name|matches
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|matches
argument_list|(
operator|new
name|String
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
name|boolean
name|matches
parameter_list|(
name|String
name|data
parameter_list|)
block|{
return|return
name|pattern
operator|.
name|matcher
argument_list|(
name|data
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
name|MimeType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
name|String
name|getNameSpaceURI
parameter_list|()
block|{
return|return
name|namespaceURI
return|;
block|}
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|localName
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuffer
argument_list|()
operator|.
name|append
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|namespaceURI
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|localName
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|//----------------------------------------------------------< Comparable>
specifier|public
name|int
name|compareTo
parameter_list|(
name|MimeType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"MimeType is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|type
operator|==
name|this
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|isDescendantOf
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|isDescendantOf
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|superType
operator|!=
literal|null
condition|)
block|{
return|return
name|superType
operator|.
name|compareTo
argument_list|(
name|type
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|superType
operator|!=
literal|null
condition|)
block|{
return|return
name|compareTo
argument_list|(
name|type
operator|.
name|superType
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|name
operator|.
name|compareTo
argument_list|(
name|type
operator|.
name|name
argument_list|)
return|;
block|}
block|}
comment|//--------------------------------------------------------------< Object>
comment|/**      * Returns the name of this media type.      *      * @return media type name      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

