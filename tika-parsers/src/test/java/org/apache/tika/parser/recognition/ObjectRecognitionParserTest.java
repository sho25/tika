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
name|lang
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
name|Arrays
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

begin_comment
comment|/**  * Testcases for Object Recognition Parser  */
end_comment

begin_class
specifier|public
class|class
name|ObjectRecognitionParserTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|CONFIG_FILE
init|=
literal|"org/apache/tika/parser/recognition/tika-config-tflow.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CAT_IMAGE
init|=
literal|"test-documents/testJPEG.jpg"
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
annotation|@
name|Ignore
argument_list|(
literal|"If tensorflow not available Ignore"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|jpegTesorflowTest
parameter_list|()
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
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
name|CONFIG_FILE
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
name|CAT_IMAGE
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
literal|"Border collie"
block|}
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|objects
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|objects
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|ObjectRecognitionParser
operator|.
name|MD_KEY
argument_list|)
argument_list|)
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
name|objects
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

