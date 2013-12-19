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
name|mime
operator|.
name|MediaTypeRegistry
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

begin_class
specifier|public
class|class
name|CompositeParserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testFindDuplicateParsers
parameter_list|()
block|{
name|Parser
name|a
init|=
operator|new
name|EmptyParser
argument_list|()
block|{
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
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Parser
name|b
init|=
operator|new
name|EmptyParser
argument_list|()
block|{
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
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Parser
name|c
init|=
operator|new
name|EmptyParser
argument_list|()
block|{
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
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|CompositeParser
name|composite
init|=
operator|new
name|CompositeParser
argument_list|(
name|MediaTypeRegistry
operator|.
name|getDefaultRegistry
argument_list|()
argument_list|,
name|a
argument_list|,
name|b
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|MediaType
argument_list|,
name|List
argument_list|<
name|Parser
argument_list|>
argument_list|>
name|duplicates
init|=
name|composite
operator|.
name|findDuplicateParsers
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duplicates
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
name|duplicates
operator|.
name|get
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parsers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|parsers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
argument_list|,
name|parsers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
argument_list|,
name|parsers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultParser
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|CompositeParser
name|parser
init|=
operator|(
name|CompositeParser
operator|)
name|config
operator|.
name|getParser
argument_list|()
decl_stmt|;
comment|// Check it has the full registry
name|assertEquals
argument_list|(
name|config
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|parser
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMimeTypeAliases
parameter_list|()
throws|throws
name|Exception
block|{
name|MediaType
name|bmpCanonical
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-ms-bmp"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bmpCanonicalMetadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|bmpCanonicalMetadata
operator|.
name|put
argument_list|(
literal|"BMP"
argument_list|,
literal|"True"
argument_list|)
expr_stmt|;
name|bmpCanonicalMetadata
operator|.
name|put
argument_list|(
literal|"Canonical"
argument_list|,
literal|"True"
argument_list|)
expr_stmt|;
name|Parser
name|bmpCanonicalParser
init|=
operator|new
name|DummyParser
argument_list|(
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
name|bmpCanonical
argument_list|)
argument_list|)
argument_list|,
name|bmpCanonicalMetadata
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|MediaType
name|bmpAlias
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"bmp"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bmpAliasMetadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|bmpAliasMetadata
operator|.
name|put
argument_list|(
literal|"BMP"
argument_list|,
literal|"True"
argument_list|)
expr_stmt|;
name|bmpAliasMetadata
operator|.
name|put
argument_list|(
literal|"Alias"
argument_list|,
literal|"True"
argument_list|)
expr_stmt|;
name|Parser
name|bmpAliasParser
init|=
operator|new
name|DummyParser
argument_list|(
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
name|bmpAlias
argument_list|)
argument_list|)
argument_list|,
name|bmpAliasMetadata
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TikaConfig
name|config
init|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|CompositeParser
name|canonical
init|=
operator|new
name|CompositeParser
argument_list|(
name|config
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|bmpCanonicalParser
argument_list|)
decl_stmt|;
name|CompositeParser
name|alias
init|=
operator|new
name|CompositeParser
argument_list|(
name|config
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|bmpAliasParser
argument_list|)
decl_stmt|;
name|CompositeParser
name|both
init|=
operator|new
name|CompositeParser
argument_list|(
name|config
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|bmpCanonicalParser
argument_list|,
name|bmpAliasParser
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
decl_stmt|;
comment|// Canonical and Canonical
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|bmpCanonical
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|canonical
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"BMP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Canonical"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Alias and Alias
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|bmpAlias
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|alias
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"BMP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Alias"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Alias type and Canonical parser
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|bmpAlias
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|canonical
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"BMP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Canonical"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Canonical type and Alias parser
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|bmpCanonical
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|alias
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"BMP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Alias"
argument_list|)
argument_list|)
expr_stmt|;
comment|// And when both are there, will go for the last one
comment|//  to be registered (which is the alias one)
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|bmpCanonical
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|both
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"BMP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"True"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Alias"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

