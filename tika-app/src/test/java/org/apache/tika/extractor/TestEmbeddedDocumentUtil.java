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
name|extractor
package|;
end_package

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
name|DigestingAutoDetectParserFactory
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
name|config
operator|.
name|TikaConfig
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
name|EmbeddedDocumentUtil
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
name|BasicContentHandlerFactory
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

begin_class
specifier|public
class|class
name|TestEmbeddedDocumentUtil
block|{
comment|//TODO -- figure out how to mock this into tika-core
annotation|@
name|Test
specifier|public
name|void
name|testSimple
parameter_list|()
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
name|parseContext
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|Parser
name|txtParser
init|=
name|EmbeddedDocumentUtil
operator|.
name|tryToFindExistingLeafParser
argument_list|(
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|csv
operator|.
name|TextAndCSVParser
operator|.
name|class
argument_list|,
name|parseContext
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|txtParser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|csv
operator|.
name|TextAndCSVParser
operator|.
name|class
argument_list|,
name|txtParser
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDoublyDecorated
parameter_list|()
block|{
name|Parser
name|d
init|=
operator|new
name|DigestingAutoDetectParserFactory
argument_list|()
operator|.
name|getParser
argument_list|(
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
decl_stmt|;
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|d
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
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parseContext
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|wrapper
argument_list|)
expr_stmt|;
name|Parser
name|txtParser
init|=
name|EmbeddedDocumentUtil
operator|.
name|tryToFindExistingLeafParser
argument_list|(
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|csv
operator|.
name|TextAndCSVParser
operator|.
name|class
argument_list|,
name|parseContext
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|txtParser
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|csv
operator|.
name|TextAndCSVParser
operator|.
name|class
argument_list|,
name|txtParser
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

