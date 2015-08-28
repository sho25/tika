begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|batch
operator|.
name|builders
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|BasicContentHandlerFactory
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
name|ContentHandlerFactory
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
name|util
operator|.
name|XMLDOMUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Builds BasicContentHandler with type defined by attribute "basicHandlerType"  * with possible values: xml, html, text, body, ignore.  * Default is text.  *<p>  * Sets the writeLimit to the value of "writeLimit.  * Default is -1;  */
end_comment

begin_class
specifier|public
class|class
name|DefaultContentHandlerFactoryBuilder
implements|implements
name|IContentHandlerFactoryBuilder
block|{
annotation|@
name|Override
specifier|public
name|ContentHandlerFactory
name|build
parameter_list|(
name|Node
name|node
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|runtimeAttributes
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|XMLDOMUtil
operator|.
name|mapifyAttrs
argument_list|(
name|node
argument_list|,
name|runtimeAttributes
argument_list|)
decl_stmt|;
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
name|type
init|=
literal|null
decl_stmt|;
name|String
name|handlerTypeString
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"basicHandlerType"
argument_list|)
decl_stmt|;
name|type
operator|=
name|BasicContentHandlerFactory
operator|.
name|parseHandlerType
argument_list|(
name|handlerTypeString
argument_list|,
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|int
name|writeLimit
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|writeLimitString
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"writeLimit"
argument_list|)
decl_stmt|;
if|if
condition|(
name|writeLimitString
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writeLimit
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
literal|"writeLimit"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//swallow and default to -1
comment|//TODO: should we throw a RuntimeException?
block|}
block|}
return|return
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
name|writeLimit
argument_list|)
return|;
block|}
block|}
end_class

end_unit

