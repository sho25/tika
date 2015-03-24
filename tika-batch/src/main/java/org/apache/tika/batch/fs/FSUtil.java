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
name|batch
operator|.
name|fs
package|;
end_package

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
name|util
operator|.
name|UUID
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

begin_comment
comment|/**  * Utility class to handle some common issues when  * reading from and writing to a file system (FS).  */
end_comment

begin_class
specifier|public
class|class
name|FSUtil
block|{
specifier|public
specifier|static
name|boolean
name|checkThisIsAncestorOfThat
parameter_list|(
name|File
name|ancestor
parameter_list|,
name|File
name|child
parameter_list|)
block|{
name|int
name|ancLen
init|=
name|ancestor
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|childLen
init|=
name|child
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|childLen
operator|<=
name|ancLen
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|childBase
init|=
name|child
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|ancLen
argument_list|)
decl_stmt|;
return|return
name|childBase
operator|.
name|equals
argument_list|(
name|ancestor
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|checkThisIsAncestorOfOrSameAsThat
parameter_list|(
name|File
name|ancestor
parameter_list|,
name|File
name|child
parameter_list|)
block|{
if|if
condition|(
name|ancestor
operator|.
name|equals
argument_list|(
name|child
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|checkThisIsAncestorOfThat
argument_list|(
name|ancestor
argument_list|,
name|child
argument_list|)
return|;
block|}
specifier|public
enum|enum
name|HANDLE_EXISTING
block|{
name|OVERWRITE
block|,
name|RENAME
block|,
name|SKIP
block|}
specifier|private
specifier|final
specifier|static
name|Pattern
name|FILE_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\A(.*?)(?:\\((\\d+)\\))?\\.([^\\.]+)\\Z"
argument_list|)
decl_stmt|;
comment|/**      * Given an output root and an initial relative path,      * return the output file according to the HANDLE_EXISTING strategy      *<p/>      * In the most basic use case, given a root directory "input",      * a file's relative path "dir1/dir2/fileA.docx", and an output directory      * "output", the output file would be "output/dir1/dir2/fileA.docx."      *<p/>      * If HANDLE_EXISTING is set to OVERWRITE, this will not check to see if the output already exists,      * and the returned file could overwrite an existing file!!!      *<p/>      * If HANDLE_EXISTING is set to RENAME, this will try to increment a counter at the end of      * the file name (fileA(2).docx) until there is a file name that doesn't exist.      *<p/>      * This will return null if handleExisting == HANDLE_EXISTING.SKIP and      * the candidate file already exists.      *<p/>      * This will throw an IOException if HANDLE_EXISTING is set to      * RENAME, and a candidate cannot output file cannot be found      * after trying to increment the file count (e.g. fileA(2).docx) 10000 times      * and then after trying 20,000 UUIDs.      *      * @param outputRoot directory root for output      * @param initialRelativePath initial relative path (including file name, which may be renamed)      * @param handleExisting what to do if the output file exists      * @param suffix suffix to add to files, can be null      * @return output file or null if no output file should be created      * @throws java.io.IOException      */
specifier|public
specifier|static
name|File
name|getOutputFile
parameter_list|(
name|File
name|outputRoot
parameter_list|,
name|String
name|initialRelativePath
parameter_list|,
name|HANDLE_EXISTING
name|handleExisting
parameter_list|,
name|String
name|suffix
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|localSuffix
init|=
operator|(
name|suffix
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|suffix
decl_stmt|;
name|File
name|cand
init|=
operator|new
name|File
argument_list|(
name|outputRoot
argument_list|,
name|initialRelativePath
operator|+
literal|"."
operator|+
name|localSuffix
argument_list|)
decl_stmt|;
if|if
condition|(
name|cand
operator|.
name|isFile
argument_list|()
condition|)
block|{
if|if
condition|(
name|handleExisting
operator|.
name|equals
argument_list|(
name|HANDLE_EXISTING
operator|.
name|OVERWRITE
argument_list|)
condition|)
block|{
return|return
name|cand
return|;
block|}
elseif|else
if|if
condition|(
name|handleExisting
operator|.
name|equals
argument_list|(
name|HANDLE_EXISTING
operator|.
name|SKIP
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|//if we're here, the output file exists, and
comment|//we must find a new name for it.
comment|//groups for "testfile(1).txt":
comment|//group(1) is "testfile"
comment|//group(2) is 1
comment|//group(3) is "txt"
comment|//Note: group(2) can be null
name|int
name|cnt
init|=
literal|0
decl_stmt|;
name|String
name|fNameBase
init|=
literal|null
decl_stmt|;
name|String
name|fNameExt
init|=
literal|""
decl_stmt|;
comment|//this doesn't include the addition of the localSuffix
name|File
name|candOnly
init|=
operator|new
name|File
argument_list|(
name|outputRoot
argument_list|,
name|initialRelativePath
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|FILE_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|candOnly
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|fNameBase
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cnt
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
if|if
condition|(
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|fNameExt
operator|=
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
name|File
name|outputParent
init|=
name|cand
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
while|while
condition|(
name|fNameBase
operator|!=
literal|null
operator|&&
name|cand
operator|.
name|isFile
argument_list|()
operator|&&
operator|++
name|cnt
operator|<
literal|10000
condition|)
block|{
name|String
name|candFileName
init|=
name|fNameBase
operator|+
literal|"("
operator|+
name|cnt
operator|+
literal|")."
operator|+
name|fNameExt
operator|+
literal|""
operator|+
name|localSuffix
decl_stmt|;
name|cand
operator|=
operator|new
name|File
argument_list|(
name|outputParent
argument_list|,
name|candFileName
argument_list|)
expr_stmt|;
block|}
comment|//reset count to 0 and try 20000 times
name|cnt
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|cand
operator|.
name|isFile
argument_list|()
operator|&&
name|cnt
operator|++
operator|<
literal|20000
condition|)
block|{
name|UUID
name|uid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|cand
operator|=
operator|new
name|File
argument_list|(
name|outputParent
argument_list|,
name|uid
operator|.
name|toString
argument_list|()
operator|+
name|fNameExt
operator|+
literal|""
operator|+
name|localSuffix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cand
operator|.
name|isFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Couldn't find candidate output file after trying "
operator|+
literal|"very, very hard"
argument_list|)
throw|;
block|}
return|return
name|cand
return|;
block|}
block|}
end_class

end_unit
