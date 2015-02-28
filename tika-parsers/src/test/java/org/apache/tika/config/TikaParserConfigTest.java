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
name|config
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
name|assertNotContained
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
name|net
operator|.
name|URL
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
name|CompositeParser
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
name|DefaultParser
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
name|ParserDecorator
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
name|executable
operator|.
name|ExecutableParser
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Junit test class for {@link TikaConfig}, which cover things  *  that {@link TikaConfigTest} can't do due to a need for the  *  full set of parsers  */
end_comment

begin_class
specifier|public
class|class
name|TikaParserConfigTest
block|{
specifier|protected
specifier|static
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
name|TikaConfig
name|getConfig
parameter_list|(
name|String
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
name|TikaConfig
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tika.config"
argument_list|,
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|TikaConfig
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|resetConfig
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tika.config"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMimeExcludeInclude
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|getConfig
argument_list|(
literal|"TIKA-1558-blacklist.xml"
argument_list|)
decl_stmt|;
name|Parser
name|parser
init|=
name|config
operator|.
name|getParser
argument_list|()
decl_stmt|;
name|MediaType
name|PDF
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"pdf"
argument_list|)
decl_stmt|;
name|MediaType
name|JPEG
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"jpeg"
argument_list|)
decl_stmt|;
comment|// Has two parsers
name|assertEquals
argument_list|(
name|CompositeParser
operator|.
name|class
argument_list|,
name|parser
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|CompositeParser
name|cParser
init|=
operator|(
name|CompositeParser
operator|)
name|parser
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cParser
operator|.
name|getAllComponentParsers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Both are decorated
name|assertTrue
argument_list|(
name|cParser
operator|.
name|getAllComponentParsers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|ParserDecorator
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cParser
operator|.
name|getAllComponentParsers
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|ParserDecorator
argument_list|)
expr_stmt|;
name|ParserDecorator
name|p0
init|=
operator|(
name|ParserDecorator
operator|)
name|cParser
operator|.
name|getAllComponentParsers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ParserDecorator
name|p1
init|=
operator|(
name|ParserDecorator
operator|)
name|cParser
operator|.
name|getAllComponentParsers
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// DefaultParser will be wrapped with excludes
name|assertEquals
argument_list|(
name|DefaultParser
operator|.
name|class
argument_list|,
name|p0
operator|.
name|getWrappedParser
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
name|PDF
argument_list|,
name|p0
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|PDF
argument_list|,
name|p0
operator|.
name|getWrappedParser
argument_list|()
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
name|JPEG
argument_list|,
name|p0
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|JPEG
argument_list|,
name|p0
operator|.
name|getWrappedParser
argument_list|()
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|// Will have an empty parser for PDF
name|assertEquals
argument_list|(
name|EmptyParser
operator|.
name|class
argument_list|,
name|p1
operator|.
name|getWrappedParser
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|p1
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|PDF
argument_list|,
name|p1
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
name|PDF
argument_list|,
name|p1
operator|.
name|getWrappedParser
argument_list|()
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParserExcludeFromDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|getConfig
argument_list|(
literal|"TIKA-1558-blacklist.xml"
argument_list|)
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
name|MediaType
name|PE_EXE
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-msdownload"
argument_list|)
decl_stmt|;
name|MediaType
name|ELF
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-elf"
argument_list|)
decl_stmt|;
comment|// Get the DefaultParser from the config
name|ParserDecorator
name|confWrappedParser
init|=
operator|(
name|ParserDecorator
operator|)
name|parser
operator|.
name|getParsers
argument_list|()
operator|.
name|get
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|confWrappedParser
argument_list|)
expr_stmt|;
name|DefaultParser
name|confParser
init|=
operator|(
name|DefaultParser
operator|)
name|confWrappedParser
operator|.
name|getWrappedParser
argument_list|()
decl_stmt|;
comment|// Get a fresh "default" DefaultParser
name|DefaultParser
name|normParser
init|=
operator|new
name|DefaultParser
argument_list|(
name|config
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|)
decl_stmt|;
comment|// The default one will offer the Executable Parser
name|assertContains
argument_list|(
name|PE_EXE
argument_list|,
name|normParser
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|ELF
argument_list|,
name|normParser
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|hasExec
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Parser
name|p
range|:
name|normParser
operator|.
name|getParsers
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|ExecutableParser
condition|)
block|{
name|hasExec
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|hasExec
argument_list|)
expr_stmt|;
comment|// The one from the config won't
comment|// TODO - Finish this
comment|/*         assertNotContained(PE_EXE, confParser.getSupportedTypes(context));         assertNotContained(ELF, confParser.getSupportedTypes(context));                  for (Parser p : confParser.getParsers().values()) {             if (p instanceof ExecutableParser)                 fail("Shouldn't have the Executable Parser from config");         } */
block|}
block|}
end_class

end_unit

