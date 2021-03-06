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
name|sax
package|;
end_package

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
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|mock
operator|.
name|MockParser
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

begin_comment
comment|/**  * Test cases for the {@link BodyContentHandler} class.  */
end_comment

begin_class
specifier|public
class|class
name|BodyContentHandlerTest
extends|extends
name|TikaTest
block|{
comment|/**      * Test that the conversion to an {@link OutputStream} doesn't leave      * characters unflushed in an internal buffer.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-179">TIKA-179</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testOutputStream
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|(
name|buffer
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
literal|"Test text"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test text\n"
argument_list|,
name|buffer
operator|.
name|toString
argument_list|(
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLimit
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TIKA-2668 - java 11-ea
name|Parser
name|p
init|=
operator|new
name|MockParser
argument_list|()
decl_stmt|;
name|WriteOutContentHandler
name|handler
init|=
operator|new
name|WriteOutContentHandler
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|Parser
index|[]
name|parsers
init|=
operator|new
name|Parser
index|[
literal|1
index|]
decl_stmt|;
name|parsers
index|[
literal|0
index|]
operator|=
name|p
expr_stmt|;
name|Parser
name|autoDetectParser
init|=
operator|new
name|AutoDetectParser
argument_list|(
name|parsers
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/example.xml"
argument_list|)
init|)
block|{
name|autoDetectParser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|tryToFindIllegalStateException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"hello wo"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|tryToFindIllegalStateException
parameter_list|(
name|Throwable
name|e
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|e
operator|instanceof
name|IllegalStateException
condition|)
block|{
throw|throw
operator|(
name|Exception
operator|)
name|e
throw|;
block|}
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tryToFindIllegalStateException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

