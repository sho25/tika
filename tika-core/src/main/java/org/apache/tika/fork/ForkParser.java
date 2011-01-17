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
name|fork
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|AutoDetectParser
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
name|DelegatingParser
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
name|Parser
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
name|WriteOutContentHandler
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
name|ForkParser
extends|extends
name|DelegatingParser
block|{
specifier|private
specifier|final
name|ClassLoader
name|loader
decl_stmt|;
specifier|private
specifier|final
name|Queue
argument_list|<
name|ForkClient
argument_list|>
name|pool
init|=
operator|new
name|LinkedList
argument_list|<
name|ForkClient
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|poolSize
init|=
literal|5
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|ForkParser
name|parser
init|=
operator|new
name|ForkParser
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|WriteOutContentHandler
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|ForkParser
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
comment|/**      *       */
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
name|ForkClient
name|client
init|=
name|acquireClient
argument_list|()
decl_stmt|;
try|try
block|{
name|client
operator|.
name|parse
argument_list|(
name|getDelegateParser
argument_list|(
name|context
argument_list|)
argument_list|,
name|stream
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|releaseClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|ForkClient
name|client
range|:
name|pool
control|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|pool
operator|.
name|clear
argument_list|()
expr_stmt|;
name|poolSize
operator|=
literal|0
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|ForkClient
name|acquireClient
parameter_list|()
throws|throws
name|IOException
block|{
name|ForkClient
name|client
init|=
name|pool
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
name|client
operator|=
operator|new
name|ForkClient
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|releaseClient
parameter_list|(
name|ForkClient
name|client
parameter_list|)
block|{
if|if
condition|(
name|pool
operator|.
name|size
argument_list|()
operator|<
name|poolSize
condition|)
block|{
name|pool
operator|.
name|offer
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

