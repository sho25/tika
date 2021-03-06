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
name|pkg
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
name|com
operator|.
name|github
operator|.
name|junrar
operator|.
name|Archive
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|junrar
operator|.
name|exception
operator|.
name|RarException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|junrar
operator|.
name|impl
operator|.
name|FileVolumeManager
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|junrar
operator|.
name|rarfile
operator|.
name|FileHeader
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
name|EncryptedDocumentException
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
name|exception
operator|.
name|UnsupportedFormatException
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
name|EmbeddedDocumentExtractor
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
name|EmbeddedDocumentUtil
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
name|metadata
operator|.
name|TikaCoreProperties
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
comment|/**  * Parser for Rar files.  */
end_comment

begin_class
specifier|public
class|class
name|RarParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6157727985054451501L
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
name|application
argument_list|(
literal|"x-rar-compressed"
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
name|arg0
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
name|EmbeddedDocumentExtractor
name|extractor
init|=
name|EmbeddedDocumentUtil
operator|.
name|getEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|String
name|mediaType
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
name|mediaType
operator|!=
literal|null
operator|&&
name|mediaType
operator|.
name|contains
argument_list|(
literal|"version=5"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedFormatException
argument_list|(
literal|"Tika does not yet support rar version 5."
argument_list|)
throw|;
block|}
name|Archive
name|rar
init|=
literal|null
decl_stmt|;
try|try
init|(
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
init|)
block|{
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
name|rar
operator|=
operator|new
name|Archive
argument_list|(
operator|new
name|FileVolumeManager
argument_list|(
name|tis
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rar
operator|.
name|isEncrypted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|EncryptedDocumentException
argument_list|()
throw|;
block|}
comment|//Without this BodyContentHandler does not work
name|xhtml
operator|.
name|element
argument_list|(
literal|"div"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|FileHeader
name|header
init|=
name|rar
operator|.
name|nextFileHeader
argument_list|()
decl_stmt|;
while|while
condition|(
name|header
operator|!=
literal|null
operator|&&
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|header
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
try|try
init|(
name|InputStream
name|subFile
init|=
name|rar
operator|.
name|getInputStream
argument_list|(
name|header
argument_list|)
init|)
block|{
name|Metadata
name|entrydata
init|=
name|PackageParser
operator|.
name|handleEntryMetadata
argument_list|(
literal|""
operator|.
name|equals
argument_list|(
name|header
operator|.
name|getFileNameW
argument_list|()
argument_list|)
condition|?
name|header
operator|.
name|getFileNameString
argument_list|()
else|:
name|header
operator|.
name|getFileNameW
argument_list|()
argument_list|,
name|header
operator|.
name|getCTime
argument_list|()
argument_list|,
name|header
operator|.
name|getMTime
argument_list|()
argument_list|,
name|header
operator|.
name|getFullUnpackSize
argument_list|()
argument_list|,
name|xhtml
argument_list|)
decl_stmt|;
if|if
condition|(
name|extractor
operator|.
name|shouldParseEmbedded
argument_list|(
name|entrydata
argument_list|)
condition|)
block|{
name|extractor
operator|.
name|parseEmbedded
argument_list|(
name|subFile
argument_list|,
name|handler
argument_list|,
name|entrydata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|header
operator|=
name|rar
operator|.
name|nextFileHeader
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RarException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"RarParser Exception"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|rar
operator|!=
literal|null
condition|)
name|rar
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
block|}
end_class

end_unit

