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
name|assertFalse
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
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
name|TIFF
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
name|metadata
operator|.
name|XMPMM
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_class
specifier|public
class|class
name|JpegParserTest
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
specifier|static
name|TimeZone
name|CURR_TIME_ZONE
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|//As of Drew Noakes' metadata-extractor 2.8.1,
comment|//unspecified timezones appear to be set to
comment|//TimeZone.getDefault().  We need to normalize this
comment|//for testing across different time zones.
comment|//We also appear to have to specify it in the surefire config:
comment|//<argLine>-Duser.timezone=UTC</argLine>
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setDefaultTimeZone
parameter_list|()
block|{
name|TimeZone
operator|.
name|setDefault
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|resetDefaultTimeZone
parameter_list|()
block|{
name|TimeZone
operator|.
name|setDefault
argument_list|(
name|CURR_TIME_ZONE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
comment|// Core EXIF/TIFF tags
name|assertEquals
argument_list|(
literal|"3888"
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
literal|"2592"
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
name|assertEquals
argument_list|(
literal|"6.25E-4"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|EXPOSURE_TIME
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1/1600
name|assertEquals
argument_list|(
literal|"5.6"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|F_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|FLASH_FIRED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"194.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|FOCAL_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"400"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|ISO_SPEED_RATINGS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Canon"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|EQUIPMENT_MAKE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Canon EOS 40D"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|EQUIPMENT_MODEL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Adobe Photoshop CS3 Macintosh"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SOFTWARE
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
name|ORIENTATION
argument_list|)
argument_list|)
expr_stmt|;
comment|// Not present
name|assertEquals
argument_list|(
literal|"240.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOLUTION_HORIZONTAL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"240.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOLUTION_VERTICAL
argument_list|)
argument_list|)
expr_stmt|;
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
comment|// Check that EXIF/TIFF tags come through with their raw values too
comment|// (This may be removed for Tika 1.0, as we support more of them
comment|//  with explicit Metadata entries)
name|assertEquals
argument_list|(
literal|"Canon EOS 40D"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Exif IFD0:Model"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Common tags
name|assertEquals
argument_list|(
literal|"2009-10-02T23:02:49"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|LAST_MODIFIED
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
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|keywords
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"'canon-55-250' expected in "
operator|+
name|keywords
argument_list|,
name|keywords
operator|.
name|contains
argument_list|(
literal|"canon-55-250"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"'moscow-birds' expected in "
operator|+
name|keywords
argument_list|,
name|keywords
operator|.
name|contains
argument_list|(
literal|"moscow-birds"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"'serbor' expected in "
operator|+
name|keywords
argument_list|,
name|keywords
operator|.
name|contains
argument_list|(
literal|"serbor"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|keywords
operator|.
name|contains
argument_list|(
literal|"canon-55-250 moscow-birds serbor"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for a file with Geographic information (lat, long etc) in it      */
annotation|@
name|Test
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
comment|// Core EXIF/TIFF tags
name|assertEquals
argument_list|(
literal|"3888"
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
literal|"2592"
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
name|assertEquals
argument_list|(
literal|"6.25E-4"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|EXPOSURE_TIME
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1/1600
name|assertEquals
argument_list|(
literal|"5.6"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|F_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|FLASH_FIRED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"194.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|FOCAL_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"400"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|ISO_SPEED_RATINGS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Canon"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|EQUIPMENT_MAKE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Canon EOS 40D"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|EQUIPMENT_MODEL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Adobe Photoshop CS3 Macintosh"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SOFTWARE
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
name|ORIENTATION
argument_list|)
argument_list|)
expr_stmt|;
comment|// Not present
name|assertEquals
argument_list|(
literal|"240.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOLUTION_HORIZONTAL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"240.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOLUTION_VERTICAL
argument_list|)
argument_list|)
expr_stmt|;
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
name|TikaCoreProperties
operator|.
name|CREATED
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
name|LAST_MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Date/Time Original should be stored in EXIF field too"
argument_list|,
literal|"2009-08-11T09:09:45"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TIFF
operator|.
name|ORIGINAL_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"canon-55-250"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for an image with the geographic information stored in a slightly      * different way, see TIKA-915 for details      * Disabled for now, pending a fix to the underlying library      */
annotation|@
name|Test
specifier|public
name|void
name|testJPEGGeo2
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
literal|"/test-documents/testJPEG_GEO_2.jpg"
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
comment|// Geo tags should be there with 5dp, and not rounded
name|assertEquals
argument_list|(
literal|"51.575762"
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
literal|"-1.567886"
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
block|}
annotation|@
name|Test
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
name|assertEquals
argument_list|(
literal|"Tosteberga \u00C4ngar"
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
literal|"Bird site in north eastern Sk\u00E5ne, Sweden.\n(new line)"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
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
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
comment|// Dublin Core
comment|// xmp handles spaces in keywords, returns "bird watching, nature reserve, coast, grazelands"
comment|// but we have to replace them with underscore
name|List
argument_list|<
name|String
argument_list|>
name|keywords
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|keywords
operator|.
name|contains
argument_list|(
literal|"coast"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keywords
operator|.
name|contains
argument_list|(
literal|"bird watching"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Core EXIF/TIFF tags
name|assertEquals
argument_list|(
literal|"103"
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
literal|"77"
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
name|assertEquals
argument_list|(
literal|"1.0E-6"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|EXPOSURE_TIME
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1/1000000
name|assertEquals
argument_list|(
literal|"2.8"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|F_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4.6"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|FOCAL_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"114"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|ISO_SPEED_RATINGS
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
name|EQUIPMENT_MAKE
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
name|EQUIPMENT_MODEL
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
name|SOFTWARE
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
name|Metadata
operator|.
name|ORIENTATION
argument_list|)
argument_list|)
expr_stmt|;
comment|// Not present
name|assertEquals
argument_list|(
literal|"300.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOLUTION_HORIZONTAL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"300.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOLUTION_VERTICAL
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJPEGTitleAndDescriptionPhotoshop
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
literal|"/test-documents/testJPEG_commented_pspcs2mac.jpg"
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
name|assertEquals
argument_list|(
literal|"Tosteberga \u00C4ngar"
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
literal|"Bird site in north eastern Sk\u00E5ne, Sweden.\n(new line)"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
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
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|keywords
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"got "
operator|+
name|keywords
argument_list|,
name|keywords
operator|.
name|contains
argument_list|(
literal|"bird watching"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJPEGTitleAndDescriptionXnviewmp
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
literal|"/test-documents/testJPEG_commented_xnviewmp026.jpg"
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
comment|// XnViewMp's default comment dialog has only comment, not headline.
comment|// Comment is embedded only if "Write comments in XMP" is enabled in settings
name|assertEquals
argument_list|(
literal|"Bird site in north eastern Sk\u00E5ne, Sweden.\n(new line)"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
comment|// xmp handles spaces in keywords, returns "bird watching, nature reserve, coast, grazelands"
comment|// but we have to replace them with underscore
name|String
index|[]
name|subject
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|keywords
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|subject
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"'coast'"
operator|+
literal|" not in "
operator|+
name|keywords
argument_list|,
name|keywords
operator|.
name|contains
argument_list|(
literal|"coast"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"'nature reserve'"
operator|+
literal|" not in "
operator|+
name|keywords
argument_list|,
name|keywords
operator|.
name|contains
argument_list|(
literal|"nature reserve"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJPEGoddTagComponent
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
literal|"/test-documents/testJPEG_oddTagComponent.jpg"
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
literal|null
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
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"251"
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
literal|"384"
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJPEGEmptyEXIFDateTime
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
literal|"/test-documents/testJPEG_EXIF_emptyDateTime.jpg"
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
literal|"300.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TIFF
operator|.
name|RESOLUTION_HORIZONTAL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"300.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TIFF
operator|.
name|RESOLUTION_VERTICAL
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJPEGXMPMM
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
literal|"/test-documents/testJPEG_EXIF_emptyDateTime.jpg"
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
comment|//TODO: when jempbox is fixed/xmpbox is used
comment|//add tests for history...currently not extracted
name|assertEquals
argument_list|(
literal|"xmp.did:49E997348D4911E1AB62EBF9B374B234"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|XMPMM
operator|.
name|DOCUMENTID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

