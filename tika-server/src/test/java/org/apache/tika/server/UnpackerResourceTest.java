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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|archivers
operator|.
name|tar
operator|.
name|TarArchiveInputStream
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
name|binding
operator|.
name|BindingFactoryManager
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
name|endpoint
operator|.
name|Server
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
name|JAXRSBindingFactory
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
name|lifecycle
operator|.
name|SingletonResourceProvider
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

begin_class
specifier|public
class|class
name|UnpackerResourceTest
extends|extends
name|CXFTestBase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|UNPACKER_PATH
init|=
literal|"/unpacker"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ALL_PATH
init|=
literal|"/all"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|endPoint
init|=
literal|"http://localhost:"
operator|+
name|TikaServerCli
operator|.
name|DEFAULT_PORT
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DOC_WAV
init|=
literal|"Doc1_ole.doc"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|WAV1_MD5
init|=
literal|"bdd0a78a54968e362445364f95d8dc96"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|WAV1_NAME
init|=
literal|"_1310388059/MSj00974840000[1].wav"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|WAV2_MD5
init|=
literal|"3bbd42fb1ac0e46a95350285f16d9596"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|WAV2_NAME
init|=
literal|"_1310388058/MSj00748450000[1].wav"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JPG_NAME
init|=
literal|"image1.jpg"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XSL_IMAGE1_MD5
init|=
literal|"68ead8f4995a3555f48a2f738b2b0c3d"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JPG_MD5
init|=
name|XSL_IMAGE1_MD5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JPG2_NAME
init|=
literal|"image2.jpg"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JPG2_MD5
init|=
literal|"b27a41d12c646d7fc4f3826cf8183c68"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DOCX_IMAGE
init|=
literal|"2pic.docx"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCX_IMAGE1_MD5
init|=
literal|"5516590467b069fa59397432677bad4d"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCX_IMAGE2_MD5
init|=
literal|"a5dd81567427070ce0a2ff3e3ef13a4c"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCX_IMAGE1_NAME
init|=
literal|"image1.jpeg"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCX_IMAGE2_NAME
init|=
literal|"image2.jpeg"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCX_EXE1_MD5
init|=
literal|"d71ffa0623014df725f8fd2710de4411"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCX_EXE1_NAME
init|=
literal|"GMapTool.exe"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCX_EXE2_MD5
init|=
literal|"2485435c7c22d35f2de9b4c98c0c2e1a"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOCX_EXE2_NAME
init|=
literal|"Setup.exe"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XSL_IMAGE2_MD5
init|=
literal|"8969288f4245120e7c3870287cce0ff3"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|APPLICATION_MSWORD
init|=
literal|"application/msword"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|APPLICATION_XML
init|=
literal|"application/xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"Content-type"
decl_stmt|;
specifier|private
name|Server
name|server
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|JAXRSServerFactoryBean
name|sf
init|=
operator|new
name|JAXRSServerFactoryBean
argument_list|()
decl_stmt|;
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
name|TarWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|ZipWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|TikaExceptionMapper
argument_list|()
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setProviders
argument_list|(
name|providers
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setResourceClasses
argument_list|(
name|UnpackerResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setResourceProvider
argument_list|(
name|UnpackerResource
operator|.
name|class
argument_list|,
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|UnpackerResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setAddress
argument_list|(
name|endPoint
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|BindingFactoryManager
name|manager
init|=
name|sf
operator|.
name|getBus
argument_list|()
operator|.
name|getExtension
argument_list|(
name|BindingFactoryManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|JAXRSBindingFactory
name|factory
init|=
operator|new
name|JAXRSBindingFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setBus
argument_list|(
name|sf
operator|.
name|getBus
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|registerBindingFactory
argument_list|(
name|JAXRSBindingFactory
operator|.
name|JAXRS_BINDING_ID
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|server
operator|=
name|sf
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDocWAV
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
name|UNPACKER_PATH
argument_list|)
operator|.
name|type
argument_list|(
name|APPLICATION_MSWORD
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/zip"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC_WAV
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|readZipArchive
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
name|assertEquals
argument_list|(
name|WAV1_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|WAV1_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WAV2_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|WAV2_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|data
operator|.
name|containsKey
argument_list|(
name|UnpackerResource
operator|.
name|TEXT_FILENAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDocWAVText
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
name|ALL_PATH
argument_list|)
operator|.
name|type
argument_list|(
name|APPLICATION_MSWORD
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/zip"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC_WAV
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|readZipArchive
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
name|assertEquals
argument_list|(
name|WAV1_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|WAV1_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WAV2_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|WAV2_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|data
operator|.
name|containsKey
argument_list|(
name|UnpackerResource
operator|.
name|TEXT_FILENAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDocPicture
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
name|UNPACKER_PATH
argument_list|)
operator|.
name|type
argument_list|(
name|APPLICATION_MSWORD
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/zip"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC_WAV
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|readZipArchive
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
name|assertEquals
argument_list|(
name|JPG_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|JPG_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDocPictureNoOle
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
name|UNPACKER_PATH
argument_list|)
operator|.
name|type
argument_list|(
name|APPLICATION_MSWORD
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/zip"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"2pic.doc"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|readZipArchive
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
name|assertEquals
argument_list|(
name|JPG2_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|JPG2_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImageDOCX
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
name|UNPACKER_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/zip"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOCX_IMAGE
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|readZipArchive
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
name|assertEquals
argument_list|(
name|DOCX_IMAGE1_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|DOCX_IMAGE1_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOCX_IMAGE2_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|DOCX_IMAGE2_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test415
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
name|UNPACKER_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"xxx/xxx"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"*/*"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC_WAV
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|415
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
name|testExeDOCX
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|TEST_DOCX_EXE
init|=
literal|"2exe.docx"
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
name|UNPACKER_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/zip"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOCX_EXE
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|readZipArchive
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
name|assertEquals
argument_list|(
name|DOCX_EXE1_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|DOCX_EXE1_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOCX_EXE2_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|DOCX_EXE2_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImageXSL
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
name|UNPACKER_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/zip"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"pic.xls"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|readZipArchive
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
name|assertEquals
argument_list|(
name|XSL_IMAGE1_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
literal|"0.jpg"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XSL_IMAGE2_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
literal|"1.jpg"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTarDocPicture
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
name|UNPACKER_PATH
argument_list|)
operator|.
name|type
argument_list|(
name|APPLICATION_MSWORD
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/x-tar"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC_WAV
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|readArchiveFromStream
argument_list|(
operator|new
name|TarArchiveInputStream
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
decl_stmt|;
name|assertEquals
argument_list|(
name|JPG_MD5
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|JPG_NAME
argument_list|)
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
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|ALL_PATH
argument_list|)
operator|.
name|header
argument_list|(
name|CONTENT_TYPE
argument_list|,
name|APPLICATION_XML
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/zip"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
literal|"test.doc"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|responseMsg
init|=
name|readArchiveText
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
name|assertNotNull
argument_list|(
name|responseMsg
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

