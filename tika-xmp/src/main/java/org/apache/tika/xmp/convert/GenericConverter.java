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
name|xmp
operator|.
name|convert
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|exception
operator|.
name|TikaException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|DublinCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|XMPRights
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Property
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPMeta
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPMetaFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPSchemaRegistry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|options
operator|.
name|PropertyOptions
import|;
end_import

begin_comment
comment|/**  * Trys to convert as much of the properties in the<code>Metadata</code> map  * to XMP namespaces. only those properties will be cnverted where the name contains   * a prefix and this prefix correlates with a "known" prefix for a standard namespace.  * For example "dc:title" would be mapped to the "title" property in the DublinCore namespace.  */
end_comment

begin_class
specifier|public
class|class
name|GenericConverter
extends|extends
name|AbstractConverter
block|{
specifier|public
name|GenericConverter
parameter_list|()
throws|throws
name|TikaException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|XMPMeta
name|process
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|XMPException
block|{
name|setMetadata
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|XMPSchemaRegistry
name|registry
init|=
name|XMPMetaFactory
operator|.
name|getSchemaRegistry
argument_list|()
decl_stmt|;
name|String
index|[]
name|keys
init|=
name|metadata
operator|.
name|names
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|String
index|[]
name|keyParts
init|=
name|key
operator|.
name|split
argument_list|(
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyParts
operator|.
name|length
operator|>
literal|0
operator|&&
name|keyParts
operator|.
name|length
operator|<=
literal|2
condition|)
block|{
name|String
name|uri
init|=
name|registry
operator|.
name|getNamespaceURI
argument_list|(
name|keyParts
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
comment|// Tika properties where the type differs from the XMP specification
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|DublinCore
operator|.
name|TITLE
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|key
operator|.
name|equals
argument_list|(
name|DublinCore
operator|.
name|DESCRIPTION
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|key
operator|.
name|equals
argument_list|(
name|XMPRights
operator|.
name|USAGE_TERMS
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|createLangAltProperty
argument_list|(
name|key
argument_list|,
name|uri
argument_list|,
name|keyParts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|DublinCore
operator|.
name|CREATOR
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|createArrayProperty
argument_list|(
name|key
argument_list|,
name|uri
argument_list|,
name|keyParts
index|[
literal|1
index|]
argument_list|,
name|PropertyOptions
operator|.
name|ARRAY_ORDERED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|PropertyType
name|type
init|=
name|Property
operator|.
name|getPropertyType
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SIMPLE
case|:
name|createProperty
argument_list|(
name|key
argument_list|,
name|uri
argument_list|,
name|keyParts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|BAG
case|:
name|createArrayProperty
argument_list|(
name|key
argument_list|,
name|uri
argument_list|,
name|keyParts
index|[
literal|1
index|]
argument_list|,
name|PropertyOptions
operator|.
name|ARRAY
argument_list|)
expr_stmt|;
break|break;
case|case
name|SEQ
case|:
name|createArrayProperty
argument_list|(
name|key
argument_list|,
name|uri
argument_list|,
name|keyParts
index|[
literal|1
index|]
argument_list|,
name|PropertyOptions
operator|.
name|ARRAY_ORDERED
argument_list|)
expr_stmt|;
break|break;
case|case
name|ALT
case|:
name|createArrayProperty
argument_list|(
name|key
argument_list|,
name|uri
argument_list|,
name|keyParts
index|[
literal|1
index|]
argument_list|,
name|PropertyOptions
operator|.
name|ARRAY_ALTERNATE
argument_list|)
expr_stmt|;
break|break;
comment|// TODO Add support for structs and lang-alts, but those types are currently not used in Tika
block|}
block|}
block|}
block|}
block|}
comment|// ignore keys that are not qualified
block|}
return|return
name|getXMPMeta
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Namespace
argument_list|>
name|getAdditionalNamespaces
parameter_list|()
block|{
comment|// no additional namespaces needed
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Namespace
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

