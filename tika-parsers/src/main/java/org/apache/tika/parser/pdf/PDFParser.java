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
name|pdf
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
name|Calendar
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|cos
operator|.
name|COSArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|cos
operator|.
name|COSBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|cos
operator|.
name|COSName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|cos
operator|.
name|COSString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|io
operator|.
name|RandomAccess
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|PDDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|PDDocumentCatalog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|PDDocumentInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|PDDocumentNameDictionary
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|PDEmbeddedFilesNameTreeNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|common
operator|.
name|filespecification
operator|.
name|PDComplexFileSpecification
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|pdmodel
operator|.
name|common
operator|.
name|filespecification
operator|.
name|PDEmbeddedFile
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
name|ParsingEmbeddedDocumentExtractor
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
name|PagedText
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
name|Property
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
name|EmbeddedContentHandler
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
comment|/**  * PDF parser.  *<p>  * This parser can process also encrypted PDF documents if the required  * password is given as a part of the input metadata associated with a  * document. If no password is given, then this parser will try decrypting  * the document using the empty password that's often used with PDFs. If  * the PDF contains any embedded documents (for example as part of a PDF  * package) then this parser will use the {@link EmbeddedDocumentExtractor}  * to handle them.  */
end_comment

begin_class
specifier|public
class|class
name|PDFParser
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
literal|752276948656079347L
decl_stmt|;
comment|// True if we let PDFBox "guess" where spaces should go:
specifier|private
name|boolean
name|enableAutoSpace
init|=
literal|true
decl_stmt|;
comment|// True if we let PDFBox remove duplicate overlapping text:
specifier|private
name|boolean
name|suppressDuplicateOverlappingText
decl_stmt|;
comment|// True if we extract annotation text ourselves
comment|// (workaround for PDFBOX-1143):
specifier|private
name|boolean
name|extractAnnotationText
init|=
literal|true
decl_stmt|;
comment|// True if we should sort text tokens by position
comment|// (necessary for some PDFs, but messes up other PDFs):
specifier|private
name|boolean
name|sortByPosition
init|=
literal|false
decl_stmt|;
comment|/**      * Metadata key for giving the document password to the parser.      *      * @since Apache Tika 0.5      * @deprecated Supply a {@link PasswordProvider} on the {@link ParseContext} instead      */
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD
init|=
literal|"org.apache.tika.parser.pdf.password"
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
literal|"pdf"
argument_list|)
argument_list|)
decl_stmt|;
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
name|PDDocument
name|pdfDocument
init|=
literal|null
decl_stmt|;
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
try|try
block|{
comment|// PDFBox can process entirely in memory, or can use a temp file
comment|//  for unpacked / processed resources
comment|// Decide which to do based on if we're reading from a file or not already
name|TikaInputStream
name|tstream
init|=
name|TikaInputStream
operator|.
name|cast
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|tstream
operator|!=
literal|null
operator|&&
name|tstream
operator|.
name|hasFile
argument_list|()
condition|)
block|{
comment|// File based, take that as a cue to use a temporary file
name|RandomAccess
name|scratchFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|tmp
operator|.
name|createTemporaryFile
argument_list|()
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|pdfDocument
operator|=
name|PDDocument
operator|.
name|load
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|scratchFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Go for the normal, stream based in-memory parsing
name|pdfDocument
operator|=
name|PDDocument
operator|.
name|load
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pdfDocument
operator|.
name|isEncrypted
argument_list|()
condition|)
block|{
name|String
name|password
init|=
literal|null
decl_stmt|;
comment|// Did they supply a new style Password Provider?
name|PasswordProvider
name|passwordProvider
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
name|passwordProvider
operator|!=
literal|null
condition|)
block|{
name|password
operator|=
name|passwordProvider
operator|.
name|getPassword
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
comment|// Fall back on the old style metadata if set
if|if
condition|(
name|password
operator|==
literal|null
operator|&&
name|metadata
operator|.
name|get
argument_list|(
name|PASSWORD
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|password
operator|=
name|metadata
operator|.
name|get
argument_list|(
name|PASSWORD
argument_list|)
expr_stmt|;
block|}
comment|// If no password is given, use an empty string as the default
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
name|password
operator|=
literal|""
expr_stmt|;
block|}
try|try
block|{
name|pdfDocument
operator|.
name|decrypt
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore
block|}
block|}
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/pdf"
argument_list|)
expr_stmt|;
name|extractMetadata
argument_list|(
name|pdfDocument
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|PDF2XHTML
operator|.
name|process
argument_list|(
name|pdfDocument
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|extractAnnotationText
argument_list|,
name|enableAutoSpace
argument_list|,
name|suppressDuplicateOverlappingText
argument_list|,
name|sortByPosition
argument_list|)
expr_stmt|;
name|extractEmbeddedDocuments
argument_list|(
name|context
argument_list|,
name|pdfDocument
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|pdfDocument
operator|!=
literal|null
condition|)
block|{
name|pdfDocument
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractEmbeddedDocuments
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|PDDocument
name|document
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|PDDocumentCatalog
name|catalog
init|=
name|document
operator|.
name|getDocumentCatalog
argument_list|()
decl_stmt|;
name|PDDocumentNameDictionary
name|names
init|=
name|catalog
operator|.
name|getNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|names
operator|!=
literal|null
condition|)
block|{
name|PDEmbeddedFilesNameTreeNode
name|embeddedFiles
init|=
name|names
operator|.
name|getEmbeddedFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|embeddedFiles
operator|!=
literal|null
condition|)
block|{
name|EmbeddedDocumentExtractor
name|embeddedExtractor
init|=
name|context
operator|.
name|get
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|embeddedExtractor
operator|==
literal|null
condition|)
block|{
name|embeddedExtractor
operator|=
operator|new
name|ParsingEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ent
range|:
name|embeddedFiles
operator|.
name|getNames
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|PDComplexFileSpecification
name|spec
init|=
operator|(
name|PDComplexFileSpecification
operator|)
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|PDEmbeddedFile
name|file
init|=
name|spec
operator|.
name|getEmbeddedFile
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|// TODO: other metadata?
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|file
operator|.
name|getSubtype
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
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
name|file
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|embeddedExtractor
operator|.
name|shouldParseEmbedded
argument_list|(
name|metadata
argument_list|)
condition|)
block|{
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|file
operator|.
name|createInputStream
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|embeddedExtractor
operator|.
name|parseEmbedded
argument_list|(
name|stream
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|metadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
block|}
block|}
block|}
block|}
specifier|private
name|void
name|extractMetadata
parameter_list|(
name|PDDocument
name|document
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|TikaException
block|{
name|PDDocumentInformation
name|info
init|=
name|document
operator|.
name|getDocumentInformation
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PagedText
operator|.
name|N_PAGES
argument_list|,
name|document
operator|.
name|getNumberOfPages
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|info
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|AUTHOR
argument_list|,
name|info
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|,
name|info
operator|.
name|getCreator
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|KEYWORDS
argument_list|,
name|info
operator|.
name|getKeywords
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
literal|"producer"
argument_list|,
name|info
operator|.
name|getProducer
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|,
name|info
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
literal|"trapped"
argument_list|,
name|info
operator|.
name|getTrapped
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|addMetadata
argument_list|(
name|metadata
argument_list|,
literal|"created"
argument_list|,
name|info
operator|.
name|getCreationDate
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|CREATION_DATE
argument_list|,
name|info
operator|.
name|getCreationDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Invalid date format, just ignore
block|}
try|try
block|{
name|Calendar
name|modified
init|=
name|info
operator|.
name|getModificationDate
argument_list|()
decl_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|Metadata
operator|.
name|LAST_MODIFIED
argument_list|,
name|modified
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|modified
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Invalid date format, just ignore
block|}
comment|// All remaining metadata is custom
comment|// Copy this over as-is
name|List
argument_list|<
name|String
argument_list|>
name|handledMetadata
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"Author"
block|,
literal|"Creator"
block|,
literal|"CreationDate"
block|,
literal|"ModDate"
block|,
literal|"Keywords"
block|,
literal|"Producer"
block|,
literal|"Subject"
block|,
literal|"Title"
block|,
literal|"Trapped"
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|COSName
name|key
range|:
name|info
operator|.
name|getDictionary
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|key
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|handledMetadata
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|name
argument_list|,
name|info
operator|.
name|getDictionary
argument_list|()
operator|.
name|getDictionaryObject
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|addMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|Calendar
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|getTime
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|property
parameter_list|,
name|Calendar
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|property
argument_list|,
name|value
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Used when processing custom metadata entries, as PDFBox won't do      *  the conversion for us in the way it does for the standard ones      */
specifier|private
name|void
name|addMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|COSBase
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|COSArray
condition|)
block|{
for|for
control|(
name|COSBase
name|v
range|:
operator|(
operator|(
name|COSArray
operator|)
name|value
operator|)
operator|.
name|toList
argument_list|()
control|)
block|{
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|COSString
condition|)
block|{
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|name
argument_list|,
operator|(
operator|(
name|COSString
operator|)
name|value
operator|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|name
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  If true (the default), the parser should estimate      *  where spaces should be inserted between words.  For      *  many PDFs this is necessary as they do not include      *  explicit whitespace characters.      */
specifier|public
name|void
name|setEnableAutoSpace
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|enableAutoSpace
operator|=
name|v
expr_stmt|;
block|}
comment|/** @see #setEnableAutoSpace. */
specifier|public
name|boolean
name|getEnableAutoSpace
parameter_list|()
block|{
return|return
name|enableAutoSpace
return|;
block|}
comment|/**      * If true (the default), text in annotations will be      * extracted.      */
specifier|public
name|void
name|setExtractAnnotationText
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|extractAnnotationText
operator|=
name|v
expr_stmt|;
block|}
comment|/**      * If true, text in annotations will be extracted.      */
specifier|public
name|boolean
name|getExtractAnnotationText
parameter_list|()
block|{
return|return
name|extractAnnotationText
return|;
block|}
comment|/**      *  If true, the parser should try to remove duplicated      *  text over the same region.  This is needed for some      *  PDFs that achieve bolding by re-writing the same      *  text in the same area.  Note that this can      *  slow down extraction substantially (PDFBOX-956) and      *  sometimes remove characters that were not in fact      *  duplicated (PDFBOX-1155).  By default this is disabled.      */
specifier|public
name|void
name|setSuppressDuplicateOverlappingText
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|suppressDuplicateOverlappingText
operator|=
name|v
expr_stmt|;
block|}
comment|/** @see #setSuppressDuplicateOverlappingText. */
specifier|public
name|boolean
name|getSuppressDuplicateOverlappingText
parameter_list|()
block|{
return|return
name|suppressDuplicateOverlappingText
return|;
block|}
comment|/**      *  If true, sort text tokens by their x/y position      *  before extracting text.  This may be necessary for      *  some PDFs (if the text tokens are not rendered "in      *  order"), while for other PDFs it can produce the      *  wrong result (for example if there are 2 columns,      *  the text will be interleaved).  Default is false.      */
specifier|public
name|void
name|setSortByPosition
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|sortByPosition
operator|=
name|v
expr_stmt|;
block|}
comment|/** @see #setSortByPosition. */
specifier|public
name|boolean
name|getSortByPosition
parameter_list|()
block|{
return|return
name|sortByPosition
return|;
block|}
block|}
end_class

end_unit

