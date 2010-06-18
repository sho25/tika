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
name|util
operator|.
name|ArrayList
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
literal|4357830439860729201L
decl_stmt|;
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
comment|/**      * The normalized media type name.      */
specifier|private
specifier|final
name|MediaType
name|type
decl_stmt|;
comment|/**      * Description of this media type.      */
specifier|private
name|String
name|description
init|=
literal|""
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
comment|/**      * Creates a media type with the give name and containing media type      * registry. The name is expected to be valid and normalized to lower      * case. This constructor should only be called by      * {@link MimeTypes#forName(String)} to keep the media type registry      * up to date.      *      * @param type normalized media type name      */
name|MimeType
parameter_list|(
name|MediaType
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
literal|"Media type name is missing"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Returns the normalized media type name.      *      * @return media type      */
specifier|public
name|MediaType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Returns the name of this media type.      *      * @return media type name (lower case)      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|type
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns the description of this media type.      *      * @return media type description      */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/**      * Set the description of this media type.      *      * @param description media type description      */
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
comment|/**      * Add some rootXML info to this mime-type      *      * @param namespaceURI      * @param localName      */
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
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
for|for
control|(
name|RootXML
name|xml
range|:
name|rootXML
control|)
block|{
if|if
condition|(
name|xml
operator|.
name|matches
argument_list|(
name|namespaceURI
argument_list|,
name|localName
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
name|matchesMagic
argument_list|(
name|data
argument_list|)
return|;
block|}
comment|/**      * Defines a RootXML description. RootXML is made of a localName and/or a      * namespaceURI.      */
class|class
name|RootXML
implements|implements
name|Serializable
block|{
comment|/**          * Serial version UID.          */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5140496601491000730L
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
if|if
condition|(
name|isEmpty
argument_list|(
name|namespaceURI
argument_list|)
operator|&&
name|isEmpty
argument_list|(
name|localName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Both namespaceURI and localName cannot be empty"
argument_list|)
throw|;
block|}
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
block|}
name|boolean
name|matches
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
comment|//Compare namespaces
if|if
condition|(
operator|!
name|isEmpty
argument_list|(
name|this
operator|.
name|namespaceURI
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|namespaceURI
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|// else if it was empty then check to see if the provided namespaceURI
comment|// is empty. If it is not, then these two aren't equal and return false
if|if
condition|(
operator|!
name|isEmpty
argument_list|(
name|namespaceURI
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|//Compare root element's local name
if|if
condition|(
operator|!
name|isEmpty
argument_list|(
name|this
operator|.
name|localName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|localName
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|// else if it was empty then check to see if the provided localName
comment|// is empty. If it is not, then these two aren't equal and return false
if|if
condition|(
operator|!
name|isEmpty
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**          * Checks if a string is null or empty.          */
specifier|private
name|boolean
name|isEmpty
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
operator|(
name|str
operator|==
literal|null
operator|)
operator|||
operator|(
name|str
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
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
name|type
operator|+
literal|", "
operator|+
name|namespaceURI
operator|+
literal|", "
operator|+
name|localName
return|;
block|}
block|}
comment|//----------------------------------------------------------< Comparable>
specifier|public
name|int
name|compareTo
parameter_list|(
name|MimeType
name|mime
parameter_list|)
block|{
return|return
name|type
operator|.
name|compareTo
argument_list|(
name|mime
operator|.
name|type
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------< Object>
comment|/**      * Returns the name of this media type.      *      * @return media type name      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|type
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

