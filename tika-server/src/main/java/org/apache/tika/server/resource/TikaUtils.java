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
name|net
operator|.
name|URL
import|;
end_import

begin_class
specifier|public
class|class
name|TikaUtils
block|{
specifier|public
specifier|static
name|InputStream
name|getInputSteam
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
name|IOException
block|{
name|String
name|fileUrl
init|=
name|httpHeaders
operator|.
name|getHeaderString
argument_list|(
literal|"fileUrl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|.
name|available
argument_list|()
operator|==
literal|0
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|fileUrl
argument_list|)
condition|)
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
return|return
name|TikaInputStream
operator|.
name|get
argument_list|(
operator|new
name|URL
argument_list|(
name|fileUrl
argument_list|)
argument_list|,
name|metadata
argument_list|)
return|;
block|}
return|return
name|is
return|;
block|}
block|}
end_class

end_unit
