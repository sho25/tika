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
name|audio
package|;
end_package

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
name|ISO_8859_1
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sound
operator|.
name|midi
operator|.
name|InvalidMidiDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sound
operator|.
name|midi
operator|.
name|MetaMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sound
operator|.
name|midi
operator|.
name|MidiMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sound
operator|.
name|midi
operator|.
name|MidiSystem
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sound
operator|.
name|midi
operator|.
name|Patch
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sound
operator|.
name|midi
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sound
operator|.
name|midi
operator|.
name|Track
import|;
end_import

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

begin_class
specifier|public
class|class
name|MidiParser
extends|extends
name|AbstractParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6343278584336189432L
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
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-midi"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|audio
argument_list|(
literal|"midi"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
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
name|SUPPORTED_TYPES
return|;
block|}
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
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"audio/midi"
argument_list|)
expr_stmt|;
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
comment|// MidiSystem expects the stream to support the mark feature
if|if
condition|(
operator|!
name|stream
operator|.
name|markSupported
argument_list|()
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
try|try
block|{
name|Sequence
name|sequence
init|=
name|MidiSystem
operator|.
name|getSequence
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|Track
index|[]
name|tracks
init|=
name|sequence
operator|.
name|getTracks
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"tracks"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|tracks
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: Use XMPDM.TRACKS?
name|Patch
index|[]
name|patches
init|=
name|sequence
operator|.
name|getPatchList
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"patches"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|patches
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|float
name|type
init|=
name|sequence
operator|.
name|getDivisionType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Sequence
operator|.
name|PPQ
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"divisionType"
argument_list|,
literal|"PPQ"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Sequence
operator|.
name|SMPTE_24
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"divisionType"
argument_list|,
literal|"SMPTE_24"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Sequence
operator|.
name|SMPTE_25
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"divisionType"
argument_list|,
literal|"SMPTE_25"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Sequence
operator|.
name|SMPTE_30
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"divisionType"
argument_list|,
literal|"SMPTE_30"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Sequence
operator|.
name|SMPTE_30DROP
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"divisionType"
argument_list|,
literal|"SMPTE_30DROP"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Sequence
operator|.
name|SMPTE_24
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"divisionType"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Track
name|track
range|:
name|tracks
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|track
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|MidiMessage
name|message
init|=
name|track
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|instanceof
name|MetaMessage
condition|)
block|{
name|MetaMessage
name|meta
init|=
operator|(
name|MetaMessage
operator|)
name|message
decl_stmt|;
comment|// Types 1-15 are reserved for text events
if|if
condition|(
name|meta
operator|.
name|getType
argument_list|()
operator|>=
literal|1
operator|&&
name|meta
operator|.
name|getType
argument_list|()
operator|<=
literal|15
condition|)
block|{
comment|// FIXME: What's the encoding?
name|xhtml
operator|.
name|characters
argument_list|(
operator|new
name|String
argument_list|(
name|meta
operator|.
name|getData
argument_list|()
argument_list|,
name|ISO_8859_1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InvalidMidiDataException
name|ignore
parameter_list|)
block|{
comment|// There is no way to know whether this exception was
comment|// caused by the document being corrupted or by the format
comment|// just being unsupported. So we do nothing.
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

