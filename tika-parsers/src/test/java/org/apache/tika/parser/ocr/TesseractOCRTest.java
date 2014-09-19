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
name|java
operator|.
name|io
operator|.
name|InputStream
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

begin_class
specifier|public
class|class
name|TesseractOCRTest
extends|extends
name|TikaTest
block|{
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
literal|"tesseract"
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
annotation|@
name|Test
specifier|public
name|void
name|testPDFOCR
parameter_list|()
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
name|assumeTrue
argument_list|(
name|canRun
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|BodyContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
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
operator|new
name|TesseractOCRParser
argument_list|()
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
name|InputStream
name|stream
init|=
name|TesseractOCRTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testOCR.pdf"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
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
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|TesseractOCRConfig
name|config
init|=
operator|new
name|TesseractOCRConfig
argument_list|()
decl_stmt|;
name|assumeTrue
argument_list|(
name|canRun
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|BodyContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
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
operator|new
name|TesseractOCRParser
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|TesseractOCRTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testOCR.docx"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
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
name|assertTrue
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"This is some text."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Here is an embedded image:"
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testPPTXOCR
parameter_list|()
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
name|assumeTrue
argument_list|(
name|canRun
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|BodyContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
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
operator|new
name|TesseractOCRParser
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|TesseractOCRTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testOCR.pptx"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Check for the image's text."
argument_list|,
name|handler
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
name|assertTrue
argument_list|(
literal|"Check for the standard text."
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"This is some text"
argument_list|)
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
end_class

end_unit

