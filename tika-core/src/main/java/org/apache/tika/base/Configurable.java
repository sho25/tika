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
name|base
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
name|config
operator|.
name|Param
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
name|ParseContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Defines contract for configurable services  * @since Apache Tika 1.14  */
end_comment

begin_interface
specifier|public
interface|interface
name|Configurable
block|{
comment|/**      * Configure an instance with Tika Context      * @param context configuration instance in the form of context      * @throws TikaConfigException when an instance fails to work at the given context      * @since Apache Tika 1.14      */
name|void
name|configure
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|TikaConfigException
function_decl|;
comment|/**      * Gets parameters of this configurable instance      * @return parameters in the form  of a map of key value pairs      */
name|Map
argument_list|<
name|String
argument_list|,
name|Param
argument_list|<
name|?
argument_list|>
argument_list|>
name|getParams
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

