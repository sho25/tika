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
name|rtf
package|;
end_package

begin_comment
comment|/**  * Contains the information for a single list in the list or list override tables.  */
end_comment

begin_class
specifier|public
class|class
name|ListDescriptor
block|{
specifier|public
specifier|final
specifier|static
name|int
name|NUMBER_TYPE_BULLET
init|=
literal|23
decl_stmt|;
specifier|public
name|int
name|id
decl_stmt|;
comment|// We record this but don't make use if it today:
specifier|public
name|int
name|templateID
decl_stmt|;
comment|// We record this but don't make use if it today:
specifier|public
name|boolean
name|isStyle
decl_stmt|;
specifier|public
name|int
index|[]
name|numberType
init|=
operator|new
name|int
index|[
literal|9
index|]
decl_stmt|;
specifier|public
name|boolean
name|isUnordered
parameter_list|(
name|int
name|level
parameter_list|)
block|{
return|return
name|numberType
index|[
name|level
index|]
operator|==
name|NUMBER_TYPE_BULLET
return|;
block|}
block|}
end_class

end_unit

