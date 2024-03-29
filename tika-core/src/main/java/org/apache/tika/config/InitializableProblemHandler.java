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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This is to be used to handle potential recoverable problems that  * might arise during initialization.  */
end_comment

begin_interface
specifier|public
interface|interface
name|InitializableProblemHandler
block|{
name|void
name|handleInitializableProblem
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|TikaConfigException
function_decl|;
comment|/**      * Strategy that simply ignores all problems.      */
name|InitializableProblemHandler
name|IGNORE
init|=
operator|new
name|InitializableProblemHandler
argument_list|()
block|{
specifier|public
name|void
name|handleInitializableProblem
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|message
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"IGNORE"
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Strategy that logs warnings of all problems using a {@link org.slf4j.Logger}      * created using the given class name.      */
name|InitializableProblemHandler
name|INFO
init|=
operator|new
name|InitializableProblemHandler
argument_list|()
block|{
specifier|public
name|void
name|handleInitializableProblem
parameter_list|(
name|String
name|classname
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|classname
argument_list|)
operator|.
name|info
argument_list|(
name|message
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
literal|"INFO"
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Strategy that logs warnings of all problems using a {@link org.slf4j.Logger}      * created using the given class name.      */
name|InitializableProblemHandler
name|WARN
init|=
operator|new
name|InitializableProblemHandler
argument_list|()
block|{
specifier|public
name|void
name|handleInitializableProblem
parameter_list|(
name|String
name|classname
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|classname
argument_list|)
operator|.
name|warn
argument_list|(
name|message
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
literal|"WARN"
return|;
block|}
block|}
decl_stmt|;
name|InitializableProblemHandler
name|THROW
init|=
operator|new
name|InitializableProblemHandler
argument_list|()
block|{
specifier|public
name|void
name|handleInitializableProblem
parameter_list|(
name|String
name|classname
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|TikaConfigException
block|{
throw|throw
operator|new
name|TikaConfigException
argument_list|(
name|message
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"THROW"
return|;
block|}
block|}
decl_stmt|;
name|InitializableProblemHandler
name|DEFAULT
init|=
name|WARN
decl_stmt|;
block|}
end_interface

end_unit

