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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
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
name|config
operator|.
name|ServiceLoader
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
name|detect
operator|.
name|AutoDetectReader
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
name|AbstractParser
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
name|ccil
operator|.
name|cowan
operator|.
name|tagsoup
operator|.
name|HTMLSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ccil
operator|.
name|cowan
operator|.
name|tagsoup
operator|.
name|Schema
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

begin_comment
comment|/**  * HTML parser. Uses TagSoup to turn the input document to HTML SAX events,  * and post-processes the events to produce XHTML and metadata expected by  * Tika clients.  */
end_comment

begin_class
specifier|public
class|class
name|HtmlParser
extends|extends
name|AbstractParser
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|7895315240498733128L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|XHTML
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"xhtml+xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|WAP_XHTML
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.wap.xhtml+xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|X_ASP
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-asp"
argument_list|)
decl_stmt|;
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
name|XHTML
argument_list|,
name|WAP_XHTML
argument_list|,
name|X_ASP
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ServiceLoader
name|LOADER
init|=
operator|new
name|ServiceLoader
argument_list|(
name|HtmlParser
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * HTML schema singleton used to amortise the heavy instantiation time.      */
specifier|private
specifier|static
specifier|final
name|Schema
name|HTML_SCHEMA
init|=
operator|new
name|HTMLSchema
argument_list|()
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
comment|// Automatically detect the character encoding
name|AutoDetectReader
name|reader
init|=
operator|new
name|AutoDetectReader
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
operator|.
name|get
argument_list|(
name|ServiceLoader
operator|.
name|class
argument_list|,
name|LOADER
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Charset
name|charset
init|=
name|reader
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|String
name|previous
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
name|MediaType
name|contentType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|previous
operator|==
literal|null
operator|||
name|previous
operator|.
name|startsWith
argument_list|(
literal|"text/html"
argument_list|)
condition|)
block|{
name|contentType
operator|=
operator|new
name|MediaType
argument_list|(
name|MediaType
operator|.
name|TEXT_HTML
argument_list|,
name|charset
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|previous
operator|.
name|startsWith
argument_list|(
literal|"application/xhtml+xml"
argument_list|)
condition|)
block|{
name|contentType
operator|=
operator|new
name|MediaType
argument_list|(
name|XHTML
argument_list|,
name|charset
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|previous
operator|.
name|startsWith
argument_list|(
literal|"application/vnd.wap.xhtml+xml"
argument_list|)
condition|)
block|{
name|contentType
operator|=
operator|new
name|MediaType
argument_list|(
name|WAP_XHTML
argument_list|,
name|charset
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|previous
operator|.
name|startsWith
argument_list|(
literal|"application/x-asp"
argument_list|)
condition|)
block|{
name|contentType
operator|=
operator|new
name|MediaType
argument_list|(
name|X_ASP
argument_list|,
name|charset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contentType
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
name|CONTENT_TYPE
argument_list|,
name|contentType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// deprecated, see TIKA-431
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|charset
operator|.
name|name
argument_list|()
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
comment|// Use schema from context or default
name|Schema
name|schema
init|=
name|context
operator|.
name|get
argument_list|(
name|Schema
operator|.
name|class
argument_list|,
name|HTML_SCHEMA
argument_list|)
decl_stmt|;
comment|// TIKA-528: Reuse share schema to avoid heavy instantiation
name|parser
operator|.
name|setProperty
argument_list|(
name|org
operator|.
name|ccil
operator|.
name|cowan
operator|.
name|tagsoup
operator|.
name|Parser
operator|.
name|schemaProperty
argument_list|,
name|schema
argument_list|)
expr_stmt|;
comment|// TIKA-599: Shared schema is thread-safe only if bogons are ignored
name|parser
operator|.
name|setFeature
argument_list|(
name|org
operator|.
name|ccil
operator|.
name|cowan
operator|.
name|tagsoup
operator|.
name|Parser
operator|.
name|ignoreBogonsFeature
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
name|reader
operator|.
name|asInputSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Maps "safe" HTML element names to semantic XHTML equivalents. If the      * given element is unknown or deemed unsafe for inclusion in the parse      * output, then this method returns<code>null</code> and the element      * will be ignored but the content inside it is still processed. See      * the {@link #isDiscardElement(String)} method for a way to discard      * the entire contents of an element.      *<p/>      * Subclasses can override this method to customize the default mapping.      *      * @param name HTML element name (upper case)      * @return XHTML element name (lower case), or      *<code>null</code> if the element is unsafe      * @since Apache Tika 0.5      * @deprecated Use the {@link HtmlMapper} mechanism to customize      * the HTML mapping. This method will be removed in Tika 1.0.      */
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
comment|/**      * Checks whether all content within the given HTML element should be      * discarded instead of including it in the parse output. Subclasses      * can override this method to customize the set of discarded elements.      *      * @param name HTML element name (upper case)      * @return<code>true</code> if content inside the named element      * should be ignored,<code>false</code> otherwise      * @since Apache Tika 0.5      * @deprecated Use the {@link HtmlMapper} mechanism to customize      * the HTML mapping. This method will be removed in Tika 1.0.      */
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
comment|/**      * @deprecated Use the {@link HtmlMapper} mechanism to customize      * the HTML mapping. This method will be removed in Tika 1.0.      */
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
comment|/**      * Adapter class that maintains backwards compatibility with the      * protected HtmlParser methods. Making HtmlParser implement HtmlMapper      * directly would require those methods to be public, which would break      * backwards compatibility with subclasses.      *      * @deprecated Use the {@link HtmlMapper} mechanism to customize      * the HTML mapping. This class will be removed in Tika 1.0.      */
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

