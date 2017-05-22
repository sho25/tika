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
name|crypto
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
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchProviderException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Collections
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
name|TimeZone
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
name|RereadableInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|cryptopro
operator|.
name|CryptoProObjectIdentifiers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|nist
operator|.
name|NISTObjectIdentifiers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|oiw
operator|.
name|OIWObjectIdentifiers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|pkcs
operator|.
name|PKCSObjectIdentifiers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|teletrust
operator|.
name|TeleTrusTObjectIdentifiers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|GeneralName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|X509ObjectIdentifiers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x9
operator|.
name|X9ObjectIdentifiers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|cms
operator|.
name|CMSSignedDataGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|tsp
operator|.
name|TimeStampToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|tsp
operator|.
name|cms
operator|.
name|CMSTimeStampedData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|tsp
operator|.
name|cms
operator|.
name|CMSTimeStampedDataParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
comment|/**  * Tika parser for Time Stamped Data Envelope (application/timestamped-data)  */
end_comment

begin_class
specifier|public
class|class
name|TSDParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3268158344501763323L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TSDParser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_LOOP_LABEL
init|=
literal|"Time-Stamp-n."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_DESCRIPTION_LABEL
init|=
literal|"Description"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_DESCRIPTION_VALUE
init|=
literal|"Time Stamped Data Envelope"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_PARSED_LABEL
init|=
literal|"File-Parsed"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_PARSED_DATE
init|=
literal|"File-Parsed-DateTime"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_DATE
init|=
literal|"Time-Stamp-DateTime"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_DATE_FORMAT
init|=
literal|"UTC"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_POLICY_ID
init|=
literal|"Policy-Id"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_SERIAL_NUMBER
init|=
literal|"Serial-Number"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_TSA
init|=
literal|"TSA"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TSD_ALGORITHM
init|=
literal|"Algorithm"
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
literal|"timestamped-data"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TSD_MIME_TYPE
init|=
literal|"application/timestamped-data"
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
comment|//Try to parse TSD file
try|try
init|(
name|RereadableInputStream
name|ris
init|=
operator|new
name|RereadableInputStream
argument_list|(
name|stream
argument_list|,
literal|2048
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|Metadata
name|TSDAndEmbeddedMetadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|TSDMetas
argument_list|>
name|tsdMetasList
init|=
name|this
operator|.
name|extractMetas
argument_list|(
name|ris
argument_list|)
decl_stmt|;
name|this
operator|.
name|buildMetas
argument_list|(
name|tsdMetasList
argument_list|,
name|metadata
operator|!=
literal|null
operator|&&
name|metadata
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|TSDAndEmbeddedMetadata
else|:
name|metadata
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
name|ris
operator|.
name|rewind
argument_list|()
expr_stmt|;
comment|//Try to parse embedded file in TSD file
name|this
operator|.
name|parseTSDContent
argument_list|(
name|ris
argument_list|,
name|handler
argument_list|,
name|TSDAndEmbeddedMetadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|TSDMetas
argument_list|>
name|extractMetas
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
name|List
argument_list|<
name|TSDMetas
argument_list|>
name|tsdMetasList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|CMSTimeStampedData
name|cmsTimeStampedData
init|=
operator|new
name|CMSTimeStampedData
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|TimeStampToken
index|[]
name|tokens
init|=
name|cmsTimeStampedData
operator|.
name|getTimeStampTokens
argument_list|()
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
name|tokens
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TSDMetas
name|tsdMetas
init|=
operator|new
name|TSDMetas
argument_list|(
literal|true
argument_list|,
name|tokens
index|[
name|i
index|]
operator|.
name|getTimeStampInfo
argument_list|()
operator|.
name|getGenTime
argument_list|()
argument_list|,
name|tokens
index|[
name|i
index|]
operator|.
name|getTimeStampInfo
argument_list|()
operator|.
name|getPolicy
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|tokens
index|[
name|i
index|]
operator|.
name|getTimeStampInfo
argument_list|()
operator|.
name|getSerialNumber
argument_list|()
argument_list|,
name|tokens
index|[
name|i
index|]
operator|.
name|getTimeStampInfo
argument_list|()
operator|.
name|getTsa
argument_list|()
argument_list|,
name|tokens
index|[
name|i
index|]
operator|.
name|getTimeStampInfo
argument_list|()
operator|.
name|getHashAlgorithm
argument_list|()
operator|.
name|getAlgorithm
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|tsdMetasList
operator|.
name|add
argument_list|(
name|tsdMetas
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in TSDParser.buildMetas {}"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|tsdMetasList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|tsdMetasList
return|;
block|}
specifier|private
name|void
name|buildMetas
parameter_list|(
name|List
argument_list|<
name|TSDMetas
argument_list|>
name|tsdMetasList
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|Integer
name|count
init|=
literal|1
decl_stmt|;
for|for
control|(
name|TSDMetas
name|tsdm
range|:
name|tsdMetasList
control|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|TSD_MIME_TYPE
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|TSD_DESCRIPTION_LABEL
argument_list|,
name|TSD_DESCRIPTION_VALUE
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|TSD_PARSED_LABEL
argument_list|,
name|tsdm
operator|.
name|getParseBuiltStr
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|TSD_PARSED_DATE
argument_list|,
name|tsdm
operator|.
name|getParsedDateStr
argument_list|()
operator|+
literal|" "
operator|+
name|TSD_DATE_FORMAT
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|TSD_DATE
argument_list|,
name|tsdm
operator|.
name|getEmitDateStr
argument_list|()
operator|+
literal|" "
operator|+
name|TSD_DATE_FORMAT
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|TSD_POLICY_ID
argument_list|,
name|tsdm
operator|.
name|getPolicyId
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|TSD_SERIAL_NUMBER
argument_list|,
name|tsdm
operator|.
name|getSerialNumberFormatted
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|TSD_TSA
argument_list|,
name|tsdm
operator|.
name|getTsaStr
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TSD_LOOP_LABEL
operator|+
name|count
operator|+
literal|" - "
operator|+
name|TSD_ALGORITHM
argument_list|,
name|tsdm
operator|.
name|getAlgorithmName
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|parseTSDContent
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
block|{
name|CMSTimeStampedDataParser
name|cmsTimeStampedDataParser
init|=
literal|null
decl_stmt|;
name|EmbeddedDocumentExtractor
name|edx
init|=
name|EmbeddedDocumentUtil
operator|.
name|getEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|edx
operator|.
name|shouldParseEmbedded
argument_list|(
name|metadata
argument_list|)
condition|)
block|{
try|try
block|{
name|cmsTimeStampedDataParser
operator|=
operator|new
name|CMSTimeStampedDataParser
argument_list|(
name|stream
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|cmsTimeStampedDataParser
operator|.
name|getContent
argument_list|()
argument_list|)
init|)
block|{
name|edx
operator|.
name|parseEmbedded
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in TSDParser.parseTSDContent {}"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|closeCMSParser
argument_list|(
name|cmsTimeStampedDataParser
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|closeCMSParser
parameter_list|(
name|CMSTimeStampedDataParser
name|cmsTimeStampedDataParser
parameter_list|)
block|{
if|if
condition|(
name|cmsTimeStampedDataParser
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cmsTimeStampedDataParser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in TSDParser.closeCMSParser {}"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|TSDMetas
block|{
specifier|private
specifier|final
name|String
name|DATE_FORMAT
init|=
literal|"dd/MM/yyyy HH:mm:ss"
decl_stmt|;
specifier|private
name|Boolean
name|parseBuilt
init|=
literal|false
decl_stmt|;
specifier|private
name|Date
name|emitDate
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
specifier|private
name|String
name|policyId
init|=
literal|""
decl_stmt|;
specifier|private
name|BigInteger
name|serialNumber
init|=
literal|null
decl_stmt|;
specifier|private
name|GeneralName
name|tsa
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|algorithm
init|=
literal|""
decl_stmt|;
specifier|private
name|Date
name|parsedDate
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
specifier|public
name|TSDMetas
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TSDMetas
parameter_list|(
name|Boolean
name|parseBuilt
parameter_list|,
name|Date
name|emitDate
parameter_list|,
name|String
name|policyId
parameter_list|,
name|BigInteger
name|serialNumber
parameter_list|,
name|GeneralName
name|tsa
parameter_list|,
name|String
name|algorithm
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|parseBuilt
operator|=
name|parseBuilt
expr_stmt|;
name|this
operator|.
name|emitDate
operator|=
name|emitDate
expr_stmt|;
name|this
operator|.
name|policyId
operator|=
name|policyId
expr_stmt|;
name|this
operator|.
name|serialNumber
operator|=
name|serialNumber
expr_stmt|;
name|this
operator|.
name|tsa
operator|=
name|tsa
expr_stmt|;
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
block|}
specifier|public
name|Boolean
name|getParseBuilt
parameter_list|()
block|{
return|return
name|parseBuilt
return|;
block|}
specifier|public
name|String
name|getParseBuiltStr
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|getParseBuilt
argument_list|()
operator|!=
literal|null
condition|?
name|this
operator|.
name|getParseBuilt
argument_list|()
else|:
literal|false
argument_list|)
return|;
block|}
specifier|public
name|void
name|setParseBuilt
parameter_list|(
name|Boolean
name|parseBuilt
parameter_list|)
block|{
name|this
operator|.
name|parseBuilt
operator|=
name|parseBuilt
expr_stmt|;
block|}
specifier|public
name|Date
name|getEmitDate
parameter_list|()
block|{
return|return
name|emitDate
return|;
block|}
specifier|public
name|void
name|setEmitDate
parameter_list|(
name|Date
name|emitDate
parameter_list|)
block|{
name|this
operator|.
name|emitDate
operator|=
name|emitDate
expr_stmt|;
block|}
specifier|public
name|String
name|getEmitDateStr
parameter_list|()
block|{
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|this
operator|.
name|DATE_FORMAT
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|sdf
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sdf
operator|.
name|format
argument_list|(
name|this
operator|.
name|getEmitDate
argument_list|()
operator|!=
literal|null
condition|?
name|this
operator|.
name|getEmitDate
argument_list|()
else|:
operator|new
name|Date
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPolicyId
parameter_list|()
block|{
return|return
name|policyId
return|;
block|}
specifier|public
name|void
name|setPolicyId
parameter_list|(
name|String
name|policyId
parameter_list|)
block|{
name|this
operator|.
name|policyId
operator|=
name|policyId
expr_stmt|;
block|}
specifier|public
name|BigInteger
name|getSerialNumber
parameter_list|()
block|{
return|return
name|serialNumber
return|;
block|}
specifier|public
name|String
name|getSerialNumberFormatted
parameter_list|()
block|{
name|String
name|outsn
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%12x"
argument_list|,
name|getSerialNumber
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|outsn
operator|!=
literal|null
condition|?
name|outsn
operator|.
name|trim
argument_list|()
else|:
literal|""
operator|+
name|getSerialNumber
argument_list|()
return|;
block|}
specifier|public
name|void
name|setSerialNumber
parameter_list|(
name|BigInteger
name|serialNumber
parameter_list|)
block|{
name|this
operator|.
name|serialNumber
operator|=
name|serialNumber
expr_stmt|;
block|}
specifier|public
name|GeneralName
name|getTsa
parameter_list|()
block|{
return|return
name|tsa
return|;
block|}
specifier|public
name|String
name|getTsaStr
parameter_list|()
block|{
return|return
name|tsa
operator|+
literal|""
return|;
block|}
specifier|public
name|void
name|setTSA
parameter_list|(
name|GeneralName
name|tsa
parameter_list|)
block|{
name|this
operator|.
name|tsa
operator|=
name|tsa
expr_stmt|;
block|}
specifier|public
name|String
name|getAlgorithm
parameter_list|()
block|{
return|return
name|algorithm
return|;
block|}
specifier|public
name|String
name|getAlgorithmName
parameter_list|()
block|{
return|return
name|OIDNameMapper
operator|.
name|getDigestAlgName
argument_list|(
name|getAlgorithm
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|setAlgorithm
parameter_list|(
name|String
name|algorithm
parameter_list|)
block|{
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
block|}
specifier|public
name|Date
name|getParsedDate
parameter_list|()
block|{
return|return
name|parsedDate
return|;
block|}
specifier|public
name|String
name|getParsedDateStr
parameter_list|()
block|{
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|this
operator|.
name|DATE_FORMAT
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|sdf
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sdf
operator|.
name|format
argument_list|(
name|this
operator|.
name|getParsedDate
argument_list|()
operator|!=
literal|null
condition|?
name|this
operator|.
name|getParsedDate
argument_list|()
else|:
operator|new
name|Date
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|setParsedDate
parameter_list|(
name|Date
name|parsedDate
parameter_list|)
block|{
name|this
operator|.
name|parsedDate
operator|=
name|parsedDate
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
literal|"TSDMetas [parseBuilt="
operator|+
name|parseBuilt
operator|+
literal|", emitDate="
operator|+
name|emitDate
operator|+
literal|", policyId="
operator|+
name|policyId
operator|+
literal|", serialNumber="
operator|+
name|serialNumber
operator|+
literal|", tsa="
operator|+
name|tsa
operator|+
literal|", algorithm="
operator|+
name|algorithm
operator|+
literal|", parsedDate="
operator|+
name|parsedDate
operator|+
literal|"]"
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|OIDNameMapper
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|encryptionAlgs
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
name|String
argument_list|>
name|digestAlgs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|X9ObjectIdentifiers
operator|.
name|id_dsa_with_sha1
operator|.
name|getId
argument_list|()
argument_list|,
literal|"DSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|X9ObjectIdentifiers
operator|.
name|id_dsa
operator|.
name|getId
argument_list|()
argument_list|,
literal|"DSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|OIWObjectIdentifiers
operator|.
name|dsaWithSHA1
operator|.
name|getId
argument_list|()
argument_list|,
literal|"DSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|rsaEncryption
operator|.
name|getId
argument_list|()
argument_list|,
literal|"RSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|sha1WithRSAEncryption
operator|.
name|getId
argument_list|()
argument_list|,
literal|"RSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|TeleTrusTObjectIdentifiers
operator|.
name|teleTrusTRSAsignatureAlgorithm
operator|.
name|getId
argument_list|()
argument_list|,
literal|"RSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|X509ObjectIdentifiers
operator|.
name|id_ea_rsa
operator|.
name|getId
argument_list|()
argument_list|,
literal|"RSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|CMSSignedDataGenerator
operator|.
name|ENCRYPTION_ECDSA
argument_list|,
literal|"ECDSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|X9ObjectIdentifiers
operator|.
name|ecdsa_with_SHA2
operator|.
name|getId
argument_list|()
argument_list|,
literal|"ECDSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|X9ObjectIdentifiers
operator|.
name|ecdsa_with_SHA224
operator|.
name|getId
argument_list|()
argument_list|,
literal|"ECDSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|X9ObjectIdentifiers
operator|.
name|ecdsa_with_SHA256
operator|.
name|getId
argument_list|()
argument_list|,
literal|"ECDSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|X9ObjectIdentifiers
operator|.
name|ecdsa_with_SHA384
operator|.
name|getId
argument_list|()
argument_list|,
literal|"ECDSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|X9ObjectIdentifiers
operator|.
name|ecdsa_with_SHA512
operator|.
name|getId
argument_list|()
argument_list|,
literal|"ECDSA"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|CMSSignedDataGenerator
operator|.
name|ENCRYPTION_RSA_PSS
argument_list|,
literal|"RSAandMGF1"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|CryptoProObjectIdentifiers
operator|.
name|gostR3410_94
operator|.
name|getId
argument_list|()
argument_list|,
literal|"GOST3410"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
name|CryptoProObjectIdentifiers
operator|.
name|gostR3410_2001
operator|.
name|getId
argument_list|()
argument_list|,
literal|"ECGOST3410"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
literal|"1.3.6.1.4.1.5849.1.6.2"
argument_list|,
literal|"ECGOST3410"
argument_list|)
expr_stmt|;
name|encryptionAlgs
operator|.
name|put
argument_list|(
literal|"1.3.6.1.4.1.5849.1.1.5"
argument_list|,
literal|"GOST3410"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|md5
operator|.
name|getId
argument_list|()
argument_list|,
literal|"MD5"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|OIWObjectIdentifiers
operator|.
name|idSHA1
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA1"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|NISTObjectIdentifiers
operator|.
name|id_sha224
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA224"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|NISTObjectIdentifiers
operator|.
name|id_sha256
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA256"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|NISTObjectIdentifiers
operator|.
name|id_sha384
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA384"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|NISTObjectIdentifiers
operator|.
name|id_sha512
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA512"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|sha1WithRSAEncryption
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA1"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|sha224WithRSAEncryption
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA224"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|sha256WithRSAEncryption
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA256"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|sha384WithRSAEncryption
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA384"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|PKCSObjectIdentifiers
operator|.
name|sha512WithRSAEncryption
operator|.
name|getId
argument_list|()
argument_list|,
literal|"SHA512"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|TeleTrusTObjectIdentifiers
operator|.
name|ripemd128
operator|.
name|getId
argument_list|()
argument_list|,
literal|"RIPEMD128"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|TeleTrusTObjectIdentifiers
operator|.
name|ripemd160
operator|.
name|getId
argument_list|()
argument_list|,
literal|"RIPEMD160"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|TeleTrusTObjectIdentifiers
operator|.
name|ripemd256
operator|.
name|getId
argument_list|()
argument_list|,
literal|"RIPEMD256"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
name|CryptoProObjectIdentifiers
operator|.
name|gostR3411
operator|.
name|getId
argument_list|()
argument_list|,
literal|"GOST3411"
argument_list|)
expr_stmt|;
name|digestAlgs
operator|.
name|put
argument_list|(
literal|"1.3.6.1.4.1.5849.1.2.1"
argument_list|,
literal|"GOST3411"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getDigestAlgName
parameter_list|(
name|String
name|digestAlgOID
parameter_list|)
block|{
name|String
name|algName
init|=
name|digestAlgs
operator|.
name|get
argument_list|(
name|digestAlgOID
argument_list|)
decl_stmt|;
if|if
condition|(
name|algName
operator|!=
literal|null
condition|)
block|{
return|return
name|algName
return|;
block|}
return|return
name|digestAlgOID
return|;
block|}
specifier|public
specifier|static
name|String
name|getEncryptionAlgName
parameter_list|(
name|String
name|encryptionAlgOID
parameter_list|)
block|{
name|String
name|algName
init|=
name|encryptionAlgs
operator|.
name|get
argument_list|(
name|encryptionAlgOID
argument_list|)
decl_stmt|;
if|if
condition|(
name|algName
operator|!=
literal|null
condition|)
block|{
return|return
name|algName
return|;
block|}
return|return
name|encryptionAlgOID
return|;
block|}
specifier|public
specifier|static
name|MessageDigest
name|getDigestInstance
parameter_list|(
name|String
name|algorithm
parameter_list|,
name|String
name|provider
parameter_list|)
throws|throws
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
block|{
if|if
condition|(
name|provider
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|,
name|provider
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
return|return
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
return|;
comment|// try rolling back
block|}
block|}
else|else
block|{
return|return
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

