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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|Content
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

begin_comment
comment|/**  * Decorator base class for the {@link Parser} interface. This class  * simply delegates all parsing calls to an underlying decorated parser  * instance. Subclasses can provide extra decoration by overriding the  * parse method.  */
end_comment

begin_class
specifier|public
class|class
name|ParserDecorator
implements|implements
name|Parser
block|{
comment|/**      * The decorated parser instance.      */
specifier|private
specifier|final
name|Parser
name|parser
decl_stmt|;
comment|/**      * Creates a decorator for the given parser.      *      * @param parser the parser instance to be decorated      */
specifier|public
name|ParserDecorator
parameter_list|(
name|Parser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
comment|/**      * Delegates the method call to the decorated parser. Subclasses should      * override this method (and use<code>super.parse()</code> to invoke      * the decorated parser) to implement extra decoration.      */
specifier|public
name|String
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Iterable
argument_list|<
name|Content
argument_list|>
name|contents
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
return|return
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|contents
argument_list|,
name|metadata
argument_list|)
return|;
block|}
block|}
end_class

end_unit

