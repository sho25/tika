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
name|multiple
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
name|ByteArrayInputStream
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
name|Collections
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
name|HashSet
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
name|Set
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
name|DummyParser
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
name|ErrorParser
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
name|multiple
operator|.
name|AbstractMultipleParser
operator|.
name|MetadataPolicy
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

begin_class
specifier|public
class|class
name|MultipleParserTest
block|{
comment|/**      * Tests how {@link AbstractMultipleParser} works out which      *  mime types to offer, based on the types of the parsers      */
annotation|@
name|Test
specifier|public
name|void
name|testMimeTypeSupported
parameter_list|()
block|{
comment|// TODO
comment|// Some media types
name|Set
argument_list|<
name|MediaType
argument_list|>
name|onlyOct
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|MediaType
argument_list|>
name|octAndText
init|=
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
name|OCTET_STREAM
argument_list|,
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO One with a subtype
block|}
comment|/**      * Test {@link FallbackParser}      */
annotation|@
name|Test
specifier|public
name|void
name|testFallback
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
name|BodyContentHandler
name|handler
decl_stmt|;
name|Metadata
name|metadata
decl_stmt|;
name|Parser
name|p
decl_stmt|;
name|String
index|[]
name|usedParsers
decl_stmt|;
comment|// Some media types
name|Set
argument_list|<
name|MediaType
argument_list|>
name|onlyOct
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
decl_stmt|;
comment|// Some parsers
name|ErrorParser
name|pFail
init|=
operator|new
name|ErrorParser
argument_list|()
decl_stmt|;
name|DummyParser
name|pContent
init|=
operator|new
name|DummyParser
argument_list|(
name|onlyOct
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
literal|"Fell back!"
argument_list|)
decl_stmt|;
name|EmptyParser
name|pNothing
init|=
operator|new
name|EmptyParser
argument_list|()
decl_stmt|;
comment|// With only one parser defined, works as normal
name|p
operator|=
operator|new
name|FallbackParser
argument_list|(
literal|null
argument_list|,
name|MetadataPolicy
operator|.
name|DISCARD_ALL
argument_list|,
name|pContent
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fell back!"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|usedParsers
operator|=
name|metadata
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|usedParsers
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// With a failing parser, will go to the working one
name|p
operator|=
operator|new
name|FallbackParser
argument_list|(
literal|null
argument_list|,
name|MetadataPolicy
operator|.
name|DISCARD_ALL
argument_list|,
name|pFail
argument_list|,
name|pContent
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fell back!"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|usedParsers
operator|=
name|metadata
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|usedParsers
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// Check we got an exception
name|assertNotNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|ParserUtils
operator|.
name|EMBEDDED_EXCEPTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|ParserUtils
operator|.
name|EMBEDDED_PARSER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|ParserUtils
operator|.
name|EMBEDDED_PARSER
argument_list|)
argument_list|)
expr_stmt|;
comment|// Won't go past a working parser to a second one, stops after one works
name|p
operator|=
operator|new
name|FallbackParser
argument_list|(
literal|null
argument_list|,
name|MetadataPolicy
operator|.
name|DISCARD_ALL
argument_list|,
name|pFail
argument_list|,
name|pContent
argument_list|,
name|pNothing
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fell back!"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|usedParsers
operator|=
name|metadata
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|usedParsers
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// TODO Check merge policies - First vs Discard
block|}
comment|/**      * Test for {@link SupplementingParser}      */
annotation|@
name|Test
specifier|public
name|void
name|testSupplemental
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
name|BodyContentHandler
name|handler
decl_stmt|;
name|Metadata
name|metadata
decl_stmt|;
name|Parser
name|p
decl_stmt|;
name|String
index|[]
name|usedParsers
decl_stmt|;
comment|// Some media types
name|Set
argument_list|<
name|MediaType
argument_list|>
name|onlyOct
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
decl_stmt|;
comment|// Some test metadata
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m1
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|m1
operator|.
name|put
argument_list|(
literal|"T1"
argument_list|,
literal|"Test1"
argument_list|)
expr_stmt|;
name|m1
operator|.
name|put
argument_list|(
literal|"TBoth"
argument_list|,
literal|"Test1"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m2
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|m2
operator|.
name|put
argument_list|(
literal|"T2"
argument_list|,
literal|"Test2"
argument_list|)
expr_stmt|;
name|m2
operator|.
name|put
argument_list|(
literal|"TBoth"
argument_list|,
literal|"Test2"
argument_list|)
expr_stmt|;
comment|// Some parsers
name|ErrorParser
name|pFail
init|=
operator|new
name|ErrorParser
argument_list|()
decl_stmt|;
name|DummyParser
name|pContent1
init|=
operator|new
name|DummyParser
argument_list|(
name|onlyOct
argument_list|,
name|m1
argument_list|,
literal|"Fell back 1!"
argument_list|)
decl_stmt|;
name|DummyParser
name|pContent2
init|=
operator|new
name|DummyParser
argument_list|(
name|onlyOct
argument_list|,
name|m2
argument_list|,
literal|"Fell back 2!"
argument_list|)
decl_stmt|;
name|EmptyParser
name|pNothing
init|=
operator|new
name|EmptyParser
argument_list|()
decl_stmt|;
comment|// Supplemental doesn't support DISCARD
try|try
block|{
operator|new
name|SupplementingParser
argument_list|(
literal|null
argument_list|,
name|MetadataPolicy
operator|.
name|DISCARD_ALL
argument_list|,
operator|new
name|Parser
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Discard shouldn't be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{}
comment|// With only one parser defined, works as normal
name|p
operator|=
operator|new
name|SupplementingParser
argument_list|(
literal|null
argument_list|,
name|MetadataPolicy
operator|.
name|FIRST_WINS
argument_list|,
name|pContent1
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fell back 1!"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"T1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"TBoth"
argument_list|)
argument_list|)
expr_stmt|;
name|usedParsers
operator|=
name|metadata
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|usedParsers
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// Check the First, Last and All policies:
comment|// First Wins
name|p
operator|=
operator|new
name|SupplementingParser
argument_list|(
literal|null
argument_list|,
name|MetadataPolicy
operator|.
name|FIRST_WINS
argument_list|,
name|pFail
argument_list|,
name|pContent1
argument_list|,
name|pContent2
argument_list|,
name|pNothing
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fell back 1!Fell back 2!"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"T1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"T2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"TBoth"
argument_list|)
argument_list|)
expr_stmt|;
name|usedParsers
operator|=
name|metadata
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|usedParsers
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EmptyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
comment|// Last Wins
name|p
operator|=
operator|new
name|SupplementingParser
argument_list|(
literal|null
argument_list|,
name|MetadataPolicy
operator|.
name|LAST_WINS
argument_list|,
name|pFail
argument_list|,
name|pContent1
argument_list|,
name|pContent2
argument_list|,
name|pNothing
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fell back 1!Fell back 2!"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"T1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"T2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"TBoth"
argument_list|)
argument_list|)
expr_stmt|;
name|usedParsers
operator|=
name|metadata
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|usedParsers
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EmptyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
comment|// Merge
name|p
operator|=
operator|new
name|SupplementingParser
argument_list|(
literal|null
argument_list|,
name|MetadataPolicy
operator|.
name|KEEP_ALL
argument_list|,
name|pFail
argument_list|,
name|pContent1
argument_list|,
name|pContent2
argument_list|,
name|pNothing
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fell back 1!Fell back 2!"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"T1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"T2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
literal|"TBoth"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test1"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
literal|"TBoth"
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test2"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
literal|"TBoth"
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|usedParsers
operator|=
name|metadata
operator|.
name|getValues
argument_list|(
literal|"X-Parsed-By"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|usedParsers
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EmptyParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|usedParsers
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
comment|// Check the error details always come through, no matter the policy
comment|// TODO
comment|// Check that each parser gets its own ContentHandler if a factory was given
comment|// TODO
block|}
block|}
end_class

end_unit
