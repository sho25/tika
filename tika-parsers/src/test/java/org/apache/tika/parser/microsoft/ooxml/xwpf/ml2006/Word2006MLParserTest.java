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
operator|.
name|xwpf
operator|.
name|ml2006
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileFilter
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
name|apache
operator|.
name|tika
operator|.
name|MultiThreadedTikaTest
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
name|parser
operator|.
name|microsoft
operator|.
name|OfficeParserConfig
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
name|utils
operator|.
name|XMLReaderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_class
specifier|public
class|class
name|Word2006MLParserTest
extends|extends
name|MultiThreadedTikaTest
block|{
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|TikaException
block|{
name|XMLReaderUtils
operator|.
name|setPoolSize
argument_list|(
name|XMLReaderUtils
operator|.
name|DEFAULT_POOL_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|basicTest
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
literal|"testWORD_2006ml.xml"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|m
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2016-11-29T17:54:00Z"
argument_list|,
name|m
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
literal|"2016-11-29T17:54:00Z"
argument_list|,
name|m
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
literal|"My Document Title"
argument_list|,
name|m
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
literal|"This is the Author"
argument_list|,
name|m
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
literal|"2"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|REVISION
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Allison, Timothy B."
argument_list|,
name|m
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
literal|"0"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|DOC_SECURITY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"260"
argument_list|,
name|m
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
literal|"3"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Office
operator|.
name|PARAGRAPH_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1742"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Office
operator|.
name|CHARACTER_COUNT_WITH_SPACES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"12"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Office
operator|.
name|LINE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"16.0000"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|APP_VERSION
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|m
operator|.
name|get
argument_list|(
name|RecursiveParserWrapper
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
name|assertContainsCount
argument_list|(
literal|"This is the Author"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertContainsCount
argument_list|(
literal|"This is an engaging title page"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"My Document Title"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"My Document Subtitle"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>\t<a href=\"#_Toc467647605\">Heading1	3</a></p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//TODO: integrate numbering
name|assertContains
argument_list|(
literal|"Really basic 2."
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContainsCount
argument_list|(
literal|"This is a text box"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//        assertContains("<p>This is a hyperlink:<a href=\"http://tika.apache.org\">tika</a></p>", content);
comment|//        assertContains("<p>This is a link to a local file:<a href=\"file:///C:\\data\\test.png\">test.png</a></p>", content);
name|assertContains
argument_list|(
literal|"<p>This is          10 spaces</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//caption
name|assertContains
argument_list|(
literal|"<p>\t<a href=\"#_Toc467647797\">Table 1: Table1 Caption\t2</a>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//embedded table
comment|//TODO: figure out how to handle embedded tables in html
name|assertContains
argument_list|(
literal|"<td>Embedded table r1c1"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//shape
name|assertContainsCount
argument_list|(
literal|"<p>This is text within a shape"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//sdt rich text
name|assertContains
argument_list|(
literal|"<p>Rich text content control"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//sdt simple text
name|assertContains
argument_list|(
literal|"<p>Simple text content control"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//sdt repeating
name|assertContains
argument_list|(
literal|"Repeating content"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//sdt dropdown
comment|//TODO: get options for dropdown
name|assertContains
argument_list|(
literal|"Drop down1"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//sdt date
name|assertContains
argument_list|(
literal|"<p>11/16/2016</p>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//test that<tab/> works
name|assertContains
argument_list|(
literal|"tab\ttab"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContainsCount
argument_list|(
literal|"serious word art"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertContainsCount
argument_list|(
literal|"Wordartr1c1"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//glossary document contents
name|assertContains
argument_list|(
literal|"Click or tap to enter a date"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//basic formatting
name|assertContains
argument_list|(
literal|"<p>The<i>quick</i> brown<b>fox</b>j<i>um</i><b><i>ped</i></b> over"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//TODO: add chart parsing
comment|//        assertContains("This is the chart", content);
name|assertContains
argument_list|(
literal|"This is a comment"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is an endnote"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"this is the footnote"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"First page header"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Even page header"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Odd page header"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"First page footer"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Even page footer"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Odd page footer"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//test default ignores deleted
name|assertNotContained
argument_list|(
literal|"frog"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Mattmann"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//test default -- do not include moveFrom
name|assertContainsCount
argument_list|(
literal|"Second paragraph"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//TODO: figure out how to get this
comment|//assertContains("This is the chart title", content);
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSkipDeletedAndMoveFrom
parameter_list|()
throws|throws
name|Exception
block|{
name|ParseContext
name|pc
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|OfficeParserConfig
name|officeParserConfig
init|=
operator|new
name|OfficeParserConfig
argument_list|()
decl_stmt|;
name|officeParserConfig
operator|.
name|setIncludeDeletedContent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|officeParserConfig
operator|.
name|setIncludeMoveFromContent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pc
operator|.
name|set
argument_list|(
name|OfficeParserConfig
operator|.
name|class
argument_list|,
name|officeParserConfig
argument_list|)
expr_stmt|;
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testWORD_2006ml.xml"
argument_list|,
name|pc
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"frog"
argument_list|,
name|r
operator|.
name|xml
argument_list|)
expr_stmt|;
name|assertContainsCount
argument_list|(
literal|"Second paragraph"
argument_list|,
name|r
operator|.
name|xml
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLReaderUtils
operator|.
name|setPoolSize
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|int
name|numThreads
init|=
name|XMLReaderUtils
operator|.
name|getPoolSize
argument_list|()
operator|*
literal|2
decl_stmt|;
name|ParseContext
index|[]
name|contexts
init|=
operator|new
name|ParseContext
index|[
name|numThreads
index|]
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
name|contexts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|contexts
index|[
name|i
index|]
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
block|}
name|testMultiThreaded
argument_list|(
operator|new
name|AutoDetectParser
argument_list|()
argument_list|,
name|contexts
argument_list|,
name|numThreads
argument_list|,
literal|2
argument_list|,
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
if|if
condition|(
name|pathname
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"testWORD_2006ml.xml"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

