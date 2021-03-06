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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|OutputStreamFactoryTest
extends|extends
name|FSBatchTestBase
block|{
annotation|@
name|Test
specifier|public
name|void
name|testIllegalState
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"os-factory-illegal-state-"
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
name|run
argument_list|(
name|runner
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countChildren
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|illegalState
init|=
literal|false
decl_stmt|;
try|try
block|{
name|ParallelFileProcessingResult
name|result
init|=
name|run
argument_list|(
name|runner
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IllegalStateException
condition|)
block|{
name|illegalState
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Should have been an illegal state exception"
argument_list|,
name|illegalState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"os-factory-skip-"
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
literal|"handleExisting"
argument_list|,
literal|"skip"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countChildren
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
name|runner
operator|=
name|getNewBatchRunner
argument_list|(
literal|"/tika-batch-config-test.xml"
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|result
operator|=
name|run
argument_list|(
name|runner
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countChildren
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* turn this back on if there is any need to add "handleExisting"     @Test     public void testRename() throws Exception {         File outputDir = getNewOutputDir("os-factory-rename-");         Map<String, String> args = getDefaultArgs("basic", outputDir);          args.put("handleExisting", "rename");         BatchProcess runner = getNewBatchRunner("/tika-batch-config-basic-test.xml", args);         ParallelFileProcessingResult result = runner.execute();         assertEquals(1, outputDir.listFiles().length);          runner = getNewBatchRunner("/tika-batch-config-basic-test.xml", args);         result = runner.execute();         assertEquals(2, outputDir.listFiles().length);          runner = getNewBatchRunner("/tika-batch-config-basic-test.xml", args);         result = runner.execute();         assertEquals(3, outputDir.listFiles().length);          int hits = 0;         for (File f : outputDir.listFiles()){             String name = f.getName();             if (name.equals("test2_ok.xml.xml")) {                 hits++;             } else if (name.equals("test1(1).txt.xml")) {                 hits++;             } else if (name.equals("test1(2).txt.xml")) {                 hits++;             }         }         assertEquals(3, hits);     }     */
block|}
end_class

end_unit

