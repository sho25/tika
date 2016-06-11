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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|Tika
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
name|TikaConfig
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
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Map
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
name|assertTrue
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|ParameterizedParserTest
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expcted
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"testparam"
argument_list|,
literal|"testparamval"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xshort"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xint"
argument_list|,
literal|"999999999"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xlong"
argument_list|,
literal|"9999999999999"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xbigint"
argument_list|,
literal|"99999999999999999999999999999999999999999999999"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xfloat"
argument_list|,
literal|"10.2"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xbool"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xdouble"
argument_list|,
literal|"4.6"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xurl"
argument_list|,
literal|"http://apache.org"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xfile"
argument_list|,
literal|"somefile"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"xuri"
argument_list|,
literal|"tika://customuri?param=value"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"inner"
argument_list|,
literal|"inner"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"missing"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testConfigurableParserTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|md
init|=
name|getMetadata
argument_list|(
literal|"TIKA-1986-parameterized.xml"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|expcted
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"mismatch for "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|md
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConfigurableParserTypesDecorated
parameter_list|()
throws|throws
name|Exception
block|{
name|Metadata
name|md
init|=
name|getMetadata
argument_list|(
literal|"TIKA-1986-parameterized-decorated.xml"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|expcted
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"mismatch for "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|md
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSomeParams
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test that a parameterized parser can read a config file
comment|//with only some changes to the initial values
name|Metadata
name|md
init|=
name|getMetadata
argument_list|(
literal|"TIKA-1986-some-parameters.xml"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"-6.0"
argument_list|,
name|md
operator|.
name|get
argument_list|(
literal|"xdouble"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testparamval"
argument_list|,
name|md
operator|.
name|get
argument_list|(
literal|"testparam"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|md
operator|.
name|get
argument_list|(
literal|"xbool"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"can we get this to work, somehow?"
argument_list|)
specifier|public
name|void
name|testBadParam
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Metadata
name|m
init|=
name|getMetadata
argument_list|(
literal|"TIKA-1986-bad-parameters.xml"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBadValue
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Metadata
name|m
init|=
name|getMetadata
argument_list|(
literal|"TIKA-1986-bad-values.xml"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaConfigException
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
literal|"No TikaConfigException"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBadType
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TODO: should this be a TikaConfigException instead of Runtime?
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Metadata
name|m
init|=
name|getMetadata
argument_list|(
literal|"TIKA-1986-bad-types.xml"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
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
literal|"No RuntimeException"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
comment|//TODO later -- add a test for a parser that isn't configurable
comment|//but that has params in the config file
specifier|private
name|Metadata
name|getMetadata
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|URL
name|url
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/org/apache/tika/config/"
operator|+
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"couldn't find: "
operator|+
name|name
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|TikaConfig
name|tikaConfig
init|=
operator|new
name|TikaConfig
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|(
name|tikaConfig
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|tika
operator|.
name|parse
argument_list|(
name|url
operator|.
name|openStream
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
return|return
name|metadata
return|;
block|}
block|}
end_class

end_unit

