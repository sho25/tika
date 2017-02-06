begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hemf
operator|.
name|extractor
operator|.
name|HemfExtractor
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
name|hemf
operator|.
name|record
operator|.
name|AbstractHemfComment
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
name|hemf
operator|.
name|record
operator|.
name|HemfCommentPublic
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
name|hemf
operator|.
name|record
operator|.
name|HemfCommentRecord
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
name|hemf
operator|.
name|record
operator|.
name|HemfRecord
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
name|hemf
operator|.
name|record
operator|.
name|HemfRecordType
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
name|hemf
operator|.
name|record
operator|.
name|HemfText
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
name|util
operator|.
name|RecordFormatException
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
comment|/**  * Extracts files embedded in EMF and offers a  * very rough capability to extract text if there  * is text stored in the EMF.  *<p/>  * To improve text extraction, we'd have to implement  * quite a bit more at the POI level.  We'd want to track changes  * in font and use that information for identifying character sets,  * inserting spaces and new lines.  */
end_comment

begin_class
specifier|public
class|class
name|EMFParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|MediaType
name|MEDIA_TYPE
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"emf"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|WMF_MEDIA_TYPE
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"wmf"
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
name|Collections
operator|.
name|singleton
argument_list|(
name|MEDIA_TYPE
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
name|context
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
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
init|=
literal|null
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
name|HemfExtractor
name|ex
init|=
operator|new
name|HemfExtractor
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|long
name|lastY
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|lastX
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|fudgeFactorX
init|=
literal|1000
decl_stmt|;
comment|//derive this from the font or frame/bounds information
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|HemfRecord
name|record
range|:
name|ex
control|)
block|{
if|if
condition|(
name|record
operator|.
name|getRecordType
argument_list|()
operator|==
name|HemfRecordType
operator|.
name|comment
condition|)
block|{
name|AbstractHemfComment
name|comment
init|=
operator|(
operator|(
name|HemfCommentRecord
operator|)
name|record
operator|)
operator|.
name|getComment
argument_list|()
decl_stmt|;
if|if
condition|(
name|comment
operator|instanceof
name|HemfCommentPublic
operator|.
name|MultiFormats
condition|)
block|{
if|if
condition|(
name|embeddedDocumentExtractor
operator|==
literal|null
condition|)
block|{
name|embeddedDocumentExtractor
operator|=
name|EmbeddedDocumentUtil
operator|.
name|getEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|handleMultiFormats
argument_list|(
operator|(
name|HemfCommentPublic
operator|.
name|MultiFormats
operator|)
name|comment
argument_list|,
name|xhtml
argument_list|,
name|embeddedDocumentExtractor
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|comment
operator|instanceof
name|HemfCommentPublic
operator|.
name|WindowsMetafile
condition|)
block|{
if|if
condition|(
name|embeddedDocumentExtractor
operator|==
literal|null
condition|)
block|{
name|embeddedDocumentExtractor
operator|=
name|EmbeddedDocumentUtil
operator|.
name|getEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|handleWMF
argument_list|(
operator|(
name|HemfCommentPublic
operator|.
name|WindowsMetafile
operator|)
name|comment
argument_list|,
name|xhtml
argument_list|,
name|embeddedDocumentExtractor
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|record
operator|.
name|getRecordType
argument_list|()
operator|.
name|equals
argument_list|(
name|HemfRecordType
operator|.
name|exttextoutw
argument_list|)
condition|)
block|{
name|HemfText
operator|.
name|ExtTextOutW
name|extTextOutW
init|=
operator|(
name|HemfText
operator|.
name|ExtTextOutW
operator|)
name|record
decl_stmt|;
if|if
condition|(
name|lastY
operator|>
operator|-
literal|1
operator|&&
name|lastY
operator|!=
name|extTextOutW
operator|.
name|getY
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|lastX
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|lastX
operator|>
operator|-
literal|1
operator|&&
name|extTextOutW
operator|.
name|getX
argument_list|()
operator|-
name|lastX
operator|>
name|fudgeFactorX
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|String
name|txt
init|=
name|extTextOutW
operator|.
name|getText
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|txt
argument_list|)
expr_stmt|;
name|lastY
operator|=
name|extTextOutW
operator|.
name|getY
argument_list|()
expr_stmt|;
name|lastX
operator|=
name|extTextOutW
operator|.
name|getX
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RecordFormatException
name|e
parameter_list|)
block|{
comment|//POI's hemfparser can throw these for "parse exceptions"
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|//convert Runtime to RecordFormatExceptions
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|handleWMF
parameter_list|(
name|HemfCommentPublic
operator|.
name|WindowsMetafile
name|comment
parameter_list|,
name|ContentHandler
name|contentHandler
parameter_list|,
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|Metadata
name|embeddedMetadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|embeddedMetadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|WMF_MEDIA_TYPE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|embeddedDocumentExtractor
operator|.
name|shouldParseEmbedded
argument_list|(
name|embeddedMetadata
argument_list|)
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
name|comment
operator|.
name|getWmfInputStream
argument_list|()
argument_list|)
init|)
block|{
name|embeddedDocumentExtractor
operator|.
name|parseEmbedded
argument_list|(
name|is
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|contentHandler
argument_list|)
argument_list|,
name|embeddedMetadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|handleMultiFormats
parameter_list|(
name|HemfCommentPublic
operator|.
name|MultiFormats
name|comment
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
for|for
control|(
name|HemfCommentPublic
operator|.
name|HemfMultiFormatsData
name|data
range|:
operator|(
operator|(
name|HemfCommentPublic
operator|.
name|MultiFormats
operator|)
name|comment
operator|)
operator|.
name|getData
argument_list|()
control|)
block|{
name|handleEmbedded
argument_list|(
name|data
operator|.
name|getData
argument_list|()
argument_list|,
name|embeddedDocumentExtractor
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|handleEmbedded
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|EmbeddedDocumentExtractor
name|embeddedDocumentExtractor
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|TikaException
throws|,
name|SAXException
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
name|data
argument_list|)
init|)
block|{
name|Metadata
name|embeddedMetadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|embeddedDocumentExtractor
operator|.
name|shouldParseEmbedded
argument_list|(
name|embeddedMetadata
argument_list|)
condition|)
block|{
name|embeddedDocumentExtractor
operator|.
name|parseEmbedded
argument_list|(
name|is
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|embeddedMetadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{          }
block|}
block|}
end_class

end_unit

