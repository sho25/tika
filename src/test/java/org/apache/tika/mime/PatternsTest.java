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

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * Test case for {@link Patterns}.  *  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|PatternsTest
extends|extends
name|TestCase
block|{
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
specifier|protected
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
argument_list|()
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
block|}
end_class

end_unit

