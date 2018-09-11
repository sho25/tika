begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
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
name|ResourceProvider
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
name|server
operator|.
name|resource
operator|.
name|DetectorResource
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
name|MetadataResource
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
name|RecursiveMetadataResource
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
name|apache
operator|.
name|tika
operator|.
name|server
operator|.
name|resource
operator|.
name|UnpackerResource
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
name|writer
operator|.
name|CSVMessageBodyWriter
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
name|writer
operator|.
name|JSONMessageBodyWriter
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
name|writer
operator|.
name|TextMessageBodyWriter
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
name|writer
operator|.
name|XMPMessageBodyWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
comment|/**  * Test to make sure that no stack traces are returned  * when the stack trace param is set to false.  */
end_comment

begin_class
specifier|public
class|class
name|StackTraceOffTest
extends|extends
name|CXFTestBase
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TEST_NULL
init|=
literal|"mock/null_pointer.xml"
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
index|[]
name|PATHS
init|=
operator|new
name|String
index|[]
block|{
literal|"/tika"
block|,
literal|"/rmeta"
block|,
literal|"/unpack"
block|,
literal|"/meta"
block|,     }
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
name|List
argument_list|<
name|ResourceProvider
argument_list|>
name|rCoreProviders
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceProvider
argument_list|>
argument_list|()
decl_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|MetadataResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|RecursiveMetadataResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|DetectorResource
argument_list|(
operator|new
name|ServerStatus
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
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
name|setResourceProviders
argument_list|(
name|rCoreProviders
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
name|providers
operator|.
name|add
argument_list|(
operator|new
name|JSONMessageBodyWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|CSVMessageBodyWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|XMPMessageBodyWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|TextMessageBodyWriter
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEncrypted
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|path
range|:
name|PATHS
control|)
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
name|path
argument_list|)
operator|.
name|accept
argument_list|(
literal|"*/*"
argument_list|)
operator|.
name|header
argument_list|(
literal|"Content-Disposition"
argument_list|,
literal|"attachment; filename="
operator|+
name|TEST_PASSWORD_PROTECTED
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_PASSWORD_PROTECTED
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null response: "
operator|+
name|path
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unprocessable: "
operator|+
name|path
argument_list|,
name|UNPROCESSEABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|msg
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
name|assertEquals
argument_list|(
literal|"should be empty: "
operator|+
name|path
argument_list|,
literal|""
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullPointerOnTika
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|path
range|:
name|PATHS
control|)
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
name|path
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
name|TEST_NULL
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null response: "
operator|+
name|path
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unprocessable: "
operator|+
name|path
argument_list|,
name|UNPROCESSEABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|msg
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
name|assertEquals
argument_list|(
literal|"should be empty: "
operator|+
name|path
argument_list|,
literal|""
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
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
comment|//no stack traces for 415
for|for
control|(
name|String
name|path
range|:
name|PATHS
control|)
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
name|path
argument_list|)
operator|.
name|type
argument_list|(
literal|"blechdeblah/deblechdeblah"
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
name|TEST_NULL
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null response: "
operator|+
name|path
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bad type: "
operator|+
name|path
argument_list|,
literal|415
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|msg
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
name|assertEquals
argument_list|(
literal|"should be empty: "
operator|+
name|path
argument_list|,
literal|""
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|//For now, make sure that non-complete document
comment|//still returns BAD_REQUEST.  We may want to
comment|//make MetadataResource return the same types of parse
comment|//exceptions as the others...
annotation|@
name|Test
specifier|public
name|void
name|testMeta
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|stream
init|=
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TikaResourceTest
operator|.
name|TEST_DOC
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
literal|"/meta"
operator|+
literal|"/Author"
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/msword"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
operator|.
name|put
argument_list|(
name|copy
argument_list|(
name|stream
argument_list|,
literal|8000
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Response
operator|.
name|Status
operator|.
name|BAD_REQUEST
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|msg
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
name|assertEquals
argument_list|(
literal|"Failed to get metadata field Author"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

