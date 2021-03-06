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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
operator|.
name|Lookup
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
operator|.
name|constant
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
operator|.
name|dropArguments
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
operator|.
name|filterReturnValue
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
operator|.
name|guardWithTest
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
operator|.
name|lookup
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodType
operator|.
name|methodType
import|;
end_import

begin_comment
comment|/**  * Copied/pasted from the Apache Lucene/Solr project.  */
end_comment

begin_class
specifier|public
class|class
name|MappedBufferCleaner
block|{
comment|/** Reference to a BufferCleaner that does unmapping; {@code null} if not supported. */
specifier|private
specifier|static
specifier|final
name|BufferCleaner
name|CLEANER
decl_stmt|;
comment|/**      *<code>true</code>, if this platform supports unmapping mmapped files.      */
specifier|public
specifier|static
specifier|final
name|boolean
name|UNMAP_SUPPORTED
decl_stmt|;
comment|/**      * if {@link #UNMAP_SUPPORTED} is {@code false}, this contains the reason why unmapping is not supported.      */
specifier|public
specifier|static
specifier|final
name|String
name|UNMAP_NOT_SUPPORTED_REASON
decl_stmt|;
static|static
block|{
specifier|final
name|Object
name|hack
init|=
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|(
name|PrivilegedAction
argument_list|<
name|Object
argument_list|>
operator|)
name|MappedBufferCleaner
operator|::
name|unmapHackImpl
argument_list|)
decl_stmt|;
if|if
condition|(
name|hack
operator|instanceof
name|BufferCleaner
condition|)
block|{
name|CLEANER
operator|=
operator|(
name|BufferCleaner
operator|)
name|hack
expr_stmt|;
name|UNMAP_SUPPORTED
operator|=
literal|true
expr_stmt|;
name|UNMAP_NOT_SUPPORTED_REASON
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|CLEANER
operator|=
literal|null
expr_stmt|;
name|UNMAP_SUPPORTED
operator|=
literal|false
expr_stmt|;
name|UNMAP_NOT_SUPPORTED_REASON
operator|=
name|hack
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * If a cleaner is available, this buffer will be cleaned.      * Otherwise, this is a no-op.      *      * @param b buffer to clean; no-op if buffer is null      * @throws IOException      */
specifier|public
specifier|static
name|void
name|freeBuffer
parameter_list|(
name|ByteBuffer
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|CLEANER
operator|!=
literal|null
operator|&&
name|b
operator|!=
literal|null
condition|)
block|{
name|CLEANER
operator|.
name|freeBuffer
argument_list|(
literal|""
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Copied/pasted from Lucene's MMapDirectory
specifier|private
interface|interface
name|BufferCleaner
block|{
name|void
name|freeBuffer
parameter_list|(
name|String
name|resourceDescription
parameter_list|,
name|ByteBuffer
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|//"Needs access to private APIs in DirectBuffer, sun.misc.Cleaner, and sun.misc.Unsafe to enable hack")
specifier|private
specifier|static
name|Object
name|unmapHackImpl
parameter_list|()
block|{
specifier|final
name|Lookup
name|lookup
init|=
name|lookup
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
comment|// *** sun.misc.Unsafe unmapping (Java 9+) ***
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|unsafeClass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.Unsafe"
argument_list|)
decl_stmt|;
comment|// first check if Unsafe has the right method, otherwise we can give up
comment|// without doing any security critical stuff:
specifier|final
name|MethodHandle
name|unmapper
init|=
name|lookup
operator|.
name|findVirtual
argument_list|(
name|unsafeClass
argument_list|,
literal|"invokeCleaner"
argument_list|,
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|ByteBuffer
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
comment|// fetch the unsafe instance and bind it to the virtual MH:
specifier|final
name|Field
name|f
init|=
name|unsafeClass
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|theUnsafe
init|=
name|f
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
return|return
name|newBufferCleaner
argument_list|(
name|ByteBuffer
operator|.
name|class
argument_list|,
name|unmapper
operator|.
name|bindTo
argument_list|(
name|theUnsafe
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
comment|// rethrow to report errors correctly (we need to catch it here, as we also catch RuntimeException below!):
throw|throw
name|se
throw|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
decl||
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// *** sun.misc.Cleaner unmapping (Java 8) ***
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|directBufferClass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"java.nio.DirectByteBuffer"
argument_list|)
decl_stmt|;
specifier|final
name|Method
name|m
init|=
name|directBufferClass
operator|.
name|getMethod
argument_list|(
literal|"cleaner"
argument_list|)
decl_stmt|;
name|m
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|MethodHandle
name|directBufferCleanerMethod
init|=
name|lookup
operator|.
name|unreflect
argument_list|(
name|m
argument_list|)
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|cleanerClass
init|=
name|directBufferCleanerMethod
operator|.
name|type
argument_list|()
operator|.
name|returnType
argument_list|()
decl_stmt|;
comment|/* "Compile" a MH that basically is equivalent to the following code:                  * void unmapper(ByteBuffer byteBuffer) {                  *   sun.misc.Cleaner cleaner = ((java.nio.DirectByteBuffer) byteBuffer).cleaner();                  *   if (Objects.nonNull(cleaner)) {                  *     cleaner.clean();                  *   } else {                  *     noop(cleaner); // the noop is needed because MethodHandles#guardWithTest always needs ELSE                  *   }                  * }                  */
specifier|final
name|MethodHandle
name|cleanMethod
init|=
name|lookup
operator|.
name|findVirtual
argument_list|(
name|cleanerClass
argument_list|,
literal|"clean"
argument_list|,
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|MethodHandle
name|nonNullTest
init|=
name|lookup
operator|.
name|findStatic
argument_list|(
name|Objects
operator|.
name|class
argument_list|,
literal|"nonNull"
argument_list|,
name|methodType
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|asType
argument_list|(
name|methodType
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|cleanerClass
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|MethodHandle
name|noop
init|=
name|dropArguments
argument_list|(
name|constant
argument_list|(
name|Void
operator|.
name|class
argument_list|,
literal|null
argument_list|)
operator|.
name|asType
argument_list|(
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
name|cleanerClass
argument_list|)
decl_stmt|;
specifier|final
name|MethodHandle
name|unmapper
init|=
name|filterReturnValue
argument_list|(
name|directBufferCleanerMethod
argument_list|,
name|guardWithTest
argument_list|(
name|nonNullTest
argument_list|,
name|cleanMethod
argument_list|,
name|noop
argument_list|)
argument_list|)
operator|.
name|asType
argument_list|(
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|ByteBuffer
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|newBufferCleaner
argument_list|(
name|directBufferClass
argument_list|,
name|unmapper
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
return|return
literal|"Unmapping is not supported, because not all required permissions are given to the Tika JAR file: "
operator|+
name|se
operator|+
literal|" [Please grant at least the following permissions: RuntimePermission(\"accessClassInPackage.sun.misc\") "
operator|+
literal|" and ReflectPermission(\"suppressAccessChecks\")]"
return|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
decl||
name|RuntimeException
name|e
parameter_list|)
block|{
return|return
literal|"Unmapping is not supported on this platform, because internal Java APIs are not compatible with this Lucene version: "
operator|+
name|e
return|;
block|}
block|}
specifier|private
specifier|static
name|BufferCleaner
name|newBufferCleaner
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|unmappableBufferClass
parameter_list|,
specifier|final
name|MethodHandle
name|unmapper
parameter_list|)
block|{
assert|assert
name|Objects
operator|.
name|equals
argument_list|(
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|ByteBuffer
operator|.
name|class
argument_list|)
argument_list|,
name|unmapper
operator|.
name|type
argument_list|()
argument_list|)
assert|;
return|return
parameter_list|(
name|String
name|resourceDescription
parameter_list|,
name|ByteBuffer
name|buffer
parameter_list|)
lambda|->
block|{
if|if
condition|(
operator|!
name|buffer
operator|.
name|isDirect
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unmapping only works with direct buffers"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|unmappableBufferClass
operator|.
name|isInstance
argument_list|(
name|buffer
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"buffer is not an instance of "
operator|+
name|unmappableBufferClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Throwable
name|error
init|=
name|AccessController
operator|.
name|doPrivileged
argument_list|(
call|(
name|PrivilegedAction
argument_list|<
name|Throwable
argument_list|>
call|)
argument_list|()
operator|->
block|{
try|try
block|{
name|unmapper
operator|.
name|invokeExact
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
name|t
return|;
block|}
block|}
block|)
return|;
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to unmap the mapped buffer: "
operator|+
name|resourceDescription
argument_list|,
name|error
argument_list|)
throw|;
block|}
block|}
empty_stmt|;
block|}
end_class

unit|}
end_unit

