begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|opendocument
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
name|Parser
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * OpenOffice parser  */
end_comment

begin_class
specifier|public
class|class
name|OpenOfficeParser
implements|implements
name|Parser
block|{
specifier|private
name|Parser
name|meta
init|=
operator|new
name|OpenOfficeMetaParser
argument_list|()
decl_stmt|;
specifier|private
name|Parser
name|content
init|=
operator|new
name|OpenOfficeContentParser
argument_list|()
decl_stmt|;
specifier|public
name|Parser
name|getMetaParser
parameter_list|()
block|{
return|return
name|meta
return|;
block|}
specifier|public
name|void
name|setMetaParser
parameter_list|(
name|Parser
name|meta
parameter_list|)
block|{
name|this
operator|.
name|meta
operator|=
name|meta
expr_stmt|;
block|}
specifier|public
name|Parser
name|getContentParser
parameter_list|()
block|{
return|return
name|content
return|;
block|}
specifier|public
name|void
name|setContentParser
parameter_list|(
name|Parser
name|content
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|ZipInputStream
name|zip
init|=
operator|new
name|ZipInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|ZipEntry
name|entry
init|=
name|zip
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
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"mimetype"
argument_list|)
condition|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|zip
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ch
init|=
name|reader
operator|.
name|read
argument_list|()
init|;
name|ch
operator|!=
operator|-
literal|1
condition|;
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"meta.xml"
argument_list|)
condition|)
block|{
name|meta
operator|.
name|parse
argument_list|(
name|zip
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"content.xml"
argument_list|)
condition|)
block|{
name|content
operator|.
name|parse
argument_list|(
name|zip
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
name|entry
operator|=
name|zip
operator|.
name|getNextEntry
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

