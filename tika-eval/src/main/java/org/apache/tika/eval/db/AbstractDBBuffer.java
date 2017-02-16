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
name|db
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * Abstract buffer for map of values and unique ids.  *<p>  * Use this for fast in memory lookups of smallish sets of values.  *  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractDBBuffer
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|rwl
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Lock
name|r
init|=
name|rwl
operator|.
name|readLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Lock
name|w
init|=
name|rwl
operator|.
name|writeLock
argument_list|()
decl_stmt|;
specifier|private
name|int
name|numWrites
init|=
literal|0
decl_stmt|;
specifier|public
name|int
name|getId
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|r
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Integer
name|v
init|=
name|m
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
return|return
name|v
return|;
block|}
block|}
finally|finally
block|{
name|r
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|w
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Integer
name|v
init|=
name|m
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
return|return
name|v
return|;
block|}
name|v
operator|=
name|m
operator|.
name|size
argument_list|()
operator|+
literal|1
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|v
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|numWrites
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
finally|finally
block|{
name|w
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getNumWrites
parameter_list|()
block|{
return|return
name|numWrites
return|;
block|}
comment|//Odd to throw RuntimeException, I know.  It should be
comment|//catastrophic if this buffer can't write to the db.
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|RuntimeException
function_decl|;
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|SQLException
function_decl|;
block|}
end_class

end_unit

