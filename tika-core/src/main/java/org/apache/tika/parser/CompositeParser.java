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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
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
name|TaggedInputStream
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
name|mime
operator|.
name|MediaTypeRegistry
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
name|TaggedContentHandler
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
comment|/**  * Composite parser that delegates parsing tasks to a component parser  * based on the declared content type of the incoming document. A fallback  * parser is defined for cases where a parser for the given content type is  * not available.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeParser
implements|implements
name|Parser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2192845797749627824L
decl_stmt|;
comment|/**      * Media type registry.      */
specifier|private
name|MediaTypeRegistry
name|registry
decl_stmt|;
comment|/**      * List of component parsers.      */
specifier|private
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
decl_stmt|;
comment|/**      * The fallback parser, used when no better parser is available.      */
specifier|private
name|Parser
name|fallback
init|=
operator|new
name|EmptyParser
argument_list|()
decl_stmt|;
specifier|public
name|CompositeParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
parameter_list|)
block|{
name|this
operator|.
name|parsers
operator|=
name|parsers
expr_stmt|;
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
block|}
specifier|public
name|CompositeParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|Parser
modifier|...
name|parsers
parameter_list|)
block|{
name|this
argument_list|(
name|registry
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|parsers
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CompositeParser
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|MediaTypeRegistry
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|getParsers
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Parser
name|parser
range|:
name|parsers
control|)
block|{
for|for
control|(
name|MediaType
name|type
range|:
name|parser
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
control|)
block|{
name|MediaType
name|canonicalType
init|=
name|registry
operator|.
name|normalize
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|canonicalType
argument_list|)
condition|)
block|{
if|if
condition|(
name|map
operator|.
name|get
argument_list|(
name|canonicalType
argument_list|)
operator|!=
name|parser
condition|)
block|{
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|log
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
literal|"Duplicate parser definition for "
operator|+
name|type
operator|+
literal|" ("
operator|+
name|canonicalType
operator|+
literal|"), using "
operator|+
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
name|map
operator|.
name|put
argument_list|(
name|canonicalType
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
comment|/**      * Returns the media type registry used to infer type relationships.      *      * @since Apache Tika 0.8      * @return media type registry      */
specifier|public
name|MediaTypeRegistry
name|getMediaTypeRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
comment|/**      * Sets the media type registry used to infer type relationships.      *      * @since Apache Tika 0.8      * @param registry media type registry      */
specifier|public
name|void
name|setMediaTypeRegistry
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|)
block|{
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
block|}
comment|/**      * Returns the component parsers.      *      * @return component parsers, keyed by media type      */
specifier|public
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|getParsers
parameter_list|()
block|{
return|return
name|getParsers
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Sets the component parsers.      *      * @param parsers component parsers, keyed by media type      */
specifier|public
name|void
name|setParsers
parameter_list|(
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|parsers
parameter_list|)
block|{
name|this
operator|.
name|parsers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Parser
argument_list|>
argument_list|(
name|parsers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|entry
range|:
name|parsers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|this
operator|.
name|parsers
operator|.
name|add
argument_list|(
name|ParserDecorator
operator|.
name|withTypes
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the fallback parser.      *      * @return fallback parser      */
specifier|public
name|Parser
name|getFallback
parameter_list|()
block|{
return|return
name|fallback
return|;
block|}
comment|/**      * Sets the fallback parser.      *      * @param fallback fallback parser      */
specifier|public
name|void
name|setFallback
parameter_list|(
name|Parser
name|fallback
parameter_list|)
block|{
name|this
operator|.
name|fallback
operator|=
name|fallback
expr_stmt|;
block|}
comment|/**      * Returns the parser that best matches the given metadata. By default      * looks for a parser that matches the content type metadata property,      * and uses the fallback parser if a better match is not found. The      * type hierarchy information included in the configured media type      * registry is used when looking for a matching parser instance.      *<p>      * Subclasses can override this method to provide more accurate      * parser resolution.      *      * @param metadata document metadata      * @return matching parser      */
specifier|protected
name|Parser
name|getParser
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
name|getParser
argument_list|(
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Parser
name|getParser
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
block|{
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|map
init|=
name|getParsers
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
comment|// We always work on the normalised, canonical form
name|type
operator|=
name|registry
operator|.
name|normalize
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
comment|// Try finding a parser for the type
name|Parser
name|parser
init|=
name|map
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
return|return
name|parser
return|;
block|}
comment|// Failing that, try for the parent of the type
name|type
operator|=
name|registry
operator|.
name|getSupertype
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
return|return
name|fallback
return|;
block|}
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
name|getParsers
argument_list|(
name|context
argument_list|)
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**      * Delegates the call to the matching component parser.      *<p>      * Potential {@link RuntimeException}s, {@link IOException}s and      * {@link SAXException}s unrelated to the given input stream and content      * handler are automatically wrapped into {@link TikaException}s to better      * honor the {@link Parser} contract.      */
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
name|Parser
name|parser
init|=
name|getParser
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|TaggedInputStream
name|taggedStream
init|=
operator|new
name|TaggedInputStream
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|TaggedContentHandler
name|taggedHandler
init|=
operator|new
name|TaggedContentHandler
argument_list|(
name|handler
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|taggedStream
argument_list|,
name|taggedHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unexpected RuntimeException from "
operator|+
name|parser
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|taggedStream
operator|.
name|throwIfCauseOf
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"TIKA-198: Illegal IOException from "
operator|+
name|parser
argument_list|,
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
name|taggedHandler
operator|.
name|throwIfCauseOf
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"TIKA-237: Illegal SAXException from "
operator|+
name|parser
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0.      */
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
block|}
end_class

end_unit

