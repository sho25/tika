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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Simple interface around a collection of consumers that allows  * for initializing and shutting shared resources (e.g. db connection, index, writer, etc.)  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ConsumersManager
block|{
comment|//maximum time to allow the ConsumersManager for either init()
comment|//or shutdown()
specifier|private
name|long
name|consumersManagerMaxMillis
init|=
literal|60000
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|FileResourceConsumer
argument_list|>
name|consumers
decl_stmt|;
specifier|public
name|ConsumersManager
parameter_list|(
name|List
argument_list|<
name|FileResourceConsumer
argument_list|>
name|consumers
parameter_list|)
block|{
name|this
operator|.
name|consumers
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|consumers
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the consumers      * @return consumers      */
specifier|public
name|List
argument_list|<
name|FileResourceConsumer
argument_list|>
name|getConsumers
parameter_list|()
block|{
return|return
name|consumers
return|;
block|}
comment|/**      * This is called by BatchProcess before submitting the threads      */
specifier|public
name|void
name|init
parameter_list|()
block|{      }
comment|/**      * This is called by BatchProcess immediately before closing.      * Beware! Some of the consumers may have hung or may not      * have completed.      */
specifier|public
name|void
name|shutdown
parameter_list|()
block|{      }
comment|/**      * {@link org.apache.tika.batch.BatchProcess} will throw an exception      * if the ConsumersManager doesn't complete init() or shutdown()      * within this amount of time.      * @return the maximum time allowed for init() or shutdown()      */
specifier|public
name|long
name|getConsumersManagerMaxMillis
parameter_list|()
block|{
return|return
name|consumersManagerMaxMillis
return|;
block|}
comment|/**      * @param consumersManagerMaxMillis maximum number of milliseconds      *                                  to allow for init() or shutdown()      * @see #getConsumersManagerMaxMillis()      */
specifier|public
name|void
name|setConsumersManagerMaxMillis
parameter_list|(
name|long
name|consumersManagerMaxMillis
parameter_list|)
block|{
name|this
operator|.
name|consumersManagerMaxMillis
operator|=
name|consumersManagerMaxMillis
expr_stmt|;
block|}
block|}
end_class

end_unit

