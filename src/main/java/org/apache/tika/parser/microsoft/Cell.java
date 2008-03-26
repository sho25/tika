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
name|microsoft
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Cell of content. Classes that implement this interface are used by  * Tika parsers (currently just the MS Excel parser) to keep track of  * individual pieces of content before they are rendered to the XHTML  * SAX event stream.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cell
block|{
comment|/**      * Renders the content to the given XHTML SAX event stream.      *      * @param handler      * @throws SAXException      */
name|void
name|render
parameter_list|(
name|XHTMLContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
function_decl|;
block|}
end_interface

end_unit

