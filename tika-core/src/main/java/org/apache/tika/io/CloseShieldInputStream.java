begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Proxy stream that prevents the underlying input stream from being closed.  *<p>  * This class is typically used in cases where an input stream needs to be  * passed to a component that wants to explicitly close the stream even if  * more input would still be available to other components.  *  * @since Apache Tika 0.4, copied from Commons IO 1.4  */
end_comment

begin_class
specifier|public
class|class
name|CloseShieldInputStream
extends|extends
name|ProxyInputStream
block|{
comment|/**      * Creates a proxy that shields the given input stream from being      * closed.      *      * @param in underlying input stream      */
specifier|public
name|CloseShieldInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Replaces the underlying input stream with a {@link ClosedInputStream}      * sentinel. The original input stream will remain open, but this proxy      * will appear closed.      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|in
operator|=
operator|new
name|ClosedInputStream
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

