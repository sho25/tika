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
name|chm
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|chm
operator|.
name|accessor
operator|.
name|DirectoryListingEntry
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
name|chm
operator|.
name|core
operator|.
name|ChmExtractor
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
name|HtmlParser
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
name|BodyContentHandler
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
comment|/**  * Extracts text and metadata from chm file  *   */
end_comment

begin_class
specifier|public
class|class
name|CHMDocumentInformation
block|{
comment|/* Class members */
specifier|private
name|ChmExtractor
name|chmExtractor
init|=
literal|null
decl_stmt|;
comment|/**      * Loads chm file as input stream and returns a new instance of chm doc info      *       * @param is      *            InputStream      *       * @return chm document information      * @throws TikaException       * @throws IOException       */
specifier|public
specifier|static
name|CHMDocumentInformation
name|load
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
return|return
operator|new
name|CHMDocumentInformation
argument_list|()
operator|.
name|getInstance
argument_list|(
name|is
argument_list|)
return|;
block|}
comment|/**      * Returns instance of chm document information      *       * @param is      *            InputStream      *       * @return      * @throws TikaException       * @throws IOException       */
specifier|private
name|CHMDocumentInformation
name|getInstance
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|setChmExtractor
argument_list|(
operator|new
name|ChmExtractor
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Appends extracted data from chm listing entries      *       * @return extracted content of chm      */
specifier|private
name|String
name|getContent
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|DirectoryListingEntry
name|entry
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DirectoryListingEntry
argument_list|>
name|it
init|=
name|getChmExtractor
argument_list|()
operator|.
name|getChmDirList
argument_list|()
operator|.
name|getDirectoryListingEntryList
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
try|try
block|{
name|entry
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|isRightEntry
argument_list|(
name|entry
argument_list|)
condition|)
block|{
name|byte
index|[]
index|[]
name|tmp
init|=
name|getChmExtractor
argument_list|()
operator|.
name|extractChmEntry
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmp
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|extract
argument_list|(
name|tmp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
comment|//ignore
block|}
comment|// catch (IOException e) {//Pushback exception from tagsoup
comment|// System.err.println(e.getMessage());
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Checks if an entry is a html or not.      *       * @param entry      *            chm directory listing entry      *       * @return boolean      */
specifier|private
name|boolean
name|isRightEntry
parameter_list|(
name|DirectoryListingEntry
name|entry
parameter_list|)
block|{
return|return
operator|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".html"
argument_list|)
operator|||
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".htm"
argument_list|)
operator|)
return|;
block|}
comment|/**      * Returns chm extractor      *       * @return chmExtractor      */
specifier|private
name|ChmExtractor
name|getChmExtractor
parameter_list|()
block|{
return|return
name|chmExtractor
return|;
block|}
comment|/**      * Sets a chm extractor      *       * @param chmExtractor      */
specifier|private
name|void
name|setChmExtractor
parameter_list|(
name|ChmExtractor
name|chmExtractor
parameter_list|)
block|{
name|this
operator|.
name|chmExtractor
operator|=
name|chmExtractor
expr_stmt|;
block|}
comment|/**      * Returns chm metadata      *       * @param metadata      *       * @throws TikaException      * @throws IOException      */
specifier|public
name|void
name|getCHMDocInformation
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
if|if
condition|(
name|getChmExtractor
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|/* Checking if file is a chm, done during creating chmItsf header */
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/x-chm"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"unknown"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns extracted text from chm file      *       * @return text      *       * @throws TikaException      */
specifier|public
name|String
name|getText
parameter_list|()
throws|throws
name|TikaException
block|{
return|return
name|getContent
argument_list|()
return|;
block|}
comment|/**      * Extracts data from byte[][]      *       * @param byteObject      * @return      * @throws IOException      * @throws SAXException      */
specifier|private
name|String
name|extract
parameter_list|(
name|byte
index|[]
index|[]
name|byteObject
parameter_list|)
block|{
comment|// throws IOException
name|StringBuilder
name|wBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|HtmlParser
name|htmlParser
init|=
operator|new
name|HtmlParser
argument_list|()
decl_stmt|;
name|BodyContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// -1
name|ParseContext
name|parser
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|byteObject
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|byteObject
index|[
name|i
index|]
argument_list|)
expr_stmt|;
try|try
block|{
name|htmlParser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|wBuf
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|byteObject
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|//                    System.err.println("\n"
comment|//                            + CHMDocumentInformation.class.getName()
comment|//                            + " extract " + e.getMessage());
block|}
finally|finally
block|{
name|wBuf
operator|.
name|append
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//
comment|// Pushback overflow from tagsoup
block|}
return|return
name|wBuf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{      }
block|}
end_class

end_unit

