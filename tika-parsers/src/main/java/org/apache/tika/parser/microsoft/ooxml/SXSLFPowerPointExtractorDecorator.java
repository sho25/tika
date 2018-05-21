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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipException
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
name|poi
operator|.
name|xslf
operator|.
name|extractor
operator|.
name|XSLFPowerPointExtractor
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
name|xslf
operator|.
name|usermodel
operator|.
name|XSLFRelation
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
name|TikaCoreProperties
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
name|ooxml
operator|.
name|xslf
operator|.
name|XSLFEventBasedPowerPointExtractor
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
name|tika
operator|.
name|utils
operator|.
name|ExceptionUtils
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_comment
comment|/**  * SAX/Streaming pptx extractior  */
end_comment

begin_class
specifier|public
class|class
name|SXSLFPowerPointExtractorDecorator
extends|extends
name|AbstractOOXMLExtractor
block|{
specifier|private
specifier|final
specifier|static
name|String
name|HANDOUT_MASTER
init|=
literal|"http://schemas.openxmlformats.org/officeDocument/2006/relationships/handoutMaster"
decl_stmt|;
comment|//a pptx file should have one of these "main story" parts
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
name|XSLFRelation
operator|.
name|MAIN
operator|.
name|getContentType
argument_list|()
block|,
name|XSLFRelation
operator|.
name|PRESENTATION_MACRO
operator|.
name|getContentType
argument_list|()
block|,
name|XSLFRelation
operator|.
name|PRESENTATIONML
operator|.
name|getContentType
argument_list|()
block|,
name|XSLFRelation
operator|.
name|PRESENTATIONML_TEMPLATE
operator|.
name|getContentType
argument_list|()
block|,
name|XSLFRelation
operator|.
name|MACRO
operator|.
name|getContentType
argument_list|()
block|,
name|XSLFRelation
operator|.
name|MACRO_TEMPLATE
operator|.
name|getContentType
argument_list|()
block|,
name|XSLFRelation
operator|.
name|THEME_MANAGER
operator|.
name|getContentType
argument_list|()
comment|//TODO: what else
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
specifier|private
name|PackagePart
name|mainDocument
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|CommentAuthors
name|commentAuthors
init|=
operator|new
name|CommentAuthors
argument_list|()
decl_stmt|;
specifier|public
name|SXSLFPowerPointExtractorDecorator
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|XSLFEventBasedPowerPointExtractor
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
name|mainDocument
operator|=
name|pps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|//if mainDocument == null, throw exception
block|}
comment|/**      * @see XSLFPowerPointExtractor#getText()      */
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
name|IOException
block|{
name|loadCommentAuthors
argument_list|()
expr_stmt|;
name|PackageRelationshipCollection
name|slidesPRC
init|=
literal|null
decl_stmt|;
try|try
block|{
name|slidesPRC
operator|=
name|mainDocument
operator|.
name|getRelationshipsByType
argument_list|(
name|XSLFRelation
operator|.
name|SLIDE
operator|.
name|getRelation
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
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|slidesPRC
operator|!=
literal|null
operator|&&
name|slidesPRC
operator|.
name|size
argument_list|()
operator|>
literal|0
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
name|slidesPRC
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|handleSlidePart
argument_list|(
name|mainDocument
operator|.
name|getRelatedPart
argument_list|(
name|slidesPRC
operator|.
name|getRelationship
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
decl||
name|ZipException
name|e
parameter_list|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|handleGeneralTextContainingPart
argument_list|(
name|XSLFRelation
operator|.
name|SLIDE_MASTER
operator|.
name|getRelation
argument_list|()
argument_list|,
literal|"slide-master"
argument_list|,
name|mainDocument
argument_list|,
name|metadata
argument_list|,
operator|new
name|PlaceHolderSkipper
argument_list|(
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|handleGeneralTextContainingPart
argument_list|(
name|HANDOUT_MASTER
argument_list|,
literal|"slide-handout-master"
argument_list|,
name|mainDocument
argument_list|,
name|metadata
argument_list|,
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|loadCommentAuthors
parameter_list|()
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
name|mainDocument
operator|.
name|getRelationshipsByType
argument_list|(
name|XSLFRelation
operator|.
name|COMMENT_AUTHORS
operator|.
name|getRelation
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
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prc
operator|==
literal|null
operator|||
name|prc
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
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
name|commentAuthorsPart
init|=
literal|null
decl_stmt|;
try|try
block|{
name|commentAuthorsPart
operator|=
name|mainDocument
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commentAuthorsPart
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|SAXParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|commentAuthorsPart
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|parser
operator|=
name|context
operator|.
name|acquireSAXParser
argument_list|()
expr_stmt|;
name|parser
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
name|XSLFCommentAuthorHandler
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
decl||
name|SAXException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|releaseParser
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|handleSlidePart
parameter_list|(
name|PackagePart
name|slidePart
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
name|slidePart
argument_list|,
literal|false
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
comment|//        Map<String, String> hyperlinks = loadHyperlinkRelationships(packagePart);
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-content"
argument_list|)
expr_stmt|;
name|SAXParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
name|slidePart
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|parser
operator|=
name|context
operator|.
name|acquireSAXParser
argument_list|()
expr_stmt|;
name|parser
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
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|releaseParser
argument_list|(
name|parser
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
name|handleGeneralTextContainingPart
argument_list|(
name|XSLFRelation
operator|.
name|SLIDE_LAYOUT
operator|.
name|getRelation
argument_list|()
argument_list|,
literal|"slide-master-content"
argument_list|,
name|slidePart
argument_list|,
name|metadata
argument_list|,
operator|new
name|PlaceHolderSkipper
argument_list|(
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|linkedRelationships
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|handleGeneralTextContainingPart
argument_list|(
name|XSLFRelation
operator|.
name|NOTES
operator|.
name|getRelation
argument_list|()
argument_list|,
literal|"slide-notes"
argument_list|,
name|slidePart
argument_list|,
name|metadata
argument_list|,
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|linkedRelationships
argument_list|)
argument_list|)
expr_stmt|;
name|handleGeneralTextContainingPart
argument_list|(
name|XSLFRelation
operator|.
name|NOTES_MASTER
operator|.
name|getRelation
argument_list|()
argument_list|,
literal|"slide-notes-master"
argument_list|,
name|slidePart
argument_list|,
name|metadata
argument_list|,
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|linkedRelationships
argument_list|)
argument_list|)
expr_stmt|;
name|handleGeneralTextContainingPart
argument_list|(
name|XSLFRelation
operator|.
name|COMMENTS
operator|.
name|getRelation
argument_list|()
argument_list|,
literal|null
argument_list|,
name|slidePart
argument_list|,
name|metadata
argument_list|,
operator|new
name|XSLFCommentsHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|)
expr_stmt|;
name|handleGeneralTextContainingPart
argument_list|(
name|AbstractOOXMLExtractor
operator|.
name|RELATION_DIAGRAM_DATA
argument_list|,
literal|"diagram-data"
argument_list|,
name|slidePart
argument_list|,
name|metadata
argument_list|,
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|linkedRelationships
argument_list|)
argument_list|)
expr_stmt|;
name|handleGeneralTextContainingPart
argument_list|(
name|XSLFRelation
operator|.
name|CHART
operator|.
name|getRelation
argument_list|()
argument_list|,
literal|"chart"
argument_list|,
name|slidePart
argument_list|,
name|metadata
argument_list|,
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|)
argument_list|,
name|linkedRelationships
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * In PowerPoint files, slides have things embedded in them,      * and slide drawings which have the images      */
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
name|parts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//TODO: consider: getPackage().getPartsByName(Pattern.compile("/ppt/embeddings/.*?
comment|//TODO: consider: getPackage().getPartsByName(Pattern.compile("/ppt/media/.*?
name|PackageRelationshipCollection
name|slidePRC
init|=
literal|null
decl_stmt|;
try|try
block|{
name|slidePRC
operator|=
name|mainDocument
operator|.
name|getRelationshipsByType
argument_list|(
name|XSLFRelation
operator|.
name|SLIDE
operator|.
name|getRelation
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
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|slidePRC
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
name|slidePRC
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PackagePart
name|slidePart
init|=
literal|null
decl_stmt|;
try|try
block|{
name|slidePart
operator|=
name|mainDocument
operator|.
name|getRelatedPart
argument_list|(
name|slidePRC
operator|.
name|getRelationship
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addSlideParts
argument_list|(
name|slidePart
argument_list|,
name|parts
argument_list|)
expr_stmt|;
block|}
block|}
name|parts
operator|.
name|add
argument_list|(
name|mainDocument
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|rel
range|:
operator|new
name|String
index|[]
block|{
name|XSLFRelation
operator|.
name|SLIDE_MASTER
operator|.
name|getRelation
argument_list|()
block|,
name|HANDOUT_MASTER
block|}
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
name|mainDocument
operator|.
name|getRelationshipsByType
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|pp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pp
operator|=
name|mainDocument
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pp
operator|!=
literal|null
condition|)
block|{
name|parts
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|parts
return|;
block|}
specifier|private
name|void
name|addSlideParts
parameter_list|(
name|PackagePart
name|slidePart
parameter_list|,
name|List
argument_list|<
name|PackagePart
argument_list|>
name|parts
parameter_list|)
block|{
for|for
control|(
name|String
name|relation
range|:
operator|new
name|String
index|[]
block|{
name|XSLFRelation
operator|.
name|VML_DRAWING
operator|.
name|getRelation
argument_list|()
block|,
name|XSLFRelation
operator|.
name|SLIDE_LAYOUT
operator|.
name|getRelation
argument_list|()
block|,
name|XSLFRelation
operator|.
name|NOTES_MASTER
operator|.
name|getRelation
argument_list|()
block|,
name|XSLFRelation
operator|.
name|NOTES
operator|.
name|getRelation
argument_list|()
block|}
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
name|slidePart
operator|.
name|getRelationshipsByType
argument_list|(
name|relation
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prc
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PackageRelationship
name|packageRelationship
range|:
name|prc
control|)
block|{
if|if
condition|(
name|packageRelationship
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
init|=
literal|null
decl_stmt|;
try|try
block|{
name|relName
operator|=
name|PackagingURIHelper
operator|.
name|createPartName
argument_list|(
name|packageRelationship
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
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_WARNING
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|relName
operator|!=
literal|null
condition|)
block|{
name|parts
operator|.
name|add
argument_list|(
name|packageRelationship
operator|.
name|getPackage
argument_list|()
operator|.
name|getPart
argument_list|(
name|relName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|//and slide of course
name|parts
operator|.
name|add
argument_list|(
name|slidePart
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|XSLFCommentsHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
name|String
name|commentAuthorId
init|=
literal|null
decl_stmt|;
specifier|private
name|StringBuilder
name|commentBuffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
name|XSLFCommentsHandler
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|)
block|{
name|this
operator|.
name|xhtml
operator|=
name|xhtml
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
literal|"cm"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|commentAuthorId
operator|=
name|atts
operator|.
name|getValue
argument_list|(
literal|""
argument_list|,
literal|"authorId"
argument_list|)
expr_stmt|;
comment|//get date (dt)?
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
comment|//TODO: require that we're in<p:text>?
name|commentBuffer
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
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
name|uri
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
if|if
condition|(
literal|"cm"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
literal|"class"
argument_list|,
literal|"slide-comment"
argument_list|)
expr_stmt|;
name|String
name|authorString
init|=
name|commentAuthors
operator|.
name|getName
argument_list|(
name|commentAuthorId
argument_list|)
decl_stmt|;
name|String
name|authorInitials
init|=
name|commentAuthors
operator|.
name|getInitials
argument_list|(
name|commentAuthorId
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorString
operator|!=
literal|null
operator|||
name|authorInitials
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|boolean
name|authorExists
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|authorString
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|authorString
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|authorExists
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|authorExists
operator|&&
name|authorInitials
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
literal|" ("
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|authorInitials
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|authorInitials
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|authorExists
operator|&&
name|authorInitials
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|characters
argument_list|(
name|commentBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|commentBuffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|commentAuthorId
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|XSLFCommentAuthorHandler
extends|extends
name|DefaultHandler
block|{
name|String
name|id
init|=
literal|null
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|String
name|initials
init|=
literal|null
decl_stmt|;
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
literal|"cmAuthor"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
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
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"id"
operator|.
name|equals
argument_list|(
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|id
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|name
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"initials"
operator|.
name|equals
argument_list|(
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|initials
operator|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|commentAuthors
operator|.
name|add
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|initials
argument_list|)
expr_stmt|;
comment|//clear out
name|id
operator|=
literal|null
expr_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
name|initials
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|PlaceHolderSkipper
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|final
name|ContentHandler
name|wrappedHandler
decl_stmt|;
name|PlaceHolderSkipper
parameter_list|(
name|ContentHandler
name|wrappedHandler
parameter_list|)
block|{
name|this
operator|.
name|wrappedHandler
operator|=
name|wrappedHandler
expr_stmt|;
block|}
name|boolean
name|inPH
init|=
literal|false
decl_stmt|;
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
literal|"ph"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inPH
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|inPH
condition|)
block|{
name|wrappedHandler
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
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
name|uri
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
if|if
condition|(
operator|!
name|inPH
condition|)
block|{
name|wrappedHandler
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"sp"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inPH
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|inPH
condition|)
block|{
name|wrappedHandler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|inPH
condition|)
block|{
name|wrappedHandler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|CommentAuthors
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nameMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initialMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|void
name|add
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|initials
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|nameMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|initials
operator|!=
literal|null
condition|)
block|{
name|initialMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|initials
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|getName
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|nameMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
name|String
name|getInitials
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|initialMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

