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
name|accessor
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
name|exception
operator|.
name|TikaException
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
name|assertion
operator|.
name|ChmAssert
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
name|ChmConstants
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
name|exception
operator|.
name|ChmParsingException
import|;
end_import

begin_comment
comment|/**  * LZXC reset table For ensuring a decompression. Reads the block named  * "::DataSpace/Storage/<SectionName>/Transform/{7FC28940-9D31-11D0-9B27-00A0C91E9C7C}/InstanceData/ResetTable"  * .  */
end_comment

begin_class
specifier|public
class|class
name|ChmLzxcResetTable
implements|implements
name|ChmAccessor
argument_list|<
name|ChmLzxcResetTable
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|8209574429411707460L
decl_stmt|;
comment|/* class members */
specifier|private
name|long
name|version
decl_stmt|;
comment|// 0000: DWORD 2 unknown (possibly a version number)
specifier|private
name|long
name|block_count
decl_stmt|;
comment|// 0004: DWORD Number of entries in reset table
specifier|private
name|long
name|unknown
decl_stmt|;
comment|// 0008: DWORD 8 unknown
specifier|private
name|long
name|table_offset
decl_stmt|;
comment|// 000C: DWORD $28 Length of table header (area
comment|// before table entries)
specifier|private
name|long
name|uncompressed_len
decl_stmt|;
comment|// 0010: QWORD Uncompressed Length
specifier|private
name|long
name|compressed_len
decl_stmt|;
comment|// 0018: QWORD Compressed Length
specifier|private
name|long
name|block_len
decl_stmt|;
comment|// 0020: QWORD 0x8000 block size for locations below
specifier|private
name|long
index|[]
name|block_address
decl_stmt|;
comment|/* local usage */
specifier|private
name|int
name|dataRemained
decl_stmt|;
specifier|private
name|int
name|currentPlace
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|getDataRemained
parameter_list|()
block|{
return|return
name|dataRemained
return|;
block|}
specifier|private
name|void
name|setDataRemained
parameter_list|(
name|int
name|dataRemained
parameter_list|)
block|{
name|this
operator|.
name|dataRemained
operator|=
name|dataRemained
expr_stmt|;
block|}
comment|/**      * Returns block addresses      *       * @return block addresses      */
specifier|public
name|long
index|[]
name|getBlockAddress
parameter_list|()
block|{
return|return
name|block_address
return|;
block|}
comment|/**      * Sets block addresses      *       * @param block_address      */
specifier|public
name|void
name|setBlockAddress
parameter_list|(
name|long
index|[]
name|block_address
parameter_list|)
block|{
name|this
operator|.
name|block_address
operator|=
name|block_address
expr_stmt|;
block|}
specifier|private
name|int
name|getCurrentPlace
parameter_list|()
block|{
return|return
name|currentPlace
return|;
block|}
specifier|private
name|void
name|setCurrentPlace
parameter_list|(
name|int
name|currentPlace
parameter_list|)
block|{
name|this
operator|.
name|currentPlace
operator|=
name|currentPlace
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"version:="
operator|+
name|getVersion
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"block_count:="
operator|+
name|getBlockCount
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"unknown:="
operator|+
name|getUnknown
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"table_offset:="
operator|+
name|getTableOffset
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"uncompressed_len:="
operator|+
name|getUncompressedLen
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"compressed_len:="
operator|+
name|getCompressedLen
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"block_len:="
operator|+
name|getBlockLen
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"block_addresses:="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|getBlockAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Enumerates chm block addresses      *       * @param data      *       * @return byte[] of addresses      * @throws TikaException       */
specifier|private
name|long
index|[]
name|enumerateBlockAddresses
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|TikaException
block|{
name|ChmAssert
operator|.
name|assertByteArrayNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|/* we have limit of number of blocks to be extracted */
if|if
condition|(
name|getBlockCount
argument_list|()
operator|>
literal|5000
condition|)
name|setBlockCount
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
if|if
condition|(
name|getBlockCount
argument_list|()
operator|<
literal|0
operator|&&
operator|(
name|getDataRemained
argument_list|()
operator|/
literal|8
operator|)
operator|>
literal|0
condition|)
name|setBlockCount
argument_list|(
name|getDataRemained
argument_list|()
operator|/
literal|8
argument_list|)
expr_stmt|;
name|long
index|[]
name|addresses
init|=
operator|new
name|long
index|[
operator|(
name|int
operator|)
name|getBlockCount
argument_list|()
index|]
decl_stmt|;
name|int
name|rem
init|=
name|getDataRemained
argument_list|()
operator|/
literal|8
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
name|rem
condition|;
name|i
operator|++
control|)
block|{
name|long
name|num
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|addresses
index|[
name|i
index|]
operator|=
name|unmarshalUint64
argument_list|(
name|data
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|addresses
return|;
block|}
comment|/**      * Validates parameters such as byte[] and chm lzxc reset table      *       * @param data      * @param chmLzxcResetTable      *       * @return boolean      * @throws TikaException       */
specifier|private
name|boolean
name|validateParamaters
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|ChmLzxcResetTable
name|chmLzxcResetTable
parameter_list|)
throws|throws
name|TikaException
block|{
name|int
name|goodParameter
init|=
literal|0
decl_stmt|;
name|ChmAssert
operator|.
name|assertByteArrayNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
operator|++
name|goodParameter
expr_stmt|;
name|ChmAssert
operator|.
name|assertChmAccessorNotNull
argument_list|(
name|chmLzxcResetTable
argument_list|)
expr_stmt|;
operator|++
name|goodParameter
expr_stmt|;
return|return
operator|(
name|goodParameter
operator|==
literal|2
operator|)
return|;
block|}
specifier|private
name|long
name|unmarshalUInt32
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|long
name|dest
parameter_list|)
throws|throws
name|TikaException
block|{
name|ChmAssert
operator|.
name|assertByteArrayNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|(
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
index|]
operator|&
literal|0xff
operator|)
operator||
operator|(
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator||
operator|(
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator||
operator|(
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
operator|+
literal|3
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
expr_stmt|;
name|setDataRemained
argument_list|(
name|this
operator|.
name|getDataRemained
argument_list|()
operator|-
literal|4
argument_list|)
expr_stmt|;
name|this
operator|.
name|setCurrentPlace
argument_list|(
name|this
operator|.
name|getCurrentPlace
argument_list|()
operator|+
literal|4
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
specifier|private
name|long
name|unmarshalUint64
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|long
name|dest
parameter_list|)
throws|throws
name|TikaException
block|{
name|ChmAssert
operator|.
name|assertByteArrayNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|byte
index|[]
name|temp
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|int
name|i
decl_stmt|,
name|j
decl_stmt|;
comment|// counters
for|for
control|(
name|i
operator|=
literal|8
operator|,
name|j
operator|=
literal|7
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|data
operator|.
name|length
operator|>
name|this
operator|.
name|getCurrentPlace
argument_list|()
condition|)
block|{
name|temp
index|[
name|j
operator|--
index|]
operator|=
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|setCurrentPlace
argument_list|(
name|this
operator|.
name|getCurrentPlace
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"data is too small to calculate address block"
argument_list|)
throw|;
block|}
name|dest
operator|=
operator|new
name|BigInteger
argument_list|(
name|temp
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|setDataRemained
argument_list|(
name|this
operator|.
name|getDataRemained
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
comment|/**      * Returns the version      *       * @return - long      */
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**      * Sets the version      *       * @param version      *            - long      */
specifier|public
name|void
name|setVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
comment|/**      * Gets a block count      *       * @return - int      */
specifier|public
name|long
name|getBlockCount
parameter_list|()
block|{
return|return
name|block_count
return|;
block|}
comment|/**      * Sets a block count      *       * @param block_count      *            - long      */
specifier|public
name|void
name|setBlockCount
parameter_list|(
name|long
name|block_count
parameter_list|)
block|{
name|this
operator|.
name|block_count
operator|=
name|block_count
expr_stmt|;
block|}
comment|/**      * Gets unknown      *       * @return - long      */
specifier|public
name|long
name|getUnknown
parameter_list|()
block|{
return|return
name|unknown
return|;
block|}
comment|/**      * Sets an unknown      *       * @param unknown      *            - long      */
specifier|public
name|void
name|setUnknown
parameter_list|(
name|long
name|unknown
parameter_list|)
block|{
name|this
operator|.
name|unknown
operator|=
name|unknown
expr_stmt|;
block|}
comment|/**      * Gets a table offset      *       * @return - long      */
specifier|public
name|long
name|getTableOffset
parameter_list|()
block|{
return|return
name|table_offset
return|;
block|}
comment|/**      * Sets a table offset      *       * @param table_offset      *            - long      */
specifier|public
name|void
name|setTableOffset
parameter_list|(
name|long
name|table_offset
parameter_list|)
block|{
name|this
operator|.
name|table_offset
operator|=
name|table_offset
expr_stmt|;
block|}
comment|/**      * Gets uncompressed length      *       * @return - {@link BigInteger }      */
specifier|public
name|long
name|getUncompressedLen
parameter_list|()
block|{
return|return
name|uncompressed_len
return|;
block|}
comment|/**      * Sets uncompressed length      *       * @param uncompressed_len      *            - {@link BigInteger}      */
specifier|public
name|void
name|setUncompressedLen
parameter_list|(
name|long
name|uncompressed_len
parameter_list|)
block|{
name|this
operator|.
name|uncompressed_len
operator|=
name|uncompressed_len
expr_stmt|;
block|}
comment|/**      * Gets compressed length      *       * @return - {@link BigInteger}      */
specifier|public
name|long
name|getCompressedLen
parameter_list|()
block|{
return|return
name|compressed_len
return|;
block|}
comment|/**      * Sets compressed length      *       * @param compressed_len      *            - {@link BigInteger}      */
specifier|public
name|void
name|setCompressedLen
parameter_list|(
name|long
name|compressed_len
parameter_list|)
block|{
name|this
operator|.
name|compressed_len
operator|=
name|compressed_len
expr_stmt|;
block|}
comment|/**      * Gets a block length      *       * @return - {@link BigInteger}      */
specifier|public
name|long
name|getBlockLen
parameter_list|()
block|{
return|return
name|block_len
return|;
block|}
comment|/**      * Sets a block length      *       * @param block_len      *            - {@link BigInteger}      */
specifier|public
name|void
name|setBlockLlen
parameter_list|(
name|long
name|block_len
parameter_list|)
block|{
name|this
operator|.
name|block_len
operator|=
name|block_len
expr_stmt|;
block|}
comment|// @Override
specifier|public
name|void
name|parse
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|ChmLzxcResetTable
name|chmLzxcResetTable
parameter_list|)
throws|throws
name|TikaException
block|{
name|setDataRemained
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|validateParamaters
argument_list|(
name|data
argument_list|,
name|chmLzxcResetTable
argument_list|)
condition|)
block|{
comment|/* unmarshal fields */
name|chmLzxcResetTable
operator|.
name|setVersion
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcResetTable
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcResetTable
operator|.
name|setBlockCount
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcResetTable
operator|.
name|getBlockCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcResetTable
operator|.
name|setUnknown
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcResetTable
operator|.
name|getUnknown
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcResetTable
operator|.
name|setTableOffset
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcResetTable
operator|.
name|getTableOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcResetTable
operator|.
name|setUncompressedLen
argument_list|(
name|unmarshalUint64
argument_list|(
name|data
argument_list|,
name|chmLzxcResetTable
operator|.
name|getUncompressedLen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcResetTable
operator|.
name|setCompressedLen
argument_list|(
name|unmarshalUint64
argument_list|(
name|data
argument_list|,
name|chmLzxcResetTable
operator|.
name|getCompressedLen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcResetTable
operator|.
name|setBlockLlen
argument_list|(
name|unmarshalUint64
argument_list|(
name|data
argument_list|,
name|chmLzxcResetTable
operator|.
name|getBlockLen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcResetTable
operator|.
name|setBlockAddress
argument_list|(
name|enumerateBlockAddresses
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* checks chmLzxcResetTable */
if|if
condition|(
name|chmLzxcResetTable
operator|.
name|getVersion
argument_list|()
operator|!=
name|ChmConstants
operator|.
name|CHM_VER_2
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"does not seem currect version of chmLzxcResetTable"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

