begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|batch
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
class|class
name|FileConsumerFutureResult
implements|implements
name|IFileProcessorFutureResult
block|{
specifier|private
specifier|final
name|FileStarted
name|fileStarted
decl_stmt|;
specifier|private
specifier|final
name|int
name|filesProcessed
decl_stmt|;
specifier|public
name|FileConsumerFutureResult
parameter_list|(
name|FileStarted
name|fs
parameter_list|,
name|int
name|filesProcessed
parameter_list|)
block|{
name|this
operator|.
name|fileStarted
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|filesProcessed
operator|=
name|filesProcessed
expr_stmt|;
block|}
specifier|public
name|FileStarted
name|getFileStarted
parameter_list|()
block|{
return|return
name|fileStarted
return|;
block|}
specifier|public
name|int
name|getFilesProcessed
parameter_list|()
block|{
return|return
name|filesProcessed
return|;
block|}
block|}
end_class

end_unit

