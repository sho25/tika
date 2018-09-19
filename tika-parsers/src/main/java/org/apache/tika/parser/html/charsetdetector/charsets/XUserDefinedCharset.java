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
name|html
operator|.
name|charsetdetector
operator|.
name|charsets
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|NotImplementedException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|CharBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CoderResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_class
specifier|public
class|class
name|XUserDefinedCharset
extends|extends
name|Charset
block|{
specifier|public
name|XUserDefinedCharset
parameter_list|()
block|{
name|super
argument_list|(
literal|"x-user-defined"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|Charset
name|cs
parameter_list|)
block|{
return|return
name|cs
operator|.
name|equals
argument_list|(
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
return|;
block|}
specifier|public
name|CharsetDecoder
name|newDecoder
parameter_list|()
block|{
return|return
operator|new
name|CharsetDecoder
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|CoderResult
name|decodeLoop
parameter_list|(
name|ByteBuffer
name|in
parameter_list|,
name|CharBuffer
name|out
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|in
operator|.
name|hasRemaining
argument_list|()
condition|)
return|return
name|CoderResult
operator|.
name|UNDERFLOW
return|;
if|if
condition|(
operator|!
name|out
operator|.
name|hasRemaining
argument_list|()
condition|)
return|return
name|CoderResult
operator|.
name|OVERFLOW
return|;
name|byte
name|b
init|=
name|in
operator|.
name|get
argument_list|()
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
operator|(
name|b
operator|>=
literal|0
operator|)
condition|?
name|b
else|:
literal|0xF700
operator|+
operator|(
name|b
operator|&
literal|0xFF
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
specifier|public
name|CharsetEncoder
name|newEncoder
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|(
literal|"Encoding to x-user-defined is not implemented"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit
