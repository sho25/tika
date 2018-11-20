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
name|server
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|common
operator|.
name|logging
operator|.
name|AbstractDelegatingLogger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|LogRecord
import|;
end_import

begin_comment
comment|/**  * null logger to swallow client messages  * in {@link TikaServerIntegrationTest}  */
end_comment

begin_class
specifier|public
class|class
name|NullWebClientLogger
extends|extends
name|AbstractDelegatingLogger
block|{
specifier|public
name|NullWebClientLogger
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|resourceBundleName
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|resourceBundleName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Level
name|getLevel
parameter_list|()
block|{
return|return
name|Level
operator|.
name|OFF
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|internalLogFormatted
parameter_list|(
name|String
name|s
parameter_list|,
name|LogRecord
name|logRecord
parameter_list|)
block|{      }
block|}
end_class

end_unit

