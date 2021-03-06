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
name|xml
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
name|Arrays
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
name|XML2003ParserTest
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
name|testBasicWord
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testWORD2003.xml"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|m
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//container doc
name|String
name|xml
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
name|xml
operator|=
name|xml
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
comment|//make sure that metadata gets dumped to xml
name|assertContains
argument_list|(
literal|"<meta name=\"meta:character-count-with-spaces\" content=\"256\""
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|//do not allow nested<p> elements
name|assertContains
argument_list|(
literal|"<p /><img href=\"02000003.jpg\" /><p /><p><img href=\"02000004.jpg\" /></p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<table><tbody>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"</tbody></table>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td><p>R1 c1</p></td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<a href=\"https://tika.apache.org/\">tika</a>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"footnote"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Mycomment"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Figure 1: My Figure"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"myEndNote"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"We have always been at war with OceaniaEurasia"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Text box"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"Text boxText box"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"MyHeader"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"MyFooter"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<img href=\"02000003.jpg\" />"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"219"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Office
operator|.
name|CHARACTER_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"256"
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
literal|"38"
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
literal|"1"
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
literal|"Allison, Timothy B."
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
literal|"2016-04-27T17:49:00Z"
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
literal|"application/vnd.ms-wordml"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
comment|//make sure embedded docs were properly processed
name|assertContains
argument_list|(
literal|"moscow-birds"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testJPEG_EXIF.jpg"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|ORIGINAL_RESOURCE_NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|//check that text is extracted with breaks between elements
name|String
name|txt
init|=
name|getText
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD2003.xml"
argument_list|)
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|)
decl_stmt|;
name|txt
operator|=
name|txt
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"beforeR1"
argument_list|,
name|txt
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"R1 c1 R1 c2"
argument_list|,
name|txt
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"footnoteFigure"
argument_list|,
name|txt
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"footnote Figure"
argument_list|,
name|txt
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test space"
argument_list|,
name|txt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBasicExcel
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testEXCEL2003.xml"
argument_list|)
decl_stmt|;
name|Metadata
name|m
init|=
name|r
operator|.
name|metadata
decl_stmt|;
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
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"16.00"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|VERSION
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-spreadsheetml"
argument_list|,
name|m
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
name|xml
init|=
name|r
operator|.
name|xml
decl_stmt|;
name|xml
operator|=
name|xml
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
comment|//confirm metadata was dumped to xml
name|assertContains
argument_list|(
literal|"<meta name=\"cp:version\" content=\"16.00\" />"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<tr><td>Col1</td><td>Col2</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td>2016-04-27T00:00:00.000</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<a href=\"https://tika.apache.org/\">tika_hyperlink</a>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td>5.5</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|//check that text is extracted with breaks between elements
name|String
name|txt
init|=
name|getText
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testEXCEL2003.xml"
argument_list|)
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|)
decl_stmt|;
name|txt
operator|=
name|txt
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
literal|"Col1 Col2 Col3 Col4 string 1 1.10"
argument_list|,
name|txt
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
name|AUTO_DETECT_PARSER
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
literal|"testWORD2003.xml"
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

