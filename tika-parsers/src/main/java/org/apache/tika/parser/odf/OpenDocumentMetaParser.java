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
name|xml
operator|.
name|AttributeDependantMetadataHandler
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
name|xml
operator|.
name|DcXMLParser
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
name|xml
operator|.
name|MetadataHandler
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
name|TeeContentHandler
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
name|xpath
operator|.
name|CompositeMatcher
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
name|xpath
operator|.
name|Matcher
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
name|xpath
operator|.
name|MatchingContentHandler
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
name|xpath
operator|.
name|XPathParser
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

begin_comment
comment|/**  * Parser for OpenDocument<code>meta.xml</code> files.  */
end_comment

begin_class
specifier|public
class|class
name|OpenDocumentMetaParser
extends|extends
name|DcXMLParser
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|8739250869531737584L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XPathParser
name|META_XPATH
init|=
operator|new
name|XPathParser
argument_list|(
literal|"meta"
argument_list|,
literal|"urn:oasis:names:tc:opendocument:xmlns:meta:1.0"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|ContentHandler
name|getMeta
parameter_list|(
name|ContentHandler
name|ch
parameter_list|,
name|Metadata
name|md
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|element
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
operator|new
name|CompositeMatcher
argument_list|(
name|META_XPATH
operator|.
name|parse
argument_list|(
literal|"//meta:"
operator|+
name|element
argument_list|)
argument_list|,
name|META_XPATH
operator|.
name|parse
argument_list|(
literal|"//meta:"
operator|+
name|element
operator|+
literal|"//text()"
argument_list|)
argument_list|)
decl_stmt|;
name|ContentHandler
name|branch
init|=
operator|new
name|MatchingContentHandler
argument_list|(
operator|new
name|MetadataHandler
argument_list|(
name|md
argument_list|,
name|name
argument_list|)
argument_list|,
name|matcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|TeeContentHandler
argument_list|(
name|ch
argument_list|,
name|branch
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ContentHandler
name|getUserDefined
parameter_list|(
name|ContentHandler
name|ch
parameter_list|,
name|Metadata
name|md
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
operator|new
name|CompositeMatcher
argument_list|(
name|META_XPATH
operator|.
name|parse
argument_list|(
literal|"//meta:user-defined/@meta:name"
argument_list|)
argument_list|,
name|META_XPATH
operator|.
name|parse
argument_list|(
literal|"//meta:user-defined//text()"
argument_list|)
argument_list|)
decl_stmt|;
comment|// eg<meta:user-defined meta:name="Info1">Text1</meta:user-defined> becomes custom:Info1=Text1
name|ContentHandler
name|branch
init|=
operator|new
name|MatchingContentHandler
argument_list|(
operator|new
name|AttributeDependantMetadataHandler
argument_list|(
name|md
argument_list|,
literal|"meta:name"
argument_list|,
name|Metadata
operator|.
name|USER_DEFINED_METADATA_NAME_PREFIX
argument_list|)
argument_list|,
name|matcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|TeeContentHandler
argument_list|(
name|ch
argument_list|,
name|branch
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ContentHandler
name|getStatistic
parameter_list|(
name|ContentHandler
name|ch
parameter_list|,
name|Metadata
name|md
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|attribute
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|META_XPATH
operator|.
name|parse
argument_list|(
literal|"//meta:document-statistic/@meta:"
operator|+
name|attribute
argument_list|)
decl_stmt|;
name|ContentHandler
name|branch
init|=
operator|new
name|MatchingContentHandler
argument_list|(
operator|new
name|MetadataHandler
argument_list|(
name|md
argument_list|,
name|name
argument_list|)
argument_list|,
name|matcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|TeeContentHandler
argument_list|(
name|ch
argument_list|,
name|branch
argument_list|)
return|;
block|}
specifier|protected
name|ContentHandler
name|getContentHandler
parameter_list|(
name|ContentHandler
name|ch
parameter_list|,
name|Metadata
name|md
parameter_list|)
block|{
comment|// Process the Dublin Core Attributes
name|ch
operator|=
name|super
operator|.
name|getContentHandler
argument_list|(
name|ch
argument_list|,
name|md
argument_list|)
expr_stmt|;
comment|// Process the OO Meta Attributes
name|ch
operator|=
name|getMeta
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
name|Metadata
operator|.
name|CREATION_DATE
operator|.
name|getName
argument_list|()
argument_list|,
literal|"creation-date"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getMeta
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
name|Metadata
operator|.
name|KEYWORDS
argument_list|,
literal|"keyword"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getMeta
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
name|Metadata
operator|.
name|EDIT_TIME
argument_list|,
literal|"editing-duration"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getMeta
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"editing-cycles"
argument_list|,
literal|"editing-cycles"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getMeta
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"initial-creator"
argument_list|,
literal|"initial-creator"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getMeta
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"generator"
argument_list|,
literal|"generator"
argument_list|)
expr_stmt|;
comment|// Process the user defined Meta Attributes
name|ch
operator|=
name|getUserDefined
argument_list|(
name|ch
argument_list|,
name|md
argument_list|)
expr_stmt|;
comment|// Process the OO Statistics Attributes
name|ch
operator|=
name|getStatistic
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"nbTab"
argument_list|,
literal|"table-count"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getStatistic
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"nbObject"
argument_list|,
literal|"object-count"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getStatistic
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"nbImg"
argument_list|,
literal|"image-count"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getStatistic
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"nbPage"
argument_list|,
literal|"page-count"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getStatistic
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"nbPara"
argument_list|,
literal|"paragraph-count"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getStatistic
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"nbWord"
argument_list|,
literal|"word-count"
argument_list|)
expr_stmt|;
name|ch
operator|=
name|getStatistic
argument_list|(
name|ch
argument_list|,
name|md
argument_list|,
literal|"nbCharacter"
argument_list|,
literal|"character-count"
argument_list|)
expr_stmt|;
name|ch
operator|=
operator|new
name|NSNormalizerContentHandler
argument_list|(
name|ch
argument_list|)
expr_stmt|;
return|return
name|ch
return|;
block|}
block|}
end_class

end_unit

