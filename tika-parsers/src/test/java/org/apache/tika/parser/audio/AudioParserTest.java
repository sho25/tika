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
name|audio
package|;
end_package

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
name|Tika
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

begin_class
specifier|public
class|class
name|AudioParserTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testWAV
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/test-documents/testWAV.wav"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|Tika
operator|.
name|parseToString
argument_list|(
name|AudioParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"audio/x-wav"
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
literal|"44100.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"samplerate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"channels"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"16"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"bits"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PCM_SIGNED"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"encoding"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAIFF
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/test-documents/testAIFF.aif"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|Tika
operator|.
name|parseToString
argument_list|(
name|AudioParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"audio/x-aiff"
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
literal|"44100.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"samplerate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"channels"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"16"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"bits"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PCM_SIGNED"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"encoding"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAU
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/test-documents/testAU.au"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|Tika
operator|.
name|parseToString
argument_list|(
name|AudioParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"audio/basic"
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
literal|"44100.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"samplerate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"channels"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"16"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"bits"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PCM_SIGNED"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"encoding"
argument_list|)
argument_list|)
expr_stmt|;
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

