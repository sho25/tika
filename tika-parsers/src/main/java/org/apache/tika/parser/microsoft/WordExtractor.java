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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|HashMap
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
name|List
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|HWPFDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|HWPFOldDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|OldWordFileFormatException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|extractor
operator|.
name|Word6Extractor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|model
operator|.
name|PicturesTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|model
operator|.
name|StyleDescription
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|usermodel
operator|.
name|CharacterRun
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|usermodel
operator|.
name|Paragraph
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|usermodel
operator|.
name|Picture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|usermodel
operator|.
name|Range
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|usermodel
operator|.
name|Table
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|usermodel
operator|.
name|TableCell
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|usermodel
operator|.
name|TableRow
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|DirectoryEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|POIFSFileSystem
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
name|TikaInputStream
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

begin_class
specifier|public
class|class
name|WordExtractor
extends|extends
name|AbstractPOIFSExtractor
block|{
specifier|public
name|WordExtractor
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|parse
parameter_list|(
name|POIFSFileSystem
name|filesystem
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|HWPFDocument
name|document
decl_stmt|;
try|try
block|{
name|document
operator|=
operator|new
name|HWPFDocument
argument_list|(
name|filesystem
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OldWordFileFormatException
name|e
parameter_list|)
block|{
name|parseWord6
argument_list|(
name|filesystem
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
return|return;
block|}
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|extractor
operator|.
name|WordExtractor
name|wordExtractor
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|extractor
operator|.
name|WordExtractor
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|addTextIfAny
argument_list|(
name|xhtml
argument_list|,
literal|"header"
argument_list|,
name|wordExtractor
operator|.
name|getHeaderText
argument_list|()
argument_list|)
expr_stmt|;
comment|// Grab the list of pictures. As far as we can tell,
comment|//  the pictures should be in order, and may be directly
comment|//  placed or referenced from an anchor
name|PicturesTable
name|pictureTable
init|=
name|document
operator|.
name|getPicturesTable
argument_list|()
decl_stmt|;
name|PicturesSource
name|pictures
init|=
operator|new
name|PicturesSource
argument_list|(
name|document
argument_list|)
decl_stmt|;
comment|// Do the main paragraph text
name|Range
name|r
init|=
name|document
operator|.
name|getRange
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|r
operator|.
name|numParagraphs
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Paragraph
name|p
init|=
name|r
operator|.
name|getParagraph
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|i
operator|+=
name|handleParagraph
argument_list|(
name|p
argument_list|,
literal|0
argument_list|,
name|r
argument_list|,
name|document
argument_list|,
name|pictures
argument_list|,
name|pictureTable
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
comment|// Do everything else
for|for
control|(
name|String
name|paragraph
range|:
name|wordExtractor
operator|.
name|getFootnoteText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|paragraph
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|paragraph
range|:
name|wordExtractor
operator|.
name|getCommentsText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|paragraph
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|paragraph
range|:
name|wordExtractor
operator|.
name|getEndnoteText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|paragraph
argument_list|)
expr_stmt|;
block|}
name|addTextIfAny
argument_list|(
name|xhtml
argument_list|,
literal|"footer"
argument_list|,
name|wordExtractor
operator|.
name|getFooterText
argument_list|()
argument_list|)
expr_stmt|;
comment|// Handle any pictures that we haven't output yet
for|for
control|(
name|Picture
name|p
init|=
name|pictures
operator|.
name|nextUnclaimed
argument_list|()
init|;
name|p
operator|!=
literal|null
condition|;
control|)
block|{
name|handlePictureCharacterRun
argument_list|(
literal|null
argument_list|,
name|p
argument_list|,
name|pictures
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
name|p
operator|=
name|pictures
operator|.
name|nextUnclaimed
argument_list|()
expr_stmt|;
block|}
comment|// Handle any embeded office documents
try|try
block|{
name|DirectoryEntry
name|op
init|=
operator|(
name|DirectoryEntry
operator|)
name|filesystem
operator|.
name|getRoot
argument_list|()
operator|.
name|getEntry
argument_list|(
literal|"ObjectPool"
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|op
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
operator|&&
name|entry
operator|instanceof
name|DirectoryEntry
condition|)
block|{
name|handleEmbededOfficeDoc
argument_list|(
operator|(
name|DirectoryEntry
operator|)
name|entry
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{         }
block|}
specifier|private
name|int
name|handleParagraph
parameter_list|(
name|Paragraph
name|p
parameter_list|,
name|int
name|parentTableLevel
parameter_list|,
name|Range
name|r
parameter_list|,
name|HWPFDocument
name|document
parameter_list|,
name|PicturesSource
name|pictures
parameter_list|,
name|PicturesTable
name|pictureTable
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
comment|// Note - a poi bug means we can't currently properly recurse
comment|//  into nested tables, so currently we don't
if|if
condition|(
name|p
operator|.
name|isInTable
argument_list|()
operator|&&
name|p
operator|.
name|getTableLevel
argument_list|()
operator|>
name|parentTableLevel
operator|&&
name|parentTableLevel
operator|==
literal|0
condition|)
block|{
name|Table
name|t
init|=
name|r
operator|.
name|getTable
argument_list|(
name|p
argument_list|)
decl_stmt|;
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
literal|"tbody"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|rn
init|=
literal|0
init|;
name|rn
operator|<
name|t
operator|.
name|numRows
argument_list|()
condition|;
name|rn
operator|++
control|)
block|{
name|TableRow
name|row
init|=
name|t
operator|.
name|getRow
argument_list|(
name|rn
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|cn
init|=
literal|0
init|;
name|cn
operator|<
name|row
operator|.
name|numCells
argument_list|()
condition|;
name|cn
operator|++
control|)
block|{
name|TableCell
name|cell
init|=
name|row
operator|.
name|getCell
argument_list|(
name|cn
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|pn
init|=
literal|0
init|;
name|pn
operator|<
name|cell
operator|.
name|numParagraphs
argument_list|()
condition|;
name|pn
operator|++
control|)
block|{
name|Paragraph
name|cellP
init|=
name|cell
operator|.
name|getParagraph
argument_list|(
name|pn
argument_list|)
decl_stmt|;
name|handleParagraph
argument_list|(
name|cellP
argument_list|,
name|p
operator|.
name|getTableLevel
argument_list|()
argument_list|,
name|cell
argument_list|,
name|document
argument_list|,
name|pictures
argument_list|,
name|pictureTable
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tbody"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
return|return
operator|(
name|t
operator|.
name|numParagraphs
argument_list|()
operator|-
literal|1
operator|)
return|;
block|}
name|StyleDescription
name|style
init|=
name|document
operator|.
name|getStyleSheet
argument_list|()
operator|.
name|getStyleDescription
argument_list|(
name|p
operator|.
name|getStyleIndex
argument_list|()
argument_list|)
decl_stmt|;
name|TagAndStyle
name|tas
init|=
name|buildParagraphTagAndStyle
argument_list|(
name|style
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|parentTableLevel
operator|>
literal|0
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|tas
operator|.
name|getStyleClass
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
name|tas
operator|.
name|getTag
argument_list|()
argument_list|,
literal|"class"
argument_list|,
name|tas
operator|.
name|getStyleClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
name|tas
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|p
operator|.
name|numCharacterRuns
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|CharacterRun
name|cr
init|=
name|p
operator|.
name|getCharacterRun
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|cr
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"\u0013"
argument_list|)
condition|)
block|{
name|j
operator|+=
name|handleSpecialCharacterRuns
argument_list|(
name|p
argument_list|,
name|j
argument_list|,
name|tas
operator|.
name|isHeading
argument_list|()
argument_list|,
name|pictures
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cr
operator|.
name|text
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"\u0008"
argument_list|)
condition|)
block|{
comment|// Floating Picture(s)
for|for
control|(
name|int
name|pn
init|=
literal|0
init|;
name|pn
operator|<
name|cr
operator|.
name|text
argument_list|()
operator|.
name|length
argument_list|()
condition|;
name|pn
operator|++
control|)
block|{
comment|// Assume they're in the order from the unclaimed list...
name|Picture
name|picture
init|=
name|pictures
operator|.
name|nextUnclaimed
argument_list|()
decl_stmt|;
comment|// Output
name|handlePictureCharacterRun
argument_list|(
name|cr
argument_list|,
name|picture
argument_list|,
name|pictures
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|pictureTable
operator|.
name|hasPicture
argument_list|(
name|cr
argument_list|)
condition|)
block|{
comment|// Inline Picture
name|Picture
name|picture
init|=
name|pictures
operator|.
name|getFor
argument_list|(
name|cr
argument_list|)
decl_stmt|;
name|handlePictureCharacterRun
argument_list|(
name|cr
argument_list|,
name|picture
argument_list|,
name|pictures
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handleCharacterRun
argument_list|(
name|cr
argument_list|,
name|tas
operator|.
name|isHeading
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
name|tas
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|private
name|void
name|handleCharacterRun
parameter_list|(
name|CharacterRun
name|cr
parameter_list|,
name|boolean
name|skipStyling
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// Skip trailing newlines
if|if
condition|(
name|cr
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"\r"
argument_list|)
condition|)
return|return;
name|List
argument_list|<
name|String
argument_list|>
name|tags
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|skipStyling
condition|)
block|{
if|if
condition|(
name|cr
operator|.
name|isBold
argument_list|()
condition|)
name|tags
operator|.
name|add
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
if|if
condition|(
name|cr
operator|.
name|isItalic
argument_list|()
condition|)
name|tags
operator|.
name|add
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
if|if
condition|(
name|cr
operator|.
name|isStrikeThrough
argument_list|()
condition|)
name|tags
operator|.
name|add
argument_list|(
literal|"s"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|tags
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Clean up the text
name|String
name|text
init|=
name|cr
operator|.
name|text
argument_list|()
decl_stmt|;
name|text
operator|=
name|text
operator|.
name|replace
argument_list|(
literal|'\r'
argument_list|,
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|text
operator|.
name|endsWith
argument_list|(
literal|"\u0007"
argument_list|)
condition|)
block|{
comment|// Strip the table cell end marker
name|text
operator|=
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|characters
argument_list|(
name|text
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|tn
init|=
name|tags
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|tn
operator|>=
literal|0
condition|;
name|tn
operator|--
control|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
name|tags
operator|.
name|get
argument_list|(
name|tn
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Can be \13..text..\15 or \13..control..\14..text..\15 .      * Nesting is allowed      */
specifier|private
name|int
name|handleSpecialCharacterRuns
parameter_list|(
name|Paragraph
name|p
parameter_list|,
name|int
name|index
parameter_list|,
name|boolean
name|skipStyling
parameter_list|,
name|PicturesSource
name|pictures
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|TikaException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|CharacterRun
argument_list|>
name|controls
init|=
operator|new
name|ArrayList
argument_list|<
name|CharacterRun
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CharacterRun
argument_list|>
name|texts
init|=
operator|new
name|ArrayList
argument_list|<
name|CharacterRun
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|has14
init|=
literal|false
decl_stmt|;
comment|// Split it into before and after the 14
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
name|index
operator|+
literal|1
init|;
name|i
operator|<
name|p
operator|.
name|numCharacterRuns
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CharacterRun
name|cr
init|=
name|p
operator|.
name|getCharacterRun
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|cr
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"\u0013"
argument_list|)
condition|)
block|{
comment|// Nested, oh joy...
name|int
name|increment
init|=
name|handleSpecialCharacterRuns
argument_list|(
name|p
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|skipStyling
argument_list|,
name|pictures
argument_list|,
name|xhtml
argument_list|)
decl_stmt|;
name|i
operator|+=
name|increment
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cr
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"\u0014"
argument_list|)
condition|)
block|{
name|has14
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cr
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"\u0015"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|has14
condition|)
block|{
name|texts
operator|=
name|controls
expr_stmt|;
name|controls
operator|=
operator|new
name|ArrayList
argument_list|<
name|CharacterRun
argument_list|>
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
if|if
condition|(
name|has14
condition|)
block|{
name|texts
operator|.
name|add
argument_list|(
name|cr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|controls
operator|.
name|add
argument_list|(
name|cr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Do we need to do something special with this?
if|if
condition|(
name|controls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|text
init|=
name|controls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|text
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|controls
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|text
operator|+=
name|controls
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|text
operator|.
name|startsWith
argument_list|(
literal|"HYPERLINK"
argument_list|)
operator|&&
name|text
operator|.
name|indexOf
argument_list|(
literal|'"'
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|String
name|url
init|=
name|text
operator|.
name|substring
argument_list|(
name|text
operator|.
name|indexOf
argument_list|(
literal|'"'
argument_list|)
operator|+
literal|1
argument_list|,
name|text
operator|.
name|lastIndexOf
argument_list|(
literal|'"'
argument_list|)
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"a"
argument_list|,
literal|"href"
argument_list|,
name|url
argument_list|)
expr_stmt|;
for|for
control|(
name|CharacterRun
name|cr
range|:
name|texts
control|)
block|{
name|handleCharacterRun
argument_list|(
name|cr
argument_list|,
name|skipStyling
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Just output the text ones
for|for
control|(
name|CharacterRun
name|cr
range|:
name|texts
control|)
block|{
if|if
condition|(
name|pictures
operator|.
name|hasPicture
argument_list|(
name|cr
argument_list|)
condition|)
block|{
name|Picture
name|picture
init|=
name|pictures
operator|.
name|getFor
argument_list|(
name|cr
argument_list|)
decl_stmt|;
name|handlePictureCharacterRun
argument_list|(
name|cr
argument_list|,
name|picture
argument_list|,
name|pictures
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handleCharacterRun
argument_list|(
name|cr
argument_list|,
name|skipStyling
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// We only had text
comment|// Output as-is
for|for
control|(
name|CharacterRun
name|cr
range|:
name|texts
control|)
block|{
name|handleCharacterRun
argument_list|(
name|cr
argument_list|,
name|skipStyling
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Tell them how many to skip over
return|return
name|i
operator|-
name|index
return|;
block|}
specifier|private
name|void
name|handlePictureCharacterRun
parameter_list|(
name|CharacterRun
name|cr
parameter_list|,
name|Picture
name|picture
parameter_list|,
name|PicturesSource
name|pictures
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
if|if
condition|(
name|picture
operator|==
literal|null
condition|)
block|{
comment|// Oh dear, we've run out...
comment|// Probably caused by multiple \u0008 images referencing
comment|//  the same real image
return|return;
block|}
comment|// Which one is it?
name|String
name|extension
init|=
name|picture
operator|.
name|suggestFileExtension
argument_list|()
decl_stmt|;
name|int
name|pictureNumber
init|=
name|pictures
operator|.
name|pictureNumber
argument_list|(
name|picture
argument_list|)
decl_stmt|;
comment|// Make up a name for the picture
comment|// There isn't one in the file, but we need to be able to reference
comment|//  the picture from the img tag and the embedded resource
name|String
name|filename
init|=
literal|"image"
operator|+
name|pictureNumber
operator|+
operator|(
name|extension
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
literal|"."
operator|+
name|extension
else|:
literal|""
operator|)
decl_stmt|;
comment|// Grab the mime type for the picture
name|String
name|mimeType
init|=
name|picture
operator|.
name|getMimeType
argument_list|()
decl_stmt|;
comment|// Output the img tag
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"img"
argument_list|,
literal|"src"
argument_list|,
literal|"embedded:"
operator|+
name|filename
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"img"
argument_list|)
expr_stmt|;
comment|// Have we already output this one?
comment|// (Only expose each individual image once)
if|if
condition|(
operator|!
name|pictures
operator|.
name|hasOutput
argument_list|(
name|picture
argument_list|)
condition|)
block|{
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|picture
operator|.
name|getContent
argument_list|()
argument_list|)
decl_stmt|;
name|handleEmbeddedResource
argument_list|(
name|stream
argument_list|,
name|filename
argument_list|,
name|mimeType
argument_list|,
name|xhtml
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pictures
operator|.
name|recordOutput
argument_list|(
name|picture
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Outputs a section of text if the given text is non-empty.      *      * @param xhtml XHTML content handler      * @param section the class of the&lt;div/&gt; section emitted      * @param text text to be emitted, if any      * @throws SAXException if an error occurs      */
specifier|private
name|void
name|addTextIfAny
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|String
name|section
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
name|section
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|text
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
block|}
specifier|protected
name|void
name|parseWord6
parameter_list|(
name|POIFSFileSystem
name|filesystem
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|HWPFOldDocument
name|doc
init|=
operator|new
name|HWPFOldDocument
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
name|Word6Extractor
name|extractor
init|=
operator|new
name|Word6Extractor
argument_list|(
name|doc
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|extractor
operator|.
name|getParagraphText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Given a style name, return what tag should be used, and      *  what style should be applied to it.       */
specifier|public
specifier|static
name|TagAndStyle
name|buildParagraphTagAndStyle
parameter_list|(
name|String
name|styleName
parameter_list|,
name|boolean
name|isTable
parameter_list|)
block|{
name|String
name|tag
init|=
literal|"p"
decl_stmt|;
name|String
name|styleClass
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|styleName
operator|.
name|equals
argument_list|(
literal|"Default"
argument_list|)
operator|||
name|styleName
operator|.
name|equals
argument_list|(
literal|"Normal"
argument_list|)
condition|)
block|{
comment|// Already setup
block|}
elseif|else
if|if
condition|(
name|styleName
operator|.
name|equals
argument_list|(
literal|"Table Contents"
argument_list|)
operator|&&
name|isTable
condition|)
block|{
comment|// Already setup
block|}
elseif|else
if|if
condition|(
name|styleName
operator|.
name|equals
argument_list|(
literal|"heading"
argument_list|)
operator|||
name|styleName
operator|.
name|equals
argument_list|(
literal|"Heading"
argument_list|)
condition|)
block|{
name|tag
operator|=
literal|"h1"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|styleName
operator|.
name|startsWith
argument_list|(
literal|"heading"
argument_list|)
operator|||
name|styleName
operator|.
name|startsWith
argument_list|(
literal|"Heading"
argument_list|)
condition|)
block|{
comment|// "Heading 3" or "Heading2" or "heading 4"
name|int
name|num
init|=
literal|1
decl_stmt|;
try|try
block|{
name|num
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|styleName
operator|.
name|substring
argument_list|(
name|styleName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{}
name|tag
operator|=
literal|"h"
operator|+
name|num
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|styleName
operator|.
name|equals
argument_list|(
literal|"Title"
argument_list|)
condition|)
block|{
name|tag
operator|=
literal|"h1"
expr_stmt|;
name|styleClass
operator|=
literal|"title"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|styleName
operator|.
name|equals
argument_list|(
literal|"Subtitle"
argument_list|)
condition|)
block|{
name|tag
operator|=
literal|"h2"
expr_stmt|;
name|styleClass
operator|=
literal|"subtitle"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|styleName
operator|.
name|equals
argument_list|(
literal|"HTML Preformatted"
argument_list|)
condition|)
block|{
name|tag
operator|=
literal|"pre"
expr_stmt|;
block|}
else|else
block|{
name|styleClass
operator|=
name|styleName
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'_'
argument_list|)
expr_stmt|;
name|styleClass
operator|=
name|styleClass
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|+
name|styleClass
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TagAndStyle
argument_list|(
name|tag
argument_list|,
name|styleClass
argument_list|)
return|;
block|}
specifier|public
specifier|static
class|class
name|TagAndStyle
block|{
specifier|private
name|String
name|tag
decl_stmt|;
specifier|private
name|String
name|styleClass
decl_stmt|;
specifier|public
name|TagAndStyle
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|styleClass
parameter_list|)
block|{
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
name|this
operator|.
name|styleClass
operator|=
name|styleClass
expr_stmt|;
block|}
specifier|public
name|String
name|getTag
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
specifier|public
name|String
name|getStyleClass
parameter_list|()
block|{
return|return
name|styleClass
return|;
block|}
specifier|public
name|boolean
name|isHeading
parameter_list|()
block|{
return|return
name|tag
operator|.
name|length
argument_list|()
operator|==
literal|2
operator|&&
name|tag
operator|.
name|startsWith
argument_list|(
literal|"h"
argument_list|)
return|;
block|}
block|}
comment|/**      * Provides access to the pictures both by offset, iteration      *  over the un-claimed, and peeking forward      */
specifier|private
specifier|static
class|class
name|PicturesSource
block|{
specifier|private
name|PicturesTable
name|picturesTable
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|Picture
argument_list|>
name|output
init|=
operator|new
name|HashSet
argument_list|<
name|Picture
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Picture
argument_list|>
name|lookup
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Picture
argument_list|>
name|nonU1based
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Picture
argument_list|>
name|all
decl_stmt|;
specifier|private
name|int
name|pn
init|=
literal|0
decl_stmt|;
specifier|private
name|PicturesSource
parameter_list|(
name|HWPFDocument
name|doc
parameter_list|)
block|{
name|picturesTable
operator|=
name|doc
operator|.
name|getPicturesTable
argument_list|()
expr_stmt|;
name|all
operator|=
name|picturesTable
operator|.
name|getAllPictures
argument_list|()
expr_stmt|;
comment|// Compute the Offset-Picture lookup
name|lookup
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Picture
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Picture
name|p
range|:
name|all
control|)
block|{
comment|// TODO Make this nicer when POI 3.7 is out
name|String
name|name
init|=
name|p
operator|.
name|suggestFullFileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
operator|>
operator|-
literal|1
condition|)
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|name
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|lookup
operator|.
name|put
argument_list|(
name|offset
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
comment|// Work out which Pictures aren't referenced by
comment|//  a \u0001 in the main text
comment|// These are \u0008 escher floating ones, ones
comment|//  found outside the normal text, and who
comment|//  knows what else...
name|nonU1based
operator|=
operator|new
name|ArrayList
argument_list|<
name|Picture
argument_list|>
argument_list|()
expr_stmt|;
name|nonU1based
operator|.
name|addAll
argument_list|(
name|all
argument_list|)
expr_stmt|;
name|Range
name|r
init|=
name|doc
operator|.
name|getRange
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|r
operator|.
name|numCharacterRuns
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CharacterRun
name|cr
init|=
name|r
operator|.
name|getCharacterRun
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|picturesTable
operator|.
name|hasPicture
argument_list|(
name|cr
argument_list|)
condition|)
block|{
name|Picture
name|p
init|=
name|getFor
argument_list|(
name|cr
argument_list|)
decl_stmt|;
name|int
name|at
init|=
name|nonU1based
operator|.
name|indexOf
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|nonU1based
operator|.
name|set
argument_list|(
name|at
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|boolean
name|hasPicture
parameter_list|(
name|CharacterRun
name|cr
parameter_list|)
block|{
return|return
name|picturesTable
operator|.
name|hasPicture
argument_list|(
name|cr
argument_list|)
return|;
block|}
specifier|private
name|void
name|recordOutput
parameter_list|(
name|Picture
name|picture
parameter_list|)
block|{
name|output
operator|.
name|add
argument_list|(
name|picture
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|hasOutput
parameter_list|(
name|Picture
name|picture
parameter_list|)
block|{
return|return
name|output
operator|.
name|contains
argument_list|(
name|picture
argument_list|)
return|;
block|}
specifier|private
name|int
name|pictureNumber
parameter_list|(
name|Picture
name|picture
parameter_list|)
block|{
return|return
name|all
operator|.
name|indexOf
argument_list|(
name|picture
argument_list|)
operator|+
literal|1
return|;
block|}
specifier|private
name|Picture
name|getFor
parameter_list|(
name|CharacterRun
name|cr
parameter_list|)
block|{
return|return
name|lookup
operator|.
name|get
argument_list|(
name|cr
operator|.
name|getPicOffset
argument_list|()
argument_list|)
return|;
block|}
comment|/**         * Return the next unclaimed one, used towards         *  the end         */
specifier|private
name|Picture
name|nextUnclaimed
parameter_list|()
block|{
name|Picture
name|p
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|pn
operator|<
name|nonU1based
operator|.
name|size
argument_list|()
condition|)
block|{
name|p
operator|=
name|nonU1based
operator|.
name|get
argument_list|(
name|pn
argument_list|)
expr_stmt|;
name|pn
operator|++
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
return|return
name|p
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

