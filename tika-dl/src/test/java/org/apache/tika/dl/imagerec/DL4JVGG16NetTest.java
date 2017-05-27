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
name|dl
operator|.
name|imagerec
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
name|Test
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|DL4JVGG16NetTest
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
name|TikaConfig
name|config
init|=
operator|new
name|TikaConfig
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"dl4j-vgg16-config.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|Metadata
name|md
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|tika
operator|.
name|parse
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"lion.jpg"
argument_list|)
argument_list|,
name|md
argument_list|)
expr_stmt|;
name|String
index|[]
name|objects
init|=
name|md
operator|.
name|getValues
argument_list|(
literal|"OBJECT"
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|object
range|:
name|objects
control|)
block|{
if|if
condition|(
name|object
operator|.
name|contains
argument_list|(
literal|"lion"
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

