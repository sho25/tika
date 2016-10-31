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
name|external
package|;
end_package

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
name|OutputStream
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
name|Map
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
name|IOUtils
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
name|NullOutputStream
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
comment|/**  * Parser that uses an external program (like catdoc or pdf2txt) to extract  *  text content and metadata from a given document.  */
end_comment

begin_class
specifier|public
class|class
name|ExternalParser
extends|extends
name|AbstractParser
block|{
comment|/**      * Consumer contract      * @since Apache Tika 1.14      */
specifier|public
interface|interface
name|LineConsumer
extends|extends
name|Serializable
block|{
comment|/**          * Consume a line          * @param line a line of string          */
name|void
name|consume
parameter_list|(
name|String
name|line
parameter_list|)
function_decl|;
comment|/**          * A null consumer          */
name|LineConsumer
name|NULL
init|=
operator|new
name|LineConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|consume
parameter_list|(
name|String
name|line
parameter_list|)
block|{
comment|// ignores
block|}
block|}
decl_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|1079128990650687037L
decl_stmt|;
comment|/**      * The token, which if present in the Command string, will      *  be replaced with the input filename.       * Alternately, the input data can be streamed over STDIN.      */
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_FILE_TOKEN
init|=
literal|"${INPUT}"
decl_stmt|;
comment|/**      * The token, which if present in the Command string, will      *  be replaced with the output filename.       * Alternately, the output data can be collected on STDOUT.      */
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_FILE_TOKEN
init|=
literal|"${OUTPUT}"
decl_stmt|;
comment|/**      * Media types supported by the external program.      */
specifier|private
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedTypes
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
comment|/**      * Regular Expressions to run over STDOUT to      *  extract Metadata.      */
specifier|private
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|metadataPatterns
init|=
literal|null
decl_stmt|;
comment|/**      * The external command to invoke.      * @see Runtime#exec(String[])      */
specifier|private
name|String
index|[]
name|command
init|=
operator|new
name|String
index|[]
block|{
literal|"cat"
block|}
decl_stmt|;
comment|/**      * A consumer for ignored Lines      */
specifier|private
name|LineConsumer
name|ignoredLineConsumer
init|=
name|LineConsumer
operator|.
name|NULL
decl_stmt|;
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
name|getSupportedTypes
argument_list|()
return|;
block|}
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|()
block|{
return|return
name|supportedTypes
return|;
block|}
specifier|public
name|void
name|setSupportedTypes
parameter_list|(
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedTypes
parameter_list|)
block|{
name|this
operator|.
name|supportedTypes
operator|=
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
name|supportedTypes
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getCommand
parameter_list|()
block|{
return|return
name|command
return|;
block|}
comment|/**      * Sets the command to be run. This can include either of      *  {@link #INPUT_FILE_TOKEN} or {@link #OUTPUT_FILE_TOKEN}      *  if the command needs filenames.      * @see Runtime#exec(String[])      */
specifier|public
name|void
name|setCommand
parameter_list|(
name|String
modifier|...
name|command
parameter_list|)
block|{
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
comment|/**      * Gets lines consumer      * @return consumer instance      */
specifier|public
name|LineConsumer
name|getIgnoredLineConsumer
parameter_list|()
block|{
return|return
name|ignoredLineConsumer
return|;
block|}
comment|/**      * Set a consumer for the lines ignored by the parse functions      * @param ignoredLineConsumer consumer instance      */
specifier|public
name|void
name|setIgnoredLineConsumer
parameter_list|(
name|LineConsumer
name|ignoredLineConsumer
parameter_list|)
block|{
name|this
operator|.
name|ignoredLineConsumer
operator|=
name|ignoredLineConsumer
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|getMetadataExtractionPatterns
parameter_list|()
block|{
return|return
name|metadataPatterns
return|;
block|}
comment|/**      * Sets the map of regular expression patterns and Metadata      *  keys. Any matching patterns will have the matching      *  metadata entries set.      * Set this to null to disable Metadata extraction.      */
specifier|public
name|void
name|setMetadataExtractionPatterns
parameter_list|(
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|patterns
parameter_list|)
block|{
name|this
operator|.
name|metadataPatterns
operator|=
name|patterns
expr_stmt|;
block|}
comment|/**      * Executes the configured external command and passes the given document      *  stream as a simple XHTML document to the given SAX content handler.      * Metadata is only extracted if {@link #setMetadataExtractionPatterns(Map)}      *  has been called to set patterns.      */
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
try|try
block|{
name|parse
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|,
name|tmp
argument_list|)
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|TikaInputStream
name|stream
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|TemporaryResources
name|tmp
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|boolean
name|inputToStdIn
init|=
literal|true
decl_stmt|;
name|boolean
name|outputFromStdOut
init|=
literal|true
decl_stmt|;
name|boolean
name|hasPatterns
init|=
operator|(
name|metadataPatterns
operator|!=
literal|null
operator|&&
operator|!
name|metadataPatterns
operator|.
name|isEmpty
argument_list|()
operator|)
decl_stmt|;
name|File
name|output
init|=
literal|null
decl_stmt|;
comment|// Build our command
name|String
index|[]
name|cmd
decl_stmt|;
if|if
condition|(
name|command
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|cmd
operator|=
name|command
index|[
literal|0
index|]
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|=
operator|new
name|String
index|[
name|command
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|command
argument_list|,
literal|0
argument_list|,
name|cmd
argument_list|,
literal|0
argument_list|,
name|command
operator|.
name|length
argument_list|)
expr_stmt|;
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
name|cmd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|cmd
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
name|INPUT_FILE_TOKEN
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|cmd
index|[
name|i
index|]
operator|=
name|cmd
index|[
name|i
index|]
operator|.
name|replace
argument_list|(
name|INPUT_FILE_TOKEN
argument_list|,
name|stream
operator|.
name|getFile
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|inputToStdIn
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|cmd
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
name|OUTPUT_FILE_TOKEN
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|output
operator|=
name|tmp
operator|.
name|createTemporaryFile
argument_list|()
expr_stmt|;
name|outputFromStdOut
operator|=
literal|false
expr_stmt|;
name|cmd
index|[
name|i
index|]
operator|=
name|cmd
index|[
name|i
index|]
operator|.
name|replace
argument_list|(
name|OUTPUT_FILE_TOKEN
argument_list|,
name|output
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Execute
name|Process
name|process
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cmd
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|process
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|cmd
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|process
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|inputToStdIn
condition|)
block|{
name|sendInput
argument_list|(
name|process
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|process
operator|.
name|getOutputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|InputStream
name|out
init|=
name|process
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|InputStream
name|err
init|=
name|process
operator|.
name|getErrorStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasPatterns
condition|)
block|{
name|extractMetadata
argument_list|(
name|err
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
if|if
condition|(
name|outputFromStdOut
condition|)
block|{
name|extractOutput
argument_list|(
name|out
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|extractMetadata
argument_list|(
name|out
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ignoreStream
argument_list|(
name|err
argument_list|)
expr_stmt|;
if|if
condition|(
name|outputFromStdOut
condition|)
block|{
name|extractOutput
argument_list|(
name|out
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ignoreStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
try|try
block|{
name|process
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{             }
block|}
comment|// Grab the output if we haven't already
if|if
condition|(
operator|!
name|outputFromStdOut
condition|)
block|{
name|extractOutput
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|output
argument_list|)
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Starts a thread that extracts the contents of the standard output      * stream of the given process to the given XHTML content handler.      * The standard output stream is closed once fully processed.      *      * @param process process      * @param xhtml XHTML content handler      * @throws SAXException if the XHTML SAX events could not be handled      * @throws IOException if an input error occurred      */
specifier|private
name|void
name|extractOutput
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
init|;
name|n
operator|!=
operator|-
literal|1
condition|;
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
control|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Starts a thread that sends the contents of the given input stream      * to the standard input stream of the given process. Potential      * exceptions are ignored, and the standard input stream is closed      * once fully processed. Note that the given input stream is<em>not</em>      * closed by this method.      *      * @param process process      * @param stream input stream      */
specifier|private
name|void
name|sendInput
parameter_list|(
specifier|final
name|Process
name|process
parameter_list|,
specifier|final
name|InputStream
name|stream
parameter_list|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|OutputStream
name|stdin
init|=
name|process
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|stdin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                 }
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{}
block|}
comment|/**      * Starts a thread that reads and discards the contents of the      * standard stream of the given process. Potential exceptions      * are ignored, and the stream is closed once fully processed.      * Note: calling this starts a new thread and blocks the current(caller) thread until the new thread dies      * @param stream stream to be ignored      */
specifier|private
specifier|static
name|void
name|ignoreStream
parameter_list|(
specifier|final
name|InputStream
name|stream
parameter_list|)
block|{
name|ignoreStream
argument_list|(
name|stream
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Starts a thread that reads and discards the contents of the      * standard stream of the given process. Potential exceptions      * are ignored, and the stream is closed once fully processed.      * @param stream stream to sent to black hole (a k a null)      * @param waitForDeath when {@code true} the caller thread will be blocked till the death of new thread.      * @return The thread that is created and started      */
specifier|private
specifier|static
name|Thread
name|ignoreStream
parameter_list|(
specifier|final
name|InputStream
name|stream
parameter_list|,
name|boolean
name|waitForDeath
parameter_list|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
operator|new
name|NullOutputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                 }
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|waitForDeath
condition|)
block|{
try|try
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{}
block|}
return|return
name|t
return|;
block|}
specifier|private
name|void
name|extractMetadata
parameter_list|(
specifier|final
name|InputStream
name|stream
parameter_list|,
specifier|final
name|Metadata
name|metadata
parameter_list|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|BufferedReader
name|reader
decl_stmt|;
name|reader
operator|=
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
expr_stmt|;
try|try
block|{
name|String
name|line
decl_stmt|;
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
name|boolean
name|consumed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Pattern
name|p
range|:
name|metadataPatterns
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|consumed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|metadataPatterns
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|!=
literal|null
operator|&&
operator|!
name|metadataPatterns
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|metadataPatterns
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|add
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|consumed
condition|)
block|{
name|ignoredLineConsumer
operator|.
name|consume
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Ignore
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{}
block|}
comment|/**      * Checks to see if the command can be run. Typically used with      *  something like "myapp --version" to check to see if "myapp"      *  is installed and on the path.      *        * @param checkCmd The check command to run      * @param errorValue What is considered an error value?       */
specifier|public
specifier|static
name|boolean
name|check
parameter_list|(
name|String
name|checkCmd
parameter_list|,
name|int
modifier|...
name|errorValue
parameter_list|)
block|{
return|return
name|check
argument_list|(
operator|new
name|String
index|[]
block|{
name|checkCmd
block|}
argument_list|,
name|errorValue
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|check
parameter_list|(
name|String
index|[]
name|checkCmd
parameter_list|,
name|int
modifier|...
name|errorValue
parameter_list|)
block|{
if|if
condition|(
name|errorValue
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|errorValue
operator|=
operator|new
name|int
index|[]
block|{
literal|127
block|}
expr_stmt|;
block|}
try|try
block|{
name|Process
name|process
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|checkCmd
argument_list|)
decl_stmt|;
name|Thread
name|stdErrSuckerThread
init|=
name|ignoreStream
argument_list|(
name|process
operator|.
name|getErrorStream
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Thread
name|stdOutSuckerThread
init|=
name|ignoreStream
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stdErrSuckerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|stdOutSuckerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|int
name|result
init|=
name|process
operator|.
name|waitFor
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|err
range|:
name|errorValue
control|)
block|{
if|if
condition|(
name|result
operator|==
name|err
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Some problem, command is there or is broken
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// Some problem, command is there or is broken
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
comment|// External process execution is banned by the security manager
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Error
name|err
parameter_list|)
block|{
if|if
condition|(
name|err
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
name|err
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"posix_spawn"
argument_list|)
operator|||
name|err
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"UNIXProcess"
argument_list|)
operator|)
condition|)
block|{
comment|//"Error forking command due to JVM locale bug
comment|//(see TIKA-1526 and SOLR-6387)"
return|return
literal|false
return|;
block|}
comment|//throw if a different kind of error
throw|throw
name|err
throw|;
block|}
block|}
block|}
end_class

end_unit

