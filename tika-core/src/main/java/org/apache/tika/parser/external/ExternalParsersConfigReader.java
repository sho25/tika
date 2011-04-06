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
name|external
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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|mime
operator|.
name|MimeTypeException
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
comment|/**  * Builds up ExternalParser instances based on XML file(s)  *  which define what to run, for what, and how to process  *  any output metadata.  * Typically used to configure up a series of external programs   *  (like catdoc or pdf2txt) to extract text content from documents.  *    *<pre>  *  TODO XML DTD Here  *</pre>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ExternalParsersConfigReader
implements|implements
name|ExternalParsersConfigReaderMetKeys
block|{
specifier|public
specifier|static
name|List
argument_list|<
name|ExternalParser
argument_list|>
name|read
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
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
return|return
name|read
argument_list|(
name|document
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
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
name|TikaException
argument_list|(
literal|"Invalid parser configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|ExternalParser
argument_list|>
name|read
parameter_list|(
name|Document
name|document
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
return|return
name|read
argument_list|(
name|document
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|ExternalParser
argument_list|>
name|read
parameter_list|(
name|Element
name|element
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|ExternalParser
argument_list|>
name|parsers
init|=
operator|new
name|ArrayList
argument_list|<
name|ExternalParser
argument_list|>
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
name|EXTERNAL_PARSERS_TAG
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
name|PARSER_TAG
argument_list|)
condition|)
block|{
name|ExternalParser
name|p
init|=
name|readParser
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|parsers
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
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
name|EXTERNAL_PARSERS_TAG
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
return|return
name|parsers
return|;
block|}
comment|/**     * Builds and Returns an ExternalParser, or null if a check     *  command was given that didn't match.     */
specifier|private
specifier|static
name|ExternalParser
name|readParser
parameter_list|(
name|Element
name|parserDef
parameter_list|)
throws|throws
name|TikaException
block|{
name|ExternalParser
name|parser
init|=
operator|new
name|ExternalParser
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|parserDef
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
name|children
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
name|children
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
name|CHECK_TAG
argument_list|)
condition|)
block|{
name|boolean
name|present
init|=
name|readCheckTagAndCheck
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|present
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|child
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|COMMAND_TAG
argument_list|)
condition|)
block|{
name|parser
operator|.
name|setCommand
argument_list|(
name|getString
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|child
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|MIMETYPES_TAG
argument_list|)
condition|)
block|{
name|parser
operator|.
name|setSupportedTypes
argument_list|(
name|readMimeTypes
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|child
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|METADATA_TAG
argument_list|)
condition|)
block|{
name|parser
operator|.
name|setMetadataExtractionPatterns
argument_list|(
name|readMetadataPatterns
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|parser
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|MediaType
argument_list|>
name|readMimeTypes
parameter_list|(
name|Element
name|mimeTypes
parameter_list|)
block|{
name|Set
argument_list|<
name|MediaType
argument_list|>
name|types
init|=
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|mimeTypes
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
name|children
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
name|children
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
name|MIMETYPE_TAG
argument_list|)
condition|)
block|{
name|types
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
name|getString
argument_list|(
name|child
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|types
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|readMetadataPatterns
parameter_list|(
name|Element
name|metadataDef
parameter_list|)
block|{
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|metadataDef
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
name|children
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
name|children
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
name|METADATA_MATCH_TAG
argument_list|)
condition|)
block|{
name|String
name|metadataKey
init|=
name|child
operator|.
name|getAttribute
argument_list|(
name|METADATA_KEY_ATTR
argument_list|)
decl_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|getString
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|pattern
argument_list|,
name|metadataKey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|metadata
return|;
block|}
specifier|private
specifier|static
name|boolean
name|readCheckTagAndCheck
parameter_list|(
name|Element
name|checkDef
parameter_list|)
block|{
name|String
name|command
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|errorVals
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|checkDef
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
name|children
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
name|children
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
name|COMMAND_TAG
argument_list|)
condition|)
block|{
name|command
operator|=
name|getString
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|child
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
name|ERROR_CODES_TAG
argument_list|)
condition|)
block|{
name|String
name|errs
init|=
name|getString
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|errs
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
try|try
block|{
name|String
name|s
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|errorVals
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{}
block|}
block|}
block|}
block|}
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
name|int
index|[]
name|errVals
init|=
operator|new
name|int
index|[
name|errorVals
operator|.
name|size
argument_list|()
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
name|errVals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|errVals
index|[
name|i
index|]
operator|=
name|errorVals
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|ExternalParser
operator|.
name|check
argument_list|(
name|command
argument_list|,
name|errVals
argument_list|)
return|;
block|}
comment|// No check command, so assume it's there
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|String
name|getString
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
name|StringBuffer
name|s
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|NodeList
name|children
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
name|children
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
name|children
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
name|TEXT_NODE
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
name|node
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

