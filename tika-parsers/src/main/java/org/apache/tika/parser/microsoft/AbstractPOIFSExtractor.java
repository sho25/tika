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
name|microsoft
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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|DirectoryEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|DocumentEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|DocumentInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|POIFSFileSystem
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
name|ZipContainerDetector
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
name|EmptyParser
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
name|parser
operator|.
name|microsoft
operator|.
name|OfficeParser
operator|.
name|POIFSDocumentType
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
name|SAXException
import|;
end_import

begin_class
specifier|abstract
class|class
name|AbstractPOIFSExtractor
block|{
specifier|private
specifier|final
name|ParseContext
name|context
decl_stmt|;
specifier|protected
name|AbstractPOIFSExtractor
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
specifier|protected
name|void
name|handleEmbededResource
parameter_list|(
name|TikaInputStream
name|resource
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|mediaType
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|filename
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
name|TIKA_MIME_FILE
argument_list|,
name|filename
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mediaType
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
name|CONTENT_TYPE
argument_list|,
name|mediaType
argument_list|)
expr_stmt|;
block|}
name|Parser
name|parser
init|=
name|context
operator|.
name|get
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|EmptyParser
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|resource
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|resource
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Handle an office document that's embedded at the POIFS level      */
specifier|protected
name|void
name|handleEmbededOfficeDoc
parameter_list|(
name|DirectoryEntry
name|dir
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
comment|// Is it an embeded OLE2 document, or an embeded OOXML document?
try|try
block|{
name|Entry
name|ooxml
init|=
name|dir
operator|.
name|getEntry
argument_list|(
literal|"Package"
argument_list|)
decl_stmt|;
comment|// It's OOXML
name|TikaInputStream
name|ooxmlStream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
operator|new
name|DocumentInputStream
argument_list|(
operator|(
name|DocumentEntry
operator|)
name|ooxml
argument_list|)
argument_list|)
decl_stmt|;
name|ZipContainerDetector
name|detector
init|=
operator|new
name|ZipContainerDetector
argument_list|()
decl_stmt|;
name|MediaType
name|type
init|=
name|detector
operator|.
name|detect
argument_list|(
name|ooxmlStream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
decl_stmt|;
name|handleEmbededResource
argument_list|(
name|ooxmlStream
argument_list|,
literal|null
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// It's regular OLE2
block|}
comment|// Need to dump the directory out to a new temp file, so
comment|//  it's stand along
name|POIFSFileSystem
name|newFS
init|=
operator|new
name|POIFSFileSystem
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|dir
argument_list|,
name|newFS
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|tmpFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"tika"
argument_list|,
literal|".ole2"
argument_list|)
decl_stmt|;
try|try
block|{
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
decl_stmt|;
name|newFS
operator|.
name|writeFilesystem
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// What kind of document is it?
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|POIFSDocumentType
name|type
init|=
name|POIFSDocumentType
operator|.
name|detectType
argument_list|(
name|dir
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
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Trigger for the document itself
name|TikaInputStream
name|embeded
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|tmpFile
argument_list|)
decl_stmt|;
try|try
block|{
name|Parser
name|parser
init|=
name|context
operator|.
name|get
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|EmptyParser
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|embeded
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|embeded
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|copy
parameter_list|(
name|DirectoryEntry
name|sourceDir
parameter_list|,
name|DirectoryEntry
name|destDir
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Entry
name|entry
range|:
name|sourceDir
control|)
block|{
if|if
condition|(
name|entry
operator|instanceof
name|DirectoryEntry
condition|)
block|{
comment|// Need to recurse
name|DirectoryEntry
name|newDir
init|=
name|destDir
operator|.
name|createDirectory
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|copy
argument_list|(
operator|(
name|DirectoryEntry
operator|)
name|entry
argument_list|,
name|newDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Copy entry
name|InputStream
name|contents
init|=
operator|new
name|DocumentInputStream
argument_list|(
operator|(
name|DocumentEntry
operator|)
name|entry
argument_list|)
decl_stmt|;
name|destDir
operator|.
name|createDocument
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|contents
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

