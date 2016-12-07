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
operator|.
name|xwpf
package|;
end_package

begin_class
specifier|public
class|class
name|XWPFParagraphProperties
block|{
specifier|private
name|String
name|styleId
decl_stmt|;
specifier|private
name|int
name|ilvl
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|numId
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|getStyleID
parameter_list|()
block|{
return|return
name|styleId
return|;
block|}
name|void
name|setStyleID
parameter_list|(
name|String
name|styleId
parameter_list|)
block|{
name|this
operator|.
name|styleId
operator|=
name|styleId
expr_stmt|;
block|}
name|void
name|reset
parameter_list|()
block|{
name|styleId
operator|=
literal|null
expr_stmt|;
name|ilvl
operator|=
operator|-
literal|1
expr_stmt|;
name|numId
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|public
name|void
name|setIlvl
parameter_list|(
name|int
name|ilvl
parameter_list|)
block|{
name|this
operator|.
name|ilvl
operator|=
name|ilvl
expr_stmt|;
block|}
specifier|public
name|void
name|setNumId
parameter_list|(
name|int
name|numId
parameter_list|)
block|{
name|this
operator|.
name|numId
operator|=
name|numId
expr_stmt|;
block|}
specifier|public
name|int
name|getIlvl
parameter_list|()
block|{
return|return
name|ilvl
return|;
block|}
specifier|public
name|int
name|getNumId
parameter_list|()
block|{
return|return
name|numId
return|;
block|}
block|}
end_class

end_unit

