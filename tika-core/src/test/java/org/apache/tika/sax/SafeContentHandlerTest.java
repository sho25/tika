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
name|sax
package|;
end_package

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
comment|/**  * Unit tests for the {@link SafeContentHandler} class.  */
end_comment

begin_class
specifier|public
class|class
name|SafeContentHandlerTest
extends|extends
name|TestCase
block|{
specifier|private
name|ContentHandler
name|output
decl_stmt|;
specifier|private
name|ContentHandler
name|safe
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
name|output
operator|=
operator|new
name|WriteOutContentHandler
argument_list|()
expr_stmt|;
name|safe
operator|=
operator|new
name|SafeContentHandler
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEmptyInput
parameter_list|()
throws|throws
name|SAXException
block|{
name|safe
operator|.
name|characters
argument_list|(
operator|new
name|char
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|safe
operator|.
name|ignorableWhitespace
argument_list|(
operator|new
name|char
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNormalCharacters
parameter_list|()
throws|throws
name|SAXException
block|{
name|safe
operator|.
name|characters
argument_list|(
literal|"abc"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNormalWhitespace
parameter_list|()
throws|throws
name|SAXException
block|{
name|safe
operator|.
name|ignorableWhitespace
argument_list|(
literal|"abc"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidCharacters
parameter_list|()
throws|throws
name|SAXException
block|{
name|safe
operator|.
name|characters
argument_list|(
literal|"ab\u0007"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|safe
operator|.
name|characters
argument_list|(
literal|"a\u000Bc"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|safe
operator|.
name|characters
argument_list|(
literal|"\u0019bc"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab a c bc"
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidWhitespace
parameter_list|()
throws|throws
name|SAXException
block|{
name|safe
operator|.
name|ignorableWhitespace
argument_list|(
literal|"ab\u0000"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|safe
operator|.
name|ignorableWhitespace
argument_list|(
literal|"a\u0001c"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|safe
operator|.
name|ignorableWhitespace
argument_list|(
literal|"\u0002bc"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab a c bc"
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidSurrogates
parameter_list|()
throws|throws
name|SAXException
block|{
name|safe
operator|.
name|ignorableWhitespace
argument_list|(
literal|"\udb00\ubfff"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|" \ubfff"
argument_list|,
name|output
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

