begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright owlocationNameEntitieship.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|config
operator|.
name|Param
import|;
end_import

begin_comment
comment|/**  * Stores URL for AgePredictor   */
end_comment

begin_class
specifier|public
class|class
name|AgeRecogniserConfig
block|{
specifier|private
name|String
name|pathClassifyModel
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|pathClassifyRegression
init|=
literal|null
decl_stmt|;
specifier|public
name|AgeRecogniserConfig
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Param
argument_list|>
name|params
parameter_list|)
block|{
name|setPathClassifyModel
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"age.path.classify"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
name|setPathClassifyRegression
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"age.path.regression"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getPathClassifyModel
parameter_list|()
block|{
return|return
name|pathClassifyModel
return|;
block|}
specifier|public
name|void
name|setPathClassifyModel
parameter_list|(
name|String
name|pathClassifyModel
parameter_list|)
block|{
name|this
operator|.
name|pathClassifyModel
operator|=
name|pathClassifyModel
expr_stmt|;
block|}
specifier|public
name|String
name|getPathClassifyRegression
parameter_list|()
block|{
return|return
name|pathClassifyRegression
return|;
block|}
specifier|public
name|void
name|setPathClassifyRegression
parameter_list|(
name|String
name|pathClassifyRegression
parameter_list|)
block|{
name|this
operator|.
name|pathClassifyRegression
operator|=
name|pathClassifyRegression
expr_stmt|;
block|}
block|}
end_class

end_unit

