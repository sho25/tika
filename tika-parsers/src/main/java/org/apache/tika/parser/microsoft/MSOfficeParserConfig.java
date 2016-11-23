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
name|microsoft
package|;
end_package

begin_class
specifier|public
class|class
name|MSOfficeParserConfig
block|{
specifier|private
name|boolean
name|includeDeletedContent
init|=
literal|true
decl_stmt|;
comment|/**      * Sets whether or not the parser should include deleted content.      *<b>This has not been implemented in all MSOffice parsers yet!!!</b>      * @param includeDeletedContent      */
specifier|public
name|void
name|setIncludeDeletedContent
parameter_list|(
name|boolean
name|includeDeletedContent
parameter_list|)
block|{
name|this
operator|.
name|includeDeletedContent
operator|=
name|includeDeletedContent
expr_stmt|;
block|}
specifier|public
name|boolean
name|getIncludeDeletedContent
parameter_list|()
block|{
return|return
name|includeDeletedContent
return|;
block|}
block|}
end_class

end_unit

