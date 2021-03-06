begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
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
name|TaggedContentHandler
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
name|TextContentHandler
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import

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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Enumeration
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
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_class
specifier|public
class|class
name|XMLTestBase
extends|extends
name|TikaTest
block|{
specifier|static
name|byte
index|[]
name|injectXML
parameter_list|(
name|byte
index|[]
name|input
parameter_list|,
name|byte
index|[]
name|toInject
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|startXML
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|endXML
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|input
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|input
index|[
name|i
index|]
operator|==
literal|'<'
operator|&&
name|i
operator|+
literal|1
operator|<
name|input
operator|.
name|length
operator|&&
name|input
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'?'
condition|)
block|{
name|startXML
operator|=
name|i
expr_stmt|;
block|}
if|if
condition|(
name|input
index|[
name|i
index|]
operator|==
literal|'?'
operator|&&
name|i
operator|+
literal|1
operator|<
name|input
operator|.
name|length
operator|&&
name|input
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'>'
condition|)
block|{
name|endXML
operator|=
name|i
operator|+
literal|1
expr_stmt|;
break|break;
block|}
block|}
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|startXML
operator|>
operator|-
literal|1
operator|&&
name|endXML
operator|>
operator|-
literal|1
condition|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|input
argument_list|,
name|startXML
argument_list|,
name|endXML
operator|-
name|startXML
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|bos
operator|.
name|write
argument_list|(
name|toInject
argument_list|)
expr_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|input
argument_list|,
name|endXML
operator|+
literal|1
argument_list|,
operator|(
name|input
operator|.
name|length
operator|-
name|endXML
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
specifier|static
name|Path
name|injectZippedXMLs
parameter_list|(
name|Path
name|original
parameter_list|,
name|byte
index|[]
name|toInject
parameter_list|,
name|boolean
name|includeSlides
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipFile
name|input
init|=
operator|new
name|ZipFile
argument_list|(
name|original
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|output
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|"tika-xxe-"
argument_list|,
literal|".zip"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|ZipOutputStream
name|outZip
init|=
operator|new
name|ZipOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|output
argument_list|)
argument_list|)
decl_stmt|;
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|zipEntryEnumeration
init|=
name|input
operator|.
name|entries
argument_list|()
decl_stmt|;
while|while
condition|(
name|zipEntryEnumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|ZipEntry
name|entry
init|=
name|zipEntryEnumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|input
operator|.
name|getInputStream
argument_list|(
name|entry
argument_list|)
argument_list|,
name|bos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
operator|&&
comment|//don't inject the slides because you'll get a bean exception
comment|//Unexpected node
operator|(
operator|!
name|includeSlides
operator|&&
operator|!
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"slides/slide"
argument_list|)
operator|)
condition|)
block|{
name|bytes
operator|=
name|injectXML
argument_list|(
name|bytes
argument_list|,
name|toInject
argument_list|)
expr_stmt|;
block|}
name|ZipEntry
name|outEntry
init|=
operator|new
name|ZipEntry
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|outZip
operator|.
name|putNextEntry
argument_list|(
name|outEntry
argument_list|)
expr_stmt|;
name|outZip
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|outZip
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|outZip
operator|.
name|flush
argument_list|()
expr_stmt|;
name|outZip
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|output
operator|.
name|toPath
argument_list|()
return|;
block|}
specifier|static
class|class
name|VulnerableDOMParser
extends|extends
name|AbstractParser
block|{
annotation|@
name|Override
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
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|TaggedContentHandler
name|tagged
init|=
operator|new
name|TaggedContentHandler
argument_list|(
name|handler
argument_list|)
decl_stmt|;
try|try
block|{
name|SAXParserFactory
name|saxParserFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|(
literal|"org.apache.xerces.parsers.SAXParser"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|saxParserFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|TextContentHandler
argument_list|(
name|handler
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"parser config ex"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|static
class|class
name|VulnerableSAXParser
extends|extends
name|AbstractParser
block|{
annotation|@
name|Override
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
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|TaggedContentHandler
name|tagged
init|=
operator|new
name|TaggedContentHandler
argument_list|(
name|handler
argument_list|)
decl_stmt|;
try|try
block|{
name|SAXParserFactory
name|saxParserFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|(
literal|"org.apache.xerces.jaxp.SAXParserFactoryImpl"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|saxParserFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|TextContentHandler
argument_list|(
name|handler
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"parser config ex"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|static
name|void
name|parse
parameter_list|(
name|String
name|testFileName
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

