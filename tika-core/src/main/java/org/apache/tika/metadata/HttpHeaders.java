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
name|metadata
package|;
end_package

begin_comment
comment|/**  * A collection of HTTP header names.  *   * @see<a href="http://rfc-ref.org/RFC-TEXTS/2616/">Hypertext Transfer Protocol --  *      HTTP/1.1 (RFC 2616)</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|HttpHeaders
block|{
name|String
name|CONTENT_ENCODING
init|=
literal|"Content-Encoding"
decl_stmt|;
name|String
name|CONTENT_LANGUAGE
init|=
literal|"Content-Language"
decl_stmt|;
name|String
name|CONTENT_LENGTH
init|=
literal|"Content-Length"
decl_stmt|;
name|String
name|CONTENT_LOCATION
init|=
literal|"Content-Location"
decl_stmt|;
name|String
name|CONTENT_DISPOSITION
init|=
literal|"Content-Disposition"
decl_stmt|;
name|String
name|CONTENT_MD5
init|=
literal|"Content-MD5"
decl_stmt|;
name|String
name|CONTENT_TYPE
init|=
literal|"Content-Type"
decl_stmt|;
name|Property
name|LAST_MODIFIED
init|=
name|Property
operator|.
name|internalDate
argument_list|(
literal|"Last-Modified"
argument_list|)
decl_stmt|;
name|String
name|LOCATION
init|=
literal|"Location"
decl_stmt|;
block|}
end_interface

end_unit

