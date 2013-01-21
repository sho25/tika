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
name|txt
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
specifier|public
class|class
name|UniversalEncodingDetector
implements|implements
name|EncodingDetector
block|{
specifier|private
specifier|static
specifier|final
name|int
name|BUFSIZE
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|LOOKAHEAD
init|=
literal|16
operator|*
name|BUFSIZE
decl_stmt|;
specifier|public
name|Charset
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
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|input
operator|.
name|mark
argument_list|(
name|LOOKAHEAD
argument_list|)
expr_stmt|;
try|try
block|{
name|UniversalEncodingListener
name|listener
init|=
operator|new
name|UniversalEncodingListener
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|BUFSIZE
index|]
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
name|int
name|m
init|=
name|input
operator|.
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
while|while
condition|(
name|m
operator|!=
operator|-
literal|1
operator|&&
name|n
operator|<
name|LOOKAHEAD
operator|&&
operator|!
name|listener
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|n
operator|+=
name|m
expr_stmt|;
name|listener
operator|.
name|handleData
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
name|input
operator|.
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|b
operator|.
name|length
argument_list|,
name|LOOKAHEAD
operator|-
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|listener
operator|.
name|dataEnd
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|LinkageError
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// juniversalchardet is not available
block|}
finally|finally
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

