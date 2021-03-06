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
comment|/**  *   * ::DataSpace/Storage/<SectionName>/ControlData This file contains $20 bytes of  * information on the compression. The information is partially known: 0000:  * DWORD 6 (unknown) 0004: ASCII 'LZXC' Compression type identifier 0008: DWORD  * 2 (Possibly numeric code for LZX) 000C: DWORD The Huffman reset interval in  * $8000-byte blocks 0010: DWORD The window size in $8000-byte blocks 0014:  * DWORD unknown (sometimes 2, sometimes 1, sometimes 0) 0018: DWORD 0 (unknown)  * 001C: DWORD 0 (unknown)  */
end_comment

begin_class
specifier|public
class|class
name|ChmLzxcControlData
implements|implements
name|ChmAccessor
argument_list|<
name|ChmLzxcControlData
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7897854774939631565L
decl_stmt|;
comment|/* class' members */
specifier|private
name|long
name|size
decl_stmt|;
comment|/* 0 */
specifier|private
name|byte
index|[]
name|signature
decl_stmt|;
specifier|private
name|long
name|version
decl_stmt|;
comment|/* 8 */
specifier|private
name|long
name|resetInterval
decl_stmt|;
comment|/* c */
specifier|private
name|long
name|windowSize
decl_stmt|;
comment|/* 10 */
specifier|private
name|long
name|windowsPerReset
decl_stmt|;
comment|/* 14 */
specifier|private
name|long
name|unknown_18
decl_stmt|;
comment|/* 18 */
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
name|ChmLzxcControlData
parameter_list|()
block|{
name|signature
operator|=
name|ChmConstants
operator|.
name|LZXC
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
expr_stmt|;
comment|/*                                                         * 4                                                         * (LZXC                                                         * )                                                         */
block|}
comment|/**      * Returns a remained data      *       * @return dataRemained      */
specifier|private
name|int
name|getDataRemained
parameter_list|()
block|{
return|return
name|dataRemained
return|;
block|}
comment|/**      * Sets a remained data      *       * @param dataRemained      */
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
comment|/**      * Returns a place holder      *       * @return current_place      */
specifier|private
name|int
name|getCurrentPlace
parameter_list|()
block|{
return|return
name|currentPlace
return|;
block|}
comment|/**      * Sets a place holder      *       * @param currentPlace      */
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
comment|/**      * Returns a size of control data      *       * @return size      */
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * Sets a size of control data      *       * @param size      */
specifier|protected
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**      * Returns a signature of control data block      *       * @return signature      */
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
comment|/**      * Sets a signature of control data block      *       * @param signature      */
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
comment|/**      * Returns a version of control data block      *       * @return version      */
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**      * Sets version of control data block      *       * @param version      */
specifier|protected
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
comment|/**      * Returns reset interval      *       * @return reset_interval      */
specifier|public
name|long
name|getResetInterval
parameter_list|()
block|{
return|return
name|resetInterval
return|;
block|}
comment|/**      * Sets a reset interval      *       * @param resetInterval      */
specifier|protected
name|void
name|setResetInterval
parameter_list|(
name|long
name|resetInterval
parameter_list|)
block|{
name|this
operator|.
name|resetInterval
operator|=
name|resetInterval
expr_stmt|;
block|}
comment|/**      * Returns a window size      *       * @return window_size      */
specifier|public
name|long
name|getWindowSize
parameter_list|()
block|{
return|return
name|windowSize
return|;
block|}
comment|/**      * Sets a window size      *       * @param windowSize      */
specifier|protected
name|void
name|setWindowSize
parameter_list|(
name|long
name|windowSize
parameter_list|)
block|{
name|this
operator|.
name|windowSize
operator|=
name|windowSize
expr_stmt|;
block|}
comment|/**      * Returns windows per reset      *       * @return      */
specifier|public
name|long
name|getWindowsPerReset
parameter_list|()
block|{
return|return
name|windowsPerReset
return|;
block|}
comment|/**      * Sets windows per reset      *       * @param windowsPerReset      */
specifier|protected
name|void
name|setWindowsPerReset
parameter_list|(
name|long
name|windowsPerReset
parameter_list|)
block|{
name|this
operator|.
name|windowsPerReset
operator|=
name|windowsPerReset
expr_stmt|;
block|}
comment|/**      * Returns unknown 18 bytes      *       * @return unknown_18      */
specifier|public
name|long
name|getUnknown_18
parameter_list|()
block|{
return|return
name|unknown_18
return|;
block|}
comment|/**      * Sets unknown 18 bytes      *       * @param unknown_18      */
specifier|protected
name|void
name|setUnknown_18
parameter_list|(
name|long
name|unknown_18
parameter_list|)
block|{
name|this
operator|.
name|unknown_18
operator|=
name|unknown_18
expr_stmt|;
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
name|ChmParsingException
block|{
assert|assert
operator|(
name|data
operator|!=
literal|null
operator|&&
name|data
operator|.
name|length
operator|>
literal|0
operator|)
assert|;
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
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
index|]
operator||
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
operator|+
literal|1
index|]
operator|<<
literal|8
operator||
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
operator|+
literal|2
index|]
operator|<<
literal|16
operator||
name|data
index|[
name|this
operator|.
name|getCurrentPlace
argument_list|()
operator|+
literal|3
index|]
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
name|void
name|unmarshalCharArray
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|ChmLzxcControlData
name|chmLzxcControlData
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
name|chmLzxcControlData
argument_list|)
expr_stmt|;
name|ChmAssert
operator|.
name|assertPositiveInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|4
argument_list|,
name|chmLzxcControlData
operator|.
name|getSignature
argument_list|()
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
comment|/**      * Returns textual representation of ChmLzxcControlData      */
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
literal|"size(unknown):="
operator|+
name|this
operator|.
name|getSize
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"signature(Compression type identifier):="
operator|+
operator|new
name|String
argument_list|(
name|this
operator|.
name|getSignature
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"version(Possibly numeric code for LZX):="
operator|+
name|this
operator|.
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
literal|"resetInterval(The Huffman reset interval):="
operator|+
name|this
operator|.
name|getResetInterval
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"windowSize:="
operator|+
name|this
operator|.
name|getWindowSize
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"windowsPerReset(unknown (sometimes 2, sometimes 1, sometimes 0):="
operator|+
name|this
operator|.
name|getWindowsPerReset
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"unknown_18:="
operator|+
name|this
operator|.
name|getUnknown_18
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
comment|// @Override
specifier|public
name|void
name|parse
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|ChmLzxcControlData
name|chmLzxcControlData
parameter_list|)
throws|throws
name|TikaException
block|{
if|if
condition|(
name|data
operator|==
literal|null
operator|||
operator|(
name|data
operator|.
name|length
operator|<
name|ChmConstants
operator|.
name|CHM_LZXC_MIN_LEN
operator|)
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"we want at least 0x18 bytes"
argument_list|)
throw|;
name|chmLzxcControlData
operator|.
name|setDataRemained
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|chmLzxcControlData
operator|.
name|setSize
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcControlData
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcControlData
operator|.
name|unmarshalCharArray
argument_list|(
name|data
argument_list|,
name|chmLzxcControlData
argument_list|,
name|ChmConstants
operator|.
name|CHM_SIGNATURE_LEN
argument_list|)
expr_stmt|;
name|chmLzxcControlData
operator|.
name|setVersion
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcControlData
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcControlData
operator|.
name|setResetInterval
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcControlData
operator|.
name|getResetInterval
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcControlData
operator|.
name|setWindowSize
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcControlData
operator|.
name|getWindowSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|chmLzxcControlData
operator|.
name|setWindowsPerReset
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcControlData
operator|.
name|getWindowsPerReset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|>=
name|ChmConstants
operator|.
name|CHM_LZXC_V2_LEN
condition|)
name|chmLzxcControlData
operator|.
name|setUnknown_18
argument_list|(
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmLzxcControlData
operator|.
name|getUnknown_18
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|chmLzxcControlData
operator|.
name|setUnknown_18
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|chmLzxcControlData
operator|.
name|getVersion
argument_list|()
operator|==
literal|2
condition|)
block|{
name|chmLzxcControlData
operator|.
name|setWindowSize
argument_list|(
name|getWindowSize
argument_list|()
operator|*
name|ChmConstants
operator|.
name|CHM_WINDOW_SIZE_BLOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|chmLzxcControlData
operator|.
name|getWindowSize
argument_list|()
operator|==
literal|0
operator|||
name|chmLzxcControlData
operator|.
name|getResetInterval
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"window size / resetInterval should be more than zero"
argument_list|)
throw|;
if|if
condition|(
name|chmLzxcControlData
operator|.
name|getWindowSize
argument_list|()
operator|==
literal|1
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"window size / resetInterval should be more than 1"
argument_list|)
throw|;
comment|/* checks a signature */
if|if
condition|(
operator|!
operator|new
name|String
argument_list|(
name|chmLzxcControlData
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
name|LZXC
argument_list|)
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"the signature does not seem to be correct"
argument_list|)
throw|;
block|}
comment|/**      * @param args      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{     }
block|}
end_class

end_unit

