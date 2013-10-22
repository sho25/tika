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
name|font
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Font
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|FontFormatException
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
name|fontbox
operator|.
name|ttf
operator|.
name|TTFParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|fontbox
operator|.
name|ttf
operator|.
name|TrueTypeFont
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
comment|/**  * Parser for TrueType font files (TTF).  */
end_comment

begin_class
specifier|public
class|class
name|TrueTypeParser
extends|extends
name|AbstractParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|44788554612243032L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|TYPE
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-font-ttf"
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
name|TYPE
argument_list|)
decl_stmt|;
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
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|cast
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// Until PDFBOX-1749 is fixed, if we can, use AWT to verify
comment|//  that the file is valid (otherwise FontBox could hang)
comment|// See TIKA-1182 for details
if|if
condition|(
name|tis
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|tis
operator|.
name|hasFile
argument_list|()
condition|)
block|{
name|Font
operator|.
name|createFont
argument_list|(
name|Font
operator|.
name|TRUETYPE_FONT
argument_list|,
name|tis
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tis
operator|.
name|mark
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Font
operator|.
name|createFont
argument_list|(
name|Font
operator|.
name|TRUETYPE_FONT
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|tis
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FontFormatException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Bad TrueType font."
argument_list|)
throw|;
block|}
block|}
comment|// Ask FontBox to parse the file for us
name|TrueTypeFont
name|font
decl_stmt|;
name|TTFParser
name|parser
init|=
operator|new
name|TTFParser
argument_list|()
decl_stmt|;
if|if
condition|(
name|tis
operator|!=
literal|null
operator|&&
name|tis
operator|.
name|hasFile
argument_list|()
condition|)
block|{
name|font
operator|=
name|parser
operator|.
name|parseTTF
argument_list|(
name|tis
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|font
operator|=
name|parser
operator|.
name|parseTTF
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
comment|// Report the details of the font
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|TYPE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|font
operator|.
name|getHeader
argument_list|()
operator|.
name|getCreated
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|font
operator|.
name|getHeader
argument_list|()
operator|.
name|getModified
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
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
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

