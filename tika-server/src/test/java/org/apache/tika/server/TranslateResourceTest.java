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
name|TranslateResource
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
name|TarWriter
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
name|ZipWriter
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
name|TranslateResourceTest
extends|extends
name|CXFTestBase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TRANSLATE_PATH
init|=
literal|"/translate"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRANSLATE_ALL_PATH
init|=
name|TRANSLATE_PATH
operator|+
literal|"/all"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRANSLATE_TXT
init|=
literal|"This won't translate"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LINGO_PATH
init|=
literal|"/org.apache.tika.language.translate.Lingo24Translator"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SRCDEST
init|=
literal|"/es/en"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEST
init|=
literal|"/en"
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
name|TranslateResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setResourceProvider
argument_list|(
name|TranslateResource
operator|.
name|class
argument_list|,
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TranslateResource
argument_list|(
operator|new
name|ServerStatus
argument_list|()
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
name|testTranslateFull
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|url
init|=
name|endPoint
operator|+
name|TRANSLATE_ALL_PATH
operator|+
name|LINGO_PATH
operator|+
name|SRCDEST
decl_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|url
argument_list|)
operator|.
name|type
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"*/*"
argument_list|)
operator|.
name|put
argument_list|(
name|TRANSLATE_TXT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|String
name|translated
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
name|TRANSLATE_TXT
argument_list|,
name|translated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTranslateAutoLang
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|url
init|=
name|endPoint
operator|+
name|TRANSLATE_ALL_PATH
operator|+
name|LINGO_PATH
operator|+
name|DEST
decl_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|url
argument_list|)
operator|.
name|type
argument_list|(
literal|"text/plain"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"*/*"
argument_list|)
operator|.
name|put
argument_list|(
name|TRANSLATE_TXT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|String
name|translated
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
name|TRANSLATE_TXT
argument_list|,
name|translated
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

