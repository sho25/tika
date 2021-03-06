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
name|OneNotePropertyId
block|{
name|OneNotePropertyEnum
name|propertyEnum
decl_stmt|;
name|long
name|pid
decl_stmt|;
name|long
name|type
decl_stmt|;
name|boolean
name|inlineBool
decl_stmt|;
specifier|public
name|OneNotePropertyId
parameter_list|()
block|{     }
specifier|public
name|OneNotePropertyId
parameter_list|(
name|long
name|pid
parameter_list|)
block|{
name|this
operator|.
name|pid
operator|=
name|pid
expr_stmt|;
name|propertyEnum
operator|=
name|OneNotePropertyEnum
operator|.
name|of
argument_list|(
name|pid
argument_list|)
expr_stmt|;
name|type
operator|=
name|pid
operator|>>
literal|26
operator|&
literal|0x1f
expr_stmt|;
name|inlineBool
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|0x2
condition|)
block|{
name|inlineBool
operator|=
operator|(
operator|(
name|pid
operator|>>
literal|31
operator|)
operator|&
literal|0x1
operator|)
operator|>
literal|0
expr_stmt|;
comment|// set the bool value from header
block|}
else|else
block|{
if|if
condition|(
operator|(
operator|(
name|pid
operator|>>
literal|31
operator|)
operator|&
literal|0x1
operator|)
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Reserved non-zero"
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|OneNotePropertyEnum
name|getPropertyEnum
parameter_list|()
block|{
return|return
name|propertyEnum
return|;
block|}
specifier|public
name|OneNotePropertyId
name|setPropertyEnum
parameter_list|(
name|OneNotePropertyEnum
name|propertyEnum
parameter_list|)
block|{
name|this
operator|.
name|propertyEnum
operator|=
name|propertyEnum
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getPid
parameter_list|()
block|{
return|return
name|pid
return|;
block|}
specifier|public
name|OneNotePropertyId
name|setPid
parameter_list|(
name|long
name|pid
parameter_list|)
block|{
name|this
operator|.
name|pid
operator|=
name|pid
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|OneNotePropertyId
name|setType
parameter_list|(
name|long
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|boolean
name|isInlineBool
parameter_list|()
block|{
return|return
name|inlineBool
return|;
block|}
specifier|public
name|OneNotePropertyId
name|setInlineBool
parameter_list|(
name|boolean
name|inlineBool
parameter_list|)
block|{
name|this
operator|.
name|inlineBool
operator|=
name|inlineBool
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{"
operator|+
name|propertyEnum
operator|+
literal|", pid=0x"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|pid
argument_list|)
operator|+
literal|", type=0x"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|type
argument_list|)
operator|+
literal|", inlineBool="
operator|+
name|inlineBool
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

