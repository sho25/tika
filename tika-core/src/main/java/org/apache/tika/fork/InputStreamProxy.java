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
name|fork
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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

begin_class
class|class
name|InputStreamProxy
extends|extends
name|InputStream
implements|implements
name|ForkProxy
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|4350939227765568438L
decl_stmt|;
specifier|private
specifier|final
name|int
name|resource
decl_stmt|;
specifier|private
specifier|transient
name|DataInputStream
name|input
decl_stmt|;
specifier|private
specifier|transient
name|DataOutputStream
name|output
decl_stmt|;
specifier|public
name|InputStreamProxy
parameter_list|(
name|int
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|DataInputStream
name|input
parameter_list|,
name|DataOutputStream
name|output
parameter_list|)
block|{
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeByte
argument_list|(
name|ForkServer
operator|.
name|RESOURCE
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
name|int
name|n
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|1
condition|)
block|{
return|return
name|input
operator|.
name|readUnsignedByte
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|n
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|output
operator|.
name|writeByte
argument_list|(
name|ForkServer
operator|.
name|RESOURCE
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
name|int
name|n
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|input
operator|.
name|readFully
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
block|}
end_class

end_unit

