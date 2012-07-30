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
comment|/**  * Contains a core set of basic Tika metadata properties, which all parsers  *  will attempt to supply (where the file format permits). These are all  *  defined in terms of other standard namespaces.  *    * Users of Tika who wish to have consistent metadata across file formats  *  can make use of these Properties, knowing that where present they will  *  have consistent semantic meaning between different file formats. (No   *  matter if one file format calls it Title, another Long-Title and another  *  Long-Name, if they all mean the same thing as defined by   *  {@link DublinCore#TITLE} then they will all be present as such)  *  * For now, most of these properties are composite ones including the deprecated  *  non-prefixed String properties from the Metadata class. In Tika 2.0, most  *  of these will revert back to simple assignments.  *   * @since Apache Tika 1.2  */
end_comment

begin_interface
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
interface|interface
name|TikaCoreProperties
block|{
comment|/**      * @see DublinCore#FORMAT      */
specifier|public
specifier|static
specifier|final
name|Property
name|FORMAT
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|FORMAT
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|FORMAT
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#IDENTIFIER     */
specifier|public
specifier|static
specifier|final
name|Property
name|IDENTIFIER
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|IDENTIFIER
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|IDENTIFIER
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#CONTRIBUTOR     */
specifier|public
specifier|static
specifier|final
name|Property
name|CONTRIBUTOR
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|CONTRIBUTOR
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|CONTRIBUTOR
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#COVERAGE     */
specifier|public
specifier|static
specifier|final
name|Property
name|COVERAGE
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|COVERAGE
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|COVERAGE
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#CREATOR     */
specifier|public
specifier|static
specifier|final
name|Property
name|CREATOR
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|CREATOR
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Office
operator|.
name|AUTHOR
block|,
name|Property
operator|.
name|internalTextBag
argument_list|(
name|Metadata
operator|.
name|CREATOR
argument_list|)
block|,
name|Property
operator|.
name|internalTextBag
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**      * @see Office#LAST_AUTHOR      */
specifier|public
specifier|static
specifier|final
name|Property
name|MODIFIER
init|=
name|Property
operator|.
name|composite
argument_list|(
name|Office
operator|.
name|LAST_AUTHOR
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|LAST_AUTHOR
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**      * @see XMP#CREATOR_TOOL      */
specifier|public
specifier|static
specifier|final
name|Property
name|CREATOR_TOOL
init|=
name|XMP
operator|.
name|CREATOR_TOOL
decl_stmt|;
comment|/**     * @see DublinCore#LANGUAGE     */
specifier|public
specifier|static
specifier|final
name|Property
name|LANGUAGE
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|LANGUAGE
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|LANGUAGE
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#PUBLISHER     */
specifier|public
specifier|static
specifier|final
name|Property
name|PUBLISHER
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|PUBLISHER
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|PUBLISHER
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#RELATION     */
specifier|public
specifier|static
specifier|final
name|Property
name|RELATION
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|RELATION
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|RELATION
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#RIGHTS     */
specifier|public
specifier|static
specifier|final
name|Property
name|RIGHTS
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|RIGHTS
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|RIGHTS
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#SOURCE     */
specifier|public
specifier|static
specifier|final
name|Property
name|SOURCE
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|SOURCE
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|SOURCE
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**     * @see DublinCore#TYPE     */
specifier|public
specifier|static
specifier|final
name|Property
name|TYPE
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|TYPE
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|TYPE
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|// Descriptive properties
comment|/**      * @see DublinCore#TITLE      */
specifier|public
specifier|static
specifier|final
name|Property
name|TITLE
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|TITLE
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**      * @see DublinCore#DESCRIPTION      */
specifier|public
specifier|static
specifier|final
name|Property
name|DESCRIPTION
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|DESCRIPTION
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|DESCRIPTION
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**      * @see DublinCore#SUBJECT      * @see Office#KEYWORDS      */
specifier|public
specifier|static
specifier|final
name|Property
name|KEYWORDS
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|SUBJECT
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Office
operator|.
name|KEYWORDS
block|,
name|Property
operator|.
name|internalTextBag
argument_list|(
name|MSOffice
operator|.
name|KEYWORDS
argument_list|)
block|,
name|Property
operator|.
name|internalTextBag
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|// Date related properties
comment|/**        * @see DublinCore#DATE        * @see Office#CREATION_DATE        */
specifier|public
specifier|static
specifier|final
name|Property
name|CREATED
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|CREATED
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Office
operator|.
name|CREATION_DATE
block|,
name|MSOffice
operator|.
name|CREATION_DATE
block|}
argument_list|)
decl_stmt|;
comment|/**        * @see DublinCore#MODIFIED       * @see Metadata#DATE       * @see Office#SAVE_DATE        */
specifier|public
specifier|static
specifier|final
name|Property
name|MODIFIED
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|MODIFIED
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Metadata
operator|.
name|DATE
block|,
name|Office
operator|.
name|SAVE_DATE
block|,
name|MSOffice
operator|.
name|LAST_SAVED
block|,
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|MODIFIED
argument_list|)
block|,
name|Property
operator|.
name|internalText
argument_list|(
literal|"Last-Modified"
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/** @see Office#PRINT_DATE */
specifier|public
specifier|static
specifier|final
name|Property
name|PRINT_DATE
init|=
name|Property
operator|.
name|composite
argument_list|(
name|Office
operator|.
name|PRINT_DATE
argument_list|,
operator|new
name|Property
index|[]
block|{
name|MSOffice
operator|.
name|LAST_PRINTED
block|}
argument_list|)
decl_stmt|;
comment|/**       * @see XMP#METADATA_DATE       */
specifier|public
specifier|static
specifier|final
name|Property
name|METADATA_DATE
init|=
name|XMP
operator|.
name|METADATA_DATE
decl_stmt|;
comment|// Geographic related properties
comment|/**      * @see Geographic#LATITUDE      */
specifier|public
specifier|static
specifier|final
name|Property
name|LATITUDE
init|=
name|Geographic
operator|.
name|LATITUDE
decl_stmt|;
comment|/**      * @see Geographic#LONGITUDE      */
specifier|public
specifier|static
specifier|final
name|Property
name|LONGITUDE
init|=
name|Geographic
operator|.
name|LONGITUDE
decl_stmt|;
comment|/**      * @see Geographic#ALTITUDE      */
specifier|public
specifier|static
specifier|final
name|Property
name|ALTITUDE
init|=
name|Geographic
operator|.
name|ALTITUDE
decl_stmt|;
comment|// Comment and rating properties
comment|/**      * @see XMP#RATING      */
specifier|public
specifier|static
specifier|final
name|Property
name|RATING
init|=
name|XMP
operator|.
name|RATING
decl_stmt|;
comment|/**       * @see OfficeOpenXMLExtended#COMMENTS       */
specifier|public
specifier|static
specifier|final
name|Property
name|COMMENTS
init|=
name|Property
operator|.
name|composite
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|COMMENTS
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalTextBag
argument_list|(
name|ClimateForcast
operator|.
name|COMMENT
argument_list|)
block|,
name|Property
operator|.
name|internalTextBag
argument_list|(
name|MSOffice
operator|.
name|COMMENTS
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|// TODO: Remove transition properties in Tika 2.0
comment|/**       * @see DublinCore#SUBJECT       * @deprecated use TikaCoreProperties#KEYWORDS      */
annotation|@
name|Deprecated
specifier|public
specifier|static
specifier|final
name|Property
name|TRANSITION_KEYWORDS_TO_DC_SUBJECT
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|SUBJECT
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalTextBag
argument_list|(
name|MSOffice
operator|.
name|KEYWORDS
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**       * @see OfficeOpenXMLExtended#COMMENTS       * @deprecated use TikaCoreProperties#DESCRIPTION      */
annotation|@
name|Deprecated
specifier|public
specifier|static
specifier|final
name|Property
name|TRANSITION_SUBJECT_TO_DC_DESCRIPTION
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|DESCRIPTION
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**       * @see DublinCore#TITLE       * @deprecated use TikaCoreProperties#TITLE      */
annotation|@
name|Deprecated
specifier|public
specifier|static
specifier|final
name|Property
name|TRANSITION_SUBJECT_TO_DC_TITLE
init|=
name|Property
operator|.
name|composite
argument_list|(
name|DublinCore
operator|.
name|TITLE
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
block|}
argument_list|)
decl_stmt|;
comment|/**       * @see OfficeOpenXMLCore#SUBJECT       * @deprecated use OfficeOpenXMLCore#SUBJECT      */
annotation|@
name|Deprecated
specifier|public
specifier|static
specifier|final
name|Property
name|TRANSITION_SUBJECT_TO_OO_SUBJECT
init|=
name|Property
operator|.
name|composite
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|SUBJECT
argument_list|,
operator|new
name|Property
index|[]
block|{
name|Property
operator|.
name|internalText
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
block|}
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

