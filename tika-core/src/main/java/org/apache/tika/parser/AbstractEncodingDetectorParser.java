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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|TikaConfig
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
name|detect
operator|.
name|DefaultEncodingDetector
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
name|detect
operator|.
name|EncodingDetector
import|;
end_import

begin_comment
comment|/**  * Abstract base class for parsers that use the AutoDetectReader and need  * to use the {@link EncodingDetector} configured by {@link TikaConfig}  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractEncodingDetectorParser
extends|extends
name|AbstractParser
block|{
specifier|private
name|EncodingDetector
name|encodingDetector
decl_stmt|;
specifier|public
name|AbstractEncodingDetectorParser
parameter_list|()
block|{
name|encodingDetector
operator|=
operator|new
name|DefaultEncodingDetector
argument_list|()
expr_stmt|;
block|}
specifier|public
name|AbstractEncodingDetectorParser
parameter_list|(
name|EncodingDetector
name|encodingDetector
parameter_list|)
block|{
name|this
operator|.
name|encodingDetector
operator|=
name|encodingDetector
expr_stmt|;
block|}
comment|/**      * Look for an EncodingDetetor in the ParseContext.  If it hasn't been      * passed in, use the original EncodingDetector from initialization.      *      * @param parseContext      * @return      */
specifier|protected
name|EncodingDetector
name|getEncodingDetector
parameter_list|(
name|ParseContext
name|parseContext
parameter_list|)
block|{
name|EncodingDetector
name|fromParseContext
init|=
name|parseContext
operator|.
name|get
argument_list|(
name|EncodingDetector
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|fromParseContext
operator|!=
literal|null
condition|)
block|{
return|return
name|fromParseContext
return|;
block|}
return|return
name|getEncodingDetector
argument_list|()
return|;
block|}
specifier|public
name|EncodingDetector
name|getEncodingDetector
parameter_list|()
block|{
return|return
name|encodingDetector
return|;
block|}
specifier|public
name|void
name|setEncodingDetector
parameter_list|(
name|EncodingDetector
name|encodingDetector
parameter_list|)
block|{
name|this
operator|.
name|encodingDetector
operator|=
name|encodingDetector
expr_stmt|;
block|}
block|}
end_class

end_unit
