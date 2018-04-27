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
name|sas
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
name|com
operator|.
name|epam
operator|.
name|parso
operator|.
name|Column
import|;
end_import

begin_import
import|import
name|com
operator|.
name|epam
operator|.
name|parso
operator|.
name|DataWriterUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|epam
operator|.
name|parso
operator|.
name|SasFileProperties
import|;
end_import

begin_import
import|import
name|com
operator|.
name|epam
operator|.
name|parso
operator|.
name|SasFileReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|epam
operator|.
name|parso
operator|.
name|impl
operator|.
name|SasFileReaderImpl
import|;
end_import

begin_comment
comment|/**  * Processes the SAS7BDAT data columnar database file used by SAS and   *  other similar languages.  */
end_comment

begin_class
specifier|public
class|class
name|SAS7BDATParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2775485539937983150L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_SAS7BDAT
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-sas-data"
argument_list|)
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
name|TYPE_SAS7BDAT
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
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|TYPE_SAS7BDAT
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
name|SasFileReader
name|sas
init|=
operator|new
name|SasFileReaderImpl
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|SasFileProperties
name|props
init|=
name|sas
operator|.
name|getSasFileProperties
argument_list|()
decl_stmt|;
comment|// Record the interesting parts of the file's metadata
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|props
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|props
operator|.
name|getDateCreated
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|props
operator|.
name|getDateModified
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO What about these?
comment|/* u64 - false compressionMethod - null endianness - 1 encoding - windows-1252 sessionEncoding - null fileType - DATA sasRelease - 9.0101M3 serverType - XP_PRO osName -  osType -  headerLength - 1024 pageLength - 8192 pageCount - 1 rowLength - 96 rowCount - 31 mixPageRowCount - 69 columnsCount - 5 */
comment|// TODO Should we output more Column info as metadata?
comment|/* 5 Columns defined:  1 - A   Label: A   Format: $58.   Size 58 of java.lang.String  2 - B   Label: B   Format:    Size 8 of java.lang.Number  3 - C   Label: C   Format: DATE8.   Size 8 of java.lang.Number  4 - D   Label: D   Format: DATETIME17.   Size 8 of java.lang.Number  5 - E   Label: E   Format:    Size 8 of java.lang.Number */
comment|// Output file contents as a table
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|props
operator|.
name|getName
argument_list|()
argument_list|)
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
name|newline
argument_list|()
expr_stmt|;
comment|// Do the column headings
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
for|for
control|(
name|Column
name|c
range|:
name|sas
operator|.
name|getColumns
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"th"
argument_list|,
literal|"title"
argument_list|,
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|c
operator|.
name|getLabel
argument_list|()
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
literal|"tr"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
comment|// Process each row in turn
name|Object
index|[]
name|row
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|row
operator|=
name|sas
operator|.
name|readNext
argument_list|()
operator|)
operator|!=
literal|null
condition|)
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
name|String
name|val
range|:
name|DataWriterUtil
operator|.
name|getRowValues
argument_list|(
name|sas
operator|.
name|getColumns
argument_list|()
argument_list|,
name|row
argument_list|)
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"td"
argument_list|,
name|val
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
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
comment|// Finish
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
block|}
end_class

end_unit

