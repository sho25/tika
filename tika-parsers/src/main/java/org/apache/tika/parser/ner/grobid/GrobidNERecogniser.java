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
name|grobid
package|;
end_package

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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|GrobidNERecogniser
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
name|GrobidNERecogniser
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
name|GROBID_REST_HOST
init|=
literal|"http://localhost:8080"
decl_stmt|;
specifier|private
name|String
name|restHostUrlStr
decl_stmt|;
comment|/*      * Useful Entities from Grobid NER       */
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
literal|"MEASUREMENT_NUMBERS"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"MEASUREMENT_UNITS"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"MEASUREMENTS"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"NORMALIZED_MEASUREMENTS"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"MEASUREMENT_TYPES"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|public
name|GrobidNERecogniser
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
name|e
operator|.
name|printStackTrace
argument_list|()
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
name|GROBID_REST_HOST
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
name|APPLICATION_JSON
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
literal|"Grobid REST Server is not running"
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
name|info
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
comment|/**      * Reads the GROBID REST URL from the properties file      * returns the GROBID REST URL      */
specifier|private
specifier|static
name|String
name|readRestUrl
parameter_list|()
throws|throws
name|IOException
block|{
name|Properties
name|grobidProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|grobidProperties
operator|.
name|load
argument_list|(
name|GrobidNERecogniser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"GrobidServer.properties"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|grobidProperties
operator|.
name|getProperty
argument_list|(
literal|"grobid.server.url"
argument_list|)
return|;
block|}
comment|/**      * Reads the GROBID REST Endpoint from the properties file      * returns the GROBID REST Endpoint      */
specifier|private
specifier|static
name|String
name|readRestEndpoint
parameter_list|()
throws|throws
name|IOException
block|{
name|Properties
name|grobidProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|grobidProperties
operator|.
name|load
argument_list|(
name|GrobidNERecogniser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"GrobidServer.properties"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|grobidProperties
operator|.
name|getProperty
argument_list|(
literal|"grobid.endpoint.text"
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
comment|/**      * Converts JSON Object to JSON Array       * @return a JSON array      */
specifier|public
name|JSONArray
name|convertToJSONArray
parameter_list|(
name|JSONObject
name|obj
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|JSONArray
name|jsonArray
init|=
operator|new
name|JSONArray
argument_list|()
decl_stmt|;
try|try
block|{
name|jsonArray
operator|=
operator|(
name|JSONArray
operator|)
name|obj
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
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
name|jsonArray
return|;
block|}
comment|/**      * Parses a JSON String and converts it to a JSON Object       * @return a JSON Object      */
specifier|public
name|JSONObject
name|convertToJSONObject
parameter_list|(
name|String
name|jsonString
parameter_list|)
block|{
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|()
decl_stmt|;
name|JSONObject
name|jsonObject
init|=
operator|new
name|JSONObject
argument_list|()
decl_stmt|;
try|try
block|{
name|jsonObject
operator|=
operator|(
name|JSONObject
operator|)
name|parser
operator|.
name|parse
argument_list|(
name|jsonString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
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
name|jsonObject
return|;
block|}
comment|/**      * recognises names of entities in the text      * @param text text which possibly contains names      * @return map of entity type -> set of names      */
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
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|measurementNumberSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|unitSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|measurementSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|normalizedMeasurementSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|measurementTypeSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|url
init|=
name|restHostUrlStr
operator|+
name|readRestEndpoint
argument_list|()
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
name|APPLICATION_JSON
argument_list|)
operator|.
name|post
argument_list|(
literal|"text="
operator|+
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
name|JSONObject
name|jsonObject
init|=
name|convertToJSONObject
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|JSONArray
name|measurements
init|=
name|convertToJSONArray
argument_list|(
name|jsonObject
argument_list|,
literal|"measurements"
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
name|measurements
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|StringBuffer
name|measurementString
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|StringBuffer
name|normalizedMeasurementString
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|JSONObject
name|quantity
init|=
operator|(
name|JSONObject
operator|)
name|convertToJSONObject
argument_list|(
name|measurements
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|"quantity"
argument_list|)
decl_stmt|;
if|if
condition|(
name|quantity
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|quantity
operator|.
name|containsKey
argument_list|(
literal|"rawValue"
argument_list|)
condition|)
block|{
name|String
name|measurementNumber
init|=
operator|(
name|String
operator|)
name|convertToJSONObject
argument_list|(
name|quantity
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|"rawValue"
argument_list|)
decl_stmt|;
name|measurementString
operator|.
name|append
argument_list|(
name|measurementNumber
argument_list|)
expr_stmt|;
name|measurementString
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|measurementNumberSet
operator|.
name|add
argument_list|(
name|measurementNumber
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|quantity
operator|.
name|containsKey
argument_list|(
literal|"normalizedQuantity"
argument_list|)
condition|)
block|{
name|String
name|normalizedMeasurementNumber
init|=
name|convertToJSONObject
argument_list|(
name|quantity
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|"normalizedQuantity"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|normalizedMeasurementString
operator|.
name|append
argument_list|(
name|normalizedMeasurementNumber
argument_list|)
expr_stmt|;
name|normalizedMeasurementString
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|quantity
operator|.
name|containsKey
argument_list|(
literal|"type"
argument_list|)
condition|)
block|{
name|String
name|measurementType
init|=
operator|(
name|String
operator|)
name|convertToJSONObject
argument_list|(
name|quantity
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
name|measurementTypeSet
operator|.
name|add
argument_list|(
name|measurementType
argument_list|)
expr_stmt|;
block|}
name|JSONObject
name|jsonObj
init|=
operator|(
name|JSONObject
operator|)
name|convertToJSONObject
argument_list|(
name|quantity
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|jsonObj
operator|.
name|containsKey
argument_list|(
literal|"rawUnit"
argument_list|)
condition|)
block|{
name|JSONObject
name|rawUnit
init|=
operator|(
name|JSONObject
operator|)
name|jsonObj
operator|.
name|get
argument_list|(
literal|"rawUnit"
argument_list|)
decl_stmt|;
name|String
name|unitName
init|=
operator|(
name|String
operator|)
name|convertToJSONObject
argument_list|(
name|rawUnit
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|unitSet
operator|.
name|add
argument_list|(
name|unitName
argument_list|)
expr_stmt|;
name|measurementString
operator|.
name|append
argument_list|(
name|unitName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jsonObj
operator|.
name|containsKey
argument_list|(
literal|"normalizedUnit"
argument_list|)
condition|)
block|{
name|JSONObject
name|normalizedUnit
init|=
operator|(
name|JSONObject
operator|)
name|jsonObj
operator|.
name|get
argument_list|(
literal|"normalizedUnit"
argument_list|)
decl_stmt|;
name|String
name|normalizedUnitName
init|=
operator|(
name|String
operator|)
name|convertToJSONObject
argument_list|(
name|normalizedUnit
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|normalizedMeasurementString
operator|.
name|append
argument_list|(
name|normalizedUnitName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|measurementString
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|measurementSet
operator|.
name|add
argument_list|(
name|measurementString
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|normalizedMeasurementString
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|normalizedMeasurementSet
operator|.
name|add
argument_list|(
name|normalizedMeasurementString
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|entities
operator|.
name|put
argument_list|(
literal|"MEASUREMENT_NUMBERS"
argument_list|,
name|measurementNumberSet
argument_list|)
expr_stmt|;
name|entities
operator|.
name|put
argument_list|(
literal|"MEASUREMENT_UNITS"
argument_list|,
name|unitSet
argument_list|)
expr_stmt|;
name|entities
operator|.
name|put
argument_list|(
literal|"MEASUREMENTS"
argument_list|,
name|measurementSet
argument_list|)
expr_stmt|;
name|entities
operator|.
name|put
argument_list|(
literal|"NORMALIZED_MEASUREMENTS"
argument_list|,
name|normalizedMeasurementSet
argument_list|)
expr_stmt|;
name|entities
operator|.
name|put
argument_list|(
literal|"MEASUREMENT_TYPES"
argument_list|,
name|measurementTypeSet
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
name|info
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
name|ENTITY_TYPES
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ENTITY_TYPES
operator|.
name|addAll
argument_list|(
name|entities
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entities
return|;
block|}
block|}
end_class

end_unit
