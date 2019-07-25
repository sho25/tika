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
name|TikaCoreProperties
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
name|ParserUtils
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_comment
comment|/**  * This is a special handler to be used only with the {@link org.apache.tika.parser.RecursiveParserWrapper}.  * It allows for finer-grained processing of embedded documents than in the legacy handlers.  * Subclasses can choose how to process individual embedded documents.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRecursiveParserWrapperHandler
extends|extends
name|DefaultHandler
implements|implements
name|Serializable
block|{
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
name|TikaCoreProperties
operator|.
name|TIKA_META_PREFIX
operator|+
literal|"content"
argument_list|)
decl_stmt|;
comment|/**      * Simple class name of the content handler      */
specifier|public
specifier|final
specifier|static
name|Property
name|TIKA_CONTENT_HANDLER
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|TikaCoreProperties
operator|.
name|TIKA_META_PREFIX
operator|+
literal|"content_handler"
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
name|TikaCoreProperties
operator|.
name|TIKA_META_PREFIX
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
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_PREFIX
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
name|TikaCoreProperties
operator|.
name|TIKA_META_EXCEPTION_PREFIX
operator|+
literal|"embedded_resource_limit_reached"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|Property
name|EMBEDDED_EXCEPTION
init|=
name|ParserUtils
operator|.
name|EMBEDDED_EXCEPTION
decl_stmt|;
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
name|TikaCoreProperties
operator|.
name|TIKA_META_PREFIX
operator|+
literal|"embedded_resource_path"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ContentHandlerFactory
name|contentHandlerFactory
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DEPTH
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxEmbeddedResources
decl_stmt|;
specifier|private
name|int
name|embeddedResources
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|embeddedDepth
init|=
literal|0
decl_stmt|;
specifier|public
name|AbstractRecursiveParserWrapperHandler
parameter_list|(
name|ContentHandlerFactory
name|contentHandlerFactory
parameter_list|)
block|{
name|this
argument_list|(
name|contentHandlerFactory
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractRecursiveParserWrapperHandler
parameter_list|(
name|ContentHandlerFactory
name|contentHandlerFactory
parameter_list|,
name|int
name|maxEmbeddedResources
parameter_list|)
block|{
name|this
operator|.
name|contentHandlerFactory
operator|=
name|contentHandlerFactory
expr_stmt|;
name|this
operator|.
name|maxEmbeddedResources
operator|=
name|maxEmbeddedResources
expr_stmt|;
block|}
specifier|public
name|ContentHandler
name|getNewContentHandler
parameter_list|()
block|{
return|return
name|contentHandlerFactory
operator|.
name|getNewContentHandler
argument_list|()
return|;
block|}
specifier|public
name|ContentHandler
name|getNewContentHandler
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|Charset
name|charset
parameter_list|)
block|{
return|return
name|contentHandlerFactory
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|charset
argument_list|)
return|;
block|}
comment|/**      * This is called before parsing each embedded document.  Override this      * for custom behavior.  Make sure to call this in your custom classes      * because this tracks the number of embedded documents.      *      * @param contentHandler local handler to be used on this embedded document      * @param metadata embedded document's metadata      */
specifier|public
name|void
name|startEmbeddedDocument
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SAXException
block|{
name|embeddedResources
operator|++
expr_stmt|;
name|embeddedDepth
operator|++
expr_stmt|;
if|if
condition|(
name|embeddedDepth
operator|>=
name|MAX_DEPTH
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Max embedded depth reached: "
operator|+
name|embeddedDepth
argument_list|)
throw|;
block|}
block|}
comment|/**      * This is called after parsing each embedded document.  Override this      * for custom behavior.  This is currently a no-op.      *      * @param contentHandler content handler that was used on this embedded document      * @param metadata metadata for this embedded document      * @throws SAXException      */
specifier|public
name|void
name|endEmbeddedDocument
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SAXException
block|{
name|embeddedDepth
operator|--
expr_stmt|;
block|}
comment|/**      * This is called after the full parse has completed.  Override this      * for custom behavior.  Make sure to call this as<code>super.endDocument(...)</code>      * in subclasses because this adds whether or not the embedded resource      * maximum has been hit to the metadata.      *      * @param contentHandler content handler that was used on the main document      * @param metadata metadata that was gathered for the main document      * @throws SAXException      */
specifier|public
name|void
name|endDocument
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|hasHitMaximumEmbeddedResources
argument_list|()
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
block|}
comment|/**      *      * @return whether this handler has hit the maximum embedded resources during the parse      */
specifier|public
name|boolean
name|hasHitMaximumEmbeddedResources
parameter_list|()
block|{
if|if
condition|(
name|maxEmbeddedResources
operator|>
operator|-
literal|1
operator|&&
name|embeddedResources
operator|>=
name|maxEmbeddedResources
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|ContentHandlerFactory
name|getContentHandlerFactory
parameter_list|()
block|{
return|return
name|contentHandlerFactory
return|;
block|}
block|}
end_class

end_unit

