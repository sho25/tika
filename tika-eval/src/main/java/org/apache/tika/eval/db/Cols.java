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
operator|.
name|db
package|;
end_package

begin_enum
specifier|public
enum|enum
name|Cols
block|{
comment|//container table
name|CONTAINER_ID
block|,
name|FILE_PATH
block|,
name|EXTRACT_FILE_LENGTH
block|,
name|EXTRACT_FILE_LENGTH_A
block|,
comment|//for comparisons
name|EXTRACT_FILE_LENGTH_B
block|,
comment|//profile table
name|ID
block|,
name|LENGTH
block|,
name|FILE_NAME
block|,
name|FILE_EXTENSION
block|,
name|ELAPSED_TIME_MILLIS
block|,
name|NUM_METADATA_VALUES
block|,
name|IS_EMBEDDED
block|,
name|EMBEDDED_FILE_PATH
block|,
name|MIME_TYPE_ID
block|,
name|MD5
block|,
name|NUM_ATTACHMENTS
block|,
name|HAS_CONTENT
block|,
comment|//content
name|CONTENT_LENGTH
block|,
name|NUM_UNIQUE_TOKENS
block|,
name|NUM_TOKENS
block|,
name|NUM_ALPHABETIC_TOKENS
block|,
comment|//alphabetic or ideographic tokens
name|COMMON_TOKENS_LANG
block|,
comment|//which language was used for the common tokens metric?
name|NUM_COMMON_TOKENS
block|,
name|TOP_N_TOKENS
block|,
name|LANG_ID_1
block|,
name|LANG_ID_PROB_1
block|,
name|LANG_ID_2
block|,
name|LANG_ID_PROB_2
block|,
name|TOKEN_ENTROPY_RATE
block|,
name|TOKEN_LENGTH_SUM
block|,
name|TOKEN_LENGTH_MEAN
block|,
name|TOKEN_LENGTH_STD_DEV
block|,
name|UNICODE_CHAR_BLOCKS
block|,
name|NUM_PAGES
block|,
comment|//number of pages a document alleges it has
comment|//content comparisons
name|TOP_10_UNIQUE_TOKEN_DIFFS_A
block|,
name|TOP_10_UNIQUE_TOKEN_DIFFS_B
block|,
name|TOP_10_MORE_IN_A
block|,
name|TOP_10_MORE_IN_B
block|,
name|OVERLAP
block|,
name|DICE_COEFFICIENT
block|,
comment|//errors
name|PARSE_ERROR_TYPE_ID
block|,
name|PARSE_ERROR_DESCRIPTION
block|,
name|PARSE_EXCEPTION_DESCRIPTION
block|,
name|EXTRACT_EXCEPTION_TYPE_ID
block|,
name|EXTRACT_EXCEPTION_DESCRIPTION
block|,
comment|//exceptions
name|ORIG_STACK_TRACE
block|,
name|SORT_STACK_TRACE
block|,
name|PARSE_EXCEPTION_TYPE_ID
block|,
name|MIME_STRING
block|,
comment|//string representation of mime type
name|DIR_NAME_A
block|,
comment|//for comparisons in REF_PAIR_NAMES
name|DIR_NAME_B
block|}
end_enum

end_unit

