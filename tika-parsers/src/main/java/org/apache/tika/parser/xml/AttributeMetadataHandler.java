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
name|xml
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
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Property
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

begin_comment
comment|/**  * SAX event handler that maps the contents of an XML attribute into  * a metadata field.  *  * @since Apache Tika 0.10  */
end_comment

begin_class
specifier|public
class|class
name|AttributeMetadataHandler
extends|extends
name|AbstractMetadataHandler
block|{
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
specifier|final
name|String
name|localName
decl_stmt|;
specifier|public
name|AttributeMetadataHandler
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|metadata
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|localName
operator|=
name|localName
expr_stmt|;
block|}
specifier|public
name|AttributeMetadataHandler
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|Property
name|property
parameter_list|)
block|{
name|super
argument_list|(
name|metadata
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|localName
operator|=
name|localName
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|attributes
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|this
operator|.
name|uri
argument_list|)
operator|&&
name|attributes
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|this
operator|.
name|localName
argument_list|)
condition|)
block|{
name|addMetadata
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

