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
name|extractor
package|;
end_package

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

begin_interface
specifier|public
interface|interface
name|EmbeddedDocumentExtractor
block|{
name|boolean
name|shouldParseEmbedded
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
function_decl|;
comment|/**      * Processes the supplied embedded resource, calling the delegating      *  parser with the appropriate details.      * @param stream The embedded resource      * @param handler The handler to use      * @param metadata The metadata for the embedded resource      * @param outputHtml Should we output HTML for this resource, or has the parser already done so?      * @throws org.xml.sax.SAXException      * @throws java.io.IOException      */
name|void
name|parseEmbedded
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
name|boolean
name|outputHtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

