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
name|fork
package|;
end_package

begin_class
class|class
name|TimeoutLimits
block|{
specifier|private
specifier|final
name|long
name|pulseMS
decl_stmt|;
specifier|private
specifier|final
name|long
name|parseTimeoutMS
decl_stmt|;
specifier|private
specifier|final
name|long
name|waitTimeoutMS
decl_stmt|;
name|TimeoutLimits
parameter_list|(
name|long
name|pulseMS
parameter_list|,
name|long
name|parseTimeoutMS
parameter_list|,
name|long
name|waitTimeoutMS
parameter_list|)
block|{
name|this
operator|.
name|pulseMS
operator|=
name|pulseMS
expr_stmt|;
name|this
operator|.
name|parseTimeoutMS
operator|=
name|parseTimeoutMS
expr_stmt|;
name|this
operator|.
name|waitTimeoutMS
operator|=
name|waitTimeoutMS
expr_stmt|;
block|}
specifier|public
name|long
name|getPulseMS
parameter_list|()
block|{
return|return
name|pulseMS
return|;
block|}
specifier|public
name|long
name|getParseTimeoutMS
parameter_list|()
block|{
return|return
name|parseTimeoutMS
return|;
block|}
specifier|public
name|long
name|getWaitTimeoutMS
parameter_list|()
block|{
return|return
name|waitTimeoutMS
return|;
block|}
block|}
end_class

end_unit
