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
name|Office
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
name|OfficeOpenXMLCore
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
name|OfficeOpenXMLExtended
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
name|microsoft
operator|.
name|ooxml
operator|.
name|OOXMLParserTest
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
name|WordParserTest
extends|extends
name|TikaTest
block|{
specifier|public
name|void
name|testWordParser
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|WordParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD.doc"
argument_list|)
decl_stmt|;
try|try
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
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
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
literal|"application/msword"
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
name|TikaCoreProperties
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
name|TikaCoreProperties
operator|.
name|CREATOR
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
specifier|public
name|void
name|testWordWithWAV
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|WordParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/Doc1_ole.doc"
argument_list|)
decl_stmt|;
try|try
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
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
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
name|assertTrue
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"MSj00974840000[1].wav"
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
specifier|private
specifier|static
class|class
name|XMLResult
block|{
specifier|public
specifier|final
name|String
name|xml
decl_stmt|;
specifier|public
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|public
name|XMLResult
parameter_list|(
name|String
name|xml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|xml
operator|=
name|xml
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
block|}
specifier|private
name|XMLResult
name|getXML
parameter_list|(
name|String
name|filePath
parameter_list|)
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
literal|"no"
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
name|filePath
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
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
return|return
operator|new
name|XMLResult
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|,
name|metadata
argument_list|)
return|;
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
comment|// Try with a document containing various tables and
comment|// formattings
name|XMLResult
name|result
init|=
name|getXML
argument_list|(
literal|"/test-documents/testWORD.doc"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|result
operator|.
name|xml
decl_stmt|;
name|Metadata
name|metadata
init|=
name|result
operator|.
name|metadata
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/msword"
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
name|TikaCoreProperties
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
name|TikaCoreProperties
operator|.
name|CREATOR
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
literal|"<h3>Heading Level 3</h3>"
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
comment|// TODO - Check for the nested table
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
comment|// Try with a document that contains images
name|xml
operator|=
name|getXML
argument_list|(
literal|"/test-documents/testWORD_3imgs.doc"
argument_list|)
operator|.
name|xml
expr_stmt|;
comment|// Images 1-3
name|assertTrue
argument_list|(
literal|"Image not found in:\n"
operator|+
name|xml
argument_list|,
name|xml
operator|.
name|contains
argument_list|(
literal|"src=\"embedded:image1.png\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Image not found in:\n"
operator|+
name|xml
argument_list|,
name|xml
operator|.
name|contains
argument_list|(
literal|"src=\"embedded:image2.jpg\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Image not found in:\n"
operator|+
name|xml
argument_list|,
name|xml
operator|.
name|contains
argument_list|(
literal|"src=\"embedded:image3.png\""
argument_list|)
argument_list|)
expr_stmt|;
comment|// Text too
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"<p>The end!"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TIKA-692: test document containing multiple
comment|// character runs within a bold tag:
name|xml
operator|=
name|getXML
argument_list|(
literal|"/test-documents/testWORD_bold_character_runs.doc"
argument_list|)
operator|.
name|xml
expr_stmt|;
comment|// Make sure bold text arrived as single
comment|// contiguous string even though Word parser
comment|// handled this as 3 character runs
name|assertTrue
argument_list|(
literal|"Bold text wasn't contiguous: "
operator|+
name|xml
argument_list|,
name|xml
operator|.
name|contains
argument_list|(
literal|"F<b>oob</b>a<b>r</b>"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TIKA-692: test document containing multiple
comment|// character runs within a bold tag:
name|xml
operator|=
name|getXML
argument_list|(
literal|"/test-documents/testWORD_bold_character_runs2.doc"
argument_list|)
operator|.
name|xml
expr_stmt|;
comment|// Make sure bold text arrived as single
comment|// contiguous string even though Word parser
comment|// handled this as 3 character runs
name|assertTrue
argument_list|(
literal|"Bold text wasn't contiguous: "
operator|+
name|xml
argument_list|,
name|xml
operator|.
name|contains
argument_list|(
literal|"F<b>oob</b>a<b>r</b>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEmbeddedNames
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|result
init|=
name|getXML
argument_list|(
literal|"/test-documents/testWORD_embedded_pdf.doc"
argument_list|)
operator|.
name|xml
decl_stmt|;
comment|// Make sure the embedded div comes out after "Here
comment|// is the pdf file" and before "Bye Bye":
name|int
name|i
init|=
name|result
operator|.
name|indexOf
argument_list|(
literal|"Here is the pdf file:"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|i
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|int
name|j
init|=
name|result
operator|.
name|indexOf
argument_list|(
literal|"<div class=\"embedded\" id=\"_1402837031\"/>"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|j
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|int
name|k
init|=
name|result
operator|.
name|indexOf
argument_list|(
literal|"Bye Bye"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|k
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|i
operator|<
name|j
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|j
operator|<
name|k
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-982
specifier|public
name|void
name|testEmbeddedRTF
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|result
init|=
name|getXML
argument_list|(
literal|"/test-documents/testWORD_embedded_rtf.doc"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"<div class=\"embedded\" id=\"_1404039792\"/>"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"_1404039792.rtf"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-1019
specifier|public
name|void
name|testDocumentLink
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|result
init|=
name|getXML
argument_list|(
literal|"/test-documents/testDocumentLink.doc"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"<div class=\"embedded\" id=\"_1327495610\"/>"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
literal|"_1327495610.unknown"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWord6Parser
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|WordParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD6.doc"
argument_list|)
decl_stmt|;
try|try
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
operator|new
name|OfficeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
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
literal|"application/msword"
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
literal|"The quick brown fox jumps over the lazy dog"
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
literal|"Gym class featuring a brown fox and lazy dog"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Gym class featuring a brown fox and lazy dog"
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
name|assertEquals
argument_list|(
literal|"Nevin Nollop"
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
literal|"Nevin Nollop"
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
literal|"The quick brown fox jumps over the lazy dog"
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
name|testVarious
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
name|InputStream
name|stream
init|=
name|WordParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD_various.doc"
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|OfficeParser
argument_list|()
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//content = content.replaceAll("\\s+"," ");
name|assertContains
argument_list|(
literal|"Footnote appears here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is a footnote."
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is the header text."
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is the footer text."
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Here is a text box"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Bold"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"italic"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"underline"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"superscript"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"subscript"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Here is a citation:"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Figure 1 This is a caption for Figure 1"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"(Kramer)"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Row 1 Col 1 Row 1 Col 2 Row 1 Col 3 Row 2 Col 1 Row 2 Col 2 Row 2 Col 3"
argument_list|,
name|content
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Row 1 column 1 Row 2 column 1 Row 1 column 2 Row 2 column 2"
argument_list|,
name|content
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is a hyperlink"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Here is a list:"
argument_list|,
name|content
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|1
init|;
name|row
operator|<=
literal|3
condition|;
name|row
operator|++
control|)
block|{
comment|//assertContains("·\tBullet " + row, content);
comment|//assertContains("\u00b7\tBullet " + row, content);
name|assertContains
argument_list|(
literal|"Bullet "
operator|+
name|row
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
name|assertContains
argument_list|(
literal|"Here is a numbered list:"
argument_list|,
name|content
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|1
init|;
name|row
operator|<=
literal|3
condition|;
name|row
operator|++
control|)
block|{
comment|//assertContains(row + ")\tNumber bullet " + row, content);
comment|//assertContains(row + ") Number bullet " + row, content);
comment|// TODO: WordExtractor fails to number the bullets:
name|assertContains
argument_list|(
literal|"Number bullet "
operator|+
name|row
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|row
init|=
literal|1
init|;
name|row
operator|<=
literal|2
condition|;
name|row
operator|++
control|)
block|{
for|for
control|(
name|int
name|col
init|=
literal|1
init|;
name|col
operator|<=
literal|3
condition|;
name|col
operator|++
control|)
block|{
name|assertContains
argument_list|(
literal|"Row "
operator|+
name|row
operator|+
literal|" Col "
operator|+
name|col
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
name|assertContains
argument_list|(
literal|"Keyword1 Keyword2"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Keyword1 Keyword2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|KEYWORDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Subject is here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// TODO: Move to OO subject in Tika 2.0
name|assertEquals
argument_list|(
literal|"Subject is here"
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
name|assertEquals
argument_list|(
literal|"Subject is here"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Suddenly some Japanese text:"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Special version of (GHQ)
name|assertContains
argument_list|(
literal|"\uff08\uff27\uff28\uff31\uff09"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// 6 other characters
name|assertContains
argument_list|(
literal|"\u30be\u30eb\u30b2\u3068\u5c3e\u5d0e\u3001\u6de1\u3005\u3068\u6700\u671f"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"And then some Gothic text:"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\uD800\uDF32\uD800\uDF3f\uD800\uDF44\uD800\uDF39\uD800\uDF43\uD800\uDF3A"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
comment|/**      * Ensures that custom OLE2 (HPSF) properties are extracted      */
specifier|public
name|void
name|testCustomProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|WordParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD_custom_props.doc"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
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
operator|new
name|OfficeParser
argument_list|()
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
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"application/msword"
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
literal|"EJ04325S"
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
literal|"Etienne Jouvin"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Etienne Jouvin"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|LAST_AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2012-01-03T22:14:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2012-01-03T22:14:00Z"
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
name|assertEquals
argument_list|(
literal|"2010-10-05T09:03:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2010-10-05T09:03:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Microsoft Office Word"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|PAGE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|WORD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"My Title"
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
literal|"My Keyword"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|KEYWORDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Normal.dotm"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|TEMPLATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"My Comments"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|COMMENTS
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: Move to OO subject in Tika 2.0
name|assertEquals
argument_list|(
literal|"My subject"
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
name|assertEquals
argument_list|(
literal|"My subject"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"EDF-DIT"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|COMPANY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"MyStringValue"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:MyCustomString"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2010-12-30T23:00:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:MyCustomDate"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

