begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|exception
operator|.
name|TikaException
import|;
end_import

begin_comment
comment|/**  * Unwrap TikaExceptions and other wrappers that we might not care about  * in downstream analysis.  This is similar to  * what tika-server does when returning stack traces.  */
end_comment

begin_class
specifier|public
class|class
name|TikaExceptionFilter
block|{
comment|/**      * Unwrap TikaExceptions and other wrappers that users might not      * care about in downstream analysis.      *      * @param t throwable to filter      * @return filtered throwable      */
specifier|public
name|Throwable
name|filter
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|TikaException
condition|)
block|{
name|Throwable
name|cause
init|=
name|t
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
return|return
name|cause
return|;
block|}
block|}
return|return
name|t
return|;
block|}
comment|/**      * This calls {@link #filter} and then prints the filtered      *<code>Throwable</code>to a<code>String</code>.      *      * @param t throwable      * @return a filtered version of the StackTrace      */
specifier|public
name|String
name|getStackTrace
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|Throwable
name|filtered
init|=
name|filter
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|w
init|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
decl_stmt|;
name|filtered
operator|.
name|printStackTrace
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|stringWriter
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|stringWriter
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit
