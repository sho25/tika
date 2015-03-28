begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|isatab
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
name|*
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
name|ISArchiveParserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testParseArchive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/test-documents/testISATab_BII-I-1/s_BII-S-1.txt"
decl_stmt|;
name|Parser
name|parser
init|=
operator|new
name|ISArchiveParser
argument_list|(
name|ISArchiveParserTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/test-documents/testISATab_BII-I-1/"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
comment|//Parser parser = new AutoDetectParser();
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|ISArchiveParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// INVESTIGATION
name|assertEquals
argument_list|(
literal|"Invalid Investigation Identifier"
argument_list|,
literal|"BII-I-1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Investigation Identifier"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid Investigation Title"
argument_list|,
literal|"Growth control of the eukaryote cell: a systems biology study in yeast"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Investigation Title"
argument_list|)
argument_list|)
expr_stmt|;
comment|// INVESTIGATION PUBLICATIONS
name|assertEquals
argument_list|(
literal|"Invalid Investigation PubMed ID"
argument_list|,
literal|"17439666"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Investigation PubMed ID"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid Investigation Publication DOI"
argument_list|,
literal|"doi:10.1186/jbiol54"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Investigation Publication DOI"
argument_list|)
argument_list|)
expr_stmt|;
comment|// INVESTIGATION CONTACTS
name|assertEquals
argument_list|(
literal|"Invalid Investigation Person Last Name"
argument_list|,
literal|"Oliver"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Investigation Person Last Name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid Investigation Person First Name"
argument_list|,
literal|"Stephen"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Investigation Person First Name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

