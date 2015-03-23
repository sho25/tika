begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|batch
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|metadata
operator|.
name|Property
import|;
end_import

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

begin_comment
comment|/**  * This is a basic interface to handle a logical "file".    * This should enable code-agnostic handling of files from different   * sources: file system, database, etc.  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|FileResource
block|{
comment|//The literal lowercased extension of a file.  This may or may not
comment|//have any relationship to the actual type of the file.
specifier|public
specifier|static
specifier|final
name|Property
name|FILE_EXTENSION
init|=
name|Property
operator|.
name|internalText
argument_list|(
literal|"tika:file_ext"
argument_list|)
decl_stmt|;
comment|/**    * This is only used in logging to identify which file    * may have caused problems.  While it is probably best    * to use unique ids for the sake of debugging, it is not     * necessary that the ids be unique.  This id    * is never used as a hashkey by the batch processors, for example.    *     * @return an id for a FileResource    */
specifier|public
name|String
name|getResourceId
parameter_list|()
function_decl|;
comment|/**    * This gets the metadata available before the parsing of the file.    * This will typically be "external" metadata: file name,    * file size, file location, data stream, etc.  That is, things    * that are known about the file from outside information, not    * file-internal metadata.    *     * @return Metadata    */
specifier|public
name|Metadata
name|getMetadata
parameter_list|()
function_decl|;
comment|/**    *     * @return an InputStream for the FileResource    * @throws java.io.IOException    */
specifier|public
name|InputStream
name|openInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

