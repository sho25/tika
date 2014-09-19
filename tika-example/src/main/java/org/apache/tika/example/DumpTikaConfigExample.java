begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|TikaConfig
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
name|detect
operator|.
name|DefaultDetector
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
name|detect
operator|.
name|Detector
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
name|language
operator|.
name|translate
operator|.
name|DefaultTranslator
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
name|language
operator|.
name|translate
operator|.
name|Translator
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
name|CompositeParser
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|DocumentBuilder
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
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
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
name|OutputStreamWriter
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
name|io
operator|.
name|Writer
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * This class shows how to dump a TikaConfig object to a configuration file.  * This allows users to easily dump the default TikaConfig as a base from which  * to start if they want to modify the default configuration file.  *<p>  * For those who want to modify the mimes file, take a look at  * tika-core/src/main/resources/org/apache/tika/mime/tika-mimetypes.xml  * for inspiration.  Consider adding org/apache/tika/mime/custom-mimetypes.xml  * for your custom mime types.  */
end_comment

begin_class
specifier|public
class|class
name|DumpTikaConfigExample
block|{
comment|/**      *      * @param config config file to dump      * @param writer writer to which to write      * @throws Exception      */
specifier|public
name|void
name|dump
parameter_list|(
name|TikaConfig
name|config
parameter_list|,
name|Writer
name|writer
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|Exception
block|{
name|DocumentBuilderFactory
name|docFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|docBuilder
init|=
name|docFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
comment|// root elements
name|Document
name|doc
init|=
name|docBuilder
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|rootElement
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"properties"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|appendChild
argument_list|(
name|rootElement
argument_list|)
expr_stmt|;
name|addMimeComment
argument_list|(
name|rootElement
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|addTranslator
argument_list|(
name|rootElement
argument_list|,
name|doc
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|addDetectors
argument_list|(
name|rootElement
argument_list|,
name|doc
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|addParsers
argument_list|(
name|rootElement
argument_list|,
name|doc
argument_list|,
name|config
argument_list|)
expr_stmt|;
comment|//now write
name|TransformerFactory
name|transformerFactory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Transformer
name|transformer
init|=
name|transformerFactory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|transformer
operator|.
name|setOutputProperty
argument_list|(
literal|"{http://xml.apache.org/xslt}indent-amount"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|transformer
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|StreamResult
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addTranslator
parameter_list|(
name|Element
name|rootElement
parameter_list|,
name|Document
name|doc
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
block|{
comment|//TikaConfig only reads the first translator from the list,
comment|//but it looks like it expects a list
name|Translator
name|translator
init|=
name|config
operator|.
name|getTranslator
argument_list|()
decl_stmt|;
if|if
condition|(
name|translator
operator|instanceof
name|DefaultTranslator
condition|)
block|{
name|Node
name|mimeComment
init|=
name|doc
operator|.
name|createComment
argument_list|(
literal|"for example: "
operator|+
literal|"<translator class=\"org.apache.tika.language.translate.GoogleTranslator\"/>"
argument_list|)
decl_stmt|;
name|rootElement
operator|.
name|appendChild
argument_list|(
name|mimeComment
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Element
name|translatorElement
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"translator"
argument_list|)
decl_stmt|;
name|translatorElement
operator|.
name|setAttribute
argument_list|(
literal|"class"
argument_list|,
name|translator
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|rootElement
operator|.
name|appendChild
argument_list|(
name|translatorElement
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addMimeComment
parameter_list|(
name|Element
name|rootElement
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|Node
name|mimeComment
init|=
name|doc
operator|.
name|createComment
argument_list|(
literal|"for example:<mimeTypeRepository resource=\"/org/apache/tika/mime/tika-mimetypes.xml\"/>"
argument_list|)
decl_stmt|;
name|rootElement
operator|.
name|appendChild
argument_list|(
name|mimeComment
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addDetectors
parameter_list|(
name|Element
name|rootElement
parameter_list|,
name|Document
name|doc
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|Detector
name|detector
init|=
name|config
operator|.
name|getDetector
argument_list|()
decl_stmt|;
name|Element
name|detectorsElement
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"detectors"
argument_list|)
decl_stmt|;
if|if
condition|(
name|detector
operator|instanceof
name|DefaultDetector
condition|)
block|{
name|List
argument_list|<
name|Detector
argument_list|>
name|children
init|=
operator|(
operator|(
name|DefaultDetector
operator|)
name|detector
operator|)
operator|.
name|getDetectors
argument_list|()
decl_stmt|;
for|for
control|(
name|Detector
name|d
range|:
name|children
control|)
block|{
name|Element
name|detectorElement
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"detector"
argument_list|)
decl_stmt|;
name|detectorElement
operator|.
name|setAttribute
argument_list|(
literal|"class"
argument_list|,
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
name|detectorsElement
operator|.
name|appendChild
argument_list|(
name|detectorElement
argument_list|)
expr_stmt|;
block|}
block|}
name|rootElement
operator|.
name|appendChild
argument_list|(
name|detectorsElement
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addParsers
parameter_list|(
name|Element
name|rootElement
parameter_list|,
name|Document
name|doc
parameter_list|,
name|TikaConfig
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
name|parsers
init|=
name|getConcreteParsers
argument_list|(
name|config
operator|.
name|getParser
argument_list|()
argument_list|)
decl_stmt|;
name|Element
name|parsersElement
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"parsers"
argument_list|)
decl_stmt|;
name|rootElement
operator|.
name|appendChild
argument_list|(
name|parsersElement
argument_list|)
expr_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
name|e
range|:
name|parsers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Element
name|parserElement
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"parser"
argument_list|)
decl_stmt|;
name|Parser
name|child
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|className
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|parserElement
operator|.
name|setAttribute
argument_list|(
literal|"class"
argument_list|,
name|className
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|MediaType
argument_list|>
name|types
init|=
operator|new
name|TreeSet
argument_list|<
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
name|types
operator|.
name|addAll
argument_list|(
name|child
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|MediaType
name|type
range|:
name|types
control|)
block|{
name|Element
name|mimeElement
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"mime"
argument_list|)
decl_stmt|;
name|mimeElement
operator|.
name|appendChild
argument_list|(
name|doc
operator|.
name|createTextNode
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|parserElement
operator|.
name|appendChild
argument_list|(
name|mimeElement
argument_list|)
expr_stmt|;
block|}
name|parsersElement
operator|.
name|appendChild
argument_list|(
name|parserElement
argument_list|)
expr_stmt|;
block|}
name|rootElement
operator|.
name|appendChild
argument_list|(
name|parsersElement
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
name|getConcreteParsers
parameter_list|(
name|Parser
name|parentParser
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
name|parsers
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentParser
operator|instanceof
name|CompositeParser
condition|)
block|{
name|addParsers
argument_list|(
operator|(
name|CompositeParser
operator|)
name|parentParser
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addParser
argument_list|(
name|parentParser
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
block|}
return|return
name|parsers
return|;
block|}
specifier|private
name|void
name|addParsers
parameter_list|(
name|CompositeParser
name|p
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
name|parsers
parameter_list|)
block|{
for|for
control|(
name|Parser
name|child
range|:
name|p
operator|.
name|getParsers
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|child
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|instanceof
name|CompositeParser
condition|)
block|{
name|addParsers
argument_list|(
operator|(
name|CompositeParser
operator|)
name|child
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addParser
argument_list|(
name|child
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|addParser
parameter_list|(
name|Parser
name|p
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Parser
argument_list|>
name|parsers
parameter_list|)
block|{
name|parsers
operator|.
name|put
argument_list|(
name|p
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
comment|/**      *      * @param args outputFile, outputEncoding, if args is empty, this prints to console      * @throws Exception      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
name|Writer
name|writer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|encoding
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
block|}
name|DumpTikaConfigExample
name|ex
init|=
operator|new
name|DumpTikaConfigExample
argument_list|()
decl_stmt|;
name|ex
operator|.
name|dump
argument_list|(
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|,
name|writer
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|writer
operator|instanceof
name|StringWriter
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

