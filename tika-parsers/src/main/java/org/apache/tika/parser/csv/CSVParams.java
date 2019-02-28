begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|csv
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MediaType
import|;
end_import

begin_class
specifier|public
class|class
name|CSVParams
block|{
specifier|private
name|MediaType
name|mediaType
init|=
literal|null
decl_stmt|;
specifier|private
name|Character
name|delimiter
init|=
literal|null
decl_stmt|;
specifier|private
name|Charset
name|charset
init|=
literal|null
decl_stmt|;
name|CSVParams
parameter_list|()
block|{}
name|CSVParams
parameter_list|(
name|MediaType
name|mediaType
parameter_list|,
name|Charset
name|charset
parameter_list|)
block|{
name|this
operator|.
name|mediaType
operator|=
name|mediaType
expr_stmt|;
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
block|}
name|CSVParams
parameter_list|(
name|MediaType
name|mediaType
parameter_list|,
name|Charset
name|charset
parameter_list|,
name|Character
name|delimiter
parameter_list|)
block|{
name|this
operator|.
name|mediaType
operator|=
name|mediaType
expr_stmt|;
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|mediaType
operator|==
literal|null
operator|&&
name|delimiter
operator|==
literal|null
operator|&&
name|charset
operator|==
literal|null
return|;
block|}
specifier|public
name|boolean
name|isComplete
parameter_list|()
block|{
return|return
name|mediaType
operator|!=
literal|null
operator|&&
name|delimiter
operator|!=
literal|null
operator|&&
name|charset
operator|!=
literal|null
return|;
block|}
specifier|public
name|MediaType
name|getMediaType
parameter_list|()
block|{
return|return
name|mediaType
return|;
block|}
specifier|public
name|void
name|setMediaType
parameter_list|(
name|MediaType
name|mediaType
parameter_list|)
block|{
name|this
operator|.
name|mediaType
operator|=
name|mediaType
expr_stmt|;
block|}
specifier|public
name|Character
name|getDelimiter
parameter_list|()
block|{
return|return
name|delimiter
return|;
block|}
specifier|public
name|void
name|setDelimiter
parameter_list|(
name|Character
name|delimiter
parameter_list|)
block|{
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
block|}
specifier|public
name|Charset
name|getCharset
parameter_list|()
block|{
return|return
name|charset
return|;
block|}
specifier|public
name|void
name|setCharset
parameter_list|(
name|Charset
name|charset
parameter_list|)
block|{
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
block|}
block|}
end_class

end_unit

