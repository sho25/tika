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
name|Date
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
name|hpsf
operator|.
name|CustomProperties
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
name|NPOIFSFileSystem
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
name|MSOffice
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
name|PagedText
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Extractor for Common OLE2 (HPSF) metadata  */
end_comment

begin_class
specifier|public
class|class
name|SummaryExtractor
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractPOIFSExtractor
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|public
name|SummaryExtractor
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
specifier|public
name|void
name|parseSummaries
parameter_list|(
name|NPOIFSFileSystem
name|filesystem
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|parseSummaries
argument_list|(
name|filesystem
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|parseSummaries
parameter_list|(
name|DirectoryNode
name|root
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|parseSummaryEntryIfExists
argument_list|(
name|root
argument_list|,
name|SUMMARY_INFORMATION
argument_list|)
expr_stmt|;
name|parseSummaryEntryIfExists
argument_list|(
name|root
argument_list|,
name|DOCUMENT_SUMMARY_INFORMATION
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|parseSummaryEntryIfExists
parameter_list|(
name|DirectoryNode
name|root
parameter_list|,
name|String
name|entryName
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
try|try
block|{
name|DocumentEntry
name|entry
init|=
operator|(
name|DocumentEntry
operator|)
name|root
operator|.
name|getEntry
argument_list|(
name|entryName
argument_list|)
decl_stmt|;
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
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// entry does not exist, just skip it
block|}
catch|catch
parameter_list|(
name|NoPropertySetStreamException
name|e
parameter_list|)
block|{
comment|// no property stream, just skip it
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring unexpected exception while parsing summary entry {}"
argument_list|,
name|entryName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|SummaryInformation
name|summary
parameter_list|)
block|{
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|summary
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|addMulti
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|,
name|summary
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|KEYWORDS
argument_list|,
name|summary
operator|.
name|getKeywords
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO Move to OO subject in Tika 2.0
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TRANSITION_SUBJECT_TO_OO_SUBJECT
argument_list|,
name|summary
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIER
argument_list|,
name|summary
operator|.
name|getLastAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|TikaCoreProperties
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
name|OfficeOpenXMLExtended
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
name|OfficeOpenXMLExtended
operator|.
name|APPLICATION
argument_list|,
name|summary
operator|.
name|getApplicationName
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|REVISION
argument_list|,
name|summary
operator|.
name|getRevNumber
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|summary
operator|.
name|getCreateDateTime
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|summary
operator|.
name|getLastSaveDateTime
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|PRINT_DATE
argument_list|,
name|summary
operator|.
name|getLastPrinted
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|Metadata
operator|.
name|EDIT_TIME
argument_list|,
name|summary
operator|.
name|getEditTime
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|DOC_SECURITY
argument_list|,
name|summary
operator|.
name|getSecurity
argument_list|()
argument_list|)
expr_stmt|;
comment|// New style counts
name|set
argument_list|(
name|Office
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
name|Office
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
name|Office
operator|.
name|PAGE_COUNT
argument_list|,
name|summary
operator|.
name|getPageCount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|summary
operator|.
name|getPageCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PagedText
operator|.
name|N_PAGES
argument_list|,
name|summary
operator|.
name|getPageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Old style, Tika 1.0 properties
comment|// TODO Remove these in Tika 2.0
name|set
argument_list|(
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
name|Metadata
operator|.
name|SECURITY
argument_list|,
name|summary
operator|.
name|getSecurity
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|MSOffice
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
name|MSOffice
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
name|MSOffice
operator|.
name|PAGE_COUNT
argument_list|,
name|summary
operator|.
name|getPageCount
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
parameter_list|)
block|{
name|set
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|COMPANY
argument_list|,
name|summary
operator|.
name|getCompany
argument_list|()
argument_list|)
expr_stmt|;
name|addMulti
argument_list|(
name|metadata
argument_list|,
name|OfficeOpenXMLExtended
operator|.
name|MANAGER
argument_list|,
name|summary
operator|.
name|getManager
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|LANGUAGE
argument_list|,
name|getLanguage
argument_list|(
name|summary
argument_list|)
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|CATEGORY
argument_list|,
name|summary
operator|.
name|getCategory
argument_list|()
argument_list|)
expr_stmt|;
comment|// New style counts
name|set
argument_list|(
name|Office
operator|.
name|SLIDE_COUNT
argument_list|,
name|summary
operator|.
name|getSlideCount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|summary
operator|.
name|getSlideCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PagedText
operator|.
name|N_PAGES
argument_list|,
name|summary
operator|.
name|getSlideCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Old style, Tika 1.0 counts
comment|// TODO Remove these in Tika 2.0
name|set
argument_list|(
name|Metadata
operator|.
name|COMPANY
argument_list|,
name|summary
operator|.
name|getCompany
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|Metadata
operator|.
name|MANAGER
argument_list|,
name|summary
operator|.
name|getManager
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|MSOffice
operator|.
name|SLIDE_COUNT
argument_list|,
name|summary
operator|.
name|getSlideCount
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|Metadata
operator|.
name|CATEGORY
argument_list|,
name|summary
operator|.
name|getCategory
argument_list|()
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|summary
operator|.
name|getCustomProperties
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getLanguage
parameter_list|(
name|DocumentSummaryInformation
name|summary
parameter_list|)
block|{
name|CustomProperties
name|customProperties
init|=
name|summary
operator|.
name|getCustomProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|customProperties
operator|!=
literal|null
condition|)
block|{
name|Object
name|value
init|=
name|customProperties
operator|.
name|get
argument_list|(
literal|"Language"
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|value
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Attempt to parse custom document properties and add to the collection of metadata      *      * @param customProperties      */
specifier|private
name|void
name|parse
parameter_list|(
name|CustomProperties
name|customProperties
parameter_list|)
block|{
if|if
condition|(
name|customProperties
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|customProperties
operator|.
name|nameSet
argument_list|()
control|)
block|{
comment|// Apply the custom prefix
name|String
name|key
init|=
name|Metadata
operator|.
name|USER_DEFINED_METADATA_NAME_PREFIX
operator|+
name|name
decl_stmt|;
comment|// Get, convert and save property value
name|Object
name|value
init|=
name|customProperties
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|set
argument_list|(
name|key
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Date
condition|)
block|{
name|Property
name|prop
init|=
name|Property
operator|.
name|externalDate
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|prop
argument_list|,
operator|(
name|Date
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
name|Property
name|prop
init|=
name|Property
operator|.
name|externalBoolean
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|prop
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Long
condition|)
block|{
name|Property
name|prop
init|=
name|Property
operator|.
name|externalInteger
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|prop
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Double
condition|)
block|{
name|Property
name|prop
init|=
name|Property
operator|.
name|externalReal
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|prop
argument_list|,
operator|(
name|Double
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
name|Property
name|prop
init|=
name|Property
operator|.
name|externalInteger
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|prop
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|set
parameter_list|(
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
name|Property
name|property
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
name|property
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
name|Property
name|property
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
name|property
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
name|Property
name|property
parameter_list|,
name|int
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
name|property
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
comment|//MS stores values that should be multiple values (e.g. dc:creator)
comment|//as a semicolon-delimited list.  We need to split
comment|//on semicolon to add each value.
specifier|public
specifier|static
name|void
name|addMulti
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|property
parameter_list|,
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|string
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
index|[]
name|parts
init|=
name|string
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
name|String
index|[]
name|current
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|val
range|:
name|current
control|)
block|{
name|seen
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|part
range|:
name|parts
control|)
block|{
if|if
condition|(
operator|!
name|seen
operator|.
name|contains
argument_list|(
name|part
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|property
argument_list|,
name|part
argument_list|)
expr_stmt|;
name|seen
operator|.
name|add
argument_list|(
name|part
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

