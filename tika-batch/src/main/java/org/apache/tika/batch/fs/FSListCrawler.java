begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|batch
operator|.
name|fs
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileNotFoundException
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|batch
operator|.
name|FileResource
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
name|batch
operator|.
name|FileResourceCrawler
import|;
end_import

begin_comment
comment|/**  * Class that "crawls" a list of files.  */
end_comment

begin_class
specifier|public
class|class
name|FSListCrawler
extends|extends
name|FileResourceCrawler
block|{
specifier|private
specifier|final
name|BufferedReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|Path
name|root
decl_stmt|;
comment|/**      *      * @param fileQueue      * @param numConsumers      * @param root      * @param list      * @param encoding      * @throws FileNotFoundException      * @throws UnsupportedEncodingException      * @deprecated      * @see #FSListCrawler(ArrayBlockingQueue, int, Path, Path, Charset)      */
annotation|@
name|Deprecated
specifier|public
name|FSListCrawler
parameter_list|(
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|fileQueue
parameter_list|,
name|int
name|numConsumers
parameter_list|,
name|File
name|root
parameter_list|,
name|File
name|list
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|UnsupportedEncodingException
block|{
name|super
argument_list|(
name|fileQueue
argument_list|,
name|numConsumers
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|list
argument_list|)
argument_list|,
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|root
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructor for a crawler that reads a list of files to process.      *<p>      * The list should be paths relative to the root.      *      * @param fileQueue queue for batch      * @param numConsumers number of consumers      * @param root root input director      * @param list text file list (one file per line) of paths relative to      *             the root for processing      * @param charset charset of the file      * @throws IOException      */
specifier|public
name|FSListCrawler
parameter_list|(
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|fileQueue
parameter_list|,
name|int
name|numConsumers
parameter_list|,
name|Path
name|root
parameter_list|,
name|Path
name|list
parameter_list|,
name|Charset
name|charset
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fileQueue
argument_list|,
name|numConsumers
argument_list|)
expr_stmt|;
name|reader
operator|=
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|list
argument_list|,
name|charset
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|String
name|line
init|=
name|nextLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InterruptedException
argument_list|(
literal|"file adder interrupted"
argument_list|)
throw|;
block|}
name|Path
name|f
init|=
name|Paths
operator|.
name|get
argument_list|(
name|root
operator|.
name|toString
argument_list|()
argument_list|,
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"File doesn't exist: {}"
argument_list|,
name|f
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|line
operator|=
name|nextLine
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"File is a directory: {}"
argument_list|,
name|f
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|line
operator|=
name|nextLine
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|tryToAdd
argument_list|(
operator|new
name|FSFileResource
argument_list|(
name|root
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|nextLine
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|nextLine
parameter_list|()
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
try|try
block|{
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|line
return|;
block|}
block|}
end_class

end_unit

