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
name|image
operator|.
name|xmp
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
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|jempbox
operator|.
name|xmp
operator|.
name|XMPMetadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jempbox
operator|.
name|xmp
operator|.
name|XMPSchemaDublinCore
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
name|metadata
operator|.
name|TikaCoreProperties
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
name|InputSource
import|;
end_import

begin_class
specifier|public
class|class
name|JempboxExtractor
block|{
specifier|private
name|XMPPacketScanner
name|scanner
init|=
operator|new
name|XMPPacketScanner
argument_list|()
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
comment|// The XMP spec says it must be unicode, but for most file formats it specifies "must be encoded in UTF-8"
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_XMP_CHARSET
init|=
literal|"UTF-8"
decl_stmt|;
specifier|public
name|JempboxExtractor
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|file
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|ByteArrayOutputStream
name|xmpraw
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|scanner
operator|.
name|parse
argument_list|(
name|file
argument_list|,
name|xmpraw
argument_list|)
condition|)
block|{
return|return;
block|}
name|Reader
name|decoded
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|xmpraw
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
name|DEFAULT_XMP_CHARSET
argument_list|)
decl_stmt|;
try|try
block|{
name|XMPMetadata
name|xmp
init|=
name|XMPMetadata
operator|.
name|load
argument_list|(
operator|new
name|InputSource
argument_list|(
name|decoded
argument_list|)
argument_list|)
decl_stmt|;
name|XMPSchemaDublinCore
name|dc
init|=
name|xmp
operator|.
name|getDublinCoreSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|dc
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|dc
operator|.
name|getTitle
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|dc
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dc
operator|.
name|getDescription
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|,
name|dc
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dc
operator|.
name|getCreators
argument_list|()
operator|!=
literal|null
operator|&&
name|dc
operator|.
name|getCreators
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|,
name|joinCreators
argument_list|(
name|dc
operator|.
name|getCreators
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dc
operator|.
name|getSubjects
argument_list|()
operator|!=
literal|null
operator|&&
name|dc
operator|.
name|getSubjects
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|keywords
init|=
name|dc
operator|.
name|getSubjects
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|keywords
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|,
name|keywords
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// TODO should we set KEYWORDS too?
comment|// All tested photo managers set the same in Iptc.Application2.Keywords and Xmp.dc.subject
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Could not parse embedded XMP metadata. That's not a serious
comment|// problem, so we'll just ignore the issue for now.
comment|// TODO: Make error handling like this configurable.
block|}
block|}
specifier|protected
name|String
name|joinCreators
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|creators
parameter_list|)
block|{
if|if
condition|(
name|creators
operator|==
literal|null
operator|||
name|creators
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
if|if
condition|(
name|creators
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|creators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|StringBuffer
name|c
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|creators
control|)
block|{
name|c
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|c
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

