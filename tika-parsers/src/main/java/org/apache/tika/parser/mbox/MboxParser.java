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
name|mbox
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
comment|/**  * Mbox (mailbox) parser. This version returns the headers for the first email  * via metadata, which means headers from subsequent emails will be lost.  */
end_comment

begin_class
specifier|public
class|class
name|MboxParser
implements|implements
name|Parser
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|MboxParser
operator|.
name|class
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
name|MediaType
operator|.
name|application
argument_list|(
literal|"mbox"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MBOX_MIME_TYPE
init|=
literal|"application/mbox"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MBOX_RECORD_DIVIDER
init|=
literal|"From "
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|EMAIL_HEADER_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([^ ]+):[ \t]*(.*)"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMAIL_HEADER_METADATA_PREFIX
init|=
name|MboxParser
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"-"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EMAIL_FROMLINE_METADATA
init|=
name|EMAIL_HEADER_METADATA_PREFIX
operator|+
literal|"from"
decl_stmt|;
specifier|private
enum|enum
name|ParseStates
block|{
name|START
block|,
name|IN_HEADER
block|,
name|IN_CONTENT
block|}
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
name|TikaException
throws|,
name|SAXException
block|{
name|InputStreamReader
name|isr
decl_stmt|;
try|try
block|{
comment|// Headers are going to be 7-bit ascii
name|isr
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"us-ascii"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|error
argument_list|(
literal|"Unexpected exception setting up MboxParser"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|isr
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|MBOX_MIME_TYPE
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
literal|"us-ascii"
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
name|ParseStates
name|parseState
init|=
name|ParseStates
operator|.
name|START
decl_stmt|;
name|String
name|multiLine
init|=
literal|null
decl_stmt|;
name|boolean
name|inQuote
init|=
literal|false
decl_stmt|;
name|int
name|numEmails
init|=
literal|0
decl_stmt|;
comment|// We're going to scan, line-by-line, for a line that starts with
comment|// "From "
for|for
control|(
name|String
name|curLine
init|=
name|reader
operator|.
name|readLine
argument_list|()
init|;
name|curLine
operator|!=
literal|null
condition|;
name|curLine
operator|=
name|reader
operator|.
name|readLine
argument_list|()
control|)
block|{
name|boolean
name|newMessage
init|=
name|curLine
operator|.
name|startsWith
argument_list|(
name|MBOX_RECORD_DIVIDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|newMessage
condition|)
block|{
name|numEmails
operator|+=
literal|1
expr_stmt|;
block|}
switch|switch
condition|(
name|parseState
condition|)
block|{
case|case
name|START
case|:
if|if
condition|(
name|newMessage
condition|)
block|{
name|parseState
operator|=
name|ParseStates
operator|.
name|IN_HEADER
expr_stmt|;
name|newMessage
operator|=
literal|false
expr_stmt|;
comment|// Fall through to IN_HEADER
block|}
else|else
block|{
break|break;
block|}
case|case
name|IN_HEADER
case|:
if|if
condition|(
name|newMessage
condition|)
block|{
name|saveHeaderInMetadata
argument_list|(
name|numEmails
argument_list|,
name|metadata
argument_list|,
name|multiLine
argument_list|)
expr_stmt|;
name|multiLine
operator|=
name|curLine
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|curLine
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Blank line is signal that we're transitioning to the content.
name|saveHeaderInMetadata
argument_list|(
name|numEmails
argument_list|,
name|metadata
argument_list|,
name|multiLine
argument_list|)
expr_stmt|;
name|parseState
operator|=
name|ParseStates
operator|.
name|IN_CONTENT
expr_stmt|;
comment|// Mimic what PackageParser does between entries.
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"email-entry"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|inQuote
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|curLine
operator|.
name|startsWith
argument_list|(
literal|" "
argument_list|)
operator|||
name|curLine
operator|.
name|startsWith
argument_list|(
literal|"\t"
argument_list|)
condition|)
block|{
name|multiLine
operator|+=
literal|" "
operator|+
name|curLine
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|saveHeaderInMetadata
argument_list|(
name|numEmails
argument_list|,
name|metadata
argument_list|,
name|multiLine
argument_list|)
expr_stmt|;
name|multiLine
operator|=
name|curLine
expr_stmt|;
block|}
break|break;
comment|// TODO - use real email parsing support so we can correctly handle
comment|// things like multipart messages and quoted-printable encoding.
comment|// We'd also want this for charset handling, where content isn't 7-bit
comment|// ascii.
case|case
name|IN_CONTENT
case|:
if|if
condition|(
name|newMessage
condition|)
block|{
name|endMessage
argument_list|(
name|xhtml
argument_list|,
name|inQuote
argument_list|)
expr_stmt|;
name|parseState
operator|=
name|ParseStates
operator|.
name|IN_HEADER
expr_stmt|;
name|multiLine
operator|=
name|curLine
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|quoted
init|=
name|curLine
operator|.
name|startsWith
argument_list|(
literal|">"
argument_list|)
decl_stmt|;
if|if
condition|(
name|inQuote
condition|)
block|{
if|if
condition|(
operator|!
name|quoted
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
name|inQuote
operator|=
literal|false
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|quoted
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
name|inQuote
operator|=
literal|true
expr_stmt|;
block|}
name|xhtml
operator|.
name|characters
argument_list|(
name|curLine
argument_list|)
expr_stmt|;
comment|// For plain text email, each line is a real break position.
name|xhtml
operator|.
name|element
argument_list|(
literal|"br"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|parseState
operator|==
name|ParseStates
operator|.
name|IN_HEADER
condition|)
block|{
name|saveHeaderInMetadata
argument_list|(
name|numEmails
argument_list|,
name|metadata
argument_list|,
name|multiLine
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseState
operator|==
name|ParseStates
operator|.
name|IN_CONTENT
condition|)
block|{
name|endMessage
argument_list|(
name|xhtml
argument_list|,
name|inQuote
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|endMessage
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|boolean
name|inQuote
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|inQuote
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
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
specifier|private
name|void
name|saveHeaderInMetadata
parameter_list|(
name|int
name|numEmails
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|String
name|curLine
parameter_list|)
block|{
if|if
condition|(
operator|(
name|curLine
operator|==
literal|null
operator|)
operator|||
operator|(
name|numEmails
operator|>
literal|1
operator|)
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|curLine
operator|.
name|startsWith
argument_list|(
name|MBOX_RECORD_DIVIDER
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|EMAIL_FROMLINE_METADATA
argument_list|,
name|curLine
operator|.
name|substring
argument_list|(
name|MBOX_RECORD_DIVIDER
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|Matcher
name|headerMatcher
init|=
name|EMAIL_HEADER_PATTERN
operator|.
name|matcher
argument_list|(
name|curLine
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|headerMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Malformed email header in mbox file: "
operator|+
name|curLine
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|headerTag
init|=
name|headerMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|String
name|headerContent
init|=
name|headerMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|headerTag
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"From"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CREATOR
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|headerTag
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Subject"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|headerTag
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Date"
argument_list|)
condition|)
block|{
comment|// TODO - parse and convert to ISO format YYYY-MM-DD
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|DATE
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|headerTag
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Message-Id"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|IDENTIFIER
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|headerTag
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"In-Reply-To"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RELATION
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|headerTag
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Content-Type"
argument_list|)
condition|)
block|{
comment|// TODO - key off content-type in headers to
comment|// set mapping to use for content and convert if necessary.
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|FORMAT
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|add
argument_list|(
name|EMAIL_HEADER_METADATA_PREFIX
operator|+
name|headerTag
argument_list|,
name|headerContent
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
end_class

end_unit

