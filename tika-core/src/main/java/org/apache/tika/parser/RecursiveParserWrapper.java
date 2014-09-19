begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|io
operator|.
name|FilenameUtils
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
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Property
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
name|TikaMetadataKeys
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
name|mime
operator|.
name|MediaType
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
import|;
end_import

begin_comment
comment|/**  * This is a helper class that wraps a parser in a recursive handler.  * It takes care of setting the embedded parser in the ParseContext   * and handling the embedded path calculations.  *<p>  * After parsing a document, call getMetadata() to retrieve a list of   * Metadata objects, one for each embedded resource.  The first item  * in the list will contain the Metadata for the outer container file.  *<p>  * Content can also be extracted and stored in the {@link #TIKA_CONTENT} field  * of a Metadata object.  Select the type of content to be stored  * at initialization.  *<p>  * If a WriteLimitReachedException is encountered, the wrapper will stop  * processing the current resource, and it will not process  * any of the child resources for the given resource.  However, it will try to   * parse as much as it can.  If a WLRE is reached in the parent document,   * no child resources will be parsed.  *<p>  * The implementation is based on Jukka's RecursiveMetadataParser  * and Nick's additions. See:   *<a href="http://wiki.apache.org/tika/RecursiveMetadata#Jukka.27s_RecursiveMetadata_Parser">RecursiveMetadataParser</a>.  *<p>  * Note that this wrapper holds all data in memory and is not appropriate  * for files with content too large to be held in memory.  *<p>  * Note, too, that this wrapper is not thread safe because it stores state.    * The client must initialize a new wrapper for each thread, and the client  * is responsible for calling {@link #reset()} after each parse.  *<p>  * The unit tests for this class are in the tika-parsers module.  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|RecursiveParserWrapper
implements|implements
name|Parser
block|{
comment|/**      * Generated serial version      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|9086536568120690938L
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TIKA_PREFIX
init|=
literal|"tika:"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TIKA_EXCEPTION_PREFIX
init|=
literal|"tika_ex:"
decl_stmt|;
comment|//move this to TikaCoreProperties?
specifier|public
specifier|final
specifier|static
name|Property
name|TIKA_CONTENT
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|TIKA_PREFIX
operator|+
literal|"content"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|Property
name|PARSE_TIME_MILLIS
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|TIKA_PREFIX
operator|+
literal|"parse_time_millis"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|Property
name|WRITE_LIMIT_REACHED
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|TIKA_EXCEPTION_PREFIX
operator|+
literal|"write_limit_reached"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|Property
name|EMBEDDED_RESOURCE_LIMIT_REACHED
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|TIKA_EXCEPTION_PREFIX
operator|+
literal|"embedded_resource_limit_reached"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|Property
name|PARSE_EXCEPTION
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
name|TIKA_EXCEPTION_PREFIX
operator|+
literal|"parse_exception"
argument_list|)
decl_stmt|;
comment|//move this to TikaCoreProperties?
specifier|public
specifier|final
specifier|static
name|Property
name|EMBEDDED_RESOURCE_PATH
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|TIKA_PREFIX
operator|+
literal|"embedded_resource_path"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Parser
name|wrappedParser
decl_stmt|;
specifier|private
specifier|final
name|ContentHandlerFactory
name|contentHandlerFactory
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadatas
init|=
operator|new
name|LinkedList
argument_list|<
name|Metadata
argument_list|>
argument_list|()
decl_stmt|;
comment|//used in naming embedded resources that don't have a name.
specifier|private
name|int
name|unknownCount
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|maxEmbeddedResources
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|boolean
name|hitMaxEmbeddedResources
init|=
literal|false
decl_stmt|;
specifier|public
name|RecursiveParserWrapper
parameter_list|(
name|Parser
name|wrappedParser
parameter_list|,
name|ContentHandlerFactory
name|contentHandlerFactory
parameter_list|)
block|{
name|this
operator|.
name|wrappedParser
operator|=
name|wrappedParser
expr_stmt|;
name|this
operator|.
name|contentHandlerFactory
operator|=
name|contentHandlerFactory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|wrappedParser
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
return|;
block|}
comment|/**      * Acts like a regular parser except it ignores the ContentHandler      * and it automatically sets/overwrites the embedded Parser in the       * ParseContext object.      *<p>      * To retrieve the results of the parse, use {@link #getMetadata()}.      *<p>      * Make sure to call {@link #reset()} after each parse.      */
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|ignore
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|String
name|name
init|=
name|getResourceName
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|EmbeddedParserDecorator
name|decorator
init|=
operator|new
name|EmbeddedParserDecorator
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|decorator
argument_list|)
expr_stmt|;
name|ContentHandler
name|localHandler
init|=
name|contentHandlerFactory
operator|.
name|getNewContentHandler
argument_list|()
decl_stmt|;
name|long
name|started
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
try|try
block|{
name|wrappedParser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|localHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|boolean
name|wlr
init|=
name|isWriteLimitReached
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|wlr
operator|==
literal|false
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|metadata
operator|.
name|set
argument_list|(
name|WRITE_LIMIT_REACHED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|long
name|elapsedMillis
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|started
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PARSE_TIME_MILLIS
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|elapsedMillis
argument_list|)
argument_list|)
expr_stmt|;
name|addContent
argument_list|(
name|localHandler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
if|if
condition|(
name|hitMaxEmbeddedResources
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|EMBEDDED_RESOURCE_LIMIT_REACHED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|metadatas
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|deepCopy
argument_list|(
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      *       * The first element in the returned list represents the       * data from the outer container file.  There is no guarantee      * about the ordering of the list after that.      *       * @return list of Metadata objects that were gathered during the parse      */
specifier|public
name|List
argument_list|<
name|Metadata
argument_list|>
name|getMetadata
parameter_list|()
block|{
return|return
name|metadatas
return|;
block|}
comment|/**      * Set the maximum number of embedded resources to store.      * If the max is hit during parsing, the {@link #EMBEDDED_RESOURCE_LIMIT_REACHED}      * property will be added to the container document's Metadata.      *       *<p>      * If this value is< 0 (the default), the wrapper will store all Metadata.      *       * @param max maximum number of embedded resources to store      */
specifier|public
name|void
name|setMaxEmbeddedResources
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|maxEmbeddedResources
operator|=
name|max
expr_stmt|;
block|}
comment|/**      * This clears the metadata list and resets {@link #unknownCount} and      * {@link #hitMaxEmbeddedResources}      */
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|metadatas
operator|.
name|clear
argument_list|()
expr_stmt|;
name|unknownCount
operator|=
literal|0
expr_stmt|;
name|hitMaxEmbeddedResources
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Copied/modified from WriteOutContentHandler.  Couldn't make that       * static, and we need to have something that will work       * with exceptions thrown from both BodyContentHandler and WriteOutContentHandler      * @param t      * @return      */
specifier|private
name|boolean
name|isWriteLimitReached
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"Your document contained more than"
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|t
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
name|isWriteLimitReached
argument_list|(
name|t
operator|.
name|getCause
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|//defensive copy
specifier|private
name|Metadata
name|deepCopy
parameter_list|(
name|Metadata
name|m
parameter_list|)
block|{
name|Metadata
name|clone
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|m
operator|.
name|names
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|m
operator|.
name|isMultiValued
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|clone
operator|.
name|set
argument_list|(
name|n
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|vals
init|=
name|m
operator|.
name|getValues
argument_list|(
name|n
argument_list|)
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
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clone
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|clone
return|;
block|}
specifier|private
name|String
name|getResourceName
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
name|String
name|objectName
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|metadata
operator|.
name|get
argument_list|(
name|TikaMetadataKeys
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|objectName
operator|=
name|metadata
operator|.
name|get
argument_list|(
name|TikaMetadataKeys
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|metadata
operator|.
name|get
argument_list|(
name|TikaMetadataKeys
operator|.
name|EMBEDDED_RELATIONSHIP_ID
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|objectName
operator|=
name|metadata
operator|.
name|get
argument_list|(
name|TikaMetadataKeys
operator|.
name|EMBEDDED_RELATIONSHIP_ID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|objectName
operator|=
literal|"embedded-"
operator|+
operator|(
operator|++
name|unknownCount
operator|)
expr_stmt|;
block|}
comment|//make sure that there isn't any path info in the objectName
comment|//some parsers can return paths, not just file names
name|objectName
operator|=
name|FilenameUtils
operator|.
name|getName
argument_list|(
name|objectName
argument_list|)
expr_stmt|;
return|return
name|objectName
return|;
block|}
specifier|private
name|void
name|addContent
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
if|if
condition|(
name|handler
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|DefaultHandler
operator|.
name|class
argument_list|)
condition|)
block|{
comment|//no-op: we can't rely on just testing for
comment|//empty content because DefaultHandler's toString()
comment|//returns e.g. "org.xml.sax.helpers.DefaultHandler@6c8b1edd"
block|}
else|else
block|{
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
operator|&&
name|content
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
name|metadata
operator|.
name|add
argument_list|(
name|TIKA_CONTENT
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Override for different behavior.      *       * @return handler to be used for each document      */
specifier|private
class|class
name|EmbeddedParserDecorator
extends|extends
name|ParserDecorator
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|207648200464263337L
decl_stmt|;
specifier|private
name|String
name|location
init|=
literal|null
decl_stmt|;
specifier|private
name|EmbeddedParserDecorator
parameter_list|(
name|String
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|wrappedParser
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|location
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|this
operator|.
name|location
operator|+=
literal|"/"
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|ignore
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
comment|//Test to see if we should avoid parsing
if|if
condition|(
name|maxEmbeddedResources
operator|>
operator|-
literal|1
operator|&&
name|metadatas
operator|.
name|size
argument_list|()
operator|>=
name|maxEmbeddedResources
condition|)
block|{
name|hitMaxEmbeddedResources
operator|=
literal|true
expr_stmt|;
return|return;
block|}
comment|// Work out what this thing is
name|String
name|objectName
init|=
name|getResourceName
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|String
name|objectLocation
init|=
name|this
operator|.
name|location
operator|+
name|objectName
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|EMBEDDED_RESOURCE_PATH
argument_list|,
name|objectLocation
argument_list|)
expr_stmt|;
comment|//ignore the content handler that is passed in
comment|//and get a fresh handler
name|ContentHandler
name|localHandler
init|=
name|contentHandlerFactory
operator|.
name|getNewContentHandler
argument_list|()
decl_stmt|;
name|Parser
name|preContextParser
init|=
name|context
operator|.
name|get
argument_list|(
name|Parser
operator|.
name|class
argument_list|)
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
operator|new
name|EmbeddedParserDecorator
argument_list|(
name|objectLocation
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|localHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|boolean
name|wlr
init|=
name|isWriteLimitReached
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|wlr
operator|==
literal|true
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|WRITE_LIMIT_REACHED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
finally|finally
block|{
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|preContextParser
argument_list|)
expr_stmt|;
block|}
comment|//Because of recursion, we need
comment|//to re-test to make sure that we limit the
comment|//number of stored resources
if|if
condition|(
name|maxEmbeddedResources
operator|>
operator|-
literal|1
operator|&&
name|metadatas
operator|.
name|size
argument_list|()
operator|>=
name|maxEmbeddedResources
condition|)
block|{
name|hitMaxEmbeddedResources
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|addContent
argument_list|(
name|localHandler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|metadatas
operator|.
name|add
argument_list|(
name|deepCopy
argument_list|(
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

