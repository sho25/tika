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
name|xml
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|CloseShieldInputStream
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
name|OfficeOpenXMLCore
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
name|OfficeOpenXMLExtended
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
name|parser
operator|.
name|xml
operator|.
name|ElementMetadataHandler
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
name|EmbeddedContentHandler
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
name|OfflineContentHandler
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
name|TaggedContentHandler
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
name|TeeContentHandler
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
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|XMLReaderUtils
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractXML2003Parser
extends|extends
name|AbstractParser
block|{
specifier|final
specifier|static
name|String
name|MS_OFFICE_PROPERTIES_URN
init|=
literal|"urn:schemas-microsoft-com:office:office"
decl_stmt|;
specifier|final
specifier|static
name|String
name|MS_DOC_PROPERTIES_URN
init|=
literal|"urn:schemas-microsoft-com:office:office"
decl_stmt|;
specifier|final
specifier|static
name|String
name|MS_SPREADSHEET_URN
init|=
literal|"urn:schemas-microsoft-com:office:spreadsheet"
decl_stmt|;
specifier|final
specifier|static
name|String
name|MS_VML_URN
init|=
literal|"urn:schemas-microsoft-com:vml"
decl_stmt|;
specifier|final
specifier|static
name|String
name|WORD_ML_URL
init|=
literal|"http://schemas.microsoft.com/office/word/2003/wordml"
decl_stmt|;
specifier|final
specifier|static
name|Attributes
name|EMPTY_ATTRS
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
specifier|final
specifier|static
name|String
name|DOCUMENT_PROPERTIES
init|=
literal|"DocumentProperties"
decl_stmt|;
specifier|final
specifier|static
name|String
name|PICT
init|=
literal|"pict"
decl_stmt|;
specifier|final
specifier|static
name|String
name|BIN_DATA
init|=
literal|"binData"
decl_stmt|;
specifier|final
specifier|static
name|String
name|A
init|=
literal|"a"
decl_stmt|;
specifier|final
specifier|static
name|String
name|BODY
init|=
literal|"body"
decl_stmt|;
specifier|final
specifier|static
name|String
name|BR
init|=
literal|"br"
decl_stmt|;
specifier|final
specifier|static
name|String
name|CDATA
init|=
literal|"cdata"
decl_stmt|;
specifier|final
specifier|static
name|String
name|DIV
init|=
literal|"div"
decl_stmt|;
specifier|final
specifier|static
name|String
name|HREF
init|=
literal|"href"
decl_stmt|;
specifier|final
specifier|static
name|String
name|IMG
init|=
literal|"img"
decl_stmt|;
specifier|final
specifier|static
name|String
name|P
init|=
literal|"p"
decl_stmt|;
specifier|final
specifier|static
name|String
name|TD
init|=
literal|"td"
decl_stmt|;
specifier|final
specifier|static
name|String
name|TR
init|=
literal|"tr"
decl_stmt|;
specifier|final
specifier|static
name|String
name|TABLE
init|=
literal|"table"
decl_stmt|;
specifier|final
specifier|static
name|String
name|TBODY
init|=
literal|"tbody"
decl_stmt|;
specifier|final
specifier|static
name|String
name|HLINK
init|=
literal|"hlink"
decl_stmt|;
specifier|final
specifier|static
name|String
name|HLINK_DEST
init|=
literal|"dest"
decl_stmt|;
specifier|final
specifier|static
name|String
name|NAME_ATTR
init|=
literal|"name"
decl_stmt|;
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
specifier|static
name|ContentHandler
name|getMSPropertiesHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|property
parameter_list|,
name|String
name|element
parameter_list|)
block|{
return|return
operator|new
name|ElementMetadataHandler
argument_list|(
name|MS_DOC_PROPERTIES_URN
argument_list|,
name|element
argument_list|,
name|metadata
argument_list|,
name|property
argument_list|)
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
name|setContentType
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
specifier|final
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
name|TaggedContentHandler
name|tagged
init|=
operator|new
name|TaggedContentHandler
argument_list|(
name|xhtml
argument_list|)
decl_stmt|;
try|try
block|{
comment|//need to get new SAXParser because
comment|//an attachment might require another SAXParser
comment|//mid-parse
name|XMLReaderUtils
operator|.
name|getSAXParser
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
operator|new
name|OfflineContentHandler
argument_list|(
operator|new
name|EmbeddedContentHandler
argument_list|(
name|getContentHandler
argument_list|(
name|tagged
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|tagged
operator|.
name|throwIfCauseOf
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"XML parse error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ContentHandler
name|getContentHandler
parameter_list|(
name|ContentHandler
name|ch
parameter_list|,
name|Metadata
name|md
parameter_list|,
name|ParseContext
name|context
parameter_list|)
block|{
comment|//ContentHandler is not currently used, but leave that as an option for
comment|//potential future additions
return|return
operator|new
name|TeeContentHandler
argument_list|(
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
literal|"Title"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|,
literal|"Author"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|Office
operator|.
name|LAST_AUTHOR
argument_list|,
literal|"LastAuthor"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|REVISION
argument_list|,
literal|"Revision"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|TOTAL_TIME
argument_list|,
literal|"TotalTime"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
literal|"Created"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|Office
operator|.
name|SAVE_DATE
argument_list|,
literal|"LastSaved"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|Office
operator|.
name|PAGE_COUNT
argument_list|,
literal|"Pages"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|Office
operator|.
name|WORD_COUNT
argument_list|,
literal|"Words"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|Office
operator|.
name|CHARACTER_COUNT
argument_list|,
literal|"Characters"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|Office
operator|.
name|CHARACTER_COUNT_WITH_SPACES
argument_list|,
literal|"CharactersWithSpaces"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|COMPANY
argument_list|,
literal|"Company"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|Office
operator|.
name|LINE_COUNT
argument_list|,
literal|"Lines"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|Office
operator|.
name|PARAGRAPH_COUNT
argument_list|,
literal|"Paragraphs"
argument_list|)
argument_list|,
name|getMSPropertiesHandler
argument_list|(
name|md
argument_list|,
name|OfficeOpenXMLCore
operator|.
name|VERSION
argument_list|,
literal|"Version"
argument_list|)
argument_list|)
return|;
block|}
specifier|abstract
specifier|protected
name|void
name|setContentType
parameter_list|(
name|Metadata
name|contentType
parameter_list|)
function_decl|;
block|}
end_class

end_unit

