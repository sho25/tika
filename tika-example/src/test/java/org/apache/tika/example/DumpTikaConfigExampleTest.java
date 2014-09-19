begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|example
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|CompositeDetector
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
name|CompositeParser
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|FileOutputStream
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|DumpTikaConfigExampleTest
block|{
specifier|private
name|File
name|configFile
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|configFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"tmp"
argument_list|,
literal|".xml"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create tmp file"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|configFile
operator|!=
literal|null
operator|&&
name|configFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|configFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|configFile
operator|!=
literal|null
operator|&&
name|configFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to clean up: "
operator|+
name|configFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDump
parameter_list|()
throws|throws
name|Exception
block|{
name|DumpTikaConfigExample
name|ex
init|=
operator|new
name|DumpTikaConfigExample
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|encoding
range|:
operator|new
name|String
index|[]
block|{
literal|"UTF-8"
block|,
literal|"UTF-16LE"
block|}
control|)
block|{
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|configFile
argument_list|)
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|ex
operator|.
name|dump
argument_list|(
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|,
name|writer
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|TikaConfig
name|c
init|=
operator|new
name|TikaConfig
argument_list|(
name|configFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CompositeParser
operator|.
name|class
argument_list|,
name|c
operator|.
name|getParser
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CompositeDetector
operator|.
name|class
argument_list|,
name|c
operator|.
name|getDetector
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|CompositeParser
name|p
init|=
operator|(
name|CompositeParser
operator|)
name|c
operator|.
name|getParser
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"enough parsers?"
argument_list|,
name|p
operator|.
name|getParsers
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|130
argument_list|)
expr_stmt|;
name|CompositeDetector
name|d
init|=
operator|(
name|CompositeDetector
operator|)
name|c
operator|.
name|getDetector
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"enough detectors?"
argument_list|,
name|d
operator|.
name|getDetectors
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|3
argument_list|)
expr_stmt|;
comment|//just try to load it into autodetect to make sure no errors are thrown
name|Parser
name|auto
init|=
operator|new
name|AutoDetectParser
argument_list|(
name|c
argument_list|)
decl_stmt|;
block|}
block|}
block|}
end_class

end_unit

