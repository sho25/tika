begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

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
name|ByteArrayOutputStream
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
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|Content
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|JDOMException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|input
operator|.
name|SAXBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|output
operator|.
name|Format
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jdom
operator|.
name|output
operator|.
name|XMLOutputter
import|;
end_import

begin_comment
comment|/**  * Class util  *   *   */
end_comment

begin_class
specifier|public
class|class
name|Utils
block|{
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Content
argument_list|>
name|structuredContent
parameter_list|)
block|{
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|print
argument_list|(
name|structuredContent
argument_list|,
name|sw
argument_list|)
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|print
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Content
argument_list|>
name|structuredContent
parameter_list|)
block|{
name|print
argument_list|(
name|structuredContent
argument_list|,
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|print
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Content
argument_list|>
name|structuredContent
parameter_list|,
name|Writer
name|outputWriter
parameter_list|)
block|{
specifier|final
name|PrintWriter
name|output
init|=
operator|new
name|PrintWriter
argument_list|(
name|outputWriter
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Content
argument_list|>
name|entry
range|:
name|structuredContent
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Content
name|ct
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|ct
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|print
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|": "
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
name|ct
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ct
operator|.
name|getValues
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|print
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|": "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ct
operator|.
name|getValues
argument_list|()
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|==
literal|0
condition|)
name|output
operator|.
name|println
argument_list|(
name|ct
operator|.
name|getValues
argument_list|()
index|[
name|j
index|]
argument_list|)
expr_stmt|;
else|else
block|{
name|output
operator|.
name|println
argument_list|(
literal|"\t"
operator|+
name|ct
operator|.
name|getValues
argument_list|()
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// there are no values, but there is a Content object
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Content '"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"' has no values."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|Document
name|parse
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|org
operator|.
name|jdom
operator|.
name|Document
name|xmlDoc
init|=
operator|new
name|org
operator|.
name|jdom
operator|.
name|Document
argument_list|()
decl_stmt|;
try|try
block|{
name|SAXBuilder
name|builder
init|=
operator|new
name|SAXBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setValidation
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|xmlDoc
operator|=
name|builder
operator|.
name|build
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JDOMException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|xmlDoc
return|;
block|}
specifier|public
specifier|static
name|List
name|unzip
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|List
name|res
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
try|try
block|{
name|ZipInputStream
name|in
init|=
operator|new
name|ZipInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|ZipEntry
name|entry
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|entry
operator|=
name|in
operator|.
name|getNextEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|ByteArrayOutputStream
name|stream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|isEntry
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|stream
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|file
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"tmp"
argument_list|,
literal|"_"
operator|+
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|saveInputStreamInFile
argument_list|(
name|isEntry
argument_list|,
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|in
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
name|logger
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
specifier|private
specifier|static
name|void
name|saveInputStreamInFile
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|saveInXmlFile
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|file
parameter_list|)
block|{
name|Format
name|f
init|=
name|Format
operator|.
name|getPrettyFormat
argument_list|()
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|XMLOutputter
name|xop
init|=
operator|new
name|XMLOutputter
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|xop
operator|.
name|output
argument_list|(
name|doc
argument_list|,
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

