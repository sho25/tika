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
name|eval
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|eval
operator|.
name|AbstractProfiler
operator|.
name|NON_EXISTENT_FILE_LENGTH
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

begin_comment
comment|/**  * Simple struct to keep track of relative path of source file (  * original binary file, e.g. /subdir/document1.doc)  * and the extract file (e.g. /subdir/document1.doc.json).  */
end_comment

begin_class
class|class
name|EvalFilePaths
block|{
specifier|private
specifier|final
name|Path
name|relativeSourceFilePath
decl_stmt|;
specifier|private
specifier|final
name|Path
name|extractFile
decl_stmt|;
specifier|private
name|long
name|sourceFileLength
init|=
name|NON_EXISTENT_FILE_LENGTH
decl_stmt|;
specifier|private
name|long
name|extractFileLength
init|=
name|NON_EXISTENT_FILE_LENGTH
decl_stmt|;
specifier|public
name|EvalFilePaths
parameter_list|(
name|Path
name|relativeSourceFilePath
parameter_list|,
name|Path
name|extractFile
parameter_list|,
name|long
name|srcFileLen
parameter_list|)
block|{
name|this
argument_list|(
name|relativeSourceFilePath
argument_list|,
name|extractFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceFileLength
operator|=
name|srcFileLen
expr_stmt|;
block|}
specifier|public
name|EvalFilePaths
parameter_list|(
name|Path
name|relativeSourceFilePath
parameter_list|,
name|Path
name|extractFile
parameter_list|)
block|{
if|if
condition|(
name|extractFile
operator|!=
literal|null
operator|&&
name|Files
operator|.
name|isRegularFile
argument_list|(
name|extractFile
argument_list|)
condition|)
block|{
try|try
block|{
name|extractFileLength
operator|=
name|Files
operator|.
name|size
argument_list|(
name|extractFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//swallow ?
block|}
block|}
name|this
operator|.
name|relativeSourceFilePath
operator|=
name|relativeSourceFilePath
expr_stmt|;
name|this
operator|.
name|extractFile
operator|=
name|extractFile
expr_stmt|;
block|}
specifier|public
name|Path
name|getRelativeSourceFilePath
parameter_list|()
block|{
return|return
name|relativeSourceFilePath
return|;
block|}
comment|//this path may or may not exist and it could be null!
specifier|public
name|Path
name|getExtractFile
parameter_list|()
block|{
return|return
name|extractFile
return|;
block|}
comment|//if it doesn't exist, it'll be -1l.
specifier|public
name|long
name|getSourceFileLength
parameter_list|()
block|{
return|return
name|sourceFileLength
return|;
block|}
specifier|public
name|long
name|getExtractFileLength
parameter_list|()
block|{
return|return
name|extractFileLength
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|EvalFilePaths
name|that
init|=
operator|(
name|EvalFilePaths
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|sourceFileLength
operator|!=
name|that
operator|.
name|sourceFileLength
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|extractFileLength
operator|!=
name|that
operator|.
name|extractFileLength
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|relativeSourceFilePath
operator|!=
literal|null
condition|?
operator|!
name|relativeSourceFilePath
operator|.
name|equals
argument_list|(
name|that
operator|.
name|relativeSourceFilePath
argument_list|)
else|:
name|that
operator|.
name|relativeSourceFilePath
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|extractFile
operator|!=
literal|null
condition|?
name|extractFile
operator|.
name|equals
argument_list|(
name|that
operator|.
name|extractFile
argument_list|)
else|:
name|that
operator|.
name|extractFile
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|relativeSourceFilePath
operator|!=
literal|null
condition|?
name|relativeSourceFilePath
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|extractFile
operator|!=
literal|null
condition|?
name|extractFile
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|sourceFileLength
operator|^
operator|(
name|sourceFileLength
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|extractFileLength
operator|^
operator|(
name|extractFileLength
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"EvalFilePaths{"
operator|+
literal|"relativeSourceFilePath="
operator|+
name|relativeSourceFilePath
operator|+
literal|", extractFile="
operator|+
name|extractFile
operator|+
literal|", sourceFileLength="
operator|+
name|sourceFileLength
operator|+
literal|", extractFileLength="
operator|+
name|extractFileLength
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

