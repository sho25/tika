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
name|mime
package|;
end_package

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
name|assertTrue
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
name|Charset
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
name|ServiceLoader
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
name|detect
operator|.
name|CompositeDetector
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
name|DefaultProbDetector
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
name|ProbabilisticMimeDetectionSelector
operator|.
name|Builder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
specifier|public
class|class
name|ProbabilisticMimeDetectionTestWithTika
block|{
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|// private ProbabilisticMimeDetectionSelector proDetector;
specifier|private
name|Tika
name|tika
decl_stmt|;
specifier|private
name|MediaTypeRegistry
name|registry
decl_stmt|;
comment|/** @inheritDoc */
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|registry
operator|=
name|MimeTypes
operator|.
name|getDefaultMimeTypes
argument_list|()
operator|.
name|getMediaTypeRegistry
argument_list|()
expr_stmt|;
name|tika
operator|=
operator|new
name|Tika
argument_list|(
operator|new
name|TikaConfig
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|CompositeDetector
name|getDefaultDetector
parameter_list|(
name|MimeTypes
name|types
parameter_list|,
name|ServiceLoader
name|loader
parameter_list|)
block|{
comment|/*                      * here is an example with the use of the builder to                      * instantiate the object.                      */
name|Builder
name|builder
init|=
operator|new
name|ProbabilisticMimeDetectionSelector
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|ProbabilisticMimeDetectionSelector
name|proDetector
init|=
operator|new
name|ProbabilisticMimeDetectionSelector
argument_list|(
name|types
argument_list|,
name|builder
operator|.
name|priorMagicFileType
argument_list|(
literal|0.5f
argument_list|)
operator|.
name|priorExtensionFileType
argument_list|(
literal|0.5f
argument_list|)
operator|.
name|priorMetaFileType
argument_list|(
literal|0.5f
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DefaultProbDetector
argument_list|(
name|proDetector
argument_list|,
name|loader
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{          }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|testFile
argument_list|(
literal|"image/svg+xml"
argument_list|,
literal|"circles.svg"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"image/svg+xml"
argument_list|,
literal|"circles-with-prefix.svg"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"image/png"
argument_list|,
literal|"datamatrix.png"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"text/html"
argument_list|,
literal|"test.html"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"application/xml"
argument_list|,
literal|"test-iso-8859-1.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"application/xml"
argument_list|,
literal|"test-utf8.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"application/xml"
argument_list|,
literal|"test-utf8-bom.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"application/xml"
argument_list|,
literal|"test-utf16le.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"application/xml"
argument_list|,
literal|"test-utf16be.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"application/xml"
argument_list|,
literal|"test-long-comment.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
literal|"application/xslt+xml"
argument_list|,
literal|"stylesheet.xsl"
argument_list|)
expr_stmt|;
name|testUrl
argument_list|(
literal|"application/rdf+xml"
argument_list|,
literal|"http://www.ai.sri.com/daml/services/owl-s/1.2/Process.owl"
argument_list|,
literal|"test-difficult-rdf1.xml"
argument_list|)
expr_stmt|;
name|testUrl
argument_list|(
literal|"application/rdf+xml"
argument_list|,
literal|"http://www.w3.org/2002/07/owl#"
argument_list|,
literal|"test-difficult-rdf2.xml"
argument_list|)
expr_stmt|;
comment|// add evil test from TIKA-327
name|testFile
argument_list|(
literal|"text/html"
argument_list|,
literal|"test-tika-327.html"
argument_list|)
expr_stmt|;
comment|// add another evil html test from TIKA-357
name|testFile
argument_list|(
literal|"text/html"
argument_list|,
literal|"testlargerbuffer.html"
argument_list|)
expr_stmt|;
comment|// test fragment of HTML with<div> (TIKA-1102)
name|testFile
argument_list|(
literal|"text/html"
argument_list|,
literal|"htmlfragment"
argument_list|)
expr_stmt|;
comment|// test binary CGM detection (TIKA-1170)
name|testFile
argument_list|(
literal|"image/cgm"
argument_list|,
literal|"plotutils-bin-cgm-v3.cgm"
argument_list|)
expr_stmt|;
comment|// test HTML detection of malformed file, previously identified as
comment|// image/cgm (TIKA-1170)
name|testFile
argument_list|(
literal|"text/html"
argument_list|,
literal|"test-malformed-header.html.bin"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testByteOrderMark
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"\ufefftest"
operator|.
name|getBytes
argument_list|(
literal|"UTF-16LE"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"\ufefftest"
operator|.
name|getBytes
argument_list|(
literal|"UTF-16BE"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"\ufefftest"
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSuperTypes
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"text/something; charset=UTF-8"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|parse
argument_list|(
literal|"text/something"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"text/something; charset=UTF-8"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"text/something; charset=UTF-8"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"text/something"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"application/something+xml"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"application/something+zip"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|APPLICATION_ZIP
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|,
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"application/vnd.apple.iwork"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|APPLICATION_ZIP
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|void
name|testUrlOnly
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|testStream
argument_list|(
name|expected
argument_list|,
name|url
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testUrl
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|testStream
argument_list|(
name|expected
argument_list|,
name|url
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testFile
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|testStream
argument_list|(
name|expected
argument_list|,
name|filename
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testStream
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|urlOrFileName
parameter_list|,
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
literal|"Test stream: ["
operator|+
name|urlOrFileName
operator|+
literal|"] is null!"
argument_list|,
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|in
operator|.
name|markSupported
argument_list|()
condition|)
block|{
name|in
operator|=
operator|new
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|// String mime = this.proDetector.detect(in, metadata).toString();
name|String
name|mime
init|=
name|tika
operator|.
name|detect
argument_list|(
name|in
argument_list|,
name|metadata
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|urlOrFileName
operator|+
literal|" is not properly detected: detected."
argument_list|,
name|expected
argument_list|,
name|mime
argument_list|)
expr_stmt|;
comment|// Add resource name and test again
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|urlOrFileName
argument_list|)
expr_stmt|;
comment|// mime = this.proDetector.detect(in, metadata).toString();
name|mime
operator|=
name|tika
operator|.
name|detect
argument_list|(
name|in
argument_list|,
name|metadata
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|urlOrFileName
operator|+
literal|" is not properly detected after adding resource name."
argument_list|,
name|expected
argument_list|,
name|mime
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertNotNull
parameter_list|(
name|String
name|string
parameter_list|,
name|InputStream
name|in
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/**      * Test for type detection of empty documents.      *       * @see<a      *      href="https://issues.apache.org/jira/browse/TIKA-483">TIKA-483</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testEmptyDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Metadata
name|namehint
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|namehint
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test.txt"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|namehint
argument_list|)
argument_list|)
expr_stmt|;
name|Metadata
name|typehint
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|typehint
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|typehint
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for things like javascript files whose content is enclosed in XML      * comment delimiters, but that aren't actually XML.      *       * @see<a      *      href="https://issues.apache.org/jira/browse/TIKA-426">TIKA-426</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testNotXML
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"<!-- test -->"
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that when we repeatedly test the detection of a document that can      * be detected with Mime Magic, that we consistently detect it correctly.      * See TIKA-391 for more details.      */
annotation|@
name|Test
specifier|public
name|void
name|testMimeMagicStability
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|testFile
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
literal|"test.xls"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests that when two magic matches both apply, and both have the same      * priority, we use the name to pick the right one based on the glob, or the      * first one we come across if not. See TIKA-1292 for more details.      */
annotation|@
name|Test
specifier|public
name|void
name|testMimeMagicClashSamePriority
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|helloWorld
init|=
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
decl_stmt|;
name|MediaType
name|helloType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
literal|"hello/world-file"
argument_list|)
decl_stmt|;
name|MediaType
name|helloXType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
literal|"hello/x-world-hello"
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
decl_stmt|;
comment|// With a filename, picks the right one
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test.hello.world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|helloType
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|helloWorld
argument_list|)
argument_list|,
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"test.x-hello-world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|helloXType
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|helloWorld
argument_list|)
argument_list|,
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
comment|// Without, goes for the one that sorts last
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
literal|"testingTESTINGtesting"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|helloXType
operator|.
name|toString
argument_list|()
argument_list|,
name|tika
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|helloWorld
argument_list|)
argument_list|,
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

