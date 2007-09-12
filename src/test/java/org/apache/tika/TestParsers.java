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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|exception
operator|.
name|LiusException
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
name|parser
operator|.
name|ParserFactory
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
name|classDir
decl_stmt|;
specifier|private
name|String
name|config
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|String
name|sep
init|=
name|File
operator|.
name|separator
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
argument_list|,
name|File
operator|.
name|pathSeparator
argument_list|)
decl_stmt|;
name|classDir
operator|=
operator|new
name|File
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|=
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|sep
operator|+
literal|"config"
operator|+
name|sep
operator|+
literal|"config.xml"
expr_stmt|;
name|String
name|log4j
init|=
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|sep
operator|+
literal|"Config"
operator|+
name|sep
operator|+
literal|"log4j"
operator|+
name|sep
operator|+
literal|"log4j.properties"
decl_stmt|;
name|tc
operator|=
name|LiusConfig
operator|.
name|getInstance
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|LiusLogger
operator|.
name|setLoggerConfigFile
argument_list|(
name|log4j
argument_list|)
expr_stmt|;
block|}
comment|/*      * public void testConfig(){ TikaConfig tc =      * TikaConfig.getInstance("C:\\tika\\config\\tikaConfig2.xml"); ParserConfig      * pc = tc.getParserConfig("text/html"); assertEquals("parse-html",      * pc.getName()); }      */
specifier|public
name|void
name|testPDFExtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testPDF.PDF"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testTXTExtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testTXT.txt"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testRTFExtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testRTF.rtf"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testXMLExtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testXML.xml"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testPPTExtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testPPT.ppt"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|parser
operator|.
name|getStrContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testWORDxtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testWORD.doc"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|parser
operator|.
name|getStrContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testEXCELExtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testEXCEL.xls"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
comment|// System.out.println(parser.getStrContent());
name|printContentsInfo
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testOOExtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testOO2.odt"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
comment|// System.out.println(parser.getStrContent());
name|printContentsInfo
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testHTMLExtraction
parameter_list|()
block|{
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|classDir
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testFiles"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"testHTML.html"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|ParserFactory
operator|.
name|getParser
argument_list|(
name|testFile
argument_list|,
name|tc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Title : Test Indexation Html"
argument_list|,
operator|(
name|parser
operator|.
name|getContent
argument_list|(
literal|"title"
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// System.out.println(parser.getStrContent());
name|printContentsInfo
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LiusException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|printContentsInfo
parameter_list|(
name|Parser
name|parser
parameter_list|)
block|{
name|String
name|mimeType
init|=
name|parser
operator|.
name|getMimeType
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Mime : "
operator|+
name|mimeType
argument_list|)
expr_stmt|;
name|String
name|strContent
init|=
name|parser
operator|.
name|getStrContent
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Content
argument_list|>
name|structuredContent
init|=
name|parser
operator|.
name|getContents
argument_list|()
decl_stmt|;
name|Utils
operator|.
name|print
argument_list|(
name|structuredContent
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=============="
argument_list|)
expr_stmt|;
comment|// Content title = parser.getContent("title");
block|}
block|}
end_class

end_unit

