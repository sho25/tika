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
name|ByteArrayInputStream
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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
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
name|detect
operator|.
name|Detector
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
name|metadata
operator|.
name|XMPDM
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
name|sax
operator|.
name|BodyContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|gagravarr
operator|.
name|tika
operator|.
name|FlacParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|gagravarr
operator|.
name|tika
operator|.
name|OpusParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|gagravarr
operator|.
name|tika
operator|.
name|VorbisParser
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
name|AutoDetectParserTest
block|{
specifier|private
name|TikaConfig
name|tika
init|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
decl_stmt|;
comment|// Easy to read constants for the MIME types:
specifier|private
specifier|static
specifier|final
name|String
name|RAW
init|=
literal|"application/octet-stream"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|EXCEL
init|=
literal|"application/vnd.ms-excel"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HTML
init|=
literal|"text/html; charset=ISO-8859-1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PDF
init|=
literal|"application/pdf"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|POWERPOINT
init|=
literal|"application/vnd.ms-powerpoint"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|KEYNOTE
init|=
literal|"application/vnd.apple.keynote"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PAGES
init|=
literal|"application/vnd.apple.pages"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NUMBERS
init|=
literal|"application/vnd.apple.numbers"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CHM
init|=
literal|"application/vnd.ms-htmlhelp"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RTF
init|=
literal|"application/rtf"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PLAINTEXT
init|=
literal|"text/plain; charset=ISO-8859-1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|UTF8TEXT
init|=
literal|"text/plain; charset=UTF-8"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|WORD
init|=
literal|"application/msword"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML
init|=
literal|"application/xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RSS
init|=
literal|"application/rss+xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BMP
init|=
literal|"image/x-ms-bmp"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GIF
init|=
literal|"image/gif"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JPEG
init|=
literal|"image/jpeg"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PNG
init|=
literal|"image/png"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OGG_VORBIS
init|=
literal|"audio/vorbis"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OGG_OPUS
init|=
literal|"audio/opus"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OGG_FLAC
init|=
literal|"audio/x-oggflac"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FLAC_NATIVE
init|=
literal|"audio/x-flac"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OPENOFFICE
init|=
literal|"application/vnd.oasis.opendocument.text"
decl_stmt|;
comment|/**      * This is where a single test is done.      * @param tp the parameters encapsulated in a TestParams instance      * @throws IOException      */
specifier|private
name|void
name|assertAutoDetect
parameter_list|(
name|TestParams
name|tp
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|AutoDetectParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|tp
operator|.
name|resourceRealName
argument_list|)
decl_stmt|;
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not open stream from specified resource: "
operator|+
name|tp
operator|.
name|resourceRealName
argument_list|)
expr_stmt|;
block|}
try|try
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
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|tp
operator|.
name|resourceStatedName
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|tp
operator|.
name|statedType
argument_list|)
expr_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|AutoDetectParser
argument_list|(
name|tika
argument_list|)
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bad content type: "
operator|+
name|tp
argument_list|,
name|tp
operator|.
name|realType
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
if|if
condition|(
name|tp
operator|.
name|expectedContentFragment
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Expected content not found: "
operator|+
name|tp
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|tp
operator|.
name|expectedContentFragment
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Convenience method -- its sole purpose of existence is to make the      * call to it more readable than it would be if a TestParams instance      * would need to be instantiated there.      *      * @param resourceRealName real name of resource      * @param resourceStatedName stated name -- will a bad name fool us?      * @param realType - the real MIME type      * @param statedType - stated MIME type - will a wrong one fool us?      * @param expectedContentFragment - something expected in the text      * @throws Exception      */
specifier|private
name|void
name|assertAutoDetect
parameter_list|(
name|String
name|resourceRealName
parameter_list|,
name|String
name|resourceStatedName
parameter_list|,
name|String
name|realType
parameter_list|,
name|String
name|statedType
parameter_list|,
name|String
name|expectedContentFragment
parameter_list|)
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
operator|new
name|TestParams
argument_list|(
name|resourceRealName
argument_list|,
name|resourceStatedName
argument_list|,
name|realType
argument_list|,
name|statedType
argument_list|,
name|expectedContentFragment
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertAutoDetect
parameter_list|(
name|String
name|resource
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|Exception
block|{
name|resource
operator|=
literal|"/test-documents/"
operator|+
name|resource
expr_stmt|;
comment|// TODO !!!!  The disabled tests below should work!
comment|// The correct MIME type should be determined regardless of the
comment|// stated type (ContentType hint) and the stated URL name.
comment|// Try different combinations of correct and incorrect arguments:
specifier|final
name|String
name|wrongMimeType
init|=
name|RAW
decl_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
name|resource
argument_list|,
name|type
argument_list|,
name|type
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
name|resource
argument_list|,
name|type
argument_list|,
literal|null
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
name|resource
argument_list|,
name|type
argument_list|,
name|wrongMimeType
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
literal|null
argument_list|,
name|type
argument_list|,
name|type
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
literal|null
argument_list|,
name|type
argument_list|,
literal|null
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
literal|null
argument_list|,
name|type
argument_list|,
name|wrongMimeType
argument_list|,
name|content
argument_list|)
expr_stmt|;
specifier|final
name|String
name|badResource
init|=
literal|"a.xyz"
decl_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
name|badResource
argument_list|,
name|type
argument_list|,
name|type
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
name|badResource
argument_list|,
name|type
argument_list|,
literal|null
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
name|resource
argument_list|,
name|badResource
argument_list|,
name|type
argument_list|,
name|wrongMimeType
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testKeynote
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testKeynote.key"
argument_list|,
name|KEYNOTE
argument_list|,
literal|"A sample presentation"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPages
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testPages.pages"
argument_list|,
name|PAGES
argument_list|,
literal|"Sample pages document"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNumbers
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testNumbers.numbers"
argument_list|,
name|NUMBERS
argument_list|,
literal|"Checking Account: 300545668"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChm
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testChm.chm"
argument_list|,
name|CHM
argument_list|,
literal|"If you do not specify a window type or a window name, the main window is used."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEpub
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testEPUB.epub"
argument_list|,
literal|"application/epub+zip"
argument_list|,
literal|"The previous headings were subchapters"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExcel
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
name|EXCEL
argument_list|,
literal|"Sample Excel Worksheet"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testHTML.html"
argument_list|,
name|HTML
argument_list|,
literal|"Test Indexation Html"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOpenOffice
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testOpenOffice2.odt"
argument_list|,
name|OPENOFFICE
argument_list|,
literal|"This is a sample Open Office document"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPDF
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testPDF.pdf"
argument_list|,
name|PDF
argument_list|,
literal|"Content Analysis Toolkit"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPowerpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testPPT.ppt"
argument_list|,
name|POWERPOINT
argument_list|,
literal|"Sample Powerpoint Slide"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRdfXml
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testRDF.rdf"
argument_list|,
literal|"application/rdf+xml"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRTF
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testRTF.rtf"
argument_list|,
name|RTF
argument_list|,
literal|"indexation Word"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testText
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testTXT.txt"
argument_list|,
name|PLAINTEXT
argument_list|,
literal|"indexation de Txt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTextNonASCIIUTF8
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testTXTNonASCIIUTF8.txt"
argument_list|,
name|UTF8TEXT
argument_list|,
literal|"The quick brown fox jumps over the lazy dog"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWord
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testWORD.doc"
argument_list|,
name|WORD
argument_list|,
literal|"Sample Word Document"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXML
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testXML.xml"
argument_list|,
name|XML
argument_list|,
literal|"Lius"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRss
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"/test-documents/rsstest.rss"
argument_list|,
literal|"feed"
argument_list|,
name|RSS
argument_list|,
literal|"application/rss+xml"
argument_list|,
literal|"Sample RSS File for Junit test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImages
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"testBMP.bmp"
argument_list|,
name|BMP
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"testGIF.gif"
argument_list|,
name|GIF
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"testJPEG.jpg"
argument_list|,
name|JPEG
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"testPNG.png"
argument_list|,
name|PNG
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Make sure that zip bomb attacks are prevented.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-216">TIKA-216</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testZipBombPrevention
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|tgz
init|=
name|AutoDetectParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/TIKA-216.tgz"
argument_list|)
decl_stmt|;
try|try
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
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
operator|new
name|AutoDetectParser
argument_list|(
name|tika
argument_list|)
operator|.
name|parse
argument_list|(
name|tgz
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Zip bomb was not detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|tgz
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Make sure XML parse errors don't trigger ZIP bomb detection.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-1322">TIKA-1322</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testNoBombDetectedForInvalidXml
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create zip with ten empty / invalid XML files, 1.xml .. 10.xml
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ZipOutputStream
name|zos
init|=
operator|new
name|ZipOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|zos
operator|.
name|putNextEntry
argument_list|(
operator|new
name|ZipEntry
argument_list|(
name|i
operator|+
literal|".xml"
argument_list|)
argument_list|)
expr_stmt|;
name|zos
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|zos
operator|.
name|finish
argument_list|()
expr_stmt|;
name|zos
operator|.
name|close
argument_list|()
expr_stmt|;
operator|new
name|AutoDetectParser
argument_list|(
name|tika
argument_list|)
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test to ensure that the Ogg Audio parsers (Vorbis, Opus, Flac etc)      *  have been correctly included, and are available      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testOggFlacAudio
parameter_list|()
throws|throws
name|Exception
block|{
comment|// The three test files should all have similar test data
name|String
index|[]
name|testFiles
init|=
operator|new
name|String
index|[]
block|{
literal|"testVORBIS.ogg"
block|,
literal|"testFLAC.flac"
block|,
literal|"testFLAC.oga"
block|,
literal|"testOPUS.opus"
block|}
decl_stmt|;
name|MediaType
index|[]
name|mediaTypes
init|=
operator|new
name|MediaType
index|[]
block|{
name|MediaType
operator|.
name|parse
argument_list|(
name|OGG_VORBIS
argument_list|)
block|,
name|MediaType
operator|.
name|parse
argument_list|(
name|FLAC_NATIVE
argument_list|)
block|,
name|MediaType
operator|.
name|parse
argument_list|(
name|OGG_FLAC
argument_list|)
block|,
name|MediaType
operator|.
name|parse
argument_list|(
name|OGG_OPUS
argument_list|)
block|}
decl_stmt|;
comment|// Check we can load the parsers, and they claim to do the right things
name|VorbisParser
name|vParser
init|=
operator|new
name|VorbisParser
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Parser not found for "
operator|+
name|mediaTypes
index|[
literal|0
index|]
argument_list|,
name|vParser
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FlacParser
name|fParser
init|=
operator|new
name|FlacParser
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Parser not found for "
operator|+
name|mediaTypes
index|[
literal|1
index|]
argument_list|,
name|fParser
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Parser not found for "
operator|+
name|mediaTypes
index|[
literal|2
index|]
argument_list|,
name|fParser
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|OpusParser
name|oParser
init|=
operator|new
name|OpusParser
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Parser not found for "
operator|+
name|mediaTypes
index|[
literal|3
index|]
argument_list|,
name|oParser
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check we found the parser
name|CompositeParser
name|parser
init|=
operator|(
name|CompositeParser
operator|)
name|tika
operator|.
name|getParser
argument_list|()
decl_stmt|;
for|for
control|(
name|MediaType
name|mt
range|:
name|mediaTypes
control|)
block|{
name|assertNotNull
argument_list|(
literal|"Parser not found for "
operator|+
name|mt
argument_list|,
name|parser
operator|.
name|getParsers
argument_list|()
operator|.
name|get
argument_list|(
name|mt
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Have each file parsed, and check
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|file
init|=
name|testFiles
index|[
name|i
index|]
decl_stmt|;
name|InputStream
name|input
init|=
name|AutoDetectParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not find test file "
operator|+
name|file
argument_list|)
expr_stmt|;
block|}
try|try
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
operator|new
name|AutoDetectParser
argument_list|(
name|tika
argument_list|)
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect content type for "
operator|+
name|file
argument_list|,
name|mediaTypes
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
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
comment|// Check some of the common metadata
comment|// Old style metadata
name|assertEquals
argument_list|(
literal|"Test Artist"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test Title"
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
comment|// New style metadata
name|assertEquals
argument_list|(
literal|"Test Artist"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test Title"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check some of the XMPDM metadata
if|if
condition|(
operator|!
name|file
operator|.
name|endsWith
argument_list|(
literal|".opus"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Test Album"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|ALBUM
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Test Artist"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|ARTIST
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Stereo"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|AUDIO_CHANNEL_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"44100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|AUDIO_SAMPLE_RATE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check some of the text
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
literal|"Test Title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test Artist"
argument_list|)
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
block|}
block|}
comment|/**      * Test case for TIKA-514. Provide constructor for AutoDetectParser that has explicit      * list of supported parsers.      * @see<a href="https://issues.apache.org/jira/browse/TIKA-514">TIKA-514</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testSpecificParserList
parameter_list|()
throws|throws
name|Exception
block|{
name|AutoDetectParser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|(
operator|new
name|MyDetector
argument_list|()
argument_list|,
operator|new
name|MyParser
argument_list|()
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"test"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"MyParser"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|MediaType
name|MY_MEDIA_TYPE
init|=
operator|new
name|MediaType
argument_list|(
literal|"application"
argument_list|,
literal|"x-myparser"
argument_list|)
decl_stmt|;
comment|/**      * A test detector which always returns the type supported      *  by the test parser      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
specifier|private
specifier|static
class|class
name|MyDetector
implements|implements
name|Detector
block|{
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|MY_MEDIA_TYPE
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
specifier|private
specifier|static
class|class
name|MyParser
extends|extends
name|AbstractParser
block|{
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
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedTypes
init|=
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
name|supportedTypes
operator|.
name|add
argument_list|(
name|MY_MEDIA_TYPE
argument_list|)
expr_stmt|;
return|return
name|supportedTypes
return|;
block|}
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
block|{
name|metadata
operator|.
name|add
argument_list|(
literal|"MyParser"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Minimal class to encapsulate all parameters -- the main reason for      * its existence is to aid in debugging via its toString() method.      *      * Getters and setters intentionally not provided.      */
specifier|private
specifier|static
class|class
name|TestParams
block|{
specifier|public
name|String
name|resourceRealName
decl_stmt|;
specifier|public
name|String
name|resourceStatedName
decl_stmt|;
specifier|public
name|String
name|realType
decl_stmt|;
specifier|public
name|String
name|statedType
decl_stmt|;
specifier|public
name|String
name|expectedContentFragment
decl_stmt|;
specifier|private
name|TestParams
parameter_list|(
name|String
name|resourceRealName
parameter_list|,
name|String
name|resourceStatedName
parameter_list|,
name|String
name|realType
parameter_list|,
name|String
name|statedType
parameter_list|,
name|String
name|expectedContentFragment
parameter_list|)
block|{
name|this
operator|.
name|resourceRealName
operator|=
name|resourceRealName
expr_stmt|;
name|this
operator|.
name|resourceStatedName
operator|=
name|resourceStatedName
expr_stmt|;
name|this
operator|.
name|realType
operator|=
name|realType
expr_stmt|;
name|this
operator|.
name|statedType
operator|=
name|statedType
expr_stmt|;
name|this
operator|.
name|expectedContentFragment
operator|=
name|expectedContentFragment
expr_stmt|;
block|}
comment|/**          * Produces a string like the following:          *          *<pre>          * Test parameters:          *   resourceRealName        = /test-documents/testEXCEL.xls          *   resourceStatedName      = null          *   realType                = application/vnd.ms-excel          *   statedType              = null          *   expectedContentFragment = Sample Excel Worksheet          *</pre>          */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Test parameters:\n"
operator|+
literal|"  resourceRealName        = "
operator|+
name|resourceRealName
operator|+
literal|"\n"
operator|+
literal|"  resourceStatedName      = "
operator|+
name|resourceStatedName
operator|+
literal|"\n"
operator|+
literal|"  realType                = "
operator|+
name|realType
operator|+
literal|"\n"
operator|+
literal|"  statedType              = "
operator|+
name|statedType
operator|+
literal|"\n"
operator|+
literal|"  expectedContentFragment = "
operator|+
name|expectedContentFragment
operator|+
literal|"\n"
return|;
block|}
block|}
block|}
end_class

end_unit

