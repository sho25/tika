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

begin_comment
comment|/**  * XPath element matcher. A matcher instance encapsulates a specific  * state in XPath evaluation.  */
end_comment

begin_class
specifier|public
class|class
name|Matcher
block|{
comment|/**      * State of a failed XPath evaluation, where nothing is matched.      * This matcher instance is used as a sentinel object whenever an      * XPath evaluation branch fails.      */
specifier|public
specifier|static
specifier|final
name|Matcher
name|FAIL
init|=
operator|new
name|Matcher
argument_list|()
decl_stmt|;
comment|/**      * Returns the XPath evaluation state that results from descending      * to a child element with the given name.      *      * @param namespace element namespace or<code>null</code>      * @param name element name      * @return next XPath evaluation state      */
specifier|public
name|Matcher
name|descend
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|FAIL
return|;
block|}
comment|/**      * Returns<code>true</code> if the XPath expression matches      * the element associated with this evaluation state.      *      * @return XPath evaluation state for this element      */
specifier|public
name|boolean
name|matchesElement
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns<code>true</code> if the XPath expression matches the named      * attribute of the element associated with this evaluation state.      *      * @param namespace attribute namespace or<code>null</code>      * @param name attribute name      * @return XPath evaluation state for named attribute of this element      */
specifier|public
name|boolean
name|matchesAttribute
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Returns<code>true</code> if the XPath expression matches all text      * nodes whose parent is the element associated with this evaluation      * state.      *      * @return XPath evaluation state for text children of this element      */
specifier|public
name|boolean
name|matchesText
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

