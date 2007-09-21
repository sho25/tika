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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|LiusConfig
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
name|LiusException
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
name|MimeTypesUtils
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
name|jdom
operator|.
name|JDOMException
import|;
end_import

begin_comment
comment|/**  * Factory class. Build parser from xml config file.  *   * @author Rida Benjelloun (ridabenjelloun@apache.org)  */
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
comment|/**      * Build parser from file and Lius config object      */
specifier|public
specifier|static
name|Parser
name|getParser
parameter_list|(
name|File
name|file
parameter_list|,
name|LiusConfig
name|tc
parameter_list|)
throws|throws
name|IOException
throws|,
name|LiusException
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read input file "
operator|+
name|file
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|mimeType
init|=
name|MimeTypesUtils
operator|.
name|getMimeType
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|ParserConfig
name|pc
init|=
name|tc
operator|.
name|getParserConfig
argument_list|(
name|mimeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|pc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|LiusException
argument_list|(
literal|"No ParserConfig available for mime-type '"
operator|+
name|mimeType
operator|+
literal|"'"
operator|+
literal|" for file "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|className
init|=
name|pc
operator|.
name|getParserClass
argument_list|()
decl_stmt|;
name|Parser
name|parser
init|=
literal|null
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|parserClass
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Loading parser class = "
operator|+
name|className
operator|+
literal|" MimeType = "
operator|+
name|mimeType
operator|+
literal|" for file "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|parserClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|(
name|Parser
operator|)
name|parserClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
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
catch|catch
parameter_list|(
name|InstantiationException
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
catch|catch
parameter_list|(
name|IllegalAccessException
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
name|parser
operator|.
name|setMimeType
argument_list|(
name|mimeType
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setNamespace
argument_list|(
name|pc
operator|.
name|getNameSpace
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setContents
argument_list|(
name|pc
operator|.
name|getContents
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|parser
return|;
block|}
comment|/**      * Build parser from string file path and Lius config object      */
specifier|public
specifier|static
name|Parser
name|getParser
parameter_list|(
name|String
name|str
parameter_list|,
name|LiusConfig
name|tc
parameter_list|)
throws|throws
name|IOException
throws|,
name|LiusException
block|{
return|return
name|getParser
argument_list|(
operator|new
name|File
argument_list|(
name|str
argument_list|)
argument_list|,
name|tc
argument_list|)
return|;
block|}
comment|/**      * Build parser from string file path and Lius config file path      */
specifier|public
specifier|static
name|Parser
name|getParser
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|tcPath
parameter_list|)
throws|throws
name|IOException
throws|,
name|LiusException
throws|,
name|JDOMException
block|{
name|LiusConfig
name|tc
init|=
name|LiusConfig
operator|.
name|getInstance
argument_list|(
name|tcPath
argument_list|)
decl_stmt|;
return|return
name|getParser
argument_list|(
operator|new
name|File
argument_list|(
name|str
argument_list|)
argument_list|,
name|tc
argument_list|)
return|;
block|}
comment|/**      * Build parser from file and Lius config file path      */
specifier|public
specifier|static
name|Parser
name|getParser
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|tcPath
parameter_list|)
throws|throws
name|IOException
throws|,
name|LiusException
throws|,
name|JDOMException
block|{
name|LiusConfig
name|tc
init|=
name|LiusConfig
operator|.
name|getInstance
argument_list|(
name|tcPath
argument_list|)
decl_stmt|;
return|return
name|getParser
argument_list|(
name|file
argument_list|,
name|tc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

