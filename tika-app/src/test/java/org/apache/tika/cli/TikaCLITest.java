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
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
comment|/**  * Tests the Tika's cli  */
end_comment

begin_class
specifier|public
class|class
name|TikaCLITest
extends|extends
name|TestCase
block|{
comment|/* Test members */
specifier|private
name|File
name|profile
init|=
literal|null
decl_stmt|;
specifier|private
name|ByteArrayOutputStream
name|outContent
init|=
literal|null
decl_stmt|;
specifier|private
name|PrintStream
name|stdout
init|=
literal|null
decl_stmt|;
specifier|private
name|PrintStream
name|reassign
init|=
literal|null
decl_stmt|;
specifier|private
name|URI
name|testDataURI
init|=
operator|new
name|File
argument_list|(
literal|"src/test/resources/test-data/"
argument_list|)
operator|.
name|toURI
argument_list|()
decl_stmt|;
specifier|private
name|String
name|resorcePrefix
init|=
name|testDataURI
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|profile
operator|=
operator|new
name|File
argument_list|(
literal|"welsh.ngp"
argument_list|)
expr_stmt|;
name|outContent
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|stdout
operator|=
name|System
operator|.
name|out
expr_stmt|;
name|reassign
operator|=
operator|new
name|PrintStream
argument_list|(
name|outContent
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|reassign
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates a welsh language profile 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testCreateProfile
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--create-profile=welsh"
block|,
literal|"-eUTF-8"
block|,
name|resorcePrefix
operator|+
literal|"welsh_corpus.txt"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|profile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests --list-parser-detail option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testListParserDetail
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--list-parser-detail"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"application/vnd.oasis.opendocument.text-web"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests --list-parser option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testListParsers
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--list-parser"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|!=
literal|null
operator|&&
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"org.apache.tika.parser.iwork.IWorkPackageParser"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests -x option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testXMLOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-x"
block|,
name|resorcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"?xml version=\"1.0\" encoding=\"UTF-8\"?"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests a -h option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testHTMLOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-h"
block|,
name|resorcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"html xmlns=\"http://www.w3.org/1999/xhtml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests -t option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testTextOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-t"
block|,
name|resorcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"finished off the cake"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests -m option of the cli 	 * @throws Exception 	 */
specifier|public
name|void
name|testMetadataOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-m"
block|,
name|resorcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests -l option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testLanguageOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-l"
block|,
name|resorcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"en"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests -d option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testDetectOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"-d"
block|,
name|resorcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests --list-met-models option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testListMetModels
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--list-met-models"
block|,
name|resorcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"text/plain"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tests --list-supported-types option of the cli 	 *  	 * @throws Exception 	 */
specifier|public
name|void
name|testListSupportedTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|params
init|=
block|{
literal|"--list-supported-types"
block|,
name|resorcePrefix
operator|+
literal|"alice.cli.test"
block|}
decl_stmt|;
name|TikaCLI
operator|.
name|main
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"supertype: application/octet-stream"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Tears down the test. Returns the System.out 	 */
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|profile
operator|!=
literal|null
operator|&&
name|profile
operator|.
name|exists
argument_list|()
condition|)
name|profile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|stdout
argument_list|)
expr_stmt|;
name|closeStreams
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|closeStreams
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|outContent
operator|!=
literal|null
condition|)
name|outContent
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|stdout
operator|!=
literal|null
condition|)
name|stdout
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|reassign
operator|!=
literal|null
condition|)
name|reassign
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

