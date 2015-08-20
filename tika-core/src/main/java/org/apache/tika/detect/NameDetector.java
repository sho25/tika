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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Content type detection based on the resource name. An instance of this  * class contains a set of regular expression patterns that are matched  * against the resource name potentially given as a part of the input metadata.  *<p>  * If a pattern matches the given name, then the media type associated with  * that pattern is returned as the likely content type of the input document.  * Otherwise the returned type is<code>application/octet-stream</code>.  *<p>  * See the {@link #detect(InputStream, Metadata)} method for more details  * of the matching algorithm.  *  * @since Apache Tika 0.3  */
end_comment

begin_class
specifier|public
class|class
name|NameDetector
implements|implements
name|Detector
block|{
comment|/**      * The regular expression patterns used for type detection.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|Pattern
argument_list|,
name|MediaType
argument_list|>
name|patterns
decl_stmt|;
comment|/**      * Creates a new content type detector based on the given name patterns.      * The given pattern map is not copied, so the caller may update the      * mappings even after this detector instance has been created. However,      * the map<em>must not be concurrently modified</em> while this instance      * is used for type detection.      *      * @param patterns map from name patterns to corresponding media types      */
specifier|public
name|NameDetector
parameter_list|(
name|Map
argument_list|<
name|Pattern
argument_list|,
name|MediaType
argument_list|>
name|patterns
parameter_list|)
block|{
name|this
operator|.
name|patterns
operator|=
name|patterns
expr_stmt|;
block|}
comment|/**      * Detects the content type of an input document based on the document      * name given in the input metadata. The RESOURCE_NAME_KEY attribute of      * the given input metadata is expected to contain the name (normally      * a file name or a URL) of the input document.      *<p>      * If a resource name is given, then it is first processed as follows.      *<ol>      *<li>      *     Potential URL query (?...) and fragment identifier (#...)      *     parts are removed from the end of the resource name.      *</li>      *<li>      *     Potential leading path elements (up to the last slash or backslash)      *     are removed from the beginning of the resource name.      *</li>      *<li>      *     Potential URL encodings (%nn, in UTF-8) are decoded.      *</li>      *<li>      *     Any leading and trailing whitespace is removed.      *</li>      *</ol>      *<p>      * The resulting name string (if any) is then matched in sequence against      * all the configured name patterns. If a match is found, then the (first)      * matching media type is returned.      *      * @param input ignored      * @param metadata input metadata, possibly with a RESOURCE_NAME_KEY value      * @return detected media type, or<code>application/octet-stream</code>      */
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
block|{
comment|// Look for a resource name in the input metadata
name|String
name|name
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
comment|// If the name is a URL, skip the trailing query and fragment parts
name|int
name|question
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
if|if
condition|(
name|question
operator|!=
operator|-
literal|1
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|question
argument_list|)
expr_stmt|;
block|}
name|int
name|hash
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|'#'
argument_list|)
decl_stmt|;
if|if
condition|(
name|hash
operator|!=
operator|-
literal|1
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hash
argument_list|)
expr_stmt|;
block|}
comment|// If the name is a URL or a path, skip all but the last component
name|int
name|slash
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|slash
operator|!=
operator|-
literal|1
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|backslash
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'\\'
argument_list|)
decl_stmt|;
if|if
condition|(
name|backslash
operator|!=
operator|-
literal|1
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|backslash
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Decode any potential URL encoding
name|int
name|percent
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|'%'
argument_list|)
decl_stmt|;
if|if
condition|(
name|percent
operator|!=
operator|-
literal|1
condition|)
block|{
try|try
block|{
name|name
operator|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|name
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"UTF-8 not supported"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// Skip any leading or trailing whitespace
name|name
operator|=
name|name
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Match the name against the registered patterns
for|for
control|(
name|Pattern
name|pattern
range|:
name|patterns
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|pattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|patterns
operator|.
name|get
argument_list|(
name|pattern
argument_list|)
return|;
block|}
block|}
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

