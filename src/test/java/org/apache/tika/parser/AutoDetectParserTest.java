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
name|io
operator|.
name|StringWriter
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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

begin_class
specifier|public
class|class
name|AutoDetectParserTest
extends|extends
name|TestCase
block|{
specifier|private
name|void
name|assertAutoDetect
parameter_list|(
name|String
name|resource
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|AutoDetectParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
decl_stmt|;
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not open stream from specified resource: "
operator|+
name|resource
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|WriteOutContentHandler
argument_list|(
name|writer
argument_list|)
decl_stmt|;
operator|new
name|AutoDetectParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|type
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
name|assertTrue
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
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
name|testAutoDetect
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testEXCEL.xls"
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|,
literal|"Sample Excel Worksheet"
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testHTML.html"
argument_list|,
literal|"text/html"
argument_list|,
literal|"Test Indexation Html"
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testOpenOffice2.odt"
argument_list|,
literal|"application/vnd.oasis.opendocument.text"
argument_list|,
literal|"This is a sample Open Office document"
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testPDF.pdf"
argument_list|,
literal|"application/pdf"
argument_list|,
literal|"Content Analysis Toolkit"
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testPPT.ppt"
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|,
literal|"Sample Powerpoint Slide"
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testRTF.rtf"
argument_list|,
literal|"application/rtf"
argument_list|,
literal|"indexation Word"
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testTXT.txt"
argument_list|,
literal|"text/plain"
argument_list|,
literal|"indexation de Txt"
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testWORD.doc"
argument_list|,
literal|"application/msword"
argument_list|,
literal|"Sample Word Document"
argument_list|)
expr_stmt|;
name|assertAutoDetect
argument_list|(
literal|"/test-documents/testXML.xml"
argument_list|,
literal|"application/xml"
argument_list|,
literal|"Lius"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

