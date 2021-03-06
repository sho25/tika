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
name|bundle
package|;
end_package

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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|bundle
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|junitBundles
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|mavenBundle
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|CoreOptions
operator|.
name|options
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|FileInputStream
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
name|net
operator|.
name|URISyntaxException
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
name|Paths
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
name|Set
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
name|Attributes
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
name|JarInputStream
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
name|Manifest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
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
name|fork
operator|.
name|ForkParser
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
name|DefaultParser
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
name|internal
operator|.
name|Activator
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
name|ocr
operator|.
name|TesseractOCRParser
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|junit
operator|.
name|PaxExam
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|spi
operator|.
name|reactors
operator|.
name|ExamReactorStrategy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|ops4j
operator|.
name|pax
operator|.
name|exam
operator|.
name|spi
operator|.
name|reactors
operator|.
name|PerMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|Bundle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceReference
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

begin_class
annotation|@
name|RunWith
argument_list|(
name|PaxExam
operator|.
name|class
argument_list|)
annotation|@
name|ExamReactorStrategy
argument_list|(
name|PerMethod
operator|.
name|class
argument_list|)
specifier|public
class|class
name|BundleIT
block|{
specifier|private
specifier|final
name|File
name|TARGET
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
annotation|@
name|Inject
specifier|private
name|Parser
name|defaultParser
decl_stmt|;
annotation|@
name|Inject
specifier|private
name|Detector
name|contentTypeDetector
decl_stmt|;
annotation|@
name|Inject
specifier|private
name|BundleContext
name|bc
decl_stmt|;
annotation|@
name|Configuration
specifier|public
name|Option
index|[]
name|configuration
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|ClassNotFoundException
block|{
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|TARGET
argument_list|,
literal|"test-bundles"
argument_list|)
decl_stmt|;
return|return
name|options
argument_list|(
name|bundle
argument_list|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"tika-core.jar"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|mavenBundle
argument_list|(
literal|"org.ops4j.pax.logging"
argument_list|,
literal|"pax-logging-api"
argument_list|,
literal|"1.8.5"
argument_list|)
argument_list|,
name|mavenBundle
argument_list|(
literal|"org.ops4j.pax.logging"
argument_list|,
literal|"pax-logging-service"
argument_list|,
literal|"1.8.5"
argument_list|)
argument_list|,
name|junitBundles
argument_list|()
argument_list|,
name|bundle
argument_list|(
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"tika-bundle.jar"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBundleLoaded
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|hasCore
init|=
literal|false
decl_stmt|,
name|hasBundle
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Bundle
name|b
range|:
name|bc
operator|.
name|getBundles
argument_list|()
control|)
block|{
if|if
condition|(
literal|"org.apache.tika.core"
operator|.
name|equals
argument_list|(
name|b
operator|.
name|getSymbolicName
argument_list|()
argument_list|)
condition|)
block|{
name|hasCore
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Core not activated"
argument_list|,
name|Bundle
operator|.
name|ACTIVE
argument_list|,
name|b
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"org.apache.tika.bundle"
operator|.
name|equals
argument_list|(
name|b
operator|.
name|getSymbolicName
argument_list|()
argument_list|)
condition|)
block|{
name|hasBundle
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bundle not activated"
argument_list|,
name|Bundle
operator|.
name|ACTIVE
argument_list|,
name|b
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Core bundle not found"
argument_list|,
name|hasCore
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Bundle bundle not found"
argument_list|,
name|hasBundle
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testManifestNoJUnit
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|TARGET
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|TARGET
argument_list|,
literal|"test-bundles"
argument_list|)
decl_stmt|;
name|File
name|tikaBundle
init|=
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"tika-bundle.jar"
argument_list|)
decl_stmt|;
name|JarInputStream
name|jarIs
init|=
operator|new
name|JarInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tikaBundle
argument_list|)
argument_list|)
decl_stmt|;
name|Manifest
name|mf
init|=
name|jarIs
operator|.
name|getManifest
argument_list|()
decl_stmt|;
name|Attributes
name|main
init|=
name|mf
operator|.
name|getMainAttributes
argument_list|()
decl_stmt|;
name|String
name|importPackage
init|=
name|main
operator|.
name|getValue
argument_list|(
literal|"Import-Package"
argument_list|)
decl_stmt|;
name|boolean
name|containsJunit
init|=
name|importPackage
operator|.
name|contains
argument_list|(
literal|"junit"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"The bundle should not import junit"
argument_list|,
name|containsJunit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBundleDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|metadataTXT
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadataTXT
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test.txt"
argument_list|)
expr_stmt|;
name|Metadata
name|metadataPDF
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadataPDF
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test.pdf"
argument_list|)
expr_stmt|;
comment|// Simple type detection
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|,
name|contentTypeDetector
operator|.
name|detect
argument_list|(
literal|null
argument_list|,
name|metadataTXT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"pdf"
argument_list|)
argument_list|,
name|contentTypeDetector
operator|.
name|detect
argument_list|(
literal|null
argument_list|,
name|metadataPDF
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testForkParser
parameter_list|()
throws|throws
name|Exception
block|{
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|Activator
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|defaultParser
argument_list|)
decl_stmt|;
name|String
name|data
init|=
literal|"<!DOCTYPE html>\n<html><body><p>test<span>content</span></p></body></html>"
decl_stmt|;
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|ContentHandler
name|contentHandler
init|=
operator|new
name|BodyContentHandler
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|MediaType
name|type
init|=
name|contentTypeDetector
operator|.
name|detect
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|,
literal|"text/html"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParseContext
name|parseCtx
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|contentHandler
argument_list|,
name|metadata
argument_list|,
name|parseCtx
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|content
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test content"
argument_list|,
name|content
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBundleSimpleText
parameter_list|()
throws|throws
name|Exception
block|{
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
comment|// Simple text extraction
name|String
name|xml
init|=
name|tika
operator|.
name|parseToString
argument_list|(
operator|new
name|File
argument_list|(
literal|"pom.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|xml
operator|.
name|contains
argument_list|(
literal|"tika-bundle"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBundleDetectors
parameter_list|()
throws|throws
name|Exception
block|{
comment|//For some reason, the detector created by OSGi has a flat
comment|//list of detectors, whereas the detector created by the traditional
comment|//service loading method has children: DefaultDetector, MimeTypes.
comment|//We have to flatten the service loaded DefaultDetector to get equivalence.
comment|//Detection behavior should all be the same.
comment|// Get the classes found within OSGi
name|ServiceReference
argument_list|<
name|Detector
argument_list|>
name|detectorRef
init|=
name|bc
operator|.
name|getServiceReference
argument_list|(
name|Detector
operator|.
name|class
argument_list|)
decl_stmt|;
name|DefaultDetector
name|detectorService
init|=
operator|(
name|DefaultDetector
operator|)
name|bc
operator|.
name|getService
argument_list|(
name|detectorRef
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|osgiDetectors
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Detector
name|d
range|:
name|detectorService
operator|.
name|getDetectors
argument_list|()
control|)
block|{
name|osgiDetectors
operator|.
name|add
argument_list|(
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check we did get a few, just in case...
name|assertTrue
argument_list|(
literal|"Should have several Detector names, found "
operator|+
name|osgiDetectors
operator|.
name|size
argument_list|()
argument_list|,
name|osgiDetectors
operator|.
name|size
argument_list|()
operator|>
literal|3
argument_list|)
expr_stmt|;
comment|// Get the raw detectors list from the traditional service loading mechanism
name|DefaultDetector
name|detector
init|=
operator|new
name|DefaultDetector
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rawDetectors
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Detector
name|d
range|:
name|detector
operator|.
name|getDetectors
argument_list|()
control|)
block|{
if|if
condition|(
name|d
operator|instanceof
name|DefaultDetector
condition|)
block|{
for|for
control|(
name|Detector
name|dChild
range|:
operator|(
operator|(
name|DefaultDetector
operator|)
name|d
operator|)
operator|.
name|getDetectors
argument_list|()
control|)
block|{
name|rawDetectors
operator|.
name|add
argument_list|(
name|dChild
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|rawDetectors
operator|.
name|add
argument_list|(
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|osgiDetectors
argument_list|,
name|rawDetectors
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBundleParsers
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Get the classes found within OSGi
name|ServiceReference
argument_list|<
name|Parser
argument_list|>
name|parserRef
init|=
name|bc
operator|.
name|getServiceReference
argument_list|(
name|Parser
operator|.
name|class
argument_list|)
decl_stmt|;
name|DefaultParser
name|parserService
init|=
operator|(
name|DefaultParser
operator|)
name|bc
operator|.
name|getService
argument_list|(
name|parserRef
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|osgiParsers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Parser
name|p
range|:
name|parserService
operator|.
name|getAllComponentParsers
argument_list|()
control|)
block|{
name|osgiParsers
operator|.
name|add
argument_list|(
name|p
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check we did get a few, just in case...
name|assertTrue
argument_list|(
literal|"Should have lots Parser names, found "
operator|+
name|osgiParsers
operator|.
name|size
argument_list|()
argument_list|,
name|osgiParsers
operator|.
name|size
argument_list|()
operator|>
literal|15
argument_list|)
expr_stmt|;
comment|// Get the raw parsers list from the traditional service loading mechanism
name|CompositeParser
name|parser
init|=
operator|(
name|CompositeParser
operator|)
name|defaultParser
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|rawParsers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Parser
name|p
range|:
name|parser
operator|.
name|getAllComponentParsers
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|DefaultParser
condition|)
block|{
for|for
control|(
name|Parser
name|pChild
range|:
operator|(
operator|(
name|DefaultParser
operator|)
name|p
operator|)
operator|.
name|getAllComponentParsers
argument_list|()
control|)
block|{
name|rawParsers
operator|.
name|add
argument_list|(
name|pChild
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|rawParsers
operator|.
name|add
argument_list|(
name|p
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|rawParsers
argument_list|,
name|osgiParsers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTesseractParser
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|Parser
name|tesseractParser
init|=
operator|new
name|TesseractOCRParser
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|stream
init|=
operator|new
name|FileInputStream
argument_list|(
literal|"src/test/resources/testOCR.jpg"
argument_list|)
init|)
block|{
name|tesseractParser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
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
annotation|@
name|Test
specifier|public
name|void
name|testTikaBundle
parameter_list|()
throws|throws
name|Exception
block|{
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
comment|// Package extraction
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Parser
name|parser
init|=
name|tika
operator|.
name|getParser
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
init|(
name|InputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"src/test/resources/test-documents.zip"
argument_list|)
argument_list|)
init|)
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testEXCEL.xls"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Sample Excel Worksheet"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testHTML.html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test Indexation Html"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testOpenOffice2.odt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"This is a sample Open Office document"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testPDF.pdf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Apache Tika"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testPPT.ppt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Sample Powerpoint Slide"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testRTF.rtf"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"indexation Word"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testTXT.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Test d'indexation de Txt"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testWORD.doc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"This is a sample Microsoft Word Document"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"testXML.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Rida Benjelloun"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

