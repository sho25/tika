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
name|chm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|chm
operator|.
name|exception
operator|.
name|ChmParsingException
import|;
end_import

begin_class
specifier|public
class|class
name|TestUtils
block|{
comment|/** 	 * Converts InputStream to byte array 	 *  	 * @param is 	 *            InputStream 	 * @return byte[] 	 *  	 * @throws IOException 	 */
specifier|public
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|is
init|)
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|nRead
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|TestParameters
operator|.
name|BUFFER_SIZE
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|nRead
operator|=
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|nRead
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|flush
argument_list|()
expr_stmt|;
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toByteArray
argument_list|()
return|;
block|}
else|else
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"InputStream is null"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

