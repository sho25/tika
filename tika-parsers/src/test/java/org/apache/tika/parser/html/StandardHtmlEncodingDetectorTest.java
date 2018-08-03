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
name|parser
operator|.
name|html
operator|.
name|charsetdetector
operator|.
name|StandardHtmlEncodingDetector
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
name|parser
operator|.
name|html
operator|.
name|charsetdetector
operator|.
name|charsets
operator|.
name|ReplacementCharset
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|assertArrayEquals
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
name|StandardHtmlEncodingDetectorTest
block|{
specifier|private
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|this
operator|.
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
block|}
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
literal|"<meta charset=WINDOWS-1252>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|quoted
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
specifier|public
name|void
name|duplicateMeta
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta charset='WINDOWS-1252'>"
operator|+
literal|"<meta charset='UTF-8'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|duplicateAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta charset='WINDOWS-1252' charset='UTF-8'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|invalidThenValid
parameter_list|()
throws|throws
name|IOException
block|{
name|assertCharset
argument_list|(
literal|"<meta charset=blah>"
operator|+
literal|"<meta charset=WINDOWS-1252>"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|spacesInAttributes
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta charset\u000C=  \t  WINDOWS-1252>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|httpEquiv
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta "
operator|+
literal|"http-equiv='content-type' "
operator|+
literal|"content='text/html; charset=\"WINDOWS-1252\"'>"
argument_list|)
expr_stmt|;
comment|// quotes around the charset are allowed
name|assertWindows1252
argument_list|(
literal|"<meta "
operator|+
literal|"content=' charset  =  WINDOWS-1252' "
operator|+
comment|// The charset may be anywhere in the content attribute
literal|"http-equiv='content-type'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|emptyAttributeEnd
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta charset=WINDOWS-1252 a>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|httpEquivDuplicateCharset
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta "
operator|+
literal|"http-equiv='content-type' "
operator|+
literal|"content='charset=WINDOWS-1252;"
operator|+
comment|// The detection should stop after the semicolon
literal|"charset=UTF-8'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|htmlFragment
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<!doctype html><html class=nojs><head><meta charset='WINDOWS-1252'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|veryBadHtml
parameter_list|()
throws|throws
name|IOException
block|{
comment|// check that the parser is not confused by garbage before the declaration
name|assertWindows1252
argument_list|(
literal|"<< l \" == / '=x\n>"
operator|+
literal|"<!--> "
operator|+
literal|"<<x'/<=> "
operator|+
literal|"<meta/>"
operator|+
literal|"<meta>"
operator|+
literal|"<a x/>"
operator|+
literal|"<meta charset='WINDOWS-1252'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|specialTag
parameter_list|()
throws|throws
name|IOException
block|{
comment|// special tags cannot have arguments, any '>' ends them
name|assertWindows1252
argument_list|(
literal|"<? x='><meta charset='WINDOWS-1252'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|longHtml
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<!doctype html>\n"
operator|+
literal|"<html>\n"
operator|+
literal|"<head>\n"
operator|+
literal|"<title>Hello world</title>\n"
argument_list|)
decl_stmt|;
name|String
name|repeated
init|=
literal|"<meta x='y' />\n"
decl_stmt|;
name|String
name|charsetMeta
init|=
literal|"<meta charset='windows-1252'>"
decl_stmt|;
while|while
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|+
name|repeated
operator|.
name|length
argument_list|()
operator|+
name|charsetMeta
operator|.
name|length
argument_list|()
operator|<
literal|1024
condition|)
name|sb
operator|.
name|append
argument_list|(
name|repeated
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|charsetMeta
argument_list|)
expr_stmt|;
name|assertWindows1252
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|tooLong
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Create a string with 1Mb of '\0' followed by a meta
name|String
name|padded
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|1000000
index|]
argument_list|,
name|StandardCharsets
operator|.
name|ISO_8859_1
argument_list|)
operator|+
literal|"<meta charset='windows-1252'>"
decl_stmt|;
comment|// Only the first bytes should be prescanned, so the algorithm should stop before the meta tag
name|assertCharset
argument_list|(
name|padded
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|incompleteMeta
parameter_list|()
throws|throws
name|IOException
block|{
name|assertCharset
argument_list|(
literal|"<meta charset='WINDOWS-1252'"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// missing '>' at the end
block|}
annotation|@
name|Test
specifier|public
name|void
name|charsetWithWhiteSpaces
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<meta charset='   \t\n  WINDOWS-1252 \t\n'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mixedCase
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<mEtA chArsEt='WInDOWs-1252'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|replacement
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Several dangerous charsets should are aliases of 'replacement' in the spec
name|String
name|inString
init|=
literal|"<meta charset='iso-2022-cn'>"
decl_stmt|;
name|assertCharset
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|inString
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|ISO_8859_1
argument_list|)
argument_list|)
argument_list|,
operator|new
name|ReplacementCharset
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|iso88591
parameter_list|()
throws|throws
name|IOException
block|{
comment|// In the spec, iso-8859-1 is an alias for WINDOWS-1252
name|assertWindows1252
argument_list|(
literal|"<meta charset='iso-8859-1'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|macintoshEncoding
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The mac roman encoding exists in java, but under the name x-MacRoman
name|assertCharset
argument_list|(
literal|"<meta charset='macintosh'>"
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"x-MacRoman"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|bom
parameter_list|()
throws|throws
name|IOException
block|{
comment|// A BOM should have precedence over the meta
name|assertCharset
argument_list|(
literal|"\ufeff<meta charset='WINDOWS-1252'>"
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
literal|"\ufeff<meta charset='WINDOWS-1252'>"
argument_list|,
name|StandardCharsets
operator|.
name|UTF_16LE
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
literal|"\ufeff<meta charset='WINDOWS-1252'>"
argument_list|,
name|StandardCharsets
operator|.
name|UTF_16BE
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
specifier|public
name|void
name|insideDescription
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
specifier|public
name|void
name|insideTag
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<tag "
operator|+
literal|"attribute=\"<meta charset='UTF-8'>\" "
operator|+
comment|// inside attribute
literal|"<meta charset='UTF-8' "
operator|+
comment|// still inside tag
literal|"/>"
operator|+
comment|// tag end
literal|"<meta charset='WINDOWS-1252'>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
argument_list|(
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
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
specifier|public
name|void
name|realWorld
parameter_list|()
throws|throws
name|IOException
block|{
name|assertWindows1252
argument_list|(
literal|"<!DOCTYPE html>\n"
operator|+
literal|"<html lang=\"fr\">\n"
operator|+
literal|"<head>\n"
operator|+
literal|"<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':\n"
operator|+
literal|"\t\t\tnew Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],\n"
operator|+
literal|"\t\t\tj=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=\n"
operator|+
literal|"\t\t\t'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);\n"
operator|+
literal|"\t\t\t})(window,document,'script','dataLayer','GTM-PNX8H8X');</script>\n"
operator|+
literal|"<title>Horaires Transilien 2018 - Lignes A B C D E H J K L N P R U</title>\n"
operator|+
literal|"<meta name=\"description\" content=\"Consultez les horaires du Transilien en temps réel. Lignes A et B du RER. Lignes C D E H J K L N P R U du Transilien.\">\n"
operator|+
literal|"<meta name=\"keywords\" content=\"horaires transilien\">\n"
operator|+
literal|"<meta charset=\"windows-1252\">\n"
operator|+
literal|"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
operator|+
literal|"<meta name=\"robots\" content=\"follow, index\">\n"
operator|+
literal|"<base hr"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
annotation|@
name|Test
specifier|public
name|void
name|withCharsetInContentType
parameter_list|()
throws|throws
name|IOException
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/html; Charset=ISO-8859-1"
argument_list|)
expr_stmt|;
comment|// ISO-8859-1 is an alias for WINDOWS-1252, even if it's set at the transport layer level
name|assertWindows1252
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertWindows1252
argument_list|(
literal|"<meta charset='UTF-8'>"
argument_list|)
expr_stmt|;
name|assertWindows1252
argument_list|(
literal|"<meta http-equiv='content-type' content='charset=utf-8'>"
argument_list|)
expr_stmt|;
comment|// if a BOM is present, it has precedence over transport layer information
name|assertCharset
argument_list|(
literal|"\ufeff<meta charset='WINDOWS-1252'>"
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
literal|"\ufeff<meta charset='WINDOWS-1252'>"
argument_list|,
name|StandardCharsets
operator|.
name|UTF_16LE
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
literal|"\ufeff<meta charset='WINDOWS-1252'>"
argument_list|,
name|StandardCharsets
operator|.
name|UTF_16BE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|throwResistance
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The preprocessing should return right after having found the charset
comment|// So if an error is thrown in the stream AFTER the declaration,
comment|// it shouldn't see it
name|assertWindows1252
argument_list|(
name|throwAfter
argument_list|(
literal|"<meta charset='WINDOWS-1252'>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertWindows1252
argument_list|(
name|throwAfter
argument_list|(
literal|"<meta charset='WINDOWS-1252'><some other tag"
argument_list|)
argument_list|)
expr_stmt|;
comment|// But if an error is thrown before the end of the meta tag, it should see it
comment|// and return unsuccessfully
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<meta charset='WINDOWS-1252'"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// If there is no meta, but an error is thrown, the detector simply returns
comment|// unsuccessfully (it should not throw runtime errors)
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<!"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<!doctype"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<!doctype html><html"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<!doctype html><html attr"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<!doctype html><html attr="
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<!doctype html><html attr=x"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCharset
argument_list|(
name|throwAfter
argument_list|(
literal|"<!doctype html><html attr='x"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|streamReset
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The stream should be reset after detection
name|byte
index|[]
name|inBytes
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
decl_stmt|;
name|byte
index|[]
name|outBytes
init|=
operator|new
name|byte
index|[
literal|5
index|]
decl_stmt|;
name|InputStream
name|inStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|inBytes
argument_list|)
decl_stmt|;
name|detectCharset
argument_list|(
name|inStream
argument_list|)
expr_stmt|;
comment|// The stream should still be readable from the beginning after detection
name|inStream
operator|.
name|read
argument_list|(
name|outBytes
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|inBytes
argument_list|,
name|outBytes
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
name|assertWindows1252
parameter_list|(
name|InputStream
name|inStream
parameter_list|)
throws|throws
name|IOException
block|{
name|assertCharset
argument_list|(
name|inStream
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
specifier|final
name|Charset
name|contentsCharset
init|=
operator|(
name|charset
operator|==
literal|null
operator|)
condition|?
name|StandardCharsets
operator|.
name|UTF_8
else|:
name|charset
decl_stmt|;
name|InputStream
name|inStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|html
operator|.
name|getBytes
argument_list|(
name|contentsCharset
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Charset
name|detected
init|=
name|detectCharset
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
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
name|detected
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertCharset
parameter_list|(
name|InputStream
name|inStream
parameter_list|,
name|Charset
name|charset
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Charset
name|detected
init|=
name|detectCharset
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|charset
argument_list|,
name|detected
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Charset
name|detectCharset
parameter_list|(
name|InputStream
name|inStream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StandardHtmlEncodingDetector
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
specifier|private
name|InputStream
name|throwAfter
parameter_list|(
name|String
name|html
parameter_list|)
block|{
name|byte
index|[]
name|contents
init|=
name|html
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|InputStream
name|contentsInStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|contents
argument_list|)
decl_stmt|;
name|InputStream
name|errorThrowing
init|=
operator|new
name|InputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"test exception"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|SequenceInputStream
argument_list|(
name|contentsInStream
argument_list|,
name|errorThrowing
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

