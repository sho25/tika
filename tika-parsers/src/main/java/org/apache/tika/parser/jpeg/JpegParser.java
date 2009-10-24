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
name|jpeg
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
name|parser
operator|.
name|Parser
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
name|Map
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
name|Iterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|imaging
operator|.
name|jpeg
operator|.
name|JpegMetadataReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|imaging
operator|.
name|jpeg
operator|.
name|JpegProcessingException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|Tag
import|;
end_import

begin_import
import|import
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|MetadataException
import|;
end_import

begin_class
specifier|public
class|class
name|JpegParser
implements|implements
name|Parser
block|{
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0.      */
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
try|try
block|{
name|com
operator|.
name|drew
operator|.
name|metadata
operator|.
name|Metadata
name|jpegMetadata
init|=
name|JpegMetadataReader
operator|.
name|readMetadata
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|directories
init|=
name|jpegMetadata
operator|.
name|getDirectoryIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|directories
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Directory
name|directory
init|=
operator|(
name|Directory
operator|)
name|directories
operator|.
name|next
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|tags
init|=
name|directory
operator|.
name|getTagIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|tags
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Tag
name|tag
init|=
operator|(
name|Tag
operator|)
name|tags
operator|.
name|next
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|tag
operator|.
name|getTagName
argument_list|()
argument_list|,
name|tag
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JpegProcessingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Can't read JPEG metadata"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MetadataException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Can't read JPEG metadata"
argument_list|,
name|e
argument_list|)
throw|;
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
block|}
end_class

end_unit

