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
name|sax
package|;
end_package

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

begin_comment
comment|/**  * Content handler decorator that prevents the {@link #startDocument()}  * and {@link #endDocument()} events from reaching the decorated handler.  * This is useful when you want to direct the results of parsing multiple  * different XML documents into a single target document without worrying  * about the {@link #startDocument()} and {@link #endDocument()} methods  * being called more than once.  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
comment|/**      * Created a decorator that prevents the given handler from      * receiving {@link #startDocument()} and {@link #endDocument()}      * events.      *      * @param handler the content handler to be decorated      */
specifier|public
name|EmbeddedContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
comment|/**      * Ignored.      */
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
block|{     }
comment|/**      * Ignored.      */
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
block|{     }
block|}
end_class

end_unit

