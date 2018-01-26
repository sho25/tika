begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  * IPTC Metadata Descriptions taken from the IPTC Photo Metadata (July 2010)   * standard. These parts Copyright 2010 International Press Telecommunications   * Council.  */
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
comment|/**  * XMP Photoshop metadata schema.   *   * A collection of property constants for the   * Photo Metadata properties defined in the XMP Photoshop  * standard.  *   * @since Apache Tika 1.2  * @see<a href="http://partners.adobe.com/public/developer/en/xmp/sdk/XMPspecification.pdf">XMP Photoshop</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Photoshop
block|{
name|String
name|NAMESPACE_URI_PHOTOSHOP
init|=
literal|"http://ns.adobe.com/photoshop/1.0/"
decl_stmt|;
name|String
name|PREFIX_PHOTOSHOP
init|=
literal|"photoshop"
decl_stmt|;
name|Property
name|AUTHORS_POSITION
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"AuthorsPosition"
argument_list|)
decl_stmt|;
comment|// TODO Replace this with proper indexed choices support
name|String
index|[]
name|_COLOR_MODE_CHOICES_INDEXED
init|=
block|{
literal|"Bitmap"
block|,
literal|"Greyscale"
block|,
literal|"Indexed Colour"
block|,
literal|"RGB Color"
block|,
literal|"CMYK Colour"
block|,
literal|"Multi-Channel"
block|,
literal|"Duotone"
block|,
literal|"LAB Colour"
block|,
literal|"reserved"
block|,
literal|"reserved"
block|,
literal|"YCbCr Colour"
block|,
literal|"YCgCo Colour"
block|,
literal|"YCbCrK Colour"
block|}
decl_stmt|;
name|Property
name|COLOR_MODE
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"ColorMode"
argument_list|,
name|_COLOR_MODE_CHOICES_INDEXED
argument_list|)
decl_stmt|;
name|Property
name|CAPTION_WRITER
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"CaptionWriter"
argument_list|)
decl_stmt|;
name|Property
name|CATEGORY
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Category"
argument_list|)
decl_stmt|;
name|Property
name|CITY
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"City"
argument_list|)
decl_stmt|;
name|Property
name|COUNTRY
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Country"
argument_list|)
decl_stmt|;
name|Property
name|CREDIT
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Credit"
argument_list|)
decl_stmt|;
name|Property
name|DATE_CREATED
init|=
name|Property
operator|.
name|internalDate
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"DateCreated"
argument_list|)
decl_stmt|;
name|Property
name|HEADLINE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Headline"
argument_list|)
decl_stmt|;
name|Property
name|INSTRUCTIONS
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Instructions"
argument_list|)
decl_stmt|;
name|Property
name|SOURCE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Source"
argument_list|)
decl_stmt|;
name|Property
name|STATE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"State"
argument_list|)
decl_stmt|;
name|Property
name|SUPPLEMENTAL_CATEGORIES
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"SupplementalCategories"
argument_list|)
decl_stmt|;
name|Property
name|TRANSMISSION_REFERENCE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"TransmissionReference"
argument_list|)
decl_stmt|;
name|Property
name|URGENCY
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_PHOTOSHOP
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Urgency"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

