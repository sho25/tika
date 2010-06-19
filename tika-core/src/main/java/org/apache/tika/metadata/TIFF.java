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
name|metadata
package|;
end_package

begin_comment
comment|/**  * XMP Exif TIFF schema. This is a collection of  * {@link Property property definition} constants for the Exif TIFF  * properties defined in the XMP standard.  *  * @since Apache Tika 0.8  * @see<a href="http://www.adobe.com/devnet/xmp/pdfs/XMPSpecificationPart2.pdf"  *>XMP Specification, Part 2: Standard Schemas</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|TIFF
block|{
comment|/**      * "Number of bits per component in each channel."      */
name|Property
name|BITS_PER_SAMPLE
init|=
name|Property
operator|.
name|internalIntegerSequence
argument_list|(
literal|"tiff:BitsPerSample"
argument_list|)
decl_stmt|;
comment|/**      * "Image height in pixels."      */
name|Property
name|IMAGE_LENGTH
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
literal|"tiff:ImageLength"
argument_list|)
decl_stmt|;
comment|/**      * "Image width in pixels."      */
name|Property
name|IMAGE_WIDTH
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
literal|"tiff:ImageWidth"
argument_list|)
decl_stmt|;
comment|/**      * "Number of components per pixel."      */
name|Property
name|SAMPLES_PER_PIXEL
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
literal|"tiff:SamplesPerPixel"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

