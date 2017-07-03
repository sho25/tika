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
name|config
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
name|exception
operator|.
name|TikaConfigException
import|;
end_import

begin_comment
comment|/**  * Components that must do special processing across multiple fields  * at initialization time should implement this interface.  *<p>  * TikaConfig will call initialize on Initializable classes after  * setting the parameters for non-statically service loaded classes.  *<p>  * TikaConfig will call checkInitialization on all Initializables,  * whether loaded statically  */
end_comment

begin_interface
specifier|public
interface|interface
name|Initializable
block|{
comment|/**      * @param params params to use for initialization      * @throws TikaConfigException      */
name|void
name|initialize
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Param
argument_list|>
name|params
parameter_list|)
throws|throws
name|TikaConfigException
function_decl|;
comment|/**      *      *      * @param problemHandler if there is a problem and no      *                                           custom initializableProblemHandler has been configured      *                                           via Initializable parameters,      *                                           this is called to respond.      * @throws TikaConfigException      */
name|void
name|checkInitialization
parameter_list|(
name|InitializableProblemHandler
name|problemHandler
parameter_list|)
throws|throws
name|TikaConfigException
function_decl|;
block|}
end_interface

end_unit

