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
name|multiple
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|ParserUtils
operator|.
name|cloneMetadata
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|ParserUtils
operator|.
name|recordParserDetails
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|ParserUtils
operator|.
name|recordParserFailure
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
name|HashSet
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
name|TemporaryResources
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
name|TikaInputStream
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
name|parser
operator|.
name|AbstractParser
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
name|ParseContext
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
name|parser
operator|.
name|ParserDecorator
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

begin_comment
comment|/**  * Abstract base class for parser wrappers which may / will  *  process a given stream multiple times, merging the results  *  of the various parsers used.  * End users should normally use {@link FallbackParser} or  *  {@link SupplementingParser} along with a Strategy.  * Note that unless you give a {@link ContentHandlerFactory},  *  you'll get content from every parser tried mushed together!  *  * @since Apache Tika 1.18  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMultipleParser
extends|extends
name|AbstractParser
block|{
comment|/**      * Serial version UID.      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5383668090329836559L
decl_stmt|;
comment|/**      * The various strategies for handling metadata emitted by      *  multiple parsers.      * Note that not all will be supported by all subclasses.      */
specifier|public
enum|enum
name|MetadataPolicy
block|{
comment|/**          * Before moving onto another parser, throw away          *  all previously seen metadata          */
name|DISCARD_ALL
block|,
comment|/**          * The first parser to output a given key wins,          *  merge in non-clashing other keys          */
name|FIRST_WINS
block|,
comment|/**          * The last parser to output a given key wins,          *  overriding previous parser values for a          *  clashing key.          */
name|LAST_WINS
block|,
comment|/**          * Where multiple parsers output a given key,          *  store all their different (unique) values          */
name|KEEP_ALL
block|}
empty_stmt|;
comment|/**      * Media type registry.      */
specifier|private
name|MediaTypeRegistry
name|registry
decl_stmt|;
comment|/**      * How we should handle metadata clashes      */
specifier|private
name|MetadataPolicy
name|policy
decl_stmt|;
comment|/**      * List of the multiple parsers to try.      */
specifier|private
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
decl_stmt|;
comment|/**      * Computed list of Mime Types to offer, which is all      *  those in common between the parsers.      * For explicit mimetypes only, use a {@link ParserDecorator}      */
specifier|private
name|Set
argument_list|<
name|MediaType
argument_list|>
name|offeredTypes
decl_stmt|;
comment|// TODO Tika Config XML Support for these parsers and their
comment|//  metadata policies + parsers + mimetypes
comment|// See https://wiki.apache.org/tika/CompositeParserDiscussion
comment|/**      * Returns the media type registry used to infer type relationships.      *      * @return media type registry      */
specifier|public
name|MediaTypeRegistry
name|getMediaTypeRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
comment|/**      * Sets the media type registry used to infer type relationships.      *      * @param registry media type registry      */
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
specifier|public
name|AbstractMultipleParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|MetadataPolicy
name|policy
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
name|policy
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
name|AbstractMultipleParser
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|MetadataPolicy
name|policy
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
name|policy
operator|=
name|policy
expr_stmt|;
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
comment|// TODO Only offer those in common to several/all parser
comment|// TODO Some sort of specialisation / subtype support
name|this
operator|.
name|offeredTypes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Parser
name|parser
range|:
name|parsers
control|)
block|{
name|offeredTypes
operator|.
name|addAll
argument_list|(
name|parser
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|offeredTypes
return|;
block|}
comment|/**      * Used to allow implementations to prepare or change things      *  before parsing occurs      */
specifier|protected
name|void
name|parserPrepare
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
block|{}
comment|/**      * Used to notify implementations that a Parser has Finished      *  or Failed, and to allow them to decide to continue or       *  abort further parsing      */
specifier|protected
specifier|abstract
name|boolean
name|parserCompleted
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|Exception
name|exception
parameter_list|)
function_decl|;
comment|/**      * Processes the given Stream through one or more parsers,       *  resetting things between parsers as requested by policy.      * The actual processing is delegated to one or more {@link Parser}s.      *       * Note that you'll get text from every parser this way, to have       *  control of which content is from which parser you need to      *  call the method with a {@link ContentHandlerFactory} instead.       */
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
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**      * Processes the given Stream through one or more parsers,       *  resetting things between parsers as requested by policy.      * The actual processing is delegated to one or more {@link Parser}s.      * You will get one ContentHandler fetched for each Parser used.      * TODO Do we need to return all the ContentHandler instances we created?      */
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandlerFactory
name|handlers
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
name|parse
argument_list|(
name|stream
argument_list|,
literal|null
argument_list|,
name|handlers
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ContentHandlerFactory
name|handlerFactory
parameter_list|,
name|Metadata
name|originalMetadata
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
comment|// Track the metadata between parsers, so we can apply our policy
name|Metadata
name|lastMetadata
init|=
name|cloneMetadata
argument_list|(
name|originalMetadata
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
name|lastMetadata
decl_stmt|;
comment|// Start tracking resources, so we can clean up when done
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Force the stream to be a Tika one
comment|// Force the stream to be file-backed, so we can re-read safely
comment|//  later if required for parser 2+
comment|// TODO Should we support RereadableInputStream as well?
comment|// TODO Can we put this re-read logic in a utils method?
name|TikaInputStream
name|taggedStream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
name|taggedStream
operator|.
name|getPath
argument_list|()
expr_stmt|;
for|for
control|(
name|Parser
name|p
range|:
name|parsers
control|)
block|{
comment|// Indicate we may need to re-read the stream later
comment|// TODO Support an InputStreamFactory as an alternative to
comment|//  Files, see TIKA-2585
name|taggedStream
operator|.
name|mark
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Get a new handler for this parser, if we can
comment|// If not, the user will get text from every parser
comment|//  mushed together onto the one solitary handler...
if|if
condition|(
name|handlerFactory
operator|!=
literal|null
condition|)
block|{
name|handler
operator|=
name|handlerFactory
operator|.
name|getNewContentHandler
argument_list|()
expr_stmt|;
block|}
comment|// Record that we used this parser
name|recordParserDetails
argument_list|(
name|p
argument_list|,
name|originalMetadata
argument_list|)
expr_stmt|;
comment|// Prepare an near-empty Metadata, will merge after
name|metadata
operator|=
name|cloneMetadata
argument_list|(
name|originalMetadata
argument_list|)
expr_stmt|;
comment|// Notify the implementation of what we're about to do
name|parserPrepare
argument_list|(
name|p
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|// Process if possible
name|Exception
name|failure
init|=
literal|null
decl_stmt|;
try|try
block|{
name|p
operator|.
name|parse
argument_list|(
name|taggedStream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Record the failure such that it can't get lost / overwritten
name|recordParserFailure
argument_list|(
name|p
argument_list|,
name|e
argument_list|,
name|originalMetadata
argument_list|)
expr_stmt|;
name|recordParserFailure
argument_list|(
name|p
argument_list|,
name|e
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|failure
operator|=
name|e
expr_stmt|;
block|}
comment|// Notify the implementation how it went
name|boolean
name|tryNext
init|=
name|parserCompleted
argument_list|(
name|p
argument_list|,
name|metadata
argument_list|,
name|handler
argument_list|,
name|context
argument_list|,
name|failure
argument_list|)
decl_stmt|;
comment|// Handle metadata merging / clashes
name|metadata
operator|=
name|mergeMetadata
argument_list|(
name|metadata
argument_list|,
name|lastMetadata
argument_list|,
name|policy
argument_list|)
expr_stmt|;
comment|// Abort if requested, with the exception if there was one
if|if
condition|(
operator|!
name|tryNext
condition|)
block|{
if|if
condition|(
name|failure
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|failure
operator|instanceof
name|IOException
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|failure
throw|;
if|if
condition|(
name|failure
operator|instanceof
name|SAXException
condition|)
throw|throw
operator|(
name|SAXException
operator|)
name|failure
throw|;
if|if
condition|(
name|failure
operator|instanceof
name|TikaException
condition|)
throw|throw
operator|(
name|TikaException
operator|)
name|failure
throw|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unexpected RuntimeException from "
operator|+
name|p
argument_list|,
name|failure
argument_list|)
throw|;
block|}
comment|// Abort processing, don't try any more parsers
break|break;
block|}
comment|// Prepare for the next parser, if present
name|lastMetadata
operator|=
name|cloneMetadata
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|taggedStream
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|// Finally, copy the latest metadata back onto their supplied object
for|for
control|(
name|String
name|n
range|:
name|metadata
operator|.
name|names
argument_list|()
control|)
block|{
name|originalMetadata
operator|.
name|remove
argument_list|(
name|n
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|val
range|:
name|metadata
operator|.
name|getValues
argument_list|(
name|n
argument_list|)
control|)
block|{
name|originalMetadata
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
specifier|static
name|Metadata
name|mergeMetadata
parameter_list|(
name|Metadata
name|newMetadata
parameter_list|,
name|Metadata
name|lastMetadata
parameter_list|,
name|MetadataPolicy
name|policy
parameter_list|)
block|{
if|if
condition|(
name|policy
operator|==
name|MetadataPolicy
operator|.
name|DISCARD_ALL
condition|)
block|{
return|return
name|newMetadata
return|;
block|}
for|for
control|(
name|String
name|n
range|:
name|lastMetadata
operator|.
name|names
argument_list|()
control|)
block|{
comment|// If this is one of the metadata keys we're setting ourselves
comment|//  for tracking/errors, then always keep the latest one!
if|if
condition|(
name|n
operator|.
name|equals
argument_list|(
name|ParserUtils
operator|.
name|X_PARSED_BY
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|n
operator|.
name|equals
argument_list|(
name|ParserUtils
operator|.
name|EMBEDDED_PARSER
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|n
operator|.
name|equals
argument_list|(
name|ParserUtils
operator|.
name|EMBEDDED_EXCEPTION
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
continue|continue;
comment|// Merge as per policy
name|String
index|[]
name|newVals
init|=
name|newMetadata
operator|.
name|getValues
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|String
index|[]
name|oldVals
init|=
name|lastMetadata
operator|.
name|getValues
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|newVals
operator|==
literal|null
operator|||
name|newVals
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// Metadata only in previous run, keep old values
for|for
control|(
name|String
name|val
range|:
name|oldVals
control|)
block|{
name|newMetadata
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|Arrays
operator|.
name|deepEquals
argument_list|(
name|oldVals
argument_list|,
name|newVals
argument_list|)
condition|)
block|{
comment|// Metadata is the same, nothing to do
continue|continue;
block|}
else|else
block|{
switch|switch
condition|(
name|policy
condition|)
block|{
case|case
name|FIRST_WINS
case|:
comment|// Use the earlier value(s) in place of this/these one/s
name|newMetadata
operator|.
name|remove
argument_list|(
name|n
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|val
range|:
name|oldVals
control|)
block|{
name|newMetadata
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
continue|continue;
case|case
name|LAST_WINS
case|:
comment|// Most recent (last) parser has already won
continue|continue;
case|case
name|KEEP_ALL
case|:
comment|// Start with old list, then add any new unique values
name|List
argument_list|<
name|String
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|oldVals
argument_list|)
argument_list|)
decl_stmt|;
name|newMetadata
operator|.
name|remove
argument_list|(
name|n
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|oldVal
range|:
name|oldVals
control|)
block|{
name|newMetadata
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|oldVal
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|newVal
range|:
name|newVals
control|)
block|{
if|if
condition|(
operator|!
name|vals
operator|.
name|contains
argument_list|(
name|newVal
argument_list|)
condition|)
block|{
name|newMetadata
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|newVal
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|newVal
argument_list|)
expr_stmt|;
block|}
block|}
continue|continue;
block|}
block|}
block|}
return|return
name|newMetadata
return|;
block|}
block|}
end_class

end_unit
