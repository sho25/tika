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
name|embedder
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
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
name|txt
operator|.
name|TXTParser
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
name|junit
operator|.
name|Test
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
comment|/**  * Unit test for {@link ExternalEmbedder}s.  */
end_comment

begin_class
specifier|public
class|class
name|ExternalEmbedderTest
block|{
specifier|protected
specifier|static
specifier|final
name|DateFormat
name|EXPECTED_METADATA_DATE_FORMATTER
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_CHARSET
init|=
name|UTF_8
operator|.
name|name
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COMMAND_METADATA_ARGUMENT_DESCRIPTION
init|=
literal|"dc:description"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_TXT_PATH
init|=
literal|"/test-documents/testTXT.txt"
decl_stmt|;
specifier|private
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
comment|/**      * Gets the expected returned metadata value for the given field      *      * @param fieldName      * @return a prefix added to the field name      */
specifier|protected
name|String
name|getExpectedMetadataValueString
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Date
name|timestamp
parameter_list|)
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" embedded "
operator|+
name|fieldName
operator|+
literal|" on "
operator|+
name|EXPECTED_METADATA_DATE_FORMATTER
operator|.
name|format
argument_list|(
name|timestamp
argument_list|)
return|;
block|}
comment|/**      * Gets the tika<code>Metadata</code> object containing data to be      * embedded.      *      * @return the populated tika metadata object      */
specifier|protected
name|Metadata
name|getMetadataToEmbed
parameter_list|(
name|Date
name|timestamp
parameter_list|)
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|,
name|getExpectedMetadataValueString
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
operator|.
name|toString
argument_list|()
argument_list|,
name|timestamp
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|metadata
return|;
block|}
comment|/**      * Gets the<code>Embedder</code> to test.      *      * @return the embedder under test      */
specifier|protected
name|Embedder
name|getEmbedder
parameter_list|()
block|{
name|ExternalEmbedder
name|embedder
init|=
operator|new
name|ExternalEmbedder
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Property
argument_list|,
name|String
index|[]
argument_list|>
name|metadataCommandArguments
init|=
operator|new
name|HashMap
argument_list|<
name|Property
argument_list|,
name|String
index|[]
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|metadataCommandArguments
operator|.
name|put
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|,
operator|new
name|String
index|[]
block|{
name|COMMAND_METADATA_ARGUMENT_DESCRIPTION
block|}
argument_list|)
expr_stmt|;
name|embedder
operator|.
name|setMetadataCommandArguments
argument_list|(
name|metadataCommandArguments
argument_list|)
expr_stmt|;
return|return
name|embedder
return|;
block|}
comment|/**      * Gets the source input stream through standard Java resource loaders       * before metadata has been embedded.      *      * @return a fresh input stream      */
specifier|protected
name|InputStream
name|getSourceStandardInputStream
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|TEST_TXT_PATH
argument_list|)
return|;
block|}
comment|/**      * Gets the source input stream via {@link TikaInputStream}      * before metadata has been embedded.      *      * @return a fresh input stream      * @throws FileNotFoundException       */
specifier|protected
name|InputStream
name|getSourceTikaInputStream
parameter_list|()
throws|throws
name|FileNotFoundException
block|{
return|return
name|TikaInputStream
operator|.
name|get
argument_list|(
name|getSourceInputFile
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Gets the source input file through standard Java resource loaders      * before metadata has been embedded.      *      * @return a fresh input stream      * @throws FileNotFoundException       */
specifier|protected
name|File
name|getSourceInputFile
parameter_list|()
throws|throws
name|FileNotFoundException
block|{
name|URL
name|origUrl
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|TEST_TXT_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|origUrl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"could not load "
operator|+
name|TEST_TXT_PATH
argument_list|)
throw|;
block|}
try|try
block|{
return|return
operator|new
name|File
argument_list|(
name|origUrl
operator|.
name|toURI
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Gets the parser to use to verify the result of the embed operation.      *      * @return the parser to read embedded metadata      */
specifier|protected
name|Parser
name|getParser
parameter_list|()
block|{
return|return
operator|new
name|TXTParser
argument_list|()
return|;
block|}
comment|/**      * Whether or not the final result of reading the now embedded metadata is      * expected in the output of the external tool      *      * @return whether or not results are expected in command line output      */
specifier|protected
name|boolean
name|getIsMetadataExpectedInOutput
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Tests embedding metadata then reading metadata to verify the results.      *      * @param isResultExpectedInOutput whether or not results are expected in command line output      */
specifier|protected
name|void
name|embedInTempFile
parameter_list|(
name|InputStream
name|sourceInputStream
parameter_list|,
name|boolean
name|isResultExpectedInOutput
parameter_list|)
block|{
name|Embedder
name|embedder
init|=
name|getEmbedder
argument_list|()
decl_stmt|;
comment|// TODO Move this check to ExternalEmbedder
name|String
name|os
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|os
operator|.
name|contains
argument_list|(
literal|"Windows"
argument_list|)
condition|)
block|{
comment|// Skip test on Windows
return|return;
block|}
name|Date
name|timestamp
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Metadata
name|metadataToEmbed
init|=
name|getMetadataToEmbed
argument_list|(
name|timestamp
argument_list|)
decl_stmt|;
try|try
block|{
name|File
name|tempOutputFile
init|=
name|tmp
operator|.
name|createTemporaryFile
argument_list|()
decl_stmt|;
name|FileOutputStream
name|tempFileOutputStream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tempOutputFile
argument_list|)
decl_stmt|;
comment|// Embed the metadata into a copy of the original output stream
name|embedder
operator|.
name|embed
argument_list|(
name|metadataToEmbed
argument_list|,
name|sourceInputStream
argument_list|,
name|tempFileOutputStream
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|Parser
name|parser
init|=
name|getParser
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
comment|// Setup the extracting content handler
name|ByteArrayOutputStream
name|result
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|OutputStreamWriter
name|outputWriter
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|result
argument_list|,
name|DEFAULT_CHARSET
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
name|outputWriter
argument_list|)
decl_stmt|;
comment|// Create a new metadata object to read the new metadata into
name|Metadata
name|embeddedMetadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|// Setup a re-read of the now embeded temp file
name|FileInputStream
name|embeddedFileInputStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|tempOutputFile
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|embeddedFileInputStream
argument_list|,
name|handler
argument_list|,
name|embeddedMetadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|String
name|outputString
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isResultExpectedInOutput
condition|)
block|{
name|outputString
operator|=
name|result
operator|.
name|toString
argument_list|(
name|DEFAULT_CHARSET
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"no metadata found"
argument_list|,
name|embeddedMetadata
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// Check each metadata property for the expected value
for|for
control|(
name|String
name|metadataName
range|:
name|metadataToEmbed
operator|.
name|names
argument_list|()
control|)
block|{
if|if
condition|(
name|metadataToEmbed
operator|.
name|get
argument_list|(
name|metadataName
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|expectedValue
init|=
name|metadataToEmbed
operator|.
name|get
argument_list|(
name|metadataName
argument_list|)
decl_stmt|;
name|boolean
name|foundExpectedValue
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|isResultExpectedInOutput
condition|)
block|{
comment|// just check that the entire output contains the expected string
name|foundExpectedValue
operator|=
name|outputString
operator|.
name|contains
argument_list|(
name|expectedValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|embeddedMetadata
operator|.
name|isMultiValued
argument_list|(
name|metadataName
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|embeddedValue
range|:
name|embeddedMetadata
operator|.
name|getValues
argument_list|(
name|metadataName
argument_list|)
control|)
block|{
if|if
condition|(
name|embeddedValue
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|embeddedValue
operator|.
name|contains
argument_list|(
name|expectedValue
argument_list|)
condition|)
block|{
name|foundExpectedValue
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
else|else
block|{
name|String
name|embeddedValue
init|=
name|embeddedMetadata
operator|.
name|get
argument_list|(
name|metadataName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"expected metadata for "
operator|+
name|metadataName
operator|+
literal|" not found"
argument_list|,
name|embeddedValue
argument_list|)
expr_stmt|;
name|foundExpectedValue
operator|=
name|embeddedValue
operator|.
name|contains
argument_list|(
name|expectedValue
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"result did not contain expected appended metadata "
operator|+
name|metadataName
operator|+
literal|"="
operator|+
name|expectedValue
argument_list|,
name|foundExpectedValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|checkSourceFileExists
parameter_list|()
block|{
name|String
name|message
init|=
literal|"the original input file was deleted"
decl_stmt|;
try|try
block|{
name|File
name|origInputFile
init|=
name|getSourceInputFile
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|,
name|origInputFile
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
argument_list|,
name|origInputFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|message
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests embedding using an input stream obtained via {@link ExternalEmbedderTest#getSourceStandardInputStream()}      *       * @throws IOException      */
annotation|@
name|Test
specifier|public
name|void
name|testEmbedStandardInputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|embedInTempFile
argument_list|(
name|getSourceStandardInputStream
argument_list|()
argument_list|,
name|getIsMetadataExpectedInOutput
argument_list|()
argument_list|)
expr_stmt|;
name|checkSourceFileExists
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests embedding using an input stream obtained via {@link ExternalEmbedderTest#getSourceTikaInputStream()}      *       * @throws IOException      */
annotation|@
name|Test
specifier|public
name|void
name|testEmbedTikaInputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|embedInTempFile
argument_list|(
name|getSourceTikaInputStream
argument_list|()
argument_list|,
name|getIsMetadataExpectedInOutput
argument_list|()
argument_list|)
expr_stmt|;
name|checkSourceFileExists
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

