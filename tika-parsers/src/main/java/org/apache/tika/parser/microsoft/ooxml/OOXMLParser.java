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
operator|.
name|ooxml
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
name|Locale
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
comment|/**  * Office Open XML (OOXML) parser.  *   */
end_comment

begin_class
specifier|public
class|class
name|OOXMLParser
implements|implements
name|Parser
block|{
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
literal|"x-tika-ooxml"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint.presentation.macroenabled.12"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.presentationml.template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.presentationml.slideshow"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint.slideshow.macroenabled.12"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint.addin.macroenabled.12"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.sheet.macroenabled.12"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.spreadsheetml.template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.template.macroenabled.12"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.addin.macroenabled.12"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-word.document.macroenabled.12"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.wordprocessingml.template"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-word.template.macroenabled.12"
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
name|OOXMLExtractorFactory
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0.      */
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

