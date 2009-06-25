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
name|sax
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
name|io
operator|.
name|ClosedInputStream
import|;
end_import

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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_comment
comment|/**  * Content handler decorator that always returns an empty stream from the  * {@link #resolveEntity(String, String)} method to prevent potential  * network or other external resources from being accessed by an XML parser.  *  * @see<a href="https://issues.apache.org/jira/browse/TIKA-185">TIKA-185</a>  */
end_comment

begin_class
specifier|public
class|class
name|OfflineContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
specifier|public
name|OfflineContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns an empty stream. This will make an XML parser silently      * ignore any external entities.      */
annotation|@
name|Override
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
return|return
operator|new
name|InputSource
argument_list|(
operator|new
name|ClosedInputStream
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

