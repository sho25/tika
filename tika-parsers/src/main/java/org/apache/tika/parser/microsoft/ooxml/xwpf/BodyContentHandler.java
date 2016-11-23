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
name|microsoft
operator|.
name|ooxml
operator|.
name|xwpf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFRelation
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
name|parser
operator|.
name|microsoft
operator|.
name|MSOfficeParserConfig
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

begin_comment
comment|/**  * This class is intended to handle anything that might contain IBodyElements:  * main document, headers, footers, notes, etc.  */
end_comment

begin_class
class|class
name|BodyContentHandler
extends|extends
name|PartHandler
block|{
specifier|private
enum|enum
name|EditType
block|{
name|NONE
block|,
name|INSERT
block|,
name|DELETE
block|}
empty_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|W_NS
init|=
literal|"http://schemas.openxmlformats.org/wordprocessingml/2006/main"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MC_NS
init|=
literal|"http://schemas.openxmlformats.org/markup-compatibility/2006"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|OFFICE_DOC_RELATIONSHIP_NS
init|=
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|char
index|[]
name|TAB
init|=
operator|new
name|char
index|[
literal|1
index|]
decl_stmt|;
static|static
block|{
name|TAB
index|[
literal|0
index|]
operator|=
literal|'\t'
expr_stmt|;
block|}
specifier|private
specifier|final
name|String
name|partName
decl_stmt|;
specifier|private
specifier|final
name|RelationshipsManager
name|relationshipsManager
decl_stmt|;
specifier|private
specifier|final
name|XHTMLContentHandler
name|handler
decl_stmt|;
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|private
specifier|final
name|ParseContext
name|parseContext
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|includeDeletedContent
decl_stmt|;
specifier|private
name|boolean
name|inR
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inT
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inRPr
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inDelText
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inAlternateContent
init|=
literal|false
decl_stmt|;
comment|//in alternate content section
specifier|private
name|boolean
name|inACChoice
init|=
literal|false
decl_stmt|;
comment|//if in alternate, choice or fallback?
specifier|private
name|boolean
name|inACFallback
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|hasWrittenAHref
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|hasWrittenFormatting
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|editAuthor
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|editDate
init|=
literal|null
decl_stmt|;
specifier|private
name|EditType
name|editType
init|=
name|EditType
operator|.
name|NONE
decl_stmt|;
specifier|private
name|String
name|hyperlink
init|=
literal|null
decl_stmt|;
specifier|private
name|TmpFormatting
name|currFormat
init|=
operator|new
name|TmpFormatting
argument_list|()
decl_stmt|;
specifier|public
name|BodyContentHandler
parameter_list|(
name|String
name|partName
parameter_list|,
name|RelationshipsManager
name|relationshipsManager
parameter_list|,
name|XHTMLContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|partName
operator|=
name|partName
expr_stmt|;
name|this
operator|.
name|relationshipsManager
operator|=
name|relationshipsManager
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|parseContext
operator|=
name|context
expr_stmt|;
name|MSOfficeParserConfig
name|config
init|=
name|context
operator|.
name|get
argument_list|(
name|MSOfficeParserConfig
operator|.
name|class
argument_list|)
decl_stmt|;
name|boolean
name|tmpIncludeDeleted
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|tmpIncludeDeleted
operator|=
name|config
operator|.
name|getIncludeDeletedContent
argument_list|()
expr_stmt|;
block|}
name|includeDeletedContent
operator|=
name|tmpIncludeDeleted
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{     }
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
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{     }
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
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|MC_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"AlternateContent"
argument_list|)
condition|)
block|{
name|inAlternateContent
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"Choice"
argument_list|)
condition|)
block|{
name|inACChoice
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"Fallback"
argument_list|)
condition|)
block|{
name|inACFallback
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|inACFallback
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|W_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|)
condition|)
block|{
name|handler
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
name|localName
operator|.
name|equals
argument_list|(
literal|"r"
argument_list|)
condition|)
block|{
name|inR
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|inT
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"tab"
argument_list|)
condition|)
block|{
name|handler
operator|.
name|characters
argument_list|(
name|TAB
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"tbl"
argument_list|)
condition|)
block|{
name|handler
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
name|localName
operator|.
name|equals
argument_list|(
literal|"tc"
argument_list|)
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"tr"
argument_list|)
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"rPr"
argument_list|)
condition|)
block|{
name|inRPr
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inR
operator|&&
name|inRPr
operator|&&
name|localName
operator|.
name|equals
argument_list|(
literal|"i"
argument_list|)
condition|)
block|{
comment|//rprs don't have to be inR; ignore those that aren't
name|currFormat
operator|.
name|italics
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inR
operator|&&
name|inRPr
operator|&&
name|localName
operator|.
name|equals
argument_list|(
literal|"b"
argument_list|)
condition|)
block|{
name|currFormat
operator|.
name|bold
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"delText"
argument_list|)
condition|)
block|{
name|inDelText
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"ins"
argument_list|)
condition|)
block|{
name|editAuthor
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"author"
argument_list|)
expr_stmt|;
name|editDate
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"date"
argument_list|)
expr_stmt|;
name|editType
operator|=
name|EditType
operator|.
name|INSERT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"del"
argument_list|)
condition|)
block|{
name|editAuthor
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"author"
argument_list|)
expr_stmt|;
name|editDate
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"date"
argument_list|)
expr_stmt|;
name|editType
operator|=
name|EditType
operator|.
name|DELETE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"hyperlink"
argument_list|)
condition|)
block|{
name|String
name|hyperlinkId
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|OFFICE_DOC_RELATIONSHIP_NS
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|hyperlinkId
operator|!=
literal|null
condition|)
block|{
name|Relationship
name|relationship
init|=
name|relationshipsManager
operator|.
name|getRelationship
argument_list|(
name|getName
argument_list|()
argument_list|,
name|hyperlinkId
argument_list|)
decl_stmt|;
if|if
condition|(
name|relationship
operator|!=
literal|null
operator|&&
name|XWPFRelation
operator|.
name|HYPERLINK
operator|.
name|getRelation
argument_list|()
operator|.
name|equals
argument_list|(
name|relationship
operator|.
name|getContentType
argument_list|()
argument_list|)
condition|)
block|{
name|hyperlink
operator|=
name|relationship
operator|.
name|getTarget
argument_list|()
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"a"
argument_list|,
literal|"href"
argument_list|,
name|hyperlink
argument_list|)
expr_stmt|;
name|hasWrittenAHref
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
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
name|uri
operator|.
name|equals
argument_list|(
name|MC_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"AlternateContent"
argument_list|)
condition|)
block|{
name|inAlternateContent
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"Choice"
argument_list|)
condition|)
block|{
name|inACChoice
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"Fallback"
argument_list|)
condition|)
block|{
name|inACFallback
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|W_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|inACFallback
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|)
condition|)
block|{
name|handler
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
name|localName
operator|.
name|equals
argument_list|(
literal|"r"
argument_list|)
condition|)
block|{
name|closeStyleTags
argument_list|()
expr_stmt|;
name|inR
operator|=
literal|false
expr_stmt|;
name|hasWrittenFormatting
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|inT
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"tbl"
argument_list|)
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"tc"
argument_list|)
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"tr"
argument_list|)
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"rPr"
argument_list|)
condition|)
block|{
name|inRPr
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"delText"
argument_list|)
condition|)
block|{
name|inDelText
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"ins"
argument_list|)
operator|||
name|localName
operator|.
name|equals
argument_list|(
literal|"del"
argument_list|)
condition|)
block|{
name|editType
operator|=
name|EditType
operator|.
name|NONE
expr_stmt|;
name|editAuthor
operator|=
literal|null
expr_stmt|;
name|editDate
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"hyperlink"
argument_list|)
operator|&&
name|hasWrittenAHref
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|hasWrittenAHref
operator|=
literal|false
expr_stmt|;
block|}
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
name|inACFallback
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|inR
operator|&&
operator|!
name|hasWrittenFormatting
condition|)
block|{
if|if
condition|(
name|currFormat
operator|.
name|bold
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currFormat
operator|.
name|italics
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
block|}
name|hasWrittenFormatting
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|inT
condition|)
block|{
name|handler
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
elseif|else
if|if
condition|(
name|includeDeletedContent
operator|&&
name|inDelText
condition|)
block|{
name|handler
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
name|inACFallback
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|inT
condition|)
block|{
name|handler
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
name|String
name|getPartContentType
parameter_list|()
block|{
return|return
name|partName
return|;
block|}
name|void
name|closeStyleTags
parameter_list|()
throws|throws
name|SAXException
block|{
if|if
condition|(
name|hasWrittenFormatting
condition|)
block|{
if|if
condition|(
name|currFormat
operator|.
name|italics
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currFormat
operator|.
name|bold
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
block|}
name|currFormat
operator|.
name|bold
operator|=
literal|false
expr_stmt|;
name|currFormat
operator|.
name|italics
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
class|class
name|TmpFormatting
block|{
name|boolean
name|italics
init|=
literal|false
decl_stmt|;
name|boolean
name|bold
init|=
literal|false
decl_stmt|;
block|}
block|}
end_class

end_unit

