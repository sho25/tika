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
name|XMPMM
block|{
name|String
name|NAMESPACE_URI
init|=
literal|"http://ns.adobe.com/xap/1.0/mm/"
decl_stmt|;
name|String
name|PREFIX
init|=
literal|"xmpMM"
decl_stmt|;
comment|/** The xmpMM prefix followed by the colon delimiter */
name|String
name|PREFIX_
init|=
name|PREFIX
operator|+
literal|":"
decl_stmt|;
comment|/**      * A reference to the resource from which this one is derived.      * This should be a minimal reference, in which missing      * components can be assumed to be unchanged.      *       * TODO This property is of type RessourceRef which is a struct      */
comment|//    Property DERIVED_FROM = Property.externalText(PREFIX_ + "DerivedFrom");
comment|/**      * The common identifier for all versions and renditions of a resource.      */
name|Property
name|DOCUMENTID
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX_
operator|+
literal|"DocumentID"
argument_list|)
decl_stmt|;
comment|/**      * An identifier for a specific incarnation of a resource, updated      * each time a file is saved.      */
name|Property
name|INSTANCEID
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX_
operator|+
literal|"InstanceID"
argument_list|)
decl_stmt|;
comment|/**      * The common identifier for the original resource from which      * the current resource is derived. For example, if you save a      * resource to a different format, then save that one to another      * format, each save operation should generate a new      * xmpMM:DocumentID that uniquely identifies the resource in      * that format, but should retain the ID of the source file here.      */
name|Property
name|ORIGINAL_DOCUMENTID
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX_
operator|+
literal|"OriginalDocumentID"
argument_list|)
decl_stmt|;
comment|/**      * The rendition class name for this resource. This property      * should be absent or set to default for a resource that is not      * a derived rendition      */
name|Property
name|RENDITION_CLASS
init|=
name|Property
operator|.
name|externalOpenChoise
argument_list|(
name|PREFIX_
operator|+
literal|"RenditionClass"
argument_list|,
literal|"default"
argument_list|,
literal|"draft"
argument_list|,
literal|"low-res"
argument_list|,
literal|"proof"
argument_list|,
literal|"screen"
argument_list|,
literal|"thumbnail"
argument_list|)
decl_stmt|;
comment|/**      * Can be used to provide additional rendition parameters that      * are too complex or verbose to encode in xmpMM:RenditionClass      */
name|Property
name|RENDITION_PARAMS
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX_
operator|+
literal|"RenditionParams"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

