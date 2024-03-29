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
name|util
operator|.
name|Stack
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
name|xml
operator|.
name|sax
operator|.
name|Attributes
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
name|DefaultHandler
import|;
end_import

begin_class
specifier|public
class|class
name|DIFContentHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|NEWLINE
init|=
operator|new
name|char
index|[]
block|{
literal|'\n'
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|TABSPACE
init|=
operator|new
name|char
index|[]
block|{
literal|'\t'
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Attributes
name|EMPTY_ATTRIBUTES
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
specifier|private
name|Stack
argument_list|<
name|String
argument_list|>
name|treeStack
decl_stmt|;
specifier|private
name|Stack
argument_list|<
name|String
argument_list|>
name|dataStack
decl_stmt|;
specifier|private
specifier|final
name|ContentHandler
name|delegate
decl_stmt|;
specifier|private
name|boolean
name|isLeaf
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
specifier|public
name|DIFContentHandler
parameter_list|(
name|ContentHandler
name|delegate
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|isLeaf
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|treeStack
operator|=
operator|new
name|Stack
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|dataStack
operator|=
operator|new
name|Stack
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Locator
name|locator
parameter_list|)
block|{
name|delegate
operator|.
name|setDocumentLocator
argument_list|(
name|locator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|String
name|value
init|=
operator|(
operator|new
name|String
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|this
operator|.
name|dataStack
operator|.
name|push
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|treeStack
operator|.
name|peek
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Entry_Title"
argument_list|)
condition|)
block|{
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|NEWLINE
argument_list|,
literal|0
argument_list|,
name|NEWLINE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|TABSPACE
argument_list|,
literal|0
argument_list|,
name|TABSPACE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"h3"
argument_list|,
literal|"h3"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|String
name|title
init|=
literal|"Title: "
decl_stmt|;
name|title
operator|=
name|title
operator|+
name|value
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|title
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|title
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|"h3"
argument_list|,
literal|"h3"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|treeStack
operator|.
name|peek
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Southernmost_Latitude"
argument_list|)
operator|||
name|this
operator|.
name|treeStack
operator|.
name|peek
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Northernmost_Latitude"
argument_list|)
operator|||
name|this
operator|.
name|treeStack
operator|.
name|peek
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Westernmost_Longitude"
argument_list|)
operator|||
name|this
operator|.
name|treeStack
operator|.
name|peek
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Easternmost_Longitude"
argument_list|)
condition|)
block|{
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|NEWLINE
argument_list|,
literal|0
argument_list|,
name|NEWLINE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|TABSPACE
argument_list|,
literal|0
argument_list|,
name|TABSPACE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|TABSPACE
argument_list|,
literal|0
argument_list|,
name|TABSPACE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"tr"
argument_list|,
literal|"tr"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"td"
argument_list|,
literal|"td"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|this
operator|.
name|treeStack
operator|.
name|peek
argument_list|()
operator|+
literal|" : "
decl_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|key
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|"td"
argument_list|,
literal|"td"
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"td"
argument_list|,
literal|"td"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|"td"
argument_list|,
literal|"td"
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|"tr"
argument_list|,
literal|"tr"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|ignorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
name|this
operator|.
name|isLeaf
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"Spatial_Coverage"
argument_list|)
condition|)
block|{
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|NEWLINE
argument_list|,
literal|0
argument_list|,
name|NEWLINE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|TABSPACE
argument_list|,
literal|0
argument_list|,
name|TABSPACE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"h3"
argument_list|,
literal|"h3"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|String
name|value
init|=
literal|"Geographic Data: "
decl_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|"h3"
argument_list|,
literal|"h3"
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|NEWLINE
argument_list|,
literal|0
argument_list|,
name|NEWLINE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|TABSPACE
argument_list|,
literal|0
argument_list|,
name|TABSPACE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"table"
argument_list|,
literal|"table"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|treeStack
operator|.
name|push
argument_list|(
name|localName
argument_list|)
expr_stmt|;
block|}
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
name|localName
operator|.
name|equals
argument_list|(
literal|"Spatial_Coverage"
argument_list|)
condition|)
block|{
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|NEWLINE
argument_list|,
literal|0
argument_list|,
name|NEWLINE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|characters
argument_list|(
name|TABSPACE
argument_list|,
literal|0
argument_list|,
name|TABSPACE
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|"table"
argument_list|,
literal|"table"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|isLeaf
condition|)
block|{
name|Stack
argument_list|<
name|String
argument_list|>
name|tempStack
init|=
operator|(
name|Stack
argument_list|<
name|String
argument_list|>
operator|)
name|this
operator|.
name|treeStack
operator|.
name|clone
argument_list|()
decl_stmt|;
name|String
name|key
init|=
literal|""
decl_stmt|;
while|while
condition|(
operator|!
name|tempStack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|key
operator|=
name|tempStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|key
operator|=
name|tempStack
operator|.
name|pop
argument_list|()
operator|+
literal|"-"
operator|+
name|key
expr_stmt|;
block|}
block|}
name|String
name|value
init|=
name|this
operator|.
name|dataStack
operator|.
name|peek
argument_list|()
decl_stmt|;
name|this
operator|.
name|metadata
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|isLeaf
operator|=
literal|false
expr_stmt|;
block|}
name|this
operator|.
name|treeStack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|this
operator|.
name|dataStack
operator|.
name|pop
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
block|{
name|delegate
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|delegate
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

