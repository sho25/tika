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
name|sax
operator|.
name|BodyContentHandler
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
specifier|public
class|class
name|AutoDetectParserTest
extends|extends
name|TestCase
block|{
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
literal|"text/html"
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
literal|"text/plain"
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
argument_list|()
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
comment|/**      * Make sure that zip bomb attacks are prevented.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-216">TIKA-216</a>      */
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
argument_list|()
decl_stmt|;
operator|new
name|AutoDetectParser
argument_list|()
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

