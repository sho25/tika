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
name|StreamingOutput
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
name|Log
name|logger
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MetadataResource
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|PUT
annotation|@
name|Consumes
argument_list|(
literal|"multipart/form-data"
argument_list|)
annotation|@
name|Produces
argument_list|(
literal|"text/csv"
argument_list|)
annotation|@
name|Path
argument_list|(
literal|"form"
argument_list|)
specifier|public
name|StreamingOutput
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
name|produceMetadata
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
return|;
block|}
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
name|produceMetadata
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
return|;
block|}
specifier|private
name|StreamingOutput
name|produceMetadata
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
name|AutoDetectParser
name|parser
init|=
name|TikaResource
operator|.
name|createParser
argument_list|()
decl_stmt|;
name|TikaResource
operator|.
name|fillMetadata
argument_list|(
name|parser
argument_list|,
name|metadata
argument_list|,
name|httpHeaders
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
argument_list|)
expr_stmt|;
return|return
operator|new
name|StreamingOutput
argument_list|()
block|{
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
name|metadataToCsv
argument_list|(
name|metadata
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|void
name|metadataToCsv
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|OutputStream
name|outputStream
parameter_list|)
throws|throws
name|IOException
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
argument_list|,
literal|"UTF-8"
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
end_class

end_unit

