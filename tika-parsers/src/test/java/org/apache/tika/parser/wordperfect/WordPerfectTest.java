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
name|wordperfect
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

begin_comment
comment|/**  * Junit test class for the {@link WordPerfectParser}.  * @author Pascal Essiembre  */
end_comment

begin_class
specifier|public
class|class
name|WordPerfectTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testWordPerfectParser
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testWordPerfect.wpd"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|WordPerfectParser
operator|.
name|WP_6_x
operator|.
name|toString
argument_list|()
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|getValues
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"test test"
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
name|testVersion50
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test file "testWordPerfect_5_0.wp" is from govdocs1: 126546.wp
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testWordPerfect_5_0.wp"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|WordPerfectParser
operator|.
name|WP_5_0
operator|.
name|toString
argument_list|()
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|getValues
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Surrounded by her family"
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
name|testVersion51
parameter_list|()
throws|throws
name|Exception
block|{
comment|//testfile "testWordperfect_5_1.wp is from govdocs1: 758750.wp
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testWordPerfect_5_1.wp"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|WordPerfectParser
operator|.
name|WP_5_1
operator|.
name|toString
argument_list|()
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|getValues
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Summer is the peak travel time"
argument_list|,
name|r
operator|.
name|xml
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
