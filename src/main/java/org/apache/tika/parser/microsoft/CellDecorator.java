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
name|microsoft
package|;
end_package

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
name|XHTMLContentHandler
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
comment|/**  * Cell decorator.  */
end_comment

begin_class
specifier|public
class|class
name|CellDecorator
implements|implements
name|Cell
block|{
specifier|private
specifier|final
name|Cell
name|cell
decl_stmt|;
specifier|public
name|CellDecorator
parameter_list|(
name|Cell
name|cell
parameter_list|)
block|{
name|this
operator|.
name|cell
operator|=
name|cell
expr_stmt|;
block|}
specifier|public
name|void
name|render
parameter_list|(
name|XHTMLContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
block|{
name|cell
operator|.
name|render
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

