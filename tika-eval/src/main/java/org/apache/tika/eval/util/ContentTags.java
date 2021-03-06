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
name|eval
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_class
specifier|public
class|class
name|ContentTags
block|{
specifier|public
specifier|static
specifier|final
name|ContentTags
name|EMPTY_CONTENT_TAGS
init|=
operator|new
name|ContentTags
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|tags
decl_stmt|;
specifier|final
name|String
name|content
decl_stmt|;
name|boolean
name|parseException
decl_stmt|;
specifier|private
name|ContentTags
parameter_list|()
block|{
name|this
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ContentTags
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|this
argument_list|(
name|content
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ContentTags
parameter_list|(
name|String
name|content
parameter_list|,
name|boolean
name|parseException
parameter_list|)
block|{
name|this
argument_list|(
name|content
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|parseException
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ContentTags
parameter_list|(
name|String
name|content
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|tags
parameter_list|)
block|{
name|this
argument_list|(
name|content
argument_list|,
name|tags
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ContentTags
parameter_list|(
name|String
name|content
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|tags
parameter_list|,
name|boolean
name|parseException
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
name|this
operator|.
name|tags
operator|=
name|tags
expr_stmt|;
name|this
operator|.
name|parseException
operator|=
name|parseException
expr_stmt|;
block|}
specifier|public
name|String
name|getContent
parameter_list|()
block|{
return|return
name|content
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getTags
parameter_list|()
block|{
return|return
name|tags
return|;
block|}
specifier|public
name|boolean
name|getParseException
parameter_list|()
block|{
return|return
name|parseException
return|;
block|}
specifier|public
name|void
name|setParseException
parameter_list|(
name|boolean
name|parseException
parameter_list|)
block|{
name|this
operator|.
name|parseException
operator|=
name|parseException
expr_stmt|;
block|}
block|}
end_class

end_unit

