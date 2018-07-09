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
name|mock
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|Field
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
name|sax
operator|.
name|XHTMLContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
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

begin_comment
comment|/**  * only parses vowels as specified in "vowel" field.  */
end_comment

begin_class
specifier|public
class|class
name|VowelParser
extends|extends
name|MockParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|vowel
init|=
literal|"aeiou"
decl_stmt|;
specifier|protected
name|void
name|write
parameter_list|(
name|Node
name|action
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
name|NamedNodeMap
name|attrs
init|=
name|action
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Node
name|eNode
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"element"
argument_list|)
decl_stmt|;
name|String
name|elementType
init|=
literal|"p"
decl_stmt|;
if|if
condition|(
name|eNode
operator|!=
literal|null
condition|)
block|{
name|elementType
operator|=
name|eNode
operator|.
name|getTextContent
argument_list|()
expr_stmt|;
block|}
name|String
name|text
init|=
name|action
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?i)(["
operator|+
name|vowel
operator|+
literal|"])"
argument_list|)
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

