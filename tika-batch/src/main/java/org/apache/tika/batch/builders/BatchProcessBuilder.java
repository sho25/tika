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
name|Collections
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|batch
operator|.
name|BatchProcess
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
name|batch
operator|.
name|ConsumersManager
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
name|batch
operator|.
name|FileResource
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
name|batch
operator|.
name|FileResourceCrawler
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
name|batch
operator|.
name|Interrupter
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
name|batch
operator|.
name|StatusReporter
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
name|ClassLoaderUtil
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
comment|/**  * Builds a BatchProcessor from a combination of runtime arguments and the  * config file.  */
end_comment

begin_class
specifier|public
class|class
name|BatchProcessBuilder
block|{
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_QUEUE_SIZE
init|=
literal|1000
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|MAX_QUEUE_SIZE_KEY
init|=
literal|"maxQueueSize"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NUM_CONSUMERS_KEY
init|=
literal|"numConsumers"
decl_stmt|;
comment|/**      * Builds a BatchProcess from runtime arguments and a      * input stream of a configuration file.  With the exception of the QueueBuilder,      * the builders choose how to adjudicate between      * runtime arguments and the elements in the configuration file.      *<p/>      * This does not close the InputStream!      * @param is inputStream      * @param runtimeAttributes incoming runtime attributes      * @return batch process      * @throws java.io.IOException      */
specifier|public
name|BatchProcess
name|build
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|runtimeAttributes
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
return|return
name|build
argument_list|(
name|docElement
argument_list|,
name|runtimeAttributes
argument_list|)
return|;
block|}
comment|/**      * Builds a FileResourceBatchProcessor from runtime arguments and a      * document node of a configuration file.  With the exception of the QueueBuilder,      * the builders choose how to adjudicate between      * runtime arguments and the elements in the configuration file.      *      * @param docElement   document element of the xml config file      * @param incomingRuntimeAttributes runtime arguments      * @return FileResourceBatchProcessor      */
specifier|public
name|BatchProcess
name|build
parameter_list|(
name|Node
name|docElement
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|incomingRuntimeAttributes
parameter_list|)
block|{
comment|//key components
name|long
name|timeoutThresholdMillis
init|=
name|XMLDOMUtil
operator|.
name|getLong
argument_list|(
literal|"timeoutThresholdMillis"
argument_list|,
name|incomingRuntimeAttributes
argument_list|,
name|docElement
argument_list|)
decl_stmt|;
name|long
name|timeoutCheckPulseMillis
init|=
name|XMLDOMUtil
operator|.
name|getLong
argument_list|(
literal|"timeoutCheckPulseMillis"
argument_list|,
name|incomingRuntimeAttributes
argument_list|,
name|docElement
argument_list|)
decl_stmt|;
name|long
name|pauseOnEarlyTerminationMillis
init|=
name|XMLDOMUtil
operator|.
name|getLong
argument_list|(
literal|"pauseOnEarlyTerminationMillis"
argument_list|,
name|incomingRuntimeAttributes
argument_list|,
name|docElement
argument_list|)
decl_stmt|;
name|int
name|maxAliveTimeSeconds
init|=
name|XMLDOMUtil
operator|.
name|getInt
argument_list|(
literal|"maxAliveTimeSeconds"
argument_list|,
name|incomingRuntimeAttributes
argument_list|,
name|docElement
argument_list|)
decl_stmt|;
name|FileResourceCrawler
name|crawler
init|=
literal|null
decl_stmt|;
name|ConsumersManager
name|consumersManager
init|=
literal|null
decl_stmt|;
name|StatusReporter
name|reporter
init|=
literal|null
decl_stmt|;
name|Interrupter
name|interrupter
init|=
literal|null
decl_stmt|;
comment|/*          * TODO: This is a bit smelly.  NumConsumers needs to be used by the crawler          * and the consumers.  This copies the incomingRuntimeAttributes and then          * supplies the numConsumers from the commandline (if it exists) or from the config file          * At least this creates an unmodifiable defensive copy of incomingRuntimeAttributes...          */
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|runtimeAttributes
init|=
name|setNumConsumersInRuntimeAttributes
argument_list|(
name|docElement
argument_list|,
name|incomingRuntimeAttributes
argument_list|)
decl_stmt|;
comment|//build queue
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|queue
init|=
name|buildQueue
argument_list|(
name|docElement
argument_list|,
name|runtimeAttributes
argument_list|)
decl_stmt|;
name|NodeList
name|children
init|=
name|docElement
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Node
argument_list|>
name|keyNodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Node
argument_list|>
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
name|keyNodes
operator|.
name|put
argument_list|(
name|nodeName
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
comment|//build consumers
name|consumersManager
operator|=
name|buildConsumersManager
argument_list|(
name|keyNodes
operator|.
name|get
argument_list|(
literal|"consumers"
argument_list|)
argument_list|,
name|runtimeAttributes
argument_list|,
name|queue
argument_list|)
expr_stmt|;
comment|//build crawler
name|crawler
operator|=
name|buildCrawler
argument_list|(
name|queue
argument_list|,
name|keyNodes
operator|.
name|get
argument_list|(
literal|"crawler"
argument_list|)
argument_list|,
name|runtimeAttributes
argument_list|)
expr_stmt|;
name|reporter
operator|=
name|buildReporter
argument_list|(
name|crawler
argument_list|,
name|consumersManager
argument_list|,
name|keyNodes
operator|.
name|get
argument_list|(
literal|"reporter"
argument_list|)
argument_list|,
name|runtimeAttributes
argument_list|)
expr_stmt|;
name|interrupter
operator|=
name|buildInterrupter
argument_list|(
name|keyNodes
operator|.
name|get
argument_list|(
literal|"interrupter"
argument_list|)
argument_list|,
name|runtimeAttributes
argument_list|)
expr_stmt|;
name|BatchProcess
name|proc
init|=
operator|new
name|BatchProcess
argument_list|(
name|crawler
argument_list|,
name|consumersManager
argument_list|,
name|reporter
argument_list|,
name|interrupter
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeoutThresholdMillis
operator|>
operator|-
literal|1
condition|)
block|{
name|proc
operator|.
name|setTimeoutThresholdMillis
argument_list|(
name|timeoutThresholdMillis
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pauseOnEarlyTerminationMillis
operator|>
operator|-
literal|1
condition|)
block|{
name|proc
operator|.
name|setPauseOnEarlyTerminationMillis
argument_list|(
name|pauseOnEarlyTerminationMillis
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|timeoutCheckPulseMillis
operator|>
operator|-
literal|1
condition|)
block|{
name|proc
operator|.
name|setTimeoutCheckPulseMillis
argument_list|(
name|timeoutCheckPulseMillis
argument_list|)
expr_stmt|;
block|}
name|proc
operator|.
name|setMaxAliveTimeSeconds
argument_list|(
name|maxAliveTimeSeconds
argument_list|)
expr_stmt|;
return|return
name|proc
return|;
block|}
specifier|private
name|Interrupter
name|buildInterrupter
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
name|attrs
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
name|String
name|className
init|=
name|attrs
operator|.
name|get
argument_list|(
literal|"builderClass"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Need to specify class name in interrupter element"
argument_list|)
throw|;
block|}
name|InterrupterBuilder
name|builder
init|=
name|ClassLoaderUtil
operator|.
name|buildClass
argument_list|(
name|InterrupterBuilder
operator|.
name|class
argument_list|,
name|className
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|(
name|node
argument_list|,
name|runtimeAttributes
argument_list|)
return|;
block|}
specifier|private
name|StatusReporter
name|buildReporter
parameter_list|(
name|FileResourceCrawler
name|crawler
parameter_list|,
name|ConsumersManager
name|consumersManager
parameter_list|,
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
name|attrs
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
name|String
name|className
init|=
name|attrs
operator|.
name|get
argument_list|(
literal|"builderClass"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Need to specify class name in reporter element"
argument_list|)
throw|;
block|}
name|StatusReporterBuilder
name|builder
init|=
name|ClassLoaderUtil
operator|.
name|buildClass
argument_list|(
name|StatusReporterBuilder
operator|.
name|class
argument_list|,
name|className
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|(
name|crawler
argument_list|,
name|consumersManager
argument_list|,
name|node
argument_list|,
name|runtimeAttributes
argument_list|)
return|;
block|}
comment|/**      * numConsumers is needed by both the crawler and the consumers. This utility method      * is to be used to extract the number of consumers from a map of String key value pairs.      *<p>      * If the value is "default", not a parseable integer or has a value< 1,      * then<code>AbstractConsumersBuilder</code>'s<code>getDefaultNumConsumers()</code>      * @param attrs attributes from which to select the NUM_CONSUMERS_KEY      * @return number of consumers      */
specifier|public
specifier|static
name|int
name|getNumConsumers
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
parameter_list|)
block|{
name|String
name|nString
init|=
name|attrs
operator|.
name|get
argument_list|(
name|BatchProcessBuilder
operator|.
name|NUM_CONSUMERS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|nString
operator|==
literal|null
operator|||
name|nString
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
condition|)
block|{
return|return
name|AbstractConsumersBuilder
operator|.
name|getDefaultNumConsumers
argument_list|()
return|;
block|}
name|int
name|n
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|n
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|nString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
if|if
condition|(
name|n
operator|<
literal|1
condition|)
block|{
name|n
operator|=
name|AbstractConsumersBuilder
operator|.
name|getDefaultNumConsumers
argument_list|()
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|setNumConsumersInRuntimeAttributes
parameter_list|(
name|Node
name|docElement
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|incomingRuntimeAttributes
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|runtimeAttributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|incomingRuntimeAttributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|runtimeAttributes
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//if this is set at runtime use that value
if|if
condition|(
name|runtimeAttributes
operator|.
name|containsKey
argument_list|(
name|NUM_CONSUMERS_KEY
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|runtimeAttributes
argument_list|)
return|;
block|}
name|Node
name|ncNode
init|=
name|docElement
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"numConsumers"
argument_list|)
decl_stmt|;
name|int
name|numConsumers
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|numConsumersString
init|=
name|ncNode
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
try|try
block|{
name|numConsumers
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numConsumersString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//swallow and just use numConsumers
block|}
comment|//TODO: should we have a max range check?
if|if
condition|(
name|numConsumers
operator|<
literal|1
condition|)
block|{
name|numConsumers
operator|=
name|AbstractConsumersBuilder
operator|.
name|getDefaultNumConsumers
argument_list|()
expr_stmt|;
block|}
name|runtimeAttributes
operator|.
name|put
argument_list|(
name|NUM_CONSUMERS_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|numConsumers
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|runtimeAttributes
argument_list|)
return|;
block|}
comment|//tries to get maxQueueSize from main element
specifier|private
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|buildQueue
parameter_list|(
name|Node
name|docElement
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
name|int
name|maxQueueSize
init|=
name|DEFAULT_MAX_QUEUE_SIZE
decl_stmt|;
name|String
name|szString
init|=
name|runtimeAttributes
operator|.
name|get
argument_list|(
name|MAX_QUEUE_SIZE_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|szString
operator|==
literal|null
condition|)
block|{
name|Node
name|szNode
init|=
name|docElement
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
name|MAX_QUEUE_SIZE_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|szNode
operator|!=
literal|null
condition|)
block|{
name|szString
operator|=
name|szNode
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|szString
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|maxQueueSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|szString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
if|if
condition|(
name|maxQueueSize
operator|<
literal|0
condition|)
block|{
name|maxQueueSize
operator|=
name|DEFAULT_MAX_QUEUE_SIZE
expr_stmt|;
block|}
return|return
operator|new
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
argument_list|(
name|maxQueueSize
argument_list|)
return|;
block|}
specifier|private
name|ConsumersManager
name|buildConsumersManager
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
parameter_list|,
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|queue
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
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
name|String
name|className
init|=
name|attrs
operator|.
name|get
argument_list|(
literal|"builderClass"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Need to specify class name in consumers element"
argument_list|)
throw|;
block|}
name|AbstractConsumersBuilder
name|builder
init|=
name|ClassLoaderUtil
operator|.
name|buildClass
argument_list|(
name|AbstractConsumersBuilder
operator|.
name|class
argument_list|,
name|className
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|(
name|node
argument_list|,
name|runtimeAttributes
argument_list|,
name|queue
argument_list|)
return|;
block|}
specifier|private
name|FileResourceCrawler
name|buildCrawler
parameter_list|(
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|queue
parameter_list|,
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
name|attrs
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
name|String
name|className
init|=
name|attrs
operator|.
name|get
argument_list|(
literal|"builderClass"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Need to specify class name in crawler element"
argument_list|)
throw|;
block|}
name|ICrawlerBuilder
name|builder
init|=
name|ClassLoaderUtil
operator|.
name|buildClass
argument_list|(
name|ICrawlerBuilder
operator|.
name|class
argument_list|,
name|className
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|(
name|node
argument_list|,
name|runtimeAttributes
argument_list|,
name|queue
argument_list|)
return|;
block|}
block|}
end_class

end_unit
