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
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|HttpHeaders
operator|.
name|CONTENT_TYPE
import|;
end_import

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
name|util
operator|.
name|Date
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
name|commons
operator|.
name|compress
operator|.
name|PasswordRequiredException
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
name|compress
operator|.
name|archivers
operator|.
name|ArchiveEntry
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
name|compress
operator|.
name|archivers
operator|.
name|ArchiveException
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
name|compress
operator|.
name|archivers
operator|.
name|ArchiveInputStream
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
name|compress
operator|.
name|archivers
operator|.
name|ArchiveStreamFactory
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
name|compress
operator|.
name|archivers
operator|.
name|StreamingNotSupportedException
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
name|compress
operator|.
name|archivers
operator|.
name|ar
operator|.
name|ArArchiveInputStream
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
name|compress
operator|.
name|archivers
operator|.
name|cpio
operator|.
name|CpioArchiveInputStream
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
name|compress
operator|.
name|archivers
operator|.
name|dump
operator|.
name|DumpArchiveInputStream
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
name|compress
operator|.
name|archivers
operator|.
name|jar
operator|.
name|JarArchiveInputStream
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
name|compress
operator|.
name|archivers
operator|.
name|sevenz
operator|.
name|SevenZFile
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
name|compress
operator|.
name|archivers
operator|.
name|tar
operator|.
name|TarArchiveInputStream
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
name|compress
operator|.
name|archivers
operator|.
name|zip
operator|.
name|UnsupportedZipFeatureException
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
name|compress
operator|.
name|archivers
operator|.
name|zip
operator|.
name|UnsupportedZipFeatureException
operator|.
name|Feature
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
name|compress
operator|.
name|archivers
operator|.
name|zip
operator|.
name|ZipArchiveInputStream
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
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * Parser for various packaging formats. Package entries will be written to  * the XHTML event stream as&lt;div class="package-entry"&gt; elements that  * contain the (optional) entry name as a&lt;h1&gt; element and the full  * structured body content of the parsed entry.  *<p>  * User must have JCE Unlimited Strength jars installed for encryption to  * work with 7Z files (see: COMPRESS-299 and TIKA-1521).  If the jars  * are not installed, an IOException will be thrown, and potentially  * wrapped in a TikaException.  */
end_comment

