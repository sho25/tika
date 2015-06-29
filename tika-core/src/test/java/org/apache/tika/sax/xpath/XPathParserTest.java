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
name|sax
operator|.
name|xpath
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
name|assertFalse
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

begin_class
specifier|public
class|class
name|XPathParserTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NS
init|=
literal|"test namespace"
decl_stmt|;
specifier|private
name|XPathParser
name|parser
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|parser
operator|=
operator|new
name|XPathParser
argument_list|()
expr_stmt|;
name|parser
operator|.
name|addPrefix
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|parser
operator|.
name|addPrefix
argument_list|(
literal|"prefix"
argument_list|,
name|NS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testText
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/text()"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAnyAttribute
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/@*"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNamedAttribute
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/@name"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPrefixedAttribute
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/@prefix:name"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNode
parameter_list|()
block|{
comment|// node() = child::node() selects all the children of the context node. Note that no attribute nodes are returned,
comment|// because attributes are not children.
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/node()"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDescendantNode
parameter_list|()
block|{
comment|// child::node() selects all the children of the context node. Note that no attribute nodes are returned,
comment|// because attributes are not children.
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/descendant::node()"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDescendantOrSelfNode
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/descendant-or-self::node()"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDescendantOrSelfElement
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/descendant-or-self::node()/*"
argument_list|)
decl_stmt|;
comment|// Matcher matcher = parser.parse("//*");
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAnyElement
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/*"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNamedElement
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/name"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
literal|null
argument_list|,
literal|"enam"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPrefixedElement
parameter_list|()
block|{
name|Matcher
name|matcher
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"/prefix:name"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Matcher
operator|.
name|FAIL
argument_list|,
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"enam"
argument_list|)
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|matcher
operator|.
name|descend
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|matchesElement
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|matcher
operator|.
name|matchesAttribute
argument_list|(
name|NS
argument_list|,
literal|"eman"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

