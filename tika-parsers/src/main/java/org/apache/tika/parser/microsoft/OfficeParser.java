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
name|Arrays
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|hpbf
operator|.
name|extractor
operator|.
name|PublisherTextExtractor
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
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-msoffice"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.visio"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.sheet.binary.macroenabled.12"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"msword"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-outlook"
argument_list|)
argument_list|)
argument_list|)
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
name|SUPPORTED_TYPES
return|;
block|}
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
name|POIFSFileSystem
name|filesystem
init|=
operator|new
name|POIFSFileSystem
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// Parse summary entries first, to make metadata available early
operator|new
name|SummaryExtractor
argument_list|(
name|metadata
argument_list|)
operator|.
name|parseSummaries
argument_list|(
name|filesystem
argument_list|)
expr_stmt|;
comment|// Parse remaining document entries
name|boolean
name|outlookExtracted
init|=
literal|false
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
name|entry
operator|instanceof
name|DirectoryEntry
condition|)
block|{
if|if
condition|(
literal|"Quill"
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
literal|"application/x-mspublisher"
argument_list|)
expr_stmt|;
name|PublisherTextExtractor
name|extractor
init|=
operator|new
name|PublisherTextExtractor
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
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|entry
operator|instanceof
name|DocumentEntry
condition|)
block|{
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
name|addTextIfAny
argument_list|(
name|xhtml
argument_list|,
literal|"header"
argument_list|,
name|extractor
operator|.
name|getHeaderText
argument_list|()
argument_list|)
expr_stmt|;
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
for|for
control|(
name|String
name|paragraph
range|:
name|extractor
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
name|extractor
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
name|extractor
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
name|extractor
operator|.
name|getFooterText
argument_list|()
argument_list|)
expr_stmt|;
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
name|Locale
name|locale
init|=
name|context
operator|.
name|get
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|ExcelExtractor
argument_list|()
operator|.
name|parse
argument_list|(
name|filesystem
argument_list|,
name|xhtml
argument_list|,
name|locale
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
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0.      */
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
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
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
block|}
end_class

end_unit

