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
name|utils
package|;
end_package

begin_comment
comment|//JDK imports
end_comment

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
name|StringWriter
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|metadata
operator|.
name|TikaMimeKeys
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
comment|/**  * Contains utility methods for parsing documents. Intended to provide simple  * entry points into the Tika framework.  */
end_comment

begin_class
specifier|public
class|class
name|ParseUtils
implements|implements
name|TikaMimeKeys
block|{
comment|/**      * Returns a parser that can handle the specified MIME type, and is set to      * receive input from a stream opened from the specified URL. NB: Close the      * input stream when it is no longer needed!      *       * @param config      * @param mimeType      *            the document's MIME type      * @return a parser appropriate to this MIME type      * @throws TikaException      */
specifier|public
specifier|static
name|Parser
name|getParser
parameter_list|(
name|String
name|mimeType
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|TikaException
block|{
return|return
name|config
operator|.
name|getParser
argument_list|(
name|mimeType
argument_list|)
return|;
block|}
comment|/**      * Returns a parser that can handle the specified MIME type, and is set to      * receive input from a stream opened from the specified URL. The MIME type      * is determined automatically. NB: Close the input stream when it is no      * longer needed!      *       * @param documentUrl      *            URL pointing to the document to parse      * @param config      * @return a parser appropriate to this MIME type and ready to read input      *         from the specified document      * @throws TikaException      */
specifier|public
specifier|static
name|Parser
name|getParser
parameter_list|(
name|URL
name|documentUrl
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|TikaException
block|{
name|String
name|mimetype
init|=
name|config
operator|.
name|getMimeRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|documentUrl
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|getParser
argument_list|(
name|mimetype
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**      * Returns a parser that can handle the specified MIME type, and is set to      * receive input from a stream opened from the specified URL. NB: Close the      * input stream when it is no longer needed!      *       * @param documentFile      *            File object pointing to the document to parse      * @param config      * @return a parser appropriate to this MIME type and ready to read input      *         from the specified document      * @throws TikaException      */
specifier|public
specifier|static
name|Parser
name|getParser
parameter_list|(
name|File
name|documentFile
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|TikaException
block|{
name|String
name|mimetype
init|=
name|config
operator|.
name|getMimeRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|documentFile
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|getParser
argument_list|(
name|mimetype
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**      * Returns a list of parsers from zip InputStream      *       * @param zip      *            InputStream      * @param config      * @return a list of parsers from zip file      * @throws TikaException      */
specifier|private
specifier|static
name|List
argument_list|<
name|Parser
argument_list|>
name|getParsersFromZip
parameter_list|(
name|InputStream
name|zipIs
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|TikaException
block|{
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
operator|new
name|ArrayList
argument_list|<
name|Parser
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|zipFiles
init|=
name|Utils
operator|.
name|unzip
argument_list|(
name|zipIs
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|zipFiles
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|File
name|zipEntry
init|=
name|zipFiles
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|parsers
operator|.
name|add
argument_list|(
name|getParser
argument_list|(
name|zipEntry
argument_list|,
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|parsers
return|;
block|}
comment|/**      * Returns a list of parsers from zip File      *       * @param zip      *            File      * @param config      * @return a list of parsers from zip file      * @throws TikaException      * @throws FileNotFoundException      */
specifier|public
specifier|static
name|List
argument_list|<
name|Parser
argument_list|>
name|getParsersFromZip
parameter_list|(
name|File
name|zip
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|TikaException
throws|,
name|FileNotFoundException
block|{
name|String
name|zipMimeType
init|=
name|config
operator|.
name|getMimeRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|zip
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|zipMimeType
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"application/zip"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"The file you are using is note a zip file"
argument_list|)
throw|;
block|}
return|return
name|getParsersFromZip
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|zip
argument_list|)
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**      * Returns a list of parsers from URL      *       * @param URL      * @param config      * @return a list of parsers from zip file      * @throws TikaException      * @throws IOException      */
specifier|public
specifier|static
name|List
argument_list|<
name|Parser
argument_list|>
name|getParsersFromZip
parameter_list|(
name|URL
name|zip
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|String
name|zipMimeType
init|=
name|config
operator|.
name|getMimeRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|zip
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|zipMimeType
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"application/zip"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"The file you are using is note a zip file"
argument_list|)
throw|;
block|}
return|return
name|getParsersFromZip
argument_list|(
name|zip
operator|.
name|openStream
argument_list|()
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**      * Gets the string content of a document read from an input stream.      *       * @param stream the stream from which to read document data      * @param config      * @param mimeType MIME type of the data      * @return the string content parsed from the document      */
specifier|public
specifier|static
name|String
name|getStringContent
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|TikaConfig
name|config
parameter_list|,
name|String
name|mimeType
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
try|try
block|{
name|Parser
name|parser
init|=
name|config
operator|.
name|getParser
argument_list|(
name|mimeType
argument_list|)
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|WriteOutContentHandler
argument_list|(
name|writer
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unexpected SAX error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Gets the string content of a document read from an input stream.      *       * @param documentUrl      *            URL pointing to the document to parse      * @param config      * @return the string content parsed from the document      */
specifier|public
specifier|static
name|String
name|getStringContent
parameter_list|(
name|URL
name|documentUrl
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|String
name|mime
init|=
name|config
operator|.
name|getMimeRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|documentUrl
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|getStringContent
argument_list|(
name|documentUrl
argument_list|,
name|config
argument_list|,
name|mime
argument_list|)
return|;
block|}
comment|/**      * Gets the string content of a document read from an input stream.      *       * @param documentUrl      *            URL pointing to the document to parse      * @param config      * @param mimeType      *            MIME type of the data      * @return the string content parsed from the document      */
specifier|public
specifier|static
name|String
name|getStringContent
parameter_list|(
name|URL
name|documentUrl
parameter_list|,
name|TikaConfig
name|config
parameter_list|,
name|String
name|mimeType
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|InputStream
name|stream
init|=
name|documentUrl
operator|.
name|openStream
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|getStringContent
argument_list|(
name|stream
argument_list|,
name|config
argument_list|,
name|mimeType
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
comment|/**      * Gets the string content of a document read from an input stream.      *       * @param documentFile      *            File object pointing to the document to parse      * @param config      * @param mimeType      *            MIME type of the data      * @return the string content parsed from the document      */
specifier|public
specifier|static
name|String
name|getStringContent
parameter_list|(
name|File
name|documentFile
parameter_list|,
name|TikaConfig
name|config
parameter_list|,
name|String
name|mimeType
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|InputStream
name|stream
init|=
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|documentFile
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|getStringContent
argument_list|(
name|stream
argument_list|,
name|config
argument_list|,
name|mimeType
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
comment|/**      * Gets the string content of a document read from an input stream.      *       * @param documentFile      *            File object pointing to the document to parse      * @param config      * @return the string content parsed from the document      */
specifier|public
specifier|static
name|String
name|getStringContent
parameter_list|(
name|File
name|documentFile
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|String
name|mime
init|=
name|config
operator|.
name|getMimeRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|documentFile
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|getStringContent
argument_list|(
name|documentFile
argument_list|,
name|config
argument_list|,
name|mime
argument_list|)
return|;
block|}
block|}
end_class

end_unit

