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
name|detect
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|io
operator|.
name|TikaInputStream
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
name|metadata
operator|.
name|Metadata
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
name|mime
operator|.
name|MediaType
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
name|mime
operator|.
name|MimeTypes
import|;
end_import

begin_comment
comment|/**  * A detector that knows about the container formats that we support   *  (eg POIFS, Zip), and is able to peek inside them to better figure   *  out the contents.  * Delegates to another {@link Detector} (normally {@link MimeTypes})  *  to handle detection for non container formats.   * Should normally be used with a {@link TikaInputStream} to minimise   *  the memory usage.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ContainerDetector
extends|extends
name|Detector
block|{
comment|/**       * What is the default type returned by this detector,       *  when it can't figure out anything more specific?      */
specifier|public
name|MediaType
name|getDefault
parameter_list|()
function_decl|;
comment|/**      * Detect on the generic input stream, if possible. This will      *  generally just return the default, as normally a       *  {@link TikaInputStream} is needed for proper detection.      */
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Does full, container aware detection for the file of      *  the appropriate container type.      */
specifier|public
name|MediaType
name|detect
parameter_list|(
name|TikaInputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

