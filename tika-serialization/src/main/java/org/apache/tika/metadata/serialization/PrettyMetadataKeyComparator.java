begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|serialization
package|;
end_package

begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_class
specifier|public
class|class
name|PrettyMetadataKeyComparator
implements|implements
name|java
operator|.
name|util
operator|.
name|Comparator
argument_list|<
name|String
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|s1
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|s2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|//this is stinky.  This should reference AbstractRecursiveParserWrapperHandler.TIKA_CONTENT
comment|//but that would require making core a dependency of serialization...
comment|//do we want to do that?
if|if
condition|(
name|s1
operator|.
name|equals
argument_list|(
literal|"tika:content"
argument_list|)
condition|)
block|{
if|if
condition|(
name|s2
operator|.
name|equals
argument_list|(
literal|"tika:content"
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
literal|2
return|;
block|}
elseif|else
if|if
condition|(
name|s2
operator|.
name|equals
argument_list|(
literal|"tika:content"
argument_list|)
condition|)
block|{
return|return
operator|-
literal|2
return|;
block|}
comment|//do we want to lowercase?
return|return
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

