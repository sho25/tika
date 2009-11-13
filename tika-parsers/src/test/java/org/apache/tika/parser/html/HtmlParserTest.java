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
name|html
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|StringWriter
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
name|apache
operator|.
name|tika
operator|.
name|Tika
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
name|BodyContentHandler
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
name|TeeContentHandler
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
name|DefaultHandler
import|;
end_import

begin_class
specifier|public
class|class
name|HtmlParserTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testParseAscii
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/test-documents/testHTML.html"
decl_stmt|;
specifier|final
name|StringWriter
name|href
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|StringWriter
name|name
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|ContentHandler
name|body
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|HtmlParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|ContentHandler
name|link
init|=
operator|new
name|DefaultHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|u
parameter_list|,
name|String
name|l
parameter_list|,
name|String
name|n
parameter_list|,
name|Attributes
name|a
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"a"
operator|.
name|equals
argument_list|(
name|l
argument_list|)
condition|)
block|{
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|href
operator|.
name|append
argument_list|(
name|a
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|name
operator|.
name|append
argument_list|(
name|a
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
operator|new
name|HtmlParser
argument_list|()
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|TeeContentHandler
argument_list|(
name|body
argument_list|,
name|link
argument_list|)
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Title : Test Indexation Html"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Tika Developers"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"refresh"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://www.apache.org/"
argument_list|,
name|href
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test-anchor"
argument_list|,
name|name
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|body
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not contain expected text:"
operator|+
literal|"Test Indexation Html"
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"Test Indexation Html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Did not contain expected text:"
operator|+
literal|"Indexation du fichier"
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"Indexation du fichier"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|XtestParseUTF8
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|String
name|path
init|=
literal|"/test-documents/testXHTML_utf8.html"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
operator|new
name|Tika
argument_list|()
operator|.
name|parseToString
argument_list|(
name|HtmlParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not contain expected text:"
operator|+
literal|"Title : Tilte with UTF-8 chars öäå"
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"Title : Tilte with UTF-8 chars öäå"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Did not contain expected text:"
operator|+
literal|"Content with UTF-8 chars"
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"Content with UTF-8 chars"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Did not contain expected text:"
operator|+
literal|"åäö"
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"åäö"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testXhtmlParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/test-documents/testXHTML.html"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|String
name|content
init|=
operator|new
name|Tika
argument_list|()
operator|.
name|parseToString
argument_list|(
name|HtmlParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/xhtml+xml"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XHTML test document"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Tika Developers"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"refresh"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"ability of Apache Tika"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"extract content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"an XHTML document"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testParseEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|HtmlParser
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|handler
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for TIKA-210      * @see<a href="https://issues.apache.org/jira/browse/TIKA-210">TIKA-210</a>      */
specifier|public
name|void
name|testCharactersDirectlyUnderBodyElement
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"<html><body>test</body></html>"
decl_stmt|;
name|String
name|content
init|=
operator|new
name|Tika
argument_list|()
operator|.
name|parseToString
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|test
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for TIKA-287      * @see<a href="https://issues.apache.org/jira/browse/TIKA-287">TIKA-287</a>      */
specifier|public
name|void
name|testBaseHref
parameter_list|()
throws|throws
name|Exception
block|{
name|assertRelativeLink
argument_list|(
literal|"http://lucene.apache.org/tika/"
argument_list|,
literal|"http://lucene.apache.org/"
argument_list|,
literal|"tika/"
argument_list|)
expr_stmt|;
name|assertRelativeLink
argument_list|(
literal|"http://domain.com/?pid=1"
argument_list|,
literal|"http://domain.com"
argument_list|,
literal|"?pid=1"
argument_list|)
expr_stmt|;
name|assertRelativeLink
argument_list|(
literal|"http://domain.com/?pid=2"
argument_list|,
literal|"http://domain.com?pid=1"
argument_list|,
literal|"?pid=2"
argument_list|)
expr_stmt|;
name|assertRelativeLink
argument_list|(
literal|"http://domain.com/file.html"
argument_list|,
literal|"http://domain.com/path/"
argument_list|,
literal|"/file.html"
argument_list|)
expr_stmt|;
name|assertRelativeLink
argument_list|(
literal|"http://domain.com/path/file.html"
argument_list|,
literal|"http://domain.com/path/"
argument_list|,
literal|"./file.html"
argument_list|)
expr_stmt|;
name|assertRelativeLink
argument_list|(
literal|"http://domain.com/path/file.html"
argument_list|,
literal|"http://domain.com/path/"
argument_list|,
literal|"file.html"
argument_list|)
expr_stmt|;
name|assertRelativeLink
argument_list|(
literal|"http://domain2.com/newpath"
argument_list|,
literal|"http://domain.com/path/to/file"
argument_list|,
literal|"http://domain2.com/newpath"
argument_list|)
expr_stmt|;
comment|// See http://www.communities.hp.com/securitysoftware/blogs/jeff/archive/2007/12/19/RFC-1808-vs-2396-vs-3986_3A00_-Browsers-vs.-programing-languages.aspx
comment|// Also http://www.ietf.org/rfc/rfc3986.txt
comment|// Also http://issues.apache.org/jira/browse/NUTCH-566
comment|// Also http://issues.apache.org/jira/browse/NUTCH-436
name|assertRelativeLink
argument_list|(
literal|"http://domain.com/path/?pid=1"
argument_list|,
literal|"http://domain.com/path/"
argument_list|,
literal|"?pid=1"
argument_list|)
expr_stmt|;
name|assertRelativeLink
argument_list|(
literal|"http://domain.com/file?pid=1"
argument_list|,
literal|"http://domain.com/file"
argument_list|,
literal|"?pid=1"
argument_list|)
expr_stmt|;
name|assertRelativeLink
argument_list|(
literal|"http://domain.com/path/d;p?pid=1"
argument_list|,
literal|"http://domain.com/path/d;p?q#f"
argument_list|,
literal|"?pid=1"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertRelativeLink
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|base
parameter_list|,
name|String
name|relative
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"<html><head><base href=\""
operator|+
name|base
operator|+
literal|"\"></head>"
operator|+
literal|"<body><a href=\""
operator|+
name|relative
operator|+
literal|"\">test</a></body></html>"
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|links
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
operator|new
name|HtmlParser
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|test
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|u
parameter_list|,
name|String
name|l
parameter_list|,
name|String
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
block|{
if|if
condition|(
name|atts
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"href"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|links
operator|.
name|add
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"href"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|links
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|url
argument_list|,
name|links
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for TIKA-268      * @see<a href="https://issues.apache.org/jira/browse/TIKA-268">TIKA-268</a>      */
specifier|public
name|void
name|testWhitespaceBetweenTableCells
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"<html><body><table><tr><td>a</td><td>b</td></table></body></html>"
decl_stmt|;
name|String
name|content
init|=
operator|new
name|Tika
argument_list|()
operator|.
name|parseToString
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|test
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"ab"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

