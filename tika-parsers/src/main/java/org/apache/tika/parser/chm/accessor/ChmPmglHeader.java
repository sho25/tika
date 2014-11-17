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
name|io
operator|.
name|UnsupportedEncodingException
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
comment|/**  * Description There are two types of directory chunks -- index chunks, and  * listing chunks. The index chunk will be omitted if there is only one listing  * chunk. A listing chunk has the following format: 0000: char[4] 'PMGL' 0004:  * DWORD Length of free space and/or quickref area at end of directory chunk  * 0008: DWORD Always 0 000C: DWORD Chunk number of previous listing chunk when  * reading directory in sequence (-1 if this is the first listing chunk) 0010:  * DWORD Chunk number of next listing chunk when reading directory in sequence  * (-1 if this is the last listing chunk) 0014: Directory listing entries (to  * quickref area) Sorted by filename; the sort is case-insensitive The quickref  * area is written backwards from the end of the chunk. One quickref entry  * exists for every n entries in the file, where n is calculated as 1 + (1<<  * quickref density). So for density = 2, n = 5 Chunklen-0002: WORD Number of  * entries in the chunk Chunklen-0004: WORD Offset of entry n from entry 0  * Chunklen-0008: WORD Offset of entry 2n from entry 0 Chunklen-000C: WORD  * Offset of entry 3n from entry 0 ... The format of a directory listing entry  * is as follows BYTE: length of name BYTEs: name (UTF-8 encoded) ENCINT:  * content section ENCINT: offset ENCINT: length The offset is from the  * beginning of the content section the file is in, after the section has been  * decompressed (if appropriate). The length also refers to length of the file  * in the section after decompression. There are two kinds of file represented  * in the directory: user data and format related files. The files which are  * format-related have names which begin with '::', the user data files have  * names which begin with "/".  *   * {@link http  * ://translated.by/you/microsoft-s-html-help-chm-format-incomplete/original  * /?show-translation-form=1 }  *   * @author olegt  *   */
end_comment

begin_class
specifier|public
class|class
name|ChmPmglHeader
implements|implements
name|ChmAccessor
argument_list|<
name|ChmPmglHeader
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6139486487475923593L
decl_stmt|;
specifier|private
name|byte
index|[]
name|signature
decl_stmt|;
specifier|private
name|long
name|free_space
decl_stmt|;
comment|/* 4 */
specifier|private
name|long
name|unknown_0008
decl_stmt|;
comment|/* 8 */
specifier|private
name|int
name|block_prev
decl_stmt|;
comment|/* c */
specifier|private
name|int
name|block_next
decl_stmt|;
comment|/* 10 */
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
name|ChmPmglHeader
parameter_list|()
block|{
try|try
block|{
name|signature
operator|=
name|ChmConstants
operator|.
name|PMGL
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|/*                                                                           * 0                                                                           * (PMGL                                                                           * )                                                                           */
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"UTF-8 not supported."
argument_list|)
throw|;
block|}
block|}
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
specifier|public
name|long
name|getFreeSpace
parameter_list|()
block|{
return|return
name|free_space
return|;
block|}
specifier|public
name|void
name|setFreeSpace
parameter_list|(
name|long
name|free_space
parameter_list|)
throws|throws
name|TikaException
block|{
if|if
condition|(
name|free_space
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Bad PMGLheader.FreeSpace="
operator|+
name|free_space
argument_list|)
throw|;
block|}
name|this
operator|.
name|free_space
operator|=
name|free_space
expr_stmt|;
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
try|try
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"signatute:="
operator|+
operator|new
name|String
argument_list|(
name|getSignature
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
operator|+
literal|", "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"UTF-8 not supported."
argument_list|)
throw|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"free space:="
operator|+
name|getFreeSpace
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"unknown0008:="
operator|+
name|getUnknown0008
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"prev block:="
operator|+
name|getBlockPrev
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"next block:="
operator|+
name|getBlockNext
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
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|unmarshalCharArray
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|ChmPmglHeader
name|chmPmglHeader
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
name|chmPmglHeader
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
name|int
name|dest
decl_stmt|;
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
parameter_list|)
throws|throws
name|ChmParsingException
block|{
name|ChmAssert
operator|.
name|assertByteArrayNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|long
name|dest
decl_stmt|;
if|if
condition|(
literal|4
operator|>
name|getDataRemained
argument_list|()
condition|)
throw|throw
operator|new
name|ChmParsingException
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
comment|// @Override
specifier|public
name|void
name|parse
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|ChmPmglHeader
name|chmPmglHeader
parameter_list|)
throws|throws
name|TikaException
block|{
if|if
condition|(
name|data
operator|.
name|length
operator|<
name|ChmConstants
operator|.
name|CHM_PMGL_LEN
condition|)
throw|throw
operator|new
name|TikaException
argument_list|(
name|ChmPmglHeader
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" we only know how to deal with a 0x14 byte structures"
argument_list|)
throw|;
comment|/* unmarshal fields */
name|chmPmglHeader
operator|.
name|unmarshalCharArray
argument_list|(
name|data
argument_list|,
name|chmPmglHeader
argument_list|,
name|ChmConstants
operator|.
name|CHM_SIGNATURE_LEN
argument_list|)
expr_stmt|;
name|chmPmglHeader
operator|.
name|setFreeSpace
argument_list|(
name|chmPmglHeader
operator|.
name|unmarshalUInt32
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|chmPmglHeader
operator|.
name|setUnknown0008
argument_list|(
name|chmPmglHeader
operator|.
name|unmarshalUInt32
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|chmPmglHeader
operator|.
name|setBlockPrev
argument_list|(
name|chmPmglHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|chmPmglHeader
operator|.
name|setBlockNext
argument_list|(
name|chmPmglHeader
operator|.
name|unmarshalInt32
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
comment|/* check structure */
try|try
block|{
if|if
condition|(
operator|!
operator|new
name|String
argument_list|(
name|chmPmglHeader
operator|.
name|getSignature
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|equals
argument_list|(
name|ChmConstants
operator|.
name|PMGL
argument_list|)
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
name|ChmPmglHeader
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" pmgl != pmgl.signature"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"UTF-8 not supported."
argument_list|)
throw|;
block|}
block|}
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
specifier|public
name|long
name|getUnknown0008
parameter_list|()
block|{
return|return
name|unknown_0008
return|;
block|}
specifier|protected
name|void
name|setUnknown0008
parameter_list|(
name|long
name|unknown_0008
parameter_list|)
block|{
name|this
operator|.
name|unknown_0008
operator|=
name|unknown_0008
expr_stmt|;
block|}
specifier|public
name|int
name|getBlockPrev
parameter_list|()
block|{
return|return
name|block_prev
return|;
block|}
specifier|protected
name|void
name|setBlockPrev
parameter_list|(
name|int
name|block_prev
parameter_list|)
block|{
name|this
operator|.
name|block_prev
operator|=
name|block_prev
expr_stmt|;
block|}
specifier|public
name|int
name|getBlockNext
parameter_list|()
block|{
return|return
name|block_next
return|;
block|}
specifier|protected
name|void
name|setBlockNext
parameter_list|(
name|int
name|block_next
parameter_list|)
block|{
name|this
operator|.
name|block_next
operator|=
name|block_next
expr_stmt|;
block|}
block|}
end_class

end_unit

