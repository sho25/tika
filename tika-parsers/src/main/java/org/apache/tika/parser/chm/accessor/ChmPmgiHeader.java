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

begin_comment
comment|/**  * Description Note: not always exists An index chunk has the following format:  * 0000: char[4] 'PMGI' 0004: DWORD Length of quickref/free area at end of  * directory chunk 0008: Directory index entries (to quickref/free area) The  * quickref area in an PMGI is the same as in an PMGL The format of a directory  * index entry is as follows: BYTE: length of name BYTEs: name (UTF-8 encoded)  * ENCINT: directory listing chunk which starts with name Encoded Integers aka  * ENCINT An ENCINT is a variable-length integer. The high bit of each byte  * indicates "continued to the next byte". Bytes are stored most significant to  * least significant. So, for example, $EA $15 is (((0xEA&0x7F)<<7)|0x15) =  * 0x3515.  *   *<p>  * Note: This class is not in use  *   * {@link http://translated.by/you/microsoft-s-html-help-chm-format-incomplete/original/?show-translation-form=1 }  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ChmPmgiHeader
implements|implements
name|ChmAccessor
argument_list|<
name|ChmPmgiHeader
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2092282339894303701L
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
name|ChmPmgiHeader
parameter_list|()
block|{
try|try
block|{
name|signature
operator|=
name|ChmConstants
operator|.
name|CHM_PMGI_MARKER
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|/* 0 (PMGI) */
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
specifier|private
name|void
name|unmarshalCharArray
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|ChmPmgiHeader
name|chmPmgiHeader
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|ChmParsingException
block|{
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
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
name|chmPmgiHeader
argument_list|)
expr_stmt|;
name|ChmAssert
operator|.
name|assertPositiveInt
argument_list|(
name|count
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
try|try
block|{
name|index
operator|=
name|ChmCommons
operator|.
name|indexOf
argument_list|(
name|data
argument_list|,
name|ChmConstants
operator|.
name|CHM_PMGI_MARKER
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
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
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|index
argument_list|,
name|chmPmgiHeader
operator|.
name|getSignature
argument_list|()
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
else|else
block|{
comment|//Some chm documents (actually most of them) do not contain
comment|//PMGI header, in this case, we just notice about it.
block|}
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
comment|/**      * Returns pmgi signature if exists      *       * @return signature      */
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
comment|/**      * Sets pmgi signature      *       * @param signature      */
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
comment|/**      * Returns pmgi free space      *       * @return free_space      */
specifier|public
name|long
name|getFreeSpace
parameter_list|()
block|{
return|return
name|free_space
return|;
block|}
comment|/**      * Sets pmgi free space      *       * @param free_space      */
specifier|protected
name|void
name|setFreeSpace
parameter_list|(
name|long
name|free_space
parameter_list|)
block|{
name|this
operator|.
name|free_space
operator|=
name|free_space
expr_stmt|;
block|}
comment|/**      * Returns textual representation of the pmgi header      */
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
literal|"signature:="
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
name|ChmPmgiHeader
name|chmPmgiHeader
parameter_list|)
throws|throws
name|TikaException
block|{
comment|/* we only know how to deal with a 0x8 byte structures */
if|if
condition|(
name|data
operator|.
name|length
operator|<
name|ChmConstants
operator|.
name|CHM_PMGI_LEN
condition|)
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"we only know how to deal with a 0x8 byte structures"
argument_list|)
throw|;
comment|/* unmarshal fields */
name|chmPmgiHeader
operator|.
name|unmarshalCharArray
argument_list|(
name|data
argument_list|,
name|chmPmgiHeader
argument_list|,
name|ChmConstants
operator|.
name|CHM_SIGNATURE_LEN
argument_list|)
expr_stmt|;
name|chmPmgiHeader
operator|.
name|setFreeSpace
argument_list|(
name|chmPmgiHeader
operator|.
name|unmarshalUInt32
argument_list|(
name|data
argument_list|,
name|chmPmgiHeader
operator|.
name|getFreeSpace
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|/* check structure */
try|try
block|{
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|chmPmgiHeader
operator|.
name|getSignature
argument_list|()
argument_list|,
name|ChmConstants
operator|.
name|CHM_PMGI_MARKER
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
condition|)
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"it does not seem to be valid a PMGI signature, check ChmItsp index_root if it was -1, means no PMGI, use PMGL insted"
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
block|{      }
block|}
end_class

end_unit

