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
name|ner
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
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
name|ner
operator|.
name|opennlp
operator|.
name|OpenNLPNERecogniser
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
name|ner
operator|.
name|regex
operator|.
name|RegexNERecogniser
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|HashSet
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

begin_comment
comment|/**  *  * This implementation of {@link org.apache.tika.parser.Parser} extracts  * entity names from text content and adds it to the metadata.  *<p>All the metadata keys will have a common suffix {@value #MD_KEY_PREFIX}</p>  *<p>The Named Entity recogniser implementation can be changed by setting the  * system property {@value #SYS_PROP_NER_IMPL} value to a name of class that  * implements {@link NERecogniser} contract</p>  * @see OpenNLPNERecogniser  * @see NERecogniser  *  */
end_comment

begin_class
specifier|public
class|class
name|NamedEntityParser
extends|extends
name|AbstractParser
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NamedEntityParser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|MEDIA_TYPES
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MD_KEY_PREFIX
init|=
literal|"NER_"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NER_IMPL
init|=
name|OpenNLPNERecogniser
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|","
operator|+
name|RegexNERecogniser
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SYS_PROP_NER_IMPL
init|=
literal|"ner.impl.class"
decl_stmt|;
specifier|public
name|Tika
name|secondaryParser
decl_stmt|;
static|static
block|{
name|MEDIA_TYPES
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|NERecogniser
argument_list|>
name|nerChain
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
specifier|private
specifier|volatile
name|boolean
name|available
init|=
literal|false
decl_stmt|;
specifier|private
specifier|synchronized
name|void
name|initialize
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|initialized
condition|)
block|{
return|return;
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
comment|//TODO: read class name from context or config
comment|//There can be multiple classes in the form of comma separated class names;
name|String
name|classNamesString
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|SYS_PROP_NER_IMPL
argument_list|,
name|DEFAULT_NER_IMPL
argument_list|)
decl_stmt|;
name|String
index|[]
name|classNames
init|=
name|classNamesString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|this
operator|.
name|nerChain
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|classNames
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|className
range|:
name|classNames
control|)
block|{
name|className
operator|=
name|className
operator|.
name|trim
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"going to load, instantiate and bind the instance of {}"
argument_list|,
name|className
argument_list|)
expr_stmt|;
try|try
block|{
name|NERecogniser
name|recogniser
init|=
operator|(
name|NERecogniser
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{} is available ? {}"
argument_list|,
name|className
argument_list|,
name|recogniser
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|recogniser
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|nerChain
operator|.
name|add
argument_list|(
name|recogniser
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|TikaConfig
name|config
init|=
operator|new
name|TikaConfig
argument_list|()
decl_stmt|;
name|this
operator|.
name|secondaryParser
operator|=
operator|new
name|Tika
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|available
operator|=
operator|!
name|nerChain
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of NERecognisers in chain {}"
argument_list|,
name|nerChain
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|this
operator|.
name|available
operator|=
literal|false
expr_stmt|;
block|}
block|}
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|parseContext
parameter_list|)
block|{
return|return
name|MEDIA_TYPES
return|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|inputStream
parameter_list|,
name|ContentHandler
name|contentHandler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|initialize
argument_list|(
name|parseContext
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|available
condition|)
block|{
return|return;
block|}
name|Reader
name|reader
init|=
name|MediaType
operator|.
name|TEXT_PLAIN
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
condition|?
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
else|:
name|secondaryParser
operator|.
name|parse
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
for|for
control|(
name|NERecogniser
name|ner
range|:
name|nerChain
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|names
init|=
name|ner
operator|.
name|recognise
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|names
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|mdKey
init|=
name|MD_KEY_PREFIX
operator|+
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|mdKey
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|contentHandler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|extractOutput
argument_list|(
name|text
operator|.
name|trim
argument_list|()
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
comment|/**      * writes the content to the given XHTML      * content handler      *      * @param content      *          the content which needs to be written      * @param xhtml      *          XHTML content handler      * @throws SAXException      *           if the XHTML SAX events could not be handled      *      */
specifier|private
name|void
name|extractOutput
parameter_list|(
name|String
name|content
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
block|{
name|xhtml
operator|.
name|startDocument
argument_list|()
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
name|characters
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
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

