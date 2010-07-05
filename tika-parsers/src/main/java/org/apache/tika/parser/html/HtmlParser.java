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
name|io
operator|.
name|BufferedInputStream
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
name|IllegalCharsetNameException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|mime
operator|.
name|MediaType
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
name|ParseContext
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
name|parser
operator|.
name|txt
operator|.
name|CharsetDetector
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
name|txt
operator|.
name|CharsetMatch
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
comment|/**  * HTML parser. Uses TagSoup to turn the input document to HTML SAX events,  * and post-processes the events to produce XHTML and metadata expected by  * Tika clients.  */
end_comment

begin_class
specifier|public
class|class
name|HtmlParser
implements|implements
name|Parser
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|MediaType
operator|.
name|text
argument_list|(
literal|"html"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"xhtml+xml"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.wap.xhtml+xml"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-asp"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// Use the widest, most common charset as our default.
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CHARSET
init|=
literal|"windows-1252"
decl_stmt|;
comment|// TIKA-357 - use bigger buffer for meta tag sniffing (was 4K)
specifier|private
specifier|static
specifier|final
name|int
name|META_TAG_BUFFER_SIZE
init|=
literal|8192
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|HTTP_EQUIV_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?is)<meta\\s+http-equiv\\s*=\\s*['\\\"]\\s*"
operator|+
literal|"Content-Type['\\\"]\\s+content\\s*=\\s*['\\\"]"
operator|+
literal|"([^'\\\"]+)['\\\"]"
argument_list|)
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
comment|/**      * TIKA-332: Check for meta http-equiv tag with charset info in      * HTML content.      *<p>      * TODO: Move this into core, along with CharsetDetector      */
specifier|private
name|String
name|getEncoding
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|mark
argument_list|(
name|META_TAG_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|META_TAG_BUFFER_SIZE
index|]
decl_stmt|;
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"us-ascii"
argument_list|)
decl_stmt|;
name|int
name|bufferSize
init|=
name|isr
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|bufferSize
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|metaString
init|=
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|HTTP_EQUIV_PATTERN
operator|.
name|matcher
argument_list|(
name|metaString
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
comment|// TIKA-349: flexible handling of attributes
comment|// We have one or more x or x=y attributes, separated by ';'
name|String
index|[]
name|attrs
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|attr
range|:
name|attrs
control|)
block|{
name|String
index|[]
name|keyValue
init|=
name|attr
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|keyValue
operator|.
name|length
operator|==
literal|2
operator|)
operator|&&
name|keyValue
index|[
literal|0
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"charset"
argument_list|)
condition|)
block|{
name|String
name|charset
init|=
name|keyValue
index|[
literal|1
index|]
decl_stmt|;
try|try
block|{
if|if
condition|(
name|Charset
operator|.
name|isSupported
argument_list|(
name|charset
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|charset
argument_list|)
expr_stmt|;
return|return
name|charset
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalCharsetNameException
name|e
parameter_list|)
block|{
comment|// Ignore malformed charset names
block|}
block|}
block|}
block|}
block|}
comment|// No charset in a meta http-equiv tag, see if it's in the passed content-encoding
comment|// hint, or the passed content-type hint.
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|String
name|incomingCharset
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
name|String
name|incomingType
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|incomingCharset
operator|==
literal|null
operator|&&
name|incomingType
operator|!=
literal|null
condition|)
block|{
comment|// TIKA-341: Use charset in content-type
name|MediaType
name|mt
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|incomingType
argument_list|)
decl_stmt|;
if|if
condition|(
name|mt
operator|!=
literal|null
condition|)
block|{
name|String
name|charset
init|=
name|mt
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
operator|(
name|charset
operator|!=
literal|null
operator|)
operator|&&
name|Charset
operator|.
name|isSupported
argument_list|(
name|charset
argument_list|)
condition|)
block|{
name|incomingCharset
operator|=
name|charset
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|incomingCharset
operator|!=
literal|null
condition|)
block|{
name|detector
operator|.
name|setDeclaredEncoding
argument_list|(
name|incomingCharset
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-341 without enabling input filtering (stripping of tags) the
comment|// short HTML tests don't work well.
name|detector
operator|.
name|enableInputFilter
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|detector
operator|.
name|setText
argument_list|(
name|stream
argument_list|)
expr_stmt|;
for|for
control|(
name|CharsetMatch
name|match
range|:
name|detector
operator|.
name|detectAll
argument_list|()
control|)
block|{
if|if
condition|(
name|Charset
operator|.
name|isSupported
argument_list|(
name|match
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|match
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// TIKA-339: Don't set language, as it's typically not a very good
comment|// guess, and it can create ambiguity if another (better) language
comment|// value is specified by a meta tag in the HTML (or via HTTP response
comment|// header).
comment|/*                 String language = match.getLanguage();                 if (language != null) {                     metadata.set(Metadata.CONTENT_LANGUAGE, match.getLanguage());                     metadata.set(Metadata.LANGUAGE, match.getLanguage());                 }                 */
break|break;
block|}
block|}
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
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|Charset
operator|.
name|isSupported
argument_list|(
name|DEFAULT_CHARSET
argument_list|)
condition|)
block|{
name|encoding
operator|=
name|DEFAULT_CHARSET
expr_stmt|;
block|}
else|else
block|{
name|encoding
operator|=
name|Charset
operator|.
name|defaultCharset
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
return|return
name|encoding
return|;
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
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
comment|// The getEncoding() method depends on the mark feature
if|if
condition|(
operator|!
name|stream
operator|.
name|markSupported
argument_list|()
condition|)
block|{
name|stream
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
comment|// Protect the stream from being closed by CyberNeko
comment|// TODO: Is this still needed, given our use of TagSoup?
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
name|source
operator|.
name|setEncoding
argument_list|(
name|getEncoding
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get the HTML mapper from the parse context
name|HtmlMapper
name|mapper
init|=
name|context
operator|.
name|get
argument_list|(
name|HtmlMapper
operator|.
name|class
argument_list|,
operator|new
name|HtmlParserMapper
argument_list|()
argument_list|)
decl_stmt|;
comment|// Parse the HTML document
name|org
operator|.
name|ccil
operator|.
name|cowan
operator|.
name|tagsoup
operator|.
name|Parser
name|parser
init|=
operator|new
name|org
operator|.
name|ccil
operator|.
name|cowan
operator|.
name|tagsoup
operator|.
name|Parser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|setContentHandler
argument_list|(
operator|new
name|XHTMLDowngradeHandler
argument_list|(
operator|new
name|HtmlHandler
argument_list|(
name|mapper
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
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
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Maps "safe" HTML element names to semantic XHTML equivalents. If the      * given element is unknown or deemed unsafe for inclusion in the parse      * output, then this method returns<code>null</code> and the element      * will be ignored but the content inside it is still processed. See      * the {@link #isDiscardElement(String)} method for a way to discard      * the entire contents of an element.      *<p>      * Subclasses can override this method to customize the default mapping.      *      * @deprecated Use the {@link HtmlMapper} mechanism to customize      *             the HTML mapping. This method will be removed in Tika 1.0.      * @since Apache Tika 0.5      * @param name HTML element name (upper case)      * @return XHTML element name (lower case), or      *<code>null</code> if the element is unsafe       */
specifier|protected
name|String
name|mapSafeElement
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|DefaultHtmlMapper
operator|.
name|INSTANCE
operator|.
name|mapSafeElement
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Checks whether all content within the given HTML element should be      * discarded instead of including it in the parse output. Subclasses      * can override this method to customize the set of discarded elements.      *      * @deprecated Use the {@link HtmlMapper} mechanism to customize      *             the HTML mapping. This method will be removed in Tika 1.0.      * @since Apache Tika 0.5      * @param name HTML element name (upper case)      * @return<code>true</code> if content inside the named element      *         should be ignored,<code>false</code> otherwise      */
specifier|protected
name|boolean
name|isDiscardElement
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|DefaultHtmlMapper
operator|.
name|INSTANCE
operator|.
name|isDiscardElement
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**     * @deprecated Use the {@link HtmlMapper} mechanism to customize     *             the HTML mapping. This method will be removed in Tika 1.0.     **/
specifier|public
name|String
name|mapSafeAttribute
parameter_list|(
name|String
name|elementName
parameter_list|,
name|String
name|attributeName
parameter_list|)
block|{
return|return
name|DefaultHtmlMapper
operator|.
name|INSTANCE
operator|.
name|mapSafeAttribute
argument_list|(
name|elementName
argument_list|,
name|attributeName
argument_list|)
return|;
block|}
comment|/**      * Adapter class that maintains backwards compatibility with the      * protected HtmlParser methods. Making HtmlParser implement HtmlMapper      * directly would require those methods to be public, which would break      * backwards compatibility with subclasses.      *      * @deprecated Use the {@link HtmlMapper} mechanism to customize      *             the HTML mapping. This class will be removed in Tika 1.0.      */
specifier|private
class|class
name|HtmlParserMapper
implements|implements
name|HtmlMapper
block|{
specifier|public
name|String
name|mapSafeElement
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|HtmlParser
operator|.
name|this
operator|.
name|mapSafeElement
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isDiscardElement
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|HtmlParser
operator|.
name|this
operator|.
name|isDiscardElement
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
name|mapSafeAttribute
parameter_list|(
name|String
name|elementName
parameter_list|,
name|String
name|attributeName
parameter_list|)
block|{
return|return
name|HtmlParser
operator|.
name|this
operator|.
name|mapSafeAttribute
argument_list|(
name|elementName
argument_list|,
name|attributeName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

