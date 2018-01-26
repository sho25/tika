begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_interface
specifier|public
interface|interface
name|HTML
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_HTML_META
init|=
literal|"html_meta"
decl_stmt|;
comment|/**      * If a script element contains a src value, this value      * is set in the embedded document's metadata      */
name|Property
name|SCRIPT_SOURCE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_HTML_META
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"scriptSrc"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

