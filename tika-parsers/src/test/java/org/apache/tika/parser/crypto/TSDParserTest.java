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
name|assertNotNull
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

begin_class
specifier|public
class|class
name|TSDParserTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testTSDFileData
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|inputXml
init|=
name|TSDParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/MANIFEST.XML.TSD"
argument_list|)
init|;
name|InputStream
name|inputTxt1
operator|=
name|TSDParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/Test1.txt.tsd"
argument_list|)
init|;
name|InputStream
name|inputTxt2
operator|=
name|TSDParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/Test2.txt.tsd"
argument_list|)
init|;
name|InputStream
name|inputDocx
operator|=
name|TSDParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/Test3.docx.tsd"
argument_list|)
init|;
name|InputStream
name|inputPdf
operator|=
name|TSDParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/Test4.pdf.tsd"
argument_list|)
init|;
name|InputStream
name|inputPng
operator|=
name|TSDParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/Test5.PNG.tsd"
argument_list|)
init|;
init|)
block|{
name|TSDParser
name|tsdParser
init|=
operator|new
name|TSDParser
argument_list|()
decl_stmt|;
name|ContentHandler
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
name|tsdParser
operator|.
name|parse
argument_list|(
name|inputXml
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Description=Time Stamped Data Envelope"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Content-Type=application/timestamped-data"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"File-Parsed=true"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|parseContext
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|tsdParser
operator|.
name|parse
argument_list|(
name|inputTxt1
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Description=Time Stamped Data Envelope"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Content-Type=application/timestamped-data"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"File-Parsed=true"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|parseContext
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|tsdParser
operator|.
name|parse
argument_list|(
name|inputTxt2
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Description=Time Stamped Data Envelope"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Content-Type=application/timestamped-data"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"File-Parsed=true"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|parseContext
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|tsdParser
operator|.
name|parse
argument_list|(
name|inputDocx
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Description=Time Stamped Data Envelope"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Content-Type=application/timestamped-data"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"File-Parsed=true"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|parseContext
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|tsdParser
operator|.
name|parse
argument_list|(
name|inputPdf
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Description=Time Stamped Data Envelope"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Content-Type=application/timestamped-data"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"File-Parsed=true"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|parseContext
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|tsdParser
operator|.
name|parse
argument_list|(
name|inputPng
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Description=Time Stamped Data Envelope"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Content-Type=application/timestamped-data"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"File-Parsed=true"
argument_list|,
name|metadata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTSDFileDataRecursiveMetadataXML
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getRecursiveMetadata
argument_list|(
literal|"MANIFEST.XML.TSD"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|TSDParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTSDFileDataRecursiveMetadataTxt1
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getRecursiveMetadata
argument_list|(
literal|"Test1.txt.tsd"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|TSDParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTSDFileDataRecursiveMetadataTxt2
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getRecursiveMetadata
argument_list|(
literal|"Test2.txt.tsd"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|TSDParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTSDFileDataRecursiveMetadataDocx
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getRecursiveMetadata
argument_list|(
literal|"Test3.docx.tsd"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|TSDParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTSDFileDataRecursiveMetadataPdf
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getRecursiveMetadata
argument_list|(
literal|"Test4.pdf.tsd"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|TSDParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//@Test
specifier|public
name|void
name|testTSDFileDataRecursiveMetadataPng
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getRecursiveMetadata
argument_list|(
literal|"Test5.PNG.tsd"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|TSDParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBrokenPdf
parameter_list|()
throws|throws
name|Exception
block|{
comment|//make sure that embedded file appears in list
comment|//and make sure embedded exception is recorded
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testTSD_broken_pdf.tsd"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_EXCEPTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"org.apache.pdfbox.pdmodel.PDDocument.load"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_EXCEPTION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

