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
name|geo
operator|.
name|topic
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|geo
operator|.
name|topic
operator|.
name|gazetteer
operator|.
name|Location
import|;
end_import

begin_class
specifier|public
class|class
name|GeoTag
block|{
name|Location
name|location
init|=
operator|new
name|Location
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|GeoTag
argument_list|>
name|alternatives
init|=
operator|new
name|ArrayList
argument_list|<
name|GeoTag
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|setMain
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|longitude
parameter_list|,
name|String
name|latitude
parameter_list|)
block|{
name|this
operator|.
name|location
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|.
name|setLatitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|.
name|setLongitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addAlternative
parameter_list|(
name|GeoTag
name|geotag
parameter_list|)
block|{
name|alternatives
operator|.
name|add
argument_list|(
name|geotag
argument_list|)
expr_stmt|;
block|}
comment|/* 	 * Store resolved geoName entities in a GeoTag 	 *  	 * @param resolvedGeonames resolved entities 	 *  	 * @param bestNER best name entity among all the extracted entities for the 	 * input stream 	 */
specifier|public
name|void
name|toGeoTag
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Location
argument_list|>
argument_list|>
name|resolvedGeonames
parameter_list|,
name|String
name|bestNER
parameter_list|)
block|{
for|for
control|(
name|String
name|key
range|:
name|resolvedGeonames
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Location
argument_list|>
name|cur
init|=
name|resolvedGeonames
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|bestNER
argument_list|)
condition|)
block|{
name|this
operator|.
name|location
operator|=
name|cur
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|GeoTag
name|alter
init|=
operator|new
name|GeoTag
argument_list|()
decl_stmt|;
name|alter
operator|.
name|location
operator|=
name|cur
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|addAlternative
argument_list|(
name|alter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

