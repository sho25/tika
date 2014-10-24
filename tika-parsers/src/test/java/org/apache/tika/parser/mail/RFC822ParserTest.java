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
name|mail
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|never
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

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
name|InputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|stream
operator|.
name|MimeConfig
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
name|parser
operator|.
name|ocr
operator|.
name|TesseractOCRParserTest
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
name|XHTMLContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_class
specifier|public
class|class
name|RFC822ParserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|Parser
name|parser
init|=
operator|new
name|RFC822Parser
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
name|getStream
argument_list|(
literal|"test-documents/testRFC822"
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
name|mock
argument_list|(
name|DefaultHandler
operator|.
name|class
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
name|verify
argument_list|(
name|handler
argument_list|)
operator|.
name|startDocument
argument_list|()
expr_stmt|;
comment|//just one body
name|verify
argument_list|(
name|handler
argument_list|)
operator|.
name|startElement
argument_list|(
name|eq
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"p"
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"p"
argument_list|)
argument_list|,
name|any
argument_list|(
name|Attributes
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|handler
argument_list|)
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"p"
argument_list|,
literal|"p"
argument_list|)
expr_stmt|;
comment|//no multi-part body parts
name|verify
argument_list|(
name|handler
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|startElement
argument_list|(
name|eq
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"div"
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"div"
argument_list|)
argument_list|,
name|any
argument_list|(
name|Attributes
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|handler
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"div"
argument_list|,
literal|"div"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|handler
argument_list|)
operator|.
name|endDocument
argument_list|()
expr_stmt|;
comment|//note no leading spaces, and no quotes
name|assertEquals
argument_list|(
literal|"Julien Nioche (JIRA)<jira@apache.org>"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[jira] Commented: (TIKA-461) RFC822 messages not parsed"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[jira] Commented: (TIKA-461) RFC822 messages not parsed"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipart
parameter_list|()
block|{
name|Parser
name|parser
init|=
operator|new
name|RFC822Parser
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
name|getStream
argument_list|(
literal|"test-documents/testRFC822-multipart"
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
name|mock
argument_list|(
name|XHTMLContentHandler
operator|.
name|class
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
name|verify
argument_list|(
name|handler
argument_list|)
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|int
name|bodyExpectedTimes
init|=
literal|4
decl_stmt|,
name|multipackExpectedTimes
init|=
literal|5
decl_stmt|;
comment|// TIKA-1422. TesseractOCRParser interferes with the number of times the handler is invoked.
comment|// But, different versions of Tesseract lead to a different number of invocations. So, we
comment|// only verify the handler if Tesseract cannot run.
if|if
condition|(
operator|!
name|TesseractOCRParserTest
operator|.
name|canRun
argument_list|()
condition|)
block|{
name|verify
argument_list|(
name|handler
argument_list|,
name|times
argument_list|(
name|bodyExpectedTimes
argument_list|)
argument_list|)
operator|.
name|startElement
argument_list|(
name|eq
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"div"
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"div"
argument_list|)
argument_list|,
name|any
argument_list|(
name|Attributes
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|handler
argument_list|,
name|times
argument_list|(
name|bodyExpectedTimes
argument_list|)
argument_list|)
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"div"
argument_list|,
literal|"div"
argument_list|)
expr_stmt|;
block|}
name|verify
argument_list|(
name|handler
argument_list|,
name|times
argument_list|(
name|multipackExpectedTimes
argument_list|)
argument_list|)
operator|.
name|startElement
argument_list|(
name|eq
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"p"
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"p"
argument_list|)
argument_list|,
name|any
argument_list|(
name|Attributes
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|handler
argument_list|,
name|times
argument_list|(
name|multipackExpectedTimes
argument_list|)
argument_list|)
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"p"
argument_list|,
literal|"p"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|handler
argument_list|)
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//repeat, this time looking at content
name|parser
operator|=
operator|new
name|RFC822Parser
argument_list|()
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|stream
operator|=
name|getStream
argument_list|(
literal|"test-documents/testRFC822-multipart"
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BodyContentHandler
argument_list|()
expr_stmt|;
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
comment|//tests correct decoding of quoted printable text, including UTF-8 bytes into Unicode
name|String
name|bodyText
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|bodyText
operator|.
name|contains
argument_list|(
literal|"body 1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bodyText
operator|.
name|contains
argument_list|(
literal|"body 2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bodyText
operator|.
name|contains
argument_list|(
literal|"R0lGODlhNgE8AMQAA"
argument_list|)
argument_list|)
expr_stmt|;
comment|//part of encoded gif
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuotedPrintable
parameter_list|()
block|{
name|Parser
name|parser
init|=
operator|new
name|RFC822Parser
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
name|getStream
argument_list|(
literal|"test-documents/testRFC822_quoted"
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
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
comment|//tests correct decoding of quoted printable text, including UTF-8 bytes into Unicode
name|String
name|bodyText
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|bodyText
operator|.
name|contains
argument_list|(
literal|"D\u00FCsseldorf has non-ascii."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bodyText
operator|.
name|contains
argument_list|(
literal|"Lines can be split like this."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bodyText
operator|.
name|contains
argument_list|(
literal|"Spaces at the end of a line \r\nmust be encoded.\r\n"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bodyText
operator|.
name|contains
argument_list|(
literal|"="
argument_list|)
argument_list|)
expr_stmt|;
comment|//there should be no escape sequences
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBase64
parameter_list|()
block|{
name|Parser
name|parser
init|=
operator|new
name|RFC822Parser
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
name|getStream
argument_list|(
literal|"test-documents/testRFC822_base64"
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
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
comment|//tests correct decoding of base64 text, including ISO-8859-1 bytes into Unicode
name|assertTrue
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Here is some text, with international characters, voil\u00E0!"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testI18NHeaders
parameter_list|()
block|{
name|Parser
name|parser
init|=
operator|new
name|RFC822Parser
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
name|getStream
argument_list|(
literal|"test-documents/testRFC822_i18nheaders"
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
name|mock
argument_list|(
name|DefaultHandler
operator|.
name|class
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
comment|//tests correct decoding of internationalized headers, both
comment|//quoted-printable (Q) and Base64 (B).
name|assertEquals
argument_list|(
literal|"Keld J\u00F8rn Simonsen<keld@dkuug.dk>"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If you can read this you understand the example."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If you can read this you understand the example."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * The from isn't in the usual form.      * See TIKA-618      */
annotation|@
name|Test
specifier|public
name|void
name|testUnusualFromAddress
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|RFC822Parser
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
name|getStream
argument_list|(
literal|"test-documents/testRFC822_oddfrom"
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
name|mock
argument_list|(
name|DefaultHandler
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|"Saved by Windows Internet Explorer 7"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Air Permit Programs | Air& Radiation | US EPA"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Air Permit Programs | Air& Radiation | US EPA"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for TIKA-640, increase header max beyond 10k bytes      */
annotation|@
name|Test
specifier|public
name|void
name|testLongHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|inputBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2000
condition|;
operator|++
name|i
control|)
block|{
name|inputBuilder
operator|.
name|append
argument_list|(
comment|//len> 50
literal|"really really really really really really long name "
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|inputBuilder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|(
literal|"From: "
operator|+
name|name
operator|+
literal|"\r\n\r\n"
operator|)
operator|.
name|getBytes
argument_list|(
literal|"US-ASCII"
argument_list|)
decl_stmt|;
name|Parser
name|parser
init|=
operator|new
name|RFC822Parser
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|DefaultHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|expected
parameter_list|)
block|{         }
name|MimeConfig
name|config
init|=
operator|new
name|MimeConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setMaxHeaderLen
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMaxLineLen
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|context
operator|.
name|set
argument_list|(
name|MimeConfig
operator|.
name|class
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
operator|.
name|trim
argument_list|()
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for TIKA-678 - not all headers may be present      */
annotation|@
name|Test
specifier|public
name|void
name|testSomeMissingHeaders
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|RFC822Parser
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
name|getStream
argument_list|(
literal|"test-documents/testRFC822-limitedheaders"
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|metadata
operator|.
name|isMultiValued
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xyz"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|metadata
operator|.
name|isMultiValued
argument_list|(
name|Metadata
operator|.
name|MESSAGE_FROM
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xyz"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|Metadata
operator|.
name|MESSAGE_FROM
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|Metadata
operator|.
name|MESSAGE_FROM
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|metadata
operator|.
name|isMultiValued
argument_list|(
name|Metadata
operator|.
name|MESSAGE_TO
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|Metadata
operator|.
name|MESSAGE_TO
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"def"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|Metadata
operator|.
name|MESSAGE_TO
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcd"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcd"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"bar biz bat"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|InputStream
name|getStream
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

