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
name|Locale
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
name|io
operator|.
name|TikaInputStream
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
comment|/**  * FileSystem(FS)Resource wraps a file name.  *<p/>  * This class automatically sets the following keys in Metadata:  *<ul>  *<li>Metadata.RESOURCE_NAME_KEY (file name)</li>  *<li>Metadata.CONTENT_LENGTH</li>  *<li>FSProperties.FS_REL_PATH</li>  *<li>FileResource.FILE_EXTENSION</li>  *</ul>,  */
end_comment

begin_class
specifier|public
class|class
name|FSFileResource
implements|implements
name|FileResource
block|{
specifier|private
specifier|final
name|Path
name|fullPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|relativePath
decl_stmt|;
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
comment|/**      *      * @param inputRoot      * @param fullPath      * @see FSFileResource#FSFileResource(Path, Path)      * @deprecated to be removed in Tika 2.0      */
annotation|@
name|Deprecated
specifier|public
name|FSFileResource
parameter_list|(
name|File
name|inputRoot
parameter_list|,
name|File
name|fullPath
parameter_list|)
block|{
name|this
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|inputRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|fullPath
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructor      *      * @param inputRoot the input root for the file      * @param fullPath the full path to the file      * @throws IllegalArgumentException if the fullPath is not      * a child of inputRoot      */
specifier|public
name|FSFileResource
parameter_list|(
name|Path
name|inputRoot
parameter_list|,
name|Path
name|fullPath
parameter_list|)
block|{
name|this
operator|.
name|fullPath
operator|=
name|fullPath
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
comment|//child path must actually be a child
assert|assert
operator|(
name|fullPath
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|inputRoot
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
operator|)
assert|;
name|this
operator|.
name|relativePath
operator|=
name|inputRoot
operator|.
name|relativize
argument_list|(
name|fullPath
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//need to set these now so that the filter can determine
comment|//whether or not to crawl this file
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|fullPath
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|sz
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|sz
operator|=
name|Files
operator|.
name|size
argument_list|(
name|fullPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//swallow
comment|//not existent file will be handled downstream
block|}
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LENGTH
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|sz
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|FSProperties
operator|.
name|FS_REL_PATH
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|FileResource
operator|.
name|FILE_EXTENSION
argument_list|,
name|getExtension
argument_list|(
name|fullPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Simple extension extractor that takes whatever comes after the      * last period in the path.  It returns a lowercased version of the "extension."      *<p>      * If there is no period, it returns an empty string.      *      * @param fullPath full path from which to try to find an extension      * @return the lowercased extension or an empty string      */
specifier|private
name|String
name|getExtension
parameter_list|(
name|Path
name|fullPath
parameter_list|)
block|{
name|String
name|p
init|=
name|fullPath
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|p
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
operator|-
literal|1
condition|)
block|{
return|return
name|p
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
return|return
literal|""
return|;
block|}
comment|/**      *      * @return file's relativePath      */
annotation|@
name|Override
specifier|public
name|String
name|getResourceId
parameter_list|()
block|{
return|return
name|relativePath
return|;
block|}
annotation|@
name|Override
specifier|public
name|Metadata
name|getMetadata
parameter_list|()
block|{
return|return
name|metadata
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|openInputStream
parameter_list|()
throws|throws
name|IOException
block|{
comment|//no need to include Metadata because we already set the
comment|//same information in the initializer
return|return
name|TikaInputStream
operator|.
name|get
argument_list|(
name|fullPath
argument_list|)
return|;
block|}
block|}
end_class

end_unit

