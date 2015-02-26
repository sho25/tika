begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   *     http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|strings
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
name|Collections
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
name|Set
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
name|Callable
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
name|ExecutionException
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
name|FutureTask
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
name|TimeUnit
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
name|TimeoutException
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

begin_comment
comment|/**  * Parser that uses the "strings" (or strings-alternative) command to find the  * printable strings in a object, or other binary, file  * (application/octet-stream). Useful as "best-effort" parser for files detected  * as application/octet-stream.  *   * @author gtotaro  *  */
end_comment

begin_class
specifier|public
class|class
name|StringsParser
extends|extends
name|AbstractParser
block|{
comment|/** 	 * Serial version UID 	 */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|802566634661575025L
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
name|singleton
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|StringsConfig
name|DEFAULT_STRINGS_CONFIG
init|=
operator|new
name|StringsConfig
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FileConfig
name|DEFAULT_FILE_CONFIG
init|=
operator|new
name|FileConfig
argument_list|()
decl_stmt|;
comment|/* 	 * This map is organized as follows: 	 * command's pathname (String) -> is it present? (Boolean), does it support -e option? (Boolean) 	 * It stores check results for command and, if present, -e (encoding) option. 	 */
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
index|[]
argument_list|>
name|STRINGS_PRESENT
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
index|[]
argument_list|>
argument_list|()
decl_stmt|;
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
name|StringsConfig
name|stringsConfig
init|=
name|context
operator|.
name|get
argument_list|(
name|StringsConfig
operator|.
name|class
argument_list|,
name|DEFAULT_STRINGS_CONFIG
argument_list|)
decl_stmt|;
name|FileConfig
name|fileConfig
init|=
name|context
operator|.
name|get
argument_list|(
name|FileConfig
operator|.
name|class
argument_list|,
name|DEFAULT_FILE_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hasStrings
argument_list|(
name|stringsConfig
argument_list|)
condition|)
block|{
return|return;
block|}
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|File
name|input
init|=
name|tis
operator|.
name|getFile
argument_list|()
decl_stmt|;
comment|// Metadata
name|metadata
operator|.
name|set
argument_list|(
literal|"strings:min-len"
argument_list|,
literal|""
operator|+
name|stringsConfig
operator|.
name|getMinLength
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"strings:encoding"
argument_list|,
name|stringsConfig
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"strings:file_output"
argument_list|,
name|doFile
argument_list|(
name|input
argument_list|,
name|fileConfig
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|totalBytes
init|=
literal|0
decl_stmt|;
comment|// Content
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
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|totalBytes
operator|=
name|doStrings
argument_list|(
name|input
argument_list|,
name|stringsConfig
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
comment|// Metadata
name|metadata
operator|.
name|set
argument_list|(
literal|"strings:length"
argument_list|,
literal|""
operator|+
name|totalBytes
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Checks if the "strings" command is supported. 	 *  	 * @param config 	 *            {@see StringsConfig} object used for testing the strings 	 *            command. 	 * @return Returns returns {@code true} if the strings command is supported. 	 */
specifier|private
name|boolean
name|hasStrings
parameter_list|(
name|StringsConfig
name|config
parameter_list|)
block|{
name|String
name|stringsProg
init|=
name|config
operator|.
name|getStringsPath
argument_list|()
operator|+
name|getStringsProg
argument_list|()
decl_stmt|;
if|if
condition|(
name|STRINGS_PRESENT
operator|.
name|containsKey
argument_list|(
name|stringsProg
argument_list|)
condition|)
block|{
return|return
name|STRINGS_PRESENT
operator|.
name|get
argument_list|(
name|stringsProg
argument_list|)
index|[
literal|0
index|]
return|;
block|}
name|String
index|[]
name|checkCmd
init|=
block|{
name|stringsProg
block|,
literal|"--version"
block|}
decl_stmt|;
try|try
block|{
name|boolean
name|hasStrings
init|=
name|ExternalParser
operator|.
name|check
argument_list|(
name|checkCmd
argument_list|)
decl_stmt|;
name|boolean
name|encodingOpt
init|=
literal|false
decl_stmt|;
comment|// Check if the -e option (encoding) is supported
if|if
condition|(
operator|!
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
condition|)
block|{
name|String
index|[]
name|checkOpt
init|=
block|{
name|stringsProg
block|,
literal|"-e"
block|,
literal|""
operator|+
name|config
operator|.
name|getEncoding
argument_list|()
operator|.
name|get
argument_list|()
block|,
literal|"/dev/null"
block|}
decl_stmt|;
name|int
index|[]
name|errorValues
init|=
block|{
literal|1
block|,
literal|2
block|}
decl_stmt|;
comment|// Exit status code: 1 = general error; 2 = incorrect usage.
name|encodingOpt
operator|=
name|ExternalParser
operator|.
name|check
argument_list|(
name|checkOpt
argument_list|,
name|errorValues
argument_list|)
expr_stmt|;
block|}
name|Boolean
index|[]
name|values
init|=
block|{
name|hasStrings
block|,
name|encodingOpt
block|}
decl_stmt|;
name|STRINGS_PRESENT
operator|.
name|put
argument_list|(
name|stringsProg
argument_list|,
name|values
argument_list|)
expr_stmt|;
return|return
name|hasStrings
return|;
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|ncdfe
parameter_list|)
block|{
comment|// This happens under OSGi + Fork Parser - see TIKA-1507
comment|// As a workaround for now, just say we can't use strings
comment|// TODO Resolve it so we don't need this try/catch block
name|Boolean
index|[]
name|values
init|=
block|{
literal|false
block|,
literal|false
block|}
decl_stmt|;
name|STRINGS_PRESENT
operator|.
name|put
argument_list|(
name|stringsProg
argument_list|,
name|values
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/** 	 * Checks if the "file" command is supported. 	 *  	 * @param config 	 * @return 	 */
specifier|private
name|boolean
name|hasFile
parameter_list|(
name|FileConfig
name|config
parameter_list|)
block|{
name|String
name|fileProg
init|=
name|config
operator|.
name|getFilePath
argument_list|()
operator|+
name|getFileProg
argument_list|()
decl_stmt|;
name|String
index|[]
name|checkCmd
init|=
block|{
name|fileProg
block|,
literal|"--version"
block|}
decl_stmt|;
name|boolean
name|hasFile
init|=
name|ExternalParser
operator|.
name|check
argument_list|(
name|checkCmd
argument_list|)
decl_stmt|;
return|return
name|hasFile
return|;
block|}
comment|/** 	 * Runs the "strings" command on the given file. 	 *  	 * @param input 	 *            {@see File} object that represents the file to parse. 	 * @param config 	 *            {@see StringsConfig} object including the strings 	 *            configuration. 	 * @param xhtml 	 *            {@see XHTMLContentHandler} object. 	 * @return the total number of bytes read using the strings command. 	 * @throws IOException 	 *             if any I/O error occurs. 	 * @throws TikaException 	 *             if the parsing process has been interrupted. 	 * @throws SAXException 	 */
specifier|private
name|int
name|doStrings
parameter_list|(
name|File
name|input
parameter_list|,
name|StringsConfig
name|config
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
throws|,
name|SAXException
block|{
name|String
name|stringsProg
init|=
name|config
operator|.
name|getStringsPath
argument_list|()
operator|+
name|getStringsProg
argument_list|()
decl_stmt|;
comment|// Builds the command array
name|ArrayList
argument_list|<
name|String
argument_list|>
name|cmdList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|cmdList
operator|.
name|add
argument_list|(
name|stringsProg
argument_list|)
expr_stmt|;
name|cmdList
operator|.
name|add
argument_list|(
literal|"-n"
argument_list|)
expr_stmt|;
name|cmdList
operator|.
name|add
argument_list|(
literal|""
operator|+
name|config
operator|.
name|getMinLength
argument_list|()
argument_list|)
expr_stmt|;
empty_stmt|;
comment|// Currently, encoding option is not supported by Windows (and other) versions
if|if
condition|(
name|STRINGS_PRESENT
operator|.
name|get
argument_list|(
name|stringsProg
argument_list|)
index|[
literal|1
index|]
condition|)
block|{
name|cmdList
operator|.
name|add
argument_list|(
literal|"-e"
argument_list|)
expr_stmt|;
name|cmdList
operator|.
name|add
argument_list|(
literal|""
operator|+
name|config
operator|.
name|getEncoding
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cmdList
operator|.
name|add
argument_list|(
name|input
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|cmd
init|=
name|cmdList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|cmdList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
specifier|final
name|Process
name|process
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
name|InputStream
name|out
init|=
name|process
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|FutureTask
argument_list|<
name|Integer
argument_list|>
name|waitTask
init|=
operator|new
name|FutureTask
argument_list|<
name|Integer
argument_list|>
argument_list|(
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|process
operator|.
name|waitFor
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Thread
name|waitThread
init|=
operator|new
name|Thread
argument_list|(
name|waitTask
argument_list|)
decl_stmt|;
name|waitThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Reads content printed out by "strings" command
name|int
name|totalBytes
init|=
literal|0
decl_stmt|;
name|totalBytes
operator|=
name|extractOutput
argument_list|(
name|out
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
try|try
block|{
name|waitTask
operator|.
name|get
argument_list|(
name|config
operator|.
name|getTimeout
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|waitThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
name|StringsParser
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" interrupted"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ee
parameter_list|)
block|{
comment|// should not be thrown
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|te
parameter_list|)
block|{
name|waitThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
name|StringsParser
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" timeout"
argument_list|,
name|te
argument_list|)
throw|;
block|}
return|return
name|totalBytes
return|;
block|}
comment|/** 	 * Extracts ASCII strings using the "strings" command. 	 *  	 * @param stream 	 *            {@see InputStream} object used for reading the binary file. 	 * @param xhtml 	 *            {@see XHTMLContentHandler} object. 	 * @return the total number of bytes read using the "strings" command. 	 * @throws SAXException 	 *             if the content element could not be written. 	 * @throws IOException 	 *             if any I/O error occurs. 	 */
specifier|private
name|int
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
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
name|int
name|totalBytes
init|=
literal|0
decl_stmt|;
try|try
block|{
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
name|IOUtils
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|n
operator|>
literal|0
condition|)
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
name|totalBytes
operator|+=
name|n
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|totalBytes
return|;
block|}
comment|/** 	 * Runs the "file" command on the given file that aims at providing an 	 * alternative way to determine the file type. 	 *  	 * @param input 	 *            {@see File} object that represents the file to detect. 	 * @return the file type provided by the "file" command using the "-b" 	 *         option (it stands for "brief mode"). 	 * @throws IOException 	 *             if any I/O error occurs. 	 */
specifier|private
name|String
name|doFile
parameter_list|(
name|File
name|input
parameter_list|,
name|FileConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|hasFile
argument_list|(
name|config
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Builds the command array
name|ArrayList
argument_list|<
name|String
argument_list|>
name|cmdList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|cmdList
operator|.
name|add
argument_list|(
name|config
operator|.
name|getFilePath
argument_list|()
operator|+
name|getFileProg
argument_list|()
argument_list|)
expr_stmt|;
name|cmdList
operator|.
name|add
argument_list|(
literal|"-b"
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|isMimetype
argument_list|()
condition|)
block|{
name|cmdList
operator|.
name|add
argument_list|(
literal|"-I"
argument_list|)
expr_stmt|;
block|}
name|cmdList
operator|.
name|add
argument_list|(
name|input
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|cmd
init|=
name|cmdList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|cmdList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
specifier|final
name|Process
name|process
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
name|InputStream
name|out
init|=
name|process
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
name|String
name|fileOutput
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|out
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|fileOutput
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// file output not available!
name|fileOutput
operator|=
literal|""
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|fileOutput
return|;
block|}
specifier|public
specifier|static
name|String
name|getStringsProg
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
condition|?
literal|"strings.exe"
else|:
literal|"strings"
return|;
block|}
specifier|public
specifier|static
name|String
name|getFileProg
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
condition|?
literal|"file.exe"
else|:
literal|"file"
return|;
block|}
block|}
end_class

end_unit
