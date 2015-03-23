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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|Locale
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Options
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
name|Document
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
name|NamedNodeMap
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
comment|/**  * Reads configurable options from a config file and returns org.apache.commons.cli.Options  * object to be used in commandline parser.  This allows users and developers to set  * which options should be made available via the commandline.  */
end_comment

begin_class
specifier|public
class|class
name|CommandLineParserBuilder
block|{
specifier|public
name|Options
name|build
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
literal|null
decl_stmt|;
name|DocumentBuilderFactory
name|fact
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|docBuilder
init|=
literal|null
decl_stmt|;
try|try
block|{
name|docBuilder
operator|=
name|fact
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
name|doc
operator|=
name|docBuilder
operator|.
name|parse
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Node
name|docElement
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|docElement
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|commandlineNode
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
continue|continue;
block|}
name|String
name|nodeName
init|=
name|child
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|equals
argument_list|(
literal|"commandline"
argument_list|)
condition|)
block|{
name|commandlineNode
operator|=
name|child
expr_stmt|;
break|break;
block|}
block|}
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
if|if
condition|(
name|commandlineNode
operator|==
literal|null
condition|)
block|{
return|return
name|options
return|;
block|}
name|NodeList
name|optionNodes
init|=
name|commandlineNode
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|optionNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|optionNode
init|=
name|optionNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|optionNode
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
continue|continue;
block|}
name|Option
name|opt
init|=
name|buildOption
argument_list|(
name|optionNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|!=
literal|null
condition|)
block|{
name|options
operator|.
name|addOption
argument_list|(
name|opt
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|options
return|;
block|}
specifier|private
name|Option
name|buildOption
parameter_list|(
name|Node
name|optionNode
parameter_list|)
block|{
name|NamedNodeMap
name|map
init|=
name|optionNode
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|String
name|opt
init|=
name|getString
argument_list|(
name|map
argument_list|,
literal|"opt"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|description
init|=
name|getString
argument_list|(
name|map
argument_list|,
literal|"description"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|longOpt
init|=
name|getString
argument_list|(
name|map
argument_list|,
literal|"longOpt"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|boolean
name|isRequired
init|=
name|getBoolean
argument_list|(
name|map
argument_list|,
literal|"required"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|hasArg
init|=
name|getBoolean
argument_list|(
name|map
argument_list|,
literal|"hasArg"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|description
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must specify at least option and description"
argument_list|)
throw|;
block|}
name|Option
name|option
init|=
operator|new
name|Option
argument_list|(
name|opt
argument_list|,
name|description
argument_list|)
decl_stmt|;
if|if
condition|(
name|longOpt
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|option
operator|.
name|setLongOpt
argument_list|(
name|longOpt
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isRequired
condition|)
block|{
name|option
operator|.
name|setRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasArg
condition|)
block|{
name|option
operator|.
name|setArgs
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|option
return|;
block|}
specifier|private
name|boolean
name|getBoolean
parameter_list|(
name|NamedNodeMap
name|map
parameter_list|,
name|String
name|opt
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
name|Node
name|n
init|=
name|map
operator|.
name|getNamedItem
argument_list|(
name|opt
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
if|if
condition|(
name|n
operator|.
name|getNodeValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
if|if
condition|(
name|n
operator|.
name|getNodeValue
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|n
operator|.
name|getNodeValue
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|defaultValue
return|;
block|}
specifier|private
name|String
name|getString
parameter_list|(
name|NamedNodeMap
name|map
parameter_list|,
name|String
name|opt
parameter_list|,
name|String
name|defaultVal
parameter_list|)
block|{
name|Node
name|n
init|=
name|map
operator|.
name|getNamedItem
argument_list|(
name|opt
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
return|return
name|defaultVal
return|;
block|}
name|String
name|value
init|=
name|n
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultVal
return|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

