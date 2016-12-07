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
name|java
operator|.
name|util
operator|.
name|Date
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|DateUtils
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

begin_comment
comment|/**  * This class is intended to handle anything that might contain IBodyElements:  * main document, headers, footers, notes, etc.  */
end_comment

begin_class
specifier|public
class|class
name|XWPFDocumentXMLBodyHandler
extends|extends
name|DefaultHandler
block|{
enum|enum
name|EditType
block|{
name|NONE
block|,
name|INSERT
block|,
name|DELETE
block|,
name|MOVE_TO
block|,
name|MOVE_FROM
block|}
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
name|O_NS
init|=
literal|"urn:schemas-microsoft-com:office:office"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PIC_NS
init|=
literal|"http://schemas.openxmlformats.org/drawingml/2006/picture"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRAWING_MAIN_NS
init|=
literal|"http://schemas.openxmlformats.org/drawingml/2006/main"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|V_NS
init|=
literal|"urn:schemas-microsoft-com:vml"
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
name|XWPFBodyContentsHandler
name|bodyContentsHandler
decl_stmt|;
comment|//private final RelationshipsManager relationshipsManager;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|linkedRelationships
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|runBuffer
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|int
name|pDepth
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|inRPr
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inNumPr
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
name|inPic
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|inPict
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|picDescription
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|picRId
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|picFilename
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|lastStartElementWasP
decl_stmt|;
comment|//alternate content can be embedded in itself.
comment|//need to track depth.
comment|//if in alternate, choose fallback, maybe make this configurable?
specifier|private
name|int
name|inACChoiceDepth
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|inACFallbackDepth
init|=
literal|0
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
name|XWPFRunProperties
name|currRunProperties
init|=
operator|new
name|XWPFRunProperties
argument_list|()
decl_stmt|;
specifier|private
name|XWPFParagraphProperties
name|currPProperties
init|=
operator|new
name|XWPFParagraphProperties
argument_list|()
decl_stmt|;
specifier|public
name|XWPFDocumentXMLBodyHandler
parameter_list|(
name|XWPFBodyContentsHandler
name|bodyContentsHandler
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|hyperlinks
parameter_list|)
block|{
name|this
operator|.
name|bodyContentsHandler
operator|=
name|bodyContentsHandler
expr_stmt|;
name|this
operator|.
name|linkedRelationships
operator|=
name|hyperlinks
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
name|lastStartElementWasP
operator|&&
operator|!
literal|"pPr"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|bodyContentsHandler
operator|.
name|startParagraph
argument_list|(
name|currPProperties
argument_list|)
expr_stmt|;
block|}
name|lastStartElementWasP
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
operator|&&
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
literal|"Choice"
argument_list|)
condition|)
block|{
name|inACChoiceDepth
operator|++
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
name|inACFallbackDepth
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|inACChoiceDepth
operator|>
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|uri
operator|==
literal|null
operator|||
name|uri
operator|.
name|equals
argument_list|(
name|O_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"OLEObject"
argument_list|)
condition|)
block|{
name|String
name|type
init|=
literal|null
decl_stmt|;
name|String
name|refId
init|=
literal|null
decl_stmt|;
comment|//TODO: want to get ProgID?
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attLocalName
init|=
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attValue
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|attLocalName
operator|.
name|equals
argument_list|(
literal|"Type"
argument_list|)
condition|)
block|{
name|type
operator|=
name|attValue
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|OFFICE_DOC_RELATIONSHIP_NS
operator|.
name|equals
argument_list|(
name|atts
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
argument_list|)
operator|&&
name|attLocalName
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|refId
operator|=
name|attValue
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|"Embed"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|bodyContentsHandler
operator|.
name|embeddedOLERef
argument_list|(
name|refId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|uri
operator|==
literal|null
operator|||
name|uri
operator|.
name|equals
argument_list|(
name|PIC_NS
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"pic"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inPic
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"cNvPr"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|picDescription
operator|=
name|atts
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"descr"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|uri
operator|==
literal|null
operator|||
name|uri
operator|.
name|equals
argument_list|(
name|DRAWING_MAIN_NS
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"blip"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|picRId
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|OFFICE_DOC_RELATIONSHIP_NS
argument_list|,
literal|"embed"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|uri
operator|==
literal|null
operator|||
name|uri
operator|.
name|equals
argument_list|(
name|V_NS
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"imagedata"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|picRId
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|OFFICE_DOC_RELATIONSHIP_NS
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|picDescription
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|O_NS
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|uri
operator|==
literal|null
operator|||
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
name|lastStartElementWasP
operator|=
literal|true
expr_stmt|;
name|pDepth
operator|++
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
name|runBuffer
operator|.
name|append
argument_list|(
literal|"\t"
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
name|bodyContentsHandler
operator|.
name|startTable
argument_list|()
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
name|bodyContentsHandler
operator|.
name|startTableCell
argument_list|()
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
name|bodyContentsHandler
operator|.
name|startTableRow
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"numPr"
argument_list|)
condition|)
block|{
name|inNumPr
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
name|currRunProperties
operator|.
name|setItalics
argument_list|(
literal|true
argument_list|)
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
name|currRunProperties
operator|.
name|setBold
argument_list|(
literal|true
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
name|startEditedSection
argument_list|(
name|editType
operator|.
name|INSERT
argument_list|,
name|atts
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
literal|"del"
argument_list|)
condition|)
block|{
name|startEditedSection
argument_list|(
name|editType
operator|.
name|DELETE
argument_list|,
name|atts
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
literal|"moveTo"
argument_list|)
condition|)
block|{
name|startEditedSection
argument_list|(
name|EditType
operator|.
name|MOVE_TO
argument_list|,
name|atts
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
literal|"moveFrom"
argument_list|)
condition|)
block|{
name|startEditedSection
argument_list|(
name|editType
operator|.
name|MOVE_FROM
argument_list|,
name|atts
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
name|String
name|hyperlink
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hyperlinkId
operator|!=
literal|null
condition|)
block|{
name|hyperlink
operator|=
name|linkedRelationships
operator|.
name|get
argument_list|(
name|hyperlinkId
argument_list|)
expr_stmt|;
name|bodyContentsHandler
operator|.
name|hyperlinkStart
argument_list|(
name|hyperlink
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|anchor
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"anchor"
argument_list|)
decl_stmt|;
if|if
condition|(
name|anchor
operator|!=
literal|null
condition|)
block|{
name|anchor
operator|=
literal|"#"
operator|+
name|anchor
expr_stmt|;
block|}
name|bodyContentsHandler
operator|.
name|hyperlinkStart
argument_list|(
name|anchor
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"footnoteReference"
argument_list|)
condition|)
block|{
name|String
name|id
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|bodyContentsHandler
operator|.
name|footnoteReference
argument_list|(
name|id
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
literal|"endnoteReference"
argument_list|)
condition|)
block|{
name|String
name|id
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|bodyContentsHandler
operator|.
name|endnoteReference
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"pStyle"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|String
name|styleId
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"val"
argument_list|)
decl_stmt|;
name|currPProperties
operator|.
name|setStyleID
argument_list|(
name|styleId
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inNumPr
operator|&&
literal|"ilvl"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|currPProperties
operator|.
name|setIlvl
argument_list|(
name|getIntVal
argument_list|(
name|atts
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inNumPr
operator|&&
literal|"numId"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|currPProperties
operator|.
name|setNumId
argument_list|(
name|getIntVal
argument_list|(
name|atts
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"bookmarkStart"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|bodyContentsHandler
operator|.
name|startBookmark
argument_list|(
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"bookmarkEnd"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|String
name|id
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|bodyContentsHandler
operator|.
name|endBookmark
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
comment|/*else if (localName.equals("headerReference")) {                 //TODO             } else if (localName.equals("footerReference")) {                 //TODO             } else if (localName.equals("commentRangeEnd")) {                 //TODO             }*/
block|}
block|}
specifier|private
name|void
name|startEditedSection
parameter_list|(
name|EditType
name|editType
parameter_list|,
name|Attributes
name|atts
parameter_list|)
block|{
name|String
name|editAuthor
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"author"
argument_list|)
decl_stmt|;
name|String
name|editDateString
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"date"
argument_list|)
decl_stmt|;
name|Date
name|editDate
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|editDateString
operator|!=
literal|null
condition|)
block|{
name|editDate
operator|=
name|DateUtils
operator|.
name|tryToParse
argument_list|(
name|editDateString
argument_list|)
expr_stmt|;
block|}
name|bodyContentsHandler
operator|.
name|startEditedSection
argument_list|(
name|editAuthor
argument_list|,
name|editDate
argument_list|,
name|editType
argument_list|)
expr_stmt|;
name|this
operator|.
name|editType
operator|=
name|editType
expr_stmt|;
block|}
specifier|private
name|int
name|getIntVal
parameter_list|(
name|Attributes
name|atts
parameter_list|)
block|{
name|String
name|valString
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|W_NS
argument_list|,
literal|"val"
argument_list|)
decl_stmt|;
if|if
condition|(
name|valString
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|valString
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
return|return
operator|-
literal|1
return|;
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
literal|"Choice"
argument_list|)
condition|)
block|{
name|inACChoiceDepth
operator|--
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
name|inACFallbackDepth
operator|--
expr_stmt|;
block|}
block|}
if|if
condition|(
name|PIC_NS
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"pic"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|handlePict
argument_list|()
expr_stmt|;
name|inPic
operator|=
literal|false
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|uri
operator|==
literal|null
operator|||
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
name|inACChoiceDepth
operator|>
literal|0
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
literal|"r"
argument_list|)
condition|)
block|{
name|bodyContentsHandler
operator|.
name|run
argument_list|(
name|currRunProperties
argument_list|,
name|runBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|inR
operator|=
literal|false
expr_stmt|;
name|runBuffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|currRunProperties
operator|.
name|setBold
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|currRunProperties
operator|.
name|setItalics
argument_list|(
literal|false
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
literal|"p"
argument_list|)
condition|)
block|{
name|bodyContentsHandler
operator|.
name|endParagraph
argument_list|()
expr_stmt|;
name|pDepth
operator|--
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
name|bodyContentsHandler
operator|.
name|endTable
argument_list|()
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
name|bodyContentsHandler
operator|.
name|endTableCell
argument_list|()
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
name|bodyContentsHandler
operator|.
name|endTableRow
argument_list|()
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
operator|||
name|localName
operator|.
name|equals
argument_list|(
literal|"moveTo"
argument_list|)
operator|||
name|localName
operator|.
name|equals
argument_list|(
literal|"moveFrom"
argument_list|)
condition|)
block|{
name|editType
operator|=
name|EditType
operator|.
name|NONE
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
name|bodyContentsHandler
operator|.
name|hyperlinkEnd
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"pict"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|handlePict
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"pPr"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|bodyContentsHandler
operator|.
name|startParagraph
argument_list|(
name|currPProperties
argument_list|)
expr_stmt|;
name|currPProperties
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|handlePict
parameter_list|()
block|{
name|String
name|picFileName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|picRId
operator|!=
literal|null
condition|)
block|{
name|picFileName
operator|=
name|linkedRelationships
operator|.
name|get
argument_list|(
name|picRId
argument_list|)
expr_stmt|;
block|}
name|bodyContentsHandler
operator|.
name|embeddedPicRef
argument_list|(
name|picFileName
argument_list|,
name|picDescription
argument_list|)
expr_stmt|;
name|picDescription
operator|=
literal|null
expr_stmt|;
name|picRId
operator|=
literal|null
expr_stmt|;
name|inPic
operator|=
literal|false
expr_stmt|;
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
name|inACChoiceDepth
operator|>
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|editType
operator|.
name|equals
argument_list|(
name|EditType
operator|.
name|MOVE_FROM
argument_list|)
operator|&&
name|inT
condition|)
block|{
if|if
condition|(
name|bodyContentsHandler
operator|.
name|getIncludeMoveFromText
argument_list|()
condition|)
block|{
name|runBuffer
operator|.
name|append
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
elseif|else
if|if
condition|(
name|inT
condition|)
block|{
name|runBuffer
operator|.
name|append
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
name|bodyContentsHandler
operator|.
name|getIncludeDeletedText
argument_list|()
operator|&&
name|editType
operator|.
name|equals
argument_list|(
name|EditType
operator|.
name|DELETE
argument_list|)
condition|)
block|{
name|runBuffer
operator|.
name|append
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
name|inACChoiceDepth
operator|>
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|inT
condition|)
block|{
name|runBuffer
operator|.
name|append
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
name|bodyContentsHandler
operator|.
name|getIncludeDeletedText
argument_list|()
operator|&&
name|inDelText
condition|)
block|{
name|runBuffer
operator|.
name|append
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
specifier|public
interface|interface
name|XWPFBodyContentsHandler
block|{
name|void
name|run
parameter_list|(
name|XWPFRunProperties
name|runProperties
parameter_list|,
name|String
name|contents
parameter_list|)
function_decl|;
comment|/**          *          * @param link the link; can be null          */
name|void
name|hyperlinkStart
parameter_list|(
name|String
name|link
parameter_list|)
function_decl|;
name|void
name|hyperlinkEnd
parameter_list|()
function_decl|;
name|void
name|startParagraph
parameter_list|(
name|XWPFParagraphProperties
name|paragraphProperties
parameter_list|)
function_decl|;
name|void
name|endParagraph
parameter_list|()
function_decl|;
name|void
name|startTable
parameter_list|()
function_decl|;
name|void
name|endTable
parameter_list|()
function_decl|;
name|void
name|startTableRow
parameter_list|()
function_decl|;
name|void
name|endTableRow
parameter_list|()
function_decl|;
name|void
name|startTableCell
parameter_list|()
function_decl|;
name|void
name|endTableCell
parameter_list|()
function_decl|;
name|void
name|startSDT
parameter_list|()
function_decl|;
name|void
name|endSDT
parameter_list|()
function_decl|;
name|void
name|startEditedSection
parameter_list|(
name|String
name|editor
parameter_list|,
name|Date
name|date
parameter_list|,
name|EditType
name|editType
parameter_list|)
function_decl|;
name|void
name|endEditedSection
parameter_list|()
function_decl|;
name|boolean
name|getIncludeDeletedText
parameter_list|()
function_decl|;
name|void
name|footnoteReference
parameter_list|(
name|String
name|id
parameter_list|)
function_decl|;
name|void
name|endnoteReference
parameter_list|(
name|String
name|id
parameter_list|)
function_decl|;
name|boolean
name|getIncludeMoveFromText
parameter_list|()
function_decl|;
name|void
name|embeddedOLERef
parameter_list|(
name|String
name|refId
parameter_list|)
function_decl|;
name|void
name|embeddedPicRef
parameter_list|(
name|String
name|picFileName
parameter_list|,
name|String
name|picDescription
parameter_list|)
function_decl|;
name|void
name|startBookmark
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
name|void
name|endBookmark
parameter_list|(
name|String
name|id
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

