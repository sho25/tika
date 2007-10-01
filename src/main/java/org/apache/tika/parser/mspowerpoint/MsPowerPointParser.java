begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|mspowerpoint
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|Content
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
name|utils
operator|.
name|MSExtractor
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
name|utils
operator|.
name|Utils
import|;
end_import

begin_comment
comment|/**  * Power point parser  */
end_comment

begin_class
specifier|public
class|class
name|MsPowerPointParser
extends|extends
name|Parser
block|{
specifier|protected
name|String
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|Iterable
argument_list|<
name|Content
argument_list|>
name|contents
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
try|try
block|{
name|MSExtractor
name|extractor
init|=
operator|new
name|PPTExtractor
argument_list|()
decl_stmt|;
name|extractor
operator|.
name|setContents
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|InputStream
index|[]
name|isa
init|=
name|Utils
operator|.
name|copyInputStream
argument_list|(
name|stream
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|extractor
operator|.
name|extractProperties
argument_list|(
name|isa
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
name|extractor
operator|.
name|extractText
argument_list|(
name|isa
index|[
literal|1
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Error parsing a PowerPoint document"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* 	 * public List<Content> getContents() { 	 * extrator.setContents(getParserConfig().getContents()); try { 	 * extrator.extract(getInputStream()); } catch (Exception e) { // TODO 	 * Auto-generated catch block e.printStackTrace(); } return 	 * getParserConfig().getContents(); } 	 */
block|}
end_class

end_unit

