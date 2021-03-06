begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|csv
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|UnsupportedCharsetException
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
name|commons
operator|.
name|csv
operator|.
name|CSVFormat
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
name|csv
operator|.
name|CSVRecord
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
name|detect
operator|.
name|AutoDetectReader
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
name|EncodingDetector
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
name|AbstractEncodingDetectorParser
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
comment|/**  * Unless the {@link TikaCoreProperties#CONTENT_TYPE_OVERRIDE} is set,  * this parser tries to assess whether the file is a text file, csv or tsv.  * If the detector detects regularity in column numbers and/or encapsulated cells,  * this parser will apply the {@link org.apache.commons.csv.CSVParser};  * otherwise, it will treat the contents as text.  *<p>  *     If there is a csv parse exception during detection, the parser sets  * the {@link Metadata#CONTENT_TYPE} to {@link MediaType#TEXT_PLAIN}  * and treats the file as {@link MediaType#TEXT_PLAIN}.  *</p>  *<p>  *     If there is a csv parse exception during the parse, the parser  *     writes what's left of the stream as if it were text and then throws  *     an exception.  As of this writing, the content that was buffered by the underlying  *     {@link org.apache.commons.csv.CSVParser} is lost.  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|TextAndCSVParser
extends|extends
name|AbstractEncodingDetectorParser
block|{
specifier|private
specifier|static
specifier|final
name|String
name|CSV_PREFIX
init|=
literal|"csv"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CHARSET
init|=
literal|"charset"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DELIMITER
init|=
literal|"delimiter"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Property
name|DELIMITER_PROPERTY
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|CSV_PREFIX
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
name|DELIMITER
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TD
init|=
literal|"td"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TR
init|=
literal|"tr"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TABLE
init|=
literal|"table"
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|CSV
init|=
name|MediaType
operator|.
name|text
argument_list|(
literal|"csv"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|TSV
init|=
name|MediaType
operator|.
name|text
argument_list|(
literal|"tsv"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MARK_LIMIT
init|=
literal|20000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|DEFAULT_DELIMITERS
init|=
operator|new
name|char
index|[]
block|{
literal|','
block|,
literal|'\t'
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Character
argument_list|,
name|String
argument_list|>
name|CHAR_TO_STRING_DELIMITER_MAP
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Character
argument_list|>
name|STRING_TO_CHAR_DELIMITER_MAP
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|put
argument_list|(
literal|','
argument_list|,
literal|"comma"
argument_list|)
expr_stmt|;
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|put
argument_list|(
literal|'\t'
argument_list|,
literal|"tab"
argument_list|)
expr_stmt|;
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|put
argument_list|(
literal|'|'
argument_list|,
literal|"pipe"
argument_list|)
expr_stmt|;
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|put
argument_list|(
literal|';'
argument_list|,
literal|"semicolon"
argument_list|)
expr_stmt|;
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|put
argument_list|(
literal|':'
argument_list|,
literal|"colon"
argument_list|)
expr_stmt|;
block|}
static|static
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Character
argument_list|,
name|String
argument_list|>
name|e
range|:
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|STRING_TO_CHAR_DELIMITER_MAP
operator|.
name|put
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|CSV
argument_list|,
name|TSV
argument_list|,
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|char
index|[]
name|delimiters
init|=
name|DEFAULT_DELIMITERS
decl_stmt|;
comment|/**      * This is the mark limit in characters (not bytes) to      * read from the stream when classifying the stream as      * csv, tsv or txt.      */
annotation|@
name|Field
specifier|private
name|int
name|markLimit
init|=
name|DEFAULT_MARK_LIMIT
decl_stmt|;
comment|/**      * minimum confidence score that there's enough      * evidence to determine csv/tsv vs. txt      */
annotation|@
name|Field
specifier|private
name|double
name|minConfidence
init|=
literal|0.50
decl_stmt|;
specifier|public
name|TextAndCSVParser
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TextAndCSVParser
parameter_list|(
name|EncodingDetector
name|encodingDetector
parameter_list|)
block|{
name|super
argument_list|(
name|encodingDetector
argument_list|)
expr_stmt|;
block|}
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
name|CSVParams
name|params
init|=
name|getOverride
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
name|Charset
name|charset
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|reader
operator|=
name|detect
argument_list|(
name|params
argument_list|,
name|stream
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|getCharset
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|charset
operator|=
name|params
operator|.
name|getCharset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|charset
operator|=
operator|(
operator|(
name|AutoDetectReader
operator|)
name|reader
operator|)
operator|.
name|getCharset
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|params
operator|.
name|getCharset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|charset
operator|=
name|params
operator|.
name|getCharset
argument_list|()
expr_stmt|;
block|}
name|updateMetadata
argument_list|(
name|params
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
comment|//if text or a non-csv/tsv category of text
comment|//treat this as text and be done
comment|//TODO -- if it was detected as a non-csv subtype of text
if|if
condition|(
operator|!
name|params
operator|.
name|getMediaType
argument_list|()
operator|.
name|getBaseType
argument_list|()
operator|.
name|equals
argument_list|(
name|CSV
argument_list|)
operator|&&
operator|!
name|params
operator|.
name|getMediaType
argument_list|()
operator|.
name|getBaseType
argument_list|()
operator|.
name|equals
argument_list|(
name|TSV
argument_list|)
condition|)
block|{
name|handleText
argument_list|(
name|reader
argument_list|,
name|charset
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
return|return;
block|}
name|CSVFormat
name|csvFormat
init|=
name|CSVFormat
operator|.
name|EXCEL
operator|.
name|withDelimiter
argument_list|(
name|params
operator|.
name|getDelimiter
argument_list|()
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|DELIMITER_PROPERTY
argument_list|,
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|get
argument_list|(
name|csvFormat
operator|.
name|getDelimiter
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|XHTMLContentHandler
name|xhtmlContentHandler
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
try|try
init|(
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|csv
operator|.
name|CSVParser
name|commonsParser
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|csv
operator|.
name|CSVParser
argument_list|(
name|reader
argument_list|,
name|csvFormat
argument_list|)
init|)
block|{
name|xhtmlContentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtmlContentHandler
operator|.
name|startElement
argument_list|(
name|TABLE
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|CSVRecord
name|row
range|:
name|commonsParser
control|)
block|{
name|xhtmlContentHandler
operator|.
name|startElement
argument_list|(
name|TR
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|cell
range|:
name|row
control|)
block|{
name|xhtmlContentHandler
operator|.
name|startElement
argument_list|(
name|TD
argument_list|)
expr_stmt|;
name|xhtmlContentHandler
operator|.
name|characters
argument_list|(
name|cell
argument_list|)
expr_stmt|;
name|xhtmlContentHandler
operator|.
name|endElement
argument_list|(
name|TD
argument_list|)
expr_stmt|;
block|}
name|xhtmlContentHandler
operator|.
name|endElement
argument_list|(
name|TR
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|//if there's a parse exception
comment|//try to get the rest of the content...treat it as text for now
comment|//There will be some content lost because of buffering.
comment|//TODO -- figure out how to improve this
name|xhtmlContentHandler
operator|.
name|endElement
argument_list|(
name|TABLE
argument_list|)
expr_stmt|;
name|xhtmlContentHandler
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"name"
argument_list|,
literal|"after exception"
argument_list|)
expr_stmt|;
name|handleText
argument_list|(
name|reader
argument_list|,
name|xhtmlContentHandler
argument_list|)
expr_stmt|;
name|xhtmlContentHandler
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|xhtmlContentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
comment|//TODO -- consider dumping what's left in the reader as text
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"exception parsing the csv"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|xhtmlContentHandler
operator|.
name|endElement
argument_list|(
name|TABLE
argument_list|)
expr_stmt|;
name|xhtmlContentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleText
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|Charset
name|charset
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
comment|// Automatically detect the character encoding
comment|//try to get detected content type; could be a subclass of text/plain
comment|//such as vcal, etc.
name|String
name|incomingMime
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
name|MediaType
name|mediaType
init|=
name|MediaType
operator|.
name|TEXT_PLAIN
decl_stmt|;
if|if
condition|(
name|incomingMime
operator|!=
literal|null
condition|)
block|{
name|MediaType
name|tmpMediaType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|incomingMime
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpMediaType
operator|!=
literal|null
condition|)
block|{
name|mediaType
operator|=
name|tmpMediaType
expr_stmt|;
block|}
block|}
name|MediaType
name|type
init|=
operator|new
name|MediaType
argument_list|(
name|mediaType
argument_list|,
name|charset
argument_list|)
decl_stmt|;
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
comment|// deprecated, see TIKA-431
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|charset
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
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
name|handleText
argument_list|(
name|reader
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|handleText
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|n
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|n
operator|!=
operator|-
literal|1
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Reader
name|detect
parameter_list|(
name|CSVParams
name|params
parameter_list|,
name|InputStream
name|stream
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
block|{
comment|//if the file was already identified as not .txt, .csv or .tsv
comment|//don't even try to csv or not
name|String
name|mediaString
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
name|mediaString
operator|!=
literal|null
condition|)
block|{
name|MediaType
name|mediaType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|mediaString
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SUPPORTED_TYPES
operator|.
name|contains
argument_list|(
name|mediaType
operator|.
name|getBaseType
argument_list|()
argument_list|)
condition|)
block|{
name|params
operator|.
name|setMediaType
argument_list|(
name|mediaType
argument_list|)
expr_stmt|;
return|return
operator|new
name|AutoDetectReader
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|getEncodingDetector
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
block|}
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getCharset
argument_list|()
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
operator|new
name|AutoDetectReader
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|getEncodingDetector
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|setCharset
argument_list|(
operator|(
operator|(
name|AutoDetectReader
operator|)
name|reader
operator|)
operator|.
name|getCharset
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|isComplete
argument_list|()
condition|)
block|{
return|return
name|reader
return|;
block|}
block|}
else|else
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|params
operator|.
name|getCharset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|.
name|getDelimiter
argument_list|()
operator|==
literal|null
operator|&&
operator|(
name|params
operator|.
name|getMediaType
argument_list|()
operator|==
literal|null
operator|||
name|isCSVOrTSV
argument_list|(
name|params
operator|.
name|getMediaType
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|CSVSniffer
name|sniffer
init|=
operator|new
name|CSVSniffer
argument_list|(
name|markLimit
argument_list|,
name|delimiters
argument_list|,
name|minConfidence
argument_list|)
decl_stmt|;
name|CSVResult
name|result
init|=
name|sniffer
operator|.
name|getBest
argument_list|(
name|reader
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|params
operator|.
name|setMediaType
argument_list|(
name|result
operator|.
name|getMediaType
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|setDelimiter
argument_list|(
name|result
operator|.
name|getDelimiter
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
specifier|private
name|CSVParams
name|getOverride
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|String
name|override
init|=
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CONTENT_TYPE_OVERRIDE
argument_list|)
decl_stmt|;
if|if
condition|(
name|override
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|CSVParams
argument_list|()
return|;
block|}
name|MediaType
name|mediaType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|override
argument_list|)
decl_stmt|;
if|if
condition|(
name|mediaType
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|CSVParams
argument_list|()
return|;
block|}
name|String
name|charsetString
init|=
name|mediaType
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
name|CHARSET
argument_list|)
decl_stmt|;
name|Charset
name|charset
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|charsetString
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|charset
operator|=
name|Charset
operator|.
name|forName
argument_list|(
name|charsetString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedCharsetException
name|e
parameter_list|)
block|{              }
block|}
if|if
condition|(
operator|!
name|isCSVOrTSV
argument_list|(
name|mediaType
argument_list|)
condition|)
block|{
return|return
operator|new
name|CSVParams
argument_list|(
name|mediaType
argument_list|,
name|charset
argument_list|)
return|;
block|}
name|String
name|delimiterString
init|=
name|mediaType
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
name|DELIMITER
argument_list|)
decl_stmt|;
if|if
condition|(
name|delimiterString
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|CSVParams
argument_list|(
name|mediaType
argument_list|,
name|charset
argument_list|)
return|;
block|}
if|if
condition|(
name|STRING_TO_CHAR_DELIMITER_MAP
operator|.
name|containsKey
argument_list|(
name|delimiterString
argument_list|)
condition|)
block|{
return|return
operator|new
name|CSVParams
argument_list|(
name|mediaType
argument_list|,
name|charset
argument_list|,
operator|(
name|char
operator|)
name|STRING_TO_CHAR_DELIMITER_MAP
operator|.
name|get
argument_list|(
name|delimiterString
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|delimiterString
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|CSVParams
argument_list|(
name|mediaType
argument_list|,
name|charset
argument_list|,
name|delimiterString
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
comment|//TODO: log bad/unrecognized delimiter string
return|return
operator|new
name|CSVParams
argument_list|(
name|mediaType
argument_list|,
name|charset
argument_list|)
return|;
block|}
specifier|static
name|boolean
name|isCSVOrTSV
parameter_list|(
name|MediaType
name|mediaType
parameter_list|)
block|{
if|if
condition|(
name|mediaType
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
name|mediaType
operator|.
name|getBaseType
argument_list|()
operator|.
name|equals
argument_list|(
name|TSV
argument_list|)
operator|||
name|mediaType
operator|.
name|getBaseType
argument_list|()
operator|.
name|equals
argument_list|(
name|CSV
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|updateMetadata
parameter_list|(
name|CSVParams
name|params
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|MediaType
name|mediaType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getMediaType
argument_list|()
operator|.
name|getBaseType
argument_list|()
operator|.
name|equals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
condition|)
block|{
name|mediaType
operator|=
name|MediaType
operator|.
name|TEXT_PLAIN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|params
operator|.
name|getDelimiter
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|getDelimiter
argument_list|()
operator|==
literal|'\t'
condition|)
block|{
name|mediaType
operator|=
name|TSV
expr_stmt|;
block|}
else|else
block|{
name|mediaType
operator|=
name|CSV
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|mediaType
operator|=
name|MediaType
operator|.
name|parse
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getCharset
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|attrs
operator|.
name|put
argument_list|(
name|CHARSET
argument_list|,
name|params
operator|.
name|getCharset
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|// deprecated, see TIKA-431
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|params
operator|.
name|getCharset
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|mediaType
operator|.
name|equals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
operator|&&
name|params
operator|.
name|getDelimiter
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|containsKey
argument_list|(
name|params
operator|.
name|getDelimiter
argument_list|()
argument_list|)
condition|)
block|{
name|attrs
operator|.
name|put
argument_list|(
name|DELIMITER
argument_list|,
name|CHAR_TO_STRING_DELIMITER_MAP
operator|.
name|get
argument_list|(
name|params
operator|.
name|getDelimiter
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attrs
operator|.
name|put
argument_list|(
name|DELIMITER
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
operator|(
name|int
operator|)
name|params
operator|.
name|getDelimiter
argument_list|()
operator|.
name|charValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|MediaType
name|type
init|=
operator|new
name|MediaType
argument_list|(
name|mediaType
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
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

