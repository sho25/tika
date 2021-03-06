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
package|;
end_package

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
name|Serializable
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
comment|/**  * Tika parser interface.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Parser
extends|extends
name|Serializable
block|{
comment|/**      * Returns the set of media types supported by this parser when used      * with the given parse context.      *      * @since Apache Tika 0.7      * @param context parse context      * @return immutable set of media types      */
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
function_decl|;
comment|/**      * Parses a document stream into a sequence of XHTML SAX events.      * Fills in related document metadata in the given metadata object.      *<p>      * The given document stream is consumed but not closed by this method.      * The responsibility to close the stream remains on the caller.      *<p>      * Information about the parsing context can be passed in the context      * parameter. See the parser implementations for the kinds of context      * information they expect.      *      * @since Apache Tika 0.5      * @param stream the document stream (input)      * @param handler handler for the XHTML SAX events (output)      * @param metadata document metadata (input and output)      * @param context parse context      * @throws IOException if the document stream could not be read      * @throws SAXException if the SAX events could not be processed      * @throws TikaException if the document could not be parsed      */
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
function_decl|;
block|}
end_interface

end_unit

