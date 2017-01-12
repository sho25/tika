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
name|ArrayList
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
name|exceptions
operator|.
name|OpenXML4JException
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
name|OPCPackage
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
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFNumbering
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
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFRelation
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
name|microsoft
operator|.
name|OfficeParserConfig
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
name|microsoft
operator|.
name|ooxml
operator|.
name|xwpf
operator|.
name|XWPFEventBasedWordExtractor
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
name|microsoft
operator|.
name|ooxml
operator|.
name|xwpf
operator|.
name|XWPFNumberingShim
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
name|microsoft
operator|.
name|ooxml
operator|.
name|xwpf
operator|.
name|XWPFStylesShim
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * This is an experimental, alternative extractor for docx files.  * This streams the main document content rather than loading the  * full document into memory.  *<p>  * This will be better for some use cases than the classic docx extractor; and,  * it will be worse for others.  *</p>  *  * @since 1.15  */
end_comment

begin_class
specifier|public
class|class
name|SXWPFWordExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
comment|//include all parts that might have embedded objects
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|MAIN_PART_RELATIONS
init|=
operator|new
name|String
index|[]
block|{
name|XWPFRelation
operator|.
name|HEADER
operator|.
name|getRelation
argument_list|()
block|,
name|XWPFRelation
operator|.
name|FOOTER
operator|.
name|getRelation
argument_list|()
block|,
name|XWPFRelation
operator|.
name|FOOTNOTE
operator|.
name|getRelation
argument_list|()
block|,
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/endnotes"
block|,
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/comments"
block|}
decl_stmt|;
comment|//a docx file should have one of these "main story" parts
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|MAIN_STORY_PART_RELATIONS
init|=
operator|new
name|String
index|[]
block|{
name|XWPFRelation
operator|.
name|DOCUMENT
operator|.
name|getContentType
argument_list|()
block|,
name|XWPFRelation
operator|.
name|MACRO_DOCUMENT
operator|.
name|getContentType
argument_list|()
block|,
name|XWPFRelation
operator|.
name|TEMPLATE
operator|.
name|getContentType
argument_list|()
block|,
name|XWPFRelation
operator|.
name|MACRO_TEMPLATE_DOCUMENT
operator|.
name|getContentType
argument_list|()
block|}
decl_stmt|;
specifier|private
specifier|final
name|OPCPackage
name|opcPackage
decl_stmt|;
specifier|private
specifier|final
name|ParseContext
name|context
decl_stmt|;
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|public
name|SXWPFWordExtractorDecorator
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|XWPFEventBasedWordExtractor
name|extractor
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|extractor
argument_list|)
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|opcPackage
operator|=
name|extractor
operator|.
name|getPackage
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
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
block|{
comment|//handle main document
name|List
argument_list|<
name|PackagePart
argument_list|>
name|pps
init|=
name|getStoryDocumentParts
argument_list|()
decl_stmt|;
if|if
condition|(
name|pps
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PackagePart
name|pp
range|:
name|pps
control|)
block|{
comment|//likely only one, but why not...
name|handleDocumentPart
argument_list|(
name|pp
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
comment|//handle glossary document
name|pps
operator|=
name|opcPackage
operator|.
name|getPartsByContentType
argument_list|(
name|XWPFRelation
operator|.
name|GLOSSARY_DOCUMENT
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|pps
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|pps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"glossary"
argument_list|)
expr_stmt|;
for|for
control|(
name|PackagePart
name|pp
range|:
name|pps
control|)
block|{
comment|//likely only one, but why not...
name|handleDocumentPart
argument_list|(
name|pp
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|handleDocumentPart
parameter_list|(
name|PackagePart
name|documentPart
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
comment|//load the numbering/list manager and styles from the main document part
name|XWPFNumbering
name|numbering
init|=
name|loadNumbering
argument_list|(
name|documentPart
argument_list|)
decl_stmt|;
name|XWPFListManager
name|listManager
init|=
operator|new
name|XWPFListManager
argument_list|(
name|numbering
argument_list|)
decl_stmt|;
name|XWPFStylesShim
name|styles
init|=
name|loadStyles
argument_list|(
name|documentPart
argument_list|)
decl_stmt|;
comment|//headers
try|try
block|{
name|PackageRelationshipCollection
name|headersPRC
init|=
name|documentPart
operator|.
name|getRelationshipsByType
argument_list|(
name|XWPFRelation
operator|.
name|HEADER
operator|.
name|getRelation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|headersPRC
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|headersPRC
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PackagePart
name|header
init|=
name|documentPart
operator|.
name|getRelatedPart
argument_list|(
name|headersPRC
operator|.
name|getRelationship
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|handlePart
argument_list|(
name|header
argument_list|,
name|styles
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
comment|//main document
name|handlePart
argument_list|(
name|documentPart
argument_list|,
name|styles
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|//for now, just dump other components at end
for|for
control|(
name|XWPFRelation
name|rel
range|:
operator|new
name|XWPFRelation
index|[]
block|{
name|XWPFRelation
operator|.
name|FOOTNOTE
block|,
name|XWPFRelation
operator|.
name|COMMENT
block|,
name|XWPFRelation
operator|.
name|FOOTER
block|,
name|XWPFRelation
operator|.
name|ENDNOTE
block|}
control|)
block|{
try|try
block|{
name|PackageRelationshipCollection
name|prc
init|=
name|documentPart
operator|.
name|getRelationshipsByType
argument_list|(
name|rel
operator|.
name|getRelation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|prc
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prc
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PackagePart
name|packagePart
init|=
name|documentPart
operator|.
name|getRelatedPart
argument_list|(
name|prc
operator|.
name|getRelationship
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|handlePart
argument_list|(
name|packagePart
argument_list|,
name|styles
argument_list|,
name|listManager
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
block|}
specifier|private
name|void
name|handlePart
parameter_list|(
name|PackagePart
name|packagePart
parameter_list|,
name|XWPFStylesShim
name|styles
parameter_list|,
name|XWPFListManager
name|listManager
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|linkedRelationships
init|=
name|loadLinkedRelationships
argument_list|(
name|packagePart
argument_list|,
literal|true
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|packagePart
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|context
operator|.
name|getSAXParser
argument_list|()
operator|.
name|parse
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
name|EmbeddedContentHandler
argument_list|(
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|,
name|styles
argument_list|,
name|listManager
argument_list|,
name|context
operator|.
name|get
argument_list|(
name|OfficeParserConfig
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|linkedRelationships
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
specifier|private
name|XWPFStylesShim
name|loadStyles
parameter_list|(
name|PackagePart
name|packagePart
parameter_list|)
block|{
try|try
block|{
name|PackageRelationshipCollection
name|stylesParts
init|=
name|packagePart
operator|.
name|getRelationshipsByType
argument_list|(
name|XWPFRelation
operator|.
name|STYLES
operator|.
name|getRelation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|stylesParts
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|PackageRelationship
name|stylesRelationShip
init|=
name|stylesParts
operator|.
name|getRelationship
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|stylesRelationShip
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PackagePart
name|stylesPart
init|=
name|packagePart
operator|.
name|getRelatedPart
argument_list|(
name|stylesRelationShip
argument_list|)
decl_stmt|;
if|if
condition|(
name|stylesPart
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|XWPFStylesShim
argument_list|(
name|stylesPart
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|OpenXML4JException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|XWPFNumbering
name|loadNumbering
parameter_list|(
name|PackagePart
name|packagePart
parameter_list|)
block|{
try|try
block|{
name|PackageRelationshipCollection
name|numberingParts
init|=
name|packagePart
operator|.
name|getRelationshipsByType
argument_list|(
name|XWPFRelation
operator|.
name|NUMBERING
operator|.
name|getRelation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|numberingParts
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|PackageRelationship
name|numberingRelationShip
init|=
name|numberingParts
operator|.
name|getRelationship
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|numberingRelationShip
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PackagePart
name|numberingPart
init|=
name|packagePart
operator|.
name|getRelatedPart
argument_list|(
name|numberingRelationShip
argument_list|)
decl_stmt|;
if|if
condition|(
name|numberingPart
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|XWPFNumberingShim
argument_list|(
name|numberingPart
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|OpenXML4JException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
return|return
literal|null
return|;
block|}
comment|/**      * This returns all items that might contain embedded objects:      * main document, headers, footers, comments, etc.      */
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|PackagePart
argument_list|>
name|getMainDocumentParts
parameter_list|()
block|{
name|List
argument_list|<
name|PackagePart
argument_list|>
name|mainStoryDocs
init|=
name|getStoryDocumentParts
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PackagePart
argument_list|>
name|relatedParts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|mainStoryDocs
operator|.
name|addAll
argument_list|(
name|opcPackage
operator|.
name|getPartsByContentType
argument_list|(
name|XWPFRelation
operator|.
name|GLOSSARY_DOCUMENT
operator|.
name|getContentType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PackagePart
name|pp
range|:
name|mainStoryDocs
control|)
block|{
name|addRelatedParts
argument_list|(
name|pp
argument_list|,
name|relatedParts
argument_list|)
expr_stmt|;
block|}
name|relatedParts
operator|.
name|addAll
argument_list|(
name|mainStoryDocs
argument_list|)
expr_stmt|;
return|return
name|relatedParts
return|;
block|}
specifier|private
name|void
name|addRelatedParts
parameter_list|(
name|PackagePart
name|documentPart
parameter_list|,
name|List
argument_list|<
name|PackagePart
argument_list|>
name|relatedParts
parameter_list|)
block|{
for|for
control|(
name|String
name|relation
range|:
name|MAIN_PART_RELATIONS
control|)
block|{
name|PackageRelationshipCollection
name|prc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|prc
operator|=
name|documentPart
operator|.
name|getRelationshipsByType
argument_list|(
name|relation
argument_list|)
expr_stmt|;
if|if
condition|(
name|prc
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prc
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PackagePart
name|packagePart
init|=
name|documentPart
operator|.
name|getRelatedPart
argument_list|(
name|prc
operator|.
name|getRelationship
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|relatedParts
operator|.
name|add
argument_list|(
name|packagePart
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{             }
block|}
block|}
comment|/**      *      * @return the first non-empty main story document part; empty list if no      * main story is found.      */
specifier|private
name|List
argument_list|<
name|PackagePart
argument_list|>
name|getStoryDocumentParts
parameter_list|()
block|{
for|for
control|(
name|String
name|contentType
range|:
name|MAIN_STORY_PART_RELATIONS
control|)
block|{
name|List
argument_list|<
name|PackagePart
argument_list|>
name|pps
init|=
name|opcPackage
operator|.
name|getPartsByContentType
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|pps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|pps
return|;
block|}
block|}
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|()
return|;
block|}
block|}
end_class

end_unit

