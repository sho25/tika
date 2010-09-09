begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|extractor
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
comment|/**  * Tika container extractor interface.  * Container Extractors provide access to the embeded  *  resources within container formats such as .zip and .doc   */
end_comment

begin_interface
specifier|public
interface|interface
name|ContainerExtractor
extends|extends
name|Serializable
block|{
comment|/**      * Is this Container Extractor able to process the      *  supplied container?      * @since Apache Tika 0.8      */
name|boolean
name|isSupported
parameter_list|(
name|TikaInputStream
name|input
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Processes a container file, and extracts all the embeded      * resources from within it.      *<p>      * The {@link EmbededResourceHandler} you supply will      * be called for each embeded resource in the container. It is      * up to you whether you process the contents of the resource or not.       *<p>      * The given document stream is consumed but not closed by this method.      * The responsibility to close the stream remains on the caller.      *<p>      * If required, nested containers (such as a .docx within a .zip)      * can automatically be recursed into, and processed inline. If      * no recurseExtractor is given, the nested containers will be      * treated as with any other embeded resources.      *      * @since Apache Tika 0.8      * @param stream the document stream (input)      * @param recurseExtractor the extractor to use on any embeded containers       * @param handler handler for the embeded files (output)      * @throws IOException if the document stream could not be read      * @throws TikaException if the container could not be parsed      */
name|void
name|extract
parameter_list|(
name|TikaInputStream
name|stream
parameter_list|,
name|ContainerExtractor
name|recurseExtractor
parameter_list|,
name|EmbededResourceHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
function_decl|;
block|}
end_interface

end_unit

