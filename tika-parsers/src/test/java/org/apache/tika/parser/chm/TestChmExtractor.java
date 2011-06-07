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
name|chm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|detect
operator|.
name|TestContainerAwareDetector
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
name|parser
operator|.
name|chm
operator|.
name|accessor
operator|.
name|ChmDirectoryListingSet
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
name|chm
operator|.
name|accessor
operator|.
name|DirectoryListingEntry
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
name|chm
operator|.
name|core
operator|.
name|ChmExtractor
import|;
end_import

begin_class
specifier|public
class|class
name|TestChmExtractor
extends|extends
name|TestCase
block|{
specifier|private
name|ChmExtractor
name|chmExtractor
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|TestContainerAwareDetector
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|TestParameters
operator|.
name|chmFile
argument_list|)
argument_list|)
decl_stmt|;
name|chmExtractor
operator|=
operator|new
name|ChmExtractor
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEnumerateChm
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|chmEntries
init|=
name|chmExtractor
operator|.
name|enumerateChm
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_CHM_ENTITIES_NUMBER
argument_list|,
name|chmEntries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetChmDirList
parameter_list|()
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|chmExtractor
operator|.
name|getChmDirList
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExtractChmEntry
parameter_list|()
block|{
name|ChmDirectoryListingSet
name|entries
init|=
name|chmExtractor
operator|.
name|getChmDirList
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|localFile
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DirectoryListingEntry
argument_list|>
name|it
init|=
name|entries
operator|.
name|getDirectoryListingEntryList
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|localFile
operator|=
name|chmExtractor
operator|.
name|extractChmEntry
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|localFile
operator|!=
literal|null
condition|)
block|{
operator|++
name|count
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TestParameters
operator|.
name|VP_CHM_ENTITIES_NUMBER
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{ 	}
block|}
end_class

end_unit

