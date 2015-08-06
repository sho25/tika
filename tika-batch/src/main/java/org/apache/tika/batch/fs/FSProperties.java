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
name|batch
operator|.
name|fs
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
name|Property
import|;
end_import

begin_class
specifier|public
class|class
name|FSProperties
block|{
specifier|private
specifier|final
specifier|static
name|String
name|TIKA_BATCH_FS_NAMESPACE
init|=
literal|"tika_batch_fs"
decl_stmt|;
comment|/**      * File's relative path (including file name) from a given source root      */
specifier|public
specifier|final
specifier|static
name|Property
name|FS_REL_PATH
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|TIKA_BATCH_FS_NAMESPACE
operator|+
literal|":relative_path"
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

