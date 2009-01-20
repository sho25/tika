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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
comment|/**  * Test cases for the {@link NameDetector} class.  */
end_comment

begin_class
specifier|public
class|class
name|NameDetectorTest
extends|extends
name|TestCase
block|{
specifier|private
name|Detector
name|detector
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
name|Map
argument_list|<
name|Pattern
argument_list|,
name|MediaType
argument_list|>
name|patterns
init|=
operator|new
name|HashMap
argument_list|<
name|Pattern
argument_list|,
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
name|patterns
operator|.
name|put
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*\\.txt"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
argument_list|,
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
expr_stmt|;
name|patterns
operator|.
name|put
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"README"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
expr_stmt|;
name|detector
operator|=
operator|new
name|NameDetector
argument_list|(
name|patterns
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDetect
parameter_list|()
block|{
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"text.txt"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"text.txt "
argument_list|)
expr_stmt|;
comment|// trailing space
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"text.txt\n"
argument_list|)
expr_stmt|;
comment|// trailing newline
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"text.txt?a=b"
argument_list|)
expr_stmt|;
comment|// URL query
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"text.txt#abc"
argument_list|)
expr_stmt|;
comment|// URL fragment
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"text%2Etxt"
argument_list|)
expr_stmt|;
comment|// URL encoded
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"text.TXT"
argument_list|)
expr_stmt|;
comment|// case insensitive
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"text.txt.gz"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"README"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|" README "
argument_list|)
expr_stmt|;
comment|// space around
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"\tREADME\n"
argument_list|)
expr_stmt|;
comment|// other whitespace
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"/a/README"
argument_list|)
expr_stmt|;
comment|// leading path
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"\\b\\README"
argument_list|)
expr_stmt|;
comment|// windows path
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"ReadMe"
argument_list|)
expr_stmt|;
comment|// case sensitive
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"README.NOW"
argument_list|)
expr_stmt|;
comment|// tough one
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|" See http://www.example.com:1234/README.txt?a=b#c \n"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
literal|"See README.txt"
argument_list|)
expr_stmt|;
comment|// even this!
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"See README"
argument_list|)
expr_stmt|;
comment|// but not this
comment|// test also the zero input cases
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
literal|null
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
literal|"NameDetector should never throw an IOException"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertDetect
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|type
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
literal|null
argument_list|,
name|metadata
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
literal|"NameDetector should never throw an IOException"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

