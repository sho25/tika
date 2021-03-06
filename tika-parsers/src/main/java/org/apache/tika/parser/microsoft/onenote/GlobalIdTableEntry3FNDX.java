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
specifier|public
class|class
name|GlobalIdTableEntry3FNDX
block|{
name|long
name|indexCopyFromStart
decl_stmt|;
name|long
name|entriesToCopy
decl_stmt|;
name|long
name|indexCopyToStart
decl_stmt|;
specifier|public
name|long
name|getIndexCopyFromStart
parameter_list|()
block|{
return|return
name|indexCopyFromStart
return|;
block|}
specifier|public
name|GlobalIdTableEntry3FNDX
name|setIndexCopyFromStart
parameter_list|(
name|long
name|indexCopyFromStart
parameter_list|)
block|{
name|this
operator|.
name|indexCopyFromStart
operator|=
name|indexCopyFromStart
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getEntriesToCopy
parameter_list|()
block|{
return|return
name|entriesToCopy
return|;
block|}
specifier|public
name|GlobalIdTableEntry3FNDX
name|setEntriesToCopy
parameter_list|(
name|long
name|entriesToCopy
parameter_list|)
block|{
name|this
operator|.
name|entriesToCopy
operator|=
name|entriesToCopy
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getIndexCopyToStart
parameter_list|()
block|{
return|return
name|indexCopyToStart
return|;
block|}
specifier|public
name|GlobalIdTableEntry3FNDX
name|setIndexCopyToStart
parameter_list|(
name|long
name|indexCopyToStart
parameter_list|)
block|{
name|this
operator|.
name|indexCopyToStart
operator|=
name|indexCopyToStart
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

