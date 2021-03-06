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
name|parser
operator|.
name|odf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|sax
operator|.
name|ContentHandlerDecorator
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
name|InputSource
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
comment|/**  * Content handler decorator that:<ul>  *<li>Maps old OpenOffice 1.0 Namespaces to the OpenDocument ones</li>  *<li>Returns a fake DTD when parser requests OpenOffice DTD</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|NSNormalizerContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
specifier|private
specifier|static
specifier|final
name|String
name|OLD_NS
init|=
literal|"http://openoffice.org/2000/"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NEW_NS
init|=
literal|"urn:oasis:names:tc:opendocument:xmlns:"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DTD_PUBLIC_ID
init|=
literal|"-//OpenOffice.org//DTD OfficeDocument 1.0//EN"
decl_stmt|;
specifier|public
name|NSNormalizerContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|mapOldNS
parameter_list|(
name|String
name|ns
parameter_list|)
block|{
if|if
condition|(
name|ns
operator|!=
literal|null
operator|&&
name|ns
operator|.
name|startsWith
argument_list|(
name|OLD_NS
argument_list|)
condition|)
block|{
return|return
name|NEW_NS
operator|+
name|ns
operator|.
name|substring
argument_list|(
name|OLD_NS
operator|.
name|length
argument_list|()
argument_list|)
operator|+
literal|":1.0"
return|;
block|}
else|else
block|{
return|return
name|ns
return|;
block|}
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
name|natts
operator|.
name|addAttribute
argument_list|(
name|mapOldNS
argument_list|(
name|atts
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|,
name|atts
operator|.
name|getQName
argument_list|(
name|i
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
name|super
operator|.
name|startElement
argument_list|(
name|mapOldNS
argument_list|(
name|namespaceURI
argument_list|)
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|endElement
argument_list|(
name|mapOldNS
argument_list|(
name|namespaceURI
argument_list|)
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|mapOldNS
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * do not load any DTDs (may be requested by parser). Fake the DTD by      * returning a empty string as InputSource      */
annotation|@
name|Override
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
if|if
condition|(
operator|(
name|systemId
operator|!=
literal|null
operator|&&
name|systemId
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|".dtd"
argument_list|)
operator|)
operator|||
name|DTD_PUBLIC_ID
operator|.
name|equals
argument_list|(
name|publicId
argument_list|)
condition|)
block|{
return|return
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|resolveEntity
argument_list|(
name|publicId
argument_list|,
name|systemId
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

