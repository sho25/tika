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
name|detect
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|mime
operator|.
name|MediaType
import|;
end_import

begin_comment
comment|/**  * Test cases for the {@link TextDetector} class.  */
end_comment

begin_class
specifier|public
class|class
name|TextDetectorTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
name|Detector
name|detector
init|=
operator|new
name|TextDetector
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testDetectNull
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
literal|null
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for type detection of empty documents.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-483">TIKA-483</a>      */
specifier|public
name|void
name|testDetectEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotText
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDetectText
parameter_list|()
throws|throws
name|Exception
block|{
name|assertText
argument_list|(
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertText
argument_list|(
literal|" \t\r\n"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertText
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|-
literal|1
block|,
operator|-
literal|2
block|,
operator|-
literal|3
block|,
literal|0x09
block|,
literal|0x0A
block|,
literal|0x0C
block|,
literal|0x0D
block|,
literal|0x1B
block|}
argument_list|)
expr_stmt|;
name|assertNotText
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
name|assertNotText
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|'H'
block|,
literal|'e'
block|,
literal|'l'
block|,
literal|'l'
block|,
literal|'o'
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
operator|(
name|byte
operator|)
literal|'.'
argument_list|)
expr_stmt|;
name|assertText
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
operator|(
name|byte
operator|)
literal|0x1f
argument_list|)
expr_stmt|;
name|assertNotText
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
literal|513
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
operator|(
name|byte
operator|)
literal|'.'
argument_list|)
expr_stmt|;
name|assertText
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
operator|(
name|byte
operator|)
literal|0x1f
argument_list|)
expr_stmt|;
name|assertNotText
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertText
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
try|try
block|{
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test that the stream has been reset
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|data
index|[
name|i
index|]
argument_list|,
operator|(
name|byte
operator|)
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected exception from TextDetector"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertNotText
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
try|try
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected exception from TextDetector"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

