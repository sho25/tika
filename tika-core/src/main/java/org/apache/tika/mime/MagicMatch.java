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
name|mime
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
name|IOException
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
name|MagicDetector
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

begin_comment
comment|/**  * Defines a magic match.  */
end_comment

begin_class
class|class
name|MagicMatch
implements|implements
name|Clause
block|{
specifier|private
specifier|final
name|MediaType
name|mediaType
decl_stmt|;
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
specifier|private
specifier|final
name|String
name|offset
decl_stmt|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|private
specifier|final
name|String
name|mask
decl_stmt|;
specifier|private
name|MagicDetector
name|detector
init|=
literal|null
decl_stmt|;
name|MagicMatch
parameter_list|(
name|MediaType
name|mediaType
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|offset
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|mask
parameter_list|)
block|{
name|this
operator|.
name|mediaType
operator|=
name|mediaType
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|mask
operator|=
name|mask
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|MagicDetector
name|getDetector
parameter_list|()
block|{
if|if
condition|(
name|detector
operator|==
literal|null
condition|)
block|{
name|detector
operator|=
name|MagicDetector
operator|.
name|parse
argument_list|(
name|mediaType
argument_list|,
name|type
argument_list|,
name|offset
argument_list|,
name|value
argument_list|,
name|mask
argument_list|)
expr_stmt|;
block|}
return|return
name|detector
return|;
block|}
specifier|public
name|boolean
name|eval
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
try|try
block|{
return|return
name|getDetector
argument_list|()
operator|.
name|detect
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
operator|!=
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Should never happen with a ByteArrayInputStream
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|getDetector
argument_list|()
operator|.
name|getLength
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|mediaType
operator|.
name|toString
argument_list|()
operator|+
literal|" "
operator|+
name|type
operator|+
literal|" "
operator|+
name|offset
operator|+
literal|" "
operator|+
name|value
operator|+
literal|" "
operator|+
name|mask
return|;
block|}
block|}
end_class

end_unit

