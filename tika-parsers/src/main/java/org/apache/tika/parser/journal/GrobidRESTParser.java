begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|journal
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
name|core
operator|.
name|MediaType
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Properties
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
name|client
operator|.
name|WebClient
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
name|cxf
operator|.
name|jaxrs
operator|.
name|ext
operator|.
name|multipart
operator|.
name|ContentDisposition
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
name|MultipartBody
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
name|ParseContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_class
specifier|public
class|class
name|GrobidRESTParser
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GrobidRESTParser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GROBID_REST_HOST
init|=
literal|"http://localhost:8080"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GROBID_ISALIVE_PATH
init|=
literal|"/grobid"
decl_stmt|;
comment|// isalive
comment|// doesn't work
comment|// nfc why
specifier|private
specifier|static
specifier|final
name|String
name|GROBID_PROCESSHEADER_PATH
init|=
literal|"/processHeaderDocument"
decl_stmt|;
specifier|private
name|String
name|restHostUrlStr
decl_stmt|;
specifier|public
name|GrobidRESTParser
parameter_list|()
block|{
name|String
name|restHostUrlStr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|restHostUrlStr
operator|=
name|readRestUrl
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"can't read rest url"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|restHostUrlStr
operator|==
literal|null
operator|||
operator|(
name|restHostUrlStr
operator|!=
literal|null
operator|&&
name|restHostUrlStr
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
condition|)
block|{
name|this
operator|.
name|restHostUrlStr
operator|=
name|GROBID_REST_HOST
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|restHostUrlStr
operator|=
name|restHostUrlStr
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|String
name|filePath
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
name|FileNotFoundException
block|{
name|File
name|pdfFile
init|=
operator|new
name|File
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|ContentDisposition
name|cd
init|=
operator|new
name|ContentDisposition
argument_list|(
literal|"form-data; name=\"input\"; filename=\""
operator|+
name|pdfFile
operator|.
name|getName
argument_list|()
operator|+
literal|"\""
argument_list|)
decl_stmt|;
name|Attachment
name|att
init|=
operator|new
name|Attachment
argument_list|(
literal|"input"
argument_list|,
operator|new
name|FileInputStream
argument_list|(
name|pdfFile
argument_list|)
argument_list|,
name|cd
argument_list|)
decl_stmt|;
name|MultipartBody
name|body
init|=
operator|new
name|MultipartBody
argument_list|(
name|att
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|restHostUrlStr
operator|+
name|GROBID_PROCESSHEADER_PATH
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|MULTIPART_FORM_DATA
argument_list|)
operator|.
name|post
argument_list|(
name|body
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|resp
init|=
name|response
operator|.
name|readEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|Metadata
name|teiMet
init|=
operator|new
name|TEIDOMParser
argument_list|()
operator|.
name|parse
argument_list|(
name|resp
argument_list|,
name|context
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|teiMet
operator|.
name|names
argument_list|()
control|)
block|{
name|metadata
operator|.
name|add
argument_list|(
literal|"grobid:header_"
operator|+
name|key
argument_list|,
name|teiMet
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't read response"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|readRestUrl
parameter_list|()
throws|throws
name|IOException
block|{
name|Properties
name|grobidProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|grobidProperties
operator|.
name|load
argument_list|(
name|GrobidRESTParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"GrobidExtractor.properties"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|grobidProperties
operator|.
name|getProperty
argument_list|(
literal|"grobid.server.url"
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|boolean
name|canRun
parameter_list|()
block|{
name|Response
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|WebClient
operator|.
name|create
argument_list|(
name|readRestUrl
argument_list|()
operator|+
name|GROBID_ISALIVE_PATH
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|TEXT_HTML
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|String
name|resp
init|=
name|response
operator|.
name|readEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|resp
operator|!=
literal|null
operator|&&
operator|!
name|resp
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|&&
name|resp
operator|.
name|startsWith
argument_list|(
literal|"<h4>"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//swallow...can't run
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

