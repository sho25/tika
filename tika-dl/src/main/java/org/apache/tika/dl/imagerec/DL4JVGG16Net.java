begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|dl
operator|.
name|imagerec
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
name|config
operator|.
name|InitializableProblemHandler
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
name|api
operator|.
name|Model
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
name|KerasModelImport
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
name|deeplearning4j
operator|.
name|zoo
operator|.
name|PretrainedType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|deeplearning4j
operator|.
name|zoo
operator|.
name|ZooModel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|deeplearning4j
operator|.
name|zoo
operator|.
name|model
operator|.
name|VGG16
import|;
end_import

begin_import
import|import
name|org
operator|.
name|deeplearning4j
operator|.
name|zoo
operator|.
name|util
operator|.
name|imagenet
operator|.
name|ImageNetLabels
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
name|nd4j
operator|.
name|linalg
operator|.
name|factory
operator|.
name|Nd4j
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

begin_class
specifier|public
class|class
name|DL4JVGG16Net
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
name|DL4JVGG16Net
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
literal|".dl4j"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"trainedmodels"
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
name|File
name|modelFile
init|=
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
decl_stmt|;
annotation|@
name|Field
specifier|private
name|File
name|locationToSave
init|=
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
decl_stmt|;
annotation|@
name|Field
specifier|private
name|boolean
name|serialize
init|=
literal|true
decl_stmt|;
annotation|@
name|Field
specifier|private
name|int
name|topN
decl_stmt|;
specifier|private
name|NativeImageLoader
name|imageLoader
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
specifier|private
name|DataNormalization
name|preProcessor
init|=
operator|new
name|VGG16ImagePreProcessor
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|available
init|=
literal|false
decl_stmt|;
specifier|private
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
specifier|private
name|ImageNetLabels
name|imageNetLabels
decl_stmt|;
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
name|checkInitialization
parameter_list|(
name|InitializableProblemHandler
name|problemHandler
parameter_list|)
throws|throws
name|TikaConfigException
block|{
comment|//TODO: what do we want to check here?
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
name|serialize
condition|)
block|{
if|if
condition|(
name|locationToSave
operator|.
name|exists
argument_list|()
condition|)
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
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Preprocessed Model doesn't exist at {}"
argument_list|,
name|locationToSave
argument_list|)
expr_stmt|;
name|locationToSave
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|ZooModel
name|zooModel
init|=
name|VGG16
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|model
operator|=
operator|(
name|ComputationGraph
operator|)
name|zooModel
operator|.
name|initPretrained
argument_list|(
name|PretrainedType
operator|.
name|IMAGENET
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Saving the Loaded model for future use. Saved models are more optimised to consume less resources."
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
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Weight graph model loaded via dl4j Helper functions"
argument_list|)
expr_stmt|;
name|ZooModel
name|zooModel
init|=
name|VGG16
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|model
operator|=
operator|(
name|ComputationGraph
operator|)
name|zooModel
operator|.
name|initPretrained
argument_list|(
name|PretrainedType
operator|.
name|IMAGENET
argument_list|)
expr_stmt|;
block|}
name|imageNetLabels
operator|=
operator|new
name|ImageNetLabels
argument_list|()
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
name|available
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
name|INDArray
name|image
init|=
name|imageLoader
operator|.
name|asMatrix
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|preProcessor
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
return|return
name|predict
argument_list|(
name|output
index|[
literal|0
index|]
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|RecognisedObject
argument_list|>
name|predict
parameter_list|(
name|INDArray
name|predictions
parameter_list|)
block|{
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
name|int
index|[]
name|topNPredictions
init|=
operator|new
name|int
index|[
name|topN
index|]
decl_stmt|;
name|float
index|[]
name|topNProb
init|=
operator|new
name|float
index|[
name|topN
index|]
decl_stmt|;
name|String
name|outLabels
index|[]
init|=
operator|new
name|String
index|[
name|topN
index|]
decl_stmt|;
comment|//brute force collect top N
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|batch
init|=
literal|0
init|;
name|batch
operator|<
name|predictions
operator|.
name|size
argument_list|(
literal|0
argument_list|)
condition|;
name|batch
operator|++
control|)
block|{
name|INDArray
name|currentBatch
init|=
name|predictions
operator|.
name|getRow
argument_list|(
name|batch
argument_list|)
operator|.
name|dup
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|topN
condition|)
block|{
name|topNPredictions
index|[
name|i
index|]
operator|=
name|Nd4j
operator|.
name|argMax
argument_list|(
name|currentBatch
argument_list|,
literal|1
argument_list|)
operator|.
name|getInt
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|topNProb
index|[
name|i
index|]
operator|=
name|currentBatch
operator|.
name|getFloat
argument_list|(
name|batch
argument_list|,
name|topNPredictions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|currentBatch
operator|.
name|putScalar
argument_list|(
literal|0
argument_list|,
name|topNPredictions
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|outLabels
index|[
name|i
index|]
operator|=
name|imageNetLabels
operator|.
name|getLabel
argument_list|(
name|topNPredictions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|objects
operator|.
name|add
argument_list|(
operator|new
name|RecognisedObject
argument_list|(
name|outLabels
index|[
name|i
index|]
argument_list|,
literal|"eng"
argument_list|,
name|outLabels
index|[
name|i
index|]
argument_list|,
name|topNProb
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
return|return
name|objects
return|;
block|}
block|}
end_class

end_unit

