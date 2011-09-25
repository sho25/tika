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
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * Utility class for tracking and ultimately closing or otherwise disposing  * a collection of temporary resources.  *<p>  * Note that this class is not thread-safe.  *  * @since Apache Tika 0.10  */
end_comment

begin_class
specifier|public
class|class
name|TemporaryResources
implements|implements
name|Closeable
block|{
comment|/**      * Tracked resources in LIFO order.      */
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Closeable
argument_list|>
name|resources
init|=
operator|new
name|LinkedList
argument_list|<
name|Closeable
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Directory for temporary files,<code>null</code> for the system default.      */
specifier|private
name|File
name|tmp
init|=
literal|null
decl_stmt|;
comment|/**      * Sets the directory to be used for the temporary files created by      * the {@link #createTemporaryFile()} method.      *      * @param tmp temporary file directory,      *            or<code>null</code> for the system default      */
specifier|public
name|void
name|setTemporaryFileDirectory
parameter_list|(
name|File
name|tmp
parameter_list|)
block|{
name|this
operator|.
name|tmp
operator|=
name|tmp
expr_stmt|;
block|}
comment|/**      * Creates and returns a temporary file that will automatically be      * deleted when the {@link #close()} method is called.      *      * @return      * @throws IOException      */
specifier|public
name|File
name|createTemporaryFile
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|File
name|file
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"apache-tika-"
argument_list|,
literal|".tmp"
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
name|addResource
argument_list|(
operator|new
name|Closeable
argument_list|()
block|{
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete temporary file "
operator|+
name|file
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
comment|/**      * Adds a new resource to the set of tracked resources that will all be      * closed when the {@link #close()} method is called.      *      * @param resource resource to be tracked      */
specifier|public
name|void
name|addResource
parameter_list|(
name|Closeable
name|resource
parameter_list|)
block|{
name|resources
operator|.
name|addFirst
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the latest of the tracked resources that implements or      * extends the given interface or class.      *      * @param klass interface or class      * @return matching resource, or<code>null</code> if not found      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
parameter_list|<
name|T
extends|extends
name|Closeable
parameter_list|>
name|T
name|getResource
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|klass
parameter_list|)
block|{
for|for
control|(
name|Closeable
name|resource
range|:
name|resources
control|)
block|{
if|if
condition|(
name|klass
operator|.
name|isAssignableFrom
argument_list|(
name|resource
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|resource
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Closes all tracked resources. The resources are closed in reverse order      * from how they were added.      *<p>      * Any thrown exceptions from managed resources are collected and      * then re-thrown only once all the resources have been closed.      *      * @throws IOException if one or more of the tracked resources      *                     could not be closed      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Release all resources and keep track of any exceptions
name|List
argument_list|<
name|IOException
argument_list|>
name|exceptions
init|=
operator|new
name|LinkedList
argument_list|<
name|IOException
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Closeable
name|resource
range|:
name|resources
control|)
block|{
try|try
block|{
name|resource
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
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|resources
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Throw any exceptions that were captured from above
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|exceptions
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
throw|throw
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
literal|"Multiple IOExceptions"
operator|+
name|exceptions
argument_list|,
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Calls the {@link #close()} method and wraps the potential      * {@link IOException} into a {@link TikaException} for convenience      * when used within Tika.      *      * @throws TikaException if one or more of the tracked resources      *                       could not be closed      */
specifier|public
name|void
name|dispose
parameter_list|()
throws|throws
name|TikaException
block|{
try|try
block|{
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
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Failed to close temporary resources"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

