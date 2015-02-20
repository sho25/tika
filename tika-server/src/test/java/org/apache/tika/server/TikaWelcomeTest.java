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
name|Collections
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
name|Tika
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
name|TikaWelcomeTest
extends|extends
name|CXFTestBase
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|WELCOME_PATH
init|=
literal|"/"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VERSION_PATH
init|=
name|TikaVersionTest
operator|.
name|VERSION_PATH
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
name|rpsCore
init|=
name|Collections
operator|.
expr|<
name|ResourceProvider
operator|>
name|singletonList
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaVersion
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ResourceProvider
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceProvider
argument_list|>
argument_list|(
name|rpsCore
argument_list|)
decl_stmt|;
name|all
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaWelcome
argument_list|(
name|tika
argument_list|,
name|rpsCore
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setResourceProviders
argument_list|(
name|all
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
block|{     }
annotation|@
name|Test
specifier|public
name|void
name|testGetHTMLWelcome
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
name|WELCOME_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"text/html"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/html"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|html
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
operator|new
name|Tika
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|html
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"href=\"http"
argument_list|,
name|html
argument_list|)
expr_stmt|;
comment|// Check our details were found
name|assertContains
argument_list|(
literal|"GET"
argument_list|,
name|html
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|WELCOME_PATH
argument_list|,
name|html
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"text/plain"
argument_list|,
name|html
argument_list|)
expr_stmt|;
comment|// Check that the Tika Version details come through too
name|assertContains
argument_list|(
name|VERSION_PATH
argument_list|,
name|html
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetTextWelcome
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
name|WELCOME_PATH
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
name|String
name|text
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
operator|new
name|Tika
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|text
argument_list|)
expr_stmt|;
comment|// Check our details were found
name|assertContains
argument_list|(
literal|"GET "
operator|+
name|WELCOME_PATH
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"=> text/plain"
argument_list|,
name|text
argument_list|)
expr_stmt|;
comment|// Check that the Tika Version details come through too
name|assertContains
argument_list|(
literal|"GET "
operator|+
name|VERSION_PATH
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

