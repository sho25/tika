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
name|cli
package|;
end_package

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|Comparator
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXTransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TransformerHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|BasicConfigurator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
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
name|log4j
operator|.
name|SimpleLayout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|WriterAppender
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
name|gui
operator|.
name|TikaGUI
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
name|html
operator|.
name|BoilerpipeContentHandler
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
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Simple command line interface for Apache Tika.  */
end_comment

begin_class
specifier|public
class|class
name|TikaCLI
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|BasicConfigurator
operator|.
name|configure
argument_list|(
operator|new
name|WriterAppender
argument_list|(
operator|new
name|SimpleLayout
argument_list|()
argument_list|,
name|System
operator|.
name|err
argument_list|)
argument_list|)
expr_stmt|;
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
name|TikaCLI
name|cli
init|=
operator|new
name|TikaCLI
argument_list|()
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cli
operator|.
name|process
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cli
operator|.
name|pipeMode
condition|)
block|{
name|cli
operator|.
name|process
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
interface|interface
name|OutputType
block|{
name|ContentHandler
name|getContentHandler
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
specifier|private
specifier|final
name|OutputType
name|XML
init|=
operator|new
name|OutputType
argument_list|()
block|{
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getTransformerHandler
argument_list|(
literal|"xml"
argument_list|,
name|encoding
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|OutputType
name|HTML
init|=
operator|new
name|OutputType
argument_list|()
block|{
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getTransformerHandler
argument_list|(
literal|"html"
argument_list|,
name|encoding
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|OutputType
name|TEXT
init|=
operator|new
name|OutputType
argument_list|()
block|{
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|BodyContentHandler
argument_list|(
name|getSystemOutWriter
argument_list|(
name|encoding
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|OutputType
name|TEXT_MAIN
init|=
operator|new
name|OutputType
argument_list|()
block|{
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|BoilerpipeContentHandler
argument_list|(
name|getSystemOutWriter
argument_list|(
name|encoding
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|OutputType
name|METADATA
init|=
operator|new
name|OutputType
argument_list|()
block|{
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|getSystemOutWriter
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DefaultHandler
argument_list|()
block|{
specifier|public
name|void
name|endDocument
parameter_list|()
block|{
name|String
index|[]
name|names
init|=
name|metadata
operator|.
name|names
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|writer
operator|.
name|println
argument_list|(
name|name
operator|+
literal|": "
operator|+
name|metadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|OutputType
name|LANGUAGE
init|=
operator|new
name|OutputType
argument_list|()
block|{
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|getSystemOutWriter
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DefaultHandler
argument_list|()
block|{
specifier|public
name|void
name|endDocument
parameter_list|()
block|{
name|String
name|language
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|LANGUAGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|language
operator|==
literal|null
condition|)
block|{
name|language
operator|=
literal|"No language detected"
expr_stmt|;
block|}
name|String
name|contentLanguage
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_LANGUAGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentLanguage
operator|==
literal|null
condition|)
block|{
name|contentLanguage
operator|=
literal|"No language detected"
expr_stmt|;
block|}
name|writer
operator|.
name|println
argument_list|(
name|Metadata
operator|.
name|LANGUAGE
operator|+
literal|": "
operator|+
name|language
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
name|Metadata
operator|.
name|CONTENT_LANGUAGE
operator|+
literal|": "
operator|+
name|contentLanguage
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|ParseContext
name|context
decl_stmt|;
specifier|private
name|AutoDetectParser
name|parser
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
specifier|private
name|OutputType
name|type
init|=
name|XML
decl_stmt|;
comment|/**      * Output character encoding, or<code>null</code> for platform default      */
specifier|private
name|String
name|encoding
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|pipeMode
init|=
literal|true
decl_stmt|;
specifier|public
name|TikaCLI
parameter_list|()
throws|throws
name|TransformerConfigurationException
block|{
name|context
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|parser
operator|=
operator|new
name|AutoDetectParser
argument_list|()
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
specifier|public
name|void
name|process
parameter_list|(
name|String
name|arg
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-?"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--help"
argument_list|)
condition|)
block|{
name|pipeMode
operator|=
literal|false
expr_stmt|;
name|usage
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-v"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--verbose"
argument_list|)
condition|)
block|{
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-g"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--gui"
argument_list|)
condition|)
block|{
name|pipeMode
operator|=
literal|false
expr_stmt|;
name|TikaGUI
operator|.
name|main
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"--list-parser"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--list-parsers"
argument_list|)
condition|)
block|{
name|pipeMode
operator|=
literal|false
expr_stmt|;
name|displayParsers
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"--list-parser-detail"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--list-parser-details"
argument_list|)
condition|)
block|{
name|pipeMode
operator|=
literal|false
expr_stmt|;
name|displayParsers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"--list-met-models"
argument_list|)
condition|)
block|{
name|pipeMode
operator|=
literal|false
expr_stmt|;
name|displayMetModels
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"-e"
argument_list|)
condition|)
block|{
name|encoding
operator|=
name|arg
operator|.
name|substring
argument_list|(
literal|"-e"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"--encoding="
argument_list|)
condition|)
block|{
name|encoding
operator|=
name|arg
operator|.
name|substring
argument_list|(
literal|"--encoding="
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-x"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--xml"
argument_list|)
condition|)
block|{
name|type
operator|=
name|XML
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-h"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--html"
argument_list|)
condition|)
block|{
name|type
operator|=
name|HTML
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-t"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--text"
argument_list|)
condition|)
block|{
name|type
operator|=
name|TEXT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-T"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--text-main"
argument_list|)
condition|)
block|{
name|type
operator|=
name|TEXT_MAIN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-m"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--metadata"
argument_list|)
condition|)
block|{
name|type
operator|=
name|METADATA
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-l"
argument_list|)
operator|||
name|arg
operator|.
name|equals
argument_list|(
literal|"--language"
argument_list|)
condition|)
block|{
name|type
operator|=
name|LANGUAGE
expr_stmt|;
block|}
else|else
block|{
name|pipeMode
operator|=
literal|false
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|parser
operator|.
name|parse
argument_list|(
name|System
operator|.
name|in
argument_list|,
name|type
operator|.
name|getContentHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|URL
name|url
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|arg
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
name|url
operator|=
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|url
operator|=
operator|new
name|URL
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|input
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
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|type
operator|.
name|getContentHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|usage
parameter_list|()
block|{
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"usage: tika [option] [file]"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Options:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -?  or --help        Print this usage message"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -v  or --verbose     Print debug level messages"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -g  or --gui         Start the Apache Tika GUI"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -x  or --xml         Output XHTML content (default)"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -h  or --html        Output HTML content"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -t  or --text        Output plain text content"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -T  or --text-main   Output plain text content (main content only)"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -m  or --metadata    Output only metadata"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -l  or --language    Output only language"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    -eX or --encoding=X  Use output encoding X"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    --list-parsers"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"         List the available document parsers"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    --list-parser-details"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"         List the available document parsers, and their supported mime types"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    --list-met-models"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"         List the available metadata models, and their supported keys"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Description:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    Apache Tika will parse the file(s) specified on the"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    command line and output the extracted text content"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    or metadata to standard output."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    Instead of a file name you can also specify the URL"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    of a document to be parsed."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    If no file name or URL is specified (or the special"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    name \"-\" is used), then the standard input stream"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    is parsed."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    Use the \"--gui\" (or \"-g\") option to start"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    the Apache Tika GUI. You can drag and drop files"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    from a normal file explorer to the GUI window to"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    extract text content and metadata from the files."
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|displayMetModels
parameter_list|()
block|{
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|modelClasses
init|=
name|Metadata
operator|.
name|class
operator|.
name|getInterfaces
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|modelClasses
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|o1
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
block|)
function|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|modelClass
range|:
name|modelClasses
control|)
block|{
comment|// we don't care about internal Tika met classes
comment|// if we do, then we can take this conditional out
if|if
condition|(
name|modelClass
operator|.
name|getSimpleName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Tika"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|modelClass
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Field
index|[]
name|keyFields
init|=
name|modelClass
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|keyFields
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Field
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Field
name|o1
parameter_list|,
name|Field
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Field
name|keyField
range|:
name|keyFields
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" "
operator|+
name|keyField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_function
specifier|private
name|void
name|displayParsers
parameter_list|(
name|boolean
name|includeMimeTypes
parameter_list|)
block|{
comment|// Invert the map
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|supported
init|=
name|parser
operator|.
name|getParsers
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Parser
argument_list|,
name|Set
argument_list|<
name|MediaType
argument_list|>
argument_list|>
name|parsers
init|=
operator|new
name|HashMap
argument_list|<
name|Parser
argument_list|,
name|Set
argument_list|<
name|MediaType
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|e
range|:
name|supported
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|parsers
operator|.
name|containsKey
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|parsers
operator|.
name|put
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|parsers
operator|.
name|get
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Get a nicely sorted list of the parsers
name|Parser
index|[]
name|sortedParsers
init|=
name|parsers
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Parser
index|[
name|parsers
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|sortedParsers
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Parser
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Parser
name|p1
parameter_list|,
name|Parser
name|p2
parameter_list|)
block|{
name|String
name|name1
init|=
name|p1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|name2
init|=
name|p2
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|name1
operator|.
name|compareTo
argument_list|(
name|name2
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Display
for|for
control|(
name|Parser
name|p
range|:
name|sortedParsers
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|p
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeMimeTypes
condition|)
block|{
for|for
control|(
name|MediaType
name|mt
range|:
name|parsers
operator|.
name|get
argument_list|(
name|p
argument_list|)
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|mt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_function

begin_comment
comment|/**      * Returns a {@link System#out} writer with the given output encoding.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-277">TIKA-277</a>      * @param encoding output encoding,      *                 or<code>null</code> for the platform default      * @return {@link System#out} writer      * @throws UnsupportedEncodingException      *         if the configured encoding is not supported      */
end_comment

begin_function
specifier|private
specifier|static
name|Writer
name|getSystemOutWriter
parameter_list|(
name|String
name|encoding
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|encoding
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"mac os x"
argument_list|)
condition|)
block|{
comment|// TIKA-324: Override the default encoding on Mac OS X
return|return
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
return|;
block|}
block|}
end_function

begin_comment
comment|/**      * Returns a transformer handler that serializes incoming SAX events      * to XHTML or HTML (depending the given method) using the given output      * encoding.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-277">TIKA-277</a>      * @param method "xml" or "html"      * @param encoding output encoding,      *                 or<code>null</code> for the platform default      * @return {@link System#out} transformer handler      * @throws TransformerConfigurationException      *         if the transformer can not be created      */
end_comment

begin_function
specifier|private
specifier|static
name|TransformerHandler
name|getTransformerHandler
parameter_list|(
name|String
name|method
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
name|SAXTransformerFactory
name|factory
init|=
operator|(
name|SAXTransformerFactory
operator|)
name|SAXTransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
name|method
argument_list|)
expr_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|setResult
argument_list|(
operator|new
name|StreamResult
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|handler
return|;
block|}
end_function

unit|}
end_unit

