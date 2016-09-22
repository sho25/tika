begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|recognition
package|;
end_package

begin_comment
comment|/**  * A model for recognised objects from graphics and texts typically includes  * human readable label for the object, language of the label, id and confidence score.  *  * @since Apache Tika 1.14  */
end_comment

begin_class
specifier|public
class|class
name|RecognisedObject
block|{
comment|/**      * Label of this object. Usually the name given to this object by humans      */
specifier|private
name|String
name|label
decl_stmt|;
comment|/**      * Language of label, Example : english      */
specifier|private
name|String
name|labelLang
decl_stmt|;
comment|/**      * Identifier for this object      */
specifier|private
name|String
name|id
decl_stmt|;
comment|/**      * Confidence score      */
specifier|private
name|double
name|confidence
decl_stmt|;
specifier|public
name|RecognisedObject
parameter_list|(
name|String
name|label
parameter_list|,
name|String
name|labelLang
parameter_list|,
name|String
name|id
parameter_list|,
name|double
name|confidence
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
name|this
operator|.
name|labelLang
operator|=
name|labelLang
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|confidence
operator|=
name|confidence
expr_stmt|;
block|}
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
name|label
return|;
block|}
specifier|public
name|void
name|setLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
block|}
specifier|public
name|String
name|getLabelLang
parameter_list|()
block|{
return|return
name|labelLang
return|;
block|}
specifier|public
name|void
name|setLabelLang
parameter_list|(
name|String
name|labelLang
parameter_list|)
block|{
name|this
operator|.
name|labelLang
operator|=
name|labelLang
expr_stmt|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|double
name|getConfidence
parameter_list|()
block|{
return|return
name|confidence
return|;
block|}
specifier|public
name|void
name|setConfidence
parameter_list|(
name|double
name|confidence
parameter_list|)
block|{
name|this
operator|.
name|confidence
operator|=
name|confidence
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"RecognisedObject{"
operator|+
literal|"label='"
operator|+
name|label
operator|+
literal|"\' ("
operator|+
name|labelLang
operator|+
literal|')'
operator|+
literal|", id='"
operator|+
name|id
operator|+
literal|'\''
operator|+
literal|", confidence="
operator|+
name|confidence
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit
