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
name|embedder
package|;
end_package

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
name|List
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
name|metadata
operator|.
name|Property
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
comment|/**  * Embedder that uses an external program (like sed or exiftool) to embed text  * content and metadata into a given document.  *  * @since Apache Tika 1.3  */
end_comment

begin_class
specifier|public
class|class
name|ExternalEmbedder
implements|implements
name|Embedder
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2828829275642475697L
decl_stmt|;
comment|/**      * Token to be replaced with a String array of metadata assignment command      * arguments      */
specifier|public
specifier|static
specifier|final
name|String
name|METADATA_COMMAND_ARGUMENTS_TOKEN
init|=
literal|"${METADATA}"
decl_stmt|;
comment|/**      * Token to be replaced with a String array of metadata assignment command      * arguments      */
specifier|public
specifier|static
specifier|final
name|String
name|METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN
init|=
literal|"${METADATA_SERIALIZED}"
decl_stmt|;
comment|/**      * Media types supported by the external program.      */
specifier|private
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedEmbedTypes
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
comment|/**      * Mapping of Tika metadata to command line parameters.      */
specifier|private
name|Map
argument_list|<
name|Property
argument_list|,
name|String
index|[]
argument_list|>
name|metadataCommandArguments
init|=
literal|null
decl_stmt|;
comment|/**      * The external command to invoke.      *      * @see Runtime#exec(String[])      */
specifier|private
name|String
index|[]
name|command
init|=
operator|new
name|String
index|[]
block|{
literal|"sed"
block|,
literal|"-e"
block|,
literal|"$a\\\n"
operator|+
name|METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN
block|,
name|ExternalParser
operator|.
name|INPUT_FILE_TOKEN
block|}
decl_stmt|;
specifier|private
name|String
name|commandAssignmentOperator
init|=
literal|"="
decl_stmt|;
specifier|private
name|String
name|commandAssignmentDelimeter
init|=
literal|", "
decl_stmt|;
specifier|private
name|String
name|commandAppendOperator
init|=
literal|"="
decl_stmt|;
specifier|private
name|boolean
name|quoteAssignmentValues
init|=
literal|false
decl_stmt|;
specifier|private
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedEmbedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|getSupportedEmbedTypes
argument_list|()
return|;
block|}
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedEmbedTypes
parameter_list|()
block|{
return|return
name|supportedEmbedTypes
return|;
block|}
specifier|public
name|void
name|setSupportedEmbedTypes
parameter_list|(
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedEmbedTypes
parameter_list|)
block|{
name|this
operator|.
name|supportedEmbedTypes
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
name|supportedEmbedTypes
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the command to be run. This can include either of      * {@link #INPUT_FILE_TOKEN} or {@link #OUTPUT_FILE_TOKEN} if the command      * needs filenames.      *      * @return      */
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
comment|/**      * Sets the command to be run. This can include either of      * {@link #INPUT_FILE_TOKEN} or {@link #OUTPUT_FILE_TOKEN} if the command      * needs filenames.      *      * @see Runtime#exec(String[])      */
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
comment|/**      * Gets the assignment operator for the command line tool, i.e. "=".      *      * @return the assignment operator      */
specifier|public
name|String
name|getCommandAssignmentOperator
parameter_list|()
block|{
return|return
name|commandAssignmentOperator
return|;
block|}
comment|/**      * Sets the assignment operator for the command line tool, i.e. "=".      *      * @param commandAssignmentOperator      */
specifier|public
name|void
name|setCommandAssignmentOperator
parameter_list|(
name|String
name|commandAssignmentOperator
parameter_list|)
block|{
name|this
operator|.
name|commandAssignmentOperator
operator|=
name|commandAssignmentOperator
expr_stmt|;
block|}
comment|/**      * Gets the delimiter for multiple assignments for the command line tool,      * i.e. ", ".      *      * @return the assignment delimiter      */
specifier|public
name|String
name|getCommandAssignmentDelimeter
parameter_list|()
block|{
return|return
name|commandAssignmentDelimeter
return|;
block|}
comment|/**      * Sets the delimiter for multiple assignments for the command line tool,      * i.e. ", ".      *      * @param commandAssignmentDelimeter      */
specifier|public
name|void
name|setCommandAssignmentDelimeter
parameter_list|(
name|String
name|commandAssignmentDelimeter
parameter_list|)
block|{
name|this
operator|.
name|commandAssignmentDelimeter
operator|=
name|commandAssignmentDelimeter
expr_stmt|;
block|}
comment|/**      * Gets the operator to append rather than replace a value for the command      * line tool, i.e. "+=".      *      * @return the append operator      */
specifier|public
name|String
name|getCommandAppendOperator
parameter_list|()
block|{
return|return
name|commandAppendOperator
return|;
block|}
comment|/**      * Sets the operator to append rather than replace a value for the command      * line tool, i.e. "+=".      *      * @param commandAppendOperator      */
specifier|public
name|void
name|setCommandAppendOperator
parameter_list|(
name|String
name|commandAppendOperator
parameter_list|)
block|{
name|this
operator|.
name|commandAppendOperator
operator|=
name|commandAppendOperator
expr_stmt|;
block|}
comment|/**      * Gets whether or not to quote assignment values, i.e. tag='value'. The      * default is false.      *      * @return whether or not to quote assignment values      */
specifier|public
name|boolean
name|isQuoteAssignmentValues
parameter_list|()
block|{
return|return
name|quoteAssignmentValues
return|;
block|}
comment|/**      * Sets whether or not to quote assignment values, i.e. tag='value'.      *      * @param quoteAssignmentValues      */
specifier|public
name|void
name|setQuoteAssignmentValues
parameter_list|(
name|boolean
name|quoteAssignmentValues
parameter_list|)
block|{
name|this
operator|.
name|quoteAssignmentValues
operator|=
name|quoteAssignmentValues
expr_stmt|;
block|}
comment|/**      * Gets the map of Metadata keys to command line parameters.      *      * @return the metadata to CLI param map      */
specifier|public
name|Map
argument_list|<
name|Property
argument_list|,
name|String
index|[]
argument_list|>
name|getMetadataCommandArguments
parameter_list|()
block|{
return|return
name|metadataCommandArguments
return|;
block|}
comment|/**      * Sets the map of Metadata keys to command line parameters. Set this to      * null to disable Metadata embedding.      *      * @param arguments      */
specifier|public
name|void
name|setMetadataCommandArguments
parameter_list|(
name|Map
argument_list|<
name|Property
argument_list|,
name|String
index|[]
argument_list|>
name|arguments
parameter_list|)
block|{
name|this
operator|.
name|metadataCommandArguments
operator|=
name|arguments
expr_stmt|;
block|}
comment|/**      * Constructs a collection of command line arguments responsible for setting      * individual metadata fields based on the given<code>metadata</code>.      *      * @param metadata the metadata to embed      * @return the metadata-related command line arguments      */
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getCommandMetadataSegments
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|commandMetadataSegments
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|metadata
operator|==
literal|null
operator|||
name|metadata
operator|.
name|names
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|commandMetadataSegments
return|;
block|}
for|for
control|(
name|String
name|metadataName
range|:
name|metadata
operator|.
name|names
argument_list|()
control|)
block|{
for|for
control|(
name|Property
name|property
range|:
name|getMetadataCommandArguments
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|metadataName
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|String
index|[]
name|metadataCommandArguments
init|=
name|getMetadataCommandArguments
argument_list|()
operator|.
name|get
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|metadataCommandArguments
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|metadataCommandArgument
range|:
name|metadataCommandArguments
control|)
block|{
if|if
condition|(
name|metadata
operator|.
name|isMultiValued
argument_list|(
name|metadataName
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|metadataValue
range|:
name|metadata
operator|.
name|getValues
argument_list|(
name|metadataName
argument_list|)
control|)
block|{
name|String
name|assignmentValue
init|=
name|metadataValue
decl_stmt|;
if|if
condition|(
name|quoteAssignmentValues
condition|)
block|{
name|assignmentValue
operator|=
literal|"'"
operator|+
name|assignmentValue
operator|+
literal|"'"
expr_stmt|;
block|}
name|commandMetadataSegments
operator|.
name|add
argument_list|(
name|metadataCommandArgument
operator|+
name|commandAppendOperator
operator|+
name|assignmentValue
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|assignmentValue
init|=
name|metadata
operator|.
name|get
argument_list|(
name|metadataName
argument_list|)
decl_stmt|;
if|if
condition|(
name|quoteAssignmentValues
condition|)
block|{
name|assignmentValue
operator|=
literal|"'"
operator|+
name|assignmentValue
operator|+
literal|"'"
expr_stmt|;
block|}
name|commandMetadataSegments
operator|.
name|add
argument_list|(
name|metadataCommandArgument
operator|+
name|commandAssignmentOperator
operator|+
name|assignmentValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
return|return
name|commandMetadataSegments
return|;
block|}
comment|/**      * Serializes a collection of metadata command line arguments into a single      * string.      *      * @param metadataCommandArguments      * @return the serialized metadata arguments string      */
specifier|protected
specifier|static
name|String
name|serializeMetadata
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|metadataCommandArguments
parameter_list|)
block|{
if|if
condition|(
name|metadataCommandArguments
operator|!=
literal|null
condition|)
block|{
return|return
name|Arrays
operator|.
name|toString
argument_list|(
name|metadataCommandArguments
operator|.
name|toArray
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|""
return|;
block|}
comment|/**      * Executes the configured external command and passes the given document      * stream as a simple XHTML document to the given SAX content handler.      * Metadata is only extracted if {@link #setMetadataCommandArguments(Map)}      * has been called to set arguments.      */
specifier|public
name|void
name|embed
parameter_list|(
specifier|final
name|Metadata
name|metadata
parameter_list|,
specifier|final
name|InputStream
name|inputStream
parameter_list|,
specifier|final
name|OutputStream
name|outputStream
parameter_list|,
specifier|final
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
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
name|hasMetadataCommandArguments
init|=
operator|(
name|metadataCommandArguments
operator|!=
literal|null
operator|&&
operator|!
name|metadataCommandArguments
operator|.
name|isEmpty
argument_list|()
operator|)
decl_stmt|;
name|boolean
name|serializeMetadataCommandArgumentsToken
init|=
literal|false
decl_stmt|;
name|boolean
name|replacedMetadataCommandArgumentsToken
init|=
literal|false
decl_stmt|;
name|TikaInputStream
name|tikaInputStream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|File
name|tempOutputFile
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|commandMetadataSegments
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasMetadataCommandArguments
condition|)
block|{
name|commandMetadataSegments
operator|=
name|getCommandMetadataSegments
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
comment|// Build our command
name|List
argument_list|<
name|String
argument_list|>
name|origCmd
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|cmd
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|commandSegment
range|:
name|origCmd
control|)
block|{
if|if
condition|(
name|commandSegment
operator|.
name|indexOf
argument_list|(
name|ExternalParser
operator|.
name|INPUT_FILE_TOKEN
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|commandSegment
operator|=
name|commandSegment
operator|.
name|replace
argument_list|(
name|ExternalParser
operator|.
name|INPUT_FILE_TOKEN
argument_list|,
name|tikaInputStream
operator|.
name|getFile
argument_list|()
operator|.
name|toString
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
name|commandSegment
operator|.
name|indexOf
argument_list|(
name|ExternalParser
operator|.
name|OUTPUT_FILE_TOKEN
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|tempOutputFile
operator|=
name|tmp
operator|.
name|createTemporaryFile
argument_list|()
expr_stmt|;
name|commandSegment
operator|=
name|commandSegment
operator|.
name|replace
argument_list|(
name|ExternalParser
operator|.
name|OUTPUT_FILE_TOKEN
argument_list|,
name|tempOutputFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|outputFromStdOut
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|commandSegment
operator|.
name|indexOf
argument_list|(
name|METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|serializeMetadataCommandArgumentsToken
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|commandSegment
operator|.
name|indexOf
argument_list|(
name|METADATA_COMMAND_ARGUMENTS_TOKEN
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|hasMetadataCommandArguments
condition|)
block|{
for|for
control|(
name|String
name|commandMetadataSegment
range|:
name|commandMetadataSegments
control|)
block|{
name|cmd
operator|.
name|add
argument_list|(
name|commandMetadataSegment
argument_list|)
expr_stmt|;
block|}
block|}
name|replacedMetadataCommandArgumentsToken
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|cmd
operator|.
name|add
argument_list|(
name|commandSegment
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasMetadataCommandArguments
condition|)
block|{
if|if
condition|(
name|serializeMetadataCommandArgumentsToken
condition|)
block|{
comment|// Find all metadata tokens and replace with encapsulated metadata
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|commandSegment
range|:
name|cmd
control|)
block|{
if|if
condition|(
name|commandSegment
operator|.
name|indexOf
argument_list|(
name|METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|commandSegment
operator|=
name|commandSegment
operator|.
name|replace
argument_list|(
name|METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN
argument_list|,
name|serializeMetadata
argument_list|(
name|commandMetadataSegments
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|commandSegment
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|replacedMetadataCommandArgumentsToken
operator|&&
operator|!
name|serializeMetadataCommandArgumentsToken
condition|)
block|{
comment|// Tack metadata onto the end of the cmd as arguments
name|cmd
operator|.
name|addAll
argument_list|(
name|commandMetadataSegments
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Execute
name|Process
name|process
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|toArray
argument_list|()
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
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
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
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ByteArrayOutputStream
name|stdErrOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|sendStdErrToOutputStream
argument_list|(
name|process
argument_list|,
name|stdErrOutputStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputToStdIn
condition|)
block|{
name|sendInputStreamToStdIn
argument_list|(
name|inputStream
argument_list|,
name|process
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// We're not writing to std in this case so close
name|process
operator|.
name|getOutputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|outputFromStdOut
condition|)
block|{
name|sendStdOutToOutputStream
argument_list|(
name|process
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
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
block|{                 }
comment|// The command is finished, read the output file into the given output stream
name|InputStream
name|tempOutputFileInputStream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|tempOutputFile
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|tempOutputFileInputStream
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|outputFromStdOut
condition|)
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
block|{                 }
block|}
else|else
block|{
try|try
block|{
comment|// Clean up temp output files
name|tempOutputFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
if|if
condition|(
operator|!
name|inputToStdIn
condition|)
block|{
comment|// Close input file (and delete if created by up TemporaryResources.createTemporaryFile)
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|tikaInputStream
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|outputStream
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stdErrOutputStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|process
operator|.
name|exitValue
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"There was an error executing the command line"
operator|+
literal|"\nExecutable Command:\n\n"
operator|+
name|cmd
operator|+
literal|"\nExecutable Error:\n\n"
operator|+
name|stdErrOutputStream
operator|.
name|toString
argument_list|(
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Creates a new thread for copying a given input stream to a given output stream.      *      * @param inputStream the source input stream      * @param outputStream the target output stream      */
specifier|private
name|void
name|multiThreadedStreamCopy
parameter_list|(
specifier|final
name|InputStream
name|inputStream
parameter_list|,
specifier|final
name|OutputStream
name|outputStream
parameter_list|)
block|{
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
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
name|inputStream
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sends the contents of the given input stream to the      * standard input of the given process. Potential exceptions are      * ignored.      *<p>      * Note that the given input stream is<em>not</em> closed by this method.      *      * @param process the process      * @param inputStream the input stream to send to standard input of the process      */
specifier|private
name|void
name|sendInputStreamToStdIn
parameter_list|(
specifier|final
name|InputStream
name|inputStream
parameter_list|,
specifier|final
name|Process
name|process
parameter_list|)
block|{
name|multiThreadedStreamCopy
argument_list|(
name|inputStream
argument_list|,
name|process
operator|.
name|getOutputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sends the standard output of the given      * process to the given output stream. Potential exceptions are      * ignored.      *<p>      * Note that the given output stream is<em>not</em> closed by this method.      *      * @param process the process      * @param outputStream the putput stream to send to standard input of the process      */
specifier|private
name|void
name|sendStdOutToOutputStream
parameter_list|(
specifier|final
name|Process
name|process
parameter_list|,
specifier|final
name|OutputStream
name|outputStream
parameter_list|)
block|{
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Starts a thread that reads and discards the contents of the standard      * stream of the given process. Potential exceptions are ignored, and the      * stream is closed once fully processed.      *      * @param process the process      * param outputStream the output stream to send to standard error of the process      */
specifier|private
name|void
name|sendStdErrToOutputStream
parameter_list|(
specifier|final
name|Process
name|process
parameter_list|,
specifier|final
name|OutputStream
name|outputStream
parameter_list|)
block|{
name|multiThreadedStreamCopy
argument_list|(
name|process
operator|.
name|getErrorStream
argument_list|()
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks to see if the command can be run. Typically used with something      * like "myapp --version" to check to see if "myapp" is installed and on the      * path.      *      * @param checkCmd the check command to run      * @param errorValue what is considered an error value?      * @return whether or not the check completed without error      */
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
comment|/**      * Checks to see if the command can be run. Typically used with something      * like "myapp --version" to check to see if "myapp" is installed and on the      * path.      *      * @param checkCmd the check command to run      * @param errorValue what is considered an error value?      * @return whether or not the check completed without error      */
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
decl_stmt|;
if|if
condition|(
name|checkCmd
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
name|checkCmd
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
name|checkCmd
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
end_class

end_unit

