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
name|mime
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
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
name|DocumentBuilderFactory
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
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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

begin_comment
comment|/**  * A reader for XML files compliant with the freedesktop MIME-info DTD.  *   *<pre>  *&lt;!DOCTYPE mime-info [  *&lt;!ELEMENT mime-info (mime-type)+&gt;  *&lt;!ATTLIST mime-info xmlns CDATA #FIXED&quot;http://www.freedesktop.org/standards/shared-mime-info&quot;&gt;  *   *&lt;!ELEMENT mime-type (comment|acronym|expanded-acronym|glob|magic|root-XML|alias|sub-class-of)*&gt;  *&lt;!ATTLIST mime-type type CDATA #REQUIRED&gt;  *   *&lt;!-- a comment describing a document with the respective MIME type. Example:&quot;WMV video&quot; --&gt;  *&lt;!ELEMENT _comment (#PCDATA)&gt;  *&lt;!ATTLIST _comment xml:lang CDATA #IMPLIED&gt;  *   *&lt;!-- a comment describing a the respective unexpanded MIME type acronym. Example:&quot;WMV&quot; --&gt;  *&lt;!ELEMENT acronym (#PCDATA)&gt;  *&lt;!ATTLIST acronym xml:lang CDATA #IMPLIED&gt;  *   *&lt;!-- a comment describing a the respective unexpanded MIME type acronym. Example:&quot;Windows Media Video&quot; --&gt;  *&lt;!ELEMENT expanded-acronym (#PCDATA)&gt;  *&lt;!ATTLIST expanded-acronym xml:lang CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT glob EMPTY&gt;  *&lt;!ATTLIST glob pattern CDATA #REQUIRED&gt;  *&lt;!ATTLIST glob isregex CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT magic (match)+&gt;  *&lt;!ATTLIST magic priority CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT match (match)*&gt;  *&lt;!ATTLIST match offset CDATA #REQUIRED&gt;  *&lt;!ATTLIST match type (string|big16|big32|little16|little32|host16|host32|byte) #REQUIRED&gt;  *&lt;!ATTLIST match value CDATA #REQUIRED&gt;  *&lt;!ATTLIST match mask CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT root-XML EMPTY&gt;  *&lt;!ATTLIST root-XML  *          namespaceURI CDATA #REQUIRED  *          localName CDATA #REQUIRED&gt;  *   *&lt;!ELEMENT alias EMPTY&gt;  *&lt;!ATTLIST alias  *          type CDATA #REQUIRED&gt;  *   *&lt;!ELEMENT sub-class-of EMPTY&gt;  *&lt;!ATTLIST sub-class-of  *         type CDATA #REQUIRED&gt;  *  ]&gt;  *</pre>  *   *   * @see http://freedesktop.org/wiki/Standards_2fshared_2dmime_2dinfo_2dspec  *   */
end_comment

