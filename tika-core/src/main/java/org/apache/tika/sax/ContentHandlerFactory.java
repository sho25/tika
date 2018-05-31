begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_comment
comment|/**  * Interface to allow easier injection of code for getting a new ContentHandler  */
end_comment

begin_interface
specifier|public
interface|interface
name|ContentHandlerFactory
extends|extends
name|Serializable
block|{
specifier|public
name|ContentHandler
name|getNewContentHandler
parameter_list|()
function_decl|;
comment|/**      * @deprecated use {@link #getNewContentHandler(OutputStream, Charset)}      */
annotation|@
name|Deprecated
specifier|public
name|ContentHandler
name|getNewContentHandler
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|UnsupportedEncodingException
function_decl|;
specifier|public
name|ContentHandler
name|getNewContentHandler
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|Charset
name|charset
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

