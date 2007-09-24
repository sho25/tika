begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2007 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
package|;
end_package

begin_comment
comment|/**  * An interface allowing a Tika object to be<code>Configured</code> by a  * {@link Configuration} object. Based on Apache Hadoop's configuration  * interface.  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|Configurable
block|{
comment|/**      * Configures the Tika object with the provided {@link Configuration} named      *<code>conf</code>.      *       * @param conf      *            The object's new {@link Configuration}.      */
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**      *       * @return The Tika object's existing {@link Configuration}.      */
specifier|public
name|Configuration
name|getConf
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

