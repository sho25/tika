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
name|fork
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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

begin_class
class|class
name|ContentHandlerResource
implements|implements
name|ForkResource
block|{
specifier|private
specifier|final
name|ContentHandler
name|handler
decl_stmt|;
specifier|public
name|ContentHandlerResource
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|Throwable
name|process
parameter_list|(
name|DataInputStream
name|input
parameter_list|,
name|DataOutputStream
name|output
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|internalProcess
argument_list|(
name|input
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
return|return
name|e
return|;
block|}
block|}
specifier|private
name|void
name|internalProcess
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|int
name|type
init|=
name|input
operator|.
name|readUnsignedByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|START_DOCUMENT
condition|)
block|{
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|END_DOCUMENT
condition|)
block|{
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|START_PREFIX_MAPPING
condition|)
block|{
name|handler
operator|.
name|startPrefixMapping
argument_list|(
name|readString
argument_list|(
name|input
argument_list|)
argument_list|,
name|readString
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|END_PREFIX_MAPPING
condition|)
block|{
name|handler
operator|.
name|endPrefixMapping
argument_list|(
name|readString
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|START_ELEMENT
condition|)
block|{
name|String
name|uri
init|=
name|readString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|String
name|localName
init|=
name|readString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|String
name|qName
init|=
name|readString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|AttributesImpl
name|atts
init|=
literal|null
decl_stmt|;
name|int
name|n
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|>=
literal|0
condition|)
block|{
name|atts
operator|=
operator|new
name|AttributesImpl
argument_list|()
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|atts
operator|.
name|addAttribute
argument_list|(
name|readString
argument_list|(
name|input
argument_list|)
argument_list|,
name|readString
argument_list|(
name|input
argument_list|)
argument_list|,
name|readString
argument_list|(
name|input
argument_list|)
argument_list|,
name|readString
argument_list|(
name|input
argument_list|)
argument_list|,
name|readString
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|handler
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|END_ELEMENT
condition|)
block|{
name|String
name|uri
init|=
name|readString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|String
name|localName
init|=
name|readString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|String
name|qName
init|=
name|readString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|CHARACTERS
condition|)
block|{
name|char
index|[]
name|ch
init|=
name|readCharacters
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|IGNORABLE_WHITESPACE
condition|)
block|{
name|char
index|[]
name|ch
init|=
name|readCharacters
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|PROCESSING_INSTRUCTION
condition|)
block|{
name|handler
operator|.
name|processingInstruction
argument_list|(
name|readString
argument_list|(
name|input
argument_list|)
argument_list|,
name|readString
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ContentHandlerProxy
operator|.
name|SKIPPED_ENTITY
condition|)
block|{
name|handler
operator|.
name|skippedEntity
argument_list|(
name|readString
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|readString
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
return|return
name|input
operator|.
name|readUTF
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|char
index|[]
name|readCharacters
parameter_list|(
name|DataInputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|n
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|char
index|[]
name|ch
init|=
operator|new
name|char
index|[
name|n
index|]
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|ch
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readChar
argument_list|()
expr_stmt|;
block|}
return|return
name|ch
return|;
block|}
block|}
end_class

end_unit

