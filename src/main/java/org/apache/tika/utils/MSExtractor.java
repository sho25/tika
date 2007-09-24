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
name|utils
package|;
end_package

begin_comment
comment|// JDK imports
end_comment

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|config
operator|.
name|Content
import|;
end_import

begin_comment
comment|// Jakarta POI imports
end_comment

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
name|hpsf
operator|.
name|PropertySetFactory
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
name|poifs
operator|.
name|eventfilesystem
operator|.
name|POIFSReader
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
name|eventfilesystem
operator|.
name|POIFSReaderEvent
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
name|eventfilesystem
operator|.
name|POIFSReaderListener
import|;
end_import

begin_comment
comment|/**  * Defines a Microsoft document content extractor.  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MSExtractor
block|{
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
specifier|private
name|String
name|text
init|=
literal|null
decl_stmt|;
specifier|private
name|POIFSReader
name|reader
init|=
literal|null
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Content
argument_list|>
name|contents
decl_stmt|;
comment|/** Constructs a new Microsoft document extractor. */
specifier|public
name|MSExtractor
parameter_list|()
block|{             }
specifier|public
name|void
name|setContents
parameter_list|(
name|List
argument_list|<
name|Content
argument_list|>
name|contents
parameter_list|)
block|{
name|this
operator|.
name|contents
operator|=
name|contents
expr_stmt|;
block|}
comment|/**      * Extracts properties and text from an MS Document input stream      */
specifier|public
name|void
name|extract
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|Exception
block|{
comment|// First, extract properties
name|this
operator|.
name|reader
operator|=
operator|new
name|POIFSReader
argument_list|()
expr_stmt|;
name|this
operator|.
name|reader
operator|.
name|registerListener
argument_list|(
operator|new
name|PropertiesReaderListener
argument_list|(
name|contents
argument_list|)
argument_list|,
name|SummaryInformation
operator|.
name|DEFAULT_STREAM_NAME
argument_list|)
expr_stmt|;
comment|//input.reset();
if|if
condition|(
name|input
operator|.
name|available
argument_list|()
operator|>
literal|0
condition|)
block|{
name|reader
operator|.
name|read
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|//input.reset();
name|this
operator|.
name|text
operator|=
name|extractText
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/**      * Extracts the text content from a Microsoft document input stream.      */
specifier|public
specifier|abstract
name|String
name|extractText
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Get the content text of the Microsoft document.      *       * @return the content text of the document      */
specifier|protected
name|String
name|getText
parameter_list|()
block|{
return|return
name|this
operator|.
name|text
return|;
block|}
specifier|private
class|class
name|PropertiesReaderListener
implements|implements
name|POIFSReaderListener
block|{
specifier|private
name|List
argument_list|<
name|Content
argument_list|>
name|contents
decl_stmt|;
name|PropertiesReaderListener
parameter_list|(
name|List
argument_list|<
name|Content
argument_list|>
name|contents
parameter_list|)
block|{
name|this
operator|.
name|contents
operator|=
name|contents
expr_stmt|;
block|}
specifier|public
name|void
name|processPOIFSReaderEvent
parameter_list|(
name|POIFSReaderEvent
name|event
parameter_list|)
block|{
if|if
condition|(
operator|!
name|event
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|SummaryInformation
operator|.
name|DEFAULT_STREAM_NAME
argument_list|)
condition|)
block|{
return|return;
block|}
try|try
block|{
name|SummaryInformation
name|si
init|=
operator|(
name|SummaryInformation
operator|)
name|PropertySetFactory
operator|.
name|create
argument_list|(
name|event
operator|.
name|getStream
argument_list|()
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
name|contents
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Content
name|content
init|=
name|contents
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"title"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"author"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"keywords"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getKeywords
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"subject"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"lastauthor"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getLastAuthor
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"comments"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getComments
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"template"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getTemplate
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"applicationname"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getApplicationName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"revnumber"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getRevNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"creationdate"
argument_list|)
condition|)
block|{
name|content
operator|.
name|setValue
argument_list|(
name|si
operator|.
name|getCreateDateTime
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|""
argument_list|)
condition|)
block|{
comment|//content.setValue(si.getCharCount());
block|}
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{                      }
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{                      }
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{                      }
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{                      }
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{                      }
elseif|else
if|if
condition|(
name|content
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{                      }
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|content
operator|.
name|getName
argument_list|()
operator|+
literal|" :"
operator|+
name|content
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{             }
block|}
block|}
block|}
end_class

begin_comment
comment|/*  * setProperty(DublinCore.TITLE, si.getTitle());  * setProperty(Office.APPLICATION_NAME, si.getApplicationName());  * setProperty(Office.AUTHOR, si.getAuthor());  * setProperty(Office.CHARACTER_COUNT, si.getCharCount());  * setProperty(Office.COMMENTS, si.getComments()); setProperty(DublinCore.DATE,  * si.getCreateDateTime()); // setProperty(Office.EDIT_TIME, si.getEditTime());  * setProperty(HttpHeaders.LAST_MODIFIED, si.getLastSaveDateTime());  * setProperty(Office.KEYWORDS, si.getKeywords());  * setProperty(Office.LAST_AUTHOR, si.getLastAuthor());  * setProperty(Office.LAST_PRINTED, si.getLastPrinted());  * setProperty(Office.LAST_SAVED, si.getLastSaveDateTime());  * setProperty(Office.PAGE_COUNT, si.getPageCount());  * setProperty(Office.REVISION_NUMBER, si.getRevNumber());  * setProperty(DublinCore.RIGHTS, si.getSecurity());  * setProperty(DublinCore.SUBJECT, si.getSubject());  * setProperty(Office.TEMPLATE, si.getTemplate());  * setProperty(Office.WORD_COUNT, si.getWordCount());  */
end_comment

end_unit

