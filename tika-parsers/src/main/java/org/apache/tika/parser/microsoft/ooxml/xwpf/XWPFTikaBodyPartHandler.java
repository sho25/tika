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
name|OfficeParserConfig
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
name|ooxml
operator|.
name|XWPFListManager
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

begin_class
specifier|public
class|class
name|XWPFTikaBodyPartHandler
implements|implements
name|XWPFDocumentXMLBodyHandler
operator|.
name|XWPFBodyContentsHandler
block|{
specifier|private
specifier|final
specifier|static
name|char
index|[]
name|NEWLINE
init|=
operator|new
name|char
index|[]
block|{
literal|'\n'
block|}
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
index|[]
block|{
literal|'\t'
block|}
decl_stmt|;
specifier|private
specifier|final
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
specifier|private
specifier|final
name|XWPFListManager
name|listManager
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|includeDeletedText
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|includeMoveFromText
decl_stmt|;
specifier|private
name|int
name|pDepth
init|=
literal|0
decl_stmt|;
comment|//paragraph depth
specifier|private
name|boolean
name|isItalics
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isBold
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|wroteHyperlinkStart
init|=
literal|false
decl_stmt|;
specifier|public
name|XWPFTikaBodyPartHandler
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|,
name|OfficeParserConfig
name|parserConfig
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
name|listManager
operator|=
name|listManager
expr_stmt|;
name|this
operator|.
name|includeDeletedText
operator|=
name|parserConfig
operator|.
name|getIncludeDeletedContent
argument_list|()
expr_stmt|;
name|this
operator|.
name|includeMoveFromText
operator|=
name|parserConfig
operator|.
name|getIncludeMoveFromContent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|XWPFRunProperties
name|runProperties
parameter_list|,
name|String
name|contents
parameter_list|)
block|{
try|try
block|{
comment|// True if we are currently in the named style tag:
if|if
condition|(
name|runProperties
operator|.
name|getBold
argument_list|()
operator|!=
name|isBold
condition|)
block|{
if|if
condition|(
name|isItalics
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
name|isItalics
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|runProperties
operator|.
name|getBold
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|isBold
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|isBold
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|runProperties
operator|.
name|getItalics
argument_list|()
operator|!=
name|isItalics
condition|)
block|{
if|if
condition|(
name|runProperties
operator|.
name|getItalics
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
name|isItalics
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
name|isItalics
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|xhtml
operator|.
name|characters
argument_list|(
name|contents
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|hyperlinkStart
parameter_list|(
name|String
name|link
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|link
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"a"
argument_list|,
literal|"href"
argument_list|,
name|link
argument_list|)
expr_stmt|;
name|wroteHyperlinkStart
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|hyperlinkEnd
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|wroteHyperlinkStart
condition|)
block|{
name|closeStyleTags
argument_list|()
expr_stmt|;
name|wroteHyperlinkStart
operator|=
literal|false
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|startParagraph
parameter_list|()
block|{
if|if
condition|(
name|pDepth
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{              }
block|}
name|pDepth
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endParagraph
parameter_list|()
block|{
try|try
block|{
name|closeStyleTags
argument_list|()
expr_stmt|;
if|if
condition|(
name|pDepth
operator|==
literal|1
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
else|else
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|NEWLINE
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
name|pDepth
operator|--
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startTable
parameter_list|()
block|{
try|try
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|endTable
parameter_list|()
block|{
try|try
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|startTableRow
parameter_list|()
block|{
try|try
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|endTableRow
parameter_list|()
block|{
try|try
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|startTableCell
parameter_list|()
block|{
try|try
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|endTableCell
parameter_list|()
block|{
try|try
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|startSDT
parameter_list|()
block|{
try|try
block|{
name|closeStyleTags
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|endSDT
parameter_list|()
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|startEditedSection
parameter_list|(
name|String
name|editor
parameter_list|,
name|Date
name|date
parameter_list|,
name|XWPFDocumentXMLBodyHandler
operator|.
name|EditType
name|editType
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|endEditedSection
parameter_list|()
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getIncludeDeletedText
parameter_list|()
block|{
return|return
name|includeDeletedText
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|footnoteReference
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|xhtml
operator|.
name|characters
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{              }
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endnoteReference
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|xhtml
operator|.
name|characters
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{              }
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getIncludeMoveFromText
parameter_list|()
block|{
return|return
name|includeMoveFromText
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|embeddedOLERef
parameter_list|(
name|String
name|relId
parameter_list|)
block|{
if|if
condition|(
name|relId
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|AttributesImpl
name|attributes
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"class"
argument_list|,
literal|"class"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"embedded"
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"id"
argument_list|,
literal|"id"
argument_list|,
literal|"CDATA"
argument_list|,
name|relId
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Override
specifier|public
name|void
name|embeddedPicRef
parameter_list|(
name|String
name|picFileName
parameter_list|,
name|String
name|picDescription
parameter_list|)
block|{
try|try
block|{
name|AttributesImpl
name|attr
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|picFileName
operator|!=
literal|null
condition|)
block|{
name|attr
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"src"
argument_list|,
literal|"src"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"embedded:"
operator|+
name|picFileName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|picDescription
operator|!=
literal|null
condition|)
block|{
name|attr
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"alt"
argument_list|,
literal|"alt"
argument_list|,
literal|"CDATA"
argument_list|,
name|picDescription
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"img"
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"img"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{          }
block|}
specifier|private
name|void
name|closeStyleTags
parameter_list|()
throws|throws
name|SAXException
block|{
if|if
condition|(
name|isItalics
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
name|isItalics
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|isBold
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|isBold
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

