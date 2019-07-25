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
name|pkg
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|compressors
operator|.
name|CompressorStreamFactory
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
name|mime
operator|.
name|MediaType
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
name|AbstractRecursiveParserWrapperHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|CompressorParserTest
extends|extends
name|TikaTest
block|{
comment|//These compressed stream types can't currently
comment|//be detected.
specifier|private
specifier|static
name|Set
argument_list|<
name|MediaType
argument_list|>
name|NOT_COVERED
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
block|{
name|NOT_COVERED
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-lz4-block"
argument_list|)
argument_list|)
expr_stmt|;
name|NOT_COVERED
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-snappy-raw"
argument_list|)
argument_list|)
expr_stmt|;
name|NOT_COVERED
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"deflate64"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSnappyFramed
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testSnappy-framed.sz"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/x-snappy"
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
name|assertContains
argument_list|(
literal|"Lorem ipsum dolor sit amet"
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
name|testLZ4Framed
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testLZ4-framed.lz4"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"application/x-lz4"
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
comment|//xml parser throws an exception for test1.xml
comment|//for now, be content that the container file is correctly identified
name|assertContains
argument_list|(
literal|"test1.xml"
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
name|testZstd
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testZSTD.zstd"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"0123456789"
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
name|testBrotli
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
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"testBROTLI_compressed.br"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testBROTLI_compressed.br"
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"XXXXXXXXXXYYYYYYYYYY"
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
name|AbstractRecursiveParserWrapperHandler
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testBROTLI_compressed"
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
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCoverage
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test that the package parser covers all inputstreams handled
comment|//by CompressorStreamFactory.  When we update commons-compress, and they add
comment|//a new stream type, we want to make sure that we're handling it.
name|CompressorStreamFactory
name|archiveStreamFactory
init|=
operator|new
name|CompressorStreamFactory
argument_list|(
literal|true
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|CompressorParser
name|compressorParser
init|=
operator|new
name|CompressorParser
argument_list|()
decl_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|archiveStreamFactory
operator|.
name|getInputStreamCompressorNames
argument_list|()
control|)
block|{
name|MediaType
name|mt
init|=
name|CompressorParser
operator|.
name|getMediaType
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|NOT_COVERED
operator|.
name|contains
argument_list|(
name|mt
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|//use this instead of assertNotEquals so that we report the
comment|//name of the missing stream
if|if
condition|(
name|mt
operator|.
name|equals
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"getting octet-stream for: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|compressorParser
operator|.
name|getSupportedTypes
argument_list|(
name|parseContext
argument_list|)
operator|.
name|contains
argument_list|(
name|mt
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"CompressorParser should support: "
operator|+
name|mt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|TikaException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testQuineXHTML
parameter_list|()
throws|throws
name|Exception
block|{
comment|//https://blog.matthewbarber.io/2019/07/22/how-to-make-compressed-file-quines
name|getXML
argument_list|(
literal|"quine.gz"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuineRecursive
parameter_list|()
throws|throws
name|Exception
block|{
comment|//https://blog.matthewbarber.io/2019/07/22/how-to-make-compressed-file-quines
name|getRecursiveMetadata
argument_list|(
literal|"quine.gz"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

