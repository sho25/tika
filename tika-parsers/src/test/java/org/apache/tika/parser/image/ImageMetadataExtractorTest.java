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
name|java
operator|.
name|util
operator|.
name|*
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|MetadataException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|Tag
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|exif
operator|.
name|ExifIFD0Directory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|exif
operator|.
name|ExifSubIFDDirectory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|jpeg
operator|.
name|JpegCommentDirectory
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
name|assertNull
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
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ImageMetadataExtractorTest
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testHandleDirectories
parameter_list|()
throws|throws
name|MetadataException
block|{
name|Metadata
name|metadata
init|=
name|mock
argument_list|(
name|Metadata
operator|.
name|class
argument_list|)
decl_stmt|;
name|ImageMetadataExtractor
operator|.
name|DirectoryHandler
name|handler1
init|=
name|mock
argument_list|(
name|ImageMetadataExtractor
operator|.
name|DirectoryHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|ImageMetadataExtractor
name|e
init|=
operator|new
name|ImageMetadataExtractor
argument_list|(
name|metadata
argument_list|,
name|handler1
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
operator|new
name|JpegCommentDirectory
argument_list|()
decl_stmt|;
name|Iterator
name|directories
init|=
name|mock
argument_list|(
name|Iterator
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|directories
operator|.
name|hasNext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|directories
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|handler1
operator|.
name|supports
argument_list|(
name|JpegCommentDirectory
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|e
operator|.
name|handle
argument_list|(
name|directories
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|handler1
argument_list|)
operator|.
name|supports
argument_list|(
name|JpegCommentDirectory
operator|.
name|class
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|handler1
argument_list|)
operator|.
name|handle
argument_list|(
name|directory
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExifHandlerSupports
parameter_list|()
block|{
name|assertTrue
argument_list|(
operator|new
name|ImageMetadataExtractor
operator|.
name|ExifHandler
argument_list|()
operator|.
name|supports
argument_list|(
name|ExifIFD0Directory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|ImageMetadataExtractor
operator|.
name|ExifHandler
argument_list|()
operator|.
name|supports
argument_list|(
name|ExifSubIFDDirectory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|ImageMetadataExtractor
operator|.
name|ExifHandler
argument_list|()
operator|.
name|supports
argument_list|(
name|Directory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|ImageMetadataExtractor
operator|.
name|ExifHandler
argument_list|()
operator|.
name|supports
argument_list|(
name|JpegCommentDirectory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExifHandlerParseDate
parameter_list|()
throws|throws
name|MetadataException
block|{
name|ExifSubIFDDirectory
name|exif
init|=
name|mock
argument_list|(
name|ExifSubIFDDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|exif
operator|.
name|containsTag
argument_list|(
name|ExifSubIFDDirectory
operator|.
name|TAG_DATETIME_ORIGINAL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|GregorianCalendar
name|calendar
init|=
operator|new
name|GregorianCalendar
argument_list|(
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
name|calendar
operator|.
name|setTimeInMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
literal|2000
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|exif
operator|.
name|getDate
argument_list|(
name|ExifSubIFDDirectory
operator|.
name|TAG_DATETIME_ORIGINAL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// jvm default timezone as in Metadata Extractor
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
operator|new
name|ImageMetadataExtractor
operator|.
name|ExifHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|exif
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should be ISO date without time zone"
argument_list|,
literal|"2000-01-01T00:00:00"
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExifHandlerParseDateFallback
parameter_list|()
throws|throws
name|MetadataException
block|{
name|ExifIFD0Directory
name|exif
init|=
name|mock
argument_list|(
name|ExifIFD0Directory
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|exif
operator|.
name|containsTag
argument_list|(
name|ExifIFD0Directory
operator|.
name|TAG_DATETIME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|GregorianCalendar
name|calendar
init|=
operator|new
name|GregorianCalendar
argument_list|(
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
name|calendar
operator|.
name|setTimeInMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
literal|1999
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|exif
operator|.
name|getDate
argument_list|(
name|ExifIFD0Directory
operator|.
name|TAG_DATETIME
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// jvm default timezone as in Metadata Extractor
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
operator|new
name|ImageMetadataExtractor
operator|.
name|ExifHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|exif
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should try EXIF Date/Time if Original is not set"
argument_list|,
literal|"1999-01-01T00:00:00"
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExifHandlerParseDateError
parameter_list|()
throws|throws
name|MetadataException
block|{
name|ExifIFD0Directory
name|exif
init|=
name|mock
argument_list|(
name|ExifIFD0Directory
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|exif
operator|.
name|containsTag
argument_list|(
name|ExifSubIFDDirectory
operator|.
name|TAG_DATETIME_ORIGINAL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|exif
operator|.
name|getDate
argument_list|(
name|ExifSubIFDDirectory
operator|.
name|TAG_DATETIME_ORIGINAL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
operator|new
name|ImageMetadataExtractor
operator|.
name|ExifHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|exif
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Parsing should proceed without date"
argument_list|,
literal|null
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCopyUnknownFieldsHandler
parameter_list|()
throws|throws
name|MetadataException
block|{
name|Directory
name|d
init|=
name|mock
argument_list|(
name|Directory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Tag
name|t1
init|=
name|mock
argument_list|(
name|Tag
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|t1
operator|.
name|getTagName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"Image Description"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t1
operator|.
name|getDescription
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"t1"
argument_list|)
expr_stmt|;
name|Tag
name|t2
init|=
name|mock
argument_list|(
name|Tag
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|t2
operator|.
name|getTagName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Metadata
operator|.
name|KEYWORDS
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t2
operator|.
name|getDescription
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"known"
argument_list|)
expr_stmt|;
name|Tag
name|t3
init|=
name|mock
argument_list|(
name|Tag
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|t3
operator|.
name|getTagName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t3
operator|.
name|getDescription
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"known"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Tag
argument_list|>
name|tags
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|t1
argument_list|,
name|t2
argument_list|,
name|t3
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|d
operator|.
name|getTags
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tags
argument_list|)
expr_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
operator|new
name|ImageMetadataExtractor
operator|.
name|CopyUnknownFieldsHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|d
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Image Description"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"keywords should be excluded from bulk copy because it is a defined field"
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
name|assertNull
argument_list|(
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
block|}
block|}
end_class

end_unit

