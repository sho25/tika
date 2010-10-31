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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|detect
operator|.
name|MagicDetector
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
name|NodeList
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
argument_list|)
decl_stmt|;
name|magic
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|magic
operator|.
name|setClause
argument_list|(
name|clause
argument_list|)
expr_stmt|;
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
name|String
name|type
init|=
literal|"string"
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
literal|0
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
name|String
name|offset
init|=
name|attr
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|colon
init|=
name|offset
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|==
operator|-
literal|1
condition|)
block|{
name|start
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|end
operator|=
name|start
expr_stmt|;
block|}
else|else
block|{
name|start
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|offset
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
argument_list|)
expr_stmt|;
name|end
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|offset
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Missing magic byte pattern"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|start
operator|<
literal|0
operator|||
name|end
operator|<
name|start
condition|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Invalid offset range: ["
operator|+
name|start
operator|+
literal|","
operator|+
name|end
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|patternBytes
init|=
name|decodeValue
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|patternBytes
operator|.
name|length
decl_stmt|;
name|byte
index|[]
name|maskBytes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|mask
operator|!=
literal|null
condition|)
block|{
name|maskBytes
operator|=
name|decodeValue
argument_list|(
name|type
argument_list|,
name|mask
argument_list|)
expr_stmt|;
name|length
operator|=
name|Math
operator|.
name|max
argument_list|(
name|patternBytes
operator|.
name|length
argument_list|,
name|maskBytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|MagicDetector
name|detector
init|=
operator|new
name|MagicDetector
argument_list|(
name|mediaType
argument_list|,
name|patternBytes
argument_list|,
name|maskBytes
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|Clause
name|clause
init|=
operator|new
name|MagicMatch
argument_list|(
name|detector
argument_list|,
name|length
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
name|byte
index|[]
name|decodeValue
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|MimeTypeException
block|{
comment|// Preliminary check
if|if
condition|(
operator|(
name|value
operator|==
literal|null
operator|)
operator|||
operator|(
name|type
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|byte
index|[]
name|decoded
init|=
literal|null
decl_stmt|;
name|String
name|tmpVal
init|=
literal|null
decl_stmt|;
name|int
name|radix
init|=
literal|8
decl_stmt|;
comment|// hex
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0x"
argument_list|)
condition|)
block|{
name|tmpVal
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|radix
operator|=
literal|16
expr_stmt|;
block|}
else|else
block|{
name|tmpVal
operator|=
name|value
expr_stmt|;
name|radix
operator|=
literal|8
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"string"
argument_list|)
condition|)
block|{
name|decoded
operator|=
name|decodeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"byte"
argument_list|)
condition|)
block|{
name|decoded
operator|=
name|tmpVal
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"host16"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"little16"
argument_list|)
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0x00FF
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"big16"
argument_list|)
condition|)
block|{
name|int
name|i
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
name|i
operator|&
literal|0x00FF
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"host32"
argument_list|)
operator|||
name|type
operator|.
name|equals
argument_list|(
literal|"little32"
argument_list|)
condition|)
block|{
name|long
name|i
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x000000FF
operator|)
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x0000FF00
operator|)
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x00FF0000
operator|)
operator|>>
literal|16
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0xFF000000
operator|)
operator|>>
literal|24
argument_list|)
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"big32"
argument_list|)
condition|)
block|{
name|long
name|i
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|tmpVal
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|decoded
operator|=
operator|new
name|byte
index|[]
block|{
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0xFF000000
operator|)
operator|>>
literal|24
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x00FF0000
operator|)
operator|>>
literal|16
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x0000FF00
operator|)
operator|>>
literal|8
argument_list|)
block|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x000000FF
operator|)
argument_list|)
block|}
expr_stmt|;
block|}
return|return
name|decoded
return|;
block|}
specifier|private
name|byte
index|[]
name|decodeString
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|MimeTypeException
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"0x"
argument_list|)
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
operator|(
name|value
operator|.
name|length
argument_list|()
operator|-
literal|2
operator|)
operator|/
literal|2
index|]
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|2
operator|+
name|i
operator|*
literal|2
argument_list|,
literal|4
operator|+
name|i
operator|*
literal|2
argument_list|)
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
try|try
block|{
name|ByteArrayOutputStream
name|decoded
init|=
operator|new
name|ByteArrayOutputStream
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
name|value
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
name|decoded
operator|.
name|write
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'x'
condition|)
block|{
name|decoded
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|2
argument_list|,
name|i
operator|+
literal|4
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|+=
literal|3
expr_stmt|;
block|}
else|else
block|{
name|int
name|j
init|=
name|i
operator|+
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|j
operator|<
name|i
operator|+
literal|4
operator|)
operator|&&
operator|(
name|j
operator|<
name|value
operator|.
name|length
argument_list|()
operator|)
operator|&&
operator|(
name|Character
operator|.
name|isDigit
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|)
operator|)
condition|)
block|{
name|j
operator|++
expr_stmt|;
block|}
name|decoded
operator|.
name|write
argument_list|(
name|Short
operator|.
name|decode
argument_list|(
literal|"0"
operator|+
name|value
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|j
argument_list|)
argument_list|)
operator|.
name|byteValue
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|=
name|j
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
name|decoded
operator|.
name|write
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|decoded
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Invalid string value: "
operator|+
name|value
argument_list|,
name|e
argument_list|)
throw|;
block|}
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

