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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|regex
operator|.
name|Pattern
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
name|ZipArchiveEntry
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
name|ZipFile
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
name|compressors
operator|.
name|CompressorException
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
name|compressors
operator|.
name|CompressorInputStream
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
name|compressors
operator|.
name|CompressorStreamFactory
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
name|extractor
operator|.
name|ExtractorFactory
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
name|openxml4j
operator|.
name|exceptions
operator|.
name|InvalidFormatException
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
name|openxml4j
operator|.
name|opc
operator|.
name|OPCPackage
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
name|openxml4j
operator|.
name|opc
operator|.
name|PackageAccess
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
name|openxml4j
operator|.
name|opc
operator|.
name|PackagePart
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
name|openxml4j
operator|.
name|opc
operator|.
name|PackageRelationshipCollection
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
name|iwork
operator|.
name|IWorkPackageParser
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
name|iwork
operator|.
name|IWorkPackageParser
operator|.
name|IWORKDocumentType
import|;
end_import

begin_comment
comment|/**  * A detector that works on Zip documents and other archive and compression  * formats to figure out exactly what the file is.  */
end_comment

begin_class
specifier|public
class|class
name|ZipContainerDetector
implements|implements
name|Detector
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|MACRO_TEMPLATE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"macroenabledtemplate$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
comment|// TODO Remove this constant once we upgrade to POI 3.12 beta 2, it is defined in ExtractorFactory there
specifier|private
specifier|static
specifier|final
name|String
name|VISIO_DOCUMENT_REL
init|=
literal|"http://schemas.microsoft.com/visio/2010/relationships/document"
decl_stmt|;
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2891763938430295453L
decl_stmt|;
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check if we have access to the document
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
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
name|input
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
name|byte
index|[]
name|prefix
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
comment|// enough for all known formats
name|int
name|length
init|=
name|tis
operator|.
name|peek
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|MediaType
name|type
init|=
name|detectArchiveFormat
argument_list|(
name|prefix
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|PackageParser
operator|.
name|isZipArchive
argument_list|(
name|type
argument_list|)
operator|&&
name|TikaInputStream
operator|.
name|isTikaInputStream
argument_list|(
name|input
argument_list|)
condition|)
block|{
return|return
name|detectZipFormat
argument_list|(
name|tis
argument_list|)
return|;
block|}
elseif|else
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
return|return
name|type
return|;
block|}
else|else
block|{
return|return
name|detectCompressorFormat
argument_list|(
name|prefix
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
specifier|private
specifier|static
name|MediaType
name|detectCompressorFormat
parameter_list|(
name|byte
index|[]
name|prefix
parameter_list|,
name|int
name|length
parameter_list|)
block|{
try|try
block|{
name|CompressorStreamFactory
name|factory
init|=
operator|new
name|CompressorStreamFactory
argument_list|()
decl_stmt|;
name|CompressorInputStream
name|cis
init|=
name|factory
operator|.
name|createCompressorInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|prefix
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|CompressorParser
operator|.
name|getMediaType
argument_list|(
name|cis
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|cis
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CompressorException
name|e
parameter_list|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
specifier|private
specifier|static
name|MediaType
name|detectArchiveFormat
parameter_list|(
name|byte
index|[]
name|prefix
parameter_list|,
name|int
name|length
parameter_list|)
block|{
try|try
block|{
name|ArchiveStreamFactory
name|factory
init|=
operator|new
name|ArchiveStreamFactory
argument_list|()
decl_stmt|;
name|ArchiveInputStream
name|ais
init|=
name|factory
operator|.
name|createArchiveInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|prefix
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|(
name|ais
operator|instanceof
name|TarArchiveInputStream
operator|)
operator|&&
operator|!
name|TarArchiveInputStream
operator|.
name|matches
argument_list|(
name|prefix
argument_list|,
name|length
argument_list|)
condition|)
block|{
comment|// ArchiveStreamFactory is too relaxed, see COMPRESS-117
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
else|else
block|{
return|return
name|PackageParser
operator|.
name|getMediaType
argument_list|(
name|ais
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|ais
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ArchiveException
name|e
parameter_list|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
specifier|private
specifier|static
name|MediaType
name|detectZipFormat
parameter_list|(
name|TikaInputStream
name|tis
parameter_list|)
block|{
try|try
block|{
name|ZipFile
name|zip
init|=
operator|new
name|ZipFile
argument_list|(
name|tis
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO: hasFile()?
try|try
block|{
name|MediaType
name|type
init|=
name|detectOpenDocument
argument_list|(
name|zip
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|detectOPCBased
argument_list|(
name|zip
argument_list|,
name|tis
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|detectIWork
argument_list|(
name|zip
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|detectJar
argument_list|(
name|zip
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|detectKmz
argument_list|(
name|zip
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|detectIpa
argument_list|(
name|zip
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
finally|finally
block|{
comment|// TODO: shouldn't we record the open
comment|// container so it can be later
comment|// reused...?
comment|// tis.setOpenContainer(zip);
try|try
block|{
name|zip
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
comment|// Fallback: it's still a zip file, we just don't know what kind of one
return|return
name|MediaType
operator|.
name|APPLICATION_ZIP
return|;
block|}
comment|/**      * OpenDocument files, along with EPub files, have a mimetype      *  entry in the root of their Zip file. This entry contains the      *  mimetype of the overall file, stored as a single string.        */
specifier|private
specifier|static
name|MediaType
name|detectOpenDocument
parameter_list|(
name|ZipFile
name|zip
parameter_list|)
block|{
try|try
block|{
name|ZipArchiveEntry
name|mimetype
init|=
name|zip
operator|.
name|getEntry
argument_list|(
literal|"mimetype"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mimetype
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|stream
init|=
name|zip
operator|.
name|getInputStream
argument_list|(
name|mimetype
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
argument_list|,
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|MediaType
name|detectOPCBased
parameter_list|(
name|ZipFile
name|zip
parameter_list|,
name|TikaInputStream
name|stream
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|zip
operator|.
name|getEntry
argument_list|(
literal|"_rels/.rels"
argument_list|)
operator|!=
literal|null
operator|||
name|zip
operator|.
name|getEntry
argument_list|(
literal|"[Content_Types].xml"
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// Use POI to open and investigate it for us
name|OPCPackage
name|pkg
init|=
name|OPCPackage
operator|.
name|open
argument_list|(
name|stream
operator|.
name|getFile
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|PackageAccess
operator|.
name|READ
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setOpenContainer
argument_list|(
name|pkg
argument_list|)
expr_stmt|;
comment|// Is at an OOXML format?
name|MediaType
name|type
init|=
name|detectOfficeOpenXML
argument_list|(
name|pkg
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
return|return
name|type
return|;
comment|// Is it XPS format?
name|type
operator|=
name|detectXPSOPC
argument_list|(
name|pkg
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
return|return
name|type
return|;
comment|// Is it an AutoCAD format?
name|type
operator|=
name|detectAutoCADOPC
argument_list|(
name|pkg
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
return|return
name|type
return|;
comment|// We don't know what it is, sorry
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Detects the type of an OfficeOpenXML (OOXML) file from      *  opened Package       */
specifier|public
specifier|static
name|MediaType
name|detectOfficeOpenXML
parameter_list|(
name|OPCPackage
name|pkg
parameter_list|)
block|{
comment|// Check for the normal Office core document
name|PackageRelationshipCollection
name|core
init|=
name|pkg
operator|.
name|getRelationshipsByType
argument_list|(
name|ExtractorFactory
operator|.
name|CORE_DOCUMENT_REL
argument_list|)
decl_stmt|;
comment|// Otherwise check for some other Office core document types
if|if
condition|(
name|core
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|core
operator|=
name|pkg
operator|.
name|getRelationshipsByType
argument_list|(
name|VISIO_DOCUMENT_REL
argument_list|)
expr_stmt|;
block|}
comment|// If we didn't find a single core document of any type, skip detection
if|if
condition|(
name|core
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
comment|// Invalid OOXML Package received
return|return
literal|null
return|;
block|}
comment|// Get the type of the core document part
name|PackagePart
name|corePart
init|=
name|pkg
operator|.
name|getPart
argument_list|(
name|core
operator|.
name|getRelationship
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|coreType
init|=
name|corePart
operator|.
name|getContentType
argument_list|()
decl_stmt|;
comment|// Turn that into the type of the overall document
name|String
name|docType
init|=
name|coreType
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|coreType
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
decl_stmt|;
comment|// The Macro Enabled formats are a little special
if|if
condition|(
name|docType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|"macroenabled"
argument_list|)
condition|)
block|{
name|docType
operator|=
name|docType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|".12"
expr_stmt|;
block|}
if|if
condition|(
name|docType
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|"macroenabledtemplate"
argument_list|)
condition|)
block|{
name|docType
operator|=
name|MACRO_TEMPLATE_PATTERN
operator|.
name|matcher
argument_list|(
name|docType
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"macroenabled.12"
argument_list|)
expr_stmt|;
block|}
comment|// Build the MediaType object and return
return|return
name|MediaType
operator|.
name|parse
argument_list|(
name|docType
argument_list|)
return|;
block|}
comment|/**      * Detects Open XML Paper Specification (XPS)      */
specifier|private
specifier|static
name|MediaType
name|detectXPSOPC
parameter_list|(
name|OPCPackage
name|pkg
parameter_list|)
block|{
name|PackageRelationshipCollection
name|xps
init|=
name|pkg
operator|.
name|getRelationshipsByType
argument_list|(
literal|"http://schemas.microsoft.com/xps/2005/06/fixedrepresentation"
argument_list|)
decl_stmt|;
if|if
condition|(
name|xps
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-xpsdocument"
argument_list|)
return|;
block|}
else|else
block|{
comment|// Non-XPS Package received
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Detects AutoCAD formats that live in OPC packaging      */
specifier|private
specifier|static
name|MediaType
name|detectAutoCADOPC
parameter_list|(
name|OPCPackage
name|pkg
parameter_list|)
block|{
name|PackageRelationshipCollection
name|dwfxSeq
init|=
name|pkg
operator|.
name|getRelationshipsByType
argument_list|(
literal|"http://schemas.autodesk.com/dwfx/2007/relationships/documentsequence"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dwfxSeq
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
literal|"model/vnd.dwfx+xps"
argument_list|)
return|;
block|}
else|else
block|{
comment|// Non-AutoCAD Package received
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|MediaType
name|detectIWork
parameter_list|(
name|ZipFile
name|zip
parameter_list|)
block|{
if|if
condition|(
name|zip
operator|.
name|getEntry
argument_list|(
name|IWorkPackageParser
operator|.
name|IWORK_COMMON_ENTRY
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// Locate the appropriate index file entry, and reads from that
comment|// the root element of the document. That is used to the identify
comment|// the correct type of the keynote container.
for|for
control|(
name|String
name|entryName
range|:
name|IWorkPackageParser
operator|.
name|IWORK_CONTENT_ENTRIES
control|)
block|{
name|IWORKDocumentType
name|type
init|=
name|IWORKDocumentType
operator|.
name|detectType
argument_list|(
name|zip
operator|.
name|getEntry
argument_list|(
name|entryName
argument_list|)
argument_list|,
name|zip
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
return|return
name|type
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
comment|// Not sure, fallback to the container type
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.apple.iwork"
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|MediaType
name|detectJar
parameter_list|(
name|ZipFile
name|zip
parameter_list|)
block|{
if|if
condition|(
name|zip
operator|.
name|getEntry
argument_list|(
literal|"META-INF/MANIFEST.MF"
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// It's a Jar file, or something based on Jar
comment|// Is it an Android APK?
if|if
condition|(
name|zip
operator|.
name|getEntry
argument_list|(
literal|"AndroidManifest.xml"
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.android.package-archive"
argument_list|)
return|;
block|}
comment|// Check for WAR and EAR
if|if
condition|(
name|zip
operator|.
name|getEntry
argument_list|(
literal|"WEB-INF/"
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-java-web-archive"
argument_list|)
return|;
block|}
if|if
condition|(
name|zip
operator|.
name|getEntry
argument_list|(
literal|"META-INF/application.xml"
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-java-enterprise-archive"
argument_list|)
return|;
block|}
comment|// Looks like a regular Jar Archive
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"java-archive"
argument_list|)
return|;
block|}
else|else
block|{
comment|// Some Android APKs miss the default Manifest
if|if
condition|(
name|zip
operator|.
name|getEntry
argument_list|(
literal|"AndroidManifest.xml"
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.android.package-archive"
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|MediaType
name|detectKmz
parameter_list|(
name|ZipFile
name|zip
parameter_list|)
block|{
name|boolean
name|kmlFound
init|=
literal|false
decl_stmt|;
name|Enumeration
argument_list|<
name|ZipArchiveEntry
argument_list|>
name|entries
init|=
name|zip
operator|.
name|getEntries
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
name|ZipArchiveEntry
name|entry
init|=
name|entries
operator|.
name|nextElement
argument_list|()
decl_stmt|;
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
operator|!
name|entry
operator|.
name|isDirectory
argument_list|()
operator|&&
name|name
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|==
operator|-
literal|1
operator|&&
name|name
operator|.
name|indexOf
argument_list|(
literal|'\\'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".kml"
argument_list|)
operator|&&
operator|!
name|kmlFound
condition|)
block|{
name|kmlFound
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
if|if
condition|(
name|kmlFound
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.google-earth.kmz"
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * To be considered as an IPA file, it needs to match all of these      */
specifier|private
specifier|static
name|HashSet
argument_list|<
name|Pattern
argument_list|>
name|ipaEntryPatterns
init|=
operator|new
name|HashSet
argument_list|<
name|Pattern
argument_list|>
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6545295886322115362L
decl_stmt|;
block|{
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/_CodeSignature/$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/_CodeSignature/CodeResources$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/CodeResources$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/Info\\.plist$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/PkgInfo$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/ResourceRules\\.plist$"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|static
name|MediaType
name|detectIpa
parameter_list|(
name|ZipFile
name|zip
parameter_list|)
block|{
comment|// Note - consider generalising this logic, if another format needs many regexp matching
name|Set
argument_list|<
name|Pattern
argument_list|>
name|tmpPatterns
init|=
operator|(
name|Set
argument_list|<
name|Pattern
argument_list|>
operator|)
name|ipaEntryPatterns
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|ZipArchiveEntry
argument_list|>
name|entries
init|=
name|zip
operator|.
name|getEntries
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
name|ZipArchiveEntry
name|entry
init|=
name|entries
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Pattern
argument_list|>
name|ip
init|=
name|tmpPatterns
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|ip
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|ip
operator|.
name|next
argument_list|()
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|ip
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tmpPatterns
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// We've found everything we need to find
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-itunes-ipa"
argument_list|)
return|;
block|}
block|}
comment|// If we get here, not all required entries were found
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

