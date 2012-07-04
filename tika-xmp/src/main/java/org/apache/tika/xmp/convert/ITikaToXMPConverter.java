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
name|xmp
operator|.
name|convert
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
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPMeta
import|;
end_import

begin_comment
comment|/**  * Interface for the specific<code>Metadata</code> to XMP converters  */
end_comment

begin_interface
specifier|public
interface|interface
name|ITikaToXMPConverter
block|{
comment|/**      * Converts a Tika {@link Metadata}-object into an {@link XMPMeta} containing the useful      * properties.      *      * @param metadata      *            a Tika Metadata object      * @return Returns an XMPMeta object.      * @throws XMPException      *             If an error occurs during the creation of the XMP object.      */
name|XMPMeta
name|process
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|XMPException
function_decl|;
block|}
end_interface

end_unit

