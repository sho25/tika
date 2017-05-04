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
name|pdf
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
comment|/**  * This class offers an initial capability to  * scrape text containing elements out of XFA, and  * it tries to link fields with values.  *<p>  * Some areas for improvement:  *<ol>  *<li>convert this to 2 lines of XPath</li>  *<li>handle metadata stored in&lt;desc&gt; section (govdocs1: 754282.pdf, 982106.pdf)</li>  *<li>handle pdf metadata (access permissions, etc.) in&lt;pdf&gt; element</li>  *<li>extract different types of uris as metadata</li>  *<li>add extraction of&lt;image&gt; data (govdocs1: 754282.pdf)</li>  *<li>add computation of traversal order for fields</li>  *<li>figure out when text extracted from xfa fields is duplicative of that  *     extracted from the rest of the pdf...and do this efficiently and quickly</li>  *<li>avoid duplication with&lt;speak&gt; and&lt;tooltip&gt; elements</li>  *</ol>  */
end_comment

begin_class
class|class
name|XFAExtractor
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|XFA_TEMPLATE_ANY_VERSION
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^http://www.xfa.org/schema/xfa-template"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|TEXT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(speak|text|contents-richtext|toolTip|exData)$"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XFA_DATA_NS
init|=
literal|"http://www.xfa.org/schema/xfa-data/1.0/"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_LN
init|=
literal|"field"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|XFA_DATA
init|=
operator|new
name|QName
argument_list|(
name|XFA_DATA_NS
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Matcher
name|xfaTemplateMatcher
decl_stmt|;
comment|//namespace any version
specifier|private
specifier|final
name|Matcher
name|textMatcher
decl_stmt|;
name|XFAExtractor
parameter_list|()
block|{
name|xfaTemplateMatcher
operator|=
name|XFA_TEMPLATE_ANY_VERSION
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|textMatcher
operator|=
name|TEXT_PATTERN
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
name|void
name|extract
parameter_list|(
name|InputStream
name|xfaIs
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|m
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|SAXException
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"xfa_content"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pdfObjRToValues
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//for now, store and dump the fields in insertion order
name|Map
argument_list|<
name|String
argument_list|,
name|XFAField
argument_list|>
name|namedFields
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//The strategy is to cache the fields in fields
comment|//and cache the values in pdfObjRToValues while
comment|//handling the text etc along the way.
comment|//
comment|//As a final step, dump the merged fields and the values.
name|XMLStreamReader
name|reader
init|=
name|context
operator|.
name|getXMLInputFactory
argument_list|()
operator|.
name|createXMLStreamReader
argument_list|(
name|xfaIs
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|reader
operator|.
name|next
argument_list|()
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|QName
name|name
init|=
name|reader
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|localName
init|=
name|name
operator|.
name|getLocalPart
argument_list|()
decl_stmt|;
if|if
condition|(
name|xfaTemplateMatcher
operator|.
name|reset
argument_list|(
name|name
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
operator|&&
name|FIELD_LN
operator|.
name|equals
argument_list|(
name|name
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|handleField
argument_list|(
name|reader
argument_list|,
name|namedFields
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XFA_DATA
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|//full qname match is important!
name|loadData
argument_list|(
name|reader
argument_list|,
name|pdfObjRToValues
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|textMatcher
operator|.
name|reset
argument_list|(
name|localName
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|scrapeTextUntil
argument_list|(
name|reader
argument_list|,
name|xhtml
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
break|break;
block|}
block|}
if|if
condition|(
name|namedFields
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"xfa_content"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//now dump fields and values
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"xfa_form"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"ol"
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|XFAField
argument_list|>
name|e
range|:
name|namedFields
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|fieldName
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|XFAField
name|field
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|fieldValue
init|=
name|pdfObjRToValues
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"fieldName"
argument_list|,
literal|"fieldName"
argument_list|,
literal|"CDATA"
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|String
name|displayFieldName
init|=
operator|(
name|field
operator|.
name|toolTip
operator|==
literal|null
operator|||
name|field
operator|.
name|toolTip
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|fieldName
else|:
name|field
operator|.
name|toolTip
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|displayFieldName
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldValue
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|fieldValue
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"li"
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"li"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ol"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"xfa_content"
argument_list|)
expr_stmt|;
block|}
comment|//try to scrape the text until the endElement
specifier|private
name|void
name|scrapeTextUntil
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|QName
name|endElement
parameter_list|)
throws|throws
name|XMLStreamException
throws|,
name|SAXException
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|keepGoing
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
operator|&&
name|keepGoing
condition|)
block|{
switch|switch
condition|(
name|reader
operator|.
name|next
argument_list|()
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
break|break;
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|int
name|start
init|=
name|reader
operator|.
name|getTextStart
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|reader
operator|.
name|getTextLength
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|reader
operator|.
name|getTextCharacters
argument_list|()
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
name|start
operator|=
name|reader
operator|.
name|getTextStart
argument_list|()
expr_stmt|;
name|length
operator|=
name|reader
operator|.
name|getTextLength
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|reader
operator|.
name|getTextCharacters
argument_list|()
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
operator|(
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|)
case|:
if|if
condition|(
name|reader
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|endElement
argument_list|)
condition|)
block|{
name|keepGoing
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"p"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
name|String
name|remainder
init|=
name|buffer
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|remainder
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|remainder
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|scrapeTextUntil
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|,
name|QName
name|endElement
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|keepGoing
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
operator|&&
name|keepGoing
condition|)
block|{
switch|switch
condition|(
name|reader
operator|.
name|next
argument_list|()
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
break|break;
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|int
name|start
init|=
name|reader
operator|.
name|getTextStart
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|reader
operator|.
name|getTextLength
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|reader
operator|.
name|getTextCharacters
argument_list|()
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
name|start
operator|=
name|reader
operator|.
name|getTextStart
argument_list|()
expr_stmt|;
name|length
operator|=
name|reader
operator|.
name|getTextLength
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|reader
operator|.
name|getTextCharacters
argument_list|()
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
operator|(
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|)
case|:
if|if
condition|(
name|reader
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|endElement
argument_list|)
condition|)
block|{
name|keepGoing
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"p"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|loadData
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pdfObjRToValues
parameter_list|)
throws|throws
name|XMLStreamException
block|{
comment|//reader is at the "xfa:data" element
comment|//scrape the contents from the text containing nodes
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|reader
operator|.
name|next
argument_list|()
condition|)
block|{
case|case
operator|(
name|XMLStreamConstants
operator|.
name|START_ELEMENT
operator|)
case|:
break|break;
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|int
name|start
init|=
name|reader
operator|.
name|getTextStart
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|reader
operator|.
name|getTextLength
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|reader
operator|.
name|getTextCharacters
argument_list|()
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
name|start
operator|=
name|reader
operator|.
name|getTextStart
argument_list|()
expr_stmt|;
name|length
operator|=
name|reader
operator|.
name|getTextLength
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|reader
operator|.
name|getTextCharacters
argument_list|()
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
operator|(
name|XMLStreamConstants
operator|.
name|END_ELEMENT
operator|)
case|:
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|localName
init|=
name|reader
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
name|pdfObjRToValues
operator|.
name|put
argument_list|(
name|localName
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|XFA_DATA
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
break|break;
block|}
block|}
block|}
specifier|private
name|void
name|handleField
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|XFAField
argument_list|>
name|fields
parameter_list|)
throws|throws
name|XMLStreamException
block|{
comment|//reader is set to the field element
name|String
name|fieldName
init|=
name|findFirstAttributeValue
argument_list|(
name|reader
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|pdfObjRef
init|=
literal|""
decl_stmt|;
name|String
name|toolTip
init|=
literal|""
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|reader
operator|.
name|next
argument_list|()
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
if|if
condition|(
literal|"toolTip"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|toolTip
operator|=
name|scrapeTextUntil
argument_list|(
name|reader
argument_list|,
name|reader
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// add checkbutton, etcif (reader.getName().equals())
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
name|xfaTemplateMatcher
operator|.
name|reset
argument_list|(
name|reader
operator|.
name|getName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
operator|&&
name|FIELD_LN
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
operator|new
name|XFAField
argument_list|(
name|fieldName
argument_list|,
name|toolTip
argument_list|,
name|pdfObjRef
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|PROCESSING_INSTRUCTION
case|:
if|if
condition|(
literal|"PDF_OBJR"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getPITarget
argument_list|()
argument_list|)
condition|)
block|{
name|pdfObjRef
operator|=
name|reader
operator|.
name|getPIData
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
specifier|private
name|String
name|findFirstAttributeValue
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|,
name|String
name|name
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
name|reader
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|reader
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
return|return
name|reader
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
class|class
name|XFAField
block|{
name|String
name|fieldName
decl_stmt|;
name|String
name|toolTip
decl_stmt|;
name|String
name|pdfObjRef
decl_stmt|;
name|String
name|value
decl_stmt|;
specifier|public
name|XFAField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|toolTip
parameter_list|,
name|String
name|pdfObjRef
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|toolTip
operator|=
name|toolTip
expr_stmt|;
name|this
operator|.
name|pdfObjRef
operator|=
name|pdfObjRef
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
literal|"XFAField{"
operator|+
literal|"fieldName='"
operator|+
name|fieldName
operator|+
literal|'\''
operator|+
literal|", toolTip='"
operator|+
name|toolTip
operator|+
literal|'\''
operator|+
literal|", pdfObjRef='"
operator|+
name|pdfObjRef
operator|+
literal|'\''
operator|+
literal|", value='"
operator|+
name|value
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
block|}
block|}
end_class

end_unit

