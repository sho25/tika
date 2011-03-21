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
operator|.
name|ooxml
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
name|Locale
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
name|ContainerAwareDetector
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
name|io
operator|.
name|TikaInputStream
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
name|TikaMetadataKeys
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

begin_class
specifier|public
class|class
name|OOXMLParserTest
extends|extends
name|TestCase
block|{
specifier|private
name|Parser
name|parser
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|ContainerAwareDetector
name|detector
init|=
operator|new
name|ContainerAwareDetector
argument_list|(
name|config
operator|.
name|getMimeRepository
argument_list|()
argument_list|)
decl_stmt|;
name|parser
operator|=
operator|new
name|AutoDetectParser
argument_list|(
name|detector
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testExcel
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL.xlsx"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
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
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
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
literal|"Simple Excel document"
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
literal|"Keith Bennett"
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
literal|"Sample Excel Worksheet"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Numbers and their Squares"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"9.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"196"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"196.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaMetadataKeys
operator|.
name|PROTECTED
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testExcelFormats
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL-formats.xlsx"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Locale
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
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
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
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
comment|// Number #,##0.00
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"1,599.99"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"-1,599.99"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Currency $#,##0.00;[Red]($#,##0.00)
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"$1,599.99"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"$1,599.99)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Scientific 0.00E+00
comment|// poi<=3.8beta1 returns 1.98E08, newer versions return 1.98+E08
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"1.98E08"
argument_list|)
operator|||
name|content
operator|.
name|contains
argument_list|(
literal|"1.98E+08"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"-1.98E08"
argument_list|)
operator|||
name|content
operator|.
name|contains
argument_list|(
literal|"-1.98E+08"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Percentage
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"2.50%"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Excel rounds up to 3%, but that requires Java 1.6 or later
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"1.5"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"2%"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"3%"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Time Format: h:mm
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"6:15"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"18:15"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Date Format: d-mmm-yy
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"17-May-07"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Currency $#,##0.00;[Red]($#,##0.00)
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"$1,599.99"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"($1,599.99)"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Below assertions represent outstanding formatting issues to be addressed
comment|// they are included to allow the issues to be progressed with the Apache POI
comment|// team - See TIKA-103.
comment|/*************************************************************************             // Date Format: m/d/yy             assertTrue(content.contains("03/10/2009"));              // Date/Time Format             assertTrue(content.contains("19/01/2008 04:35"));              // Custom Number (0 "dollars and" .00 "cents")             assertTrue(content.contains("19 dollars and .99 cents"));              // Custom Number ("At" h:mm AM/PM "on" dddd mmmm d"," yyyy)             assertTrue(content.contains("At 4:20 AM on Thursday May 17, 2007"));              // Fraction (2.5): # ?/?             assertTrue(content.contains("2 1 / 2"));             **************************************************************************/
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * We have a number of different powerpoint files,      *  such as presentation, macro-enabled etc      */
specifier|public
name|void
name|testPowerPoint
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|extensions
init|=
operator|new
name|String
index|[]
block|{
literal|"pptx"
block|,
literal|"pptm"
block|,
literal|"ppsm"
block|,
literal|"ppsx"
block|,
comment|//"thmx", // TIKA-418: Will be supported in POI 3.7 beta 2
comment|//"xps" // TIKA-418: Not yet supported by POI
block|}
decl_stmt|;
for|for
control|(
name|String
name|extension
range|:
name|extensions
control|)
block|{
name|String
name|filename
init|=
literal|"testPPT."
operator|+
name|extension
decl_stmt|;
name|InputStream
name|input
init|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filename
argument_list|)
decl_stmt|;
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
comment|// TODO: should auto-detect without the resource name
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|filename
argument_list|)
expr_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
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
name|input
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
literal|"application/vnd.openxmlformats-officedocument.presentationml.presentation"
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
literal|"Attachment Test"
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
literal|"Rajiv"
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Theme files don't have the text in them
if|if
condition|(
name|extension
operator|.
name|equals
argument_list|(
literal|"thmx"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"Text missing for "
operator|+
name|filename
operator|+
literal|"\n"
operator|+
name|content
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"Attachment Test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Text missing for "
operator|+
name|filename
operator|+
literal|"\n"
operator|+
name|content
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"This is a test file data with the same content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Text missing for "
operator|+
name|filename
operator|+
literal|"\n"
operator|+
name|content
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"content parsing"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Text missing for "
operator|+
name|filename
operator|+
literal|"\n"
operator|+
name|content
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"Different words to test against"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Text missing for "
operator|+
name|filename
operator|+
literal|"\n"
operator|+
name|content
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"Mystery"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Test the plain text output of the Word converter      * @throws Exception      */
specifier|public
name|void
name|testWord
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD.docx"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
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
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
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
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
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
literal|"Sample Word Document"
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
literal|"Keith Bennett"
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
name|assertTrue
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Sample Word Document"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Test the plain text output of the Word converter      * @throws Exception      */
specifier|public
name|void
name|testWordFootnote
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/footnotes.docx"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
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
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
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
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
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
name|assertTrue
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"snoska"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Test that the word converter is able to generate the      *  correct HTML for the document      */
specifier|public
name|void
name|testWordHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
literal|null
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
comment|// Try with a document containing various tables and formattings
name|input
operator|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD.docx"
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
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
literal|"Sample Word Document"
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
literal|"Keith Bennett"
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
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"Sample Word Document"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check that custom headings came through
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<h1 class=\"title\">"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Regular headings
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<h1>Heading Level 1</h1>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<h2>Heading Level 2</h2>"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Headings with anchor tags in them
name|assertTrue
argument_list|(
name|xml
operator|.
name|replaceAll
argument_list|(
literal|"\r?\n"
argument_list|,
literal|""
argument_list|)
operator|.
name|contains
argument_list|(
literal|"<h3><a name=\"OnLevel3\"/>Heading Level 3</h3>"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Bold and italic
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<b>BOLD</b>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<i>ITALIC</i>"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Table
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<table>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<td>"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Links
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<a href=\"http://tika.apache.org/\">Tika</a>"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Anchor links
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<a href=\"#OnMainHeading\">The Main Heading Bookmark</a>"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Paragraphs with other styles
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<p class=\"signature\">This one"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Try with a document that contains images
name|sw
operator|=
operator|new
name|StringWriter
argument_list|()
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
name|input
operator|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD_3imgs.docx"
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Images 2-4 (there is no 1!)
comment|//            assertTrue("Image not found in:\n"+xml, xml.contains("<img src=\"embedded:image2.png\"/>"));
comment|//            assertTrue("Image not found in:\n"+xml, xml.contains("<img src=\"embedded:image3.jpeg\"/>"));
comment|//            assertTrue("Image not found in:\n"+xml, xml.contains("<img src=\"embedded:image4.png\"/>"));
comment|// Text too
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<p>The end!</p>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Test that we can extract image from docx header      */
specifier|public
name|void
name|testWordPicturesInHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
literal|null
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
comment|// Try with a document containing various tables and formattings
name|input
operator|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/headerPic.docx"
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
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
comment|// Check that custom headings came through
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<img"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Documents with some sheets are protected, but not all.       * See TIKA-364.      */
specifier|public
name|void
name|testProtectedExcelSheets
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/protectedSheets.xlsx"
argument_list|)
decl_stmt|;
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
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
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
name|input
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
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
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
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaMetadataKeys
operator|.
name|PROTECTED
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * An excel document which is password protected.       * See TIKA-437.      */
specifier|public
name|void
name|testProtectedExcelFile
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|OOXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/protectedFile.xlsx"
argument_list|)
decl_stmt|;
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
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
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
name|input
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
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
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
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaMetadataKeys
operator|.
name|PROTECTED
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
literal|"Office"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

