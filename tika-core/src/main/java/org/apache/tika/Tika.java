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
name|io
operator|.
name|Reader
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|TikaConfig
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
name|DefaultDetector
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
name|exception
operator|.
name|TikaException
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
name|parser
operator|.
name|AutoDetectParser
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
name|parser
operator|.
name|ParseContext
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
name|parser
operator|.
name|Parser
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
name|parser
operator|.
name|ParsingReader
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
name|sax
operator|.
name|BodyContentHandler
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
name|sax
operator|.
name|WriteOutContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Facade class for accessing Tika functionality. This class hides much of  * the underlying complexity of the lower level Tika classes and provides  * simple methods for many common parsing and type detection operations.  *  * @since Apache Tika 0.5  * @see Parser  * @see Detector  */
end_comment

begin_class
specifier|public
class|class
name|Tika
block|{
comment|/**      * The detector instance used by this facade.      */
specifier|private
specifier|final
name|Detector
name|detector
decl_stmt|;
comment|/**      * The parser instance used by this facade.      */
specifier|private
specifier|final
name|Parser
name|parser
decl_stmt|;
comment|/**      * Maximum length of the strings returned by the parseToString methods.      * Used to prevent out of memory problems with huge input documents.      * The default setting is 100k characters.      */
specifier|private
name|int
name|maxStringLength
init|=
literal|100
operator|*
literal|1000
decl_stmt|;
comment|/**      * Creates a Tika facade using the given detector and parser instances.      *      * @since Apache Tika 0.8      * @param detector type detector      * @param parser document parser      */
specifier|public
name|Tika
parameter_list|(
name|Detector
name|detector
parameter_list|,
name|Parser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|detector
operator|=
name|detector
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
comment|/**      * Creates a Tika facade using the given configuration.      *      * @param config Tika configuration      */
specifier|public
name|Tika
parameter_list|(
name|TikaConfig
name|config
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|DefaultDetector
argument_list|(
name|config
operator|.
name|getMimeRepository
argument_list|()
argument_list|)
argument_list|,
operator|new
name|AutoDetectParser
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a Tika facade using the default configuration.      */
specifier|public
name|Tika
parameter_list|()
block|{
name|this
argument_list|(
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a Tika facade using the given detector instance and the      * default parser configuration.      *      * @since Apache Tika 0.8      * @param detector type detector      */
specifier|public
name|Tika
parameter_list|(
name|Detector
name|detector
parameter_list|)
block|{
name|this
argument_list|(
name|detector
argument_list|,
operator|new
name|AutoDetectParser
argument_list|(
name|detector
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Detects the media type of the given document. The type detection is      * based on the content of the given document stream and any given      * document metadata. The document stream can be<code>null</code>,      * in which case only the given document metadata is used for type      * detection.      *<p>      * If the document stream supports the      * {@link InputStream#markSupported() mark feature}, then the stream is      * marked and reset to the original position before this method returns.      * Only a limited number of bytes are read from the stream.      *<p>      * The given document stream is<em>not</em> closed by this method.      *<p>      * Unlike in the {@link #parse(InputStream, Metadata)} method, the      * given document metadata is<em>not</em> modified by this method.      *      * @param stream the document stream, or<code>null</code>      * @param metadata document metadata      * @return detected media type      * @throws IOException if the stream can not be read      */
specifier|public
name|String
name|detect
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|stream
operator|==
literal|null
operator|||
name|stream
operator|.
name|markSupported
argument_list|()
condition|)
block|{
return|return
name|detector
operator|.
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
else|else
block|{
return|return
name|detector
operator|.
name|detect
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|)
argument_list|,
name|metadata
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Detects the media type of the given document. The type detection is      * based on the content of the given document stream and the name of the      * document.      *<p>      * If the document stream supports the      * {@link InputStream#markSupported() mark feature}, then the stream is      * marked and reset to the original position before this method returns.      * Only a limited number of bytes are read from the stream.      *<p>      * The given document stream is<em>not</em> closed by this method.      *      * @since Apache Tika 0.9      * @param stream the document stream      * @param name document name      * @return detected media type      * @throws IOException if the stream can not be read      */
specifier|public
name|String
name|detect
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
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
name|name
argument_list|)
expr_stmt|;
return|return
name|detect
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
return|;
block|}
comment|/**      * Detects the media type of the given document. The type detection is      * based on the content of the given document stream.      *<p>      * If the document stream supports the      * {@link InputStream#markSupported() mark feature}, then the stream is      * marked and reset to the original position before this method returns.      * Only a limited number of bytes are read from the stream.      *<p>      * The given document stream is<em>not</em> closed by this method.      *      * @param stream the document stream      * @return detected media type      * @throws IOException if the stream can not be read      */
specifier|public
name|String
name|detect
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|detect
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Detects the media type of the given document. The type detection is      * based on the first few bytes of a document and the document name.      *<p>      * For best results at least a few kilobytes of the document data      * are needed. See also the other detect() methods for better      * alternatives when you have more than just the document prefix      * available for type detection.      *      * @since Apache Tika 0.9      * @param prefix first few bytes of the document      * @param name document name      * @return detected media type      */
specifier|public
name|String
name|detect
parameter_list|(
name|byte
index|[]
name|prefix
parameter_list|,
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|InputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|detect
argument_list|(
name|stream
argument_list|,
name|name
argument_list|)
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
literal|"Unexpected IOException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Detects the media type of the given document. The type detection is      * based on the first few bytes of a document.      *<p>      * For best results at least a few kilobytes of the document data      * are needed. See also the other detect() methods for better      * alternatives when you have more than just the document prefix      * available for type detection.      *      * @since Apache Tika 0.9      * @param prefix first few bytes of the document      * @return detected media type      */
specifier|public
name|String
name|detect
parameter_list|(
name|byte
index|[]
name|prefix
parameter_list|)
block|{
try|try
block|{
name|InputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|detect
argument_list|(
name|stream
argument_list|)
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
literal|"Unexpected IOException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Detects the media type of the given file. The type detection is      * based on the document content and a potential known file extension.      *<p>      * Use the {@link #detect(String)} method when you want to detect the      * type of the document without actually accessing the file.      *      * @param file the file      * @return detected media type      * @throws IOException if the file can not be read      */
specifier|public
name|String
name|detect
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|detect
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Detects the media type of the resource at the given URL. The type      * detection is based on the document content and a potential known      * file extension included in the URL.      *<p>      * Use the {@link #detect(String)} method when you want to detect the      * type of the document without actually accessing the URL.      *      * @param url the URL of the resource      * @return detected media type      * @throws IOException if the resource can not be read      */
specifier|public
name|String
name|detect
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|url
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|detect
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
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
comment|/**      * Detects the media type of a document with the given file name.      * The type detection is based on known file name extensions.      *<p>      * The given name can also be a URL or a full file path. In such cases      * only the file name part of the string is used for type detection.       *      * @param name the file name of the document      * @return detected media type      */
specifier|public
name|String
name|detect
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|detect
argument_list|(
operator|(
name|InputStream
operator|)
literal|null
argument_list|,
name|name
argument_list|)
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
literal|"Unexpected IOException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Parses the given document and returns the extracted text content.      * Input metadata like a file name or a content type hint can be passed      * in the given metadata instance. Metadata information extracted from      * the document is returned in that same metadata instance.      *<p>      * The returned reader will be responsible for closing the given stream.      * The stream and any associated resources will be closed at or before      * the time when the {@link Reader#close()} method is called.      *      * @param stream the document to be parsed      * @param metadata document metadata      * @return extracted text content      * @throws IOException if the document can not be read or parsed      */
specifier|public
name|Reader
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
return|return
operator|new
name|ParsingReader
argument_list|(
name|parser
argument_list|,
name|stream
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|/**      * Parses the given document and returns the extracted text content.      *<p>      * The returned reader will be responsible for closing the given stream.      * The stream and any associated resources will be closed at or before      * the time when the {@link Reader#close()} method is called.      *      * @param stream the document to be parsed      * @return extracted text content      * @throws IOException if the document can not be read or parsed      */
specifier|public
name|Reader
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Parses the given file and returns the extracted text content.      *      * @param file the file to be parsed      * @return extracted text content      * @throws IOException if the file can not be read or parsed      */
specifier|public
name|Reader
name|parse
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parse
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Parses the resource at the given URL and returns the extracted      * text content.      *      * @param url the URL of the resource to be parsed      * @return extracted text content      * @throws IOException if the resource can not be read or parsed      */
specifier|public
name|Reader
name|parse
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|url
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
return|return
name|parse
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
return|;
block|}
comment|/**      * Parses the given document and returns the extracted text content.      * The given input stream is closed by this method.      *<p>      * To avoid unpredictable excess memory use, the returned string contains      * only up to {@link #getMaxStringLength()} first characters extracted      * from the input document. Use the {@link #setMaxStringLength(int)}      * method to adjust this limitation.      *<p>      *<strong>NOTE:</strong> Unlike most other Tika methods that take an      * {@link InputStream}, this method will close the given stream for      * you as a convenience. With other methods you are still responsible      * for closing the stream or a wrapper instance returned by Tika.      *      * @param stream the document to be parsed      * @param metadata document metadata      * @return extracted text content      * @throws IOException if the document can not be read      * @throws TikaException if the document can not be parsed      */
specifier|public
name|String
name|parseToString
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|WriteOutContentHandler
name|handler
init|=
operator|new
name|WriteOutContentHandler
argument_list|(
name|maxStringLength
argument_list|)
decl_stmt|;
try|try
block|{
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|BodyContentHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|handler
operator|.
name|isWriteLimitReached
argument_list|(
name|e
argument_list|)
condition|)
block|{
comment|// This should never happen with BodyContentHandler...
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unexpected SAX processing failure"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|handler
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Parses the given document and returns the extracted text content.      * The given input stream is closed by this method.      *<p>      * To avoid unpredictable excess memory use, the returned string contains      * only up to {@link #getMaxStringLength()} first characters extracted      * from the input document. Use the {@link #setMaxStringLength(int)}      * method to adjust this limitation.      *<p>      *<strong>NOTE:</strong> Unlike most other Tika methods that take an      * {@link InputStream}, this method will close the given stream for      * you as a convenience. With other methods you are still responsible      * for closing the stream or a wrapper instance returned by Tika.      *      * @param stream the document to be parsed      * @return extracted text content      * @throws IOException if the document can not be read      * @throws TikaException if the document can not be parsed      */
specifier|public
name|String
name|parseToString
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
return|return
name|parseToString
argument_list|(
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Parses the given file and returns the extracted text content.      *<p>      * To avoid unpredictable excess memory use, the returned string contains      * only up to {@link #getMaxStringLength()} first characters extracted      * from the input document. Use the {@link #setMaxStringLength(int)}      * method to adjust this limitation.      *      * @param file the file to be parsed      * @return extracted text content      * @throws IOException if the file can not be read      * @throws TikaException if the file can not be parsed      */
specifier|public
name|String
name|parseToString
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
return|return
name|parseToString
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Parses the resource at the given URL and returns the extracted      * text content.      *<p>      * To avoid unpredictable excess memory use, the returned string contains      * only up to {@link #getMaxStringLength()} first characters extracted      * from the input document. Use the {@link #setMaxStringLength(int)}      * method to adjust this limitation.      *      * @param url the URL of the resource to be parsed      * @return extracted text content      * @throws IOException if the resource can not be read      * @throws TikaException if the resource can not be parsed      */
specifier|public
name|String
name|parseToString
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|url
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
return|return
name|parseToString
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
return|;
block|}
comment|/**      * Returns the maximum length of strings returned by the      * parseToString methods.      *      * @since Apache Tika 0.7      * @return maximum string length, or -1 if the limit has been disabled      */
specifier|public
name|int
name|getMaxStringLength
parameter_list|()
block|{
return|return
name|maxStringLength
return|;
block|}
comment|/**      * Sets the maximum length of strings returned by the parseToString      * methods.      *      * @since Apache Tika 0.7      * @param maxStringLength maximum string length,      *                        or -1 to disable this limit      */
specifier|public
name|void
name|setMaxStringLength
parameter_list|(
name|int
name|maxStringLength
parameter_list|)
block|{
name|this
operator|.
name|maxStringLength
operator|=
name|maxStringLength
expr_stmt|;
block|}
block|}
end_class

end_unit

