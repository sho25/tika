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
comment|/**  * XMP Rights management schema.   *   * A collection of property constants for the   * rights management properties defined in the XMP   * standard.  *   * @since Apache Tika 1.2  * @see<a href="http://partners.adobe.com/public/developer/en/xmp/sdk/XMPspecification.pdf">XMP Photoshop</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|XMPRights
block|{
name|String
name|NAMESPACE_URI_XMP_RIGHTS
init|=
literal|"http://ns.adobe.com/xap/1.0/rights/"
decl_stmt|;
name|String
name|PREFIX_XMP_RIGHTS
init|=
literal|"xmpRights"
decl_stmt|;
comment|/**      * A Web URL for a rights management certificate.      */
name|Property
name|CERTIFICATE
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_XMP_RIGHTS
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Certificate"
argument_list|)
decl_stmt|;
comment|/**      * When true, indicates that this is a rights-managed resource. When      * false, indicates that this is a public-domain resource. Omit if the      * state is unknown.      */
name|Property
name|MARKED
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|PREFIX_XMP_RIGHTS
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Marked"
argument_list|)
decl_stmt|;
comment|/**      * A list of legal owners of the resource.      */
name|Property
name|OWNER
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
name|PREFIX_XMP_RIGHTS
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Owner"
argument_list|)
decl_stmt|;
comment|/**      * A word or short phrase that identifies a resource as a member of a userdefined collection.      * TODO This is actually a language alternative property      */
name|Property
name|USAGE_TERMS
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_XMP_RIGHTS
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"UsageTerms"
argument_list|)
decl_stmt|;
comment|/**      * A Web URL for a statement of the ownership and usage rights for this resource.      */
name|Property
name|WEB_STATEMENT
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|PREFIX_XMP_RIGHTS
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"WebStatement"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

