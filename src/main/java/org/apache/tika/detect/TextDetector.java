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
name|util
operator|.
name|Arrays
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

begin_comment
comment|/**  * Content type detection of plain text documents. This detector looks at the  * beginning of the document input stream and considers the document to be  * a text document if no ASCII (ISO-Latin-1, UTF-8, etc.) control bytes are  * found.  *<p>  * Note that text documents with a character encoding like UTF-16 are better  * detected with {@link MagicDetector} and an appropriate magic byte pattern.  *  * @since Apache Tika 0.3  */
end_comment

begin_class
specifier|public
class|class
name|TextDetector
implements|implements
name|Detector
block|{
comment|/**      * The number of bytes from the beginning of the document stream      * to test for control bytes.      */
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_OF_BYTES_TO_TEST
init|=
literal|512
decl_stmt|;
comment|/**      * Lookup table for all the ASCII/ISO-Latin/UTF-8/etc. control bytes      * in the range below 0x20 (the space character). If an entry in this      * table is<code>true</code> then that byte is very unlikely to occur      * in a plain text document.      *<p>      * The contents of this lookup table are based on the following definition      * from section 4 of the "Content-Type Processing Model" Internet-draft      * (<a href="http://webblaze.cs.berkeley.edu/2009/mime-sniff/mime-sniff.txt"      *>draft-abarth-mime-sniff-01</a>).      *<pre>      * +-------------------------+      * | Binary data byte ranges |      * +-------------------------+      * | 0x00 -- 0x08            |      * | 0x0B                    |      * | 0x0E -- 0x1A            |      * | 0x1C -- 0x1F            |      * +-------------------------+      *</pre>      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-154">TIKA-154</a>      */
specifier|private
specifier|static
specifier|final
name|boolean
index|[]
name|IS_CONTROL_BYTE
init|=
operator|new
name|boolean
index|[
literal|0x20
index|]
decl_stmt|;
static|static
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|IS_CONTROL_BYTE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IS_CONTROL_BYTE
index|[
literal|0x09
index|]
operator|=
literal|false
expr_stmt|;
comment|// tabulator
name|IS_CONTROL_BYTE
index|[
literal|0x0A
index|]
operator|=
literal|false
expr_stmt|;
comment|// new line
name|IS_CONTROL_BYTE
index|[
literal|0x0C
index|]
operator|=
literal|false
expr_stmt|;
comment|// new page
name|IS_CONTROL_BYTE
index|[
literal|0x0D
index|]
operator|=
literal|false
expr_stmt|;
comment|// carriage return
name|IS_CONTROL_BYTE
index|[
literal|0x1B
index|]
operator|=
literal|false
expr_stmt|;
comment|// escape
block|}
comment|/**      * Looks at the beginning of the document input stream to determine      * whether the document is text or not.      *      * @param input document input stream, or<code>null</code>      * @param metadata ignored      * @return "text/plain" if the input stream suggest a text document,      *         "application/octet-stream" otherwise      */
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
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMBER_OF_BYTES_TO_TEST
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ch
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|MediaType
operator|.
name|TEXT_PLAIN
return|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|<
name|IS_CONTROL_BYTE
operator|.
name|length
operator|&&
name|IS_CONTROL_BYTE
index|[
name|ch
index|]
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
return|return
name|MediaType
operator|.
name|TEXT_PLAIN
return|;
block|}
block|}
end_class

end_unit

