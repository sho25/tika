begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|epub
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
name|assertNotNull
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
name|RecursiveParserWrapperHandler
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
name|EpubParserTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testXMLParser
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|xmlResult
init|=
name|getXML
argument_list|(
literal|"testEPUB.epub"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/epub+zip"
argument_list|,
name|xmlResult
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
name|assertEquals
argument_list|(
literal|"en"
argument_list|,
name|xmlResult
operator|.
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
name|assertEquals
argument_list|(
literal|"This is an ePub test publication for Tika."
argument_list|,
name|xmlResult
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Apache"
argument_list|,
name|xmlResult
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|PUBLISHER
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|xmlResult
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"Plus a simple div"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"First item"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"The previous headings were<strong>subchapters</strong>"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Table data"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is the text for chapter Two"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//make sure style/script elements aren't extracted
name|assertNotContained
argument_list|(
literal|"nothing to see here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"nor here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"font-style"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//make sure that there is only one of each
name|assertContainsCount
argument_list|(
literal|"<html"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertContainsCount
argument_list|(
literal|"<head"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertContainsCount
argument_list|(
literal|"<body"
argument_list|,
name|content
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEpubOrder
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
literal|"testEPUB.epub"
argument_list|)
decl_stmt|;
comment|//test attachments
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/jpeg"
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
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapperHandler
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
name|int
name|tocIndex
init|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"h3 class=\"toc_heading\">Table of Contents<"
argument_list|)
decl_stmt|;
name|int
name|ch1
init|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"<h1>Chapter 1"
argument_list|)
decl_stmt|;
name|int
name|ch2
init|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"<h1>Chapter 2"
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|tocIndex
operator|>
operator|-
literal|1
operator|&&
name|ch1
operator|>
operator|-
literal|1
operator|&&
name|ch2
operator|>
operator|-
literal|1
operator|)
assert|;
assert|assert
operator|(
name|tocIndex
operator|<
name|ch1
operator|)
assert|;
assert|assert
operator|(
name|tocIndex
operator|<
name|ch2
operator|)
assert|;
assert|assert
operator|(
name|ch1
operator|<
name|ch2
operator|)
assert|;
name|InputStream
name|is
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/parser/epub/tika-config.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|Parser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|(
operator|new
name|TikaConfig
argument_list|(
name|is
argument_list|)
argument_list|)
decl_stmt|;
name|xml
operator|=
name|getXML
argument_list|(
literal|"testEPUB.epub"
argument_list|,
name|p
argument_list|)
operator|.
name|xml
expr_stmt|;
name|tocIndex
operator|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"h3 class=\"toc_heading\">Table of Contents<"
argument_list|)
expr_stmt|;
name|ch1
operator|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"<h1>Chapter 1"
argument_list|)
expr_stmt|;
name|ch2
operator|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"<h1>Chapter 2"
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|tocIndex
operator|>
operator|-
literal|1
operator|&&
name|ch1
operator|>
operator|-
literal|1
operator|&&
name|ch2
operator|>
operator|-
literal|1
operator|)
assert|;
assert|assert
operator|(
name|tocIndex
operator|>
name|ch1
operator|)
assert|;
assert|assert
operator|(
name|tocIndex
operator|>
name|ch2
operator|)
assert|;
assert|assert
operator|(
name|ch1
operator|<
name|ch2
operator|)
assert|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTruncated
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|EpubParser
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|truncate
argument_list|(
literal|"testEPUB.epub"
argument_list|,
literal|10000
argument_list|)
init|)
block|{
name|metadataList
operator|=
name|getRecursiveMetadata
argument_list|(
name|is
argument_list|,
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|String
name|xml
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapperHandler
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
name|int
name|ch1
init|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"<h1>Chapter 1"
argument_list|)
decl_stmt|;
name|int
name|ch2
init|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"<h1>Chapter 2"
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|ch1
operator|<
name|ch2
operator|)
assert|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContentsWXMLExtensions
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TIKA-2310
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testEPUB_xml_ext.epub"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"It was a bright cold day in April"
argument_list|,
name|metadataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapperHandler
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

