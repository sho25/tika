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
name|detect
package|;
end_package

begin_class
specifier|public
specifier|abstract
class|class
name|TrainedModel
block|{
specifier|public
specifier|abstract
name|double
name|predict
parameter_list|(
name|double
index|[]
name|input
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|float
name|predict
parameter_list|(
name|float
index|[]
name|input
parameter_list|)
function_decl|;
block|}
end_class

end_unit

