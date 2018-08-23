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
name|fork
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|TikaTest
operator|.
name|assertContains
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|TikaTest
operator|.
name|debug
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
name|assertNotNull
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileFilter
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
name|NotSerializableException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|commons
operator|.
name|io
operator|.
name|filefilter
operator|.
name|TrueFileFilter
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
name|MultiThreadedTikaTest
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
name|Tika
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
name|detect
operator|.
name|Detector
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
name|fork
operator|.
name|ForkParser
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
name|EmptyParser
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
name|RecursiveParserWrapper
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
name|sax
operator|.
name|BodyContentHandler
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Test that the ForkParser correctly behaves when  *  wired in to the regular Parsers and their test data  */
end_comment

begin_class
specifier|public
class|class
name|ForkParserIntegrationTest
extends|extends
name|MultiThreadedTikaTest
block|{
specifier|private
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
comment|// TODO Use TikaConfig instead, when it works
comment|/**      * Simple text parsing      */
annotation|@
name|Test
specifier|public
name|void
name|testForkedTextParsing
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|tika
operator|.
name|getParser
argument_list|()
argument_list|)
init|)
block|{
name|ContentHandler
name|output
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testTXT.txt"
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|output
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|output
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Test d'indexation"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"http://www.apache.org"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This error has a message and an equals() implementation as to be able       * to match it against the serialized version of itself.      */
specifier|static
class|class
name|AnError
extends|extends
name|Error
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6197267350768803348L
decl_stmt|;
specifier|private
name|String
name|message
decl_stmt|;
name|AnError
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|AnError
name|anError
init|=
operator|(
name|AnError
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|message
operator|.
name|equals
argument_list|(
name|anError
operator|.
name|message
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|message
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|/**      * This error isn't serializable on the server, so can't be sent back      *  to the Fork Client once it has occured      */
specifier|static
class|class
name|WontBeSerializedError
extends|extends
name|RuntimeException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
name|WontBeSerializedError
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeObject
parameter_list|(
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
name|out
parameter_list|)
block|{
name|RuntimeException
name|e
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"Bang!"
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|ste
range|:
name|e
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
if|if
condition|(
name|ste
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
name|ForkParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
specifier|static
class|class
name|BrokenParser
implements|implements
name|Parser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|995871497930817839L
decl_stmt|;
specifier|public
name|Error
name|err
init|=
operator|new
name|AnError
argument_list|(
literal|"Simulated fail"
argument_list|)
decl_stmt|;
specifier|public
name|RuntimeException
name|re
init|=
literal|null
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
if|if
condition|(
name|re
operator|!=
literal|null
condition|)
throw|throw
name|re
throw|;
throw|throw
name|err
throw|;
block|}
block|}
comment|/**      * TIKA-831 Parsers throwing errors should be caught and      *  properly reported      */
annotation|@
name|Test
specifier|public
name|void
name|testParsingErrorInForkedParserShouldBeReported
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokenParser
name|brokenParser
init|=
operator|new
name|BrokenParser
argument_list|()
decl_stmt|;
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParser
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|brokenParser
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testTXT.txt"
argument_list|)
decl_stmt|;
comment|// With a serializable error, we'll get that back
try|try
block|{
name|ContentHandler
name|output
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|output
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected TikaException caused by Error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|brokenParser
operator|.
name|err
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// With a non serializable one, we'll get something else
comment|// TODO Fix this test
name|brokenParser
operator|=
operator|new
name|BrokenParser
argument_list|()
expr_stmt|;
name|brokenParser
operator|.
name|re
operator|=
operator|new
name|WontBeSerializedError
argument_list|(
literal|"Can't Serialize"
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|ForkParser
argument_list|(
name|ForkParser
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|brokenParser
argument_list|)
expr_stmt|;
comment|//        try {
comment|//           ContentHandler output = new BodyContentHandler();
comment|//           ParseContext context = new ParseContext();
comment|//           parser.parse(stream, output, new Metadata(), context);
comment|//           fail("Expected TikaException caused by Error");
comment|//       } catch (TikaException e) {
comment|//           assertEquals(TikaException.class, e.getCause().getClass());
comment|//           assertEquals("Bang!", e.getCause().getMessage());
comment|//       }
block|}
comment|/**      * If we supply a non serializable object on the ParseContext,      *  check we get a helpful exception back      */
annotation|@
name|Test
specifier|public
name|void
name|testParserHandlingOfNonSerializable
parameter_list|()
throws|throws
name|Exception
block|{
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|tika
operator|.
name|getParser
argument_list|()
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Detector
operator|.
name|class
argument_list|,
operator|new
name|Detector
argument_list|()
block|{
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|ContentHandler
name|output
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testTXT.txt"
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|output
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have blown up with a non serializable ParseContext"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
comment|// Check the right details
name|assertNotNull
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NotSerializableException
operator|.
name|class
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unable to serialize ParseContext to pass to the Forked Parser"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * TIKA-832      */
annotation|@
name|Test
specifier|public
name|void
name|testAttachingADebuggerOnTheForkedParserShouldWork
parameter_list|()
throws|throws
name|Exception
block|{
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|tika
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|tika
operator|.
name|getParser
argument_list|()
argument_list|)
decl_stmt|;
name|parser
operator|.
name|setJavaCommand
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"java"
argument_list|,
literal|"-Xmx32m"
argument_list|,
literal|"-Xdebug"
argument_list|,
literal|"-Xrunjdwp:transport=dt_socket,address=54321,server=y,suspend=n"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|ContentHandler
name|body
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testTXT.txt"
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|body
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|body
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Test d'indexation"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"http://www.apache.org"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * TIKA-808 - Ensure that parsing of our test PDFs work under      * the Fork Parser, to ensure that complex parsing behaves      */
annotation|@
name|Test
specifier|public
name|void
name|testForkedPDFParsing
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|tika
operator|.
name|getParser
argument_list|()
argument_list|)
init|)
block|{
name|ContentHandler
name|output
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPDF.pdf"
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
operator|new
name|EmptyParser
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|output
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|output
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Apache Tika"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Tika - Content Analysis Toolkit"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"incubator"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Apache Software Foundation"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testForkedPackageParsing
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|tika
operator|.
name|getParser
argument_list|()
argument_list|)
init|)
block|{
name|ContentHandler
name|output
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/moby.zip"
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|output
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Moby Dick"
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"use for development/one off testing.  This is a beast and takes enormous resources and time"
argument_list|)
specifier|public
name|void
name|smokeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|tika
operator|.
name|getParser
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numThreads
init|=
literal|5
decl_stmt|;
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|ForkParserIntegrationTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|wrapper
argument_list|)
decl_stmt|;
name|parser
operator|.
name|setServerPulseMillis
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setServerParseTimeoutMillis
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setPoolSize
argument_list|(
name|numThreads
argument_list|)
expr_stmt|;
name|ParseContext
index|[]
name|parseContexts
init|=
operator|new
name|ParseContext
index|[
name|numThreads
index|]
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|parseContexts
index|[
name|i
index|]
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|super
operator|.
name|testMultiThreaded
argument_list|(
name|parser
argument_list|,
name|parseContexts
argument_list|,
name|numThreads
argument_list|,
literal|5
argument_list|,
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
if|if
condition|(
name|pathname
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|contains
argument_list|(
literal|"mock"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
comment|/*                             if (pathname.getName().contains("11_hang.rar") ||                                     pathname.getName().contains("radar_profiles_2009.mat") ||                                     pathname.getAbsolutePath().contains("mock")) {                                 //return false;                             }                             return true;*/
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

