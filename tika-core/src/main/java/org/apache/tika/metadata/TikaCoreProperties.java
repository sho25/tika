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
comment|/**      * A file might contain different types of embedded documents.      * The most common is the ATTACHMENT.      *<p>      * An INLINE embedded resource should be used for embedded image      * files that are used to render the page image (as in PDXObjImages in PDF files).      *<p>      * A MACRO is code that is embedded in the document and is intended      * to be executable within the application that opens the document.  This      * includes traditional macros within Microsoft Office files and      * javascript within PDFActions.  This would not include, e.g., an      * .exe file embedded in a .zip file.      *<p>      * Not all parsers have yet implemented this.      *      */
specifier|public
enum|enum
name|EmbeddedResourceType
block|{
name|INLINE
block|,
comment|//image that is intended to be displayed in a rendering of the file
name|ATTACHMENT
block|,
comment|//standard attachment as in email
name|MACRO
block|,
comment|//any code that is intended to be run by the application
name|METADATA
block|,
comment|//e.g. xmp, xfa
name|FONT
block|,
comment|//embedded font files
name|THUMBNAIL
block|;
comment|//TODO: set this in parsers that handle thumbnails
block|}
empty_stmt|;
comment|/**      * The common delimiter used between the namespace abbreviation and the property name      */
name|String
name|NAMESPACE_PREFIX_DELIMITER
init|=
literal|":"
decl_stmt|;
comment|/**      * Use this to prefix metadata properties that store information      * about the parsing process.  Users should be able to distinguish      * between metadata that was contained within the document and      * metadata about the parsing process.      * In Tika 2.0 (or earlier?), let's change X-ParsedBy to X-TIKA-Parsed-By.      */
specifier|public
specifier|static
name|String
name|TIKA_META_PREFIX
init|=
literal|"X-TIKA"
operator|+
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
comment|/**      * Use this to store parse exception information in the Metadata object.      */
specifier|public
specifier|static
name|String
name|TIKA_META_EXCEPTION_PREFIX
init|=
name|TIKA_META_PREFIX
operator|+
literal|"EXCEPTION"
operator|+
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
comment|/**      * Use this to store exceptions caught during a parse that are      * non-fatal, e.g. if a parser is in lenient mode and more      * content can be extracted if we ignore an exception thrown by      * a dependency.      */
specifier|public
specifier|static
specifier|final
name|Property
name|TIKA_META_EXCEPTION_WARNING
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
name|TIKA_META_EXCEPTION_PREFIX
operator|+
literal|"warn"
argument_list|)
decl_stmt|;
comment|/**      * Use this to store exceptions caught while trying to read the      * stream of an embedded resource.  Do not use this if there is      * a parse exception on the embedded resource.      */
name|Property
name|TIKA_META_EXCEPTION_EMBEDDED_STREAM
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
name|TIKA_META_EXCEPTION_PREFIX
operator|+
literal|"embedded_stream_exception"
argument_list|)
decl_stmt|;
name|String
name|RESOURCE_NAME_KEY
init|=
literal|"resourceName"
decl_stmt|;
name|String
name|PROTECTED
init|=
literal|"protected"
decl_stmt|;
name|String
name|EMBEDDED_RELATIONSHIP_ID
init|=
literal|"embeddedRelationshipId"
decl_stmt|;
name|String
name|EMBEDDED_STORAGE_CLASS_ID
init|=
literal|"embeddedStorageClassId"
decl_stmt|;
name|String
name|EMBEDDED_RESOURCE_TYPE_KEY
init|=
literal|"embeddedResourceType"
decl_stmt|;
comment|/**      * Some file formats can store information about their original      * file name/location or about their attachment's original file name/location.      */
specifier|public
specifier|static
specifier|final
name|Property
name|ORIGINAL_RESOURCE_NAME
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
name|TIKA_META_PREFIX
operator|+
literal|"origResourceName"
argument_list|)
decl_stmt|;
comment|/**      * This is currently used to identify Content-Type that may be      * included within a document, such as in html documents      * (e.g.<meta http-equiv="content-type" content="text/html; charset=UTF-8">)      , or the value might come from outside the document.  This information      * may be faulty and should be treated only as a hint.      */
name|Property
name|CONTENT_TYPE_HINT
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_TYPE
operator|+
literal|"-Hint"
argument_list|)
decl_stmt|;
name|Property
name|CONTENT_TYPE_OVERRIDE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_TYPE
operator|+
literal|"-Override"
argument_list|)
decl_stmt|;
comment|/**      * @see DublinCore#FORMAT      */
name|Property
name|FORMAT
init|=
name|DublinCore
operator|.
name|FORMAT
decl_stmt|;
comment|/**     * @see DublinCore#IDENTIFIER     */
name|Property
name|IDENTIFIER
init|=
name|DublinCore
operator|.
name|IDENTIFIER
decl_stmt|;
comment|/**     * @see DublinCore#CONTRIBUTOR     */
name|Property
name|CONTRIBUTOR
init|=
name|DublinCore
operator|.
name|CONTRIBUTOR
decl_stmt|;
comment|/**     * @see DublinCore#COVERAGE     */
name|Property
name|COVERAGE
init|=
name|DublinCore
operator|.
name|COVERAGE
decl_stmt|;
comment|/**     * @see DublinCore#CREATOR     */
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
block|,             }
argument_list|)
decl_stmt|;
comment|/**      * @see Office#LAST_AUTHOR      */
name|Property
name|MODIFIER
init|=
name|Office
operator|.
name|LAST_AUTHOR
decl_stmt|;
comment|/**      * @see XMP#CREATOR_TOOL      */
name|Property
name|CREATOR_TOOL
init|=
name|XMP
operator|.
name|CREATOR_TOOL
decl_stmt|;
comment|/**     * @see DublinCore#LANGUAGE     */
name|Property
name|LANGUAGE
init|=
name|DublinCore
operator|.
name|LANGUAGE
decl_stmt|;
comment|/**     * @see DublinCore#PUBLISHER     */
name|Property
name|PUBLISHER
init|=
name|DublinCore
operator|.
name|PUBLISHER
decl_stmt|;
comment|/**     * @see DublinCore#RELATION     */
name|Property
name|RELATION
init|=
name|DublinCore
operator|.
name|RELATION
decl_stmt|;
comment|/**     * @see DublinCore#RIGHTS     */
name|Property
name|RIGHTS
init|=
name|DublinCore
operator|.
name|RIGHTS
decl_stmt|;
comment|/**     * @see DublinCore#SOURCE     */
name|Property
name|SOURCE
init|=
name|DublinCore
operator|.
name|SOURCE
decl_stmt|;
comment|/**     * @see DublinCore#TYPE     */
name|Property
name|TYPE
init|=
name|DublinCore
operator|.
name|TYPE
decl_stmt|;
comment|// Descriptive properties
comment|/**      * @see DublinCore#TITLE      */
name|Property
name|TITLE
init|=
name|DublinCore
operator|.
name|TITLE
decl_stmt|;
comment|/**      * @see DublinCore#DESCRIPTION      */
name|Property
name|DESCRIPTION
init|=
name|DublinCore
operator|.
name|DESCRIPTION
decl_stmt|;
comment|/**      * {@link DublinCore#SUBJECT}; should include both subject and keywords      *  if a document format has both.  See also {@link Office#KEYWORDS}      *  and {@link OfficeOpenXMLCore#SUBJECT}.      */
name|Property
name|SUBJECT
init|=
name|DublinCore
operator|.
name|SUBJECT
decl_stmt|;
comment|// Date related properties
comment|/**        * @see DublinCore#DATE        * @see Office#CREATION_DATE        */
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
block|,              }
argument_list|)
decl_stmt|;
comment|/**        * @see DublinCore#MODIFIED       * @see Office#SAVE_DATE       */
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
name|Office
operator|.
name|SAVE_DATE
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
name|Property
name|PRINT_DATE
init|=
name|Office
operator|.
name|PRINT_DATE
decl_stmt|;
comment|/**       * @see XMP#METADATA_DATE       */
name|Property
name|METADATA_DATE
init|=
name|XMP
operator|.
name|METADATA_DATE
decl_stmt|;
comment|// Geographic related properties
comment|/**      * @see Geographic#LATITUDE      */
name|Property
name|LATITUDE
init|=
name|Geographic
operator|.
name|LATITUDE
decl_stmt|;
comment|/**      * @see Geographic#LONGITUDE      */
name|Property
name|LONGITUDE
init|=
name|Geographic
operator|.
name|LONGITUDE
decl_stmt|;
comment|/**      * @see Geographic#ALTITUDE      */
name|Property
name|ALTITUDE
init|=
name|Geographic
operator|.
name|ALTITUDE
decl_stmt|;
comment|// Comment and rating properties
comment|/**      * @see XMP#RATING      */
name|Property
name|RATING
init|=
name|XMP
operator|.
name|RATING
decl_stmt|;
comment|/**       * @see OfficeOpenXMLExtended#COMMENTS       */
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
block|}
argument_list|)
decl_stmt|;
comment|/**      * Embedded resource type property      */
name|Property
name|EMBEDDED_RESOURCE_TYPE
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
name|EMBEDDED_RESOURCE_TYPE_KEY
argument_list|,
name|EmbeddedResourceType
operator|.
name|ATTACHMENT
operator|.
name|toString
argument_list|()
argument_list|,
name|EmbeddedResourceType
operator|.
name|INLINE
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Property
name|HAS_SIGNATURE
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
literal|"hasSignature"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

