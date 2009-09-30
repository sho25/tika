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
name|mime
package|;
end_package

begin_comment
comment|// JDK imports
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
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
name|Detector
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
name|XmlRootExtractor
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

begin_comment
comment|/**  * This class is a MimeType repository. It gathers a set of MimeTypes and  * enables to retrieves a content-type from its name, from a file name, or from  * a magic character sequence.  *<p>  * The MIME type detection methods that take an {@link InputStream} as  * an argument will never reads more than {@link #getMinLength()} bytes  * from the stream. Also the given stream is never  * {@link InputStream#close() closed}, {@link InputStream#mark(int) marked},  * or {@link InputStream#reset() reset} by the methods. Thus a client can  * use the {@link InputStream#markSupported() mark feature} of the stream  * (if available) to restore the stream back to the state it was before type  * detection if it wants to process the stream based on the detected type.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MimeTypes
implements|implements
name|Detector
block|{
comment|/**      * Name of the {@link #rootMimeType root} type, application/octet-stream.      */
specifier|public
specifier|final
specifier|static
name|String
name|OCTET_STREAM
init|=
literal|"application/octet-stream"
decl_stmt|;
comment|/**      * Name of the {@link #textMimeType text} type, text/plain.      */
specifier|public
specifier|final
specifier|static
name|String
name|PLAIN_TEXT
init|=
literal|"text/plain"
decl_stmt|;
comment|/**      * Name of the {@link #xml xml} type, application/xml.      */
specifier|public
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"application/xml"
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
comment|/**      * Root type, application/octet-stream.      */
specifier|private
specifier|final
name|MimeType
name|rootMimeType
decl_stmt|;
comment|/**      * Text type, text/plain.      */
specifier|private
specifier|final
name|MimeType
name|textMimeType
decl_stmt|;
comment|/*      * xml type, application/xml      */
specifier|private
specifier|final
name|MimeType
name|xmlMimeType
decl_stmt|;
comment|/** All the registered MimeTypes indexed on their name */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MimeType
argument_list|>
name|types
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MimeType
argument_list|>
argument_list|()
decl_stmt|;
comment|/** The patterns matcher */
specifier|private
name|Patterns
name|patterns
init|=
operator|new
name|Patterns
argument_list|()
decl_stmt|;
comment|/** List of all registered magics */
specifier|private
name|SortedSet
argument_list|<
name|Magic
argument_list|>
name|magics
init|=
operator|new
name|TreeSet
argument_list|<
name|Magic
argument_list|>
argument_list|()
decl_stmt|;
comment|/** List of all registered rootXML */
specifier|private
name|SortedSet
argument_list|<
name|MimeType
argument_list|>
name|xmls
init|=
operator|new
name|TreeSet
argument_list|<
name|MimeType
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|XmlRootExtractor
name|xmlRootExtractor
decl_stmt|;
specifier|public
name|MimeTypes
parameter_list|()
block|{
name|rootMimeType
operator|=
operator|new
name|MimeType
argument_list|(
name|this
argument_list|,
name|OCTET_STREAM
argument_list|)
expr_stmt|;
name|textMimeType
operator|=
operator|new
name|MimeType
argument_list|(
name|this
argument_list|,
name|PLAIN_TEXT
argument_list|)
expr_stmt|;
name|xmlMimeType
operator|=
operator|new
name|MimeType
argument_list|(
name|this
argument_list|,
name|XML
argument_list|)
expr_stmt|;
try|try
block|{
name|textMimeType
operator|.
name|setSuperType
argument_list|(
name|rootMimeType
argument_list|)
expr_stmt|;
name|xmlMimeType
operator|.
name|setSuperType
argument_list|(
name|rootMimeType
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Error in MimeType logic"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|types
operator|.
name|put
argument_list|(
name|rootMimeType
operator|.
name|getName
argument_list|()
argument_list|,
name|rootMimeType
argument_list|)
expr_stmt|;
name|types
operator|.
name|put
argument_list|(
name|textMimeType
operator|.
name|getName
argument_list|()
argument_list|,
name|textMimeType
argument_list|)
expr_stmt|;
name|types
operator|.
name|put
argument_list|(
name|xmlMimeType
operator|.
name|getName
argument_list|()
argument_list|,
name|xmlMimeType
argument_list|)
expr_stmt|;
try|try
block|{
name|xmlRootExtractor
operator|=
operator|new
name|XmlRootExtractor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to create a XmlRootExtractor"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Find the Mime Content Type of a file.      *      * @param file      *            to analyze.      * @return the Mime Content Type of the specified file, or<code>null</code>      *         if none is found.      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|getMimeType
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Find the Mime Content Type of a document from its URL.      *      * @param url      *            of the document to analyze.      * @return the Mime Content Type of the specified document URL, or      *<code>null</code> if none is found.      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
return|return
name|getMimeType
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Find the Mime Content Type of a document from its name.      * Returns application/octet-stream if no better match is found.      *      * @param name of the document to analyze.      * @return the Mime Content Type of the specified document name      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MimeType
name|type
init|=
name|patterns
operator|.
name|matches
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
return|return
name|type
return|;
block|}
name|type
operator|=
name|patterns
operator|.
name|matches
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
return|return
name|type
return|;
block|}
else|else
block|{
return|return
name|rootMimeType
return|;
block|}
block|}
comment|/**      * Returns the MIME type that best matches the given first few bytes      * of a document stream. Returns application/octet-stream if no better      * match is found.      *<p>      * The given byte array is expected to be at least {@link #getMinLength()}      * long, or shorter only if the document stream itself is shorter.      *      * @param data first few bytes of a document stream      * @return matching MIME type      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Data is missing"
argument_list|)
throw|;
block|}
comment|// First, check for XML descriptions (level by level)
comment|// Problem: Regexp matching doesn't work for all XML encodings
for|for
control|(
name|MimeType
name|type
range|:
name|xmls
control|)
block|{
if|if
condition|(
name|type
operator|.
name|matchesXML
argument_list|(
name|data
argument_list|)
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
comment|// Then, check for magic bytes
name|MimeType
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Magic
name|magic
range|:
name|magics
control|)
block|{
if|if
condition|(
name|magic
operator|.
name|eval
argument_list|(
name|data
argument_list|)
condition|)
block|{
name|result
operator|=
name|magic
operator|.
name|getType
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
comment|// When detecting generic XML, parse XML to determine the root element
if|if
condition|(
literal|"application/xml"
operator|.
name|equals
argument_list|(
name|result
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|QName
name|rootElement
init|=
name|xmlRootExtractor
operator|.
name|extractRootElement
argument_list|(
name|data
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootElement
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|MimeType
name|type
range|:
name|xmls
control|)
block|{
if|if
condition|(
name|type
operator|.
name|matchesXML
argument_list|(
name|rootElement
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|rootElement
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
name|type
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|// Finally, assume plain text if no control bytes are found
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
name|data
index|[
name|i
index|]
operator|&
literal|0xFF
decl_stmt|;
comment|// prevent sign extension
if|if
condition|(
name|b
operator|<
name|IS_CONTROL_BYTE
operator|.
name|length
operator|&&
name|IS_CONTROL_BYTE
index|[
name|b
index|]
condition|)
block|{
return|return
name|rootMimeType
return|;
block|}
block|}
return|return
name|textMimeType
return|;
block|}
comment|/**      * Returns the MIME type that best matches the first few bytes of the      * given document stream.      *      * @see #getMimeType(byte[])      * @param stream document stream      * @return matching MIME type, or<code>null</code> if no match is found      * @throws IOException if the stream can be read      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMimeType
argument_list|(
name|readMagicHeader
argument_list|(
name|stream
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Reads the first {@link #getMinLength()} bytes from the given stream.      * If the stream is shorter, then the entire content of the stream is      * returned.      *<p>      * The given stream is never {@link InputStream#close() closed},      * {@link InputStream#mark(int) marked}, or      * {@link InputStream#reset() reset} by this method.      *      * @param stream stream to be read      * @return first {@link #getMinLength()} (or fewer) bytes of the stream      * @throws IOException if the stream can not be read      */
specifier|private
name|byte
index|[]
name|readMagicHeader
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"InputStream is missing"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|getMinLength
argument_list|()
index|]
decl_stmt|;
name|int
name|totalRead
init|=
literal|0
decl_stmt|;
name|int
name|lastRead
init|=
name|stream
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
while|while
condition|(
name|lastRead
operator|!=
operator|-
literal|1
condition|)
block|{
name|totalRead
operator|+=
name|lastRead
expr_stmt|;
if|if
condition|(
name|totalRead
operator|==
name|bytes
operator|.
name|length
condition|)
block|{
return|return
name|bytes
return|;
block|}
name|lastRead
operator|=
name|stream
operator|.
name|read
argument_list|(
name|bytes
argument_list|,
name|totalRead
argument_list|,
name|bytes
operator|.
name|length
operator|-
name|totalRead
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|shorter
init|=
operator|new
name|byte
index|[
name|totalRead
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|shorter
argument_list|,
literal|0
argument_list|,
name|totalRead
argument_list|)
expr_stmt|;
return|return
name|shorter
return|;
block|}
specifier|public
name|String
name|getType
parameter_list|(
name|String
name|typeName
parameter_list|,
name|String
name|url
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|typeName
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|typeName
argument_list|)
expr_stmt|;
block|}
return|return
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|,
name|metadata
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
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ByteArrayInputStream throws an IOException!"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Determines the MIME type of the resource pointed to by the specified URL.      * Examines the file's header, and if it cannot determine the MIME type      * from the header, guesses the MIME type from the URL extension      * (e.g. "pdf).      *      * @param url URL of the document      * @return type of the document      * @throws IOException if the document can not be accessed      */
specifier|public
name|String
name|getType
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
name|url
operator|.
name|openStream
argument_list|()
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|detect
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Find the Mime Content Type of a document from its name and its content.      * The policy used to guess the Mime Content Type is:      *<ol>      *<li>Try to find the type based on the provided data.</li>      *<li>If a type is found, then return it, otherwise try to find the type      * based on the file name</li>      *</ol>      *      * @param name      *            of the document to analyze.      * @param data      *            are the first bytes of the document's content.      * @return the Mime Content Type of the specified document, or      *<code>null</code> if none is found.      * @see #getMinLength()      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
comment|// First, try to get the mime-type from the content
name|MimeType
name|mimeType
init|=
name|getMimeType
argument_list|(
name|data
argument_list|)
decl_stmt|;
comment|// If no mime-type found, then try to get the mime-type from
comment|// the document name
if|if
condition|(
name|mimeType
operator|==
literal|null
condition|)
block|{
name|mimeType
operator|=
name|getMimeType
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|mimeType
return|;
block|}
comment|/**      * Returns the MIME type that best matches the given document name and      * the first few bytes of the given document stream.      *      * @see #getMimeType(String, byte[])      * @param name document name      * @param stream document stream      * @return matching MIME type, or<code>null</code> if no match is found      * @throws IOException if the stream can not be read      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|String
name|name
parameter_list|,
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMimeType
argument_list|(
name|name
argument_list|,
name|readMagicHeader
argument_list|(
name|stream
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the registered media type with the given name (or alias).      * The named media type is automatically registered (and returned) if      * it doesn't already exist.      *      * @param name media type name (case-insensitive)      * @return the registered media type with the given name or alias      * @throws MimeTypeException if the given media type name is invalid      */
specifier|public
specifier|synchronized
name|MimeType
name|forName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MimeTypeException
block|{
if|if
condition|(
name|MimeType
operator|.
name|isValid
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
name|MimeType
name|type
init|=
name|types
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
operator|new
name|MimeType
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"text/"
argument_list|)
condition|)
block|{
name|type
operator|.
name|setSuperType
argument_list|(
name|textMimeType
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|"+xml"
argument_list|)
condition|)
block|{
name|type
operator|.
name|setSuperType
argument_list|(
name|xmlMimeType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|.
name|setSuperType
argument_list|(
name|rootMimeType
argument_list|)
expr_stmt|;
block|}
name|types
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
return|return
name|type
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Invalid media type name: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
comment|/**      * Adds an alias for the given media type. This method should only      * be called from {@link MimeType#addAlias(String)}.      *      * @param type media type      * @param alias media type alias (normalized to lower case)      * @throws MimeTypeException if the alias already exists      */
specifier|synchronized
name|void
name|addAlias
parameter_list|(
name|MimeType
name|type
parameter_list|,
name|String
name|alias
parameter_list|)
throws|throws
name|MimeTypeException
block|{
if|if
condition|(
operator|!
name|types
operator|.
name|containsKey
argument_list|(
name|alias
argument_list|)
condition|)
block|{
name|types
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|MimeTypeException
argument_list|(
literal|"Media type alias already exists: "
operator|+
name|alias
argument_list|)
throw|;
block|}
block|}
comment|/**      * Adds a file name pattern for the given media type. Assumes that the      * pattern being added is<b>not</b> a JDK standard regular expression.      *      * @param type      *            media type      * @param pattern      *            file name pattern      * @throws MimeTypeException      *             if the pattern conflicts with existing ones      */
specifier|public
name|void
name|addPattern
parameter_list|(
name|MimeType
name|type
parameter_list|,
name|String
name|pattern
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|this
operator|.
name|addPattern
argument_list|(
name|type
argument_list|,
name|pattern
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a file name pattern for the given media type. The caller can specify      * whether the pattern being added<b>is</b> or<b>is not</b> a JDK standard      * regular expression via the<code>isRegex</code> parameter. If the value      * is set to true, then a JDK standard regex is assumed, otherwise the      * freedesktop glob type is assumed.      *      * @param type      *            media type      * @param pattern      *            file name pattern      * @param isRegex      *            set to true if JDK std regexs are desired, otherwise set to      *            false.      * @throws MimeTypeException      *             if the pattern conflicts with existing ones.      *      */
specifier|public
name|void
name|addPattern
parameter_list|(
name|MimeType
name|type
parameter_list|,
name|String
name|pattern
parameter_list|,
name|boolean
name|isRegex
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|patterns
operator|.
name|add
argument_list|(
name|pattern
argument_list|,
name|isRegex
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the minimum length of data to provide to analyzing methods based      * on the document's content in order to check all the known MimeTypes.      *      * @return the minimum length of data to provide.      * @see #getMimeType(byte[])      * @see #getMimeType(String, byte[])      */
specifier|public
name|int
name|getMinLength
parameter_list|()
block|{
return|return
literal|1024
return|;
comment|// return minLength;
block|}
comment|/**      * Add the specified mime-type in the repository.      *      * @param type      *            is the mime-type to add.      */
name|void
name|add
parameter_list|(
name|MimeType
name|type
parameter_list|)
block|{
comment|// Update the magics index...
if|if
condition|(
name|type
operator|.
name|hasMagic
argument_list|()
condition|)
block|{
name|magics
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getMagics
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Update the xml (xmlRoot) index...
if|if
condition|(
name|type
operator|.
name|hasRootXML
argument_list|()
condition|)
block|{
name|xmls
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Automatically detects the MIME type of a document based on magic      * markers in the stream prefix and any given metadata hints.      *<p>      * The given stream is expected to support marks, so that this method      * can reset the stream to the position it was in before this method      * was called.      *      * @param input document stream, or<code>null</code>      * @param metadata metadata hints      * @return MIME type of the document      * @throws IOException if the document stream could not be read      */
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
name|MimeType
name|type
init|=
name|rootMimeType
decl_stmt|;
comment|// Get type based on magic prefix
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|mark
argument_list|(
name|getMinLength
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|prefix
init|=
name|readMagicHeader
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|type
operator|=
name|getMimeType
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Get type based on resourceName hint (if available)
name|String
name|resourceName
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
name|resourceName
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
literal|null
decl_stmt|;
comment|// Deal with a URI or a path name in as the resource  name
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|int
name|slash
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|slash
operator|+
literal|1
operator|<
name|path
operator|.
name|length
argument_list|()
condition|)
block|{
name|name
operator|=
name|path
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|name
operator|=
name|resourceName
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|MimeType
name|hint
init|=
name|getMimeType
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|hint
operator|.
name|isDescendantOf
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|type
operator|=
name|hint
expr_stmt|;
block|}
block|}
block|}
comment|// Get type based on metadata hint (if available)
name|String
name|typeName
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
name|typeName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|MimeType
name|hint
init|=
name|forName
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|hint
operator|.
name|isDescendantOf
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|type
operator|=
name|hint
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MimeTypeException
name|e
parameter_list|)
block|{
comment|// Malformed type name, ignore
block|}
block|}
return|return
name|MediaType
operator|.
name|parse
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

