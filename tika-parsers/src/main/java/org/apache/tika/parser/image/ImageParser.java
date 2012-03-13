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
name|image
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
name|Arrays
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|javax
operator|.
name|imageio
operator|.
name|IIOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|imageio
operator|.
name|ImageIO
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|imageio
operator|.
name|ImageReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|imageio
operator|.
name|metadata
operator|.
name|IIOMetadata
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|imageio
operator|.
name|stream
operator|.
name|ImageInputStream
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
name|io
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

begin_class
specifier|public
class|class
name|ImageParser
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
literal|7852529269245520335L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|CANONICAL_BMP_TYPE
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-ms-bmp"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|JAVA_BMP_TYPE
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"bmp"
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
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|CANONICAL_BMP_TYPE
argument_list|,
name|JAVA_BMP_TYPE
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"gif"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"png"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"vnd.wap.wbmp"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-icon"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-xcf"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
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
name|String
name|type
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
comment|// Java has a different idea of the BMP mime type to
comment|//  what the canonical one is, fix this up.
if|if
condition|(
name|CANONICAL_BMP_TYPE
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|type
operator|=
name|JAVA_BMP_TYPE
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|Iterator
argument_list|<
name|ImageReader
argument_list|>
name|iterator
init|=
name|ImageIO
operator|.
name|getImageReadersByMIMEType
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ImageReader
name|reader
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|ImageInputStream
name|imageStream
init|=
name|ImageIO
operator|.
name|createImageInputStream
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|stream
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|setInput
argument_list|(
name|imageStream
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|reader
operator|.
name|getWidth
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|reader
operator|.
name|getHeight
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"height"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|reader
operator|.
name|getHeight
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"width"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|reader
operator|.
name|getWidth
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|loadMetadata
argument_list|(
name|reader
operator|.
name|getImageMetadata
argument_list|(
literal|0
argument_list|)
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|imageStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Translate certain Metadata tags from the ImageIO
comment|//  specific namespace into the general Tika one
name|setIfPresent
argument_list|(
name|metadata
argument_list|,
literal|"CommentExtensions CommentExtension"
argument_list|,
name|Metadata
operator|.
name|COMMENTS
argument_list|)
expr_stmt|;
name|setIfPresent
argument_list|(
name|metadata
argument_list|,
literal|"markerSequence com"
argument_list|,
name|Metadata
operator|.
name|COMMENTS
argument_list|)
expr_stmt|;
name|setIfPresent
argument_list|(
name|metadata
argument_list|,
literal|"Data BitsPerSample"
argument_list|,
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IIOException
name|e
parameter_list|)
block|{
comment|// TIKA-619: There is a known bug in the Sun API when dealing with GIF images
comment|//  which Tika will just ignore.
if|if
condition|(
operator|!
operator|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Unexpected block type 0!"
argument_list|)
operator|&&
name|type
operator|.
name|equals
argument_list|(
literal|"image/gif"
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
name|type
operator|+
literal|" parse error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
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
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|setIfPresent
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|imageIOkey
parameter_list|,
name|String
name|tikaKey
parameter_list|)
block|{
if|if
condition|(
name|metadata
operator|.
name|get
argument_list|(
name|imageIOkey
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|tikaKey
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|imageIOkey
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|setIfPresent
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|imageIOkey
parameter_list|,
name|Property
name|tikaProp
parameter_list|)
block|{
if|if
condition|(
name|metadata
operator|.
name|get
argument_list|(
name|imageIOkey
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|v
init|=
name|metadata
operator|.
name|get
argument_list|(
name|imageIOkey
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|endsWith
argument_list|(
literal|" "
argument_list|)
condition|)
block|{
name|v
operator|=
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|v
operator|.
name|lastIndexOf
argument_list|(
literal|' '
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|metadata
operator|.
name|set
argument_list|(
name|tikaProp
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|loadMetadata
parameter_list|(
name|IIOMetadata
name|imageMetadata
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|String
index|[]
name|names
init|=
name|imageMetadata
operator|.
name|getMetadataFormatNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|names
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|length
init|=
name|names
operator|.
name|length
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|loadNode
argument_list|(
name|metadata
argument_list|,
name|imageMetadata
operator|.
name|getAsTree
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|loadNode
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Node
name|node
parameter_list|,
name|String
name|parents
parameter_list|,
name|boolean
name|addThisNodeName
parameter_list|)
block|{
if|if
condition|(
name|addThisNodeName
condition|)
block|{
if|if
condition|(
name|parents
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|parents
operator|+=
literal|" "
expr_stmt|;
block|}
name|parents
operator|+=
name|node
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
block|}
name|NamedNodeMap
name|map
init|=
name|node
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
name|int
name|length
init|=
name|map
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|==
literal|1
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|parents
argument_list|,
name|normalize
argument_list|(
name|map
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|1
condition|)
block|{
name|StringBuilder
name|value
init|=
operator|new
name|StringBuilder
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
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|value
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|Node
name|attr
init|=
name|map
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|value
operator|.
name|append
argument_list|(
name|attr
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|value
operator|.
name|append
argument_list|(
name|normalize
argument_list|(
name|attr
operator|.
name|getNodeValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|metadata
operator|.
name|add
argument_list|(
name|parents
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Node
name|child
init|=
name|node
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
comment|// print children recursively
name|loadNode
argument_list|(
name|metadata
argument_list|,
name|child
argument_list|,
name|parents
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|normalize
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|Boolean
operator|.
name|FALSE
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

