begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|UriInfo
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|io
operator|.
name|TikaInputStream
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
name|TikaCoreProperties
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
name|server
operator|.
name|ServerStatus
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

begin_class
annotation|@
name|Path
argument_list|(
literal|"/detect"
argument_list|)
specifier|public
class|class
name|DetectorResource
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
name|DetectorResource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ServerStatus
name|serverStatus
decl_stmt|;
specifier|public
name|DetectorResource
parameter_list|(
name|ServerStatus
name|serverStatus
parameter_list|)
block|{
name|this
operator|.
name|serverStatus
operator|=
name|serverStatus
expr_stmt|;
block|}
annotation|@
name|PUT
annotation|@
name|Path
argument_list|(
literal|"stream"
argument_list|)
annotation|@
name|Consumes
argument_list|(
literal|"*/*"
argument_list|)
annotation|@
name|Produces
argument_list|(
literal|"text/plain"
argument_list|)
specifier|public
name|String
name|detect
parameter_list|(
specifier|final
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
specifier|final
name|UriInfo
name|info
parameter_list|)
block|{
name|Metadata
name|met
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|TikaResource
operator|.
name|getInputStream
argument_list|(
name|is
argument_list|,
name|httpHeaders
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|filename
init|=
name|TikaResource
operator|.
name|detectFilename
argument_list|(
name|httpHeaders
operator|.
name|getRequestHeaders
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Detecting media type for Filename: {}"
argument_list|,
name|filename
argument_list|)
expr_stmt|;
name|met
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|filename
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|TikaResource
operator|.
name|getConfig
argument_list|()
operator|.
name|getDetector
argument_list|()
operator|.
name|detect
argument_list|(
name|tis
argument_list|,
name|met
argument_list|)
operator|.
name|toString
argument_list|()
return|;
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
literal|"Unable to detect MIME type for file. Reason: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|MediaType
operator|.
name|OCTET_STREAM
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|e
parameter_list|)
block|{
name|serverStatus
operator|.
name|setStatus
argument_list|(
name|ServerStatus
operator|.
name|STATUS
operator|.
name|ERROR
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|serverStatus
operator|.
name|complete
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

