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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|InputStream
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
name|EncryptedDocumentException
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

begin_comment
comment|/**  * Test case for parsing rar files.  */
end_comment

begin_class
specifier|public
class|class
name|RarParserTest
extends|extends
name|AbstractPkgTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testRarParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
comment|// Should auto-detect!
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
try|try
init|(
name|InputStream
name|stream
init|=
name|RarParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test-documents.rar"
argument_list|)
init|)
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
name|recursingContext
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"application/x-rar-compressed"
argument_list|,
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testEXCEL.xls"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Sample Excel Worksheet"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testHTML.html"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Test Indexation Html"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testOpenOffice2.odt"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is a sample Open Office document"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testPDF.pdf"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Apache Tika"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testPPT.ppt"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Sample Powerpoint Slide"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testRTF.rtf"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"indexation Word"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testTXT.txt"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Test d'indexation de Txt"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testWORD.doc"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is a sample Microsoft Word Document"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testXML.xml"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Rida Benjelloun"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that the ParseContext parser is correctly      *  fired for all the embedded entries.      */
annotation|@
name|Test
specifier|public
name|void
name|testEmbedded
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
comment|// Should auto-detect!
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
try|try
init|(
name|InputStream
name|stream
init|=
name|RarParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test-documents.rar"
argument_list|)
init|)
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
name|trackingContext
argument_list|)
expr_stmt|;
block|}
comment|// Should have found all 9 documents, but not the directory
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|tracker
operator|.
name|mediatypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|tracker
operator|.
name|modifiedAts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should have names but not content types, as rar doesn't
comment|//  store the content types
name|assertEquals
argument_list|(
literal|"test-documents/testEXCEL.xls"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-documents/testHTML.html"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-documents/testOpenOffice2.odt"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-documents/testPDF.pdf"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-documents/testPPT.ppt"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-documents/testRTF.rtf"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-documents/testTXT.txt"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-documents/testWORD.doc"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-documents/testXML.xml"
argument_list|,
name|tracker
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|tracker
operator|.
name|mediatypes
control|)
block|{
name|assertNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|crt
range|:
name|tracker
operator|.
name|createdAts
control|)
block|{
name|assertNull
argument_list|(
name|crt
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|mod
range|:
name|tracker
operator|.
name|modifiedAts
control|)
block|{
name|assertNotNull
argument_list|(
name|mod
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Modified at "
operator|+
name|mod
argument_list|,
name|mod
operator|.
name|startsWith
argument_list|(
literal|"20"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Should have filenames in the content string
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testHTML.html"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testEXCEL.xls"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testOpenOffice2.odt"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testPDF.pdf"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testPPT.ppt"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testRTF.rtf"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testTXT.txt"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testWORD.doc"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test-documents/testXML.xml"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
comment|/**      * Note - we don't currently support Encrypted RAR files,      * so all we can do is throw a helpful exception      */
annotation|@
name|Test
specifier|public
name|void
name|testEncryptedRar
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|RarParser
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|RarParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test-documents-enc.rar"
argument_list|)
init|)
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|PasswordProvider
operator|.
name|class
argument_list|,
operator|new
name|PasswordProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
literal|"ApacheTika"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Note - we don't currently support encrypted RAR
comment|// files so we can't check the contents
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|trackingContext
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No support yet for Encrypted RAR files"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EncryptedDocumentException
name|e
parameter_list|)
block|{
comment|// Good, as expected right now
block|}
block|}
block|}
end_class

end_unit

