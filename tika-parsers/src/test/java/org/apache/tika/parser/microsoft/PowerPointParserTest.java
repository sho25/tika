begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|List
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
name|RecursiveParserWrapper
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
name|junit
operator|.
name|Ignore
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

begin_class
specifier|public
class|class
name|PowerPointParserTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testPowerPointParser
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|PowerPointParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPPT.ppt"
argument_list|)
init|)
block|{
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
literal|"application/vnd.ms-powerpoint"
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
literal|"Sample Powerpoint Slide"
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Sample Powerpoint Slide"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Powerpoint X for Mac"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVarious
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
name|String
name|xml
init|=
name|getXML
argument_list|(
literal|"testPPT_various.ppt"
argument_list|,
name|metadata
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"<p>Footnote appears here"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>[1] This is a footnote."
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>This is the header text.</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>This is the footer text.</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>Here is a text box</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>Bold "
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"italic underline superscript subscript"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"underline"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"superscript"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"subscript"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>Here is a citation:"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Figure 1 This is a caption for Figure 1"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"(Kramer)"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<table><tr>\t<td>Row 1 Col 1</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td>Row 2 Col 2</td>\t<td>Row 2 Col 3</td></tr>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>Row 1 column 1</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>Row 2 column 2</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p><a href=\"http://tika.apache.org/\">This is a hyperlink</a>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>Here is a list:"
argument_list|,
name|xml
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
literal|"<li>Bullet "
operator|+
name|row
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
name|assertContains
argument_list|(
literal|"Here is a numbered list:"
argument_list|,
name|xml
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
comment|// TODO: OOXMLExtractor fails to number the bullets:
name|assertContains
argument_list|(
literal|"<li>Number bullet "
operator|+
name|row
argument_list|,
name|xml
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
name|xml
argument_list|)
expr_stmt|;
block|}
block|}
name|assertContains
argument_list|(
literal|"Keyword1 Keyword2"
argument_list|,
name|xml
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
name|xml
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
comment|// TODO: Remove subject in Tika 2.0
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
name|assertContains
argument_list|(
literal|"Suddenly some Japanese text:"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// Special version of (GHQ)
name|assertContains
argument_list|(
literal|"\uff08\uff27\uff28\uff31\uff09"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// 6 other characters
name|assertContains
argument_list|(
literal|"\u30be\u30eb\u30b2\u3068\u5c3e\u5d0e\u3001\u6de1\u3005\u3068\u6700\u671f"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"And then some Gothic text:"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\uD800\uDF32\uD800\uDF3f\uD800\uDF44\uD800\uDF39\uD800\uDF43\uD800\uDF3A"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMasterFooter
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
try|try
init|(
name|InputStream
name|stream
init|=
name|PowerPointParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPPT_masterFooter.ppt"
argument_list|)
init|)
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Master footer is here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Make sure boilerplate text didn't come through:
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|content
operator|.
name|indexOf
argument_list|(
literal|"Click to edit Master"
argument_list|)
argument_list|)
expr_stmt|;
comment|//TIKA-1171
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|content
operator|.
name|indexOf
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * TIKA-712 Master Slide Text from PPT and PPTX files      *  should be extracted too      */
annotation|@
name|Test
specifier|public
name|void
name|testMasterText
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
try|try
init|(
name|InputStream
name|stream
init|=
name|PowerPointParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPPT_masterText.ppt"
argument_list|)
init|)
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Text that I added to the master slide"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Make sure boilerplate text didn't come through:
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|content
operator|.
name|indexOf
argument_list|(
literal|"Click to edit Master"
argument_list|)
argument_list|)
expr_stmt|;
comment|//TIKA-1171
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|content
operator|.
name|indexOf
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMasterText2
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
try|try
init|(
name|InputStream
name|stream
init|=
name|PowerPointParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPPT_masterText2.ppt"
argument_list|)
init|)
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
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Text that I added to the master slide"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|// Make sure boilerplate text didn't come through:
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|content
operator|.
name|indexOf
argument_list|(
literal|"Click to edit Master"
argument_list|)
argument_list|)
expr_stmt|;
comment|//TIKA-1171
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|content
operator|.
name|indexOf
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Ensures that custom OLE2 (HPSF) properties are extracted      */
annotation|@
name|Test
specifier|public
name|void
name|testCustomProperties
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
try|try
init|(
name|InputStream
name|input
init|=
name|PowerPointParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPPT_custom_props.ppt"
argument_list|)
init|)
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
name|assertEquals
argument_list|(
literal|"application/vnd.ms-powerpoint"
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
literal|"JOUVIN ETIENNE"
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
literal|"EJ04325S"
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
literal|"EJ04325S"
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
literal|"2011-08-22T13:32:58Z"
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
literal|"2011-08-22T13:32:58Z"
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
literal|"2011-08-22T13:30:53Z"
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
literal|"2011-08-22T13:30:53Z"
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
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|SLIDE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
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
literal|"Test extraction properties pptx"
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
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:myCustomBoolean"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:myCustomNumber"
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
literal|"2010-12-30T22:00:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:MyCustomDate"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2010-12-29T22:00:00Z"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:myCustomSecondDate"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-1025
annotation|@
name|Test
specifier|public
name|void
name|testEmbeddedPlacedholder
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|result
init|=
name|getXML
argument_list|(
literal|"testPPT_embedded2.ppt"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"<div class=\"embedded\" id=\"1\" />"
argument_list|,
name|result
operator|.
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<div class=\"embedded\" id=\"14\" />"
argument_list|,
name|result
operator|.
name|xml
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-817
annotation|@
name|Test
specifier|public
name|void
name|testAutoDatePPT
parameter_list|()
throws|throws
name|Exception
block|{
comment|//decision was made in POI-52367 not to generate
comment|//autodate automatically.  For pptx, where value is stored,
comment|//value is extracted.  For ppt, however, no date is extracted.
name|XMLResult
name|result
init|=
name|getXML
argument_list|(
literal|"testPPT_autodate.ppt"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"<div class=\"slide-content\"><p>Now</p>"
argument_list|,
name|result
operator|.
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCommentAuthorship
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testPPT_comment.ppt"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"<p class=\"slide-comment\"><b>Allison, Timothy B. (ATB)"
argument_list|,
name|r
operator|.
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmbeddedPDF
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testPPT_EmbeddedPDF.ppt"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"Apache Tika project"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3.pdf"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Hello World"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"4.pdf"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"POI 3.15-final not finding any macros in this ppt"
argument_list|)
specifier|public
name|void
name|testMacros
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testPPT_macros.ppt"
argument_list|)
decl_stmt|;
name|Metadata
name|macroMetadata
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"Sub Embolden()"
argument_list|,
name|macroMetadata
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Sub Italicize()"
argument_list|,
name|macroMetadata
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"text/x-vbasic"
argument_list|,
name|macroMetadata
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
name|TikaCoreProperties
operator|.
name|EmbeddedResourceType
operator|.
name|MACRO
operator|.
name|toString
argument_list|()
argument_list|,
name|macroMetadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|EMBEDDED_RESOURCE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

