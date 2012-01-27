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
comment|/**  * A collection of Dublin Core metadata names.  *   * TODO Prefix these keys with the dc: prefix  *   * @see<a href="http://dublincore.org">dublincore.org</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|DublinCore
block|{
comment|/**      * Typically, Format may include the media-type or dimensions of the      * resource. Format may be used to determine the software, hardware or      * other equipment needed to display or operate the resource. Examples      * of dimensions include size and duration. Recommended best practice is      * to select a value from a controlled vocabulary (for example, the list      * of Internet Media Types [MIME] defining computer media formats).      */
name|String
name|FORMAT
init|=
literal|"format"
decl_stmt|;
comment|/**      * Recommended best practice is to identify the resource by means of      * a string or number conforming to a formal identification system.      * Example formal identification systems include the Uniform Resource      * Identifier (URI) (including the Uniform Resource Locator (URL)),      * the Digital Object Identifier (DOI) and the International Standard      * Book Number (ISBN).      */
name|String
name|IDENTIFIER
init|=
literal|"identifier"
decl_stmt|;
comment|/**      * Date on which the resource was changed.      * TODO Make me a Date Property      */
name|String
name|MODIFIED
init|=
literal|"modified"
decl_stmt|;
comment|/**      * An entity responsible for making contributions to the content of the      * resource. Examples of a Contributor include a person, an organisation,      * or a service. Typically, the name of a Contributor should be used to      * indicate the entity.      */
name|String
name|CONTRIBUTOR
init|=
literal|"contributor"
decl_stmt|;
comment|/**      * The extent or scope of the content of the resource. Coverage will      * typically include spatial location (a place name or geographic      * coordinates), temporal period (a period label, date, or date range)      * or jurisdiction (such as a named administrative entity). Recommended      * best practice is to select a value from a controlled vocabulary (for      * example, the Thesaurus of Geographic Names [TGN]) and that, where      * appropriate, named places or time periods be used in preference to      * numeric identifiers such as sets of coordinates or date ranges.      */
name|String
name|COVERAGE
init|=
literal|"coverage"
decl_stmt|;
comment|/**      * An entity primarily responsible for making the content of the resource.      * Examples of a Creator include a person, an organisation, or a service.      * Typically, the name of a Creator should be used to indicate the entity.      */
name|String
name|CREATOR
init|=
literal|"creator"
decl_stmt|;
comment|/**      * A date associated with an event in the life cycle of the resource.      * Typically, Date will be associated with the creation or availability of      * the resource. Recommended best practice for encoding the date value is      * defined in a profile of ISO 8601 [W3CDTF] and follows the YYYY-MM-DD      * format.      */
name|Property
name|DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
literal|"date"
argument_list|)
decl_stmt|;
comment|/**      * An account of the content of the resource. Description may include      * but is not limited to: an abstract, table of contents, reference to      * a graphical representation of content or a free-text account of      * the content.      */
name|String
name|DESCRIPTION
init|=
literal|"description"
decl_stmt|;
comment|/**      * A language of the intellectual content of the resource. Recommended      * best practice is to use RFC 3066 [RFC3066], which, in conjunction      * with ISO 639 [ISO639], defines two- and three-letter primary language      * tags with optional subtags. Examples include "en" or "eng" for English,      * "akk" for Akkadian, and "en-GB" for English used in the United Kingdom.      */
name|String
name|LANGUAGE
init|=
literal|"language"
decl_stmt|;
comment|/**      * An entity responsible for making the resource available. Examples of      * a Publisher include a person, an organisation, or a service. Typically,      * the name of a Publisher should be used to indicate the entity.      */
name|String
name|PUBLISHER
init|=
literal|"publisher"
decl_stmt|;
comment|/**      * A reference to a related resource. Recommended best practice is to      * reference the resource by means of a string or number conforming to      * a formal identification system.      */
name|String
name|RELATION
init|=
literal|"relation"
decl_stmt|;
comment|/**      * Information about rights held in and over the resource. Typically,      * a Rights element will contain a rights management statement for      * the resource, or reference a service providing such information.      * Rights information often encompasses Intellectual Property Rights      * (IPR), Copyright, and various Property Rights. If the Rights element      * is absent, no assumptions can be made about the status of these and      * other rights with respect to the resource.      */
name|String
name|RIGHTS
init|=
literal|"rights"
decl_stmt|;
comment|/**      * A reference to a resource from which the present resource is derived.      * The present resource may be derived from the Source resource in whole      * or in part. Recommended best practice is to reference the resource by      * means of a string or number conforming to a formal identification      * system.      */
name|String
name|SOURCE
init|=
literal|"source"
decl_stmt|;
comment|/**      * The topic of the content of the resource. Typically, a Subject will      * be expressed as keywords, key phrases or classification codes that      * describe a topic of the resource. Recommended best practice is to      * select a value from a controlled vocabulary or formal classification      * scheme.      */
name|String
name|SUBJECT
init|=
literal|"subject"
decl_stmt|;
comment|/**      * A name given to the resource. Typically, a Title will be a name by      * which the resource is formally known.      */
name|String
name|TITLE
init|=
literal|"title"
decl_stmt|;
comment|/**      * The nature or genre of the content of the resource. Type includes terms      * describing general categories, functions, genres, or aggregation levels      * for content. Recommended best practice is to select a value from a      * controlled vocabulary (for example, the DCMI Type Vocabulary      * [DCMITYPE]). To describe the physical or digital manifestation of      * the resource, use the Format element.      */
name|String
name|TYPE
init|=
literal|"type"
decl_stmt|;
block|}
end_interface

end_unit

