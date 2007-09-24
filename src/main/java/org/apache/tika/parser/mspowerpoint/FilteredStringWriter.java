begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|mspowerpoint
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_comment
comment|/**  * Writes to optimize ASCII output. Not needed chars are filtered (ignored).  *   *   */
end_comment

begin_class
specifier|public
class|class
name|FilteredStringWriter
extends|extends
name|StringWriter
block|{
comment|/**    * @see StringWriter#StringWriter()    */
specifier|public
name|FilteredStringWriter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * @param initialSize    * @see StringWriter#StringWriter(int)    */
specifier|public
name|FilteredStringWriter
parameter_list|(
specifier|final
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Chars which are not useful for Nutch indexing are filtered (ignored) on    * writing to the writer.    *     * @see java.io.Writer#write(int)    */
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|int
name|ch
parameter_list|)
block|{
if|if
condition|(
name|ch
operator|==
literal|'\r'
condition|)
block|{
comment|// PowerPoint seems to store files with \r as the line break
comment|// -> unify to platform specific format
name|super
operator|.
name|write
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
literal|0
condition|)
block|{
name|super
operator|.
name|write
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
literal|'\b'
condition|)
block|{
comment|// ignore it
block|}
elseif|else
if|if
condition|(
name|Character
operator|.
name|isISOControl
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
condition|)
block|{
comment|// replace by blank
comment|// super.write(' ');
block|}
elseif|else
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
condition|)
block|{
comment|// unify to blank
name|super
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|write
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

