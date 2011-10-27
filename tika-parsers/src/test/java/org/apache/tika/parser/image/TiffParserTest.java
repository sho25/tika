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
name|image
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
name|image
operator|.
name|TiffParser
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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
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
name|Arrays
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

begin_class
specifier|public
class|class
name|TiffParserTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
name|Parser
name|parser
init|=
operator|new
name|TiffParser
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testTIFF
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
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"image/tiff"
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testTIFF.tif"
argument_list|)
decl_stmt|;
name|parser
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Licensed to the Apache Software Foundation (ASF) under one or "
operator|+
literal|"more contributor license agreements.  See the NOTICE file "
operator|+
literal|"distributed with this work for additional information regarding "
operator|+
literal|"copyright ownership."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
comment|// All EXIF/TIFF tags
name|assertEquals
argument_list|(
literal|"Inch"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOLUTION_UNIT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Core EXIF/TIFF tags
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"8"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SAMPLES_PER_PIXEL
argument_list|)
argument_list|)
expr_stmt|;
comment|// Embedded XMP
name|List
argument_list|<
name|String
argument_list|>
name|subject
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"got "
operator|+
name|subject
argument_list|,
name|subject
operator|.
name|contains
argument_list|(
literal|"cat"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got "
operator|+
name|subject
argument_list|,
name|subject
operator|.
name|contains
argument_list|(
literal|"garden"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

