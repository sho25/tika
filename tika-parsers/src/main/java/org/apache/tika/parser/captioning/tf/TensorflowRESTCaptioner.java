begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|captioning
operator|.
name|tf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ByteArrayEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|DefaultHttpClient
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
name|Field
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
name|Param
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
name|TikaConfigException
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
name|IOUtils
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
name|recognition
operator|.
name|ObjectRecogniser
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
name|captioning
operator|.
name|CaptionObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|net
operator|.
name|URI
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
name|Collections
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  * Tensorflow image captioner.  * This implementation uses Tensorflow via REST API.  *<p>  * NOTE : //TODO: link to wiki page here  *  * @since Apache Tika 1.16  */
end_comment

begin_class
specifier|public
class|class
name|TensorflowRESTCaptioner
implements|implements
name|ObjectRecogniser
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TensorflowRESTCaptioner
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_MIMES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|MediaType
index|[]
block|{
name|MediaType
operator|.
name|image
argument_list|(
literal|"jpeg"
argument_list|)
block|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"png"
argument_list|)
block|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"gif"
argument_list|)
block|}
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LABEL_LANG
init|=
literal|"en"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|URI
name|apiBaseUri
decl_stmt|;
annotation|@
name|Field
specifier|private
name|int
name|captions
decl_stmt|;
annotation|@
name|Field
specifier|private
name|int
name|maxCaptionLength
decl_stmt|;
specifier|private
name|URI
name|apiUri
decl_stmt|;
specifier|private
name|URI
name|healthUri
decl_stmt|;
specifier|private
name|boolean
name|available
decl_stmt|;
specifier|protected
name|URI
name|getApiUri
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
name|apiUri
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedMimes
parameter_list|()
block|{
return|return
name|SUPPORTED_MIMES
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|available
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Param
argument_list|>
name|params
parameter_list|)
throws|throws
name|TikaConfigException
block|{
try|try
block|{
name|healthUri
operator|=
name|URI
operator|.
name|create
argument_list|(
name|apiBaseUri
operator|+
literal|"/ping"
argument_list|)
expr_stmt|;
name|apiUri
operator|=
name|URI
operator|.
name|create
argument_list|(
name|apiBaseUri
operator|+
name|String
operator|.
name|format
argument_list|(
literal|"/captions?beam_size=%1$d&max_caption_length=%2$d"
argument_list|,
name|captions
argument_list|,
name|maxCaptionLength
argument_list|)
argument_list|)
expr_stmt|;
name|DefaultHttpClient
name|client
init|=
operator|new
name|DefaultHttpClient
argument_list|()
decl_stmt|;
name|HttpResponse
name|response
init|=
name|client
operator|.
name|execute
argument_list|(
operator|new
name|HttpGet
argument_list|(
name|healthUri
argument_list|)
argument_list|)
decl_stmt|;
name|available
operator|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|200
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Available = {}, API Status = {}"
argument_list|,
name|available
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Captions = {}, MaxCaptionLength = {}"
argument_list|,
name|captions
argument_list|,
name|maxCaptionLength
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|available
operator|=
literal|false
expr_stmt|;
throw|throw
operator|new
name|TikaConfigException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CaptionObject
argument_list|>
name|recognise
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
name|List
argument_list|<
name|CaptionObject
argument_list|>
name|capObjs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|DefaultHttpClient
name|client
init|=
operator|new
name|DefaultHttpClient
argument_list|()
decl_stmt|;
name|HttpPost
name|request
init|=
operator|new
name|HttpPost
argument_list|(
name|getApiUri
argument_list|(
name|metadata
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|ByteArrayOutputStream
name|byteStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
init|)
block|{
comment|//TODO: convert this to stream, this might cause OOM issue
comment|// InputStreamEntity is not working
comment|// request.setEntity(new InputStreamEntity(stream, -1));
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|byteStream
argument_list|)
expr_stmt|;
name|request
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
name|byteStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HttpResponse
name|response
init|=
name|client
operator|.
name|execute
argument_list|(
name|request
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|reply
init|=
name|response
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
init|)
block|{
name|String
name|replyMessage
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|reply
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|200
condition|)
block|{
name|JSONObject
name|jReply
init|=
operator|(
name|JSONObject
operator|)
operator|new
name|JSONParser
argument_list|()
operator|.
name|parse
argument_list|(
name|replyMessage
argument_list|)
decl_stmt|;
name|JSONArray
name|jCaptions
init|=
operator|(
name|JSONArray
operator|)
name|jReply
operator|.
name|get
argument_list|(
literal|"captions"
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
name|jCaptions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|JSONObject
name|jCaption
init|=
operator|(
name|JSONObject
operator|)
name|jCaptions
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|sentence
init|=
operator|(
name|String
operator|)
name|jCaption
operator|.
name|get
argument_list|(
literal|"sentence"
argument_list|)
decl_stmt|;
name|Double
name|confidence
init|=
operator|(
name|Double
operator|)
name|jCaption
operator|.
name|get
argument_list|(
literal|"confidence"
argument_list|)
decl_stmt|;
name|capObjs
operator|.
name|add
argument_list|(
operator|new
name|CaptionObject
argument_list|(
name|sentence
argument_list|,
name|LABEL_LANG
argument_list|,
name|confidence
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Status = {}"
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Response = {}"
argument_list|,
name|replyMessage
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|capObjs
return|;
block|}
block|}
end_class

end_unit

