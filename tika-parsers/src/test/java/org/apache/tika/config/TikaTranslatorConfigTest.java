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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|language
operator|.
name|translate
operator|.
name|DefaultTranslator
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
name|language
operator|.
name|translate
operator|.
name|EmptyTranslator
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

begin_comment
comment|/**  * Junit test class for {@link TikaConfig}, which cover things  *  that {@link TikaConfigTest} can't do due to a need for the  *  full set of translators  */
end_comment

begin_class
specifier|public
class|class
name|TikaTranslatorConfigTest
extends|extends
name|AbstractTikaConfigTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testDefaultBehaviour
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getTranslator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DefaultTranslator
operator|.
name|class
argument_list|,
name|config
operator|.
name|getTranslator
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRequestsDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|getConfig
argument_list|(
literal|"TIKA-1702-translator-default.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getDetector
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getTranslator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DefaultTranslator
operator|.
name|class
argument_list|,
name|config
operator|.
name|getTranslator
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRequestsEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|getConfig
argument_list|(
literal|"TIKA-1702-translator-empty.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getDetector
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getTranslator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EmptyTranslator
operator|.
name|class
argument_list|,
name|config
operator|.
name|getTranslator
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Currently, Translators don't support Composites, so      *  if multiple translators are given, only the first wins      */
annotation|@
name|Test
specifier|public
name|void
name|testRequestsMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|getConfig
argument_list|(
literal|"TIKA-1702-translator-empty-default.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getDetector
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getTranslator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EmptyTranslator
operator|.
name|class
argument_list|,
name|config
operator|.
name|getTranslator
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
