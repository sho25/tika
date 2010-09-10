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
name|extractor
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|XHTMLContentHandler
operator|.
name|XHTML
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
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
name|DocumentSelector
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
name|DelegatingParser
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * Helper class for parsers of package archives or other compound document  * formats that support embedded or attached component documents.  *  * @since Apache Tika 0.8  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedDocumentExtractor
block|{
specifier|private
specifier|static
specifier|final
name|File
name|ABSTRACT_PATH
init|=
operator|new
name|File
argument_list|(
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Parser
name|DELEGATING_PARSER
init|=
operator|new
name|DelegatingParser
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ParseContext
name|context
decl_stmt|;
specifier|public
name|EmbeddedDocumentExtractor
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|boolean
name|shouldParseEmbedded
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|DocumentSelector
name|selector
init|=
name|context
operator|.
name|get
argument_list|(
name|DocumentSelector
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|selector
operator|!=
literal|null
condition|)
block|{
return|return
name|selector
operator|.
name|select
argument_list|(
name|metadata
argument_list|)
return|;
block|}
name|FilenameFilter
name|filter
init|=
name|context
operator|.
name|get
argument_list|(
name|FilenameFilter
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
return|return
name|filter
operator|.
name|accept
argument_list|(
name|ABSTRACT_PATH
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|parseEmbedded
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
name|SAXException
throws|,
name|IOException
block|{
name|AttributesImpl
name|attributes
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"class"
argument_list|,
literal|"class"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"package-entry"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|XHTML
argument_list|,
literal|"div"
argument_list|,
literal|"div"
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
name|XHTML
argument_list|,
literal|"h1"
argument_list|,
literal|"h1"
argument_list|,
operator|new
name|AttributesImpl
argument_list|()
argument_list|)
expr_stmt|;
name|char
index|[]
name|chars
init|=
name|name
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|XHTML
argument_list|,
literal|"h1"
argument_list|,
literal|"h1"
argument_list|)
expr_stmt|;
block|}
comment|// Use the delegate parser to parse this entry
try|try
block|{
name|DELEGATING_PARSER
operator|.
name|parse
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|(
name|handler
argument_list|)
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
comment|// Could not parse the entry, just skip the content
block|}
name|handler
operator|.
name|endElement
argument_list|(
name|XHTML
argument_list|,
literal|"div"
argument_list|,
literal|"div"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

