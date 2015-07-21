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
name|pdfbox
operator|.
name|pdfparser
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
name|pdfbox
operator|.
name|cos
operator|.
name|COSString
import|;
end_import

begin_comment
comment|/**  * In fairly rare cases, a PDF's XMP will contain a string that  * has incorrectly been encoded with PDFEncoding: an octal for non-ascii and  * ascii for ascii, e.g. "\376\377\000M\000i\000c\000r\000o\000s\000o\000f\000t\000"  *<p>  * This class can be used to decode those strings.  *<p>  * See TIKA-1678.  Many thanks to Andrew Jackson for raising this issue  * and Tilman Hausherr for the solution.  *<p>  * Unfortunately because {@link BaseParser#parseCOSString()} is protected, we  * had to put this in o.a.pdfbox.pdfparser and not in o.a.t.parser.pdf  */
end_comment

begin_class
specifier|public
class|class
name|PDFOctalUnicodeDecoder
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|PDF_ENCODING_BOMS
init|=
block|{
literal|"\\376\\377"
block|,
comment|//UTF-16BE
literal|"\\377\\376"
block|,
comment|//UTF-16LE
literal|"\\357\\273\\277"
comment|//UTF-8
block|}
decl_stmt|;
comment|/**      * Does this string contain an octal-encoded UTF BOM?      * Call this statically to determine if you should bother creating a new parser to parse it.      * @param s      * @return      */
specifier|public
specifier|static
name|boolean
name|shouldDecode
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|length
argument_list|()
operator|<
literal|8
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|BOM
range|:
name|PDF_ENCODING_BOMS
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
name|BOM
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * This assumes that {@link #shouldDecode(String)} has been called      * and has returned true.  If you run this on a non-octal encoded string,      * disaster will happen!      *      * @param value      * @return      */
specifier|public
name|String
name|decode
parameter_list|(
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|String
argument_list|(
literal|"("
operator|+
name|value
operator|+
literal|")"
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-1"
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|COSStringParser
name|p
init|=
operator|new
name|COSStringParser
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|COSString
name|cosString
init|=
name|p
operator|.
name|parseCOSString
argument_list|()
decl_stmt|;
if|if
condition|(
name|cosString
operator|!=
literal|null
condition|)
block|{
return|return
name|cosString
operator|.
name|getString
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//oh well, we tried.
block|}
comment|//just return value if something went wrong
return|return
name|value
return|;
block|}
specifier|private
class|class
name|COSStringParser
extends|extends
name|BaseParser
block|{
specifier|private
name|COSStringParser
parameter_list|(
name|InputStream
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
