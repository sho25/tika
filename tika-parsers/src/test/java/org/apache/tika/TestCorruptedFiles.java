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
package|;
end_package

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
name|ByteArrayInputStream
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
name|OutputStream
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
name|StandardOpenOption
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Callable
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
name|ExecutorCompletionService
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
name|ExecutorService
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
name|Executors
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
name|Future
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
name|TimeUnit
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
name|extractor
operator|.
name|EmbeddedDocumentExtractor
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
name|io
operator|.
name|TikaInputStream
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * This can be used to test parsers against corrupted/fuzzed files.  * This aims to ignore SAXExceptions, TikaException and IOException.  * However, if there is another exception, or parsing takes longer than  * {@link #MAX_ALLOWABLE_TIME_MILLIS}, then the tmp file that triggered  * the problem is reported, the seed is reported, and the stacktrace is printed out.  *<p>  * This should not be part of the regular unit tests because it will likely  * unearth a large number of bugs.  *</p>  */
end_comment

begin_class
annotation|@
name|Ignore
specifier|public
class|class
name|TestCorruptedFiles
extends|extends
name|TikaTest
block|{
comment|//I did the per_10000, because I wasn't able to reproduce
comment|//with the seed if I used Random's nextFloat()...may have been
comment|//user error....
comment|/**      *  per 10,000 bytes, how many should be corrupted      */
specifier|private
specifier|static
specifier|final
name|int
name|PER_10000_CORRUPTED
init|=
literal|10
decl_stmt|;
comment|/**      * per 10,000 iterations, how many should be truncated instead of corrupted      */
specifier|private
specifier|static
specifier|final
name|double
name|PER_10000_TRUNCATED
init|=
literal|0
decl_stmt|;
comment|/**      * how much time to allow for the parse      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_ALLOWABLE_TIME_MILLIS
init|=
literal|60000
decl_stmt|;
comment|/**      * how many times to corrupt and then try to parse the file      */
specifier|private
specifier|static
specifier|final
name|int
name|NUM_ITERATIONS
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|HANDLE_EMBEDDED_DOCS_INDIVIDUALLY
init|=
literal|true
decl_stmt|;
specifier|private
specifier|static
name|Random
name|randomSeedGenerator
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|Path
name|CORRUPTED
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|FAILED
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|CORRUPTED
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|"tika-corrupted-"
argument_list|,
literal|".tmp"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|FAILED
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|CORRUPTED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TRIGGERING FILE:"
operator|+
name|CORRUPTED
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingle
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fileName
init|=
literal|"testEXCEL_embeddedPDF_windows.xls"
decl_stmt|;
name|debug
argument_list|(
name|getRecursiveMetadata
argument_list|(
literal|"testEXCEL_embeddedPDF_windows.xls"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|seed
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|seed
operator|=
name|randomSeedGenerator
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|FAILED
operator|=
literal|true
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
name|testSingleFile
argument_list|(
name|getBytes
argument_list|(
name|fileName
argument_list|)
argument_list|,
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
expr_stmt|;
name|FAILED
operator|=
literal|false
expr_stmt|;
block|}
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
name|fail
argument_list|(
literal|"error "
operator|+
name|fileName
operator|+
literal|" seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmbeddedOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fileName
init|=
literal|"testEXCEL_embeddedPDF_windows.xls"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|embedded
init|=
name|extract
argument_list|(
name|getBytes
argument_list|(
name|fileName
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|seed
init|=
literal|0
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|e
range|:
name|embedded
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|seed
operator|=
name|randomSeedGenerator
operator|.
name|nextLong
argument_list|()
expr_stmt|;
try|try
block|{
name|FAILED
operator|=
literal|true
expr_stmt|;
name|testSingleFile
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
expr_stmt|;
name|FAILED
operator|=
literal|false
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
name|fail
argument_list|(
literal|"error fileName "
operator|+
name|fileName
operator|+
literal|" "
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|" seed: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|reproduce
parameter_list|()
throws|throws
name|Throwable
block|{
name|long
name|seed
init|=
operator|-
literal|3351614222367486714L
decl_stmt|;
name|String
name|fileName
init|=
literal|"testEXCEL_embeddedPDF_windows.xls"
decl_stmt|;
try|try
block|{
name|FAILED
operator|=
literal|true
expr_stmt|;
name|testSingleFile
argument_list|(
name|getBytes
argument_list|(
name|fileName
argument_list|)
argument_list|,
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
expr_stmt|;
name|FAILED
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{          }
block|}
specifier|public
name|void
name|testSingleFile
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|Random
name|random
parameter_list|)
throws|throws
name|Throwable
block|{
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|ExecutorCompletionService
name|executorCompletionService
init|=
operator|new
name|ExecutorCompletionService
argument_list|(
name|executorService
argument_list|)
decl_stmt|;
name|executorCompletionService
operator|.
name|submit
argument_list|(
operator|new
name|ParseTask
argument_list|(
name|bytes
argument_list|,
name|random
argument_list|)
argument_list|)
expr_stmt|;
name|Future
argument_list|<
name|Boolean
argument_list|>
name|future
init|=
name|executorCompletionService
operator|.
name|poll
argument_list|(
name|MAX_ALLOWABLE_TIME_MILLIS
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|future
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"timed out"
argument_list|)
throw|;
block|}
comment|//if the exception isn't caught, it will be thrown here
name|Boolean
name|result
init|=
name|future
operator|.
name|get
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"timed out"
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|ParseTask
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
block|{
specifier|private
name|byte
index|[]
name|corrupted
init|=
literal|null
decl_stmt|;
name|ParseTask
parameter_list|(
name|byte
index|[]
name|original
parameter_list|,
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|corrupted
operator|=
name|corrupt
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|original
argument_list|)
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|CORRUPTED
argument_list|)
expr_stmt|;
name|OutputStream
name|os
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|CORRUPTED
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|corrupted
argument_list|)
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
try|try
block|{
name|p
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|corrupted
argument_list|)
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
decl||
name|TikaException
decl||
name|IOException
name|e
parameter_list|)
block|{              }
return|return
literal|true
return|;
block|}
block|}
specifier|private
name|byte
index|[]
name|corrupt
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|bos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|bytes
return|;
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
operator|<=
name|PER_10000_TRUNCATED
condition|)
block|{
name|int
name|truncatedLength
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|bytes
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|corrupted
init|=
operator|new
name|byte
index|[
name|truncatedLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|corrupted
argument_list|,
literal|0
argument_list|,
name|truncatedLength
argument_list|)
expr_stmt|;
return|return
name|corrupted
return|;
block|}
else|else
block|{
name|byte
index|[]
name|corrupted
init|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|length
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|c
init|=
operator|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
operator|<
name|PER_10000_CORRUPTED
operator|)
condition|?
operator|(
name|byte
operator|)
name|random
operator|.
name|nextInt
argument_list|(
literal|255
argument_list|)
else|:
name|bytes
index|[
name|i
index|]
decl_stmt|;
name|corrupted
index|[
name|i
index|]
operator|=
name|c
expr_stmt|;
block|}
return|return
name|corrupted
return|;
block|}
block|}
specifier|private
name|byte
index|[]
name|getBytes
parameter_list|(
name|String
name|testFile
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|testFile
argument_list|)
argument_list|,
name|bos
argument_list|)
expr_stmt|;
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|extract
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|parseContext
operator|.
name|set
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|,
operator|new
name|MyEmbeddedDocumentExtractor
argument_list|(
name|map
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|bytes
argument_list|)
init|)
block|{
name|p
operator|.
name|parse
argument_list|(
name|is
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|private
class|class
name|MyEmbeddedDocumentExtractor
implements|implements
name|EmbeddedDocumentExtractor
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|zout
decl_stmt|;
specifier|private
name|int
name|cnt
init|=
literal|0
decl_stmt|;
name|MyEmbeddedDocumentExtractor
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|zout
parameter_list|)
block|{
name|this
operator|.
name|zout
operator|=
name|zout
expr_stmt|;
block|}
specifier|public
name|boolean
name|shouldParseEmbedded
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CONTENT TYPE: "
operator|+
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"image/x-emf"
operator|.
name|equals
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|parseEmbedded
parameter_list|(
name|InputStream
name|inputStream
parameter_list|,
name|ContentHandler
name|contentHandler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|boolean
name|b
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|inputStream
argument_list|,
name|bos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|zout
operator|.
name|put
argument_list|(
literal|"embedded-"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|cnt
operator|++
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

