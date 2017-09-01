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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|BatchProcessDriverCLI
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
name|BatchProcessTest
extends|extends
name|FSBatchTestBase
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
specifier|public
name|void
name|oneHeavyHangTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"one_heavy_hang-"
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
literal|"one_heavy_hang"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|countChildren
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|hvyHang
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test0_heavy_hang.xml.xml"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|hvyHang
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|hvyHang
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
name|BatchProcess
operator|.
name|BATCH_CONSTANTS
operator|.
name|BATCH_PROCESS_FATAL_MUST_RESTART
operator|.
name|toString
argument_list|()
argument_list|,
name|streamStrings
operator|.
name|getErrString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
specifier|public
name|void
name|allHeavyHangsTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|//each of the three threads hits a heavy hang.  The BatchProcess runs into
comment|//all timedouts and shuts down.
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"allHeavyHangs-"
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
literal|"heavy_heavy_hangs"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|countChildren
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|hvyHang
range|:
name|listPaths
argument_list|(
name|outputDir
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|hvyHang
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"file length for "
operator|+
name|hvyHang
operator|.
name|getFileName
argument_list|()
operator|+
literal|" should be 0, but is: "
operator|+
name|Files
operator|.
name|size
argument_list|(
name|hvyHang
argument_list|)
argument_list|,
literal|0
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|hvyHang
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertContains
argument_list|(
name|BatchProcess
operator|.
name|BATCH_CONSTANTS
operator|.
name|BATCH_PROCESS_FATAL_MUST_RESTART
operator|.
name|toString
argument_list|()
argument_list|,
name|streamStrings
operator|.
name|getErrString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|allHeavyHangsTestWithCrazyNumberConsumersTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"allHeavyHangsCrazyNumberConsumers-"
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
literal|"heavy_heavy_hangs"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|countChildren
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|hvyHang
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test"
operator|+
name|i
operator|+
literal|"_heavy_hang.xml.xml"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|hvyHang
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|hvyHang
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertContains
argument_list|(
literal|"This is tika-batch's first test file"
argument_list|,
name|readFileToString
argument_list|(
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test6_ok.xml.xml"
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
comment|//key that the process realize that there were no more processable files
comment|//in the queue and does not ask for a restart!
name|assertNotContained
argument_list|(
name|BatchProcess
operator|.
name|BATCH_CONSTANTS
operator|.
name|BATCH_PROCESS_FATAL_MUST_RESTART
operator|.
name|toString
argument_list|()
argument_list|,
name|streamStrings
operator|.
name|getErrString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|allHeavyHangsTestWithStarvedCrawler
parameter_list|()
throws|throws
name|Exception
block|{
comment|//this tests that if all consumers are hung and the crawler is
comment|//waiting to add to the queue, there isn't deadlock.  The batchrunner should
comment|//shutdown and ask to be restarted.
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"allHeavyHangsStarvedCrawler-"
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
literal|"heavy_heavy_hangs"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxQueueSize"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"timeoutThresholdMillis"
argument_list|,
literal|"100000000"
argument_list|)
expr_stmt|;
comment|//make sure that the batch process doesn't time out
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|countChildren
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|hvyHang
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test"
operator|+
name|i
operator|+
literal|"_heavy_hang.xml.xml"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|hvyHang
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|hvyHang
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertContains
argument_list|(
name|BatchProcess
operator|.
name|BATCH_CONSTANTS
operator|.
name|BATCH_PROCESS_FATAL_MUST_RESTART
operator|.
name|toString
argument_list|()
argument_list|,
name|streamStrings
operator|.
name|getErrString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Crawler timed out"
argument_list|,
name|streamStrings
operator|.
name|getErrString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
specifier|public
name|void
name|outOfMemory
parameter_list|()
throws|throws
name|Exception
block|{
comment|//the first consumer should sleep for 10 seconds
comment|//the second should be tied up in a heavy hang
comment|//the third one should hit the oom after processing test2_ok.xml
comment|//no consumers should process test2-4.txt!
comment|//i.e. the first consumer will finish in 10 seconds and
comment|//then otherwise would be looking for more, but the oom should prevent that
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"oom-"
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
literal|"oom"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"timeoutThresholdMillis"
argument_list|,
literal|"30000"
argument_list|)
expr_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|countChildren
argument_list|(
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is tika-batch's first test file"
argument_list|,
name|readFileToString
argument_list|(
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test2_ok.xml.xml"
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|BatchProcess
operator|.
name|BATCH_CONSTANTS
operator|.
name|BATCH_PROCESS_FATAL_MUST_RESTART
operator|.
name|toString
argument_list|()
argument_list|,
name|streamStrings
operator|.
name|getErrString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
specifier|public
name|void
name|noRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"no_restart"
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
literal|"no_restart"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|Path
name|test2
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test2_norestart.xml.xml"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"test2_norestart.xml"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|test2
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|test3
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test3_ok.xml.xml"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"test3_ok.xml"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|test3
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"exitStatus="
operator|+
name|BatchProcessDriverCLI
operator|.
name|PROCESS_NO_RESTART_EXIT_CODE
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"causeForTermination='MAIN_LOOP_EXCEPTION_NO_RESTART'"
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * This tests to make sure that BatchProcess waits the appropriate      * amount of time on an early termination before stopping.      *      * If this fails, then interruptible parsers (e.g. those with      * nio channels) will be interrupted and there will be corrupted data.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testWaitAfterEarlyTermination
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"wait_after_early_termination"
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
literal|"wait_after_early_termination"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxAliveTimeSeconds"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
comment|//main process loop should stop after 5 seconds
name|args
operator|.
name|put
argument_list|(
literal|"timeoutThresholdMillis"
argument_list|,
literal|"300000"
argument_list|)
expr_stmt|;
comment|//effectively never
name|args
operator|.
name|put
argument_list|(
literal|"pauseOnEarlyTerminationMillis"
argument_list|,
literal|"20000"
argument_list|)
expr_stmt|;
comment|//let the parser have up to 20 seconds
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
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
name|assertContains
argument_list|(
literal|"<p>some content</p>"
argument_list|,
name|readFileToString
argument_list|(
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test0_sleep.xml.xml"
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"exitStatus="
operator|+
name|BatchProcessDriverCLI
operator|.
name|PROCESS_RESTART_EXIT_CODE
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"causeForTermination='BATCH_PROCESS_ALIVE_TOO_LONG'"
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testTimeOutAfterBeingAskedToShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"timeout_after_early_termination"
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
literal|"timeout_after_early_termination"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxAliveTimeSeconds"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
comment|//main process loop should stop after 5 seconds
name|args
operator|.
name|put
argument_list|(
literal|"timeoutThresholdMillis"
argument_list|,
literal|"10000"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"pauseOnEarlyTerminationMillis"
argument_list|,
literal|"20000"
argument_list|)
expr_stmt|;
comment|//let the parser have up to 20 seconds
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|paths
init|=
name|listPaths
argument_list|(
name|outputDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|paths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|paths
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"exitStatus="
operator|+
name|BatchProcessDriverCLI
operator|.
name|PROCESS_RESTART_EXIT_CODE
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"causeForTermination='BATCH_PROCESS_ALIVE_TOO_LONG'"
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testRedirectionOfStreams
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test redirection of system.err to system.out
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"noisy_parsers"
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
literal|"noisy_parsers"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxAliveTimeSeconds"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
comment|//main process loop should stop after 5 seconds
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
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
name|assertContains
argument_list|(
literal|"System.out"
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"System.err"
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|streamStrings
operator|.
name|getErrString
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|&&
operator|!
name|streamStrings
operator|.
name|getErrString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Picked up"
argument_list|)
condition|)
block|{
comment|// see https://github.com/apache/tika/pull/201
name|fail
argument_list|(
literal|"nothing should have been written to stderr, but I saw>"
operator|+
name|streamStrings
operator|.
name|getErrString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\r"
argument_list|,
literal|"<r>"
operator|+
literal|"<"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testConsumersManagerInitHang
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"init_hang"
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
literal|"noisy_parsers"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hangOnInit"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|,
literal|"/tika-batch-config-MockConsumersBuilder.xml"
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|BatchProcessDriverCLI
operator|.
name|PROCESS_NO_RESTART_EXIT_CODE
argument_list|,
name|ex
operator|.
name|getExitValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"causeForTermination='CONSUMERS_MANAGER_DIDNT_INIT_IN_TIME_NO_RESTART'"
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
specifier|public
name|void
name|testConsumersManagerShutdownHang
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"shutdown_hang"
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
literal|"noisy_parsers"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hangOnShutdown"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|,
literal|"/tika-batch-config-MockConsumersBuilder.xml"
argument_list|)
decl_stmt|;
name|StreamStrings
name|streamStrings
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|BatchProcessDriverCLI
operator|.
name|PROCESS_NO_RESTART_EXIT_CODE
argument_list|,
name|ex
operator|.
name|getExitValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"ConsumersManager did not shutdown within"
argument_list|,
name|streamStrings
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHierarchicalWFileList
parameter_list|()
throws|throws
name|Exception
block|{
comment|//tests to make sure that hierarchy is maintained when reading from
comment|//file list
comment|//also tests that list actually works.
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"hierarchical_file_list"
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
literal|"hierarchical"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fileList"
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/testFileList.txt"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
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
name|args
operator|.
name|put
argument_list|(
literal|"basicHandlerType"
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|,
literal|"/tika-batch-config-MockConsumersBuilder.xml"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|execute
argument_list|()
expr_stmt|;
name|Path
name|test1
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test1.xml.json"
argument_list|)
decl_stmt|;
name|Path
name|test2
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"sub1a/test2.xml.json"
argument_list|)
decl_stmt|;
name|Path
name|test3
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"sub1a/sub2a/test3.xml.json"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"test1 exists"
argument_list|,
name|Files
operator|.
name|exists
argument_list|(
name|test1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test1 length> 10"
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|test1
argument_list|)
operator|>
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|test3
argument_list|)
operator|&&
name|Files
operator|.
name|size
argument_list|(
name|test3
argument_list|)
operator|>
literal|10
argument_list|)
expr_stmt|;
name|Path
name|test2Dir
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"sub1a"
argument_list|)
decl_stmt|;
comment|//should be just the subdirectory, no actual test2 file
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|countChildren
argument_list|(
name|test2Dir
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|test2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHandlingOfIllegalXMLCharsInException
parameter_list|()
throws|throws
name|Exception
block|{
comment|//tests to make sure that hierarchy is maintained when reading from
comment|//file list
comment|//also tests that list actually works.
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"illegal_xml_chars_in_exception"
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
literal|"illegal_xml_chars_in_exception"
argument_list|,
name|outputDir
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"numConsumers"
argument_list|,
literal|"1"
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
name|args
operator|.
name|put
argument_list|(
literal|"basicHandlerType"
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|,
literal|"/tika-batch-config-MockConsumersBuilder.xml"
argument_list|,
literal|"/log4j-on.properties"
argument_list|)
decl_stmt|;
name|StreamStrings
name|ss
init|=
name|ex
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|ss
operator|.
name|getOutString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"error writing xml stream for"
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"parse_ex resourceId=\"test0_bad_chars.xml\""
argument_list|,
name|ss
operator|.
name|getOutString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverrideOutputSuffix
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|outputDir
init|=
name|getNewOutputDir
argument_list|(
literal|"outputSuffixTest"
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
literal|"numConsumers"
argument_list|,
literal|"1"
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
name|args
operator|.
name|put
argument_list|(
literal|"basicHandlerType"
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|BatchProcessTestExecutor
name|ex
init|=
operator|new
name|BatchProcessTestExecutor
argument_list|(
name|args
argument_list|,
literal|"/tika-batch-config-test-suffix-override.xml"
argument_list|,
literal|"/log4j-on.properties"
argument_list|)
decl_stmt|;
name|ex
operator|.
name|execute
argument_list|()
expr_stmt|;
name|Path
name|targ
init|=
name|outputDir
operator|.
name|resolve
argument_list|(
literal|"test0.xml.mysuffix"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|isRegularFile
argument_list|(
name|targ
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|BatchProcessTestExecutor
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
specifier|private
specifier|final
name|String
name|configPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|loggerProps
decl_stmt|;
specifier|private
name|int
name|exitValue
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
specifier|public
name|BatchProcessTestExecutor
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|this
argument_list|(
name|args
argument_list|,
literal|"/tika-batch-config-test.xml"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BatchProcessTestExecutor
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|configPath
parameter_list|)
block|{
name|this
argument_list|(
name|args
argument_list|,
name|configPath
argument_list|,
literal|"/log4j_process.properties"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BatchProcessTestExecutor
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|String
name|configPath
parameter_list|,
name|String
name|loggerProps
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|configPath
operator|=
name|configPath
expr_stmt|;
name|this
operator|.
name|loggerProps
operator|=
name|loggerProps
expr_stmt|;
block|}
specifier|private
name|StreamStrings
name|execute
parameter_list|()
block|{
name|Process
name|p
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ProcessBuilder
name|b
init|=
name|getNewBatchRunnerProcess
argument_list|(
name|configPath
argument_list|,
name|loggerProps
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|p
operator|=
name|b
operator|.
name|start
argument_list|()
expr_stmt|;
name|StringStreamGobbler
name|errorGobbler
init|=
operator|new
name|StringStreamGobbler
argument_list|(
name|p
operator|.
name|getErrorStream
argument_list|()
argument_list|)
decl_stmt|;
name|StringStreamGobbler
name|outGobbler
init|=
operator|new
name|StringStreamGobbler
argument_list|(
name|p
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|Thread
name|errorThread
init|=
operator|new
name|Thread
argument_list|(
name|errorGobbler
argument_list|)
decl_stmt|;
name|Thread
name|outThread
init|=
operator|new
name|Thread
argument_list|(
name|outGobbler
argument_list|)
decl_stmt|;
name|errorThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|outThread
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|exitValue
operator|=
name|p
operator|.
name|exitValue
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|IllegalThreadStateException
name|e
parameter_list|)
block|{
comment|//still going;
block|}
block|}
name|errorGobbler
operator|.
name|stopGobblingAndDie
argument_list|()
expr_stmt|;
name|outGobbler
operator|.
name|stopGobblingAndDie
argument_list|()
expr_stmt|;
name|errorThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|outThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
operator|new
name|StreamStrings
argument_list|(
name|outGobbler
operator|.
name|toString
argument_list|()
argument_list|,
name|errorGobbler
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|destroyProcess
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|int
name|getExitValue
parameter_list|()
block|{
return|return
name|exitValue
return|;
block|}
block|}
specifier|private
class|class
name|StreamStrings
block|{
specifier|private
specifier|final
name|String
name|outString
decl_stmt|;
specifier|private
specifier|final
name|String
name|errString
decl_stmt|;
specifier|private
name|StreamStrings
parameter_list|(
name|String
name|outString
parameter_list|,
name|String
name|errString
parameter_list|)
block|{
name|this
operator|.
name|outString
operator|=
name|outString
expr_stmt|;
name|this
operator|.
name|errString
operator|=
name|errString
expr_stmt|;
block|}
specifier|private
name|String
name|getOutString
parameter_list|()
block|{
return|return
name|outString
return|;
block|}
specifier|private
name|String
name|getErrString
parameter_list|()
block|{
return|return
name|errString
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"OUT>>"
operator|+
name|outString
operator|+
literal|"<<\n"
operator|+
literal|"ERR>>"
operator|+
name|errString
operator|+
literal|"<<\n"
return|;
block|}
block|}
block|}
end_class

end_unit

