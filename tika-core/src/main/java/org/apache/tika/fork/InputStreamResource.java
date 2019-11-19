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
name|InputStreamResource
implements|implements
name|ForkResource
block|{
specifier|private
specifier|final
name|InputStream
name|stream
decl_stmt|;
specifier|public
name|InputStreamResource
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
specifier|public
name|Throwable
name|process
parameter_list|(
name|DataInputStream
name|input
parameter_list|,
name|DataOutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|n
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|n
index|]
decl_stmt|;
name|int
name|m
decl_stmt|;
try|try
block|{
name|m
operator|=
name|stream
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// returning exception causes deadlock
comment|// return e;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|m
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|output
operator|.
name|writeInt
argument_list|(
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|>
literal|0
condition|)
block|{
name|output
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

