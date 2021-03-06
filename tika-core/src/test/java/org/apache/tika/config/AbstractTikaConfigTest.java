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
name|config
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
name|assertNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|parser
operator|.
name|ParseContext
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

begin_comment
comment|/**  * Parent of Junit test classes for {@link TikaConfig}, including  *  Tika Core based ones, and ones in Tika Parsers that do things   *  that {@link TikaConfigTest} can't, do due to a need for the  *  full set of "real" classes of parsers / detectors  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTikaConfigTest
extends|extends
name|TikaTest
block|{
specifier|protected
specifier|static
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
name|String
name|getConfigPath
parameter_list|(
name|String
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
name|TikaConfig
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Test Tika Config not found: "
operator|+
name|config
argument_list|,
name|url
argument_list|)
expr_stmt|;
return|return
name|url
operator|.
name|toExternalForm
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|TikaConfig
name|getConfig
parameter_list|(
name|String
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"tika.config"
argument_list|,
name|getConfigPath
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TikaConfig
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|resetConfig
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tika.config"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

