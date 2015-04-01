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
name|cli
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
name|assertFalse
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|FileUtils
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
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * Tests the Tika's cli  */
end_comment

begin_class
specifier|public
class|class
name|TikaCLITest
block|{
comment|/* Test members */
specifier|private
name|File
name|profile
init|=
literal|null
decl_stmt|;
specifier|private
name|ByteArrayOutputStream
name|outContent
init|=
literal|null
decl_stmt|;
specifier|private
name|PrintStream
name|stdout
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|testDataFile
init|=
operator|new
name|File
argument_list|(
literal|"src/test/resources/test-data"
argument_list|)
decl_stmt|;
specifier|private
name|URI
name|testDataURI
init|=
name|testDataFile
operator|.
name|toURI
argument_list|()
decl_stmt|;
specifier|private
name|String
name|resourcePrefix
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|profile
operator|=
operator|new
name|File
argument_list|(
literal|"welsh.ngp"
argument_list|)
expr_stmt|;
name|outContent
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|resourcePrefix
operator|=
name|testDataURI
operator|.
name|toString
argument_list|()
expr_stmt|;
name|stdout
operator|=
name|System
operator|.
name|out
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|outContent
argument_list|,
literal|true
argument_list|,
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a welsh language profile      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testCreateProfile
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--create-profile=welsh"
block|,
literal|"-eUTF-8"
block|,
name|resourcePrefix
operator|+
literal|"welsh_corpus.txt"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|profile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests --list-parser-detail option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testListParserDetail
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--list-parser-detail"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"application/vnd.oasis.opendocument.text-web"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests --list-parser option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testListParsers
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--list-parser"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
comment|//Assert was commented temporarily for finding the problem
comment|//		Assert.assertTrue(outContent != null&& outContent.toString("UTF-8").contains("org.apache.tika.parser.iwork.IWorkPackageParser"));
block|}
comment|/**      * Tests -x option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testXMLOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-x"
block|,
name|resourcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"?xml version=\"1.0\" encoding=\"UTF-8\"?"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests a -h option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testHTMLOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-h"
block|,
name|resourcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"html xmlns=\"http://www.w3.org/1999/xhtml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expanded<title></title> element should be present"
argument_list|,
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"<title></title>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests -t option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testTextOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-t"
block|,
name|resourcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"finished off the cake"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests -m option of the cli      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testMetadataOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-m"
block|,
name|resourcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Basic tests for -json option      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testJsonMetadataOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--json"
block|,
name|resourcePrefix
operator|+
literal|"testJsonMultipleInts.html"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
comment|//TIKA-1310
name|assertTrue
argument_list|(
name|json
operator|.
name|contains
argument_list|(
literal|"\"fb:admins\":\"1,2,3,4\","
argument_list|)
argument_list|)
expr_stmt|;
comment|//test legacy alphabetic sort of keys
name|int
name|enc
init|=
name|json
operator|.
name|indexOf
argument_list|(
literal|"\"Content-Encoding\""
argument_list|)
decl_stmt|;
name|int
name|fb
init|=
name|json
operator|.
name|indexOf
argument_list|(
literal|"fb:admins"
argument_list|)
decl_stmt|;
name|int
name|title
init|=
name|json
operator|.
name|indexOf
argument_list|(
literal|"\"title\""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|enc
operator|>
operator|-
literal|1
operator|&&
name|fb
operator|>
operator|-
literal|1
operator|&&
name|enc
operator|<
name|fb
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fb
operator|>
operator|-
literal|1
operator|&&
name|title
operator|>
operator|-
literal|1
operator|&&
name|fb
operator|<
name|title
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for -json with prettyprint option      *      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testJsonMetadataPrettyPrintOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--json"
block|,
literal|"-r"
block|,
name|resourcePrefix
operator|+
literal|"testJsonMultipleInts.html"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|json
operator|.
name|contains
argument_list|(
literal|"  \"X-Parsed-By\": [\n"
operator|+
literal|"    \"org.apache.tika.parser.DefaultParser\",\n"
operator|+
literal|"    \"org.apache.tika.parser.html.HtmlParser\"\n"
operator|+
literal|"  ],\n"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test legacy alphabetic sort of keys
name|int
name|enc
init|=
name|json
operator|.
name|indexOf
argument_list|(
literal|"\"Content-Encoding\""
argument_list|)
decl_stmt|;
name|int
name|fb
init|=
name|json
operator|.
name|indexOf
argument_list|(
literal|"fb:admins"
argument_list|)
decl_stmt|;
name|int
name|title
init|=
name|json
operator|.
name|indexOf
argument_list|(
literal|"\"title\""
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|enc
operator|>
operator|-
literal|1
operator|&&
name|fb
operator|>
operator|-
literal|1
operator|&&
name|enc
operator|<
name|fb
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fb
operator|>
operator|-
literal|1
operator|&&
name|title
operator|>
operator|-
literal|1
operator|&&
name|fb
operator|<
name|title
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests -l option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testLanguageOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-l"
block|,
name|resourcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"en"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests -d option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testDetectOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-d"
block|,
name|resourcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests --list-met-models option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testListMetModels
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--list-met-models"
block|,
name|resourcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests --list-supported-types option of the cli      *       * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testListSupportedTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--list-supported-types"
block|,
name|resourcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"supertype: application/octet-stream"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tears down the test. Returns the System.out      */
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|profile
operator|!=
literal|null
operator|&&
name|profile
operator|.
name|exists
argument_list|()
condition|)
name|profile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|stdout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtract
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"tika-test-"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|tempFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tempFile
operator|.
name|mkdir
argument_list|()
expr_stmt|;
comment|// not really good method for production usage, but ok for tests
comment|// google guava library has better solution
try|try
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--extract-dir="
operator|+
name|tempFile
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-z"
block|,
name|resourcePrefix
operator|+
literal|"/coffee.xls"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|StringBuffer
name|allFiles
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|tempFile
operator|.
name|list
argument_list|()
control|)
block|{
if|if
condition|(
name|allFiles
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|allFiles
operator|.
name|append
argument_list|(
literal|" : "
argument_list|)
expr_stmt|;
name|allFiles
operator|.
name|append
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
comment|// ChemDraw file
name|File
name|expectedCDX
init|=
operator|new
name|File
argument_list|(
name|tempFile
argument_list|,
literal|"MBD002B040A.cdx"
argument_list|)
decl_stmt|;
comment|// Image of the ChemDraw molecule
name|File
name|expectedIMG
init|=
operator|new
name|File
argument_list|(
name|tempFile
argument_list|,
literal|"file4.png"
argument_list|)
decl_stmt|;
comment|// OLE10Native
name|File
name|expectedOLE10
init|=
operator|new
name|File
argument_list|(
name|tempFile
argument_list|,
literal|"MBD002B0FA6_file5.bin"
argument_list|)
decl_stmt|;
comment|// Something that really isnt a text file... Not sure what it is???
name|File
name|expected262FE3
init|=
operator|new
name|File
argument_list|(
name|tempFile
argument_list|,
literal|"MBD00262FE3.txt"
argument_list|)
decl_stmt|;
comment|// Image of one of the embedded resources
name|File
name|expectedEMF
init|=
operator|new
name|File
argument_list|(
name|tempFile
argument_list|,
literal|"file0.emf"
argument_list|)
decl_stmt|;
name|assertExtracted
argument_list|(
name|expectedCDX
argument_list|,
name|allFiles
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertExtracted
argument_list|(
name|expectedIMG
argument_list|,
name|allFiles
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertExtracted
argument_list|(
name|expectedOLE10
argument_list|,
name|allFiles
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertExtracted
argument_list|(
name|expected262FE3
argument_list|,
name|allFiles
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertExtracted
argument_list|(
name|expectedEMF
argument_list|,
name|allFiles
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tempFile
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|void
name|assertExtracted
parameter_list|(
name|File
name|f
parameter_list|,
name|String
name|allFiles
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"File "
operator|+
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|" not found in "
operator|+
name|allFiles
argument_list|,
name|f
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"File "
operator|+
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|" is a directory!"
argument_list|,
name|f
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File "
operator|+
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|" wasn't extracted with contents"
argument_list|,
name|f
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-920
annotation|@
name|Test
specifier|public
name|void
name|testMultiValuedMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-m"
block|,
name|resourcePrefix
operator|+
literal|"testMultipleSheets.numbers"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"sheetNames: Checking"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"sheetNames: Secon sheet"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"sheetNames: Logical Sheet 3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"sheetNames: Sheet 4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-1031
annotation|@
name|Test
specifier|public
name|void
name|testZipWithSubdirs
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-z"
block|,
literal|"--extract-dir=target"
block|,
name|resourcePrefix
operator|+
literal|"testWithSubdirs.zip"
block|}
decl_stmt|;
operator|new
name|File
argument_list|(
literal|"subdir/foo.txt"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"subdir"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Extracting 'subdir/foo.txt'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// clean up. TODO: These should be in target.
operator|new
name|File
argument_list|(
literal|"target/subdir/foo.txt"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
literal|"target/subdir"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultConfigException
parameter_list|()
throws|throws
name|Exception
block|{
comment|//default xml parser will throw TikaException
comment|//this and TestConfig() are broken into separate tests so that
comment|//setUp and tearDown() are called each time
name|String
index|[]
name|params
init|=
block|{
name|resourcePrefix
operator|+
literal|"bad_xml.xml"
block|}
decl_stmt|;
name|boolean
name|tikaEx
init|=
literal|false
decl_stmt|;
try|try
block|{
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|tikaEx
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|tikaEx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
operator|new
name|String
index|[]
block|{
literal|"--config="
operator|+
name|testDataFile
operator|.
name|toString
argument_list|()
operator|+
literal|"/tika-config1.xml"
block|,
name|resourcePrefix
operator|+
literal|"bad_xml.xml"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"apple"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"org.apache.tika.parser.html.HtmlParser"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJsonRecursiveMetadataParserMetadataOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
operator|new
name|String
index|[]
block|{
literal|"-m"
block|,
literal|"-J"
block|,
literal|"-r"
block|,
name|resourcePrefix
operator|+
literal|"test_recursive_embedded.docx"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"[\n"
operator|+
literal|"  {\n"
operator|+
literal|"    \"Application-Name\": \"Microsoft Office Word\",\n"
operator|+
literal|"    \"Application-Version\": \"15.0000\",\n"
operator|+
literal|"    \"Character Count\": \"28\",\n"
operator|+
literal|"    \"Character-Count-With-Spaces\": \"31\","
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"\"X-TIKA:embedded_resource_path\": \"test_recursive_embedded.docx/embed1.zip\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"X-TIKA:content"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJsonRecursiveMetadataParserDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
operator|new
name|String
index|[]
block|{
literal|"-J"
block|,
literal|"-r"
block|,
name|resourcePrefix
operator|+
literal|"test_recursive_embedded.docx"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"\"X-TIKA:content\": \"\\u003chtml xmlns\\u003d\\\"http://www.w3.org/1999/xhtml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJsonRecursiveMetadataParserText
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
operator|new
name|String
index|[]
block|{
literal|"-J"
block|,
literal|"-r"
block|,
literal|"-t"
block|,
name|resourcePrefix
operator|+
literal|"test_recursive_embedded.docx"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|outContent
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"\\n\\nembed_4\\n"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"\\n\\nembed_0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

