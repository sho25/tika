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
name|Reader
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
comment|/**  * Text parser  */
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
comment|// Use the declared character encoding, if available
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
operator|!=
literal|null
condition|)
block|{
name|detector
operator|.
name|setDeclaredEncoding
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
block|}
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
name|detector
operator|.
name|setText
argument_list|(
name|stream
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
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to detect character encoding"
argument_list|)
throw|;
block|}
name|Reader
name|reader
init|=
name|match
operator|.
name|getReader
argument_list|()
decl_stmt|;
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
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LANGUAGE
argument_list|,
name|match
operator|.
name|getLanguage
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|LANGUAGE
argument_list|,
name|match
operator|.
name|getLanguage
argument_list|()
argument_list|)
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
for|for
control|(
name|int
name|n
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
init|;
name|n
operator|!=
operator|-
literal|1
condition|;
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
control|)
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
end_class

end_unit

