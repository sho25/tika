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
name|io
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
comment|/**  * Exception when trying to read extract  */
end_comment

begin_class
specifier|public
class|class
name|ExtractReaderException
extends|extends
name|IOException
block|{
specifier|public
enum|enum
name|TYPE
block|{
comment|//what do you see when you look at the extract file
name|NO_EXTRACT_FILE
block|,
name|ZERO_BYTE_EXTRACT_FILE
block|,
name|IO_EXCEPTION
block|,
name|EXTRACT_PARSE_EXCEPTION
block|,
name|EXTRACT_FILE_TOO_SHORT
block|,
name|EXTRACT_FILE_TOO_LONG
block|,
name|INCORRECT_EXTRACT_FILE_SUFFIX
block|;
comment|//extract file must have suffix of .json or .txt,
comment|// optionally followed by gzip, zip or bz2
block|}
specifier|private
specifier|final
name|TYPE
name|type
decl_stmt|;
specifier|public
name|ExtractReaderException
parameter_list|(
name|TYPE
name|exceptionType
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|exceptionType
expr_stmt|;
block|}
specifier|public
name|TYPE
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
end_class

end_unit

