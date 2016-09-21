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
operator|.
name|tf
package|;
end_package

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
name|Param
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
name|recognition
operator|.
name|RecognisedObject
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
name|helpers
operator|.
name|DefaultHandler
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
name|HashMap
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

begin_class
annotation|@
name|Ignore
specifier|public
class|class
name|TensorflowImageRecParserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|recognise
parameter_list|()
throws|throws
name|Exception
block|{
name|TensorflowImageRecParser
name|recogniser
init|=
operator|new
name|TensorflowImageRecParser
argument_list|()
decl_stmt|;
name|recogniser
operator|.
name|initialize
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Param
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"test-documents/testJPEG.jpg"
argument_list|)
init|)
block|{
name|List
argument_list|<
name|RecognisedObject
argument_list|>
name|objects
init|=
name|recogniser
operator|.
name|recognise
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|5
operator|==
name|objects
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|objectLabels
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RecognisedObject
name|object
range|:
name|objects
control|)
block|{
name|objectLabels
operator|.
name|add
argument_list|(
name|object
operator|.
name|getLabel
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|objectLabels
argument_list|)
expr_stmt|;
name|String
index|[]
name|expected
init|=
block|{
literal|"English foxhound"
block|,
literal|"Egyptian cat"
block|,
literal|"collie"
block|,
literal|"Border collie"
block|}
decl_stmt|;
for|for
control|(
name|String
name|label
range|:
name|expected
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|label
operator|+
literal|" is expected"
argument_list|,
name|objectLabels
operator|.
name|contains
argument_list|(
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

