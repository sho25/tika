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
name|server
package|;
end_package

begin_import
import|import
name|au
operator|.
name|com
operator|.
name|bytecode
operator|.
name|opencsv
operator|.
name|CSVWriter
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
name|parser
operator|.
name|AutoDetectParser
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
name|config
operator|.
name|TikaConfig
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
name|detect
operator|.
name|Detector
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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PUT
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|WebApplicationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|StreamingOutput
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|Arrays
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

begin_class
annotation|@
name|Path
argument_list|(
literal|"/meta"
argument_list|)
specifier|public
class|class
name|MetadataResource
block|{
specifier|private
specifier|static
specifier|final
name|String
name|CONTENT_LENGTH
init|=
literal|"Content-Length"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FILE_NNAME
init|=
literal|"File-Name"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|RESOURCE_NAME
init|=
literal|"resourceName"
decl_stmt|;
annotation|@
name|PUT
annotation|@
name|Produces
argument_list|(
literal|"text/csv"
argument_list|)
specifier|public
name|StreamingOutput
name|getMetadata
parameter_list|(
name|InputStream
name|is
parameter_list|,
annotation|@
name|Context
name|HttpHeaders
name|httpHeaders
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Detector
name|detector
init|=
operator|new
name|HeaderTrustingDetectorFactory
argument_list|()
operator|.
name|createDetector
argument_list|(
name|httpHeaders
argument_list|)
decl_stmt|;
specifier|final
name|AutoDetectParser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|(
name|detector
argument_list|)
decl_stmt|;
specifier|final
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
specifier|final
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fillMetadata
argument_list|(
name|httpHeaders
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
return|return
operator|new
name|StreamingOutput
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|outputStream
parameter_list|)
throws|throws
name|IOException
throws|,
name|WebApplicationException
block|{
name|CSVWriter
name|writer
init|=
operator|new
name|CSVWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|outputStream
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|metadata
operator|.
name|names
argument_list|()
control|)
block|{
name|String
index|[]
name|values
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|values
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeNext
argument_list|(
name|list
operator|.
name|toArray
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|private
name|void
name|fillMetadata
parameter_list|(
name|HttpHeaders
name|httpHeaders
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fileName
init|=
name|httpHeaders
operator|.
name|getRequestHeader
argument_list|(
name|FILE_NNAME
argument_list|)
decl_stmt|,
name|cl
init|=
name|httpHeaders
operator|.
name|getRequestHeader
argument_list|(
name|CONTENT_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|cl
operator|!=
literal|null
operator|&&
operator|!
name|cl
operator|.
name|isEmpty
argument_list|()
condition|)
name|metadata
operator|.
name|set
argument_list|(
name|CONTENT_LENGTH
argument_list|,
name|cl
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
operator|&&
operator|!
name|fileName
operator|.
name|isEmpty
argument_list|()
condition|)
name|metadata
operator|.
name|set
argument_list|(
name|RESOURCE_NAME
argument_list|,
name|fileName
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|HeaderTrustingDetectorFactory
block|{
specifier|public
name|Detector
name|createDetector
parameter_list|(
name|HttpHeaders
name|httpHeaders
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
specifier|final
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
name|mediaType
init|=
name|httpHeaders
operator|.
name|getMediaType
argument_list|()
decl_stmt|;
if|if
condition|(
name|mediaType
operator|==
literal|null
operator|||
name|mediaType
operator|.
name|equals
argument_list|(
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
operator|.
name|APPLICATION_OCTET_STREAM_TYPE
argument_list|)
condition|)
return|return
operator|(
operator|new
name|TikaConfig
argument_list|()
operator|)
operator|.
name|getMimeRepository
argument_list|()
return|;
else|else
return|return
operator|new
name|Detector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|inputStream
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|MediaType
operator|.
name|parse
argument_list|(
name|mediaType
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

