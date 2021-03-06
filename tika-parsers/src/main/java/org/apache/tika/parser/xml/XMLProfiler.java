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
name|xml
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|CloseShieldInputStream
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MediaType
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
name|parser
operator|.
name|AbstractParser
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
name|parser
operator|.
name|ParseContext
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
name|OfflineContentHandler
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
name|utils
operator|.
name|XMLReaderUtils
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
name|DefaultHandler
import|;
end_import

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  *<p>  *  * This parser enables profiling of XML.  It captures the root entity as well as  * entity uris/namespaces and entity local names in parallel arrays.  *</p>  *<p>  *  * This parser is not part of the default set of parsers and must be "turned on"  * via a tika config:  *  *&lt;properties&gt;  *&lt;parsers&gt;  *&lt;parser class="org.apache.tika.parser.DefaultParser"/&gt;  *&lt;parser class="org.apache.tika.parser.xml.XMLProfiler"/&gt;  *&lt;/parsers&gt;  *&lt;/properties&gt;  *</p>  *<p>  *     This was initially designed to profile xmp and xfa in PDFs.  Further  *     work would need to be done to extract other types of xml and/or  *     xmp in other file formats.  Please open a ticket.  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|XMLProfiler
extends|extends
name|AbstractParser
block|{
specifier|public
specifier|static
name|Property
name|ROOT_ENTITY
init|=
name|Property
operator|.
name|internalText
argument_list|(
literal|"xmlprofiler:root_entity"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|Property
name|ENTITY_URIS
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
literal|"xmlprofiler:entity_uris"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|Property
name|ENTITY_LOCAL_NAMES
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
literal|"xmlprofiler:entity_local_names"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"xml"
argument_list|)
argument_list|,
comment|//https://wwwimages2.adobe.com/content/dam/acom/en/devnet/xmp/pdfs/XMP%20SDK%20Release%20cc-2016-08/XMPSpecificationPart3.pdf
comment|//"If a MIME type is needed, use application/rdf+xml."
name|MediaType
operator|.
name|application
argument_list|(
literal|"rdf+xml"
argument_list|)
argument_list|,
comment|//xmp
comment|//xfa: https://en.wikipedia.org/wiki/XFA
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.adobe.xdp+xml"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|XMLReaderUtils
operator|.
name|parseSAX
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
operator|new
name|OfflineContentHandler
argument_list|(
operator|new
name|XMLProfileHandler
argument_list|(
name|metadata
argument_list|)
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|XMLProfileHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
name|int
name|starts
init|=
literal|0
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|>
name|entities
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|XMLProfileHandler
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
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
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
if|if
condition|(
name|starts
operator|==
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|ROOT_ENTITY
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|localNames
init|=
name|entities
operator|.
name|get
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|localNames
operator|==
literal|null
condition|)
block|{
name|localNames
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|entities
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|localNames
argument_list|)
expr_stmt|;
block|}
name|localNames
operator|.
name|add
argument_list|(
name|localName
argument_list|)
expr_stmt|;
name|starts
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|String
index|[]
name|uris
init|=
operator|new
name|String
index|[
name|entities
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|String
index|[]
name|localNames
init|=
operator|new
name|String
index|[
name|entities
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|>
name|e
range|:
name|entities
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|uris
index|[
name|i
index|]
operator|=
name|e
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|localNames
index|[
name|i
index|]
operator|=
name|joinWith
argument_list|(
literal|" "
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|metadata
operator|.
name|set
argument_list|(
name|ENTITY_URIS
argument_list|,
name|uris
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENTITY_LOCAL_NAMES
argument_list|,
name|localNames
argument_list|)
expr_stmt|;
block|}
specifier|static
name|String
name|joinWith
parameter_list|(
name|String
name|delimiter
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|strings
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

