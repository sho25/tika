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
name|util
operator|.
name|List
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

begin_class
specifier|public
class|class
name|PatternsTest
block|{
specifier|private
name|MimeTypes
name|fullTypes
init|=
name|MimeTypes
operator|.
name|getDefaultMimeTypes
argument_list|()
decl_stmt|;
specifier|private
name|Patterns
name|patterns
decl_stmt|;
specifier|private
name|MimeTypes
name|types
decl_stmt|;
specifier|private
name|MimeType
name|text
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|MimeTypeException
block|{
name|patterns
operator|=
operator|new
name|Patterns
argument_list|(
operator|new
name|MediaTypeRegistry
argument_list|()
argument_list|)
expr_stmt|;
name|types
operator|=
operator|new
name|MimeTypes
argument_list|()
expr_stmt|;
name|text
operator|=
name|types
operator|.
name|forName
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
block|}
comment|/** Test add() */
annotation|@
name|Test
specifier|public
name|void
name|testAdd
parameter_list|()
throws|throws
name|MimeTypeException
block|{
try|try
block|{
name|patterns
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected result
block|}
try|try
block|{
name|patterns
operator|.
name|add
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected result
block|}
try|try
block|{
name|patterns
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected result
block|}
block|}
comment|/** Test matches() */
annotation|@
name|Test
specifier|public
name|void
name|testMatches
parameter_list|()
block|{
try|try
block|{
name|patterns
operator|.
name|matches
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected result
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtension
parameter_list|()
throws|throws
name|MimeTypeException
block|{
name|MimeType
name|doc
init|=
name|types
operator|.
name|forName
argument_list|(
literal|"application/vnd.ms-word"
argument_list|)
decl_stmt|;
name|patterns
operator|.
name|add
argument_list|(
literal|"*.doc"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".doc"
argument_list|,
name|doc
operator|.
name|getExtension
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtensions
parameter_list|()
throws|throws
name|Exception
block|{
name|MimeType
name|jpeg
init|=
name|fullTypes
operator|.
name|forName
argument_list|(
literal|"image/jpeg"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|".jpg"
argument_list|,
name|jpeg
operator|.
name|getExtension
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|extensions
init|=
name|jpeg
operator|.
name|getExtensions
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|extensions
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extensions
operator|.
name|contains
argument_list|(
literal|".jpg"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extensions
operator|.
name|contains
argument_list|(
literal|".jpeg"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

