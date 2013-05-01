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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TransformerHandler
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

begin_comment
comment|/**  * Content handler decorator which wraps a {@link TransformerHandler} in order to   * allow the<code>TITLE</code> tag to render as<code>&lt;title&gt;&lt;/title&gt;</code>  * rather than<code>&lt;title/&gt;</code> which is accomplished  * by calling the {@link TransformerHandler#characters(char[], int, int)} method  * with a<code>length</code> of 1 but a zero length char array.  *<p>  * This workaround is an unfortunate circumstance of the limitations imposed by the  * implementation of the XML serialization code in the JDK brought over from  * the xalan project which no longer allows for the specification of an   * alternate<code>content-handler</code> via xslt templates or other means.  *   * @see<a href="https://issues.apache.org/jira/browse/TIKA-725">TIKA-725</a>  */
end_comment

begin_class
specifier|public
class|class
name|ExpandedTitleContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
specifier|private
name|boolean
name|isTitleTagOpen
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TITLE_TAG
init|=
literal|"TITLE"
decl_stmt|;
specifier|public
name|ExpandedTitleContentHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ExpandedTitleContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
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
name|super
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|isTitleTagOpen
operator|=
literal|false
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
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
if|if
condition|(
name|TITLE_TAG
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
operator|&&
name|XHTMLContentHandler
operator|.
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|isTitleTagOpen
operator|=
literal|true
expr_stmt|;
block|}
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
if|if
condition|(
name|TITLE_TAG
operator|.
name|equalsIgnoreCase
argument_list|(
name|localName
argument_list|)
operator|&&
name|XHTMLContentHandler
operator|.
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|isTitleTagOpen
operator|=
literal|false
expr_stmt|;
block|}
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
if|if
condition|(
name|isTitleTagOpen
operator|&&
name|length
operator|==
literal|0
condition|)
block|{
comment|// Hack to close the title tag
try|try
block|{
name|super
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
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// Expected, just wanted to close the title tag
block|}
block|}
else|else
block|{
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
block|}
block|}
end_class

end_unit
