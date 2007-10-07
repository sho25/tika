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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|tika
operator|.
name|config
operator|.
name|ParserConfig
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

begin_comment
comment|/**  * Factory class. Build parser from xml config file.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ParserFactory
block|{
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
specifier|static
name|Parser
name|getParser
parameter_list|(
name|ParserConfig
name|config
parameter_list|)
throws|throws
name|TikaException
block|{
name|String
name|className
init|=
name|config
operator|.
name|getParserClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|className
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Parser class name missing from ParserConfig."
argument_list|)
throw|;
block|}
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Loading parser class = "
operator|+
name|className
argument_list|)
expr_stmt|;
name|Parser
name|parser
init|=
operator|(
name|Parser
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
return|return
operator|new
name|ParserPostProcessor
argument_list|(
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Unable to instantiate parser: "
operator|+
name|className
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

