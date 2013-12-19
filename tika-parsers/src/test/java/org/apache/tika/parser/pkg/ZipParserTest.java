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
name|HashSet
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
name|tika
operator|.
name|Tika
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
comment|/**  * Test case for parsing zip files.  */
end_comment

begin_class
specifier|public
class|class
name|ZipParserTest
extends|extends
name|AbstractPkgTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testZipParsing
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
name|InputStream
name|stream
init|=
name|ZipParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test-documents.zip"
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
name|recursingContext
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
name|assertEquals
argument_list|(
literal|"application/zip"
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
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testEXCEL.xls"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Sample Excel Worksheet"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testHTML.html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test Indexation Html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testOpenOffice2.odt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"This is a sample Open Office document"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testPDF.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Apache Tika"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testPPT.ppt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Sample Powerpoint Slide"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testRTF.rtf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"indexation Word"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testTXT.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test d'indexation de Txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testWORD.doc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"This is a sample Microsoft Word Document"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testXML.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Rida Benjelloun"
argument_list|)
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
name|InputStream
name|stream
init|=
name|ZipParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test-documents.zip"
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
name|trackingContext
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
comment|// Should have found all 9 documents
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
comment|// Should have names but not content types, as zip doesn't
comment|//  store the content types
name|assertEquals
argument_list|(
literal|"testEXCEL.xls"
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
literal|"testHTML.html"
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
literal|"testOpenOffice2.odt"
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
literal|"testPDF.pdf"
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
literal|"testPPT.ppt"
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
literal|"testRTF.rtf"
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
literal|"testTXT.txt"
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
literal|"testWORD.doc"
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
literal|"testXML.xml"
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
block|}
comment|/**      * Test case for the ability of the ZIP parser to extract the name of      * a ZIP entry even if the content of the entry is unreadable due to an      * unsupported compression method.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-346">TIKA-346</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testUnsupportedZipCompressionMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
operator|new
name|Tika
argument_list|()
operator|.
name|parseToString
argument_list|(
name|ZipParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/moby.zip"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"README"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|GatherRelIDsDocumentExtractor
implements|implements
name|EmbeddedDocumentExtractor
block|{
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|allRelIDs
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|shouldParseEmbedded
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|String
name|relID
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|EMBEDDED_RELATIONSHIP_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|relID
operator|!=
literal|null
condition|)
block|{
name|allRelIDs
operator|.
name|add
argument_list|(
name|relID
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|parseEmbedded
parameter_list|(
name|InputStream
name|inputStream
parameter_list|,
name|ContentHandler
name|contentHandler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|boolean
name|outputHtml
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"should never be called"
argument_list|)
throw|;
block|}
block|}
comment|// TIKA-1036
annotation|@
name|Test
specifier|public
name|void
name|testPlaceholders
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
name|getXML
argument_list|(
literal|"testEmbedded.zip"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"<div class=\"embedded\" id=\"test1.txt\" />"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<div class=\"embedded\" id=\"test2.txt\" />"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// Also make sure EMBEDDED_RELATIONSHIP_ID was
comment|// passed when parsing the embedded docs:
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
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
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|GatherRelIDsDocumentExtractor
name|relIDs
init|=
operator|new
name|GatherRelIDsDocumentExtractor
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|,
name|relIDs
argument_list|)
expr_stmt|;
name|InputStream
name|input
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEmbedded.zip"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|relIDs
operator|.
name|allRelIDs
operator|.
name|contains
argument_list|(
literal|"test1.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|relIDs
operator|.
name|allRelIDs
operator|.
name|contains
argument_list|(
literal|"test2.txt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

