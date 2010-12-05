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
name|detect
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|POIFSFileSystem
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
name|mime
operator|.
name|MediaType
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

begin_comment
comment|/**  * Junit test class for {@link ContainerAwareDetector}  */
end_comment

begin_class
specifier|public
class|class
name|TestContainerAwareDetector
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
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
specifier|private
name|void
name|assertDetect
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|Exception
block|{
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|TestContainerAwareDetector
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/test-documents/"
operator|+
name|file
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
name|type
argument_list|)
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testDetectOLE2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Microsoft office types known by POI
name|assertDetect
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testWORD.doc"
argument_list|,
literal|"application/msword"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testPPT.ppt"
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
expr_stmt|;
comment|// Try some ones that POI doesn't handle, that are still OLE2 based
name|assertDetect
argument_list|(
literal|"testWORKS.wps"
argument_list|,
literal|"application/vnd.ms-works"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testCOREL.shw"
argument_list|,
literal|"application/x-corelpresentations"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testQUATTRO.qpw"
argument_list|,
literal|"application/x-quattro-pro"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testQUATTRO.wb3"
argument_list|,
literal|"application/x-quattro-pro"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOpenContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|TestContainerAwareDetector
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/test-documents/testPPT.ppt"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|assertNull
argument_list|(
name|stream
operator|.
name|getOpenContainer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|)
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stream
operator|.
name|getOpenContainer
argument_list|()
operator|instanceof
name|POIFSFileSystem
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testDetectODF
parameter_list|()
throws|throws
name|Exception
block|{
name|assertDetect
argument_list|(
literal|"testODFwithOOo3.odt"
argument_list|,
literal|"application/vnd.oasis.opendocument.text"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testOpenOffice2.odf"
argument_list|,
literal|"application/vnd.oasis.opendocument.formula"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDetectOOXML
parameter_list|()
throws|throws
name|Exception
block|{
name|assertDetect
argument_list|(
literal|"testEXCEL.xlsx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testWORD.docx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testPPT.pptx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
expr_stmt|;
comment|// Check some of the less common OOXML types
name|assertDetect
argument_list|(
literal|"testPPT.pptm"
argument_list|,
literal|"application/vnd.ms-powerpoint.presentation.macroenabled.12"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testPPT.ppsx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.presentationml.slideshow"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testPPT.ppsm"
argument_list|,
literal|"application/vnd.ms-powerpoint.slideshow.macroEnabled.12"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDetectIWork
parameter_list|()
throws|throws
name|Exception
block|{
name|assertDetect
argument_list|(
literal|"testKeynote.key"
argument_list|,
literal|"application/vnd.apple.keynote"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testNumbers.numbers"
argument_list|,
literal|"application/vnd.apple.numbers"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testPages.pages"
argument_list|,
literal|"application/vnd.apple.pages"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDetectZip
parameter_list|()
throws|throws
name|Exception
block|{
name|assertDetect
argument_list|(
literal|"test-documents.zip"
argument_list|,
literal|"application/zip"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"test-zip-of-zip.zip"
argument_list|,
literal|"application/zip"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
literal|"testJAR.jar"
argument_list|,
literal|"application/java-archive"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|TikaInputStream
name|getTruncatedFile
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|input
init|=
name|TestContainerAwareDetector
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|n
index|]
decl_stmt|;
name|int
name|m
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|m
operator|<
name|bytes
operator|.
name|length
condition|)
block|{
name|int
name|i
init|=
name|input
operator|.
name|read
argument_list|(
name|bytes
argument_list|,
name|m
argument_list|,
name|bytes
operator|.
name|length
operator|-
name|m
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
operator|-
literal|1
condition|)
block|{
name|m
operator|+=
name|i
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected end of stream"
argument_list|)
throw|;
block|}
block|}
return|return
name|TikaInputStream
operator|.
name|get
argument_list|(
name|bytes
argument_list|)
return|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testTruncatedFiles
parameter_list|()
throws|throws
name|Exception
block|{
comment|// First up a truncated OOXML (zip) file
name|TikaInputStream
name|xlsx
init|=
name|getTruncatedFile
argument_list|(
literal|"testEXCEL.xlsx"
argument_list|,
literal|300
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_ZIP
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|xlsx
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|xlsx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Now a truncated OLE2 file
name|TikaInputStream
name|xls
init|=
name|getTruncatedFile
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
literal|400
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-msoffice"
argument_list|)
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|xls
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|xls
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

