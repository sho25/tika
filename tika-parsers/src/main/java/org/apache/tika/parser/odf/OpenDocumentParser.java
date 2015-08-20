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
name|odf
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
name|Enumeration
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
name|ZipFile
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
name|IOUtils
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
name|EndDocumentShieldingContentHandler
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * OpenOffice parser  */
end_comment

begin_class
specifier|public
class|class
name|OpenDocumentParser
extends|extends
name|AbstractParser
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6410276875438618287L
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
literal|"vnd.sun.xml.writer"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.text"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.graphics"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.presentation"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.spreadsheet"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.chart"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.image"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.formula"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.text-master"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.text-web"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.text-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.graphics-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.presentation-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.spreadsheet-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.chart-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.image-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.oasis.opendocument.formula-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.text"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.graphics"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.presentation"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.spreadsheet"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.chart"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.image"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.formula"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.text-master"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.text-web"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.text-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.graphics-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.presentation-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.spreadsheet-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.chart-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.image-template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-vnd.oasis.opendocument.formula-template"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|META_NAME
init|=
literal|"meta.xml"
decl_stmt|;
specifier|private
name|Parser
name|meta
init|=
operator|new
name|OpenDocumentMetaParser
argument_list|()
decl_stmt|;
specifier|private
name|Parser
name|content
init|=
operator|new
name|OpenDocumentContentParser
argument_list|()
decl_stmt|;
specifier|public
name|Parser
name|getMetaParser
parameter_list|()
block|{
return|return
name|meta
return|;
block|}
specifier|public
name|void
name|setMetaParser
parameter_list|(
name|Parser
name|meta
parameter_list|)
block|{
name|this
operator|.
name|meta
operator|=
name|meta
expr_stmt|;
block|}
specifier|public
name|Parser
name|getContentParser
parameter_list|()
block|{
return|return
name|content
return|;
block|}
specifier|public
name|void
name|setContentParser
parameter_list|(
name|Parser
name|content
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
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
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|baseHandler
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
comment|// Open the Zip stream
comment|// Use a File if we can, and an already open zip is even better
name|ZipFile
name|zipFile
init|=
literal|null
decl_stmt|;
name|ZipInputStream
name|zipStream
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|stream
operator|instanceof
name|TikaInputStream
condition|)
block|{
name|TikaInputStream
name|tis
init|=
operator|(
name|TikaInputStream
operator|)
name|stream
decl_stmt|;
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
if|if
condition|(
name|container
operator|instanceof
name|ZipFile
condition|)
block|{
name|zipFile
operator|=
operator|(
name|ZipFile
operator|)
name|container
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tis
operator|.
name|hasFile
argument_list|()
condition|)
block|{
name|zipFile
operator|=
operator|new
name|ZipFile
argument_list|(
name|tis
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|zipStream
operator|=
operator|new
name|ZipInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|zipStream
operator|=
operator|new
name|ZipInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
comment|// Prepare to handle the content
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|baseHandler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
comment|// As we don't know which of the metadata or the content
comment|//  we'll hit first, catch the endDocument call initially
name|EndDocumentShieldingContentHandler
name|handler
init|=
operator|new
name|EndDocumentShieldingContentHandler
argument_list|(
name|xhtml
argument_list|)
decl_stmt|;
comment|// If we can, process the metadata first, then the
comment|//  rest of the file afterwards
comment|// Only possible to guarantee that when opened from a file not a stream
name|ZipEntry
name|entry
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|zipFile
operator|!=
literal|null
condition|)
block|{
name|entry
operator|=
name|zipFile
operator|.
name|getEntry
argument_list|(
name|META_NAME
argument_list|)
expr_stmt|;
name|handleZipEntry
argument_list|(
name|entry
argument_list|,
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|entry
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|entries
init|=
name|zipFile
operator|.
name|entries
argument_list|()
decl_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|entry
operator|=
name|entries
operator|.
name|nextElement
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|META_NAME
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|handleZipEntry
argument_list|(
name|entry
argument_list|,
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|entry
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
name|zipFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
do|do
block|{
name|entry
operator|=
name|zipStream
operator|.
name|getNextEntry
argument_list|()
expr_stmt|;
name|handleZipEntry
argument_list|(
name|entry
argument_list|,
name|zipStream
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|entry
operator|!=
literal|null
condition|)
do|;
name|zipStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Only now call the end document
if|if
condition|(
name|handler
operator|.
name|getEndDocumentWasCalled
argument_list|()
condition|)
block|{
name|handler
operator|.
name|reallyEndDocument
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleZipEntry
parameter_list|(
name|ZipEntry
name|entry
parameter_list|,
name|InputStream
name|zip
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|EndDocumentShieldingContentHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"mimetype"
argument_list|)
condition|)
block|{
name|String
name|type
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|zip
argument_list|,
name|UTF_8
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
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|META_NAME
argument_list|)
condition|)
block|{
name|meta
operator|.
name|parse
argument_list|(
name|zip
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"content.xml"
argument_list|)
condition|)
block|{
if|if
condition|(
name|content
operator|instanceof
name|OpenDocumentContentParser
condition|)
block|{
operator|(
operator|(
name|OpenDocumentContentParser
operator|)
name|content
operator|)
operator|.
name|parseInternal
argument_list|(
name|zip
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Foreign content parser was set:
name|content
operator|.
name|parse
argument_list|(
name|zip
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
elseif|else
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"styles.xml"
argument_list|)
condition|)
block|{
if|if
condition|(
name|content
operator|instanceof
name|OpenDocumentContentParser
condition|)
block|{
operator|(
operator|(
name|OpenDocumentContentParser
operator|)
name|content
operator|)
operator|.
name|parseInternal
argument_list|(
name|zip
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Foreign content parser was set:
name|content
operator|.
name|parse
argument_list|(
name|zip
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
block|}
block|}
end_class

end_unit

