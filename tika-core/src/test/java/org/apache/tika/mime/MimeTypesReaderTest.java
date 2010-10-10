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
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|config
operator|.
name|TikaConfig
import|;
end_import

begin_comment
comment|/**  * These tests try to ensure that the MimeTypesReader  *  has correctly processed the mime-types.xml file.  * To do this, it tests that various aspects of the  *  mime-types.xml file have ended up correctly as  *  globs, matches, magics etc.  *    * If you make updates to mime-types.xml, then the  *  checks in this test may no longer hold true.  * As such, if tests here start failing after your  *  changes, please review the test details, and  *  update it to match the new state of the file!   */
end_comment

begin_class
specifier|public
class|class
name|MimeTypesReaderTest
extends|extends
name|TestCase
block|{
specifier|private
name|MimeTypes
name|mimeTypes
decl_stmt|;
specifier|private
name|SortedSet
argument_list|<
name|Magic
argument_list|>
name|magics
decl_stmt|;
specifier|private
name|SortedSet
argument_list|<
name|MimeType
argument_list|>
name|xmls
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
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
name|SortedSet
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
name|Field
name|xmlsField
init|=
name|mimeTypes
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"xmls"
argument_list|)
decl_stmt|;
name|xmlsField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|xmls
operator|=
operator|(
name|SortedSet
argument_list|<
name|MimeType
argument_list|>
operator|)
name|xmlsField
operator|.
name|get
argument_list|(
name|mimeTypes
argument_list|)
expr_stmt|;
block|}
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
name|length
argument_list|,
name|html
operator|.
name|getMagics
argument_list|()
operator|.
name|length
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
name|length
argument_list|,
name|excel
operator|.
name|getMagics
argument_list|()
operator|.
name|length
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
block|}
end_class

end_unit

