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
name|html
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
name|ByteBuffer
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|EncodingDetector
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

begin_comment
comment|/**  * Character encoding detector for determining the character encoding of a  * HTML document based on the potential charset parameter found in a  * Content-Type http-equiv meta tag somewhere near the beginning. Especially  * useful for determining the type among multiple closely related encodings  * (ISO-8859-*) for which other types of encoding detection are unreliable.  *  * @since Apache Tika 1.2  */
end_comment

begin_class
specifier|public
class|class
name|HtmlEncodingDetector
implements|implements
name|EncodingDetector
block|{
comment|// TIKA-357 - use bigger buffer for meta tag sniffing (was 4K)
specifier|private
specifier|static
specifier|final
name|int
name|META_TAG_BUFFER_SIZE
init|=
literal|8192
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|HTTP_EQUIV_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?is)<meta\\s+http-equiv\\s*=\\s*['\\\"]\\s*"
operator|+
literal|"Content-Type['\\\"]\\s+content\\s*=\\s*['\\\"]"
operator|+
literal|"([^'\\\"]+)['\\\"]"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|META_CHARSET_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?is)<meta\\s+charset\\s*=\\s*['\\\"]([^'\\\"]+)['\\\"]"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Charset
name|ASCII
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"US-ASCII"
argument_list|)
decl_stmt|;
specifier|public
name|Charset
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
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Read enough of the text stream to capture possible meta tags
name|input
operator|.
name|mark
argument_list|(
name|META_TAG_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|META_TAG_BUFFER_SIZE
index|]
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
name|int
name|m
init|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|m
operator|!=
operator|-
literal|1
operator|&&
name|n
operator|<
name|buffer
operator|.
name|length
condition|)
block|{
name|n
operator|+=
name|m
expr_stmt|;
name|m
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|n
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|n
argument_list|)
expr_stmt|;
block|}
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Interpret the head as ASCII and try to spot a meta tag with
comment|// a possible character encoding hint
name|String
name|charset
init|=
literal|null
decl_stmt|;
name|String
name|head
init|=
name|ASCII
operator|.
name|decode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Matcher
name|equiv
init|=
name|HTTP_EQUIV_PATTERN
operator|.
name|matcher
argument_list|(
name|head
argument_list|)
decl_stmt|;
if|if
condition|(
name|equiv
operator|.
name|find
argument_list|()
condition|)
block|{
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|equiv
operator|.
name|group
argument_list|(
literal|1
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
name|charset
operator|=
name|type
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
name|charset
operator|==
literal|null
condition|)
block|{
comment|// TIKA-892: HTML5 meta charset tag
name|Matcher
name|meta
init|=
name|META_CHARSET_PATTERN
operator|.
name|matcher
argument_list|(
name|head
argument_list|)
decl_stmt|;
if|if
condition|(
name|meta
operator|.
name|find
argument_list|()
condition|)
block|{
name|charset
operator|=
name|meta
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
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
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit
