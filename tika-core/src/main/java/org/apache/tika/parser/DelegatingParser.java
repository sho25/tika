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
comment|/**  * Base class for parser implementations that want to delegate parts of the  * task of parsing an input document to another parser. The delegate parser  * is looked up from the parsing context using the {@link Parser} class as  * the key.  *  * @since Apache Tika 0.4, major changes in Tika 0.5  */
end_comment

begin_class
specifier|public
class|class
name|DelegatingParser
extends|extends
name|AbstractParser
block|{
comment|/**      * Returns the parser instance to which parsing tasks should be delegated.      * The default implementation looks up the delegate parser from the given      * parse context, and uses an {@link EmptyParser} instance as a fallback.      * Subclasses can override this method to implement alternative delegation      * strategies.      *      * @since Apache Tika 0.7      * @param context parse context      * @return delegate parser      */
specifier|protected
name|Parser
name|getDelegateParser
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|get
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|EmptyParser
operator|.
name|INSTANCE
argument_list|)
return|;
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
return|return
name|getDelegateParser
argument_list|(
name|context
argument_list|)
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
return|;
block|}
comment|/**      * Looks up the delegate parser from the parsing context and      * delegates the parse operation to it. If a delegate parser is not      * found, then an empty XHTML document is returned.      *<p>      * Subclasses should override this method to parse the top level      * structure of the given document stream. Parsed sub-streams can      * be passed to this base class method to be parsed by the configured      * delegate parser.      */
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
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
name|getDelegateParser
argument_list|(
name|context
argument_list|)
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

