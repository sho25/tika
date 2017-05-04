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
name|txt
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|CloseShieldInputStream
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
name|AutoDetectReader
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
name|EncodingDetector
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
name|AbstractEncodingDetectorParser
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
comment|/**  * Plain text parser. The text encoding of the document stream is  * automatically detected based on the byte patterns found at the  * beginning of the stream and the given document metadata, most  * notably the<code>charset</code> parameter of a  * {@link org.apache.tika.metadata.HttpHeaders#CONTENT_TYPE} value.  *<p/>  * This parser sets the following output metadata entries:  *<dl>  *<dt>{@link org.apache.tika.metadata.HttpHeaders#CONTENT_TYPE}</dt>  *<dd><code>text/plain; charset=...</code></dd>  *</dl>  */
end_comment

begin_class
specifier|public
class|class
name|TXTParser
extends|extends
name|AbstractEncodingDetectorParser
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6656102320836888910L
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
name|TEXT_PLAIN
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
specifier|public
name|TXTParser
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TXTParser
parameter_list|(
name|EncodingDetector
name|encodingDetector
parameter_list|)
block|{
name|super
argument_list|(
name|encodingDetector
argument_list|)
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
comment|// Automatically detect the character encoding
try|try
init|(
name|AutoDetectReader
name|reader
init|=
operator|new
name|AutoDetectReader
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|getEncodingDetector
argument_list|(
name|context
argument_list|)
argument_list|)
init|)
block|{
comment|//try to get detected content type; could be a subclass of text/plain
comment|//such as vcal, etc.
name|String
name|incomingMime
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
name|MediaType
name|mediaType
init|=
name|MediaType
operator|.
name|TEXT_PLAIN
decl_stmt|;
if|if
condition|(
name|incomingMime
operator|!=
literal|null
condition|)
block|{
name|MediaType
name|tmpMediaType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|incomingMime
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpMediaType
operator|!=
literal|null
condition|)
block|{
name|mediaType
operator|=
name|tmpMediaType
expr_stmt|;
block|}
block|}
name|Charset
name|charset
init|=
name|reader
operator|.
name|getCharset
argument_list|()
decl_stmt|;
name|MediaType
name|type
init|=
operator|new
name|MediaType
argument_list|(
name|mediaType
argument_list|,
name|charset
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
comment|// deprecated, see TIKA-431
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|charset
operator|.
name|name
argument_list|()
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
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|n
init|=
name|reader
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
name|xhtml
operator|.
name|characters
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
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
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

