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
name|xml
package|;
end_package

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
name|Set
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
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|extractor
operator|.
name|EmbeddedDocumentExtractor
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
name|extractor
operator|.
name|EmbeddedDocumentUtil
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
name|TikaMetadataKeys
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
name|ParseContext
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

begin_class
specifier|public
class|class
name|FictionBookParser
extends|extends
name|XMLParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|4195954546491524374L
decl_stmt|;
annotation|@
name|Override
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
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-fictionbook+xml"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ContentHandler
name|getContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|BinaryElementsDataHandler
argument_list|(
name|EmbeddedDocumentUtil
operator|.
name|getEmbeddedDocumentExtractor
argument_list|(
name|context
argument_list|)
argument_list|,
name|handler
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|BinaryElementsDataHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ELEMENT_BINARY
init|=
literal|"binary"
decl_stmt|;
specifier|private
name|boolean
name|binaryMode
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ATTRIBUTE_ID
init|=
literal|"id"
decl_stmt|;
specifier|private
specifier|final
name|EmbeddedDocumentExtractor
name|partExtractor
decl_stmt|;
specifier|private
specifier|final
name|ContentHandler
name|handler
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|binaryData
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ATTRIBUTE_CONTENT_TYPE
init|=
literal|"content-type"
decl_stmt|;
specifier|private
name|BinaryElementsDataHandler
parameter_list|(
name|EmbeddedDocumentExtractor
name|partExtractor
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|partExtractor
operator|=
name|partExtractor
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
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
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
name|binaryMode
operator|=
name|ELEMENT_BINARY
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
expr_stmt|;
if|if
condition|(
name|binaryMode
condition|)
block|{
name|binaryData
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaMetadataKeys
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
name|ATTRIBUTE_ID
argument_list|)
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
name|attributes
operator|.
name|getValue
argument_list|(
name|ATTRIBUTE_CONTENT_TYPE
argument_list|)
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
name|binaryMode
condition|)
block|{
try|try
block|{
name|partExtractor
operator|.
name|parseEmbedded
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|binaryData
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"IOException in parseEmbedded"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|binaryMode
operator|=
literal|false
expr_stmt|;
name|binaryData
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
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
name|binaryMode
condition|)
block|{
name|handler
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
else|else
block|{
name|binaryData
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
name|handler
operator|.
name|ignorableWhitespace
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
end_class

end_unit

