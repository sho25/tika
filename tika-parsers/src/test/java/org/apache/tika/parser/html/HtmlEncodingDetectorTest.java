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
name|html
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
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|HtmlEncodingDetectorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|basic
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta charset='WINDOWS-1252'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"can we can prove this harms detection"
argument_list|)
specifier|public
name|void
name|utf16
parameter_list|()
throws|throws
name|IOException
block|{
comment|// According to the specification 'If charset is a UTF-16 encoding, then set charset to UTF-8.'
name|assertCharset
argument_list|(
literal|"<meta charset='UTF-16BE'>"
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xUserDefined
parameter_list|()
throws|throws
name|IOException
block|{
comment|// According to the specification 'If charset is x-user-defined, then set charset to windows-1252.'
name|assertWindows1252
argument_list|(
literal|"<meta charset='x-user-defined'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|withSlash
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta/charset='WINDOWS-1252'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"until we do a full parse"
argument_list|)
specifier|public
name|void
name|insideTag
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta name='description'"
operator|+
literal|"content='If I write charset=UTF-8 here, it doesnt mean the page is in UTF-8'/>"
operator|+
literal|"<meta charset='WINDOWS-1252'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"until we do a full parse"
argument_list|)
specifier|public
name|void
name|missingAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta content='charset=UTF-8'>"
operator|+
comment|// missing http-equiv attribute
literal|"<meta charset='WINDOWS-1252'>"
comment|// valid declaration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"until we do a full parse"
argument_list|)
specifier|public
name|void
name|insideSpecialTag
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Content inside<?,<!, and</ should be ignored
for|for
control|(
name|byte
name|b
range|:
literal|"?!/"
operator|.
name|getBytes
argument_list|()
control|)
name|assertWindows1252
argument_list|(
literal|"<"
operator|+
operator|(
name|char
operator|)
name|b
operator|+
comment|// start comment
literal|"<meta charset='UTF-8'>"
operator|+
comment|// inside special tag
literal|"<meta charset='WINDOWS-1252'>"
comment|// real charset declaration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"until we can prove this harms detection"
argument_list|)
specifier|public
name|void
name|spaceBeforeTag
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"< meta charset='UTF-8'>"
operator|+
comment|// invalid charset declaration
literal|"<meta charset='WINDOWS-1252'>"
comment|// real charset declaration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|invalidAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta "
operator|+
literal|"badcharset='UTF-8' "
operator|+
comment|// invalid charset declaration
literal|"charset='WINDOWS-1252'>"
comment|// real charset declaration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"until we can prove this harms detection"
argument_list|)
specifier|public
name|void
name|unmatchedQuote
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta http-equiv='content-type' content='charset=\"UTF-8'>"
operator|+
comment|// invalid charset declaration
literal|"<meta charset='WINDOWS-1252'>"
comment|// real charset declaration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"until we do a full parse"
argument_list|)
specifier|public
name|void
name|withCompactComment
parameter_list|()
throws|throws
name|IOException
block|{
comment|//<!--> is a valid comment
name|assertWindows1252
argument_list|(
literal|"<!--"
operator|+
comment|// start comment
literal|"<meta charset='UTF-8'>"
operator|+
comment|// inside comment
literal|"-->"
operator|+
comment|// end comment
literal|"<!-->"
operator|+
comment|// compact comment
literal|"<meta charset='WINDOWS-1252'>"
comment|// outside comment, charset declaration
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertWindows1252
parameter_list|(
name|String
name|html
parameter_list|)
throws|throws
name|IOException
block|{
name|assertCharset
argument_list|(
name|html
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"WINDOWS-1252"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertCharset
parameter_list|(
name|String
name|html
parameter_list|,
name|Charset
name|charset
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|html
operator|+
literal|" should be detected as "
operator|+
name|charset
argument_list|,
name|charset
argument_list|,
name|detectCharset
argument_list|(
name|html
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Charset
name|detectCharset
parameter_list|(
name|String
name|test
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
name|InputStream
name|inStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|test
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|HtmlEncodingDetector
argument_list|()
operator|.
name|detect
argument_list|(
name|inStream
argument_list|,
name|metadata
argument_list|)
return|;
block|}
block|}
end_class

end_unit

