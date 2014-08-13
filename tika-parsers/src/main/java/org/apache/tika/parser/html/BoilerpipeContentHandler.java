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
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * Uses the<a href="http://code.google.com/p/boilerpipe/">boilerpipe</a>  * library to automatically extract the main content from a web page.  *  * Use this as a {@link ContentHandler} object passed to  * {@link HtmlParser#parse(java.io.InputStream, ContentHandler, Metadata, org.apache.tika.parser.ParseContext)}  */
end_comment

begin_class
specifier|public
class|class
name|BoilerpipeContentHandler
extends|extends
name|BoilerpipeHTMLContentHandler
block|{
specifier|private
specifier|static
class|class
name|RecordedElement
block|{
specifier|public
enum|enum
name|ElementType
block|{
name|START
block|,
name|END
block|,
name|CONTINUE
block|}
specifier|private
name|String
name|uri
decl_stmt|;
specifier|private
name|String
name|localName
decl_stmt|;
specifier|private
name|String
name|qName
decl_stmt|;
specifier|private
name|Attributes
name|attrs
decl_stmt|;
specifier|private
name|List
argument_list|<
name|char
index|[]
argument_list|>
name|characters
decl_stmt|;
specifier|private
name|ElementType
name|elementType
decl_stmt|;
specifier|public
name|RecordedElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attrs
parameter_list|)
block|{
name|this
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|attrs
argument_list|,
name|ElementType
operator|.
name|START
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RecordedElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
block|{
name|this
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
literal|null
argument_list|,
name|ElementType
operator|.
name|END
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RecordedElement
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|ElementType
operator|.
name|CONTINUE
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|RecordedElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attrs
parameter_list|,
name|RecordedElement
operator|.
name|ElementType
name|elementType
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|localName
operator|=
name|localName
expr_stmt|;
name|this
operator|.
name|qName
operator|=
name|qName
expr_stmt|;
name|this
operator|.
name|attrs
operator|=
name|attrs
expr_stmt|;
name|this
operator|.
name|elementType
operator|=
name|elementType
expr_stmt|;
name|this
operator|.
name|characters
operator|=
operator|new
name|ArrayList
argument_list|<
name|char
index|[]
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"<%s> of type %s"
argument_list|,
name|localName
argument_list|,
name|elementType
argument_list|)
return|;
block|}
specifier|public
name|String
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
specifier|public
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
name|getQName
parameter_list|()
block|{
return|return
name|qName
return|;
block|}
specifier|public
name|Attributes
name|getAttrs
parameter_list|()
block|{
return|return
name|attrs
return|;
block|}
specifier|public
name|List
argument_list|<
name|char
index|[]
argument_list|>
name|getCharacters
parameter_list|()
block|{
return|return
name|characters
return|;
block|}
specifier|public
name|RecordedElement
operator|.
name|ElementType
name|getElementType
parameter_list|()
block|{
return|return
name|elementType
return|;
block|}
block|}
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
specifier|private
name|boolean
name|includeMarkup
decl_stmt|;
specifier|private
name|boolean
name|inHeader
decl_stmt|;
specifier|private
name|boolean
name|inFooter
decl_stmt|;
specifier|private
name|int
name|headerCharOffset
decl_stmt|;
specifier|private
name|List
argument_list|<
name|RecordedElement
argument_list|>
name|elements
decl_stmt|;
specifier|private
name|TextDocument
name|td
decl_stmt|;
comment|/**      * Creates a new boilerpipe-based content extractor, using the      * {@link DefaultExtractor} extraction rules and "delegate" as the content handler.      *      * @param delegate      *            The {@link ContentHandler} object      */
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
comment|/**      * Creates a new boilerpipe-based content extractor, using the given      * extraction rules. The extracted main content will be passed to the      *<delegate> content handler.      *      * @param delegate      *            The {@link ContentHandler} object      * @param extractor      *            Extraction rules to use, e.g. {@link ArticleExtractor}      */
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
name|td
operator|=
literal|null
expr_stmt|;
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
specifier|public
name|void
name|setIncludeMarkup
parameter_list|(
name|boolean
name|includeMarkup
parameter_list|)
block|{
name|this
operator|.
name|includeMarkup
operator|=
name|includeMarkup
expr_stmt|;
block|}
specifier|public
name|boolean
name|isIncludeMarkup
parameter_list|()
block|{
return|return
name|includeMarkup
return|;
block|}
comment|/**      * Retrieves the built TextDocument      *      * @return TextDocument      */
specifier|public
name|TextDocument
name|getTextDocument
parameter_list|()
block|{
return|return
name|td
return|;
block|}
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
name|delegate
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|inHeader
operator|=
literal|true
expr_stmt|;
name|inFooter
operator|=
literal|false
expr_stmt|;
name|headerCharOffset
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|includeMarkup
condition|)
block|{
name|elements
operator|=
operator|new
name|ArrayList
argument_list|<
name|RecordedElement
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
empty_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
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
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
if|if
condition|(
name|inHeader
condition|)
block|{
name|delegate
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inFooter
condition|)
block|{
comment|// Do nothing
block|}
elseif|else
if|if
condition|(
name|includeMarkup
condition|)
block|{
name|elements
operator|.
name|add
argument_list|(
operator|new
name|RecordedElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// This happens for the<body> element, if we're not doing markup.
name|delegate
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|characters
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|inHeader
condition|)
block|{
name|delegate
operator|.
name|characters
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|headerCharOffset
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inFooter
condition|)
block|{
comment|// Do nothing
block|}
elseif|else
if|if
condition|(
name|includeMarkup
condition|)
block|{
name|RecordedElement
name|element
init|=
name|elements
operator|.
name|get
argument_list|(
name|elements
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|char
index|[]
name|characters
init|=
operator|new
name|char
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|characters
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|element
operator|.
name|getCharacters
argument_list|()
operator|.
name|add
argument_list|(
name|characters
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
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
name|localName
parameter_list|,
name|String
name|qName
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
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
if|if
condition|(
name|inHeader
condition|)
block|{
name|delegate
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
name|inHeader
operator|=
operator|!
name|localName
operator|.
name|equals
argument_list|(
literal|"head"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inFooter
condition|)
block|{
comment|// Do nothing
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"body"
argument_list|)
condition|)
block|{
name|inFooter
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeMarkup
condition|)
block|{
comment|// Add the end element, and the continuation from the previous element
name|elements
operator|.
name|add
argument_list|(
operator|new
name|RecordedElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
argument_list|)
expr_stmt|;
name|elements
operator|.
name|add
argument_list|(
operator|new
name|RecordedElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
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
name|td
operator|=
name|toTextDocument
argument_list|()
expr_stmt|;
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
comment|// At this point we have all the information we need to either emit N paragraphs
comment|// of plain text (if not including markup), or we have to replay our recorded elements
comment|// and only emit character runs that passed the boilerpipe filters.
if|if
condition|(
name|includeMarkup
condition|)
block|{
name|BitSet
name|validCharacterRuns
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
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
name|BitSet
name|bs
init|=
name|block
operator|.
name|getContainedTextElements
argument_list|()
decl_stmt|;
if|if
condition|(
name|bs
operator|!=
literal|null
condition|)
block|{
name|validCharacterRuns
operator|.
name|or
argument_list|(
name|bs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Now have bits set for all valid character runs. Replay our recorded elements,
comment|// but only emit character runs flagged as valid.
name|int
name|curCharsIndex
init|=
name|headerCharOffset
decl_stmt|;
for|for
control|(
name|RecordedElement
name|element
range|:
name|elements
control|)
block|{
switch|switch
condition|(
name|element
operator|.
name|getElementType
argument_list|()
condition|)
block|{
case|case
name|START
case|:
name|delegate
operator|.
name|startElement
argument_list|(
name|element
operator|.
name|getUri
argument_list|()
argument_list|,
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|element
operator|.
name|getQName
argument_list|()
argument_list|,
name|element
operator|.
name|getAttrs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Fall through
case|case
name|CONTINUE
case|:
comment|// Now emit characters that are valid. Note that boilerpipe pre-increments the character index, so
comment|// we have to follow suit.
for|for
control|(
name|char
index|[]
name|chars
range|:
name|element
operator|.
name|getCharacters
argument_list|()
control|)
block|{
name|curCharsIndex
operator|++
expr_stmt|;
if|if
condition|(
name|validCharacterRuns
operator|.
name|get
argument_list|(
name|curCharsIndex
argument_list|)
condition|)
block|{
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
comment|// https://issues.apache.org/jira/browse/TIKA-961
if|if
condition|(
operator|!
name|Character
operator|.
name|isWhitespace
argument_list|(
name|chars
index|[
name|chars
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
condition|)
block|{
comment|// Only add whitespace for certain elements
if|if
condition|(
name|XHTMLContentHandler
operator|.
name|ENDLINE
operator|.
name|contains
argument_list|(
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
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
block|}
block|}
break|break;
case|case
name|END
case|:
name|delegate
operator|.
name|endElement
argument_list|(
name|element
operator|.
name|getUri
argument_list|()
argument_list|,
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|element
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unhandled element type: "
operator|+
name|element
operator|.
name|getElementType
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
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
comment|// We defer ending any prefix mapping until here, which is why we don't pass this
comment|// through to the delegate in an overridden method.
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

