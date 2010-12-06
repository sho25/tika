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
name|detect
operator|.
name|ContainerAwareDetector
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
name|MimeTypes
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
name|AutoDetectParser
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
name|microsoft
operator|.
name|AbstractPOIContainerExtractionTest
import|;
end_import

begin_comment
comment|/**  * Tests that the various POI OOXML powered parsers are  *  able to extract their embedded contents.  */
end_comment

begin_class
specifier|public
class|class
name|OOXMLContainerExtractionTest
extends|extends
name|AbstractPOIContainerExtractionTest
block|{
specifier|private
name|ContainerExtractor
name|extractor
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerAwareDetector
name|detector
init|=
operator|new
name|ContainerAwareDetector
argument_list|(
name|MimeTypes
operator|.
name|getDefaultMimeTypes
argument_list|()
argument_list|)
decl_stmt|;
name|extractor
operator|=
operator|new
name|ParserContainerExtractor
argument_list|(
operator|new
name|AutoDetectParser
argument_list|(
name|detector
argument_list|)
argument_list|,
name|detector
argument_list|)
expr_stmt|;
block|}
comment|/**      * For office files which don't have anything embedded in them      */
specifier|public
name|void
name|testWithoutEmbedded
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|files
init|=
operator|new
name|String
index|[]
block|{
literal|"testEXCEL.xlsx"
block|,
literal|"testWORD.docx"
block|,
literal|"testPPT.pptx"
block|,        }
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
name|TrackingHandler
name|handler
decl_stmt|;
comment|// Excel with 1 image
name|handler
operator|=
name|process
argument_list|(
literal|"testEXCEL_1img.xlsx"
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
comment|// PowerPoint with 2 images + sound
comment|// TODO Figure out why we can't find the sound anywhere...
name|handler
operator|=
name|process
argument_list|(
literal|"testPPT_2imgs.pptx"
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
literal|"image2.gif"
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
name|TYPE_GIF
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
comment|// icon of sound
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
comment|// Word with 1 image
name|handler
operator|=
name|process
argument_list|(
literal|"testWORD_1img.docx"
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
literal|"testWORD_3imgs.docx"
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
literal|"image2.png"
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
literal|"image3.jpeg"
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
literal|"image4.png"
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
name|TrackingHandler
name|handler
decl_stmt|;
comment|// Excel with a word doc and a powerpoint doc, both of which have images in them
comment|// Without recursion, should see both documents + the images
name|handler
operator|=
name|process
argument_list|(
literal|"testEXCEL_embeded.xlsx"
argument_list|,
name|extractor
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
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
literal|7
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// We know the rough filenames
name|assertEquals
argument_list|(
literal|"Microsoft_Office_PowerPoint_Presentation1.pptx"
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
literal|"Microsoft_Office_Word_97_-_2003_Document1.doc"
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
literal|"Microsoft_Office_Word_Document2.docx"
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
literal|"image1.png"
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
literal|"image2.emf"
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
literal|"image3.emf"
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
literal|"image4.emf"
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
comment|// But we do know their types
name|assertEquals
argument_list|(
name|TYPE_PPTX
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
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded office doc
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
literal|2
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
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
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
literal|4
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
literal|5
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
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
comment|// With recursion, should get the images embedded in the office files too
name|handler
operator|=
name|process
argument_list|(
literal|"testEXCEL_embeded.xlsx"
argument_list|,
name|extractor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|23
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
literal|23
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
name|TYPE_PPTX
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
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//   PNG inside .pptx
name|assertEquals
argument_list|(
name|TYPE_GIF
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
comment|//   PNG inside .pptx
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
comment|//   PNG inside .pptx
name|assertEquals
argument_list|(
name|TYPE_XLSX
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
comment|//   .xlsx inside .pptx
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
comment|//     PNG inside .xlsx inside .pptx
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
comment|//   .docx inside .pptx
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
comment|//     PNG inside .docx inside .pptx
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
comment|//     JPG inside .docx inside .pptx
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
comment|//     PNG inside .docx inside .pptx
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
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|//   .doc inside .pptx
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
literal|11
argument_list|)
argument_list|)
expr_stmt|;
comment|//    PNG inside .doc inside .pptx
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
literal|12
argument_list|)
argument_list|)
expr_stmt|;
comment|//   Icon of item inside .pptx
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
literal|13
argument_list|)
argument_list|)
expr_stmt|;
comment|//   Icon of item inside .pptx
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
literal|14
argument_list|)
argument_list|)
expr_stmt|;
comment|//   Icon of item inside .pptx
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
literal|15
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
literal|16
argument_list|)
argument_list|)
expr_stmt|;
comment|//   PNG inside .doc
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
literal|17
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
literal|18
argument_list|)
argument_list|)
expr_stmt|;
comment|//   PNG inside .docx
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
literal|19
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
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
literal|20
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
literal|21
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
literal|22
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
comment|// Word with .docx, powerpoint and excel
name|handler
operator|=
name|process
argument_list|(
literal|"testWORD_embeded.docx"
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
comment|// We know their rough filenames
name|assertEquals
argument_list|(
literal|"Microsoft_Office_PowerPoint_Presentation2.pptx"
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
literal|"image6.emf"
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
literal|"Microsoft_Office_Word_97_-_2003_Document1.doc"
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
literal|"image1.png"
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
literal|"image2.jpeg"
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
literal|"image3.png"
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
literal|"image4.emf"
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
literal|"Microsoft_Office_Excel_Worksheet1.xlsx"
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
literal|"image5.emf"
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
comment|// But we do know their types
name|assertEquals
argument_list|(
name|TYPE_PPTX
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
comment|// Embedded office doc
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
name|TYPE_DOC
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
name|TYPE_EMF
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
comment|// Icon of embedded office doc
name|assertEquals
argument_list|(
name|TYPE_XLSX
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
comment|// Embeded office doc
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
literal|8
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
comment|// With recursion, should get their images too
name|handler
operator|=
name|process
argument_list|(
literal|"testWORD_embeded.docx"
argument_list|,
name|extractor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|14
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
literal|14
argument_list|,
name|handler
operator|.
name|mediaTypes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// But we do know their types
name|assertEquals
argument_list|(
name|TYPE_PPTX
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
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//   PNG inside .pptx
name|assertEquals
argument_list|(
name|TYPE_GIF
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
comment|//   GIF inside .pptx
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
comment|//   PNG inside .pptx
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
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
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
literal|5
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
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|//   PNG inside .doc
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
literal|8
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
literal|9
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
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
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
name|assertEquals
argument_list|(
name|TYPE_XLSX
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
comment|// Embeded office doc
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
comment|//   PNG inside .xlsx
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
literal|13
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
comment|// PowerPoint with excel and word
name|handler
operator|=
name|process
argument_list|(
literal|"testPPT_embeded.pptx"
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
comment|// We don't know their exact filenames
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
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image5.gif"
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
literal|"image6.png"
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
literal|"Microsoft_Office_Excel_Worksheet1.xlsx"
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
literal|"Microsoft_Office_Word_Document2.docx"
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
literal|"Microsoft_Office_Word_97_-_2003_Document1.doc"
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
literal|"image1.emf"
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
literal|"image2.emf"
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
literal|"image3.emf"
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
comment|// But we do know their types
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
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_GIF
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
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded image
name|assertEquals
argument_list|(
name|TYPE_XLSX
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
name|TYPE_DOCX
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
name|TYPE_DOC
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
comment|// Embedded office doc
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
literal|6
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
literal|7
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
literal|8
argument_list|)
argument_list|)
expr_stmt|;
comment|// Icon of embedded office doc
block|}
block|}
end_class

end_unit

