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
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Interface for error handling strategies in service class loading.  * You can implement this interface for a custom error handling mechanism,  * or use one of the predefined strategies.  *  * @since Apache Tika 0.9  */
end_comment

begin_interface
specifier|public
interface|interface
name|LoadErrorHandler
block|{
comment|/**      * Handles a problem encountered when trying to load the specified      * service class. The implementation can log or otherwise process      * the given error information. If the method returns normally, then      * the service loader simply skips this class and continues with the      * next one.      *      * @param classname name of the service class      * @param throwable the encountered problem      */
name|void
name|handleLoadError
parameter_list|(
name|String
name|classname
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
function_decl|;
comment|/**      * Strategy that simply ignores all problems.      */
name|LoadErrorHandler
name|IGNORE
init|=
operator|new
name|LoadErrorHandler
argument_list|()
block|{
specifier|public
name|void
name|handleLoadError
parameter_list|(
name|String
name|classname
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{         }
block|}
decl_stmt|;
comment|/**      * Strategy that logs warnings of all problems using a {@link Logger}      * created using the given class name.      */
name|LoadErrorHandler
name|WARN
init|=
operator|new
name|LoadErrorHandler
argument_list|()
block|{
specifier|public
name|void
name|handleLoadError
parameter_list|(
name|String
name|classname
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|Logger
operator|.
name|getLogger
argument_list|(
name|classname
argument_list|)
operator|.
name|log
argument_list|(
name|Level
operator|.
name|WARNING
argument_list|,
literal|"Unable to load "
operator|+
name|classname
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**      * Strategy that throws a {@link RuntimeException} with the given      * throwable as the root cause, thus interrupting the entire service      * loading operation.      */
name|LoadErrorHandler
name|THROW
init|=
operator|new
name|LoadErrorHandler
argument_list|()
block|{
specifier|public
name|void
name|handleLoadError
parameter_list|(
name|String
name|classname
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to load "
operator|+
name|classname
argument_list|,
name|throwable
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

