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
operator|.
name|resource
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Consumes
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
name|POST
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
name|MultivaluedMap
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
name|Response
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
name|UriInfo
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|ext
operator|.
name|multipart
operator|.
name|Attachment
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
name|parser
operator|.
name|RecursiveParserWrapper
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
name|BasicContentHandlerFactory
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
name|server
operator|.
name|MetadataList
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
annotation|@
name|Path
argument_list|(
literal|"/rmeta"
argument_list|)
specifier|public
class|class
name|RecursiveMetadataResource
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|logger
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RecursiveMetadataResource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TikaConfig
name|tikaConfig
decl_stmt|;
specifier|public
name|RecursiveMetadataResource
parameter_list|(
name|TikaConfig
name|tikaConfig
parameter_list|)
block|{
name|this
operator|.
name|tikaConfig
operator|=
name|tikaConfig
expr_stmt|;
block|}
annotation|@
name|POST
annotation|@
name|Consumes
argument_list|(
literal|"multipart/form-data"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
literal|"text/csv"
block|,
literal|"application/json"
block|}
argument_list|)
annotation|@
name|Path
argument_list|(
literal|"form"
argument_list|)
specifier|public
name|Response
name|getMetadataFromMultipart
parameter_list|(
name|Attachment
name|att
parameter_list|,
annotation|@
name|Context
name|UriInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
name|parseMetadata
argument_list|(
name|att
operator|.
name|getObject
argument_list|(
name|InputStream
operator|.
name|class
argument_list|)
argument_list|,
name|att
operator|.
name|getHeaders
argument_list|()
argument_list|,
name|info
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|PUT
annotation|@
name|Produces
argument_list|(
literal|"application/json"
argument_list|)
specifier|public
name|Response
name|getMetadata
parameter_list|(
name|InputStream
name|is
parameter_list|,
annotation|@
name|Context
name|HttpHeaders
name|httpHeaders
parameter_list|,
annotation|@
name|Context
name|UriInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
name|parseMetadata
argument_list|(
name|is
argument_list|,
name|httpHeaders
operator|.
name|getRequestHeaders
argument_list|()
argument_list|,
name|info
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|MetadataList
name|parseMetadata
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|MultivaluedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|httpHeaders
parameter_list|,
name|UriInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
specifier|final
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|AutoDetectParser
name|parser
init|=
name|TikaResource
operator|.
name|createParser
argument_list|(
name|tikaConfig
argument_list|)
decl_stmt|;
comment|//TODO: parameterize choice of handler and max chars?
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
name|type
init|=
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
decl_stmt|;
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|parser
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|TikaResource
operator|.
name|fillMetadata
argument_list|(
name|parser
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
name|httpHeaders
argument_list|)
expr_stmt|;
comment|//no need to add parser to parse recursively
name|TikaResource
operator|.
name|fillParseContext
argument_list|(
name|context
argument_list|,
name|httpHeaders
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|TikaResource
operator|.
name|logRequest
argument_list|(
name|logger
argument_list|,
name|info
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|TikaResource
operator|.
name|parse
argument_list|(
name|wrapper
argument_list|,
name|logger
argument_list|,
name|info
operator|.
name|getPath
argument_list|()
argument_list|,
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
return|return
operator|new
name|MetadataList
argument_list|(
name|wrapper
operator|.
name|getMetadata
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

