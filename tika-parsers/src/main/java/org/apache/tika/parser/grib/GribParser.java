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
name|grib
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
name|File
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
name|TemporaryResources
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
name|metadata
operator|.
name|Property
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
name|TikaCoreProperties
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
name|AbstractParser
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
name|ucar
operator|.
name|nc2
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|ucar
operator|.
name|nc2
operator|.
name|Dimension
import|;
end_import

begin_import
import|import
name|ucar
operator|.
name|nc2
operator|.
name|NetcdfFile
import|;
end_import

begin_import
import|import
name|ucar
operator|.
name|nc2
operator|.
name|Variable
import|;
end_import

begin_import
import|import
name|ucar
operator|.
name|nc2
operator|.
name|dataset
operator|.
name|NetcdfDataset
import|;
end_import

begin_class
specifier|public
class|class
name|GribParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|7855458954474247655L
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GRIB_MIME_TYPE
init|=
literal|"application/x-grib2"
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-grib2"
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
comment|//Set MIME type as grib2
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|GRIB_MIME_TYPE
argument_list|)
expr_stmt|;
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|,
operator|new
name|TemporaryResources
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|gribFile
init|=
name|tis
operator|.
name|getFile
argument_list|()
decl_stmt|;
try|try
block|{
name|NetcdfFile
name|ncFile
init|=
name|NetcdfDataset
operator|.
name|openFile
argument_list|(
name|gribFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// first parse out the set of global attributes
for|for
control|(
name|Attribute
name|attr
range|:
name|ncFile
operator|.
name|getGlobalAttributes
argument_list|()
control|)
block|{
name|Property
name|property
init|=
name|resolveMetadataKey
argument_list|(
name|attr
operator|.
name|getFullName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|.
name|getDataType
argument_list|()
operator|.
name|isString
argument_list|()
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|property
argument_list|,
name|attr
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|attr
operator|.
name|getDataType
argument_list|()
operator|.
name|isNumeric
argument_list|()
condition|)
block|{
name|int
name|value
init|=
name|attr
operator|.
name|getNumericValue
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|property
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
literal|"dimensions:"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
for|for
control|(
name|Dimension
name|dim
range|:
name|ncFile
operator|.
name|getDimensions
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"li"
argument_list|,
name|dim
operator|.
name|getFullName
argument_list|()
operator|+
literal|"="
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|dim
operator|.
name|getLength
argument_list|()
argument_list|)
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
literal|"variables:"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
for|for
control|(
name|Variable
name|var
range|:
name|ncFile
operator|.
name|getVariables
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|var
operator|.
name|getDataType
argument_list|()
argument_list|)
operator|+
name|var
operator|.
name|getNameAndDimensions
argument_list|()
operator|+
literal|";"
argument_list|)
expr_stmt|;
for|for
control|(
name|Attribute
name|element
range|:
name|var
operator|.
name|getAttributes
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"li"
argument_list|,
literal|" :"
operator|+
name|element
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"NetCDF parse error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Property
name|resolveMetadataKey
parameter_list|(
name|String
name|localName
parameter_list|)
block|{
if|if
condition|(
literal|"title"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return
name|TikaCoreProperties
operator|.
name|TITLE
return|;
block|}
return|return
name|Property
operator|.
name|internalText
argument_list|(
name|localName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

