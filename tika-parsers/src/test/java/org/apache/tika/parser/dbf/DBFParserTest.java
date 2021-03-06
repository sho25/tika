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
name|dbf
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|DBFParserTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testDBF.dbf"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DBFReader
operator|.
name|Version
operator|.
name|FOXBASE_PLUS
operator|.
name|getFullMimeString
argument_list|()
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2016-05-24T00:00:00Z"
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UTF-8"
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|r
operator|.
name|xml
operator|.
name|replaceAll
argument_list|(
literal|"[\\t\\r\\n]"
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
comment|//header
name|assertContains
argument_list|(
literal|"<thead><th>TEXT_FIELD</th><th>NUMERIC_FI</th><th>DATE_FIELD</th></thead>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|//look for contents
name|assertContains
argument_list|(
literal|"普林斯顿大学"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"\u0627\u0645\u0639\u0629"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"05/26/2016"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td>4.0</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|//make sure there is no problem around row 10
comment|//where we're buffering
name|assertContains
argument_list|(
literal|"<td>8.0</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td>9.0</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td>10.0</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td>11.0</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<td>licour</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGB18030Encoded
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testDBF_gb18030.dbf"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DBFReader
operator|.
name|Version
operator|.
name|FOXBASE_PLUS
operator|.
name|getFullMimeString
argument_list|()
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"虽然该"
argument_list|,
name|r
operator|.
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTruncated
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|DBFParser
argument_list|()
decl_stmt|;
comment|//should throw exception for truncation in header
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|129
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
name|truncate
argument_list|(
literal|"testDBF.dbf"
argument_list|,
name|i
argument_list|)
argument_list|,
name|p
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception for truncation in header: "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|TikaException
name|e
parameter_list|)
block|{
comment|//ok -- expected
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Should only throw IOExceptions or TikaExceptions"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//default don't throw exception for truncation while reading body
for|for
control|(
name|int
name|i
init|=
literal|129
init|;
name|i
operator|<
literal|204
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
name|truncate
argument_list|(
literal|"testDBF.dbf"
argument_list|,
name|i
argument_list|)
argument_list|,
name|p
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|TikaException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Shouldn't have thrown exception for truncation while reading cells: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
name|DBFReader
operator|.
name|STRICT
operator|=
literal|true
expr_stmt|;
comment|//if strict is true throw exception for truncation in body
for|for
control|(
name|int
name|i
init|=
literal|129
init|;
name|i
operator|<
literal|204
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
name|truncate
argument_list|(
literal|"testDBF.dbf"
argument_list|,
name|i
argument_list|)
argument_list|,
name|p
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception for truncation while reading cells: "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|TikaException
name|e
parameter_list|)
block|{                 }
block|}
block|}
finally|finally
block|{
comment|//reset for other tests
name|DBFReader
operator|.
name|STRICT
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSpecificTruncated
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
name|truncate
argument_list|(
literal|"testDBF.dbf"
argument_list|,
literal|781
argument_list|)
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|r
operator|.
name|xml
operator|.
name|replaceAll
argument_list|(
literal|"[\\t\\r\\n]"
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
comment|//if you don't keep track of bytes read, you could get content from prev row
name|assertNotContained
argument_list|(
literal|"holt red hath in every"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertNotContained
argument_list|(
literal|"<td>holt</td><td>18.0</td>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|//check that the last row ends with holt but is correctly formatted
name|assertContains
argument_list|(
literal|"<td>holt</td><td /><td /></tr>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVariants
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testDBF.dbf"
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|bos
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|bytes
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
for|for
control|(
name|DBFReader
operator|.
name|Version
name|version
range|:
name|DBFReader
operator|.
name|Version
operator|.
name|values
argument_list|()
control|)
block|{
comment|//this cast happens to work because of the range of possible values
name|bytes
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|version
operator|.
name|getId
argument_list|()
expr_stmt|;
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|version
operator|.
name|getFullMimeString
argument_list|()
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* commented out until we get permission to add the test file     @Test     public void testEncodingInHeaderAndDateTime() throws Exception {         XMLResult r = getXML("prem2007_2.dbf");         String xml = r.xml.replaceAll("[\\r\\n\\t]", " ");         assertEquals("application/x-dbf; dbf_version=Visual_FoxPro", r.metadata.get(Metadata.CONTENT_TYPE));         assertContains("<th>莉こ晤鎢</th>", xml);//header         assertContains("<td>齠褕</td>", xml);//content         assertContains("<td>2010-04-20T00:00:00Z</td>", xml);     }     */
block|}
end_class

end_unit

