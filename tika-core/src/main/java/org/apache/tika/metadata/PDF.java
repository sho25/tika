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
comment|/**  * PDF properties collection.  *  * @since Apache Tika 1.14  */
end_comment

begin_interface
specifier|public
interface|interface
name|PDF
block|{
name|String
name|PDF_PREFIX
init|=
literal|"pdf"
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
name|String
name|PDFA_PREFIX
init|=
literal|"pdfa"
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
name|String
name|PDFAID_PREFIX
init|=
literal|"pdfaid"
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
name|String
name|PDF_PREFLIGHT_PREFIX
init|=
literal|"pdf-preflight"
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
comment|/**      * Prefix to be used for properties that record what was stored      * in the docinfo section (as opposed to XMP)      */
name|String
name|PDF_DOC_INFO_PREFIX
init|=
name|PDF_PREFIX
operator|+
literal|"docinfo"
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
name|String
name|PDF_DOC_INFO_CUSTOM_PREFIX
init|=
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"custom"
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
name|Property
name|DOC_INFO_CREATED
init|=
name|Property
operator|.
name|internalDate
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"created"
argument_list|)
decl_stmt|;
name|Property
name|DOC_INFO_CREATOR
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"creator"
argument_list|)
decl_stmt|;
name|Property
name|DOC_INFO_CREATOR_TOOL
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"creator_tool"
argument_list|)
decl_stmt|;
name|Property
name|DOC_INFO_MODIFICATION_DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"modified"
argument_list|)
decl_stmt|;
name|Property
name|DOC_INFO_KEY_WORDS
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"keywords"
argument_list|)
decl_stmt|;
name|Property
name|DOC_INFO_PRODUCER
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"producer"
argument_list|)
decl_stmt|;
name|Property
name|DOC_INFO_SUBJECT
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"subject"
argument_list|)
decl_stmt|;
name|Property
name|DOC_INFO_TITLE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"title"
argument_list|)
decl_stmt|;
name|Property
name|DOC_INFO_TRAPPED
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_DOC_INFO_PREFIX
operator|+
literal|"trapped"
argument_list|)
decl_stmt|;
name|Property
name|PDF_VERSION
init|=
name|Property
operator|.
name|internalRational
argument_list|(
name|PDF_PREFIX
operator|+
literal|"PDFVersion"
argument_list|)
decl_stmt|;
name|Property
name|PDFA_VERSION
init|=
name|Property
operator|.
name|internalRational
argument_list|(
name|PDFA_PREFIX
operator|+
literal|"PDFVersion"
argument_list|)
decl_stmt|;
name|Property
name|PDF_EXTENSION_VERSION
init|=
name|Property
operator|.
name|internalRational
argument_list|(
name|PDF_PREFIX
operator|+
literal|"PDFExtensionVersion"
argument_list|)
decl_stmt|;
name|Property
name|PDFAID_CONFORMANCE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDFAID_PREFIX
operator|+
literal|"conformance"
argument_list|)
decl_stmt|;
name|Property
name|PDFAID_PART
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDFAID_PREFIX
operator|+
literal|"part"
argument_list|)
decl_stmt|;
name|Property
name|IS_ENCRYPTED
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PDF_PREFIX
operator|+
literal|"encrypted"
argument_list|)
decl_stmt|;
comment|/**      * This specifies where an action or destination would be found/triggered      * in the document: on document open, before close, etc.      */
name|Property
name|ACTION_TRIGGER
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_PREFIX
operator|+
literal|"actionTrigger"
argument_list|)
decl_stmt|;
name|Property
name|CHARACTERS_PER_PAGE
init|=
name|Property
operator|.
name|internalIntegerSequence
argument_list|(
name|PDF_PREFIX
operator|+
literal|"charsPerPage"
argument_list|)
decl_stmt|;
name|Property
name|UNMAPPED_UNICODE_CHARS_PER_PAGE
init|=
name|Property
operator|.
name|internalIntegerSequence
argument_list|(
name|PDF_PREFIX
operator|+
literal|"unmappedUnicodeCharsPerPage"
argument_list|)
decl_stmt|;
comment|/**      * Has XFA      */
name|Property
name|HAS_XFA
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PDF_PREFIX
operator|+
literal|"hasXFA"
argument_list|)
decl_stmt|;
comment|/**      * Has XMP, whether or not it is valid      */
name|Property
name|HAS_XMP
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PDF_PREFIX
operator|+
literal|"hasXMP"
argument_list|)
decl_stmt|;
comment|/**      * If xmp is extracted by, e.g. the XMLProfiler, where did it come from?      * The document document catalog or a specific page...or?      */
name|Property
name|XMP_LOCATION
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_PREFIX
operator|+
literal|"xmpLocation"
argument_list|)
decl_stmt|;
comment|/**      * Has> 0 AcroForm fields      */
name|Property
name|HAS_ACROFORM_FIELDS
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PDF_PREFIX
operator|+
literal|"hasAcroFormFields"
argument_list|)
decl_stmt|;
name|Property
name|HAS_MARKED_CONTENT
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PDF_PREFIX
operator|+
literal|"hasMarkedContent"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_IS_VALID
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"isValid"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_PARSE_EXCEPTION
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"parseException"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_VALIDATION_ERRORS
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"validationErrors"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_SPECIFICATION
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"specification"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_TRAILER_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"trailerCount"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_XREF_TYPE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"xrefType"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_ICC_PROFILE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"iccProfile"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_IS_LINEARIZED
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"isLinearized"
argument_list|)
decl_stmt|;
name|Property
name|PREFLIGHT_INCREMENTAL_UPDATES
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PDF_PREFLIGHT_PREFIX
operator|+
literal|"hasIncrementalUpdates"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

