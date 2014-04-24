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
name|code
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ParseContext
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

begin_class
specifier|public
class|class
name|SourceCodeParserTest
extends|extends
name|TikaTest
block|{
specifier|private
name|SourceCodeParser
name|sourceCodeParser
init|=
operator|new
name|SourceCodeParser
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSupportTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedTypes
init|=
name|sourceCodeParser
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|supportedTypes
operator|.
name|contains
argument_list|(
operator|new
name|MediaType
argument_list|(
literal|"text"
argument_list|,
literal|"x-java-source"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|supportedTypes
operator|.
name|contains
argument_list|(
operator|new
name|MediaType
argument_list|(
literal|"text"
argument_list|,
literal|"x-groovy"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|supportedTypes
operator|.
name|contains
argument_list|(
operator|new
name|MediaType
argument_list|(
literal|"text"
argument_list|,
literal|"x-c++src"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sourceCodeParser
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
operator|new
name|MediaType
argument_list|(
literal|"text"
argument_list|,
literal|"html"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHTMLRender
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|htmlContent
init|=
name|getXML
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testJAVA.java"
argument_list|)
argument_list|,
name|sourceCodeParser
argument_list|,
name|createMetadata
argument_list|(
literal|"text/x-java-source"
argument_list|)
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertTrue
argument_list|(
name|htmlContent
operator|.
name|indexOf
argument_list|(
literal|"&gt;&lt;code&gt;&lt;span class=\"java_javadoc_comment\"&gt;"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|htmlContent
operator|.
name|indexOf
argument_list|(
literal|"&lt;span class=\"java_type\"&gt;HelloWorld&lt;/span&gt;"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|htmlContent
operator|.
name|indexOf
argument_list|(
literal|"&gt;&lt;span class=\"java_keyword\"&gt;public&lt;"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHTMLRenderWithReturnLine
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|htmlContent
init|=
name|getXML
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testJAVA.java"
argument_list|)
argument_list|,
name|sourceCodeParser
argument_list|,
name|createMetadata
argument_list|(
literal|"text/x-java-source"
argument_list|)
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertTrue
argument_list|(
name|htmlContent
operator|.
name|indexOf
argument_list|(
literal|"&lt;span class=\"java_javadoc_comment\"&gt;&amp;nbsp;*&lt;/span&gt;&lt;br /&gt;"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoC
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|metadata
init|=
name|createMetadata
argument_list|(
literal|"text/x-groovy"
argument_list|)
decl_stmt|;
name|getText
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testGROOVY.groovy"
argument_list|)
argument_list|,
name|sourceCodeParser
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"LoC"
argument_list|)
argument_list|,
literal|"9"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthor
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|metadata
init|=
name|createMetadata
argument_list|(
literal|"text/x-c++src"
argument_list|)
decl_stmt|;
name|getText
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testCPP.cpp"
argument_list|)
argument_list|,
name|sourceCodeParser
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hong-Thai Nguyen"
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReturnContentAsIsForTextHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|strContent
init|=
name|getXML
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testJAVA.java"
argument_list|)
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|,
name|createMetadata
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertTrue
argument_list|(
name|strContent
operator|.
name|indexOf
argument_list|(
literal|"public class HelloWorld {"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Metadata
name|createMetadata
parameter_list|(
name|String
name|mimeType
parameter_list|)
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
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"testFile"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|mimeType
argument_list|)
expr_stmt|;
return|return
name|metadata
return|;
block|}
block|}
end_class

end_unit

