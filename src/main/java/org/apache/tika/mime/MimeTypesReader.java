begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|// Commons Logging imports
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|// DOM imports
end_comment

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

begin_comment
comment|// JDK imports
end_comment

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

begin_comment
comment|/**  * A reader for XML files compliant with the freedesktop MIME-info DTD.  *   *<pre>  *&lt;!DOCTYPE mime-info [  *&lt;!ELEMENT mime-info (mime-type)+&gt;  *&lt;!ATTLIST mime-info xmlns CDATA #FIXED&quot;http://www.freedesktop.org/standards/shared-mime-info&quot;&gt;  *   *&lt;!ELEMENT mime-type (comment|acronym|expanded-acronym|glob|magic|root-XML|alias|sub-class-of)*&gt;  *&lt;!ATTLIST mime-type type CDATA #REQUIRED&gt;  *   *&lt;!-- a comment describing a document with the respective MIME type. Example:&quot;WMV video&quot; --&gt;  *&lt;!ELEMENT comment (#PCDATA)&gt;  *&lt;!ATTLIST comment xml:lang CDATA #IMPLIED&gt;  *   *&lt;!-- a comment describing a the respective unexpanded MIME type acronym. Example:&quot;WMV&quot; --&gt;  *&lt;!ELEMENT acronym (#PCDATA)&gt;  *&lt;!ATTLIST acronym xml:lang CDATA #IMPLIED&gt;  *   *&lt;!-- a comment describing a the respective unexpanded MIME type acronym. Example:&quot;Windows Media Video&quot; --&gt;  *&lt;!ELEMENT expanded-acronym (#PCDATA)&gt;  *&lt;!ATTLIST expanded-acronym xml:lang CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT glob EMPTY&gt;  *&lt;!ATTLIST glob pattern CDATA #REQUIRED&gt;  *&lt;!ATTLIST glob isregex CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT magic (match)+&gt;  *&lt;!ATTLIST magic priority CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT match (match)*&gt;  *&lt;!ATTLIST match offset CDATA #REQUIRED&gt;  *&lt;!ATTLIST match type (string|big16|big32|little16|little32|host16|host32|byte) #REQUIRED&gt;  *&lt;!ATTLIST match value CDATA #REQUIRED&gt;  *&lt;!ATTLIST match mask CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT root-XML EMPTY&gt;  *&lt;!ATTLIST root-XML  *          namespaceURI CDATA #REQUIRED  *          localName CDATA #REQUIRED&gt;  *   *&lt;!ELEMENT alias EMPTY&gt;  *&lt;!ATTLIST alias  *          type CDATA #REQUIRED&gt;  *   *&lt;!ELEMENT sub-class-of EMPTY&gt;  *&lt;!ATTLIST sub-class-of  *         type CDATA #REQUIRED&gt;  *  ]&gt;  *</pre>  *   *   * @see http://freedesktop.org/wiki/Standards_2fshared_2dmime_2dinfo_2dspec  *   */
end_comment

