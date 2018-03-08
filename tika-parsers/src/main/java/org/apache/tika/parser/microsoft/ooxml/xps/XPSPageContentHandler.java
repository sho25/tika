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
name|xps
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
name|ArrayList
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
name|Comparator
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
name|LinkedHashSet
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
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_comment
comment|/**  * Handles an individual page.  For now, this marks up  * canvas entities in a&lt;div&gt; tag.  Based on the spec,  * it currently relies on order within the xml for order of output  * of text to xhtml.  We could do more complex processing of coordinates  * for bidi-languages, but the spec implies that we should be able  * to rely on storage order.  *<p/>  * As with our PDFParser, this currently dumps urls at the bottom of the page  * and does not attempt to calculate the correct anchor text.  *<p/>  * TODO: integrate table markup  */
end_comment

begin_class
class|class
name|XPSPageContentHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|static
specifier|final
name|String
name|GLYPHS
init|=
literal|"Glyphs"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CANVAS
init|=
literal|"Canvas"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CLIP
init|=
literal|"Clip"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NULL_CLIP
init|=
literal|"NULL_CLIP"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|UNICODE_STRING
init|=
literal|"UnicodeString"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ORIGIN_X
init|=
literal|"OriginX"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ORIGIN_Y
init|=
literal|"OriginY"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BIDI_LEVEL
init|=
literal|"BidiLevel"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDICES
init|=
literal|"Indices"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"Name"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PATH
init|=
literal|"Path"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NAVIGATE_URI
init|=
literal|"FixedPage.NavigateUri"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|IMAGE_SOURCE
init|=
literal|"ImageSource"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|IMAGE_BRUSH
init|=
literal|"ImageBrush"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|AUTOMATION_PROPERITES_HELP_TEXT
init|=
literal|"AutomationProperties.HelpText"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|URL_DIV
init|=
literal|"urls"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DIV
init|=
literal|"div"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CLASS
init|=
literal|"class"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PAGE
init|=
literal|"page"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CANVAS_SAX
init|=
literal|"canvas"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|P
init|=
literal|"p"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HREF
init|=
literal|"href"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|A
init|=
literal|"a"
decl_stmt|;
specifier|private
specifier|final
name|XHTMLContentHandler
name|xhml
decl_stmt|;
comment|//path in zip file for an image rendered on this page
specifier|private
name|String
name|imageSourcePathInZip
init|=
literal|null
decl_stmt|;
comment|//embedded images sometimes include full path info of original image
specifier|private
name|String
name|originalLocationOnDrive
init|=
literal|null
decl_stmt|;
comment|//buffer for the glyph runs within a given canvas
comment|//in insertion order
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|GlyphRun
argument_list|>
argument_list|>
name|canvases
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|urls
init|=
operator|new
name|LinkedHashSet
argument_list|()
decl_stmt|;
specifier|private
name|Stack
argument_list|<
name|String
argument_list|>
name|canvasStack
init|=
operator|new
name|Stack
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Metadata
argument_list|>
name|embeddedInfos
decl_stmt|;
comment|//sort based on y coordinate of first element in each row
comment|//this requires every row to have at least one element
specifier|private
specifier|static
name|Comparator
argument_list|<
name|?
super|super
name|List
argument_list|<
name|GlyphRun
argument_list|>
argument_list|>
name|ROW_SORTER
init|=
operator|new
name|Comparator
argument_list|<
name|List
argument_list|<
name|GlyphRun
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|o1
parameter_list|,
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|originY
operator|<
name|o2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|originY
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|o1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|originY
operator|>
name|o2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|originY
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
specifier|public
name|XPSPageContentHandler
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Metadata
argument_list|>
name|embeddedInfos
parameter_list|)
block|{
name|this
operator|.
name|xhml
operator|=
name|xhtml
expr_stmt|;
name|this
operator|.
name|embeddedInfos
operator|=
name|embeddedInfos
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
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|CANVAS
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|String
name|clip
init|=
name|getVal
argument_list|(
name|CLIP
argument_list|,
name|atts
argument_list|)
decl_stmt|;
if|if
condition|(
name|clip
operator|==
literal|null
condition|)
block|{
name|canvasStack
operator|.
name|push
argument_list|(
name|NULL_CLIP
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|canvasStack
operator|.
name|push
argument_list|(
name|clip
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
elseif|else
if|if
condition|(
name|PATH
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|//for now just grab them and dump them at the end of the page.
name|String
name|url
init|=
name|getVal
argument_list|(
name|NAVIGATE_URI
argument_list|,
name|atts
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|urls
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|originalLocationOnDrive
operator|=
name|getVal
argument_list|(
name|AUTOMATION_PROPERITES_HELP_TEXT
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|IMAGE_BRUSH
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|imageSourcePathInZip
operator|=
name|getVal
argument_list|(
name|IMAGE_SOURCE
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|GLYPHS
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|name
init|=
literal|null
decl_stmt|;
name|Float
name|originX
init|=
literal|null
decl_stmt|;
name|Float
name|originY
init|=
literal|null
decl_stmt|;
name|String
name|unicodeString
init|=
literal|null
decl_stmt|;
name|Integer
name|bidilevel
init|=
literal|1
decl_stmt|;
name|String
name|indicesString
init|=
literal|null
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
name|lName
init|=
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|value
operator|=
operator|(
name|value
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|value
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|ORIGIN_X
operator|.
name|equals
argument_list|(
name|lName
argument_list|)
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|originX
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
block|}
elseif|else
if|if
condition|(
name|ORIGIN_Y
operator|.
name|equals
argument_list|(
name|lName
argument_list|)
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|originY
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
block|}
elseif|else
if|if
condition|(
name|UNICODE_STRING
operator|.
name|equals
argument_list|(
name|lName
argument_list|)
condition|)
block|{
name|unicodeString
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BIDI_LEVEL
operator|.
name|equals
argument_list|(
name|lName
argument_list|)
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|bidilevel
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
block|}
elseif|else
if|if
condition|(
name|INDICES
operator|.
name|equals
argument_list|(
name|lName
argument_list|)
condition|)
block|{
name|indicesString
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NAME
operator|.
name|equals
argument_list|(
name|lName
argument_list|)
condition|)
block|{
name|name
operator|=
name|value
expr_stmt|;
block|}
block|}
if|if
condition|(
name|unicodeString
operator|!=
literal|null
condition|)
block|{
name|originX
operator|=
operator|(
name|originX
operator|==
literal|null
operator|)
condition|?
name|Integer
operator|.
name|MIN_VALUE
else|:
name|originX
expr_stmt|;
name|originY
operator|=
operator|(
name|originY
operator|==
literal|null
operator|)
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|originY
expr_stmt|;
name|String
name|currentCanvasClip
init|=
operator|(
name|canvasStack
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|canvasStack
operator|.
name|peek
argument_list|()
else|:
name|NULL_CLIP
decl_stmt|;
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|runs
init|=
name|canvases
operator|.
name|get
argument_list|(
name|currentCanvasClip
argument_list|)
decl_stmt|;
if|if
condition|(
name|runs
operator|==
literal|null
condition|)
block|{
name|runs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|runs
operator|.
name|add
argument_list|(
operator|new
name|GlyphRun
argument_list|(
name|name
argument_list|,
name|originY
argument_list|,
name|originX
argument_list|,
name|unicodeString
argument_list|,
name|bidilevel
argument_list|,
name|indicesString
argument_list|)
argument_list|)
expr_stmt|;
name|canvases
operator|.
name|put
argument_list|(
name|currentCanvasClip
argument_list|,
name|runs
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
name|CANVAS
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|canvasStack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|canvasStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|PATH
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
comment|//this assumes that there cannot be a path within a path
comment|//not sure if this is true or if we need to track path depth
if|if
condition|(
name|imageSourcePathInZip
operator|!=
literal|null
condition|)
block|{
name|Metadata
name|m
init|=
name|embeddedInfos
operator|.
name|get
argument_list|(
name|imageSourcePathInZip
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
block|{
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|originalLocationOnDrive
operator|!=
literal|null
condition|)
block|{
name|String
name|val
init|=
name|m
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|m
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|,
name|originalLocationOnDrive
argument_list|)
expr_stmt|;
block|}
block|}
name|m
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|EMBEDDED_RESOURCE_TYPE
argument_list|,
name|TikaCoreProperties
operator|.
name|EmbeddedResourceType
operator|.
name|INLINE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|embeddedInfos
operator|.
name|put
argument_list|(
name|imageSourcePathInZip
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
comment|//reset
name|imageSourcePathInZip
operator|=
literal|null
expr_stmt|;
name|originalLocationOnDrive
operator|=
literal|null
expr_stmt|;
block|}
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
name|xhml
operator|.
name|startElement
argument_list|(
name|DIV
argument_list|,
name|CLASS
argument_list|,
name|PAGE
argument_list|)
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
name|writePage
argument_list|()
expr_stmt|;
name|xhml
operator|.
name|endElement
argument_list|(
name|DIV
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
name|void
name|writePage
parameter_list|()
throws|throws
name|SAXException
block|{
if|if
condition|(
name|canvases
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|GlyphRun
argument_list|>
argument_list|>
name|e
range|:
name|canvases
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|clip
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|runs
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|runs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|xhml
operator|.
name|startElement
argument_list|(
name|DIV
argument_list|,
name|CLASS
argument_list|,
name|CANVAS_SAX
argument_list|)
expr_stmt|;
comment|//a list of rows sorted by the y of the first element in each row
name|List
argument_list|<
name|List
argument_list|<
name|GlyphRun
argument_list|>
argument_list|>
name|rows
init|=
name|buildRows
argument_list|(
name|runs
argument_list|)
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|row
range|:
name|rows
control|)
block|{
name|writeRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|xhml
operator|.
name|endElement
argument_list|(
name|DIV
argument_list|)
expr_stmt|;
block|}
comment|//for now just dump the urls at the end of the page
comment|//At some point, we could link them back up to their
comment|//true anchor text.
if|if
condition|(
name|urls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhml
operator|.
name|startElement
argument_list|(
name|DIV
argument_list|,
name|CLASS
argument_list|,
name|URL_DIV
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|u
range|:
name|urls
control|)
block|{
name|xhml
operator|.
name|startElement
argument_list|(
name|A
argument_list|,
name|HREF
argument_list|,
name|u
argument_list|)
expr_stmt|;
name|xhml
operator|.
name|characters
argument_list|(
name|u
argument_list|)
expr_stmt|;
name|xhml
operator|.
name|endElement
argument_list|(
name|A
argument_list|)
expr_stmt|;
block|}
name|xhml
operator|.
name|endElement
argument_list|(
name|DIV
argument_list|)
expr_stmt|;
block|}
name|canvases
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|writeRow
parameter_list|(
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|row
parameter_list|)
throws|throws
name|SAXException
block|{
comment|/*         int rtl = 0;         int ltr = 0;         //if the row is entirely rtl, sort all as rtl         //otherwise sort ltr         for (GlyphRun r : row) {             //ignore directionality of pure spaces             if (r.unicodeString == null || r.unicodeString.trim().length() == 0) {                 continue;             }             if (r.direction == GlyphRun.DIRECTION.RTL) {                 rtl++;             } else {                 ltr++;             }         }         if (rtl> 0&& ltr == 0) {             Collections.sort(row, GlyphRun.RTL_COMPARATOR);         } else {             Collections.sort(row, GlyphRun.LTR_COMPARATOR);         }*/
name|xhml
operator|.
name|startElement
argument_list|(
name|P
argument_list|)
expr_stmt|;
for|for
control|(
name|GlyphRun
name|run
range|:
name|row
control|)
block|{
comment|//figure out if you need to add a space
name|xhml
operator|.
name|characters
argument_list|(
name|run
operator|.
name|unicodeString
argument_list|)
expr_stmt|;
block|}
name|xhml
operator|.
name|endElement
argument_list|(
name|P
argument_list|)
expr_stmt|;
block|}
comment|//returns a List of rows (where a row is a list of glyphruns)
comment|//the List is sorted in increasing order of the first y of each row
specifier|private
name|List
argument_list|<
name|List
argument_list|<
name|GlyphRun
argument_list|>
argument_list|>
name|buildRows
parameter_list|(
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|glyphRuns
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|GlyphRun
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|float
name|maxY
init|=
operator|-
literal|1.0f
decl_stmt|;
for|for
control|(
name|GlyphRun
name|glyphRun
range|:
name|glyphRuns
control|)
block|{
if|if
condition|(
name|rows
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|row
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|row
operator|.
name|add
argument_list|(
name|glyphRun
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|boolean
name|addedNewRow
init|=
literal|false
decl_stmt|;
comment|//can rely on the last row having the highest y
name|List
argument_list|<
name|GlyphRun
argument_list|>
name|row
init|=
name|rows
operator|.
name|get
argument_list|(
name|rows
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|//0.5 is a purely heuristic/magical number that should be derived
comment|//from the data, not made up. TODO: fix this
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|glyphRun
operator|.
name|originY
operator|-
name|row
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|originY
argument_list|)
operator|<
literal|0.5
condition|)
block|{
name|row
operator|.
name|add
argument_list|(
name|glyphRun
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|row
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|row
operator|.
name|add
argument_list|(
name|glyphRun
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|addedNewRow
operator|=
literal|true
expr_stmt|;
block|}
comment|//sort rows so that they are in ascending order of y
comment|//in most xps files in our test corpus, this is never triggered
comment|//because the runs are already ordered correctly
if|if
condition|(
name|maxY
operator|>
operator|-
literal|1.0f
operator|&&
name|addedNewRow
operator|&&
name|glyphRun
operator|.
name|originY
operator|<
name|maxY
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|rows
argument_list|,
name|ROW_SORTER
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|glyphRun
operator|.
name|originY
operator|>
name|maxY
condition|)
block|{
name|maxY
operator|=
name|glyphRun
operator|.
name|originY
expr_stmt|;
block|}
block|}
block|}
return|return
name|rows
return|;
block|}
specifier|private
specifier|static
name|String
name|getVal
parameter_list|(
name|String
name|localName
parameter_list|,
name|Attributes
name|atts
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
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|final
specifier|static
class|class
name|GlyphRun
block|{
specifier|private
enum|enum
name|DIRECTION
block|{
name|LTR
block|,
name|RTL
block|}
comment|//TODO: use name in conjunction with Frag information
comment|//to do a better job of extracting paragraph and table structure
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|float
name|originY
decl_stmt|;
specifier|private
specifier|final
name|float
name|originX
decl_stmt|;
comment|//not currently used, but could be used for bidi text calculations
specifier|private
specifier|final
name|String
name|unicodeString
decl_stmt|;
specifier|private
specifier|final
name|String
name|indicesString
decl_stmt|;
comment|//not currently used, but could be used for width calculations
comment|//not used yet
specifier|private
specifier|final
name|DIRECTION
name|direction
decl_stmt|;
specifier|private
name|GlyphRun
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|originY
parameter_list|,
name|float
name|originX
parameter_list|,
name|String
name|unicodeString
parameter_list|,
name|Integer
name|bidiLevel
parameter_list|,
name|String
name|indicesString
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|unicodeString
operator|=
name|unicodeString
expr_stmt|;
name|this
operator|.
name|originY
operator|=
name|originY
expr_stmt|;
name|this
operator|.
name|originX
operator|=
name|originX
expr_stmt|;
if|if
condition|(
name|bidiLevel
operator|==
literal|null
condition|)
block|{
name|direction
operator|=
name|DIRECTION
operator|.
name|LTR
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|bidiLevel
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|direction
operator|=
name|DIRECTION
operator|.
name|LTR
expr_stmt|;
block|}
else|else
block|{
name|direction
operator|=
name|DIRECTION
operator|.
name|RTL
expr_stmt|;
block|}
block|}
name|this
operator|.
name|indicesString
operator|=
name|indicesString
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
