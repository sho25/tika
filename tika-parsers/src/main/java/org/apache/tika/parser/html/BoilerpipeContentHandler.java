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
name|Writer
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

begin_import
import|import
name|de
operator|.
name|l3s
operator|.
name|boilerpipe
operator|.
name|BoilerpipeExtractor
import|;
end_import

begin_import
import|import
name|de
operator|.
name|l3s
operator|.
name|boilerpipe
operator|.
name|BoilerpipeProcessingException
import|;
end_import

begin_import
import|import
name|de
operator|.
name|l3s
operator|.
name|boilerpipe
operator|.
name|document
operator|.
name|TextBlock
import|;
end_import

begin_import
import|import
name|de
operator|.
name|l3s
operator|.
name|boilerpipe
operator|.
name|document
operator|.
name|TextDocument
import|;
end_import

begin_import
import|import
name|de
operator|.
name|l3s
operator|.
name|boilerpipe
operator|.
name|extractors
operator|.
name|ArticleExtractor
import|;
end_import

begin_import
import|import
name|de
operator|.
name|l3s
operator|.
name|boilerpipe
operator|.
name|extractors
operator|.
name|DefaultExtractor
import|;
end_import

begin_import
import|import
name|de
operator|.
name|l3s
operator|.
name|boilerpipe
operator|.
name|sax
operator|.
name|BoilerpipeHTMLContentHandler
import|;
end_import

begin_comment
comment|/**  * Uses the<a href="http://code.google.com/p/boilerpipe/">boilerpipe</a>  * library to automatically extract the main content from a web page.  *   * Use this as a {@link ContentHandler} object passed to  * {@link HtmlParser#parse(java.io.InputStream, ContentHandler, Metadata, org.apache.tika.parser.ParseContext)}  */
end_comment

begin_class
specifier|public
class|class
name|BoilerpipeContentHandler
extends|extends
name|BoilerpipeHTMLContentHandler
block|{
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
specifier|private
name|ContentHandler
name|delegate
decl_stmt|;
specifier|private
name|BoilerpipeExtractor
name|extractor
decl_stmt|;
comment|/**      * Creates a new boilerpipe-based content extractor, using the      * {@link DefaultExtractor} extraction rules and "delegate" as the content handler.      *       * @param delegate      *            The {@link ContentHandler} object      */
specifier|public
name|BoilerpipeContentHandler
parameter_list|(
name|ContentHandler
name|delegate
parameter_list|)
block|{
name|this
argument_list|(
name|delegate
argument_list|,
name|DefaultExtractor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a content handler that writes XHTML body character events to      * the given writer.      *      * @param writer writer      */
specifier|public
name|BoilerpipeContentHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|WriteOutContentHandler
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new boilerpipe-based content extractor, using the given      * extraction rules. The extracted main content will be passed to the      *<delegate> content handler.      *       * @param delegate      *            The {@link ContentHandler} object      * @param extractor      *            Extraction rules to use, e.g. {@link ArticleExtractor}      */
specifier|public
name|BoilerpipeContentHandler
parameter_list|(
name|ContentHandler
name|delegate
parameter_list|,
name|BoilerpipeExtractor
name|extractor
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|extractor
operator|=
name|extractor
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|TextDocument
name|td
init|=
name|toTextDocument
argument_list|()
decl_stmt|;
try|try
block|{
name|extractor
operator|.
name|process
argument_list|(
name|td
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BoilerpipeProcessingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Attributes
name|emptyAttrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|delegate
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|startPrefixMapping
argument_list|(
literal|""
argument_list|,
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"html"
argument_list|,
literal|"html"
argument_list|,
name|emptyAttrs
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"head"
argument_list|,
literal|"head"
argument_list|,
name|emptyAttrs
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"title"
argument_list|,
literal|"title"
argument_list|,
name|emptyAttrs
argument_list|)
expr_stmt|;
if|if
condition|(
name|td
operator|.
name|getTitle
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|char
index|[]
name|titleChars
init|=
name|td
operator|.
name|getTitle
argument_list|()
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|delegate
operator|.
name|characters
argument_list|(
name|titleChars
argument_list|,
literal|0
argument_list|,
name|titleChars
operator|.
name|length
argument_list|)
expr_stmt|;
name|delegate
operator|.
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
name|delegate
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"title"
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"head"
argument_list|,
literal|"head"
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"body"
argument_list|,
literal|"body"
argument_list|,
name|emptyAttrs
argument_list|)
expr_stmt|;
for|for
control|(
name|TextBlock
name|block
range|:
name|td
operator|.
name|getTextBlocks
argument_list|()
control|)
block|{
if|if
condition|(
name|block
operator|.
name|isContent
argument_list|()
condition|)
block|{
name|delegate
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"p"
argument_list|,
literal|"p"
argument_list|,
name|emptyAttrs
argument_list|)
expr_stmt|;
name|char
index|[]
name|chars
init|=
name|block
operator|.
name|getText
argument_list|()
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|delegate
operator|.
name|characters
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"p"
argument_list|,
literal|"p"
argument_list|)
expr_stmt|;
name|delegate
operator|.
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
name|delegate
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"body"
argument_list|,
literal|"body"
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"html"
argument_list|,
literal|"html"
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|endPrefixMapping
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

