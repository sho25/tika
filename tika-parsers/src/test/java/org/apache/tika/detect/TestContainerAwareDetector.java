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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
import|;
end_import

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
name|NPOIFSFileSystem
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
name|Detector
name|detector
init|=
operator|new
name|DefaultDetector
argument_list|()
decl_stmt|;
specifier|private
name|void
name|assertTypeByData
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
name|assertTypeByNameAndData
argument_list|(
name|file
argument_list|,
literal|null
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertTypeByNameAndData
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
name|assertTypeByNameAndData
argument_list|(
name|file
argument_list|,
name|file
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertTypeByNameAndData
parameter_list|(
name|String
name|dataFile
parameter_list|,
name|String
name|name
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
name|dataFile
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|m
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|name
argument_list|)
expr_stmt|;
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
name|m
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
name|assertTypeByData
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testWORD.doc"
argument_list|,
literal|"application/msword"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPPT.ppt"
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"test-outlook.msg"
argument_list|,
literal|"application/vnd.ms-outlook"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"test-outlook2003.msg"
argument_list|,
literal|"application/vnd.ms-outlook"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testVISIO.vsd"
argument_list|,
literal|"application/vnd.visio"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPUBLISHER.pub"
argument_list|,
literal|"application/x-mspublisher"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testWORKS.wps"
argument_list|,
literal|"application/vnd.ms-works"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testWORKS2000.wps"
argument_list|,
literal|"application/vnd.ms-works"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testWORKSSpreadsheet7.0.xlr"
argument_list|,
literal|"application/x-tika-msworks-spreadsheet"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPROJECT2003.mpp"
argument_list|,
literal|"application/vnd.ms-project"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPROJECT2007.mpp"
argument_list|,
literal|"application/vnd.ms-project"
argument_list|)
expr_stmt|;
comment|// Try some ones that POI doesn't handle, that are still OLE2 based
name|assertTypeByData
argument_list|(
literal|"testCOREL.shw"
argument_list|,
literal|"application/x-corelpresentations"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testQUATTRO.qpw"
argument_list|,
literal|"application/x-quattro-pro"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testQUATTRO.wb3"
argument_list|,
literal|"application/x-quattro-pro"
argument_list|)
expr_stmt|;
comment|// With the filename and data
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testWORD.doc"
argument_list|,
literal|"application/msword"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testPPT.ppt"
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
expr_stmt|;
comment|// With the wrong filename supplied, data will trump filename
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
literal|"notWord.doc"
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testWORD.doc"
argument_list|,
literal|"notExcel.xls"
argument_list|,
literal|"application/msword"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testPPT.ppt"
argument_list|,
literal|"notWord.doc"
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
expr_stmt|;
comment|// With a filename of a totally different type, data will trump filename
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
literal|"notPDF.pdf"
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
literal|"notPNG.png"
argument_list|,
literal|"application/vnd.ms-excel"
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
name|NPOIFSFileSystem
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
name|assertTypeByData
argument_list|(
literal|"testODFwithOOo3.odt"
argument_list|,
literal|"application/vnd.oasis.opendocument.text"
argument_list|)
expr_stmt|;
name|assertTypeByData
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
name|assertTypeByData
argument_list|(
literal|"testEXCEL.xlsx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testWORD.docx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPPT.pptx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
expr_stmt|;
comment|// Check some of the less common OOXML types
name|assertTypeByData
argument_list|(
literal|"testPPT.pptm"
argument_list|,
literal|"application/vnd.ms-powerpoint.presentation.macroenabled.12"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPPT.ppsx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.presentationml.slideshow"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPPT.ppsm"
argument_list|,
literal|"application/vnd.ms-powerpoint.slideshow.macroEnabled.12"
argument_list|)
expr_stmt|;
comment|// .xlsb is an OOXML file containing the binary parts, and not
comment|//  an OLE2 file as you might initially expect!
name|assertTypeByData
argument_list|(
literal|"testEXCEL.xlsb"
argument_list|,
literal|"application/vnd.ms-excel.sheet.binary.macroEnabled.12"
argument_list|)
expr_stmt|;
comment|// With the filename and data
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL.xlsx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testWORD.docx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testPPT.pptx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
expr_stmt|;
comment|// With the wrong filename supplied, data will trump filename
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL.xlsx"
argument_list|,
literal|"notWord.docx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testWORD.docx"
argument_list|,
literal|"notExcel.xlsx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testPPT.pptx"
argument_list|,
literal|"notWord.docx"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
expr_stmt|;
comment|// With an incorrect filename of a different container type, data trumps filename
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL.xlsx"
argument_list|,
literal|"notOldExcel.xls"
argument_list|,
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Password Protected OLE2 files are fairly straightforward to detect, as they      *  have the same structure as regular OLE2 files. (Core streams may be encrypted      *  however)      */
specifier|public
name|void
name|testDetectProtectedOLE2
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTypeByData
argument_list|(
literal|"testEXCEL_protected_passtika.xls"
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testWORD_protected_passtika.doc"
argument_list|,
literal|"application/msword"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPPT_protected_passtika.ppt"
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL_protected_passtika.xls"
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testWORD_protected_passtika.doc"
argument_list|,
literal|"application/msword"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testPPT_protected_passtika.ppt"
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Password Protected OOXML files are much more tricky beasts to work with.      * They have a very different structure to regular OOXML files, and instead      *  of being ZIP based they are actually an OLE2 file which contains the      *  OOXML structure within an encrypted stream.      * This makes detecting them much harder...      */
specifier|public
name|void
name|testDetectProtectedOOXML
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Encrypted Microsoft Office OOXML files have OLE magic but
comment|//  special streams, so we can tell they're Protected OOXML
name|assertTypeByData
argument_list|(
literal|"testEXCEL_protected_passtika.xlsx"
argument_list|,
literal|"application/x-tika-ooxml-protected"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testWORD_protected_passtika.docx"
argument_list|,
literal|"application/x-tika-ooxml-protected"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testPPT_protected_passtika.pptx"
argument_list|,
literal|"application/x-tika-ooxml-protected"
argument_list|)
expr_stmt|;
comment|// At the moment, we can't use the name to specialise
comment|// See discussions on TIKA-790 for details
name|assertTypeByNameAndData
argument_list|(
literal|"testEXCEL_protected_passtika.xlsx"
argument_list|,
literal|"application/x-tika-ooxml-protected"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testWORD_protected_passtika.docx"
argument_list|,
literal|"application/x-tika-ooxml-protected"
argument_list|)
expr_stmt|;
name|assertTypeByNameAndData
argument_list|(
literal|"testPPT_protected_passtika.pptx"
argument_list|,
literal|"application/x-tika-ooxml-protected"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Check that temporary files created by Tika are removed after      * closing TikaInputStream.      */
specifier|public
name|void
name|testRemovalTempfiles
parameter_list|()
throws|throws
name|Exception
block|{
name|assertRemovalTempfiles
argument_list|(
literal|"testWORD.docx"
argument_list|)
expr_stmt|;
name|assertRemovalTempfiles
argument_list|(
literal|"test-documents.zip"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|countTemporaryFiles
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"apache-tika-"
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|length
return|;
block|}
specifier|private
name|void
name|assertRemovalTempfiles
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numberOfTempFiles
init|=
name|countTemporaryFiles
argument_list|()
decl_stmt|;
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
name|fileName
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
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
name|assertEquals
argument_list|(
name|numberOfTempFiles
argument_list|,
name|countTemporaryFiles
argument_list|()
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
name|assertTypeByData
argument_list|(
literal|"testKeynote.key"
argument_list|,
literal|"application/vnd.apple.keynote"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"testNumbers.numbers"
argument_list|,
literal|"application/vnd.apple.numbers"
argument_list|)
expr_stmt|;
name|assertTypeByData
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
name|assertTypeByData
argument_list|(
literal|"test-documents.zip"
argument_list|,
literal|"application/zip"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"test-zip-of-zip.zip"
argument_list|,
literal|"application/zip"
argument_list|)
expr_stmt|;
name|assertTypeByData
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
comment|// With only the data supplied, the best we can do is the container
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
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-ooxml"
argument_list|)
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|xlsx
argument_list|,
name|m
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
comment|// With truncated data + filename, we can use the filename to specialise
name|xlsx
operator|=
name|getTruncatedFile
argument_list|(
literal|"testEXCEL.xlsx"
argument_list|,
literal|300
argument_list|)
expr_stmt|;
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|m
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"testEXCEL.xlsx"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|xlsx
argument_list|,
name|m
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
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
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
name|m
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
comment|// Finally a truncated OLE2 file, with a filename available
name|xls
operator|=
name|getTruncatedFile
argument_list|(
literal|"testEXCEL.xls"
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|m
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"testEXCEL.xls"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel"
argument_list|)
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|xls
argument_list|,
name|m
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

