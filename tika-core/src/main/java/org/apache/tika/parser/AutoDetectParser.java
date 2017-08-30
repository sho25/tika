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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|TikaConfig
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
name|DefaultDetector
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
name|exception
operator|.
name|ZeroByteFileException
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
name|TemporaryResources
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
name|mime
operator|.
name|MediaTypeRegistry
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
name|SecureContentHandler
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

begin_class
specifier|public
class|class
name|AutoDetectParser
extends|extends
name|CompositeParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6110455808615143122L
decl_stmt|;
comment|//private final TikaConfig config;
comment|/**      * The type detector used by this parser to auto-detect the type      * of a document.      */
specifier|private
name|Detector
name|detector
decl_stmt|;
comment|// always set in the constructor
comment|/**      * Creates an auto-detecting parser instance using the default Tika      * configuration.      */
specifier|public
name|AutoDetectParser
parameter_list|()
block|{
name|this
argument_list|(
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AutoDetectParser
parameter_list|(
name|Detector
name|detector
parameter_list|)
block|{
name|this
argument_list|(
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
name|setDetector
argument_list|(
name|detector
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an auto-detecting parser instance using the specified set of parser.      * This allows one to create a Tika configuration where only a subset of the      * available parsers have their 3rd party jars included, as otherwise the      * use of the default TikaConfig will throw various "ClassNotFound" exceptions.      *       * @param parsers      */
specifier|public
name|AutoDetectParser
parameter_list|(
name|Parser
modifier|...
name|parsers
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|DefaultDetector
argument_list|()
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AutoDetectParser
parameter_list|(
name|Detector
name|detector
parameter_list|,
name|Parser
modifier|...
name|parsers
parameter_list|)
block|{
name|super
argument_list|(
name|MediaTypeRegistry
operator|.
name|getDefaultRegistry
argument_list|()
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
name|setDetector
argument_list|(
name|detector
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AutoDetectParser
parameter_list|(
name|TikaConfig
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|config
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
name|setDetector
argument_list|(
name|config
operator|.
name|getDetector
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the type detector used by this parser to auto-detect the type      * of a document.      *      * @return type detector      * @since Apache Tika 0.4      */
specifier|public
name|Detector
name|getDetector
parameter_list|()
block|{
return|return
name|detector
return|;
block|}
comment|/**      * Sets the type detector used by this parser to auto-detect the type      * of a document.      *      * @param detector type detector      * @since Apache Tika 0.4      */
specifier|public
name|void
name|setDetector
parameter_list|(
name|Detector
name|detector
parameter_list|)
block|{
name|this
operator|.
name|detector
operator|=
name|detector
expr_stmt|;
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
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
try|try
block|{
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
comment|// Automatically detect the MIME type of the document
name|MediaType
name|type
init|=
name|detector
operator|.
name|detect
argument_list|(
name|tis
argument_list|,
name|metadata
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
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//check for zero-byte inputstream
if|if
condition|(
name|tis
operator|.
name|getOpenContainer
argument_list|()
operator|==
literal|null
condition|)
block|{
name|tis
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|tis
operator|.
name|read
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|ZeroByteFileException
argument_list|(
literal|"InputStream must have> 0 bytes"
argument_list|)
throw|;
block|}
name|tis
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// TIKA-216: Zip bomb prevention
name|SecureContentHandler
name|sch
init|=
name|handler
operator|!=
literal|null
condition|?
operator|new
name|SecureContentHandler
argument_list|(
name|handler
argument_list|,
name|tis
argument_list|)
else|:
literal|null
decl_stmt|;
comment|//pass self to handle embedded documents if
comment|//the caller hasn't specified one.
if|if
condition|(
name|context
operator|.
name|get
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|)
operator|==
literal|null
condition|)
block|{
name|Parser
name|p
init|=
name|context
operator|.
name|get
argument_list|(
name|Parser
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|set
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|,
operator|new
name|ParsingEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// Parse the document
name|super
operator|.
name|parse
argument_list|(
name|tis
argument_list|,
name|sch
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
comment|// Convert zip bomb exceptions to TikaExceptions
name|sch
operator|.
name|throwIfCauseOf
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
finally|finally
block|{
name|tmp
operator|.
name|dispose
argument_list|()
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
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

