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
name|parser
operator|.
name|microsoft
operator|.
name|onenote
package|;
end_package

begin_class
class|class
name|IndentUtil
block|{
specifier|public
specifier|static
name|String
name|getIndent
parameter_list|(
name|int
name|indentLevel
parameter_list|)
block|{
name|String
name|retval
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indentLevel
condition|;
operator|++
name|i
control|)
block|{
name|retval
operator|+=
literal|"  "
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
block|}
end_class

end_unit

