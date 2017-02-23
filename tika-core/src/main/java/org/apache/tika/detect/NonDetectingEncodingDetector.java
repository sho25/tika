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
name|detect
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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

begin_comment
comment|/**  * Always returns the charset passed in via the initializer.  */
end_comment

begin_class
specifier|public
class|class
name|NonDetectingEncodingDetector
implements|implements
name|EncodingDetector
block|{
comment|//would have preferred final, but need mutability for
comment|//loading via TikaConfig
specifier|private
name|Charset
name|charset
init|=
name|StandardCharsets
operator|.
name|UTF_8
decl_stmt|;
comment|/**      * Sets charset to UTF-8.      */
specifier|public
name|NonDetectingEncodingDetector
parameter_list|()
block|{      }
specifier|public
name|NonDetectingEncodingDetector
parameter_list|(
name|Charset
name|charset
parameter_list|)
block|{
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Charset
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|charset
return|;
block|}
specifier|public
name|void
name|setCharset
parameter_list|(
name|Charset
name|charset
parameter_list|)
block|{
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
block|}
block|}
end_class

end_unit

