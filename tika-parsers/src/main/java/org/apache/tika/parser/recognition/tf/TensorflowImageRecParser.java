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
name|io
operator|.
name|IOUtils
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
name|external
operator|.
name|ExternalParser
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Set
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

begin_comment
comment|/**  * This is an implementation of {@link ObjectRecogniser} powered by<a href="http://www.tensorflow.org"> Tensorflow<a/>  *  convolutional neural network (CNN). This implementation binds to Python API using {@link ExternalParser}.  *<br/>  * // NOTE: This is a proof of concept for an efficient implementation using JNI binding to Tensorflow's C++ api.  *  *<br/>  *<p>  *<b>Environment Setup:</b>  *<ol>  *<li> Python must be available</li>  *<li> Tensorflow must be available for import by the python script.<a href="https://www.tensorflow.org/versions/r0.9/get_started/os_setup.html#pip-installation"> Setup Instructions here</a></li>  *<li> All dependencies of tensor flow (such as numpy) must also be available.<a href="https://www.tensorflow.org/versions/r0.9/tutorials/image_recognition/index.html#image-recognition">Follow the image recognition guide and make sure it works</a></li>  *</ol>  *</p>  * @since Apache Tika 1.14  */
end_comment

begin_class
specifier|public
class|class
name|TensorflowImageRecParser
extends|extends
name|ExternalParser
implements|implements
name|ObjectRecogniser
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
name|TensorflowImageRecParser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SCRIPT_FILE_NAME
init|=
literal|"classify_image.py"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_MIMES
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|image
argument_list|(
literal|"jpeg"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|File
name|DEFAULT_SCRIPT_FILE
init|=
operator|new
name|File
argument_list|(
literal|"tensorflow"
operator|+
name|File
operator|.
name|separator
operator|+
name|SCRIPT_FILE_NAME
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|File
name|DEFAULT_MODEL_FILE
init|=
operator|new
name|File
argument_list|(
literal|"tensorflow"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"tf-objectrec-model"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|LineConsumer
name|IGNORED_LINE_LOGGER
init|=
operator|new
name|LineConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|consume
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|executor
init|=
literal|"python"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|File
name|scriptFile
init|=
name|DEFAULT_SCRIPT_FILE
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|modelArg
init|=
literal|"--model_dir"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|File
name|modelFile
init|=
name|DEFAULT_MODEL_FILE
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|imageArg
init|=
literal|"--image_file"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|outPattern
init|=
literal|"(.*) \\(score = ([0-9]+\\.[0-9]+)\\)$"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|availabilityTestArgs
init|=
literal|""
decl_stmt|;
comment|//when no args are given, the script will test itself!
specifier|private
name|boolean
name|available
init|=
literal|false
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedMimes
parameter_list|()
block|{
return|return
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
name|configure
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|TikaConfigException
block|{
name|super
operator|.
name|configure
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|modelFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|modelFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Model doesn't exist at {}. Expecting the script to download it."
argument_list|,
name|modelFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|scriptFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|scriptFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Copying script to : {}"
argument_list|,
name|scriptFile
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|sourceStream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|SCRIPT_FILE_NAME
argument_list|)
init|)
block|{
try|try
init|(
name|OutputStream
name|destStream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|scriptFile
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|sourceStream
argument_list|,
name|destStream
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Copied.."
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|availabilityCheckArgs
init|=
block|{
name|executor
block|,
name|scriptFile
operator|.
name|getAbsolutePath
argument_list|()
block|,
name|modelArg
block|,
name|modelFile
operator|.
name|getAbsolutePath
argument_list|()
block|,
name|availabilityTestArgs
block|}
decl_stmt|;
name|available
operator|=
name|ExternalParser
operator|.
name|check
argument_list|(
name|availabilityCheckArgs
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Available? {}"
argument_list|,
name|available
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|available
condition|)
block|{
return|return;
block|}
name|String
index|[]
name|parseCmd
init|=
block|{
name|executor
block|,
name|scriptFile
operator|.
name|getAbsolutePath
argument_list|()
block|,
name|modelArg
block|,
name|modelFile
operator|.
name|getAbsolutePath
argument_list|()
block|,
name|imageArg
block|,
name|INPUT_FILE_TOKEN
block|,
literal|"--out_file"
block|,
name|OUTPUT_FILE_TOKEN
block|}
decl_stmt|;
comment|//inserting output token to let external parser parse metadata
name|setCommand
argument_list|(
name|parseCmd
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|patterns
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|patterns
operator|.
name|put
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|outPattern
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setMetadataExtractionPatterns
argument_list|(
name|patterns
argument_list|)
expr_stmt|;
name|setIgnoredLineConsumer
argument_list|(
name|IGNORED_LINE_LOGGER
argument_list|)
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
name|Metadata
name|md
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|md
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RecognisedObject
argument_list|>
name|objects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|md
operator|.
name|names
argument_list|()
control|)
block|{
name|double
name|confidence
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|md
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
name|objects
operator|.
name|add
argument_list|(
operator|new
name|RecognisedObject
argument_list|(
name|key
argument_list|,
literal|"eng"
argument_list|,
name|key
argument_list|,
name|confidence
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
block|}
end_class

end_unit

