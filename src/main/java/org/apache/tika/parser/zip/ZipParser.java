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
name|zip
package|;
end_package

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
name|AutoDetectParser
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
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_comment
comment|/**  * Zip File Parser.  */
end_comment

begin_class
specifier|public
class|class
name|ZipParser
extends|extends
name|AbstractParser
block|{
specifier|private
name|Parser
name|parser
decl_stmt|;
comment|/**      * Parses the given stream as a Zip file.      */
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
name|TikaException
throws|,
name|SAXException
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/zip"
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
comment|// At the end we want to close the Zip stream to release any associated
comment|// resources, but the underlying document stream should not be closed
name|ZipInputStream
name|zip
init|=
operator|new
name|ZipInputStream
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|ZipEntry
name|entry
init|=
name|zip
operator|.
name|getNextEntry
argument_list|()
decl_stmt|;
while|while
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|parseEntry
argument_list|(
name|xhtml
argument_list|,
name|entry
argument_list|,
name|zip
argument_list|)
expr_stmt|;
name|entry
operator|=
name|zip
operator|.
name|getNextEntry
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|zip
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * Parses the given Zip entry using the underlying parser instance.      * It is not an error if the entry can not be parsed, in that case      * just the entry name is emitted.      *      * @param xhtml XHTML event handler      * @param entry zip entry      * @param stream zip stream      * @throws IOException if an IO error occurs      * @throws SAXException if a SAX error occurs      */
specifier|private
name|void
name|parseEntry
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|ZipEntry
name|entry
parameter_list|,
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ContentHandler
name|content
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|getParser
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|content
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|content
operator|.
name|toString
argument_list|()
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
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Parser
name|getParser
parameter_list|()
block|{
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|AutoDetectParser
argument_list|()
return|;
block|}
return|return
name|parser
return|;
block|}
specifier|public
name|void
name|setParser
parameter_list|(
name|Parser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
block|}
end_class

end_unit

