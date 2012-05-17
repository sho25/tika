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
name|metadata
operator|.
name|Property
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
name|TikaCoreProperties
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
name|Map
import|;
end_import

begin_class
class|class
name|NumbersContentHandler
extends|extends
name|DefaultHandler
block|{
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
name|inSheet
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inText
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|parseText
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inMetadata
init|=
literal|false
decl_stmt|;
specifier|private
name|Property
name|metadataKey
decl_stmt|;
specifier|private
name|String
name|metadataPropertyQName
decl_stmt|;
specifier|private
name|boolean
name|inTable
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|numberOfSheets
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|numberOfColumns
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|currentColumn
init|=
literal|0
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|menuItems
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
specifier|private
name|String
name|currentMenuItemId
decl_stmt|;
name|NumbersContentHandler
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
name|PAGE_COUNT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numberOfSheets
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
literal|"ls:workspace"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inSheet
operator|=
literal|true
expr_stmt|;
name|numberOfSheets
operator|++
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|String
name|sheetName
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"ls:workspace-name"
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"sheetNames"
argument_list|,
name|sheetName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"sf:text"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inText
operator|=
literal|true
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"sf:p"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|parseText
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
literal|"sf:metadata"
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
return|return;
block|}
if|if
condition|(
name|inMetadata
operator|&&
name|metadataKey
operator|==
literal|null
condition|)
block|{
name|metadataKey
operator|=
name|resolveMetadataKey
argument_list|(
name|localName
argument_list|)
expr_stmt|;
name|metadataPropertyQName
operator|=
name|qName
expr_stmt|;
block|}
if|if
condition|(
name|inMetadata
operator|&&
name|metadataKey
operator|!=
literal|null
operator|&&
literal|"sf:string"
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
name|metadataKey
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
if|if
condition|(
operator|!
name|inSheet
condition|)
block|{
return|return;
block|}
if|if
condition|(
literal|"sf:tabular-model"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inTable
operator|=
literal|true
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|currentColumn
operator|=
literal|0
expr_stmt|;
comment|//            String tableName = attributes.getValue("sf:name");
block|}
if|if
condition|(
literal|"sf:menu-choices"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|menuItems
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|inTable
operator|&&
literal|"sf:grid"
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
literal|"sf:numcols"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|menuItems
operator|!=
literal|null
operator|&&
literal|"sf:t"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|currentMenuItemId
operator|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:ID"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentMenuItemId
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
name|menuItems
operator|.
name|put
argument_list|(
name|currentMenuItemId
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|inTable
operator|&&
literal|"sf:ct"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
if|if
condition|(
name|currentColumn
operator|>=
name|numberOfColumns
condition|)
block|{
name|currentColumn
operator|=
literal|0
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
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
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:s"
argument_list|)
argument_list|)
expr_stmt|;
name|currentColumn
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|inTable
operator|&&
operator|(
literal|"sf:n"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|||
literal|"sf:rn"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
name|currentColumn
operator|>=
name|numberOfColumns
condition|)
block|{
name|currentColumn
operator|=
literal|0
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
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
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sf:v"
argument_list|)
argument_list|)
expr_stmt|;
name|currentColumn
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|inTable
operator|&&
literal|"sf:proxied-cell-ref"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
if|if
condition|(
name|currentColumn
operator|>=
name|numberOfColumns
condition|)
block|{
name|currentColumn
operator|=
literal|0
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
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
name|menuItems
operator|.
name|get
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|"sfa:IDREF"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|currentColumn
operator|++
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
name|parseText
operator|&&
name|length
operator|>
literal|0
condition|)
block|{
name|xhtml
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
literal|"ls:workspace"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inSheet
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
if|if
condition|(
literal|"sf:text"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inText
operator|=
literal|false
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"sf:p"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|parseText
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
literal|"sf:metadata"
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
if|if
condition|(
name|inMetadata
operator|&&
name|qName
operator|.
name|equals
argument_list|(
name|metadataPropertyQName
argument_list|)
condition|)
block|{
name|metadataPropertyQName
operator|=
literal|null
expr_stmt|;
name|metadataKey
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|inSheet
condition|)
block|{
return|return;
block|}
if|if
condition|(
literal|"sf:menu-choices"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{         }
if|if
condition|(
literal|"sf:tabular-model"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|inTable
operator|=
literal|false
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentMenuItemId
operator|!=
literal|null
operator|&&
literal|"sf:t"
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|currentMenuItemId
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|Property
name|resolveMetadataKey
parameter_list|(
name|String
name|localName
parameter_list|)
block|{
if|if
condition|(
literal|"authors"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
return|;
block|}
if|if
condition|(
literal|"title"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return
name|TikaCoreProperties
operator|.
name|TITLE
return|;
block|}
return|return
name|Property
operator|.
name|internalText
argument_list|(
name|localName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

