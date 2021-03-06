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
operator|.
name|ooxml
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|UnderlinePatterns
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTUnderline
import|;
end_import

begin_comment
comment|/**  * WARNING: This class is mutable.  Make a copy of it  * if you want persistence!  */
end_comment

begin_class
specifier|public
class|class
name|RunProperties
block|{
name|boolean
name|italics
init|=
literal|false
decl_stmt|;
name|boolean
name|bold
init|=
literal|false
decl_stmt|;
name|boolean
name|strikeThrough
init|=
literal|false
decl_stmt|;
name|UnderlinePatterns
name|underline
init|=
name|UnderlinePatterns
operator|.
name|NONE
decl_stmt|;
specifier|public
name|boolean
name|isItalics
parameter_list|()
block|{
return|return
name|italics
return|;
block|}
specifier|public
name|boolean
name|isBold
parameter_list|()
block|{
return|return
name|bold
return|;
block|}
specifier|public
name|void
name|setItalics
parameter_list|(
name|boolean
name|italics
parameter_list|)
block|{
name|this
operator|.
name|italics
operator|=
name|italics
expr_stmt|;
block|}
specifier|public
name|void
name|setBold
parameter_list|(
name|boolean
name|bold
parameter_list|)
block|{
name|this
operator|.
name|bold
operator|=
name|bold
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStrikeThrough
parameter_list|()
block|{
return|return
name|strikeThrough
return|;
block|}
specifier|public
name|void
name|setStrike
parameter_list|(
name|boolean
name|strikeThrough
parameter_list|)
block|{
name|this
operator|.
name|strikeThrough
operator|=
name|strikeThrough
expr_stmt|;
block|}
specifier|public
name|UnderlinePatterns
name|getUnderline
parameter_list|()
block|{
return|return
name|underline
return|;
block|}
specifier|public
name|void
name|setUnderline
parameter_list|(
name|String
name|underlineString
parameter_list|)
block|{
if|if
condition|(
name|underlineString
operator|==
literal|null
operator|||
name|underlineString
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|underline
operator|=
name|UnderlinePatterns
operator|.
name|SINGLE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|UnderlinePatterns
operator|.
name|NONE
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|underlineString
argument_list|)
condition|)
block|{
name|underline
operator|=
name|UnderlinePatterns
operator|.
name|NONE
expr_stmt|;
block|}
else|else
block|{
comment|//TODO -- fill out rest
name|underline
operator|=
name|UnderlinePatterns
operator|.
name|SINGLE
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

