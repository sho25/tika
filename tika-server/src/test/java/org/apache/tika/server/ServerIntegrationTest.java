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
name|TikaTest
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
name|OfficeOpenXMLExtended
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
name|serialization
operator|.
name|JsonMetadataList
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
name|util
operator|.
name|List
import|;
end_import

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

begin_class
specifier|public
class|class
name|ServerIntegrationTest
extends|extends
name|TikaTest
block|{
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
name|META_PATH
init|=
literal|"/rmeta"
decl_stmt|;
specifier|protected
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
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-p"
block|,
name|Integer
operator|.
name|toString
argument_list|(
name|TikaServerCli
operator|.
name|DEFAULT_PORT
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//test for the server being available...rather than this sleep call
name|Thread
operator|.
name|sleep
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
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
name|accept
argument_list|(
literal|"application/json"
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
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|JsonMetadataList
operator|.
name|fromJson
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Microsoft Office Word"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"plundered our seas"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|get
argument_list|(
literal|"X-TIKA:content"
argument_list|)
argument_list|)
expr_stmt|;
comment|//assertEquals("a38e6c7b38541af87148dee9634cb811", metadataList.get(10).get("X-TIKA:digest:MD5"));
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

