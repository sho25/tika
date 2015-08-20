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
name|feed
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
name|Collections
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
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|CloseShieldInputStream
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
name|metadata
operator|.
name|TikaCoreProperties
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|AbstractParser
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
name|ParseContext
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

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|syndication
operator|.
name|feed
operator|.
name|synd
operator|.
name|SyndContent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|syndication
operator|.
name|feed
operator|.
name|synd
operator|.
name|SyndEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|syndication
operator|.
name|feed
operator|.
name|synd
operator|.
name|SyndFeed
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|syndication
operator|.
name|io
operator|.
name|FeedException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|syndication
operator|.
name|io
operator|.
name|SyndFeedInput
import|;
end_import

begin_comment
comment|/**  * Feed parser.  *<p>  * Uses Rome for parsing the feeds. A feed description is put in a paragraph  * with its link and title in an anchor.  */
end_comment

begin_class
specifier|public
class|class
name|FeedParser
extends|extends
name|AbstractParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3785361933034525186L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"rss+xml"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"atom+xml"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
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
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
comment|// set the encoding?
try|try
block|{
name|SyndFeed
name|feed
init|=
operator|new
name|SyndFeedInput
argument_list|()
operator|.
name|build
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|title
init|=
name|stripTags
argument_list|(
name|feed
operator|.
name|getTitleEx
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|description
init|=
name|stripTags
argument_list|(
name|feed
operator|.
name|getDescriptionEx
argument_list|()
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|,
name|description
argument_list|)
expr_stmt|;
comment|// store the other fields in the metadata
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|e
range|:
name|feed
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|SyndEntry
name|entry
init|=
operator|(
name|SyndEntry
operator|)
name|e
decl_stmt|;
name|String
name|link
init|=
name|entry
operator|.
name|getLink
argument_list|()
decl_stmt|;
if|if
condition|(
name|link
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"li"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"a"
argument_list|,
literal|"href"
argument_list|,
name|link
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|stripTags
argument_list|(
name|entry
operator|.
name|getTitleEx
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|SyndContent
name|content
init|=
name|entry
operator|.
name|getDescription
argument_list|()
decl_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|stripTags
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"li"
argument_list|)
expr_stmt|;
block|}
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FeedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"RSS parse error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|String
name|stripTags
parameter_list|(
name|SyndContent
name|c
parameter_list|)
block|{
if|if
condition|(
name|c
operator|==
literal|null
condition|)
return|return
literal|""
return|;
name|String
name|value
init|=
name|c
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
index|[]
name|parts
init|=
name|value
operator|.
name|split
argument_list|(
literal|"<[^>]*>"
argument_list|)
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|part
range|:
name|parts
control|)
name|buf
operator|.
name|append
argument_list|(
name|part
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
end_class

end_unit

