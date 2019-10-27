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
name|xliff
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
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Content Handler for XLIFF 1.2 documents.  */
end_comment

begin_class
specifier|public
class|class
name|XLIFF12ContentHandler
extends|extends
name|DefaultHandler
block|{
specifier|private
name|int
name|numberOfFiles
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|numberOfTUs
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|inTransUnit
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|XHTMLContentHandler
name|xhtml
decl_stmt|;
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
name|XLIFF12ContentHandler
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|xhtml
operator|=
name|xhtml
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
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
specifier|final
name|AttributesImpl
name|attributeVals
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attributeVals
operator|.
name|setAttributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"file"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|numberOfFiles
operator|++
expr_stmt|;
comment|// Write out the original file name
name|metadata
operator|.
name|add
argument_list|(
literal|"original"
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
literal|"source-language"
argument_list|)
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"h1"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|"original"
argument_list|)
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"h1"
argument_list|)
expr_stmt|;
comment|// Add the files source and target languages
name|metadata
operator|.
name|add
argument_list|(
literal|"source-language"
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
literal|"source-language"
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"target-language"
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
literal|"target-language"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"trans-unit"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|numberOfTUs
operator|++
expr_stmt|;
name|inTransUnit
operator|=
literal|true
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
name|attributeVals
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"source"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
name|attributeVals
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"target"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
name|attributeVals
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
literal|"file"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"trans-unit"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|inTransUnit
operator|=
literal|false
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"source"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"target"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|inTransUnit
operator|&&
name|length
operator|!=
literal|0
condition|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"file-count"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numberOfFiles
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"tu-count"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numberOfTUs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
