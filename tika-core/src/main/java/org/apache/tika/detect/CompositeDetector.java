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
name|Arrays
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
name|mime
operator|.
name|MediaTypeRegistry
import|;
end_import

begin_comment
comment|/**  * Content type detector that combines multiple different detection mechanisms.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeDetector
implements|implements
name|Detector
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5980683158436430252L
decl_stmt|;
specifier|private
specifier|final
name|MediaTypeRegistry
name|registry
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Detector
argument_list|>
name|detectors
decl_stmt|;
specifier|public
name|CompositeDetector
parameter_list|(
name|MediaTypeRegistry
name|registry
parameter_list|,
name|List
argument_list|<
name|Detector
argument_list|>
name|detectors
parameter_list|)
block|{
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
name|this
operator|.
name|detectors
operator|=
name|detectors
expr_stmt|;
block|}
specifier|public
name|CompositeDetector
parameter_list|(
name|List
argument_list|<
name|Detector
argument_list|>
name|detectors
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|MediaTypeRegistry
argument_list|()
argument_list|,
name|detectors
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CompositeDetector
parameter_list|(
name|Detector
modifier|...
name|detectors
parameter_list|)
block|{
name|this
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|detectors
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|OCTET_STREAM
decl_stmt|;
for|for
control|(
name|Detector
name|detector
range|:
name|getDetectors
argument_list|()
control|)
block|{
name|MediaType
name|detected
init|=
name|detector
operator|.
name|detect
argument_list|(
name|input
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
if|if
condition|(
name|registry
operator|.
name|isSpecializationOf
argument_list|(
name|detected
argument_list|,
name|type
argument_list|)
condition|)
block|{
name|type
operator|=
name|detected
expr_stmt|;
block|}
block|}
return|return
name|type
return|;
block|}
comment|/**      * Returns the component detectors.      */
specifier|public
name|List
argument_list|<
name|Detector
argument_list|>
name|getDetectors
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|detectors
argument_list|)
return|;
block|}
block|}
end_class

end_unit

