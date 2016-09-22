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
name|mime
operator|.
name|MediaType
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
name|File
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
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|net
operator|.
name|URISyntaxException
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
name|HashSet
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
import|import static
name|org
operator|.
name|osgi
operator|.
name|util
operator|.
name|measurement
operator|.
name|Unit
operator|.
name|s
import|;
end_import

begin_comment
comment|/**  * A test Parsers to test {@link Field}  * @since Apache Tika 1.14  */
end_comment

begin_class
specifier|public
class|class
name|DummyParameterizedParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
name|Set
argument_list|<
name|MediaType
argument_list|>
name|MIMES
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|MIMES
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
expr_stmt|;
name|MIMES
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|TEXT_HTML
argument_list|)
expr_stmt|;
name|MIMES
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
expr_stmt|;
name|MIMES
operator|.
name|add
argument_list|(
name|MediaType
operator|.
name|OCTET_STREAM
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Field
argument_list|(
name|name
operator|=
literal|"testparam"
argument_list|)
specifier|private
name|String
name|testParam
init|=
literal|"init_string"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|short
name|xshort
init|=
operator|-
literal|2
decl_stmt|;
annotation|@
name|Field
specifier|private
name|int
name|xint
init|=
operator|-
literal|3
decl_stmt|;
annotation|@
name|Field
specifier|private
name|long
name|xlong
init|=
operator|-
literal|4
decl_stmt|;
annotation|@
name|Field
argument_list|(
name|name
operator|=
literal|"xbigint"
argument_list|)
specifier|private
name|BigInteger
name|xbigInt
decl_stmt|;
annotation|@
name|Field
specifier|private
name|float
name|xfloat
init|=
operator|-
literal|5.0f
decl_stmt|;
annotation|@
name|Field
specifier|private
name|double
name|xdouble
init|=
operator|-
literal|6.0d
decl_stmt|;
annotation|@
name|Field
specifier|private
name|boolean
name|xbool
init|=
literal|true
decl_stmt|;
annotation|@
name|Field
specifier|private
name|URL
name|xurl
decl_stmt|;
annotation|@
name|Field
specifier|private
name|URI
name|xuri
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|missing
init|=
literal|"default"
decl_stmt|;
specifier|private
name|String
name|inner
init|=
literal|"inner"
decl_stmt|;
specifier|private
name|File
name|xfile
decl_stmt|;
specifier|public
name|DummyParameterizedParser
parameter_list|()
block|{
try|try
block|{
name|xurl
operator|=
operator|new
name|URL
argument_list|(
literal|"http://tika.apache.org/url"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|xuri
operator|=
operator|new
name|URI
argument_list|(
literal|"http://tika.apache.org/uri"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Field
specifier|public
name|void
name|setXfile
parameter_list|(
name|File
name|xfile
parameter_list|)
block|{
name|this
operator|.
name|xfile
operator|=
name|xfile
expr_stmt|;
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
name|MIMES
return|;
block|}
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
name|metadata
operator|.
name|add
argument_list|(
literal|"testparam"
argument_list|,
name|testParam
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xshort"
argument_list|,
name|xshort
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xint"
argument_list|,
name|xint
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xlong"
argument_list|,
name|xlong
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xbigint"
argument_list|,
name|xbigInt
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xfloat"
argument_list|,
name|xfloat
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xdouble"
argument_list|,
name|xdouble
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xbool"
argument_list|,
name|xbool
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xuri"
argument_list|,
name|xuri
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xurl"
argument_list|,
name|xurl
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"xfile"
argument_list|,
name|xfile
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"inner"
argument_list|,
name|inner
operator|+
literal|""
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"missing"
argument_list|,
name|missing
operator|+
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
