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
name|parser
operator|.
name|txt
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
name|Collections
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

begin_class
specifier|public
class|class
name|Icu4jEncodingDetector
implements|implements
name|EncodingDetector
block|{
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|CharsetDetector
name|detector
init|=
operator|new
name|CharsetDetector
argument_list|()
decl_stmt|;
name|String
name|incomingCharset
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
decl_stmt|;
name|String
name|incomingType
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|incomingCharset
operator|==
literal|null
operator|&&
name|incomingType
operator|!=
literal|null
condition|)
block|{
comment|// TIKA-341: Use charset in content-type
name|MediaType
name|mt
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|incomingType
argument_list|)
decl_stmt|;
if|if
condition|(
name|mt
operator|!=
literal|null
condition|)
block|{
name|incomingCharset
operator|=
name|mt
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
literal|"charset"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|incomingCharset
operator|!=
literal|null
condition|)
block|{
name|detector
operator|.
name|setDeclaredEncoding
argument_list|(
name|incomingCharset
argument_list|)
expr_stmt|;
block|}
name|detector
operator|.
name|setText
argument_list|(
name|input
argument_list|)
expr_stmt|;
for|for
control|(
name|CharsetMatch
name|match
range|:
name|detector
operator|.
name|detectAll
argument_list|()
control|)
block|{
if|if
condition|(
name|Charset
operator|.
name|isSupported
argument_list|(
name|match
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|MediaType
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"charset"
argument_list|,
name|match
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
end_class

end_unit

