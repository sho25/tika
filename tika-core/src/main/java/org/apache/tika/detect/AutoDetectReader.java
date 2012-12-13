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
name|detect
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|LoadErrorHandler
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
name|config
operator|.
name|ServiceLoader
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
name|utils
operator|.
name|CharsetUtils
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
comment|/**  * An input stream reader that automatically detects the character encoding  * to be used for converting bytes to characters.  *  * @since Apache Tika 1.2  */
end_comment

begin_class
specifier|public
class|class
name|AutoDetectReader
extends|extends
name|BufferedReader
block|{
specifier|private
specifier|static
specifier|final
name|ServiceLoader
name|DEFAULT_LOADER
init|=
operator|new
name|ServiceLoader
argument_list|(
name|AutoDetectReader
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Charset
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|List
argument_list|<
name|EncodingDetector
argument_list|>
name|detectors
parameter_list|,
name|LoadErrorHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
comment|// Ask all given detectors for the character encoding
for|for
control|(
name|EncodingDetector
name|detector
range|:
name|detectors
control|)
block|{
try|try
block|{
name|Charset
name|charset
init|=
name|detector
operator|.
name|detect
argument_list|(
name|input
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
if|if
condition|(
name|charset
operator|!=
literal|null
condition|)
block|{
return|return
name|charset
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|e
parameter_list|)
block|{
comment|// TIKA-1041: Detector dependencies not present.
name|handler
operator|.
name|handleLoadError
argument_list|(
name|detector
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Try determining the encoding based on hints in document metadata
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|String
name|charset
init|=
name|type
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
literal|"charset"
argument_list|)
decl_stmt|;
if|if
condition|(
name|charset
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|CharsetUtils
operator|.
name|forName
argument_list|(
name|charset
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Failed to detect the character encoding of a document"
argument_list|)
throw|;
block|}
specifier|private
specifier|final
name|Charset
name|charset
decl_stmt|;
specifier|private
name|AutoDetectReader
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Charset
name|charset
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|charset
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
comment|// TIKA-240: Drop the BOM if present
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|read
argument_list|()
operator|!=
literal|'\ufeff'
condition|)
block|{
comment|// zero-width no-break space
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|AutoDetectReader
parameter_list|(
name|BufferedInputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|List
argument_list|<
name|EncodingDetector
argument_list|>
name|detectors
parameter_list|,
name|LoadErrorHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|this
argument_list|(
name|stream
argument_list|,
name|detect
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|,
name|detectors
argument_list|,
name|handler
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AutoDetectReader
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ServiceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|this
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|loader
operator|.
name|loadServiceProviders
argument_list|(
name|EncodingDetector
operator|.
name|class
argument_list|)
argument_list|,
name|loader
operator|.
name|getLoadErrorHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AutoDetectReader
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|this
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|DEFAULT_LOADER
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AutoDetectReader
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|this
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Charset
name|getCharset
parameter_list|()
block|{
return|return
name|charset
return|;
block|}
specifier|public
name|InputSource
name|asInputSource
parameter_list|()
block|{
name|InputSource
name|source
init|=
operator|new
name|InputSource
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|source
operator|.
name|setEncoding
argument_list|(
name|charset
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|source
return|;
block|}
block|}
end_class

end_unit

