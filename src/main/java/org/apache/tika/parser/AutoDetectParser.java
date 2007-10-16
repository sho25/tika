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
name|ByteArrayOutputStream
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
name|MimeType
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
name|MimeTypeException
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
name|MimeTypes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|JDOMException
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
implements|implements
name|Parser
block|{
specifier|private
name|TikaConfig
name|config
decl_stmt|;
comment|/**      * Creates an auto-detecting parser instance using the default Tika      * configuration.      */
specifier|public
name|AutoDetectParser
parameter_list|()
block|{
try|try
block|{
name|config
operator|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
catch|catch
parameter_list|(
name|JDOMException
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
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|TikaConfig
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
specifier|public
name|void
name|setConfig
parameter_list|(
name|TikaConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
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
name|MimeType
name|type
init|=
name|getMimeType
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
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get the parser configured for the detected MIME type
name|Parser
name|parser
init|=
name|config
operator|.
name|getParser
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
name|parser
operator|=
name|config
operator|.
name|getParser
argument_list|(
name|MimeTypes
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"No parsers available: "
operator|+
name|type
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|// Parse the document
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
comment|/**      * Automatically detects the MIME type of a document based on magic      * markers in the stream prefix and any given metadata hints.      *<p>      * The given stream is expected to support marks, so that this method      * can reset the stream to the position it was in before this method      * was called.      *      * @param stream document stream      * @param metadata metadata hints      * @return MIME type of the document      * @throws IOException if the document stream could not be read      */
specifier|private
name|MimeType
name|getMimeType
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|MimeTypes
name|types
init|=
name|config
operator|.
name|getMimeRepository
argument_list|()
decl_stmt|;
name|MimeType
name|type
init|=
literal|null
decl_stmt|;
comment|// Get type based on metadata hint (if available)
name|String
name|typename
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|typename
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|typename
operator|=
name|MimeType
operator|.
name|clean
argument_list|(
name|typename
argument_list|)
expr_stmt|;
name|type
operator|=
name|types
operator|.
name|forName
argument_list|(
name|typename
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|e
parameter_list|)
block|{
comment|// Malformed type name, ignore
block|}
block|}
comment|// Get (or verify) type based on filename hint (if available)
name|String
name|filename
init|=
name|metadata
operator|.
name|get
argument_list|(
literal|"filename"
argument_list|)
decl_stmt|;
if|if
condition|(
name|filename
operator|!=
literal|null
condition|)
block|{
name|MimeType
name|match
init|=
name|types
operator|.
name|getMimeType
argument_list|(
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
operator|&&
operator|(
name|type
operator|==
literal|null
operator|||
operator|!
name|type
operator|.
name|matches
argument_list|(
name|filename
argument_list|)
operator|)
condition|)
block|{
name|type
operator|=
name|match
expr_stmt|;
block|}
block|}
comment|// Get (or verify) type based on magic prefix
name|stream
operator|.
name|mark
argument_list|(
name|types
operator|.
name|getMinLength
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|prefix
init|=
name|getPrefix
argument_list|(
name|stream
argument_list|,
name|types
operator|.
name|getMinLength
argument_list|()
argument_list|)
decl_stmt|;
name|MimeType
name|match
init|=
name|types
operator|.
name|getMimeType
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
operator|&&
operator|(
name|type
operator|==
literal|null
operator|||
operator|!
name|type
operator|.
name|matches
argument_list|(
name|prefix
argument_list|)
operator|)
condition|)
block|{
name|type
operator|=
name|match
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// Finally, use the default type if no matches found
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|types
operator|.
name|forName
argument_list|(
name|MimeTypes
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
comment|/**      * Reads and returns the first<code>length</code> bytes from the      * given stream. If the stream ends before that, returns all bytes      * from the stream.      *       * @param input input stream      * @param length number of bytes to read and return      * @return stream prefix      * @throws IOException if the stream could not be read      */
specifier|private
name|byte
index|[]
name|getPrefix
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|output
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|min
argument_list|(
literal|1024
argument_list|,
name|length
argument_list|)
index|]
decl_stmt|;
name|int
name|n
init|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|n
operator|!=
operator|-
literal|1
condition|)
block|{
name|output
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|int
name|remaining
init|=
name|length
operator|-
name|output
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|n
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|buffer
operator|.
name|length
argument_list|,
name|remaining
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|n
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|output
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

