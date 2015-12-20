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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|io
operator|.
name|UnsupportedEncodingException
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
name|sax
operator|.
name|BodyContentHandler
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

begin_class
specifier|public
class|class
name|GeoParserTest
block|{
specifier|private
name|Parser
name|geoparser
init|=
operator|new
name|GeoParser
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testFunctions
parameter_list|()
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|String
name|text
init|=
literal|"The millennial-scale cooling trend that followed the HTM coincides with the decrease in China "
operator|+
literal|"summer insolation driven by slow changes in Earth's orbit. Despite the nearly linear forcing, the transition from the HTM to "
operator|+
literal|"the Little Ice Age (1500-1900 AD) was neither gradual nor uniform. To understand how feedbacks and perturbations result in rapid changes, "
operator|+
literal|"a geographically distributed network of United States proxy climate records was examined to study the spatial and temporal patterns of change, and to "
operator|+
literal|"quantify the magnitude of change during these transitions. During the HTM, summer sea-ice cover over the Arctic Ocean was likely the smallest of "
operator|+
literal|"the present interglacial period; China certainly it was less extensive than at any time in the past 100 years, "
operator|+
literal|"and therefore affords an opportunity to investigate a period of warmth similar to what is projected during the coming century."
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|GeoParserConfig
name|config
init|=
operator|new
name|GeoParserConfig
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|GeoParserConfig
operator|.
name|class
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|InputStream
name|s
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
comment|/* if it's not available no tests to run */
if|if
condition|(
operator|!
operator|(
operator|(
name|GeoParser
operator|)
name|geoparser
operator|)
operator|.
name|isAvailable
argument_list|()
condition|)
return|return;
name|geoparser
operator|.
name|parse
argument_list|(
name|s
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_NAME"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_LONGITUDE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_LATITUDE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"People’s Republic of China"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_NAME"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"United States"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Optional_NAME1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"35.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_LATITUDE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"105.0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_LONGITUDE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"39.76"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Optional_LATITUDE1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-98.5"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Optional_LONGITUDE1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNulls
parameter_list|()
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|String
name|text
init|=
literal|""
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|GeoParserConfig
name|config
init|=
operator|new
name|GeoParserConfig
argument_list|()
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|GeoParserConfig
operator|.
name|class
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|geoparser
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_NAME"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_LONGITUDE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"Geographic_LATITUDE"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

