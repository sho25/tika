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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * Test cases for the {@link LinkContentHandler} class.  */
end_comment

begin_class
specifier|public
class|class
name|LinkContentHandlerTest
extends|extends
name|TestCase
block|{
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/TIKA-975">TIKA-975</a>      */
specifier|public
name|void
name|testWhitespaceCollapsing
parameter_list|()
throws|throws
name|Exception
block|{
name|LinkContentHandler
name|linkContentHandler
init|=
operator|new
name|LinkContentHandler
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|linkContentHandler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"a"
argument_list|,
literal|""
argument_list|,
operator|new
name|AttributesImpl
argument_list|()
argument_list|)
expr_stmt|;
name|char
index|[]
name|anchorText
init|=
block|{
literal|'\n'
block|,
literal|'N'
block|,
literal|'o'
block|,
literal|' '
block|,
literal|'w'
block|,
literal|'h'
block|,
literal|'i'
block|,
literal|'t'
block|,
literal|'e'
block|,
literal|'\n'
block|,
literal|'\t'
block|,
literal|'\t'
block|,
literal|'s'
block|,
literal|'p'
block|,
literal|'a'
block|,
literal|'c'
block|,
literal|'e'
block|}
decl_stmt|;
name|linkContentHandler
operator|.
name|characters
argument_list|(
name|anchorText
argument_list|,
literal|1
argument_list|,
name|anchorText
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|linkContentHandler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"a"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"No white space"
argument_list|,
name|linkContentHandler
operator|.
name|getLinks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/TIKA-975">TIKA-975</a>      */
specifier|public
name|void
name|testDefaultBehavior
parameter_list|()
throws|throws
name|Exception
block|{
name|LinkContentHandler
name|linkContentHandler
init|=
operator|new
name|LinkContentHandler
argument_list|()
decl_stmt|;
name|linkContentHandler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"a"
argument_list|,
literal|""
argument_list|,
operator|new
name|AttributesImpl
argument_list|()
argument_list|)
expr_stmt|;
name|char
index|[]
name|anchorText
init|=
block|{
literal|' '
block|,
literal|'a'
block|,
literal|'n'
block|,
literal|'c'
block|,
literal|'h'
block|,
literal|'o'
block|,
literal|'r'
block|,
literal|' '
block|}
decl_stmt|;
name|linkContentHandler
operator|.
name|characters
argument_list|(
name|anchorText
argument_list|,
literal|0
argument_list|,
name|anchorText
operator|.
name|length
argument_list|)
expr_stmt|;
name|linkContentHandler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"a"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|" anchor "
argument_list|,
name|linkContentHandler
operator|.
name|getLinks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
