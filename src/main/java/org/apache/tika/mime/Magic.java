begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
package|;
end_package

begin_comment
comment|/**  * Defines a magic for a MimeType. A magic is made of one or several  * MagicClause.  *   *   */
end_comment

begin_class
class|class
name|Magic
implements|implements
name|Clause
implements|,
name|Comparable
argument_list|<
name|Magic
argument_list|>
block|{
specifier|private
name|MimeType
name|type
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|priority
init|=
literal|50
decl_stmt|;
specifier|private
name|Clause
name|clause
init|=
literal|null
decl_stmt|;
name|Magic
parameter_list|()
block|{
name|this
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|Magic
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
name|void
name|setType
parameter_list|(
name|MimeType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
name|MimeType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
name|void
name|setClause
parameter_list|(
name|Clause
name|clause
parameter_list|)
block|{
name|this
operator|.
name|clause
operator|=
name|clause
expr_stmt|;
block|}
specifier|public
name|boolean
name|eval
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|clause
operator|.
name|eval
argument_list|(
name|data
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|clause
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|priority
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|clause
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Magic
name|o
parameter_list|)
block|{
name|int
name|diff
init|=
name|o
operator|.
name|priority
operator|-
name|priority
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
name|o
operator|.
name|size
argument_list|()
operator|-
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|diff
return|;
block|}
block|}
end_class

end_unit

