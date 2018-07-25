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
name|fork
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
name|TikaTest
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
name|parser
operator|.
name|AutoDetectParserFactory
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
name|ToXMLContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|File
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|ArrayList
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
name|HashMap
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
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarOutputStream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|ForkParserTikaBinTest
extends|extends
name|TikaTest
block|{
specifier|private
specifier|static
name|Path
name|JAR_DIR
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JAR_FILE_NAME
init|=
literal|"mock-tika-app.jar"
decl_stmt|;
specifier|private
specifier|static
name|Path
name|JAR_FILE
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|EMPTY_MAP
init|=
name|Collections
operator|.
name|EMPTY_MAP
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|bootstrapJar
parameter_list|()
throws|throws
name|Exception
block|{
name|JAR_DIR
operator|=
name|Files
operator|.
name|createTempDirectory
argument_list|(
literal|"tika-fork-tikabin-"
argument_list|)
expr_stmt|;
name|JAR_FILE
operator|=
name|JAR_DIR
operator|.
name|resolve
argument_list|(
name|JAR_FILE_NAME
argument_list|)
expr_stmt|;
try|try
init|(
name|JarOutputStream
name|jarOs
init|=
operator|new
name|JarOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|JAR_FILE
argument_list|)
argument_list|)
init|)
block|{
name|ClassLoader
name|loader
init|=
name|ForkServer
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|klass
range|:
name|getClasses
argument_list|(
literal|"org.apache.tika"
argument_list|)
control|)
block|{
name|String
name|path
init|=
name|klass
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|".class"
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
init|)
block|{
name|jarOs
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|jarOs
argument_list|)
expr_stmt|;
block|}
block|}
try|try
init|(
name|InputStream
name|input
init|=
name|ForkParserTikaBinTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/config/TIKA-2653-vowel-parser-ae.xml"
argument_list|)
init|)
block|{
name|jarOs
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"org/apache/tika/parser/TIKA-2653-vowel-parser-ae.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|jarOs
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|InputStream
name|input
init|=
name|ForkParserTikaBinTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/mime/tika-mimetypes.xml"
argument_list|)
init|)
block|{
name|jarOs
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"org/apache/tika/mime/tika-mimetypes.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|jarOs
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|InputStream
name|input
init|=
name|ForkParserTikaBinTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/mime/custom-mimetypes.xml"
argument_list|)
init|)
block|{
name|jarOs
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"org/apache/tika/mime/custom-mimetypes.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|input
argument_list|,
name|jarOs
argument_list|)
expr_stmt|;
block|}
name|jarOs
operator|.
name|putNextEntry
argument_list|(
operator|new
name|JarEntry
argument_list|(
literal|"META-INF/services/org.apache.tika.parser.Parser"
argument_list|)
argument_list|)
expr_stmt|;
name|jarOs
operator|.
name|write
argument_list|(
literal|"org.apache.tika.parser.mock.VowelParser\n"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Path
name|tikaConfigVowelParser
init|=
name|JAR_DIR
operator|.
name|resolve
argument_list|(
literal|"TIKA_2653-iou.xml"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|ForkServer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/config/TIKA-2653-vowel-parser-iou.xml"
argument_list|)
init|;
name|OutputStream
name|os
operator|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|tikaConfigVowelParser
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Files
operator|.
name|delete
argument_list|(
name|JAR_DIR
operator|.
name|resolve
argument_list|(
literal|"TIKA_2653-iou.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|JAR_FILE
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|JAR_DIR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExplicitParserFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|xmlResult
init|=
name|getXML
argument_list|(
operator|new
name|ParserFactoryFactory
argument_list|(
literal|"org.apache.tika.parser.mock.MockParserFactory"
argument_list|,
name|EMPTY_MAP
argument_list|)
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"hello world!"
argument_list|,
name|xmlResult
operator|.
name|xml
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nikolai Lobachevsky"
argument_list|,
name|xmlResult
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVowelParserAsDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|ParserFactoryFactory
name|pff
init|=
operator|new
name|ParserFactoryFactory
argument_list|(
literal|"org.apache.tika.parser.AutoDetectParserFactory"
argument_list|,
name|EMPTY_MAP
argument_list|)
decl_stmt|;
name|XMLResult
name|xmlResult
init|=
name|getXML
argument_list|(
name|pff
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"eooeuiooueoeeao"
argument_list|,
name|xmlResult
operator|.
name|xml
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nikolai Lobachevsky"
argument_list|,
name|xmlResult
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVowelParserInClassPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|AutoDetectParserFactory
operator|.
name|TIKA_CONFIG_PATH
argument_list|,
literal|"TIKA-2653-vowel-parser-ae.xml"
argument_list|)
expr_stmt|;
name|ParserFactoryFactory
name|pff
init|=
operator|new
name|ParserFactoryFactory
argument_list|(
literal|"org.apache.tika.parser.AutoDetectParserFactory"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|XMLResult
name|xmlResult
init|=
name|getXML
argument_list|(
name|pff
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"eeeeea"
argument_list|,
name|xmlResult
operator|.
name|xml
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nikolai Lobachevsky"
argument_list|,
name|xmlResult
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVowelParserFromDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|AutoDetectParserFactory
operator|.
name|TIKA_CONFIG_PATH
argument_list|,
name|JAR_DIR
operator|.
name|resolve
argument_list|(
literal|"TIKA_2653-iou.xml"
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParserFactoryFactory
name|pff
init|=
operator|new
name|ParserFactoryFactory
argument_list|(
literal|"org.apache.tika.parser.AutoDetectParserFactory"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|XMLResult
name|xmlResult
init|=
name|getXML
argument_list|(
name|pff
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"oouioouoo"
argument_list|,
name|xmlResult
operator|.
name|xml
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nikolai Lobachevsky"
argument_list|,
name|xmlResult
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPFFWithClassLoaderFromParentProcess
parameter_list|()
throws|throws
name|Exception
block|{
comment|//The UpperCasingContentHandler is not sent to the bootstrap test jar file in @BeforeClass.
comment|//this tests that the content handler was loaded from the parent process.
name|ParserFactoryFactory
name|pff
init|=
operator|new
name|ParserFactoryFactory
argument_list|(
literal|"org.apache.tika.parser.AutoDetectParserFactory"
argument_list|,
name|EMPTY_MAP
argument_list|)
decl_stmt|;
name|XMLResult
name|xmlResult
init|=
name|getXML
argument_list|(
name|pff
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|UpperCasingContentHandler
argument_list|()
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"EOOEUIOOUEOEEAO"
argument_list|,
name|xmlResult
operator|.
name|xml
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nikolai Lobachevsky"
argument_list|,
name|xmlResult
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|XMLResult
name|getXML
parameter_list|(
name|ParserFactoryFactory
name|pff
parameter_list|)
throws|throws
name|TikaException
throws|,
name|SAXException
throws|,
name|IOException
block|{
return|return
name|getXML
argument_list|(
name|pff
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|XMLResult
name|getXML
parameter_list|(
name|ParserFactoryFactory
name|pff
parameter_list|,
name|ClassLoader
name|classloader
parameter_list|,
name|ContentHandler
name|contentHandler
parameter_list|)
throws|throws
name|TikaException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|java
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|java
operator|.
name|add
argument_list|(
literal|"java"
argument_list|)
expr_stmt|;
name|ForkParser
name|parser
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|classloader
operator|!=
literal|null
condition|)
block|{
name|parser
operator|=
operator|new
name|ForkParser
argument_list|(
name|JAR_DIR
argument_list|,
name|pff
argument_list|,
name|classloader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|=
operator|new
name|ForkParser
argument_list|(
name|JAR_DIR
argument_list|,
name|pff
argument_list|)
expr_stmt|;
block|}
name|parser
operator|.
name|setJavaCommand
argument_list|(
name|java
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setServerPulseMillis
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|ContentHandler
name|handler
init|=
operator|(
name|contentHandler
operator|==
literal|null
operator|)
condition|?
operator|new
name|ToXMLContentHandler
argument_list|()
else|:
name|contentHandler
decl_stmt|;
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/example.xml"
argument_list|)
init|)
block|{
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|m
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|XMLResult
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
argument_list|,
name|m
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|Class
argument_list|>
name|getClasses
parameter_list|(
name|String
name|packageName
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|IOException
block|{
name|ClassLoader
name|classLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|packageName
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|resources
init|=
name|classLoader
operator|.
name|getResources
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|dirs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|resources
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|URL
name|resource
init|=
name|resources
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|dirs
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|resource
operator|.
name|getFile
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"%20"
argument_list|,
literal|" "
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|Class
argument_list|>
name|classes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|directory
range|:
name|dirs
control|)
block|{
name|classes
operator|.
name|addAll
argument_list|(
name|findClasses
argument_list|(
name|directory
argument_list|,
name|packageName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|classes
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|Class
argument_list|>
name|findClasses
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|packageName
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
name|List
argument_list|<
name|Class
argument_list|>
name|classes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|classes
return|;
block|}
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|classes
operator|.
name|addAll
argument_list|(
name|findClasses
argument_list|(
name|file
argument_list|,
name|packageName
operator|+
literal|"."
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".class"
argument_list|)
condition|)
block|{
comment|//exclude TypeDetectionBenchmark because it is not serializable
comment|//exclude UpperCasingContentHandler because we want to test that
comment|//we can serialize it from the parent process into the child process
if|if
condition|(
operator|!
name|file
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"TypeDetectionBenchmark"
argument_list|)
operator|&&
operator|!
name|file
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"UpperCasingContentHandler"
argument_list|)
condition|)
block|{
name|classes
operator|.
name|add
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|packageName
operator|+
literal|'.'
operator|+
name|file
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|file
operator|.
name|getName
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|6
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|classes
return|;
block|}
block|}
end_class

end_unit
