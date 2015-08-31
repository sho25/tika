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
name|commons
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
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|chm
operator|.
name|core
operator|.
name|ChmCommons
operator|.
name|EntryType
import|;
end_import

begin_comment
comment|/**  * Holds test parameters such as verification points  */
end_comment

begin_class
specifier|public
class|class
name|TestParameters
block|{
comment|/* Prevents initialization */
specifier|private
name|TestParameters
parameter_list|()
block|{     }
comment|/* Tests values */
specifier|static
specifier|final
name|int
name|nameLength
init|=
literal|5
decl_stmt|;
specifier|static
specifier|final
name|String
name|entryName
init|=
name|TestParameters
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|static
name|EntryType
name|entryType
init|=
name|EntryType
operator|.
name|COMPRESSED
decl_stmt|;
specifier|static
specifier|final
name|int
name|offset
init|=
literal|3
decl_stmt|;
specifier|static
specifier|final
name|int
name|length
init|=
literal|20
decl_stmt|;
specifier|static
specifier|final
name|int
name|NTHREADS
init|=
literal|2
decl_stmt|;
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|16384
decl_stmt|;
specifier|static
specifier|final
name|byte
index|[]
name|chmData
init|=
name|readResource
argument_list|(
literal|"/test-documents/testChm.chm"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|byte
index|[]
name|readResource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
try|try
init|(
name|InputStream
name|stream
init|=
name|TestParameters
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
init|)
block|{
return|return
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|stream
argument_list|)
return|;
block|}
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
block|}
comment|/* Verification points */
specifier|static
specifier|final
name|String
name|VP_CHM_MIME_TYPE
init|=
literal|"Content-Type=application/x-chm"
decl_stmt|;
specifier|static
specifier|final
name|String
name|VP_EXTRACTED_TEXT
init|=
literal|"The TCard method accepts only numeric arguments"
decl_stmt|;
specifier|static
specifier|final
name|String
name|VP_ISTF_SIGNATURE
init|=
literal|"ITSF"
decl_stmt|;
specifier|static
specifier|final
name|String
name|VP_ISTP_SIGNATURE
init|=
literal|"ITSP"
decl_stmt|;
specifier|static
specifier|final
name|String
name|VP_PMGL_SIGNATURE
init|=
literal|"PMGL"
decl_stmt|;
specifier|static
specifier|final
name|String
name|VP_CONTROL_DATA_SIGNATURE
init|=
literal|"LZXC"
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_DIRECTORY_LENGTH
init|=
literal|4180
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_DATA_OFFSET_LENGTH
init|=
literal|4300
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_DIRECTORY_OFFSET
init|=
literal|120
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_ITSF_HEADER_LENGTH
init|=
literal|96
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_LANGUAGE_ID
init|=
literal|1033
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_LAST_MODIFIED
init|=
literal|1042357880
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_UNKNOWN_000C
init|=
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_UNKNOWN_LEN
init|=
literal|24
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_UNKNOWN_OFFSET
init|=
literal|96
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_VERSION
init|=
literal|3
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_BLOCK_LENGTH
init|=
literal|4096
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_BLOCK_INDEX_INTERVAL
init|=
literal|2
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_ITSP_HEADER_LENGTH
init|=
literal|84
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_INDEX_DEPTH
init|=
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_INDEX_HEAD
init|=
literal|0
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_INDEX_ROOT
init|=
operator|-
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_UNKNOWN_NUM_BLOCKS
init|=
operator|-
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_ITSP_UNKNOWN_000C
init|=
literal|10
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_ITSP_UNKNOWN_0024
init|=
literal|0
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_ITSP_UNKNOWN_002C
init|=
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_ITSP_BYTEARR_LEN
init|=
literal|16
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_ITSP_VERSION
init|=
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_RESET_INTERVAL
init|=
literal|2
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_CONTROL_DATA_SIZE
init|=
literal|6
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_UNKNOWN_18
init|=
literal|0
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_CONTROL_DATA_VERSION
init|=
literal|2
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_WINDOW_SIZE
init|=
literal|65536
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_WINDOWS_PER_RESET
init|=
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_CHM_ENTITIES_NUMBER
init|=
literal|100
decl_stmt|;
comment|//updated  by Hawking
specifier|static
specifier|final
name|int
name|VP_PMGI_FREE_SPACE
init|=
literal|3
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_PMGL_BLOCK_NEXT
init|=
operator|-
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_PMGL_BLOCK_PREV
init|=
operator|-
literal|1
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_PMGL_FREE_SPACE
init|=
literal|1644
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_PMGL_UNKNOWN_008
init|=
literal|0
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_RESET_TABLE_BA
init|=
literal|12
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_RES_TBL_BLOCK_LENGTH
init|=
literal|32768
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_RES_TBL_COMPR_LENGTH
init|=
literal|177408
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_RES_TBL_UNCOMP_LENGTH
init|=
literal|383786
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_TBL_OFFSET
init|=
literal|40
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_RES_TBL_UNKNOWN
init|=
literal|8
decl_stmt|;
specifier|static
specifier|final
name|int
name|VP_RES_TBL_VERSION
init|=
literal|2
decl_stmt|;
block|}
end_class

end_unit

