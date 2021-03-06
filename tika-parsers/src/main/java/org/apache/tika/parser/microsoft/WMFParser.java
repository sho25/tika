begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|microsoft
package|;
end_package

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
name|lang
operator|.
name|reflect
operator|.
name|Array
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwmf
operator|.
name|record
operator|.
name|HwmfFont
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwmf
operator|.
name|record
operator|.
name|HwmfRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwmf
operator|.
name|record
operator|.
name|HwmfRecordType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwmf
operator|.
name|record
operator|.
name|HwmfText
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwmf
operator|.
name|usermodel
operator|.
name|HwmfPicture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|util
operator|.
name|LocaleUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|util
operator|.
name|RecordFormatException
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
name|parser
operator|.
name|AbstractParser
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
name|sax
operator|.
name|XHTMLContentHandler
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
name|ContentHandler
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * This parser offers a very rough capability to extract text if there  * is text stored in the WMF files.  */
end_comment

begin_class
specifier|public
class|class
name|WMFParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|MediaType
name|MEDIA_TYPE
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"wmf"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|MEDIA_TYPE
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
try|try
block|{
name|HwmfPicture
name|picture
init|=
literal|null
decl_stmt|;
try|try
block|{
name|picture
operator|=
operator|new
name|HwmfPicture
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|//POI can throw this on corrupt files
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Charset
name|charset
init|=
name|LocaleUtil
operator|.
name|CHARSET_1252
decl_stmt|;
comment|//TODO: make x/y info public in POI so that we can use it here
comment|//to determine when to keep two text parts on the same line
for|for
control|(
name|HwmfRecord
name|record
range|:
name|picture
operator|.
name|getRecords
argument_list|()
control|)
block|{
comment|//this is pure hackery for specifying the font
comment|//TODO: do what Graphics does by maintaining the stack, etc.!
comment|//This fix should be done within POI
if|if
condition|(
name|record
operator|.
name|getWmfRecordType
argument_list|()
operator|.
name|equals
argument_list|(
name|HwmfRecordType
operator|.
name|createFontIndirect
argument_list|)
condition|)
block|{
name|HwmfFont
name|font
init|=
operator|(
operator|(
name|HwmfText
operator|.
name|WmfCreateFontIndirect
operator|)
name|record
operator|)
operator|.
name|getFont
argument_list|()
decl_stmt|;
name|charset
operator|=
operator|(
name|font
operator|.
name|getCharset
argument_list|()
operator|==
literal|null
operator|||
name|font
operator|.
name|getCharset
argument_list|()
operator|.
name|getCharset
argument_list|()
operator|==
literal|null
operator|)
condition|?
name|LocaleUtil
operator|.
name|CHARSET_1252
else|:
name|font
operator|.
name|getCharset
argument_list|()
operator|.
name|getCharset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|record
operator|.
name|getWmfRecordType
argument_list|()
operator|.
name|equals
argument_list|(
name|HwmfRecordType
operator|.
name|extTextOut
argument_list|)
condition|)
block|{
name|HwmfText
operator|.
name|WmfExtTextOut
name|textOut
init|=
operator|(
name|HwmfText
operator|.
name|WmfExtTextOut
operator|)
name|record
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|textOut
operator|.
name|getText
argument_list|(
name|charset
argument_list|)
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|record
operator|.
name|getWmfRecordType
argument_list|()
operator|.
name|equals
argument_list|(
name|HwmfRecordType
operator|.
name|textOut
argument_list|)
condition|)
block|{
name|HwmfText
operator|.
name|WmfTextOut
name|textOut
init|=
operator|(
name|HwmfText
operator|.
name|WmfTextOut
operator|)
name|record
decl_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|textOut
operator|.
name|getText
argument_list|(
name|charset
argument_list|)
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RecordFormatException
name|e
parameter_list|)
block|{
comment|//POI's hwmfparser can throw these for "parse exceptions"
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|//convert Runtime to RecordFormatExceptions
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|//POI's hwmfparser can throw these for parse exceptions
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

