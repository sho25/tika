begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|serialization
package|;
end_package

begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|junit
operator|.
name|Test
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|JsonMetadataTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testBasicSerializationAndDeserialization
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"k1"
argument_list|,
literal|"v1"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"k1"
argument_list|,
literal|"v2"
argument_list|)
expr_stmt|;
comment|//test duplicate value
name|metadata
operator|.
name|add
argument_list|(
literal|"k3"
argument_list|,
literal|"v3"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"k3"
argument_list|,
literal|"v3"
argument_list|)
expr_stmt|;
comment|//test numeral with comma
name|metadata
operator|.
name|add
argument_list|(
literal|"k4"
argument_list|,
literal|"500,000"
argument_list|)
expr_stmt|;
comment|//test Chinese
name|metadata
operator|.
name|add
argument_list|(
literal|"alma_mater"
argument_list|,
literal|"\u666E\u6797\u65AF\u987F\u5927\u5B66"
argument_list|)
expr_stmt|;
comment|//test url
name|metadata
operator|.
name|add
argument_list|(
literal|"url"
argument_list|,
literal|"/myApp/myAction.html?method=router&cmd=1"
argument_list|)
expr_stmt|;
comment|//simple html entities
name|metadata
operator|.
name|add
argument_list|(
literal|"html"
argument_list|,
literal|"<html><body>&amp;&nbsp;</body></html>"
argument_list|)
expr_stmt|;
comment|//simple json escape chars
name|metadata
operator|.
name|add
argument_list|(
literal|"json_escapes"
argument_list|,
literal|"the: \"quick\" brown, fox"
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JsonMetadata
operator|.
name|toJson
argument_list|(
name|metadata
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|Metadata
name|deserialized
init|=
name|JsonMetadata
operator|.
name|fromJson
argument_list|(
operator|new
name|StringReader
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|deserialized
operator|.
name|names
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|metadata
argument_list|,
name|deserialized
argument_list|)
expr_stmt|;
comment|//test that this really is 6 Chinese characters
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|deserialized
operator|.
name|get
argument_list|(
literal|"alma_mater"
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|//now test pretty print;
name|writer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|JsonMetadata
operator|.
name|setPrettyPrinting
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JsonMetadata
operator|.
name|toJson
argument_list|(
name|metadata
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"  \"json_escapes\": \"the: \\\"quick\\\" brown, fox\",\n"
operator|+
literal|"  \"k1\": [\n"
operator|+
literal|"    \"v1\",\n"
operator|+
literal|"    \"v2\"\n"
operator|+
literal|"  ],\n"
operator|+
literal|"  \"k3\": [\n"
operator|+
literal|"    \"v3\",\n"
operator|+
literal|"    \"v3\"\n"
operator|+
literal|"  ],\n"
operator|+
literal|"  \"k4\": \"500,000\",\n"
operator|+
literal|"  \"url\": \"/myApp/myAction.html?method\\u003drouter\\u0026cmd\\u003d1\"\n"
operator|+
literal|"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeserializationException
parameter_list|()
block|{
comment|//malformed json; 500,000 should be in quotes
name|String
name|json
init|=
literal|"{\"k1\":[\"v1\",\"v2\"],\"k3\":\"v3\",\"k4\":500,000}"
decl_stmt|;
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Metadata
name|deserialized
init|=
name|JsonMetadata
operator|.
name|fromJson
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNull
parameter_list|()
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
try|try
block|{
name|JsonMetadata
operator|.
name|toJson
argument_list|(
literal|null
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"null"
argument_list|,
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLargeNumberOfKeys
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|m
operator|.
name|set
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"val_"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JsonMetadata
operator|.
name|toJson
argument_list|(
name|m
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|Metadata
name|deserialized
init|=
name|JsonMetadata
operator|.
name|fromJson
argument_list|(
operator|new
name|StringReader
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|m
argument_list|,
name|deserialized
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLargeValues
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
literal|1000000
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"v"
argument_list|)
expr_stmt|;
block|}
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m
operator|.
name|add
argument_list|(
literal|"large_value1"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|add
argument_list|(
literal|"large_value2"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JsonMetadata
operator|.
name|toJson
argument_list|(
name|m
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|Metadata
name|deserialized
init|=
name|JsonMetadata
operator|.
name|fromJson
argument_list|(
operator|new
name|StringReader
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|m
argument_list|,
name|deserialized
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

