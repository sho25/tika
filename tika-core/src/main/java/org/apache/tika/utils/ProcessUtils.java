begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
package|;
end_package

begin_class
specifier|public
class|class
name|ProcessUtils
block|{
comment|/**      * This should correctly put double-quotes around an argument if      * ProcessBuilder doesn't seem to work (as it doesn't      * on paths with spaces on Windows)      *      * @param arg      * @return      */
specifier|public
specifier|static
name|String
name|escapeCommandLine
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
if|if
condition|(
name|arg
operator|==
literal|null
condition|)
block|{
return|return
name|arg
return|;
block|}
comment|//need to test for " " on windows, can't just add double quotes
comment|//across platforms.
if|if
condition|(
name|arg
operator|.
name|contains
argument_list|(
literal|" "
argument_list|)
operator|&&
name|SystemUtils
operator|.
name|IS_OS_WINDOWS
operator|&&
operator|(
operator|!
name|arg
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
operator|&&
operator|!
name|arg
operator|.
name|endsWith
argument_list|(
literal|"\""
argument_list|)
operator|)
condition|)
block|{
name|arg
operator|=
literal|"\""
operator|+
name|arg
operator|+
literal|"\""
expr_stmt|;
block|}
return|return
name|arg
return|;
block|}
block|}
end_class

end_unit

