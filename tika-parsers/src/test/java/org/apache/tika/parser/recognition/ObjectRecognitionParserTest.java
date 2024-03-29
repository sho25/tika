begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|recognition
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|client
operator|.
name|WebClient
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
name|io
operator|.
name|IOUtils
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
name|parser
operator|.
name|recognition
operator|.
name|tf
operator|.
name|TensorflowImageRecParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|SAXException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Reader
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

begin_comment
comment|/**  * Testcases for Object Recognition Parser  */
end_comment

begin_class
specifier|public
class|class
name|ObjectRecognitionParserTest
block|{
comment|// Config files
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_FILE_OBJ_REC
init|=
literal|"org/apache/tika/parser/recognition/tika-config-tflow.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_REST_FILE_OBJ_REC
init|=
literal|"org/apache/tika/parser/recognition/tika-config-tflow-rest.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_REST_FILE_IM2TXT
init|=
literal|"org/apache/tika/parser/recognition/tika-config-tflow-im2txt-rest.xml"
decl_stmt|;
comment|// Test images
specifier|private
specifier|static
specifier|final
name|String
name|CAT_IMAGE_JPEG
init|=
literal|"test-documents/testJPEG.jpg"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CAT_IMAGE_PNG
init|=
literal|"test-documents/testPNG.png"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CAT_IMAGE_GIF
init|=
literal|"test-documents/testGIF.gif"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BASEBALL_IMAGE_JPEG
init|=
literal|"test-documents/baseball.jpg"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BASEBALL_IMAGE_PNG
init|=
literal|"test-documents/baseball.png"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BASEBALL_IMAGE_GIF
init|=
literal|"test-documents/baseball.gif"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ClassLoader
name|loader
init|=
name|ObjectRecognitionParserTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ObjectRecognitionParserTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|jpegTFObjRecTest
parameter_list|()
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
name|TensorflowImageRecParser
name|p
init|=
operator|new
name|TensorflowImageRecParser
argument_list|()
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|p
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|CONFIG_FILE_OBJ_REC
argument_list|)
init|)
block|{
assert|assert
name|stream
operator|!=
literal|null
assert|;
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|(
operator|new
name|TikaConfig
argument_list|(
name|stream
argument_list|)
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
init|(
name|InputStream
name|imageStream
init|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|CAT_IMAGE_JPEG
argument_list|)
init|)
block|{
name|Reader
name|reader
init|=
name|tika
operator|.
name|parse
argument_list|(
name|imageStream
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|IOUtils
operator|.
name|readLines
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|StringUtils
operator|.
name|join
argument_list|(
name|lines
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
name|String
index|[]
name|expectedObjects
init|=
block|{
literal|"Egyptian cat"
block|,
literal|"tabby, tabby cat"
block|}
decl_stmt|;
name|String
name|metaValues
init|=
name|StringUtils
operator|.
name|join
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|ObjectRecognitionParser
operator|.
name|MD_KEY_OBJ_REC
argument_list|)
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|expectedObject
range|:
name|expectedObjects
control|)
block|{
name|String
name|message
init|=
literal|"'"
operator|+
name|expectedObject
operator|+
literal|"' must have been detected"
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|message
argument_list|,
name|text
operator|.
name|contains
argument_list|(
name|expectedObject
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|message
argument_list|,
name|metaValues
operator|.
name|contains
argument_list|(
name|expectedObject
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|jpegRESTObjRecTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|apiUrl
init|=
literal|"http://localhost:8764/inception/v4/ping"
decl_stmt|;
name|boolean
name|available
init|=
literal|false
decl_stmt|;
name|int
name|status
init|=
literal|500
decl_stmt|;
try|try
block|{
name|status
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|apiUrl
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|available
operator|=
name|status
operator|==
literal|200
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|available
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedObjects
init|=
block|{
literal|"Egyptian cat"
block|,
literal|"tabby, tabby cat"
block|}
decl_stmt|;
name|doRecognize
argument_list|(
name|CONFIG_REST_FILE_OBJ_REC
argument_list|,
name|CAT_IMAGE_JPEG
argument_list|,
name|ObjectRecognitionParser
operator|.
name|MD_KEY_OBJ_REC
argument_list|,
name|expectedObjects
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pngRESTObjRecTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|apiUrl
init|=
literal|"http://localhost:8764/inception/v4/ping"
decl_stmt|;
name|boolean
name|available
init|=
literal|false
decl_stmt|;
name|int
name|status
init|=
literal|500
decl_stmt|;
try|try
block|{
name|status
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|apiUrl
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|available
operator|=
name|status
operator|==
literal|200
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|available
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedObjects
init|=
block|{
literal|"Egyptian cat"
block|,
literal|"tabby, tabby cat"
block|}
decl_stmt|;
name|doRecognize
argument_list|(
name|CONFIG_REST_FILE_OBJ_REC
argument_list|,
name|CAT_IMAGE_PNG
argument_list|,
name|ObjectRecognitionParser
operator|.
name|MD_KEY_OBJ_REC
argument_list|,
name|expectedObjects
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gifRESTObjRecTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|apiUrl
init|=
literal|"http://localhost:8764/inception/v4/ping"
decl_stmt|;
name|boolean
name|available
init|=
literal|false
decl_stmt|;
name|int
name|status
init|=
literal|500
decl_stmt|;
try|try
block|{
name|status
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|apiUrl
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|available
operator|=
name|status
operator|==
literal|200
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|available
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedObjects
init|=
block|{
literal|"Egyptian cat"
block|}
decl_stmt|;
name|doRecognize
argument_list|(
name|CONFIG_REST_FILE_OBJ_REC
argument_list|,
name|CAT_IMAGE_GIF
argument_list|,
name|ObjectRecognitionParser
operator|.
name|MD_KEY_OBJ_REC
argument_list|,
name|expectedObjects
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|jpegRESTim2txtTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|apiUrl
init|=
literal|"http://localhost:8764/inception/v3/ping"
decl_stmt|;
name|boolean
name|available
init|=
literal|false
decl_stmt|;
name|int
name|status
init|=
literal|500
decl_stmt|;
try|try
block|{
name|status
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|apiUrl
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|available
operator|=
name|status
operator|==
literal|200
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|available
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedCaption
init|=
block|{
literal|"a baseball player holding a bat on a field"
block|}
decl_stmt|;
name|doRecognize
argument_list|(
name|CONFIG_REST_FILE_IM2TXT
argument_list|,
name|BASEBALL_IMAGE_JPEG
argument_list|,
name|ObjectRecognitionParser
operator|.
name|MD_KEY_IMG_CAP
argument_list|,
name|expectedCaption
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pngRESTim2txtTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|apiUrl
init|=
literal|"http://localhost:8764/inception/v3/ping"
decl_stmt|;
name|boolean
name|available
init|=
literal|false
decl_stmt|;
name|int
name|status
init|=
literal|500
decl_stmt|;
try|try
block|{
name|status
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|apiUrl
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|available
operator|=
name|status
operator|==
literal|200
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|available
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedCaption
init|=
block|{
literal|"a baseball player holding a bat on a field"
block|}
decl_stmt|;
name|doRecognize
argument_list|(
name|CONFIG_REST_FILE_IM2TXT
argument_list|,
name|BASEBALL_IMAGE_PNG
argument_list|,
name|ObjectRecognitionParser
operator|.
name|MD_KEY_IMG_CAP
argument_list|,
name|expectedCaption
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gifRESTim2txtTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|apiUrl
init|=
literal|"http://localhost:8764/inception/v3/ping"
decl_stmt|;
name|boolean
name|available
init|=
literal|false
decl_stmt|;
name|int
name|status
init|=
literal|500
decl_stmt|;
try|try
block|{
name|status
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|apiUrl
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|available
operator|=
name|status
operator|==
literal|200
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|available
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedCaption
init|=
block|{
literal|"a baseball player pitching a ball on top of a field"
block|}
decl_stmt|;
name|doRecognize
argument_list|(
name|CONFIG_REST_FILE_IM2TXT
argument_list|,
name|BASEBALL_IMAGE_GIF
argument_list|,
name|ObjectRecognitionParser
operator|.
name|MD_KEY_IMG_CAP
argument_list|,
name|expectedCaption
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doRecognize
parameter_list|(
name|String
name|configFile
parameter_list|,
name|String
name|testImg
parameter_list|,
name|String
name|mdKey
parameter_list|,
name|String
index|[]
name|expectedObjects
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|stream
init|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|configFile
argument_list|)
init|)
block|{
assert|assert
name|stream
operator|!=
literal|null
assert|;
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|(
operator|new
name|TikaConfig
argument_list|(
name|stream
argument_list|)
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
init|(
name|InputStream
name|imageStream
init|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|testImg
argument_list|)
init|)
block|{
name|Reader
name|reader
init|=
name|tika
operator|.
name|parse
argument_list|(
name|imageStream
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|String
name|metaValues
init|=
name|StringUtils
operator|.
name|join
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|mdKey
argument_list|)
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MetaValues = {}"
argument_list|,
name|metaValues
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|expectedObject
range|:
name|expectedObjects
control|)
block|{
name|String
name|message
init|=
literal|"'"
operator|+
name|expectedObject
operator|+
literal|"' must have been detected"
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|message
argument_list|,
name|text
operator|.
name|contains
argument_list|(
name|expectedObject
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|message
argument_list|,
name|metaValues
operator|.
name|contains
argument_list|(
name|expectedObject
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

