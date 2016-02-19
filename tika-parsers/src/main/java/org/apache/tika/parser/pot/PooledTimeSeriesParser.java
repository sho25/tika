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
name|pot
package|;
end_package

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
name|TemporaryResources
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
name|parser
operator|.
name|AbstractParser
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
name|external
operator|.
name|ExternalParser
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
name|XHTMLContentHandler
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
name|ContentHandler
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|DefaultExecutor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|ExecuteWatchdog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|PumpStreamHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|exec
operator|.
name|environment
operator|.
name|EnvironmentUtils
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
name|helpers
operator|.
name|AttributesImpl
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
comment|/**  * Uses the Pooled Time Series algorithm + command line tool, to  *  generate a numeric representation of the video suitable for  *  similarity searches.  *<p>See https://wiki.apache.org/tika/PooledTimeSeriesParser for  *  more details and setup instructions.  */
end_comment

begin_class
specifier|public
class|class
name|PooledTimeSeriesParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2855917932512164988L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|MediaType
index|[]
block|{
name|MediaType
operator|.
name|video
argument_list|(
literal|"avi"
argument_list|)
block|,
name|MediaType
operator|.
name|video
argument_list|(
literal|"mp4"
argument_list|)
comment|// TODO: Add all supported video types
block|}
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|PooledTimeSeriesParser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|ExternalParser
operator|.
name|check
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"pooled-time-series"
block|,
literal|"--help"
block|}
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Returns the set of media types supported by this parser when used with the    * given parse context.    *    * @param context    *          parse context    * @return immutable set of media types    * @since Apache Tika 0.7    */
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
comment|/**    * Parses a document stream into a sequence of XHTML SAX events. Fills in    * related document metadata in the given metadata object.    *<p>    * The given document stream is consumed but not closed by this method. The    * responsibility to close the stream remains on the caller.    *<p>    * Information about the parsing context can be passed in the context    * parameter. See the parser implementations for the kinds of context    * information they expect.    *    * @param stream    *          the document stream (input)    * @param handler    *          handler for the XHTML SAX events (output)    * @param metadata    *          document metadata (input and output)    * @param context    *          parse context    * @throws IOException    *           if the document stream could not be read    * @throws SAXException    *           if the SAX events could not be processed    * @throws TikaException    *           if the document could not be parsed    * @since Apache Tika 0.5    */
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
if|if
condition|(
operator|!
name|isAvailable
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warning
argument_list|(
literal|"PooledTimeSeries not installed!"
argument_list|)
expr_stmt|;
return|return;
block|}
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
name|File
name|output
init|=
literal|null
decl_stmt|;
try|try
block|{
name|TikaInputStream
name|tikaStream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
name|File
name|input
init|=
name|tikaStream
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|String
name|cmdOutput
init|=
name|computePoT
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|FileInputStream
name|ofStream
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|input
operator|.
name|getAbsoluteFile
argument_list|()
operator|+
literal|".of.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|FileInputStream
name|ogStream
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|input
operator|.
name|getAbsoluteFile
argument_list|()
operator|+
literal|".hog.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|extractHeaderOutput
argument_list|(
name|ofStream
argument_list|,
name|metadata
argument_list|,
literal|"of"
argument_list|)
expr_stmt|;
name|extractHeaderOutput
argument_list|(
name|ogStream
argument_list|,
name|metadata
argument_list|,
literal|"og"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|doExtract
argument_list|(
name|ofStream
argument_list|,
name|xhtml
argument_list|,
literal|"Histogram of Optical Flows (HOF)"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"of_frames"
argument_list|)
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"of_vecSize"
argument_list|)
argument_list|)
expr_stmt|;
name|doExtract
argument_list|(
name|ogStream
argument_list|,
name|xhtml
argument_list|,
literal|"Histogram of Oriented Gradients (HOG)"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"og_frames"
argument_list|)
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"og_vecSize"
argument_list|)
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|String
name|computePoT
parameter_list|(
name|File
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|CommandLine
name|cmdLine
init|=
operator|new
name|CommandLine
argument_list|(
literal|"pooled-time-series"
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|outputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|cmdLine
operator|.
name|addArgument
argument_list|(
literal|"-f"
argument_list|)
expr_stmt|;
name|cmdLine
operator|.
name|addArgument
argument_list|(
name|input
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|fine
argument_list|(
literal|"Executing: "
operator|+
name|cmdLine
argument_list|)
expr_stmt|;
name|DefaultExecutor
name|exec
init|=
operator|new
name|DefaultExecutor
argument_list|()
decl_stmt|;
name|exec
operator|.
name|setExitValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ExecuteWatchdog
name|watchdog
init|=
operator|new
name|ExecuteWatchdog
argument_list|(
literal|60000
argument_list|)
decl_stmt|;
name|exec
operator|.
name|setWatchdog
argument_list|(
name|watchdog
argument_list|)
expr_stmt|;
name|PumpStreamHandler
name|streamHandler
init|=
operator|new
name|PumpStreamHandler
argument_list|(
name|outputStream
argument_list|)
decl_stmt|;
name|exec
operator|.
name|setStreamHandler
argument_list|(
name|streamHandler
argument_list|)
expr_stmt|;
name|int
name|exitValue
init|=
name|exec
operator|.
name|execute
argument_list|(
name|cmdLine
argument_list|,
name|EnvironmentUtils
operator|.
name|getProcEnvironment
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|outputStream
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
return|;
block|}
comment|/**    * Reads the contents of the given stream and write it to the given XHTML    * content handler. The stream is closed once fully processed.    *    * @param stream    *          Stream where is the result of ocr    * @param xhtml    *          XHTML content handler    * @param tableTitle    *          The name of the matrix/table to display.    * @param frames    *          Number of frames read from the video.    * @param vecSize    *          Size of the OF or HOG vector.    * @throws SAXException    *           if the XHTML SAX events could not be handled    * @throws IOException    *           if an input error occurred    */
specifier|private
name|void
name|doExtract
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|String
name|tableTitle
parameter_list|,
name|String
name|frames
parameter_list|,
name|String
name|vecSize
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|AttributesImpl
name|attributes
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
literal|"rows"
argument_list|,
literal|"CDATA"
argument_list|,
name|frames
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
literal|"cols"
argument_list|,
literal|"CDATA"
argument_list|,
name|vecSize
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"h3"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|tableTitle
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"h3"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"table"
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|val
range|:
name|line
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractHeaderOutput
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|String
index|[]
name|firstLine
init|=
name|line
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|String
name|frames
init|=
name|firstLine
index|[
literal|0
index|]
decl_stmt|;
name|String
name|vecSize
init|=
name|firstLine
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
literal|""
expr_stmt|;
block|}
name|metadata
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|"_frames"
argument_list|,
name|frames
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|prefix
operator|+
literal|"_vecSize"
argument_list|,
name|vecSize
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

