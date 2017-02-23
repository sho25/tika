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
name|txt
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|ISO_8859_1
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|assertNull
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
name|StringWriter
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
name|TikaTest
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
name|config
operator|.
name|TikaConfig
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
name|detect
operator|.
name|CompositeEncodingDetector
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
name|detect
operator|.
name|EncodingDetector
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
name|AbstractEncodingDetectorParser
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|WriteOutContentHandler
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
name|TXTParserTest
extends|extends
name|TikaTest
block|{
specifier|private
name|Parser
name|parser
init|=
operator|new
name|TXTParser
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testEnglishText
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"Hello, World! This is simple UTF-8 text content written"
operator|+
literal|" in English to test autodetection of both the character"
operator|+
literal|" encoding and the language of the input stream."
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|ISO_8859_1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|WriteOutContentHandler
argument_list|(
name|writer
argument_list|)
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"text/plain; charset=ISO-8859-1"
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
comment|// TIKA-501: Remove language detection from TXTParser
name|assertNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_LANGUAGE
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|LANGUAGE
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Hello"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"World"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"autodetection"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"stream"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUTF8Text
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"I\u00F1t\u00EBrn\u00E2ti\u00F4n\u00E0liz\u00E6ti\u00F8n"
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
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
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
literal|"text/plain; charset=UTF-8"
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
literal|"UTF-8"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
comment|// deprecated
name|assertContains
argument_list|(
name|text
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyText
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
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
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
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/plain; charset=UTF-8"
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
literal|"\n"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for the heuristics that we use to assign an eight-bit character      * encoding to mostly ASCII sequences. If a more specific match can not      * be made, a string with a CR(LF) in it is most probably windows-1252,      * otherwise ISO-8859-1, except if it contains the currency/euro symbol      * (byte 0xa4) in which case it's more likely to be ISO-8859-15.      */
annotation|@
name|Test
specifier|public
name|void
name|testLatinDetectionHeuristics
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|windows
init|=
literal|"test\r\n"
decl_stmt|;
name|String
name|unix
init|=
literal|"test\n"
decl_stmt|;
name|String
name|euro
init|=
literal|"test \u20ac\n"
decl_stmt|;
name|Metadata
name|metadata
decl_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|windows
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-15"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
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
literal|"text/plain; charset=windows-1252"
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
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|unix
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-15"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
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
literal|"text/plain; charset=ISO-8859-1"
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
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|euro
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-15"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
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
literal|"text/plain; charset=ISO-8859-15"
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
block|}
comment|/**      * Test case for TIKA-240: Drop the BOM when extracting plain text      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-240">TIKA-240</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testDropByteOrderMark
parameter_list|()
throws|throws
name|Exception
block|{
name|assertExtractText
argument_list|(
literal|"UTF-8 BOM"
argument_list|,
literal|"test"
argument_list|,
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xEF
block|,
operator|(
name|byte
operator|)
literal|0xBB
block|,
operator|(
name|byte
operator|)
literal|0xBF
block|,
literal|'t'
block|,
literal|'e'
block|,
literal|'s'
block|,
literal|'t'
block|}
argument_list|)
expr_stmt|;
name|assertExtractText
argument_list|(
literal|"UTF-16 BE BOM"
argument_list|,
literal|"test"
argument_list|,
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xFE
block|,
operator|(
name|byte
operator|)
literal|0xFF
block|,
literal|0
block|,
literal|'t'
block|,
literal|0
block|,
literal|'e'
block|,
literal|0
block|,
literal|'s'
block|,
literal|0
block|,
literal|'t'
block|}
argument_list|)
expr_stmt|;
name|assertExtractText
argument_list|(
literal|"UTF-16 LE BOM"
argument_list|,
literal|"test"
argument_list|,
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xFF
block|,
operator|(
name|byte
operator|)
literal|0xFE
block|,
literal|'t'
block|,
literal|0
block|,
literal|'e'
block|,
literal|0
block|,
literal|'s'
block|,
literal|0
block|,
literal|'t'
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for TIKA-335: using incoming charset      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-335">TIKA-335</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testUseIncomingCharsetAsHint
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Could be ISO 8859-1 or ISO 8859-15 or ...
comment|// u00e1 is latin small letter a with acute
specifier|final
name|String
name|test2
init|=
literal|"the name is \u00e1ndre"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|test2
operator|.
name|getBytes
argument_list|(
name|ISO_8859_1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
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
literal|"text/plain; charset=ISO-8859-1"
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
literal|"ISO-8859-1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
comment|// deprecated
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/plain; charset=ISO-8859-15"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|test2
operator|.
name|getBytes
argument_list|(
name|ISO_8859_1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
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
literal|"text/plain; charset=ISO-8859-15"
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
literal|"ISO-8859-15"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
comment|// deprecated
block|}
comment|/**      * Test case for TIKA-341: using charset in content-type      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-341">TIKA-341</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testUsingCharsetInContentTypeHeader
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Could be ISO 8859-1 or ISO 8859-15 or ...
comment|// u00e1 is latin small letter a with acute
specifier|final
name|String
name|test2
init|=
literal|"the name is \u00e1ndre"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|test2
operator|.
name|getBytes
argument_list|(
name|ISO_8859_1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
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
literal|"text/plain; charset=ISO-8859-1"
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
literal|"ISO-8859-1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
comment|// deprecated
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/html; charset=ISO-8859-15"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|test2
operator|.
name|getBytes
argument_list|(
name|ISO_8859_1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
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
literal|"text/html; charset=ISO-8859-15"
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
literal|"ISO-8859-15"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
comment|// deprecated
block|}
specifier|private
name|void
name|assertExtractText
parameter_list|(
name|String
name|msg
parameter_list|,
name|String
name|expected
parameter_list|,
name|byte
index|[]
name|input
parameter_list|)
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
block|{
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|// Ignore the whitespace added by XHTMLContentHandler
block|}
block|}
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|input
argument_list|)
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
name|msg
argument_list|,
name|expected
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test case for TIKA-339: don't override incoming language      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-335">TIKA-335</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testRetainIncomingLanguage
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|test
init|=
literal|"Simple Content"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|LANGUAGE
argument_list|,
literal|"en"
argument_list|)
expr_stmt|;
name|parser
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
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
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
literal|"en"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|LANGUAGE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCP866
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|TXTParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/russian.cp866.txt"
argument_list|)
argument_list|,
operator|new
name|WriteOutContentHandler
argument_list|(
name|writer
argument_list|)
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
literal|"text/plain; charset=IBM866"
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEBCDIC_CP500
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|TXTParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/english.cp500.txt"
argument_list|)
argument_list|,
operator|new
name|WriteOutContentHandler
argument_list|(
name|writer
argument_list|)
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
literal|"text/plain; charset=IBM500"
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
comment|// Additional check that it isn't too eager on short blocks of text
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"<html><body>hello world</body></html>"
operator|.
name|getBytes
argument_list|(
name|ISO_8859_1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|WriteOutContentHandler
argument_list|(
name|writer
argument_list|)
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
literal|"text/plain; charset=ISO-8859-1"
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
block|}
comment|/**      * Test case for TIKA-771: "Hello, World!" in UTF-8/ASCII gets detected as IBM500      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-771">TIKA-771</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testCharsetDetectionWithShortSnipet
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|text
init|=
literal|"Hello, World!"
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
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
literal|"text/plain; charset=ISO-8859-1"
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
comment|// Now verify that if we tell the parser the encoding is UTF-8, that's what
comment|// we get back (see TIKA-868)
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/binary; charset=UTF-8"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
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
literal|"application/binary; charset=UTF-8"
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
block|}
comment|//TIKA-2047
annotation|@
name|Test
specifier|public
name|void
name|testSubclassingMimeTypesRemain
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testVCalendar.vcs"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"text/x-vcalendar; charset=ISO-8859-1"
argument_list|,
name|r
operator|.
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEncodingDetectorsAreLoaded
parameter_list|()
block|{
name|EncodingDetector
name|encodingDetector
init|=
operator|(
operator|(
name|AbstractEncodingDetectorParser
operator|)
name|parser
operator|)
operator|.
name|getEncodingDetector
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|encodingDetector
operator|instanceof
name|CompositeEncodingDetector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEncodingDetectorConfigurability
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|tikaConfig
init|=
operator|new
name|TikaConfig
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/config/TIKA-2273-no-icu4j-encoding-detector.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|AutoDetectParser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|(
name|tikaConfig
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
name|getXML
argument_list|(
literal|"english.cp500.txt"
argument_list|,
name|p
argument_list|)
operator|.
name|metadata
decl_stmt|;
name|fail
argument_list|(
literal|"can't detect w/out ICU"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|assertContains
argument_list|(
literal|"Failed to detect"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

