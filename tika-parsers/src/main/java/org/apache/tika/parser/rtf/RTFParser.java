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
name|rtf
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
name|io
operator|.
name|input
operator|.
name|TaggedInputStream
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
comment|/**  * RTF parser  */
end_comment

begin_class
specifier|public
class|class
name|RTFParser
extends|extends
name|AbstractParser
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4165069489372320313L
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
name|MediaType
operator|.
name|application
argument_list|(
literal|"rtf"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * maximum number of bytes per embedded object/pict (default: 20MB)      */
specifier|private
specifier|static
name|int
name|EMB_OBJ_MAX_BYTES
init|=
literal|20
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|//20MB
comment|/**      * See {@link #setMaxBytesForEmbeddedObject(int)}.      *      * @return maximum number of bytes allowed for an embedded object.      */
specifier|public
specifier|static
name|int
name|getMaxBytesForEmbeddedObject
parameter_list|()
block|{
return|return
name|EMB_OBJ_MAX_BYTES
return|;
block|}
comment|/**      * Bytes for embedded objects are currently cached in memory.      * If something goes wrong during the parsing of an embedded object,      * it is possible that a read length may be crazily too long      * and cause a heap crash.      *      * @param max maximum number of bytes to allow for embedded objects.  If      *            the embedded object has more than this number of bytes, skip it.      */
specifier|public
specifier|static
name|void
name|setMaxBytesForEmbeddedObject
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|EMB_OBJ_MAX_BYTES
operator|=
name|max
expr_stmt|;
block|}
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
name|TaggedInputStream
name|tagged
init|=
operator|new
name|TaggedInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
try|try
block|{
name|XHTMLContentHandler
name|xhtmlHandler
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|RTFEmbObjHandler
name|embObjHandler
init|=
operator|new
name|RTFEmbObjHandler
argument_list|(
name|xhtmlHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|TextExtractor
name|ert
init|=
operator|new
name|TextExtractor
argument_list|(
name|xhtmlHandler
argument_list|,
name|metadata
argument_list|,
name|embObjHandler
argument_list|)
decl_stmt|;
name|ert
operator|.
name|extract
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/rtf"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|tagged
operator|.
name|throwIfCauseOf
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Error parsing an RTF document"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

