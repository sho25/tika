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
operator|.
name|mime
package|;
end_package

begin_comment
comment|// Junit imports
end_comment

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
name|net
operator|.
name|MalformedURLException
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
name|IOException
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

begin_comment
comment|// Tika imports
end_comment

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

begin_comment
comment|/**  *   * Test Suite for the {@link MimeTypes} repository.  *   */
end_comment

begin_class
specifier|public
class|class
name|TestMimeTypes
extends|extends
name|TestCase
block|{
specifier|private
name|MimeTypes
name|repo
decl_stmt|;
specifier|private
specifier|static
name|URL
name|u
decl_stmt|;
static|static
block|{
try|try
block|{
name|u
operator|=
operator|new
name|URL
argument_list|(
literal|"http://mydomain.com/x.pdf?x=y"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
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
specifier|private
specifier|static
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"/a/b/c/x.pdf"
argument_list|)
decl_stmt|;
specifier|public
name|TestMimeTypes
parameter_list|()
block|{
try|try
block|{
name|repo
operator|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
operator|.
name|getMimeRepository
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
specifier|public
name|void
name|testCaseSensitivity
parameter_list|()
block|{
name|MimeType
name|type
init|=
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.PDF"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.pdf"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.PdF"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.pdF"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLoadMimeTypes
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|repo
operator|.
name|forName
argument_list|(
literal|"application/octet-stream"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|repo
operator|.
name|forName
argument_list|(
literal|"text/x-tex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests MIME type determination based solely on the URL's extension.      */
specifier|public
name|void
name|testGuessMimeTypes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.pdf"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
name|u
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
name|f
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/plain"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.txt"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.htm"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.html"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/xhtml+xml"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.xhtml"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/xml"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.xml"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/msword"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.doc"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.ppt"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.xls"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/zip"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.zip"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.odt"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/octet-stream"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"x.xyz"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests MimeTypes.getMimeType(URL), which examines both the byte header      * and, if necessary, the URL's extension.      */
specifier|public
name|void
name|testMimeDeterminationForTestDocuments
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"text/html"
argument_list|,
name|getMimeType
argument_list|(
literal|"testHTML.html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/zip"
argument_list|,
name|getMimeType
argument_list|(
literal|"test-documents.zip"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|getMimeType
argument_list|(
literal|"testEXCEL.xls"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html"
argument_list|,
name|getMimeType
argument_list|(
literal|"testHTML_utf8.html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
argument_list|,
name|getMimeType
argument_list|(
literal|"testOpenOffice2.odt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|getMimeType
argument_list|(
literal|"testPDF.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|,
name|getMimeType
argument_list|(
literal|"testPPT.ppt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/rtf"
argument_list|,
name|getMimeType
argument_list|(
literal|"testRTF.rtf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/plain"
argument_list|,
name|getMimeType
argument_list|(
literal|"testTXT.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/msword"
argument_list|,
name|getMimeType
argument_list|(
literal|"testWORD.doc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/xml"
argument_list|,
name|getMimeType
argument_list|(
literal|"testXML.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getMimeType
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|String
name|type
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URL
name|url
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/test-documents/"
operator|+
name|filename
argument_list|)
decl_stmt|;
name|type
operator|=
name|repo
operator|.
name|getType
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
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
return|return
name|type
return|;
block|}
block|}
end_class

end_unit

