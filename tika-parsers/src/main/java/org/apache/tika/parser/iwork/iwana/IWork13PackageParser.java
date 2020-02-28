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
name|iwork
operator|.
name|iwana
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
name|util
operator|.
name|Arrays
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
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|archivers
operator|.
name|zip
operator|.
name|ZipFile
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

begin_class
specifier|public
class|class
name|IWork13PackageParser
extends|extends
name|AbstractParser
block|{
specifier|public
enum|enum
name|IWork13DocumentType
block|{
name|KEYNOTE13
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.apple.keynote.13"
argument_list|)
argument_list|)
block|,
name|NUMBERS13
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.apple.numbers.13"
argument_list|)
argument_list|)
block|,
name|PAGES13
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.apple.pages.13"
argument_list|)
argument_list|)
block|,
name|UNKNOWN13
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.apple.unknown.13"
argument_list|)
argument_list|)
block|;
specifier|private
specifier|final
name|MediaType
name|mediaType
decl_stmt|;
name|IWork13DocumentType
parameter_list|(
name|MediaType
name|mediaType
parameter_list|)
block|{
name|this
operator|.
name|mediaType
operator|=
name|mediaType
expr_stmt|;
block|}
specifier|public
name|MediaType
name|getType
parameter_list|()
block|{
return|return
name|mediaType
return|;
block|}
specifier|public
specifier|static
name|MediaType
name|detect
parameter_list|(
name|ZipFile
name|zipFile
parameter_list|)
block|{
name|MediaType
name|type
init|=
literal|null
decl_stmt|;
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|entries
init|=
name|zipFile
operator|.
name|getEntries
argument_list|()
decl_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|ZipEntry
name|entry
init|=
name|entries
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|type
operator|=
name|IWork13DocumentType
operator|.
name|detectIfPossible
argument_list|(
name|entry
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
return|return
name|type
return|;
block|}
comment|// If we get here, we don't know what it is
return|return
name|UNKNOWN13
operator|.
name|getType
argument_list|()
return|;
block|}
comment|/**          * @return Specific type if this identifies one, otherwise null          */
specifier|public
specifier|static
name|MediaType
name|detectIfPossible
parameter_list|(
name|ZipEntry
name|entry
parameter_list|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|endsWith
argument_list|(
literal|".iwa"
argument_list|)
condition|)
return|return
literal|null
return|;
comment|// Is it a uniquely identifying filename?
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Index/MasterSlide.iwa"
argument_list|)
operator|||
name|name
operator|.
name|startsWith
argument_list|(
literal|"Index/MasterSlide-"
argument_list|)
condition|)
block|{
return|return
name|KEYNOTE13
operator|.
name|getType
argument_list|()
return|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Index/Slide.iwa"
argument_list|)
operator|||
name|name
operator|.
name|startsWith
argument_list|(
literal|"Index/Slide-"
argument_list|)
condition|)
block|{
return|return
name|KEYNOTE13
operator|.
name|getType
argument_list|()
return|;
block|}
comment|// Is it the main document?
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"Index/Document.iwa"
argument_list|)
condition|)
block|{
comment|// TODO Decode the snappy stream, and check for the Message Type
comment|// =     2 (TN::SheetArchive), it is a numbers file;
comment|// = 10000 (TP::DocumentArchive), that's a pages file
block|}
comment|// Unknown
return|return
literal|null
return|;
block|}
block|}
comment|/**      * All iWork 13 files contain this, so we can detect based on it      */
specifier|public
specifier|final
specifier|static
name|String
name|IWORK13_COMMON_ENTRY
init|=
literal|"Metadata/BuildVersionHistory.plist"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Set
argument_list|<
name|MediaType
argument_list|>
name|supportedTypes
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|IWork13DocumentType
operator|.
name|KEYNOTE13
operator|.
name|getType
argument_list|()
argument_list|,
name|IWork13DocumentType
operator|.
name|NUMBERS13
operator|.
name|getType
argument_list|()
argument_list|,
name|IWork13DocumentType
operator|.
name|PAGES13
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
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
name|supportedTypes
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
comment|// Open the Zip stream
comment|// Use a File if we can, and an already open zip is even better
name|ZipFile
name|zipFile
init|=
literal|null
decl_stmt|;
name|ZipInputStream
name|zipStream
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|stream
operator|instanceof
name|TikaInputStream
condition|)
block|{
name|TikaInputStream
name|tis
init|=
operator|(
name|TikaInputStream
operator|)
name|stream
decl_stmt|;
name|Object
name|container
init|=
operator|(
operator|(
name|TikaInputStream
operator|)
name|stream
operator|)
operator|.
name|getOpenContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|container
operator|instanceof
name|ZipFile
condition|)
block|{
name|zipFile
operator|=
operator|(
name|ZipFile
operator|)
name|container
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tis
operator|.
name|hasFile
argument_list|()
condition|)
block|{
name|zipFile
operator|=
operator|new
name|ZipFile
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
name|zipStream
operator|=
operator|new
name|ZipInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|zipStream
operator|=
operator|new
name|ZipInputStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
comment|// For now, just detect
name|MediaType
name|type
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|zipFile
operator|!=
literal|null
condition|)
block|{
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|entries
init|=
name|zipFile
operator|.
name|getEntries
argument_list|()
decl_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|ZipEntry
name|entry
init|=
name|entries
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|IWork13DocumentType
operator|.
name|detectIfPossible
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|ZipEntry
name|entry
init|=
name|zipStream
operator|.
name|getNextEntry
argument_list|()
decl_stmt|;
while|while
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|IWork13DocumentType
operator|.
name|detectIfPossible
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
name|entry
operator|=
name|zipStream
operator|.
name|getNextEntry
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

