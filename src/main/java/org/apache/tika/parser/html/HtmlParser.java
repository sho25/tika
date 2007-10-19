begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|html
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|exception
operator|.
name|TikaException
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
name|parser
operator|.
name|Parser
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
name|ContentHandlerDecorator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|cyberneko
operator|.
name|html
operator|.
name|parsers
operator|.
name|SAXParser
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
name|InputSource
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
comment|/**  * Simple HTML parser that extracts title.  */
end_comment

begin_class
specifier|public
class|class
name|HtmlParser
implements|implements
name|Parser
block|{
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
specifier|final
name|SAXParser
name|parser
init|=
operator|new
name|SAXParser
argument_list|()
decl_stmt|;
specifier|final
name|InputSource
name|source
decl_stmt|;
name|Reader
name|utf8Reader
decl_stmt|;
try|try
block|{
name|utf8Reader
operator|=
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|Utils
operator|.
name|getUTF8Reader
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|ex
parameter_list|)
block|{
name|utf8Reader
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|utf8Reader
operator|==
literal|null
condition|)
block|{
name|source
operator|=
operator|new
name|InputSource
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|source
operator|=
operator|new
name|InputSource
argument_list|(
name|utf8Reader
argument_list|)
expr_stmt|;
block|}
name|parser
operator|.
name|setContentHandler
argument_list|(
operator|new
name|TitleExtractingContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|TitleExtractingContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TAG_TITLE
init|=
literal|"TITLE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TAG_HEAD
init|=
literal|"HEAD"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TAG_HTML
init|=
literal|"HTML"
decl_stmt|;
specifier|private
name|Phase
name|phase
init|=
name|Phase
operator|.
name|START
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
specifier|private
name|StringBuilder
name|title
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
specifier|static
enum|enum
name|Phase
block|{
name|START
block|,
name|HTML
block|,
name|HEAD
block|,
name|TITLE
block|,
name|IGNORE
block|;         }
specifier|public
name|TitleExtractingContentHandler
parameter_list|(
specifier|final
name|ContentHandler
name|handler
parameter_list|,
specifier|final
name|Metadata
name|metadata
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
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
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
name|START
case|:
if|if
condition|(
name|TAG_HTML
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|phase
operator|=
name|Phase
operator|.
name|HTML
expr_stmt|;
block|}
break|break;
case|case
name|HTML
case|:
if|if
condition|(
name|TAG_HEAD
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|phase
operator|=
name|Phase
operator|.
name|HEAD
expr_stmt|;
block|}
break|break;
case|case
name|HEAD
case|:
if|if
condition|(
name|TAG_TITLE
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|phase
operator|=
name|Phase
operator|.
name|TITLE
expr_stmt|;
block|}
break|break;
block|}
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|name
argument_list|,
name|atts
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
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
name|TITLE
case|:
name|title
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
break|break;
block|}
name|super
operator|.
name|characters
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
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
name|TITLE
case|:
if|if
condition|(
name|TAG_TITLE
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|phase
operator|=
name|Phase
operator|.
name|IGNORE
expr_stmt|;
block|}
break|break;
block|}
name|super
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|name
argument_list|)
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
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|,
name|title
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

