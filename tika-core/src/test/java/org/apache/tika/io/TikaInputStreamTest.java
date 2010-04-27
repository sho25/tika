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
name|io
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
specifier|public
class|class
name|TikaInputStreamTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testFileBased
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|file
init|=
name|createTempFile
argument_list|(
literal|"Hello, World!"
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
operator|new
name|TikaInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The file returned by the getFile() method should"
operator|+
literal|" be the file used to instantiate a TikaInputStream"
argument_list|,
name|file
argument_list|,
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The contents of the TikaInputStream should equal the"
operator|+
literal|" contents of the underlying file"
argument_list|,
literal|"Hello, World!"
argument_list|,
name|readStream
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The close() method must not remove the file used to"
operator|+
literal|" instantiate a TikaInputStream"
argument_list|,
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testStreamBased
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|input
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"Hello, World!"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
operator|new
name|TikaInputStream
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|File
name|file
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|)
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|file
operator|!=
literal|null
operator|&&
name|file
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The contents of the file returned by the getFile method"
operator|+
literal|" should equal the contents of the TikaInputStream"
argument_list|,
literal|"Hello, World!"
argument_list|,
name|readFile
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The contents of the TikaInputStream should not get modified"
operator|+
literal|" by reading the file first"
argument_list|,
literal|"Hello, World!"
argument_list|,
name|readStream
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The close() method must remove the temporary file created"
operator|+
literal|" by a TikaInputStream"
argument_list|,
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|File
name|createTempFile
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"tika-"
argument_list|,
literal|".tmp"
argument_list|)
decl_stmt|;
name|OutputStream
name|stream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|file
return|;
block|}
specifier|private
name|String
name|readFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|readStream
argument_list|(
name|stream
argument_list|)
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|readStream
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

