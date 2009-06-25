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
comment|/**  * Closed input stream. This stream returns -1 to all attempts to read  * something from the stream.  *<p>  * Typically uses of this class include testing for corner cases in methods  * that accept input streams and acting as a sentinel value instead of a  *<code>null</code> input stream.  *  * @since Apache Tika 0.4, copied from Commons IO 1.4  */
end_comment

begin_class
specifier|public
class|class
name|ClosedInputStream
extends|extends
name|InputStream
block|{
comment|/**      * Returns -1 to indicate that the stream is closed.      *      * @return always -1      */
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

