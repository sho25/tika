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
comment|/**  * Defines a MagicClause.  *   */
end_comment

begin_class
class|class
name|MagicClause
implements|implements
name|Clause
block|{
specifier|private
name|Operator
name|op
init|=
literal|null
decl_stmt|;
specifier|private
name|Clause
name|c1
init|=
literal|null
decl_stmt|;
specifier|private
name|Clause
name|c2
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|size
init|=
literal|0
decl_stmt|;
name|MagicClause
parameter_list|(
name|Operator
name|op
parameter_list|,
name|Clause
name|c1
parameter_list|,
name|Clause
name|c2
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|c1
operator|=
name|c1
expr_stmt|;
name|this
operator|.
name|c2
operator|=
name|c2
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|c1
operator|.
name|size
argument_list|()
operator|+
name|c2
operator|.
name|size
argument_list|()
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
name|op
operator|.
name|eval
argument_list|(
name|c1
operator|.
name|eval
argument_list|(
name|data
argument_list|)
argument_list|,
name|c2
operator|.
name|eval
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuffer
argument_list|()
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|c1
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|c2
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

