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
name|dwg
package|;
end_package

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
name|Collections
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
name|util
operator|.
name|IOUtils
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
name|util
operator|.
name|StringUtil
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
name|EndianUtils
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
name|mime
operator|.
name|MediaType
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
name|AbstractParser
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

begin_comment
comment|/**  * DWG (CAD Drawing) parser. This is a very basic parser, which just  *  looks for bits of the headers.  * Note that we use Apache POI for various parts of the processing, as  *  lots of the low level string/int/short concepts are the same.  */
end_comment

begin_class
specifier|public
class|class
name|DWGParser
extends|extends
name|AbstractParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7744232583079169119L
decl_stmt|;
specifier|private
specifier|static
name|MediaType
name|TYPE
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"vnd.dwg"
argument_list|)
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|TYPE
argument_list|)
return|;
block|}
comment|/** The order of the fields in the header */
specifier|private
specifier|static
specifier|final
name|Property
index|[]
name|HEADER_PROPERTIES_ENTRIES
init|=
block|{
name|TikaCoreProperties
operator|.
name|TITLE
block|,
name|TikaCoreProperties
operator|.
name|SUBJECT
block|,
name|TikaCoreProperties
operator|.
name|AUTHOR
block|,
name|TikaCoreProperties
operator|.
name|KEYWORDS
block|,
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|COMMENTS
argument_list|)
block|,
name|TikaCoreProperties
operator|.
name|LAST_AUTHOR
block|,
literal|null
block|,
comment|// Unknown?
name|TikaCoreProperties
operator|.
name|RELATION
block|,
comment|// Hyperlink
block|}
decl_stmt|;
comment|/** For the 2000 file, they're indexed */
specifier|private
specifier|static
specifier|final
name|Property
index|[]
name|HEADER_2000_PROPERTIES_ENTRIES
init|=
block|{
literal|null
block|,
name|TikaCoreProperties
operator|.
name|RELATION
block|,
comment|// 0x01
name|TikaCoreProperties
operator|.
name|TITLE
block|,
comment|// 0x02
name|TikaCoreProperties
operator|.
name|SUBJECT
block|,
comment|// 0x03
name|TikaCoreProperties
operator|.
name|AUTHOR
block|,
comment|// 0x04
literal|null
block|,
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|COMMENTS
argument_list|)
block|,
comment|// 0x06
name|TikaCoreProperties
operator|.
name|KEYWORDS
block|,
comment|// 0x07
name|TikaCoreProperties
operator|.
name|LAST_AUTHOR
block|,
comment|// 0x08
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HEADER_2000_PROPERTIES_MARKER_STR
init|=
literal|"DWGPROPS COOKIE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|HEADER_2000_PROPERTIES_MARKER
init|=
operator|new
name|byte
index|[
name|HEADER_2000_PROPERTIES_MARKER_STR
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
static|static
block|{
name|StringUtil
operator|.
name|putCompressedUnicode
argument_list|(
name|HEADER_2000_PROPERTIES_MARKER_STR
argument_list|,
name|HEADER_2000_PROPERTIES_MARKER
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**       * How far to skip after the last standard property, before      *  we find any custom properties that might be there.      */
specifier|private
specifier|static
specifier|final
name|int
name|CUSTOM_PROPERTIES_SKIP
init|=
literal|20
decl_stmt|;
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
comment|// First up, which version of the format are we handling?
name|byte
index|[]
name|header
init|=
operator|new
name|byte
index|[
literal|128
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|header
argument_list|)
expr_stmt|;
name|String
name|version
init|=
operator|new
name|String
argument_list|(
name|header
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|,
literal|"US-ASCII"
argument_list|)
decl_stmt|;
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|version
operator|.
name|equals
argument_list|(
literal|"AC1015"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|TYPE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipTo2000PropertyInfoSection
argument_list|(
name|stream
argument_list|,
name|header
argument_list|)
condition|)
block|{
name|get2000Props
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|version
operator|.
name|equals
argument_list|(
literal|"AC1018"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|TYPE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipToPropertyInfoSection
argument_list|(
name|stream
argument_list|,
name|header
argument_list|)
condition|)
block|{
name|get2004Props
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|version
operator|.
name|equals
argument_list|(
literal|"AC1021"
argument_list|)
operator|||
name|version
operator|.
name|equals
argument_list|(
literal|"AC1024"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|TYPE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipToPropertyInfoSection
argument_list|(
name|stream
argument_list|,
name|header
argument_list|)
condition|)
block|{
name|get2007and2010Props
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unsupported AutoCAD drawing version: "
operator|+
name|version
argument_list|)
throw|;
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * Stored as US-ASCII      */
specifier|private
name|void
name|get2004Props
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
comment|// Standard properties
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|HEADER_PROPERTIES_ENTRIES
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|headerValue
init|=
name|read2004String
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|handleHeader
argument_list|(
name|i
argument_list|,
name|headerValue
argument_list|,
name|metadata
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
comment|// Custom properties
name|int
name|customCount
init|=
name|skipToCustomProperties
argument_list|(
name|stream
argument_list|)
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
name|customCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|propName
init|=
name|read2004String
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|String
name|propValue
init|=
name|read2004String
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|propName
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|propValue
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|String
name|read2004String
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|int
name|stringLen
init|=
name|EndianUtils
operator|.
name|readUShortLE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|byte
index|[]
name|stringData
init|=
operator|new
name|byte
index|[
name|stringLen
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|stringData
argument_list|)
expr_stmt|;
comment|// Often but not always null terminated
if|if
condition|(
name|stringData
index|[
name|stringLen
operator|-
literal|1
index|]
operator|==
literal|0
condition|)
block|{
name|stringLen
operator|--
expr_stmt|;
block|}
name|String
name|value
init|=
name|StringUtil
operator|.
name|getFromCompressedUnicode
argument_list|(
name|stringData
argument_list|,
literal|0
argument_list|,
name|stringLen
argument_list|)
decl_stmt|;
return|return
name|value
return|;
block|}
comment|/**      * Stored as UCS2, so 16 bit "unicode"      */
specifier|private
name|void
name|get2007and2010Props
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
comment|// Standard properties
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|HEADER_PROPERTIES_ENTRIES
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|headerValue
init|=
name|read2007and2010String
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|handleHeader
argument_list|(
name|i
argument_list|,
name|headerValue
argument_list|,
name|metadata
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
comment|// Custom properties
name|int
name|customCount
init|=
name|skipToCustomProperties
argument_list|(
name|stream
argument_list|)
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
name|customCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|propName
init|=
name|read2007and2010String
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|String
name|propValue
init|=
name|read2007and2010String
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|propName
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|propValue
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|String
name|read2007and2010String
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|int
name|stringLen
init|=
name|EndianUtils
operator|.
name|readUShortLE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|byte
index|[]
name|stringData
init|=
operator|new
name|byte
index|[
name|stringLen
operator|*
literal|2
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|stringData
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|StringUtil
operator|.
name|getFromUnicodeLE
argument_list|(
name|stringData
argument_list|)
decl_stmt|;
comment|// Some strings are null terminated
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|0
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
specifier|private
name|void
name|get2000Props
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
name|int
name|propCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|propCount
operator|<
literal|30
condition|)
block|{
name|int
name|propIdx
init|=
name|EndianUtils
operator|.
name|readUShortLE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|EndianUtils
operator|.
name|readUShortLE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|int
name|valueType
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|propIdx
operator|==
literal|0x28
condition|)
block|{
comment|// This one seems not to follow the pattern
name|length
operator|=
literal|0x19
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propIdx
operator|==
literal|90
condition|)
block|{
comment|// We think this means the end of properties
break|break;
block|}
name|byte
index|[]
name|value
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|valueType
operator|==
literal|0x1e
condition|)
block|{
comment|// Normal string, good
name|String
name|val
init|=
name|StringUtil
operator|.
name|getFromCompressedUnicode
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
decl_stmt|;
comment|// Is it one we can look up by index?
if|if
condition|(
name|propIdx
operator|<
name|HEADER_2000_PROPERTIES_ENTRIES
operator|.
name|length
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|HEADER_2000_PROPERTIES_ENTRIES
index|[
name|propIdx
index|]
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propIdx
operator|==
literal|0x012c
condition|)
block|{
name|int
name|splitAt
init|=
name|val
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitAt
operator|>
operator|-
literal|1
condition|)
block|{
name|String
name|propName
init|=
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|splitAt
argument_list|)
decl_stmt|;
name|String
name|propVal
init|=
name|val
operator|.
name|substring
argument_list|(
name|splitAt
operator|+
literal|1
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|propName
argument_list|,
name|propVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// No idea...
block|}
name|propCount
operator|++
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleHeader
parameter_list|(
name|int
name|headerNumber
parameter_list|,
name|String
name|value
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|Property
name|headerProp
init|=
name|HEADER_PROPERTIES_ENTRIES
index|[
name|headerNumber
index|]
decl_stmt|;
if|if
condition|(
name|headerProp
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|headerProp
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Grab the offset, then skip there      */
specifier|private
name|boolean
name|skipToPropertyInfoSection
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|byte
index|[]
name|header
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
comment|// The offset is stored in the header from 0x20 onwards
name|long
name|offsetToSection
init|=
name|EndianUtils
operator|.
name|getLongLE
argument_list|(
name|header
argument_list|,
literal|0x20
argument_list|)
decl_stmt|;
name|long
name|toSkip
init|=
name|offsetToSection
operator|-
name|header
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|offsetToSection
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
while|while
condition|(
name|toSkip
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|skip
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|min
argument_list|(
operator|(
name|int
operator|)
name|toSkip
argument_list|,
literal|0x4000
argument_list|)
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|skip
argument_list|)
expr_stmt|;
name|toSkip
operator|-=
name|skip
operator|.
name|length
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * We think it can be anywhere...      */
specifier|private
name|boolean
name|skipTo2000PropertyInfoSection
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|byte
index|[]
name|header
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|val
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|val
operator|!=
operator|-
literal|1
condition|)
block|{
name|val
operator|=
name|stream
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|val
operator|==
name|HEADER_2000_PROPERTIES_MARKER
index|[
literal|0
index|]
condition|)
block|{
name|boolean
name|going
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|HEADER_2000_PROPERTIES_MARKER
operator|.
name|length
operator|&&
name|going
condition|;
name|i
operator|++
control|)
block|{
name|val
operator|=
name|stream
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|val
operator|!=
name|HEADER_2000_PROPERTIES_MARKER
index|[
name|i
index|]
condition|)
name|going
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|going
condition|)
block|{
comment|// Bingo, found it
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|int
name|skipToCustomProperties
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
comment|// There should be 4 zero bytes next
name|byte
index|[]
name|padding
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|padding
argument_list|)
expr_stmt|;
if|if
condition|(
name|padding
index|[
literal|0
index|]
operator|==
literal|0
operator|&&
name|padding
index|[
literal|1
index|]
operator|==
literal|0
operator|&&
name|padding
index|[
literal|2
index|]
operator|==
literal|0
operator|&&
name|padding
index|[
literal|3
index|]
operator|==
literal|0
condition|)
block|{
comment|// Looks hopeful, skip on
name|padding
operator|=
operator|new
name|byte
index|[
name|CUSTOM_PROPERTIES_SKIP
index|]
expr_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|padding
argument_list|)
expr_stmt|;
comment|// We should now have the count
name|int
name|count
init|=
name|EndianUtils
operator|.
name|readUShortLE
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// Sanity check it
if|if
condition|(
name|count
operator|>
literal|0
operator|&&
name|count
operator|<
literal|0x7f
condition|)
block|{
comment|// Looks plausible
return|return
name|count
return|;
block|}
else|else
block|{
comment|// No properties / count is too high to trust
return|return
literal|0
return|;
block|}
block|}
else|else
block|{
comment|// No padding. That probably means no custom props
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

