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
name|epub
package|;
end_package

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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashMap
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
name|List
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
name|Map
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
name|ZipException
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
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|Field
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
name|parser
operator|.
name|utils
operator|.
name|ZipSalvager
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
name|xml
operator|.
name|DcXMLParser
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
name|ParserUtils
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

begin_comment
comment|/**  * Epub parser  */
end_comment

begin_class
specifier|public
class|class
name|EpubParser
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
literal|215176772484050550L
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
literal|"epub+zip"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ibooks+zip"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|Parser
name|meta
init|=
operator|new
name|DcXMLParser
argument_list|()
decl_stmt|;
specifier|private
name|Parser
name|content
init|=
operator|new
name|EpubContentParser
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
annotation|@
name|Field
name|boolean
name|streaming
init|=
literal|false
decl_stmt|;
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
comment|// Because an EPub file is often made up of multiple XHTML files,
comment|//  we need explicit control over the start and end of the document
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
name|IOException
name|caughtException
init|=
literal|null
decl_stmt|;
name|ContentHandler
name|childHandler
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
if|if
condition|(
name|streaming
condition|)
block|{
try|try
block|{
name|streamingParse
argument_list|(
name|stream
argument_list|,
name|childHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|bufferedParse
argument_list|(
name|stream
argument_list|,
name|childHandler
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|caughtException
operator|=
name|e
expr_stmt|;
block|}
block|}
comment|// Finish everything
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|caughtException
operator|!=
literal|null
condition|)
block|{
throw|throw
name|caughtException
throw|;
block|}
block|}
specifier|private
name|void
name|streamingParse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|bodyHandler
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
name|TikaException
throws|,
name|SAXException
block|{
name|ZipArchiveInputStream
name|zip
init|=
operator|new
name|ZipArchiveInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|ZipArchiveEntry
name|entry
init|=
name|zip
operator|.
name|getNextZipEntry
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
name|updateMimeType
argument_list|(
name|zip
argument_list|,
name|metadata
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
literal|"metadata.xml"
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
literal|".opf"
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
literal|".htm"
argument_list|)
operator|||
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".html"
argument_list|)
operator|||
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".xhtml"
argument_list|)
condition|)
block|{
name|content
operator|.
name|parse
argument_list|(
name|zip
argument_list|,
name|bodyHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|entry
operator|=
name|zip
operator|.
name|getNextZipEntry
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|updateMimeType
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|type
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|is
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
comment|//often has trailing new lines
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|type
operator|=
name|type
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
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
specifier|private
name|void
name|bufferedParse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|bodyHandler
parameter_list|,
name|XHTMLContentHandler
name|xhtml
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
name|TikaException
throws|,
name|SAXException
block|{
name|TikaInputStream
name|tis
decl_stmt|;
name|TemporaryResources
name|temporaryResources
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|TikaInputStream
operator|.
name|isTikaInputStream
argument_list|(
name|stream
argument_list|)
condition|)
block|{
name|tis
operator|=
name|TikaInputStream
operator|.
name|cast
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|temporaryResources
operator|=
operator|new
name|TemporaryResources
argument_list|()
expr_stmt|;
name|tis
operator|=
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
name|temporaryResources
argument_list|)
expr_stmt|;
block|}
name|ZipFile
name|zipFile
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zipFile
operator|=
operator|new
name|ZipFile
argument_list|(
name|tis
operator|.
name|getPath
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ZipException
name|e
parameter_list|)
block|{
name|ParserUtils
operator|.
name|recordParserFailure
argument_list|(
name|this
argument_list|,
name|e
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|trySalvage
argument_list|(
name|tis
operator|.
name|getPath
argument_list|()
argument_list|,
name|bodyHandler
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
comment|//if we had to wrap tis
if|if
condition|(
name|temporaryResources
operator|!=
literal|null
condition|)
block|{
name|tis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|bufferedParseZipFile
argument_list|(
name|zipFile
argument_list|,
name|bodyHandler
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|trySalvage
parameter_list|(
name|Path
name|brokenZip
parameter_list|,
name|ContentHandler
name|bodyHandler
parameter_list|,
name|XHTMLContentHandler
name|xhtml
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
name|TikaException
throws|,
name|SAXException
block|{
name|TemporaryResources
name|resources
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
try|try
block|{
name|Path
name|salvaged
init|=
name|resources
operator|.
name|createTempFile
argument_list|()
decl_stmt|;
name|ZipSalvager
operator|.
name|salvageCopy
argument_list|(
name|brokenZip
operator|.
name|toFile
argument_list|()
argument_list|,
name|salvaged
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
name|ZipFile
name|zipFile
init|=
operator|new
name|ZipFile
argument_list|(
name|salvaged
operator|.
name|toFile
argument_list|()
argument_list|)
init|)
block|{
name|success
operator|=
name|bufferedParseZipFile
argument_list|(
name|zipFile
argument_list|,
name|bodyHandler
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|salvaged
argument_list|)
init|)
block|{
name|streamingParse
argument_list|(
name|is
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|resources
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|bufferedParseZipFile
parameter_list|(
name|ZipFile
name|zipFile
parameter_list|,
name|ContentHandler
name|bodyHandler
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|boolean
name|isStrict
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
name|String
name|rootOPF
init|=
name|getRoot
argument_list|(
name|zipFile
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootOPF
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ZipArchiveEntry
name|zae
init|=
name|zipFile
operator|.
name|getEntry
argument_list|(
name|rootOPF
argument_list|)
decl_stmt|;
if|if
condition|(
name|zae
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|zipFile
operator|.
name|canReadEntryData
argument_list|(
name|zae
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|meta
operator|.
name|parse
argument_list|(
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|zae
argument_list|)
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
name|ContentOrderScraper
name|contentOrderScraper
init|=
operator|new
name|ContentOrderScraper
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|zae
argument_list|)
init|)
block|{
name|XMLReaderUtils
operator|.
name|parseSAX
argument_list|(
name|is
argument_list|,
operator|new
name|OfflineContentHandler
argument_list|(
name|contentOrderScraper
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|//if no content items, false
if|if
condition|(
name|contentOrderScraper
operator|.
name|contentItems
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|relativePath
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|rootOPF
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|relativePath
operator|=
name|rootOPF
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|rootOPF
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isStrict
condition|)
block|{
name|int
name|found
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|contentOrderScraper
operator|.
name|contentItems
control|)
block|{
name|HRefMediaPair
name|hRefMediaPair
init|=
name|contentOrderScraper
operator|.
name|locationMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|hRefMediaPair
operator|!=
literal|null
operator|&&
name|hRefMediaPair
operator|.
name|href
operator|!=
literal|null
condition|)
block|{
name|zae
operator|=
name|zipFile
operator|.
name|getEntry
argument_list|(
name|relativePath
operator|+
name|hRefMediaPair
operator|.
name|href
argument_list|)
expr_stmt|;
if|if
condition|(
name|zae
operator|!=
literal|null
operator|&&
name|zipFile
operator|.
name|canReadEntryData
argument_list|(
name|zae
argument_list|)
condition|)
block|{
name|found
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|//if not perfect match btwn items and readable items
comment|//return false
if|if
condition|(
name|found
operator|!=
name|contentOrderScraper
operator|.
name|contentItems
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|extractMetadata
argument_list|(
name|zipFile
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|processed
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|contentOrderScraper
operator|.
name|contentItems
control|)
block|{
name|HRefMediaPair
name|hRefMediaPair
init|=
name|contentOrderScraper
operator|.
name|locationMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|hRefMediaPair
operator|!=
literal|null
operator|&&
name|hRefMediaPair
operator|.
name|href
operator|!=
literal|null
operator|&&
name|hRefMediaPair
operator|.
name|href
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
operator|.
name|contains
argument_list|(
literal|"html"
argument_list|)
condition|)
block|{
name|zae
operator|=
name|zipFile
operator|.
name|getEntry
argument_list|(
name|relativePath
operator|+
name|hRefMediaPair
operator|.
name|href
argument_list|)
expr_stmt|;
if|if
condition|(
name|zae
operator|!=
literal|null
condition|)
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|zae
argument_list|)
init|)
block|{
name|content
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|bodyHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|processed
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//now handle embedded files
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
init|=
name|EmbeddedDocumentUtil
operator|.
name|getEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|contentOrderScraper
operator|.
name|locationMap
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|processed
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|HRefMediaPair
name|hRefMediaPair
init|=
name|contentOrderScraper
operator|.
name|locationMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldHandleEmbedded
argument_list|(
name|hRefMediaPair
operator|.
name|media
argument_list|)
condition|)
block|{
name|handleEmbedded
argument_list|(
name|zipFile
argument_list|,
name|relativePath
argument_list|,
name|hRefMediaPair
argument_list|,
name|embeddedDocumentExtractor
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|shouldHandleEmbedded
parameter_list|(
name|String
name|media
parameter_list|)
block|{
if|if
condition|(
name|media
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|lc
init|=
name|media
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
if|if
condition|(
name|lc
operator|.
name|contains
argument_list|(
literal|"css"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|lc
operator|.
name|contains
argument_list|(
literal|"svg"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|lc
operator|.
name|endsWith
argument_list|(
literal|"/xml"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|lc
operator|.
name|contains
argument_list|(
literal|"x-ibooks"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|handleEmbedded
parameter_list|(
name|ZipFile
name|zipFile
parameter_list|,
name|String
name|relativePath
parameter_list|,
name|HRefMediaPair
name|hRefMediaPair
parameter_list|,
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|parentMetadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
if|if
condition|(
name|hRefMediaPair
operator|.
name|href
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|fullPath
init|=
name|relativePath
operator|+
name|hRefMediaPair
operator|.
name|href
decl_stmt|;
name|ZipArchiveEntry
name|ze
init|=
name|zipFile
operator|.
name|getEntry
argument_list|(
name|fullPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|zipFile
operator|.
name|canReadEntryData
argument_list|(
name|ze
argument_list|)
condition|)
block|{
return|return;
block|}
name|Metadata
name|embeddedMetadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|hRefMediaPair
operator|.
name|media
argument_list|)
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
name|hRefMediaPair
operator|.
name|media
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|embeddedDocumentExtractor
operator|.
name|shouldParseEmbedded
argument_list|(
name|embeddedMetadata
argument_list|)
condition|)
block|{
return|return;
block|}
name|TikaInputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|ze
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|parentMetadata
argument_list|)
expr_stmt|;
return|return;
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"embedded"
argument_list|)
expr_stmt|;
try|try
block|{
name|embeddedDocumentExtractor
operator|.
name|parseEmbedded
argument_list|(
name|stream
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|embeddedMetadata
argument_list|,
literal|false
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
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractMetadata
parameter_list|(
name|ZipFile
name|zipFile
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
name|TikaException
throws|,
name|SAXException
block|{
name|ZipArchiveEntry
name|zae
init|=
name|zipFile
operator|.
name|getEntry
argument_list|(
literal|"mimetype"
argument_list|)
decl_stmt|;
if|if
condition|(
name|zae
operator|!=
literal|null
operator|&&
name|zipFile
operator|.
name|canReadEntryData
argument_list|(
name|zae
argument_list|)
condition|)
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|zae
argument_list|)
init|)
block|{
name|updateMimeType
argument_list|(
name|is
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
name|zae
operator|=
name|zipFile
operator|.
name|getEntry
argument_list|(
literal|"metadata.xml"
argument_list|)
expr_stmt|;
if|if
condition|(
name|zae
operator|!=
literal|null
operator|&&
name|zipFile
operator|.
name|canReadEntryData
argument_list|(
name|zae
argument_list|)
condition|)
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|zae
argument_list|)
init|)
block|{
name|meta
operator|.
name|parse
argument_list|(
name|is
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
block|}
block|}
specifier|private
name|String
name|getRoot
parameter_list|(
name|ZipFile
name|zipFile
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
name|ZipArchiveEntry
name|container
init|=
name|zipFile
operator|.
name|getEntry
argument_list|(
literal|"META-INF/container.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|RootFinder
name|rootFinder
init|=
operator|new
name|RootFinder
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|zipFile
operator|.
name|getInputStream
argument_list|(
name|container
argument_list|)
init|)
block|{
name|XMLReaderUtils
operator|.
name|parseSAX
argument_list|(
name|is
argument_list|,
operator|new
name|OfflineContentHandler
argument_list|(
name|rootFinder
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|rootFinder
operator|.
name|root
return|;
block|}
else|else
block|{
name|Enumeration
argument_list|<
name|ZipArchiveEntry
argument_list|>
name|entryEnum
init|=
name|zipFile
operator|.
name|getEntries
argument_list|()
decl_stmt|;
while|while
condition|(
name|entryEnum
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|ZipArchiveEntry
name|ze
init|=
name|entryEnum
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
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|".opf"
argument_list|)
operator|&&
name|zipFile
operator|.
name|canReadEntryData
argument_list|(
name|ze
argument_list|)
condition|)
block|{
return|return
name|ze
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|RootFinder
extends|extends
name|DefaultHandler
block|{
name|String
name|root
init|=
literal|null
decl_stmt|;
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
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"rootfile"
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|root
operator|=
name|XMLReaderUtils
operator|.
name|getAttrValue
argument_list|(
literal|"full-path"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|ContentOrderScraper
extends|extends
name|DefaultHandler
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|HRefMediaPair
argument_list|>
name|locationMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|contentItems
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|inManifest
init|=
literal|false
decl_stmt|;
name|boolean
name|inSpine
init|=
literal|false
decl_stmt|;
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
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"manifest"
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inManifest
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"spine"
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inSpine
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|inManifest
condition|)
block|{
if|if
condition|(
literal|"item"
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|String
name|id
init|=
name|XMLReaderUtils
operator|.
name|getAttrValue
argument_list|(
literal|"id"
argument_list|,
name|atts
argument_list|)
decl_stmt|;
name|String
name|href
init|=
name|XMLReaderUtils
operator|.
name|getAttrValue
argument_list|(
literal|"href"
argument_list|,
name|atts
argument_list|)
decl_stmt|;
name|String
name|mime
init|=
name|XMLReaderUtils
operator|.
name|getAttrValue
argument_list|(
literal|"media-type"
argument_list|,
name|atts
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|href
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|href
operator|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|href
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{                         }
name|locationMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|HRefMediaPair
argument_list|(
name|href
argument_list|,
name|mime
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|inSpine
condition|)
block|{
if|if
condition|(
literal|"itemRef"
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|String
name|id
init|=
name|XMLReaderUtils
operator|.
name|getAttrValue
argument_list|(
literal|"idref"
argument_list|,
name|atts
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|contentItems
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"manifest"
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inManifest
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"spine"
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inSpine
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|HRefMediaPair
block|{
specifier|private
specifier|final
name|String
name|href
decl_stmt|;
specifier|private
specifier|final
name|String
name|media
decl_stmt|;
name|HRefMediaPair
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|media
parameter_list|)
block|{
name|this
operator|.
name|href
operator|=
name|href
expr_stmt|;
name|this
operator|.
name|media
operator|=
name|media
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HRefMediaPair{"
operator|+
literal|"href='"
operator|+
name|href
operator|+
literal|'\''
operator|+
literal|", media='"
operator|+
name|media
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
block|}
block|}
end_class

end_unit

