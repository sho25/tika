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
name|chm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|parser
operator|.
name|chm
operator|.
name|accessor
operator|.
name|DirectoryListingEntry
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
name|chm
operator|.
name|core
operator|.
name|ChmExtractor
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
name|html
operator|.
name|HtmlParser
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

begin_class
specifier|public
class|class
name|ChmParser
extends|extends
name|AbstractParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5938777307516469802L
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
literal|"vnd.ms-htmlhelp"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"chm"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-chm"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
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
annotation|@
name|Override
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
name|ChmExtractor
name|chmExtractor
init|=
operator|new
name|ChmExtractor
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// metadata
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/vnd.ms-htmlhelp"
argument_list|)
expr_stmt|;
comment|// content
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
for|for
control|(
name|DirectoryListingEntry
name|entry
range|:
name|chmExtractor
operator|.
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
control|)
block|{
specifier|final
name|String
name|entryName
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|entryName
operator|.
name|endsWith
argument_list|(
literal|".html"
argument_list|)
operator|||
name|entryName
operator|.
name|endsWith
argument_list|(
literal|".htm"
argument_list|)
condition|)
block|{
comment|//                AttributesImpl attrs = new AttributesImpl();
comment|//                attrs.addAttribute("", "name", "name", "String", entryName);
comment|//                xhtml.startElement("", "document", "document", attrs);
name|byte
index|[]
name|data
init|=
name|chmExtractor
operator|.
name|extractChmEntry
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|parsePage
argument_list|(
name|data
argument_list|,
name|xhtml
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|//                xhtml.endElement("", "", "document");
block|}
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|parsePage
parameter_list|(
name|byte
index|[]
name|byteObject
parameter_list|,
name|ContentHandler
name|xhtml
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|TikaException
block|{
comment|// throws IOException
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|HtmlParser
name|htmlParser
init|=
operator|new
name|HtmlParser
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|EmbeddedContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|)
decl_stmt|;
comment|// -1
try|try
block|{
name|stream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|byteObject
argument_list|)
expr_stmt|;
name|htmlParser
operator|.
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
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
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
name|IOException
name|e
parameter_list|)
block|{
comment|// Pushback overflow from tagsoup
block|}
block|}
block|}
end_class

end_unit

