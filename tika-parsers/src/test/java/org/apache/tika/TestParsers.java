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
name|FileInputStream
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
name|net
operator|.
name|URL
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
name|config
operator|.
name|TikaConfig
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
name|utils
operator|.
name|ParseUtils
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Junit test class for Tika {@link Parser}s.  */
end_comment

begin_class
specifier|public
class|class
name|TestParsers
extends|extends
name|TestCase
block|{
specifier|private
name|TikaConfig
name|tc
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|tc
operator|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testPDFExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testPDF.pdf"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/pdf"
argument_list|)
decl_stmt|;
name|String
name|s3
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s3
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTXTExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testTXT.txt"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"text/plain"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRTFExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testRTF.rtf"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/rtf"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testXMLExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testXML.xml"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/xml"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPPTExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testPPT.ppt"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/vnd.ms-powerpoint"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
name|tc
operator|.
name|getParser
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
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
literal|"Sample Powerpoint Slide"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWORDxtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testWORD.doc"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/msword"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
name|tc
operator|.
name|getParser
argument_list|(
literal|"application/msword"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
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
literal|"Sample Word Document"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEXCELExtraction
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|expected
init|=
literal|"Numbers and their Squares"
decl_stmt|;
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testEXCEL.xls"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/vnd.ms-excel"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Text does not contain '"
operator|+
name|expected
operator|+
literal|"'"
argument_list|,
name|s1
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
name|tc
operator|.
name|getParser
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
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
literal|"Simple Excel document"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOOExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testOpenOffice2.odt"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/vnd.oasis.opendocument.text"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOutlookExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/test-outlook.msg"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/vnd.ms-outlook"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHTMLExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testHTML.html"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"text/html"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
name|tc
operator|.
name|getParser
argument_list|(
literal|"text/html"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testZipFileExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/test-documents.zip"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"application/zip"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
name|tc
operator|.
name|getParser
argument_list|(
literal|"application/zip"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMP3Extraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testMP3.mp3"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
name|ParseUtils
operator|.
name|getStringContent
argument_list|(
name|file
argument_list|,
name|tc
argument_list|,
literal|"audio/mpeg"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
name|tc
operator|.
name|getParser
argument_list|(
literal|"audio/mpeg"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
comment|/**      * This method will give you back the filename incl. the absolute path name      * to the resource. If the resource does not exist it will give you back the      * resource name incl. the path.      *       * It will give you back an absolute path incl. the name which is in the      * same directory as the the class you've called it from.      *       * @param name      * @return      */
specifier|public
name|String
name|getFileResource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|URL
name|url
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
return|return
name|url
operator|.
name|getFile
argument_list|()
return|;
block|}
else|else
block|{
comment|// We have a file which does not exists
comment|// We got the path
name|url
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
return|return
name|url
operator|.
name|getFile
argument_list|()
operator|+
name|name
return|;
block|}
block|}
specifier|public
name|File
name|getResourceAsFile
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|getFileResource
argument_list|(
name|filename
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|InputStream
name|getResourceAsStream
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

