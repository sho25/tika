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
name|utils
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_class
specifier|public
class|class
name|ExceptionUtils
block|{
specifier|private
specifier|final
specifier|static
name|Pattern
name|MSG_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|":[^\r\n]+"
argument_list|)
decl_stmt|;
comment|/**      * Simple util to get stack trace.      *<p>      * This will unwrap a TikaException and return the cause if not null      *<p>      * NOTE: If your stacktraces are truncated, make sure to start your jvm      * with: -XX:-OmitStackTraceInFastThrow      *      * @param t throwable      * @return      * @throws IOException      */
specifier|public
specifier|static
name|String
name|getFilteredStackTrace
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|t
decl_stmt|;
if|if
condition|(
operator|(
name|t
operator|instanceof
name|TikaException
operator|)
operator|&&
name|t
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cause
operator|=
name|t
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
name|getStackTrace
argument_list|(
name|cause
argument_list|)
return|;
block|}
comment|/**      * Get the full stacktrace as a string      * @param t      * @return      */
specifier|public
specifier|static
name|String
name|getStackTrace
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|Writer
name|result
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|writer
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|result
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|result
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Utility method to trim the message from a stack trace      * string.      *<p>      * E.g.<code>java.lang.IllegalStateException: Potential loop detected</code>      * will be trimmed to<code>java.lang.IllegalStateException</code>      * @param trace string view of stack trace      * @return trimmed stack trace      */
specifier|public
specifier|static
name|String
name|trimMessage
parameter_list|(
name|String
name|trace
parameter_list|)
block|{
name|Matcher
name|msgMatcher
init|=
name|MSG_PATTERN
operator|.
name|matcher
argument_list|(
name|trace
argument_list|)
decl_stmt|;
if|if
condition|(
name|msgMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
name|msgMatcher
operator|.
name|replaceFirst
argument_list|(
literal|""
argument_list|)
return|;
block|}
return|return
name|trace
return|;
block|}
block|}
end_class

end_unit
