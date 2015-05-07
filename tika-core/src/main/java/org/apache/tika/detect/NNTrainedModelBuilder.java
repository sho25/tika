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
name|NNTrainedModelBuilder
block|{
specifier|private
name|MediaType
name|type
decl_stmt|;
specifier|private
name|int
name|numOfInputs
decl_stmt|;
specifier|private
name|int
name|numOfHidden
decl_stmt|;
specifier|private
name|int
name|numOfOutputs
decl_stmt|;
specifier|private
name|float
index|[]
name|params
decl_stmt|;
specifier|public
name|MediaType
name|getType
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
specifier|public
name|int
name|getNumOfInputs
parameter_list|()
block|{
return|return
name|numOfInputs
return|;
block|}
specifier|public
name|int
name|getNumOfHidden
parameter_list|()
block|{
return|return
name|numOfHidden
return|;
block|}
specifier|public
name|int
name|getNumOfOutputs
parameter_list|()
block|{
return|return
name|numOfOutputs
return|;
block|}
specifier|public
name|float
index|[]
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
specifier|final
name|MediaType
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
name|setNumOfInputs
parameter_list|(
specifier|final
name|int
name|numOfInputs
parameter_list|)
block|{
name|this
operator|.
name|numOfInputs
operator|=
name|numOfInputs
expr_stmt|;
block|}
specifier|public
name|void
name|setNumOfHidden
parameter_list|(
specifier|final
name|int
name|numOfHidden
parameter_list|)
block|{
name|this
operator|.
name|numOfHidden
operator|=
name|numOfHidden
expr_stmt|;
block|}
specifier|public
name|void
name|setNumOfOutputs
parameter_list|(
specifier|final
name|int
name|numOfOutputs
parameter_list|)
block|{
name|this
operator|.
name|numOfOutputs
operator|=
name|numOfOutputs
expr_stmt|;
block|}
specifier|public
name|void
name|setParams
parameter_list|(
name|float
index|[]
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
specifier|public
name|NNTrainedModel
name|build
parameter_list|()
block|{
return|return
operator|new
name|NNTrainedModel
argument_list|(
name|numOfInputs
argument_list|,
name|numOfHidden
argument_list|,
name|numOfOutputs
argument_list|,
name|params
argument_list|)
return|;
block|}
block|}
end_class

end_unit
