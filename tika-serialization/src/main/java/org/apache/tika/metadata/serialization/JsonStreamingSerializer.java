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
name|metadata
operator|.
name|serialization
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|stream
operator|.
name|JsonWriter
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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
specifier|public
class|class
name|JsonStreamingSerializer
implements|implements
name|AutoCloseable
block|{
specifier|private
specifier|final
name|JsonWriter
name|jsonWriter
decl_stmt|;
name|boolean
name|hasStartedArray
init|=
literal|false
decl_stmt|;
specifier|public
name|JsonStreamingSerializer
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|this
operator|.
name|jsonWriter
operator|=
operator|new
name|JsonWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|hasStartedArray
condition|)
block|{
name|jsonWriter
operator|.
name|beginArray
argument_list|()
expr_stmt|;
name|hasStartedArray
operator|=
literal|true
expr_stmt|;
block|}
name|String
index|[]
name|names
init|=
name|metadata
operator|.
name|names
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|jsonWriter
operator|.
name|beginObject
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|n
range|:
name|names
control|)
block|{
name|jsonWriter
operator|.
name|name
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|String
index|[]
name|values
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|jsonWriter
operator|.
name|value
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jsonWriter
operator|.
name|beginArray
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|v
range|:
name|values
control|)
block|{
name|jsonWriter
operator|.
name|value
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|jsonWriter
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
block|}
name|jsonWriter
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|jsonWriter
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|jsonWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
name|jsonWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

