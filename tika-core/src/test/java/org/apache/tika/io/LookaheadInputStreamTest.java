begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|io
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
comment|/**  * Test cases for the {@link LookaheadInputStream} class.  */
end_comment

begin_class
specifier|public
class|class
name|LookaheadInputStreamTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testNullStream
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|lookahead
init|=
operator|new
name|LookaheadInputStream
argument_list|(
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEmptyStream
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|InputStream
name|lookahead
init|=
operator|new
name|LookaheadInputStream
argument_list|(
name|stream
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|lookahead
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBasicLookahead
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|}
argument_list|)
decl_stmt|;
name|InputStream
name|lookahead
init|=
operator|new
name|LookaheadInputStream
argument_list|(
name|stream
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|'a'
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|lookahead
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|'a'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testZeroLookahead
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|}
argument_list|)
decl_stmt|;
name|InputStream
name|lookahead
init|=
operator|new
name|LookaheadInputStream
argument_list|(
name|stream
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|lookahead
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|'a'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMarkLookahead
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|}
argument_list|)
decl_stmt|;
name|InputStream
name|lookahead
init|=
operator|new
name|LookaheadInputStream
argument_list|(
name|stream
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|lookahead
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'a'
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|lookahead
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|'a'
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|lookahead
operator|.
name|mark
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|lookahead
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|lookahead
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|'a'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSkipLookahead
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|}
argument_list|)
decl_stmt|;
name|InputStream
name|lookahead
init|=
operator|new
name|LookaheadInputStream
argument_list|(
name|stream
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lookahead
operator|.
name|skip
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|lookahead
operator|.
name|skip
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookahead
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|lookahead
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|'a'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'c'
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

