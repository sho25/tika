begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PipedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PipedWriter
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
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executor
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
name|sax
operator|.
name|BodyContentHandler
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

begin_comment
comment|/**  * Reader for the text content from a given binary stream. This class  * uses a background parsing task with a {@link Parser}  * ({@link AutoDetectParser} by default) to parse the text content from  * a given input stream. The {@link BodyContentHandler} class and a pipe  * is used to convert the push-based SAX event stream to the pull-based  * character stream defined by the {@link Reader} interface.  *  * @since Apache Tika 0.2  */
end_comment

begin_class
specifier|public
class|class
name|ParsingReader
extends|extends
name|Reader
block|{
comment|/**      * Parser instance used for parsing the given binary stream.      */
specifier|private
specifier|final
name|Parser
name|parser
decl_stmt|;
comment|/**      * Buffered read end of the pipe.      */
specifier|private
specifier|final
name|Reader
name|reader
decl_stmt|;
comment|/**      * Write end of the pipe.      */
specifier|private
specifier|final
name|Writer
name|writer
decl_stmt|;
comment|/**      * The binary stream being parsed.      */
specifier|private
specifier|final
name|InputStream
name|stream
decl_stmt|;
comment|/**      * Metadata associated with the document being parsed.      */
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
comment|/**      * The parse context.      */
specifier|private
specifier|final
name|ParseContext
name|context
decl_stmt|;
comment|/**      * An exception (if any) thrown by the parsing thread.      */
specifier|private
specifier|transient
name|Throwable
name|throwable
decl_stmt|;
comment|/**      * Utility method that returns a {@link Metadata} instance      * for a document with the given name.      *      * @param name resource name (or<code>null</code>)      * @return metadata instance      */
specifier|private
specifier|static
name|Metadata
name|getMetadata
parameter_list|(
name|String
name|name
parameter_list|)
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
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|length
argument_list|()
operator|>
literal|0
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
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|metadata
return|;
block|}
comment|/**      * Creates a reader for the text content of the given binary stream.      *      * @param stream binary stream      * @throws IOException if the document can not be parsed      */
specifier|public
name|ParsingReader
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|AutoDetectParser
argument_list|()
argument_list|,
name|stream
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a reader for the text content of the given binary stream      * with the given name.      *      * @param stream binary stream      * @param name document name      * @throws IOException if the document can not be parsed      */
specifier|public
name|ParsingReader
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
name|this
argument_list|(
operator|new
name|AutoDetectParser
argument_list|()
argument_list|,
name|stream
argument_list|,
name|getMetadata
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a reader for the text content of the given file.      *      * @param file file      * @throws FileNotFoundException if the given file does not exist      * @throws IOException if the document can not be parsed      */
specifier|public
name|ParsingReader
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
name|this
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a reader for the text content of the given binary stream      * with the given document metadata. The given parser is used for      * parsing. A new background thread is started for the parsing task.      *<p>      * The created reader will be responsible for closing the given stream.      * The stream and any associated resources will be closed at or before      * the time when the {@link #close()} method is called on this reader.      *      * @param parser parser instance      * @param stream binary stream      * @param metadata document metadata      * @throws IOException if the document can not be parsed      */
specifier|public
name|ParsingReader
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|InputStream
name|stream
parameter_list|,
specifier|final
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|parser
argument_list|,
name|stream
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|,
operator|new
name|Executor
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|(
name|Runnable
name|command
parameter_list|)
block|{
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
name|name
operator|=
literal|"Apache Tika: "
operator|+
name|name
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|"Apache Tika"
expr_stmt|;
block|}
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|command
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a reader for the text content of the given binary stream      * with the given document metadata. The given parser is used for the      * parsing task that is run with the given executor. The given executor      *<em>must</em> run the parsing task asynchronously in a separate thread,      * since the current thread must return to the caller that can then      * consume the parsed text through the {@link Reader} interface.      *<p>      * The created reader will be responsible for closing the given stream.      * The stream and any associated resources will be closed at or before      * the time when the {@link #close()} method is called on this reader.      *      * @param parser parser instance      * @param stream binary stream      * @param metadata document metadata      * @param context parsing context      * @param executor executor for the parsing task      * @throws IOException if the document can not be parsed      * @since Apache Tika 0.4      */
specifier|public
name|ParsingReader
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|Executor
name|executor
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|PipedReader
name|pipedReader
init|=
operator|new
name|PipedReader
argument_list|()
decl_stmt|;
name|this
operator|.
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
name|pipedReader
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|writer
operator|=
operator|new
name|PipedWriter
argument_list|(
name|pipedReader
argument_list|)
expr_stmt|;
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
name|e
argument_list|)
throw|;
comment|// Should never happen
block|}
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|ParsingTask
argument_list|()
argument_list|)
expr_stmt|;
comment|// TIKA-203: Buffer first character to force metadata extraction
name|reader
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|reader
operator|.
name|read
argument_list|()
expr_stmt|;
name|reader
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0      * @see<a href="https://issues.apache.org/jira/browse/TIKA-275">TIKA-275</a>      */
specifier|public
name|ParsingReader
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|parser
argument_list|,
name|stream
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0      * @see<a href="https://issues.apache.org/jira/browse/TIKA-275">TIKA-275</a>      */
specifier|public
name|ParsingReader
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|InputStream
name|stream
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|Executor
name|executor
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|parser
argument_list|,
name|stream
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|,
name|executor
argument_list|)
expr_stmt|;
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
block|}
comment|/**      * The background parsing task.      */
specifier|private
class|class
name|ParsingTask
implements|implements
name|Runnable
block|{
comment|/**          * Parses the given binary stream and writes the text content          * to the write end of the pipe. Potential exceptions (including          * the one caused if the read end is closed unexpectedly) are          * stored before the input stream is closed and processing is stopped.          */
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|throwable
operator|=
name|t
expr_stmt|;
block|}
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|throwable
operator|==
literal|null
condition|)
block|{
name|throwable
operator|=
name|t
expr_stmt|;
block|}
block|}
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|throwable
operator|==
literal|null
condition|)
block|{
name|throwable
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Reads parsed text from the pipe connected to the parsing thread.      * Fails if the parsing thread has thrown an exception.      *      * @param cbuf character buffer      * @param off start offset within the buffer      * @param len maximum number of characters to read      * @throws IOException if the parsing thread has failed or      *                     if for some reason the pipe does not work properly      */
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|throwable
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|throwable
throw|;
block|}
elseif|else
if|if
condition|(
name|throwable
operator|!=
literal|null
condition|)
block|{
name|IOException
name|exception
init|=
operator|new
name|IOException
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|exception
operator|.
name|initCause
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
throw|throw
name|exception
throw|;
block|}
return|return
name|reader
operator|.
name|read
argument_list|(
name|cbuf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/**      * Closes the read end of the pipe. If the parsing thread is still      * running, next write to the pipe will fail and cause the thread      * to stop. Thus there is no need to explicitly terminate the thread.      *      * @throws IOException if the pipe can not be closed      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

