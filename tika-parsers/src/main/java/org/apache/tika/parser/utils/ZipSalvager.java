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
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|file
operator|.
name|Files
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
name|ZipException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|archivers
operator|.
name|zip
operator|.
name|ZipArchiveEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|archivers
operator|.
name|zip
operator|.
name|ZipArchiveInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|compress
operator|.
name|archivers
operator|.
name|zip
operator|.
name|ZipArchiveOutputStream
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|ZipSalvager
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ZipSalvager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * This streams the broken zip and rebuilds a new zip that      * is at least a valid zip file.  The contents of the final stream      * may be truncated, but the result should be a valid zip file.      *<p>      * This does nothing fancy to fix the underlying broken zip.      *      * @param brokenZip      * @param salvagedZip      */
specifier|public
specifier|static
name|void
name|salvageCopy
parameter_list|(
name|InputStream
name|brokenZip
parameter_list|,
name|File
name|salvagedZip
parameter_list|)
block|{
try|try
init|(
name|ZipArchiveOutputStream
name|outputStream
init|=
operator|new
name|ZipArchiveOutputStream
argument_list|(
name|salvagedZip
argument_list|)
init|)
block|{
name|ZipArchiveInputStream
name|zipArchiveInputStream
init|=
operator|new
name|ZipArchiveInputStream
argument_list|(
name|brokenZip
argument_list|)
decl_stmt|;
name|ZipArchiveEntry
name|zae
init|=
name|zipArchiveInputStream
operator|.
name|getNextZipEntry
argument_list|()
decl_stmt|;
while|while
condition|(
name|zae
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|zae
operator|.
name|isDirectory
argument_list|()
operator|&&
name|zipArchiveInputStream
operator|.
name|canReadEntryData
argument_list|(
name|zae
argument_list|)
condition|)
block|{
comment|//create a new ZAE and copy over only the name so that
comment|//if there is bad info (e.g. CRC) in brokenZip's zae, that
comment|//won't be propagated or cause an exception
name|outputStream
operator|.
name|putArchiveEntry
argument_list|(
operator|new
name|ZipArchiveEntry
argument_list|(
name|zae
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//this will copy an incomplete stream...so there
comment|//could be truncation of the xml/contents, but the zip file
comment|//should be intact.
name|boolean
name|successfullyCopied
init|=
literal|false
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|zipArchiveInputStream
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
name|successfullyCopied
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//this can hit a "truncated ZipFile" IOException
block|}
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|outputStream
operator|.
name|closeArchiveEntry
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|successfullyCopied
condition|)
block|{
break|break;
block|}
block|}
name|zae
operator|=
name|zipArchiveInputStream
operator|.
name|getNextZipEntry
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ZipException
decl||
name|EOFException
name|e
parameter_list|)
block|{
break|break;
block|}
block|}
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|outputStream
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"problem fixing zip"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|salvageCopy
parameter_list|(
name|File
name|brokenZip
parameter_list|,
name|File
name|salvagedZip
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|brokenZip
operator|.
name|toPath
argument_list|()
argument_list|)
init|)
block|{
name|salvageCopy
argument_list|(
name|is
argument_list|,
name|salvagedZip
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
