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
name|example
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
name|StringWriter
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
name|DirectoryStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Tika
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
name|metadata
operator|.
name|serialization
operator|.
name|JsonMetadataList
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
name|AutoDetectParser
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
name|parser
operator|.
name|RecursiveParserWrapper
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
name|BasicContentHandlerFactory
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
name|BodyContentHandler
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
name|ContentHandlerFactory
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

begin_class
specifier|public
class|class
name|ParsingExample
block|{
comment|/**      * Example of how to use Tika's parseToString method to parse the content of a file,      * and return any text found.      *      * Note: Tika.parseToString() will extract content from the outer container      * document and any embedded/attached documents.      *      * @return The content of a file.      */
specifier|public
name|String
name|parseToStringExample
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|InputStream
name|stream
init|=
name|ParsingExample
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test.doc"
argument_list|)
decl_stmt|;
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|tika
operator|.
name|parseToString
argument_list|(
name|stream
argument_list|)
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Example of how to use Tika to parse a file when you do not know its file type      * ahead of time.      *      * AutoDetectParser attempts to discover the file's type automatically, then call      * the exact Parser built for that file type.      *      * The stream to be parsed by the Parser. In this case, we get a file from the      * resources folder of this project.      *      * Handlers are used to get the exact information you want out of the host of      * information gathered by Parsers. The body content handler, intuitively, extracts      * everything that would go between HTML body tags.      *      * The Metadata object will be filled by the Parser with Metadata discovered about      * the file being parsed.      *      * Note: This example will extract content from the outer document and all      * embedded documents.  However, if you choose to use a {@link ParseContext},      * make sure to set a {@link Parser} or else embedded content will not be      * parsed.      *      * @return The content of a file.      */
specifier|public
name|String
name|parseExample
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|InputStream
name|stream
init|=
name|ParsingExample
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test.doc"
argument_list|)
decl_stmt|;
name|AutoDetectParser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|BodyContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
return|return
name|handler
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * If you don't want content from embedded documents, send in      * a {@link org.apache.tika.parser.ParseContext} that does not contain a      * {@link Parser}.      *      * @return The content of a file.      */
specifier|public
name|String
name|parseNoEmbeddedExample
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|InputStream
name|stream
init|=
name|ParsingExample
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|)
decl_stmt|;
name|AutoDetectParser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|BodyContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
block|{
name|parser
operator|.
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
return|return
name|handler
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This example shows how to extract content from the outer document and all      * embedded documents.  The key is to specify a {@link Parser} in the {@link ParseContext}.      *      * @return content, including from embedded documents      * @throws IOException      * @throws SAXException      * @throws TikaException      */
specifier|public
name|String
name|parseEmbeddedExample
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|InputStream
name|stream
init|=
name|ParsingExample
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|)
decl_stmt|;
name|AutoDetectParser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|BodyContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
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
return|return
name|handler
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * For documents that may contain embedded documents, it might be helpful      * to create list of metadata objects, one for the container document and      * one for each embedded document.  This allows easy access to both the      * extracted content and the metadata of each embedded document.      * Note that many document formats can contain embedded documents,      * including traditional container formats -- zip, tar and others -- but also      * common office document formats including: MSWord, MSExcel,      * MSPowerPoint, RTF, PDF, MSG and several others.      *<p>      * The "content" format is determined by the ContentHandlerFactory, and      * the content is stored in {@link org.apache.tika.parser.RecursiveParserWrapper#TIKA_CONTENT}      *<p>      * The drawback to the RecursiveParserWrapper is that it caches metadata and contents      * in memory.  This should not be used on files whose contents are too big to be handled      * in memory.      *      * @return a list of metadata object, one each for the container file and each embedded file      * @throws IOException      * @throws SAXException      * @throws TikaException      */
specifier|public
name|List
argument_list|<
name|Metadata
argument_list|>
name|recursiveParserWrapperExample
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|Parser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|ContentHandlerFactory
name|factory
init|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|HTML
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|p
argument_list|,
name|factory
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|ParsingExample
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test_recursive_embedded.docx"
argument_list|)
expr_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
try|try
block|{
name|wrapper
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|wrapper
operator|.
name|getMetadata
argument_list|()
return|;
block|}
comment|/**      * We include a simple JSON serializer for a list of metadata with      * {@link org.apache.tika.metadata.serialization.JsonMetadataList}.      * That class also includes a deserializer to convert from JSON      * back to a List<Metadata>.      *<p>      * This functionality is also available in tika-app's GUI, and      * with the -J option on tika-app's commandline.  For tika-server      * users, there is the "rmeta" service that will return this format.      *      * @return a JSON representation of a list of Metadata objects      * @throws IOException      * @throws SAXException      * @throws TikaException      */
specifier|public
name|String
name|serializedRecursiveParserWrapperExample
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|List
name|metadataList
init|=
name|recursiveParserWrapperExample
argument_list|()
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JsonMetadataList
operator|.
name|toJson
argument_list|(
name|metadataList
argument_list|,
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *      * @param outputPath -- output directory to place files      * @return list of files created      * @throws IOException      * @throws SAXException      * @throws TikaException      */
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|extractEmbeddedDocumentsExample
parameter_list|(
name|Path
name|outputPath
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|InputStream
name|stream
init|=
name|ParsingExample
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|)
decl_stmt|;
name|ExtractEmbeddedFiles
name|ex
init|=
operator|new
name|ExtractEmbeddedFiles
argument_list|()
decl_stmt|;
name|ex
operator|.
name|extract
argument_list|(
name|stream
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|dirStream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|outputPath
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|entry
range|:
name|dirStream
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

