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
name|microsoft
operator|.
name|ooxml
operator|.
name|xps
package|;
end_package

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
name|TikaCoreProperties
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
name|RecursiveParserWrapper
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
name|java
operator|.
name|util
operator|.
name|List
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
name|XPSParserTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testPPT.xps"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//metadata
name|assertEquals
argument_list|(
literal|"Rajiv"
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
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2010-06-29T12:06:31Z"
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
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2010-06-29T12:06:31Z"
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
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Attachment Test"
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
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"<p>Attachment Test</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<div class=\"canvas\"><p>Different"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//I'd want this to be "tika content", but copy+paste in Windows yields tikacontent
name|assertContains
argument_list|(
literal|"tikacontent"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/jpeg"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|testVarious
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testXPS_various.xps"
argument_list|)
decl_stmt|;
comment|//confirm embedded images and thumbnails were extracted
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//now check for content in the right order
name|String
name|quickBrownFox
init|=
literal|"\u0644\u062B\u0639\u0644\u0628\u0020"
operator|+
literal|"\u0627\u0644\u0628\u0646\u064A\u0020"
operator|+
literal|"\u0627\u0644\u0633\u0631\u064A\u0639"
decl_stmt|;
name|String
name|content
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
name|quickBrownFox
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"The \u0627\u0644\u0628\u0646\u064A fox"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\u0644\u062B\u0639\u0644\u0628 brown \u0627\u0644\u0633\u0631\u064A\u0639"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//make sure the urls come through
name|assertContains
argument_list|(
literal|"<a href=\"http://tika.apache.org/\">http://tika.apache.org/</a>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|Metadata
name|metadata
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Allison, Timothy B."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2017-12-12T11:15:38Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2017-12-12T11:15:38Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/png"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|Metadata
name|inlineJpeg
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"image/jpeg"
argument_list|,
name|inlineJpeg
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"INetCache"
argument_list|,
name|inlineJpeg
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TikaCoreProperties
operator|.
name|EmbeddedResourceType
operator|.
name|INLINE
operator|.
name|toString
argument_list|()
argument_list|,
name|inlineJpeg
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|EMBEDDED_RESOURCE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/jpeg"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
comment|//        assertEquals(TikaCoreProperties.EmbeddedResourceType.THUMBNAIL.toString(),
comment|//              inlineJpeg.get(TikaCoreProperties.EMBEDDED_RESOURCE_TYPE));
block|}
block|}
end_class

end_unit

