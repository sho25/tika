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
name|image
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|WebPParserTest
extends|extends
name|TikaTest
block|{
comment|/*         Two photos in test-documents (testWebp_Alpha_Lossy.webp and testWebp_Alpha_Lossless.webp)         are in the public domain.  These files were retrieved from:         https://github.com/drewnoakes/metadata-extractor-images/tree/master/webp         These photos are also available here:         https://developers.google.com/speed/webp/gallery2#webp_links         Credits for the photo:         "Free Stock Photo in High Resolution - Yellow Rose 3 - Flowers"         Image Author: Jon Sullivan      */
annotation|@
name|Test
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|metadata
init|=
name|getXML
argument_list|(
literal|"testWebp_Alpha_Lossy.webp"
argument_list|)
operator|.
name|metadata
decl_stmt|;
name|assertEquals
argument_list|(
literal|"301"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Image Height"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"400"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Image Width"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Has Alpha"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Is Animation"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/webp"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|=
name|getXML
argument_list|(
literal|"testWebp_Alpha_Lossless.webp"
argument_list|)
operator|.
name|metadata
expr_stmt|;
comment|//unfortunately, there isn't much metadata in lossless
name|assertEquals
argument_list|(
literal|"image/webp"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

