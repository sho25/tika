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
name|hmef
operator|.
name|Attachment
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
name|HMEFMessage
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
name|MAPIAttribute
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
name|extractor
operator|.
name|EmbeddedDocumentExtractor
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
name|extractor
operator|.
name|ParsingEmbeddedDocumentExtractor
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
name|EmbeddedContentHandler
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
comment|/**  * A POI-powered Tika Parser for TNEF (Transport Neutral  *  Encoding Format) messages, aka winmail.dat  */
end_comment

begin_class
specifier|public
class|class
name|TNEFParser
implements|implements
name|Parser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|4611820730372823452L
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
literal|"vnd.ms-tnef"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"ms-tnef"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tnef"
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
comment|// We work by recursing, so get the appropriate bits
name|EmbeddedDocumentExtractor
name|ex
init|=
name|context
operator|.
name|get
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|)
decl_stmt|;
name|EmbeddedDocumentExtractor
name|embeddedExtractor
decl_stmt|;
if|if
condition|(
name|ex
operator|==
literal|null
condition|)
block|{
name|embeddedExtractor
operator|=
operator|new
name|ParsingEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|embeddedExtractor
operator|=
name|ex
expr_stmt|;
block|}
comment|// Ask POI to process the file for us
name|HMEFMessage
name|msg
init|=
operator|new
name|HMEFMessage
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// Set the message subject if known
name|String
name|subject
init|=
name|msg
operator|.
name|getSubject
argument_list|()
decl_stmt|;
if|if
condition|(
name|subject
operator|!=
literal|null
operator|&&
name|subject
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
comment|// Recurse into the message body RTF
name|MAPIAttribute
name|attr
init|=
name|msg
operator|.
name|getMessageMAPIAttribute
argument_list|(
name|MAPIProperty
operator|.
name|RTF_COMPRESSED
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|!=
literal|null
operator|&&
name|attr
operator|instanceof
name|MAPIRtfAttribute
condition|)
block|{
name|MAPIRtfAttribute
name|rtf
init|=
operator|(
name|MAPIRtfAttribute
operator|)
name|attr
decl_stmt|;
name|handleEmbedded
argument_list|(
literal|"message.rtf"
argument_list|,
literal|"application/rtf"
argument_list|,
name|rtf
operator|.
name|getData
argument_list|()
argument_list|,
name|embeddedExtractor
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
comment|// Recurse into each attachment in turn
for|for
control|(
name|Attachment
name|attachment
range|:
name|msg
operator|.
name|getAttachments
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|attachment
operator|.
name|getLongFilename
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|name
operator|=
name|attachment
operator|.
name|getFilename
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|String
name|ext
init|=
name|attachment
operator|.
name|getExtension
argument_list|()
decl_stmt|;
if|if
condition|(
name|ext
operator|!=
literal|null
condition|)
block|{
name|name
operator|=
literal|"unknown"
operator|+
name|ext
expr_stmt|;
block|}
block|}
name|handleEmbedded
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|attachment
operator|.
name|getContents
argument_list|()
argument_list|,
name|embeddedExtractor
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleEmbedded
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|byte
index|[]
name|contents
parameter_list|,
name|EmbeddedDocumentExtractor
name|embeddedExtractor
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
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
if|if
condition|(
name|embeddedExtractor
operator|.
name|shouldParseEmbedded
argument_list|(
name|metadata
argument_list|)
condition|)
block|{
name|embeddedExtractor
operator|.
name|parseEmbedded
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|contents
argument_list|)
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|metadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

