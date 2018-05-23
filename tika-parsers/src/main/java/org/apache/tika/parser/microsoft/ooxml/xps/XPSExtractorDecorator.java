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
operator|.
name|ooxml
operator|.
name|xps
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
name|IOUtils
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
name|poi
operator|.
name|POIXMLDocument
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
name|POIXMLTextExtractor
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
name|PackageRelationship
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
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|ZipPackage
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
name|util
operator|.
name|ZipEntrySource
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
name|microsoft
operator|.
name|ooxml
operator|.
name|AbstractOOXMLExtractor
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
name|OfflineContentHandler
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
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|ExceptionUtils
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
name|utils
operator|.
name|XMLReaderUtils
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
name|Attributes
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
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_class
specifier|public
class|class
name|XPSExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
specifier|private
specifier|static
name|String
name|XPS_DOCUMENT
init|=
literal|"http://schemas.microsoft.com/xps/2005/06/fixedrepresentation"
decl_stmt|;
specifier|private
specifier|final
name|ParseContext
name|context
decl_stmt|;
specifier|private
specifier|final
name|ZipPackage
name|pkg
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Metadata
argument_list|>
name|embeddedImages
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|XPSExtractorDecorator
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|POIXMLTextExtractor
name|extractor
parameter_list|)
throws|throws
name|TikaException
block|{
name|super
argument_list|(
name|context
argument_list|,
name|extractor
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
if|if
condition|(
name|extractor
operator|.
name|getPackage
argument_list|()
operator|instanceof
name|ZipPackage
condition|)
block|{
name|this
operator|.
name|pkg
operator|=
operator|(
name|ZipPackage
operator|)
name|extractor
operator|.
name|getPackage
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"OPCPackage must be a ZipPackage"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|POIXMLDocument
name|getDocument
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|buildXHTML
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|PackageRelationshipCollection
name|prc
init|=
name|pkg
operator|.
name|getRelationshipsByType
argument_list|(
name|XPS_DOCUMENT
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prc
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PackageRelationship
name|pr
init|=
name|prc
operator|.
name|getRelationship
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//there should only be one.
comment|//in the test file, this points to FixedDocSeq.fdseq
try|try
block|{
name|handleDocuments
argument_list|(
name|pr
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|//now handle embedded images
if|if
condition|(
name|embeddedImages
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|EmbeddedDocumentUtil
name|embeddedDocumentUtil
init|=
operator|new
name|EmbeddedDocumentUtil
argument_list|(
name|context
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Metadata
argument_list|>
name|embeddedImage
range|:
name|embeddedImages
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|zipPath
init|=
name|embeddedImage
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
name|embeddedImage
operator|.
name|getValue
argument_list|()
decl_stmt|;
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
name|handleEmbeddedImage
argument_list|(
name|zipPath
argument_list|,
name|metadata
argument_list|,
name|embeddedDocumentUtil
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|handleEmbeddedImage
parameter_list|(
name|String
name|zipPath
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|EmbeddedDocumentUtil
name|embeddedDocumentUtil
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|getZipStream
argument_list|(
name|zipPath
argument_list|,
name|pkg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|TikaException
name|e
parameter_list|)
block|{
comment|//store this exception in the parent's metadata
name|EmbeddedDocumentUtil
operator|.
name|recordEmbeddedStreamException
argument_list|(
name|e
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|embeddedDocumentUtil
operator|.
name|parseEmbedded
argument_list|(
name|stream
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleDocuments
parameter_list|(
name|PackageRelationship
name|packageRelationship
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
init|(
name|InputStream
name|stream
init|=
name|pkg
operator|.
name|getPart
argument_list|(
name|packageRelationship
argument_list|)
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|XMLReaderUtils
operator|.
name|parseSAX
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
operator|new
name|OfflineContentHandler
argument_list|(
operator|new
name|EmbeddedContentHandler
argument_list|(
operator|new
name|FixedDocSeqHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|PackagePart
argument_list|>
name|getMainDocumentParts
parameter_list|()
throws|throws
name|TikaException
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
specifier|private
class|class
name|FixedDocSeqHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|final
specifier|static
name|String
name|DOCUMENT_REFERENCE
init|=
literal|"DocumentReference"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|SOURCE
init|=
literal|"Source"
decl_stmt|;
specifier|private
specifier|final
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
specifier|private
name|FixedDocSeqHandler
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|)
block|{
name|this
operator|.
name|xhtml
operator|=
name|xhtml
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|DOCUMENT_REFERENCE
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|lName
init|=
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|SOURCE
operator|.
name|equals
argument_list|(
name|lName
argument_list|)
condition|)
block|{
name|handleDocumentRef
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|handleDocumentRef
parameter_list|(
name|String
name|docRef
parameter_list|)
throws|throws
name|SAXException
block|{
comment|//docRef is a path to a FixedDocumentSequence document,
comment|// e.g. /Documents/1/FixedDoc.fdoc
comment|//relative root is /Documents/1 ..need this Pages...
name|String
name|relativeRoot
init|=
literal|null
decl_stmt|;
name|int
name|i
init|=
name|docRef
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|relativeRoot
operator|=
name|docRef
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|relativeRoot
operator|=
literal|""
expr_stmt|;
block|}
name|String
name|zipPath
init|=
operator|(
name|docRef
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|?
name|docRef
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
name|docRef
operator|)
decl_stmt|;
if|if
condition|(
name|pkg
operator|instanceof
name|ZipPackage
condition|)
block|{
try|try
init|(
name|InputStream
name|stream
init|=
name|getZipStream
argument_list|(
name|zipPath
argument_list|,
name|pkg
argument_list|)
init|)
block|{
name|XMLReaderUtils
operator|.
name|parseSAX
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
operator|new
name|OfflineContentHandler
argument_list|(
operator|new
name|EmbeddedContentHandler
argument_list|(
operator|new
name|PageContentPartHandler
argument_list|(
name|relativeRoot
argument_list|,
name|xhtml
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|TikaException
argument_list|(
literal|"IOException trying to read: "
operator|+
name|docRef
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
operator|new
name|TikaException
argument_list|(
literal|"Package must be ZipPackage"
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|PageContentPartHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|static
specifier|final
name|String
name|PAGE_CONTENT
init|=
literal|"PageContent"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE
init|=
literal|"Source"
decl_stmt|;
specifier|private
specifier|final
name|String
name|relativeRoot
decl_stmt|;
specifier|private
specifier|final
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
specifier|private
name|PageContentPartHandler
parameter_list|(
name|String
name|relativeRoot
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
block|{
name|this
operator|.
name|relativeRoot
operator|=
name|relativeRoot
expr_stmt|;
name|this
operator|.
name|xhtml
operator|=
name|xhtml
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|PAGE_CONTENT
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|pagePath
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|SOURCE
operator|.
name|equals
argument_list|(
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|pagePath
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|pagePath
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|pagePath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|pagePath
operator|=
name|relativeRoot
operator|+
literal|"/"
operator|+
name|pagePath
expr_stmt|;
block|}
comment|//trim initial /
if|if
condition|(
name|pagePath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|pagePath
operator|=
name|pagePath
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|InputStream
name|stream
init|=
name|getZipStream
argument_list|(
name|pagePath
argument_list|,
name|pkg
argument_list|)
init|)
block|{
name|XMLReaderUtils
operator|.
name|parseSAX
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
operator|new
name|OfflineContentHandler
argument_list|(
operator|new
name|XPSPageContentHandler
argument_list|(
name|xhtml
argument_list|,
name|embeddedImages
argument_list|)
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|InputStream
name|getZipStream
parameter_list|(
name|String
name|zipPath
parameter_list|,
name|ZipPackage
name|zipPackage
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|String
name|targPath
init|=
operator|(
name|zipPath
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|zipPath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|?
name|zipPath
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
name|zipPath
operator|)
decl_stmt|;
name|ZipEntrySource
name|zipEntrySource
init|=
name|zipPackage
operator|.
name|getZipArchive
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|zipEntryEnumeration
init|=
name|zipEntrySource
operator|.
name|getEntries
argument_list|()
decl_stmt|;
name|ZipEntry
name|zipEntry
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|zipEntryEnumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|ZipEntry
name|ze
init|=
name|zipEntryEnumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|ze
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|targPath
argument_list|)
condition|)
block|{
name|zipEntry
operator|=
name|ze
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|zipEntry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Couldn't find required zip entry: "
operator|+
name|zipPath
argument_list|)
throw|;
block|}
return|return
name|zipEntrySource
operator|.
name|getInputStream
argument_list|(
name|zipEntry
argument_list|)
return|;
block|}
block|}
end_class

end_unit

