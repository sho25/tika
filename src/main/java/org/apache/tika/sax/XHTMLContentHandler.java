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
name|sax
package|;
end_package

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
name|Set
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

begin_comment
comment|/**  * Content handler decorator that simplifies the task of producing XHTML  * events for Tika content parsers.  */
end_comment

begin_class
specifier|public
class|class
name|XHTMLContentHandler
extends|extends
name|SafeContentHandler
block|{
comment|/**      * The XHTML namespace URI      */
specifier|public
specifier|static
specifier|final
name|String
name|XHTML
init|=
literal|"http://www.w3.org/1999/xhtml"
decl_stmt|;
comment|/**      * The newline character that gets inserted after block elements.      */
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|NL
init|=
operator|new
name|char
index|[]
block|{
literal|'\n'
block|}
decl_stmt|;
comment|/**      * The tab character gets inserted before table cells and list items.      */
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|TAB
init|=
operator|new
name|char
index|[]
block|{
literal|'\t'
block|}
decl_stmt|;
comment|/**      * The elements that get prepended with the {@link #TAB} character.      */
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|INDENT
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
literal|"li"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"dd"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"dt"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"th"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**      * The elements that get appended with the {@link #NL} character.      */
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ENDLINE
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
literal|"p"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"h1"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"h2"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"h3"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"h4"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"h5"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"h6"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"ol"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"dl"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"pre"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"hr"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"blockquote"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"address"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"fieldset"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"form"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"noscript"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"li"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"dt"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"dd"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"noframes"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"br"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**      * Metadata associated with the document. Used to fill in the      *&lt;head/&gt; section.      */
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
comment|/**      * Flag to indicate whether the document element has been started.      */
specifier|private
name|boolean
name|started
init|=
literal|false
decl_stmt|;
specifier|public
name|XHTMLContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
comment|/**      * Starts an XHTML document by setting up the namespace mappings.      * The standard XHTML prefix is generated lazily when the first      * element is started.      */
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|startPrefixMapping
argument_list|(
literal|""
argument_list|,
name|XHTML
argument_list|)
expr_stmt|;
block|}
comment|/**      * Generates the following XHTML prefix when called for the first time:      *<pre>      *&lt;html&gt;      *&lt;head&gt;      *&lt;title&gt;...&lt;/title&gt;      *&lt;/head&gt;      *&lt;body&gt;      *</pre>      */
specifier|private
name|void
name|lazyStartDocument
parameter_list|()
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|started
operator|=
literal|true
expr_stmt|;
name|startElement
argument_list|(
literal|"html"
argument_list|)
expr_stmt|;
name|startElement
argument_list|(
literal|"head"
argument_list|)
expr_stmt|;
name|startElement
argument_list|(
literal|"title"
argument_list|)
expr_stmt|;
name|String
name|title
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
operator|&&
name|title
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|characters
argument_list|(
name|title
argument_list|)
expr_stmt|;
block|}
name|endElement
argument_list|(
literal|"title"
argument_list|)
expr_stmt|;
name|endElement
argument_list|(
literal|"head"
argument_list|)
expr_stmt|;
name|startElement
argument_list|(
literal|"body"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Ends the XHTML document by writing the following footer and      * clearing the namespace mappings:      *<pre>      *&lt;/body&gt;      *&lt;/html&gt;      *</pre>      */
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|lazyStartDocument
argument_list|()
expr_stmt|;
name|endElement
argument_list|(
literal|"body"
argument_list|)
expr_stmt|;
name|endElement
argument_list|(
literal|"html"
argument_list|)
expr_stmt|;
name|endPrefixMapping
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * Starts the given element. Table cells and list items are automatically      * indented by emitting a tab character as ignorable whitespace.      */
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
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
name|lazyStartDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|&&
name|INDENT
operator|.
name|contains
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|ignorableWhitespace
argument_list|(
name|TAB
argument_list|,
literal|0
argument_list|,
name|TAB
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|local
argument_list|,
name|name
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
comment|/**      * Ends the given element. Block elements are automatically followed      * by a newline character.      */
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
name|super
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|local
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|&&
name|ENDLINE
operator|.
name|contains
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|ignorableWhitespace
argument_list|(
name|NL
argument_list|,
literal|0
argument_list|,
name|NL
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------< public convenience methods>
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|startElement
argument_list|(
name|XHTML
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
operator|new
name|AttributesImpl
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|attribute
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
name|AttributesImpl
name|attributes
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
name|XHTML
argument_list|,
name|attribute
argument_list|,
name|attribute
argument_list|,
literal|"CDATA"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|startElement
argument_list|(
name|XHTML
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|endElement
argument_list|(
name|XHTML
argument_list|,
name|name
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|characters
parameter_list|(
name|String
name|characters
parameter_list|)
throws|throws
name|SAXException
block|{
name|characters
argument_list|(
name|characters
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|characters
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|element
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
name|startElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|characters
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|endElement
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

