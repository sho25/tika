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
name|mp4
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|XMPDM
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_comment
comment|/**  * Test case for parsing mp4 files.  *   * TODO Work out why this test passes in Eclipse, but fails from Maven  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MP4ParserTest
extends|extends
name|TestCase
block|{
comment|/**      * Test that we can extract information from      *  a M4A MP4 Audio file      */
specifier|public
name|void
name|testMP4ParsingAudio
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
name|InputStream
name|stream
init|=
name|MP4ParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testMP4.m4a"
argument_list|)
decl_stmt|;
try|try
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
operator|new
name|ParseContext
argument_list|()
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
comment|// Check core properties
name|assertEquals
argument_list|(
literal|"audio/mp4"
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
literal|"Test Title"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test Artist"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2012-01-28T18:39:18Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2012-01-28T18:40:25Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check the textual contents
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test Title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test Artist"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test Album"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"2008"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test Comment"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test Genre"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check XMPDM-typed audio properties
name|assertEquals
argument_list|(
literal|"Test Album"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|ALBUM
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test Artist"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|ARTIST
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test Composer"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|COMPOSER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2008"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|RELEASE_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test Genre"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|GENRE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Test Comments"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|LOG_COMMENT
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|TRACK_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"44100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|AUDIO_SAMPLE_RATE
argument_list|)
argument_list|)
expr_stmt|;
comment|//assertEquals("Stereo", metadata.get(XMPDM.AUDIO_CHANNEL_TYPE)); // TODO Extract
name|assertEquals
argument_list|(
literal|"M4A"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPDM
operator|.
name|AUDIO_COMPRESSOR
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check again by file, rather than stream
name|TikaInputStream
name|tstream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|MP4ParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testMP4.m4a"
argument_list|)
argument_list|)
decl_stmt|;
name|tstream
operator|.
name|getFile
argument_list|()
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|tstream
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
block|}
finally|finally
block|{
name|tstream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// TODO Test a MP4 Video file
comment|// TODO Test an old QuickTime Video File
block|}
end_class

end_unit

