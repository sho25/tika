begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   *     http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|strings
package|;
end_package

begin_comment
comment|/**  * Character encoding of the strings that are to be found using the "strings" command.  *  */
end_comment

begin_enum
specifier|public
enum|enum
name|StringsEncoding
block|{
name|SINGLE_7_BIT
argument_list|(
literal|'s'
argument_list|,
literal|"single-7-bit-byte"
argument_list|)
block|,
comment|// default
name|SINGLE_8_BIT
argument_list|(
literal|'S'
argument_list|,
literal|"single-8-bit-byte"
argument_list|)
block|,
name|BIGENDIAN_16_BIT
argument_list|(
literal|'b'
argument_list|,
literal|"16-bit bigendian"
argument_list|)
block|,
name|LITTLEENDIAN_16_BIT
argument_list|(
literal|'l'
argument_list|,
literal|"16-bit littleendian"
argument_list|)
block|,
name|BIGENDIAN_32_BIT
argument_list|(
literal|'B'
argument_list|,
literal|"32-bit bigendian"
argument_list|)
block|,
name|LITTLEENDIAN_32_BIT
argument_list|(
literal|'L'
argument_list|,
literal|"32-bit littleendian"
argument_list|)
block|;
specifier|private
name|char
name|value
decl_stmt|;
specifier|private
name|String
name|encoding
decl_stmt|;
specifier|private
name|StringsEncoding
parameter_list|(
name|char
name|value
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|encoding
operator|=
name|encoding
expr_stmt|;
block|}
specifier|public
name|char
name|get
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|encoding
return|;
block|}
block|}
end_enum

end_unit

