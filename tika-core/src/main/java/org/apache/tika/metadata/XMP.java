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

begin_interface
specifier|public
interface|interface
name|XMP
block|{
name|String
name|NAMESPACE_URI
init|=
literal|"http://ns.adobe.com/xap/1.0/"
decl_stmt|;
name|String
name|PREFIX
init|=
literal|"xmp"
decl_stmt|;
comment|/** The xmp prefix followed by the colon delimiter */
name|String
name|PREFIX_
init|=
name|PREFIX
operator|+
name|TikaCoreProperties
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
comment|/**      * The date and time the resource was created. For a digital file, this need not      * match a file-system creation time. For a freshly created resource, it should      * be close to that time, modulo the time taken to write the file. Later file      * transfer, copying, and so on, can make the file-system time arbitrarily different.      */
name|Property
name|CREATE_DATE
init|=
name|Property
operator|.
name|externalDate
argument_list|(
name|PREFIX_
operator|+
literal|"CreateDate"
argument_list|)
decl_stmt|;
comment|/**      * The name of the first known tool used to create the resource.      */
name|Property
name|CREATOR_TOOL
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX_
operator|+
literal|"CreatorTool"
argument_list|)
decl_stmt|;
comment|/**      * An unordered array of text strings that unambiguously identify the resource      * within a given context. An array item may be qualified with xmpidq:Scheme      * (see 8.7, “xmpidq namespace”) to denote the formal identification system to      * which that identifier conforms.      */
name|Property
name|IDENTIFIER
init|=
name|Property
operator|.
name|externalTextBag
argument_list|(
name|PREFIX_
operator|+
literal|"Identifier"
argument_list|)
decl_stmt|;
comment|/**      * A word or short phrase that identifies a resource as a member of a userdefined collection.      */
name|Property
name|LABEL
init|=
name|Property
operator|.
name|externalDate
argument_list|(
name|PREFIX_
operator|+
literal|"Label"
argument_list|)
decl_stmt|;
comment|/**      * The date and time that any metadata for this resource was last changed. It      * should be the same as or more recent than xmp:ModifyDate      */
name|Property
name|METADATA_DATE
init|=
name|Property
operator|.
name|externalDate
argument_list|(
name|PREFIX_
operator|+
literal|"MetadataDate"
argument_list|)
decl_stmt|;
comment|/**      * The date and time the resource was last modified.      */
name|Property
name|MODIFY_DATE
init|=
name|Property
operator|.
name|externalDate
argument_list|(
name|PREFIX_
operator|+
literal|"ModifyDate"
argument_list|)
decl_stmt|;
comment|/**      * A user-assigned rating for this file. The value shall be -1 or in the range      * [0..5], where -1 indicates “rejected” and 0 indicates “unrated”. If xmp:Rating      * is not present, a value of 0 should be assumed.      */
name|Property
name|RATING
init|=
name|Property
operator|.
name|externalReal
argument_list|(
name|PREFIX_
operator|+
literal|"Rating"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

