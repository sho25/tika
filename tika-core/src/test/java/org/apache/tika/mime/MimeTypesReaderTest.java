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
name|mime
package|;
end_package

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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * These tests try to ensure that the MimeTypesReader  *  has correctly processed the mime-types.xml file.  * To do this, it tests that various aspects of the  *  mime-types.xml file have ended up correctly as  *  globs, matches, magics etc.  *    * If you make updates to mime-types.xml, then the  *  checks in this test may no longer hold true.  * As such, if tests here start failing after your  *  changes, please review the test details, and  *  update it to match the new state of the file!   */
end_comment

begin_class
specifier|public
class|class
name|MimeTypesReaderTest
block|{
specifier|private
name|MimeTypes
name|mimeTypes
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Magic
argument_list|>
name|magics
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|SecurityException
throws|,
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|this
operator|.
name|mimeTypes
operator|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
operator|.
name|getMimeRepository
argument_list|()
expr_stmt|;
name|Field
name|magicsField
init|=
name|mimeTypes
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"magics"
argument_list|)
decl_stmt|;
name|magicsField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|magics
operator|=
operator|(
name|List
argument_list|<
name|Magic
argument_list|>
operator|)
name|magicsField
operator|.
name|get
argument_list|(
name|mimeTypes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHtmlMatches
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|minMatches
init|=
literal|10
decl_stmt|;
comment|// Check on the type
name|MimeType
name|html
init|=
name|mimeTypes
operator|.
name|forName
argument_list|(
literal|"text/html"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|html
operator|.
name|hasMagic
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"There should be at least "
operator|+
name|minMatches
operator|+
literal|" HTML matches, found "
operator|+
name|html
operator|.
name|getMagics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|html
operator|.
name|getMagics
argument_list|()
operator|.
name|size
argument_list|()
operator|>=
name|minMatches
argument_list|)
expr_stmt|;
comment|// Check on the overall magics
name|List
argument_list|<
name|Magic
argument_list|>
name|htmlMagics
init|=
operator|new
name|ArrayList
argument_list|<
name|Magic
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Magic
name|magic
range|:
name|magics
control|)
block|{
if|if
condition|(
name|magic
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"text/html"
argument_list|)
condition|)
block|{
name|htmlMagics
operator|.
name|add
argument_list|(
name|magic
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"There should be at least "
operator|+
name|minMatches
operator|+
literal|" HTML matches, found "
operator|+
name|htmlMagics
operator|.
name|size
argument_list|()
argument_list|,
name|htmlMagics
operator|.
name|size
argument_list|()
operator|>=
name|minMatches
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExcelMatches
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|minMatches
init|=
literal|4
decl_stmt|;
comment|// Check on the type
name|MimeType
name|excel
init|=
name|mimeTypes
operator|.
name|forName
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|excel
operator|.
name|hasMagic
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"There should be at least "
operator|+
name|minMatches
operator|+
literal|" Excel matches, found "
operator|+
name|excel
operator|.
name|getMagics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|excel
operator|.
name|getMagics
argument_list|()
operator|.
name|size
argument_list|()
operator|>=
name|minMatches
argument_list|)
expr_stmt|;
comment|// Check on the overall magics
name|List
argument_list|<
name|Magic
argument_list|>
name|excelMagics
init|=
operator|new
name|ArrayList
argument_list|<
name|Magic
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Magic
name|magic
range|:
name|magics
control|)
block|{
if|if
condition|(
name|magic
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|)
condition|)
block|{
name|excelMagics
operator|.
name|add
argument_list|(
name|magic
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"There should be at least "
operator|+
name|minMatches
operator|+
literal|" Excel matches, found "
operator|+
name|excelMagics
operator|.
name|size
argument_list|()
argument_list|,
name|excelMagics
operator|.
name|size
argument_list|()
operator|>=
name|minMatches
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since TIKA-515      */
annotation|@
name|Test
specifier|public
name|void
name|testReadComment
parameter_list|()
block|{
try|try
block|{
name|assertNotNull
argument_list|(
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
literal|"application/msword"
argument_list|)
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @since TIKA-1012      */
annotation|@
name|Test
specifier|public
name|void
name|testReadExtendedMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|MimeType
name|mime
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
literal|"image/x-ms-bmp"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"BMP"
argument_list|,
name|mime
operator|.
name|getAcronym
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"com.microsoft.bmp"
argument_list|,
name|mime
operator|.
name|getUniformTypeIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://en.wikipedia.org/wiki/BMP_file_format"
argument_list|,
name|mime
operator|.
name|getLinks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|mime
operator|=
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
literal|"application/xml"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XML"
argument_list|,
name|mime
operator|.
name|getAcronym
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"public.xml"
argument_list|,
name|mime
operator|.
name|getUniformTypeIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://en.wikipedia.org/wiki/Xml"
argument_list|,
name|mime
operator|.
name|getLinks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadParameterHierarchy
parameter_list|()
throws|throws
name|Exception
block|{
name|MimeType
name|mimeBTree4
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
literal|"application/x-berkeley-db;format=btree;version=4"
argument_list|)
decl_stmt|;
name|MediaType
name|mtBTree4
init|=
name|mimeBTree4
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// Canonicalised with spaces
name|assertEquals
argument_list|(
literal|"application/x-berkeley-db; format=btree; version=4"
argument_list|,
name|mimeBTree4
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/x-berkeley-db; format=btree; version=4"
argument_list|,
name|mtBTree4
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Parent has one parameter
name|MediaType
name|mtBTree
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|getMediaTypeRegistry
argument_list|()
operator|.
name|getSupertype
argument_list|(
name|mtBTree4
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/x-berkeley-db; format=btree"
argument_list|,
name|mtBTree
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Parent of that has none
name|MediaType
name|mtBD
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|getMediaTypeRegistry
argument_list|()
operator|.
name|getSupertype
argument_list|(
name|mtBTree
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/x-berkeley-db"
argument_list|,
name|mtBD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// If we use one with parameters not known in the media registry,
comment|//  getting the parent will return the non-parameter version
name|MediaType
name|mtAlt
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-berkeley-db; format=unknown; version=42"
argument_list|)
decl_stmt|;
name|MediaType
name|mtAltP
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|getMediaTypeRegistry
argument_list|()
operator|.
name|getSupertype
argument_list|(
name|mtAlt
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/x-berkeley-db"
argument_list|,
name|mtAltP
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * TIKA-746 Ensures that the custom mimetype maps were also       *  loaded and used      */
annotation|@
name|Test
specifier|public
name|void
name|testCustomMimeTypes
parameter_list|()
block|{
comment|// Check that it knows about our three special ones
name|String
name|helloWorld
init|=
literal|"hello/world"
decl_stmt|;
name|String
name|helloWorldFile
init|=
literal|"hello/world-file"
decl_stmt|;
name|String
name|helloXWorld
init|=
literal|"hello/x-world-hello"
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
name|helloWorld
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
name|helloWorldFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
name|helloXWorld
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check that the details come through as expected
try|try
block|{
name|MimeType
name|hw
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
name|helloWorld
argument_list|)
decl_stmt|;
name|MimeType
name|hwf
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
name|helloWorldFile
argument_list|)
decl_stmt|;
name|MimeType
name|hxw
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
name|helloXWorld
argument_list|)
decl_stmt|;
comment|// The parent has no comments, globs, magic etc
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|hw
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|hw
operator|.
name|getExtension
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hw
operator|.
name|getExtensions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hw
operator|.
name|getMagics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// The file one does
name|assertEquals
argument_list|(
literal|"A \"Hello World\" file"
argument_list|,
name|hwf
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".hello.world"
argument_list|,
name|hwf
operator|.
name|getExtension
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hwf
operator|.
name|getMagics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// The alternate one has most
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|hxw
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".x-hello-world"
argument_list|,
name|hxw
operator|.
name|getExtension
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hxw
operator|.
name|getMagics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check that we can correct detect with the file one:
comment|// By name
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test.hello.world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hwf
operator|.
name|toString
argument_list|()
argument_list|,
name|this
operator|.
name|mimeTypes
operator|.
name|detect
argument_list|(
literal|null
argument_list|,
name|m
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|m
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test.x-hello-world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hxw
operator|.
name|toString
argument_list|()
argument_list|,
name|this
operator|.
name|mimeTypes
operator|.
name|detect
argument_list|(
literal|null
argument_list|,
name|m
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// By contents - picks the x one as that sorts later
name|m
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|ByteArrayInputStream
name|s
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|(
literal|"ASCII"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hxw
operator|.
name|toString
argument_list|()
argument_list|,
name|this
operator|.
name|mimeTypes
operator|.
name|detect
argument_list|(
name|s
argument_list|,
name|m
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetExtensionForPowerPoint
parameter_list|()
throws|throws
name|Exception
block|{
name|MimeType
name|mt
init|=
name|this
operator|.
name|mimeTypes
operator|.
name|forName
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|)
decl_stmt|;
name|String
name|ext
init|=
name|mt
operator|.
name|getExtension
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|".ppt"
argument_list|,
name|ext
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".ppt"
argument_list|,
name|mt
operator|.
name|getExtensions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

