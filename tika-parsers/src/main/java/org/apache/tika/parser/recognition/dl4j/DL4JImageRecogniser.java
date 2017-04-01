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
name|dl4j
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
name|datavec
operator|.
name|image
operator|.
name|loader
operator|.
name|NativeImageLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|deeplearning4j
operator|.
name|nn
operator|.
name|graph
operator|.
name|ComputationGraph
import|;
end_import

begin_import
import|import
name|org
operator|.
name|deeplearning4j
operator|.
name|nn
operator|.
name|modelimport
operator|.
name|keras
operator|.
name|trainedmodels
operator|.
name|TrainedModelHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|deeplearning4j
operator|.
name|nn
operator|.
name|modelimport
operator|.
name|keras
operator|.
name|trainedmodels
operator|.
name|TrainedModels
import|;
end_import

begin_import
import|import
name|org
operator|.
name|deeplearning4j
operator|.
name|util
operator|.
name|ModelSerializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|nd4j
operator|.
name|linalg
operator|.
name|api
operator|.
name|ndarray
operator|.
name|INDArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|nd4j
operator|.
name|linalg
operator|.
name|dataset
operator|.
name|api
operator|.
name|preprocessor
operator|.
name|DataNormalization
import|;
end_import

begin_import
import|import
name|org
operator|.
name|nd4j
operator|.
name|linalg
operator|.
name|dataset
operator|.
name|api
operator|.
name|preprocessor
operator|.
name|VGG16ImagePreProcessor
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

begin_class
specifier|public
class|class
name|DL4JImageRecogniser
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
name|DL4JImageRecogniser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
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
specifier|private
specifier|static
specifier|final
name|String
name|HOME_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BASE_DIR
init|=
literal|".dl4j/trainedmodels"
decl_stmt|;
specifier|private
specifier|static
name|String
name|MODEL_DIR
init|=
name|HOME_DIR
operator|+
name|File
operator|.
name|separator
operator|+
name|BASE_DIR
decl_stmt|;
specifier|private
specifier|static
name|String
name|MODEL_DIR_PREPROCESSED
init|=
name|MODEL_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"tikaPreprocessed"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|modelType
init|=
literal|"VGG16"
decl_stmt|;
annotation|@
name|Field
specifier|private
name|File
name|modelFile
decl_stmt|;
annotation|@
name|Field
specifier|private
name|String
name|outPattern
init|=
literal|"(.*) \\(score = ([0-9]+\\.[0-9]+)\\)$"
decl_stmt|;
name|File
name|locationToSave
decl_stmt|;
specifier|private
name|boolean
name|available
init|=
literal|false
decl_stmt|;
name|ComputationGraph
name|model
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
name|modelType
operator|.
name|equals
argument_list|(
literal|"VGG16NOTOP"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaConfigException
argument_list|(
literal|"VGG16NOTOP is not supported right now"
argument_list|)
throw|;
comment|/*# TODO hookup VGGNOTOP by uncommenting following code once the issue is resolved by dl4j team 				modelFile = new File(MODEL_DIR_PREPROCESSED+File.separator+"vgg16_notop.zip"); 				locationToSave= new File(MODEL_DIR+File.separator+"tikaPreprocessed"+File.separator+"vgg16.zip");*/
block|}
elseif|else
if|if
condition|(
name|modelType
operator|.
name|equals
argument_list|(
literal|"VGG16"
argument_list|)
condition|)
block|{
name|modelFile
operator|=
operator|new
name|File
argument_list|(
name|MODEL_DIR_PREPROCESSED
operator|+
name|File
operator|.
name|separator
operator|+
literal|"vgg16.zip"
argument_list|)
expr_stmt|;
name|locationToSave
operator|=
operator|new
name|File
argument_list|(
name|MODEL_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"tikaPreprocessed"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"vgg16.zip"
argument_list|)
expr_stmt|;
block|}
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
literal|"Preprocessed Model doesn't exist at {}"
argument_list|,
name|modelFile
argument_list|)
expr_stmt|;
name|TrainedModelHelper
name|helper
decl_stmt|;
if|if
condition|(
name|modelType
operator|.
name|equals
argument_list|(
literal|"VGG16NOTOP"
argument_list|)
condition|)
block|{
name|helper
operator|=
operator|new
name|TrainedModelHelper
argument_list|(
name|TrainedModels
operator|.
name|VGG16NOTOP
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|modelType
operator|.
name|equals
argument_list|(
literal|"VGG16"
argument_list|)
condition|)
block|{
name|helper
operator|=
operator|new
name|TrainedModelHelper
argument_list|(
name|TrainedModels
operator|.
name|VGG16
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|TikaConfigException
argument_list|(
literal|"Unknown or unsupported model"
argument_list|)
throw|;
block|}
name|model
operator|=
name|helper
operator|.
name|loadModel
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Saving the Loaded model for future use. Saved models are more optimised to consume less resource."
argument_list|)
expr_stmt|;
name|ModelSerializer
operator|.
name|writeModel
argument_list|(
name|model
argument_list|,
name|locationToSave
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|available
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|model
operator|=
name|ModelSerializer
operator|.
name|restoreComputationGraph
argument_list|(
name|locationToSave
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Preprocessed Model Loaded from {}"
argument_list|,
name|locationToSave
argument_list|)
expr_stmt|;
name|available
operator|=
literal|true
expr_stmt|;
block|}
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"exception occured"
argument_list|)
expr_stmt|;
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
name|NativeImageLoader
name|loader
init|=
operator|new
name|NativeImageLoader
argument_list|(
literal|224
argument_list|,
literal|224
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|INDArray
name|image
init|=
name|loader
operator|.
name|asMatrix
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|DataNormalization
name|scaler
init|=
operator|new
name|VGG16ImagePreProcessor
argument_list|()
decl_stmt|;
name|scaler
operator|.
name|transform
argument_list|(
name|image
argument_list|)
expr_stmt|;
name|INDArray
index|[]
name|output
init|=
name|model
operator|.
name|output
argument_list|(
literal|false
argument_list|,
name|image
argument_list|)
decl_stmt|;
name|String
name|modelOutput
init|=
name|TrainedModels
operator|.
name|VGG16
operator|.
name|decodePredictions
argument_list|(
name|output
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|modelOutput
operator|=
name|modelOutput
operator|.
name|replace
argument_list|(
literal|"Predictions for batch  :\n"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|modelOutput
operator|=
name|modelOutput
operator|.
name|replace
argument_list|(
literal|"%"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|String
name|objs
index|[]
init|=
name|modelOutput
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
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
name|obj
range|:
name|objs
control|)
block|{
name|String
name|data
index|[]
decl_stmt|;
name|data
operator|=
name|obj
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|double
name|confidence
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|data
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|objects
operator|.
name|add
argument_list|(
operator|new
name|RecognisedObject
argument_list|(
name|data
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
argument_list|,
literal|"eng"
argument_list|,
name|data
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
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

