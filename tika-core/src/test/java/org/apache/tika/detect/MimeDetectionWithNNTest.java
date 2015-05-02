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
name|detect
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
name|mime
operator|.
name|MimeDetectionTest
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
name|MimeDetectionWithNNTest
block|{
specifier|private
name|Detector
name|detector
decl_stmt|;
comment|/** @inheritDoc */
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|detector
operator|=
operator|new
name|NNExampleModelDetector
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * The test case only works on the detector that only has grb model as 	 * currently the grb model is used as an example; if more models are added 	 * in the TrainedModelDetector, the following tests will need to modified to reflect 	 * the corresponding type instead of test-equal with the "OCTET_STREAM"; 	 *  	 * @throws Exception 	 */
annotation|@
name|Test
specifier|public
name|void
name|testDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|octetStream_str
init|=
name|MediaType
operator|.
name|OCTET_STREAM
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|grb_str
init|=
literal|"application/x-grib"
decl_stmt|;
name|testFile
argument_list|(
name|grb_str
argument_list|,
literal|"gdas1.forecmwf.2014062612.grib2"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|grb_str
argument_list|,
literal|"GLDAS_CLM10SUBP_3H.A19790202.0000.001.grb"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"circles.svg"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"circles-with-prefix.svg"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"datamatrix.png"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test.html"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test-iso-8859-1.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test-utf8.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test-utf8-bom.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test-utf16le.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test-utf16be.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test-long-comment.xml"
argument_list|)
expr_stmt|;
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"stylesheet.xsl"
argument_list|)
expr_stmt|;
name|testUrl
argument_list|(
name|octetStream_str
argument_list|,
literal|"http://www.ai.sri.com/daml/services/owl-s/1.2/Process.owl"
argument_list|,
literal|"test-difficult-rdf1.xml"
argument_list|)
expr_stmt|;
name|testUrl
argument_list|(
name|octetStream_str
argument_list|,
literal|"http://www.w3.org/2002/07/owl#"
argument_list|,
literal|"test-difficult-rdf2.xml"
argument_list|)
expr_stmt|;
comment|// add evil test from TIKA-327
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test-tika-327.html"
argument_list|)
expr_stmt|;
comment|// add another evil html test from TIKA-357
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"testlargerbuffer.html"
argument_list|)
expr_stmt|;
comment|// test fragment of HTML with<div> (TIKA-1102)
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"htmlfragment"
argument_list|)
expr_stmt|;
comment|// test binary CGM detection (TIKA-1170)
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"plotutils-bin-cgm-v3.cgm"
argument_list|)
expr_stmt|;
comment|// test HTML detection of malformed file, previously identified as
comment|// image/cgm (TIKA-1170)
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"test-malformed-header.html.bin"
argument_list|)
expr_stmt|;
comment|// test GCMD Directory Interchange Format (.dif) TIKA-1561
name|testFile
argument_list|(
name|octetStream_str
argument_list|,
literal|"brwNIMS_2014.dif"
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
name|MimeDetectionTest
operator|.
name|class
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
name|MimeDetectionTest
operator|.
name|class
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
name|String
name|mime
init|=
name|this
operator|.
name|detector
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
comment|// metadata.set(Metadata.RESOURCE_NAME_KEY, urlOrFileName);
name|mime
operator|=
name|this
operator|.
name|detector
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
comment|/** 	 * Test for type detection of empty documents. 	 */
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
argument_list|,
name|detector
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
block|}
block|}
end_class

end_unit

