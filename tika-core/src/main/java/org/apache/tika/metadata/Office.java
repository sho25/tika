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
comment|/**  * Office Document properties collection. These properties apply to   *  Office / Productivity Documents of all forms, including (but not limited   *  to) MS Office and OpenDocument formats.  * This is a logical collection of properties, which may be drawn from a  *  few different external definitions.  *   * Note that some of the legacy properties from the {@link MSOffice}  *  collection still need to be migrated over  */
end_comment

begin_interface
specifier|public
interface|interface
name|Office
block|{
comment|// These are taken from the OpenDocumentFormat specification
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI_DOC_META
init|=
literal|"urn:oasis:names:tc:opendocument:xmlns:meta:1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_DOC_META
init|=
literal|"doc-meta"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_DOC_META_STATS
init|=
literal|"doc-meta-stats"
decl_stmt|;
comment|/**      * For user defined metadata entries in the document,     *  what prefix should be attached to the key names.     * eg<meta:user-defined meta:name="Info1">Text1</meta:user-defined> becomes custom:Info1=Text1     */
specifier|public
specifier|static
specifier|final
name|String
name|USER_DEFINED_METADATA_NAME_PREFIX
init|=
literal|"custom:"
decl_stmt|;
comment|/**     * Keywords pertaining to a document.      */
name|Property
name|KEYWORDS
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
name|PREFIX_DOC_META
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"keyword"
argument_list|)
decl_stmt|;
comment|/**     * Name of the initial creator/author of a document     */
name|Property
name|INITIAL_AUTHOR
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_DOC_META
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"initial-author"
argument_list|)
decl_stmt|;
comment|/**     * Name of the last (most recent) author of a document     */
name|Property
name|LAST_AUTHOR
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_DOC_META
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"last-author"
argument_list|)
decl_stmt|;
comment|/** When was the document created? */
name|Property
name|CREATION_DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
name|PREFIX_DOC_META
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"creation-date"
argument_list|)
decl_stmt|;
comment|/** When was the document last saved? */
name|Property
name|SAVE_DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
name|PREFIX_DOC_META
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"save-date"
argument_list|)
decl_stmt|;
comment|/** When was the document last printed? */
name|Property
name|PRINT_DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
name|PREFIX_DOC_META
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"print-date"
argument_list|)
decl_stmt|;
comment|/** The number of Slides are there in the (presentation) document */
name|Property
name|SLIDE_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"slide-count"
argument_list|)
decl_stmt|;
comment|/** The number of Pages are there in the (paged) document */
name|Property
name|PAGE_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"page-count"
argument_list|)
decl_stmt|;
comment|/** The number of individual Paragraphs in the document */
name|Property
name|PARAGRAPH_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"paragraph-count"
argument_list|)
decl_stmt|;
comment|/** The number of lines in the document */
name|Property
name|LINE_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"line-count"
argument_list|)
decl_stmt|;
comment|/** The number of Words in the document */
name|Property
name|WORD_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"word-count"
argument_list|)
decl_stmt|;
comment|/** The number of Characters in the document */
name|Property
name|CHARACTER_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"character-count"
argument_list|)
decl_stmt|;
comment|/** The number of Characters in the document, including spaces */
name|Property
name|CHARACTER_COUNT_WITH_SPACES
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"character-count-with-spaces"
argument_list|)
decl_stmt|;
comment|/** The number of Tables in the document */
name|Property
name|TABLE_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"table-count"
argument_list|)
decl_stmt|;
comment|/** The number of Images in the document */
name|Property
name|IMAGE_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"image-count"
argument_list|)
decl_stmt|;
comment|/**       * The number of Objects in the document. These are typically non-Image resources       * embedded in the document, such as other documents or non-Image media.       */
name|Property
name|OBJECT_COUNT
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|PREFIX_DOC_META_STATS
operator|+
name|Metadata
operator|.
name|PREFIX_DELIMITER
operator|+
literal|"object-count"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

