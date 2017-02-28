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
name|eval
operator|.
name|tokens
package|;
end_package

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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|StandardCharsets
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
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|Gson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|GsonBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_class
specifier|public
class|class
name|AnalyzerManager
block|{
specifier|private
specifier|final
specifier|static
name|String
name|GENERAL
init|=
literal|"general"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ALPHA_IDEOGRAPH
init|=
literal|"alpha"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|COMMON_TOKENS
init|=
literal|"common_tokens"
decl_stmt|;
specifier|private
specifier|final
name|Analyzer
name|generalAnalyzer
decl_stmt|;
specifier|private
specifier|final
name|Analyzer
name|commonTokensAnalyzer
decl_stmt|;
specifier|private
name|AnalyzerManager
parameter_list|(
name|Analyzer
name|generalAnalyzer
parameter_list|,
name|Analyzer
name|commonTokensAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|generalAnalyzer
operator|=
name|generalAnalyzer
expr_stmt|;
name|this
operator|.
name|commonTokensAnalyzer
operator|=
name|commonTokensAnalyzer
expr_stmt|;
block|}
specifier|public
specifier|static
name|AnalyzerManager
name|newInstance
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|AnalyzerManager
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"lucene-analyzers.json"
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|GsonBuilder
name|builder
init|=
operator|new
name|GsonBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|registerTypeHierarchyAdapter
argument_list|(
name|Map
operator|.
name|class
argument_list|,
operator|new
name|AnalyzerDeserializer
argument_list|()
argument_list|)
expr_stmt|;
name|Gson
name|gson
init|=
name|builder
operator|.
name|create
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|map
init|=
name|gson
operator|.
name|fromJson
argument_list|(
name|reader
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
name|Analyzer
name|general
init|=
name|map
operator|.
name|get
argument_list|(
name|GENERAL
argument_list|)
decl_stmt|;
name|Analyzer
name|alphaIdeo
init|=
name|map
operator|.
name|get
argument_list|(
name|ALPHA_IDEOGRAPH
argument_list|)
decl_stmt|;
name|Analyzer
name|common
init|=
name|map
operator|.
name|get
argument_list|(
name|COMMON_TOKENS
argument_list|)
decl_stmt|;
if|if
condition|(
name|general
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JsonParseException
argument_list|(
literal|"Must specify "
operator|+
name|GENERAL
operator|+
literal|" analyzer"
argument_list|)
throw|;
block|}
if|if
condition|(
name|common
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JsonParseException
argument_list|(
literal|"Must specify "
operator|+
name|COMMON_TOKENS
operator|+
literal|" analyzer"
argument_list|)
throw|;
block|}
return|return
operator|new
name|AnalyzerManager
argument_list|(
name|general
argument_list|,
name|common
argument_list|)
return|;
block|}
comment|/**      * This analyzer should be used to extract all tokens.      * @return      */
specifier|public
name|Analyzer
name|getGeneralAnalyzer
parameter_list|()
block|{
return|return
name|generalAnalyzer
return|;
block|}
comment|/**      * This analyzer should be used to generate common tokens lists from      * large corpora.  It is not used by tika-eval in profiling or comparing.      * @return      */
specifier|public
name|Analyzer
name|getCommonTokensAnalyzer
parameter_list|()
block|{
return|return
name|commonTokensAnalyzer
return|;
block|}
block|}
end_class

end_unit

