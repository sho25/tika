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
name|microsoft
package|;
end_package

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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

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
name|SAXTransformerFactory
import|;
end_import

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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
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
name|AutoDetectParser
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
name|BodyContentHandler
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

begin_comment
comment|/**  * Test case for parsing Outlook files.  */
end_comment

begin_class
specifier|public
class|class
name|OutlookParserTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testOutlookParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
comment|// Should auto-detect!
name|ContentHandler
name|handler
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
name|OutlookParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test-outlook.msg"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
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
literal|"application/vnd.ms-outlook"
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
literal|"Microsoft Outlook Express 6"
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
literal|"Nouvel utilisateur de Outlook Express"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|MESSAGE_RECIPIENT_ADDRESS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"L'\u00C9quipe Microsoft Outlook Express"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
comment|// Stored as Thu, 5 Apr 2007 09:26:06 -0700
name|assertEquals
argument_list|(
literal|"2007-04-05T16:26:06Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Microsoft Outlook Express 6"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"L'\u00C9quipe Microsoft Outlook Express"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Nouvel utilisateur de Outlook Express"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Messagerie et groupes de discussion"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for TIKA-197      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-197">TIKA-197</a>      */
specifier|public
name|void
name|testMultipleCopies
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
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
name|OutlookParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testMSG.msg"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
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
literal|"application/vnd.ms-outlook"
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"From"
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for TIKA-395, to ensure parser works for new Outlook formats.       *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-395">TIKA-395</a>      */
specifier|public
name|void
name|testOutlookNew
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
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
name|OutlookParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test-outlook2003.msg"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
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
literal|"application/vnd.ms-outlook"
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
literal|"Welcome to Microsoft Office Outlook 2003"
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Outlook 2003"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Streamlined Mail Experience"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Navigation Pane"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOutlookHTMLVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|// Check the HTML version
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|SAXTransformerFactory
name|factory
init|=
operator|(
name|SAXTransformerFactory
operator|)
name|SAXTransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
operator|new
name|StreamResult
argument_list|(
name|sw
argument_list|)
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|OutlookParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testMSG_chinese.msg"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
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
comment|// As the HTML version should have been processed, ensure
comment|//  we got some of the links
name|String
name|content
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"<dd>tests.chang@fengttt.com</dd>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"<p>Alfresco MSG format testing"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"<li>1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"<li>2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure we don't have nested html docs
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|content
operator|.
name|split
argument_list|(
literal|"<body>"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//assertEquals(2, content.split("<\\/body>").length); // TODO Fix
block|}
block|}
end_class

end_unit

