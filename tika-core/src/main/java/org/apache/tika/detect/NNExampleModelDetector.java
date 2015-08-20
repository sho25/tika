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
name|detect
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_class
specifier|public
class|class
name|NNExampleModelDetector
extends|extends
name|TrainedModelDetector
block|{
specifier|private
specifier|static
specifier|final
name|String
name|EXAMPLE_NNMODEL_FILE
init|=
literal|"tika-example.nnmodel"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|public
name|NNExampleModelDetector
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|NNExampleModelDetector
parameter_list|(
specifier|final
name|File
name|modelFile
parameter_list|)
block|{
name|loadDefaultModels
argument_list|(
name|modelFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|loadDefaultModels
parameter_list|(
name|InputStream
name|modelStream
parameter_list|)
block|{
name|BufferedReader
name|bReader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|modelStream
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|NNTrainedModelBuilder
name|nnBuilder
init|=
operator|new
name|NNTrainedModelBuilder
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|line
operator|=
name|bReader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
name|readDescription
argument_list|(
name|nnBuilder
argument_list|,
name|line
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|readNNParams
argument_list|(
name|nnBuilder
argument_list|,
name|line
argument_list|)
expr_stmt|;
comment|// add this model into map of trained models.
name|super
operator|.
name|registerModels
argument_list|(
name|nnBuilder
operator|.
name|getType
argument_list|()
argument_list|,
name|nnBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to read the default media type registry"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * this method gets overwritten to register load neural network models 	 */
annotation|@
name|Override
specifier|public
name|void
name|loadDefaultModels
parameter_list|(
name|ClassLoader
name|classLoader
parameter_list|)
block|{
if|if
condition|(
name|classLoader
operator|==
literal|null
condition|)
block|{
name|classLoader
operator|=
name|TrainedModelDetector
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
block|}
comment|// This allows us to replicate class.getResource() when using
comment|// the classloader directly
name|String
name|classPrefix
init|=
name|TrainedModelDetector
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|"/"
decl_stmt|;
comment|// Get the core URL, and all the extensions URLs
name|URL
name|modelURL
init|=
name|classLoader
operator|.
name|getResource
argument_list|(
name|classPrefix
operator|+
name|EXAMPLE_NNMODEL_FILE
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|modelURL
operator|.
name|openStream
argument_list|()
expr_stmt|;
name|loadDefaultModels
argument_list|(
name|stream
argument_list|)
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
name|RuntimeException
argument_list|(
literal|"Unable to read the default media type registry"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|stream
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to read the default media type registry"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** 	 * read the comments where the model configuration is written, e.g the 	 * number of inputs, hiddens and output please ensure the first char in the 	 * given string is # In this example grb model file, there are 4 elements 1) 	 * type 2) number of input units 3) number of hidden units. 4) number of 	 * output units. 	 *  	 */
specifier|private
name|void
name|readDescription
parameter_list|(
specifier|final
name|NNTrainedModelBuilder
name|builder
parameter_list|,
specifier|final
name|String
name|line
parameter_list|)
block|{
name|int
name|numInputs
init|=
literal|0
decl_stmt|;
name|int
name|numHidden
init|=
literal|0
decl_stmt|;
name|int
name|numOutputs
init|=
literal|0
decl_stmt|;
name|String
name|tline
init|=
name|line
decl_stmt|;
name|String
index|[]
name|sarr
init|=
name|tline
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
try|try
block|{
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|sarr
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|numInputs
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sarr
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|numHidden
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sarr
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|numOutputs
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sarr
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setNumOfInputs
argument_list|(
name|numInputs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setNumOfHidden
argument_list|(
name|numHidden
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setNumOfOutputs
argument_list|(
name|numOutputs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to parse the model configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Read the next line for the model parameters and populate the build which 	 * later will be used to instantiate the instance of TrainedModel 	 *  	 * @param builder 	 * @param line 	 */
specifier|private
name|void
name|readNNParams
parameter_list|(
specifier|final
name|NNTrainedModelBuilder
name|builder
parameter_list|,
specifier|final
name|String
name|line
parameter_list|)
block|{
name|String
name|tline
init|=
name|line
decl_stmt|;
name|String
index|[]
name|sarr
init|=
name|tline
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|sarr
operator|.
name|length
decl_stmt|;
name|float
index|[]
name|params
init|=
operator|new
name|float
index|[
name|n
index|]
decl_stmt|;
try|try
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|fstr
range|:
name|sarr
control|)
block|{
name|params
index|[
name|i
index|]
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|fstr
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|builder
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to parse the model configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

