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

begin_comment
comment|/**  * Test cases for the {@link MagicDetector} class.  */
end_comment

begin_class
specifier|public
class|class
name|MagicDetectorTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testDetectSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|MediaType
name|html
init|=
operator|new
name|MediaType
argument_list|(
literal|"text"
argument_list|,
literal|"html"
argument_list|)
decl_stmt|;
name|Detector
name|detector
init|=
operator|new
name|MagicDetector
argument_list|(
name|html
argument_list|,
literal|"<html"
operator|.
name|getBytes
argument_list|(
literal|"ASCII"
argument_list|)
argument_list|)
decl_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"<html><head/><body/></html>"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"<HTML"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"<?xml?><html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDetectOffsetRange
parameter_list|()
throws|throws
name|Exception
block|{
name|MediaType
name|html
init|=
operator|new
name|MediaType
argument_list|(
literal|"text"
argument_list|,
literal|"html"
argument_list|)
decl_stmt|;
name|Detector
name|detector
init|=
operator|new
name|MagicDetector
argument_list|(
name|html
argument_list|,
literal|"<html"
operator|.
name|getBytes
argument_list|(
literal|"ASCII"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|64
argument_list|)
decl_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"<html><head/><body/></html>"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"<?xml?><html/>"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"\n<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"\u0000<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"<htm"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|" html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"<HTML"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"0........1.........2.........3.........4.........5.........6"
operator|+
literal|"1234<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"0........1.........2.........3.........4.........5.........6"
operator|+
literal|"12345<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDetectMask
parameter_list|()
throws|throws
name|Exception
block|{
name|MediaType
name|html
init|=
operator|new
name|MediaType
argument_list|(
literal|"text"
argument_list|,
literal|"html"
argument_list|)
decl_stmt|;
name|byte
name|up
init|=
operator|(
name|byte
operator|)
literal|0xdf
decl_stmt|;
name|Detector
name|detector
init|=
operator|new
name|MagicDetector
argument_list|(
name|html
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|'<'
block|,
literal|'H'
block|,
literal|'T'
block|,
literal|'M'
block|,
literal|'L'
block|}
argument_list|,
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xff
block|,
name|up
block|,
name|up
block|,
name|up
block|,
name|up
block|}
argument_list|,
literal|0
argument_list|,
literal|64
argument_list|)
decl_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"<HTML><head/><body/></html>"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"<?xml?><HtMl/>"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"\n<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"\u0000<HTML"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"<htm"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|" html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|html
argument_list|,
literal|"0        1         2         3         4         5         6"
operator|+
literal|"1234<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|"0        1         2         3         4         5         6"
operator|+
literal|"12345<html"
argument_list|)
expr_stmt|;
name|assertDetect
argument_list|(
name|detector
argument_list|,
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertDetect
parameter_list|(
name|Detector
name|detector
parameter_list|,
name|MediaType
name|type
parameter_list|,
name|String
name|data
parameter_list|)
block|{
try|try
block|{
name|assertEquals
argument_list|(
name|type
argument_list|,
name|detector
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getBytes
argument_list|(
literal|"ASCII"
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected exception from MagicDetector"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

