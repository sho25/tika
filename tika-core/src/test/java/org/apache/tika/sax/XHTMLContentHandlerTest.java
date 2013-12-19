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

begin_comment
comment|/**  * Unit tests for the {@link XHTMLContentHandler} class.  */
end_comment

begin_class
specifier|public
class|class
name|XHTMLContentHandlerTest
block|{
specifier|private
name|ContentHandler
name|output
decl_stmt|;
specifier|private
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|output
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
name|xhtml
operator|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|output
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test that content in block elements are properly separated in text      * output.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-188">TIKA-188</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testExtraWhitespace
parameter_list|()
throws|throws
name|SAXException
block|{
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"b"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
comment|// inlines should not cause extra whitespace
name|xhtml
operator|.
name|characters
argument_list|(
literal|"r"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"th"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"th"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"td"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"td"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|String
index|[]
name|words
init|=
name|output
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|words
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|words
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|words
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|words
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"y"
argument_list|,
name|words
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|words
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|words
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test that content in option elements are properly separated in text      * output.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-394">TIKA-394</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testWhitespaceWithOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"form"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"select"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"option"
argument_list|,
literal|"opt1"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"option"
argument_list|,
literal|"opt2"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"select"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"form"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|String
index|[]
name|words
init|=
name|output
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|words
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"opt1"
argument_list|,
name|words
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"opt2"
argument_list|,
name|words
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhitespaceWithMenus
parameter_list|()
throws|throws
name|Exception
block|{
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"menu"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"li"
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"li"
argument_list|,
literal|"two"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"menu"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|String
index|[]
name|words
init|=
name|getRealWords
argument_list|(
name|output
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|words
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|words
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"two"
argument_list|,
name|words
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return array of non-zerolength words. Splitting on whitespace will get us      * empty words for emptylines.      *       * @param string some mix of newlines and real words      * @return array of real words.      */
specifier|private
specifier|static
name|String
index|[]
name|getRealWords
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|String
index|[]
name|possibleWords
init|=
name|string
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|words
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|possibleWords
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|word
range|:
name|possibleWords
control|)
block|{
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|words
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|words
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|words
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

