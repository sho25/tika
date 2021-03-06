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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|XHTMLContentHandler
operator|.
name|XHTML
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
name|LinkedList
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Content handler that collects links from an XHTML document.  */
end_comment

begin_class
specifier|public
class|class
name|LinkContentHandler
extends|extends
name|DefaultHandler
block|{
comment|/**      * Stack of link builders, one for each level of nested links currently      * being processed. A usual case of a nested link would be a hyperlinked      * image (<code>&a href="..."&gt;&lt;img src="..."&gt;&lt;&gt;</code>),      * but it's possible (though unlikely) for also other kinds of nesting      * to occur.      */
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|LinkBuilder
argument_list|>
name|builderStack
init|=
operator|new
name|LinkedList
argument_list|<
name|LinkBuilder
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Collected links */
specifier|private
specifier|final
name|List
argument_list|<
name|Link
argument_list|>
name|links
init|=
operator|new
name|ArrayList
argument_list|<
name|Link
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Whether to collapse whitespace in anchor text */
specifier|private
name|boolean
name|collapseWhitespaceInAnchor
decl_stmt|;
comment|/**      * Default constructor      */
specifier|public
name|LinkContentHandler
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Default constructor      *      * @param collapseWhitespaceInAnchor      */
specifier|public
name|LinkContentHandler
parameter_list|(
name|boolean
name|collapseWhitespaceInAnchor
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|collapseWhitespaceInAnchor
operator|=
name|collapseWhitespaceInAnchor
expr_stmt|;
block|}
comment|/**      * Returns the list of collected links.      *      * @return collected links      */
specifier|public
name|List
argument_list|<
name|Link
argument_list|>
name|getLinks
parameter_list|()
block|{
return|return
name|links
return|;
block|}
comment|//-------------------------------------------------------< ContentHandler>
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
name|local
parameter_list|,
name|String
name|name
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"a"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|LinkBuilder
name|builder
init|=
operator|new
name|LinkBuilder
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setURI
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"href"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setTitle
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setRel
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"rel"
argument_list|)
argument_list|)
expr_stmt|;
name|builderStack
operator|.
name|addFirst
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"link"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|LinkBuilder
name|builder
init|=
operator|new
name|LinkBuilder
argument_list|(
literal|"link"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setURI
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"href"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setRel
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"rel"
argument_list|)
argument_list|)
expr_stmt|;
name|builderStack
operator|.
name|addFirst
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"script"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
if|if
condition|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"src"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LinkBuilder
name|builder
init|=
operator|new
name|LinkBuilder
argument_list|(
literal|"script"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setURI
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"src"
argument_list|)
argument_list|)
expr_stmt|;
name|builderStack
operator|.
name|addFirst
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"iframe"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|LinkBuilder
name|builder
init|=
operator|new
name|LinkBuilder
argument_list|(
literal|"iframe"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setURI
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"src"
argument_list|)
argument_list|)
expr_stmt|;
name|builderStack
operator|.
name|addFirst
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"img"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|LinkBuilder
name|builder
init|=
operator|new
name|LinkBuilder
argument_list|(
literal|"img"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setURI
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"src"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setTitle
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setRel
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"rel"
argument_list|)
argument_list|)
expr_stmt|;
name|builderStack
operator|.
name|addFirst
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|String
name|alt
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"alt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|alt
operator|!=
literal|null
condition|)
block|{
name|char
index|[]
name|ch
init|=
name|alt
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|characters
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
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
block|{
for|for
control|(
name|LinkBuilder
name|builder
range|:
name|builderStack
control|)
block|{
name|builder
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
block|{
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
name|local
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|builderStack
operator|.
name|isEmpty
argument_list|()
operator|&&
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"a"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
operator|||
literal|"img"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
operator|||
literal|"link"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
operator|||
literal|"script"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
operator|||
literal|"iframe"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
comment|// ensure this is the correct builder. not all</script> tags correspond
comment|// to a LinkBuilder, e.g. for embedded scripts
if|if
condition|(
name|builderStack
operator|.
name|getFirst
argument_list|()
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|LinkBuilder
name|builder
init|=
name|builderStack
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|links
operator|.
name|add
argument_list|(
name|builder
operator|.
name|getLink
argument_list|(
name|collapseWhitespaceInAnchor
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

