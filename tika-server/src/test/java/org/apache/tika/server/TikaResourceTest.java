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
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|core
operator|.
name|header
operator|.
name|MediaTypes
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|test
operator|.
name|framework
operator|.
name|JerseyTest
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
name|assertTrue
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
name|JerseyTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TIKA_PATH
init|=
literal|"tika"
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
specifier|private
specifier|static
specifier|final
name|int
name|UNPROCESSEABLE
init|=
literal|422
decl_stmt|;
specifier|public
name|TikaResourceTest
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|(
literal|"org.apache.tika.server"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to see that the message "Hello World" is sent in the response.    */
annotation|@
name|Test
specifier|public
name|void
name|testHelloWorld
parameter_list|()
block|{
name|String
name|responseMsg
init|=
name|resource
argument_list|()
operator|.
name|path
argument_list|(
name|TIKA_PATH
argument_list|)
operator|.
name|get
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TikaResource
operator|.
name|GREETING
argument_list|,
name|responseMsg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleWord
parameter_list|()
block|{
name|String
name|responseMsg
init|=
name|resource
argument_list|()
operator|.
name|path
argument_list|(
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/msword"
argument_list|)
operator|.
name|put
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_DOC
argument_list|)
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
block|{
name|String
name|serviceWadl
init|=
name|resource
argument_list|()
operator|.
name|path
argument_list|(
literal|"application.wadl"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaTypes
operator|.
name|WADL
argument_list|)
operator|.
name|get
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|serviceWadl
operator|.
name|length
argument_list|()
operator|>
literal|0
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
name|ClientResponse
name|cr
init|=
name|resource
argument_list|()
operator|.
name|path
argument_list|(
name|TIKA_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|)
operator|.
name|put
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
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
name|cr
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

