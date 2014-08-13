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
name|ByteArrayInputStream
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
name|text
operator|.
name|ParseException
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
name|Locale
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
name|hmef
operator|.
name|attribute
operator|.
name|MAPIRtfAttribute
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
name|datatypes
operator|.
name|ByteChunk
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
name|Chunk
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
name|MAPIProperty
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
name|StringChunk
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
name|Types
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
name|html
operator|.
name|HtmlParser
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
name|mbox
operator|.
name|MboxParser
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
name|rtf
operator|.
name|RTFParser
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
name|txt
operator|.
name|CharsetDetector
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
name|txt
operator|.
name|CharsetMatch
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
name|BodyContentHandler
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
name|NPOIFSFileSystem
name|filesystem
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|TikaException
block|{
name|this
argument_list|(
name|filesystem
operator|.
name|getRoot
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OutlookExtractor
parameter_list|(
name|DirectoryNode
name|root
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
name|root
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
comment|// If the message contains strings that aren't stored
comment|//  as Unicode, try to sort out an encoding for them
if|if
condition|(
name|msg
operator|.
name|has7BitEncodingStrings
argument_list|()
condition|)
block|{
if|if
condition|(
name|msg
operator|.
name|getHeaders
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// There's normally something in the headers
name|msg
operator|.
name|guess7BitEncoding
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Nothing in the header, try encoding detection
comment|//  on the message body
name|StringChunk
name|text
init|=
name|msg
operator|.
name|getMainChunks
argument_list|()
operator|.
name|textBodyChunk
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|detector
operator|.
name|setText
argument_list|(
name|text
operator|.
name|getRawValue
argument_list|()
argument_list|)
expr_stmt|;
name|CharsetMatch
name|match
init|=
name|detector
operator|.
name|detect
argument_list|()
decl_stmt|;
if|if
condition|(
name|match
operator|.
name|getConfidence
argument_list|()
operator|>
literal|35
condition|)
block|{
name|msg
operator|.
name|set7BitEncoding
argument_list|(
name|match
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Start with the metadata
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
name|TikaCoreProperties
operator|.
name|CREATOR
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
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|subject
argument_list|)
expr_stmt|;
comment|// TODO: Move to description in Tika 2.0
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TRANSITION_SUBJECT_TO_DC_DESCRIPTION
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
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|msg
operator|.
name|getMessageDate
argument_list|()
operator|.
name|getTime
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
name|msg
operator|.
name|getMessageDate
argument_list|()
operator|.
name|getTime
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
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
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
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// See if we can parse it as a normal mail date
try|try
block|{
name|Date
name|d
init|=
name|MboxParser
operator|.
name|parseDate
argument_list|(
name|date
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|d
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
name|d
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// Store it as-is, and hope for the best...
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|date
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
name|date
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|from
operator|!=
literal|null
condition|)
block|{
name|header
argument_list|(
name|xhtml
argument_list|,
literal|"From"
argument_list|,
name|from
argument_list|)
expr_stmt|;
block|}
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
comment|// Get the message body. Preference order is: html, rtf, text
name|Chunk
name|htmlChunk
init|=
literal|null
decl_stmt|;
name|Chunk
name|rtfChunk
init|=
literal|null
decl_stmt|;
name|Chunk
name|textChunk
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Chunk
name|chunk
range|:
name|msg
operator|.
name|getMainChunks
argument_list|()
operator|.
name|getChunks
argument_list|()
control|)
block|{
if|if
condition|(
name|chunk
operator|.
name|getChunkId
argument_list|()
operator|==
name|MAPIProperty
operator|.
name|BODY_HTML
operator|.
name|id
condition|)
block|{
name|htmlChunk
operator|=
name|chunk
expr_stmt|;
block|}
if|if
condition|(
name|chunk
operator|.
name|getChunkId
argument_list|()
operator|==
name|MAPIProperty
operator|.
name|RTF_COMPRESSED
operator|.
name|id
condition|)
block|{
name|rtfChunk
operator|=
name|chunk
expr_stmt|;
block|}
if|if
condition|(
name|chunk
operator|.
name|getChunkId
argument_list|()
operator|==
name|MAPIProperty
operator|.
name|BODY
operator|.
name|id
condition|)
block|{
name|textChunk
operator|=
name|chunk
expr_stmt|;
block|}
block|}
name|boolean
name|doneBody
init|=
literal|false
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"message-body"
argument_list|)
expr_stmt|;
if|if
condition|(
name|htmlChunk
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|htmlChunk
operator|instanceof
name|ByteChunk
condition|)
block|{
name|data
operator|=
operator|(
operator|(
name|ByteChunk
operator|)
name|htmlChunk
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|htmlChunk
operator|instanceof
name|StringChunk
condition|)
block|{
name|data
operator|=
operator|(
operator|(
name|StringChunk
operator|)
name|htmlChunk
operator|)
operator|.
name|getRawValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|HtmlParser
name|htmlParser
init|=
operator|new
name|HtmlParser
argument_list|()
decl_stmt|;
name|htmlParser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|doneBody
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rtfChunk
operator|!=
literal|null
operator|&&
operator|!
name|doneBody
condition|)
block|{
name|ByteChunk
name|chunk
init|=
operator|(
name|ByteChunk
operator|)
name|rtfChunk
decl_stmt|;
name|MAPIRtfAttribute
name|rtf
init|=
operator|new
name|MAPIRtfAttribute
argument_list|(
name|MAPIProperty
operator|.
name|RTF_COMPRESSED
argument_list|,
name|Types
operator|.
name|BINARY
operator|.
name|getId
argument_list|()
argument_list|,
name|chunk
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|RTFParser
name|rtfParser
init|=
operator|new
name|RTFParser
argument_list|()
decl_stmt|;
name|rtfParser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|rtf
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|doneBody
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|textChunk
operator|!=
literal|null
operator|&&
operator|!
name|doneBody
condition|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
operator|(
operator|(
name|StringChunk
operator|)
name|textChunk
operator|)
operator|.
name|getValue
argument_list|()
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
comment|// Process the attachments
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
name|handleEmbeddedOfficeDoc
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
operator|!=
literal|null
operator|&&
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

