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
name|ner
operator|.
name|nltk
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
name|parser
operator|.
name|ner
operator|.
name|NERecogniser
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|Locale
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|client
operator|.
name|WebClient
import|;
end_import

begin_comment
comment|/**  *  This class offers an implementation of {@link NERecogniser} based on  *  ne_chunk() module of NLTK. This NER requires additional setup,  *  due to Http requests to an endpoint server that runs NLTK.  *  See<a href="http://wiki.apache.org/tika/TikaAndNLTK">  *  */
end_comment

begin_class
specifier|public
class|class
name|NLTKNERecogniser
implements|implements
name|NERecogniser
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
name|NLTKNERecogniser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|available
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NLTK_REST_HOST
init|=
literal|"http://localhost:8881"
decl_stmt|;
specifier|private
name|String
name|restHostUrlStr
decl_stmt|;
comment|/**      * some common entities identified by NLTK      */
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ENTITY_TYPES
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
literal|"NAMES"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|public
name|NLTKNERecogniser
parameter_list|()
block|{
try|try
block|{
name|String
name|restHostUrlStr
init|=
literal|""
decl_stmt|;
try|try
block|{
name|restHostUrlStr
operator|=
name|readRestUrl
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't read rest url"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|restHostUrlStr
operator|==
literal|null
operator|||
name|restHostUrlStr
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|this
operator|.
name|restHostUrlStr
operator|=
name|NLTK_REST_HOST
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|restHostUrlStr
operator|=
name|restHostUrlStr
expr_stmt|;
block|}
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|restHostUrlStr
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|TEXT_HTML
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|responseCode
init|=
name|response
operator|.
name|getStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseCode
operator|==
literal|200
condition|)
block|{
name|available
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NLTKRest Server is not running"
argument_list|)
expr_stmt|;
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
block|}
specifier|private
specifier|static
name|String
name|readRestUrl
parameter_list|()
throws|throws
name|IOException
block|{
name|Properties
name|nltkProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|nltkProperties
operator|.
name|load
argument_list|(
name|NLTKNERecogniser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"NLTKServer.properties"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|nltkProperties
operator|.
name|getProperty
argument_list|(
literal|"nltk.server.url"
argument_list|)
return|;
block|}
comment|/**      * @return {@code true} if server endpoint is available.      * returns {@code false} if server endpoint is not avaliable for service.      */
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|available
return|;
block|}
comment|/**      * Gets set of entity types recognised by this recogniser      * @return set of entity classes/types      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getEntityTypes
parameter_list|()
block|{
return|return
name|ENTITY_TYPES
return|;
block|}
comment|/**      * recognises names of entities in the text      * @param text text which possibly contains names      * @return map of entity type -&gt; set of names      */
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|recognise
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|entities
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|url
init|=
name|restHostUrlStr
operator|+
literal|"/nltk"
decl_stmt|;
name|Response
name|response
init|=
name|WebClient
operator|.
name|create
argument_list|(
name|url
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|TEXT_HTML
argument_list|)
operator|.
name|post
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|int
name|responseCode
init|=
name|response
operator|.
name|getStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseCode
operator|==
literal|200
condition|)
block|{
name|String
name|result
init|=
name|response
operator|.
name|readEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|()
decl_stmt|;
name|JSONObject
name|j
init|=
operator|(
name|JSONObject
operator|)
name|parser
operator|.
name|parse
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|keys
init|=
name|j
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|keys
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
literal|"result"
argument_list|)
condition|)
block|{
name|ENTITY_TYPES
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|entities
operator|.
name|put
argument_list|(
name|key
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
operator|new
name|HashSet
argument_list|(
operator|(
name|Collection
operator|)
name|j
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|debug
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
name|entities
return|;
block|}
block|}
end_class

end_unit