begin_class
specifier|final
class|class
name|MimeTypesReader
implements|implements
name|MimeTypesReaderMetKeys
block|{
specifier|private
specifier|final
name|MimeTypes
name|types
decl_stmt|;
name|MimeTypesReader
parameter_list|(
name|MimeTypes
name|types
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
block|}
name|void
name|read
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
try|try
block|{
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|document
init|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|stream
argument_list|)
argument_list|)
decl_stmt|;
name|read
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Unable to create an XML parser"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Invalid type configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|void
name|read
parameter_list|(
name|Document
name|document
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|Element
name|element
init|=
name|document
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|element
operator|!=
literal|null
operator|&&
name|element
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|MIME_INFO_TAG
argument_list|)
condition|)
block|{
name|NodeList
name|nodes
init|=
name|element
operator|.
name|getChildNodes
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
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|Element
name|child
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|MIME_TYPE_TAG
argument_list|)
condition|)
block|{
name|readMimeType
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Not a<"
operator|+
name|MIME_INFO_TAG
operator|+
literal|"/> configuration document: "
operator|+
name|element
operator|.
name|getTagName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** Read Element named mime-type. */
specifier|private
name|void
name|readMimeType
parameter_list|(
name|Element
name|element
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|String
name|name
init|=
name|element
operator|.
name|getAttribute
argument_list|(
name|MIME_TYPE_TYPE_ATTR
argument_list|)
decl_stmt|;
name|MimeType
name|type
init|=
name|types
operator|.
name|forName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|NodeList
name|nodes
init|=
name|element
operator|.
name|getChildNodes
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
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|Element
name|nodeElement
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|nodeElement
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|COMMENT_TAG
argument_list|)
condition|)
block|{
name|type
operator|.
name|setDescription
argument_list|(
name|nodeElement
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeElement
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|GLOB_TAG
argument_list|)
condition|)
block|{
name|boolean
name|useRegex
init|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|nodeElement
operator|.
name|getAttribute
argument_list|(
name|ISREGEX_ATTR
argument_list|)
argument_list|)
decl_stmt|;
name|types
operator|.
name|addPattern
argument_list|(
name|type
argument_list|,
name|nodeElement
operator|.
name|getAttribute
argument_list|(
name|PATTERN_ATTR
argument_list|)
argument_list|,
name|useRegex
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeElement
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|MAGIC_TAG
argument_list|)
condition|)
block|{
name|readMagic
argument_list|(
name|nodeElement
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeElement
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|ALIAS_TAG
argument_list|)
condition|)
block|{
name|String
name|alias
init|=
name|nodeElement
operator|.
name|getAttribute
argument_list|(
name|ALIAS_TYPE_ATTR
argument_list|)
decl_stmt|;
name|MediaType
name|aliasType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|alias
argument_list|)
decl_stmt|;
if|if
condition|(
name|aliasType
operator|!=
literal|null
condition|)
block|{
name|types
operator|.
name|addAlias
argument_list|(
name|type
argument_list|,
name|aliasType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Invalid media type alias: "
operator|+
name|alias
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|nodeElement
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|ROOT_XML_TAG
argument_list|)
condition|)
block|{
name|readRootXML
argument_list|(
name|nodeElement
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeElement
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|SUB_CLASS_OF_TAG
argument_list|)
condition|)
block|{
name|String
name|parent
init|=
name|nodeElement
operator|.
name|getAttribute
argument_list|(
name|SUB_CLASS_TYPE_ATTR
argument_list|)
decl_stmt|;
name|types
operator|.
name|setSuperType
argument_list|(
name|type
argument_list|,
name|MediaType
operator|.
name|parse
argument_list|(
name|parent
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|types
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read Element named magic.       * @throws MimeTypeException if the configuration is invalid      */
specifier|private
name|void
name|readMagic
parameter_list|(
name|Element
name|element
parameter_list|,
name|MimeType
name|mimeType
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|int
name|priority
init|=
literal|50
decl_stmt|;
name|String
name|value
init|=
name|element
operator|.
name|getAttribute
argument_list|(
name|MAGIC_PRIORITY_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|priority
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Clause
name|clause
range|:
name|readMatches
argument_list|(
name|element
argument_list|,
name|mimeType
operator|.
name|getType
argument_list|()
argument_list|)
control|)
block|{
name|Magic
name|magic
init|=
operator|new
name|Magic
argument_list|(
name|mimeType
argument_list|,
name|priority
argument_list|,
name|clause
argument_list|)
decl_stmt|;
name|mimeType
operator|.
name|addMagic
argument_list|(
name|magic
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|Clause
argument_list|>
name|readMatches
parameter_list|(
name|Element
name|element
parameter_list|,
name|MediaType
name|mediaType
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|NodeList
name|nodes
init|=
name|element
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|nodes
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|Clause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<
name|Clause
argument_list|>
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|Element
name|nodeElement
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|nodeElement
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|MATCH_TAG
argument_list|)
condition|)
block|{
name|clauses
operator|.
name|add
argument_list|(
name|readMatch
argument_list|(
name|nodeElement
argument_list|,
name|mediaType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|clauses
return|;
block|}
comment|/** Read Element named match. */
specifier|private
name|Clause
name|readMatch
parameter_list|(
name|Element
name|element
parameter_list|,
name|MediaType
name|mediaType
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|Clause
name|clause
init|=
name|getMagicClause
argument_list|(
name|element
argument_list|,
name|mediaType
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Clause
argument_list|>
name|subClauses
init|=
name|readMatches
argument_list|(
name|element
argument_list|,
name|mediaType
argument_list|)
decl_stmt|;
if|if
condition|(
name|subClauses
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|clause
return|;
block|}
elseif|else
if|if
condition|(
name|subClauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|AndClause
argument_list|(
name|clause
argument_list|,
name|subClauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|AndClause
argument_list|(
name|clause
argument_list|,
operator|new
name|OrClause
argument_list|(
name|subClauses
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
name|Clause
name|getMagicClause
parameter_list|(
name|Element
name|element
parameter_list|,
name|MediaType
name|mediaType
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|String
name|type
init|=
literal|"string"
decl_stmt|;
name|String
name|offset
init|=
literal|null
decl_stmt|;
name|String
name|value
init|=
literal|null
decl_stmt|;
name|String
name|mask
init|=
literal|null
decl_stmt|;
name|NamedNodeMap
name|attrs
init|=
name|element
operator|.
name|getAttributes
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
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Attr
name|attr
init|=
operator|(
name|Attr
operator|)
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|MATCH_OFFSET_ATTR
argument_list|)
condition|)
block|{
name|offset
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|attr
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|MATCH_TYPE_ATTR
argument_list|)
condition|)
block|{
name|type
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|attr
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|MATCH_VALUE_ATTR
argument_list|)
condition|)
block|{
name|value
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|attr
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|MATCH_MASK_ATTR
argument_list|)
condition|)
block|{
name|mask
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MagicMatch
argument_list|(
name|mediaType
argument_list|,
name|type
argument_list|,
name|offset
argument_list|,
name|value
argument_list|,
name|mask
argument_list|)
return|;
block|}
comment|/** Read Element named root-XML. */
specifier|private
name|void
name|readRootXML
parameter_list|(
name|Element
name|element
parameter_list|,
name|MimeType
name|mimeType
parameter_list|)
block|{
name|mimeType
operator|.
name|addRootXML
argument_list|(
name|element
operator|.
name|getAttribute
argument_list|(
name|NS_URI_ATTR
argument_list|)
argument_list|,
name|element
operator|.
name|getAttribute
argument_list|(
name|LOCAL_NAME_ATTR
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

