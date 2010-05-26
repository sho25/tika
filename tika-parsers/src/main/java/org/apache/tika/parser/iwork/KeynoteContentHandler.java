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
name|iwork
package|;
end_package

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
name|DefaultHandler
import|;
end_import

begin_class
class|class
name|KeynoteContentHandler
extends|extends
name|DefaultHandler
block|{
specifier|public
specifier|final
specifier|static
name|String
name|PRESENTATION_WIDTH
init|=
literal|"slides-width"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PRESENTATION_HEIGHT
init|=
literal|"slides-height"
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
name|boolean
name|inSlide
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inTheme
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inTitle
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inBody
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|tableId
decl_stmt|;
specifier|private
name|Integer
name|numberOfColumns
init|=
literal|null
decl_stmt|;
specifier|private
name|Integer
name|currentColumn
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|inMetadata
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inMetaDataTitle
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inMetaDataAuthors
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inParsableText
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|numberOfSlides
init|=
literal|0
decl_stmt|;
name|KeynoteContentHandler
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
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
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|SLIDE_COUNT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numberOfSlides
argument_list|)
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
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"key:theme"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inTheme
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:slide"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inSlide
operator|=
literal|true
expr_stmt|;
name|numberOfSlides
operator|++
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:title-placeholder"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
name|inSlide
condition|)
block|{
name|inTitle
operator|=
literal|true
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"h1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sf:sticky-note"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
name|inSlide
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:notes"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
name|inSlide
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:body-placeholder"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
name|inSlide
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|inBody
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:size"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
operator|!
name|inTheme
condition|)
block|{
name|String
name|width
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:w"
argument_list|)
decl_stmt|;
name|String
name|height
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:h"
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PRESENTATION_WIDTH
argument_list|,
name|width
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PRESENTATION_HEIGHT
argument_list|,
name|height
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sf:text-body"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inParsableText
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:metadata"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inMetadata
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inMetadata
operator|&&
literal|"key:title"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inMetaDataTitle
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inMetadata
operator|&&
literal|"key:authors"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inMetaDataAuthors
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inMetaDataTitle
operator|&&
literal|"key:string"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
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
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inMetaDataAuthors
operator|&&
literal|"key:string"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inSlide
operator|&&
literal|"sf:tabular-model"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|tableId
operator|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:ID"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tableId
operator|!=
literal|null
operator|&&
literal|"sf:columns"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|numberOfColumns
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sf:count"
argument_list|)
argument_list|)
expr_stmt|;
name|currentColumn
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tableId
operator|!=
literal|null
operator|&&
literal|"sf:ct"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|parseTableData
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tableId
operator|!=
literal|null
operator|&&
literal|"sf:n"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|parseTableData
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sf:v"
argument_list|)
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
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"key:theme"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inTheme
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:slide"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inSlide
operator|=
literal|false
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:title-placeholder"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
name|inSlide
condition|)
block|{
name|inTitle
operator|=
literal|false
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"h1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sf:sticky-note"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
name|inSlide
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:notes"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
name|inSlide
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:body-placeholder"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|&&
name|inSlide
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|inBody
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sf:text-body"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inParsableText
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"key:metadata"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inMetadata
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inMetadata
operator|&&
literal|"key:title"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inMetaDataTitle
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inMetadata
operator|&&
literal|"key:authors"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inMetaDataAuthors
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inSlide
operator|&&
literal|"sf:tabular-model"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|tableId
operator|=
literal|null
expr_stmt|;
name|numberOfColumns
operator|=
literal|null
expr_stmt|;
name|currentColumn
operator|=
literal|null
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
operator|!
name|inParsableText
operator|||
operator|!
name|inSlide
condition|)
block|{
return|return;
block|}
name|String
name|text
init|=
operator|new
name|String
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|parseTableData
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|currentColumn
operator|==
literal|0
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|element
argument_list|(
literal|"td"
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentColumn
operator|.
name|equals
argument_list|(
name|numberOfColumns
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

