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
comment|/**  * Content handler decorator that simplifies the task of producing XMP output.  *  * @since Apache Tika 1.0  */
end_comment

begin_class
specifier|public
class|class
name|XMPContentHandler
extends|extends
name|SafeContentHandler
block|{
comment|/**      * The RDF namespace URI      */
specifier|public
specifier|static
specifier|final
name|String
name|RDF
init|=
literal|"http://www.w3.org/1999/02/22-rdf-syntax-ns#"
decl_stmt|;
comment|/**      * The XMP namespace URI      */
specifier|public
specifier|static
specifier|final
name|String
name|XMP
init|=
literal|"http://ns.adobe.com/xap/1.0/"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Attributes
name|EMPTY_ATTRIBUTES
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
specifier|public
name|XMPContentHandler
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
comment|/**      * Starts an XMP document by setting up the namespace mappings and      * writing out the following header:      *<pre>      *&lt;rdf:RDF&gt;      *</pre>      */
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|startPrefixMapping
argument_list|(
literal|"rdf"
argument_list|,
name|RDF
argument_list|)
expr_stmt|;
name|startPrefixMapping
argument_list|(
literal|"xmp"
argument_list|,
name|XMP
argument_list|)
expr_stmt|;
name|startElement
argument_list|(
name|RDF
argument_list|,
literal|"RDF"
argument_list|,
literal|"rdf:RDF"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
block|}
comment|/**      * Ends the XMP document by writing the following footer and      * clearing the namespace mappings:      *<pre>      *&lt;/rdf:RDF&gt;      *</pre>      */
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|endElement
argument_list|(
name|RDF
argument_list|,
literal|"RDF"
argument_list|,
literal|"rdf:RDF"
argument_list|)
expr_stmt|;
name|endPrefixMapping
argument_list|(
literal|"xmp"
argument_list|)
expr_stmt|;
name|endPrefixMapping
argument_list|(
literal|"rdf"
argument_list|)
expr_stmt|;
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|//------------------------------------------< public convenience methods>
specifier|private
name|String
name|prefix
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|uri
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|startDescription
parameter_list|(
name|String
name|about
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|AttributesImpl
name|attributes
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
name|RDF
argument_list|,
literal|"about"
argument_list|,
literal|"rdf:about"
argument_list|,
literal|"CDATA"
argument_list|,
name|about
argument_list|)
expr_stmt|;
name|startElement
argument_list|(
name|RDF
argument_list|,
literal|"Description"
argument_list|,
literal|"rdf:Description"
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endDescription
parameter_list|()
throws|throws
name|SAXException
block|{
name|endElement
argument_list|(
name|RDF
argument_list|,
literal|"Description"
argument_list|,
literal|"rdf:Description"
argument_list|)
expr_stmt|;
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|property
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
name|String
name|qname
init|=
name|prefix
operator|+
literal|":"
operator|+
name|name
decl_stmt|;
name|startElement
argument_list|(
name|uri
argument_list|,
name|name
argument_list|,
name|qname
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|characters
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|endElement
argument_list|(
name|uri
argument_list|,
name|name
argument_list|,
name|qname
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|metadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SAXException
block|{
name|description
argument_list|(
name|metadata
argument_list|,
literal|"xmp"
argument_list|,
name|XMP
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"dc"
argument_list|,
literal|"http://purl.org/dc/elements/1.1/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"xmpTPg"
argument_list|,
literal|"http://ns.adobe.com/xap/1.0/t/pg/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"xmpRigths"
argument_list|,
literal|"http://ns.adobe.com/xap/1.0/rights/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"xmpMM"
argument_list|,
literal|"http://ns.adobe.com/xap/1.0/mm/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"xmpidq"
argument_list|,
literal|"http://ns.adobe.com/xmp/identifier/qual/1.0/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"xmpBJ"
argument_list|,
literal|"http://ns.adobe.com/xap/1.0/bj/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"xmpDM"
argument_list|,
literal|"http://ns.adobe.com/xmp/1.0/DynamicMedia/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"pdf"
argument_list|,
literal|"http://ns.adobe.com/pdf/1.3/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"photoshop"
argument_list|,
literal|"s http://ns.adobe.com/photoshop/1.0/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"crs"
argument_list|,
literal|"http://ns.adobe.com/camera-raw-settings/1.0/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"tiff"
argument_list|,
literal|"http://ns.adobe.com/tiff/1.0/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"exif"
argument_list|,
literal|"http://ns.adobe.com/exif/1.0/"
argument_list|)
expr_stmt|;
name|description
argument_list|(
name|metadata
argument_list|,
literal|"aux"
argument_list|,
literal|"http://ns.adobe.com/exif/1.0/aux/"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|description
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Property
name|property
range|:
name|Property
operator|.
name|getProperties
argument_list|(
name|prefix
argument_list|)
control|)
block|{
name|String
name|value
init|=
name|metadata
operator|.
name|get
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|count
operator|++
operator|==
literal|0
condition|)
block|{
name|startDescription
argument_list|(
literal|""
argument_list|,
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
name|property
argument_list|(
name|property
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|endDescription
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

