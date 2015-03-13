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
name|DetectorResourceTest
extends|extends
name|CXFTestBase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DETECT_PATH
init|=
literal|"/detect"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DETECT_STREAM_PATH
init|=
name|DETECT_PATH
operator|+
literal|"/stream"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FOO_CSV
init|=
literal|"foo.csv"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CDEC_CSV_NO_EXT
init|=
literal|"CDEC_WEATHER_2010_03_02"
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
name|DetectorResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setResourceProvider
argument_list|(
name|DetectorResource
operator|.
name|class
argument_list|,
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|DetectorResource
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
name|testDetectCsvWithExt
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|url
init|=
name|endPoint
operator|+
name|DETECT_STREAM_PATH
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
name|DETECT_STREAM_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"text/csv"
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
name|FOO_CSV
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|FOO_CSV
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|String
name|readMime
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
literal|"text/csv"
argument_list|,
name|readMime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDetectCsvNoExt
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|url
init|=
name|endPoint
operator|+
name|DETECT_STREAM_PATH
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
name|DETECT_STREAM_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"text/csv"
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
name|CDEC_CSV_NO_EXT
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|CDEC_CSV_NO_EXT
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|String
name|readMime
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
literal|"text/plain"
argument_list|,
name|readMime
argument_list|)
expr_stmt|;
comment|// now trick it by adding .csv to the end
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|DETECT_STREAM_PATH
argument_list|)
operator|.
name|type
argument_list|(
literal|"text/csv"
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
name|CDEC_CSV_NO_EXT
operator|+
literal|".csv"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|CDEC_CSV_NO_EXT
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|readMime
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
name|assertEquals
argument_list|(
literal|"text/csv"
argument_list|,
name|readMime
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

