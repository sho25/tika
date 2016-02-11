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
name|langdetect
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
name|Writer
import|;
end_import

begin_comment
comment|/**  * Writer that builds a language profile based on all the written content.  *  * @since Apache Tika 0.10  */
end_comment

begin_class
specifier|public
class|class
name|LanguageWriter
extends|extends
name|Writer
block|{
specifier|private
specifier|final
name|LanguageDetector
name|detector
decl_stmt|;
specifier|public
name|LanguageWriter
parameter_list|(
name|LanguageDetector
name|detector
parameter_list|)
block|{
name|this
operator|.
name|detector
operator|=
name|detector
expr_stmt|;
name|detector
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the language detector used by this writer. Note that      * the returned language detector gets updated whenever new characters      * are written.      *      * @return language detector      */
specifier|public
name|LanguageDetector
name|getDetector
parameter_list|()
block|{
return|return
name|detector
return|;
block|}
comment|/**      * Returns the detected language based on text written thus far.      *       * @return LanguageResult      */
specifier|public
name|LanguageResult
name|getLanguage
parameter_list|()
block|{
return|return
name|detector
operator|.
name|detect
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|detector
operator|.
name|addText
argument_list|(
name|cbuf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**      * Ignored.      */
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
comment|/**      * Ignored.      */
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
block|{     }
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|detector
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

