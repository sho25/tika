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
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hsmf
operator|.
name|MAPIMessage
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
name|hsmf
operator|.
name|datatypes
operator|.
name|AttachmentChunks
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
name|hsmf
operator|.
name|exceptions
operator|.
name|ChunkNotFoundException
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Outlook Message Parser.  */
end_comment

begin_class
specifier|public
class|class
name|OutlookExtractor
extends|extends
name|AbstractPOIFSExtractor
block|{
specifier|private
specifier|final
name|MAPIMessage
name|msg
decl_stmt|;
specifier|public
name|OutlookExtractor
parameter_list|(
name|POIFSFileSystem
name|filesystem
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|TikaException
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|msg
operator|=
operator|new
name|MAPIMessage
argument_list|(
name|filesystem
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Failed to parse Outlook message"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|TikaException
throws|,
name|SAXException
throws|,
name|IOException
block|{
try|try
block|{
name|msg
operator|.
name|setReturnNullOnMissingChunk
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|subject
init|=
name|msg
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|String
name|from
init|=
name|msg
operator|.
name|getDisplayFrom
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|from
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|MESSAGE_FROM
argument_list|,
name|from
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|MESSAGE_TO
argument_list|,
name|msg
operator|.
name|getDisplayTo
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|MESSAGE_CC
argument_list|,
name|msg
operator|.
name|getDisplayCC
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|MESSAGE_BCC
argument_list|,
name|msg
operator|.
name|getDisplayBCC
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|,
name|subject
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|,
name|msg
operator|.
name|getConversationTopic
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|String
name|recipientAddress
range|:
name|msg
operator|.
name|getRecipientEmailAddressList
argument_list|()
control|)
block|{
if|if
condition|(
name|recipientAddress
operator|!=
literal|null
condition|)
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|MESSAGE_RECIPIENT_ADDRESS
argument_list|,
name|recipientAddress
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ChunkNotFoundException
name|he
parameter_list|)
block|{}
comment|// Will be fixed in POI 3.7 Final
comment|// Date - try two ways to find it
comment|// First try via the proper chunk
if|if
condition|(
name|msg
operator|.
name|getMessageDate
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|EDIT_TIME
argument_list|,
name|msg
operator|.
name|getMessageDate
argument_list|()
operator|.
name|getTime
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|LAST_SAVED
argument_list|,
name|msg
operator|.
name|getMessageDate
argument_list|()
operator|.
name|getTime
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
comment|// Failing that try via the raw headers
name|String
index|[]
name|headers
init|=
name|msg
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|headers
operator|!=
literal|null
operator|&&
name|headers
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|header
range|:
name|headers
control|)
block|{
if|if
condition|(
name|header
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"date:"
argument_list|)
condition|)
block|{
name|String
name|date
init|=
name|header
operator|.
name|substring
argument_list|(
name|header
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|EDIT_TIME
argument_list|,
name|date
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|LAST_SAVED
argument_list|,
name|date
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ChunkNotFoundException
name|he
parameter_list|)
block|{
comment|// We can't find the date, sorry...
block|}
block|}
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|subject
argument_list|)
expr_stmt|;
comment|// Output the from and to details in text, as you
comment|//  often want them in text form for searching
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"dl"
argument_list|)
expr_stmt|;
name|header
argument_list|(
name|xhtml
argument_list|,
literal|"From"
argument_list|,
name|from
argument_list|)
expr_stmt|;
name|header
argument_list|(
name|xhtml
argument_list|,
literal|"To"
argument_list|,
name|msg
operator|.
name|getDisplayTo
argument_list|()
argument_list|)
expr_stmt|;
name|header
argument_list|(
name|xhtml
argument_list|,
literal|"Cc"
argument_list|,
name|msg
operator|.
name|getDisplayCC
argument_list|()
argument_list|)
expr_stmt|;
name|header
argument_list|(
name|xhtml
argument_list|,
literal|"Bcc"
argument_list|,
name|msg
operator|.
name|getDisplayBCC
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|header
argument_list|(
name|xhtml
argument_list|,
literal|"Recipients"
argument_list|,
name|msg
operator|.
name|getRecipientEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChunkNotFoundException
name|e
parameter_list|)
block|{}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"dl"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|msg
operator|.
name|getTextBody
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AttachmentChunks
name|attachment
range|:
name|msg
operator|.
name|getAttachmentFiles
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"attachment-entry"
argument_list|)
expr_stmt|;
name|String
name|filename
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|attachment
operator|.
name|attachLongFileName
operator|!=
literal|null
condition|)
block|{
name|filename
operator|=
name|attachment
operator|.
name|attachLongFileName
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|attachment
operator|.
name|attachFileName
operator|!=
literal|null
condition|)
block|{
name|filename
operator|=
name|attachment
operator|.
name|attachFileName
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|filename
operator|!=
literal|null
operator|&&
name|filename
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|filename
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attachment
operator|.
name|attachData
operator|!=
literal|null
condition|)
block|{
name|handleEmbeddedResource
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|attachment
operator|.
name|attachData
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|filename
argument_list|,
literal|null
argument_list|,
name|xhtml
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attachment
operator|.
name|attachmentDirectory
operator|!=
literal|null
condition|)
block|{
name|handleEmbededOfficeDoc
argument_list|(
name|attachment
operator|.
name|attachmentDirectory
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ChunkNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"POI MAPIMessage broken - didn't return null on missing chunk"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|header
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"dt"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"dd"
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

