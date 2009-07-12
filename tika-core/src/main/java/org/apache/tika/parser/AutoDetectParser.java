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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|CountingInputStream
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
name|sax
operator|.
name|SecureContentHandler
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
try|try
block|{
name|setConfig
argument_list|(
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
comment|// FIXME: This should never happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|AutoDetectParser
parameter_list|(
name|TikaConfig
name|config
parameter_list|)
block|{
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setConfig
parameter_list|(
name|TikaConfig
name|config
parameter_list|)
block|{
name|setParsers
argument_list|(
name|config
operator|.
name|getParsers
argument_list|()
argument_list|)
expr_stmt|;
name|setDetector
argument_list|(
name|config
operator|.
name|getMimeRepository
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
comment|/**      * Sets the type detector used by this parser to auto-detect the type      * of a document. Note that calling the {@link #setConfig(TikaConfig)}      * method will override the type detector setting with the type settings      * included in the given configuration.      *      * @param detector type detector      * @since Apache Tika 0.4      */
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
comment|// We need buffering to enable MIME magic detection before parsing
if|if
condition|(
operator|!
name|stream
operator|.
name|markSupported
argument_list|()
condition|)
block|{
name|stream
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
comment|// Automatically detect the MIME type of the document
name|MediaType
name|type
init|=
name|detector
operator|.
name|detect
argument_list|(
name|stream
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
comment|// TIKA-216: Zip bomb prevention
name|CountingInputStream
name|count
init|=
operator|new
name|CountingInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|SecureContentHandler
name|secure
init|=
operator|new
name|SecureContentHandler
argument_list|(
name|handler
argument_list|,
name|count
argument_list|)
decl_stmt|;
comment|// Parse the document
try|try
block|{
name|super
operator|.
name|parse
argument_list|(
name|count
argument_list|,
name|secure
argument_list|,
name|metadata
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
name|secure
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
block|}
end_class

end_unit

