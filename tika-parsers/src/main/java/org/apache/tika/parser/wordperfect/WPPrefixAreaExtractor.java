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
name|wordperfect
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

begin_comment
comment|/**  * Extracts WordPerfect Prefix Area data from a WordPerfect document.  * Applies to both 5.x and 6+ documents.  * @author Pascal Essiembre  */
end_comment

begin_class
specifier|final
class|class
name|WPPrefixAreaExtractor
block|{
specifier|private
name|WPPrefixAreaExtractor
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|//WP5.x:
comment|//  Prefix Area:            16 bytes (standard header)
comment|//WP6.x:
comment|//  Prefix Area:            30 bytes (16 standard header + 14 index header)
specifier|public
specifier|static
name|WPPrefixArea
name|extract
parameter_list|(
name|WPInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|WPPrefixArea
name|prefixArea
init|=
operator|new
name|WPPrefixArea
argument_list|()
decl_stmt|;
name|in
operator|.
name|mark
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|prefixArea
operator|.
name|setFileId
argument_list|(
name|in
operator|.
name|readWPString
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1-4
name|prefixArea
operator|.
name|setDocAreaPointer
argument_list|(
name|in
operator|.
name|readWPLong
argument_list|()
argument_list|)
expr_stmt|;
comment|// 5-8
name|prefixArea
operator|.
name|setProductType
argument_list|(
name|in
operator|.
name|readWP
argument_list|()
argument_list|)
expr_stmt|;
comment|// 9
name|prefixArea
operator|.
name|setFileType
argument_list|(
name|in
operator|.
name|readWPChar
argument_list|()
argument_list|)
expr_stmt|;
comment|// 10
name|prefixArea
operator|.
name|setMajorVersion
argument_list|(
name|in
operator|.
name|readWP
argument_list|()
argument_list|)
expr_stmt|;
comment|// 11
name|prefixArea
operator|.
name|setMinorVersion
argument_list|(
name|in
operator|.
name|readWP
argument_list|()
argument_list|)
expr_stmt|;
comment|// 12
name|prefixArea
operator|.
name|setEncrypted
argument_list|(
name|in
operator|.
name|readWPShort
argument_list|()
operator|!=
literal|0
argument_list|)
expr_stmt|;
comment|// 13-14
name|prefixArea
operator|.
name|setIndexAreaPointer
argument_list|(
name|in
operator|.
name|readWPShort
argument_list|()
argument_list|)
expr_stmt|;
comment|// 15-16
comment|// only applies to 6.x:
name|prefixArea
operator|.
name|setFileSize
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|prefixArea
operator|.
name|getMajorVersion
argument_list|()
operator|==
name|WPPrefixArea
operator|.
name|WP6_MAJOR_VERSION
condition|)
block|{
try|try
block|{
name|in
operator|.
name|skip
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// 4 reserved bytes: skip     // 17-20
name|prefixArea
operator|.
name|setFileSize
argument_list|(
name|in
operator|.
name|readWPLong
argument_list|()
argument_list|)
expr_stmt|;
comment|// 21-24
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// May fail if no index header, which is fine.
block|}
block|}
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|prefixArea
return|;
block|}
block|}
end_class

end_unit

