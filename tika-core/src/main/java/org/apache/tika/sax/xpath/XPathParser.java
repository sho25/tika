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
name|Map
import|;
end_import

begin_comment
comment|/**  * Parser for a very simple XPath subset. Only the following XPath constructs  * (with namespaces) are supported:  *<ul>  *<li><code>.../node()</code></li>  *<li><code>.../text()</code></li>  *<li><code>.../@*</code></li>  *<li><code>.../@name</code></li>  *<li><code>.../*...</code></li>  *<li><code>.../name...</code></li>  *<li><code>...//*...</code></li>  *<li><code>...//name...</code></li>  *</ul>  *<p>  * In addition the non-abbreviated<code>.../descendant::node()</code>  * construct can be used for cases where the descendant-or-self axis  * used by the<code>...//node()</code> construct is not appropriate.  */
end_comment

begin_class
specifier|public
class|class
name|XPathParser
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|prefixes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|XPathParser
parameter_list|()
block|{     }
specifier|public
name|XPathParser
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespace
parameter_list|)
block|{
name|addPrefix
argument_list|(
name|prefix
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addPrefix
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespace
parameter_list|)
block|{
name|prefixes
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parses the given simple XPath expression to an evaluation state      * initialized at the document node. Invalid expressions are not flagged      * as errors, they just result in a failing evaluation state.      *      * @param xpath simple XPath expression      * @return XPath evaluation state      */
specifier|public
name|Matcher
name|parse
parameter_list|(
name|String
name|xpath
parameter_list|)
block|{
if|if
condition|(
name|xpath
operator|.
name|equals
argument_list|(
literal|"/text()"
argument_list|)
condition|)
block|{
return|return
name|TextMatcher
operator|.
name|INSTANCE
return|;
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|equals
argument_list|(
literal|"/node()"
argument_list|)
condition|)
block|{
return|return
name|NodeMatcher
operator|.
name|INSTANCE
return|;
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|equals
argument_list|(
literal|"/descendant::node()"
argument_list|)
operator|||
name|xpath
operator|.
name|equals
argument_list|(
literal|"/descendant:node()"
argument_list|)
condition|)
block|{
comment|// for compatibility
return|return
operator|new
name|CompositeMatcher
argument_list|(
name|TextMatcher
operator|.
name|INSTANCE
argument_list|,
operator|new
name|ChildMatcher
argument_list|(
operator|new
name|SubtreeMatcher
argument_list|(
name|NodeMatcher
operator|.
name|INSTANCE
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|equals
argument_list|(
literal|"/@*"
argument_list|)
condition|)
block|{
return|return
name|AttributeMatcher
operator|.
name|INSTANCE
return|;
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|ElementMatcher
operator|.
name|INSTANCE
return|;
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"/@"
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|xpath
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|!=
operator|-
literal|1
condition|)
block|{
name|prefix
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefixes
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
operator|new
name|NamedAttributeMatcher
argument_list|(
name|prefixes
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Matcher
operator|.
name|FAIL
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"/*"
argument_list|)
condition|)
block|{
return|return
operator|new
name|ChildMatcher
argument_list|(
name|parse
argument_list|(
name|xpath
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"///"
argument_list|)
condition|)
block|{
return|return
name|Matcher
operator|.
name|FAIL
return|;
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"//"
argument_list|)
condition|)
block|{
return|return
operator|new
name|SubtreeMatcher
argument_list|(
name|parse
argument_list|(
name|xpath
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|int
name|slash
init|=
name|xpath
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|slash
operator|==
operator|-
literal|1
condition|)
block|{
name|slash
operator|=
name|xpath
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|String
name|name
init|=
name|xpath
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|slash
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|!=
operator|-
literal|1
condition|)
block|{
name|prefix
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefixes
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
operator|new
name|NamedElementMatcher
argument_list|(
name|prefixes
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
argument_list|,
name|name
argument_list|,
name|parse
argument_list|(
name|xpath
operator|.
name|substring
argument_list|(
name|slash
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Matcher
operator|.
name|FAIL
return|;
block|}
block|}
else|else
block|{
return|return
name|Matcher
operator|.
name|FAIL
return|;
block|}
block|}
block|}
end_class

end_unit

