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
name|dbf
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
name|detect
operator|.
name|AutoDetectReader
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This is a Tika wrapper around the DBFReader.  *<p>  * This reads many dbase3 file variants (not DBASE 7, yet!).  *<p>  * It caches the first 10 rows and then runs encoding dectection  * on the "character" cells.  */
end_comment

begin_class
specifier|public
class|class
name|DBFParser
extends|extends
name|AbstractParser
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DBF_VERSION_MIME_ATTRIBUTE
init|=
literal|"dbf_version"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ROWS_TO_BUFFER_FOR_CHARSET_DETECTION
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_CHARS_FOR_CHARSET_DETECTION
init|=
literal|20000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Charset
name|DEFAULT_CHARSET
init|=
name|StandardCharsets
operator|.
name|ISO_8859_1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-dbf"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
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
name|SUPPORTED_TYPES
return|;
block|}
annotation|@
name|Override
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
name|SAXException
throws|,
name|TikaException
block|{
name|DBFReader
name|reader
init|=
name|DBFReader
operator|.
name|open
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|DBFFileHeader
name|header
init|=
name|reader
operator|.
name|getHeader
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/x-dbf; "
operator|+
name|DBF_VERSION_MIME_ATTRIBUTE
operator|+
literal|"="
operator|+
name|header
operator|.
name|getVersion
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|//insert metadata here
name|Calendar
name|lastModified
init|=
name|header
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastModified
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|lastModified
argument_list|)
expr_stmt|;
block|}
comment|//buffer first X rows for charset detection
name|List
argument_list|<
name|DBFRow
argument_list|>
name|firstRows
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|DBFRow
name|row
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|row
operator|!=
literal|null
operator|&&
name|i
operator|++
operator|<
name|ROWS_TO_BUFFER_FOR_CHARSET_DETECTION
condition|)
block|{
name|firstRows
operator|.
name|add
argument_list|(
name|row
operator|.
name|deepCopy
argument_list|()
argument_list|)
expr_stmt|;
name|row
operator|=
name|reader
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|Charset
name|charset
init|=
name|getCharset
argument_list|(
name|firstRows
argument_list|,
name|header
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|charset
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"thead"
argument_list|)
expr_stmt|;
for|for
control|(
name|DBFColumnHeader
name|col
range|:
name|header
operator|.
name|getCols
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"th"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|col
operator|.
name|name
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"th"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"thead"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tbody"
argument_list|)
expr_stmt|;
comment|//now write cached rows
while|while
condition|(
name|firstRows
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DBFRow
name|cachedRow
init|=
name|firstRows
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|writeRow
argument_list|(
name|cachedRow
argument_list|,
name|charset
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
comment|//now continue with rest
while|while
condition|(
name|row
operator|!=
literal|null
condition|)
block|{
name|writeRow
argument_list|(
name|row
argument_list|,
name|charset
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
name|row
operator|=
name|reader
operator|.
name|next
argument_list|()
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
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Charset
name|getCharset
parameter_list|(
name|List
argument_list|<
name|DBFRow
argument_list|>
name|firstRows
parameter_list|,
name|DBFFileHeader
name|header
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
comment|//TODO: potentially use codepage info in the header
name|Charset
name|charset
init|=
name|DEFAULT_CHARSET
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
for|for
control|(
name|DBFRow
name|row
range|:
name|firstRows
control|)
block|{
for|for
control|(
name|DBFCell
name|cell
range|:
name|row
operator|.
name|cells
control|)
block|{
if|if
condition|(
name|cell
operator|.
name|getColType
argument_list|()
operator|.
name|equals
argument_list|(
name|DBFColumnHeader
operator|.
name|ColType
operator|.
name|C
argument_list|)
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
name|cell
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|bos
operator|.
name|size
argument_list|()
operator|>
name|MAX_CHARS_FOR_CHARSET_DETECTION
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
name|byte
index|[]
name|bytes
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|>
literal|20
condition|)
block|{
name|charset
operator|=
operator|new
name|AutoDetectReader
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|bytes
argument_list|)
argument_list|)
operator|.
name|getCharset
argument_list|()
expr_stmt|;
block|}
return|return
name|charset
return|;
block|}
specifier|private
name|void
name|writeRow
parameter_list|(
name|DBFRow
name|row
parameter_list|,
name|Charset
name|charset
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
for|for
control|(
name|DBFCell
name|cell
range|:
name|row
operator|.
name|cells
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|cell
operator|.
name|getString
argument_list|(
name|charset
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

