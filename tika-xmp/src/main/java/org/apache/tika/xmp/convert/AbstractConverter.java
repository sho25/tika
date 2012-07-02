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
name|com
operator|.
name|adobe
operator|.
name|xmp
operator|.
name|XMPConst
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
name|XMPUtils
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
comment|/**  * Base class for Tika Metadata to XMP converter which provides some needed  * common functionality.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractConverter
implements|implements
name|ITikaToXMPConverter
block|{
specifier|private
name|Metadata
name|metadata
decl_stmt|;
specifier|protected
name|XMPMeta
name|meta
decl_stmt|;
specifier|abstract
specifier|public
name|XMPMeta
name|process
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|XMPException
function_decl|;
comment|/** 	 * Every Converter has to provide information about namespaces 	 * that are used additionally to the core set of XMP namespaces. 	 *  	 * @return the additional namespace information 	 */
specifier|abstract
specifier|protected
name|Set
argument_list|<
name|Namespace
argument_list|>
name|getAdditionalNamespaces
parameter_list|()
function_decl|;
specifier|public
name|AbstractConverter
parameter_list|()
throws|throws
name|TikaException
block|{
name|meta
operator|=
name|XMPMetaFactory
operator|.
name|create
argument_list|()
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|registerNamespaces
argument_list|(
name|getAdditionalNamespaces
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
specifier|public
name|XMPMeta
name|getXMPMeta
parameter_list|()
block|{
return|return
name|meta
return|;
block|}
comment|// --- utility methods used by sub-classes ---
comment|/**  	 * Registers a number<code>Namespace</code> information with XMPCore. 	 * Any already registered namespace is not registered again. 	 *  	 * @param namespaces the list of namespaces to be registered 	 * @throws TikaException in case a namespace oculd not be registered 	 */
specifier|protected
name|void
name|registerNamespaces
parameter_list|(
name|Set
argument_list|<
name|Namespace
argument_list|>
name|namespaces
parameter_list|)
throws|throws
name|TikaException
block|{
name|XMPSchemaRegistry
name|registry
init|=
name|XMPMetaFactory
operator|.
name|getSchemaRegistry
argument_list|()
decl_stmt|;
for|for
control|(
name|Namespace
name|namespace
range|:
name|namespaces
control|)
block|{
comment|// Any already registered namespace is not registered again
try|try
block|{
name|registry
operator|.
name|registerNamespace
argument_list|(
name|namespace
operator|.
name|uri
argument_list|,
name|namespace
operator|.
name|prefix
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMPException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Namespace needed by converter could not be registiered with XMPCore"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** 	 * @see AbstractConverter#createProperty(String, String, String); 	 */
specifier|protected
name|void
name|createProperty
parameter_list|(
name|Property
name|metadataProperty
parameter_list|,
name|String
name|ns
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|XMPException
block|{
name|createProperty
argument_list|(
name|metadataProperty
operator|.
name|getName
argument_list|()
argument_list|,
name|ns
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates a simple property. 	 * @param tikaKey Key in the Tika metadata map 	 * @param ns namespace the property should be created in 	 * @param propertyName name of the property 	 * @throws XMPException if the property could not be created 	 */
specifier|protected
name|void
name|createProperty
parameter_list|(
name|String
name|tikaKey
parameter_list|,
name|String
name|ns
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|XMPException
block|{
name|String
name|value
init|=
name|metadata
operator|.
name|get
argument_list|(
name|tikaKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|meta
operator|.
name|setProperty
argument_list|(
name|ns
argument_list|,
name|propertyName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * @see AbstractConverter#createLangAltProperty(String, String, String); 	 */
specifier|protected
name|void
name|createLangAltProperty
parameter_list|(
name|Property
name|metadataProperty
parameter_list|,
name|String
name|ns
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|XMPException
block|{
name|createLangAltProperty
argument_list|(
name|metadataProperty
operator|.
name|getName
argument_list|()
argument_list|,
name|ns
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates a language alternative property in the x-default language 	 * @param tikaKey Key in the Tika metadata map 	 * @param ns namespace the property should be created in 	 * @param propertyName name of the property 	 * @throws XMPException if the property could not be created 	 */
specifier|protected
name|void
name|createLangAltProperty
parameter_list|(
name|String
name|tikaKey
parameter_list|,
name|String
name|ns
parameter_list|,
name|String
name|propertyName
parameter_list|)
throws|throws
name|XMPException
block|{
name|String
name|value
init|=
name|metadata
operator|.
name|get
argument_list|(
name|tikaKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|meta
operator|.
name|setLocalizedText
argument_list|(
name|ns
argument_list|,
name|propertyName
argument_list|,
literal|null
argument_list|,
name|XMPConst
operator|.
name|X_DEFAULT
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|createArrayProperty
parameter_list|(
name|Property
name|metadataProperty
parameter_list|,
name|String
name|nsDc
parameter_list|,
name|String
name|arrayProperty
parameter_list|,
name|int
name|arrayType
parameter_list|)
throws|throws
name|XMPException
block|{
name|createArrayProperty
argument_list|(
name|metadataProperty
operator|.
name|getName
argument_list|()
argument_list|,
name|nsDc
argument_list|,
name|arrayProperty
argument_list|,
name|arrayType
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates an array property from a list of values. 	 * @param tikaKey Key in the Tika metadata map 	 * @param ns namespace the property should be created in 	 * @param propertyName name of the property 	 * @param arrayType depicts which kind of array shall be created 	 * @throws XMPException if the property could not be created 	 */
specifier|protected
name|void
name|createArrayProperty
parameter_list|(
name|String
name|tikaKey
parameter_list|,
name|String
name|ns
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|int
name|arrayType
parameter_list|)
throws|throws
name|XMPException
block|{
name|String
index|[]
name|values
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|tikaKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|meta
operator|.
name|setProperty
argument_list|(
name|ns
argument_list|,
name|propertyName
argument_list|,
literal|null
argument_list|,
operator|new
name|PropertyOptions
argument_list|(
name|arrayType
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|meta
operator|.
name|appendArrayItem
argument_list|(
name|ns
argument_list|,
name|propertyName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|createCommaSeparatedArray
parameter_list|(
name|Property
name|metadataProperty
parameter_list|,
name|String
name|nsDc
parameter_list|,
name|String
name|arrayProperty
parameter_list|,
name|int
name|arrayType
parameter_list|)
throws|throws
name|XMPException
block|{
name|createCommaSeparatedArray
argument_list|(
name|metadataProperty
operator|.
name|getName
argument_list|()
argument_list|,
name|nsDc
argument_list|,
name|arrayProperty
argument_list|,
name|arrayType
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates an array property from a comma separated list. 	 * @param tikaKey Key in the Tika metadata map 	 * @param ns namespace the property should be created in 	 * @param propertyName name of the property 	 * @param arrayType depicts which kind of array shall be created 	 * @throws XMPException if the property could not be created 	 */
specifier|protected
name|void
name|createCommaSeparatedArray
parameter_list|(
name|String
name|tikaKey
parameter_list|,
name|String
name|ns
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|int
name|arrayType
parameter_list|)
throws|throws
name|XMPException
block|{
name|String
name|value
init|=
name|metadata
operator|.
name|get
argument_list|(
name|tikaKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|XMPUtils
operator|.
name|separateArrayItems
argument_list|(
name|meta
argument_list|,
name|ns
argument_list|,
name|propertyName
argument_list|,
name|value
argument_list|,
operator|new
name|PropertyOptions
argument_list|(
name|arrayType
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

