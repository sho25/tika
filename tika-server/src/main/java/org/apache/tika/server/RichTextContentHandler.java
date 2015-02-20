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
name|server
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|WriteOutContentHandler
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
name|Attributes
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
class|class
name|RichTextContentHandler
extends|extends
name|WriteOutContentHandler
block|{
specifier|public
name|RichTextContentHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"img"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
operator|&&
name|attributes
operator|.
name|getValue
argument_list|(
literal|"alt"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|nfo
init|=
literal|"[image: "
operator|+
name|attributes
operator|.
name|getValue
argument_list|(
literal|"alt"
argument_list|)
operator|+
literal|']'
decl_stmt|;
name|characters
argument_list|(
name|nfo
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|nfo
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"a"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
operator|&&
name|attributes
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|nfo
init|=
literal|"[bookmark: "
operator|+
name|attributes
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|']'
decl_stmt|;
name|characters
argument_list|(
name|nfo
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|nfo
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

