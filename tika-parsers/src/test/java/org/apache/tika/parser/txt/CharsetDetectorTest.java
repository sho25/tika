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
name|txt
package|;
end_package

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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|CharsetDetectorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testTagDropper
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|CharsetDetectorTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/resume.html"
argument_list|)
decl_stmt|;
try|try
block|{
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|detector
operator|.
name|enableInputFilter
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|detector
operator|.
name|setText
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|CharsetMatch
index|[]
name|matches
init|=
name|detector
operator|.
name|detectAll
argument_list|()
decl_stmt|;
name|CharsetMatch
name|mm
init|=
literal|null
decl_stmt|;
for|for
control|(
name|CharsetMatch
name|m
range|:
name|matches
control|)
block|{
if|if
condition|(
name|mm
operator|==
literal|null
operator|||
name|mm
operator|.
name|getConfidence
argument_list|()
operator|<
name|m
operator|.
name|getConfidence
argument_list|()
condition|)
block|{
name|mm
operator|=
name|m
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|mm
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UTF-8"
argument_list|,
name|mm
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
