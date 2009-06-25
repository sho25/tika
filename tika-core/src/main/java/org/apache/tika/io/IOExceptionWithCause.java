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

begin_comment
comment|/**  * Subclasses IOException with the {@link Throwable} constructors missing before Java 6. If you are using Java 6,  * consider this class deprecated and use {@link IOException}.  *   * @author<a href="http://commons.apache.org/io/">Apache Commons IO</a>  * @since Apache Tika 0.4, copied from Commons IO 1.4  */
end_comment

begin_class
specifier|public
class|class
name|IOExceptionWithCause
extends|extends
name|IOException
block|{
comment|/**      * Defines the serial version UID.      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/**      * Constructs a new instance with the given message and cause.      *<p>      * As specified in {@link Throwable}, the message in the given<code>cause</code> is not used in this instance's      * message.      *</p>      *       * @param message      *            the message (see {@link #getMessage()})      * @param cause      *            the cause (see {@link #getCause()}). A<code>null</code> value is allowed.      */
specifier|public
name|IOExceptionWithCause
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new instance with the given cause.      *<p>      * The message is set to<code>cause==null ? null : cause.toString()</code>, which by default contains the class      * and message of<code>cause</code>. This constructor is useful for call sites that just wrap another throwable.      *</p>      *       * @param cause      *            the cause (see {@link #getCause()}). A<code>null</code> value is allowed.      */
specifier|public
name|IOExceptionWithCause
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
operator|==
literal|null
condition|?
literal|null
else|:
name|cause
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

