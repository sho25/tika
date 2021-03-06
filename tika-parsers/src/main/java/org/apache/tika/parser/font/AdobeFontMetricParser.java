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
name|font
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
name|Collections
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|fontbox
operator|.
name|afm
operator|.
name|AFMParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|fontbox
operator|.
name|afm
operator|.
name|FontMetrics
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
name|XHTMLContentHandler
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
comment|/**  * Parser for AFM Font Files  */
end_comment

begin_class
specifier|public
class|class
name|AdobeFontMetricParser
extends|extends
name|AbstractParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4820306522217196835L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|AFM_TYPE
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-font-adobe-metric"
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
name|singleton
argument_list|(
name|AFM_TYPE
argument_list|)
decl_stmt|;
comment|// TIKA-1325 Replace these with properties, from a well known standard
specifier|static
specifier|final
name|String
name|MET_AVG_CHAR_WIDTH
init|=
literal|"AvgCharacterWidth"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_DOC_VERSION
init|=
literal|"DocVersion"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_PS_NAME
init|=
literal|"PSName"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_FONT_NAME
init|=
literal|"FontName"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_FONT_FULL_NAME
init|=
literal|"FontFullName"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_FONT_FAMILY_NAME
init|=
literal|"FontFamilyName"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_FONT_SUB_FAMILY_NAME
init|=
literal|"FontSubFamilyName"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_FONT_VERSION
init|=
literal|"FontVersion"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_FONT_WEIGHT
init|=
literal|"FontWeight"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_FONT_NOTICE
init|=
literal|"FontNotice"
decl_stmt|;
specifier|static
specifier|final
name|String
name|MET_FONT_UNDERLINE_THICKNESS
init|=
literal|"FontUnderlineThickness"
decl_stmt|;
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
name|FontMetrics
name|fontMetrics
decl_stmt|;
name|AFMParser
name|parser
init|=
operator|new
name|AFMParser
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// Have FontBox process the file
name|fontMetrics
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
comment|// Get the comments in the file to display in xhtml
name|List
argument_list|<
name|String
argument_list|>
name|unModifiableComments
init|=
name|fontMetrics
operator|.
name|getComments
argument_list|()
decl_stmt|;
comment|//have to copy because we modify list in extractCreationDate
name|List
argument_list|<
name|String
argument_list|>
name|comments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|comment
range|:
name|unModifiableComments
control|)
block|{
name|comments
operator|.
name|add
argument_list|(
name|comment
argument_list|)
expr_stmt|;
block|}
comment|// Get the creation date
name|extractCreationDate
argument_list|(
name|metadata
argument_list|,
name|comments
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
name|AFM_TYPE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|fontMetrics
operator|.
name|getFullName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add metadata associated with the font type
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_AVG_CHAR_WIDTH
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|fontMetrics
operator|.
name|getAverageCharacterWidth
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_DOC_VERSION
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|fontMetrics
operator|.
name|getAFMVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_FONT_NAME
argument_list|,
name|fontMetrics
operator|.
name|getFontName
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_FONT_FULL_NAME
argument_list|,
name|fontMetrics
operator|.
name|getFullName
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_FONT_FAMILY_NAME
argument_list|,
name|fontMetrics
operator|.
name|getFamilyName
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_FONT_VERSION
argument_list|,
name|fontMetrics
operator|.
name|getFontVersion
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_FONT_WEIGHT
argument_list|,
name|fontMetrics
operator|.
name|getWeight
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_FONT_NOTICE
argument_list|,
name|fontMetrics
operator|.
name|getNotice
argument_list|()
argument_list|)
expr_stmt|;
name|addMetadataByString
argument_list|(
name|metadata
argument_list|,
name|MET_FONT_UNDERLINE_THICKNESS
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|fontMetrics
operator|.
name|getUnderlineThickness
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Output the remaining comments as text
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
comment|// Display the comments
if|if
condition|(
name|comments
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
literal|"Comments"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"comments"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|comment
range|:
name|comments
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|comment
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
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|addMetadataByString
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
comment|// Add metadata if an appropriate value is passed
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addMetadataByProperty
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
comment|// Add metadata if an appropriate value is passed
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|extractCreationDate
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|comments
parameter_list|)
block|{
name|String
name|date
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|comments
control|)
block|{
comment|// Look for the creation date
if|if
condition|(
name|value
operator|.
name|matches
argument_list|(
literal|".*Creation\\sDate.*"
argument_list|)
condition|)
block|{
name|date
operator|=
name|value
operator|.
name|substring
argument_list|(
name|value
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|+
literal|2
argument_list|)
expr_stmt|;
name|comments
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// If appropriate date then store as metadata
if|if
condition|(
name|date
operator|!=
literal|null
condition|)
block|{
name|addMetadataByProperty
argument_list|(
name|metadata
argument_list|,
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

