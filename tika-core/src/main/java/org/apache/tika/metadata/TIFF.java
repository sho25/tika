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
comment|/**      * "Exposure time in seconds."      */
name|Property
name|EXPOSURE_TIME
init|=
name|Property
operator|.
name|internalRational
argument_list|(
literal|"exif:ExposureTime"
argument_list|)
decl_stmt|;
comment|//  TODO "exif:Flash"
comment|/**      * "F-Number."      * The f-number is the focal length divided by the "effective" aperture       *  diameter. It is a dimensionless number that is a measure of lens speed.       */
name|Property
name|F_NUMBER
init|=
name|Property
operator|.
name|internalRational
argument_list|(
literal|"exif:FNumber"
argument_list|)
decl_stmt|;
comment|/**      * "Focal length of the lens, in millimeters."      */
name|Property
name|FOCAL_LENGTH
init|=
name|Property
operator|.
name|internalRational
argument_list|(
literal|"exif:FocalLength"
argument_list|)
decl_stmt|;
comment|/**      * "ISO Speed and ISO Latitude of the input device as specified in ISO 12232"      */
name|Property
name|ISO_SPEED_RATINGS
init|=
name|Property
operator|.
name|internalIntegerSequence
argument_list|(
literal|"exif:IsoSpeedRatings"
argument_list|)
decl_stmt|;
comment|/**      * "Manufacturer of the recording equipment."      */
name|Property
name|EQUIPMENT_MAKE
init|=
name|Property
operator|.
name|internalText
argument_list|(
literal|"tiff:Make"
argument_list|)
decl_stmt|;
comment|/**      * "Model name or number of the recording equipment."      */
name|Property
name|EQUIPMENT_MODEL
init|=
name|Property
operator|.
name|internalText
argument_list|(
literal|"tiff:Model"
argument_list|)
decl_stmt|;
comment|/**      * "Software or firmware used to generate the image."      */
name|Property
name|SOFTWARE
init|=
name|Property
operator|.
name|internalText
argument_list|(
literal|"tiff:Software"
argument_list|)
decl_stmt|;
comment|/**      * "The Orientation of the image."      *  1 = 0th row at top, 0th column at left      *  2 = 0th row at top, 0th column at right      *  3 = 0th row at bottom, 0th column at right      *  4 = 0th row at bottom, 0th column at left      *  5 = 0th row at left, 0th column at top      *  6 = 0th row at right, 0th column at top      *  7 = 0th row at right, 0th column at bottom      *  8 = 0th row at left, 0th column at bottom      */
name|Property
name|ORIENTATION
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"tiff:Orientation"
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|,
literal|"4"
argument_list|,
literal|"5"
argument_list|,
literal|"6"
argument_list|,
literal|"7"
argument_list|,
literal|"8"
argument_list|)
decl_stmt|;
comment|/**      * "Horizontal resolution in pixels per unit."      */
name|Property
name|RESOLUTION_HORIZONTAL
init|=
name|Property
operator|.
name|internalRational
argument_list|(
literal|"tiff:XResolution"
argument_list|)
decl_stmt|;
comment|/**      * "Vertical resolution in pixels per unit."      */
name|Property
name|RESOLUTION_VERTICAL
init|=
name|Property
operator|.
name|internalRational
argument_list|(
literal|"tiff:YResolution"
argument_list|)
decl_stmt|;
comment|/**      * "Units used for Horizontal and Vertical Resolutions."      * One of "Inch" or "cm"      */
name|Property
name|RESOLUTION_UNIT
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"tiff:ResolutionUnit"
argument_list|,
literal|"Inch"
argument_list|,
literal|"cm"
argument_list|)
decl_stmt|;
comment|/**      * "Date and time when original image was generated"      */
name|Property
name|ORIGINAL_DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
literal|"exif:DateTimeOriginal"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

