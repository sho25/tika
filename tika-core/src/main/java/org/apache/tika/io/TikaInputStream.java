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
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

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
name|Closeable
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
name|FileOutputStream
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
name|io
operator|.
name|OutputStream
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
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
comment|/**  * Input stream with extended capabilities. The purpose of this class is  * to allow files and other resources and information to be associated with  * the {@link InputStream} instance passed through the  * {@link org.apache.tika.parser.Parser} interface and other similar APIs.  *<p>  * TikaInputStream instances can be created using the various static  *<code>get()</code> factory methods. Most of these methods take an optional  * {@link Metadata} argument that is then filled with the available input  * metadata from the given resource. The created TikaInputStream instance  * keeps track of the original resource used to create it, while behaving  * otherwise just like a normal, buffered {@link InputStream}.  * A TikaInputStream instance is also guaranteed to support the  * {@link #mark(int)} feature.  *<p>  * Code that wants to access the underlying file or other resources  * associated with a TikaInputStream should first use the  * {@link #get(InputStream)} factory method to cast or wrap a given  * {@link InputStream} into a TikaInputStream instance.  *  * @since Apache Tika 0.8  */
end_comment

begin_class
specifier|public
class|class
name|TikaInputStream
extends|extends
name|TaggedInputStream
block|{
comment|/**      * Checks whether the given stream is a TikaInputStream instance.      * The given stream can be<code>null</code>, in which case the return      * value is<code>false</code>.      *       * @param stream input stream, possibly<code>null</code>      * @return<code>true</code> if the stream is a TikaInputStream instance,      *<code>false</code> otherwise      */
specifier|public
specifier|static
name|boolean
name|isTikaInputStream
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
return|return
name|stream
operator|instanceof
name|TikaInputStream
return|;
block|}
comment|/**      * Casts or wraps the given stream to a TikaInputStream instance.      * This method can be used to access the functionality of this class      * even when given just a normal input stream instance.      *<p>      * The given temporary file provider is used for any temporary files,      * and should be disposed when the returned stream is no longer used.      *<p>      * Use this method instead of the {@link #get(InputStream)} alternative      * when you<em>don't</em> explicitly close the returned stream. The      * recommended access pattern is:      *<pre>      * try (TemporaryResources tmp = new TemporaryResources()) {      *     TikaInputStream stream = TikaInputStream.get(..., tmp);      *     // process stream but don't close it      * }      *</pre>      *<p>      * The given stream instance will<em>not</em> be closed when the      * {@link TemporaryResources#close()} method is called by the      * try-with-resources statement. The caller is expected to explicitly      * close the original stream when it's no longer used.      *      * @since Apache Tika 0.10      * @param stream normal input stream      * @return a TikaInputStream instance      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|TemporaryResources
name|tmp
parameter_list|)
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
name|NullPointerException
argument_list|(
literal|"The Stream must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|stream
operator|instanceof
name|TikaInputStream
condition|)
block|{
return|return
operator|(
name|TikaInputStream
operator|)
name|stream
return|;
block|}
else|else
block|{
comment|// Make sure that the stream is buffered and that it
comment|// (properly) supports the mark feature
if|if
condition|(
operator|!
operator|(
name|stream
operator|instanceof
name|BufferedInputStream
operator|)
operator|&&
operator|!
operator|(
name|stream
operator|instanceof
name|ByteArrayInputStream
operator|)
condition|)
block|{
name|stream
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TikaInputStream
argument_list|(
name|stream
argument_list|,
name|tmp
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
comment|/**      * Casts or wraps the given stream to a TikaInputStream instance.      * This method can be used to access the functionality of this class      * even when given just a normal input stream instance.      *<p>      * Use this method instead of the      * {@link #get(InputStream, TemporaryResources)} alternative when you      *<em>do</em> explicitly close the returned stream. The recommended      * access pattern is:      *<pre>      * try (TikaInputStream stream = TikaInputStream.get(...)) {      *     // process stream      * }      *</pre>      *<p>      * The given stream instance will be closed along with any other resources      * associated with the returned TikaInputStream instance when the      * {@link #close()} method is called by the try-with-resources statement.      *      * @param stream normal input stream      * @return a TikaInputStream instance      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|stream
argument_list|,
operator|new
name|TemporaryResources
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the given stream casts to a TikaInputStream, or      *<code>null</code> if the stream is not a TikaInputStream.      *      * @since Apache Tika 0.10      * @param stream normal input stream      * @return a TikaInputStream instance      */
specifier|public
specifier|static
name|TikaInputStream
name|cast
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
if|if
condition|(
name|stream
operator|instanceof
name|TikaInputStream
condition|)
block|{
return|return
operator|(
name|TikaInputStream
operator|)
name|stream
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Creates a TikaInputStream from the given array of bytes.      *<p>      * Note that you must always explicitly close the returned stream as in      * some cases it may end up writing the given data to a temporary file.      *      * @param data input data      * @return a TikaInputStream instance      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|data
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Creates a TikaInputStream from the given array of bytes. The length of      * the array is stored as input metadata in the given metadata instance.      *<p>      * Note that you must always explicitly close the returned stream as in      * some cases it may end up writing the given data to a temporary file.      *      * @param data input data      * @param metadata metadata instance      * @return a TikaInputStream instance      * @throws IOException      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LENGTH
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|data
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TikaInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|,
operator|new
name|TemporaryResources
argument_list|()
argument_list|,
name|data
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**      * Creates a TikaInputStream from the given file.      *<p>      * Note that you must always explicitly close the returned stream to      * prevent leaking open file handles.      *      * @param file input file      * @return a TikaInputStream instance      * @throws FileNotFoundException if the file does not exist      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
return|return
name|get
argument_list|(
name|file
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Creates a TikaInputStream from the given file. The file name and      * length are stored as input metadata in the given metadata instance.      *<p>      * Note that you must always explicitly close the returned stream to      * prevent leaking open file handles.      *      * @param file input file      * @param metadata metadata instance      * @return a TikaInputStream instance      * @throws FileNotFoundException if the file does not exist      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|File
name|file
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LENGTH
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TikaInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/**      * Creates a TikaInputStream from the given database BLOB.      *<p>      * Note that the result set containing the BLOB may need to be kept open      * until the returned TikaInputStream has been processed and closed.      * You must also always explicitly close the returned stream as in      * some cases it may end up writing the blob data to a temporary file.      *      * @param blob database BLOB      * @return a TikaInputStream instance      * @throws SQLException if BLOB data can not be accessed      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|Blob
name|blob
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|get
argument_list|(
name|blob
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Blob size threshold that limits the largest BLOB size to be      * buffered fully in memory by the {@link #get(Blob, Metadata)}      * method.      */
specifier|private
specifier|static
specifier|final
name|int
name|BLOB_SIZE_THRESHOLD
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/**      * Creates a TikaInputStream from the given database BLOB. The BLOB      * length (if available) is stored as input metadata in the given      * metadata instance.      *<p>      * Note that the result set containing the BLOB may need to be kept open      * until the returned TikaInputStream has been processed and closed.      * You must also always explicitly close the returned stream as in      * some cases it may end up writing the blob data to a temporary file.      *      * @param blob database BLOB      * @param metadata metadata instance      * @return a TikaInputStream instance      * @throws SQLException if BLOB data can not be accessed      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|Blob
name|blob
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SQLException
block|{
name|long
name|length
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|length
operator|=
name|blob
operator|.
name|length
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LENGTH
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ignore
parameter_list|)
block|{         }
comment|// Prefer an in-memory buffer for reasonably sized blobs to reduce
comment|// the likelihood of problems caused by long-lived database accesses
if|if
condition|(
literal|0
operator|<=
name|length
operator|&&
name|length
operator|<=
name|BLOB_SIZE_THRESHOLD
condition|)
block|{
comment|// the offset in Blob.getBytes() starts at 1
return|return
name|get
argument_list|(
name|blob
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|length
argument_list|)
argument_list|,
name|metadata
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TikaInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|blob
operator|.
name|getBinaryStream
argument_list|()
argument_list|)
argument_list|,
operator|new
name|TemporaryResources
argument_list|()
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
comment|/**      * Creates a TikaInputStream from the resource at the given URI.      *<p>      * Note that you must always explicitly close the returned stream as in      * some cases it may end up writing the resource to a temporary file.      *      * @param uri resource URI      * @return a TikaInputStream instance      * @throws IOException if the resource can not be accessed      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|uri
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Creates a TikaInputStream from the resource at the given URI. The      * available input metadata is stored in the given metadata instance.      *<p>      * Note that you must always explicitly close the returned stream as in      * some cases it may end up writing the resource to a temporary file.      *      * @param uri resource URI      * @param metadata metadata instance      * @return a TikaInputStream instance      * @throws IOException if the resource can not be accessed      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Special handling for file:// URIs
if|if
condition|(
literal|"file"
operator|.
name|equalsIgnoreCase
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
return|return
name|get
argument_list|(
name|file
argument_list|,
name|metadata
argument_list|)
return|;
block|}
block|}
return|return
name|get
argument_list|(
name|uri
operator|.
name|toURL
argument_list|()
argument_list|,
name|metadata
argument_list|)
return|;
block|}
comment|/**      * Creates a TikaInputStream from the resource at the given URL.      *<p>      * Note that you must always explicitly close the returned stream as in      * some cases it may end up writing the resource to a temporary file.      *      * @param url resource URL      * @return a TikaInputStream instance      * @throws IOException if the resource can not be accessed      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|url
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Creates a TikaInputStream from the resource at the given URL. The      * available input metadata is stored in the given metadata instance.      *<p>      * Note that you must always explicitly close the returned stream as in      * some cases it may end up writing the resource to a temporary file.      *      * @param url resource URL      * @param metadata metadata instance      * @return a TikaInputStream instance      * @throws IOException if the resource can not be accessed      */
specifier|public
specifier|static
name|TikaInputStream
name|get
parameter_list|(
name|URL
name|url
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Special handling for file:// URLs
if|if
condition|(
literal|"file"
operator|.
name|equalsIgnoreCase
argument_list|(
name|url
operator|.
name|getProtocol
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
return|return
name|get
argument_list|(
name|file
argument_list|,
name|metadata
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|// fall through
block|}
block|}
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|url
operator|.
name|getPath
argument_list|()
decl_stmt|;
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
comment|// works even with -1!
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|path
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|type
init|=
name|connection
operator|.
name|getContentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
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
name|type
argument_list|)
expr_stmt|;
block|}
name|String
name|encoding
init|=
name|connection
operator|.
name|getContentEncoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|encoding
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
name|CONTENT_ENCODING
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
name|int
name|length
init|=
name|connection
operator|.
name|getContentLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|>=
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LENGTH
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TikaInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|,
operator|new
name|TemporaryResources
argument_list|()
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**      * The file that contains the contents of this stream. This is either      * the original file passed to the {@link #TikaInputStream(File)}      * constructor or a temporary file created by a call to the      * {@link #getFile()} method. If neither has been called, then      * the value is<code>null</code>.      */
specifier|private
name|File
name|file
decl_stmt|;
comment|/**      * Tracker of temporary resources.      */
specifier|private
specifier|final
name|TemporaryResources
name|tmp
decl_stmt|;
comment|/**      * Total length of the stream, or -1 if unknown.      */
specifier|private
name|long
name|length
decl_stmt|;
comment|/**      * Current read position within this stream.      */
specifier|private
name|long
name|position
init|=
literal|0
decl_stmt|;
comment|/**      * Marked position, or -1 if there is no current mark.      */
specifier|private
name|long
name|mark
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * A opened container, such as a POIFS FileSystem      *  for an OLE2 document, or a Zip file for a      *  zip based (eg ooxml, odf) document.      */
specifier|private
name|Object
name|openContainer
decl_stmt|;
comment|/**      * Creates a TikaInputStream instance. This private constructor is used      * by the static factory methods based on the available information.      *      * @param file the file that contains the stream      * @throws FileNotFoundException if the file does not exist      */
specifier|private
name|TikaInputStream
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|super
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|tmp
operator|=
operator|new
name|TemporaryResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a TikaInputStream instance. This private constructor is used      * by the static factory methods based on the available information.      *<p>      * The given stream needs to be included in the given temporary resource      * collection if the caller wants it also to get closed when the      * {@link #close()} method is invoked.      *      * @param stream<em>buffered</em> stream (must support the mark feature)      * @param tmp tracker for temporary resources associated with this stream      * @param length total length of the stream, or -1 if unknown      */
specifier|private
name|TikaInputStream
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|TemporaryResources
name|tmp
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|tmp
operator|=
name|tmp
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|/**      * Fills the given buffer with upcoming bytes from this stream without      * advancing the current stream position. The buffer is filled up unless      * the end of stream is encountered before that. This method will block      * if not enough bytes are immediately available.      *      * @param buffer byte buffer      * @return number of bytes written to the buffer      * @throws IOException if the stream can not be read      */
specifier|public
name|int
name|peek
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
name|mark
argument_list|(
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|m
init|=
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
condition|)
block|{
name|n
operator|+=
name|m
expr_stmt|;
if|if
condition|(
name|n
operator|<
name|buffer
operator|.
name|length
condition|)
block|{
name|m
operator|=
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
else|else
block|{
name|m
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
name|reset
argument_list|()
expr_stmt|;
return|return
name|n
return|;
block|}
comment|/**      * Returns the open container object, such as a      *  POIFS FileSystem in the event of an OLE2      *  document being detected and processed by      *  the OLE2 detector.       */
specifier|public
name|Object
name|getOpenContainer
parameter_list|()
block|{
return|return
name|openContainer
return|;
block|}
comment|/**      * Stores the open container object against      *  the stream, eg after a Zip contents       *  detector has loaded the file to decide      *  what it contains.      */
specifier|public
name|void
name|setOpenContainer
parameter_list|(
name|Object
name|container
parameter_list|)
block|{
name|openContainer
operator|=
name|container
expr_stmt|;
if|if
condition|(
name|container
operator|instanceof
name|Closeable
condition|)
block|{
name|tmp
operator|.
name|addResource
argument_list|(
operator|(
name|Closeable
operator|)
name|container
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|hasFile
parameter_list|()
block|{
return|return
name|file
operator|!=
literal|null
return|;
block|}
specifier|public
name|File
name|getFile
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|position
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream is already being read"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Spool the entire stream into a temporary file
name|file
operator|=
name|tmp
operator|.
name|createTemporaryFile
argument_list|()
expr_stmt|;
try|try
init|(
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
comment|// Create a new input stream and make sure it'll get closed
name|FileInputStream
name|newStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|addResource
argument_list|(
name|newStream
argument_list|)
expr_stmt|;
comment|// Replace the spooled stream with the new stream in a way
comment|// that still ends up closing the old stream if or when the
comment|// close() method is called. The closing of the new stream
comment|// is already being handled as noted above.
specifier|final
name|InputStream
name|oldStream
init|=
name|in
decl_stmt|;
name|in
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|newStream
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|oldStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|length
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|file
return|;
block|}
specifier|public
name|FileChannel
name|getFileChannel
parameter_list|()
throws|throws
name|IOException
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|addResource
argument_list|(
name|fis
argument_list|)
expr_stmt|;
name|FileChannel
name|channel
init|=
name|fis
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|addResource
argument_list|(
name|channel
argument_list|)
expr_stmt|;
return|return
name|channel
return|;
block|}
specifier|public
name|boolean
name|hasLength
parameter_list|()
block|{
return|return
name|length
operator|!=
operator|-
literal|1
return|;
block|}
comment|/**      * Returns the length (in bytes) of this stream. Note that if the length      * was not available when this stream was instantiated, then this method      * will use the {@link #getFile()} method to buffer the entire stream to      * a temporary file in order to calculate the stream length. This case      * will only work if the stream has not yet been consumed.      *      * @return stream length      * @throws IOException if the length can not be determined      */
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
block|{
name|length
operator|=
name|getFile
argument_list|()
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
return|return
name|length
return|;
block|}
comment|/**      * Returns the current position within the stream.      *      * @return stream position      */
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|ln
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|n
init|=
name|super
operator|.
name|skip
argument_list|(
name|ln
argument_list|)
decl_stmt|;
name|position
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
name|super
operator|.
name|mark
argument_list|(
name|readlimit
argument_list|)
expr_stmt|;
name|mark
operator|=
name|position
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|position
operator|=
name|mark
expr_stmt|;
name|mark
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|=
literal|null
expr_stmt|;
name|mark
operator|=
operator|-
literal|1
expr_stmt|;
comment|// The close method was explicitly called, so we indeed
comment|// are expected to close the input stream. Handle that
comment|// by adding that stream as a resource to be tracked before
comment|// closing all of them. This way also possible exceptions from
comment|// the close() calls get managed properly.
name|tmp
operator|.
name|addResource
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterRead
parameter_list|(
name|int
name|n
parameter_list|)
block|{
if|if
condition|(
name|n
operator|!=
operator|-
literal|1
condition|)
block|{
name|position
operator|+=
name|n
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|str
init|=
literal|"TikaInputStream of "
decl_stmt|;
if|if
condition|(
name|hasFile
argument_list|()
condition|)
block|{
name|str
operator|+=
name|file
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|str
operator|+=
name|in
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|openContainer
operator|!=
literal|null
condition|)
block|{
name|str
operator|+=
literal|" (in "
operator|+
name|openContainer
operator|+
literal|")"
expr_stmt|;
block|}
return|return
name|str
return|;
block|}
block|}
end_class

end_unit

