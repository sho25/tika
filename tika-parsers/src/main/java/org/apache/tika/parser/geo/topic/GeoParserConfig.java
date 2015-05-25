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
name|Serializable
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

begin_class
specifier|public
class|class
name|GeoParserConfig
implements|implements
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|private
name|String
name|nerModelPath
init|=
literal|null
decl_stmt|;
specifier|public
name|GeoParserConfig
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|GeoParserConfig
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"en-ner-location.bin"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nerModelPath
operator|=
operator|new
name|File
argument_list|(
name|GeoParserConfig
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"en-ner-location.bin"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setNERModelPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
return|return;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
operator|||
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
name|nerModelPath
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|String
name|getNERPath
parameter_list|()
block|{
return|return
name|nerModelPath
return|;
block|}
block|}
end_class

end_unit

