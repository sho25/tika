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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Sentinel class for the crawler to add to the queue to let  * the consumers know that they should shutdown.  */
end_comment

begin_class
class|class
name|PoisonFileResource
implements|implements
name|FileResource
block|{
comment|/**    * always returns null    */
annotation|@
name|Override
specifier|public
name|Metadata
name|getMetadata
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * always returns null    */
annotation|@
name|Override
specifier|public
name|InputStream
name|openInputStream
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * always returns null    */
annotation|@
name|Override
specifier|public
name|String
name|getResourceId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

