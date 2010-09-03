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
name|jpeg
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

begin_class
specifier|public
class|class
name|JpegParserTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
name|Parser
name|parser
init|=
operator|new
name|JpegParser
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testJPEG
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
literal|"image/jpeg"
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
literal|"/test-documents/testJPEG_EXIF.jpg"
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
comment|// All EXIF/TIFF tags
name|assertEquals
argument_list|(
literal|"Canon EOS 40D"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Model"
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
literal|"68"
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
literal|null
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
comment|// Common tags
name|assertEquals
argument_list|(
literal|"Date/Time for when the photo was taken, unspecified time zone"
argument_list|,
literal|"2009-10-02T23:02:49"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Date/Time Original for when the photo was taken, unspecified time zone"
argument_list|,
literal|"2009-08-11T09:09:45"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|ORIGINAL_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"canon-55-250 moscow-birds serbor"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|KEYWORDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJPEGGeo
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
literal|"image/jpeg"
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
literal|"/test-documents/testJPEG_GEO.jpg"
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
comment|// Geo tags
name|assertEquals
argument_list|(
literal|"12.54321"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|LATITUDE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-54.1234"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|LONGITUDE
argument_list|)
argument_list|)
expr_stmt|;
comment|// All EXIF/TIFF tags
name|assertEquals
argument_list|(
literal|"Canon EOS 40D"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Model"
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
literal|"68"
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
literal|null
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
comment|// Common tags
name|assertEquals
argument_list|(
literal|"Date/Time Original for when the photo was taken, unspecified time zone"
argument_list|,
literal|"2009-08-11T09:09:45"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|ORIGINAL_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"This image has different Date/Time than Date/Time Original, so it is probably modification date"
argument_list|,
literal|"2009-10-02T23:02:49"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"canon-55-250 moscow-birds serbor"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|KEYWORDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJPEGTitleAndDescription
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
literal|"image/jpeg"
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
literal|"/test-documents/testJPEG_commented.jpg"
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
comment|// embedded comments with non-ascii characters
comment|//assertEquals("Tosteberga \u00C4ngar", metadata.get(Metadata.TITLE));
name|assertEquals
argument_list|(
literal|"Tosteberga "
operator|+
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|-
literal|61
block|,
operator|-
literal|124
block|}
argument_list|)
operator|+
literal|"ngar"
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
comment|//assertEquals("Bird site in north eastern Sk\u00E5ne, Sweden.\n(new line)", metadata.get(Metadata.DESCRIPTION));
name|assertEquals
argument_list|(
literal|"Bird site in north eastern Sk"
operator|+
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|-
literal|61
block|,
operator|-
literal|91
block|}
argument_list|)
operator|+
literal|"ne, Sweden.\n(new line)"
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
name|assertEquals
argument_list|(
literal|"Some Tourist"
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
literal|"grazelands nature reserve bird watching coast"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|KEYWORDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

