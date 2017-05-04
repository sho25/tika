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
name|image
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_class
specifier|public
class|class
name|ImageParserTest
extends|extends
name|TikaTest
block|{
specifier|private
specifier|final
name|Parser
name|parser
init|=
operator|new
name|ImageParser
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testBMP
parameter_list|()
throws|throws
name|Exception
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
name|CONTENT_TYPE
argument_list|,
literal|"image/bmp"
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testBMP.bmp"
argument_list|)
decl_stmt|;
name|parser
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"height"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"width"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"8 8 8"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Data BitsPerSample"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension PixelAspectRatio"
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO: figure out why we're getting 0.35273367 in Ubuntu, but not Windows
comment|//assertEquals("0", metadata.get("Dimension VerticalPhysicalPixelSpacing"));
comment|//assertEquals("0", metadata.get("Dimension HorizontalPhysicalPixelSpacing"));
name|assertEquals
argument_list|(
literal|"BI_RGB"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression CompressionTypeName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/bmp"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"8 8 8"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGIF
parameter_list|()
throws|throws
name|Exception
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
name|CONTENT_TYPE
argument_list|,
literal|"image/gif"
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testGIF.gif"
argument_list|)
decl_stmt|;
name|parser
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"height"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"width"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression Lossless"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Normal"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension ImageOrientation"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lzw"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression CompressionTypeName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension HorizontalPixelOffset"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"imageLeftPosition=0, imageTopPosition=0, imageWidth=100, imageHeight=75, interlaceFlag=false"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"ImageDescriptor"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Index"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Data SampleFormat"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Chroma NumChannels"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression NumProgressiveScans"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RGB"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Chroma ColorSpaceType"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"CommentExtensions CommentExtension"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value=Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership., encoding=ISO-8859-1, compression=none"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Text TextEntry"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Chroma BlackIsZero"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"disposalMethod=none, userInputFlag=false, transparentColorFlag=false, delayTime=0, transparentColorIndex=0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"GraphicControlExtension"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension VerticalPixelOffset"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/gif"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|COMMENTS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJPEG
parameter_list|()
throws|throws
name|Exception
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
name|CONTENT_TYPE
argument_list|,
literal|"image/jpeg"
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testJPEG.jpg"
argument_list|)
decl_stmt|;
name|parser
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"height"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"width"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0.35277778"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension VerticalPixelSize"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression Lossless"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"class=0, htableId=0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"markerSequence dht dhtable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"majorVersion=1, minorVersion=1, resUnits=1, Xdensity=72, Ydensity=72, thumbWidth=0, thumbHeight=0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"JPEGvariety app0JFIF"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"225"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"markerSequence unknown"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"componentSelector=1, dcHuffTable=0, acHuffTable=0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"markerSequence sos scanComponentSpec"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"normal"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension ImageOrientation"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension PixelAspectRatio"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"elementPrecision=0, qtableId=0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"markerSequence dqt dqtable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"numScanComponents=3, startSpectralSelection=0, endSpectralSelection=63, approxHigh=0, approxLow=0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"markerSequence sos"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"componentId=1, HsamplingFactor=1, VsamplingFactor=1, QtableSelector=0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"markerSequence sof componentSpec"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JPEG"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression CompressionTypeName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0.35277778"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension HorizontalPixelSize"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"markerSequence com"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Chroma NumChannels"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression NumProgressiveScans"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"YCbCr"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Chroma ColorSpaceType"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword=comment, value=Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Text TextEntry"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/jpeg"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"process=0, samplePrecision=8, numLines=75, samplesPerLine=100, numFrameComponents=3"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"markerSequence sof"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|COMMENTS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPNG
parameter_list|()
throws|throws
name|Exception
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
name|CONTENT_TYPE
argument_list|,
literal|"image/png"
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPNG.png"
argument_list|)
decl_stmt|;
name|parser
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"height"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"width"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0.35273367"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension VerticalPixelSize"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"8 8 8"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Data BitsPerSample"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Perceptual"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"sRGB"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression Lossless"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"year=2008, month=5, day=6, hour=6, minute=18, second=47"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"tIME"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Normal"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension ImageOrientation"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension PixelAspectRatio"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword=Comment, value=Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership."
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"tEXt tEXtEntry"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"deflate"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression CompressionTypeName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UnsignedIntegral"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Data SampleFormat"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0.35273367"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Dimension HorizontalPixelSize"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"none"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Transparency Alpha"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pixelsPerUnitXAxis=2835, pixelsPerUnitYAxis=2835, unitSpecifier=meter"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"pHYs"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Chroma NumChannels"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Compression NumProgressiveScans"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RGB"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Chroma ColorSpaceType"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"keyword=Comment, value=Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership., encoding=ISO-8859-1, compression=none"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Text TextEntry"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PixelInterleaved"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Data PlanarConfiguration"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"width=100, height=75, bitDepth=8, colorType=RGB, compressionMethod=deflate, filterMethod=adaptive, interlaceMethod=none"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"IHDR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Chroma BlackIsZero"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"year=2008, month=5, day=6, hour=6, minute=18, second=47"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Document ImageModificationTime"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/png"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"75"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"8 8 8"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|BITS_PER_SAMPLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// TIKA-2232
specifier|public
name|void
name|testJBIG2
parameter_list|()
throws|throws
name|Exception
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
name|CONTENT_TYPE
argument_list|,
literal|"image/x-jbig2"
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testJBIG2.jb2"
argument_list|)
decl_stmt|;
name|parser
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"78"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"height"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"328"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"width"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"image/x-jbig2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

