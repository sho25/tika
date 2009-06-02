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
comment|/**  * Base class for parser implementations that want to delegate parts of the  * task of parsing an input document to another parser. The default base  * class implementation simply delegates the entire parsing task to a dummy  * {@link EmptyParser} instance, but subclasses can implement more complex  * processing rules and a more complete delegate parser can be specified  * through the {@link #setDelegate(Parser)} method.  *<p>  * The Tika configuration mechanism also contains a way to automatically  * set the delegate parser of all configured delegating parsers  * implementations. This feature is most notably used by the  * {@link AutoDetectParser} class to make it the recursive target of all  * delegated parsing tasks.  *  * @since Apache Tika 0.4  */
end_comment

begin_class
specifier|public
class|class
name|DelegatingParser
implements|implements
name|Parser
block|{
comment|/**      * The parser to which parts of the parsing tasks are delegated.      */
specifier|private
specifier|transient
name|Parser
name|delegate
init|=
operator|new
name|EmptyParser
argument_list|()
decl_stmt|;
comment|/**      * Returns delegate parser instance.      *      * @return delegate parser      */
specifier|public
name|Parser
name|getDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
comment|/**      * Sets the delegate parser instance.      *      * @param delegate delegate parser      */
specifier|public
name|void
name|setDelegate
parameter_list|(
name|Parser
name|delegate
parameter_list|)
block|{
if|if
condition|(
name|delegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Delegate parser of "
operator|+
name|this
operator|+
literal|" can not be null"
argument_list|)
throw|;
block|}
else|else
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
block|}
comment|/**      * Parses the given document using the specified delegate parser.      * Subclasses should override this method with more complex delegation      * rules based on the structure of the input document. The default      * implementation simply delegates the entire parsing task to the      * specified delegate parser.      */
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
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
name|delegate
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

