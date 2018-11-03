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
name|parser
operator|.
name|envi
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|TikaTest
operator|.
name|assertContains
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
name|InputStream
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
name|parser
operator|.
name|ParseContext
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
name|parser
operator|.
name|Parser
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
name|sax
operator|.
name|ToXMLContentHandler
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

begin_comment
comment|/**  * Test cases to exercise the {@link EnviHeaderParser}.  */
end_comment

begin_class
specifier|public
class|class
name|EnviHeaderParserTest
block|{
specifier|private
name|Parser
name|parser
decl_stmt|;
specifier|private
name|ToXMLContentHandler
name|handler
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|setParser
argument_list|(
operator|new
name|EnviHeaderParser
argument_list|()
argument_list|)
expr_stmt|;
name|setHandler
argument_list|(
operator|new
name|ToXMLContentHandler
argument_list|()
argument_list|)
expr_stmt|;
name|setMetadata
argument_list|(
operator|new
name|Metadata
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|setParser
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|setHandler
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|setMetadata
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParseGlobalMetadata
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|stream
init|=
name|EnviHeaderParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/envi_test_header.hdr"
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
literal|"Test ENVI file 'envi_test_header.hdr' not found"
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check content of test file
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"<body><p>ENVI</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>samples = 2400</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>lines   = 2400</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>map info = {Sinusoidal, 1.5000, 1.5000, -10007091.3643, 5559289.2856, 4.6331271653e+02, 4.6331271653e+02, , units=Meters}</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"content=\"application/envi.hdr\""
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"projection info = {16, 6371007.2, 0.000000, 0.0, 0.0, Sinusoidal, units=Meters}"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParseGlobalMetadataToConvertMapInfo
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|stream
init|=
name|EnviHeaderParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/envi_test_header.hdr"
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
literal|"Test ENVI file 'envi_test_header.hdr' not found"
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check content of test file
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"<body><p>ENVI</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>samples = 2400</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>lines   = 2400</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>map info = {Sinusoidal, 1.5000, 1.5000, -10007091.3643, 5559289.2856, 4.6331271653e+02, 4.6331271653e+02, , units=Meters}</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"content=\"application/envi.hdr\""
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"projection info = {16, 6371007.2, 0.000000, 0.0, 0.0, Sinusoidal, units=Meters}"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParseGlobalMetadataMultiLineMetadataValues
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|EnviHeaderParser
argument_list|()
decl_stmt|;
name|ToXMLContentHandler
name|handler
init|=
operator|new
name|ToXMLContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|EnviHeaderParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/ang20150420t182050_corr_v1e_img.hdr"
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
literal|"Test ENVI file 'ang20150420t182050_corr_v1e_img.hdr' not found"
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check content of test file
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"<body><p>ENVI</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>description = {  Georeferenced Image built from input GLT. [Wed Jun 10 04:37:54 2015] [Wed  Jun 10 04:48:52 2015]}</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>samples = 739</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>lat/lon = { 36.79077627261556, -108.48370867914815 }</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>map info = { UTM , 1.000 , 1.000 , 724522.127 , 4074620.759 , 1.1000000000e+00 , 1.1000000000e+00 , 12 , North , WGS-84 , units=Meters , rotation=75.00000000 }</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"correction factors = { 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.880586 , 1.741631 , 1.524632 , 1.564904 , 1.390035 , 1.342501 , 1.264768 , 1.200357 , 1.131471 , 1.06512 , 1.105211 , 1.081234 , 1.064999 , 1.056798 , 1.042573 , 1.009621 , 1.009031 , 1.002107 , 0.997332 , 0.976807 , 0.963755 , 0.969028 , 0.962823 , 0.949522 , 0.960568 , 0.957813 , 0.934766 , 0.944994 , 0.937726 , 0.935257 , 0.932706 , 0.932568 , 0.933217 , 0.928705 , 0.929294 , 0.936669 , 0.935498 , 0.94823 , 0.949846 , 0.945885 , 0.935468 , 0.930084 , 0.934473 , 0.935378 , 0.939193 , 0.935081 , 0.937398 , 0.943396 , 0.947133 , 0.950645 , 0.945531 , 0.940295 , 0.933129 , 0.930664 , 0.92736 , 0.931786 , 0.928217 , 0.928205 , 0.926481 , 0.928583 , 0.930504 , 0.93648 , 0.930731 , 0.931265 , 0.935063 , 0.93434 , 0.926983 , 0.932689 , 0.936477 , 0.939647 , 0.940155 , 0.937519 , 0.939448 , 0.942124 , 0.93653 , 0.9435 , 0.959204 , 0.942566 , 0.940873 , 0.939414 , 0.939822 , 0.940174 , 0.941372 , 0.939347 , 0.942108 , 0.942664 , 0.934811 , 0.934567 , 0.937712 , 0.940611 , 0.944809 , 0.939877 , 0.943376 , 0.939189 , 0.943619 , 0.946268 , 0.940166 , 0.953752 , 0.958975 , 0.954512 , 0.954103 , 0.958978 , 0.953247 , 0.952199 , 0.956082 , 0.957846 , 0.970078 , 0.973704 , 0.980014 , 0.928845 , 0.922973 , 0.954414 , 0.95521 , 0.961276 , 0.964513 , 0.965296 , 0.964644 , 0.954999 , 0.951133 , 0.956216 , 0.951977 , 0.948547 , 0.949499 , 0.952685 , 0.950158 , 0.944263 , 0.936946 , 0.938394 , 0.941325 , 0.94116 , 0.941397 , 0.940811 , 0.942695 , 0.945228 , 0.953929 , 0.962457 , 0.968728 , 0.963947 , 0.961222 , 0.963003 , 0.967658 , 0.969773 , 0.970294 , 0.963456 , 0.970497 , 0.976972 , 0.961611 , 0.953081 , 0.945668 , 0.993867 , 1.019915 , 0.997013 , 0.977643 , 0.998022 , 1.007041 , 1.003881 , 0.991335 , 0.976202 , 0.967636 , 0.969294 , 0.965331 , 0.968705 , 0.965705 , 0.973601 , 0.97282 , 0.970848 , 0.970687 , 0.969394 , 0.972263 , 0.969286 , 0.970327 , 0.97754 , 0.984703 , 0.993916 , 1.02186 , 1.054704 , 1.061183 , 1.036962 , 1.012519 , 0.991209 , 0.975974 , 0.965446 , 0.958801 , 0.951519 , 0.960628 , 0.957276 , 0.96061 , 0.95689 , 0.956666 , 0.965567 , 0.982251 , 1.038526 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.131643 , 1.150333 , 1.112466 , 1.06255 , 1.033182 , 1.03018 , 1.006512 , 0.993285 , 1.003557 , 1.014441 , 0.998586 , 0.994426 , 0.987975 , 0.984681 , 0.981863 , 0.964106 , 0.956432 , 0.954467 , 0.956429 , 0.950668 , 0.947217 , 0.944635 , 0.942942 , 0.941078 , 0.943502 , 0.950408 , 0.957625 , 0.965073 , 0.976012 , 0.976683 , 0.975625 , 0.968011 , 0.968843 , 0.970632 , 0.960977 , 0.960505 , 0.955015 , 0.953597 , 0.951119 , 0.945679 , 0.949988 , 0.951236 , 0.947813 , 0.948004 , 0.950015 , 0.939258 , 0.945863 , 0.953927 , 0.953145 , 0.945291 , 0.942319 , 0.947022 , 0.948264 , 0.947112 , 0.942092 , 0.943128 , 0.948068 , 0.944432 , 0.950396 , 0.964006 , 0.961019 , 0.951786 , 0.957457 , 0.950327 , 0.954375 , 0.9608 , 0.965864 , 0.982396 , 1.011334 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.289798 , 1.126375 , 1.074621 , 1.069365 , 1.051364 , 1.009432 , 0.984578 , 0.991187 , 1.016306 , 1.083681 , 1.076539 , 1.104657 , 1.114005 , 1.081102 , 1.039196 , 1.009524 , 0.966515 , 0.953688 , 0.966274 , 0.973232 , 0.966402 , 0.946801 , 0.951952 , 0.965679 , 0.96977 , 0.946541 , 0.941678 , 0.937528 , 0.922351 , 0.914192 , 0.92879 , 0.932284 , 0.933182 , 0.922322 , 0.91851 , 0.919591 , 0.925027 , 0.924611 , 0.932288 , 0.933352 , 0.930517 , 0.931666 , 0.931763 , 0.932655 , 0.928945 , 0.933308 , 0.932392 , 0.932943 , 0.935328 , 0.947019 , 0.954093 , 0.95156 , 0.939591 , 0.942808 , 0.944862 , 0.944004 , 0.949161 , 0.950992 , 0.956738 , 0.951184 , 0.953545 , 0.958836 , 0.966134 , 0.956752 , 0.951961 , 0.958667 , 0.9579 , 0.968531 , 0.973792 , 0.969238 , 0.970838 , 0.954552 , 0.968166 , 0.989176 , 0.974784 , 0.970674 , 0.9733 , 0.990576 , 1.0062 , 1.010295 , 0.99378 , 0.986109 , 1.007054 , 1.005377 , 1.010013 , 1.014671 , 1.021618 , 1.021229 , 1.021003 , 1.020866 , 1.029358 , 1.042136 , 1.030482 , 1.019556 , 1.036656 , 1.05348 , 1.015947 , 1.07263 , 1.092879 , 1.053624 , 1.086491 , 1.139334 , 1.163645 , 1.162487 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 }"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the parser      */
specifier|public
name|Parser
name|getParser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
comment|/**      * @param parser the parser to set      */
specifier|public
name|void
name|setParser
parameter_list|(
name|Parser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
comment|/**      * @return the handler      */
specifier|public
name|ToXMLContentHandler
name|getHandler
parameter_list|()
block|{
return|return
name|handler
return|;
block|}
comment|/**      * @param handler the handler to set      */
specifier|public
name|void
name|setHandler
parameter_list|(
name|ToXMLContentHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
comment|/**      * @return the metadata      */
specifier|public
name|Metadata
name|getMetadata
parameter_list|()
block|{
return|return
name|metadata
return|;
block|}
comment|/**      * @param metadata the metadata to set      */
specifier|public
name|void
name|setMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
block|}
end_class

end_unit

