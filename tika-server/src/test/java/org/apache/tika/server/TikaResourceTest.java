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
name|server
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|attachment
operator|.
name|AttachmentUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|JAXRSServerFactoryBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|client
operator|.
name|WebClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|ext
operator|.
name|multipart
operator|.
name|Attachment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|lifecycle
operator|.
name|SingletonResourceProvider
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
name|ocr
operator|.
name|TesseractOCRConfig
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
name|ocr
operator|.
name|TesseractOCRParser
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
name|server
operator|.
name|resource
operator|.
name|TikaResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ProcessingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_class
specifier|public
class|class
name|TikaResourceTest
extends|extends
name|CXFTestBase
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TEST_DOC
init|=
literal|"test.doc"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_PASSWORD_PROTECTED
init|=
literal|"password.xls"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_RECURSIVE_DOC
init|=
literal|"test_recursive_embedded.docx"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_OOM
init|=
literal|"mock/fake_oom.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|STREAM_CLOSED_FAULT
init|=
literal|"java.io.IOException: Stream Closed"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TIKA_PATH
init|=
literal|"/tika"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|UNPROCESSEABLE
init|=
literal|422
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUpResources
parameter_list|(
name|JAXRSServerFactoryBean
name|sf
parameter_list|)
block|{
name|sf
operator|.
name|setResourceClasses
argument_list|(
name|TikaResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setResourceProvider
argument_list|(
name|TikaResource
operator|.
name|class
argument_list|,
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUpProviders
parameter_list|(
name|JAXRSServerFactoryBean
name|sf
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|providers
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|TikaServerParseExceptionMapper
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setProviders
argument_list|(
name|providers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHelloWorld
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TikaResource
operator|.
name|GREETING
argument_list|,
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleWord
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/msword"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTextMain
parameter_list|()
throws|throws
name|Exception
block|{
comment|//boilerpipe
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
operator|+
literal|"/main"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testHTML.html"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"Title : Test Indexation Html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"Indexation du fichier"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTextMainMultipart
parameter_list|()
throws|throws
name|Exception
block|{
comment|//boilerpipe
name|Attachment
name|attachmentPart
init|=
operator|new
name|Attachment
argument_list|(
literal|"myhtml"
argument_list|,
literal|"text/html"
argument_list|,
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testHTML.html"
argument_list|)
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
operator|+
literal|"/form/main"
argument_list|)
operator|.
name|type
argument_list|(
literal|"multipart/form-data"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|post
argument_list|(
name|attachmentPart
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"Title : Test Indexation Html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"Indexation du fichier"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testApplicationWadl
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
operator|+
literal|"?_wadl"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|resp
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resp
operator|.
name|startsWith
argument_list|(
literal|"<application"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPasswordXLS
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"password.xls"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|UNPROCESSEABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleWordHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/msword"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/html"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<meta name=\"X-TIKA:digest:MD5\" content=\"f8be45c34e8919eedba48cc8d207fbf0\"/>"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<meta name=\"X-TIKA:digest:SHA1\" content=\"N4EBCE7EGTIGZWETEJ6WD3W4KN32TLPG\"/>"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPasswordXLSHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/html"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"password.xls"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|UNPROCESSEABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleWordXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/msword"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/xml"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPasswordXLSXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/xml"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"password.xls"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|UNPROCESSEABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleWordMultipartXML
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC
argument_list|)
expr_stmt|;
name|Attachment
name|attachmentPart
init|=
operator|new
name|Attachment
argument_list|(
literal|"myworddoc"
argument_list|,
literal|"application/msword"
argument_list|,
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|WebClient
name|webClient
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
operator|+
literal|"/form"
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|webClient
operator|.
name|type
argument_list|(
literal|"multipart/form-data"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/xml"
argument_list|)
operator|.
name|post
argument_list|(
name|attachmentPart
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<meta name=\"X-TIKA:digest:MD5\" content=\"f8be45c34e8919eedba48cc8d207fbf0\"/>"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJAXBAndActivationDependency
parameter_list|()
block|{
comment|//TIKA-2778
name|AttachmentUtil
operator|.
name|getCommandMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmbedded
parameter_list|()
throws|throws
name|Exception
block|{
comment|//first try text
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_RECURSIVE_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"Course of human events"
argument_list|)
argument_list|)
expr_stmt|;
comment|//now go for xml -- different call than text
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/xml"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_RECURSIVE_DOC
argument_list|)
argument_list|)
expr_stmt|;
name|responseMsg
operator|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"Course of human events"
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<meta name=\"X-TIKA:digest:MD5\" content=\"59f626e09a8c16ab6dbc2800c685f772\"/>"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
comment|//TIKA-1845
annotation|@
name|Test
specifier|public
name|void
name|testWMFInRTF
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/rtf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testRTF_npeFromWMFInTikaServer.rtf"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"Example text"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//TIKA-2638 and TIKA-2816
annotation|@
name|Test
specifier|public
name|void
name|testOCRLanguageConfig
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|new
name|TesseractOCRParser
argument_list|()
operator|.
name|hasTesseract
argument_list|(
operator|new
name|TesseractOCRConfig
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_PDF_HEADER_PREFIX
operator|+
literal|"OcrStrategy"
argument_list|,
literal|"ocr_only"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_OCR_HEADER_PREFIX
operator|+
literal|"Language"
argument_list|,
literal|"eng+fra"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_OCR_HEADER_PREFIX
operator|+
literal|"MinFileSizeToOcr"
argument_list|,
literal|"10"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_OCR_HEADER_PREFIX
operator|+
literal|"MaxFileSizeToOcr"
argument_list|,
literal|"1000000000"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testOCR.pdf"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"Happy New Year 2003!"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
comment|//TIKA-2290
annotation|@
name|Test
specifier|public
name|void
name|testPDFOCRConfig
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|new
name|TesseractOCRParser
argument_list|()
operator|.
name|hasTesseract
argument_list|(
operator|new
name|TesseractOCRConfig
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_PDF_HEADER_PREFIX
operator|+
literal|"OcrStrategy"
argument_list|,
literal|"no_ocr"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testOCR.pdf"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_PDF_HEADER_PREFIX
operator|+
literal|"OcrStrategy"
argument_list|,
literal|"ocr_only"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testOCR.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|responseMsg
operator|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Happy New Year 2003!"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
comment|//now try a bad value
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_PDF_HEADER_PREFIX
operator|+
literal|"OcrStrategy"
argument_list|,
literal|"non-sense-value"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testOCR.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//TIKA-2669
annotation|@
name|Test
specifier|public
name|void
name|testPDFConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testPDFTwoTextBoxes.pdf"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|responseMsg
operator|=
name|responseMsg
operator|.
name|replaceAll
argument_list|(
literal|"[\r\n ]+"
argument_list|,
literal|" "
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Left column line 1 Right column line 1 Left colu mn line 2 Right column line 2"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_PDF_HEADER_PREFIX
operator|+
literal|"sortByPosition"
argument_list|,
literal|"false"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testPDFTwoTextBoxes.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|responseMsg
operator|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
name|responseMsg
operator|=
name|responseMsg
operator|.
name|replaceAll
argument_list|(
literal|"[\r\n ]+"
argument_list|,
literal|" "
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Left column line 1 Left column line 2 Right column line 1 Right column line 2"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
comment|//make sure that default reverts to initial config option
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testPDFTwoTextBoxes.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|responseMsg
operator|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
name|responseMsg
operator|=
name|responseMsg
operator|.
name|replaceAll
argument_list|(
literal|"[\r\n ]+"
argument_list|,
literal|" "
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Left column line 1 Right column line 1 Left colu mn line 2 Right column line 2"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractTextAcceptPlainText
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TIKA-2384
name|Attachment
name|attachmentPart
init|=
operator|new
name|Attachment
argument_list|(
literal|"my-docx-file"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|,
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"2pic.docx"
argument_list|)
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
operator|+
literal|"/form"
argument_list|)
operator|.
name|type
argument_list|(
literal|"multipart/form-data"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|post
argument_list|(
name|attachmentPart
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|responseMsg
operator|.
name|contains
argument_list|(
literal|"P1040893.JPG"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotFound
argument_list|(
name|STREAM_CLOSED_FAULT
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDataIntegrityCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_OCR_HEADER_PREFIX
operator|+
literal|"tesseractPath"
argument_list|,
literal|"C://tmp//hello.bat\u0000"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testOCR.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ProcessingException
name|e
parameter_list|)
block|{
comment|//can't tell why this intermittently happens. :(
comment|//started after the upgrade to 3.2.7
block|}
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_OCR_HEADER_PREFIX
operator|+
literal|"tesseractPath"
argument_list|,
literal|"bogus path"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testOCR.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTrustedMethodPrevention
parameter_list|()
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_OCR_HEADER_PREFIX
operator|+
literal|"trustedPageSeparator"
argument_list|,
literal|"\u0020"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testOCR.pdf"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloatInHeader
parameter_list|()
block|{
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/pdf"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|X_TIKA_PDF_HEADER_PREFIX
operator|+
literal|"averageCharTolerance"
argument_list|,
literal|"2.0"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testOCR.pdf"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOOMInLegacyMode
parameter_list|()
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_OOM
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//oom may or may not cause an exception depending
comment|//on the timing
block|}
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_RECURSIVE_DOC
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"plundered our seas"
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnicodePasswordProtectedSpaces
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TIKA-2858
specifier|final
name|String
name|password
init|=
literal|"    "
decl_stmt|;
specifier|final
name|String
name|encoded
init|=
operator|new
name|Base64
argument_list|()
operator|.
name|encodeAsString
argument_list|(
name|password
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|PASSWORD_BASE64_UTF8
argument_list|,
name|encoded
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testPassword4Spaces.pdf"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"Just some text."
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnicodePasswordProtectedUnicode
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TIKA-2858
specifier|final
name|String
name|password
init|=
literal|"  !<> \" \\ \u20AC \u0153 \u00A4 \u0031\u2044\u0034 \u0031\u2044\u0032 \uD841\uDF0E \uD867\uDD98 \uD83D\uDE00  "
decl_stmt|;
specifier|final
name|String
name|encoded
init|=
operator|new
name|Base64
argument_list|()
operator|.
name|encodeAsString
argument_list|(
name|password
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|TIKA_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|header
argument_list|(
name|TikaResource
operator|.
name|PASSWORD_BASE64_UTF8
argument_list|,
name|encoded
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"testUnicodePassword.pdf"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|getStringFromInputStream
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"Just some text."
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

