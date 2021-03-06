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
operator|.
name|charsetdetector
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
name|charsets
operator|.
name|ReplacementCharset
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
name|XUserDefinedCharset
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
name|IllegalCharsetNameException
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
name|charset
operator|.
name|UnsupportedCharsetException
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
name|Locale
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

begin_comment
comment|/**  * Singleton class that associates standard charset names to java charset implementations  * https://encoding.spec.whatwg.org/#ref-for-iso-8859-8-i  */
end_comment

begin_class
specifier|final
class|class
name|CharsetAliases
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Charset
argument_list|>
name|charsetsByLabel
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|CharsetAliases
parameter_list|()
block|{     }
comment|/**      * @param label a charset name      * @return the corresponding java charset, if there is one. Otherwise, null      */
specifier|static
name|Charset
name|getCharsetByLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|==
literal|null
condition|)
return|return
literal|null
return|;
synchronized|synchronized
init|(
name|charsetsByLabel
init|)
block|{
comment|// Lazy initialization
if|if
condition|(
name|charsetsByLabel
operator|.
name|isEmpty
argument_list|()
condition|)
name|addAll
argument_list|()
expr_stmt|;
block|}
name|label
operator|=
name|label
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
return|return
name|charsetsByLabel
operator|.
name|get
argument_list|(
name|label
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|addAll
parameter_list|()
block|{
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"Big5"
argument_list|)
argument_list|,
literal|"big5"
argument_list|,
literal|"big5-hkscs"
argument_list|,
literal|"cn-big5"
argument_list|,
literal|"csbig5"
argument_list|,
literal|"x-x-big5"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"EUC-JP"
argument_list|)
argument_list|,
literal|"cseucpkdfmtjapanese"
argument_list|,
literal|"euc-jp"
argument_list|,
literal|"x-euc-jp"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"EUC-KR"
argument_list|)
argument_list|,
literal|"cseuckr"
argument_list|,
literal|"csksc56011987"
argument_list|,
literal|"euc-kr"
argument_list|,
literal|"iso-ir-149"
argument_list|,
literal|"korean"
argument_list|,
literal|"ks_c_5601-1987"
argument_list|,
literal|"ks_c_5601-1989"
argument_list|,
literal|"ksc5601"
argument_list|,
literal|"ksc_5601"
argument_list|,
literal|"windows-949"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"GBK"
argument_list|)
argument_list|,
literal|"chinese"
argument_list|,
literal|"csgb2312"
argument_list|,
literal|"csiso58gb231280"
argument_list|,
literal|"gb2312"
argument_list|,
literal|"gb_2312"
argument_list|,
literal|"gb_2312-80"
argument_list|,
literal|"gbk"
argument_list|,
literal|"iso-ir-58"
argument_list|,
literal|"x-gbk"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"IBM866"
argument_list|)
argument_list|,
literal|"866"
argument_list|,
literal|"cp866"
argument_list|,
literal|"csibm866"
argument_list|,
literal|"ibm866"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-2022-JP"
argument_list|)
argument_list|,
literal|"csiso2022jp"
argument_list|,
literal|"iso-2022-jp"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-10"
argument_list|,
literal|"ISO-8859-4"
argument_list|)
argument_list|,
literal|"csisolatin6"
argument_list|,
literal|"iso-8859-10"
argument_list|,
literal|"iso-ir-157"
argument_list|,
literal|"iso8859-10"
argument_list|,
literal|"iso885910"
argument_list|,
literal|"l6"
argument_list|,
literal|"latin6"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-13"
argument_list|)
argument_list|,
literal|"iso-8859-13"
argument_list|,
literal|"iso8859-13"
argument_list|,
literal|"iso885913"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-14"
argument_list|,
literal|"ISO-8859-1"
argument_list|)
argument_list|,
literal|"iso-8859-14"
argument_list|,
literal|"iso8859-14"
argument_list|,
literal|"iso885914"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-15"
argument_list|)
argument_list|,
literal|"csisolatin9"
argument_list|,
literal|"iso-8859-15"
argument_list|,
literal|"iso8859-15"
argument_list|,
literal|"iso885915"
argument_list|,
literal|"iso_8859-15"
argument_list|,
literal|"l9"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-16"
argument_list|,
literal|"ISO-8859-1"
argument_list|)
argument_list|,
literal|"iso-8859-16"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-2"
argument_list|)
argument_list|,
literal|"csisolatin2"
argument_list|,
literal|"iso-8859-2"
argument_list|,
literal|"iso-ir-101"
argument_list|,
literal|"iso8859-2"
argument_list|,
literal|"iso88592"
argument_list|,
literal|"iso_8859-2"
argument_list|,
literal|"iso_8859-2:1987"
argument_list|,
literal|"l2"
argument_list|,
literal|"latin2"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-3"
argument_list|)
argument_list|,
literal|"csisolatin3"
argument_list|,
literal|"iso-8859-3"
argument_list|,
literal|"iso-ir-109"
argument_list|,
literal|"iso8859-3"
argument_list|,
literal|"iso88593"
argument_list|,
literal|"iso_8859-3"
argument_list|,
literal|"iso_8859-3:1988"
argument_list|,
literal|"l3"
argument_list|,
literal|"latin3"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-4"
argument_list|)
argument_list|,
literal|"csisolatin4"
argument_list|,
literal|"iso-8859-4"
argument_list|,
literal|"iso-ir-110"
argument_list|,
literal|"iso8859-4"
argument_list|,
literal|"iso88594"
argument_list|,
literal|"iso_8859-4"
argument_list|,
literal|"iso_8859-4:1988"
argument_list|,
literal|"l4"
argument_list|,
literal|"latin4"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-5"
argument_list|)
argument_list|,
literal|"csisolatincyrillic"
argument_list|,
literal|"cyrillic"
argument_list|,
literal|"iso-8859-5"
argument_list|,
literal|"iso-ir-144"
argument_list|,
literal|"iso8859-5"
argument_list|,
literal|"iso88595"
argument_list|,
literal|"iso_8859-5"
argument_list|,
literal|"iso_8859-5:1988"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-6"
argument_list|)
argument_list|,
literal|"arabic"
argument_list|,
literal|"asmo-708"
argument_list|,
literal|"csiso88596e"
argument_list|,
literal|"csiso88596i"
argument_list|,
literal|"csisolatinarabic"
argument_list|,
literal|"ecma-114"
argument_list|,
literal|"iso-8859-6"
argument_list|,
literal|"iso-8859-6-e"
argument_list|,
literal|"iso-8859-6-i"
argument_list|,
literal|"iso-ir-127"
argument_list|,
literal|"iso8859-6"
argument_list|,
literal|"iso88596"
argument_list|,
literal|"iso_8859-6"
argument_list|,
literal|"iso_8859-6:1987"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-7"
argument_list|)
argument_list|,
literal|"csisolatingreek"
argument_list|,
literal|"ecma-118"
argument_list|,
literal|"elot_928"
argument_list|,
literal|"greek"
argument_list|,
literal|"greek8"
argument_list|,
literal|"iso-8859-7"
argument_list|,
literal|"iso-ir-126"
argument_list|,
literal|"iso8859-7"
argument_list|,
literal|"iso88597"
argument_list|,
literal|"iso_8859-7"
argument_list|,
literal|"iso_8859-7:1987"
argument_list|,
literal|"sun_eu_greek"
argument_list|)
expr_stmt|;
comment|// ISO-8859-8 actually should have an influence on the layout direction
comment|// (text should be decoded in the visual order). However, this is not implemented in tika.
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-8"
argument_list|)
argument_list|,
literal|"csiso88598e"
argument_list|,
literal|"csisolatinhebrew"
argument_list|,
literal|"hebrew"
argument_list|,
literal|"iso-8859-8"
argument_list|,
literal|"iso-8859-8-e"
argument_list|,
literal|"iso-ir-138"
argument_list|,
literal|"iso8859-8"
argument_list|,
literal|"iso88598"
argument_list|,
literal|"iso_8859-8"
argument_list|,
literal|"iso_8859-8:1988"
argument_list|,
literal|"visual"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"ISO-8859-8-I"
argument_list|,
literal|"ISO-8859-8"
argument_list|)
argument_list|,
literal|"csiso88598i"
argument_list|,
literal|"iso-8859-8-i"
argument_list|,
literal|"logical"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"KOI8-R"
argument_list|)
argument_list|,
literal|"cskoi8r"
argument_list|,
literal|"koi"
argument_list|,
literal|"koi8"
argument_list|,
literal|"koi8-r"
argument_list|,
literal|"koi8_r"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"KOI8-U"
argument_list|)
argument_list|,
literal|"koi8-ru"
argument_list|,
literal|"koi8-u"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"Shift_JIS"
argument_list|)
argument_list|,
literal|"csshiftjis"
argument_list|,
literal|"ms932"
argument_list|,
literal|"ms_kanji"
argument_list|,
literal|"shift-jis"
argument_list|,
literal|"shift_jis"
argument_list|,
literal|"sjis"
argument_list|,
literal|"windows-31j"
argument_list|,
literal|"x-sjis"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"UTF-16BE"
argument_list|)
argument_list|,
literal|"utf-16be"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"UTF-16LE"
argument_list|)
argument_list|,
literal|"utf-16"
argument_list|,
literal|"utf-16le"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|"unicode-1-1-utf-8"
argument_list|,
literal|"utf-8"
argument_list|,
literal|"utf8"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"gb18030"
argument_list|)
argument_list|,
literal|"gb18030"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1250"
argument_list|)
argument_list|,
literal|"cp1250"
argument_list|,
literal|"windows-1250"
argument_list|,
literal|"x-cp1250"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1251"
argument_list|)
argument_list|,
literal|"cp1251"
argument_list|,
literal|"windows-1251"
argument_list|,
literal|"x-cp1251"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1252"
argument_list|)
argument_list|,
literal|"ansi_x3.4-1968"
argument_list|,
literal|"ascii"
argument_list|,
literal|"cp1252"
argument_list|,
literal|"cp819"
argument_list|,
literal|"csisolatin1"
argument_list|,
literal|"ibm819"
argument_list|,
literal|"iso-8859-1"
argument_list|,
literal|"iso-ir-100"
argument_list|,
literal|"iso8859-1"
argument_list|,
literal|"iso88591"
argument_list|,
literal|"iso_8859-1"
argument_list|,
literal|"iso_8859-1:1987"
argument_list|,
literal|"l1"
argument_list|,
literal|"latin1"
argument_list|,
literal|"us-ascii"
argument_list|,
literal|"windows-1252"
argument_list|,
literal|"x-cp1252"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1253"
argument_list|)
argument_list|,
literal|"cp1253"
argument_list|,
literal|"windows-1253"
argument_list|,
literal|"x-cp1253"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1254"
argument_list|)
argument_list|,
literal|"cp1254"
argument_list|,
literal|"csisolatin5"
argument_list|,
literal|"iso-8859-9"
argument_list|,
literal|"iso-ir-148"
argument_list|,
literal|"iso8859-9"
argument_list|,
literal|"iso88599"
argument_list|,
literal|"iso_8859-9"
argument_list|,
literal|"iso_8859-9:1989"
argument_list|,
literal|"l5"
argument_list|,
literal|"latin5"
argument_list|,
literal|"windows-1254"
argument_list|,
literal|"x-cp1254"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1255"
argument_list|)
argument_list|,
literal|"cp1255"
argument_list|,
literal|"windows-1255"
argument_list|,
literal|"x-cp1255"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1256"
argument_list|)
argument_list|,
literal|"cp1256"
argument_list|,
literal|"windows-1256"
argument_list|,
literal|"x-cp1256"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1257"
argument_list|)
argument_list|,
literal|"cp1257"
argument_list|,
literal|"windows-1257"
argument_list|,
literal|"x-cp1257"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-1258"
argument_list|)
argument_list|,
literal|"cp1258"
argument_list|,
literal|"windows-1258"
argument_list|,
literal|"x-cp1258"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"windows-874"
argument_list|)
argument_list|,
literal|"dos-874"
argument_list|,
literal|"iso-8859-11"
argument_list|,
literal|"iso8859-11"
argument_list|,
literal|"iso885911"
argument_list|,
literal|"tis-620"
argument_list|,
literal|"windows-874"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"x-MacCyrillic"
argument_list|)
argument_list|,
literal|"x-mac-cyrillic"
argument_list|,
literal|"x-mac-ukrainian"
argument_list|)
expr_stmt|;
name|addCharset
argument_list|(
name|charset
argument_list|(
literal|"x-MacRoman"
argument_list|)
argument_list|,
literal|"csmacintosh"
argument_list|,
literal|"mac"
argument_list|,
literal|"macintosh"
argument_list|,
literal|"x-mac-roman"
argument_list|)
expr_stmt|;
comment|// The "replacement" charset is a dummy charset. It is present to mitigate wrong-charset attacks
name|addCharset
argument_list|(
operator|new
name|ReplacementCharset
argument_list|()
argument_list|,
literal|"csiso2022kr"
argument_list|,
literal|"hz-gb-2312"
argument_list|,
literal|"iso-2022-cn"
argument_list|,
literal|"iso-2022-cn-ext"
argument_list|,
literal|"iso-2022-kr"
argument_list|,
literal|"replacement"
argument_list|)
expr_stmt|;
comment|// The x-user-defined charset is not present in java
name|addCharset
argument_list|(
operator|new
name|XUserDefinedCharset
argument_list|()
argument_list|,
literal|"x-user-defined"
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param names jvm charset names      * @return the first of the given charsets that exists in the current JVM,      * or ISO_8859_1 if none exists      */
specifier|private
specifier|static
name|Charset
name|charset
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
try|try
block|{
return|return
name|Charset
operator|.
name|forName
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalCharsetNameException
decl||
name|UnsupportedCharsetException
name|e
parameter_list|)
block|{
comment|/* pass */
block|}
block|}
comment|// The only single-byte charset extended charset that must be present on every Java platform
return|return
name|StandardCharsets
operator|.
name|ISO_8859_1
return|;
block|}
comment|/**      * @param charset name of the charset in the JVM      * @param names   standard W3C charset names      */
specifier|private
specifier|static
name|void
name|addCharset
parameter_list|(
name|Charset
name|charset
parameter_list|,
name|String
modifier|...
name|names
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|charsetsByLabel
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|charset
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

