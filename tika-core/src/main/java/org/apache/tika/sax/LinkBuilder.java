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
package|;
end_package

begin_class
class|class
name|LinkBuilder
block|{
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
specifier|private
name|String
name|uri
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|title
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|rel
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|public
name|LinkBuilder
parameter_list|(
name|String
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
specifier|public
name|void
name|setURI
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|uri
operator|=
literal|""
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setTitle
parameter_list|(
name|String
name|title
parameter_list|)
block|{
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|title
operator|=
name|title
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|title
operator|=
literal|""
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setRel
parameter_list|(
name|String
name|rel
parameter_list|)
block|{
if|if
condition|(
name|rel
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|rel
operator|=
literal|""
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|text
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Link
name|getLink
parameter_list|()
block|{
return|return
operator|new
name|Link
argument_list|(
name|type
argument_list|,
name|uri
argument_list|,
name|title
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
end_class

end_unit