begin_class
specifier|final
class|class
name|MimeTypesReader
block|{
comment|/** The logger to use */
specifier|private
name|Log
name|logger
init|=
literal|null
decl_stmt|;
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
argument_list|(
name|types
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|MimeTypesReader
parameter_list|(
name|MimeTypes
name|types
parameter_list|,
name|Log
name|logger
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
if|if
condition|(
name|logger
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|logger
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
block|}
name|void
name|read
parameter_list|(
name|String
name|filepath
parameter_list|)
block|{
name|read
argument_list|(
name|MimeTypesReader
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|filepath
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|read
parameter_list|(
name|InputStream
name|stream
parameter_list|)
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
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|" while loading mime-types"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|void
name|read
parameter_list|(
name|Document
name|document
parameter_list|)
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
literal|"mime-info"
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
literal|"mime-type"
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
name|logger
operator|.
name|warn
argument_list|(
literal|"Not a<mime-info/> configuration document"
argument_list|)
expr_stmt|;
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
block|{
name|String
name|name
init|=
name|element
operator|.
name|getAttribute
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
try|try
block|{
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
literal|"_comment"
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
literal|"glob"
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
literal|"isregex"
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
literal|"pattern"
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
literal|"magic"
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
literal|"alias"
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
literal|"type"
argument_list|)
decl_stmt|;
try|try
block|{
name|type
operator|.
name|addAlias
argument_list|(
name|alias
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Invalid media type alias: "
operator|+
name|alias
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
literal|"root-XML"
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
literal|"sub-class-of"
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
literal|"type"
argument_list|)
decl_stmt|;
try|try
block|{
name|type
operator|.
name|setSuperType
argument_list|(
name|types
operator|.
name|forName
argument_list|(
name|parent
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Invalid parent type: "
operator|+
name|parent
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
name|MimeTypeException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Invalid media type configuration entry: "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Read Element named magic. */
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
block|{
name|Magic
name|magic
init|=
literal|null
decl_stmt|;
try|try
block|{
name|magic
operator|=
operator|new
name|Magic
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|element
operator|.
name|getAttribute
argument_list|(
literal|"priority"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|magic
operator|=
operator|new
name|Magic
argument_list|()
expr_stmt|;
block|}
name|magic
operator|.
name|setType
argument_list|(
name|mimeType
argument_list|)
expr_stmt|;
name|magic
operator|.
name|setClause
argument_list|(
name|readMatches
argument_list|(
name|element
argument_list|)
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
specifier|private
name|Clause
name|readMatches
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
name|Clause
name|sub
init|=
literal|null
decl_stmt|;
name|Clause
name|prev
init|=
name|Clause
operator|.
name|FALSE
decl_stmt|;
name|Clause
name|clause
init|=
literal|null
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
literal|"match"
argument_list|)
condition|)
block|{
name|sub
operator|=
name|readMatches
argument_list|(
name|nodeElement
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|clause
operator|=
operator|new
name|MagicClause
argument_list|(
name|Operator
operator|.
name|AND
argument_list|,
name|readMatch
argument_list|(
name|nodeElement
argument_list|)
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|clause
operator|=
name|readMatch
argument_list|(
name|nodeElement
argument_list|)
expr_stmt|;
block|}
name|clause
operator|=
operator|new
name|MagicClause
argument_list|(
name|Operator
operator|.
name|OR
argument_list|,
name|prev
argument_list|,
name|clause
argument_list|)
expr_stmt|;
name|prev
operator|=
name|clause
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|mte
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|mte
operator|+
literal|" while reading magic-match ["
operator|+
name|nodeElement
operator|+
literal|"], Ignoring!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|clause
return|;
block|}
comment|/** Read Element named match. */
specifier|private
name|MagicMatch
name|readMatch
parameter_list|(
name|Element
name|element
parameter_list|)
throws|throws
name|MimeTypeException
block|{
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
name|String
name|type
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
literal|"offset"
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
literal|"type"
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
literal|"value"
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
literal|"mask"
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
comment|// Parse OffSet
name|String
index|[]
name|offsets
init|=
name|offset
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|int
name|offStart
init|=
literal|0
decl_stmt|;
name|int
name|offEnd
init|=
literal|0
decl_stmt|;
try|try
block|{
name|offStart
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|offsets
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// WARN log + avoid loading
block|}
try|try
block|{
name|offEnd
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|offsets
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// WARN log
block|}
name|offEnd
operator|=
name|Math
operator|.
name|max
argument_list|(
name|offStart
argument_list|,
name|offEnd
argument_list|)
expr_stmt|;
return|return
operator|new
name|MagicMatch
argument_list|(
name|offStart
argument_list|,
name|offEnd
argument_list|,
name|type
argument_list|,
name|mask
argument_list|,
name|value
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
literal|"namespaceURI"
argument_list|)
argument_list|,
name|element
operator|.
name|getAttribute
argument_list|(
literal|"localName"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

