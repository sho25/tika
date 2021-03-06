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
name|util
operator|.
name|Collection
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
name|javax
operator|.
name|imageio
operator|.
name|spi
operator|.
name|ServiceRegistry
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
name|ServiceLoader
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
name|MimeTypes
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
name|utils
operator|.
name|ServiceLoaderUtils
import|;
end_import

begin_comment
comment|/**  * A composite detector based on all the {@link Detector} implementations  * available through the {@link ServiceRegistry service provider mechanism}.  *   * Detectors are loaded and returned in a specified order, of user supplied  *  followed by non-MimeType Tika, followed by the Tika MimeType class.  * If you need to control the order of the Detectors, you should instead  *  construct your own {@link CompositeDetector} and pass in the list  *  of Detectors in the required order.  *  * @since Apache Tika 0.9  */
end_comment

begin_class
specifier|public
class|class
name|DefaultDetector
extends|extends
name|CompositeDetector
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|8170114575326908027L
decl_stmt|;
comment|/**      * Finds all statically loadable detectors and sort the list by name,      * rather than discovery order. Detectors are used in the given order,      * so put the Tika parsers last so that non-Tika (user supplied)      * parsers can take precedence.      *      * @param loader service loader      * @return ordered list of statically loadable detectors      */
specifier|private
specifier|static
name|List
argument_list|<
name|Detector
argument_list|>
name|getDefaultDetectors
parameter_list|(
name|MimeTypes
name|types
parameter_list|,
name|ServiceLoader
name|loader
parameter_list|)
block|{
name|List
argument_list|<
name|Detector
argument_list|>
name|detectors
init|=
name|loader
operator|.
name|loadStaticServiceProviders
argument_list|(
name|Detector
operator|.
name|class
argument_list|)
decl_stmt|;
name|ServiceLoaderUtils
operator|.
name|sortLoadedClasses
argument_list|(
name|detectors
argument_list|)
expr_stmt|;
comment|// Finally the Tika MimeTypes as a fallback
name|detectors
operator|.
name|add
argument_list|(
name|types
argument_list|)
expr_stmt|;
return|return
name|detectors
return|;
block|}
specifier|private
specifier|transient
specifier|final
name|ServiceLoader
name|loader
decl_stmt|;
specifier|public
name|DefaultDetector
parameter_list|(
name|MimeTypes
name|types
parameter_list|,
name|ServiceLoader
name|loader
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Detector
argument_list|>
argument_list|>
name|excludeDetectors
parameter_list|)
block|{
name|super
argument_list|(
name|types
operator|.
name|getMediaTypeRegistry
argument_list|()
argument_list|,
name|getDefaultDetectors
argument_list|(
name|types
argument_list|,
name|loader
argument_list|)
argument_list|,
name|excludeDetectors
argument_list|)
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
specifier|public
name|DefaultDetector
parameter_list|(
name|MimeTypes
name|types
parameter_list|,
name|ServiceLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|types
argument_list|,
name|loader
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultDetector
parameter_list|(
name|MimeTypes
name|types
parameter_list|,
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|types
argument_list|,
operator|new
name|ServiceLoader
argument_list|(
name|loader
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultDetector
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|MimeTypes
operator|.
name|getDefaultMimeTypes
argument_list|()
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultDetector
parameter_list|(
name|MimeTypes
name|types
parameter_list|)
block|{
name|this
argument_list|(
name|types
argument_list|,
operator|new
name|ServiceLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultDetector
parameter_list|()
block|{
name|this
argument_list|(
name|MimeTypes
operator|.
name|getDefaultMimeTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Detector
argument_list|>
name|getDetectors
parameter_list|()
block|{
if|if
condition|(
name|loader
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Detector
argument_list|>
name|detectors
init|=
name|loader
operator|.
name|loadDynamicServiceProviders
argument_list|(
name|Detector
operator|.
name|class
argument_list|)
decl_stmt|;
name|detectors
operator|.
name|addAll
argument_list|(
name|super
operator|.
name|getDetectors
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|detectors
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getDetectors
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

