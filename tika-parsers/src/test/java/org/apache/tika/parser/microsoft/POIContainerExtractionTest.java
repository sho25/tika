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
name|parser
operator|.
name|microsoft
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
name|extractor
operator|.
name|ContainerExtractor
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
name|extractor
operator|.
name|ParserContainerExtractor
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
name|mime
operator|.
name|MediaType
import|;
end_import

begin_comment
comment|/**  * Tests that the various POI powered parsers are  *  able to extract their embedded contents.  */
end_comment

begin_class
specifier|public
class|class
name|POIContainerExtractionTest
extends|extends
name|AbstractPOIContainerExtractionTest
block|{
comment|/**      * For office files which don't have anything embedded in them      */
specifier|public
name|void
name|testWithoutEmbedded
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerExtractor
name|extractor
init|=
operator|new
name|ParserContainerExtractor
argument_list|()
decl_stmt|;
name|String
index|[]
name|files
init|=
operator|new
name|String
index|[]
block|{
literal|"testEXCEL.xls"
block|,
literal|"testWORD.doc"
block|,
literal|"testPPT.ppt"
block|,
literal|"testVISIO.vsd"
block|,
literal|"test-outlook.msg"
block|}
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
comment|// Process it without recursing
name|TrackingHandler
name|handler
init|=
name|process
argument_list|(
name|file
argument_list|,
name|extractor
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Won't have fired
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ditto with recursing
name|handler
operator|=
name|process
argument_list|(
name|file
argument_list|,
name|extractor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Office files with embedded images, but no other      *  office files in them      */
specifier|public
name|void
name|testEmbeddedImages
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerExtractor
name|extractor
init|=
operator|new
name|ParserContainerExtractor
argument_list|()
decl_stmt|;
name|TrackingHandler
name|handler
decl_stmt|;
comment|// Excel with 1 image
name|handler
operator|=
name|process
argument_list|(
literal|"testEXCEL_1img.xls"
argument_list|,
name|extractor
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// PowerPoint with 2 images + sound
comment|// TODO
comment|// Word with 1 image
name|handler
operator|=
name|process
argument_list|(
literal|"testWORD_1img.doc"
argument_list|,
name|extractor
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image1.png"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Word with 3 images
name|handler
operator|=
name|process
argument_list|(
literal|"testWORD_3imgs.doc"
argument_list|,
name|extractor
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image1.png"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image2.jpg"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image3.png"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_JPG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Office files which have other office files      *  embedded into them. The embedded office files      *  will sometimes have images in them.      *        *  eg xls      *       -> word      *           -> image      *           -> image      *       -> powerpoint      *       -> excel      *           -> image      */
specifier|public
name|void
name|testEmbeddedOfficeFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerExtractor
name|extractor
init|=
operator|new
name|ParserContainerExtractor
argument_list|()
decl_stmt|;
name|TrackingHandler
name|handler
decl_stmt|;
comment|// Excel with a word doc and a powerpoint doc, both of which have images in them
comment|// Without recursion, should see both documents + the images
name|handler
operator|=
name|process
argument_list|(
literal|"testEXCEL_embeded.xls"
argument_list|,
name|extractor
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// We don't know their filenames
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// But we do know their types
name|assertEquals
argument_list|(
name|TYPE_EMF
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
name|assertEquals
argument_list|(
name|TYPE_EMF
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_PPT
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
name|assertEquals
argument_list|(
name|TYPE_DOC
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
comment|// With recursion, should get the images embedded in the office files too
name|handler
operator|=
name|process
argument_list|(
literal|"testEXCEL_embeded.xls"
argument_list|,
name|extractor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image1.png"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_EMF
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
name|assertEquals
argument_list|(
name|TYPE_EMF
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_PPT
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
name|assertEquals
argument_list|(
name|TYPE_DOC
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|//   PNG inside .doc
comment|// Word with .docx, powerpoint and excel
name|handler
operator|=
name|process
argument_list|(
literal|"testWORD_embeded.doc"
argument_list|,
name|extractor
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Filenames are a bit iffy...
comment|//       for(String filename : handler.filenames)
comment|//          assertEquals(null, filename);
comment|// But we do know their types
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"image/unknown"
argument_list|)
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc?
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"image/unknown"
argument_list|)
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc?
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"image/unknown"
argument_list|)
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc?
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_JPG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_DOCX
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
name|assertEquals
argument_list|(
name|TYPE_PPT
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
name|assertEquals
argument_list|(
name|TYPE_XLS
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
comment|// With recursion, should get their images too
name|handler
operator|=
name|process
argument_list|(
literal|"testWORD_embeded.doc"
argument_list|,
name|extractor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// We don't know their filenames, except for doc images + docx
name|assertEquals
argument_list|(
literal|"image1"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image2"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image3"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image4.png"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image5.jpg"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image6.png"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image2.png"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image3.jpeg"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image4.png"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertNull
argument_list|(
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// But we do know their types
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"image/unknown"
argument_list|)
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc?
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"image/unknown"
argument_list|)
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc?
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"image/unknown"
argument_list|)
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc?
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_JPG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_DOCX
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
comment|//    PNG inside .docx
name|assertEquals
argument_list|(
name|TYPE_JPG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
comment|//    JPG inside .docx
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
comment|//    PNG inside .docx
name|assertEquals
argument_list|(
name|TYPE_PPT
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
name|assertEquals
argument_list|(
name|TYPE_XLS
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|11
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
name|assertEquals
argument_list|(
name|TYPE_PNG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|12
argument_list|)
argument_list|)
expr_stmt|;
comment|//    PNG inside .xls
comment|// PowerPoint with excel and word
comment|// TODO
comment|// Outlook with a text file and a word document
name|handler
operator|=
name|process
argument_list|(
literal|"testMSG_att_doc.msg"
argument_list|,
name|extractor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-unicode.doc"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_DOC
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pj1.txt"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_TXT
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Outlook with a pdf and another outlook message
name|handler
operator|=
name|process
argument_list|(
literal|"testMSG_att_msg.msg"
argument_list|,
name|extractor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_MSG
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"smbprn.00009008.KdcPjl.pdf"
argument_list|,
name|handler
operator|.
name|filenames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_PDF
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

