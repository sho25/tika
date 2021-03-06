begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|batch
operator|.
name|fs
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|batch
operator|.
name|BatchProcess
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
name|batch
operator|.
name|ParallelFileProcessingResult
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
name|HandlerBuilderTest
extends|extends
name|FSBatchTestBase
block|{
annotation|@
name|Test
specifier|public
name|void
name|testXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"handler-xml-"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|getDefaultArgs
argument_list|(
literal|"basic"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"basicHandlerType"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|BatchProcess
name|runner
init|=
name|getNewBatchRunner
argument_list|(
literal|"/tika-batch-config-test.xml"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ParallelFileProcessingResult
name|result
init|=
name|run
argument_list|(
name|runner
argument_list|)
decl_stmt|;
name|Path
name|outputFile
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test0.xml.xml"
argument_list|)
decl_stmt|;
name|String
name|resultString
init|=
name|readFileToString
argument_list|(
name|outputFile
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"<html xmlns=\"http://www.w3.org/1999/xhtml\">"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"This is tika-batch's first test file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"handler-html-"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|getDefaultArgs
argument_list|(
literal|"basic"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"basicHandlerType"
argument_list|,
literal|"html"
argument_list|)
expr_stmt|;
name|BatchProcess
name|runner
init|=
name|getNewBatchRunner
argument_list|(
literal|"/tika-batch-config-test.xml"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ParallelFileProcessingResult
name|result
init|=
name|run
argument_list|(
name|runner
argument_list|)
decl_stmt|;
name|Path
name|outputFile
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test0.xml.html"
argument_list|)
decl_stmt|;
name|String
name|resultString
init|=
name|readFileToString
argument_list|(
name|outputFile
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"<html xmlns=\"http://www.w3.org/1999/xhtml\">"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"This is tika-batch's first test file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testText
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"handler-txt-"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|getDefaultArgs
argument_list|(
literal|"basic"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"basicHandlerType"
argument_list|,
literal|"txt"
argument_list|)
expr_stmt|;
name|BatchProcess
name|runner
init|=
name|getNewBatchRunner
argument_list|(
literal|"/tika-batch-config-test.xml"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ParallelFileProcessingResult
name|result
init|=
name|run
argument_list|(
name|runner
argument_list|)
decl_stmt|;
name|Path
name|outputFile
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test0.xml.txt"
argument_list|)
decl_stmt|;
name|String
name|resultString
init|=
name|readFileToString
argument_list|(
name|outputFile
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"<html xmlns=\"http://www.w3.org/1999/xhtml\">"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"This is tika-batch's first test file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXMLWithWriteLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"handler-xml-write-limit-"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|getDefaultArgs
argument_list|(
literal|"basic"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"writeLimit"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|BatchProcess
name|runner
init|=
name|getNewBatchRunner
argument_list|(
literal|"/tika-batch-config-test.xml"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ParallelFileProcessingResult
name|result
init|=
name|run
argument_list|(
name|runner
argument_list|)
decl_stmt|;
name|Path
name|outputFile
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test0.xml.xml"
argument_list|)
decl_stmt|;
name|String
name|resultString
init|=
name|readFileToString
argument_list|(
name|outputFile
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
comment|//this is not ideal. How can we change handlers to writeout whatever
comment|//they've gotten so far, up to the writeLimit?
name|assertTrue
argument_list|(
name|resultString
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRecursiveParserWrapper
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"handler-recursive-parser"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
name|getDefaultArgs
argument_list|(
literal|"basic"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"basicHandlerType"
argument_list|,
literal|"txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"recursiveParserWrapper"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|BatchProcess
name|runner
init|=
name|getNewBatchRunner
argument_list|(
literal|"/tika-batch-config-test.xml"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ParallelFileProcessingResult
name|result
init|=
name|run
argument_list|(
name|runner
argument_list|)
decl_stmt|;
name|Path
name|outputFile
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test0.xml.json"
argument_list|)
decl_stmt|;
name|String
name|resultString
init|=
name|readFileToString
argument_list|(
name|outputFile
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"\"author\":\"Nikolai Lobachevsky\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resultString
operator|.
name|contains
argument_list|(
literal|"tika-batch\\u0027s first test file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

