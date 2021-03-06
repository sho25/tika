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
operator|.
name|xwpf
operator|.
name|ml2006
package|;
end_package

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
name|parser
operator|.
name|microsoft
operator|.
name|OfficeParserConfig
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
name|microsoft
operator|.
name|ooxml
operator|.
name|OOXMLWordAndPowerPointTextHandler
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
name|microsoft
operator|.
name|ooxml
operator|.
name|OOXMLTikaBodyPartHandler
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Simple wrapper/extension of OOXMLWordAndPowerPointTextHandler to fit  * into the inline parsing scheme.  */
end_comment

begin_class
class|class
name|WordAndPowerPointTextPartHandler
extends|extends
name|OOXMLWordAndPowerPointTextHandler
implements|implements
name|PartHandler
block|{
specifier|private
specifier|final
name|String
name|contentType
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|public
name|WordAndPowerPointTextPartHandler
parameter_list|(
name|String
name|contentType
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|RelationshipsManager
name|relationshipsManager
parameter_list|,
name|OfficeParserConfig
name|officeParserConfig
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|OOXMLTikaBodyPartHandler
argument_list|(
name|xhtml
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|officeParserConfig
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentType
operator|=
name|contentType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|contentType
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endPart
parameter_list|()
throws|throws
name|SAXException
throws|,
name|TikaException
block|{
comment|//no-op
block|}
block|}
end_class

end_unit

