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
name|netcdf
package|;
end_package

begin_comment
comment|//JDK imports
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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

begin_comment
comment|//TIKA imports
end_comment

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
name|IOUtils
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
name|parser
operator|.
name|Parser
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

begin_comment
comment|//NETCDF imports
end_comment

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
name|NetcdfFile
import|;
end_import

begin_comment
comment|/**  * A {@link Parser} for<a  * href="http://www.unidata.ucar.edu/software/netcdf/index.html">NetCDF</a>  * files using the UCAR, MIT-licensed<a  * href="http://www.unidata.ucar.edu/software/netcdf-java/">NetCDF for Java</a>  * API.  */
end_comment

begin_class
specifier|public
class|class
name|NetCDFParser
extends|extends
name|AbstractParser
block|{
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
literal|"x-netcdf"
argument_list|)
argument_list|)
decl_stmt|;
comment|/*      * (non-Javadoc)      *       * @see      * org.apache.tika.parser.Parser#getSupportedTypes(org.apache.tika.parser      * .ParseContext)      */
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
comment|/*      * (non-Javadoc)      *       * @see org.apache.tika.parser.Parser#parse(java.io.InputStream,      * org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata,      * org.apache.tika.parser.ParseContext)      */
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
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
literal|""
expr_stmt|;
block|}
try|try
block|{
name|NetcdfFile
name|ncFile
init|=
name|NetcdfFile
operator|.
name|openInMemory
argument_list|(
name|name
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
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
name|String
name|attrName
init|=
name|attr
operator|.
name|getName
argument_list|()
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
name|attrName
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
name|attrName
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
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

