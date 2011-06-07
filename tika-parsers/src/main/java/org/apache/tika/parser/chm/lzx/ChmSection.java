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
name|chm
operator|.
name|lzx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|parser
operator|.
name|chm
operator|.
name|core
operator|.
name|ChmCommons
import|;
end_import

begin_class
specifier|public
class|class
name|ChmSection
block|{
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
name|int
name|swath
decl_stmt|;
comment|// kiks
specifier|private
name|int
name|total
decl_stmt|;
comment|// remains
specifier|private
name|int
name|buffer
decl_stmt|;
comment|// val
specifier|public
name|ChmSection
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|ChmCommons
operator|.
name|assertByteArrayNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|setData
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
comment|/* Utilities */
specifier|public
name|byte
index|[]
name|reverseByteOrder
parameter_list|(
name|byte
index|[]
name|toBeReversed
parameter_list|)
block|{
name|ChmCommons
operator|.
name|assertByteArrayNotNull
argument_list|(
name|toBeReversed
argument_list|)
expr_stmt|;
name|ChmCommons
operator|.
name|reverse
argument_list|(
name|toBeReversed
argument_list|)
expr_stmt|;
return|return
name|toBeReversed
return|;
block|}
specifier|public
name|int
name|checkBit
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
operator|(
name|getBuffer
argument_list|()
operator|&
operator|(
literal|1
operator|<<
operator|(
name|getTotal
argument_list|()
operator|-
name|i
operator|)
operator|)
operator|)
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
literal|1
return|;
block|}
specifier|public
name|int
name|getSyncBits
parameter_list|(
name|int
name|bit
parameter_list|)
block|{
return|return
name|getDesyncBits
argument_list|(
name|bit
argument_list|,
name|bit
argument_list|)
return|;
block|}
specifier|public
name|int
name|getDesyncBits
parameter_list|(
name|int
name|bit
parameter_list|,
name|int
name|removeBit
parameter_list|)
block|{
while|while
condition|(
name|getTotal
argument_list|()
operator|<
literal|16
condition|)
block|{
name|setBuffer
argument_list|(
operator|(
name|getBuffer
argument_list|()
operator|<<
literal|16
operator|)
operator|+
name|unmarshalUByte
argument_list|()
operator|+
operator|(
name|unmarshalUByte
argument_list|()
operator|<<
literal|8
operator|)
argument_list|)
expr_stmt|;
name|setTotal
argument_list|(
name|getTotal
argument_list|()
operator|+
literal|16
argument_list|)
expr_stmt|;
block|}
name|int
name|tmp
init|=
operator|(
name|getBuffer
argument_list|()
operator|>>>
operator|(
name|getTotal
argument_list|()
operator|-
name|bit
operator|)
operator|)
decl_stmt|;
name|setTotal
argument_list|(
name|getTotal
argument_list|()
operator|-
name|removeBit
argument_list|)
expr_stmt|;
name|setBuffer
argument_list|(
name|getBuffer
argument_list|()
operator|-
operator|(
operator|(
name|getBuffer
argument_list|()
operator|>>>
name|getTotal
argument_list|()
operator|)
operator|<<
name|getTotal
argument_list|()
operator|)
argument_list|)
expr_stmt|;
return|return
name|tmp
return|;
block|}
specifier|public
name|int
name|unmarshalUByte
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|getByte
argument_list|()
operator|&
literal|255
argument_list|)
return|;
block|}
specifier|public
name|byte
name|getByte
parameter_list|()
block|{
if|if
condition|(
name|getSwath
argument_list|()
operator|<
name|getData
argument_list|()
operator|.
name|length
condition|)
block|{
name|setSwath
argument_list|(
name|getSwath
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|getData
argument_list|()
index|[
name|getSwath
argument_list|()
operator|-
literal|1
index|]
return|;
block|}
else|else
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getLeft
parameter_list|()
block|{
return|return
operator|(
name|getData
argument_list|()
operator|.
name|length
operator|-
name|getSwath
argument_list|()
operator|)
return|;
block|}
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
specifier|public
name|BigInteger
name|getBigInteger
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|getData
argument_list|()
operator|==
literal|null
condition|)
return|return
name|BigInteger
operator|.
name|ZERO
return|;
if|if
condition|(
name|getData
argument_list|()
operator|.
name|length
operator|-
name|getSwath
argument_list|()
operator|<
name|i
condition|)
name|i
operator|=
name|getData
argument_list|()
operator|.
name|length
operator|-
name|getSwath
argument_list|()
expr_stmt|;
name|byte
index|[]
name|tmp
init|=
operator|new
name|byte
index|[
name|i
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|-
literal|1
init|;
name|j
operator|>=
literal|0
condition|;
name|j
operator|--
control|)
block|{
name|tmp
index|[
name|i
operator|-
name|j
operator|-
literal|1
index|]
operator|=
name|getData
argument_list|()
index|[
name|getSwath
argument_list|()
operator|+
name|j
index|]
expr_stmt|;
block|}
name|setSwath
argument_list|(
name|getSwath
argument_list|()
operator|+
name|i
argument_list|)
expr_stmt|;
return|return
operator|new
name|BigInteger
argument_list|(
name|tmp
argument_list|)
return|;
block|}
specifier|public
name|byte
index|[]
name|stringToAsciiBytes
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|char
index|[]
name|c
init|=
name|s
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|byte
index|[]
name|byteval
init|=
operator|new
name|byte
index|[
name|c
operator|.
name|length
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
name|c
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|byteval
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|c
index|[
name|i
index|]
expr_stmt|;
return|return
name|byteval
return|;
block|}
specifier|public
name|BigInteger
name|unmarshalUlong
parameter_list|()
block|{
return|return
name|getBigInteger
argument_list|(
literal|8
argument_list|)
return|;
block|}
specifier|public
name|long
name|unmarshalUInt
parameter_list|()
block|{
return|return
name|getBigInteger
argument_list|(
literal|4
argument_list|)
operator|.
name|longValue
argument_list|()
return|;
block|}
specifier|public
name|int
name|unmarshalInt
parameter_list|()
block|{
return|return
name|getBigInteger
argument_list|(
literal|4
argument_list|)
operator|.
name|intValue
argument_list|()
return|;
block|}
specifier|public
name|byte
index|[]
name|unmarshalBytes
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
return|return
operator|new
name|byte
index|[
literal|1
index|]
return|;
name|byte
index|[]
name|t
init|=
operator|new
name|byte
index|[
name|i
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|i
condition|;
name|j
operator|++
control|)
name|t
index|[
name|j
index|]
operator|=
name|getData
argument_list|()
index|[
name|j
operator|+
name|getSwath
argument_list|()
index|]
expr_stmt|;
name|setSwath
argument_list|(
name|getSwath
argument_list|()
operator|+
name|i
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
specifier|public
name|BigInteger
name|getEncint
parameter_list|()
block|{
name|byte
name|ob
decl_stmt|;
name|BigInteger
name|bi
init|=
name|BigInteger
operator|.
name|ZERO
decl_stmt|;
name|byte
index|[]
name|nb
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|ob
operator|=
name|this
operator|.
name|getByte
argument_list|()
operator|)
operator|<
literal|0
condition|)
block|{
name|nb
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|ob
operator|&
literal|0x7f
operator|)
argument_list|)
expr_stmt|;
name|bi
operator|=
name|bi
operator|.
name|shiftLeft
argument_list|(
literal|7
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BigInteger
argument_list|(
name|nb
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|nb
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|ob
operator|&
literal|0x7f
operator|)
argument_list|)
expr_stmt|;
name|bi
operator|=
name|bi
operator|.
name|shiftLeft
argument_list|(
literal|7
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BigInteger
argument_list|(
name|nb
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bi
return|;
block|}
specifier|public
name|char
name|unmarshalUtfChar
parameter_list|()
block|{
name|byte
name|ob
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
name|byte
index|[]
name|ba
decl_stmt|;
name|ob
operator|=
name|this
operator|.
name|getByte
argument_list|()
expr_stmt|;
if|if
condition|(
name|ob
operator|<
literal|0
condition|)
block|{
name|i
operator|=
literal|2
expr_stmt|;
while|while
condition|(
operator|(
name|ob
operator|<<
operator|(
literal|24
operator|+
name|i
operator|)
operator|)
operator|<
literal|0
condition|)
name|i
operator|++
expr_stmt|;
block|}
name|ba
operator|=
operator|new
name|byte
index|[
name|i
index|]
expr_stmt|;
name|ba
index|[
literal|0
index|]
operator|=
name|ob
expr_stmt|;
name|int
name|j
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|i
condition|)
block|{
name|ba
index|[
name|j
index|]
operator|=
name|this
operator|.
name|getByte
argument_list|()
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
name|i
operator|=
name|ba
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|1
condition|)
return|return
operator|(
name|char
operator|)
name|ba
index|[
literal|0
index|]
return|;
else|else
block|{
name|int
name|n
decl_stmt|;
name|n
operator|=
name|ba
index|[
literal|0
index|]
operator|&
literal|15
expr_stmt|;
comment|// 00001111b, gets last 4 bits
name|j
operator|=
literal|1
expr_stmt|;
while|while
condition|(
name|j
operator|<
name|i
condition|)
name|n
operator|=
operator|(
name|n
operator|<<
literal|6
operator|)
operator|+
operator|(
name|ba
index|[
name|j
operator|++
index|]
operator|&
literal|63
operator|)
expr_stmt|;
comment|// 00111111b,gets last 6 bits
return|return
operator|(
name|char
operator|)
name|n
return|;
block|}
block|}
specifier|private
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|int
name|getSwath
parameter_list|()
block|{
return|return
name|swath
return|;
block|}
specifier|public
name|void
name|setSwath
parameter_list|(
name|int
name|swath
parameter_list|)
block|{
name|this
operator|.
name|swath
operator|=
name|swath
expr_stmt|;
block|}
specifier|public
name|int
name|getTotal
parameter_list|()
block|{
return|return
name|total
return|;
block|}
specifier|public
name|void
name|setTotal
parameter_list|(
name|int
name|total
parameter_list|)
block|{
name|this
operator|.
name|total
operator|=
name|total
expr_stmt|;
block|}
specifier|private
name|int
name|getBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
specifier|private
name|void
name|setBuffer
parameter_list|(
name|int
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
comment|/** 	 * @param args 	 */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|int
name|result
init|=
literal|8
operator|&
literal|255
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"result "
operator|+
name|result
argument_list|)
expr_stmt|;
name|byte
index|[]
name|array
init|=
block|{
literal|4
block|,
literal|78
block|,
operator|-
literal|67
block|,
literal|90
block|,
literal|1
block|,
operator|-
literal|33
block|}
decl_stmt|;
name|ChmSection
name|chmSection
init|=
operator|new
name|ChmSection
argument_list|(
name|array
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"before "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|array
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"after "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|chmSection
operator|.
name|reverseByteOrder
argument_list|(
name|array
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

