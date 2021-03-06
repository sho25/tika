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
name|mail
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
name|james
operator|.
name|mime4j
operator|.
name|MimeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|message
operator|.
name|DefaultBodyDescriptorBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|parser
operator|.
name|MimeStreamParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|stream
operator|.
name|MimeConfig
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
name|Field
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
name|detect
operator|.
name|Detector
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
name|EmbeddedDocumentUtil
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

begin_comment
comment|/**  * Uses apache-mime4j to parse emails. Each part is treated with the  * corresponding parser and displayed within elements.  *<p/>  * A {@link MimeConfig} object can be passed in the parsing context  * to better control the parsing process.  *  * @author jnioche@digitalpebble.com  */
end_comment

begin_class
specifier|public
class|class
name|RFC822Parser
extends|extends
name|AbstractParser
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5504243905998074168L
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
name|parse
argument_list|(
literal|"message/rfc822"
argument_list|)
argument_list|)
decl_stmt|;
comment|//rely on the detector to be thread-safe
comment|//built lazily and then reused
specifier|private
name|Detector
name|detector
decl_stmt|;
annotation|@
name|Field
specifier|private
name|boolean
name|extractAllAlternatives
init|=
literal|false
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
comment|// Get the mime4j configuration, or use a default one
name|MimeConfig
name|config
init|=
operator|new
name|MimeConfig
operator|.
name|Builder
argument_list|()
operator|.
name|setMaxLineLen
argument_list|(
literal|100000
argument_list|)
operator|.
name|setMaxHeaderLen
argument_list|(
literal|100000
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|config
operator|=
name|context
operator|.
name|get
argument_list|(
name|MimeConfig
operator|.
name|class
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|Detector
name|localDetector
init|=
name|context
operator|.
name|get
argument_list|(
name|Detector
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|localDetector
operator|==
literal|null
condition|)
block|{
comment|//lazily load this if necessary
if|if
condition|(
name|detector
operator|==
literal|null
condition|)
block|{
name|EmbeddedDocumentUtil
name|embeddedDocumentUtil
init|=
operator|new
name|EmbeddedDocumentUtil
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|detector
operator|=
name|embeddedDocumentUtil
operator|.
name|getDetector
argument_list|()
expr_stmt|;
block|}
name|localDetector
operator|=
name|detector
expr_stmt|;
block|}
name|MimeStreamParser
name|parser
init|=
operator|new
name|MimeStreamParser
argument_list|(
name|config
argument_list|,
literal|null
argument_list|,
operator|new
name|DefaultBodyDescriptorBuilder
argument_list|()
argument_list|)
decl_stmt|;
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
name|MailContentHandler
name|mch
init|=
operator|new
name|MailContentHandler
argument_list|(
name|xhtml
argument_list|,
name|localDetector
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
name|config
operator|.
name|isStrictParsing
argument_list|()
argument_list|,
name|extractAllAlternatives
argument_list|)
decl_stmt|;
name|parser
operator|.
name|setContentHandler
argument_list|(
name|mch
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setContentDecoding
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|TikaInputStream
name|tstream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|tstream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|tstream
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
literal|"Failed to parse an email message"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MimeException
name|e
parameter_list|)
block|{
comment|// Unwrap the exception in case it was not thrown by mime4j
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|TikaException
condition|)
block|{
throw|throw
operator|(
name|TikaException
operator|)
name|cause
throw|;
block|}
elseif|else
if|if
condition|(
name|cause
operator|instanceof
name|SAXException
condition|)
block|{
throw|throw
operator|(
name|SAXException
operator|)
name|cause
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Failed to parse an email message"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * Until version 1.17, Tika handled all body parts as embedded objects (see TIKA-2478).      * In 1.17, we modified the parser to select only the best alternative body      * parts for multipart/alternative sections, and we inline the content      * as we do for .msg files.      *      * The legacy behavior can be set by setting {@link #extractAllAlternatives}      * to<code>true</code>.  As of 1.17, the default value is<code>false</code>      *      * @param extractAllAlternatives whether or not to extract all alternative parts      * @since 1.17      */
specifier|public
name|void
name|setExtractAllAlternatives
parameter_list|(
name|boolean
name|extractAllAlternatives
parameter_list|)
block|{
name|this
operator|.
name|extractAllAlternatives
operator|=
name|extractAllAlternatives
expr_stmt|;
block|}
block|}
end_class

end_unit

