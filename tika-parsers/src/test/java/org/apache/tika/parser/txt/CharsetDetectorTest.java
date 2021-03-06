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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
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
name|TikaTest
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
name|CharsetDetectorTest
extends|extends
name|TikaTest
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
try|try
init|(
name|InputStream
name|in
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/resume.html"
argument_list|)
init|)
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
block|}
comment|/* https://issues.apache.org/jira/browse/TIKA-1248    * Verify empty or null declaredEncoding doesn't cause an exception    *     */
annotation|@
name|Test
specifier|public
name|void
name|testEmptyOrNullDeclaredCharset
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|InputStream
name|in
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/resume.html"
argument_list|)
init|)
block|{
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|Reader
name|reader
init|=
name|detector
operator|.
name|getReader
argument_list|(
name|in
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|ready
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|=
name|detector
operator|.
name|getReader
argument_list|(
name|in
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|ready
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWin125XHeuristics
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TIKA-2219
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|detector
operator|.
name|setText
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testTXT_win-1252.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|CharsetMatch
name|charset
init|=
name|detector
operator|.
name|detect
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"windows-1252"
argument_list|,
name|charset
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetTextConsistency
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TIKA-2475
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/multi-language.txt"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|fileBytes
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|)
decl_stmt|;
name|InputStream
name|fileStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|fileBytes
argument_list|)
decl_stmt|;
name|CharsetDetector
name|fromBytesDetector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|fromBytesDetector
operator|.
name|setText
argument_list|(
name|fileBytes
argument_list|)
expr_stmt|;
name|CharsetDetector
name|fromStreamDetector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|fromStreamDetector
operator|.
name|setText
argument_list|(
name|fileStream
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ISO-8859-1"
argument_list|,
name|fromBytesDetector
operator|.
name|detect
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ISO-8859-1"
argument_list|,
name|fromStreamDetector
operator|.
name|detect
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testZeroLength
parameter_list|()
throws|throws
name|Exception
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
name|setText
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|//charset detector returns "UTF-8" when there's no data
name|assertEquals
argument_list|(
literal|"UTF-8"
argument_list|,
name|detector
operator|.
name|detect
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLengthResetCorrectly
parameter_list|()
throws|throws
name|IOException
block|{
comment|//test that the underlying array.length is reset correctly
comment|//first fill the buffer with windows-1256
name|String
name|computer
init|=
literal|"\u0627\u0644\u062D\u0627\u0633\u0648\u0628"
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|computer
argument_list|)
expr_stmt|;
block|}
name|detector
operator|.
name|setText
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"windows-1256"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"windows-1256"
argument_list|,
name|detector
operator|.
name|detect
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|computer
argument_list|)
expr_stmt|;
block|}
comment|//then fill a small part of the buffer with UTF-8
name|detector
operator|.
name|setText
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UTF-8"
argument_list|,
name|detector
operator|.
name|detect
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

