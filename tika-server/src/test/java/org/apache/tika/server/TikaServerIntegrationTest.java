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
name|server
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|common
operator|.
name|logging
operator|.
name|LogUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|client
operator|.
name|WebClient
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
name|OfficeOpenXMLExtended
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
name|serialization
operator|.
name|JsonMetadataList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|StandardCopyOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Permission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
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
name|Random
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
name|TimeoutException
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

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

begin_class
specifier|public
class|class
name|TikaServerIntegrationTest
extends|extends
name|TikaTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TikaServerIntegrationTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_RECURSIVE_DOC
init|=
literal|"test_recursive_embedded.docx"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_OOM
init|=
literal|"mock/fake_oom.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_SYSTEM_EXIT
init|=
literal|"mock/system_exit.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_HEAVY_HANG
init|=
literal|"mock/heavy_hang_30000.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_HEAVY_HANG_SHORT
init|=
literal|"mock/heavy_hang_100.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_STDOUT_STDERR
init|=
literal|"mock/testStdOutErr.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_STATIC_STDOUT_STDERR
init|=
literal|"mock/testStaticStdOutErr.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|META_PATH
init|=
literal|"/rmeta"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MAX_WAIT_MS
init|=
literal|60000
decl_stmt|;
comment|//running into conflicts on 9998 with the CXFTestBase tests
comment|//TODO: figure out why?!
specifier|private
specifier|static
specifier|final
name|String
name|INTEGRATION_TEST_PORT
init|=
literal|"9999"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|endPoint
init|=
literal|"http://localhost:"
operator|+
name|INTEGRATION_TEST_PORT
decl_stmt|;
specifier|private
name|SecurityManager
name|existingSecurityManager
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Path
name|LOG_FILE
decl_stmt|;
specifier|private
specifier|static
class|class
name|MyExitException
extends|extends
name|RuntimeException
block|{
specifier|private
specifier|final
name|int
name|status
decl_stmt|;
name|MyExitException
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
specifier|public
name|int
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|staticSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|LogUtils
operator|.
name|setLoggerClass
argument_list|(
name|NullWebClientLogger
operator|.
name|class
argument_list|)
expr_stmt|;
name|LOG_FILE
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|"tika-server-integration"
argument_list|,
literal|".xml"
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|TikaServerIntegrationTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/logging/log4j_child.xml"
argument_list|)
argument_list|,
name|LOG_FILE
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|existingSecurityManager
operator|=
name|System
operator|.
name|getSecurityManager
argument_list|()
expr_stmt|;
name|System
operator|.
name|setSecurityManager
argument_list|(
operator|new
name|SecurityManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|checkExit
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|super
operator|.
name|checkExit
argument_list|(
name|status
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MyExitException
argument_list|(
name|status
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkPermission
parameter_list|(
name|Permission
name|perm
parameter_list|)
block|{
comment|// all ok
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkPermission
parameter_list|(
name|Permission
name|perm
parameter_list|,
name|Object
name|context
parameter_list|)
block|{
comment|// all ok
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|staticTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Files
operator|.
name|delete
argument_list|(
name|LOG_FILE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setSecurityManager
argument_list|(
name|existingSecurityManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-maxFiles"
block|,
literal|"2000"
block|,
literal|"-spawnChild"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-tmpFilePrefix"
block|,
literal|"basic-"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|testBaseline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOOM
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-JXmx256m"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-pingPulseMillis"
block|,
literal|"100"
block|,
literal|"-tmpFilePrefix"
block|,
literal|"tika-server-oom"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_OOM
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//oom may or may not cause an exception depending
comment|//on the timing
block|}
comment|//give some time for the server to crash/kill itself
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
try|try
block|{
name|testBaseline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSystemExit
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-tmpFilePrefix"
block|,
literal|"tika-server-systemexit"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_SYSTEM_EXIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//sys exit causes catchable problems for the client
block|}
comment|//give some time for the server to crash/kill itself
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
try|try
block|{
name|testBaseline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTimeoutOk
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test that there's enough time for this file.
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-taskTimeoutMillis"
block|,
literal|"10000"
block|,
literal|"-taskPulseMillis"
block|,
literal|"500"
block|,
literal|"-pingPulseMillis"
block|,
literal|"500"
block|,
literal|"-tmpFilePrefix"
block|,
literal|"tika-server-timeoutok"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_HEAVY_HANG_SHORT
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//potential exception depending on timing
block|}
try|try
block|{
name|testBaseline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
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
name|testTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-taskTimeoutMillis"
block|,
literal|"10000"
block|,
literal|"-taskPulseMillis"
block|,
literal|"100"
block|,
literal|"-pingPulseMillis"
block|,
literal|"100"
block|,
literal|"-tmpFilePrefix"
block|,
literal|"tika-server-timeout"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_HEAVY_HANG
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//catchable exception when server shuts down.
block|}
try|try
block|{
name|testBaseline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBadJVMArgs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|i
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-JXms20m"
block|,
literal|"-JXmx10m"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-tmpFilePrefix"
block|,
literal|"tika-server-badargs"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
operator|new
name|Thread
operator|.
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|i
operator|.
name|set
argument_list|(
operator|(
operator|(
name|MyExitException
operator|)
name|e
operator|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|serverThread
operator|.
name|join
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|i
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStdErrOutBasic
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|i
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-taskTimeoutMillis"
block|,
literal|"60000"
block|,
literal|"-taskPulseMillis"
block|,
literal|"500"
block|,
literal|"-pingPulseMillis"
block|,
literal|"100"
block|,
literal|"-tmpFilePrefix"
block|,
literal|"tika-server-stderr"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_STDOUT_STDERR
argument_list|)
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|JsonMetadataList
operator|.
name|fromJson
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"quick brown fox"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"X-TIKA:content"
argument_list|)
argument_list|)
expr_stmt|;
name|testBaseline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"This works, but prints too much junk to the console.  Figure out how to gobble/redirect."
argument_list|)
specifier|public
name|void
name|testStaticStdErrOutBasic
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|i
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-taskTimeoutMillis"
block|,
literal|"60000"
block|,
literal|"-taskPulseMillis"
block|,
literal|"500"
block|,
literal|"-pingPulseMillis"
block|,
literal|"100"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_STATIC_STDOUT_STDERR
argument_list|)
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|JsonMetadataList
operator|.
name|fromJson
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"quick brown fox"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"X-TIKA:content"
argument_list|)
argument_list|)
expr_stmt|;
name|testBaseline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStdErrOutLogging
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|i
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-spawnChild"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|,
literal|"-taskTimeoutMillis"
block|,
literal|"10000"
block|,
literal|"-taskPulseMillis"
block|,
literal|"500"
block|,
literal|"-pingPulseMillis"
block|,
literal|"100"
block|,
literal|"-maxRestarts"
block|,
literal|"0"
block|,
literal|"-JDlog4j.configuration=file:"
operator|+
name|LOG_FILE
operator|.
name|toAbsolutePath
argument_list|()
block|,
literal|"-tmpFilePrefix"
block|,
literal|"tika-server-stderrlogging"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_STDOUT_STDERR
argument_list|)
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|JsonMetadataList
operator|.
name|fromJson
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"quick brown fox"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"X-TIKA:content"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|testBaseline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|awaitServerStartup
parameter_list|()
throws|throws
name|Exception
block|{
name|Instant
name|started
init|=
name|Instant
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
name|Duration
operator|.
name|between
argument_list|(
name|started
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
decl_stmt|;
name|WebClient
name|client
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
literal|"/tika"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"text/plain"
argument_list|)
decl_stmt|;
while|while
condition|(
name|elapsed
operator|<
name|MAX_WAIT_MS
condition|)
block|{
try|try
block|{
name|Response
name|response
init|=
name|client
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatus
argument_list|()
operator|==
literal|200
condition|)
block|{
name|elapsed
operator|=
name|Duration
operator|.
name|between
argument_list|(
name|started
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"client observes server successfully started after "
operator|+
name|elapsed
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"tika test client failed to connect to server with status: {}"
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ProcessingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"tika test client failed to connect to server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|elapsed
operator|=
name|Duration
operator|.
name|between
argument_list|(
name|started
argument_list|,
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|toMillis
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"couldn't connect to server after "
operator|+
name|elapsed
operator|+
literal|" ms"
argument_list|)
throw|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"turn this into a real test"
argument_list|)
specifier|public
name|void
name|testMaxFiles
parameter_list|()
throws|throws
name|Exception
block|{
comment|//this isn't a real regression test yet.
comment|//Can watch logs at least for confirmation of behavior
comment|//TODO: convert to real test
name|Thread
name|serverThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|TikaServerCli
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-maxFiles"
block|,
literal|"10"
block|,
literal|"-spawnChild"
block|,
literal|"-taskTimeoutMillis"
block|,
literal|"10000"
block|,
literal|"-taskPulseMillis"
block|,
literal|"500"
block|,
literal|"-p"
block|,
name|INTEGRATION_TEST_PORT
block|}
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|serverThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FILE # "
operator|+
name|i
argument_list|)
expr_stmt|;
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
name|Response
name|response
init|=
literal|null
decl_stmt|;
name|String
name|file
init|=
name|TEST_RECURSIVE_DOC
decl_stmt|;
try|try
block|{
if|if
condition|(
name|r
operator|.
name|nextFloat
argument_list|()
operator|<
literal|0.01
condition|)
block|{
name|file
operator|=
name|TEST_SYSTEM_EXIT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|r
operator|.
name|nextFloat
argument_list|()
operator|<
literal|0.015
condition|)
block|{
name|file
operator|=
name|TEST_OOM
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|r
operator|.
name|nextFloat
argument_list|()
operator|<
literal|0.02
condition|)
block|{
name|file
operator|=
name|TEST_HEAVY_HANG
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"about to process: "
operator|+
name|file
argument_list|)
expr_stmt|;
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|ex
operator|||
name|response
operator|.
name|getStatus
argument_list|()
operator|!=
literal|200
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"restarting"
argument_list|)
expr_stmt|;
name|i
operator|--
expr_stmt|;
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"done awaiting"
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|file
operator|.
name|equals
argument_list|(
name|TEST_RECURSIVE_DOC
argument_list|)
condition|)
block|{
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|JsonMetadataList
operator|.
name|fromJson
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Microsoft Office Word"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"plundered our seas"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|get
argument_list|(
literal|"X-TIKA:content"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//assertEquals("a38e6c7b38541af87148dee9634cb811", metadataList.get(10).get("X-TIKA:digest:MD5"));
block|}
name|serverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|testBaseline
parameter_list|()
throws|throws
name|Exception
block|{
name|awaitServerStartup
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|endPoint
operator|+
name|META_PATH
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/json"
argument_list|)
operator|.
name|put
argument_list|(
name|ClassLoader
operator|.
name|getSystemResourceAsStream
argument_list|(
name|TEST_RECURSIVE_DOC
argument_list|)
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|(
name|InputStream
operator|)
name|response
operator|.
name|getEntity
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|JsonMetadataList
operator|.
name|fromJson
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Microsoft Office Word"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"plundered our seas"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|get
argument_list|(
literal|"X-TIKA:content"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

