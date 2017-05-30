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
name|captioning
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|recognition
operator|.
name|RecognisedObject
import|;
end_import

begin_comment
comment|/**  * A model for caption objects from graphics and texts typically includes  * human readable sentence, language of the sentence and confidence score.  *  * @since Apache Tika 1.16  */
end_comment

begin_class
specifier|public
class|class
name|CaptionObject
extends|extends
name|RecognisedObject
block|{
specifier|public
name|CaptionObject
parameter_list|(
name|String
name|sentence
parameter_list|,
name|String
name|sentenceLang
parameter_list|,
name|double
name|confidence
parameter_list|)
block|{
name|super
argument_list|(
name|sentence
argument_list|,
name|sentenceLang
argument_list|,
literal|null
argument_list|,
name|confidence
argument_list|)
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
literal|"Caption{"
operator|+
literal|"sentence='"
operator|+
name|label
operator|+
literal|"\' ("
operator|+
name|labelLang
operator|+
literal|')'
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

