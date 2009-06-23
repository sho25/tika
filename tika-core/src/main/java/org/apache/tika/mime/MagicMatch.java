begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
package|;
end_package

begin_comment
comment|// JDK imports
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_comment
comment|/**  * Defines a magic match.  *   *   */
end_comment

begin_class
class|class
name|MagicMatch
implements|implements
name|Clause
block|{
specifier|private
name|int
name|offsetStart
decl_stmt|;
specifier|private
name|int
name|offsetEnd
decl_stmt|;
specifier|private
name|String
name|type
decl_stmt|;
specifier|private
name|BigInteger
name|mask
decl_stmt|;
specifier|private
name|BigInteger
name|value
decl_stmt|;
specifier|private
name|int
name|length
decl_stmt|;
name|MagicMatch
parameter_list|(
name|int
name|offsetStart
parameter_list|,
name|int
name|offsetEnd
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|mask
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|this
operator|.
name|offsetStart
operator|=
name|offsetStart
expr_stmt|;
name|this
operator|.
name|offsetEnd
operator|=
name|offsetEnd
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|byte
index|[]
name|decoded
init|=
name|decodeValue
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|this
operator|.
name|length
operator|=
name|decoded
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|value
operator|=
operator|new
name|BigInteger
argument_list|(
name|decoded
argument_list|)
expr_stmt|;
if|if
condition|(
name|mask
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|mask
operator|=
operator|new
name|BigInteger
argument_list|(
name|decodeValue
argument_list|(
name|type
argument_list|,
name|mask
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|this
operator|.
name|value
operator|.
name|and
argument_list|(
name|this
operator|.
name|mask
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|byte
index|[]
name|decodeValue
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|MimeTypeException
block|{
comment|// Preliminary check
if|if
condition|(
operator|(
name|value
operator|==
literal|null
operator|)
operator|||
operator|(
name|type
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|byte
index|[]
name|decoded
init|=
literal|null
decl_stmt|;
name|String
name|tmpVal
init|=
literal|null
decl_stmt|;
name|int
name|radix
init|=
literal|8
decl_stmt|;
comment|// hex
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0x"
argument_list|)
condition|)
block|{
name|tmpVal
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|radix
operator|=
literal|16
expr_stmt|;
block|}
else|else
block|{
name|tmpVal
operator|=
name|value
expr_stmt|;
name|radix
operator|=
literal|8
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"string"
argument_list|)
condition|)
block|{
name|decoded
operator|=
name|decodeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
name|decoded
operator|=
name|tmpVal
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"host16"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"little16"
argument_list|)
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0x00FF
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"big16"
argument_list|)
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0x00FF
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"host32"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"little32"
argument_list|)
condition|)
block|{
name|long
name|i
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x000000FF
operator|)
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x0000FF00
operator|)
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x00FF0000
operator|)
operator|>>
literal|16
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0xFF000000
operator|)
operator|>>
literal|24
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"big32"
argument_list|)
condition|)
block|{
name|long
name|i
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0xFF000000
operator|)
operator|>>
literal|24
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x00FF0000
operator|)
operator|>>
literal|16
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x0000FF00
operator|)
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x000000FF
operator|)
argument_list|)
block|}
expr_stmt|;
block|}
return|return
name|decoded
return|;
block|}
specifier|private
name|byte
index|[]
name|decodeString
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|MimeTypeException
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0x"
argument_list|)
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
operator|(
name|value
operator|.
name|length
argument_list|()
operator|-
literal|2
operator|)
operator|/
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|2
operator|+
name|i
operator|*
literal|2
argument_list|,
literal|4
operator|+
name|i
operator|*
literal|2
argument_list|)
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
try|try
block|{
name|ByteArrayOutputStream
name|decoded
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
name|decoded
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'x'
condition|)
block|{
name|decoded
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|2
argument_list|,
name|i
operator|+
literal|4
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|+=
literal|3
expr_stmt|;
block|}
else|else
block|{
name|int
name|j
init|=
name|i
operator|+
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|j
operator|<
name|i
operator|+
literal|4
operator|)
operator|&&
operator|(
name|j
operator|<
name|value
operator|.
name|length
argument_list|()
operator|)
operator|&&
operator|(
name|Character
operator|.
name|isDigit
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|)
operator|)
condition|)
block|{
name|j
operator|++
expr_stmt|;
block|}
name|decoded
operator|.
name|write
argument_list|(
name|Short
operator|.
name|decode
argument_list|(
literal|"0"
operator|+
name|value
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|j
argument_list|)
argument_list|)
operator|.
name|byteValue
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|=
name|j
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
name|decoded
operator|.
name|write
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|decoded
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Invalid string value: "
operator|+
name|value
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|boolean
name|eval
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|offsetStart
init|;
name|i
operator|<=
name|offsetEnd
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|data
operator|.
name|length
operator|<
operator|(
name|this
operator|.
name|length
operator|+
name|i
operator|)
condition|)
block|{
comment|// Not enough data...
return|return
literal|false
return|;
block|}
name|byte
index|[]
name|array
init|=
operator|new
name|byte
index|[
name|this
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|i
argument_list|,
name|array
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|length
argument_list|)
expr_stmt|;
name|BigInteger
name|content
init|=
operator|new
name|BigInteger
argument_list|(
name|array
argument_list|)
decl_stmt|;
comment|// System.out.println("Evaluating " + content);
if|if
condition|(
name|mask
operator|!=
literal|null
condition|)
block|{
name|content
operator|=
name|content
operator|.
name|and
argument_list|(
name|mask
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|content
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|offsetStart
operator|+
literal|":"
operator|+
name|offsetEnd
operator|+
literal|"("
operator|+
name|type
operator|+
literal|")-"
operator|+
name|mask
operator|+
literal|"#"
operator|+
name|value
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

