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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * This OutputStream writes all data to the famous<b>/dev/null</b>.  *<p>  * This output stream has no destination (file/socket etc.) and all  * bytes written to it are ignored and lost.  *   * @author Jeremias Maerki  * @since Apache Tika 0.4, copied from Commons IO 1.4  */
end_comment

begin_class
specifier|public
class|class
name|NullOutputStream
extends|extends
name|OutputStream
block|{
comment|/**      * A singleton.      */
specifier|public
specifier|static
specifier|final
name|NullOutputStream
name|NULL_OUTPUT_STREAM
init|=
operator|new
name|NullOutputStream
argument_list|()
decl_stmt|;
comment|/**      * Does nothing - output to<code>/dev/null</code>.      * @param b The bytes to write      * @param off The start offset      * @param len The number of bytes to write      */
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|//to /dev/null
block|}
comment|/**      * Does nothing - output to<code>/dev/null</code>.      * @param b The byte to write      */
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
block|{
comment|//to /dev/null
block|}
comment|/**      * Does nothing - output to<code>/dev/null</code>.      * @param b The bytes to write      * @throws IOException never      */
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
comment|//to /dev/null
block|}
block|}
end_class

end_unit

