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
name|eval
operator|.
name|io
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|utils
operator|.
name|XMLReaderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|XMLLogReader
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XMLLogReader
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//class that wraps a logger's xml output
comment|//into a single xml parseable input stream.
specifier|public
name|void
name|read
parameter_list|(
name|InputStream
name|xmlLogFileIs
parameter_list|,
name|XMLLogMsgHandler
name|handler
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|InputStream
name|is
init|=
operator|new
name|LogXMLWrappingInputStream
argument_list|(
name|xmlLogFileIs
argument_list|)
decl_stmt|;
name|XMLInputFactory
name|factory
init|=
name|XMLReaderUtils
operator|.
name|getXMLInputFactory
argument_list|()
decl_stmt|;
name|XMLStreamReader
name|reader
init|=
name|factory
operator|.
name|createXMLStreamReader
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|Level
name|level
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|reader
operator|.
name|next
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|reader
operator|.
name|getEventType
argument_list|()
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
if|if
condition|(
literal|"event"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|level
operator|=
name|Level
operator|.
name|toLevel
argument_list|(
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"level"
argument_list|)
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"message"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|handler
operator|.
name|handleMsg
argument_list|(
name|level
argument_list|,
name|reader
operator|.
name|getElementText
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error parsing: {}"
argument_list|,
name|reader
operator|.
name|getElementText
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"SQLException: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
literal|"event"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|level
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"message"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
comment|//do we care any more?
block|}
break|break;
block|}
empty_stmt|;
block|}
block|}
class|class
name|LogXMLWrappingInputStream
extends|extends
name|InputStream
block|{
comment|//plagiarized from log4j's chainsaw
specifier|private
specifier|final
specifier|static
name|String
name|HEADER
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
operator|+
literal|"<log4j:eventSet version=\"1.2\" "
operator|+
literal|"xmlns:log4j=\"http://jakarta.apache.org/log4j/\">"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FOOTER
init|=
literal|"</log4j:eventSet>"
decl_stmt|;
specifier|private
name|InputStream
index|[]
name|streams
decl_stmt|;
name|int
name|currentStreamIndex
init|=
literal|0
decl_stmt|;
specifier|private
name|LogXMLWrappingInputStream
parameter_list|(
name|InputStream
name|xmlLogFileIs
parameter_list|)
block|{
name|streams
operator|=
operator|new
name|InputStream
index|[
literal|3
index|]
expr_stmt|;
name|streams
index|[
literal|0
index|]
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|HEADER
operator|.
name|getBytes
argument_list|(
name|IOUtils
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|streams
index|[
literal|1
index|]
operator|=
name|xmlLogFileIs
expr_stmt|;
name|streams
index|[
literal|2
index|]
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|FOOTER
operator|.
name|getBytes
argument_list|(
name|IOUtils
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|c
init|=
name|streams
index|[
name|currentStreamIndex
index|]
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|streams
index|[
name|currentStreamIndex
index|]
argument_list|)
expr_stmt|;
while|while
condition|(
name|currentStreamIndex
operator|<
name|streams
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|currentStreamIndex
operator|++
expr_stmt|;
name|int
name|tmpC
init|=
name|streams
index|[
name|currentStreamIndex
index|]
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tmpC
operator|<
literal|0
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|streams
index|[
name|currentStreamIndex
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|tmpC
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
return|return
name|c
return|;
block|}
block|}
block|}
end_class

end_unit

