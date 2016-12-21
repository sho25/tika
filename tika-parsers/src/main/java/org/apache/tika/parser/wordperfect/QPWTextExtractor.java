begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright 2015-2016 Norconex Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|wordperfect
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|DirectoryNode
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
name|Office
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
name|QuattroPro
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Extracts text from a Quattro Pro document according to QPW v9 File Format.  * This format appears to be compatible with more recent versions too.  * @author Pascal Essiembre  */
end_comment

begin_class
class|class
name|QPWTextExtractor
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|QPWTextExtractor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OLE_DOCUMENT_NAME
init|=
literal|"NativeContent_MAIN"
decl_stmt|;
specifier|private
enum|enum
name|Extractor
block|{
name|IGNORE
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|ctx
operator|.
name|in
operator|.
name|skipWPByte
argument_list|(
name|ctx
operator|.
name|bodyLength
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|BOF
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|ctx
operator|.
name|metadata
operator|.
name|set
argument_list|(
name|QuattroPro
operator|.
name|ID
argument_list|,
name|ctx
operator|.
name|in
operator|.
name|readWPString
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|metadata
operator|.
name|set
argument_list|(
name|QuattroPro
operator|.
name|VERSION
argument_list|,
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|metadata
operator|.
name|set
argument_list|(
name|QuattroPro
operator|.
name|BUILD
argument_list|,
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// Last saved bits
name|ctx
operator|.
name|metadata
operator|.
name|set
argument_list|(
name|QuattroPro
operator|.
name|LOWEST_VERSION
argument_list|,
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|metadata
operator|.
name|set
argument_list|(
name|Office
operator|.
name|PAGE_COUNT
argument_list|,
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|in
operator|.
name|skipWPByte
argument_list|(
name|ctx
operator|.
name|bodyLength
operator|-
literal|14
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|USER
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|ctx
operator|.
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|,
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIER
argument_list|,
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|EXT_LINK
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// index
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// page first
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// page last
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|STRING_TABLE
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|long
name|entries
init|=
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
expr_stmt|;
comment|// Total used
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
expr_stmt|;
comment|// Total saved
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|entries
condition|;
name|i
operator|++
control|)
block|{
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|,
name|BOS
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// sheet #
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// first col index
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// last col index
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
expr_stmt|;
comment|// first row index
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
expr_stmt|;
comment|// last row index
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// format
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// flags
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|SHEET_HEADFOOT
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// flag
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|FORMULA_STRING_VALUE
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// column
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
expr_stmt|;
comment|// row
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|CGENERICLABEL
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// column
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
expr_stmt|;
comment|// row
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// format index
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|,
name|CCOMMENT
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|ctx
operator|.
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
comment|// column
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
expr_stmt|;
comment|// row
name|ctx
operator|.
name|in
operator|.
name|readWPLong
argument_list|()
expr_stmt|;
comment|// flag
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
comment|// author name
name|ctx
operator|.
name|xhtml
operator|.
name|characters
argument_list|(
name|getQstrLabel
argument_list|(
name|ctx
operator|.
name|in
argument_list|)
argument_list|)
expr_stmt|;
comment|// comment
block|}
block|}
block|,
comment|// Use to print out a chunk
name|DEBUG
block|{
annotation|@
name|Override
specifier|public
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"REC ("
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|ctx
operator|.
name|type
argument_list|)
operator|+
literal|"/"
operator|+
name|ctx
operator|.
name|bodyLength
operator|+
literal|"):"
operator|+
name|ctx
operator|.
name|in
operator|.
name|readWPString
argument_list|(
name|ctx
operator|.
name|bodyLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|;
specifier|public
specifier|abstract
name|void
name|extract
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
function_decl|;
block|}
comment|// Holds extractors for each record types we are interested in.
comment|// All record types not defined here will be skipped.
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Extractor
argument_list|>
name|EXTRACTORS
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
comment|//--- Global Records ---
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0001
argument_list|,
name|Extractor
operator|.
name|BOF
argument_list|)
expr_stmt|;
comment|// Beginning of file
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0005
argument_list|,
name|Extractor
operator|.
name|USER
argument_list|)
expr_stmt|;
comment|// User
comment|//--- Notebook Records ---
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0403
argument_list|,
name|Extractor
operator|.
name|EXT_LINK
argument_list|)
expr_stmt|;
comment|// External link
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0407
argument_list|,
name|Extractor
operator|.
name|STRING_TABLE
argument_list|)
expr_stmt|;
comment|// String table
comment|//--- Sheet Records ---
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0601
argument_list|,
name|Extractor
operator|.
name|BOS
argument_list|)
expr_stmt|;
comment|// Beginning of sheet
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0605
argument_list|,
name|Extractor
operator|.
name|SHEET_HEADFOOT
argument_list|)
expr_stmt|;
comment|// Sheet header
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0606
argument_list|,
name|Extractor
operator|.
name|SHEET_HEADFOOT
argument_list|)
expr_stmt|;
comment|// Sheet footer
comment|//--- Cells ---
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0c02
argument_list|,
name|Extractor
operator|.
name|FORMULA_STRING_VALUE
argument_list|)
expr_stmt|;
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0c72
argument_list|,
name|Extractor
operator|.
name|CGENERICLABEL
argument_list|)
expr_stmt|;
name|EXTRACTORS
operator|.
name|put
argument_list|(
literal|0x0c80
argument_list|,
name|Extractor
operator|.
name|CCOMMENT
argument_list|)
expr_stmt|;
block|}
class|class
name|Context
block|{
specifier|private
specifier|final
name|WPInputStream
name|in
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
name|int
name|type
decl_stmt|;
specifier|private
name|int
name|bodyLength
decl_stmt|;
specifier|public
name|Context
parameter_list|(
name|WPInputStream
name|in
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
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
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
specifier|public
name|void
name|extract
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|POIFSFileSystem
name|pfs
init|=
operator|new
name|POIFSFileSystem
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|DirectoryNode
name|rootNode
init|=
name|pfs
operator|.
name|getRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|rootNode
operator|==
literal|null
operator|||
operator|!
name|rootNode
operator|.
name|hasEntry
argument_list|(
name|OLE_DOCUMENT_NAME
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unsupported QuattroPro file format. "
operator|+
literal|"Looking for OLE entry \""
operator|+
name|OLE_DOCUMENT_NAME
operator|+
literal|"\". Found: "
operator|+
name|rootNode
operator|.
name|getEntryNames
argument_list|()
argument_list|)
throw|;
block|}
comment|//TODO shall we validate and throw warning/error if the file does not
comment|//start with a BOF and ends with a EOF?
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
try|try
init|(
name|WPInputStream
name|in
init|=
operator|new
name|WPInputStream
argument_list|(
name|pfs
operator|.
name|createDocumentInputStream
argument_list|(
name|OLE_DOCUMENT_NAME
argument_list|)
argument_list|)
init|)
block|{
name|Context
name|ctx
init|=
operator|new
name|Context
argument_list|(
name|in
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
while|while
condition|(
name|hasNext
argument_list|(
name|in
argument_list|)
condition|)
block|{
name|ctx
operator|.
name|type
operator|=
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
name|ctx
operator|.
name|bodyLength
operator|=
name|in
operator|.
name|readWPShort
argument_list|()
expr_stmt|;
name|Extractor
name|extractor
init|=
name|EXTRACTORS
operator|.
name|get
argument_list|(
name|ctx
operator|.
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|extractor
operator|!=
literal|null
condition|)
block|{
name|extractor
operator|.
name|extract
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Use DEBUG to find out what we are ignoring
comment|//                    Extractor.DEBUG.extract(ctx);
name|Extractor
operator|.
name|IGNORE
operator|.
name|extract
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|hasNext
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|in
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
return|;
block|}
finally|finally
block|{
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getQstrLabel
parameter_list|(
name|WPInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|// QSTR
name|int
name|count
init|=
name|in
operator|.
name|readWPShort
argument_list|()
decl_stmt|;
name|in
operator|.
name|readWPByte
argument_list|()
expr_stmt|;
comment|// string type
name|char
index|[]
name|text
init|=
operator|new
name|char
index|[
name|count
operator|+
literal|1
index|]
decl_stmt|;
name|text
index|[
literal|0
index|]
operator|=
name|in
operator|.
name|readWPChar
argument_list|()
expr_stmt|;
comment|// QSTRLABEL
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|text
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|in
operator|.
name|readWPChar
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|text
argument_list|)
return|;
block|}
block|}
end_class

end_unit

