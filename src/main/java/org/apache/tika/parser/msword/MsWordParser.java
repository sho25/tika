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
name|msword
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|utils
operator|.
name|MSExtractor
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
name|utils
operator|.
name|RegexUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|MalformedPatternException
import|;
end_import

begin_comment
comment|/**  * Word parser  *   *   */
end_comment

begin_class
specifier|public
class|class
name|MsWordParser
extends|extends
name|Parser
block|{
specifier|private
name|MSExtractor
name|extractor
init|=
operator|new
name|WordExtractor
argument_list|()
decl_stmt|;
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
specifier|public
name|List
argument_list|<
name|Content
argument_list|>
name|getContents
parameter_list|()
block|{
if|if
condition|(
name|contentStr
operator|==
literal|null
condition|)
block|{
comment|// extractor
try|try
block|{
name|contentStr
operator|=
name|extractor
operator|.
name|extractText
argument_list|(
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Content
argument_list|>
name|ctt
init|=
name|super
operator|.
name|getContents
argument_list|()
decl_stmt|;
name|Iterator
name|i
init|=
name|ctt
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Content
name|ct
init|=
operator|(
name|Content
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|ct
operator|.
name|getTextSelect
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ct
operator|.
name|getTextSelect
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"fulltext"
argument_list|)
condition|)
block|{
name|ct
operator|.
name|setValue
argument_list|(
name|contentStr
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ct
operator|.
name|getRegexSelect
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|valuesLs
init|=
name|RegexUtils
operator|.
name|extract
argument_list|(
name|contentStr
argument_list|,
name|ct
operator|.
name|getRegexSelect
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|valuesLs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ct
operator|.
name|setValue
argument_list|(
name|valuesLs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ct
operator|.
name|setValues
argument_list|(
name|valuesLs
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedPatternException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ctt
return|;
block|}
block|}
end_class

end_unit

