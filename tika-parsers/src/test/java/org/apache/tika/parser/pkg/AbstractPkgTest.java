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
name|pkg
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
name|ArrayList
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
name|tika
operator|.
name|TikaTest
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
name|junit
operator|.
name|Before
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
comment|/**  * Parent class for all Package based Test cases  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPkgTest
extends|extends
name|TikaTest
block|{
specifier|protected
name|ParseContext
name|trackingContext
decl_stmt|;
specifier|protected
name|ParseContext
name|recursingContext
decl_stmt|;
specifier|protected
name|Parser
name|autoDetectParser
decl_stmt|;
specifier|protected
name|EmbeddedTrackingParser
name|tracker
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|tracker
operator|=
operator|new
name|EmbeddedTrackingParser
argument_list|()
expr_stmt|;
name|trackingContext
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|trackingContext
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|tracker
argument_list|)
expr_stmt|;
name|autoDetectParser
operator|=
operator|new
name|AutoDetectParser
argument_list|()
expr_stmt|;
name|recursingContext
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|recursingContext
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|autoDetectParser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
specifier|protected
specifier|static
class|class
name|EmbeddedTrackingParser
extends|extends
name|AbstractParser
block|{
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|filenames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|mediatypes
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|createdAts
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|modifiedAts
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|byte
index|[]
name|lastSeenStart
decl_stmt|;
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|filenames
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mediatypes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|createdAts
operator|.
name|clear
argument_list|()
expr_stmt|;
name|modifiedAts
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
comment|// Cheat!
return|return
operator|(
operator|new
name|AutoDetectParser
argument_list|()
operator|)
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
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
name|filenames
operator|.
name|add
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|mediatypes
operator|.
name|add
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|createdAts
operator|.
name|add
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|modifiedAts
operator|.
name|add
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|lastSeenStart
operator|=
operator|new
name|byte
index|[
literal|32
index|]
expr_stmt|;
name|stream
operator|.
name|read
argument_list|(
name|lastSeenStart
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

