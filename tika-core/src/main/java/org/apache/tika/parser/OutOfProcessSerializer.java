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

begin_class
class|class
name|OutOfProcessSerializer
extends|extends
name|ObjectInputStream
block|{
specifier|private
specifier|final
name|ClassLoader
name|loader
decl_stmt|;
specifier|public
name|OutOfProcessSerializer
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
name|IOException
throws|,
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
specifier|static
name|void
name|serialize
parameter_list|(
name|DataOutputStream
name|output
parameter_list|,
name|Object
name|object
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|serializer
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|writeObject
argument_list|(
name|object
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|buffer
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|static
name|Object
name|deserialize
parameter_list|(
name|DataInputStream
name|input
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|int
name|n
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|n
index|]
decl_stmt|;
name|input
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|ObjectInputStream
name|deserializer
init|=
operator|new
name|OutOfProcessSerializer
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|,
name|loader
argument_list|)
decl_stmt|;
return|return
name|deserializer
operator|.
name|readObject
argument_list|()
return|;
block|}
block|}
end_class

end_unit

