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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|EmbededResourceHandler
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
name|io
operator|.
name|TikaInputStream
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
comment|/**  * Tests that the various POI powered parsers are  *  able to extract their embeded contents.  */
end_comment

begin_class
specifier|public
class|class
name|POIContainerExtractionTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_DOC
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"msword"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_PPT
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_XLS
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_DOCX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_PPTX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_XLSX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_JPG
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"jpeg"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_PNG
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"png"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE_EMF
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-emf"
argument_list|)
decl_stmt|;
comment|/**      * For office files which don't have anything embeded in them      */
specifier|public
name|void
name|testWithoutEmbeded
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
comment|/**      * Office files with embeded images, but no other      *  office files in them      */
specifier|public
name|void
name|testEmbededImages
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
comment|/**      * Office files which have other office files      *  embeded into them. The embeded office files      *  will sometimes have images in them.      *        *  eg xls      *       -> word      *           -> image      *           -> image      *       -> powerpoint      *       -> excel      *           -> image      */
specifier|public
name|void
name|testEmbededOfficeFiles
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
comment|// Icon of embeded office doc
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
comment|// Icon of embeded office doc
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
comment|// Embeded image
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
comment|// Embeded office doc
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
comment|// Embeded office doc
comment|// With recursion, should get the images embeded in the office files too
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
literal|null
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
comment|// Icon of embeded office doc
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
comment|// Icon of embeded office doc
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
comment|// Embeded image
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
comment|// Embeded office doc
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
literal|8
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
literal|8
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
for|for
control|(
name|String
name|filename
range|:
name|handler
operator|.
name|filenames
control|)
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|filename
argument_list|)
expr_stmt|;
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
comment|// Icon of embeded office doc?
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
comment|// Icon of embeded office doc?
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
comment|// Embeded image
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
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded image
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
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded image
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
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded office doc
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
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded office doc
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
literal|7
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded office doc
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
comment|// TODO - Not all resources of embeded files are currently extracted
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
comment|// We don't know their filenames
for|for
control|(
name|String
name|filename
range|:
name|handler
operator|.
name|filenames
control|)
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|filename
argument_list|)
expr_stmt|;
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
comment|// Icon of embeded office doc?
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
comment|// Icon of embeded office doc?
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
comment|// Embeded image
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
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded image
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
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded image
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
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded office doc
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
literal|6
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embeded office doc
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
literal|7
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
literal|8
argument_list|)
argument_list|)
expr_stmt|;
comment|//    PNG inside .xls
comment|// PowerPoint with excel and word
comment|// TODO
comment|// Outlook with a text file and a word document
comment|// TODO
comment|// Outlook with a pdf and another outlook message
comment|// TODO
block|}
specifier|private
name|TrackingHandler
name|process
parameter_list|(
name|String
name|filename
parameter_list|,
name|ContainerExtractor
name|extractor
parameter_list|,
name|boolean
name|recurse
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|POIContainerExtractionTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filename
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|filename
operator|+
literal|" not found"
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|extractor
operator|.
name|isSupported
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
comment|// Process it
name|TrackingHandler
name|handler
init|=
operator|new
name|TrackingHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|recurse
condition|)
block|{
name|extractor
operator|.
name|extract
argument_list|(
name|stream
argument_list|,
name|extractor
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|extractor
operator|.
name|extract
argument_list|(
name|stream
argument_list|,
literal|null
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
comment|// So they can check what happened
return|return
name|handler
return|;
block|}
specifier|private
specifier|static
class|class
name|TrackingHandler
implements|implements
name|EmbededResourceHandler
block|{
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|filenames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|MediaType
argument_list|>
name|mediaTypes
init|=
operator|new
name|ArrayList
argument_list|<
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|handle
parameter_list|(
name|String
name|filename
parameter_list|,
name|MediaType
name|mediaType
parameter_list|,
name|InputStream
name|stream
parameter_list|)
block|{
name|filenames
operator|.
name|add
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|mediaTypes
operator|.
name|add
argument_list|(
name|mediaType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

