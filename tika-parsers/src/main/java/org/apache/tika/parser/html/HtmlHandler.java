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
name|html
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Set
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
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
name|sax
operator|.
name|TextContentHandler
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
name|sax
operator|.
name|XHTMLContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
class|class
name|HtmlHandler
extends|extends
name|TextContentHandler
block|{
comment|// List of attributes that need to be resolved.
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|URI_ATTRIBUTES
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
literal|"src"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"href"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"longdesc"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"usemap"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"data"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"cite"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"codebase"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"classid"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|HtmlMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|private
name|int
name|bodyLevel
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|discardLevel
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|titleLevel
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|title
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
name|HtmlHandler
parameter_list|(
name|HtmlMapper
name|mapper
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|super
argument_list|(
name|xhtml
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
name|this
operator|.
name|xhtml
operator|=
name|xhtml
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
comment|// Try to determine the default base URL, if one has not been given
if|if
condition|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_LOCATION
argument_list|)
operator|==
literal|null
condition|)
block|{
name|String
name|name
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|trim
argument_list|()
expr_stmt|;
try|try
block|{
operator|new
name|URL
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// test URL format
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LOCATION
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|// The resource name is not a valid URL, ignore it
block|}
block|}
block|}
block|}
specifier|public
name|HtmlHandler
parameter_list|(
name|HtmlMapper
name|mapper
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
argument_list|(
name|mapper
argument_list|,
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|local
parameter_list|,
name|String
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"TITLE"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|titleLevel
operator|>
literal|0
condition|)
block|{
name|titleLevel
operator|++
expr_stmt|;
block|}
if|if
condition|(
literal|"BODY"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
operator|(
literal|"FRAMESET"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|)
operator|||
name|bodyLevel
operator|>
literal|0
condition|)
block|{
name|bodyLevel
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|mapper
operator|.
name|isDiscardElement
argument_list|(
name|name
argument_list|)
operator|||
name|discardLevel
operator|>
literal|0
condition|)
block|{
name|discardLevel
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|bodyLevel
operator|==
literal|0
operator|&&
name|discardLevel
operator|==
literal|0
condition|)
block|{
if|if
condition|(
literal|"META"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|atts
operator|.
name|getValue
argument_list|(
literal|"content"
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// TIKA-478: For cases where we have either a name or "http-equiv", assume
comment|// that XHTMLContentHandler will emit these in the<head>, thus passing them
comment|// through safely.
if|if
condition|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"http-equiv"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"http-equiv"
argument_list|)
argument_list|,
name|atts
operator|.
name|getValue
argument_list|(
literal|"content"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// Record the meta tag in the metadata
name|metadata
operator|.
name|set
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|atts
operator|.
name|getValue
argument_list|(
literal|"content"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Normalize if possible
if|if
condition|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ICBM"
argument_list|)
condition|)
block|{
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\s*(-?\\d+\\.\\d+)[,\\s]+(-?\\d+\\.\\d+)\\s*"
argument_list|)
operator|.
name|matcher
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"content"
argument_list|)
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
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|LATITUDE
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|LONGITUDE
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"BASE"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|atts
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LOCATION
argument_list|,
name|resolve
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|local
argument_list|,
literal|"base"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"LINK"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|startElementWithSafeAttributes
argument_list|(
literal|"link"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bodyLevel
operator|>
literal|0
operator|&&
name|discardLevel
operator|==
literal|0
condition|)
block|{
name|String
name|safe
init|=
name|mapper
operator|.
name|mapSafeElement
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|safe
operator|!=
literal|null
condition|)
block|{
name|startElementWithSafeAttributes
argument_list|(
name|safe
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
name|title
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startElementWithSafeAttributes
parameter_list|(
name|String
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|atts
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return;
block|}
name|AttributesImpl
name|newAttributes
init|=
operator|new
name|AttributesImpl
argument_list|(
name|atts
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|att
init|=
literal|0
init|;
name|att
operator|<
name|newAttributes
operator|.
name|getLength
argument_list|()
condition|;
name|att
operator|++
control|)
block|{
name|String
name|normAttrName
init|=
name|mapper
operator|.
name|mapSafeAttribute
argument_list|(
name|name
argument_list|,
name|newAttributes
operator|.
name|getLocalName
argument_list|(
name|att
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|normAttrName
operator|==
literal|null
condition|)
block|{
name|newAttributes
operator|.
name|removeAttribute
argument_list|(
name|att
argument_list|)
expr_stmt|;
name|att
operator|--
expr_stmt|;
block|}
else|else
block|{
comment|// We have a remapped attribute name, so set it as it might have changed.
name|newAttributes
operator|.
name|setLocalName
argument_list|(
name|att
argument_list|,
name|normAttrName
argument_list|)
expr_stmt|;
comment|// And resolve relative links. Eventually this should be pushed
comment|// into the HtmlMapper code.
if|if
condition|(
name|URI_ATTRIBUTES
operator|.
name|contains
argument_list|(
name|normAttrName
argument_list|)
condition|)
block|{
name|newAttributes
operator|.
name|setValue
argument_list|(
name|att
argument_list|,
name|resolve
argument_list|(
name|newAttributes
operator|.
name|getValue
argument_list|(
name|att
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
name|name
argument_list|,
name|newAttributes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|local
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|bodyLevel
operator|==
literal|0
operator|&&
name|discardLevel
operator|==
literal|0
condition|)
block|{
if|if
condition|(
literal|"LINK"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"link"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"BASE"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"base"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bodyLevel
operator|>
literal|0
operator|&&
name|discardLevel
operator|==
literal|0
condition|)
block|{
name|String
name|safe
init|=
name|mapper
operator|.
name|mapSafeElement
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|safe
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
name|safe
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XHTMLContentHandler
operator|.
name|ENDLINE
operator|.
name|contains
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
condition|)
block|{
comment|// TIKA-343: Replace closing block tags (and<br/>) with a
comment|// newline unless the HtmlMapper above has already mapped
comment|// them to something else
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|titleLevel
operator|>
literal|0
condition|)
block|{
name|titleLevel
operator|--
expr_stmt|;
if|if
condition|(
name|titleLevel
operator|==
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|,
name|title
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bodyLevel
operator|>
literal|0
condition|)
block|{
name|bodyLevel
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|discardLevel
operator|>
literal|0
condition|)
block|{
name|discardLevel
operator|--
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|titleLevel
operator|>
literal|0
operator|&&
name|bodyLevel
operator|==
literal|0
condition|)
block|{
name|title
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bodyLevel
operator|>
literal|0
operator|&&
name|discardLevel
operator|==
literal|0
condition|)
block|{
name|super
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|bodyLevel
operator|>
literal|0
operator|&&
name|discardLevel
operator|==
literal|0
condition|)
block|{
name|super
operator|.
name|ignorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|resolve
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|url
operator|=
name|url
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|// Return the URL as-is if no base URL is available
if|if
condition|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_LOCATION
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
name|url
return|;
block|}
comment|// Check for common non-hierarchical and pseudo URI prefixes
name|String
name|lower
init|=
name|url
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
if|if
condition|(
name|lower
operator|.
name|startsWith
argument_list|(
literal|"urn:"
argument_list|)
operator|||
name|lower
operator|.
name|startsWith
argument_list|(
literal|"mailto:"
argument_list|)
operator|||
name|lower
operator|.
name|startsWith
argument_list|(
literal|"tel:"
argument_list|)
operator|||
name|lower
operator|.
name|startsWith
argument_list|(
literal|"data:"
argument_list|)
operator|||
name|lower
operator|.
name|startsWith
argument_list|(
literal|"javascript:"
argument_list|)
operator|||
name|lower
operator|.
name|startsWith
argument_list|(
literal|"about:"
argument_list|)
condition|)
block|{
return|return
name|url
return|;
block|}
try|try
block|{
name|URL
name|base
init|=
operator|new
name|URL
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_LOCATION
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
comment|// We need to handle one special case, where the relativeUrl is
comment|// just a query string (like "?pid=1"), and the baseUrl doesn't
comment|// end with a '/'. In that case, the URL class removes the last
comment|// portion of the path, which we don't want.
name|String
name|path
init|=
name|base
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"?"
argument_list|)
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
operator|new
name|URL
argument_list|(
name|base
operator|.
name|getProtocol
argument_list|()
argument_list|,
name|base
operator|.
name|getHost
argument_list|()
argument_list|,
name|base
operator|.
name|getPort
argument_list|()
argument_list|,
name|base
operator|.
name|getPath
argument_list|()
operator|+
name|url
argument_list|)
operator|.
name|toExternalForm
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|URL
argument_list|(
name|base
argument_list|,
name|url
argument_list|)
operator|.
name|toExternalForm
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|// Unknown or broken format; just return the URL as received.
return|return
name|url
return|;
block|}
block|}
block|}
end_class

end_unit

