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
name|rtf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|sax
operator|.
name|WriteOutContentHandler
import|;
end_import

begin_comment
comment|/**  * Junit test class for the Tika {@link RTFParser}  */
end_comment

begin_class
specifier|public
class|class
name|RTFParserTest
extends|extends
name|TikaTest
block|{
specifier|private
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
specifier|private
specifier|static
class|class
name|Result
block|{
specifier|public
specifier|final
name|String
name|text
decl_stmt|;
specifier|public
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|public
name|Result
parameter_list|(
name|String
name|text
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testBasicExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testRTF.rtf"
argument_list|)
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
name|tika
operator|.
name|getParser
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
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
literal|"application/rtf"
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
name|assertContains
argument_list|(
literal|"Test"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"indexation Word"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUmlautSpacesExtraction2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getText
argument_list|(
literal|"testRTFUmlautSpaces2.rtf"
argument_list|)
decl_stmt|;
name|content
operator|=
name|content
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\u00DCbersicht"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUnicodeUCNControlWordCharacterDoublingExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getText
argument_list|(
literal|"testRTFUnicodeUCNControlWordCharacterDoubling.rtf"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"\u5E74"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\u5ff5"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"0 "
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"abc"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Doubled character \u5E74"
argument_list|,
name|content
operator|.
name|contains
argument_list|(
literal|"\u5E74\u5E74"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHexEscapeInsideWord
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getText
argument_list|(
literal|"testRTFHexEscapeInsideWord.rtf"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"ESP\u00cdRITO"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWindowsCodepage1250
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getText
argument_list|(
literal|"testRTFWindowsCodepage1250.rtf"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"za\u017c\u00f3\u0142\u0107 g\u0119\u015bl\u0105 ja\u017a\u0144"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"ZA\u017b\u00d3\u0141\u0106 G\u0118\u015aL\u0104 JA\u0179\u0143"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTableCellSeparation
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testRTFTableCellSeparation.rtf"
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|tika
operator|.
name|parseToString
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|content
operator|=
name|content
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"a b c d \u00E4 \u00EB \u00F6 \u00FC"
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"a b c d \u00E4 \u00EB \u00F6 \u00FC"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTableCellSeparation2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getText
argument_list|(
literal|"testRTFTableCellSeparation2.rtf"
argument_list|)
decl_stmt|;
comment|// TODO: why do we insert extra whitespace...?
name|content
operator|=
name|content
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Station Fax"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWordPadCzechCharactersExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testRTFWordPadCzechCharacters.rtf"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|tika
operator|.
name|parseToString
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|s1
operator|.
name|contains
argument_list|(
literal|"\u010Cl\u00E1nek t\u00FDdne"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s1
operator|.
name|contains
argument_list|(
literal|"starov\u011Bk\u00E9 \u017Eidovsk\u00E9 n\u00E1bo\u017Eensk\u00E9 texty"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWord2010CzechCharactersExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testRTFWord2010CzechCharacters.rtf"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|tika
operator|.
name|parseToString
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|s1
operator|.
name|contains
argument_list|(
literal|"\u010Cl\u00E1nek t\u00FDdne"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s1
operator|.
name|contains
argument_list|(
literal|"starov\u011Bk\u00E9 \u017Eidovsk\u00E9 n\u00E1bo\u017Eensk\u00E9 texty"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMS932Extraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testRTF-ms932.rtf"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|tika
operator|.
name|parseToString
argument_list|(
name|file
argument_list|)
decl_stmt|;
comment|// Hello in Japanese
name|assertTrue
argument_list|(
name|s1
operator|.
name|contains
argument_list|(
literal|"\u3053\u3093\u306b\u3061\u306f"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify title, since it was also encoded with MS932:
name|Result
name|r
init|=
name|getResult
argument_list|(
literal|"testRTF-ms932.rtf"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"\u30bf\u30a4\u30c8\u30eb"
argument_list|,
name|r
operator|.
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
block|}
specifier|public
name|void
name|testUmlautSpacesExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testRTFUmlautSpaces.rtf"
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|tika
operator|.
name|parseToString
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|s1
operator|.
name|contains
argument_list|(
literal|"\u00DCbersicht"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGothic
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getText
argument_list|(
literal|"testRTFUnicodeGothic.rtf"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"\uD800\uDF32\uD800\uDF3f\uD800\uDF44\uD800\uDF39\uD800\uDF43\uD800\uDF3A"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJapaneseText
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|r
init|=
name|getResult
argument_list|(
literal|"testRTFJapanese.rtf"
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|r
operator|.
name|text
decl_stmt|;
comment|// Verify title -- this title uses upr escape inside
comment|// title info field:
name|assertEquals
argument_list|(
literal|"\u30be\u30eb\u30b2\u3068\u5c3e\u5d0e\u3001\u6de1\u3005\u3068\u6700\u671f\u3000"
argument_list|,
name|r
operator|.
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
literal|"VMazel"
argument_list|,
name|r
operator|.
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
literal|"VMazel"
argument_list|,
name|r
operator|.
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
name|assertEquals
argument_list|(
literal|"StarWriter"
argument_list|,
name|r
operator|.
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
name|assertContains
argument_list|(
literal|"1."
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"4."
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
literal|"\u6771\u4eac\u90fd\u4e09\u9df9\u5e02"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMaxLength
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testRTFJapanese.rtf"
argument_list|)
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
name|TikaInputStream
operator|.
name|get
argument_list|(
name|file
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
comment|// Test w/ default limit:
name|Tika
name|localTika
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|localTika
operator|.
name|parseToString
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
comment|// parseToString closes for convenience:
comment|//stream.close();
name|assertTrue
argument_list|(
name|content
operator|.
name|length
argument_list|()
operator|>
literal|500
argument_list|)
expr_stmt|;
comment|// Test setting max length on the instance:
name|localTika
operator|.
name|setMaxStringLength
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|stream
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|file
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|content
operator|=
name|localTika
operator|.
name|parseToString
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
comment|// parseToString closes for convenience:
comment|//stream.close();
name|assertTrue
argument_list|(
name|content
operator|.
name|length
argument_list|()
operator|<=
literal|200
argument_list|)
expr_stmt|;
comment|// Test setting max length per-call:
name|stream
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|file
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|content
operator|=
name|localTika
operator|.
name|parseToString
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// parseToString closes for convenience:
comment|//stream.close();
name|assertTrue
argument_list|(
name|content
operator|.
name|length
argument_list|()
operator|<=
literal|100
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTextWithCurlyBraces
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getText
argument_list|(
literal|"testRTFWithCurlyBraces.rtf"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"{ some text inside curly brackets }"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testControls
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|r
init|=
name|getResult
argument_list|(
literal|"testRTFControls.rtf"
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|r
operator|.
name|text
decl_stmt|;
name|assertContains
argument_list|(
literal|"Thiswordhasanem\u2014dash"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Thiswordhasanen\u2013dash"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Thiswordhasanon\u2011breakinghyphen"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Thiswordhasanonbreaking\u00a0space"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Thiswordhasanoptional\u00adhyphen"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\u2018Single quoted text\u2019"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\u201cDouble quoted text\u201d"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\u201cDouble quoted text again\u201d"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidUnicode
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|r
init|=
name|getResult
argument_list|(
literal|"testRTFInvalidUnicode.rtf"
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|r
operator|.
name|text
decl_stmt|;
name|assertContains
argument_list|(
literal|"Unpaired hi \ufffd here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Unpaired lo \ufffd here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Mismatched pair \ufffd\ufffd here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVarious
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|r
init|=
name|getResult
argument_list|(
literal|"testRTFVarious.rtf"
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|r
operator|.
name|text
decl_stmt|;
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
comment|// Table
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
comment|// 2-columns
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
name|assertContains
argument_list|(
literal|"\u00b7\tBullet "
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
name|assertContains
argument_list|(
name|row
operator|+
literal|")\tNumber bullet "
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
name|r
operator|.
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
name|assertEquals
argument_list|(
literal|"Subject is here"
argument_list|,
name|r
operator|.
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
literal|"Subject is here"
argument_list|,
name|r
operator|.
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
specifier|public
name|void
name|testVariousStyle
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getXML
argument_list|(
literal|"testRTFVarious.rtf"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"<b>Bold</b>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<i>italic</i>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBoldItalic
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getXML
argument_list|(
literal|"testRTFBoldItalic.rtf"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"<b>bold</b>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<b>bold</b><b><i>italic</i></b>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<b><i>italic</i></b><b>bold</b>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<i>italic</i>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<b>bold then</b><b><i>italic then</i></b><i> not bold</i>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<i>italic then</i><b><i>bold then</i></b><b> not italic</b>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHyperlink
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|content
init|=
name|getXML
argument_list|(
literal|"testRTFHyperlink.rtf"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"our most<a href=\"http://r.office.microsoft.com/r/rlidwelcomeFAQ?clid=1033\">frequently asked questions</a>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|content
operator|.
name|indexOf
argument_list|(
literal|"<p>\t\t</p>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIgnoredControlWord
parameter_list|()
throws|throws
name|Exception
block|{
name|assertContains
argument_list|(
literal|"<p>The quick brown fox jumps over the lazy dog</p>"
argument_list|,
name|getXML
argument_list|(
literal|"testRTFIgnoredControlWord.rtf"
argument_list|)
operator|.
name|xml
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFontAfterBufferedText
parameter_list|()
throws|throws
name|Exception
block|{
name|assertContains
argument_list|(
literal|"\u0423\u0432\u0430\u0436\u0430\u0435\u043c\u044b\u0439 \u043a\u043b\u0438\u0435\u043d\u0442!"
argument_list|,
name|getXML
argument_list|(
literal|"testFontAfterBufferedText.rtf"
argument_list|)
operator|.
name|xml
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-782
specifier|public
name|void
name|testBinControlWord
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|getXML
argument_list|(
literal|"testBinControlWord.rtf"
argument_list|)
operator|.
name|xml
operator|.
name|indexOf
argument_list|(
literal|"\u00ff\u00ff\u00ff\u00ff"
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// TIKA-999
specifier|public
name|void
name|testMetaDataCounts
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|xml
init|=
name|getXML
argument_list|(
literal|"test_embedded_package.rtf"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|xml
operator|.
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
literal|"7"
argument_list|,
name|xml
operator|.
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
literal|"36"
argument_list|,
name|xml
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|CHARACTER_COUNT
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Result
name|getResult
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|file
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/"
operator|+
name|filename
argument_list|)
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
name|tika
operator|.
name|getParser
argument_list|()
operator|.
name|parse
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
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
return|return
operator|new
name|Result
argument_list|(
name|content
argument_list|,
name|metadata
argument_list|)
return|;
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
name|filename
parameter_list|)
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
name|InputStream
name|input
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|tika
operator|.
name|getParser
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
specifier|private
name|String
name|getText
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getResult
argument_list|(
name|filename
argument_list|)
operator|.
name|text
return|;
block|}
block|}
end_class

end_unit

