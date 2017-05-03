begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|mock
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
name|ByteArrayOutputStream
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
name|InputStream
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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|exception
operator|.
name|TikaException
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
name|Parser
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
name|MockParserTest
extends|extends
name|TikaTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|M
init|=
literal|"/test-documents/mock/"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Parser
name|PARSER
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|XMLResult
name|getXML
parameter_list|(
name|String
name|path
parameter_list|,
name|Metadata
name|m
parameter_list|)
throws|throws
name|Exception
block|{
comment|//note that this is specific to MockParserTest with addition of M to the path!
name|InputStream
name|is
init|=
name|getResourceAsStream
argument_list|(
name|M
operator|+
name|path
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|super
operator|.
name|getXML
argument_list|(
name|is
argument_list|,
name|PARSER
argument_list|,
name|m
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExample
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
name|ByteArrayOutputStream
name|outBos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|errBos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|tmpOut
init|=
operator|new
name|PrintStream
argument_list|(
name|outBos
argument_list|,
literal|true
argument_list|,
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|PrintStream
name|tmpErr
init|=
operator|new
name|PrintStream
argument_list|(
name|errBos
argument_list|,
literal|true
argument_list|,
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|tmpOut
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|tmpErr
argument_list|)
expr_stmt|;
try|try
block|{
name|assertThrowable
argument_list|(
literal|"example.xml"
argument_list|,
name|m
argument_list|,
name|IOException
operator|.
name|class
argument_list|,
literal|"not another IOException"
argument_list|)
expr_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
name|String
name|outString
init|=
operator|new
name|String
argument_list|(
name|outBos
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"writing to System.out"
argument_list|,
name|outString
argument_list|)
expr_stmt|;
name|String
name|errString
init|=
operator|new
name|String
argument_list|(
name|errBos
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"writing to System.err"
argument_list|,
name|errString
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNothingBad
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|getXML
argument_list|(
literal|"nothing_bad.xml"
argument_list|,
name|m
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Geoffrey Chaucer"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"author"
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>And bathed every veyne in swich licour,</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullPointer
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|assertThrowable
argument_list|(
literal|"null_pointer.xml"
argument_list|,
name|m
argument_list|,
name|NullPointerException
operator|.
name|class
argument_list|,
literal|"another null pointer exception"
argument_list|)
expr_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullPointerNoMsg
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|assertThrowable
argument_list|(
literal|"null_pointer_no_msg.xml"
argument_list|,
name|m
argument_list|,
name|NullPointerException
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSleep
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|start
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|getXML
argument_list|(
literal|"sleep.xml"
argument_list|,
name|m
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|long
name|elapsed
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
decl_stmt|;
comment|//should sleep for at least 3000
name|boolean
name|enoughTimeHasElapsed
init|=
name|elapsed
operator|>
literal|2000
decl_stmt|;
name|assertTrue
argument_list|(
literal|"not enough time has not elapsed: "
operator|+
name|elapsed
argument_list|,
name|enoughTimeHasElapsed
argument_list|)
expr_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHeavyHang
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|start
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|getXML
argument_list|(
literal|"heavy_hang.xml"
argument_list|,
name|m
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|long
name|elapsed
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
decl_stmt|;
comment|//should sleep for at least 3000
name|boolean
name|enoughTimeHasElapsed
init|=
name|elapsed
operator|>
literal|2000
decl_stmt|;
name|assertTrue
argument_list|(
literal|"not enough time has elapsed: "
operator|+
name|elapsed
argument_list|,
name|enoughTimeHasElapsed
argument_list|)
expr_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFakeOOM
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|assertThrowable
argument_list|(
literal|"fake_oom.xml"
argument_list|,
name|m
argument_list|,
name|OutOfMemoryError
operator|.
name|class
argument_list|,
literal|"not another oom"
argument_list|)
expr_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRealOOM
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Note: we're not actually testing the diff between fake and real oom
comment|//i.e. by creating child process and setting different -Xmx or
comment|//memory profiling.
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|assertThrowable
argument_list|(
literal|"real_oom.xml"
argument_list|,
name|m
argument_list|,
name|OutOfMemoryError
operator|.
name|class
argument_list|,
literal|"Java heap space"
argument_list|)
expr_stmt|;
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInterruptibleSleep
parameter_list|()
block|{
comment|//Without static initialization of the parser, it can take ~1 second after t.start()
comment|//before the parser actually calls parse.  This is
comment|//just the time it takes to instantiate and call AutoDetectParser, do the detection, etc.
comment|//This is not thread creation overhead.
name|ParserRunnable
name|r
init|=
operator|new
name|ParserRunnable
argument_list|(
literal|"sleep_interruptible.xml"
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|long
name|start
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|t
operator|.
name|join
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
name|long
name|elapsed
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
decl_stmt|;
name|boolean
name|shortEnough
init|=
name|elapsed
operator|<
literal|2000
decl_stmt|;
comment|//the xml file specifies 3000
name|assertTrue
argument_list|(
literal|"elapsed ("
operator|+
name|elapsed
operator|+
literal|" millis) was not short enough"
argument_list|,
name|shortEnough
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonInterruptibleSleep
parameter_list|()
block|{
name|ParserRunnable
name|r
init|=
operator|new
name|ParserRunnable
argument_list|(
literal|"sleep_not_interruptible.xml"
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|long
name|start
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
try|try
block|{
comment|//make sure that the thread has actually started
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|t
operator|.
name|join
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
name|long
name|elapsed
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|start
decl_stmt|;
name|boolean
name|longEnough
init|=
name|elapsed
operator|>=
literal|3000
decl_stmt|;
comment|//the xml file specifies 3000, this sleeps 1000
name|assertTrue
argument_list|(
literal|"elapsed ("
operator|+
name|elapsed
operator|+
literal|" millis) was not long enough"
argument_list|,
name|longEnough
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|ParserRunnable
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
name|ParserRunnable
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
block|{
name|getXML
argument_list|(
name|path
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|assertMockParser
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|assertThrowable
parameter_list|(
name|String
name|path
parameter_list|,
name|Metadata
name|m
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
name|expected
parameter_list|,
name|String
name|message
parameter_list|)
block|{
try|try
block|{
name|getXML
argument_list|(
name|path
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|//if this is a throwable wrapped in a TikaException, use the cause
if|if
condition|(
name|t
operator|instanceof
name|TikaException
operator|&&
name|t
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|t
operator|=
name|t
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|t
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|expected
argument_list|)
operator|)
condition|)
block|{
name|fail
argument_list|(
name|t
operator|.
name|getClass
argument_list|()
operator|+
literal|" is not assignable from "
operator|+
name|expected
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|message
argument_list|,
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|assertMockParser
parameter_list|(
name|Metadata
name|m
parameter_list|)
block|{
name|String
index|[]
name|parsers
init|=
name|m
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
decl_stmt|;
comment|//make sure that it was actually parsed by mock.
name|boolean
name|parsedByMock
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|parser
range|:
name|parsers
control|)
block|{
if|if
condition|(
name|parser
operator|.
name|equals
argument_list|(
literal|"org.apache.tika.parser.mock.MockParser"
argument_list|)
condition|)
block|{
name|parsedByMock
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"mock parser should have been called"
argument_list|,
name|parsedByMock
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

