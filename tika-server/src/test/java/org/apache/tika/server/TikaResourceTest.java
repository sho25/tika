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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|TikaResourceTest
extends|extends
name|CXFTestBase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TIKA_PATH
init|=
literal|"/tika"
decl_stmt|;
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
name|TEST_XLSX
init|=
literal|"16637.xlsx"
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
argument_list|(
name|tika
argument_list|)
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
block|{}
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
name|put
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
block|}
block|}
end_class

end_unit

