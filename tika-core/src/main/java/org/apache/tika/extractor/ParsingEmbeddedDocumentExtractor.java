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

begin_comment
comment|/**  * Helper class for parsers of package archives or other compound document  * formats that support embedded or attached component documents.  *  * @since Apache Tika 0.8  */
end_comment

begin_class
specifier|public
class|class
name|ParsingEmbeddedDocumentExtractor
implements|implements
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
name|ParsingEmbeddedDocumentExtractor
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
parameter_list|,
name|boolean
name|outputHtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
if|if
condition|(
name|outputHtml
condition|)
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
block|}
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
operator|&&
name|outputHtml
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
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|TikaInputStream
name|newStream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
if|if
condition|(
name|stream
operator|instanceof
name|TikaInputStream
condition|)
block|{
specifier|final
name|Object
name|container
init|=
operator|(
operator|(
name|TikaInputStream
operator|)
name|stream
operator|)
operator|.
name|getOpenContainer
argument_list|()
decl_stmt|;
comment|// TODO: we can't let ZipPackage through,
comment|// becase of POI bug 51949.  This is less
comment|// efficient because the inner parser will
comment|// have to re-open the zip archive again.
comment|// Once we upgrade to POI 3.8 beta 5 we can
comment|// remove this:
if|if
condition|(
operator|(
name|container
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|container
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ZipPackage"
argument_list|)
operator|)
operator|)
condition|)
block|{
name|newStream
operator|.
name|setOpenContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
name|DELEGATING_PARSER
operator|.
name|parse
argument_list|(
name|newStream
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
comment|// TODO: can we log a warning somehow?
comment|// Could not parse the entry, just skip the content
block|}
finally|finally
block|{
name|tmp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|outputHtml
condition|)
block|{
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
block|}
end_class

end_unit

