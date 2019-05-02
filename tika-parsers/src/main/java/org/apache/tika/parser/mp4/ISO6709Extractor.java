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
operator|.
name|mp4
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Matcher
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

begin_class
class|class
name|ISO6709Extractor
implements|implements
name|Serializable
block|{
comment|//based on: https://en.wikipedia.org/wiki/ISO_6709
comment|//strip lat long -- ignore crs for now
specifier|private
specifier|static
specifier|final
name|Pattern
name|ISO6709_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\A([-+])(\\d{2,6})(\\.\\d+)?([-+])(\\d{3,7})(\\.\\d+)?"
argument_list|)
decl_stmt|;
comment|//must be thread safe
specifier|public
name|void
name|extract
parameter_list|(
name|String
name|s
parameter_list|,
name|Metadata
name|m
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Matcher
name|matcher
init|=
name|ISO6709_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|lat
init|=
name|getLat
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|lng
init|=
name|getLng
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|5
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|6
argument_list|)
argument_list|)
decl_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|LATITUDE
argument_list|,
name|lat
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|LONGITUDE
argument_list|,
name|lng
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//ignore problems for now?
block|}
block|}
specifier|private
name|String
name|getLng
parameter_list|(
name|String
name|sign
parameter_list|,
name|String
name|integer
parameter_list|,
name|String
name|flot
parameter_list|)
block|{
name|String
name|flotNormed
init|=
operator|(
name|flot
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|flot
decl_stmt|;
if|if
condition|(
name|integer
operator|.
name|length
argument_list|()
operator|==
literal|3
condition|)
block|{
return|return
name|sign
operator|+
name|integer
operator|+
name|flotNormed
return|;
block|}
elseif|else
if|if
condition|(
name|integer
operator|.
name|length
argument_list|()
operator|==
literal|5
condition|)
block|{
return|return
name|calcDecimalDegrees
argument_list|(
name|sign
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
operator|+
name|flotNormed
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|integer
operator|.
name|length
argument_list|()
operator|==
literal|7
condition|)
block|{
return|return
name|calcDecimalDegrees
argument_list|(
name|sign
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
operator|+
name|flotNormed
argument_list|)
return|;
block|}
else|else
block|{
comment|//ignore problems for now?
block|}
return|return
literal|""
return|;
block|}
specifier|private
name|String
name|getLat
parameter_list|(
name|String
name|sign
parameter_list|,
name|String
name|integer
parameter_list|,
name|String
name|flot
parameter_list|)
block|{
name|String
name|flotNormed
init|=
operator|(
name|flot
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|flot
decl_stmt|;
if|if
condition|(
name|integer
operator|.
name|length
argument_list|()
operator|==
literal|2
condition|)
block|{
return|return
name|sign
operator|+
name|integer
operator|+
name|flotNormed
return|;
block|}
elseif|else
if|if
condition|(
name|integer
operator|.
name|length
argument_list|()
operator|==
literal|4
condition|)
block|{
return|return
name|calcDecimalDegrees
argument_list|(
name|sign
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
operator|+
name|flotNormed
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|integer
operator|.
name|length
argument_list|()
operator|==
literal|6
condition|)
block|{
return|return
name|calcDecimalDegrees
argument_list|(
name|sign
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
argument_list|,
name|integer
operator|.
name|substring
argument_list|(
literal|4
argument_list|,
literal|6
argument_list|)
operator|+
name|flotNormed
argument_list|)
return|;
block|}
else|else
block|{
comment|//ignore problems for now?
block|}
return|return
literal|""
return|;
block|}
specifier|private
name|String
name|calcDecimalDegrees
parameter_list|(
name|String
name|sign
parameter_list|,
name|String
name|degrees
parameter_list|,
name|String
name|minutes
parameter_list|)
block|{
name|double
name|d
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|degrees
argument_list|)
decl_stmt|;
name|d
operator|+=
operator|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|minutes
argument_list|)
operator|/
literal|60
operator|)
expr_stmt|;
return|return
name|sign
operator|+
name|String
operator|.
name|format
argument_list|(
literal|"%.8f"
argument_list|,
name|d
argument_list|)
return|;
block|}
specifier|private
name|String
name|calcDecimalDegrees
parameter_list|(
name|String
name|sign
parameter_list|,
name|String
name|degrees
parameter_list|,
name|String
name|minutes
parameter_list|,
name|String
name|seconds
parameter_list|)
block|{
name|double
name|d
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|degrees
argument_list|)
decl_stmt|;
name|d
operator|+=
operator|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|minutes
argument_list|)
operator|/
literal|60
operator|)
expr_stmt|;
name|d
operator|+=
operator|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|seconds
argument_list|)
operator|/
literal|3600
operator|)
expr_stmt|;
return|return
name|sign
operator|+
name|String
operator|.
name|format
argument_list|(
literal|"%.8f"
argument_list|,
name|d
argument_list|)
return|;
block|}
block|}
end_class

end_unit

