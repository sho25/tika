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
name|mat
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
comment|//JMatIO imports
end_comment

begin_import
import|import
name|com
operator|.
name|jmatio
operator|.
name|io
operator|.
name|MatFileHeader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jmatio
operator|.
name|io
operator|.
name|MatFileReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jmatio
operator|.
name|types
operator|.
name|MLArray
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jmatio
operator|.
name|types
operator|.
name|MLStructure
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_class
specifier|public
class|class
name|MatParser
extends|extends
name|AbstractParser
block|{
specifier|public
specifier|static
specifier|final
name|String
name|MATLAB_MIME_TYPE
init|=
literal|"application/x-matlab-data"
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
literal|"x-matlab-data"
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
comment|//Set MIME type as Matlab
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|MATLAB_MIME_TYPE
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Use TIS so we can spool a temp file for parsing.
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|//Extract information from header file
name|MatFileReader
name|mfr
init|=
operator|new
name|MatFileReader
argument_list|(
name|tis
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
comment|//input .mat file
name|MatFileHeader
name|hdr
init|=
name|mfr
operator|.
name|getMatFileHeader
argument_list|()
decl_stmt|;
comment|//.mat header information
comment|// Example header: "MATLAB 5.0 MAT-file, Platform: MACI64, Created on: Sun Mar  2 23:41:57 2014"
name|String
index|[]
name|parts
init|=
name|hdr
operator|.
name|getDescription
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
comment|// Break header information into its parts
if|if
condition|(
name|parts
index|[
literal|2
index|]
operator|.
name|contains
argument_list|(
literal|"Created"
argument_list|)
condition|)
block|{
name|int
name|lastIndex1
init|=
name|parts
index|[
literal|2
index|]
operator|.
name|lastIndexOf
argument_list|(
literal|"Created on:"
argument_list|)
decl_stmt|;
name|String
name|dateCreated
init|=
name|parts
index|[
literal|2
index|]
operator|.
name|substring
argument_list|(
name|lastIndex1
operator|+
literal|"Created on:"
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"createdOn"
argument_list|,
name|dateCreated
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parts
index|[
literal|1
index|]
operator|.
name|contains
argument_list|(
literal|"Platform"
argument_list|)
condition|)
block|{
name|int
name|lastIndex2
init|=
name|parts
index|[
literal|1
index|]
operator|.
name|lastIndexOf
argument_list|(
literal|"Platform:"
argument_list|)
decl_stmt|;
name|String
name|platform
init|=
name|parts
index|[
literal|1
index|]
operator|.
name|substring
argument_list|(
name|lastIndex2
operator|+
literal|"Platform:"
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"platform"
argument_list|,
name|platform
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parts
index|[
literal|0
index|]
operator|.
name|contains
argument_list|(
literal|"MATLAB"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"fileType"
argument_list|,
name|parts
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Get endian indicator from header file
name|String
name|endianBytes
init|=
operator|new
name|String
argument_list|(
name|hdr
operator|.
name|getEndianIndicator
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
comment|// Retrieve endian bytes and convert to string
name|String
name|endianCode
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|endianBytes
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
comment|// Convert bytes to characters to string
name|metadata
operator|.
name|set
argument_list|(
literal|"endian"
argument_list|,
name|endianCode
argument_list|)
expr_stmt|;
comment|//Text output
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
comment|//Loop through each variable
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|MLArray
argument_list|>
name|entry
range|:
name|mfr
operator|.
name|getContent
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|varName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|MLArray
name|varData
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|varName
operator|+
literal|":"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|varData
argument_list|)
argument_list|)
expr_stmt|;
comment|// If the variable is a structure, extract variable info from structure
if|if
condition|(
name|varData
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|MLStructure
name|mlStructure
init|=
operator|(
name|MLStructure
operator|)
name|mfr
operator|.
name|getMLArray
argument_list|(
name|varName
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|newline
argument_list|()
expr_stmt|;
for|for
control|(
name|MLArray
name|element
range|:
name|mlStructure
operator|.
name|getAllFields
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"li"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|element
argument_list|)
argument_list|)
expr_stmt|;
comment|// If there is an embedded structure, extract variable info.
if|if
condition|(
name|element
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
comment|// Should this actually be a recursive call?
name|xhtml
operator|.
name|element
argument_list|(
literal|"li"
argument_list|,
name|element
operator|.
name|contentToString
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"li"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ul"
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Error parsing Matlab file with MatParser"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

