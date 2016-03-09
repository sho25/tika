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
name|Properties
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
comment|/**  * Abstract base class for new parsers. This method implements the old  * deprecated parse method so subclasses won't have to.  *  * @since Apache Tika 0.10  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractParser
implements|implements
name|ConfigurableParser
block|{
comment|/**      * Configuration supplied at runtime      */
specifier|protected
name|ParseContext
name|context
decl_stmt|;
comment|/**      * Serial version UID.      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|7186985395903074255L
decl_stmt|;
comment|/**      * Calls the      * {@link Parser#parse(InputStream, ContentHandler, Metadata, ParseContext)}      * method with an empty {@link ParseContext}. This method exists as a      * leftover from Tika 0.x when the three-argument parse() method still      * existed in the {@link Parser} interface. No new code should call this      * method anymore, it's only here for backwards compatibility.      *      * @deprecated use the {@link Parser#parse(InputStream, ContentHandler, Metadata, ParseContext)} method instead      */
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
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * called by the framework to supply runtime parameters which may be      * required for initialization      * @param context the parser context at runtime      * @since Apache Tika 1.13      */
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|TikaException
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
block|}
end_class

end_unit

