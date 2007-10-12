begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ParserConfig
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
name|TikaConfig
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
name|ParserFactory
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
name|ParseUtils
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
name|Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|JDOMException
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

begin_comment
comment|/**  * Junit test class for Tika {@link Parser}s.  */
end_comment

begin_class
specifier|public
class|class
name|TestParsers
extends|extends
name|TestCase
block|{
specifier|private
name|TikaConfig
name|tc
decl_stmt|;
specifier|private
name|File
name|testFilesBaseDir
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|JDOMException
throws|,
name|IOException
block|{
comment|/*          * FIXME the old mechanism does not work anymore when running the tests          * with Maven - need a resource-based one, but this means more changes          * to classes which rely on filenames.          *           * String sep = File.separator; StringTokenizer st = new          * StringTokenizer(System.getProperty( "java.class.path"),          * File.pathSeparator);          *           * classDir = new File(st.nextToken());          *           * config = classDir.getParent() + sep + "config" + sep + "config.xml";          *           * String log4j = classDir.getParent() + sep + "Config" + sep + "log4j" +          * sep + "log4j.properties";          */
comment|// FIXME for now, fix filenames according to Maven testing layout
comment|// The file below should be the default configuration for the test of
comment|// getDefaultConfig() to be legitimate.
specifier|final
name|String
name|tikaConfigFilename
init|=
literal|"target/classes/org/apache/tika/tika-config.xml"
decl_stmt|;
name|testFilesBaseDir
operator|=
operator|new
name|File
argument_list|(
literal|"src/test/resources/test-documents"
argument_list|)
expr_stmt|;
name|tc
operator|=
operator|new
name|TikaConfig
argument_list|(
name|tikaConfigFilename
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPDFExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testPDF.pdf"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/pdf"
argument_list|)
decl_stmt|;
name|String
name|s3
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s3
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTXTExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testTXT.txt"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"text/plain"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRTFExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testRTF.rtf"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/rtf"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testXMLExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testXML.xml"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/xml"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPPTExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testPPT.ppt"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|ParserConfig
name|config
init|=
name|tc
operator|.
name|getParserConfig
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|)
decl_stmt|;
name|Parser
name|parser
init|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|config
argument_list|)
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
operator|new
name|FileInputStream
argument_list|(
name|file
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
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
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
literal|"Sample Powerpoint Slide"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWORDxtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testWORD.doc"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/msword"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|ParserConfig
name|config
init|=
name|tc
operator|.
name|getParserConfig
argument_list|(
literal|"application/msword"
argument_list|)
decl_stmt|;
name|Parser
name|parser
init|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|config
argument_list|)
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
operator|new
name|FileInputStream
argument_list|(
name|file
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
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
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
literal|"Sample Word Document"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEXCELExtraction
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|expected
init|=
literal|"Numbers and their Squares Number Square 1.0 "
operator|+
literal|"1.0 2.0 4.0 3.0 9.0 4.0 16.0 5.0 25.0 6.0 36.0 7.0 49.0 8.0 "
operator|+
literal|"64.0 9.0 81.0 10.0 100.0 11.0 121.0 12.0 144.0 13.0 169.0 "
operator|+
literal|"14.0 196.0 15.0 225.0 Written and saved in Microsoft Excel "
operator|+
literal|"X for Mac Service Release 1."
decl_stmt|;
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testEXCEL.xls"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Text does not contain '"
operator|+
name|expected
operator|+
literal|"'"
argument_list|,
name|s1
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|ParserConfig
name|config
init|=
name|tc
operator|.
name|getParserConfig
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|)
decl_stmt|;
name|Parser
name|parser
init|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|config
argument_list|)
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
operator|new
name|FileInputStream
argument_list|(
name|file
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
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
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
literal|"Simple Excel document"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOOExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testOpenOffice2.odt"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/vnd.oasis.opendocument.text"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHTMLExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testHTML.html"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"text/html"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|ParserConfig
name|config
init|=
name|tc
operator|.
name|getParserConfig
argument_list|(
literal|"text/html"
argument_list|)
decl_stmt|;
name|Parser
name|parser
init|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parser
argument_list|)
expr_stmt|;
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
operator|new
name|FileInputStream
argument_list|(
name|file
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
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
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
literal|"Title : Test Indexation Html"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|text
init|=
name|metadata
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"Test Indexation Html"
decl_stmt|;
name|assertTrue
argument_list|(
literal|"text contains '"
operator|+
name|expected
operator|+
literal|"'"
argument_list|,
name|text
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testZipExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|zip
init|=
name|getTestFile
argument_list|(
literal|"test-documents.zip"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
name|ParseUtils
operator|.
name|getParsersFromZip
argument_list|(
name|zip
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|zipFiles
init|=
name|Utils
operator|.
name|unzip
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|zip
argument_list|)
argument_list|)
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
name|parsers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Parser
name|zipEntryParser
init|=
name|parsers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|zipEntryParser
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|zipFiles
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
comment|/* FIXME: Doesn't work with the new Parser interface                 ParserConfig config = tc.getParserConfig(                         zipEntryParser.getMimeType());                 Map<String, Content> contents = config.getContents();                 assertNotNull(contents);                 InputStream stream = new FileInputStream(zipFiles.get(j));                 try {                     zipEntryParser.getContents(stream, contents);                     assertNotNull(contents.get("fullText"));                 } finally {                     stream.close();                 }                 */
block|}
block|}
block|}
specifier|private
name|File
name|getTestFile
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|testFilesBaseDir
argument_list|,
name|filename
argument_list|)
return|;
block|}
block|}
end_class

end_unit