begin_class
specifier|public
class|class
name|PackageParser
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
operator|-
literal|5331043266963888708L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|ZIP
init|=
name|MediaType
operator|.
name|APPLICATION_ZIP
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|JAR
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"java-archive"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|AR
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-archive"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|CPIO
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-cpio"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|DUMP
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-unix-dump"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TAR
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tar"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|SEVENZ
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-7z-compressed"
argument_list|)
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
name|MediaType
operator|.
name|set
argument_list|(
name|ZIP
argument_list|,
name|JAR
argument_list|,
name|AR
argument_list|,
name|CPIO
argument_list|,
name|DUMP
argument_list|,
name|TAR
argument_list|,
name|SEVENZ
argument_list|)
decl_stmt|;
specifier|static
name|MediaType
name|getMediaType
parameter_list|(
name|ArchiveInputStream
name|stream
parameter_list|)
block|{
if|if
condition|(
name|stream
operator|instanceof
name|JarArchiveInputStream
condition|)
block|{
return|return
name|JAR
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|ZipArchiveInputStream
condition|)
block|{
return|return
name|ZIP
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|ArArchiveInputStream
condition|)
block|{
return|return
name|AR
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|CpioArchiveInputStream
condition|)
block|{
return|return
name|CPIO
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|DumpArchiveInputStream
condition|)
block|{
return|return
name|DUMP
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|TarArchiveInputStream
condition|)
block|{
return|return
name|TAR
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|SevenZWrapper
condition|)
block|{
return|return
name|SEVENZ
return|;
block|}
else|else
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
specifier|static
name|boolean
name|isZipArchive
parameter_list|(
name|MediaType
name|type
parameter_list|)
block|{
return|return
name|type
operator|.
name|equals
argument_list|(
name|ZIP
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
name|JAR
argument_list|)
return|;
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
comment|// Ensure that the stream supports the mark feature
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
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
name|ArchiveInputStream
name|ais
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ArchiveStreamFactory
name|factory
init|=
name|context
operator|.
name|get
argument_list|(
name|ArchiveStreamFactory
operator|.
name|class
argument_list|,
operator|new
name|ArchiveStreamFactory
argument_list|()
argument_list|)
decl_stmt|;
comment|// At the end we want to close the archive stream to release
comment|// any associated resources, but the underlying document stream
comment|// should not be closed
name|ais
operator|=
name|factory
operator|.
name|createArchiveInputStream
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StreamingNotSupportedException
name|sne
parameter_list|)
block|{
comment|// Most archive formats work on streams, but a few need files
if|if
condition|(
name|sne
operator|.
name|getFormat
argument_list|()
operator|.
name|equals
argument_list|(
name|ArchiveStreamFactory
operator|.
name|SEVEN_Z
argument_list|)
condition|)
block|{
comment|// Rework as a file, and wrap
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|TikaInputStream
name|tstream
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
comment|// Seven Zip suports passwords, was one given?
name|String
name|password
init|=
literal|null
decl_stmt|;
name|PasswordProvider
name|provider
init|=
name|context
operator|.
name|get
argument_list|(
name|PasswordProvider
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|provider
operator|!=
literal|null
condition|)
block|{
name|password
operator|=
name|provider
operator|.
name|getPassword
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
name|SevenZFile
name|sevenz
decl_stmt|;
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
name|sevenz
operator|=
operator|new
name|SevenZFile
argument_list|(
name|tstream
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sevenz
operator|=
operator|new
name|SevenZFile
argument_list|(
name|tstream
operator|.
name|getFile
argument_list|()
argument_list|,
name|password
operator|.
name|getBytes
argument_list|(
literal|"UnicodeLittleUnmarked"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Pending a fix for COMPRESS-269 / TIKA-1525, this bit is a little nasty
name|ais
operator|=
operator|new
name|SevenZWrapper
argument_list|(
name|sevenz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tmp
operator|.
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unknown non-streaming format "
operator|+
name|sne
operator|.
name|getFormat
argument_list|()
argument_list|,
name|sne
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ArchiveException
name|e
parameter_list|)
block|{
name|tmp
operator|.
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to unpack document stream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|MediaType
name|type
init|=
name|getMediaType
argument_list|(
name|ais
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|CONTENT_TYPE
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Use the delegate parser to parse the contained document
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
try|try
block|{
name|ArchiveEntry
name|entry
init|=
name|ais
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
if|if
condition|(
operator|!
name|entry
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|parseEntry
argument_list|(
name|ais
argument_list|,
name|entry
argument_list|,
name|extractor
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
name|entry
operator|=
name|ais
operator|.
name|getNextEntry
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedZipFeatureException
name|zfe
parameter_list|)
block|{
comment|// If it's an encrypted document of unknown password, report as such
if|if
condition|(
name|zfe
operator|.
name|getFeature
argument_list|()
operator|==
name|Feature
operator|.
name|ENCRYPTION
condition|)
block|{
throw|throw
operator|new
name|EncryptedDocumentException
argument_list|(
name|zfe
argument_list|)
throw|;
block|}
comment|// Otherwise fall through to raise the exception as normal
block|}
catch|catch
parameter_list|(
name|PasswordRequiredException
name|pre
parameter_list|)
block|{
throw|throw
operator|new
name|EncryptedDocumentException
argument_list|(
name|pre
argument_list|)
throw|;
block|}
finally|finally
block|{
name|ais
operator|.
name|close
argument_list|()
expr_stmt|;
name|tmp
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
specifier|private
name|void
name|parseEntry
parameter_list|(
name|ArchiveInputStream
name|archive
parameter_list|,
name|ArchiveEntry
name|entry
parameter_list|,
name|EmbeddedDocumentExtractor
name|extractor
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|archive
operator|.
name|canReadEntryData
argument_list|(
name|entry
argument_list|)
condition|)
block|{
comment|// Fetch the metadata on the entry contained in the archive
name|Metadata
name|entrydata
init|=
name|handleEntryMetadata
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|entry
operator|.
name|getLastModifiedDate
argument_list|()
argument_list|,
name|entry
operator|.
name|getSize
argument_list|()
argument_list|,
name|xhtml
argument_list|)
decl_stmt|;
comment|// Recurse into the entry if desired
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
comment|// For detectors to work, we need a mark/reset supporting
comment|// InputStream, which ArchiveInputStream isn't, so wrap
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
try|try
block|{
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|archive
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
name|extractor
operator|.
name|parseEmbedded
argument_list|(
name|tis
argument_list|,
name|xhtml
argument_list|,
name|entrydata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
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
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|Metadata
name|handleEntryMetadata
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|createAt
parameter_list|,
name|Date
name|modifiedAt
parameter_list|,
name|Long
name|size
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
name|Metadata
name|entrydata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|createAt
operator|!=
literal|null
condition|)
block|{
name|entrydata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|createAt
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|modifiedAt
operator|!=
literal|null
condition|)
block|{
name|entrydata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|modifiedAt
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|entrydata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LENGTH
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|name
operator|=
name|name
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|entrydata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|name
argument_list|)
expr_stmt|;
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
literal|"embedded"
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"id"
argument_list|,
literal|"id"
argument_list|,
literal|"CDATA"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|entrydata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|EMBEDDED_RELATIONSHIP_ID
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|entrydata
return|;
block|}
comment|// Pending a fix for COMPRESS-269, we have to wrap ourselves
specifier|private
specifier|static
class|class
name|SevenZWrapper
extends|extends
name|ArchiveInputStream
block|{
specifier|private
name|SevenZFile
name|file
decl_stmt|;
specifier|private
name|SevenZWrapper
parameter_list|(
name|SevenZFile
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|read
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|read
argument_list|(
name|b
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ArchiveEntry
name|getNextEntry
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|getNextEntry
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

