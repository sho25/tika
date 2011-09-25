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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
comment|/**  * SAX event handler that serializes the HTML document to a character stream.  * The incoming SAX events are expected to be well-formed (properly nested,  * etc.) and valid HTML.  *  * @since Apache Tika 1.0  */
end_comment

begin_class
specifier|public
class|class
name|ToHTMLContentHandler
extends|extends
name|ToXMLContentHandler
block|{
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|EMPTY_ELEMENTS
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"area"
argument_list|,
literal|"base"
argument_list|,
literal|"basefont"
argument_list|,
literal|"br"
argument_list|,
literal|"col"
argument_list|,
literal|"frame"
argument_list|,
literal|"hr"
argument_list|,
literal|"img"
argument_list|,
literal|"input"
argument_list|,
literal|"isindex"
argument_list|,
literal|"link"
argument_list|,
literal|"meta"
argument_list|,
literal|"param"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|ToHTMLContentHandler
parameter_list|(
name|OutputStream
name|stream
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|super
argument_list|(
name|stream
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ToHTMLContentHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|inStartElement
condition|)
block|{
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|inStartElement
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|EMPTY_ELEMENTS
operator|.
name|contains
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|namespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
name|super
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

