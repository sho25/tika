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
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
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
name|SAXParserFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|concurrent
operator|.
name|ArrayBlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * A reader for XML files compliant with the freedesktop MIME-info DTD.  *   *<pre>  *&lt;!DOCTYPE mime-info [  *&lt;!ELEMENT mime-info (mime-type)+&gt;  *&lt;!ATTLIST mime-info xmlns CDATA #FIXED&quot;http://www.freedesktop.org/standards/shared-mime-info&quot;&gt;  *   *&lt;!ELEMENT mime-type (comment|acronym|expanded-acronym|glob|magic|root-XML|alias|sub-class-of)*&gt;  *&lt;!ATTLIST mime-type type CDATA #REQUIRED&gt;  *   *&lt;!-- a comment describing a document with the respective MIME type. Example:&quot;WMV video&quot; --&gt;  *&lt;!ELEMENT _comment (#PCDATA)&gt;  *&lt;!ATTLIST _comment xml:lang CDATA #IMPLIED&gt;  *   *&lt;!-- a comment describing a the respective unexpanded MIME type acronym. Example:&quot;WMV&quot; --&gt;  *&lt;!ELEMENT acronym (#PCDATA)&gt;  *&lt;!ATTLIST acronym xml:lang CDATA #IMPLIED&gt;  *   *&lt;!-- a comment describing a the respective unexpanded MIME type acronym. Example:&quot;Windows Media Video&quot; --&gt;  *&lt;!ELEMENT expanded-acronym (#PCDATA)&gt;  *&lt;!ATTLIST expanded-acronym xml:lang CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT glob EMPTY&gt;  *&lt;!ATTLIST glob pattern CDATA #REQUIRED&gt;  *&lt;!ATTLIST glob isregex CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT magic (match)+&gt;  *&lt;!ATTLIST magic priority CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT match (match)*&gt;  *&lt;!ATTLIST match offset CDATA #REQUIRED&gt;  *&lt;!ATTLIST match type (string|big16|big32|little16|little32|host16|host32|byte) #REQUIRED&gt;  *&lt;!ATTLIST match value CDATA #REQUIRED&gt;  *&lt;!ATTLIST match mask CDATA #IMPLIED&gt;  *   *&lt;!ELEMENT root-XML EMPTY&gt;  *&lt;!ATTLIST root-XML  *          namespaceURI CDATA #REQUIRED  *          localName CDATA #REQUIRED&gt;  *   *&lt;!ELEMENT alias EMPTY&gt;  *&lt;!ATTLIST alias  *          type CDATA #REQUIRED&gt;  *   *&lt;!ELEMENT sub-class-of EMPTY&gt;  *&lt;!ATTLIST sub-class-of  *         type CDATA #REQUIRED&gt;  *  ]&gt;  *</pre>  *   * In addition to the standard fields, this will also read two Tika specific fields:  *  - link  *  - uti  *   *  * @see<a href="https://freedesktop.org/wiki/Specifications/shared-mime-info-spec/">https://freedesktop.org/wiki/Specifications/shared-mime-info-spec/</a>  */
end_comment

begin_class
specifier|public
class|class
name|MimeTypesReader
extends|extends
name|DefaultHandler
implements|implements
name|MimeTypesReaderMetKeys
block|{
comment|/**      * Parser pool size      */
specifier|private
specifier|static
name|int
name|POOL_SIZE
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ReentrantReadWriteLock
name|READ_WRITE_LOCK
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|ArrayBlockingQueue
argument_list|<
name|SAXParser
argument_list|>
name|SAX_PARSERS
init|=
operator|new
name|ArrayBlockingQueue
argument_list|<>
argument_list|(
name|POOL_SIZE
argument_list|)
decl_stmt|;
static|static
block|{
try|try
block|{
name|setPoolSize
argument_list|(
name|POOL_SIZE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"problem initializing SAXParser pool"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|final
name|MimeTypes
name|types
decl_stmt|;
comment|/** Current type */
specifier|protected
name|MimeType
name|type
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|priority
decl_stmt|;
specifier|protected
name|StringBuilder
name|characters
init|=
literal|null
decl_stmt|;
specifier|protected
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
specifier|public
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
name|SAXParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|acquireSAXParser
argument_list|()
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
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
finally|finally
block|{
name|releaseParser
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|read
parameter_list|(
name|Document
name|document
parameter_list|)
throws|throws
name|MimeTypeException
block|{
try|try
block|{
name|TransformerFactory
name|factory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setFeature
argument_list|(
name|XMLConstants
operator|.
name|FEATURE_SECURE_PROCESSING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Transformer
name|transformer
init|=
name|factory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|document
argument_list|)
argument_list|,
operator|new
name|SAXResult
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Failed to parse type registry"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
return|return
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
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
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|MIME_TYPE_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|MIME_TYPE_TYPE_ATTR
argument_list|)
decl_stmt|;
try|try
block|{
name|type
operator|=
name|types
operator|.
name|forName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|e
parameter_list|)
block|{
name|handleMimeError
argument_list|(
name|name
argument_list|,
name|e
argument_list|,
name|qName
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|ALIAS_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|String
name|alias
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|ALIAS_TYPE_ATTR
argument_list|)
decl_stmt|;
name|types
operator|.
name|addAlias
argument_list|(
name|type
argument_list|,
name|MediaType
operator|.
name|parse
argument_list|(
name|alias
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SUB_CLASS_OF_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|String
name|parent
init|=
name|attributes
operator|.
name|getValue
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
elseif|else
if|if
condition|(
name|ACRONYM_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|||
name|COMMENT_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|||
name|TIKA_LINK_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
operator|||
name|TIKA_UTI_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|characters
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|GLOB_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|String
name|pattern
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|PATTERN_ATTR
argument_list|)
decl_stmt|;
name|String
name|isRegex
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|ISREGEX_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|pattern
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|types
operator|.
name|addPattern
argument_list|(
name|type
argument_list|,
name|pattern
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|isRegex
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
name|handleGlobError
argument_list|(
name|type
argument_list|,
name|pattern
argument_list|,
name|e
argument_list|,
name|qName
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|ROOT_XML_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|String
name|namespace
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|NS_URI_ATTR
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|LOCAL_NAME_ATTR
argument_list|)
decl_stmt|;
name|type
operator|.
name|addRootXML
argument_list|(
name|namespace
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|MATCH_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|String
name|kind
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|MATCH_TYPE_ATTR
argument_list|)
decl_stmt|;
name|String
name|offset
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|MATCH_OFFSET_ATTR
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|MATCH_VALUE_ATTR
argument_list|)
decl_stmt|;
name|String
name|mask
init|=
name|attributes
operator|.
name|getValue
argument_list|(
name|MATCH_MASK_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|kind
operator|==
literal|null
condition|)
block|{
name|kind
operator|=
literal|"string"
expr_stmt|;
block|}
name|current
operator|=
operator|new
name|ClauseRecord
argument_list|(
operator|new
name|MagicMatch
argument_list|(
name|type
operator|.
name|getType
argument_list|()
argument_list|,
name|kind
argument_list|,
name|offset
argument_list|,
name|value
argument_list|,
name|mask
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|MAGIC_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|attributes
operator|.
name|getValue
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
else|else
block|{
name|priority
operator|=
literal|50
expr_stmt|;
block|}
name|current
operator|=
operator|new
name|ClauseRecord
argument_list|(
literal|null
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
block|{
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|MIME_TYPE_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|type
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|COMMENT_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|type
operator|.
name|setDescription
argument_list|(
name|characters
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|characters
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ACRONYM_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|type
operator|.
name|setAcronym
argument_list|(
name|characters
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|characters
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TIKA_UTI_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|type
operator|.
name|setUniformTypeIdentifier
argument_list|(
name|characters
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|characters
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TIKA_LINK_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
try|try
block|{
name|type
operator|.
name|addLink
argument_list|(
operator|new
name|URI
argument_list|(
name|characters
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unable to parse link: "
operator|+
name|characters
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|characters
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|MATCH_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
name|current
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|MAGIC_TAG
operator|.
name|equals
argument_list|(
name|qName
argument_list|)
condition|)
block|{
for|for
control|(
name|Clause
name|clause
range|:
name|current
operator|.
name|getClauses
argument_list|()
control|)
block|{
name|type
operator|.
name|addMagic
argument_list|(
operator|new
name|Magic
argument_list|(
name|type
argument_list|,
name|priority
argument_list|,
name|clause
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|current
operator|=
literal|null
expr_stmt|;
block|}
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
block|{
if|if
condition|(
name|characters
operator|!=
literal|null
condition|)
block|{
name|characters
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
block|}
specifier|protected
name|void
name|handleMimeError
parameter_list|(
name|String
name|input
parameter_list|,
name|MimeTypeException
name|ex
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
specifier|protected
name|void
name|handleGlobError
parameter_list|(
name|MimeType
name|type
parameter_list|,
name|String
name|pattern
parameter_list|,
name|MimeTypeException
name|ex
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
specifier|private
name|ClauseRecord
name|current
init|=
operator|new
name|ClauseRecord
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|private
class|class
name|ClauseRecord
block|{
specifier|private
name|ClauseRecord
name|parent
decl_stmt|;
specifier|private
name|Clause
name|clause
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Clause
argument_list|>
name|subclauses
init|=
literal|null
decl_stmt|;
specifier|public
name|ClauseRecord
parameter_list|(
name|Clause
name|clause
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|current
expr_stmt|;
name|this
operator|.
name|clause
operator|=
name|clause
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|subclauses
operator|!=
literal|null
condition|)
block|{
name|Clause
name|subclause
decl_stmt|;
if|if
condition|(
name|subclauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|subclause
operator|=
name|subclauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|subclause
operator|=
operator|new
name|OrClause
argument_list|(
name|subclauses
argument_list|)
expr_stmt|;
block|}
name|clause
operator|=
operator|new
name|AndClause
argument_list|(
name|clause
argument_list|,
name|subclause
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parent
operator|.
name|subclauses
operator|==
literal|null
condition|)
block|{
name|parent
operator|.
name|subclauses
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|parent
operator|.
name|subclauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|parent
operator|.
name|subclauses
operator|=
operator|new
name|ArrayList
argument_list|<
name|Clause
argument_list|>
argument_list|(
name|parent
operator|.
name|subclauses
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|subclauses
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
name|current
operator|=
name|current
operator|.
name|parent
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|Clause
argument_list|>
name|getClauses
parameter_list|()
block|{
return|return
name|subclauses
return|;
block|}
block|}
comment|/**      * Acquire a SAXParser from the pool; create one if it      * doesn't exist.  Make sure to {@link #releaseParser(SAXParser)} in      * a<code>finally</code> block every time you call this.      *      * @return a SAXParser      * @throws TikaException      */
specifier|public
specifier|static
name|SAXParser
name|acquireSAXParser
parameter_list|()
throws|throws
name|TikaException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|SAXParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|READ_WRITE_LOCK
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|parser
operator|=
name|SAX_PARSERS
operator|.
name|poll
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"interrupted while waiting for SAXParser"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|READ_WRITE_LOCK
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
return|return
name|parser
return|;
block|}
block|}
block|}
comment|/**      * Return parser to the pool for reuse      *      * @param parser parser to return      */
specifier|public
specifier|static
name|void
name|releaseParser
parameter_list|(
name|SAXParser
name|parser
parameter_list|)
block|{
try|try
block|{
name|parser
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|//ignore
block|}
try|try
block|{
name|READ_WRITE_LOCK
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|//if there are extra parsers (e.g. after a reset of the pool to a smaller size),
comment|// this parser will not be added and will then be gc'd
name|SAX_PARSERS
operator|.
name|offer
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|READ_WRITE_LOCK
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Set the pool size for cached XML parsers.      *      * @param poolSize      */
specifier|public
specifier|static
name|void
name|setPoolSize
parameter_list|(
name|int
name|poolSize
parameter_list|)
throws|throws
name|TikaException
block|{
try|try
block|{
comment|//stop the world with a write lock
comment|//parsers that are currently in use will be offered, but not
comment|//accepted and will be gc'd
name|READ_WRITE_LOCK
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|SAX_PARSERS
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<>
argument_list|(
name|poolSize
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|poolSize
condition|;
name|i
operator|++
control|)
block|{
name|SAX_PARSERS
operator|.
name|offer
argument_list|(
name|newSAXParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|POOL_SIZE
operator|=
name|poolSize
expr_stmt|;
block|}
finally|finally
block|{
name|READ_WRITE_LOCK
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|SAXParser
name|newSAXParser
parameter_list|()
throws|throws
name|TikaException
block|{
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|.
name|setFeature
argument_list|(
name|XMLConstants
operator|.
name|FEATURE_SECURE_PROCESSING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|newSAXParser
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
decl||
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"prooblem creating SAX parser factory"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

