begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|// Junit imports
end_comment

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
name|net
operator|.
name|MalformedURLException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_comment
comment|/**  *   * Test Suite for the {@link MimeTypes} repository.  *   */
end_comment

begin_class
specifier|public
class|class
name|TestMimeTypes
extends|extends
name|TestCase
block|{
specifier|private
name|MimeTypes
name|repo
decl_stmt|;
specifier|private
specifier|static
name|URL
name|u
decl_stmt|;
static|static
block|{
try|try
block|{
name|u
operator|=
operator|new
name|URL
argument_list|(
literal|"http://mydomain.com/x.pdf?x=y"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"/a/b/c/x.pdf"
argument_list|)
decl_stmt|;
specifier|public
name|TestMimeTypes
parameter_list|()
block|{
try|try
block|{
name|repo
operator|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
operator|.
name|getMimeRepository
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testCaseSensitivity
parameter_list|()
block|{
name|MimeType
name|type
init|=
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.PDF"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.pdf"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.PdF"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repo
operator|.
name|getMimeType
argument_list|(
literal|"test.pdF"
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLoadMimeTypes
parameter_list|()
throws|throws
name|MimeTypeException
block|{
name|assertNotNull
argument_list|(
name|repo
operator|.
name|forName
argument_list|(
literal|"application/octet-stream"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|repo
operator|.
name|forName
argument_list|(
literal|"text/x-tex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests MIME type determination based solely on the URL's extension.      */
specifier|public
name|void
name|testGuessMimeTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTypeByName
argument_list|(
literal|"application/pdf"
argument_list|,
literal|"x.pdf"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
name|u
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|repo
operator|.
name|getMimeType
argument_list|(
name|f
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"text/plain"
argument_list|,
literal|"x.txt"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"text/html"
argument_list|,
literal|"x.htm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"text/html"
argument_list|,
literal|"x.html"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/xhtml+xml"
argument_list|,
literal|"x.xhtml"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/xml"
argument_list|,
literal|"x.xml"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/zip"
argument_list|,
literal|"x.zip"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
argument_list|,
literal|"x.odt"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/octet-stream"
argument_list|,
literal|"x.unknown"
argument_list|)
expr_stmt|;
comment|// Test for the MS Office media types and file extensions listed in
comment|// http://blogs.msdn.com/vsofficedeveloper/pages/Office-2007-Open-XML-MIME-Types.aspx
name|assertTypeByName
argument_list|(
literal|"application/msword"
argument_list|,
literal|"x.doc"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/msword"
argument_list|,
literal|"x.dot"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|,
literal|"x.docx"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.wordprocessingml.template"
argument_list|,
literal|"x.dotx"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-word.document.macroenabled.12"
argument_list|,
literal|"x.docm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-word.template.macroenabled.12"
argument_list|,
literal|"x.dotm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
literal|"x.xls"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
literal|"x.xlt"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
literal|"x.xla"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|,
literal|"x.xlsx"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.spreadsheetml.template"
argument_list|,
literal|"x.xltx"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-excel.sheet.macroenabled.12"
argument_list|,
literal|"x.xlsm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-excel.template.macroenabled.12"
argument_list|,
literal|"x.xltm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-excel.addin.macroenabled.12"
argument_list|,
literal|"x.xlam"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-excel.sheet.binary.macroenabled.12"
argument_list|,
literal|"x.xlsb"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|,
literal|"x.ppt"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|,
literal|"x.pot"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|,
literal|"x.pps"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|,
literal|"x.ppa"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|,
literal|"x.pptx"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.presentationml.template"
argument_list|,
literal|"x.potx"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.openxmlformats-officedocument.presentationml.slideshow"
argument_list|,
literal|"x.ppsx"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-powerpoint.addin.macroenabled.12"
argument_list|,
literal|"x.ppam"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-powerpoint.presentation.macroenabled.12"
argument_list|,
literal|"x.pptm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-powerpoint.template.macroenabled.12"
argument_list|,
literal|"x.potm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/vnd.ms-powerpoint.slideshow.macroenabled.12"
argument_list|,
literal|"x.ppsm"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOoxmlDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTypeByData
argument_list|(
literal|"application/x-tika-ooxml"
argument_list|,
literal|"testWORD.docx"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"application/x-tika-ooxml"
argument_list|,
literal|"testEXCEL.xlsx"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"application/x-tika-ooxml"
argument_list|,
literal|"testPPT.pptx"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJpegDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"testJPEG.jpg"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"testJPEG.jpg"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"x.jpg"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"x.JPG"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"x.jpeg"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"x.JPEG"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"x.jpe"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"x.jif"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"x.jfif"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"x.jfi"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTiffDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"image/tiff"
argument_list|,
literal|"testTIFF.tif"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/tiff"
argument_list|,
literal|"testTIFF.tif"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/tiff"
argument_list|,
literal|"x.tiff"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/tiff"
argument_list|,
literal|"x.tif"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/tiff"
argument_list|,
literal|"x.TIF"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGifDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"image/gif"
argument_list|,
literal|"testGIF.gif"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/gif"
argument_list|,
literal|"testGIF.gif"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/gif"
argument_list|,
literal|"x.gif"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/gif"
argument_list|,
literal|"x.GIF"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPngDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"image/png"
argument_list|,
literal|"testPNG.png"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/png"
argument_list|,
literal|"testPNG.png"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/png"
argument_list|,
literal|"x.png"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/png"
argument_list|,
literal|"x.PNG"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBmpDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"image/bmp"
argument_list|,
literal|"testBMP.bmp"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/bmp"
argument_list|,
literal|"testBMP.bmp"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/bmp"
argument_list|,
literal|"x.bmp"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/bmp"
argument_list|,
literal|"x.BMP"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/bmp"
argument_list|,
literal|"x.dib"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/bmp"
argument_list|,
literal|"x.DIB"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPnmDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"image/x-portable-bitmap"
argument_list|,
literal|"testPBM.pbm"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"image/x-portable-graymap"
argument_list|,
literal|"testPGM.pgm"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"image/x-portable-pixmap"
argument_list|,
literal|"testPPM.ppm"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/x-portable-bitmap"
argument_list|,
literal|"testPBM.pbm"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/x-portable-graymap"
argument_list|,
literal|"testPGM.pgm"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/x-portable-pixmap"
argument_list|,
literal|"testPPM.ppm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-portable-anymap"
argument_list|,
literal|"x.pnm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-portable-anymap"
argument_list|,
literal|"x.PNM"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-portable-bitmap"
argument_list|,
literal|"x.pbm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-portable-bitmap"
argument_list|,
literal|"x.PBM"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-portable-graymap"
argument_list|,
literal|"x.pgm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-portable-graymap"
argument_list|,
literal|"x.PGM"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-portable-pixmap"
argument_list|,
literal|"x.ppm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-portable-pixmap"
argument_list|,
literal|"x.PPM"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCgmDetection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: Need a test image file
name|assertTypeByName
argument_list|(
literal|"image/cgm"
argument_list|,
literal|"x.cgm"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/cgm"
argument_list|,
literal|"x.CGM"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRdfXmlDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTypeByName
argument_list|(
literal|"application/rdf+xml"
argument_list|,
literal|"x.rdf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/rdf+xml"
argument_list|,
literal|"x.owl"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSvgDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"image/svg+xml"
argument_list|,
literal|"testSVG.svg"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"image/svg+xml"
argument_list|,
literal|"testSVG.svg"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/svg+xml"
argument_list|,
literal|"x.svg"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/svg+xml"
argument_list|,
literal|"x.SVG"
argument_list|)
expr_stmt|;
comment|// Should *.svgz be svg or gzip
name|assertType
argument_list|(
literal|"application/x-gzip"
argument_list|,
literal|"testSVG.svgz"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"application/x-gzip"
argument_list|,
literal|"testSVG.svgz"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/svg+xml"
argument_list|,
literal|"x.svgz"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/svg+xml"
argument_list|,
literal|"x.SVGZ"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPdfDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"application/pdf"
argument_list|,
literal|"testPDF.pdf"
argument_list|)
expr_stmt|;
name|assertTypeByData
argument_list|(
literal|"application/pdf"
argument_list|,
literal|"testPDF.pdf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/pdf"
argument_list|,
literal|"x.pdf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/pdf"
argument_list|,
literal|"x.PDF"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSwfDetection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: Need a test flash file
name|assertTypeByName
argument_list|(
literal|"application/x-shockwave-flash"
argument_list|,
literal|"x.swf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/x-shockwave-flash"
argument_list|,
literal|"x.SWF"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWmfDetection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: Need a test wmf file
name|assertTypeByName
argument_list|(
literal|"application/x-msmetafile"
argument_list|,
literal|"x.wmf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/x-msmetafile"
argument_list|,
literal|"x.WMF"
argument_list|)
expr_stmt|;
comment|// TODO: Need a test emf file
name|assertTypeByName
argument_list|(
literal|"application/x-msmetafile"
argument_list|,
literal|"x.emf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/x-msmetafile"
argument_list|,
literal|"x.EMF"
argument_list|)
expr_stmt|;
comment|// TODO: Need a test wmz file
name|assertTypeByName
argument_list|(
literal|"application/x-ms-wmz"
argument_list|,
literal|"x.wmz"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/x-ms-wmz"
argument_list|,
literal|"x.WMZ"
argument_list|)
expr_stmt|;
comment|// TODO: Need a test emf file
name|assertTypeByName
argument_list|(
literal|"application/x-gzip"
argument_list|,
literal|"x.emz"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/x-gzip"
argument_list|,
literal|"x.EMZ"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPsDetection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: Need a test postscript file
name|assertTypeByName
argument_list|(
literal|"application/postscript"
argument_list|,
literal|"x.ps"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/postscript"
argument_list|,
literal|"x.PS"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/postscript"
argument_list|,
literal|"x.eps"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/postscript"
argument_list|,
literal|"x.epsf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"application/postscript"
argument_list|,
literal|"x.epsi"
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since TIKA-194      */
specifier|public
name|void
name|testJavaRegex
parameter_list|()
throws|throws
name|Exception
block|{
name|MimeType
name|testType
init|=
operator|new
name|MimeType
argument_list|(
name|this
operator|.
name|repo
argument_list|,
literal|"foo/bar"
argument_list|)
decl_stmt|;
name|this
operator|.
name|repo
operator|.
name|add
argument_list|(
name|testType
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|repo
operator|.
name|forName
argument_list|(
literal|"foo/bar"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|pattern
init|=
literal|"rtg_sst_grb_0\\.5\\.\\d{8}"
decl_stmt|;
name|this
operator|.
name|repo
operator|.
name|addPattern
argument_list|(
name|testType
argument_list|,
name|pattern
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|testFileName
init|=
literal|"rtg_sst_grb_0.5.12345678"
decl_stmt|;
name|assertNotNull
argument_list|(
name|this
operator|.
name|repo
operator|.
name|getMimeType
argument_list|(
name|testFileName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|repo
operator|.
name|getMimeType
argument_list|(
name|testFileName
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
literal|"foo/bar"
argument_list|)
expr_stmt|;
name|MimeType
name|testType2
init|=
operator|new
name|MimeType
argument_list|(
name|this
operator|.
name|repo
argument_list|,
literal|"foo/bar2"
argument_list|)
decl_stmt|;
name|this
operator|.
name|repo
operator|.
name|add
argument_list|(
name|testType2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|repo
operator|.
name|forName
argument_list|(
literal|"foo/bar2"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|repo
operator|.
name|addPattern
argument_list|(
name|testType2
argument_list|,
name|pattern
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|this
operator|.
name|repo
operator|.
name|getMimeType
argument_list|(
name|testFileName
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"foo/bar2"
argument_list|,
name|this
operator|.
name|repo
operator|.
name|getMimeType
argument_list|(
name|testFileName
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRawDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTypeByName
argument_list|(
literal|"image/x-raw-adobe"
argument_list|,
literal|"x.dng"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-adobe"
argument_list|,
literal|"x.DNG"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-hasselblad"
argument_list|,
literal|"x.3fr"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-fuji"
argument_list|,
literal|"x.raf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-canon"
argument_list|,
literal|"x.crw"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-canon"
argument_list|,
literal|"x.cr2"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-kodak"
argument_list|,
literal|"x.k25"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-kodak"
argument_list|,
literal|"x.kdc"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-kodak"
argument_list|,
literal|"x.dcs"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-kodak"
argument_list|,
literal|"x.drf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-minolta"
argument_list|,
literal|"x.mrw"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-nikon"
argument_list|,
literal|"x.nef"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-nikon"
argument_list|,
literal|"x.nrw"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-olympus"
argument_list|,
literal|"x.orf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-pentax"
argument_list|,
literal|"x.ptx"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-pentax"
argument_list|,
literal|"x.pef"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-sony"
argument_list|,
literal|"x.arw"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-sony"
argument_list|,
literal|"x.srf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-sony"
argument_list|,
literal|"x.sr2"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-sigma"
argument_list|,
literal|"x.x3f"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-epson"
argument_list|,
literal|"x.erf"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-mamiya"
argument_list|,
literal|"x.mef"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-leaf"
argument_list|,
literal|"x.mos"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-panasonic"
argument_list|,
literal|"x.raw"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-panasonic"
argument_list|,
literal|"x.rw2"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-phaseone"
argument_list|,
literal|"x.cap"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-phaseone"
argument_list|,
literal|"x.iiq"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-phaseone"
argument_list|,
literal|"x.cap"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-red"
argument_list|,
literal|"x.r3d"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-imacon"
argument_list|,
literal|"x.fff"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-logitech"
argument_list|,
literal|"x.pxn"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-casio"
argument_list|,
literal|"x.bay"
argument_list|)
expr_stmt|;
name|assertTypeByName
argument_list|(
literal|"image/x-raw-rawzor"
argument_list|,
literal|"x.rwz"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests MimeTypes.getMimeType(URL), which examines both the byte header      * and, if necessary, the URL's extension.      */
specifier|public
name|void
name|testMimeDeterminationForTestDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|assertType
argument_list|(
literal|"text/html"
argument_list|,
literal|"testHTML.html"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"application/zip"
argument_list|,
literal|"test-documents.zip"
argument_list|)
expr_stmt|;
comment|// TODO: Currently returns generic MS Office type based on
comment|// the magic header. The getMimeType method should understand
comment|// MS Office types better.
comment|// assertEquals("application/vnd.ms-excel",
comment|// getMimeType("testEXCEL.xls"));
comment|// assertEquals("application/vnd.ms-powerpoint",
comment|// getMimeType("testPPT.ppt"));
comment|// assertEquals("application/msword", getMimeType("testWORD.doc"));
name|assertType
argument_list|(
literal|"text/html"
argument_list|,
literal|"testHTML_utf8.html"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
argument_list|,
literal|"testOpenOffice2.odt"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"application/pdf"
argument_list|,
literal|"testPDF.pdf"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"application/rtf"
argument_list|,
literal|"testRTF.rtf"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"text/plain"
argument_list|,
literal|"testTXT.txt"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"application/xml"
argument_list|,
literal|"testXML.xml"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"audio/basic"
argument_list|,
literal|"testAU.au"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"audio/x-aiff"
argument_list|,
literal|"testAIFF.aif"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"audio/x-wav"
argument_list|,
literal|"testWAV.wav"
argument_list|)
expr_stmt|;
name|assertType
argument_list|(
literal|"audio/midi"
argument_list|,
literal|"testMID.mid"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertType
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|stream
init|=
name|TestMimeTypes
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
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
name|filename
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|repo
operator|.
name|detect
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
operator|.
name|toString
argument_list|()
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
block|}
specifier|private
name|void
name|assertTypeByName
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
name|filename
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|repo
operator|.
name|detect
argument_list|(
literal|null
argument_list|,
name|metadata
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertTypeByData
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
name|stream
init|=
name|TestMimeTypes
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|repo
operator|.
name|detect
argument_list|(
name|stream
argument_list|,
name|metadata
argument_list|)
operator|.
name|toString
argument_list|()
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
block|}
block|}
end_class

end_unit

