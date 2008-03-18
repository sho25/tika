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
comment|/**  * Evaluation state of a<code>...//...</code> XPath expression. Applies the  * contained evaluation state to the current element and all its descendants.  */
end_comment

begin_class
specifier|public
class|class
name|SubtreeMatcher
extends|extends
name|Matcher
block|{
specifier|private
specifier|final
name|Matcher
name|then
decl_stmt|;
specifier|public
name|SubtreeMatcher
parameter_list|(
name|Matcher
name|then
parameter_list|)
block|{
name|this
operator|.
name|then
operator|=
name|then
expr_stmt|;
block|}
annotation|@
name|Override
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
name|Matcher
name|next
init|=
name|then
operator|.
name|descend
argument_list|(
name|namespace
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|==
name|FAIL
operator|||
name|next
operator|==
name|then
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
operator|new
name|CompositeMatcher
argument_list|(
name|next
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matchesElement
parameter_list|()
block|{
return|return
name|then
operator|.
name|matchesElement
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|then
operator|.
name|matchesAttribute
argument_list|(
name|namespace
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matchesText
parameter_list|()
block|{
return|return
name|then
operator|.
name|matchesText
argument_list|()
return|;
block|}
block|}
end_class

end_unit

