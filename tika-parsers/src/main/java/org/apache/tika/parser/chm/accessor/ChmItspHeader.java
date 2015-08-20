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
name|ChmCommons
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

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Directory header The directory starts with a header; its format is as  * follows: 0000: char[4] 'ITSP' 0004: DWORD Version number 1 0008: DWORD Length  * of the directory header 000C: DWORD $0a (unknown) 0010: DWORD $1000 Directory  * chunk size 0014: DWORD "Density" of quickref section, usually 2 0018: DWORD  * Depth of the index tree - 1 there is no index, 2 if there is one level of  * PMGI chunks 001C: DWORD Chunk number of root index chunk, -1 if there is none  * (though at least one file has 0 despite there being no index chunk, probably  * a bug) 0020: DWORD Chunk number of first PMGL (listing) chunk 0024: DWORD  * Chunk number of last PMGL (listing) chunk 0028: DWORD -1 (unknown) 002C:  * DWORD Number of directory chunks (total) 0030: DWORD Windows language ID  * 0034: GUID {5D02926A-212E-11D0-9DF9-00A0C922E6EC} 0044: DWORD $54 (This is  * the length again) 0048: DWORD -1 (unknown) 004C: DWORD -1 (unknown) 0050:  * DWORD -1 (unknown)  *   * {@link http  * ://translated.by/you/microsoft-s-html-help-chm-format-incomplete/original  * /?show-translation-form=1}  *   */
end_comment

begin_class
specifier|public
class|class
name|ChmItspHeader
implements|implements
name|ChmAccessor
argument_list|<
name|ChmItspHeader
argument_list|>
block|{
comment|// TODO: refactor all unmarshals
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1962394421998181341L
decl_stmt|;
specifier|private
name|byte
index|[]
name|signature
decl_stmt|;
specifier|private
name|int
name|version
decl_stmt|;
comment|/* 4 */
specifier|private
name|int
name|header_len
decl_stmt|;
comment|/* 8 */
specifier|private
name|int
name|unknown_000c
decl_stmt|;
comment|/* c */
specifier|private
name|long
name|block_len
decl_stmt|;
comment|/* 10 */
specifier|private
name|int
name|blockidx_intvl
decl_stmt|;
comment|/* 14 */
specifier|private
name|int
name|index_depth
decl_stmt|;
comment|/* 18 */
specifier|private
name|int
name|index_root
decl_stmt|;
comment|/* 1c */
specifier|private
name|int
name|index_head
decl_stmt|;
comment|/* 20 */
specifier|private
name|int
name|unknown_0024
decl_stmt|;
comment|/* 24 */
specifier|private
name|long
name|num_blocks
decl_stmt|;
comment|/* 28 */
specifier|private
name|int
name|unknown_002c
decl_stmt|;
comment|/* 2c */
specifier|private
name|long
name|lang_id
decl_stmt|;
comment|/* 30 */
specifier|private
name|byte
index|[]
name|system_uuid
init|=
operator|new
name|byte
index|[
name|ChmConstants
operator|.
name|BYTE_ARRAY_LENGHT
index|]
decl_stmt|;
comment|/* 34 */
specifier|private
name|byte
index|[]
name|unknown_0044
init|=
operator|new
name|byte
index|[
name|ChmConstants
operator|.
name|BYTE_ARRAY_LENGHT
index|]
decl_stmt|;
comment|/* 44 */
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
specifier|public
name|ChmItspHeader
parameter_list|()
block|{
name|signature
operator|=
name|ChmConstants
operator|.
name|ITSP
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
expr_stmt|;
comment|/*                                                         * 0                                                         * (ITSP                                                         * )                                                         */
block|}
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
literal|"[ signature:="
operator|+
operator|new
name|String
argument_list|(
name|getSignature
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
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
literal|"version:=\t"
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
literal|"header_len:=\t"
operator|+
name|getHeader_len
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
literal|"unknown_00c:=\t"
operator|+
name|getUnknown_000c
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
literal|"block_len:=\t"
operator|+
name|getBlock_len
argument_list|()
operator|+
literal|" [directory chunk size]"
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
literal|"blockidx_intvl:="
operator|+
name|getBlockidx_intvl
argument_list|()
operator|+
literal|", density of quickref section, usually 2"
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
literal|"index_depth:=\t"
operator|+
name|getIndex_depth
argument_list|()
operator|+
literal|", depth of the index tree - 1 there is no index, 2 if there is one level of PMGI chunk"
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
literal|"index_root:=\t"
operator|+
name|getIndex_root
argument_list|()
operator|+
literal|", chunk number of root index chunk, -1 if there is none"
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
literal|"index_head:=\t"
operator|+
name|getIndex_head
argument_list|()
operator|+
literal|", chunk number of first PMGL (listing) chunk"
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
literal|"unknown_0024:=\t"
operator|+
name|getUnknown_0024
argument_list|()
operator|+
literal|", chunk number of last PMGL (listing) chunk"
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
literal|"num_blocks:=\t"
operator|+
name|getNum_blocks
argument_list|()
operator|+
literal|", -1 (unknown)"
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
literal|"unknown_002c:=\t"
operator|+
name|getUnknown_002c
argument_list|()
operator|+
literal|", number of directory chunks (total)"
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
literal|"lang_id:=\t"
operator|+
name|getLang_id
argument_list|()
operator|+
literal|" - "
operator|+
name|ChmCommons
operator|.
name|getLanguage
argument_list|(
name|getLang_id
argument_list|()
argument_list|)
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
literal|"system_uuid:="
operator|+
name|getSystem_uuid
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
literal|"unknown_0044:="
operator|+
name|getUnknown_0044
argument_list|()
operator|+
literal|" ]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Copies 4 bits from data[]      *       * @param data      * @param chmItspHeader      * @param count      * @throws TikaException       */
specifier|private
name|void
name|unmarshalCharArray
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|ChmItspHeader
name|chmItspHeader
parameter_list|,
name|int
name|count
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
name|ChmAssert
operator|.
name|assertChmAccessorNotNull
argument_list|(
name|chmItspHeader
argument_list|)
expr_stmt|;
name|this
operator|.
name|setDataRemained
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|chmItspHeader
operator|.
name|signature
argument_list|,
literal|0
argument_list|,
name|count
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
name|count
argument_list|)
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
name|count
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|unmarshalInt32
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|dataLenght
parameter_list|,
name|int
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
if|if
condition|(
literal|4
operator|>
name|this
operator|.
name|getDataRemained
argument_list|()
condition|)
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"4> dataLenght"
argument_list|)
throw|;
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
name|this
operator|.
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
return|return
name|dest
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
name|int
name|dataLenght
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
if|if
condition|(
literal|4
operator|>
name|dataLenght
condition|)
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"4> dataLenght"
argument_list|)
throw|;
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
name|byte
index|[]
name|unmarshalUuid
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|dataLenght
parameter_list|,
name|byte
index|[]
name|dest
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|this
operator|.
name|getCurrentPlace
argument_list|()
argument_list|,
name|dest
argument_list|,
literal|0
argument_list|,
name|count
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
name|count
argument_list|)
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
name|count
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
comment|/**      * Returns how many bytes remained      *       * @return int      */
specifier|private
name|int
name|getDataRemained
parameter_list|()
block|{
return|return
name|dataRemained
return|;
block|}
comment|/**      * Sets how many bytes remained      *       * @param dataRemained      */
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
comment|/**      * Returns a place holder      *       * @return current place      */
specifier|private
name|int
name|getCurrentPlace
parameter_list|()
block|{
return|return
name|currentPlace
return|;
block|}
comment|/**      * Sets current place      *       * @param currentPlace      */
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
comment|/**      * Returns a signature of the header      *       * @return itsp signature      */
specifier|public
name|byte
index|[]
name|getSignature
parameter_list|()
block|{
return|return
name|signature
return|;
block|}
comment|/**      * Sets itsp signature      *       * @param signature      */
specifier|protected
name|void
name|setSignature
parameter_list|(
name|byte
index|[]
name|signature
parameter_list|)
block|{
name|this
operator|.
name|signature
operator|=
name|signature
expr_stmt|;
block|}
comment|/**      * Returns version of itsp header      *       * @return version      */
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**      * Sets a version of itsp header      *       * @param version      */
specifier|protected
name|void
name|setVersion
parameter_list|(
name|int
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
comment|/**      * Returns header length      *       * @return header length      */
specifier|public
name|int
name|getHeader_len
parameter_list|()
block|{
return|return
name|header_len
return|;
block|}
comment|/**      * Sets itsp header length      *       * @param header_len      */
specifier|protected
name|void
name|setHeader_len
parameter_list|(
name|int
name|header_len
parameter_list|)
block|{
name|this
operator|.
name|header_len
operator|=
name|header_len
expr_stmt|;
block|}
comment|/**      * Returns 000c unknown bytes      */
specifier|public
name|int
name|getUnknown_000c
parameter_list|()
block|{
return|return
name|unknown_000c
return|;
block|}
comment|/**      * Sets 000c unknown bytes Unknown means here that those guys who cracked      * the chm format do not know what's it purposes for      *       * @param unknown_000c      */
specifier|protected
name|void
name|setUnknown_000c
parameter_list|(
name|int
name|unknown_000c
parameter_list|)
block|{
name|this
operator|.
name|unknown_000c
operator|=
name|unknown_000c
expr_stmt|;
block|}
comment|/**      * Returns block's length      *       * @return block_length      */
specifier|public
name|long
name|getBlock_len
parameter_list|()
block|{
return|return
name|block_len
return|;
block|}
comment|/**      * Sets block length      *       * @param block_len      */
specifier|protected
name|void
name|setBlock_len
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
comment|/**      * Returns block index interval      *       * @return blockidx_intvl      */
specifier|public
name|int
name|getBlockidx_intvl
parameter_list|()
block|{
return|return
name|blockidx_intvl
return|;
block|}
comment|/**      * Sets block index interval      *       * @param blockidx_intvl      */
specifier|protected
name|void
name|setBlockidx_intvl
parameter_list|(
name|int
name|blockidx_intvl
parameter_list|)
block|{
name|this
operator|.
name|blockidx_intvl
operator|=
name|blockidx_intvl
expr_stmt|;
block|}
comment|/**      * Returns an index depth      *       * @return index_depth      */
specifier|public
name|int
name|getIndex_depth
parameter_list|()
block|{
return|return
name|index_depth
return|;
block|}
comment|/**      * Sets an index depth      *       * @param index_depth      */
specifier|protected
name|void
name|setIndex_depth
parameter_list|(
name|int
name|index_depth
parameter_list|)
block|{
name|this
operator|.
name|index_depth
operator|=
name|index_depth
expr_stmt|;
block|}
comment|/**      * Returns index root      *       * @return index_root      */
specifier|public
name|int
name|getIndex_root
parameter_list|()
block|{
return|return
name|index_root
return|;
block|}
comment|/**      * Sets an index root      *       * @param index_root      */
specifier|protected
name|void
name|setIndex_root
parameter_list|(
name|int
name|index_root
parameter_list|)
block|{
name|this
operator|.
name|index_root
operator|=
name|index_root
expr_stmt|;
block|}
comment|/**      * Returns an index head      *       * @return index_head      */
specifier|public
name|int
name|getIndex_head
parameter_list|()
block|{
return|return
name|index_head
return|;
block|}
comment|/**      * Sets an index head      *       * @param index_head      */
specifier|protected
name|void
name|setIndex_head
parameter_list|(
name|int
name|index_head
parameter_list|)
block|{
name|this
operator|.
name|index_head
operator|=
name|index_head
expr_stmt|;
block|}
comment|/**      * Returns 0024 unknown bytes      *       * @return unknown_0024      */
specifier|public
name|int
name|getUnknown_0024
parameter_list|()
block|{
return|return
name|unknown_0024
return|;
block|}
comment|/**      * Sets 0024 unknown bytes      *       * @param unknown_0024      */
specifier|protected
name|void
name|setUnknown_0024
parameter_list|(
name|int
name|unknown_0024
parameter_list|)
block|{
name|this
operator|.
name|unknown_0024
operator|=
name|unknown_0024
expr_stmt|;
block|}
comment|/**      * Returns number of blocks      *       * @return num_blocks      */
specifier|public
name|long
name|getNum_blocks
parameter_list|()
block|{
return|return
name|num_blocks
return|;
block|}
comment|/**      * Sets number of blocks containing in the chm file      *       * @param num_blocks      */
specifier|protected
name|void
name|setNum_blocks
parameter_list|(
name|long
name|num_blocks
parameter_list|)
block|{
name|this
operator|.
name|num_blocks
operator|=
name|num_blocks
expr_stmt|;
block|}
comment|/**      * Returns 002c unknown bytes      *       * @return unknown_002c      */
specifier|public
name|int
name|getUnknown_002c
parameter_list|()
block|{
return|return
name|unknown_002c
return|;
block|}
comment|/**      * Sets 002c unknown bytes      *       * @param unknown_002c      */
specifier|protected
name|void
name|setUnknown_002c
parameter_list|(
name|int
name|unknown_002c
parameter_list|)
block|{
name|this
operator|.
name|unknown_002c
operator|=
name|unknown_002c
expr_stmt|;
block|}
comment|/**      * Returns language id      *       * @return lang_id      */
specifier|public
name|long
name|getLang_id
parameter_list|()
block|{
return|return
name|lang_id
return|;
block|}
comment|/**      * Sets language id      *       * @param lang_id      */
specifier|protected
name|void
name|setLang_id
parameter_list|(
name|long
name|lang_id
parameter_list|)
block|{
name|this
operator|.
name|lang_id
operator|=
name|lang_id
expr_stmt|;
block|}
comment|/**      * Returns system uuid      *       * @return system_uuid      */
specifier|public
name|byte
index|[]
name|getSystem_uuid
parameter_list|()
block|{
return|return
name|system_uuid
return|;
block|}
comment|/**      * Sets system uuid      *       * @param system_uuid      */
specifier|protected
name|void
name|setSystem_uuid
parameter_list|(
name|byte
index|[]
name|system_uuid
parameter_list|)
block|{
name|this
operator|.
name|system_uuid
operator|=
name|system_uuid
expr_stmt|;
block|}
comment|/**      * Returns 0044 unknown bytes      *       * @return unknown_0044      */
specifier|public
name|byte
index|[]
name|getUnknown_0044
parameter_list|()
block|{
return|return
name|unknown_0044
return|;
block|}
comment|/**      * Sets 0044 unknown bytes      *       * @param unknown_0044      */
specifier|protected
name|void
name|setUnknown_0044
parameter_list|(
name|byte
index|[]
name|unknown_0044
parameter_list|)
block|{
name|this
operator|.
name|unknown_0044
operator|=
name|unknown_0044
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
name|ChmItspHeader
name|chmItspHeader
parameter_list|)
throws|throws
name|TikaException
block|{
comment|/* we only know how to deal with the 0x58 and 0x60 byte structures */
if|if
condition|(
name|data
operator|.
name|length
operator|!=
name|ChmConstants
operator|.
name|CHM_ITSP_V1_LEN
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"we only know how to deal with the 0x58 and 0x60 byte structures"
argument_list|)
throw|;
comment|/* unmarshal common fields */
name|chmItspHeader
operator|.
name|unmarshalCharArray
argument_list|(
name|data
argument_list|,
name|chmItspHeader
argument_list|,
name|ChmConstants
operator|.
name|CHM_SIGNATURE_LEN
argument_list|)
expr_stmt|;
comment|// ChmCommons.unmarshalCharArray(data, chmItspHeader,
comment|// ChmConstants.CHM_SIGNATURE_LEN);
name|chmItspHeader
operator|.
name|setVersion
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setHeader_len
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getHeader_len
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setUnknown_000c
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getUnknown_000c
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setBlock_len
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getBlock_len
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setBlockidx_intvl
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getBlockidx_intvl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setIndex_depth
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getIndex_depth
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setIndex_root
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getIndex_root
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setIndex_head
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getIndex_head
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setUnknown_0024
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getUnknown_0024
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setNum_blocks
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getNum_blocks
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setUnknown_002c
argument_list|(
operator|(
name|chmItspHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getUnknown_002c
argument_list|()
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setLang_id
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getLang_id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setSystem_uuid
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalUuid
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getSystem_uuid
argument_list|()
argument_list|,
name|ChmConstants
operator|.
name|BYTE_ARRAY_LENGHT
argument_list|)
argument_list|)
expr_stmt|;
name|chmItspHeader
operator|.
name|setUnknown_0044
argument_list|(
name|chmItspHeader
operator|.
name|unmarshalUuid
argument_list|(
name|data
argument_list|,
name|chmItspHeader
operator|.
name|getDataRemained
argument_list|()
argument_list|,
name|chmItspHeader
operator|.
name|getUnknown_0044
argument_list|()
argument_list|,
name|ChmConstants
operator|.
name|BYTE_ARRAY_LENGHT
argument_list|)
argument_list|)
expr_stmt|;
comment|/* Checks validity of the itsp header */
if|if
condition|(
operator|!
operator|new
name|String
argument_list|(
name|chmItspHeader
operator|.
name|getSignature
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
operator|.
name|equals
argument_list|(
name|ChmConstants
operator|.
name|ITSP
argument_list|)
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"seems not valid signature"
argument_list|)
throw|;
if|if
condition|(
name|chmItspHeader
operator|.
name|getVersion
argument_list|()
operator|!=
name|ChmConstants
operator|.
name|CHM_VER_1
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"!=ChmConstants.CHM_VER_1"
argument_list|)
throw|;
if|if
condition|(
name|chmItspHeader
operator|.
name|getHeader_len
argument_list|()
operator|!=
name|ChmConstants
operator|.
name|CHM_ITSP_V1_LEN
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"!= ChmConstants.CHM_ITSP_V1_LEN"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

