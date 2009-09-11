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
name|parser
operator|.
name|html
package|;
end_package

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
name|io
operator|.
name|CloseShieldInputStream
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
name|parser
operator|.
name|Parser
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
name|TeeContentHandler
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
name|WriteOutContentHandler
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|xpath
operator|.
name|Matcher
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
name|xpath
operator|.
name|MatchingContentHandler
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
name|xpath
operator|.
name|XPathParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|cyberneko
operator|.
name|html
operator|.
name|parsers
operator|.
name|SAXParser
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
name|InputSource
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

begin_comment
comment|/**  * HTML parser. Uses CyberNeko to turn the input document to HTML SAX events,  * and post-processes the events to produce XHTML and metadata expected by  * Tika clients.  */
end_comment

begin_class
specifier|public
class|class
name|HtmlParser
implements|implements
name|Parser
block|{
comment|/**      * Set of safe mappings from incoming HTML elements to outgoing      * XHTML elements. Ensures that the output is valid XHTML 1.0 Strict.      */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|SAFE_ELEMENTS
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
comment|/**      * Set of HTML elements whose content will be discarded.      */
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|DISCARD_ELEMENTS
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
comment|// Based on http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"P"
argument_list|,
literal|"p"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"H1"
argument_list|,
literal|"h1"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"H2"
argument_list|,
literal|"h2"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"H3"
argument_list|,
literal|"h3"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"H4"
argument_list|,
literal|"h4"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"H5"
argument_list|,
literal|"h5"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"H6"
argument_list|,
literal|"h6"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"UL"
argument_list|,
literal|"ul"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"OL"
argument_list|,
literal|"ol"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"LI"
argument_list|,
literal|"li"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"DL"
argument_list|,
literal|"dl"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"DT"
argument_list|,
literal|"dt"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"DD"
argument_list|,
literal|"dd"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"PRE"
argument_list|,
literal|"pre"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"BLOCKQUOTE"
argument_list|,
literal|"blockquote"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"TABLE"
argument_list|,
literal|"table"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"THEAD"
argument_list|,
literal|"thead"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"TBODY"
argument_list|,
literal|"tbody"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"TR"
argument_list|,
literal|"tr"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"TH"
argument_list|,
literal|"th"
argument_list|)
expr_stmt|;
name|SAFE_ELEMENTS
operator|.
name|put
argument_list|(
literal|"TD"
argument_list|,
literal|"td"
argument_list|)
expr_stmt|;
name|DISCARD_ELEMENTS
operator|.
name|add
argument_list|(
literal|"STYLE"
argument_list|)
expr_stmt|;
name|DISCARD_ELEMENTS
operator|.
name|add
argument_list|(
literal|"SCRIPT"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
comment|// Protect the stream from being closed by CyberNeko
name|stream
operator|=
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|// Prepare the input source using the encoding hint if available
name|InputSource
name|source
init|=
operator|new
name|InputSource
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|String
name|encoding
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
name|source
operator|.
name|setEncoding
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
block|}
comment|// Prepare the HTML content handler that generates proper
comment|// XHTML events to records relevant document metadata
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|XPathParser
name|xpath
init|=
operator|new
name|XPathParser
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Matcher
name|body
init|=
name|xpath
operator|.
name|parse
argument_list|(
literal|"/HTML/BODY//node()"
argument_list|)
decl_stmt|;
name|Matcher
name|title
init|=
name|xpath
operator|.
name|parse
argument_list|(
literal|"/HTML/HEAD/TITLE//node()"
argument_list|)
decl_stmt|;
name|Matcher
name|meta
init|=
name|xpath
operator|.
name|parse
argument_list|(
literal|"/HTML/HEAD/META//node()"
argument_list|)
decl_stmt|;
name|handler
operator|=
operator|new
name|TeeContentHandler
argument_list|(
operator|new
name|MatchingContentHandler
argument_list|(
name|getBodyHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|body
argument_list|)
argument_list|,
operator|new
name|MatchingContentHandler
argument_list|(
name|getTitleHandler
argument_list|(
name|metadata
argument_list|)
argument_list|,
name|title
argument_list|)
argument_list|,
operator|new
name|MatchingContentHandler
argument_list|(
name|getMetaHandler
argument_list|(
name|metadata
argument_list|)
argument_list|,
name|meta
argument_list|)
argument_list|)
expr_stmt|;
comment|// Parse the HTML document
name|SAXParser
name|parser
init|=
operator|new
name|SAXParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|setContentHandler
argument_list|(
operator|new
name|XHTMLDowngradeHandler
argument_list|(
name|handler
argument_list|)
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0.      */
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ContentHandler
name|getTitleHandler
parameter_list|(
specifier|final
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
operator|new
name|WriteOutContentHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|u
parameter_list|,
name|String
name|l
parameter_list|,
name|String
name|n
parameter_list|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|,
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|private
name|ContentHandler
name|getMetaHandler
parameter_list|(
specifier|final
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
operator|new
name|WriteOutContentHandler
argument_list|()
block|{
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
block|}
block|}
block|}
return|;
block|}
specifier|private
name|ContentHandler
name|getBodyHandler
parameter_list|(
specifier|final
name|XHTMLContentHandler
name|xhtml
parameter_list|)
block|{
return|return
operator|new
name|TextContentHandler
argument_list|(
name|xhtml
argument_list|)
block|{
specifier|private
name|int
name|discardLevel
init|=
literal|0
decl_stmt|;
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
name|discardLevel
operator|!=
literal|0
condition|)
block|{
name|discardLevel
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DISCARD_ELEMENTS
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|discardLevel
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SAFE_ELEMENTS
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
name|SAFE_ELEMENTS
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"A"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|href
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
decl_stmt|;
if|if
condition|(
name|href
operator|==
literal|null
condition|)
block|{
name|href
operator|=
literal|""
expr_stmt|;
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"a"
argument_list|,
literal|"href"
argument_list|,
name|href
argument_list|)
expr_stmt|;
block|}
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
name|discardLevel
operator|!=
literal|0
condition|)
block|{
name|discardLevel
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SAFE_ELEMENTS
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
name|SAFE_ELEMENTS
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"A"
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
literal|"a"
argument_list|)
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
block|}
return|;
block|}
block|}
end_class

end_unit

