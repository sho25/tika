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
name|sax
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * Content handler decorator that maps element<code>QName</code>s using  * a<code>Map</code>. Not mappable elements are not forwarded.  * Attributes may also be mapped (for each element different using  * a<code>Map</code> for attributes), not mappable attributes are not  * forwarded. The default is to not map any attributes and therefore do  * not forward any of them.  */
end_comment

begin_class
specifier|public
class|class
name|ElementMappingContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|QName
argument_list|,
name|TargetElement
argument_list|>
name|mappings
decl_stmt|;
specifier|public
name|ElementMappingContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Map
argument_list|<
name|QName
argument_list|,
name|TargetElement
argument_list|>
name|mappings
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|this
operator|.
name|mappings
operator|=
name|mappings
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
name|TargetElement
name|mapping
init|=
name|mappings
operator|.
name|get
argument_list|(
operator|new
name|QName
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapping
operator|!=
literal|null
condition|)
block|{
name|QName
name|tag
init|=
name|mapping
operator|.
name|getMappedTagName
argument_list|()
decl_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|tag
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|tag
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|getQNameAsString
argument_list|(
name|tag
argument_list|)
argument_list|,
name|mapping
operator|.
name|mapAttributes
argument_list|(
name|atts
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
name|TargetElement
name|mapping
init|=
name|mappings
operator|.
name|get
argument_list|(
operator|new
name|QName
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapping
operator|!=
literal|null
condition|)
block|{
name|QName
name|tag
init|=
name|mapping
operator|.
name|getMappedTagName
argument_list|()
decl_stmt|;
name|super
operator|.
name|endElement
argument_list|(
name|tag
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|tag
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|getQNameAsString
argument_list|(
name|tag
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
specifier|final
name|String
name|getQNameAsString
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|String
name|prefix
init|=
name|qname
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|prefix
operator|+
literal|":"
operator|+
name|qname
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|qname
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|TargetElement
block|{
comment|/**          * Creates an TargetElement, attributes of this element will          * be mapped as specified          */
specifier|public
name|TargetElement
parameter_list|(
name|QName
name|mappedTagName
parameter_list|,
name|Map
argument_list|<
name|QName
argument_list|,
name|QName
argument_list|>
name|attributesMapping
parameter_list|)
block|{
name|this
operator|.
name|mappedTagName
operator|=
name|mappedTagName
expr_stmt|;
name|this
operator|.
name|attributesMapping
operator|=
name|attributesMapping
expr_stmt|;
block|}
comment|/**          * A shortcut that automatically creates the QName object          */
specifier|public
name|TargetElement
parameter_list|(
name|String
name|mappedTagURI
parameter_list|,
name|String
name|mappedTagLocalName
parameter_list|,
name|Map
argument_list|<
name|QName
argument_list|,
name|QName
argument_list|>
name|attributesMapping
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|QName
argument_list|(
name|mappedTagURI
argument_list|,
name|mappedTagLocalName
argument_list|)
argument_list|,
name|attributesMapping
argument_list|)
expr_stmt|;
block|}
comment|/**          * Creates an TargetElement with no attributes, all attributes          * will be deleted from SAX stream          */
specifier|public
name|TargetElement
parameter_list|(
name|QName
name|mappedTagName
parameter_list|)
block|{
name|this
argument_list|(
name|mappedTagName
argument_list|,
name|Collections
operator|.
expr|<
name|QName
argument_list|,
name|QName
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** A shortcut that automatically creates the QName object */
specifier|public
name|TargetElement
parameter_list|(
name|String
name|mappedTagURI
parameter_list|,
name|String
name|mappedTagLocalName
parameter_list|)
block|{
name|this
argument_list|(
name|mappedTagURI
argument_list|,
name|mappedTagLocalName
argument_list|,
name|Collections
operator|.
expr|<
name|QName
argument_list|,
name|QName
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|QName
name|getMappedTagName
parameter_list|()
block|{
return|return
name|mappedTagName
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|QName
argument_list|>
name|getAttributesMapping
parameter_list|()
block|{
return|return
name|attributesMapping
return|;
block|}
specifier|public
name|Attributes
name|mapAttributes
parameter_list|(
specifier|final
name|Attributes
name|atts
parameter_list|)
block|{
name|AttributesImpl
name|natts
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|QName
name|name
init|=
name|attributesMapping
operator|.
name|get
argument_list|(
operator|new
name|QName
argument_list|(
name|atts
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
argument_list|,
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|natts
operator|.
name|addAttribute
argument_list|(
name|name
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|name
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|getQNameAsString
argument_list|(
name|name
argument_list|)
argument_list|,
name|atts
operator|.
name|getType
argument_list|(
name|i
argument_list|)
argument_list|,
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|natts
return|;
block|}
specifier|private
specifier|final
name|QName
name|mappedTagName
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|QName
argument_list|,
name|QName
argument_list|>
name|attributesMapping
decl_stmt|;
block|}
block|}
end_class

end_unit

