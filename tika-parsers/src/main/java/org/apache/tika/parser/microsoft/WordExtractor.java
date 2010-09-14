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
name|microsoft
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|util
operator|.
name|List
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
name|hwpf
operator|.
name|HWPFDocument
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
name|hwpf
operator|.
name|HWPFOldDocument
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
name|hwpf
operator|.
name|OldWordFileFormatException
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
name|hwpf
operator|.
name|extractor
operator|.
name|Word6Extractor
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
name|hwpf
operator|.
name|model
operator|.
name|PicturesTable
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
name|hwpf
operator|.
name|usermodel
operator|.
name|Picture
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
name|poifs
operator|.
name|filesystem
operator|.
name|DirectoryEntry
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
name|poifs
operator|.
name|filesystem
operator|.
name|Entry
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
name|poifs
operator|.
name|filesystem
operator|.
name|POIFSFileSystem
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
name|SAXException
import|;
end_import

begin_class
specifier|public
class|class
name|WordExtractor
extends|extends
name|AbstractPOIFSExtractor
block|{
specifier|public
name|WordExtractor
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|parse
parameter_list|(
name|POIFSFileSystem
name|filesystem
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|HWPFDocument
name|document
decl_stmt|;
try|try
block|{
name|document
operator|=
operator|new
name|HWPFDocument
argument_list|(
name|filesystem
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OldWordFileFormatException
name|e
parameter_list|)
block|{
name|parseWord6
argument_list|(
name|filesystem
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
return|return;
block|}
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|extractor
operator|.
name|WordExtractor
name|wordExtractor
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|extractor
operator|.
name|WordExtractor
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|addTextIfAny
argument_list|(
name|xhtml
argument_list|,
literal|"header"
argument_list|,
name|wordExtractor
operator|.
name|getHeaderText
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|paragraph
range|:
name|wordExtractor
operator|.
name|getParagraphText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|paragraph
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|paragraph
range|:
name|wordExtractor
operator|.
name|getFootnoteText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|paragraph
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|paragraph
range|:
name|wordExtractor
operator|.
name|getCommentsText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|paragraph
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|paragraph
range|:
name|wordExtractor
operator|.
name|getEndnoteText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|paragraph
argument_list|)
expr_stmt|;
block|}
name|addTextIfAny
argument_list|(
name|xhtml
argument_list|,
literal|"footer"
argument_list|,
name|wordExtractor
operator|.
name|getFooterText
argument_list|()
argument_list|)
expr_stmt|;
comment|// Handle any embeded images
name|PicturesTable
name|pictureTable
init|=
name|document
operator|.
name|getPicturesTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|pictureTable
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Picture
argument_list|>
name|pictures
init|=
operator|(
name|List
argument_list|<
name|Picture
argument_list|>
operator|)
name|pictureTable
operator|.
name|getAllPictures
argument_list|()
decl_stmt|;
comment|// TODO Generics fixed in newer version
for|for
control|(
name|Picture
name|picture
range|:
name|pictures
control|)
block|{
comment|// TODO When we have upgraded POI, we can use this code instead
comment|//String mimeType = picture.getMimeType();
comment|// This code is cut'n'paste from a newer version of POI
name|String
name|mimeType
init|=
literal|"image/unknown"
decl_stmt|;
name|String
name|extension
init|=
name|picture
operator|.
name|suggestFileExtension
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"jpg"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|mimeType
operator|=
literal|"image/jpeg"
expr_stmt|;
block|}
if|if
condition|(
literal|"png"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|mimeType
operator|=
literal|"image/png"
expr_stmt|;
block|}
if|if
condition|(
literal|"gif"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|mimeType
operator|=
literal|"image/gif"
expr_stmt|;
block|}
if|if
condition|(
literal|"bmp"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|mimeType
operator|=
literal|"image/bmp"
expr_stmt|;
block|}
if|if
condition|(
literal|"tiff"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|mimeType
operator|=
literal|"image/tiff"
expr_stmt|;
block|}
if|if
condition|(
literal|"wmf"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|mimeType
operator|=
literal|"image/x-wmf"
expr_stmt|;
block|}
if|if
condition|(
literal|"emf"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|mimeType
operator|=
literal|"image/x-emf"
expr_stmt|;
block|}
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|picture
operator|.
name|getContent
argument_list|()
argument_list|)
decl_stmt|;
name|handleEmbeddedResource
argument_list|(
name|stream
argument_list|,
literal|null
argument_list|,
name|mimeType
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Handle any embeded office documents
try|try
block|{
name|DirectoryEntry
name|op
init|=
operator|(
name|DirectoryEntry
operator|)
name|filesystem
operator|.
name|getRoot
argument_list|()
operator|.
name|getEntry
argument_list|(
literal|"ObjectPool"
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|op
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
operator|&&
name|entry
operator|instanceof
name|DirectoryEntry
condition|)
block|{
name|handleEmbededOfficeDoc
argument_list|(
operator|(
name|DirectoryEntry
operator|)
name|entry
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{         }
block|}
comment|/**      * Outputs a section of text if the given text is non-empty.      *      * @param xhtml XHTML content handler      * @param section the class of the&lt;div/&gt; section emitted      * @param text text to be emitted, if any      * @throws SAXException if an error occurs      */
specifier|private
name|void
name|addTextIfAny
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|String
name|section
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
name|section
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|parseWord6
parameter_list|(
name|POIFSFileSystem
name|filesystem
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|HWPFOldDocument
name|doc
init|=
operator|new
name|HWPFOldDocument
argument_list|(
name|filesystem
argument_list|)
decl_stmt|;
name|Word6Extractor
name|extractor
init|=
operator|new
name|Word6Extractor
argument_list|(
name|doc
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|extractor
operator|.
name|getParagraphText
argument_list|()
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

