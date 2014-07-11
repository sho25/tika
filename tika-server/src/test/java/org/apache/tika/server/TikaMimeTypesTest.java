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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|TikaMimeTypesTest
extends|extends
name|CXFTestBase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|MIMETYPES_PATH
init|=
literal|"/mime-types"
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
name|TikaMimeTypes
operator|.
name|class
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setResourceProvider
argument_list|(
name|TikaMimeTypes
operator|.
name|class
argument_list|,
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaMimeTypes
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
name|testGetPlainText
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
name|MIMETYPES_PATH
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
literal|"text/plain"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"application/xml"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"video/x-ogm"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"supertype: video/ogg"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"alias:     image/bmp"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetHTML
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
name|MIMETYPES_PATH
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
literal|"text/plain"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"application/xml"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"video/x-ogm"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<h2>text/plain"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"name=\"text/plain"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Super Type:<a href=\"#video/ogg\">video/ogg"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Alias: image/bmp"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|testGetJSON
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
name|MIMETYPES_PATH
argument_list|)
operator|.
name|type
argument_list|(
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|accept
argument_list|(
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|jsonStr
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
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|json
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|jsonStr
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|json
operator|.
name|containsKey
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|json
operator|.
name|containsKey
argument_list|(
literal|"application/xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|json
operator|.
name|containsKey
argument_list|(
literal|"video/x-ogm"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|json
operator|.
name|containsKey
argument_list|(
literal|"image/x-ms-bmp"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|bmp
init|=
name|json
operator|.
name|get
argument_list|(
literal|"image/x-ms-bmp"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|bmp
operator|.
name|containsKey
argument_list|(
literal|"alias"
argument_list|)
argument_list|)
expr_stmt|;
name|Object
index|[]
name|aliases
init|=
operator|(
name|Object
index|[]
operator|)
name|bmp
operator|.
name|get
argument_list|(
literal|"alias"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|aliases
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/bmp"
argument_list|,
name|aliases
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"org.apache.tika.parser.image.ImageParser"
argument_list|,
name|bmp
operator|.
name|get
argument_list|(
literal|"parser"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ogm
init|=
name|json
operator|.
name|get
argument_list|(
literal|"video/x-ogm"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"video/ogg"
argument_list|,
name|ogm
operator|.
name|get
argument_list|(
literal|"supertype"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"org.gagravarr.tika.OggParser"
argument_list|,
name|ogm
operator|.
name|get
argument_list|(
literal|"parser"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
