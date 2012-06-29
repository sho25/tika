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
name|commons
operator|.
name|compress
operator|.
name|compressors
operator|.
name|bzip2
operator|.
name|BZip2CompressorInputStream
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
name|gzip
operator|.
name|GzipCompressorInputStream
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
name|gzip
operator|.
name|GzipUtils
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
name|pack200
operator|.
name|Pack200CompressorInputStream
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
name|xz
operator|.
name|XZCompressorInputStream
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
comment|/**  * Parser for various compression formats.  */
end_comment

begin_class
specifier|public
class|class
name|CompressorParser
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
literal|2793565792967222459L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|BZIP
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-bzip"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|BZIP2
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-bzip2"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|GZIP
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-gzip"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|XZ
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-xz"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|PACK
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"application/x-java-pack200"
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
name|BZIP
argument_list|,
name|BZIP2
argument_list|,
name|GZIP
argument_list|,
name|XZ
argument_list|,
name|PACK
argument_list|)
decl_stmt|;
specifier|static
name|MediaType
name|getMediaType
parameter_list|(
name|CompressorInputStream
name|stream
parameter_list|)
block|{
if|if
condition|(
name|stream
operator|instanceof
name|BZip2CompressorInputStream
condition|)
block|{
return|return
name|BZIP2
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|GzipCompressorInputStream
condition|)
block|{
return|return
name|GZIP
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|XZCompressorInputStream
condition|)
block|{
return|return
name|XZ
return|;
block|}
elseif|else
if|if
condition|(
name|stream
operator|instanceof
name|Pack200CompressorInputStream
condition|)
block|{
return|return
name|PACK
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
comment|// At the end we want to close the compression stream to release
comment|// any associated resources, but the underlying document stream
comment|// should not be closed
name|stream
operator|=
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|// Ensure that the stream supports the mark feature
name|stream
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|CompressorInputStream
name|cis
decl_stmt|;
try|try
block|{
name|CompressorStreamFactory
name|factory
init|=
operator|new
name|CompressorStreamFactory
argument_list|()
decl_stmt|;
name|cis
operator|=
name|factory
operator|.
name|createCompressorInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CompressorException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unable to uncompress document stream"
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
name|cis
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
name|Metadata
name|entrydata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".tbz"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|)
operator|+
literal|".tar"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".tbz2"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|5
argument_list|)
operator|+
literal|".tar"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".bz"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|3
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".bz2"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".xz"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|3
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".pack"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|5
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
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
name|GzipUtils
operator|.
name|getUncompressedFilename
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
block|}
comment|// Use the delegate parser to parse the compressed document
name|EmbeddedDocumentExtractor
name|extractor
init|=
name|context
operator|.
name|get
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|,
operator|new
name|ParsingEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
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
name|cis
argument_list|,
name|xhtml
argument_list|,
name|entrydata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cis
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

