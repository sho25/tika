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
name|microsoft
operator|.
name|onenote
package|;
end_package

begin_class
class|class
name|RootObjectReference
block|{
name|CompactID
name|oidRoot
init|=
operator|new
name|CompactID
argument_list|()
decl_stmt|;
name|RootObjectReferenceBase
name|rootObjectReferenceBase
init|=
operator|new
name|RootObjectReferenceBase
argument_list|()
decl_stmt|;
specifier|public
name|CompactID
name|getOidRoot
parameter_list|()
block|{
return|return
name|oidRoot
return|;
block|}
specifier|public
name|RootObjectReference
name|setOidRoot
parameter_list|(
name|CompactID
name|oidRoot
parameter_list|)
block|{
name|this
operator|.
name|oidRoot
operator|=
name|oidRoot
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|RootObjectReferenceBase
name|getRootObjectReferenceBase
parameter_list|()
block|{
return|return
name|rootObjectReferenceBase
return|;
block|}
specifier|public
name|RootObjectReference
name|setRootObjectReferenceBase
parameter_list|(
name|RootObjectReferenceBase
name|rootObjectReferenceBase
parameter_list|)
block|{
name|this
operator|.
name|rootObjectReferenceBase
operator|=
name|rootObjectReferenceBase
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

