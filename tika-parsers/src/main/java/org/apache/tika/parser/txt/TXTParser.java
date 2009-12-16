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
name|BufferedInputStream
import|;
end_import

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
name|Reader
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|DublinCore
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
name|HttpHeaders
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
comment|/**  * Plain text parser. The text encoding of the document stream is  * automatically detected based on the byte patterns found at the  * beginning of the stream. The input metadata key  * {@link HttpHeaders#CONTENT_ENCODING} is used as an encoding hint  * if the automatic encoding detection fails.  *<p>  * This parser sets the following output metadata entries:  *<dl>  *<dt>{@link HttpHeaders#CONTENT_TYPE}</dt>  *<dd><code>text/plain</code></dd>  *<dt>{@link HttpHeaders#CONTENT_ENCODING}</dt>  *<dd>The detected text encoding of the document.</dd>  *<dt>  *     {@link HttpHeaders#CONTENT_LANGUAGE} and {@link DublinCore#LANGUAGE}  *</dt>  *<dd>  *     The default language of the detected encoding. Only set if the  *     detected encoding is associated with some specific language  *     (for example KOI8-R with Russian or SJIS with Japanese).  *</dd>  *</dl>  */
end_comment

begin_class
specifier|public
class|class
name|TXTParser
implements|implements
name|Parser
block|{
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
comment|// CharsetDetector expects a stream to support marks
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
comment|// Detect the content encoding (the stream is reset to the beginning)
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|String
name|incomingCharset
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
decl_stmt|;
name|String
name|incomingType
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
name|incomingCharset
operator|==
literal|null
operator|&&
name|incomingType
operator|!=
literal|null
condition|)
block|{
comment|// TIKA-341: Use charset in content-type
name|MediaType
name|mt
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|incomingType
argument_list|)
decl_stmt|;
if|if
condition|(
name|mt
operator|!=
literal|null
condition|)
block|{
name|incomingCharset
operator|=
name|mt
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
literal|"charset"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|incomingCharset
operator|!=
literal|null
condition|)
block|{
name|detector
operator|.
name|setDeclaredEncoding
argument_list|(
name|incomingCharset
argument_list|)
expr_stmt|;
block|}
name|detector
operator|.
name|setText
argument_list|(
name|stream
argument_list|)
expr_stmt|;
for|for
control|(
name|CharsetMatch
name|match
range|:
name|detector
operator|.
name|detectAll
argument_list|()
control|)
block|{
if|if
condition|(
name|Charset
operator|.
name|isSupported
argument_list|(
name|match
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|match
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Is the encoding language-specific (KOI8-R, SJIS, etc.)?
name|String
name|language
init|=
name|match
operator|.
name|getLanguage
argument_list|()
decl_stmt|;
if|if
condition|(
name|language
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_LANGUAGE
argument_list|,
name|language
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|LANGUAGE
argument_list|,
name|language
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
name|String
name|encoding
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Text encoding could not be detected and no encoding"
operator|+
literal|" hint is available in document metadata"
argument_list|)
throw|;
block|}
comment|// TIKA-341: Only stomp on content-type after we're done trying to
comment|// use it to guess at the charset.
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
try|try
block|{
name|Reader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
comment|// TIKA-240: Drop the BOM when extracting plain text
name|reader
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|int
name|bom
init|=
name|reader
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|bom
operator|!=
literal|'\ufeff'
condition|)
block|{
comment|// zero-width no-break space
name|reader
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unsupported text encoding: "
operator|+
name|encoding
argument_list|,
name|e
argument_list|)
throw|;
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

