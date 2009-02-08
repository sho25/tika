begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|hdgf
operator|.
name|extractor
operator|.
name|VisioTextExtractor
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
name|hpsf
operator|.
name|DocumentSummaryInformation
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
name|hpsf
operator|.
name|MarkUnsupportedException
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
name|hpsf
operator|.
name|NoPropertySetStreamException
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
name|hpsf
operator|.
name|PropertySet
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
name|hpsf
operator|.
name|SummaryInformation
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
name|hpsf
operator|.
name|UnexpectedPropertySetTypeException
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
name|hslf
operator|.
name|extractor
operator|.
name|PowerPointExtractor
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
name|WordExtractor
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
name|DocumentEntry
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
name|DocumentInputStream
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
name|Parser
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
comment|/**  * Defines a Microsoft document content extractor.  */
end_comment

begin_class
specifier|public
class|class
name|OfficeParser
implements|implements
name|Parser
block|{
specifier|private
specifier|static
specifier|final
name|String
name|SUMMARY_INFORMATION
init|=
name|SummaryInformation
operator|.
name|DEFAULT_STREAM_NAME
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCUMENT_SUMMARY_INFORMATION
init|=
name|DocumentSummaryInformation
operator|.
name|DEFAULT_STREAM_NAME
decl_stmt|;
comment|/**      * Extracts properties and text from an MS Document input stream      */
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
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
name|boolean
name|outlookExtracted
init|=
literal|false
decl_stmt|;
name|POIFSFileSystem
name|filesystem
init|=
operator|new
name|POIFSFileSystem
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|entries
init|=
name|filesystem
operator|.
name|getRoot
argument_list|()
operator|.
name|getEntries
argument_list|()
decl_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|entries
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|entry
operator|instanceof
name|DocumentEntry
operator|)
condition|)
block|{
comment|// Skip directory entries
block|}
elseif|else
if|if
condition|(
name|SUMMARY_INFORMATION
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|DOCUMENT_SUMMARY_INFORMATION
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|parse
argument_list|(
operator|(
name|DocumentEntry
operator|)
name|entry
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"WordDocument"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|setType
argument_list|(
name|metadata
argument_list|,
literal|"application/msword"
argument_list|)
expr_stmt|;
name|WordExtractor
name|extractor
init|=
operator|new
name|WordExtractor
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|paragraph
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
name|paragraph
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"PowerPoint Document"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|setType
argument_list|(
name|metadata
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
expr_stmt|;
name|PowerPointExtractor
name|extractor
init|=
operator|new
name|PowerPointExtractor
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|extractor
operator|.
name|getText
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Workbook"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|setType
argument_list|(
name|metadata
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
expr_stmt|;
operator|new
name|ExcelExtractor
argument_list|()
operator|.
name|parse
argument_list|(
name|filesystem
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"VisioDocument"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|setType
argument_list|(
name|metadata
argument_list|,
literal|"application/vnd.visio"
argument_list|)
expr_stmt|;
name|VisioTextExtractor
name|extractor
init|=
operator|new
name|VisioTextExtractor
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|text
range|:
name|extractor
operator|.
name|getAllText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|outlookExtracted
operator|&&
name|name
operator|.
name|startsWith
argument_list|(
literal|"__substg1.0_"
argument_list|)
condition|)
block|{
comment|// TODO: Cleaner mechanism for detecting Outlook
name|outlookExtracted
operator|=
literal|true
expr_stmt|;
name|setType
argument_list|(
name|metadata
argument_list|,
literal|"application/vnd.ms-outlook"
argument_list|)
expr_stmt|;
operator|new
name|OutlookExtractor
argument_list|(
name|filesystem
argument_list|)
operator|.
name|parse
argument_list|(
name|xhtml
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|DocumentEntry
name|entry
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
try|try
block|{
name|PropertySet
name|properties
init|=
operator|new
name|PropertySet
argument_list|(
operator|new
name|DocumentInputStream
argument_list|(
name|entry
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|properties
operator|.
name|isSummaryInformation
argument_list|()
condition|)
block|{
name|parse
argument_list|(
operator|new
name|SummaryInformation
argument_list|(
name|properties
argument_list|)
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|properties
operator|.
name|isDocumentSummaryInformation
argument_list|()
condition|)
block|{
name|parse
argument_list|(
operator|new
name|DocumentSummaryInformation
argument_list|(
name|properties
argument_list|)
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoPropertySetStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Not a HPSF document"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UnexpectedPropertySetTypeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unexpected HPSF document"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MarkUnsupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Invalid DocumentInputStream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|SummaryInformation
name|summary
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|TITLE
argument_list|,
name|summary
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|summary
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|KEYWORDS
argument_list|,
name|summary
operator|.
name|getKeywords
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|SUBJECT
argument_list|,
name|summary
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LAST_AUTHOR
argument_list|,
name|summary
operator|.
name|getLastAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|COMMENTS
argument_list|,
name|summary
operator|.
name|getComments
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|TEMPLATE
argument_list|,
name|summary
operator|.
name|getTemplate
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|APPLICATION_NAME
argument_list|,
name|summary
operator|.
name|getApplicationName
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|REVISION_NUMBER
argument_list|,
name|summary
operator|.
name|getRevNumber
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
literal|"creationdate"
argument_list|,
name|summary
operator|.
name|getCreateDateTime
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|CHARACTER_COUNT
argument_list|,
name|summary
operator|.
name|getCharCount
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
literal|"edittime"
argument_list|,
name|summary
operator|.
name|getEditTime
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LAST_SAVED
argument_list|,
name|summary
operator|.
name|getLastSaveDateTime
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|PAGE_COUNT
argument_list|,
name|summary
operator|.
name|getPageCount
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
literal|"security"
argument_list|,
name|summary
operator|.
name|getSecurity
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|WORD_COUNT
argument_list|,
name|summary
operator|.
name|getWordCount
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LAST_PRINTED
argument_list|,
name|summary
operator|.
name|getLastPrinted
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|DocumentSummaryInformation
name|summary
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|set
argument_list|(
name|metadata
argument_list|,
literal|"company"
argument_list|,
name|summary
operator|.
name|getCompany
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|metadata
argument_list|,
literal|"manager"
argument_list|,
name|summary
operator|.
name|getManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setType
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|set
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|set
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|Date
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|set
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

