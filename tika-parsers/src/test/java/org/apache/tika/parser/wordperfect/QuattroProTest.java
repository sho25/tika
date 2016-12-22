begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright 2016 Norconex Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|QuattroProTest
extends|extends
name|TikaTest
block|{
comment|//TODO add testWB/testQUATTRO.wb3 if .wb? files get supported
annotation|@
name|Test
specifier|public
name|void
name|testQPW
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testQUATTRO.qpw"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|QuattroProParser
operator|.
name|QP_9
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
literal|"This is an example spreadsheet"
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
name|testWB3
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testQUATTRO.wb3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"org.apache.tika.parser.EmptyParser"
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|get
argument_list|(
literal|"X-Parsed-By"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|QuattroProParser
operator|.
name|QP_7_8
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
block|}
block|}
end_class

end_unit

