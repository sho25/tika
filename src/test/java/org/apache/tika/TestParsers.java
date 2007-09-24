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
name|IOException
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
name|Content
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
name|LiusConfig
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
name|log
operator|.
name|LiusLogger
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
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|JDOMException
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

begin_comment
comment|/**  * Junit test class     * @author Rida Benjelloun (ridabenjelloun@apache.org)    */
end_comment

begin_class
specifier|public
class|class
name|TestParsers
extends|extends
name|TestCase
block|{
specifier|private
name|LiusConfig
name|tc
decl_stmt|;
specifier|private
name|File
name|testFilesBaseDir
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|JDOMException
throws|,
name|IOException
block|{
comment|/* FIXME the old mechanism does not work anymore when running the tests          * with Maven - need a resource-based one, but this means more          * changes to classes which rely on filenames.          *           String sep = File.separator;         StringTokenizer st = new StringTokenizer(System.getProperty(                 "java.class.path"), File.pathSeparator);          classDir = new File(st.nextToken());          config = classDir.getParent() + sep + "config" + sep + "config.xml";          String log4j = classDir.getParent() + sep + "Config" + sep + "log4j"                 + sep + "log4j.properties";          */
comment|// FIXME for now, fix filenames according to Maven testing layout
specifier|final
name|String
name|liusConfigFilename
init|=
literal|"target/classes/config.xml"
decl_stmt|;
specifier|final
name|String
name|log4jPropertiesFilename
init|=
literal|"target/classes/log4j/log4j.properties"
decl_stmt|;
name|testFilesBaseDir
operator|=
operator|new
name|File
argument_list|(
literal|"src/test/resources/test-documents"
argument_list|)
expr_stmt|;
name|tc
operator|=
name|LiusConfig
operator|.
name|getInstance
argument_list|(
name|liusConfigFilename
argument_list|)
expr_stmt|;
name|LiusLogger
operator|.
name|setLoggerConfigFile
argument_list|(
name|log4jPropertiesFilename
argument_list|)
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
name|getTestFile
argument_list|(
literal|"testPDF.pdf"
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
name|Parser
name|parser
init|=
name|ParseUtils
operator|.
name|getParser
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|String
name|s3
init|=
name|parser
operator|.
name|getStrContent
argument_list|()
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
name|getTestFile
argument_list|(
literal|"testTXT.txt"
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
name|getTestFile
argument_list|(
literal|"testRTF.rtf"
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
name|getTestFile
argument_list|(
literal|"testXML.xml"
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
name|getTestFile
argument_list|(
literal|"testPPT.ppt"
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
name|getTestFile
argument_list|(
literal|"testWORD.doc"
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
block|}
specifier|public
name|void
name|testEXCELExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testEXCEL.xls"
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
name|getTestFile
argument_list|(
literal|"testOpenOffice2.odt"
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
name|testHTMLExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getTestFile
argument_list|(
literal|"testHTML.html"
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
name|ParseUtils
operator|.
name|getParser
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"org.apache.tika.parser.html.HtmlParser"
argument_list|,
name|parser
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Content
name|content
init|=
name|parser
operator|.
name|getContent
argument_list|(
literal|"title"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Title : Test Indexation Html"
argument_list|,
name|content
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html"
argument_list|,
name|parser
operator|.
name|getMimeType
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|text
init|=
name|Utils
operator|.
name|toString
argument_list|(
name|parser
operator|.
name|getContents
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"Test Indexation Html"
decl_stmt|;
name|assertTrue
argument_list|(
literal|"text contains '"
operator|+
name|expected
operator|+
literal|"'"
argument_list|,
name|text
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|parser
operator|.
name|getInputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|File
name|getTestFile
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|testFilesBaseDir
argument_list|,
name|filename
argument_list|)
return|;
block|}
block|}
end_class

end_unit

