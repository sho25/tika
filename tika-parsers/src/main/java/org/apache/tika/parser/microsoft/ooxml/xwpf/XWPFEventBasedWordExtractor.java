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
operator|.
name|xwpf
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|Date
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
name|POIXMLProperties
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
name|util
operator|.
name|SAXHelper
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
name|parser
operator|.
name|microsoft
operator|.
name|ooxml
operator|.
name|OOXMLWordAndPowerPointTextHandler
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
name|ParagraphProperties
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
name|RunProperties
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
name|XWPFListManager
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
name|XMLReader
import|;
end_import

begin_comment
comment|//TODO: move this into POI?
end_comment

begin_comment
comment|/**  * Experimental class that is based on POI's XSSFEventBasedExcelExtractor  *  */
end_comment

begin_class
specifier|public
class|class
name|XWPFEventBasedWordExtractor
extends|extends
name|POIXMLTextExtractor
block|{
specifier|private
name|OPCPackage
name|container
decl_stmt|;
specifier|private
name|POIXMLProperties
name|properties
decl_stmt|;
specifier|public
name|XWPFEventBasedWordExtractor
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|XmlException
throws|,
name|OpenXML4JException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|OPCPackage
operator|.
name|open
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XWPFEventBasedWordExtractor
parameter_list|(
name|OPCPackage
name|container
parameter_list|)
throws|throws
name|XmlException
throws|,
name|OpenXML4JException
throws|,
name|IOException
block|{
name|super
argument_list|(
operator|(
name|POIXMLDocument
operator|)
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|properties
operator|=
operator|new
name|POIXMLProperties
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Use:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  XWPFEventBasedWordExtractor<filename.xlsx>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|XWPFEventBasedWordExtractor
name|extractor
init|=
operator|new
name|XWPFEventBasedWordExtractor
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|extractor
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|extractor
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|OPCPackage
name|getPackage
parameter_list|()
block|{
return|return
name|this
operator|.
name|container
return|;
block|}
specifier|public
name|POIXMLProperties
operator|.
name|CoreProperties
name|getCoreProperties
parameter_list|()
block|{
return|return
name|this
operator|.
name|properties
operator|.
name|getCoreProperties
argument_list|()
return|;
block|}
specifier|public
name|POIXMLProperties
operator|.
name|ExtendedProperties
name|getExtendedProperties
parameter_list|()
block|{
return|return
name|this
operator|.
name|properties
operator|.
name|getExtendedProperties
argument_list|()
return|;
block|}
specifier|public
name|POIXMLProperties
operator|.
name|CustomProperties
name|getCustomProperties
parameter_list|()
block|{
return|return
name|this
operator|.
name|properties
operator|.
name|getCustomProperties
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getText
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|//handle main document
name|List
argument_list|<
name|PackagePart
argument_list|>
name|pps
init|=
name|container
operator|.
name|getPartsByContentType
argument_list|(
name|XWPFRelation
operator|.
name|DOCUMENT
operator|.
name|getContentType
argument_list|()
argument_list|)
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
try|try
block|{
name|handleDocumentPart
argument_list|(
name|pp
argument_list|,
name|sb
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//handle glossary document
name|pps
operator|=
name|container
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
for|for
control|(
name|PackagePart
name|pp
range|:
name|pps
control|)
block|{
comment|//likely only one, but why not...
try|try
block|{
name|handleDocumentPart
argument_list|(
name|pp
argument_list|,
name|sb
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|handleDocumentPart
parameter_list|(
name|PackagePart
name|documentPart
parameter_list|,
name|StringBuilder
name|sb
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
name|xwpfListManager
init|=
operator|new
name|XWPFListManager
argument_list|(
name|numbering
argument_list|)
decl_stmt|;
comment|//TODO: XWPFStyles styles = loadStyles(documentPart);
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
name|xwpfListManager
argument_list|,
name|sb
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
name|xwpfListManager
argument_list|,
name|sb
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
name|xwpfListManager
argument_list|,
name|sb
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
name|XWPFListManager
name|xwpfListManager
parameter_list|,
name|StringBuilder
name|buffer
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
name|hyperlinks
init|=
name|loadHyperlinkRelationships
argument_list|(
name|packagePart
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
name|XMLReader
name|reader
init|=
name|SAXHelper
operator|.
name|newXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
operator|new
name|OOXMLWordAndPowerPointTextHandler
argument_list|(
operator|new
name|XWPFToTextContentHandler
argument_list|(
name|buffer
argument_list|)
argument_list|,
name|hyperlinks
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|loadHyperlinkRelationships
parameter_list|(
name|PackagePart
name|bodyPart
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|hyperlinks
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|PackageRelationshipCollection
name|prc
init|=
name|bodyPart
operator|.
name|getRelationshipsByType
argument_list|(
name|XWPFRelation
operator|.
name|HYPERLINK
operator|.
name|getRelation
argument_list|()
argument_list|)
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
name|prc
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PackageRelationship
name|pr
init|=
name|prc
operator|.
name|getRelationship
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|pr
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|String
name|id
init|=
name|pr
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|url
init|=
operator|(
name|pr
operator|.
name|getTargetURI
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|pr
operator|.
name|getTargetURI
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|url
operator|!=
literal|null
condition|)
block|{
name|hyperlinks
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|url
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
block|{         }
return|return
name|hyperlinks
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
name|container
operator|.
name|getPart
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
name|XWPFNumbering
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
specifier|private
class|class
name|XWPFToTextContentHandler
implements|implements
name|OOXMLWordAndPowerPointTextHandler
operator|.
name|XWPFBodyContentsHandler
block|{
specifier|private
specifier|final
name|StringBuilder
name|buffer
decl_stmt|;
specifier|public
name|XWPFToTextContentHandler
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|RunProperties
name|runProperties
parameter_list|,
name|String
name|contents
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|contents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|hyperlinkStart
parameter_list|(
name|String
name|link
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|hyperlinkEnd
parameter_list|()
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|startParagraph
parameter_list|(
name|ParagraphProperties
name|paragraphProperties
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|endParagraph
parameter_list|()
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startTable
parameter_list|()
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|endTable
parameter_list|()
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|startTableRow
parameter_list|()
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|endTableRow
parameter_list|()
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startTableCell
parameter_list|()
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|endTableCell
parameter_list|()
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startSDT
parameter_list|()
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|endSDT
parameter_list|()
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startEditedSection
parameter_list|(
name|String
name|editor
parameter_list|,
name|Date
name|date
parameter_list|,
name|OOXMLWordAndPowerPointTextHandler
operator|.
name|EditType
name|editType
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|endEditedSection
parameter_list|()
block|{          }
annotation|@
name|Override
specifier|public
name|boolean
name|getIncludeDeletedText
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|footnoteReference
parameter_list|(
name|String
name|id
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|void
name|endnoteReference
parameter_list|(
name|String
name|id
parameter_list|)
block|{          }
annotation|@
name|Override
specifier|public
name|boolean
name|getIncludeMoveFromText
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|embeddedOLERef
parameter_list|(
name|String
name|refId
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|embeddedPicRef
parameter_list|(
name|String
name|picFileName
parameter_list|,
name|String
name|picDescription
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|startBookmark
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|void
name|endBookmark
parameter_list|(
name|String
name|id
parameter_list|)
block|{
comment|//no-op
block|}
block|}
block|}
end_class

end_unit

