begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2016 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|image
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

begin_comment
comment|/**  *  * @author Manisha Kampasi  */
end_comment

begin_class
specifier|public
class|class
name|ICNSType
block|{
specifier|private
specifier|final
name|int
name|type
decl_stmt|;
specifier|private
specifier|final
name|int
name|width
decl_stmt|;
specifier|private
specifier|final
name|int
name|height
decl_stmt|;
specifier|private
specifier|final
name|int
name|bitsPerPixel
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|hasMask
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|hasRetinaDisplay
decl_stmt|;
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|int
name|getWidth
parameter_list|()
block|{
return|return
name|width
return|;
block|}
specifier|public
name|int
name|getHeight
parameter_list|()
block|{
return|return
name|height
return|;
block|}
specifier|public
name|int
name|getBitsPerPixel
parameter_list|()
block|{
return|return
name|bitsPerPixel
return|;
block|}
specifier|public
name|boolean
name|hasMask
parameter_list|()
block|{
return|return
name|hasMask
return|;
block|}
specifier|public
name|boolean
name|hasRetinaDisplay
parameter_list|()
block|{
return|return
name|hasRetinaDisplay
return|;
block|}
specifier|public
specifier|static
name|int
name|converttoInt
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot convert to integer"
argument_list|)
throw|;
block|}
return|return
operator|(
operator|(
literal|0xff
operator|&
name|bytes
index|[
literal|0
index|]
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
literal|0xff
operator|&
name|bytes
index|[
literal|1
index|]
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
literal|0xff
operator|&
name|bytes
index|[
literal|2
index|]
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
literal|0xff
operator|&
name|bytes
index|[
literal|3
index|]
operator|)
return|;
block|}
specifier|private
name|ICNSType
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|,
name|int
name|bitsPerPixel
parameter_list|,
name|boolean
name|hasMask
parameter_list|,
name|boolean
name|hasRetinaDisplay
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bytes
operator|=
name|type
operator|.
name|getBytes
argument_list|(
literal|"US-ASCII"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|cannotHappen
parameter_list|)
block|{         }
name|this
operator|.
name|type
operator|=
name|converttoInt
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|this
operator|.
name|width
operator|=
name|width
expr_stmt|;
name|this
operator|.
name|height
operator|=
name|height
expr_stmt|;
name|this
operator|.
name|bitsPerPixel
operator|=
name|bitsPerPixel
expr_stmt|;
name|this
operator|.
name|hasMask
operator|=
name|hasMask
expr_stmt|;
name|this
operator|.
name|hasRetinaDisplay
operator|=
name|hasRetinaDisplay
expr_stmt|;
block|}
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_32x32_1BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ICON"
argument_list|,
literal|32
argument_list|,
literal|32
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x12_1BIT_IMAGE_AND_MASK
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icm#"
argument_list|,
literal|16
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x12_4BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icm4"
argument_list|,
literal|16
argument_list|,
literal|12
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x12_8BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icm8"
argument_list|,
literal|16
argument_list|,
literal|12
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x16_8BIT_MASK
init|=
operator|new
name|ICNSType
argument_list|(
literal|"s8mk"
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|,
literal|8
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x16_1BIT_IMAGE_AND_MASK
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ics#"
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x16_4BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ics4"
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x16_8BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ics8"
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x16_24BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"is32"
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|,
literal|24
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_32x32_8BIT_MASK
init|=
operator|new
name|ICNSType
argument_list|(
literal|"l8mk"
argument_list|,
literal|32
argument_list|,
literal|32
argument_list|,
literal|8
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_32x32_1BIT_IMAGE_AND_MASK
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ICN#"
argument_list|,
literal|32
argument_list|,
literal|32
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_32x32_4BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icl4"
argument_list|,
literal|32
argument_list|,
literal|32
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_32x32_8BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icl8"
argument_list|,
literal|32
argument_list|,
literal|32
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_32x32_24BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"il32"
argument_list|,
literal|32
argument_list|,
literal|32
argument_list|,
literal|24
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_48x48_8BIT_MASK
init|=
operator|new
name|ICNSType
argument_list|(
literal|"h8mk"
argument_list|,
literal|48
argument_list|,
literal|48
argument_list|,
literal|8
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_48x48_1BIT_IMAGE_AND_MASK
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ich#"
argument_list|,
literal|48
argument_list|,
literal|48
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_48x48_4BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ich4"
argument_list|,
literal|48
argument_list|,
literal|48
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_48x48_8BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ich8"
argument_list|,
literal|48
argument_list|,
literal|48
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_48x48_24BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ih32"
argument_list|,
literal|48
argument_list|,
literal|48
argument_list|,
literal|24
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_128x128_8BIT_MASK
init|=
operator|new
name|ICNSType
argument_list|(
literal|"t8mk"
argument_list|,
literal|128
argument_list|,
literal|128
argument_list|,
literal|8
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_128x128_24BIT_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"it32"
argument_list|,
literal|128
argument_list|,
literal|128
argument_list|,
literal|24
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x16_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icp4"
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_32x32_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icp5"
argument_list|,
literal|32
argument_list|,
literal|32
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_64x64_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icp6"
argument_list|,
literal|64
argument_list|,
literal|64
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_128x128_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"icp7"
argument_list|,
literal|128
argument_list|,
literal|128
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_256x256_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ic08"
argument_list|,
literal|256
argument_list|,
literal|256
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_512x512_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ic09"
argument_list|,
literal|512
argument_list|,
literal|512
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_1024x1024_2X_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ic10"
argument_list|,
literal|1024
argument_list|,
literal|1024
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_16x16_2X_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ic11"
argument_list|,
literal|16
argument_list|,
literal|16
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_32x32_2X_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ic12"
argument_list|,
literal|32
argument_list|,
literal|32
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_128x128_2X_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ic13"
argument_list|,
literal|128
argument_list|,
literal|128
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ICNSType
name|ICNS_256x256_2X_JPEG_PNG_IMAGE
init|=
operator|new
name|ICNSType
argument_list|(
literal|"ic14"
argument_list|,
literal|256
argument_list|,
literal|256
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ICNSType
index|[]
name|allImageTypes
init|=
block|{
name|ICNS_32x32_1BIT_IMAGE
block|,
name|ICNS_16x12_1BIT_IMAGE_AND_MASK
block|,
name|ICNS_16x12_4BIT_IMAGE
block|,
name|ICNS_16x12_8BIT_IMAGE
block|,
name|ICNS_16x16_1BIT_IMAGE_AND_MASK
block|,
name|ICNS_16x16_4BIT_IMAGE
block|,
name|ICNS_16x16_8BIT_IMAGE
block|,
name|ICNS_16x16_24BIT_IMAGE
block|,
name|ICNS_32x32_1BIT_IMAGE_AND_MASK
block|,
name|ICNS_32x32_4BIT_IMAGE
block|,
name|ICNS_32x32_8BIT_IMAGE
block|,
name|ICNS_32x32_24BIT_IMAGE
block|,
name|ICNS_48x48_1BIT_IMAGE_AND_MASK
block|,
name|ICNS_48x48_4BIT_IMAGE
block|,
name|ICNS_48x48_8BIT_IMAGE
block|,
name|ICNS_48x48_24BIT_IMAGE
block|,
name|ICNS_128x128_24BIT_IMAGE
block|,
name|ICNS_16x16_8BIT_MASK
block|,
name|ICNS_32x32_8BIT_MASK
block|,
name|ICNS_48x48_8BIT_MASK
block|,
name|ICNS_128x128_8BIT_MASK
block|,
name|ICNS_16x16_JPEG_PNG_IMAGE
block|,
name|ICNS_32x32_JPEG_PNG_IMAGE
block|,
name|ICNS_64x64_JPEG_PNG_IMAGE
block|,
name|ICNS_128x128_JPEG_PNG_IMAGE
block|,
name|ICNS_256x256_JPEG_PNG_IMAGE
block|,
name|ICNS_512x512_JPEG_PNG_IMAGE
block|,
name|ICNS_1024x1024_2X_JPEG_PNG_IMAGE
block|,
name|ICNS_16x16_2X_JPEG_PNG_IMAGE
block|,
name|ICNS_32x32_2X_JPEG_PNG_IMAGE
block|,
name|ICNS_128x128_2X_JPEG_PNG_IMAGE
block|,
name|ICNS_256x256_2X_JPEG_PNG_IMAGE
block|}
decl_stmt|;
specifier|public
specifier|static
name|ICNSType
name|findIconType
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|int
name|type
init|=
name|converttoInt
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
for|for
control|(
name|ICNSType
name|allImageType
range|:
name|allImageTypes
control|)
block|{
if|if
condition|(
name|allImageType
operator|.
name|getType
argument_list|()
operator|==
name|type
condition|)
block|{
return|return
name|allImageType
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

