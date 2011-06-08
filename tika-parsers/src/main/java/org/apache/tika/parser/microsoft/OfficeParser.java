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
name|security
operator|.
name|GeneralSecurityException
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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hdgf
operator|.
name|extractor
operator|.
name|VisioTextExtractor
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
name|hpbf
operator|.
name|extractor
operator|.
name|PublisherTextExtractor
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
name|crypt
operator|.
name|Decryptor
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
name|crypt
operator|.
name|EcmaDecryptor
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
name|crypt
operator|.
name|EncryptionInfo
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
name|NPOIFSFileSystem
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
name|microsoft
operator|.
name|ooxml
operator|.
name|OOXMLParser
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
comment|/**  * Defines a Microsoft document content extractor.  */
end_comment

begin_class
specifier|public
class|class
name|OfficeParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|7393462244028653479L
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
name|POIFSDocumentType
operator|.
name|WORKBOOK
operator|.
name|type
argument_list|,
name|POIFSDocumentType
operator|.
name|OLE10_NATIVE
operator|.
name|type
argument_list|,
name|POIFSDocumentType
operator|.
name|WORDDOCUMENT
operator|.
name|type
argument_list|,
name|POIFSDocumentType
operator|.
name|UNKNOWN
operator|.
name|type
argument_list|,
name|POIFSDocumentType
operator|.
name|ENCRYPTED
operator|.
name|type
argument_list|,
name|POIFSDocumentType
operator|.
name|POWERPOINT
operator|.
name|type
argument_list|,
name|POIFSDocumentType
operator|.
name|PUBLISHER
operator|.
name|type
argument_list|,
name|POIFSDocumentType
operator|.
name|VISIO
operator|.
name|type
argument_list|,
name|POIFSDocumentType
operator|.
name|OUTLOOK
operator|.
name|type
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.sheet.binary.macroenabled.12"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
enum|enum
name|POIFSDocumentType
block|{
name|WORKBOOK
argument_list|(
literal|"xls"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel"
argument_list|)
argument_list|)
block|,
name|OLE10_NATIVE
argument_list|(
literal|"ole"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-msoffice"
argument_list|)
argument_list|)
block|,
name|WORDDOCUMENT
argument_list|(
literal|"doc"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"msword"
argument_list|)
argument_list|)
block|,
name|UNKNOWN
argument_list|(
literal|"unknown"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-msoffice"
argument_list|)
argument_list|)
block|,
name|ENCRYPTED
argument_list|(
literal|"ole"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-msoffice"
argument_list|)
argument_list|)
block|,
name|POWERPOINT
argument_list|(
literal|"ppt"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint"
argument_list|)
argument_list|)
block|,
name|PUBLISHER
argument_list|(
literal|"pub"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-mspublisher"
argument_list|)
argument_list|)
block|,
name|VISIO
argument_list|(
literal|"vsd"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.visio"
argument_list|)
argument_list|)
block|,
name|WORKS
argument_list|(
literal|"wps"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-works"
argument_list|)
argument_list|)
block|,
name|OUTLOOK
argument_list|(
literal|"msg"
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-outlook"
argument_list|)
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|extension
decl_stmt|;
specifier|private
specifier|final
name|MediaType
name|type
decl_stmt|;
name|POIFSDocumentType
parameter_list|(
name|String
name|extension
parameter_list|,
name|MediaType
name|type
parameter_list|)
block|{
name|this
operator|.
name|extension
operator|=
name|extension
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|getExtension
parameter_list|()
block|{
return|return
name|extension
return|;
block|}
specifier|public
name|MediaType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
specifier|static
name|POIFSDocumentType
name|detectType
parameter_list|(
name|POIFSFileSystem
name|fs
parameter_list|)
block|{
return|return
name|detectType
argument_list|(
name|fs
operator|.
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|POIFSDocumentType
name|detectType
parameter_list|(
name|NPOIFSFileSystem
name|fs
parameter_list|)
block|{
return|return
name|detectType
argument_list|(
name|fs
operator|.
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|POIFSDocumentType
name|detectType
parameter_list|(
name|DirectoryEntry
name|node
parameter_list|)
block|{
for|for
control|(
name|Entry
name|entry
range|:
name|node
control|)
block|{
name|POIFSDocumentType
name|type
init|=
name|detectType
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|UNKNOWN
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
return|return
name|UNKNOWN
return|;
block|}
specifier|public
specifier|static
name|POIFSDocumentType
name|detectType
parameter_list|(
name|Entry
name|entry
parameter_list|)
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
literal|"Workbook"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|WORKBOOK
return|;
block|}
if|if
condition|(
literal|"EncryptedPackage"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|ENCRYPTED
return|;
block|}
if|if
condition|(
literal|"WordDocument"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|WORDDOCUMENT
return|;
block|}
if|if
condition|(
literal|"Quill"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|PUBLISHER
return|;
block|}
if|if
condition|(
literal|"PowerPoint Document"
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
return|return
name|POWERPOINT
return|;
block|}
if|if
condition|(
literal|"VisioDocument"
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
return|return
name|VISIO
return|;
block|}
if|if
condition|(
literal|"CONTENTS"
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
return|return
name|WORKS
return|;
block|}
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"__substg1.0_"
argument_list|)
condition|)
block|{
return|return
name|OUTLOOK
return|;
block|}
if|if
condition|(
literal|"\u0001Ole10Native"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|POIFSDocumentType
operator|.
name|OLE10_NATIVE
return|;
block|}
return|return
name|UNKNOWN
return|;
block|}
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
comment|/**      * Extracts properties and text from an MS Document input stream      */
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
name|NPOIFSFileSystem
name|filesystem
decl_stmt|;
if|if
condition|(
name|stream
operator|instanceof
name|TikaInputStream
condition|)
block|{
name|TikaInputStream
name|tstream
init|=
operator|(
name|TikaInputStream
operator|)
name|stream
decl_stmt|;
if|if
condition|(
name|tstream
operator|.
name|getOpenContainer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|filesystem
operator|=
operator|(
name|NPOIFSFileSystem
operator|)
name|tstream
operator|.
name|getOpenContainer
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tstream
operator|.
name|hasFile
argument_list|()
condition|)
block|{
name|filesystem
operator|=
operator|new
name|NPOIFSFileSystem
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
name|filesystem
operator|=
operator|new
name|NPOIFSFileSystem
argument_list|(
name|tstream
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|filesystem
operator|=
operator|new
name|NPOIFSFileSystem
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
comment|// Parse summary entries first, to make metadata available early
operator|new
name|SummaryExtractor
argument_list|(
name|metadata
argument_list|)
operator|.
name|parseSummaries
argument_list|(
name|filesystem
argument_list|)
expr_stmt|;
comment|// Parse remaining document entries
name|boolean
name|outlookExtracted
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|filesystem
operator|.
name|getRoot
argument_list|()
control|)
block|{
name|POIFSDocumentType
name|type
init|=
name|POIFSDocumentType
operator|.
name|detectType
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|POIFSDocumentType
operator|.
name|UNKNOWN
condition|)
block|{
name|setType
argument_list|(
name|metadata
argument_list|,
name|type
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PUBLISHER
case|:
name|PublisherTextExtractor
name|publisherTextExtractor
init|=
operator|new
name|PublisherTextExtractor
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|publisherTextExtractor
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|WORDDOCUMENT
case|:
operator|new
name|WordExtractor
argument_list|(
name|context
argument_list|)
operator|.
name|parse
argument_list|(
name|filesystem
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
break|break;
case|case
name|POWERPOINT
case|:
operator|new
name|HSLFExtractor
argument_list|(
name|context
argument_list|)
operator|.
name|parse
argument_list|(
name|filesystem
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
break|break;
case|case
name|WORKBOOK
case|:
name|Locale
name|locale
init|=
name|context
operator|.
name|get
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|ExcelExtractor
argument_list|(
name|context
argument_list|)
operator|.
name|parse
argument_list|(
name|filesystem
argument_list|,
name|xhtml
argument_list|,
name|locale
argument_list|)
expr_stmt|;
break|break;
case|case
name|VISIO
case|:
name|VisioTextExtractor
name|visioTextExtractor
init|=
operator|new
name|VisioTextExtractor
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|text
range|:
name|visioTextExtractor
operator|.
name|getAllText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|OUTLOOK
case|:
if|if
condition|(
operator|!
name|outlookExtracted
condition|)
block|{
name|outlookExtracted
operator|=
literal|true
expr_stmt|;
name|OutlookExtractor
name|extractor
init|=
operator|new
name|OutlookExtractor
argument_list|(
name|filesystem
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|extractor
operator|.
name|parse
argument_list|(
name|xhtml
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|ENCRYPTED
case|:
name|EncryptionInfo
name|info
init|=
operator|new
name|EncryptionInfo
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
name|Decryptor
name|d
init|=
operator|new
name|EcmaDecryptor
argument_list|(
name|info
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|d
operator|.
name|verifyPassword
argument_list|(
name|Decryptor
operator|.
name|DEFAULT_PASSWORD
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to process: document is encrypted"
argument_list|)
throw|;
block|}
name|OOXMLParser
name|parser
init|=
operator|new
name|OOXMLParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|d
operator|.
name|getDataStream
argument_list|(
name|filesystem
argument_list|)
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to process encrypted document"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setType
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|MediaType
name|type
parameter_list|)
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

