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
name|font
package|;
end_package

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
name|FileUtils
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
name|lang
operator|.
name|reflect
operator|.
name|MethodUtils
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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|OperatingSystemMXBean
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
name|parser
operator|.
name|font
operator|.
name|AdobeFontMetricParser
operator|.
name|*
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Test case for parsing various different font files.  */
end_comment

begin_class
specifier|public
class|class
name|FontParsersTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testAdobeFontMetricParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
comment|// Should auto-detect!
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
try|try
init|(
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|FontParsersTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/test-documents/testAFM.afm"
argument_list|)
argument_list|)
init|)
block|{
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
name|assertEquals
argument_list|(
literal|"application/x-font-adobe-metric"
argument_list|,
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
name|assertEquals
argument_list|(
literal|"TestFullName"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fri Jul 15 17:50:51 2011"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TestFontName"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TestFullName"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_FULL_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TestSymbol"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_FAMILY_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Medium"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_WEIGHT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"001.008"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_VERSION
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Test that the comments got extracted
name|assertContains
argument_list|(
literal|"Comments"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is a comment in a sample file"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"UniqueID 12345"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTTFParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
comment|// Should auto-detect!
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
comment|//Open Sans font is ASL 2.0 according to
comment|//http://www.google.com/fonts/specimen/Open+Sans
comment|//...despite the copyright in the file's metadata.
try|try
init|(
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|FontParsersTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/test-documents/testTrueType3.ttf"
argument_list|)
argument_list|)
init|)
block|{
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
name|assertEquals
argument_list|(
literal|"application/x-font-ttf"
argument_list|,
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
name|assertEquals
argument_list|(
literal|"Open Sans Bold"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2010-12-30T11:04:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2011-05-05T12:37:53Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Open Sans Bold"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Open Sans"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_FAMILY_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bold"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_SUB_FAMILY_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OpenSans-Bold"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_PS_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Digitized"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Copyright"
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Open Sans"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Trademark"
argument_list|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|9
argument_list|)
argument_list|)
expr_stmt|;
comment|// Not extracted
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_FULL_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_WEIGHT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|MET_FONT_VERSION
argument_list|)
argument_list|)
expr_stmt|;
comment|// Currently, the parser doesn't extract any contents
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

