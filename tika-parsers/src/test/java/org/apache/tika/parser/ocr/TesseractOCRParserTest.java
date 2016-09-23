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
name|ocr
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
name|parser
operator|.
name|ocr
operator|.
name|TesseractOCRParser
operator|.
name|getTesseractProg
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
name|assertEquals
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
name|Assume
operator|.
name|assumeTrue
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
name|List
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
name|TikaTest
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
name|AutoDetectParser
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
name|DefaultParser
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
name|RecursiveParserWrapper
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
name|external
operator|.
name|ExternalParser
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
name|image
operator|.
name|ImageParser
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
name|pdf
operator|.
name|PDFParserConfig
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
name|BasicContentHandlerFactory
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_class
specifier|public
class|class
name|TesseractOCRParserTest
extends|extends
name|TikaTest
block|{
specifier|public
specifier|static
name|boolean
name|canRun
parameter_list|()
block|{
name|TesseractOCRConfig
name|config
init|=
operator|new
name|TesseractOCRConfig
argument_list|()
decl_stmt|;
name|TesseractOCRParserTest
name|tesseractOCRTest
init|=
operator|new
name|TesseractOCRParserTest
argument_list|()
decl_stmt|;
return|return
name|tesseractOCRTest
operator|.
name|canRun
argument_list|(
name|config
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|canRun
parameter_list|(
name|TesseractOCRConfig
name|config
parameter_list|)
block|{
name|String
index|[]
name|checkCmd
init|=
block|{
name|config
operator|.
name|getTesseractPath
argument_list|()
operator|+
name|getTesseractProg
argument_list|()
block|}
decl_stmt|;
comment|// If Tesseract is not on the path, do not run the test.
return|return
name|ExternalParser
operator|.
name|check
argument_list|(
name|checkCmd
argument_list|)
return|;
block|}
comment|/*     Check that if Tesseract is not found, the TesseractOCRParser claims to not support     any file types. So, the standard image parser is called instead.      */
annotation|@
name|Test
specifier|public
name|void
name|offersNoTypesIfNotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|TesseractOCRParser
name|parser
init|=
operator|new
name|TesseractOCRParser
argument_list|()
decl_stmt|;
name|DefaultParser
name|defaultParser
init|=
operator|new
name|DefaultParser
argument_list|()
decl_stmt|;
name|MediaType
name|png
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"png"
argument_list|)
decl_stmt|;
comment|// With an invalid path, will offer no types
name|TesseractOCRConfig
name|invalidConfig
init|=
operator|new
name|TesseractOCRConfig
argument_list|()
decl_stmt|;
name|invalidConfig
operator|.
name|setTesseractPath
argument_list|(
literal|"/made/up/path"
argument_list|)
expr_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parseContext
operator|.
name|set
argument_list|(
name|TesseractOCRConfig
operator|.
name|class
argument_list|,
name|invalidConfig
argument_list|)
expr_stmt|;
comment|// No types offered
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|parser
operator|.
name|getSupportedTypes
argument_list|(
name|parseContext
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// And DefaultParser won't use us
name|assertEquals
argument_list|(
name|ImageParser
operator|.
name|class
argument_list|,
name|defaultParser
operator|.
name|getParsers
argument_list|(
name|parseContext
argument_list|)
operator|.
name|get
argument_list|(
name|png
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*     If Tesseract is found, test we retrieve the proper number of supporting Parsers.      */
annotation|@
name|Test
specifier|public
name|void
name|offersTypesIfFound
parameter_list|()
throws|throws
name|Exception
block|{
name|TesseractOCRParser
name|parser
init|=
operator|new
name|TesseractOCRParser
argument_list|()
decl_stmt|;
name|DefaultParser
name|defaultParser
init|=
operator|new
name|DefaultParser
argument_list|()
decl_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|MediaType
name|png
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"png"
argument_list|)
decl_stmt|;
comment|// Assuming that Tesseract is on the path, we should find 5 Parsers that support PNG.
name|assumeTrue
argument_list|(
name|canRun
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|parser
operator|.
name|getSupportedTypes
argument_list|(
name|parseContext
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|parser
operator|.
name|getSupportedTypes
argument_list|(
name|parseContext
argument_list|)
operator|.
name|contains
argument_list|(
name|png
argument_list|)
argument_list|)
expr_stmt|;
comment|// DefaultParser will now select the TesseractOCRParser.
name|assertEquals
argument_list|(
name|TesseractOCRParser
operator|.
name|class
argument_list|,
name|defaultParser
operator|.
name|getParsers
argument_list|(
name|parseContext
argument_list|)
operator|.
name|get
argument_list|(
name|png
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPDFOCR
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|resource
init|=
literal|"/test-documents/testOCR.pdf"
decl_stmt|;
name|String
index|[]
name|nonOCRContains
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
name|testBasicOCR
argument_list|(
name|resource
argument_list|,
name|nonOCRContains
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDOCXOCR
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|resource
init|=
literal|"/test-documents/testOCR.docx"
decl_stmt|;
name|String
index|[]
name|nonOCRContains
init|=
block|{
literal|"This is some text."
block|,
literal|"Here is an embedded image:"
block|}
decl_stmt|;
name|testBasicOCR
argument_list|(
name|resource
argument_list|,
name|nonOCRContains
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPPTXOCR
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|resource
init|=
literal|"/test-documents/testOCR.pptx"
decl_stmt|;
name|String
index|[]
name|nonOCRContains
init|=
block|{
literal|"This is some text"
block|}
decl_stmt|;
name|testBasicOCR
argument_list|(
name|resource
argument_list|,
name|nonOCRContains
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOCROutputsHOCR
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|canRun
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|resource
init|=
literal|"/test-documents/testOCR.pdf"
decl_stmt|;
name|String
index|[]
name|nonOCRContains
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
name|String
name|contents
init|=
name|runOCR
argument_list|(
name|resource
argument_list|,
name|nonOCRContains
argument_list|,
literal|2
argument_list|,
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|XML
argument_list|,
name|TesseractOCRConfig
operator|.
name|OUTPUT_TYPE
operator|.
name|HOCR
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"<span class=\"ocrx_word\" id=\"word_1_1\""
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Happy</span>"
argument_list|,
name|contents
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testBasicOCR
parameter_list|(
name|String
name|resource
parameter_list|,
name|String
index|[]
name|nonOCRContains
parameter_list|,
name|int
name|numMetadatas
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|contents
init|=
name|runOCR
argument_list|(
name|resource
argument_list|,
name|nonOCRContains
argument_list|,
name|numMetadatas
argument_list|,
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|,
name|TesseractOCRConfig
operator|.
name|OUTPUT_TYPE
operator|.
name|TXT
argument_list|)
decl_stmt|;
if|if
condition|(
name|canRun
argument_list|()
condition|)
block|{
if|if
condition|(
name|resource
operator|.
name|substring
argument_list|(
name|resource
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|,
name|resource
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|".jpg"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|contents
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Apache"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|contents
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Happy New Year 2003!"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|String
name|runOCR
parameter_list|(
name|String
name|resource
parameter_list|,
name|String
index|[]
name|nonOCRContains
parameter_list|,
name|int
name|numMetadatas
parameter_list|,
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
name|handlerType
parameter_list|,
name|TesseractOCRConfig
operator|.
name|OUTPUT_TYPE
name|outputType
parameter_list|)
throws|throws
name|Exception
block|{
name|TesseractOCRConfig
name|config
init|=
operator|new
name|TesseractOCRConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setOutputType
argument_list|(
name|outputType
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
operator|new
name|AutoDetectParser
argument_list|()
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|handlerType
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|PDFParserConfig
name|pdfConfig
init|=
operator|new
name|PDFParserConfig
argument_list|()
decl_stmt|;
name|pdfConfig
operator|.
name|setExtractInlineImages
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parseContext
operator|.
name|set
argument_list|(
name|TesseractOCRConfig
operator|.
name|class
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|parseContext
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
name|parseContext
operator|.
name|set
argument_list|(
name|PDFParserConfig
operator|.
name|class
argument_list|,
name|pdfConfig
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|TesseractOCRParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
init|)
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
operator|(
operator|(
name|RecursiveParserWrapper
operator|)
name|parser
operator|)
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numMetadatas
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuilder
name|contents
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Metadata
name|m
range|:
name|metadataList
control|)
block|{
name|contents
operator|.
name|append
argument_list|(
name|m
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|needle
range|:
name|nonOCRContains
control|)
block|{
name|assertContains
argument_list|(
name|needle
argument_list|,
name|contents
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|names
argument_list|()
operator|.
name|length
operator|>
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|names
argument_list|()
operator|.
name|length
operator|>
literal|10
argument_list|)
expr_stmt|;
comment|//test at least one value
name|assertEquals
argument_list|(
literal|"deflate"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|"Compression CompressionTypeName"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|contents
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleImage
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|canRun
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|getXML
argument_list|(
literal|"testOCR.jpg"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"OCR Testing"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImageMagick
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|stream
init|=
name|TesseractOCRConfig
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-properties/TesseractOCR.properties"
argument_list|)
decl_stmt|;
name|TesseractOCRConfig
name|config
init|=
operator|new
name|TesseractOCRConfig
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|String
index|[]
name|CheckCmd
init|=
block|{
name|config
operator|.
name|getImageMagickPath
argument_list|()
operator|+
name|TesseractOCRParser
operator|.
name|getImageMagickProg
argument_list|()
block|}
decl_stmt|;
name|assumeTrue
argument_list|(
name|ExternalParser
operator|.
name|check
argument_list|(
name|CheckCmd
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNormalMetadataToo
parameter_list|()
throws|throws
name|Exception
block|{
comment|//this should be successful whether or not TesseractOCR is installed/active
comment|//If tesseract is installed, the internal metadata extraction parser should
comment|//work; and if tesseract isn't installed, the regular parsers should take over.
comment|//gif
name|Metadata
name|m
init|=
name|getXML
argument_list|(
literal|"testGIF.gif"
argument_list|)
operator|.
name|metadata
decl_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|names
argument_list|()
operator|.
name|length
operator|>
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RGB"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"Chroma ColorSpaceType"
argument_list|)
argument_list|)
expr_stmt|;
comment|//jpg
name|m
operator|=
name|getXML
argument_list|(
literal|"testOCR.jpg"
argument_list|)
operator|.
name|metadata
expr_stmt|;
name|assertEquals
argument_list|(
literal|"136"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"66"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"8"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SAMPLES_PER_PIXEL
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is a test Apache Tika imag"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|COMMENTS
argument_list|)
argument_list|)
expr_stmt|;
comment|//bmp
name|m
operator|=
name|getXML
argument_list|(
literal|"testBMP.bmp"
argument_list|)
operator|.
name|metadata
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
comment|//png
name|m
operator|=
name|getXML
argument_list|(
literal|"testPNG.png"
argument_list|)
operator|.
name|metadata
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UnsignedIntegral"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"Data SampleFormat"
argument_list|)
argument_list|)
expr_stmt|;
comment|//tiff
name|m
operator|=
name|getXML
argument_list|(
literal|"testTIFF.tif"
argument_list|)
operator|.
name|metadata
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"72 dots per inch"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"Y Resolution"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

