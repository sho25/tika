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
name|FileNotFoundException
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
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hpsf
operator|.
name|ClassID
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
name|DirectoryNode
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
name|Ole10Native
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
name|Ole10NativeException
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
name|config
operator|.
name|TikaConfig
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
name|Detector
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
name|mime
operator|.
name|MimeType
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
name|MimeTypeException
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
name|MimeTypes
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
name|PasswordProvider
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
name|parser
operator|.
name|pkg
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
name|EmbeddedDocumentUtil
name|embeddedDocumentUtil
decl_stmt|;
specifier|private
name|PasswordProvider
name|passwordProvider
decl_stmt|;
specifier|protected
specifier|final
name|Metadata
name|parentMetadata
decl_stmt|;
comment|//metadata of the parent/container document
specifier|protected
specifier|final
name|OfficeParserConfig
name|officeParserConfig
decl_stmt|;
specifier|protected
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
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|AbstractPOIFSExtractor
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|Metadata
name|parentMetadata
parameter_list|)
block|{
name|embeddedDocumentUtil
operator|=
operator|new
name|EmbeddedDocumentUtil
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|passwordProvider
operator|=
name|context
operator|.
name|get
argument_list|(
name|PasswordProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|officeParserConfig
operator|=
name|context
operator|.
name|get
argument_list|(
name|OfficeParserConfig
operator|.
name|class
argument_list|,
operator|new
name|OfficeParserConfig
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentMetadata
operator|=
name|parentMetadata
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|// Note - these cache, but avoid creating the default TikaConfig if not needed
specifier|protected
name|TikaConfig
name|getTikaConfig
parameter_list|()
block|{
return|return
name|embeddedDocumentUtil
operator|.
name|getTikaConfig
argument_list|()
return|;
block|}
specifier|protected
name|Detector
name|getDetector
parameter_list|()
block|{
return|return
name|embeddedDocumentUtil
operator|.
name|getDetector
argument_list|()
return|;
block|}
comment|/**      * @deprecated use {@link #embeddedDocumentUtil}      * @return mimetypes      */
specifier|protected
name|MimeTypes
name|getMimeTypes
parameter_list|()
block|{
return|return
name|embeddedDocumentUtil
operator|.
name|getMimeTypes
argument_list|()
return|;
block|}
comment|/**      * Returns the password to be used for this file, or null      * if no / default password should be used      */
specifier|protected
name|String
name|getPassword
parameter_list|()
block|{
if|if
condition|(
name|passwordProvider
operator|!=
literal|null
condition|)
block|{
return|return
name|passwordProvider
operator|.
name|getPassword
argument_list|(
name|parentMetadata
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|handleEmbeddedResource
parameter_list|(
name|TikaInputStream
name|resource
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|relationshipID
parameter_list|,
name|String
name|mediaType
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|boolean
name|outputHtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|handleEmbeddedResource
argument_list|(
name|resource
argument_list|,
name|filename
argument_list|,
name|relationshipID
argument_list|,
literal|null
argument_list|,
name|mediaType
argument_list|,
name|xhtml
argument_list|,
name|outputHtml
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|handleEmbeddedResource
parameter_list|(
name|TikaInputStream
name|resource
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|relationshipID
parameter_list|,
name|ClassID
name|storageClassID
parameter_list|,
name|String
name|mediaType
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|boolean
name|outputHtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|handleEmbeddedResource
argument_list|(
name|resource
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|filename
argument_list|,
name|relationshipID
argument_list|,
name|storageClassID
argument_list|,
name|mediaType
argument_list|,
name|xhtml
argument_list|,
name|outputHtml
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|handleEmbeddedResource
parameter_list|(
name|TikaInputStream
name|resource
parameter_list|,
name|Metadata
name|embeddedMetadata
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|relationshipID
parameter_list|,
name|ClassID
name|storageClassID
parameter_list|,
name|String
name|mediaType
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|boolean
name|outputHtml
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
if|if
condition|(
name|filename
operator|!=
literal|null
condition|)
block|{
name|embeddedMetadata
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
name|embeddedMetadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|filename
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|relationshipID
operator|!=
literal|null
condition|)
block|{
name|embeddedMetadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|EMBEDDED_RELATIONSHIP_ID
argument_list|,
name|relationshipID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storageClassID
operator|!=
literal|null
condition|)
block|{
name|embeddedMetadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|EMBEDDED_STORAGE_CLASS_ID
argument_list|,
name|storageClassID
operator|.
name|toString
argument_list|()
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
name|embeddedMetadata
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
if|if
condition|(
name|embeddedDocumentUtil
operator|.
name|shouldParseEmbedded
argument_list|(
name|embeddedMetadata
argument_list|)
condition|)
block|{
name|embeddedDocumentUtil
operator|.
name|parseEmbedded
argument_list|(
name|resource
argument_list|,
name|xhtml
argument_list|,
name|embeddedMetadata
argument_list|,
name|outputHtml
argument_list|)
expr_stmt|;
block|}
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
name|handleEmbeddedOfficeDoc
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
name|handleEmbeddedOfficeDoc
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
comment|/**      * Handle an office document that's embedded at the POIFS level      */
specifier|protected
name|void
name|handleEmbeddedOfficeDoc
parameter_list|(
name|DirectoryEntry
name|dir
parameter_list|,
name|String
name|resourceName
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
comment|// Is it an embedded OLE2 document, or an embedded OOXML document?
if|if
condition|(
name|dir
operator|.
name|hasEntry
argument_list|(
literal|"Package"
argument_list|)
condition|)
block|{
comment|// It's OOXML (has a ZipFile):
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
try|try
init|(
name|TikaInputStream
name|stream
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
init|)
block|{
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
literal|null
decl_stmt|;
try|try
block|{
comment|//if there's a stream error while detecting...
name|type
operator|=
name|detector
operator|.
name|detect
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordEmbeddedStreamException
argument_list|(
name|e
argument_list|,
name|parentMetadata
argument_list|)
expr_stmt|;
return|return;
block|}
name|handleEmbeddedResource
argument_list|(
name|stream
argument_list|,
literal|null
argument_list|,
name|dir
operator|.
name|getName
argument_list|()
argument_list|,
name|dir
operator|.
name|getStorageClsid
argument_list|()
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|,
name|xhtml
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// It's regular OLE2:
comment|// What kind of document is it?
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
name|TikaCoreProperties
operator|.
name|EMBEDDED_RELATIONSHIP_ID
argument_list|,
name|dir
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|dir
operator|.
name|getStorageClsid
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|EMBEDDED_STORAGE_CLASS_ID
argument_list|,
name|dir
operator|.
name|getStorageClsid
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|TikaInputStream
name|embedded
init|=
literal|null
decl_stmt|;
name|String
name|rName
init|=
operator|(
name|resourceName
operator|==
literal|null
operator|)
condition|?
name|dir
operator|.
name|getName
argument_list|()
else|:
name|resourceName
decl_stmt|;
try|try
block|{
if|if
condition|(
name|type
operator|==
name|POIFSDocumentType
operator|.
name|OLE10_NATIVE
condition|)
block|{
try|try
block|{
comment|// Try to un-wrap the OLE10Native record:
name|Ole10Native
name|ole
init|=
name|Ole10Native
operator|.
name|createFromEmbeddedOleObject
argument_list|(
operator|(
name|DirectoryNode
operator|)
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|ole
operator|.
name|getLabel
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|rName
operator|+
literal|'/'
operator|+
name|ole
operator|.
name|getLabel
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ole
operator|.
name|getCommand
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|,
name|ole
operator|.
name|getCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ole
operator|.
name|getFileName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|,
name|ole
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
name|ole
operator|.
name|getDataBuffer
argument_list|()
decl_stmt|;
name|embedded
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Ole10NativeException
name|ex
parameter_list|)
block|{
comment|// Not a valid OLE10Native record, skip it
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordEmbeddedStreamException
argument_list|(
name|e
argument_list|,
name|parentMetadata
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|POIFSDocumentType
operator|.
name|COMP_OBJ
condition|)
block|{
try|try
block|{
comment|//TODO: figure out if the equivalent of OLE 1.0's
comment|//getCommand() and getFileName() exist for OLE 2.0 to populate
comment|//TikaCoreProperties.ORIGINAL_RESOURCE_NAME
comment|// Grab the contents and process
name|DocumentEntry
name|contentsEntry
decl_stmt|;
try|try
block|{
name|contentsEntry
operator|=
operator|(
name|DocumentEntry
operator|)
name|dir
operator|.
name|getEntry
argument_list|(
literal|"CONTENTS"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ioe
parameter_list|)
block|{
name|contentsEntry
operator|=
operator|(
name|DocumentEntry
operator|)
name|dir
operator|.
name|getEntry
argument_list|(
literal|"Contents"
argument_list|)
expr_stmt|;
block|}
name|DocumentInputStream
name|inp
init|=
operator|new
name|DocumentInputStream
argument_list|(
name|contentsEntry
argument_list|)
decl_stmt|;
name|byte
index|[]
name|contents
init|=
operator|new
name|byte
index|[
name|contentsEntry
operator|.
name|getSize
argument_list|()
index|]
decl_stmt|;
name|inp
operator|.
name|readFully
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|embedded
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|contents
argument_list|)
expr_stmt|;
comment|// Try to work out what it is
name|MediaType
name|mediaType
init|=
name|getDetector
argument_list|()
operator|.
name|detect
argument_list|(
name|embedded
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|extension
init|=
name|type
operator|.
name|getExtension
argument_list|()
decl_stmt|;
try|try
block|{
name|MimeType
name|mimeType
init|=
name|getMimeTypes
argument_list|()
operator|.
name|forName
argument_list|(
name|mediaType
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|extension
operator|=
name|mimeType
operator|.
name|getExtension
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|mte
parameter_list|)
block|{
comment|// No details on this type are known
block|}
comment|// Record what we can do about it
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|mediaType
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|rName
operator|+
name|extension
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordEmbeddedStreamException
argument_list|(
name|e
argument_list|,
name|parentMetadata
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
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
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|rName
operator|+
literal|'.'
operator|+
name|type
operator|.
name|getExtension
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Should we parse it?
if|if
condition|(
name|embeddedDocumentUtil
operator|.
name|shouldParseEmbedded
argument_list|(
name|metadata
argument_list|)
condition|)
block|{
if|if
condition|(
name|embedded
operator|==
literal|null
condition|)
block|{
comment|// Make a TikaInputStream that just
comment|// passes the root directory of the
comment|// embedded document, and is otherwise
comment|// empty (byte[0]):
name|embedded
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|embedded
operator|.
name|setOpenContainer
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|embeddedDocumentUtil
operator|.
name|parseEmbedded
argument_list|(
name|embedded
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|EmbeddedDocumentUtil
operator|.
name|recordEmbeddedStreamException
argument_list|(
name|e
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|embedded
operator|!=
literal|null
condition|)
block|{
name|embedded
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

