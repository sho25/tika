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
name|microsoft
operator|.
name|ooxml
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|POIXMLDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|POIXMLTextExtractor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|exceptions
operator|.
name|InvalidFormatException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|PackagePart
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|PackagePartName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|PackageRelationship
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|PackageRelationshipCollection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|PackagingURIHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|TargetMode
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
name|io
operator|.
name|TikaInputStream
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
name|parser
operator|.
name|EmptyParser
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
name|parser
operator|.
name|Parser
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
name|EmbeddedContentHandler
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
name|XHTMLContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlbeans
operator|.
name|XmlException
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

begin_comment
comment|/**  * Base class for all Tika OOXML extractors.  *   * Tika extractors decorate POI extractors so that the parsed content of  * documents is returned as a sequence of XHTML SAX events. Subclasses must  * implement the buildXHTML method {@link #buildXHTML(XHTMLContentHandler)} that  * populates the {@link XHTMLContentHandler} object received as parameter.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractOOXMLExtractor
implements|implements
name|OOXMLExtractor
block|{
specifier|static
specifier|final
name|String
name|RELATION_AUDIO
init|=
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/audio"
decl_stmt|;
specifier|static
specifier|final
name|String
name|RELATION_IMAGE
init|=
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"
decl_stmt|;
specifier|static
specifier|final
name|String
name|RELATION_OLE_OBJECT
init|=
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject"
decl_stmt|;
specifier|static
specifier|final
name|String
name|RELATION_PACKAGE
init|=
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/package"
decl_stmt|;
specifier|protected
name|POIXMLTextExtractor
name|extractor
decl_stmt|;
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
specifier|public
name|AbstractOOXMLExtractor
parameter_list|(
name|POIXMLTextExtractor
name|extractor
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|extractor
operator|=
name|extractor
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * @see org.apache.tika.parser.microsoft.ooxml.OOXMLExtractor#getDocument()      */
specifier|public
name|POIXMLDocument
name|getDocument
parameter_list|()
block|{
return|return
name|extractor
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.tika.parser.microsoft.ooxml.OOXMLExtractor#getMetadataExtractor()      */
specifier|public
name|MetadataExtractor
name|getMetadataExtractor
parameter_list|()
block|{
return|return
operator|new
name|MetadataExtractor
argument_list|(
name|extractor
argument_list|,
name|type
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.tika.parser.microsoft.ooxml.OOXMLExtractor#getXHTML(org.xml.sax.ContentHandler,      *      org.apache.tika.metadata.Metadata)      */
specifier|public
name|void
name|getXHTML
parameter_list|(
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
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
throws|,
name|TikaException
block|{
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|buildXHTML
argument_list|(
name|xhtml
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
comment|// Now do any embedded parts
name|List
argument_list|<
name|PackagePart
argument_list|>
name|mainParts
init|=
name|getMainDocumentParts
argument_list|()
decl_stmt|;
for|for
control|(
name|PackagePart
name|part
range|:
name|mainParts
control|)
block|{
name|PackageRelationshipCollection
name|rels
decl_stmt|;
try|try
block|{
name|rels
operator|=
name|part
operator|.
name|getRelationships
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Corrupt OOXML file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
for|for
control|(
name|PackageRelationship
name|rel
range|:
name|rels
control|)
block|{
comment|// Is it an embedded type (not part of the document)
if|if
condition|(
name|rel
operator|.
name|getRelationshipType
argument_list|()
operator|.
name|equals
argument_list|(
name|RELATION_AUDIO
argument_list|)
operator|||
name|rel
operator|.
name|getRelationshipType
argument_list|()
operator|.
name|equals
argument_list|(
name|RELATION_IMAGE
argument_list|)
operator|||
name|rel
operator|.
name|getRelationshipType
argument_list|()
operator|.
name|equals
argument_list|(
name|RELATION_OLE_OBJECT
argument_list|)
operator|||
name|rel
operator|.
name|getRelationshipType
argument_list|()
operator|.
name|equals
argument_list|(
name|RELATION_PACKAGE
argument_list|)
condition|)
block|{
if|if
condition|(
name|rel
operator|.
name|getTargetMode
argument_list|()
operator|==
name|TargetMode
operator|.
name|INTERNAL
condition|)
block|{
name|PackagePartName
name|relName
decl_stmt|;
try|try
block|{
name|relName
operator|=
name|PackagingURIHelper
operator|.
name|createPartName
argument_list|(
name|rel
operator|.
name|getTargetURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Broken OOXML file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|PackagePart
name|relPart
init|=
name|rel
operator|.
name|getPackage
argument_list|()
operator|.
name|getPart
argument_list|(
name|relName
argument_list|)
decl_stmt|;
name|handleEmbedded
argument_list|(
name|rel
argument_list|,
name|relPart
argument_list|,
name|handler
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**      * Handles an embedded resource in the file      */
specifier|protected
name|void
name|handleEmbedded
parameter_list|(
name|PackageRelationship
name|rel
parameter_list|,
name|PackagePart
name|part
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
throws|,
name|TikaException
block|{
comment|// Get the name
name|String
name|name
init|=
name|rel
operator|.
name|getTargetURI
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Get the content type
name|String
name|type
init|=
name|part
operator|.
name|getContentType
argument_list|()
decl_stmt|;
comment|// Call the recursing handler
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|TIKA_MIME_FILE
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
name|context
operator|.
name|get
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|EmptyParser
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|part
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * Populates the {@link XHTMLContentHandler} object received as parameter.      */
specifier|protected
specifier|abstract
name|void
name|buildXHTML
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XmlException
throws|,
name|IOException
function_decl|;
comment|/**      * Return a list of the main parts of the document, used      *  when searching for embedded resources.      * This should be all the parts of the document that end      *  up with things embedded into them.      */
specifier|protected
specifier|abstract
name|List
argument_list|<
name|PackagePart
argument_list|>
name|getMainDocumentParts
parameter_list|()
throws|throws
name|TikaException
function_decl|;
block|}
end_class

end_unit

