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

begin_comment
comment|/**  * Dummy detector that returns application/octet-stream for all documents.  */
end_comment

begin_class
specifier|public
class|class
name|EmptyDetector
implements|implements
name|Detector
block|{
comment|/**      * Singleton instance of this class.      */
specifier|public
specifier|static
specifier|final
name|EmptyDetector
name|INSTANCE
init|=
operator|new
name|EmptyDetector
argument_list|()
decl_stmt|;
specifier|public
name|MediaType
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
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
end_class

end_unit

