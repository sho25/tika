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
name|chm
operator|.
name|accessor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  *   * Defines an accessor interface  *   * @param<T>  */
end_comment

begin_interface
specifier|public
interface|interface
name|ChmAccessor
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Serializable
block|{
comment|/**      * Parses chm accessor      *       * @param data      *            chm file      * @param chmAccessor      */
name|void
name|parse
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|T
name|chmAccessor
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

