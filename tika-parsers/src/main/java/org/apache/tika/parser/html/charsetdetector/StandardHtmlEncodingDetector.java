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
operator|.
name|charsetdetector
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|BoundedInputStream
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
name|Field
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
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|html
operator|.
name|charsetdetector
operator|.
name|CharsetAliases
operator|.
name|getCharsetByLabel
import|;
end_import

begin_comment
comment|/**  * An encoding detector that tries to respect the spirit of the HTML spec  * part 12.2.3 "The input byte stream", or at least the part that is compatible with  * the implementation of tika.  *<p>  * https://html.spec.whatwg.org/multipage/parsing.html#the-input-byte-stream  *<p>  * If a resource was fetched over HTTP, then HTTP headers should be added to tika metadata  * when using {@link #detect}, especially {@link Metadata#CONTENT_TYPE}, as it may contain charset information.  *<p>  * This encoding detector may return null if no encoding is detected.  * It is meant to be used inside a {@link org.apache.tika.detect.CompositeDetector}.  * For instance:  *<pre> {@code  *     EncodingDetector detector = new CompositeDetector(  *         new StandardHtmlEncodingDetector(),  *         new Icu4jEncodingDetector()  *     );  * }</pre>  *<p>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|StandardHtmlEncodingDetector
implements|implements
name|EncodingDetector
block|{
specifier|private
specifier|static
specifier|final
name|int
name|META_TAG_BUFFER_SIZE
init|=
literal|8192
decl_stmt|;
annotation|@
name|Field
specifier|private
name|int
name|markLimit
init|=
name|META_TAG_BUFFER_SIZE
decl_stmt|;
comment|/**      * Extracts a charset from a Content-Type HTTP header.      *      * @param metadata parser metadata      * @return a charset if there is one specified, or null      */
specifier|private
specifier|static
name|Charset
name|charsetFromContentType
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|String
name|contentType
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
name|MediaType
name|mediatype
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|mediatype
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|charsetLabel
init|=
name|mediatype
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
literal|"charset"
argument_list|)
decl_stmt|;
return|return
name|getCharsetByLabel
argument_list|(
name|charsetLabel
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|int
name|limit
init|=
name|getMarkLimit
argument_list|()
decl_stmt|;
name|input
operator|.
name|mark
argument_list|(
name|limit
argument_list|)
expr_stmt|;
comment|// Never read more than the first META_TAG_BUFFER_SIZE bytes
name|InputStream
name|limitedStream
init|=
operator|new
name|BoundedInputStream
argument_list|(
name|input
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|PreScanner
name|preScanner
init|=
operator|new
name|PreScanner
argument_list|(
name|limitedStream
argument_list|)
decl_stmt|;
comment|// The order of priority for detection is:
comment|// 1. Byte Order Mark
name|Charset
name|detectedCharset
init|=
name|preScanner
operator|.
name|detectBOM
argument_list|()
decl_stmt|;
comment|// 2. Transport-level information (Content-Type HTTP header)
if|if
condition|(
name|detectedCharset
operator|==
literal|null
condition|)
name|detectedCharset
operator|=
name|charsetFromContentType
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
comment|// 3. HTML<meta> tag
if|if
condition|(
name|detectedCharset
operator|==
literal|null
condition|)
name|detectedCharset
operator|=
name|preScanner
operator|.
name|scan
argument_list|()
expr_stmt|;
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|detectedCharset
return|;
block|}
specifier|public
name|int
name|getMarkLimit
parameter_list|()
block|{
return|return
name|markLimit
return|;
block|}
comment|/**      * How far into the stream to read for charset detection.      * Default is 8192.      */
annotation|@
name|Field
specifier|public
name|void
name|setMarkLimit
parameter_list|(
name|int
name|markLimit
parameter_list|)
block|{
name|this
operator|.
name|markLimit
operator|=
name|markLimit
expr_stmt|;
block|}
block|}
end_class

end_unit
