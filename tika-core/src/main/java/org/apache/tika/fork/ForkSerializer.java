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
name|fork
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectStreamClass
import|;
end_import

begin_comment
comment|/**  * Utility class for serializing and deserializing objects. Normal Java  * serialization is used, but each serialized object graph is first written  * or read into an in-memory buffer before it is written to a given byte  * stream or deserialized. This way the underlying stream can be used for  * other things like loading referenced classes while the object graph is  * still being deserialized.  */
end_comment

begin_class
class|class
name|ForkSerializer
extends|extends
name|ObjectInputStream
block|{
comment|/** The class loader used when deserializing objects. */
specifier|private
specifier|final
name|ClassLoader
name|loader
decl_stmt|;
comment|/**      * Creates a new object input stream that uses the given class loader      * when deserializing objects.      *<p>      * Note that this functionality could easily be implemented as a simple      * anonymous {@link ObjectInputStream} subclass, but since the      * functionality is needed during the somewhat complicated bootstrapping      * of the stdin/out communication channel of a forked server process,      * it's better if class has a stable name that can be referenced at      * compile-time by the {@link ForkClient} class.      *      * @param input underlying input stream      * @param loader class loader used when deserializing objects      * @throws IOException if this stream could not be initiated      */
specifier|public
name|ForkSerializer
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
comment|/**      * Loads the identified class from the specified class loader.      *      * @param desc class description      * @return class loaded class      * @throws ClassNotFoundException if the class can not be found      */
annotation|@
name|Override
specifier|protected
name|Class
argument_list|<
name|?
argument_list|>
name|resolveClass
parameter_list|(
name|ObjectStreamClass
name|desc
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|desc
operator|.
name|getName
argument_list|()
argument_list|,
literal|false
argument_list|,
name|loader
argument_list|)
return|;
block|}
block|}
end_class

end_unit

