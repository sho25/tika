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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|HashMap
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|Status
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
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
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
name|Assert
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

begin_import
import|import
name|au
operator|.
name|com
operator|.
name|bytecode
operator|.
name|opencsv
operator|.
name|CSVReader
import|;
end_import

begin_class
specifier|public
class|class
name|MetadataEPTest
extends|extends
name|CXFTestBase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|META_PATH
init|=
literal|"/metadata"
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
name|Server
name|server
decl_stmt|;
specifier|private
specifier|static
name|InputStream
name|copy
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|int
name|remaining
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|remaining
index|]
decl_stmt|;
name|int
name|n
init|=
name|in
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|remaining
operator|-=
name|n
expr_stmt|;
block|}
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
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
name|sf
operator|.
name|setResourceClasses
argument_list|(
name|MetadataEP
operator|.
name|class
argument_list|)
expr_stmt|;
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
name|CSVMessageBodyWriter
argument_list|()
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
name|sf
operator|.
name|setProviders
argument_list|(
name|providers
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
name|testSimpleWord_CSV
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
name|META_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/msword"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/csv"
argument_list|)
operator|.
name|post
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TikaResourceTest
operator|.
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Status
operator|.
name|OK
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
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
name|CSVReader
name|csvReader
init|=
operator|new
name|CSVReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|nextLine
decl_stmt|;
while|while
condition|(
operator|(
name|nextLine
operator|=
name|csvReader
operator|.
name|readNext
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|put
argument_list|(
name|nextLine
index|[
literal|0
index|]
argument_list|,
name|nextLine
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Maxim Valyanskiy"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleWord_JSON
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
name|META_PATH
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
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TikaResourceTest
operator|.
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Status
operator|.
name|OK
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
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
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
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|metadata
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Maxim Valyanskiy"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetField_Author_TEXT
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
name|META_PATH
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
name|post
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TikaResourceTest
operator|.
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Status
operator|.
name|OK
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
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Maxim Valyanskiy"
argument_list|,
name|w
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetField_Author_JSON
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
name|META_PATH
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
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TikaResourceTest
operator|.
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Status
operator|.
name|OK
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
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
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
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|metadata
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Maxim Valyanskiy"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetField_XXX_NotFound
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
name|META_PATH
operator|+
literal|"/xxx"
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
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TikaResourceTest
operator|.
name|TEST_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Status
operator|.
name|NOT_FOUND
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetField_Author_TEXT_Partial_BAD_REQUEST
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
name|META_PATH
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
name|post
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetField_Author_TEXT_Partial_Found
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
name|META_PATH
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
name|post
argument_list|(
name|copy
argument_list|(
name|stream
argument_list|,
literal|12000
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Status
operator|.
name|OK
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
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Maxim Valyanskiy"
argument_list|,
name|w
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

