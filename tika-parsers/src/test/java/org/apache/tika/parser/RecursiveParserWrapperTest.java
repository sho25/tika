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
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|assertNull
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
name|io
operator|.
name|InputStream
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
name|List
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
name|parser
operator|.
name|utils
operator|.
name|CommonsDigester
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
name|BasicContentHandlerFactory
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
name|ContentHandlerFactory
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
name|ParserUtils
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_class
specifier|public
class|class
name|RecursiveParserWrapperTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testBasicXML
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getMetadata
argument_list|(
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|XML
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Metadata
name|container
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|container
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
comment|//not much differentiates html from xml in this test file
name|assertTrue
argument_list|(
name|content
operator|.
name|indexOf
argument_list|(
literal|"<p class=\"header\" />"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBasicHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getMetadata
argument_list|(
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|HTML
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Metadata
name|container
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|container
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
comment|//not much differentiates html from xml in this test file
name|assertTrue
argument_list|(
name|content
operator|.
name|indexOf
argument_list|(
literal|"<p class=\"header\"></p>"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBasicText
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getMetadata
argument_list|(
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Metadata
name|container
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|container
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|indexOf
argument_list|(
literal|"<p "
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|indexOf
argument_list|(
literal|"embed_0"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIgnoreContent
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getMetadata
argument_list|(
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|IGNORE
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Metadata
name|container
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|container
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCharLimit
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
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|Parser
name|wrapped
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|wrapped
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|,
literal|60
argument_list|)
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|RecursiveParserWrapperTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test_recursive_embedded.docx"
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|wrapper
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|wlr
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Metadata
name|m
range|:
name|list
control|)
block|{
name|String
name|limitReached
init|=
name|m
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|WRITE_LIMIT_REACHED
argument_list|)
decl_stmt|;
if|if
condition|(
name|limitReached
operator|!=
literal|null
operator|&&
name|limitReached
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
name|wlr
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|wlr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMaxEmbedded
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|maxEmbedded
init|=
literal|4
decl_stmt|;
name|int
name|totalNoLimit
init|=
literal|12
decl_stmt|;
comment|//including outer container file
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|limitReached
init|=
literal|null
decl_stmt|;
name|Parser
name|wrapped
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|wrapped
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|RecursiveParserWrapperTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test_recursive_embedded.docx"
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|wrapper
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
comment|//test default
name|assertEquals
argument_list|(
name|totalNoLimit
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|limitReached
operator|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_LIMIT_REACHED
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|limitReached
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|reset
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//test setting value
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|stream
operator|=
name|RecursiveParserWrapperTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test_recursive_embedded.docx"
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setMaxEmbeddedResources
argument_list|(
name|maxEmbedded
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|list
operator|=
name|wrapper
operator|.
name|getMetadata
argument_list|()
expr_stmt|;
comment|//add 1 for outer container file
name|assertEquals
argument_list|(
name|maxEmbedded
operator|+
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|limitReached
operator|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_LIMIT_REACHED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|limitReached
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|reset
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//test setting value< 0
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|stream
operator|=
name|RecursiveParserWrapperTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test_recursive_embedded.docx"
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|setMaxEmbeddedResources
argument_list|(
operator|-
literal|2
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|totalNoLimit
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|limitReached
operator|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_LIMIT_REACHED
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|limitReached
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmbeddedResourcePath
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|targets
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed2.zip"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed2.zip/embed3.zip"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed2.zip/embed3.zip/embed4.zip"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed2.zip/embed3.zip/embed4.zip/embed4.txt"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed2.zip/embed3.zip/embed3.txt"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed2.zip/embed2a.txt"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed2.zip/embed2b.txt"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed1b.txt"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/embed1.zip/embed1a.txt"
argument_list|)
expr_stmt|;
name|targets
operator|.
name|add
argument_list|(
literal|"/image1.emf"
argument_list|)
expr_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test_recursive_embedded.docx"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getMetadata
argument_list|(
name|metadata
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|XML
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Metadata
name|container
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|container
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|indexOf
argument_list|(
literal|"<p class=\"header\" />"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Metadata
name|m
range|:
name|list
control|)
block|{
name|String
name|path
init|=
name|m
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|EMBEDDED_RESOURCE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|seen
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|targets
argument_list|,
name|seen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmbeddedNPE
parameter_list|()
throws|throws
name|Exception
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
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test_recursive_embedded_npe.docx"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getMetadata
argument_list|(
name|metadata
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|//default behavior (user doesn't specify whether or not to catch embedded exceptions
comment|//is to catch the exception
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|mockNPEMetadata
init|=
name|list
operator|.
name|get
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"java.lang.NullPointerException"
argument_list|,
name|mockNPEMetadata
operator|.
name|get
argument_list|(
name|ParserUtils
operator|.
name|EMBEDDED_EXCEPTION
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test_recursive_embedded_npe.docx"
argument_list|)
expr_stmt|;
name|list
operator|=
name|getMetadata
argument_list|(
name|metadata
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//Composite parser swallows caught TikaExceptions, IOExceptions and SAXExceptions
comment|//and just doesn't bother to report that there was an exception.
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPrimaryExcWEmbedded
parameter_list|()
throws|throws
name|Exception
block|{
comment|//if embedded content is handled and then
comment|//the parser hits an exception in the container document,
comment|//that the first element of the returned list is the container document
comment|//and the second is the embedded content
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"embedded_then_npe.xml"
argument_list|)
expr_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|Parser
name|wrapped
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|wrapped
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"/test-documents/mock/embedded_then_npe.xml"
decl_stmt|;
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
name|boolean
name|npe
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|RecursiveParserWrapperTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
condition|)
block|{
name|npe
operator|=
literal|true
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"npe"
argument_list|,
name|npe
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|wrapper
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|outerMetadata
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Metadata
name|embeddedMetadata
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"main_content"
argument_list|,
name|outerMetadata
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"embedded_then_npe.xml"
argument_list|,
name|outerMetadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nikolai Lobachevsky"
argument_list|,
name|outerMetadata
operator|.
name|get
argument_list|(
literal|"author"
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"some_embedded_content"
argument_list|,
name|embeddedMetadata
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"embed1.xml"
argument_list|,
name|embeddedMetadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"embeddedAuthor"
argument_list|,
name|embeddedMetadata
operator|.
name|get
argument_list|(
literal|"author"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDigesters
parameter_list|()
throws|throws
name|Exception
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
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test_recursive_embedded.docx"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getMetadata
argument_list|(
name|metadata
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|true
argument_list|,
operator|new
name|CommonsDigester
argument_list|(
literal|100000
argument_list|,
name|CommonsDigester
operator|.
name|DigestAlgorithm
operator|.
name|MD5
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Metadata
name|m0
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Metadata
name|m6
init|=
name|list
operator|.
name|get
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|String
name|md5Key
init|=
literal|"X-TIKA:digest:MD5"
decl_stmt|;
name|assertEquals
argument_list|(
literal|"59f626e09a8c16ab6dbc2800c685f772"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|md5Key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ccdf3882e7e4c2454e28884db9b0a54d"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|get
argument_list|(
name|md5Key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a869bf6432ebd14e19fc79416274e0c9"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|get
argument_list|(
name|md5Key
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|Metadata
argument_list|>
name|getMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|ContentHandlerFactory
name|contentHandlerFactory
parameter_list|,
name|boolean
name|catchEmbeddedExceptions
parameter_list|,
name|DigestingParser
operator|.
name|Digester
name|digester
parameter_list|)
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
name|Parser
name|wrapped
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
if|if
condition|(
name|digester
operator|!=
literal|null
condition|)
block|{
name|wrapped
operator|=
operator|new
name|DigestingParser
argument_list|(
name|wrapped
argument_list|,
name|digester
argument_list|)
expr_stmt|;
block|}
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|wrapped
argument_list|,
name|contentHandlerFactory
argument_list|,
name|catchEmbeddedExceptions
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
literal|"/test-documents/test_recursive_embedded.docx"
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
literal|"/test-documents/"
operator|+
name|path
expr_stmt|;
block|}
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|RecursiveParserWrapperTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|path
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapper
operator|.
name|getMetadata
argument_list|()
return|;
block|}
specifier|private
name|List
argument_list|<
name|Metadata
argument_list|>
name|getMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|ContentHandlerFactory
name|contentHandlerFactory
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getMetadata
argument_list|(
name|metadata
argument_list|,
name|contentHandlerFactory
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

