begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|recognition
operator|.
name|tf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|Field
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
name|config
operator|.
name|Param
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
name|TikaConfigException
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
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
name|mime
operator|.
name|MediaType
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
name|parser
operator|.
name|ParseContext
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
name|parser
operator|.
name|recognition
operator|.
name|ObjectRecogniser
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
name|parser
operator|.
name|recognition
operator|.
name|RecognisedObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

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
name|io
operator|.
name|InputStream
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
name|InvocationTargetException
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLClassLoader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Tensor Flow image recogniser which has high performance.  * This implementation takes addon jar and binds it using reflections without  * without corrupting classpath with incompatible version of dependencies.  *<p>  * The addon jar can be built from https://github.com/thammegowda/tensorflow-grpc-java  *  * @since Apache Tika 1.14  */
end_comment

begin_class
specifier|public
class|class
name|TensorflowGrpcRecogniser
implements|implements
name|ObjectRecogniser
implements|,
name|Closeable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TensorflowGrpcRecogniser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LABEL_LANG
init|=
literal|"en"
decl_stmt|;
specifier|private
specifier|static
name|ClassLoader
name|PARENT_CL
init|=
name|TensorflowGrpcRecogniser
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
static|static
block|{
while|while
condition|(
name|PARENT_CL
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PARENT_CL
operator|=
name|PARENT_CL
operator|.
name|getParent
argument_list|()
expr_stmt|;
comment|//move up the heighrarchy until we get the JDK classloader
block|}
block|}
annotation|@
name|Field
specifier|private
name|String
name|recogniserClass
init|=
literal|"edu.usc.irds.tensorflow.grpc.TensorflowObjectRecogniser"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|host
init|=
literal|"localhost"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|int
name|port
init|=
literal|9000
decl_stmt|;
annotation|@
name|Field
argument_list|(
name|name
operator|=
literal|"addon"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|private
name|File
name|addon
decl_stmt|;
specifier|private
name|boolean
name|available
decl_stmt|;
specifier|private
name|Object
name|instance
decl_stmt|;
specifier|private
name|Method
name|recogniseMethod
decl_stmt|;
specifier|private
name|Method
name|closeMethod
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedMimes
parameter_list|()
block|{
return|return
name|TensorflowImageRecParser
operator|.
name|SUPPORTED_MIMES
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|available
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Param
argument_list|>
name|params
parameter_list|)
throws|throws
name|TikaConfigException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|addon
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TikaConfigException
argument_list|(
literal|"File "
operator|+
name|addon
operator|+
literal|" doesnt exists"
argument_list|)
throw|;
block|}
name|URL
index|[]
name|urls
init|=
block|{
name|addon
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
block|}
decl_stmt|;
name|URLClassLoader
name|loader
init|=
operator|new
name|URLClassLoader
argument_list|(
name|urls
argument_list|,
name|PARENT_CL
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|recogniserClass
argument_list|,
literal|true
argument_list|,
name|loader
argument_list|)
decl_stmt|;
name|instance
operator|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|recogniseMethod
operator|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"recognise"
argument_list|,
name|InputStream
operator|.
name|class
argument_list|)
expr_stmt|;
name|closeMethod
operator|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"close"
argument_list|)
expr_stmt|;
name|available
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaConfigException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RecognisedObject
argument_list|>
name|recognise
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|List
argument_list|<
name|RecognisedObject
argument_list|>
name|recObjs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|Object
name|result
init|=
name|recogniseMethod
operator|.
name|invoke
argument_list|(
name|instance
argument_list|,
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
argument_list|>
name|objects
init|=
operator|(
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
argument_list|>
operator|)
name|result
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|object
range|:
name|objects
control|)
block|{
name|RecognisedObject
name|recObj
init|=
operator|new
name|RecognisedObject
argument_list|(
name|object
operator|.
name|getKey
argument_list|()
argument_list|,
name|LABEL_LANG
argument_list|,
name|object
operator|.
name|getKey
argument_list|()
argument_list|,
name|object
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|recObjs
operator|.
name|add
argument_list|(
name|recObj
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Result is null"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|recObjs
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closeMethod
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|closeMethod
operator|.
name|invoke
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

