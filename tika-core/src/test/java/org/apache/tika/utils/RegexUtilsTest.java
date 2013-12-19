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
name|utils
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test case for {@link RegexUtils}.  *  * @version $Revision$ $Date$  */
end_comment

begin_class
specifier|public
class|class
name|RegexUtilsTest
block|{
comment|/**       * Test {@link RegexUtils#extractLinks(String)} with no links.      */
annotation|@
name|Test
specifier|public
name|void
name|testExtractLinksNone
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|links
init|=
literal|null
decl_stmt|;
name|links
operator|=
name|RegexUtils
operator|.
name|extractLinks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|links
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|links
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|links
operator|=
name|RegexUtils
operator|.
name|extractLinks
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|links
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|links
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|links
operator|=
name|RegexUtils
operator|.
name|extractLinks
argument_list|(
literal|"Test with no links "
operator|+
literal|"What about www.google.com"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|links
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|links
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**       * Test {@link RegexUtils#extractLinks(String)} for http.      */
annotation|@
name|Test
specifier|public
name|void
name|testExtractLinksHttp
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|links
init|=
name|RegexUtils
operator|.
name|extractLinks
argument_list|(
literal|"Test with http://www.nutch.org/index.html is it found? "
operator|+
literal|"What about www.google.com at http://www.google.de "
operator|+
literal|"A longer URL could be http://www.sybit.com/solutions/portals.html"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Url not found!"
argument_list|,
name|links
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong URL"
argument_list|,
literal|"http://www.nutch.org/index.html"
argument_list|,
name|links
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong URL"
argument_list|,
literal|"http://www.google.de"
argument_list|,
name|links
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong URL"
argument_list|,
literal|"http://www.sybit.com/solutions/portals.html"
argument_list|,
name|links
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**       * Test {@link RegexUtils#extractLinks(String)} for ftp.      */
annotation|@
name|Test
specifier|public
name|void
name|testExtractLinksFtp
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|links
init|=
name|RegexUtils
operator|.
name|extractLinks
argument_list|(
literal|"Test with ftp://www.nutch.org is it found? "
operator|+
literal|"What about www.google.com at ftp://www.google.de"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Url not found!"
argument_list|,
name|links
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong URL"
argument_list|,
literal|"ftp://www.nutch.org"
argument_list|,
name|links
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong URL"
argument_list|,
literal|"ftp://www.google.de"
argument_list|,
name|links
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

